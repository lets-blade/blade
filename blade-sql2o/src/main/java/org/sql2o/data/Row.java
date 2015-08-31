package org.sql2o.data;

import org.sql2o.Sql2oException;
import org.sql2o.converters.*;
import org.sql2o.quirks.Quirks;

import java.math.BigDecimal;
import java.util.*;

import static java.util.Arrays.asList;
import static org.sql2o.converters.Convert.throwIfNull;

/**
 * Represents a result set row.
 */
@SuppressWarnings({"UnusedDeclaration", "RedundantTypeArguments"})
public class Row {
    
    private final Object[] values;
    private final boolean isCaseSensitive;
    private final Quirks quirks;
    private final Map<String, Integer> columnNameToIdxMap;

    public Row(Map<String, Integer> columnNameToIdxMap, int columnCnt, boolean isCaseSensitive, Quirks quirks) {
        this.columnNameToIdxMap = columnNameToIdxMap;
        this.isCaseSensitive = isCaseSensitive;
        this.quirks = quirks;
        // lol. array works better
        this.values = new Object[columnCnt];
    }

    void addValue(int columnIndex, Object value){
        values[columnIndex]=value;
    }

    public Object getObject(int columnIndex){
        return values[columnIndex];
    }
    
    public Object getObject(String columnName){
        Integer index = columnNameToIdxMap.get(
                isCaseSensitive?columnName
                :columnName.toLowerCase());
        if(index!=null) return getObject(index);
        throw new Sql2oException(String.format("Column with name '%s' does not exist", columnName));
    }

    @SuppressWarnings("unchecked")
    public <V> V getObject(int columnIndex, Class clazz){
        try{
            return (V) throwIfNull(clazz, quirks.converterOf(clazz)).convert(getObject(columnIndex));
        } catch (ConverterException ex){
            throw new Sql2oException("Error converting value", ex);
        }
    }

    @SuppressWarnings("unchecked")
    public <V> V getObject(String columnName, Class clazz) {
        try{
            return (V) throwIfNull(clazz, quirks.converterOf(clazz)).convert(getObject(columnName));
        } catch (ConverterException ex){
            throw new Sql2oException("Error converting value", ex);
        }
    }

    public BigDecimal getBigDecimal(int columnIndex){
        return this.<BigDecimal>getObject(columnIndex, BigDecimal.class);
    }
    
    public BigDecimal getBigDecimal(String columnName){
        return this.<BigDecimal>getObject(columnName, BigDecimal.class);
    }
    
    public Double getDouble(int columnIndex){
        return this.<Double>getObject(columnIndex, Double.class);
    }
    
    public Double getDouble(String columnName){
        return this.<Double>getObject(columnName, Double.class);
    }
    
    public Float getFloat(int columnIndex){
        return this.<Float>getObject(columnIndex, Float.class);
    }
    
    public Float getFloat(String columnName){
        return this.<Float>getObject(columnName, Float.class);
    }
    
    public Long getLong(int columnIndex){
        return this.<Long>getObject(columnIndex, Long.class);
    }
    
    public Long getLong(String columnName){
        return this.<Long>getObject(columnName, Long.class);
    }
    
    public Integer getInteger(int columnIndex){
        return this.<Integer>getObject(columnIndex, Integer.class);
    }
    
    public Integer getInteger(String columnName){
        return this.<Integer>getObject(columnName, Integer.class);
    }
    
    public Short getShort(int columnIndex){
        return this.<Short>getObject(columnIndex, Short.class);
    }
    
    public Short getShort(String columnName){
        return this.<Short>getObject(columnName, Short.class);
    }
    
    public Byte getByte(int columnIndex){
        return this.<Byte>getObject(columnIndex, Byte.class);
    }
    
    public Byte getByte(String columnName){
        return this.<Byte>getObject(columnName, Byte.class);
    }
    
    public Date getDate(int columnIndex){
        return this.<Date>getObject(columnIndex, Date.class);
    }
    
    public Date getDate(String columnName){
        return this.<Date>getObject(columnName, Date.class);
    }

    public String getString(int columnIndex){
        return this.<String>getObject(columnIndex, String.class);
    }
    
    public String getString(String columnName){
        return this.<String>getObject(columnName, String.class);
    }

    /**
     * View row as a simple map.
     */
    @SuppressWarnings("NullableProblems")
    public Map<String, Object> asMap()
    {   final List<Object> listOfValues = asList(values);
        return new Map<String, Object>() {
            public int size() {
                return values.length;
            }

            public boolean isEmpty() {
                return size()==0;
            }

            public boolean containsKey(Object key) {
                return columnNameToIdxMap.containsKey(key);
            }

            public boolean containsValue(Object value) {
                return listOfValues.contains(value);
            }

            public Object get(Object key) {
                return values[columnNameToIdxMap.get(key)];
            }

            public Object put(String key, Object value) {
                throw new UnsupportedOperationException("Row map is immutable.");
            }

            public Object remove(Object key) {
                throw new UnsupportedOperationException("Row map is immutable.");
            }

            public void putAll(Map<? extends String, ?> m) {
                throw new UnsupportedOperationException("Row map is immutable.");
            }

            public void clear() {
                throw new UnsupportedOperationException("Row map is immutable.");
            }

            public Set<String> keySet() {
                return columnNameToIdxMap.keySet();
            }

            public Collection<Object> values() {
                return listOfValues;
            }

            public Set<Entry<String, Object>> entrySet() {
                throw new UnsupportedOperationException("Row map does not support entrySet.");
            }
        };
    }
}
