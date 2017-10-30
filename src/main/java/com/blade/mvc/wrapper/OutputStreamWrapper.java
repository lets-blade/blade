package com.blade.mvc.wrapper;

import com.blade.kit.DateKit;
import com.blade.mvc.Const;
import com.blade.mvc.WebContext;
import com.blade.server.netty.HttpConst;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.DefaultFileRegion;
import io.netty.handler.codec.http.*;

import java.io.*;
import java.nio.channels.FileChannel;

/**
 * OutputStream Wrapper
 *
 * @author biezhi
 * @date 2017/8/2
 */
public class OutputStreamWrapper implements Closeable, Flushable {

    private OutputStream          outputStream;
    private File                  file;
    private ChannelHandlerContext ctx;

    public OutputStreamWrapper(OutputStream outputStream, File file, ChannelHandlerContext ctx) {
        this.outputStream = outputStream;
        this.file = file;
        this.ctx = ctx;
    }

    public File getFile() {
        return file;
    }

    public OutputStream getRaw() {
        return outputStream;
    }

    public void write(byte[] b) throws IOException {
        outputStream.write(b);
    }

    public void write(int b) throws IOException {
        outputStream.write(b);
    }

    public void write(byte[] bytes, int off, int len) throws IOException {
        outputStream.write(bytes, off, len);
    }

    @Override
    public void flush() throws IOException {
        outputStream.flush();
    }

    @Override
    public void close() throws IOException {
        try {
            this.flush();
            FileChannel file       = new FileInputStream(this.file).getChannel();
            long        fileLength = file.size();

            HttpResponse httpResponse = new DefaultHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.OK);
            httpResponse.headers().set(HttpConst.CONTENT_LENGTH, fileLength);
            httpResponse.headers().set(HttpConst.DATE, DateKit.gmtDate());
            httpResponse.headers().set(HttpConst.SERVER, "blade/" + Const.VERSION);

            boolean keepAlive = WebContext.request().keepAlive();
            if (keepAlive) {
                httpResponse.headers().set(HttpConst.CONNECTION, HttpConst.KEEP_ALIVE);
            }

            // Write the initial line and the header.
            ctx.write(httpResponse);
            ctx.write(new DefaultFileRegion(file, 0, fileLength), ctx.newProgressivePromise());
            // Write the end marker.
            ctx.writeAndFlush(LastHttpContent.EMPTY_LAST_CONTENT);
        } finally {
            if(null != outputStream){
                outputStream.close();
            }
        }
    }

}