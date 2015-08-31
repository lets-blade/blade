package org.sql2o.quirks;

import org.sql2o.converters.Convert;
import org.sql2o.converters.Converter;
import org.sql2o.quirks.parameterparsing.impl.DefaultSqlParameterParsingStrategy;
import org.sql2o.quirks.parameterparsing.SqlParameterParsingStrategy;

import java.io.InputStream;
import java.sql.*;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * @author aldenquimby@gmail.com
 * @since 4/6/14
 */
public class NoQuirks implements Quirks {
    protected final Map<Class,Converter>  converters;
    private final SqlParameterParsingStrategy sqlParameterParsingStrategy = new DefaultSqlParameterParsingStrategy();

    public NoQuirks(Map<Class, Converter> converters) {
        // protective copy
        // to avoid someone change this collection outside
        // so this makes converters thread-safe
        this.converters = new HashMap<Class, Converter>(converters);
    }

    public NoQuirks() {
        this(Collections.<Class,Converter>emptyMap());
    }

    @SuppressWarnings("unchecked")
    public <E> Converter<E> converterOf(Class<E> ofClass) {
        // if nobody change this collection outside constructor
        // it's thread-safe
        Converter c =  converters.get(ofClass);
        // if no "local" converter let's look in global
        return c!=null?c:Convert.getConverterIfExists(ofClass);

    }

    public String getColumnName(ResultSetMetaData meta, int colIdx) throws SQLException {
        return meta.getColumnLabel(colIdx);
    }

    public boolean returnGeneratedKeysByDefault() {
        return true;
    }

    public void setParameter(PreparedStatement statement, int paramIdx, Object value) throws SQLException {
        statement.setObject(paramIdx, value);
    }

    public void setParameter(PreparedStatement statement, int paramIdx, InputStream value) throws SQLException {
        statement.setBinaryStream(paramIdx, value);
    }

    public void setParameter(PreparedStatement statement, int paramIdx, int value) throws SQLException {
        statement.setInt(paramIdx, value);
    }

    public void setParameter(PreparedStatement statement, int paramIdx, Integer value) throws SQLException {
        if (value == null) {
            statement.setNull(paramIdx, Types.INTEGER);
        } else {
            statement.setInt(paramIdx, value);
        }
    }

    public void setParameter(PreparedStatement statement, int paramIdx, long value) throws SQLException {
        statement.setLong(paramIdx, value);
    }

    public void setParameter(PreparedStatement statement, int paramIdx, Long value) throws SQLException {
        if (value == null) {
            statement.setNull(paramIdx, Types.BIGINT);
        } else {
            statement.setLong(paramIdx, value);
        }
    }

    public void setParameter(PreparedStatement statement, int paramIdx, String value) throws SQLException {
        if (value == null) {
            statement.setNull(paramIdx, Types.VARCHAR);
        } else {
            statement.setString(paramIdx, value);
        }
    }

    public void setParameter(PreparedStatement statement, int paramIdx, Timestamp value) throws SQLException {
        if (value == null) {
            statement.setNull(paramIdx, Types.TIMESTAMP);
        } else {
            statement.setTimestamp(paramIdx, value);
        }
    }

    public void setParameter(PreparedStatement statement, int paramIdx, Time value) throws SQLException {
        if (value == null) {
            statement.setNull(paramIdx, Types.TIME);
        } else {
            statement.setTime(paramIdx, value);
        }
    }

    public Object getRSVal(ResultSet rs, int idx) throws SQLException {
        return rs.getObject(idx);
    }

    @Override
    public void closeStatement(Statement statement) throws SQLException {
        statement.close();
    }

    @Override
    public SqlParameterParsingStrategy getSqlParameterParsingStrategy() {
        return this.sqlParameterParsingStrategy;
    }
}
