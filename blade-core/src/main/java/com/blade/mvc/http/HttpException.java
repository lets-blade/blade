package com.blade.mvc.http;

import java.io.IOException;

public class HttpException extends IOException {

    /**
     * Serialization marker.
     */
    private static final long serialVersionUID = -505306086879848229L;

    /**
     * Status code.
     */
    private final transient int status;

    /**
     * Ctor.
     *
     * @param code HTTP status code
     */
    public HttpException(final int code) {
        super(Integer.toString(code));
        this.status = code;
    }

    /**
     * Ctor.
     *
     * @param code  HTTP status code
     * @param cause Cause of the problem
     */
    public HttpException(final int code, final String cause) {
        super(String.format("[%03d] %s", code, cause));
        this.status = code;
    }

    /**
     * Ctor.
     *
     * @param cause Cause of the problem
     * @param code  HTTP status code
     */
    public HttpException(final Throwable cause, final int code) {
        super(cause);
        this.status = code;
    }

    /**
     * Ctor.
     *
     * @param code  HTTP status code
     * @param msg   Exception message
     * @param cause Cause of the problem
     */
    public HttpException(final int code, final String msg,
                         final Throwable cause) {
        super(String.format("[%03d] %s", code, msg), cause);
        this.status = code;
    }

    /**
     * HTTP status code.
     *
     * @return Code
     */
    public final int code() {
        return this.status;
    }

}
