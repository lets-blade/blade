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
        Blade.me()
                .get("/hello", (req, res) -> {})
                .webSocket("/webscoket", new WebSocketHandler() {
                    @Override
                    public void onConnect(WebSocketContext ctx) {
                        System.out.println("客户端连接上了: " + ctx.getSession());
                    }

                    @Override
                    public void onText(WebSocketContext ctx) {
                        System.out.println("收到:" + ctx.getReqText());
                        ctx.message("发送: Hello");
                    }

                    @Override
                    public void onDisConnect(WebSocketContext ctx) {
                        System.out.println("客户端关闭链接: " + ctx.getSession());
                    }
                }).start();
    }

}
