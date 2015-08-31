package org.sql2o.converters;

/**
 * Used by sql2o to convert a value from the database into a  {@link Double}.
 */
public class DoubleConverter extends NumberConverter<Double> {

    public DoubleConverter(boolean primitive) {
        super(primitive);
    }

    @Override
    protected Double convertNumberValue(Number val) {
        return  val.doubleValue();
    }

    @Override
    protected Double convertStringValue(String val) {
        return Double.parseDouble(val);
    }

    @Override
    protected String getTypeDescription() {
        return Double.class.toString();
    }
}
