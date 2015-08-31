package org.sql2o.converters;

import org.sql2o.tools.IOUtils;

import java.io.IOException;
import java.io.Reader;
import java.sql.Clob;
import java.sql.SQLException;

/**
 * Used by sql2o to convert a value from the database into a {@link String}.
 */
public class StringConverter extends ConverterBase<String> {

    public String convert(Object val) throws ConverterException {
        if (val == null){
            return null;
        }

        if (val instanceof Clob) {
            Clob clobVal = (Clob)val;
            try
            {
                try {
                    return clobVal.getSubString(1, (int)clobVal.length());
                } catch (SQLException e) {
                    throw new ConverterException("error converting clob to String", e);
                }
            } finally {
                try {
                    clobVal.free();
                } catch (Throwable ignore) {
                    //ignore
                }
            }
        }

        if(val instanceof Reader){
            Reader reader = (Reader) val;
            try {
                try {
                    return IOUtils.toString(reader);
                } catch (IOException e) {
                    throw new ConverterException("error converting reader to String", e);
                }
            } finally {
                try {
                    reader.close();
                } catch (Throwable ignore) {
                    // ignore
                }
            }
        }

        return val.toString().trim();
    }
}
