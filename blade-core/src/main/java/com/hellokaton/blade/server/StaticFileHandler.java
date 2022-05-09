/**
 * Copyright (c) 2022, katon nice (hellokaton@gmail.com)
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
package com.hellokaton.blade.server;

import com.hellokaton.blade.Blade;
import com.hellokaton.blade.exception.ForbiddenException;
import com.hellokaton.blade.exception.NotFoundException;
import com.hellokaton.blade.kit.*;
import com.hellokaton.blade.mvc.BladeConst;
import com.hellokaton.blade.mvc.HttpConst;
import com.hellokaton.blade.mvc.WebContext;
import com.hellokaton.blade.mvc.handler.RequestHandler;
import com.hellokaton.blade.mvc.http.Request;
import com.hellokaton.blade.mvc.route.Route;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.DefaultFileRegion;
import io.netty.handler.codec.http.*;
import io.netty.handler.ssl.SslHandler;
import io.netty.handler.stream.ChunkedFile;
import io.netty.util.CharsetUtil;
import lombok.extern.slf4j.Slf4j;
import lombok.var;

import java.io.*;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLDecoder;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.regex.Pattern;

import static com.hellokaton.blade.kit.BladeKit.*;
import static com.hellokaton.blade.mvc.BladeConst.HTTP_DATE_FORMAT;
import static com.hellokaton.blade.mvc.BladeConst.REQUEST_TO_STATIC_ATTR;
import static io.netty.handler.codec.http.HttpResponseStatus.*;
import static io.netty.handler.codec.http.HttpVersion.HTTP_1_0;
import static io.netty.handler.codec.http.HttpVersion.HTTP_1_1;
import static java.util.stream.Collectors.toList;

/**
 * static file handler
 *
 * @author biezhi
 * 2017/5/31
 */
@Slf4j
public class StaticFileHandler implements RequestHandler {

