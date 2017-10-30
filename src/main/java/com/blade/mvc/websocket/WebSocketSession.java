package com.blade.mvc.websocket;

import com.blade.kit.UUID;
import io.netty.channel.Channel;
import lombok.Data;

/**
 * @author biezhi
 * @date 2017/10/30
 */
@Data
public class WebSocketSession {

    private Channel channel;
    private String uuid;

    public WebSocketSession(Channel channel) {
        this.channel = channel;
        this.uuid = UUID.UU32();
    }
}
