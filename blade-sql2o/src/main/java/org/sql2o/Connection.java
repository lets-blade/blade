package org.sql2o;

import org.sql2o.converters.Converter;
import org.sql2o.converters.ConverterException;
import org.sql2o.logging.LocalLoggerFactory;
import org.sql2o.logging.Logger;
import org.sql2o.quirks.Quirks;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.sql2o.converters.Convert.throwIfNull;

/**
 * Represents a connection to the database with a transaction.
 */
public class Connection implements AutoCloseable {
    
    private final static Logger logger = LocalLoggerFactory.getLogger(Connection.class);

    private java.sql.Connection jdbcConnection;
    private Sql2o sql2o;

    private Integer result = null;
    private int[] batchResult = null;
    private List<Object> keys;
    private boolean canGetKeys;
    
    private boolean rollbackOnException = true;

    public boolean isRollbackOnException() {
        return rollbackOnException;
    }

    public Connection setRollbackOnException(boolean rollbackOnException) {
        this.rollbackOnException = rollbackOnException;
        return this;
    }

    final boolean autoClose;

    Connection(Sql2o sql2o, boolean autoClose) {

        this.autoClose = autoClose;
        this.sql2o = sql2o;
        createConnection();
    }

    void onException() {
        if (isRollbackOnException()) {
            rollback(this.autoClose);
        }
    }

    public java.sql.Connection getJdbcConnection() {
        return jdbcConnection;
    }

    public Sql2o getSql2o() {
        return sql2o;
    }

    public Query createQuery(String queryText, String name){
        boolean returnGeneratedKeys = this.sql2o.getQuirks().returnGeneratedKeysByDefault();
        return createQuery(queryText, name, returnGeneratedKeys);
    }

