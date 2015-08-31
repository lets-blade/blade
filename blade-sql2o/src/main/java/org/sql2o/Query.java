package org.sql2o;

import static org.sql2o.converters.Convert.throwIfNull;

import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.sql2o.converters.Converter;
import org.sql2o.converters.ConverterException;
import org.sql2o.data.LazyTable;
import org.sql2o.data.Row;
import org.sql2o.data.Table;
import org.sql2o.data.TableResultSetIterator;
import org.sql2o.logging.LocalLoggerFactory;
import org.sql2o.logging.Logger;
import org.sql2o.quirks.Quirks;
import org.sql2o.reflection.PojoIntrospector;

/**
 * Represents a sql2o statement. With sql2o, all statements are instances of the Query class.
 */
@SuppressWarnings("UnusedDeclaration")
public class Query implements AutoCloseable {

    private final static Logger logger = LocalLoggerFactory.getLogger(Query.class);

    private Connection connection;
    private Map<String, String> caseSensitiveColumnMappings;
    private Map<String, String> columnMappings;
    private final PreparedStatement statement;
    private boolean caseSensitive;
    private boolean autoDeriveColumnNames;
    private boolean throwOnMappingFailure = true;
    private String name;
    private boolean returnGeneratedKeys;
    private final Map<String, List<Integer>> paramNameToIdxMap;
    private final Set<String> addedParameters;
    private final String parsedQuery;

    private ResultSetHandlerFactoryBuilder resultSetHandlerFactoryBuilder;

    @Override
    public String toString() {
        return parsedQuery;
    }

    public Query(Connection connection, String queryText, String name, boolean returnGeneratedKeys) {
        this.connection = connection;
        this.name = name;
        this.returnGeneratedKeys = returnGeneratedKeys;
        this.setColumnMappings(connection.getSql2o().getDefaultColumnMappings());
        this.caseSensitive = connection.getSql2o().isDefaultCaseSensitive();

        paramNameToIdxMap = new HashMap<String, List<Integer>>();
        addedParameters = new HashSet<String>();

        parsedQuery = connection.getSql2o().getQuirks().getSqlParameterParsingStrategy().parseSql(queryText, paramNameToIdxMap);
        try {
            if (returnGeneratedKeys) {
                statement = connection.getJdbcConnection().prepareStatement(parsedQuery, Statement.RETURN_GENERATED_KEYS);
            } else {
                statement = connection.getJdbcConnection().prepareStatement(parsedQuery);
            }
        } catch(SQLException ex) {
            throw new Sql2oException(String.format("Error preparing statement - %s", ex.getMessage()), ex);
        }
        connection.registerStatement(statement);

    }

    // ------------------------------------------------
    // ------------- Getter/Setters -------------------
    // ------------------------------------------------

    public boolean isCaseSensitive() {
        return caseSensitive;
    }

    public Query setCaseSensitive(boolean caseSensitive) {
        this.caseSensitive = caseSensitive;
        return this;
    }

    public boolean isAutoDeriveColumnNames() {
        return autoDeriveColumnNames;
    }

    public Query setAutoDeriveColumnNames(boolean autoDeriveColumnNames) {
        this.autoDeriveColumnNames = autoDeriveColumnNames;
        return this;
    }

    public Query throwOnMappingFailure(boolean throwOnMappingFailure) {
        this.throwOnMappingFailure = throwOnMappingFailure;
        return this;
    }

    public boolean isThrowOnMappingFailure() {
        return throwOnMappingFailure;
    }

    public Connection getConnection(){
        return this.connection;
    }

    public String getName() {
        return name;
    }

    public ResultSetHandlerFactoryBuilder getResultSetHandlerFactoryBuilder() {
        if (resultSetHandlerFactoryBuilder == null) {
            resultSetHandlerFactoryBuilder = new DefaultResultSetHandlerFactoryBuilder();
        }
        return resultSetHandlerFactoryBuilder;
    }

    public void setResultSetHandlerFactoryBuilder(ResultSetHandlerFactoryBuilder resultSetHandlerFactoryBuilder) {
        this.resultSetHandlerFactoryBuilder = resultSetHandlerFactoryBuilder;
    }

    public Map<String, List<Integer>> getParamNameToIdxMap() {
        return paramNameToIdxMap;
    }

    // ------------------------------------------------
    // ------------- Add Parameters -------------------
    // ------------------------------------------------