    private static final String STYLE = "body{background:#fff;margin:0;padding:30px;-webkit-font-smoothing:antialiased;font-family:Menlo,Consolas,monospace}main{max-width:920px}header{display:flex;justify-content:space-between}#toggle{display:none;cursor:pointer}#toggle:before{display:inline-block;content:url(\"data:image/svg+xml; utf8, <svg height='24px' version='1.1' viewBox='0 0 24 24' width='24px' xmlns='http://www.w3.org/2000/svg'><g fill='none' fill-rule='evenodd' stroke='none' stroke-width='1'><g transform='translate(-431.000000, -479.000000)'><g transform='translate(215.000000, 119.000000)'/><path d='M432,480 L432,486 L438,486 L438,480 L432,480 Z M440,480 L440,486 L446,486 L446,480 L440,480 Z M448,480 L448,486 L454,486 L454,480 L448,480 Z M449,481 L449,485 L453,485 L453,481 L449,481 Z M441,481 L441,485 L445,485 L445,481 L441,481 Z M433,481 L433,485 L437,485 L437,481 L433,481 Z M432,488 L432,494 L438,494 L438,488 L432,488 Z M440,488 L440,494 L446,494 L446,488 L440,488 Z M448,488 L448,494 L454,494 L454,488 L448,488 Z M449,489 L449,493 L453,493 L453,489 L449,489 Z M441,489 L441,493 L445,493 L445,489 L441,489 Z M433,489 L433,493 L437,493 L437,489 L433,489 Z M432,496 L432,502 L438,502 L438,496 L432,496 Z M440,496 L440,502 L446,502 L446,496 L440,496 Z M448,496 L448,502 L454,502 L454,496 L448,496 Z M449,497 L449,501 L453,501 L453,497 L449,497 Z M441,497 L441,501 L445,501 L445,497 L441,497 Z M433,497 L433,501 L437,501 L437,497 L433,497 Z' fill='#000000'/></g></g></svg>\")}#toggle.single-column:before{content:url(\"data:image/svg+xml; utf8, <svg height='24px' viewBox='0 0 24 24' width='24px' xmlns='http://www.w3.org/2000/svg'><g fill='none' fill-rule='evenodd' id='miu' stroke='none' stroke-width='1'><g transform='translate(-359.000000, -479.000000)'><g transform='translate(215.000000, 119.000000)'/><path d='M360.577138,485 C360.258394,485 360,485.221932 360,485.5 C360,485.776142 360.262396,486 360.577138,486 L381.422862,486 C381.741606,486 382,485.778068 382,485.5 C382,485.223858 381.737604,485 381.422862,485 L360.577138,485 L360.577138,485 Z M360.577138,490 C360.258394,490 360,490.221932 360,490.5 C360,490.776142 360.262396,491 360.577138,491 L381.422862,491 C381.741606,491 382,490.778068 382,490.5 C382,490.223858 381.737604,490 381.422862,490 L360.577138,490 L360.577138,490 Z M360.577138,495 C360.258394,495 360,495.221932 360,495.5 C360,495.776142 360.262396,496 360.577138,496 L381.422862,496 C381.741606,496 382,495.778068 382,495.5 C382,495.223858 381.737604,495 381.422862,495 L360.577138,495 L360.577138,495 Z' fill='#000000'/></g></g></svg>\")}a{color:#1A00F2;text-decoration:none}h1{font-size:18px;font-weight:500;margin-top:0;color:#000;font-family:-apple-system,Helvetica;display:flex}h1 a{color:inherit;font-weight:700;border-bottom:1px dashed transparent}h1 a::after{content:'/'}h1 a:hover{color:#7d7d7d}h1 i{font-style:normal}ul{margin:0;padding:20px 0 0 0}ul.single-column{flex-direction:column}ul li{list-style:none;padding:10px 0;font-size:14px;display:flex;justify-content:space-between}ul li i{color:#9B9B9B;font-size:11px;display:block;font-style:normal;white-space:nowrap;padding-left:15px}ul a{color:#1A00F2;white-space:nowrap;overflow:hidden;display:block;text-overflow:ellipsis}ul a::before{content:url(\"data:image/svg+xml; utf8, <svg xmlns='http://www.w3.org/2000/svg' width='16' height='16' viewBox='0 0 64 64'><g fill='transparent' stroke='currentColor' stroke-miterlimit='10'><path stroke-width='4' d='M50.46 56H13.54V8h22.31a4.38 4.38 0 0 1 3.1 1.28l10.23 10.24a4.38 4.38 0 0 1 1.28 3.1z'/><path stroke-width='2' d='M35.29 8.31v14.72h14.06'/></g></svg>\");display:inline-block;vertical-align:middle;margin-right:10px}ul a:hover{color:#000}ul a[class=''] + i{display:none}ul a[class='']::before{content:url(\"data:image/svg+xml; utf8, <svg xmlns='http://www.w3.org/2000/svg' width='16' height='16' viewBox='0 0 64 64'><path fill='transparent' stroke='currentColor' stroke-width='4' stroke-miterlimit='10' d='M56 53.71H8.17L8 21.06a2.13 2.13 0 0 1 2.13-2.13h2.33l2.13-4.28A4.78 4.78 0 0 1 18.87 12h9.65a4.78 4.78 0 0 1 4.28 2.65l2.13 4.28h17.36a3.55 3.55 0 0 1 3.55 3.55z'/></svg>\")}ul a[class='gif']::before,ul a[class='jpg']::before,ul a[class='png']::before,ul a[class='svg']::before{content:url(\"data:image/svg+xml; utf8, <svg width='16' height='16' viewBox='0 0 80 80' xmlns='http://www.w3.org/2000/svg' fill='none' stroke='currentColor' stroke-width='5' stroke-linecap='round' stroke-linejoin='round'><rect x='6' y='6' width='68' height='68' rx='5' ry='5'/><circle cx='24' cy='24' r='8'/><path d='M73 49L59 34 37 52M53 72L27 42 7 58'/></svg>\");width:16px}@media (min-width:768px){#toggle{display:inline-block}ul{display:flex;flex-wrap:wrap}ul li{width:230px;padding-right:20px}ul.single-column li{width:auto}}@media (min-width:992px){body{padding:45px}h1{font-size:15px}ul li{font-size:13px;box-sizing:border-box;justify-content:flex-start}ul li:hover i{opacity:1}ul li i{font-size:10px;opacity:0;margin-left:10px;margin-top:3px;padding-left:0}}";

