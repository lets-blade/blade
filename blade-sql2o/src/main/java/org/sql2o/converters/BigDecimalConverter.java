package org.sql2o.converters;

import java.math.BigDecimal;

/**
 * Used by sql2o to convert a value from the database into a {@link BigDecimal}.
 */
public class BigDecimalConverter extends NumberConverter<BigDecimal>{

    public BigDecimalConverter() {
        super(false);
    }

    @Override
    protected BigDecimal convertNumberValue(Number val) {
        if (val instanceof BigDecimal){
            return (BigDecimal)val;
        }
        else{
            return BigDecimal.valueOf(val.doubleValue());
        }
    }

    @Override
    protected BigDecimal convertStringValue(String val) {
        return BigDecimal.valueOf(Double.parseDouble(val));
    }

    @Override
    protected String getTypeDescription() {
        return BigDecimal.class.toString();
    }
}
