package com.mysql.cj.jdbc;

import java.sql.*;
import java.util.Properties;
import java.util.logging.Logger;

public class Driver implements java.sql.Driver {

    private final String URL = "jdbc:mysql://localhost:3306/pay_my_buddy?useSSL=false&useUnicode=true&useJDBCCompliantTimezoneShift=true&useLegacyDatetimeCode=false&serverTimezone=UTC";
    private final String USERNAME = "root";
    private final String PASSWORD = "password";


    public Driver() {
        try {
            java.sql.DriverManager.registerDriver(this);
        } catch (SQLException E) {
            throw new RuntimeException("Can't register driver!");
        }
    }

    public Connection connect() throws SQLException {
        if (!acceptsURL(URL)) {
            return null;
        }
        return DriverManager.getConnection(URL, USERNAME, PASSWORD);
    }

    @Override
    public Connection connect(String url, Properties info) throws SQLException {
        return null;
    }

    @Override
    public boolean acceptsURL(String url) throws SQLException {
        return url.startsWith("jdbc:my_database:");
    }

    @Override
    public DriverPropertyInfo[] getPropertyInfo(String url, Properties info) throws SQLException {
        return new DriverPropertyInfo[0];
    }

    @Override
    public int getMajorVersion() {
        return 0;
    }

    @Override
    public int getMinorVersion() {
        return 1;
    }

    @Override
    public boolean jdbcCompliant() {
        return false;
    }

    @Override
    public Logger getParentLogger() throws SQLFeatureNotSupportedException {
        return Logger.getLogger(Driver.class.getName());
    }
}
