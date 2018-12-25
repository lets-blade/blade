package com.blade.mvc.websocket;

import com.blade.kit.UUID;
import io.netty.channel.ChannelHandlerContext;
import lombok.Getter;
import lombok.experimental.Accessors;

/**
 * @author biezhi,darren
 * @date 2017/10/30
 */
@Getter
@Accessors(fluent = true)
public class WebSocketSession {

    private ChannelHandlerContext handlerContext;
    private String uuid;

    public WebSocketSession(ChannelHandlerContext handlerContext) {
        this.handlerContext = handlerContext;
        this.uuid = UUID.UU32();
    }
}
