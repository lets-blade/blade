package org.sql2o.converters;

/**
 * Represents a converter.
 */
public interface Converter<T> {

    /**
     * Conversion from SQL to Java.
     */
    T convert(Object val) throws ConverterException;

    /**
     * Conversion from Java to SQL.
     */
    Object toDatabaseParam(T val);
}
