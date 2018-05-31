package com.blade.mvc;

import com.blade.Blade;
import com.blade.mvc.http.Request;
import com.blade.mvc.http.Response;
import io.netty.util.concurrent.FastThreadLocal;

/**
 * Blade Web Context
 * <p>
 * Cached current thread context request and response instance
 *
 * @author biezhi
 * 2017/6/1
 */
public class WebContext {

    /**
     * ThreadLocal, used netty fast theadLocal
     */
    private static final FastThreadLocal<WebContext> fastThreadLocal = new FastThreadLocal<>();

    /**
     * Blade instance, when the project is initialized when it will permanently reside in memory
     */
    private static Blade blade;

    /**
     * ContextPath, default is "/"
     */
    private static String contextPath;

    /**
     * Http Request instance of current thread context
     */
    private Request request;

    /**
     * Http Response instance of current thread context
     */
    private Response response;

    public WebContext(Request request, Response response) {
        this.request = request;
        this.response = response;
    }

    /**
     * Set current thread context WebContext instance
     *
     * @param webContext webContext instance
     */
    public static void set(WebContext webContext) {
        fastThreadLocal.set(webContext);
    }

    /**
     * Get current thread context WebContext instance
     *
     * @return WebContext instance
     */
    public static WebContext get() {
        return fastThreadLocal.get();
    }

    /**
     * Remove current thread context WebContext instance
     */
    public static void remove() {
        fastThreadLocal.remove();
    }

    /**
     * Get current thread context Request instance
     *
     * @return Request instance
     */
    public static Request request() {
        WebContext webContext = get();
        return null != webContext ? webContext.request : null;
    }

    /**
     * Get current thread context Response instance
     *
     * @return Response instance
     */
    public static Response response() {
        WebContext webContext = get();
        return null != webContext ? webContext.response : null;
    }

    /**
     * Initializes the project when it starts
     *
     * @param blade       Blade instance
     * @param contextPath context path
     */
    public static void init(Blade blade, String contextPath) {
        WebContext.blade = blade;
        WebContext.contextPath = contextPath;
    }

    /**
     * Get blade instance
     *
     * @return return Blade
     */
    public static Blade blade() {
        return blade;
    }

    public static SessionManager sessionManager() {
        return null != blade() ? blade().sessionManager() : null;
    }

    /**
     * Get context path
     *
     * @return return context path string, e.g: /
     */
    public static String contextPath() {
        return contextPath;
    }

    public static void clean() {
        fastThreadLocal.remove();
        blade = null;
    }

}
