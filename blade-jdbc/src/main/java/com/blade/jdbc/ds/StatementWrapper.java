package com.blade.jdbc.ds;

import java.io.InputStream;
import java.io.Reader;
import java.math.BigDecimal;
import java.net.URL;
import java.sql.Array;
import java.sql.Blob;
import java.sql.CallableStatement;
import java.sql.Clob;
import java.sql.Connection;
import java.sql.Date;
import java.sql.NClob;
import java.sql.ParameterMetaData;
import java.sql.PreparedStatement;
import java.sql.Ref;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.RowId;
import java.sql.SQLException;
import java.sql.SQLWarning;
import java.sql.SQLXML;
import java.sql.Statement;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.Calendar;
import java.util.Map;

class StatementWrapper implements CallableStatement {
    
	final Statement delegate;
    final ConnectionWrapper connection;
    
    StatementWrapper(Statement delegate, ConnectionWrapper connection) {
        this.delegate = delegate;
        this.connection = connection;
    }
    
    @Override
    public void registerOutParameter(int parameterIndex, int sqlType) throws SQLException {
        try {
            ((CallableStatement) delegate).registerOutParameter(parameterIndex, sqlType);
        } catch (Throwable e) {
            throw handleException(e);
        }
    }

    @Override
    public void registerOutParameter(int parameterIndex, int sqlType, int scale) throws SQLException {
        try {
            ((CallableStatement) delegate).registerOutParameter(parameterIndex, sqlType, scale);
        } catch (Throwable e) {
            throw handleException(e);
        }
    }

    @Override
    public boolean wasNull() throws SQLException {
        try {
            return ((CallableStatement) delegate).wasNull();
        } catch (Throwable e) {
            throw handleException(e);
        }
    }

    @Override
    public String getString(int parameterIndex) throws SQLException {
        try {
            return ((CallableStatement) delegate).getString(parameterIndex);
        } catch (Throwable e) {
            throw handleException(e);
        }
    }

    @Override
    public boolean getBoolean(int parameterIndex) throws SQLException {
        try {
            return ((CallableStatement) delegate).getBoolean(parameterIndex);
        } catch (Throwable e) {
            throw handleException(e);
        }
    }

    @Override
    public byte getByte(int parameterIndex) throws SQLException {
        try {
            return ((CallableStatement) delegate).getByte(parameterIndex);
        } catch (Throwable e) {
            throw handleException(e);
        }
    }

    @Override
    public short getShort(int parameterIndex) throws SQLException {
        try {
            return ((CallableStatement) delegate).getShort(parameterIndex);
        } catch (Throwable e) {
            throw handleException(e);
        }
    }

    @Override
    public int getInt(int parameterIndex) throws SQLException {
        try {
            return ((CallableStatement) delegate).getInt(parameterIndex);
        } catch (Throwable e) {
            throw handleException(e);
        }
    }

    @Override
    public long getLong(int parameterIndex) throws SQLException {
        try {
            return ((CallableStatement) delegate).getLong(parameterIndex);
        } catch (Throwable e) {
            throw handleException(e);
        }
    }

    @Override
    public float getFloat(int parameterIndex) throws SQLException {
        try {
            return ((CallableStatement) delegate).getFloat(parameterIndex);
        } catch (Throwable e) {
            throw handleException(e);
        }
    }

    @Override
    public double getDouble(int parameterIndex) throws SQLException {
        try {
            return ((CallableStatement) delegate).getDouble(parameterIndex);
        } catch (Throwable e) {
            throw handleException(e);
        }
    }

    @Override
	public BigDecimal getBigDecimal(int parameterIndex, int scale) throws SQLException {
        try {
        	return ((CallableStatement) delegate).getBigDecimal(parameterIndex);
        } catch (Throwable e) {
            throw handleException(e);
        }
    }

    @Override
    public byte[] getBytes(int parameterIndex) throws SQLException {
        try {
            return ((CallableStatement) delegate).getBytes(parameterIndex);
        } catch (Throwable e) {
            throw handleException(e);
        }
    }

    @Override
    public Date getDate(int parameterIndex) throws SQLException {
        try {
            return ((CallableStatement) delegate).getDate(parameterIndex);
        } catch (Throwable e) {
            throw handleException(e);
        }
    }

    @Override
    public Time getTime(int parameterIndex) throws SQLException {
        try {
            return ((CallableStatement) delegate).getTime(parameterIndex);
        } catch (Throwable e) {
            throw handleException(e);
        }
    }

    @Override
    public Timestamp getTimestamp(int parameterIndex) throws SQLException {
        try {
            return ((CallableStatement) delegate).getTimestamp(parameterIndex);
        } catch (Throwable e) {
            throw handleException(e);
        }
    }

    @Override
    public Object getObject(int parameterIndex) throws SQLException {
        try {
            return ((CallableStatement) delegate).getObject(parameterIndex);
        } catch (Throwable e) {
            throw handleException(e);
        }
    }

    @Override
    public BigDecimal getBigDecimal(int parameterIndex) throws SQLException {
        try {
            return ((CallableStatement) delegate).getBigDecimal(parameterIndex);
        } catch (Throwable e) {
            throw handleException(e);
        }
    }

    @Override
    public Object getObject(int parameterIndex, Map<String, Class<?>> map) throws SQLException {
        try {
            return ((CallableStatement) delegate).getObject(parameterIndex, map);
        } catch (Throwable e) {
            throw handleException(e);
        }
    }

    @Override
    public Ref getRef(int parameterIndex) throws SQLException {
        try {
            return ((CallableStatement) delegate).getRef(parameterIndex);
        } catch (Throwable e) {
            throw handleException(e);
        }
    }

    @Override
    public Blob getBlob(int parameterIndex) throws SQLException {
        try {
            return ((CallableStatement) delegate).getBlob(parameterIndex);
        } catch (Throwable e) {
            throw handleException(e);
        }
    }

