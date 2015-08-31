package org.sql2o.converters;

/**
 * Used by sql2o to convert a value from the database into a {@link Long}.
 */
public class LongConverter extends NumberConverter<Long> {

    public LongConverter(boolean primitive) {
        super(primitive);
    }

    @Override
    protected Long convertNumberValue(Number val) {
        return val.longValue();
    }

    @Override
    protected Long convertStringValue(String val) {
        return Long.parseLong(val);
    }

    @Override
    protected String getTypeDescription() {
        return Long.class.toString();
    }
}
