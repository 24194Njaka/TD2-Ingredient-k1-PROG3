package org.example;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBConnection {

    private static final String JDBC_URL = System.getenv("JDBC_URL");
    private static final String USERNAME = System.getenv("USERNAME");
    private static final String PASSWORD = System.getenv("PASSWORD");

    private DBConnection() {
    }

    public static Connection getDBConnection() throws SQLException {
        if (JDBC_URL == null || USERNAME == null || PASSWORD == null) {
            throw new RuntimeException(
                    "Variables d'environnement manquantes : JDBC_URL, USERNAME ou PASSWORD"
            );
        }

        return DriverManager.getConnection(JDBC_URL, USERNAME, PASSWORD);
    }
}