    @Override
    public Clob getClob(int parameterIndex) throws SQLException {
        try {
            return ((CallableStatement) delegate).getClob(parameterIndex);
        } catch (Throwable e) {
            throw handleException(e);
        }
    }

    @Override
    public Array getArray(int parameterIndex) throws SQLException {
        try {
            return ((CallableStatement) delegate).getArray(parameterIndex);
        } catch (Throwable e) {
            throw handleException(e);
        }
    }

    @Override
    public Date getDate(int parameterIndex, Calendar cal) throws SQLException {
        try {
            return ((CallableStatement) delegate).getDate(parameterIndex, cal);
        } catch (Throwable e) {
            throw handleException(e);
        }
    }

    @Override
    public Time getTime(int parameterIndex, Calendar cal) throws SQLException {
        try {
            return ((CallableStatement) delegate).getTime(parameterIndex, cal);
        } catch (Throwable e) {
            throw handleException(e);
        }
    }

    @Override
    public Timestamp getTimestamp(int parameterIndex, Calendar cal) throws SQLException {
        try {
            return ((CallableStatement) delegate).getTimestamp(parameterIndex, cal);
        } catch (Throwable e) {
            throw handleException(e);
        }
    }

    @Override
    public void registerOutParameter(int parameterIndex, int sqlType, String typeName) throws SQLException {
        try {
            ((CallableStatement) delegate).registerOutParameter(parameterIndex, sqlType, typeName);
        } catch (Throwable e) {
            throw handleException(e);
        }
    }

    @Override
    public void registerOutParameter(String parameterName, int sqlType) throws SQLException {
        try {
            ((CallableStatement) delegate).registerOutParameter(parameterName, sqlType);
        } catch (Throwable e) {
            throw handleException(e);
        }
    }

    @Override
    public void registerOutParameter(String parameterName, int sqlType, int scale) throws SQLException {
        try {
            ((CallableStatement) delegate).registerOutParameter(parameterName, sqlType, scale);
        } catch (Throwable e) {
            throw handleException(e);
        }
    }

    @Override
    public void registerOutParameter(String parameterName, int sqlType, String typeName) throws SQLException {
        try {
            ((CallableStatement) delegate).registerOutParameter(parameterName, sqlType, typeName);
        } catch (Throwable e) {
            throw handleException(e);
        }
    }

    @Override
    public URL getURL(int parameterIndex) throws SQLException {
        try {
            return ((CallableStatement) delegate).getURL(parameterIndex);
        } catch (Throwable e) {
            throw handleException(e);
        }
    }

    @Override
    public void setURL(String parameterName, URL val) throws SQLException {
        try {
            ((CallableStatement) delegate).setURL(parameterName, val);
        } catch (Throwable e) {
            throw handleException(e);
        }
    }

    @Override
    public void setNull(String parameterName, int sqlType) throws SQLException {
        try {
            ((CallableStatement) delegate).setNull(parameterName, sqlType);
        } catch (Throwable e) {
            throw handleException(e);
        }
    }

    @Override
    public void setBoolean(String parameterName, boolean x) throws SQLException {
        try {
            ((CallableStatement) delegate).setBoolean(parameterName, x);
        } catch (Throwable e) {
            throw handleException(e);
        }
    }

    @Override
    public void setByte(String parameterName, byte x) throws SQLException {
        try {
            ((CallableStatement) delegate).setByte(parameterName, x);
        } catch (Throwable e) {
            throw handleException(e);
        }
    }

    @Override
    public void setShort(String parameterName, short x) throws SQLException {
        try {
            ((CallableStatement) delegate).setShort(parameterName, x);
        } catch (Throwable e) {
            throw handleException(e);
        }
    }

    @Override
    public void setInt(String parameterName, int x) throws SQLException {
        try {
            ((CallableStatement) delegate).setInt(parameterName, x);
        } catch (Throwable e) {
            throw handleException(e);
        }
    }

    @Override
    public void setLong(String parameterName, long x) throws SQLException {
        try {
            ((CallableStatement) delegate).setLong(parameterName, x);
        } catch (Throwable e) {
            throw handleException(e);
        }
    }

    @Override
    public void setFloat(String parameterName, float x) throws SQLException {
        try {
            ((CallableStatement) delegate).setFloat(parameterName, x);
        } catch (Throwable e) {
            throw handleException(e);
        }
    }

    @Override
    public void setDouble(String parameterName, double x) throws SQLException {
        try {
            ((CallableStatement) delegate).setDouble(parameterName, x);
        } catch (Throwable e) {
            throw handleException(e);
        }
    }

    @Override
    public void setBigDecimal(String parameterName, BigDecimal x) throws SQLException {
        try {
            ((CallableStatement) delegate).setBigDecimal(parameterName, x);
        } catch (Throwable e) {
            throw handleException(e);
        }
    }

    @Override
    public void setString(String parameterName, String x) throws SQLException {
        try {
            ((CallableStatement) delegate).setString(parameterName, x);
        } catch (Throwable e) {
            throw handleException(e);
        }
    }

    @Override
    public void setBytes(String parameterName, byte[] x) throws SQLException {
        try {
            ((CallableStatement) delegate).setBytes(parameterName, x);
        } catch (Throwable e) {
            throw handleException(e);
        }
    }

    @Override
    public void setDate(String parameterName, Date x) throws SQLException {
        try {
            ((CallableStatement) delegate).setDate(parameterName, x);
        } catch (Throwable e) {
            throw handleException(e);
        }
    }

    @Override
    public void setTime(String parameterName, Time x) throws SQLException {
        try {
            ((CallableStatement) delegate).setTime(parameterName, x);
        } catch (Throwable e) {
            throw handleException(e);
        }
    }

    @Override
    public void setTimestamp(String parameterName, Timestamp x) throws SQLException {
        try {
            ((CallableStatement) delegate).setTimestamp(parameterName, x);
        } catch (Throwable e) {
            throw handleException(e);
        }
    }