    private static final Pattern ALLOWED_FILE_NAME = Pattern.compile("[^-._]?[^<>&\"]*");

    private static JarFile CACHE_JAR_FILE;

    private final boolean showFileList;

    /**
     * default cache 30 days.
     */
    private final int httpCacheSeconds;

    public StaticFileHandler(Blade blade) {
        this.showFileList = blade.environment().getBoolean(BladeConst.ENV_KEY_STATIC_LIST, false);
        this.httpCacheSeconds = blade.environment().getInt(BladeConst.ENV_KEY_HTTP_CACHE_TIMEOUT, 86400 * 30);
    }

    /**
     * print static file to client
     *
     * @param webContext web context
     */
    @Override
    public void handle(WebContext webContext) throws Exception {
        Request request = webContext.getRequest();

        ChannelHandlerContext ctx = webContext.getChannelHandlerContext();
        if (!com.hellokaton.blade.mvc.http.HttpMethod.GET.name().equals(request.method())) {
            sendError(ctx, METHOD_NOT_ALLOWED);
            return;
        }

        Instant start = Instant.now();

        String uri = request.attribute(REQUEST_TO_STATIC_ATTR);
        if (null != uri) {
            Route route = webContext.getRoute();
            if (route.isWildcard()) {
                uri = uri + request.uri().replace(route.getRewritePath(), "");
            }
        } else {
            uri = URLDecoder.decode(request.uri(), "UTF-8");
        }
        String method = StringKit.padRight(request.method(), 6);
        String cleanURL = getCleanURL(request, uri);

        // webjars
        if (cleanURL.startsWith(BladeConst.WEB_JARS)) {
            InputStream input = getResourceStreamFromJar(uri);
            writeWithJarFile(request, ctx, uri, start, cleanURL, method, input);
            return;
        }

        // jar file
        if (BladeKit.runtimeIsJAR()) {
            InputStream input = StaticFileHandler.class.getResourceAsStream(cleanURL);
            writeWithJarFile(request, ctx, uri, start, cleanURL, method, input);
            return;
        }

        // disk file
        final String path = sanitizeUri(cleanURL);
        if (path == null) {
            log403(log, method, uri);
            throw new ForbiddenException();
        }

        File file = new File(path);
        if (file.isHidden() || !file.exists()) {
            // gradle resources path
            File resourcesDirectory = getGradleResourcesDirectory();
            if (resourcesDirectory.isDirectory()) {
                file = new File(resourcesDirectory.getPath() + "/" + cleanURL.substring(1));
                if (file.isHidden() || !file.exists()) {
                    throw new NotFoundException(uri);
                }
            } else {
                throw new NotFoundException(uri);
            }
        }

        if (file.isDirectory() && showFileList) {
            sendListing(ctx, uri, getFileMetas(file), cleanURL);
            return;
        }

        if (!file.isFile()) {
            sendError(ctx, FORBIDDEN);
            return;
        }

        // Cache Validation
        String ifModifiedSince = request.header(HttpConst.HEADER_IF_MODIFIED_SINCE);
        if (ifModifiedSince != null && !ifModifiedSince.isEmpty()) {
            this.http304(request, ctx, file, ifModifiedSince);
            log304(log, method, uri);
            return;
        }

        HttpResponse httpResponse = new DefaultHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.OK);
        setContentTypeHeader(httpResponse, file);
        setDateAndCacheHeaders(httpResponse, file);

        if (request.useGZIP()) {
            File output = new File(file.getPath() + ".gz");
            IOKit.compressGZIP(file, output);
            file = output;
            setGzip(httpResponse);
        }

        RandomAccessFile raf;
        try {
            raf = new RandomAccessFile(file, "r");
        } catch (FileNotFoundException ignore) {
            sendError(ctx, NOT_FOUND);
            return;
        }

        long length;
        try {
            length = raf.length();
        } catch (IOException e) {
            log.error("Read file {} length error", file.getName(), e);
            sendError(ctx, INTERNAL_SERVER_ERROR);
            return;
        }

