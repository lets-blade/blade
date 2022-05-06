package com.hellokaton.blade.mvc.ui;

import com.hellokaton.blade.kit.DateKit;
import lombok.Data;

import java.io.Serializable;

/**
 * RestResponse
 *
 * @param <T>
 * @since 2.0.2-beta
 */
@Data
public class RestResponse<T> implements Serializable {

    /**
     * Server response data
     */
    private T payload;
    /**
     * Error message
     */
    private String msg;

    /**
     * Status code
     */
    private int code = 0;

    /**
     * Server response time
     */
    private long timestamp;

    /**
     * Request ID
     */
    private String requestId;

    public RestResponse() {
        this.timestamp = DateKit.nowUnix();
    }

    public RestResponse(int code) {
        this.timestamp = DateKit.nowUnix();
        this.code = code;
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

    public static <T> RestResponse<T> success() {
        return ok();
    }

    public static <T> RestResponse<T> success(T payload) {
        return ok(payload);
    }

    public static <T> RestResponse<T> ok() {
        return new RestResponse<>();
    }

    public static <T> RestResponse<T> ok(T payload) {
        return new RestResponse<T>().payload(payload);
    }

    public static <T> RestResponse<T> ok(T payload, int code) {
        return new RestResponse<T>().payload(payload).code(code);
    }

    public static <T> RestResponse<T> fail() {
        return new RestResponse<T>(-1);
    }

    public static <T> RestResponse<T> fail(String message) {
        return fail(-1, message);
    }

    public static <T> RestResponse<T> fail(int code, String message) {
        return new RestResponse<T>(code).message(message);
    }

    public boolean isSuccess() {
        return this.code == 0;
    }

}