    @Override
    public void setAsciiStream(String parameterName, InputStream x, int length) throws SQLException {
        try {
            ((CallableStatement) delegate).setAsciiStream(parameterName, x, length);
        } catch (Throwable e) {
            throw handleException(e);
        }
    }

    @Override
    public void setBinaryStream(String parameterName, InputStream x, int length) throws SQLException {
        try {
            ((CallableStatement) delegate).setBinaryStream(parameterName, x, length);
        } catch (Throwable e) {
            throw handleException(e);
        }
    }

    @Override
    public void setObject(String parameterName, Object x, int targetSqlType, int scale) throws SQLException {
        try {
            ((CallableStatement) delegate).setObject(parameterName, x, targetSqlType, scale);
        } catch (Throwable e) {
            throw handleException(e);
        }
    }

    @Override
    public void setObject(String parameterName, Object x, int targetSqlType) throws SQLException {
        try {
            ((CallableStatement) delegate).setObject(parameterName, x, targetSqlType);
        } catch (Throwable e) {
            throw handleException(e);
        }
    }

    @Override
    public void setObject(String parameterName, Object x) throws SQLException {
        try {
            ((CallableStatement) delegate).setObject(parameterName, x);
        } catch (Throwable e) {
            throw handleException(e);
        }
    }

    @Override
    public void setCharacterStream(String parameterName, Reader reader, int length) throws SQLException {
        try {
            ((CallableStatement) delegate).setCharacterStream(parameterName, reader, length);
        } catch (Throwable e) {
            throw handleException(e);
        }
    }

    @Override
    public void setDate(String parameterName, Date x, Calendar cal) throws SQLException {
        try {
            ((CallableStatement) delegate).setDate(parameterName, x, cal);
        } catch (Throwable e) {
            throw handleException(e);
        }
    }

    @Override
    public void setTime(String parameterName, Time x, Calendar cal) throws SQLException {
        try {
            ((CallableStatement) delegate).setTime(parameterName, x, cal);
        } catch (Throwable e) {
            throw handleException(e);
        }
    }

    @Override
    public void setTimestamp(String parameterName, Timestamp x, Calendar cal) throws SQLException {
        try {
            ((CallableStatement) delegate).setTimestamp(parameterName, x, cal);
        } catch (Throwable e) {
            throw handleException(e);
        }
    }

    @Override
    public void setNull(String parameterName, int sqlType, String typeName) throws SQLException {
        try {
            ((CallableStatement) delegate).setNull(parameterName, sqlType, typeName);
        } catch (Throwable e) {
            throw handleException(e);
        }
    }

    @Override
    public String getString(String parameterName) throws SQLException {
        try {
            return ((CallableStatement) delegate).getString(parameterName);
        } catch (Throwable e) {
            throw handleException(e);
        }
    }

    @Override
    public boolean getBoolean(String parameterName) throws SQLException {
        try {
            return ((CallableStatement) delegate).getBoolean(parameterName);
        } catch (Throwable e) {
            throw handleException(e);
        }
    }

    public byte getByte(String parameterName) throws SQLException {
        try {
            return ((CallableStatement) delegate).getByte(parameterName);
        } catch (Throwable e) {
            throw handleException(e);
        }
    }

    public short getShort(String parameterName) throws SQLException {
        try {
            return ((CallableStatement) delegate).getShort(parameterName);
        } catch (Throwable e) {
            throw handleException(e);
        }
    }

    public int getInt(String parameterName) throws SQLException {
        try {
            return ((CallableStatement) delegate).getInt(parameterName);
        } catch (Throwable e) {
            throw handleException(e);
        }
    }

    public long getLong(String parameterName) throws SQLException {
        try {
            return ((CallableStatement) delegate).getLong(parameterName);
        } catch (Throwable e) {
            throw handleException(e);
        }
    }

    public float getFloat(String parameterName) throws SQLException {
        try {
            return ((CallableStatement) delegate).getFloat(parameterName);
        } catch (Throwable e) {
            throw handleException(e);
        }
    }

    public double getDouble(String parameterName) throws SQLException {
        try {
            return ((CallableStatement) delegate).getDouble(parameterName);
        } catch (Throwable e) {
            throw handleException(e);
        }
    }

    public byte[] getBytes(String parameterName) throws SQLException {
        try {
            return ((CallableStatement) delegate).getBytes(parameterName);
        } catch (Throwable e) {
            throw handleException(e);
        }
    }

    public Date getDate(String parameterName) throws SQLException {
        try {
            return ((CallableStatement) delegate).getDate(parameterName);
        } catch (Throwable e) {
            throw handleException(e);
        }
    }

    public Time getTime(String parameterName) throws SQLException {
        try {
            return ((CallableStatement) delegate).getTime(parameterName);
        } catch (Throwable e) {
            throw handleException(e);
        }
    }

    public Timestamp getTimestamp(String parameterName) throws SQLException {
        try {
            return ((CallableStatement) delegate).getTimestamp(parameterName);
        } catch (Throwable e) {
            throw handleException(e);
        }
    }

    public Object getObject(String parameterName) throws SQLException {
        try {
            return ((CallableStatement) delegate).getObject(parameterName);
        } catch (Throwable e) {
            throw handleException(e);
        }
    }

    public BigDecimal getBigDecimal(String parameterName) throws SQLException {
        try {
            return ((CallableStatement) delegate).getBigDecimal(parameterName);
        } catch (Throwable e) {
            throw handleException(e);
        }
    }

    public Object getObject(String parameterName, Map<String, Class<?>> map) throws SQLException {
        try {
            return ((CallableStatement) delegate).getObject(parameterName, map);
        } catch (Throwable e) {
            throw handleException(e);
        }
    }

    public Ref getRef(String parameterName) throws SQLException {
        try {
            return ((CallableStatement) delegate).getRef(parameterName);
        } catch (Throwable e) {
            throw handleException(e);
        }
    }

    public Blob getBlob(String parameterName) throws SQLException {
        try {
            return ((CallableStatement) delegate).getBlob(parameterName);
        } catch (Throwable e) {
            throw handleException(e);
        }
    }

