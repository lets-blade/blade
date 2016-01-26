package com.blade.route;

import com.blade.Blade;

/**
 * @author XieEnlong
 * @date 2016/1/26.
 */
public abstract class RouteGroup {

    private String prefix;

    private Blade blade;

    /**
     * Register a GET request route
     * @param path		route path, request url
     * @param handler	execute route Handle
     */
    public void get(String path, RouteHandler handler) {
        blade.get(formatPath(prefix, path), handler);
    }

    /**
     * Register a POST request route
     *
     * @param path		route path, request url
     * @param handler	execute route Handle
     */
    public void post(String path, RouteHandler handler){
        blade.post(formatPath(prefix, path), handler);
    }

    /**
     * Register a DELETE request route
     *
     * @param path		route path, request url
     * @param handler	execute route Handle
     */
    public void delete(String path, RouteHandler handler){
        blade.delete(formatPath(prefix, path), handler);
    }

    /**
     * Register a PUT request route
     *
     * @param path		route path, request url
     * @param handler	execute route Handle
     */
    public void put(String path, RouteHandler handler){
        blade.put(formatPath(prefix, path), handler);
    }

    public abstract void route();

    public void merge(String path, Blade blade) {
        this.prefix = path;
        this.blade = blade;
        route();
    }

    public static String formatPath(String prefix, String path) {
        if (prefix.endsWith("/")) {
            if (path.startsWith("/")) {
                return prefix + path.substring(1);
            } else {
                return prefix + path;
            }
        } else {
            if (path.startsWith("/")) {
                return prefix + path;
            } else {
                return prefix + "/" + path;
            }
        }
    }
}
