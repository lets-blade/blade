package org.sql2o.converters;

/**
 * Base class for numeric converters.
 */
public abstract class NumberConverter<V extends Number> extends ConverterBase<V> {

    private boolean isPrimitive;

    public NumberConverter(boolean primitive) {
        isPrimitive = primitive;
    }

    public V convert(Object val) {
        if (val == null) {
            return isPrimitive ? convertNumberValue(0) : null;
        }

        // val.getClass().isPrimitive() is ALWAYS false
        // since boxing (i.e. Object val=(int)1;)
        // changes type from Integet.TYPE to Integer.class
        // learn 2 java :)

        else if (/*val.getClass().isPrimitive() || */val instanceof Number ) {
            return convertNumberValue((Number)val);
        }
        else if (val instanceof String){
            String stringVal = ((String)val).trim();
            stringVal = stringVal.isEmpty() ? null : stringVal;

            if (stringVal == null) {
                return isPrimitive ? convertNumberValue(0) : null;
            }

            return convertStringValue(stringVal);
        }
        else{
            throw new IllegalArgumentException("Cannot convert type " + val.getClass().toString() + " to " + getTypeDescription());
        }
    }

    protected abstract V convertNumberValue(Number val);
    
    protected abstract V convertStringValue(String val);
    
    protected abstract String getTypeDescription();
}
