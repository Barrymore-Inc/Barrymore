package me.uquark.barrymore.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseProvider {
    private static final String DB_URL = "jdbc:firebirdsql://localhost:3050/barrymore?encoding=UTF8";
    private static final String DB_USER = "barrymore";
    private static final String DB_PASS = "barrymore";
    public static Connection CONNECTION;

    public static Connection connect() throws SQLException {
        return DriverManager.getConnection(DB_URL, DB_USER, DB_PASS);
    }
}