    public Clob getClob(String parameterName) throws SQLException {
        try {
            return ((CallableStatement) delegate).getClob(parameterName);
        } catch (Throwable e) {
            throw handleException(e);
        }
    }

    public Array getArray(String parameterName) throws SQLException {
        try {
            return ((CallableStatement) delegate).getArray(parameterName);
        } catch (Throwable e) {
            throw handleException(e);
        }
    }

    public Date getDate(String parameterName, Calendar cal) throws SQLException {
        try {
            return ((CallableStatement) delegate).getDate(parameterName, cal);
        } catch (Throwable e) {
            throw handleException(e);
        }
    }

    public Time getTime(String parameterName, Calendar cal) throws SQLException {
        try {
            return ((CallableStatement) delegate).getTime(parameterName, cal);
        } catch (Throwable e) {
            throw handleException(e);
        }
    }

    public Timestamp getTimestamp(String parameterName, Calendar cal) throws SQLException {
        try {
            return ((CallableStatement) delegate).getTimestamp(parameterName, cal);
        } catch (Throwable e) {
            throw handleException(e);
        }
    }

    public URL getURL(String parameterName) throws SQLException {
        try {
            return ((CallableStatement) delegate).getURL(parameterName);
        } catch (Throwable e) {
            throw handleException(e);
        }
    }

    public RowId getRowId(int parameterIndex) throws SQLException {
        try {
            return ((CallableStatement) delegate).getRowId(parameterIndex);
        } catch (Throwable e) {
            throw handleException(e);
        }
    }

    public RowId getRowId(String parameterName) throws SQLException {
        try {
            return ((CallableStatement) delegate).getRowId(parameterName);
        } catch (Throwable e) {
            throw handleException(e);
        }
    }

    public void setRowId(String parameterName, RowId x) throws SQLException {
        try {
            ((CallableStatement) delegate).setRowId(parameterName, x);
        } catch (Throwable e) {
            throw handleException(e);
        }
    }

    public void setNString(String parameterName, String value) throws SQLException {
        try {
            ((CallableStatement) delegate).setNString(parameterName, value);
        } catch (Throwable e) {
            throw handleException(e);
        }
    }

    public void setNCharacterStream(String parameterName, Reader value, long length) throws SQLException {
        try {
            ((CallableStatement) delegate).setNCharacterStream(parameterName, value, length);
        } catch (Throwable e) {
            throw handleException(e);
        }
    }

    public void setNClob(String parameterName, NClob value) throws SQLException {
        try {
            ((CallableStatement) delegate).setNClob(parameterName, value);
        } catch (Throwable e) {
            throw handleException(e);
        }
    }

    public void setClob(String parameterName, Reader reader, long length) throws SQLException {
        try {
            ((CallableStatement) delegate).setClob(parameterName, reader, length);
        } catch (Throwable e) {
            throw handleException(e);
        }
    }

    public void setBlob(String parameterName, InputStream inputStream, long length) throws SQLException {
        try {
            ((CallableStatement) delegate).setBlob(parameterName, inputStream, length);
        } catch (Throwable e) {
            throw handleException(e);
        }
    }

    public void setNClob(String parameterName, Reader reader, long length) throws SQLException {
        try {
            ((CallableStatement) delegate).setNClob(parameterName, reader, length);
        } catch (Throwable e) {
            throw handleException(e);
        }
    }

    public NClob getNClob(int parameterIndex) throws SQLException {
        try {
            return ((CallableStatement) delegate).getNClob(parameterIndex);
        } catch (Throwable e) {
            throw handleException(e);
        }
    }

    public NClob getNClob(String parameterName) throws SQLException {
        try {
            return ((CallableStatement) delegate).getNClob(parameterName);
        } catch (Throwable e) {
            throw handleException(e);
        }
    }

    public void setSQLXML(String parameterName, SQLXML xmlObject) throws SQLException {
        try {
            ((CallableStatement) delegate).setSQLXML(parameterName, xmlObject);
        } catch (Throwable e) {
            throw handleException(e);
        }
    }

    public SQLXML getSQLXML(int parameterIndex) throws SQLException {
        try {
            return ((CallableStatement) delegate).getSQLXML(parameterIndex);
        } catch (Throwable e) {
            throw handleException(e);
        }
    }

    public SQLXML getSQLXML(String parameterName) throws SQLException {
        try {
            return ((CallableStatement) delegate).getSQLXML(parameterName);
        } catch (Throwable e) {
            throw handleException(e);
        }
    }

    public String getNString(int parameterIndex) throws SQLException {
        try {
            return ((CallableStatement) delegate).getNString(parameterIndex);
        } catch (Throwable e) {
            throw handleException(e);
        }
    }

    public String getNString(String parameterName) throws SQLException {
        try {
            return ((CallableStatement) delegate).getNString(parameterName);
        } catch (Throwable e) {
            throw handleException(e);
        }
    }

    public Reader getNCharacterStream(int parameterIndex) throws SQLException {
        try {
            return ((CallableStatement) delegate).getNCharacterStream(parameterIndex);
        } catch (Throwable e) {
            throw handleException(e);
        }
    }

    public Reader getNCharacterStream(String parameterName) throws SQLException {
        try {
            return ((CallableStatement) delegate).getNCharacterStream(parameterName);
        } catch (Throwable e) {
            throw handleException(e);
        }
    }

    public Reader getCharacterStream(int parameterIndex) throws SQLException {
        try {
            return ((CallableStatement) delegate).getCharacterStream(parameterIndex);
        } catch (Throwable e) {
            throw handleException(e);
        }
    }

    public Reader getCharacterStream(String parameterName) throws SQLException {
        try {
            return ((CallableStatement) delegate).getCharacterStream(parameterName);
        } catch (Throwable e) {
            throw handleException(e);
        }
    }

