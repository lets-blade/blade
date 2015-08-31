package org.sql2o.converters;

/**
 * Used by sql2o to convert a value from the database into a {@link Float}.
 */
public class FloatConverter extends NumberConverter<Float> {

    public FloatConverter(boolean primitive) {
        super(primitive);
    }

    @Override
    protected Float convertNumberValue(Number val) {
        return val.floatValue();
    }

    @Override
    protected Float convertStringValue(String val) {
        return Float.parseFloat(val);
    }

    @Override
    protected String getTypeDescription() {
        return Float.class.toString();
    }
}
