package org.sql2o.converters;

import java.util.UUID;

/**
 * Used by sql2o to convert a value from the database into a {@link UUID}.
 */
public class UUIDConverter extends ConverterBase<UUID> {
    public UUID convert(Object val) throws ConverterException {
        if (val == null){
            return null;
        }

        if (val instanceof UUID){
            return (UUID)val;
        }

        if(val instanceof String){
            return UUID.fromString((String) val);
        }

        throw new ConverterException("Cannot convert type " + val.getClass() + " " + UUID.class);
    }
}

