package org.sql2o.converters;

import java.util.Date;

/**
 * Used by sql2o to convert a value from the database into a {@link Date}.
 */
public class DateConverter extends AbstractDateConverter<Date> {
    public static final DateConverter instance = new DateConverter();

    public DateConverter() {
        super(Date.class);
    }

    @Override
    protected Date fromMilliseconds(long millisecond) {
        return new Date(millisecond);
    }
}