    private void addParameterInternal(String name, ParameterSetter parameterSetter) {
        addedParameters.add(name);
        for (int paramIdx : this.getParamNameToIdxMap().get(name)) {
            try {
                parameterSetter.setParameter(paramIdx);
            } catch (SQLException e) {
                throw new RuntimeException(String.format("Error adding parameter '%s' - %s", name, e.getMessage()), e);
            }
        }
    }

    @SuppressWarnings("unchecked")
    private Object convertParameter(Object value) {
        if (value == null) {
            return null;
        }
        Converter converter = getQuirks().converterOf(value.getClass());
        if (converter == null) {
            // let's try to add parameter AS IS
            return value;
        }
        return converter.toDatabaseParam(value);
    }

    public <T> Query addParameter(String name, Class<T> parameterClass, T value){
        //TODO: must cover most of types: BigDecimal,Boolean,SmallInt,Double,Float,byte[]
        if(InputStream.class.isAssignableFrom(parameterClass))
            return addParameter(name, (InputStream)value);
        if(Integer.class==parameterClass)
            return addParameter(name, (Integer)value);
        if(Long.class==parameterClass)
            return addParameter(name, (Long)value);
        if(String.class==parameterClass)
            return addParameter(name, (String)value);
        if(Timestamp.class==parameterClass)
            return addParameter(name, (Timestamp)value);
        if(Time.class==parameterClass)
            return addParameter(name, (Time)value);


        final Object convertedValue = convertParameter(value);

        addParameterInternal(name, new ParameterSetter() {
            public void setParameter(int paramIdx) throws SQLException {
                getConnection().getSql2o().getQuirks().setParameter(statement, paramIdx, convertedValue);
            }
        });

        return this;
    }

    public Query withParams(Object... paramValues){
        int i=0;
        for (Object paramValue : paramValues) {
            addParameter("p" + (++i), paramValue);
        }
        return this;
    }

    @SuppressWarnings("unchecked")
    public Query addParameter(String name, Object value) {
        return value == null
                ? addParameter(name, Object.class, value)
                : addParameter(name,
                    (Class<Object>) value.getClass(),
                    value);
    }

    public Query addParameter(String name, final InputStream value){
        addParameterInternal(name, new ParameterSetter() {
            public void setParameter(int paramIdx) throws SQLException {
                getConnection().getSql2o().getQuirks().setParameter(statement, paramIdx, value);
            }
        });

        return this;
    }

    public Query addParameter(String name, final int value){
        addParameterInternal(name, new ParameterSetter() {
            public void setParameter(int paramIdx) throws SQLException {
                getConnection().getSql2o().getQuirks().setParameter(statement, paramIdx, value);
            }
        });

        return this;
    }

    public Query addParameter(String name, final Integer value) {
        addParameterInternal(name, new ParameterSetter() {
            public void setParameter(int paramIdx) throws SQLException {
                getConnection().getSql2o().getQuirks().setParameter(statement, paramIdx, value);
            }
        });

        return this;
    }

    public Query addParameter(String name, final long value){
        addParameterInternal(name, new ParameterSetter() {
            public void setParameter(int paramIdx) throws SQLException {
                getConnection().getSql2o().getQuirks().setParameter(statement, paramIdx, value);
            }
        });

        return this;
    }

    public Query addParameter(String name, final Long value){
        addParameterInternal(name, new ParameterSetter() {
            public void setParameter(int paramIdx) throws SQLException {
                getConnection().getSql2o().getQuirks().setParameter(statement, paramIdx, value);
            }
        });

        return this;
    }

    public Query addParameter(String name, final String value) {
        addParameterInternal(name, new ParameterSetter() {
            public void setParameter(int paramIdx) throws SQLException {
                getConnection().getSql2o().getQuirks().setParameter(statement, paramIdx, value);
            }
        });

        return this;
    }

    public Query addParameter(String name, final Timestamp value){
        addParameterInternal(name, new ParameterSetter() {
            public void setParameter(int paramIdx) throws SQLException {
                getConnection().getSql2o().getQuirks().setParameter(statement, paramIdx, value);
            }
        });

        return this;
    }

    public Query addParameter(String name, final Time value) {
        addParameterInternal(name, new ParameterSetter() {
            public void setParameter(int paramIdx) throws SQLException {
                getConnection().getSql2o().getQuirks().setParameter(statement, paramIdx, value);
            }
        });

        return this;
    }

