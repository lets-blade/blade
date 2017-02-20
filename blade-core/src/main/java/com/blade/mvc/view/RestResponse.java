package com.blade.mvc.view;

/**
 * rest返回对象
 *
 * @param <T>
 * @since 1.7.1-alpha
 */
public class RestResponse<T> {

    /**
     * 服务器响应数据
     */
    private T payload;

    /**
     * 请求是否成功
     */
    private boolean success;

    /**
     * 错误信息
     */
    private String msg;

    /**
     * 状态码
     */
    private int code = -1;

    /**
     * 服务器响应时间
     */
    private long timestamp;

    public RestResponse() {
        this.timestamp = System.currentTimeMillis() / 1000;
    }

    public RestResponse(boolean success) {
        this.timestamp = System.currentTimeMillis() / 1000;
        this.success = success;
    }

    public RestResponse(boolean success, T payload) {
        this.timestamp = System.currentTimeMillis() / 1000;
        this.success = success;
        this.payload = payload;
    }

    public RestResponse(boolean success, T payload, int code) {
        this.timestamp = System.currentTimeMillis() / 1000;
        this.success = success;
        this.payload = payload;
        this.code = code;
    }

    public RestResponse(boolean success, String msg) {
        this.timestamp = System.currentTimeMillis() / 1000;
        this.success = success;
        this.msg = msg;
    }

    public RestResponse(boolean success, String msg, int code) {
        this.timestamp = System.currentTimeMillis() / 1000;
        this.success = success;
        this.msg = msg;
        this.code = code;
    }

    public T payload() {
        return payload;
    }

    public void payload(T payload) {
        this.payload = payload;
    }

    public boolean success() {
        return success;
    }

    public void success(boolean success) {
        this.success = success;
    }

    public String msg() {
        return msg;
    }

    public void msg(String msg) {
        this.msg = msg;
    }

    public long timestamp() {
        return timestamp;
    }

    public void timestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public int code() {
        return code;
    }

    public void code(int code) {
        this.code = code;
    }

    public static RestResponse ok(){
        return new RestResponse(true);
    }

    public static <T> RestResponse ok(T payload){
        return new RestResponse(true, payload);
    }

    public static <T> RestResponse ok(int code){
        return new RestResponse(true, null, code);
    }

    public static <T> RestResponse ok(T payload, int code){
        return new RestResponse(true, payload, code);
    }

    public static RestResponse fail(){
        return new RestResponse(false);
    }

    public static RestResponse fail(String msg){
        return new RestResponse(false, msg);
    }

    public static RestResponse fail(int code){
        return new RestResponse(false, null, code);
    }

    public static RestResponse fail(int code, String msg){
        return new RestResponse(false, msg, code);
    }

}