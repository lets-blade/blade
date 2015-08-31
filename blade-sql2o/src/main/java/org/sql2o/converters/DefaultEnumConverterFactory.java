package org.sql2o.converters;

/**
 * Default implementation of {@link EnumConverterFactory},
 * used by sql2o to convert a value from the database into an {@link Enum}.
 */
public class DefaultEnumConverterFactory implements EnumConverterFactory {
    public <E extends Enum> Converter<E> newConverter(final Class<E> enumType) {
        return new Converter<E>() {
            @SuppressWarnings("unchecked")
            public E convert(Object val) throws ConverterException {
                if (val == null) {
                    return null;
                }
                try {
                    if (val instanceof String){
                        return (E)Enum.valueOf(enumType, val.toString());
                    } else if (val instanceof Number){
                        return enumType.getEnumConstants()[((Number)val).intValue()];
                    }
                } catch (Throwable t) {
                    throw new ConverterException("Error converting value '" + val.toString() + "' to " + enumType.getName(), t);
                }
                throw new ConverterException("Cannot convert type '" + val.getClass().getName() + "' to an Enum");
            }

            public Object toDatabaseParam(Enum val) {
                return val.toString();
            }
        };
    }
}