    public void setBlob(String parameterName, Blob x) throws SQLException {
        try {
            ((CallableStatement) delegate).setBlob(parameterName, x);
        } catch (Throwable e) {
            throw handleException(e);
        }
    }

    public void setClob(String parameterName, Clob x) throws SQLException {
        try {
            ((CallableStatement) delegate).setClob(parameterName, x);
        } catch (Throwable e) {
            throw handleException(e);
        }
    }

    public void setAsciiStream(String parameterName, InputStream x, long length) throws SQLException {
        try {
            ((CallableStatement) delegate).setAsciiStream(parameterName, x, length);
        } catch (Throwable e) {
            throw handleException(e);
        }
    }

    public void setBinaryStream(String parameterName, InputStream x, long length) throws SQLException {
        try {
            ((CallableStatement) delegate).setBinaryStream(parameterName, x, length);
        } catch (Throwable e) {
            throw handleException(e);
        }
    }

    public void setCharacterStream(String parameterName, Reader reader, long length) throws SQLException {
        try {
            ((CallableStatement) delegate).setCharacterStream(parameterName, reader, length);
        } catch (Throwable e) {
            throw handleException(e);
        }
    }

    public void setAsciiStream(String parameterName, InputStream x) throws SQLException {
        try {
            ((CallableStatement) delegate).setAsciiStream(parameterName, x);
        } catch (Throwable e) {
            throw handleException(e);
        }
    }

    public void setBinaryStream(String parameterName, InputStream x) throws SQLException {
        try {
            ((CallableStatement) delegate).setBinaryStream(parameterName, x);
        } catch (Throwable e) {
            throw handleException(e);
        }
    }

    public void setCharacterStream(String parameterName, Reader reader) throws SQLException {
        try {
            ((CallableStatement) delegate).setCharacterStream(parameterName, reader);
        } catch (Throwable e) {
            throw handleException(e);
        }
    }

    public void setNCharacterStream(String parameterName, Reader value) throws SQLException {
        try {
            ((CallableStatement) delegate).setNCharacterStream(parameterName, value);
        } catch (Throwable e) {
            throw handleException(e);
        }
    }

    public void setClob(String parameterName, Reader reader) throws SQLException {
        try {
            ((CallableStatement) delegate).setClob(parameterName, reader);
        } catch (Throwable e) {
            throw handleException(e);
        }
    }

    public void setBlob(String parameterName, InputStream inputStream) throws SQLException {
        try {
            ((CallableStatement) delegate).setBlob(parameterName, inputStream);
        } catch (Throwable e) {
            throw handleException(e);
        }
    }

    public void setNClob(String parameterName, Reader reader) throws SQLException {
        try {
            ((CallableStatement) delegate).setNClob(parameterName, reader);
        } catch (Throwable e) {
            throw handleException(e);
        }
    }

    public <T> T getObject(int parameterIndex, Class<T> type) throws SQLException {
        throw new SQLException("JDK 7 feature unavailable");
    }

    public <T> T getObject(String parameterName, Class<T> type) throws SQLException {
        throw new SQLException("JDK 7 feature unavailable");
    }

    public ResultSet executeQuery() throws SQLException {
        try {
            return ((PreparedStatement) delegate).executeQuery();
        } catch (Throwable e) {
            throw handleException(e);
        }
    }

    public int executeUpdate() throws SQLException {
        try {
            return ((PreparedStatement) delegate).executeUpdate();
        } catch (Throwable e) {
            throw handleException(e);
        }
    }

    public void setNull(int parameterIndex, int sqlType) throws SQLException {
        try {
            ((PreparedStatement) delegate).setNull(parameterIndex, sqlType);
        } catch (Throwable e) {
            throw handleException(e);
        }
    }

    public void setBoolean(int parameterIndex, boolean x) throws SQLException {
        try {
            ((PreparedStatement) delegate).setBoolean(parameterIndex, x);
        } catch (Throwable e) {
            throw handleException(e);
        }
    }

    public void setByte(int parameterIndex, byte x) throws SQLException {
        try {
            ((PreparedStatement) delegate).setByte(parameterIndex, x);
        } catch (Throwable e) {
            throw handleException(e);
        }
    }

    public void setShort(int parameterIndex, short x) throws SQLException {
        try {
            ((PreparedStatement) delegate).setShort(parameterIndex, x);
        } catch (Throwable e) {
            throw handleException(e);
        }
    }

    public void setInt(int parameterIndex, int x) throws SQLException {
        try {
            ((PreparedStatement) delegate).setInt(parameterIndex, x);
        } catch (Throwable e) {
            throw handleException(e);
        }
    }

    public void setLong(int parameterIndex, long x) throws SQLException {
        try {
            ((PreparedStatement) delegate).setLong(parameterIndex, x);
        } catch (Throwable e) {
            throw handleException(e);
        }
    }

    public void setFloat(int parameterIndex, float x) throws SQLException {
        try {
            ((PreparedStatement) delegate).setFloat(parameterIndex, x);
        } catch (Throwable e) {
            throw handleException(e);
        }
    }

    public void setDouble(int parameterIndex, double x) throws SQLException {
        try {
            ((PreparedStatement) delegate).setDouble(parameterIndex, x);
        } catch (Throwable e) {
            throw handleException(e);
        }
    }

    public void setBigDecimal(int parameterIndex, BigDecimal x) throws SQLException {
        try {
            ((PreparedStatement) delegate).setBigDecimal(parameterIndex, x);
        } catch (Throwable e) {
            throw handleException(e);
        }
    }

    public void setString(int parameterIndex, String x) throws SQLException {
        try {
            ((PreparedStatement) delegate).setString(parameterIndex, x);
        } catch (Throwable e) {
            throw handleException(e);
        }
    }

    public void setBytes(int parameterIndex, byte[] x) throws SQLException {
        try {
            ((PreparedStatement) delegate).setBytes(parameterIndex, x);
        } catch (Throwable e) {
            throw handleException(e);
        }
    }

