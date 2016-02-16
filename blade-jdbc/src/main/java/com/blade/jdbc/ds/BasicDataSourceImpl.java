package com.blade.jdbc.ds;

import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.Driver;
import java.sql.SQLException;
import java.sql.SQLFeatureNotSupportedException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Properties;

import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BasicDataSourceImpl implements DataSource, BasicDataSource {
	
    private static final Logger log = LoggerFactory.getLogger(BasicDataSourceImpl.class);
    
    private final LinkedList<ConnectionWrapper> pool = new LinkedList<ConnectionWrapper>();
    
    private int loginTimeout = 10;
    private PrintWriter logWriter;
    
    private final String name;
    private final Driver driver;
    private final String url;
    private final String user;
    private final String password;
    private final long keepAlive;
    private final long borrowTimeout;
    private final int lockTimeout;
    private final int poolSize;
    
    private long checkIdleConnectionsTime;
    private int activeCount;
    private int waitingThreads;
    private boolean closed;

    public BasicDataSourceImpl(String name, String driver, String url, String user, String pass) {
        try {
            this.name = name;
            this.driver = (Driver) Class.forName(driver).newInstance();
            this.url = url;
            this.user = user;
            this.password = pass;
            this.keepAlive = 1800 * 1000L;
            this.borrowTimeout = 3 * 1000L;
            this.lockTimeout = -1;
            this.poolSize = 10;
        } catch (Exception e) {
            throw new IllegalArgumentException("Invalid DataSource descriptor for " + name, e);
        }
    }
    
    public void close() {
        synchronized (pool) {
            for (ConnectionWrapper connection : pool) {
                connection.closeUnderlyingConnection();
            }
            activeCount = 0;
            closed = true;
            pool.clear();
            pool.notifyAll();
        }
    }

    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return "DataSourceImpl{" + name + '}';
    }

    @Override
    public Connection getConnection() throws SQLException {
    	return borrowConnection();
    }

    @Override
    public Connection getConnection(String username, String password) throws SQLException {
        return getConnection();
    }

    @Override
    public PrintWriter getLogWriter() {
        return logWriter;
    }

    @Override
    public void setLogWriter(PrintWriter out) {
        this.logWriter = out;
    }

    @Override
    public void setLoginTimeout(int seconds) {
        this.loginTimeout = seconds;
    }

    @Override
    public int getLoginTimeout() {
        return loginTimeout;
    }
    
    public <T> T unwrap(Class<T> iface) throws SQLException {
        throw new SQLException("Impossible to unwrap");
    }

    public boolean isWrapperFor(Class<?> iface) {
        return false;
    }

    private ConnectionWrapper borrowConnection() throws SQLException {
        long accessTime = System.currentTimeMillis();
        long waited = 0;

        if (accessTime > checkIdleConnectionsTime) {
            checkIdleConnectionsTime = accessTime + keepAlive / 10;
            closeIdleConnections(accessTime - keepAlive);
        }

        reuse:
        synchronized (pool) {
            while (!closed) {
                // First try to get an idle object from the queue
                ConnectionWrapper connection = pool.pollFirst();
                if (connection != null) {
                    connection.lastAccessTime = accessTime;
                    return connection;
                }

                // If capacity permits, create a new object out of the lock
                if (activeCount < poolSize) {
                	activeCount++;
                    break reuse;
                }

                // Lastly wait until an existing connection becomes free
                waitForFreeConnection(borrowTimeout - waited);
                waited = System.currentTimeMillis() - accessTime;
            }
            throw new SQLException("DataSource is closed");
        }

        ConnectionWrapper connection = null;
        try {
            return connection = new ConnectionWrapper(getRawConnection(), this, accessTime);
        } finally {
            if (connection == null) decreaseCount();
        }
    }

    private Connection getRawConnection() throws SQLException {
        Properties props = new Properties();
        if (user != null) props.put("user", user);
        if (password != null) props.put("password", password);

        Connection connection = driver.connect(url, props);
        if (connection == null) {
            throw new SQLException("Unsupported connection string: " + url);
        }

        if (lockTimeout >= 0) {
            executeRawSQL(connection, "SET LOCK_TIMEOUT " + lockTimeout);
        }

        return connection;
    }

    private void executeRawSQL(Connection connection, String sql) {
        try {
            Statement stmt = connection.createStatement();
            try {
                stmt.executeUpdate(sql);
            } finally {
                stmt.close();
            }
        } catch (Throwable e) {
            log.error("Cannot execute " + sql + " on " + toString(), e);
        }
    }

    private void closeIdleConnections(long closeTime) {
        ArrayList<ConnectionWrapper> idleConnections = new ArrayList<ConnectionWrapper>();

        synchronized (pool) {
            for (Iterator<ConnectionWrapper> iterator = pool.iterator(); iterator.hasNext(); ) {
                ConnectionWrapper connection = iterator.next();
                if (connection.lastAccessTime < closeTime) {
                    idleConnections.add(connection);
                    iterator.remove();
                    decreaseCount();
                }
            }
        }

        if (!idleConnections.isEmpty()) {
            if (log.isDebugEnabled()) {
                log.debug("Closing " + idleConnections.size() + " idle connections on " + toString());
            }
            for (ConnectionWrapper connection : idleConnections) {
                connection.closeUnderlyingConnection();
            }
        }
    }

    private void waitForFreeConnection(long waitTime) throws SQLException {
        if (waitTime <= 0) {
            throw new SQLException("DataSource timed out waiting for a free connection");
        }

        waitingThreads++;
        try {
            pool.wait(waitTime);
        } catch (InterruptedException e) {
            throw new SQLException("Interrupted while waiting for a free connection");
        }
        waitingThreads--;
    }

    private void decreaseCount() {
        synchronized (pool) {
            if (!closed) {
            	activeCount--;
                if (waitingThreads > 0) pool.notify();
            }
        }
    }
    
    void releaseConnection(ConnectionWrapper connection) {
        if (connection.invalidate) {
            decreaseCount();
        } else {
            synchronized (pool) {
                if (!closed) {
                    pool.addFirst(connection);
                    if (waitingThreads > 0) pool.notify();
                    return;
                }
            }
        }
        connection.closeUnderlyingConnection();
    }

    @Override
    public String getUrl() {
        return url;
    }

    @Override
    public int activeCount() {
        return activeCount;
    }

    @Override
    public int getIdleConnections() {
        synchronized (pool) {
            return pool.size();
        }
    }
    
    @Override
    public int getMaxConnections() {
        return poolSize;
    }

    @Override
    public long getBorrowTimeout() {
        return borrowTimeout;
    }

    @Override
    public long getLockTimeout() {
        return lockTimeout;
    }

	@Override
	public java.util.logging.Logger getParentLogger() throws SQLFeatureNotSupportedException {
		return null;
	}
    
}
