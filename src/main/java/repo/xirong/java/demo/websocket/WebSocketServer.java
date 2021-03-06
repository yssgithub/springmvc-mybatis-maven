package repo.xirong.java.demo.websocket;

import com.alibaba.fastjson.JSONObject;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Component;

import javax.websocket.OnClose;
import javax.websocket.OnError;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArraySet;

@Component
@ServerEndpoint("/websocket/{userId}")
public class WebSocketServer {

    private static Log log = LogFactory.getLog(WebSocketServer.class);

    // 连接客户端数量
    private static int onlineCount = 0;

    // 所有的连接客户端
    private static Map<String, WebSocketServer> clients = new ConcurrentHashMap<String, WebSocketServer>();

    // 当前客户端连接的唯一标示
    private Session session;

    // 线程安全的集合类
    private static CopyOnWriteArraySet<WebSocketServer> webSocketSet = new CopyOnWriteArraySet<WebSocketServer>();

    // 当前客户端连接的用户ID
    private String userId;

    /**
     * 客户端连接服务端回调函数
     *
     * @param userId 用户ID
     * @param session 会话
     * @throws IOException
     */
    @OnOpen
    public void onOpen(@PathParam("userId") String userId, Session session) throws IOException {
        // 设置超时时间
        session.setMaxIdleTimeout(3600000);
        // 用户标识
        this.userId = userId;
        // 用户session
        this.session = session;
        // 往集合添加,广播用
        webSocketSet.add(this);
        // 往Map添加,定向发送用
        clients.put(userId, this);
        // 连接数添加
        addOnlineCount();
        System.out.println("WebSocket日志: 有新连接"+userId+"加入！当前在线人数为" + getOnlineCount());
        // 广播
        sendMessage(userId+"建立了连接");
        // 定向发送
        sendMessage(userId, "你好"+userId+",我们建立了连接");
    }

    @OnClose
    public void onClose() throws IOException {
        webSocketSet.remove(this);
        clients.remove(userId);
        subOnlineCount();
        System.out.println("WebSocket日志: 有一连接关闭！当前在线人数为" + getOnlineCount());
        // 广播
        sendMessage(userId+"断开了连接");
        // 定向发送
        sendMessage(userId, "你好"+userId+",期待我们下次再见");
    }

    /**
     * 接受到来自客户端的消息
     *
     * @param message
     * @throws IOException
     */
    @OnMessage
    public void onMessage(String message) throws IOException {
        System.out.println("WebSocket日志: 来自客户端的消息:" + message);
        log.info("服务端：接收到客户端信息【"+message+"】");
    }

    /**
     * 根据消息中的To(发送对象)发送消息
     * 如果是All则表示广播,否则发送给指定对象
     * to:消息接收人
     * data:可是普通字符串消息,也可以是图片的BASE64,也可以是json的字符串形式
     */
    public void sendWebSocketMessage(String message) {
//        JSONObject jsonObject = JSONObject.parseObject(message, JSONObject.class);
        JSONObject jsonObject = JSONObject.parseObject(message);
        sendWebSocketMessage(jsonObject);
    }

    /**
     * 根据消息中的To(发送对象)发送消息
     * 如果是All则表示广播,否则发送给指定对象
     * to:消息接收人
     * data:可是普通字符串消息,也可以是图片的BASE64,也可以是json的字符串形式
     */
    public void sendWebSocketMessage(Map<String, Object> messageMap) {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("to", messageMap.get("to"));
        jsonObject.put("data", messageMap.get("data"));
        sendWebSocketMessage(jsonObject);
    }

    /**
     * 根据消息中的To(发送对象)发送消息
     * 如果是All则表示广播,否则发送给指定对象
     * to:消息接收人
     * data:可是普通字符串消息,也可以是图片的BASE64,也可以是json的字符串形式
     */
    public void sendWebSocketMessage(JSONObject jsonObject) {
        if (StringUtils.isEmpty(jsonObject.getString("to"))
            || StringUtils.isEmpty(jsonObject.getString("data"))) {
            log.error("消息缺失接收人或消息内容");
            return;
        }
        String message = jsonObject.getString("data");
        String to = jsonObject.getString("to");
        if (!"All".equals(to)) {
            sendMessage(to, message);
        } else {
            sendMessage(message);
        }
    }

    /**
     * 广播发送消息
     */
    public void sendMessage(String message){
        for(WebSocketServer webSocket : webSocketSet){
            log.info("【websocket消息】 广播消息，message="+message);
            try {
                webSocket.session.getBasicRemote().sendText(message);
            }catch (Exception e){
                e.printStackTrace();
            }
        }
    }

    /**
     * 定向发送消息
     */
    public void sendMessage(String userId, String message){
        try {
            WebSocketServer socketServer = clients.get(userId);
            socketServer.session.getBasicRemote().sendText(message);
            log.info("【websocket消息】 定向消息，outTradeNo="+userId+"/message="+message);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    /**
     * 服务端报错了
     *
     * @param session
     * @param error
     */
    @OnError
    public void onError(Session session, Throwable error) {
        System.out.println("WebSocket日志: 发生错误");
        error.printStackTrace();
    }

    /**
     * 客户端连接数+1
     */
    public static synchronized void addOnlineCount() {
        WebSocketServer.onlineCount++;
    }

    /**
     * 客户端连接数-1
     */
    public static synchronized void subOnlineCount() {
        WebSocketServer.onlineCount--;
    }

    public static synchronized int getOnlineCount() {
        return onlineCount;
    }

    public static synchronized Map<String, WebSocketServer> getClients() {
        return clients;
    }

}
