package org.sql2o.converters;

/**
 * Used by sql2o to convert a value from the database into a {@link Short}.
 */
public class ShortConverter extends NumberConverter<Short> {

    public ShortConverter(boolean primitive) {
        super(primitive);
    }

    @Override
    protected Short convertNumberValue(Number val) {
        return val.shortValue();
    }

    @Override
    protected Short convertStringValue(String val) {
        return Short.parseShort(val);
    }

    @Override
    protected String getTypeDescription() {
        return Short.class.toString();
    }
}
