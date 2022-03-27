package com.svr.app.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DataBase {

    private static final String url = "jdbc:postgresql://localhost:5432/moodle_lab";
    private static final String username = "postgres";
    private static final String password = "hidra01#";
    private static Connection connection;

    public static Connection getInstance() throws SQLException {
        if ( connection == null ) {
            connection = DriverManager.getConnection( url, username, password);
        }
        return connection;
    }

}
