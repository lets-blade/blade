package org.sql2o.converters.joda;

import org.joda.time.LocalTime;
import org.sql2o.converters.Converter;
import org.sql2o.converters.ConverterException;

import java.sql.Timestamp;

/**
 * Created by lars on 12/18/13.
 */
public class LocalTimeConverter implements Converter<LocalTime> {

    public LocalTime convert(Object val) throws ConverterException {
        if (val == null) {
            return null;
        }
        try {
            // Joda has it's own pluggable converters infrastructure
            // it will throw IllegalArgumentException if can't convert
            // look @ org.joda.time.convert.ConverterManager
            return new LocalTime(val);
        } catch (IllegalArgumentException ex) {
            throw new ConverterException("Don't know how to convert from type '" + val.getClass().getName() + "' to type '" + LocalTime.class.getName() + "'", ex);
        }
    }

    public Object toDatabaseParam(LocalTime val) {
        return new Timestamp(val.toDateTimeToday().getMillis());
    }
}