    public Query bind(final Object pojo) {
        Class<?> clazz = pojo.getClass();
        Map<String, PojoIntrospector.ReadableProperty> propertyMap = PojoIntrospector.readableProperties(clazz);
        for (PojoIntrospector.ReadableProperty property : propertyMap.values()) {
            if (addedParameters.contains( property.name )) continue;
            try {
                if( this.getParamNameToIdxMap().containsKey(property.name)) {

                    @SuppressWarnings("unchecked")
                    final Class<Object> type = (Class<Object>) property.type;
                    this.addParameter(property.name, type, property.get(pojo));
                }
            }
            catch(IllegalArgumentException ex) {
                logger.debug("Ignoring Illegal Arguments", ex);
            }
            catch(IllegalAccessException ex) {
                throw new RuntimeException(ex);
            }
            catch(InvocationTargetException ex) {
                throw new RuntimeException(ex);
            }
        }
        return this;
    }

    public void close() {
        connection.removeStatement(statement);
        try {
            this.getQuirks().closeStatement(statement);
        } catch (Throwable ex){
            logger.warn("Could not close statement.", ex);
        }
    }

    // ------------------------------------------------
    // -------------------- Execute -------------------
    // ------------------------------------------------

    /**
     * Iterable {@link java.sql.ResultSet} that wraps {@link PojoResultSetIterator}.
     */
    private abstract class ResultSetIterableBase<T> implements ResultSetIterable<T> {
        private long start;
        private long afterExecQuery;
        protected ResultSet rs;

        boolean autoCloseConnection = false;

        public ResultSetIterableBase() {
            try {
                start = System.currentTimeMillis();
                rs = statement.executeQuery();
                afterExecQuery = System.currentTimeMillis();
            }
            catch (SQLException ex) {
                throw new Sql2oException("Database error: " + ex.getMessage(), ex);
            }
        }

        @Override
        public void close() {
            try {
                if (rs != null) {
                    rs.close();

                    // log the query
                    long afterClose = System.currentTimeMillis();
                    logger.debug("total: {} ms, execution: {} ms, reading and parsing: {} ms; executed [{}]", new Object[]{
                            afterClose - start,
                            afterExecQuery-start,
                            afterClose - afterExecQuery,
                            name
                    });

                    rs = null;
                }
            }
            catch (SQLException ex) {
                throw new Sql2oException("Error closing ResultSet.", ex);
            }
            finally {
                if (this.isAutoCloseConnection()){
                    connection.close();
                } else {
                    closeConnectionIfNecessary();
                }
            }
        }

        @Override
        public boolean isAutoCloseConnection() {
            return this.autoCloseConnection;
        }

        @Override
        public void setAutoCloseConnection(boolean autoCloseConnection) {
            this.autoCloseConnection = autoCloseConnection;
        }
    }

    /**
     * Read a collection lazily. Generally speaking, this should only be used if you are reading MANY
     * results and keeping them all in a Collection would cause memory issues. You MUST call
     * {@link org.sql2o.ResultSetIterable#close()} when you are done iterating.
     *
     * @param returnType type of each row
     * @return iterable results
     */
    public <T> ResultSetIterable<T> executeAndFetchLazy(final Class<T> returnType) {
        final ResultSetHandlerFactory<T> resultSetHandlerFactory = newResultSetHandlerFactory(returnType);
        return executeAndFetchLazy(resultSetHandlerFactory);
    }

    private <T> ResultSetHandlerFactory<T> newResultSetHandlerFactory(Class<T> returnType) {
        final Quirks quirks = getConnection().getSql2o().getQuirks();
        ResultSetHandlerFactoryBuilder builder = getResultSetHandlerFactoryBuilder();
        if(builder==null) builder=new DefaultResultSetHandlerFactoryBuilder();
        builder.setAutoDeriveColumnNames(this.autoDeriveColumnNames);
        builder.setCaseSensitive(this.caseSensitive);
        builder.setColumnMappings(this.columnMappings);
        builder.setQuirks(quirks);
        builder.throwOnMappingError(this.throwOnMappingFailure);
        return builder.newFactory(returnType);
    }

    /**
     * Read a collection lazily. Generally speaking, this should only be used if you are reading MANY
     * results and keeping them all in a Collection would cause memory issues. You MUST call
     * {@link org.sql2o.ResultSetIterable#close()} when you are done iterating.
     *
     * @param resultSetHandlerFactory factory to provide ResultSetHandler
     * @return iterable results
     */
    public <T> ResultSetIterable<T> executeAndFetchLazy(final ResultSetHandlerFactory<T> resultSetHandlerFactory) {
        final Quirks quirks = getConnection().getSql2o().getQuirks();
        return new ResultSetIterableBase<T>() {
            public Iterator<T> iterator() {
                return new PojoResultSetIterator<T>(rs, isCaseSensitive(), quirks, resultSetHandlerFactory);
            }
        };
    }