        httpResponse.headers().set(HttpHeaderNames.CONTENT_LENGTH, length);
        if (request.keepAlive()) {
            httpResponse.headers().set(HttpHeaderNames.CONNECTION, HttpHeaderValues.KEEP_ALIVE);
        }

        // Write the initial line and the header.
        ctx.write(httpResponse);

        // Write the content.
        ChannelFuture sendFileFuture;
        ChannelFuture lastContentFuture;
        if (ctx.pipeline().get(SslHandler.class) == null) {
            sendFileFuture = ctx.write(
                    new DefaultFileRegion(raf.getChannel(), 0, length),
                    ctx.newProgressivePromise()
            );

            // Write the end marker.
            lastContentFuture = ctx.writeAndFlush(LastHttpContent.EMPTY_LAST_CONTENT);

        } else {
            sendFileFuture = ctx.writeAndFlush(
                    new HttpChunkedInput(
                            new ChunkedFile(raf, 0, length, 8192)
                    ),
                    ctx.newProgressivePromise());
            // HttpChunkedInput will write the end marker (LastHttpContent) for us.
            lastContentFuture = sendFileFuture;
        }

        ProgressiveFutureListener progressiveListener = ProgressiveFutureListener.create(file.getName(), raf.getChannel()).hideProgress();
        sendFileFuture.addListener(progressiveListener);

