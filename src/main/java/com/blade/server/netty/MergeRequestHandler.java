/**
 * Copyright (c) 2018, biezhi 王爵 nice (biezhi.me@gmail.com)
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.blade.server.netty;

import com.blade.mvc.handler.ExceptionHandler;
import com.blade.mvc.http.HttpRequest;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.*;
import lombok.extern.slf4j.Slf4j;

import static io.netty.handler.codec.http.HttpVersion.HTTP_1_1;

/**
 * Merge Netty HttpObject as {@link HttpRequest}
 *
 * @author biezhi
 * 2018/10/15
 */
@Slf4j
public class MergeRequestHandler extends SimpleChannelInboundHandler<HttpObject> {

    private HttpRequest httpRequest;

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, HttpObject msg) {
        if (msg instanceof io.netty.handler.codec.http.HttpRequest) {
            httpRequest = new HttpRequest();
            httpRequest.setNettyRequest((io.netty.handler.codec.http.HttpRequest) msg);
            return;
        }
        if (msg instanceof HttpContent) {
            httpRequest.appendContent((HttpContent) msg);
        }
        if (msg instanceof LastHttpContent) {
            ctx.fireChannelRead(httpRequest);
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        if (!ExceptionHandler.isResetByPeer(cause)) {
            log.error(cause.getMessage(), cause);
            FullHttpResponse response = new DefaultFullHttpResponse(HTTP_1_1, HttpResponseStatus.valueOf(500));
            ctx.writeAndFlush(response).addListener(ChannelFutureListener.CLOSE);
        }
    }

}