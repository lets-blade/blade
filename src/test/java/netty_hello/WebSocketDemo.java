package netty_hello;

import com.blade.Blade;
import com.blade.mvc.handler.WebSocketHandler;
import com.blade.mvc.websocket.WebSocketContext;

/**
 * @author biezhi
 * @date 2017/10/30
 */
public class WebSocketDemo {

    public static void main(String[] args) {
        Blade.of()
                .get("/hello", ctx -> ctx.text("get route"))
                .webSocket("/websocket", new WebSocketHandler() {
                    @Override
                    public void onConnect(WebSocketContext ctx) {
                        System.out.println("客户端连接上了ws1: " + ctx.getSession());
                    }

                    @Override
                    public void onText(WebSocketContext ctx) {
                        System.out.println("ws1收到:" + ctx.getReqText());
                        ctx.message("发送: Hello");
                    }

                    @Override
                    public void onDisConnect(WebSocketContext ctx) {
                        System.out.println("ws1客户端关闭链接: " + ctx.getSession());
                    }
                })
                .webSocket("/websocket2", new WebSocketHandler() {
                    @Override
                    public void onConnect(WebSocketContext ctx) {
                        System.out.println("客户端连接上了ws2: " + ctx.getSession());
                    }

                    @Override
                    public void onText(WebSocketContext ctx) {
                        System.out.println("ws2收到:" + ctx.getReqText());
                        ctx.message("发送: Hello");
                    }

                    @Override
                    public void onDisConnect(WebSocketContext ctx) {
                        System.out.println("ws2客户端关闭链接: " + ctx.getSession());
                    }
                }).start(WebSocketDemo.class);
    }

}
