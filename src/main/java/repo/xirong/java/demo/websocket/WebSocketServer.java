package repo.xirong.java.demo.websocket;

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
        this.userId = userId;
        this.session = session;

        webSocketSet.add(this);
        addOnlineCount();
        clients.put(userId, this);
        System.out.println("WebSocket日志: 有新连接加入！当前在线人数为" + getOnlineCount());
        sendMessage("建立了连接");
    }

    @OnClose
    public void onClose() throws IOException {
        webSocketSet.remove(this);
        clients.remove(userId);
        subOnlineCount();
        System.out.println("WebSocket日志: 有一连接关闭！当前在线人数为" + getOnlineCount());
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
