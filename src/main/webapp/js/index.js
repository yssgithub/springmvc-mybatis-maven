$(function() {

    var websocket;


    // 首先判断是否 支持 WebSocket
    if('WebSocket' in window) {
        websocket = new WebSocket("ws://localhost:8085/springmvc/websocket/123");
        // websocket = new WebSocket("ws://localhost:8085/springmvc/websocketHandler/123");
    } else if('MozWebSocket' in window) {
        websocket = new MozWebSocket("ws://localhost:8085/springmvc/websocket/123");
        // websocket = new MozWebSocket("ws://localhost:8085/springmvc/websocketHandler/123");
    } else {
        // 需引入sockjs依赖，后端使用handler
        websocket = new SockJS("http://localhost:8085/springmvc/sockjs/websocket/123");
    }

    // 打开时
    websocket.onopen = function(evnt) {
        console.log("  websocket.onopen  ");
    };


    // 处理消息时
    websocket.onmessage = function(evnt) {
        $("#msg").append("<p>(<font color='red'>" + evnt.data + "</font>)</p>");
        console.log("  websocket.onmessage   ");
        console.log(evnt.data);

    };


    websocket.onerror = function(evnt) {
        console.log("  websocket.onerror  ");
        console.log(evnt);
    };

    websocket.onclose = function(evnt) {
        console.log("  websocket.onclose  ");
        console.log(evnt);
    };


    // 点击了发送消息按钮的响应事件
    $("#TXBTN").click(function(){

        // 获取消息内容
        var text = $("#tx").val();

        // 判断
        if(text == null || text == ""){
            alert(" content  can not empty!!");
            return false;
        }

        var msg = {
            msgContent: text,
            postsId: 1
        };

        // 发送消息
        websocket.send(JSON.stringify(msg));

    });


});