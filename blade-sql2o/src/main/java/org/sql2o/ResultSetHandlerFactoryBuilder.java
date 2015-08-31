package org.sql2o;

import org.sql2o.quirks.Quirks;

import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: dimzon
 * Date: 4/7/14
 * Time: 4:28 AM
 * To change this template use File | Settings | File Templates.
 */
public interface ResultSetHandlerFactoryBuilder {
    boolean isCaseSensitive();

    void setCaseSensitive(boolean caseSensitive);

    boolean isAutoDeriveColumnNames();

    void setAutoDeriveColumnNames(boolean autoDeriveColumnNames);

    boolean isThrowOnMappingError();

    void throwOnMappingError(boolean throwOnMappingError);

    Map<String, String> getColumnMappings();

    void setColumnMappings(Map<String, String> columnMappings);

    Quirks getQuirks();

    void setQuirks(Quirks quirksMode);

    <E> ResultSetHandlerFactory<E> newFactory(Class<E> clazz);
}
