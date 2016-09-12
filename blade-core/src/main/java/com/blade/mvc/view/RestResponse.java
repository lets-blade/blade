package com.blade.mvc.view;

/**
 * rest返回对象
 *
 * @param <T>
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
     * 服务器响应时间
     */
    private long timestamp;

    public static <T> RestResponse<T> build(T data){
        RestResponse<T> r = new RestResponse<T>();
        r.setPayload(data);
        r.setSuccess(true);
        return r;
    }
    public RestResponse() {
        this.timestamp = System.currentTimeMillis() / 1000;
    }

    public RestResponse(T payload) {
        this.success = true;
        this.payload = payload;
        this.timestamp = System.currentTimeMillis() / 1000;
    }

    public RestResponse(boolean success) {
        this.success = success;
        this.timestamp = System.currentTimeMillis() / 1000;
    }

    public RestResponse(String msg) {
        this.success = false;
        this.msg = msg;
        this.timestamp = System.currentTimeMillis() / 1000;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public boolean isSuccess() {
        return success;
    }


    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public T getPayload() {
        return payload;
    }

    public void setPayload(T payload) {
        this.setPayload(payload, true);
    }

    public void setPayload(T payload, boolean success) {
        this.payload = payload;
        this.success = success;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    @Override
    public String toString() {
        return "RestResponse{" +
                "payload=" + payload +
                ", success=" + success +
                ", msg=" + msg +
                ", timestamp=" + timestamp +
                '}';
    }

    public void error(String msg) {
        this.msg = msg;
        this.success = false;
    }
}