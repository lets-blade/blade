package org.sql2o.converters;

import java.sql.Timestamp;
import java.util.Date;

/**
 * Used by sql2o to convert a value from the database into a {@link Date}.
 */
public abstract class AbstractDateConverter<E extends Date> implements Converter<E> {
    private final Class<E> classOfDate;
    protected AbstractDateConverter(Class<E> classOfDate) {
        this.classOfDate = classOfDate;
    }

    protected abstract E fromMilliseconds(long millisecond);

    @SuppressWarnings("unchecked")
    public E convert(Object val) throws ConverterException {
        if (val == null){
            return null;
        }

        if (classOfDate.isInstance(val)){
            return (E) val;
        }

        if(val instanceof java.util.Date){
            return fromMilliseconds(((Date) val).getTime());
        }

        if (val instanceof Number){
            return fromMilliseconds(((Number) val).longValue());
        }

        throw new ConverterException("Cannot convert type " + val.getClass().toString() + " to java.util.Date");
    }

    public Timestamp toDatabaseParam(Date val) {
        if(val==null) return null;
        return (val instanceof Timestamp)
                ? (Timestamp) val
                :new Timestamp(val.getTime());
    }
}
