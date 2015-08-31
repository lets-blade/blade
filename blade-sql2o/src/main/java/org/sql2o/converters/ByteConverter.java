package org.sql2o.converters;

/**
 * Used by sql2o to convert a value from the database into a  {@link Byte}.
 */
public class ByteConverter extends NumberConverter<Byte> {

    public ByteConverter(boolean primitive) {
        super(primitive);
    }

    @Override
    protected Byte convertNumberValue(Number val) {
        return val.byteValue();
    }

    @Override
    protected Byte convertStringValue(String val) {
        return Byte.parseByte(val);
    }

    @Override
    protected String getTypeDescription() {
        return Byte.class.toString();
    }
}