    public void setDate(int parameterIndex, Date x) throws SQLException {
        try {
            ((PreparedStatement) delegate).setDate(parameterIndex, x);
        } catch (Throwable e) {
            throw handleException(e);
        }
    }

    public void setTime(int parameterIndex, Time x) throws SQLException {
        try {
            ((PreparedStatement) delegate).setTime(parameterIndex, x);
        } catch (Throwable e) {
            throw handleException(e);
        }
    }

    public void setTimestamp(int parameterIndex, Timestamp x) throws SQLException {
        try {
            ((PreparedStatement) delegate).setTimestamp(parameterIndex, x);
        } catch (Throwable e) {
            throw handleException(e);
        }
    }

    public void setAsciiStream(int parameterIndex, InputStream x, int length) throws SQLException {
        try {
            ((PreparedStatement) delegate).setAsciiStream(parameterIndex, x, length);
        } catch (Throwable e) {
            throw handleException(e);
        }
    }

    @Override
    @SuppressWarnings("deprecation")
	public void setUnicodeStream(int parameterIndex, InputStream x, int length) throws SQLException {
        try {
            ((PreparedStatement) delegate).setUnicodeStream(parameterIndex, x, length);
        } catch (Throwable e) {
            throw handleException(e);
        }
    }

    @Override
    public void setBinaryStream(int parameterIndex, InputStream x, int length) throws SQLException {
        try {
            ((PreparedStatement) delegate).setBinaryStream(parameterIndex, x, length);
        } catch (Throwable e) {
            throw handleException(e);
        }
    }

    public void clearParameters() throws SQLException {
        try {
            ((PreparedStatement) delegate).clearParameters();
        } catch (Throwable e) {
            throw handleException(e);
        }
    }

    public void setObject(int parameterIndex, Object x, int targetSqlType) throws SQLException {
        try {
            ((PreparedStatement) delegate).setObject(parameterIndex, x, targetSqlType);
        } catch (Throwable e) {
            throw handleException(e);
        }
    }

    public void setObject(int parameterIndex, Object x) throws SQLException {
        try {
            ((PreparedStatement) delegate).setObject(parameterIndex, x);
        } catch (Throwable e) {
            throw handleException(e);
        }
    }

    public boolean execute() throws SQLException {
        try {
            return ((PreparedStatement) delegate).execute();
        } catch (Throwable e) {
            throw handleException(e);
        }
    }

    public void addBatch() throws SQLException {
        try {
            ((PreparedStatement) delegate).addBatch();
        } catch (Throwable e) {
            throw handleException(e);
        }
    }

    public void setCharacterStream(int parameterIndex, Reader reader, int length) throws SQLException {
        try {
            ((PreparedStatement) delegate).setCharacterStream(parameterIndex, reader, length);
        } catch (Throwable e) {
            throw handleException(e);
        }
    }

    public void setRef(int parameterIndex, Ref x) throws SQLException {
        try {
            ((PreparedStatement) delegate).setRef(parameterIndex, x);
        } catch (Throwable e) {
            throw handleException(e);
        }
    }

    public void setBlob(int parameterIndex, Blob x) throws SQLException {
        try {
            ((PreparedStatement) delegate).setBlob(parameterIndex, x);
        } catch (Throwable e) {
            throw handleException(e);
        }
    }

    public void setClob(int parameterIndex, Clob x) throws SQLException {
        try {
            ((PreparedStatement) delegate).setClob(parameterIndex, x);
        } catch (Throwable e) {
            throw handleException(e);
        }
    }

    public void setArray(int parameterIndex, Array x) throws SQLException {
        try {
            ((PreparedStatement) delegate).setArray(parameterIndex, x);
        } catch (Throwable e) {
            throw handleException(e);
        }
    }

    public ResultSetMetaData getMetaData() throws SQLException {
        try {
            return ((PreparedStatement) delegate).getMetaData();
        } catch (Throwable e) {
            throw handleException(e);
        }
    }

    public void setDate(int parameterIndex, Date x, Calendar cal) throws SQLException {
        try {
            ((PreparedStatement) delegate).setDate(parameterIndex, x, cal);
        } catch (Throwable e) {
            throw handleException(e);
        }
    }

    public void setTime(int parameterIndex, Time x, Calendar cal) throws SQLException {
        try {
            ((PreparedStatement) delegate).setTime(parameterIndex, x, cal);
        } catch (Throwable e) {
            throw handleException(e);
        }
    }

    public void setTimestamp(int parameterIndex, Timestamp x, Calendar cal) throws SQLException {
        try {
            ((PreparedStatement) delegate).setTimestamp(parameterIndex, x, cal);
        } catch (Throwable e) {
            throw handleException(e);
        }
    }

    public void setNull(int parameterIndex, int sqlType, String typeName) throws SQLException {
        try {
            ((PreparedStatement) delegate).setNull(parameterIndex, sqlType, typeName);
        } catch (Throwable e) {
            throw handleException(e);
        }
    }

    public void setURL(int parameterIndex, URL x) throws SQLException {
        try {
            ((PreparedStatement) delegate).setURL(parameterIndex, x);
        } catch (Throwable e) {
            throw handleException(e);
        }
    }

    public ParameterMetaData getParameterMetaData() throws SQLException {
        try {
            return ((PreparedStatement) delegate).getParameterMetaData();
        } catch (Throwable e) {
            throw handleException(e);
        }
    }

    public void setRowId(int parameterIndex, RowId x) throws SQLException {
        try {
            ((PreparedStatement) delegate).setRowId(parameterIndex, x);
        } catch (Throwable e) {
            throw handleException(e);
        }
    }

    public void setNString(int parameterIndex, String value) throws SQLException {
        try {
            ((PreparedStatement) delegate).setNString(parameterIndex, value);
        } catch (Throwable e) {
            throw handleException(e);
        }
    }

    public void setNCharacterStream(int parameterIndex, Reader value, long length) throws SQLException {
        try {
            ((PreparedStatement) delegate).setNCharacterStream(parameterIndex, value, length);
        } catch (Throwable e) {
            throw handleException(e);
        }
    }

