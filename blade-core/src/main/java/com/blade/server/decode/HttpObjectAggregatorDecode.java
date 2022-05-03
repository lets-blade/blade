package com.blade.server.decode;

import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.*;
import lombok.extern.slf4j.Slf4j;

import static io.netty.handler.codec.http.HttpHeaderNames.CONNECTION;
import static io.netty.handler.codec.http.HttpHeaderNames.CONTENT_LENGTH;

@Slf4j
public class HttpObjectAggregatorDecode extends HttpObjectAggregator {

    private static final FullHttpResponse EXPECTATION_FAILED = new DefaultFullHttpResponse(
            HttpVersion.HTTP_1_1, HttpResponseStatus.EXPECTATION_FAILED, Unpooled.EMPTY_BUFFER);
    private static final FullHttpResponse TOO_LARGE_CLOSE = new DefaultFullHttpResponse(
            HttpVersion.HTTP_1_1, HttpResponseStatus.REQUEST_ENTITY_TOO_LARGE, Unpooled.EMPTY_BUFFER);
    private static final FullHttpResponse TOO_LARGE = new DefaultFullHttpResponse(
            HttpVersion.HTTP_1_1, HttpResponseStatus.REQUEST_ENTITY_TOO_LARGE, Unpooled.EMPTY_BUFFER);

    static {
        EXPECTATION_FAILED.headers().set(CONTENT_LENGTH, 0);
        TOO_LARGE.headers().set(CONTENT_LENGTH, 0);

        TOO_LARGE_CLOSE.headers().set(CONTENT_LENGTH, 0);
        TOO_LARGE_CLOSE.headers().set(CONNECTION, HttpHeaderValues.CLOSE);
    }

    public HttpObjectAggregatorDecode(int maxContentLength) {
        super(maxContentLength);
    }

    @Override
    protected void handleOversizedMessage(final ChannelHandlerContext ctx, HttpMessage oversized) throws Exception {
        if (oversized instanceof HttpRequest) {
            // send back a 413 and close the connection
            HttpRequest httpRequest = (HttpRequest) oversized;
            log.info("Request [{}] Failed to send a 413 Request Entity Too Large.", httpRequest.uri());
            // If the client started to send data already, close because it's impossible to recover.
            // If keep-alive is off and 'Expect: 100-continue' is missing, no need to leave the connection open.
            if (oversized instanceof FullHttpMessage ||
                    !HttpUtil.is100ContinueExpected(oversized) && !HttpUtil.isKeepAlive(oversized)) {
                ChannelFuture future = ctx.writeAndFlush(TOO_LARGE_CLOSE.retainedDuplicate());
                future.addListener(new ChannelFutureListener() {
                    @Override
                    public void operationComplete(ChannelFuture future) throws Exception {
                        if (!future.isSuccess()) {
                            log.warn("Failed to send a 413 Request Entity Too Large.", future.cause());
                        }
                        ctx.close();
                    }
                });
            } else {
                ctx.writeAndFlush(TOO_LARGE.retainedDuplicate()).addListener(new ChannelFutureListener() {
                    @Override
                    public void operationComplete(ChannelFuture future) throws Exception {
                        if (!future.isSuccess()) {
                            log.warn("Failed to send a 413 Request Entity Too Large.", future.cause());
                            ctx.close();
                        }
                    }
                });
            }
        } else if (oversized instanceof HttpResponse) {
            ctx.close();
            throw new TooLongHttpContentException("Response entity too large: " + oversized);
        } else {
            throw new IllegalStateException();
        }
    }

}
