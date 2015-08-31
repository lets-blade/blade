package org.sql2o;

/**
 * Represents an exception thrown by Sql2o.
 */
public class Sql2oException extends RuntimeException {

    public Sql2oException() {
    }

    public Sql2oException(String message) {
        super(message);
    }

    public Sql2oException(String message, Throwable cause) {
        super(message, cause);
    }

    public Sql2oException(Throwable cause) {
        super(cause);
    }
}
