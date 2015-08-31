package org.sql2o.quirks;


import org.sql2o.converters.Converter;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Map;

public class OracleQuirks extends NoQuirks {
    public OracleQuirks() {
        super();
    }

    public OracleQuirks(Map<Class, Converter> converters) {
        super(converters);
    }

    @Override
    public Object getRSVal(ResultSet rs, int idx) throws SQLException {
        Object o = super.getRSVal(rs, idx);
        // oracle timestamps are not always convertible to a java Date. If ResultSet.getTimestamp is used instead of
        // ResultSet.getObject, a normal java.sql.Timestamp instance is returnd.
        if (o != null && o.getClass().getCanonicalName().startsWith("oracle.sql.TIMESTAMP")){
            //TODO: move to sql2o-oracle
            //TODO: use TIMESTAMP.dateValue
            o = rs.getTimestamp(idx);
        }
        return o;
    }

    @Override
    public boolean returnGeneratedKeysByDefault() {
        return false;
    }
}
