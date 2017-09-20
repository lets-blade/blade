package com.blade.mvc.ui;

import com.blade.kit.DateKit;
import lombok.AllArgsConstructor;
import lombok.Builder;

/**
 * RestResponse
 *
 * @param <T>
 * @since 2.0.2-beta
 */
@Builder
@AllArgsConstructor
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
    @Builder.Default
    private int code = -1;

    /**
     * 服务器响应时间
     */
    @Builder.Default
    private long timestamp = DateKit.nowUnix();

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

    public static RestResponse ok() {
        return RestResponse.builder().success(true).build();
    }

    public static <T> RestResponse ok(T payload) {
        return RestResponse.builder().success(true).payload(payload).build();
    }

    public static <T> RestResponse ok(T payload, int code) {
        return RestResponse.builder().success(true).payload(payload).code(code).build();
    }

    public static RestResponse fail() {
        return RestResponse.builder().success(false).build();
    }

    public static RestResponse fail(String msg) {
        return RestResponse.builder().success(false).msg(msg).build();
    }

    public static RestResponse fail(int code) {
        return RestResponse.builder().success(false).code(code).build();
    }

    public static RestResponse fail(int code, String msg) {
        return RestResponse.builder().success(false).msg(msg).code(code).build();
    }

}