    /**
     * Read a collection lazily. Generally speaking, this should only be used if you are reading MANY
     * results and keeping them all in a Collection would cause memory issues. You MUST call
     * {@link org.sql2o.ResultSetIterable#close()} when you are done iterating.
     *
     * @param resultSetHandler ResultSetHandler
     * @return iterable results
     */
    public <T> ResultSetIterable<T> executeAndFetchLazy(final ResultSetHandler<T> resultSetHandler) {
        final ResultSetHandlerFactory<T> factory = newResultSetHandlerFactory(resultSetHandler);
        return executeAndFetchLazy(factory);
    }

    private static  <T> ResultSetHandlerFactory<T> newResultSetHandlerFactory(final ResultSetHandler<T> resultSetHandler) {
        return new ResultSetHandlerFactory<T>() {
            public ResultSetHandler<T> newResultSetHandler(ResultSetMetaData resultSetMetaData) throws SQLException {
                return resultSetHandler;
            }
        };
    }

    public <T> List<T> executeAndFetch(Class<T> returnType){
        return executeAndFetch(newResultSetHandlerFactory(returnType));
    }

    public <T> List<T> executeAndFetch(ResultSetHandler<T> resultSetHandler){
        return executeAndFetch(newResultSetHandlerFactory(resultSetHandler));
    }

    public <T> List<T> executeAndFetch(ResultSetHandlerFactory<T> factory){
        List<T> list = new ArrayList<T>();

        // if sql2o moves to java 7 at some point, this could be much cleaner using try-with-resources
        ResultSetIterable<T> iterable = null;
        try {
            iterable = executeAndFetchLazy(factory);
            for (T item : iterable) {
                list.add(item);
            }
        }
        finally {
            if (iterable != null) {
                iterable.close();
            }
        }

        return list;
    }

    public <T> T executeAndFetchFirst(Class<T> returnType){
        return executeAndFetchFirst(newResultSetHandlerFactory(returnType));
    }

    public <T> T executeAndFetchFirst(ResultSetHandler<T> resultSetHandler){
        return executeAndFetchFirst(newResultSetHandlerFactory(resultSetHandler));
    }

    public <T> T executeAndFetchFirst(ResultSetHandlerFactory<T> resultSetHandlerFactory){
        // if sql2o moves to java 7 at some point, this could be much cleaner using try-with-resources
        ResultSetIterable<T> iterable = null;
        try {
            iterable = executeAndFetchLazy(resultSetHandlerFactory);
            Iterator<T> iterator = iterable.iterator();
            return iterator.hasNext() ? iterator.next() : null;
        }
        finally {
            if (iterable != null) {
                iterable.close();
            }
        }
    }

    public LazyTable executeAndFetchTableLazy() {
        final LazyTable lt = new LazyTable();

        lt.setRows(new ResultSetIterableBase<Row>() {
            public Iterator<Row> iterator() {
                return new TableResultSetIterator(rs, isCaseSensitive(), getConnection().getSql2o().getQuirks(), lt);
            }
        });

        return lt;
    }

    public Table executeAndFetchTable() {
        LazyTable lt =  executeAndFetchTableLazy();
        List<Row> rows = new ArrayList<Row>();
        try {
            for (Row item : lt.rows()) {
                rows.add(item);
            }
        }
        finally {
           lt.close();
        }
        // lt==null is always false
        return new Table(lt.getName(), rows, lt.columns());
    }

    public Connection executeUpdate(){
        long start = System.currentTimeMillis();
        try{
            this.connection.setResult(statement.executeUpdate());
            this.connection.setKeys(this.returnGeneratedKeys ? statement.getGeneratedKeys() : null);
            connection.setCanGetKeys(this.returnGeneratedKeys);
        }
        catch(SQLException ex){
            this.connection.onException();
            throw new Sql2oException("Error in executeUpdate, " + ex.getMessage(), ex);
        }
        finally {
            closeConnectionIfNecessary();
        }

        long end = System.currentTimeMillis();
        logger.debug("total: {} ms; executed update [{}]", new Object[]{
                end - start,
                this.getName() == null ? "No name" : this.getName()
        });

        return this.connection;
    }

