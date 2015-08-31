package org.sql2o;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * User: dimzon
 * Date: 4/7/14
 * Time: 11:06 PM
 */
public class DelegatingResultSetHandler<E> implements ResultSetHandler<E> {
    private volatile ResultSetHandler<E> inner = null;
    private final ResultSetHandlerFactory<E> factory;

    public DelegatingResultSetHandler(ResultSetHandlerFactory<E> factory) {
        this.factory = factory;
    }

    public E handle(ResultSet resultSet) throws SQLException {
        if(inner==null) inner = factory.newResultSetHandler(resultSet.getMetaData());
        return inner.handle(resultSet);
    }
}
