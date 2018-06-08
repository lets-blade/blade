package com.blade.mvc.ui;

import com.blade.kit.DateKit;
import com.blade.kit.StringKit;
import lombok.Builder;
import lombok.Data;

/**
 * RestResponse
 *
 * @param <T>
 * @since 2.0.2-beta
 */
@Data
public class RestResponse<T> {

    /**
     * Server response data
     */
    private T payload;

    /**
     * The request was successful
     */
    private boolean success;

    /**
     * Error message
     */
    private String msg;

    /**
     * Status code
     */
    @Builder.Default
    private int code = -1;

    /**
     * Server response time
     */
    private long timestamp;

    public RestResponse() {
        this.timestamp = DateKit.nowUnix();
    }

    public RestResponse(boolean success) {
        this.timestamp = DateKit.nowUnix();
        this.success = success;
    }

    public RestResponse(boolean success, T payload) {
        this.timestamp = DateKit.nowUnix();
        this.success = success;
        this.payload = payload;
    }

    public T getPayload() {
        return payload;
    }

    public boolean isSuccess() {
        return success;
    }

    public String getMsg() {
        return msg;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public RestResponse<T> peek(Runnable runnable) {
        runnable.run();
        return this;
    }

    public RestResponse<T> success(boolean success) {
        this.success = success;
        return this;
    }

    public RestResponse<T> payload(T payload) {
        this.payload = payload;
        return this;
    }

    public RestResponse<T> code(int code) {
        this.code = code;
        return this;
    }

    public RestResponse<T> message(String msg) {
        this.msg = msg;
        return this;
    }

    public static <T> RestResponse<T> ok() {
        return new RestResponse<T>().success(true);
    }

    public static <T> RestResponse<T> ok(T payload) {
        return new RestResponse<T>().success(true).payload(payload);
    }

    public static <T> RestResponse ok(T payload, int code) {
        return new RestResponse<T>().success(true).payload(payload).code(code);
    }

    public static <T> RestResponse<T> fail() {
        return new RestResponse<T>().success(false);
    }

    public static <T> RestResponse<T> fail(String message) {
        return new RestResponse<T>().success(false).message(message);
    }

    public static <T> RestResponse fail(int code, String message) {
        return new RestResponse<T>().success(false).message(message).code(code);
    }

}