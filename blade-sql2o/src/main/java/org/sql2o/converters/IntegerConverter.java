package org.sql2o.converters;

/**
 * Used by sql2o to convert a value from the database into an {@link Integer}.
 */
public class IntegerConverter extends NumberConverter<Integer>{

    public IntegerConverter(boolean primitive) {
        super(primitive);
    }

    @Override
    protected Integer convertNumberValue(Number val) {
        return val.intValue();
    }

    @Override
    protected Integer convertStringValue(String val) {
        return Integer.parseInt(val);
    }

    @Override
    protected String getTypeDescription() {
        return Integer.class.toString();
    }
}