        // Decide whether to close the connection or not.
        if (!request.keepAlive()) {
            lastContentFuture.addListener(ChannelFutureListener.CLOSE);
        }
        log200AndCost(log, start, method, uri);
    }

    private void http304(Request request, ChannelHandlerContext ctx,
                         File file, String ifModifiedSince) throws ParseException {
        SimpleDateFormat dateFormatter = new SimpleDateFormat(HTTP_DATE_FORMAT, Locale.US);
        Date ifModifiedSinceDate = dateFormatter.parse(ifModifiedSince);

        // Only compare up to the second because the datetime format we send to the client
        // does not have milliseconds
        long ifModifiedSinceDateSeconds = ifModifiedSinceDate.getTime() / 1000;
        long fileLastModifiedSeconds = file.lastModified() / 1000;
        if (ifModifiedSinceDateSeconds == fileLastModifiedSeconds) {
            this.sendNotModified(ctx, request);
        }
    }

    private void sendNotModified(ChannelHandlerContext ctx, Request request) {
        FullHttpResponse response = new DefaultFullHttpResponse(HTTP_1_1, NOT_MODIFIED, Unpooled.EMPTY_BUFFER);
        setDateHeader(response);
        sendAndCleanupConnection(ctx, request, response);
    }

    public static final String HTTP_DATE_GMT_TIMEZONE = "GMT";

    /**
     * Sets the Date header for the HTTP response
     *
     * @param response HTTP response
     */
    private static void setDateHeader(FullHttpResponse response) {
        SimpleDateFormat dateFormatter = new SimpleDateFormat(HTTP_DATE_FORMAT, Locale.US);
        dateFormatter.setTimeZone(TimeZone.getTimeZone(HTTP_DATE_GMT_TIMEZONE));

        Calendar time = new GregorianCalendar();
        response.headers().set(HttpHeaderNames.DATE, dateFormatter.format(time.getTime()));
    }


    /**
     * If Keep-Alive is disabled, attaches "Connection: close" header to the response
     * and closes the connection after the response being sent.
     */
    private void sendAndCleanupConnection(ChannelHandlerContext ctx, Request request, FullHttpResponse response) {
        final boolean keepAlive = request.keepAlive();
        HttpUtil.setContentLength(response, response.content().readableBytes());
        if (!keepAlive) {
            // We're going to close the connection as soon as the response is sent,
            // so we should also make it clear for the client.
            response.headers().set(HttpHeaderNames.CONNECTION, HttpHeaderValues.CLOSE);
        } else if (HTTP_1_0.text().equals(request.protocol())) {
            response.headers().set(HttpHeaderNames.CONNECTION, HttpHeaderValues.KEEP_ALIVE);
        }

        ChannelFuture flushPromise = ctx.writeAndFlush(response);

        if (!keepAlive) {
            // Close the connection as soon as the response is sent.
            flushPromise.addListener(ChannelFutureListener.CLOSE);
        }
    }

    private List<FileMeta> getFileMetas(File file) {
        if (file.isHidden() || !file.canRead() || file.listFiles() == null) {
            return Collections.emptyList();
        }

        return Arrays.stream(Objects.requireNonNull(file.listFiles()))
                .filter(f -> !f.isHidden() && f.canRead())
                .map(FileMeta::buildByFile)
                .collect(toList());
    }

    private File getGradleResourcesDirectory() {
        return new File(new File(BladeConst.class.getResource("/").getPath()).getParent() + "/resources");
    }

    private String getCleanURL(Request request, String uri) {
        uri = PathKit.cleanPath(uri.replaceFirst(request.contextPath(), "/"));
        if (uri.endsWith("/")) {
            uri = StringKit.stripEnd(uri, "/");
        }
        return uri;
    }

    private InputStream getResourceStreamFromJar(String uri) {
        return StaticFileHandler.class.getResourceAsStream("/META-INF/resources" + uri);
    }

    private URL getURLFromJar(String uri) {
        return StaticFileHandler.class.getClassLoader().getResource(uri);
    }

    private void writeWithJarFile(Request request, ChannelHandlerContext ctx,
                                  String uri, Instant start,
                                  String cleanUri, String method,
                                  InputStream input) throws IOException {
        if (null == input) {
            throw new NotFoundException(uri);
        }
        if (writeJarResource(ctx, request, cleanUri, input)) {
            log200AndCost(log, start, method, uri);
        }
    }

    private boolean writeJarResource(ChannelHandlerContext ctx, Request request,
                                     String cleanUri, InputStream input) throws IOException {
        try {
            var staticInputStream = new StaticInputStream(input);

            int size = staticInputStream.size();

            if (this.executeHttp304(ctx, request, size, -1)) {
                log304(log, request.method(), cleanUri);
                return false;
            }

            FullHttpResponse httpResponse = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, OK, staticInputStream.asByteBuf());

            setDateAndCacheHeaders(httpResponse, null);
            String contentType = MimeTypeKit.parse(cleanUri);
            if (null != contentType) {
                httpResponse.headers().set(HttpHeaderNames.CONTENT_TYPE, contentType);
            }
            httpResponse.headers().set(HttpHeaderNames.CONTENT_LENGTH, size);

            if (request.keepAlive()) {
                httpResponse.headers().set(HttpHeaderNames.CONNECTION, HttpHeaderValues.KEEP_ALIVE);
            }
            // Write the initial line and the header.
            ctx.writeAndFlush(httpResponse);
            return true;
        } catch (NullPointerException e) {
            String jarPath = cleanUri.substring(1);

            URL dirURL = getURLFromJar(jarPath);
            if (null != dirURL && dirURL.getProtocol().equals("jar")) {
                List<FileMeta> listFiles = getResourceListing(dirURL, jarPath);
                sendListing(ctx, request.uri(), listFiles, cleanUri);
                return true;
            } else {
                throw e;
            }
        } catch (IOException e) {
            throw e;
        }
    }

    private boolean executeHttp304(ChannelHandlerContext ctx, Request request, long size, long lastModified) {
        String ifModifiedSince = request.header(HttpConst.HEADER_IF_MODIFIED_SINCE);

        if (StringKit.isNotEmpty(ifModifiedSince) && httpCacheSeconds > 0) {

            Date ifModifiedSinceDate = format(ifModifiedSince, HTTP_DATE_FORMAT);

            // Only compare up to the second because the datetime format we send to the client
            // does not have milliseconds
            long ifModifiedSinceDateSeconds = ifModifiedSinceDate.getTime() / 1000;
            if (ifModifiedSinceDateSeconds == lastModified / 1000) {

                FullHttpResponse response = new DefaultFullHttpResponse(HTTP_1_1, NOT_MODIFIED);

                String contentType = MimeTypeKit.parse(request.uri());
                if (null != contentType) {
                    response.headers().set(HttpHeaderNames.CONTENT_TYPE, contentType);
                }

                response.headers().set(HttpHeaderNames.DATE, DateKit.gmtDate());
                response.headers().set(HttpHeaderNames.CONTENT_LENGTH, size);
                if (request.keepAlive()) {
                    response.headers().set(HttpHeaderNames.CONNECTION, HttpHeaderValues.KEEP_ALIVE);
                }
                // Close the connection as soon as the error message is sent.
                ctx.writeAndFlush(response).addListener(ChannelFutureListener.CLOSE);
                return true;
            }
        }
        return false;
    }

    public Date format(String date, String pattern) {
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern(pattern, Locale.US);
        LocalDateTime formatted = LocalDateTime.parse(date, fmt);
        Instant instant = formatted.atZone(ZoneId.systemDefault()).toInstant();
        return Date.from(instant);
    }

    /**
     * Sets the Date and Cache headers for the HTTP Response
     *
     * @param response    HTTP response
     * @param fileToCache file to extract content type
     */
    private void setDateAndCacheHeaders(HttpResponse response, File fileToCache) {
        SimpleDateFormat dateFormatter = new SimpleDateFormat(HTTP_DATE_FORMAT, Locale.US);
        dateFormatter.setTimeZone(TimeZone.getTimeZone(HTTP_DATE_GMT_TIMEZONE));
        // Date header
        Calendar time = new GregorianCalendar();
        response.headers().set(HttpHeaderNames.DATE, dateFormatter.format(time.getTime()));

        // Add cache headers
        if (httpCacheSeconds > 0) {
            time.add(Calendar.SECOND, httpCacheSeconds);

            response.headers().set(HttpHeaderNames.EXPIRES, dateFormatter.format(time.getTime()));
            response.headers().set(HttpHeaderNames.CACHE_CONTROL, "private, max-age=" + httpCacheSeconds);
            response.headers().set(
                    HttpHeaderNames.LAST_MODIFIED, dateFormatter.format(new Date(fileToCache.lastModified())));
        }
    }

    List<FileMeta> getResourceListing(URL dirURL, String path) throws IOException {
        List<FileMeta> listFiles = new ArrayList<>();
        if (null == CACHE_JAR_FILE) {
            //strip out only the JAR file
            String jarPath = dirURL.getPath().substring(5, dirURL.getPath().indexOf("!"));
            CACHE_JAR_FILE = new JarFile(URLDecoder.decode(jarPath, "UTF-8"));
        }

        Enumeration<JarEntry> entries = CACHE_JAR_FILE.entries();
        while (entries.hasMoreElements()) {
            JarEntry jarEntry = entries.nextElement();
            String name = jarEntry.getName();
            if (!name.startsWith(path) || name.equals(path + "/")) {
                continue;
            }
            FileMeta fileMeta = new FileMeta();
            fileMeta.name = name.substring(path.length() + 1);
            fileMeta.isDirectory = jarEntry.isDirectory();
            if (!fileMeta.isDirectory) {
                fileMeta.length = jarEntry.getSize();
            }
            listFiles.add(fileMeta);
        }
        return listFiles;
    }

    static class FileMeta {
        boolean isDirectory;
        String name;
        long length;

        static FileMeta buildByFile(File f) {
            FileMeta fileMeta = new FileMeta();
            fileMeta.name = f.getName();
            fileMeta.isDirectory = f.isDirectory();
            if (f.isFile()) {
                fileMeta.length = f.length();
            }
            return fileMeta;
        }
    }

    private static void sendListing(ChannelHandlerContext ctx, String uri, List<FileMeta> listFiles, String dirPath) {
        var response = new DefaultFullHttpResponse(HTTP_1_1, OK);
        response.headers().set(HttpHeaderNames.CONTENT_TYPE, "text/html; charset=UTF-8");
        StringBuilder buf = new StringBuilder()
                .append("<!DOCTYPE html>\r\n")
                .append("<html><head>")
                .append("<meta charset='utf-8' />")
                .append("<meta name='viewport' content='width=device-width, initial-scale=1'>")
                .append("<title>")
                .append("Files within: ")
                .append(dirPath)
                .append("</title><style>" + STYLE + "</style></head><body>")
                .append("<main><header><h1><i>Index of&nbsp;</i>");

        String[] dirs = uri.split("/");

        for (String s : dirs) {
            if (StringKit.isEmpty(s)) {
                continue;
            }
            String path = uri.substring(0, uri.indexOf(s) + s.length());
            buf.append("<a href='").append(path).append("'>").append(s).append("</a>");
        }

        buf.append("</h1>");
        buf.append("<a id='toggle' title='click to toggle the view'></a>");
        buf.append("</header>");
        buf.append("<ul id='files' class='single-column'>");

        if (dirs.length > 2) {
            String parent = uri.substring(0, uri.lastIndexOf("/"));
            buf.append("<li><a href='").append(parent).append("' title='' class=''>..<i></i></li>");
        }

        for (FileMeta f : listFiles) {
            String name = f.name;
            if (!ALLOWED_FILE_NAME.matcher(name).matches()) {
                continue;
            }
            String subPath = (uri + "/" + name).replace("//", "/");
            buf.append("<li><a href='").append(subPath).append("' title='").append(name).append("'");
            if (f.isDirectory) {
                buf.append(" class=''>");
                buf.append(name).append("<i></i></li>");
            } else {
                buf.append(" class='css'>");
                String size = ConvertKit.byte2FitMemoryString(f.length);
                buf.append(name).append("</a><i>");
                buf.append(size).append("</i></li>");
            }
        }
        buf.append("</ul>");
        buf.append("</main>");
        buf.append("<script type='text/javascript'>");
        buf.append("(function() {");
        buf.append("toggle.addEventListener('click', function() {");
        buf.append("files.classList.toggle('single-column');");
        buf.append("toggle.classList.toggle('single-column');");
        buf.append("});");
        buf.append("})();");
        buf.append("</script>");
        buf.append("</body></html>");

        ByteBuf buffer = Unpooled.copiedBuffer(buf, CharsetUtil.UTF_8);
        response.content().writeBytes(buffer);
        buffer.release();

        // Close the connection as soon as the error message is sent.
        ctx.writeAndFlush(response).addListener(ChannelFutureListener.CLOSE);
    }

    private static void sendError(ChannelHandlerContext ctx, HttpResponseStatus status) {
        var response = new DefaultFullHttpResponse(HTTP_1_1, status,
                Unpooled.copiedBuffer("Failure: " + status + "\r\n", CharsetUtil.UTF_8));

        response.headers().set(HttpHeaderNames.CONTENT_TYPE, HttpConst.CONTENT_TYPE_TEXT);
        // Close the connection as soon as the error message is sent.
        ctx.writeAndFlush(response).addListener(ChannelFutureListener.CLOSE);
    }

    private static final Pattern INSECURE_URI = Pattern.compile(".*[<>&\"].*");

    private static String sanitizeUri(String uri) {
        if (uri.isEmpty() || uri.charAt(0) != NettyHttpConst.CHAR_SLASH) {
            return null;
        }
        // Convert file separators.
        uri = uri.replace(NettyHttpConst.CHAR_SLASH, File.separatorChar);
        // Simplistic dumb security check.
        // You will have to do something serious in the production environment.
        if (uri.contains(File.separator + NettyHttpConst.CHAR_POINT) ||
                uri.contains('.' + File.separator) ||
                uri.charAt(0) == '.' || uri.charAt(uri.length() - 1) == '.' ||
                INSECURE_URI.matcher(uri).matches()) {
            return null;
        }
        // Maven resources path
        String path = BladeConst.CLASSPATH + File.separator + uri.substring(1);
        return path.replace("//", "/");
    }

    /**
     * Sets the content type header for the HTTP Response
     *
     * @param response HTTP response
     * @param file     file to extract content type
     */
    private static void setContentTypeHeader(HttpResponse response, File file) {
        String contentType = MimeTypeKit.parse(file.getName());
        if (null == contentType) {
            contentType = URLConnection.guessContentTypeFromName(file.getName());
        }
        response.headers().set(HttpHeaderNames.CONTENT_TYPE, contentType);
    }

    private void setGzip(HttpResponse response) {
        response.headers().set(HttpHeaderNames.CONTENT_ENCODING, HttpHeaderValues.GZIP);
    }

}