    public void setNClob(int parameterIndex, NClob value) throws SQLException {
        try {
            ((PreparedStatement) delegate).setNClob(parameterIndex, value);
        } catch (Throwable e) {
            throw handleException(e);
        }
    }

    public void setClob(int parameterIndex, Reader reader, long length) throws SQLException {
        try {
            ((PreparedStatement) delegate).setClob(parameterIndex, reader, length);
        } catch (Throwable e) {
            throw handleException(e);
        }
    }

    public void setBlob(int parameterIndex, InputStream inputStream, long length) throws SQLException {
        try {
            ((PreparedStatement) delegate).setBlob(parameterIndex, inputStream, length);
        } catch (Throwable e) {
            throw handleException(e);
        }
    }

    public void setNClob(int parameterIndex, Reader reader, long length) throws SQLException {
        try {
            ((PreparedStatement) delegate).setNClob(parameterIndex, reader, length);
        } catch (Throwable e) {
            throw handleException(e);
        }
    }

    public void setSQLXML(int parameterIndex, SQLXML xmlObject) throws SQLException {
        try {
            ((PreparedStatement) delegate).setSQLXML(parameterIndex, xmlObject);
        } catch (Throwable e) {
            throw handleException(e);
        }
    }

    public void setObject(int parameterIndex, Object x, int targetSqlType, int scaleOrLength) throws SQLException {
        try {
            ((PreparedStatement) delegate).setObject(parameterIndex, x, targetSqlType, scaleOrLength);
        } catch (Throwable e) {
            throw handleException(e);
        }
    }

    public void setAsciiStream(int parameterIndex, InputStream x, long length) throws SQLException {
        try {
            ((PreparedStatement) delegate).setAsciiStream(parameterIndex, x, length);
        } catch (Throwable e) {
            throw handleException(e);
        }
    }

    public void setBinaryStream(int parameterIndex, InputStream x, long length) throws SQLException {
        try {
            ((PreparedStatement) delegate).setBinaryStream(parameterIndex, x, length);
        } catch (Throwable e) {
            throw handleException(e);
        }
    }

    public void setCharacterStream(int parameterIndex, Reader reader, long length) throws SQLException {
        try {
            ((PreparedStatement) delegate).setCharacterStream(parameterIndex, reader, length);
        } catch (Throwable e) {
            throw handleException(e);
        }
    }

    public void setAsciiStream(int parameterIndex, InputStream x) throws SQLException {
        try {
            ((PreparedStatement) delegate).setAsciiStream(parameterIndex, x);
        } catch (Throwable e) {
            throw handleException(e);
        }
    }

    public void setBinaryStream(int parameterIndex, InputStream x) throws SQLException {
        try {
            ((PreparedStatement) delegate).setBinaryStream(parameterIndex, x);
        } catch (Throwable e) {
            throw handleException(e);
        }
    }

    public void setCharacterStream(int parameterIndex, Reader reader) throws SQLException {
        try {
            ((PreparedStatement) delegate).setCharacterStream(parameterIndex, reader);
        } catch (Throwable e) {
            throw handleException(e);
        }
    }

    public void setNCharacterStream(int parameterIndex, Reader value) throws SQLException {
        try {
            ((PreparedStatement) delegate).setNCharacterStream(parameterIndex, value);
        } catch (Throwable e) {
            throw handleException(e);
        }
    }

    public void setClob(int parameterIndex, Reader reader) throws SQLException {
        try {
            ((PreparedStatement) delegate).setClob(parameterIndex, reader);
        } catch (Throwable e) {
            throw handleException(e);
        }
    }

    public void setBlob(int parameterIndex, InputStream inputStream) throws SQLException {
        try {
            ((PreparedStatement) delegate).setBlob(parameterIndex, inputStream);
        } catch (Throwable e) {
            throw handleException(e);
        }
    }

    public void setNClob(int parameterIndex, Reader reader) throws SQLException {
        try {
            ((PreparedStatement) delegate).setNClob(parameterIndex, reader);
        } catch (Throwable e) {
            throw handleException(e);
        }
    }

    public ResultSet executeQuery(String sql) throws SQLException {
        try {
            return delegate.executeQuery(sql);
        } catch (Throwable e) {
            throw handleException(e);
        }
    }

    public int executeUpdate(String sql) throws SQLException {
        try {
            return delegate.executeUpdate(sql);
        } catch (Throwable e) {
            throw handleException(e);
        }
    }

    public void close() throws SQLException {
        try {
            delegate.close();
        } catch (Throwable e) {
            throw handleException(e);
        }
    }

    public int getMaxFieldSize() throws SQLException {
        try {
            return delegate.getMaxFieldSize();
        } catch (Throwable e) {
            throw handleException(e);
        }
    }

    public void setMaxFieldSize(int max) throws SQLException {
        try {
            delegate.setMaxFieldSize(max);
        } catch (Throwable e) {
            throw handleException(e);
        }
    }

    public int getMaxRows() throws SQLException {
        try {
            return delegate.getMaxRows();
        } catch (Throwable e) {
            throw handleException(e);
        }
    }

    public void setMaxRows(int max) throws SQLException {
        try {
            delegate.setMaxRows(max);
        } catch (Throwable e) {
            throw handleException(e);
        }
    }

    public void setEscapeProcessing(boolean enable) throws SQLException {
        try {
            delegate.setEscapeProcessing(enable);
        } catch (Throwable e) {
            throw handleException(e);
        }
    }

    public int getQueryTimeout() throws SQLException {
        try {
            return delegate.getQueryTimeout();
        } catch (Throwable e) {
            throw handleException(e);
        }
    }

    public void setQueryTimeout(int seconds) throws SQLException {
        try {
            delegate.setQueryTimeout(seconds);
        } catch (Throwable e) {
            throw handleException(e);
        }
    }

    public void cancel() throws SQLException {
        try {
            delegate.cancel();
        } catch (Throwable e) {
            throw handleException(e);
        }
    }

