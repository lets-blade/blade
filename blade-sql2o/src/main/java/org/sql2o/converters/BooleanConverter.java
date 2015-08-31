package org.sql2o.converters;

/**
 * Created with IntelliJ IDEA.
 * User: lars
 * Date: 6/1/13
 * Time: 10:54 PM
 * To change this template use File | Settings | File Templates.
 */
public class BooleanConverter extends ConverterBase<Boolean> {

    public Boolean convert(Object val) throws ConverterException {
        if (val == null) return null;

        if (val instanceof Boolean) {
            return (Boolean) val;
        }

        if (val instanceof Number) {
            return ((Number)val).intValue() != 0;
        }

        if (val instanceof Character) {
            // cast to char is required to compile with java 8
        	char c = (Character) val;
			return c == 'Y' || c == 'T' || c == 'J';
        }

        if (val instanceof String) {
            String strVal = ((String)val).trim();
            return "Y".equalsIgnoreCase(strVal) || "YES".equalsIgnoreCase(strVal) || "TRUE".equalsIgnoreCase(strVal) ||
                    "T".equalsIgnoreCase(strVal) || "J".equalsIgnoreCase(strVal);
        }

        throw new ConverterException("Don't know how to convert type " + val.getClass().getName() + " to " + Boolean.class.getName());
    }
}