    public Query createQuery(String queryText, String name, boolean returnGeneratedKeys){

        try {
            if (jdbcConnection.isClosed()){
                createConnection();
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return new Query(this, queryText, name, returnGeneratedKeys);
    }

    public Query createQueryWithParams(String queryText, Object... paramValues){
        Query query = createQuery(queryText, null);
        boolean destroy = true;
        try {
            query.withParams(paramValues);
            destroy = false;
            return query;
        } finally {
            // instead of re-wrapping exception
            // just keep it as-is
            // but kill a query
            if(destroy) query.close();
        }
    }

    public Query createQuery(String queryText){
        return createQuery(queryText, null);
    }

    public Query createQuery(String queryText, boolean returnGeneratedKeys) {
        return createQuery(queryText, null, returnGeneratedKeys);
    }

    public Sql2o rollback(){
        return this.rollback(true).sql2o;
    }

    public Connection rollback(boolean closeConnection){
        try {
            jdbcConnection.rollback();
        }
        catch (SQLException e) {
            logger.warn("Could not roll back transaction. message: {}", e);
        }
        finally {
            if(closeConnection) this.closeJdbcConnection();
        }
        return this;
    }

    public Sql2o commit(){
        return this.commit(true).sql2o;
    }

    public Connection commit(boolean closeConnection){
        try {
            jdbcConnection.commit();
        }
        catch (SQLException e) {
            throw new Sql2oException(e);
        }
        finally {
            if(closeConnection)
                this.closeJdbcConnection();
        }
        return this;
    }

    public int getResult(){
        if (this.result == null){
            throw new Sql2oException("It is required to call executeUpdate() method before calling getResult().");
        }
        return this.result;
    }

    void setResult(int result){
        this.result = result;
    }

    public int[] getBatchResult() {
        if (this.batchResult == null){
            throw new Sql2oException("It is required to call executeBatch() method before calling getBatchResult().");
        }
        return this.batchResult;
    }

    void setBatchResult(int[] value) {
        this.batchResult = value;
    }

    void setKeys(ResultSet rs) throws SQLException {
        if (rs == null){
            this.keys = null;
            return;
        }
        this.keys = new ArrayList<Object>();
        while(rs.next()){
            this.keys.add(rs.getObject(1));
        }
    }

    public Object getKey(){
        if (!this.canGetKeys){
            throw new Sql2oException("Keys where not fetched from database. Please call executeUpdate(true) to fetch keys");
        }
        if (this.keys != null && this.keys.size() > 0){
            return  keys.get(0);
        }
        return null;
    }

    @SuppressWarnings("unchecked") // need to change Convert
    public <V> V getKey(Class returnType){
        final Quirks quirks = this.sql2o.getQuirks();
        Object key = getKey();
        try {
            Converter<V> converter = throwIfNull(returnType, quirks.converterOf(returnType));
            return converter.convert(key);
        } catch (ConverterException e) {
            throw new Sql2oException("Exception occurred while converting value from database to type " + returnType.toString(), e);
        }
    }

    public Object[] getKeys(){
        if (!this.canGetKeys){
            throw new Sql2oException("Keys where not fetched from database. Please set the returnGeneratedKeys parameter in the createQuery() method to enable fetching of generated keys.");
        }
        if (this.keys != null){
            return this.keys.toArray();
        }
        return null;
    }

    @SuppressWarnings("unchecked") // need to change Convert
    public <V> List<V> getKeys(Class<V> returnType) {
        final Quirks quirks = sql2o.getQuirks();
        if (!this.canGetKeys) {
            throw new Sql2oException("Keys where not fetched from database. Please set the returnGeneratedKeys parameter in the createQuery() method to enable fetching of generated keys.");
        }

        if (this.keys != null) {
            try {
                Converter<V> converter = throwIfNull(returnType, quirks.converterOf(returnType));

                List<V> convertedKeys = new ArrayList<V>(this.keys.size());

                for (Object key : this.keys) {
                    convertedKeys.add(converter.convert(key));
                }

                return convertedKeys;
            }
            catch (ConverterException e) {
                throw new Sql2oException("Exception occurred while converting value from database to type " + returnType.toString(), e);
            }
        }

        return null;
    }

    void setCanGetKeys(boolean canGetKeys) {
        this.canGetKeys = canGetKeys;
    }

    private final Set<Statement> statements = new HashSet<Statement>();

    void registerStatement(Statement statement){
        statements.add(statement);
    }
    void removeStatement(Statement statement){
        statements.remove(statement);
    }

    public void close() {
        boolean connectionIsClosed;
        try {
            connectionIsClosed = jdbcConnection.isClosed();
        } catch (SQLException e) {
            throw new Sql2oException("Sql2o encountered a problem while trying to determine whether the connection is closed.", e);
        }

        if (!connectionIsClosed) {

            for (Statement statement : statements) {
                try {
                    getSql2o().getQuirks().closeStatement(statement);
                } catch (Throwable e) {
                    logger.warn("Could not close statement.", e);
                }
            }
            statements.clear();

            boolean autoCommit = false;
            try {
                autoCommit = jdbcConnection.getAutoCommit();
            }
            catch (SQLException e) {
                logger.warn("Could not determine connection auto commit mode.", e);
            }

            // if in transaction, rollback, otherwise just close
            if (autoCommit) {
                this.closeJdbcConnection();
            }
            else {
                this.rollback(true);
            }
        }
    }

    private void createConnection(){
        try{
            if(this.getSql2o().getDataSource() == null && this.getSql2o().getConnectionPool() == null)
                throw new Sql2oException("DataSource can't be null");
            else if(this.getSql2o().getDataSource() != null) {
                this.jdbcConnection = this.getSql2o().getDataSource().getConnection();
            }else if(this.getSql2o().getConnectionPool() != null) { //Get Connection Object from PooledConnection
                this.jdbcConnection = this.getSql2o().getConnectionPool().getConnection();
            }
        }
        catch(Exception ex){
            throw new RuntimeException(String.format("Could not aquire a connection from DataSource - ", ex.getMessage()), ex);
        }
    }

    private void closeJdbcConnection() {
        try {
            jdbcConnection.close();
        }
        catch (SQLException e) {
            logger.warn("Could not close connection. message: {}", e);
        }
    }
}
