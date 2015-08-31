package org.sql2o;

import javax.sql.DataSource;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.SQLFeatureNotSupportedException;
import java.util.Properties;

/**
 * Used internally by sql2o, if the {@link Sql2o#Sql2o(String, String, String)} constructor overload.
 */
public class GenericDatasource implements DataSource {

    private final String url;
    private final Properties properties;

    public GenericDatasource(String url, String user, String password) {

        if (!url.startsWith("jdbc")){
            url = "jdbc:" + url;
        }

        this.url = url;
        this.properties = new Properties();
        set(properties,user,password);
    }

    private void set(Properties info, String user, String password) {
        if (user != null) {
            info.put("user", user);
        }
        if (password != null) {
            info.put("password", password);
        }
    }

    public GenericDatasource(String url, Properties properties) {

        if (!url.startsWith("jdbc")){
            url = "jdbc:" + url;
        }

        this.url = url;
        this.properties = properties;
    }

    public String getUrl() {
        return url;
    }

    public String getUser() {
        return properties.getProperty("user");
    }

    public String getPassword() {
        return properties.getProperty("password");
    }

    public Connection getConnection() throws SQLException {
        return DriverManager.getConnection(this.getUrl(), properties);
    }

    public Connection getConnection(String username, String password) throws SQLException {
        Properties info = new Properties(this.properties);
        set(info,username,password);
        return DriverManager.getConnection(this.getUrl(), info);
    }

    public PrintWriter getLogWriter() throws SQLException {
        return DriverManager.getLogWriter();
    }

    public void setLogWriter(PrintWriter printWriter) throws SQLException {
        DriverManager.setLogWriter(printWriter);
    }

    public void setLoginTimeout(int i) throws SQLException {
        DriverManager.setLoginTimeout(i);
    }

    public int getLoginTimeout() throws SQLException {
        return DriverManager.getLoginTimeout();
    }

    public java.util.logging.Logger getParentLogger() throws SQLFeatureNotSupportedException {
        throw new SQLFeatureNotSupportedException();
    }

    public <T> T unwrap(Class<T> tClass) throws SQLException {
        throw new SQLFeatureNotSupportedException();
    }

    public boolean isWrapperFor(Class<?> aClass) throws SQLException {
        return false;
    }
}
