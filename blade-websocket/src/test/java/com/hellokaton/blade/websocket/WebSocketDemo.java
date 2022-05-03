package com.hellokaton.blade.websocket;

import com.hellokaton.blade.Blade;

/**
 * @author biezhi
 * @date 2017/10/30
 */
public class WebSocketDemo {

    public static void main(String[] args) {
        Blade.of()
                .get("/hello", ctx -> ctx.text("get route"))
                .post("/post", ctx -> ctx.text(ctx.request().query("param","null")))
//                .webSocket("/websocket", new WebSocketHandler() {
//                    @Override
//                    public void onConnect(WebSocketContext ctx) {
//                        System.out.println(ctx.session().uuid()+":open");
//                        ctx.message(ctx.session().uuid()+":open");
//                    }
//
//                    @Override
//                    public void onText(WebSocketContext ctx) {
//                        if("close".equals(ctx.message())){
//                            ctx.disconnect();
//                        } else {
//                            System.out.println(ctx.session().uuid()+":"+ctx.message());
//                            ctx.message(ctx.message());
//                        }
//                    }
//
//                    @Override
//                    public void onDisConnect(WebSocketContext ctx) {
//                        System.out.println(ctx.session().uuid()+":close:" + ctx.session());
//                    }
//                })
                .start(WebSocketDemo.class);
    }

}