    public Object executeScalar(){
        long start = System.currentTimeMillis();
        try {
            ResultSet rs = this.statement.executeQuery();
            if (rs.next()){
                Object o = getQuirks().getRSVal(rs, 1);
                long end = System.currentTimeMillis();
                logger.debug("total: {} ms; executed scalar [{}]", new Object[]{
                        end - start,
                        this.getName() == null ? "No name" : this.getName()
                });
                return o;
            }
            else{
                return null;
            }

        }
        catch (SQLException e) {
            this.connection.onException();
            throw new Sql2oException("Database error occurred while running executeScalar: " + e.getMessage(), e);
        }
        finally{
            closeConnectionIfNecessary();
        }

    }

    private Quirks getQuirks() {
        return this.connection.getSql2o().getQuirks();
    }

    public <V> V executeScalar(Class<V> returnType){
        try {
            Converter<V> converter;
            //noinspection unchecked
            converter = throwIfNull(returnType, getQuirks().converterOf(returnType));
            //noinspection unchecked
            return executeScalar(converter);
        } catch (ConverterException e) {
            throw new Sql2oException("Error occured while converting value from database to type " + returnType, e);
        }
    }

    public <V> V executeScalar(Converter<V> converter){
        try {
            //noinspection unchecked
            return converter.convert(executeScalar());
        } catch (ConverterException e) {
            throw new Sql2oException("Error occured while converting value from database", e);
        }
    }



    public <T> List<T> executeScalarList(final Class<T> returnType){
        return executeAndFetch(newScalarResultSetHandler(returnType));
    }

    private <T> ResultSetHandler<T> newScalarResultSetHandler(final Class<T> returnType) {
        final Quirks quirks = getQuirks();
        try {
            final Converter<T> converter = throwIfNull(returnType, quirks.converterOf(returnType));
            return new ResultSetHandler<T>() {
                public T handle(ResultSet resultSet) throws SQLException {
                    Object value = quirks.getRSVal(resultSet, 1);
                    try {
                        return (converter.convert(value));
                    } catch (ConverterException e) {
                        throw new Sql2oException("Error occurred while converting value from database to type " + returnType, e);
                    }
                }
            };
        } catch (ConverterException e) {
            throw new Sql2oException("Can't get converter for type " + returnType, e);
        }
    }

    /************** batch stuff *******************/

    public Query addToBatch(){
        try {
            statement.addBatch();
        } catch (SQLException e) {
            throw new Sql2oException("Error while adding statement to batch", e);
        }

        return this;
    }

    public Connection executeBatch() throws Sql2oException {
        long start = System.currentTimeMillis();
        try {
            connection.setBatchResult(statement.executeBatch());
            try {
                connection.setKeys(this.returnGeneratedKeys ? statement.getGeneratedKeys() : null);
                connection.setCanGetKeys(this.returnGeneratedKeys);
            } catch (SQLException sqlex) {
                throw new Sql2oException("Error while trying to fetch generated keys from database. If you are not expecting any generated keys, fix this error by setting the fetchGeneratedKeys parameter in the createQuery() method to 'false'", sqlex);
            }
        }
        catch (Throwable e) {
            this.connection.onException();
            throw new Sql2oException("Error while executing batch operation: " + e.getMessage(), e);
        }
        finally {
            closeConnectionIfNecessary();
        }

        long end = System.currentTimeMillis();
        logger.debug("total: {} ms; executed batch [{}]", new Object[]{
                end - start,
                this.getName() == null ? "No name" : this.getName()
        });

        return this.connection;
    }

    /*********** column mapping ****************/

    public Map<String, String> getColumnMappings() {
        if (this.isCaseSensitive()){
            return this.caseSensitiveColumnMappings;
        }
        else{
            return this.columnMappings;
        }
    }

    public Query setColumnMappings(Map<String, String> mappings){

        this.caseSensitiveColumnMappings = new HashMap<String, String>();
        this.columnMappings = new HashMap<String, String>();

        for (Map.Entry<String,String> entry : mappings.entrySet()){
            this.caseSensitiveColumnMappings.put(entry.getKey(), entry.getValue());
            this.columnMappings.put(entry.getKey().toLowerCase(), entry.getValue().toLowerCase());
        }

        return this;
    }

    public Query addColumnMapping(String columnName, String propertyName){
        this.caseSensitiveColumnMappings.put(columnName, propertyName);
        this.columnMappings.put(columnName.toLowerCase(), propertyName.toLowerCase());

        return this;
    }

    /************** private stuff ***************/

    private void closeConnectionIfNecessary(){
        try{
            if (connection.autoClose){
                connection.close();
            }
        }
        catch (Exception ex){
            throw new Sql2oException("Error while attempting to close connection", ex);
        }
    }

    private interface ParameterSetter{
        void setParameter(int paramIdx) throws SQLException;
    }
}