    public SQLWarning getWarnings() throws SQLException {
        try {
            return delegate.getWarnings();
        } catch (Throwable e) {
            throw handleException(e);
        }
    }

    public void clearWarnings() throws SQLException {
        try {
            delegate.clearWarnings();
        } catch (Throwable e) {
            throw handleException(e);
        }
    }

    public void setCursorName(String name) throws SQLException {
        try {
            delegate.setCursorName(name);
        } catch (Throwable e) {
            throw handleException(e);
        }
    }

    public boolean execute(String sql) throws SQLException {
        try {
            return delegate.execute(sql);
        } catch (Throwable e) {
            throw handleException(e);
        }
    }

    public ResultSet getResultSet() throws SQLException {
        try {
            return delegate.getResultSet();
        } catch (Throwable e) {
            throw handleException(e);
        }
    }

    public int getUpdateCount() throws SQLException {
        try {
            return delegate.getUpdateCount();
        } catch (Throwable e) {
            throw handleException(e);
        }
    }

    public boolean getMoreResults() throws SQLException {
        try {
            return delegate.getMoreResults();
        } catch (Throwable e) {
            throw handleException(e);
        }
    }

    public void setFetchDirection(int direction) throws SQLException {
        try {
            delegate.setFetchDirection(direction);
        } catch (Throwable e) {
            throw handleException(e);
        }
    }

    public int getFetchDirection() throws SQLException {
        try {
            return delegate.getFetchDirection();
        } catch (Throwable e) {
            throw handleException(e);
        }
    }

    public void setFetchSize(int rows) throws SQLException {
        try {
            delegate.setFetchSize(rows);
        } catch (Throwable e) {
            throw handleException(e);
        }
    }

    public int getFetchSize() throws SQLException {
        try {
            return delegate.getFetchSize();
        } catch (Throwable e) {
            throw handleException(e);
        }
    }

    public int getResultSetConcurrency() throws SQLException {
        try {
            return delegate.getResultSetConcurrency();
        } catch (Throwable e) {
            throw handleException(e);
        }
    }

    public int getResultSetType() throws SQLException {
        try {
            return delegate.getResultSetType();
        } catch (Throwable e) {
            throw handleException(e);
        }
    }

    public void addBatch(String sql) throws SQLException {
        try {
            delegate.addBatch(sql);
        } catch (Throwable e) {
            throw handleException(e);
        }
    }

    public void clearBatch() throws SQLException {
        try {
            delegate.clearBatch();
        } catch (Throwable e) {
            throw handleException(e);
        }
    }

    public int[] executeBatch() throws SQLException {
        try {
            return delegate.executeBatch();
        } catch (Throwable e) {
            throw handleException(e);
        }
    }

    public Connection getConnection() throws SQLException {
        try {
            return delegate.getConnection();
        } catch (Throwable e) {
            throw handleException(e);
        }
    }

    public boolean getMoreResults(int current) throws SQLException {
        try {
            return delegate.getMoreResults(current);
        } catch (Throwable e) {
            throw handleException(e);
        }
    }

    public ResultSet getGeneratedKeys() throws SQLException {
        try {
            return delegate.getGeneratedKeys();
        } catch (Throwable e) {
            throw handleException(e);
        }
    }

    public int executeUpdate(String sql, int autoGeneratedKeys) throws SQLException {
        try {
            return delegate.executeUpdate(sql, autoGeneratedKeys);
        } catch (Throwable e) {
            throw handleException(e);
        }
    }

    public int executeUpdate(String sql, int[] columnIndexes) throws SQLException {
        try {
            return delegate.executeUpdate(sql, columnIndexes);
        } catch (Throwable e) {
            throw handleException(e);
        }
    }

    public int executeUpdate(String sql, String[] columnNames) throws SQLException {
        try {
            return delegate.executeUpdate(sql, columnNames);
        } catch (Throwable e) {
            throw handleException(e);
        }
    }

    public boolean execute(String sql, int autoGeneratedKeys) throws SQLException {
        try {
            return delegate.execute(sql, autoGeneratedKeys);
        } catch (Throwable e) {
            throw handleException(e);
        }
    }

    public boolean execute(String sql, int[] columnIndexes) throws SQLException {
        try {
            return delegate.execute(sql, columnIndexes);
        } catch (Throwable e) {
            throw handleException(e);
        }
    }

    public boolean execute(String sql, String[] columnNames) throws SQLException {
        try {
            return delegate.execute(sql, columnNames);
        } catch (Throwable e) {
            throw handleException(e);
        }
    }

    public int getResultSetHoldability() throws SQLException {
        try {
            return delegate.getResultSetHoldability();
        } catch (Throwable e) {
            throw handleException(e);
        }
    }

    public boolean isClosed() throws SQLException {
        try {
            return delegate.isClosed();
        } catch (Throwable e) {
            throw handleException(e);
        }
    }

    public void setPoolable(boolean poolable) throws SQLException {
        try {
            delegate.setPoolable(poolable);
        } catch (Throwable e) {
            throw handleException(e);
        }
    }

    public boolean isPoolable() throws SQLException {
        try {
            return delegate.isPoolable();
        } catch (Throwable e) {
            throw handleException(e);
        }
    }

    public void closeOnCompletion() throws SQLException {
        throw new SQLException("JDK 7 feature unavailable");
    }

    public boolean isCloseOnCompletion() throws SQLException {
        throw new SQLException("JDK 7 feature unavailable");
    }

    public <T> T unwrap(Class<T> iface) throws SQLException {
        try {
            return delegate.unwrap(iface);
        } catch (Throwable e) {
            throw handleException(e);
        }
    }

    public boolean isWrapperFor(Class<?> iface) throws SQLException {
        try {
            return delegate.isWrapperFor(iface);
        } catch (Throwable e) {
            throw handleException(e);
        }
    }

    private SQLException handleException(Throwable e) {
        return connection.handleException(e);
    }
}
