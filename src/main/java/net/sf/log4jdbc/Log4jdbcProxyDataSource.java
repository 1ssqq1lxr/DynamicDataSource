package net.sf.log4jdbc;

import com.alibaba.druid.pool.DruidDataSource;

import javax.sql.DataSource;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.SQLFeatureNotSupportedException;
import java.util.logging.Logger;

public class Log4jdbcProxyDataSource implements DataSource {
    private DruidDataSource realDataSource;
    private SpyLogDelegator spyLogDelegator = SpyLogFactory.getSpyLogDelegator();

    public void setDumpSqlMaxLineLength(int dumpSqlMaxLineLength)
    {
        DriverSpy.DumpSqlMaxLineLength = dumpSqlMaxLineLength;
    }

    public void setDataSource(DruidDataSource realDataSource)
    {
        this.realDataSource = realDataSource;
    }

    public SpyLogDelegator getLogFormatter() {
        return this.spyLogDelegator;
    }

    public void setLogFormatter(SpyLogDelegator spyLogDelegator)
    {
        this.spyLogDelegator = spyLogDelegator;
    }

    public Connection getConnection() throws SQLException
    {
        Connection connection = this.realDataSource.getConnection();
        return new ConnectionSpy(connection, DriverSpy.getRdbmsSpecifics(connection));
    }

    public Connection getConnection(String username, String password) throws SQLException
    {
        Connection connection = this.realDataSource.getConnection(username, password);
        return new ConnectionSpy(connection, DriverSpy.getRdbmsSpecifics(connection));
    }
    public int getLoginTimeout() throws SQLException {
        return this.realDataSource.getLoginTimeout();
    }
    public PrintWriter getLogWriter() throws SQLException {
        return this.realDataSource.getLogWriter();
    }
    public boolean isWrapperFor(Class<?> iface) throws SQLException {
        return this.realDataSource.isWrapperFor(iface);
    }
    public void setLoginTimeout(int seconds) throws SQLException {
        this.realDataSource.setLoginTimeout(seconds);
    }
    public void setLogWriter(PrintWriter out) throws SQLException {
        this.realDataSource.setLogWriter(out);
    }
    public <T> T unwrap(Class<T> iface) throws SQLException {
        return this.realDataSource.unwrap(iface);
    }

    public Logger getParentLogger()
            throws SQLFeatureNotSupportedException
    {
        return null;
    }

    public void setUrl(String url) {
        this.realDataSource.setUrl(url);
    }

    public String getUrl() {
        return this.realDataSource.getUrl();
    }

    public void setUsername(String username) {
        this.realDataSource.setUsername(username);
    }

    public String getUsername() {
        return this.realDataSource.getUsername();
    }

    public void setPassword(String password) {
        this.realDataSource.setPassword(password);
    }

    public String getPassword() {
        return this.realDataSource.getPassword();
    }

    public void setDriverClassName(String driverClass) {
        this.realDataSource.setDriverClassName(driverClass);
    }

    public String getDriverClassName() {
        return this.realDataSource.getDriverClassName();
    }

    public void setInitialSize(int initialSize) {
        this.realDataSource.setInitialSize(initialSize);
    }

    public int getInitialSize() {
        return this.realDataSource.getInitialSize();
    }

    public void setMinIdle(int minIdle) {
        this.realDataSource.setMinIdle(minIdle);
    }

    public int getMinIdle() {
        return this.realDataSource.getMinIdle();
    }

    public void setMaxActive(int maxactive) {
        this.realDataSource.setMaxActive(maxactive);
    }

    public int getMaxActive() {
        return this.realDataSource.getMaxActive();
    }

    public void setTestOnBorrow(boolean testOnBorrow) {
        this.realDataSource.setTestOnBorrow(testOnBorrow);
    }

    public boolean isTestOnBorrow() {
        return this.realDataSource.isTestOnBorrow();
    }

    public void setTestWhileIdle(boolean testWhileIdle) {
        this.realDataSource.setTestWhileIdle(testWhileIdle);
    }

    public boolean isTestWhileIdle() {
        return this.realDataSource.isTestWhileIdle();
    }

    public void setTimeBetweenEvictionRunsMillis(long timeBetweenEvictionRunsMillis) {
        this.realDataSource.setTimeBetweenEvictionRunsMillis(timeBetweenEvictionRunsMillis);
    }

    public long getTimeBetweenEvictionRunsMillis() {
        return this.realDataSource.getTimeBetweenEvictionRunsMillis();
    }
}
