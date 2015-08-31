package org.sql2o;

/**
 * Iterable {@link java.sql.ResultSet}. Needs to be closeable, because allowing manual
 * iteration means it's impossible to know when to close the ResultSet and Connection.
 *
 * @author aldenquimby@gmail.com
 */
public interface ResultSetIterable<T> extends Iterable<T>, AutoCloseable {
    // override close to not throw
    void close();

    boolean isAutoCloseConnection();
    void setAutoCloseConnection(boolean autoCloseConnection);
}
