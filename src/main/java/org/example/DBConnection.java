package org.example;

import io.github.cdimascio.dotenv.Dotenv;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBConnection {

    private static final Dotenv dotenv = Dotenv.load();

    public static Connection getDBConnection() {
        try {
            String jdbcUrl = dotenv.get("JDBC_URL");
            String username = dotenv.get("USERNAME");
            String password = dotenv.get("PASSWORD");

            if (jdbcUrl == null || username == null || password == null) {
                throw new RuntimeException("Variables d'environnement manquantes (.env)");
            }

            return DriverManager.getConnection(jdbcUrl, username, password);

        } catch (SQLException e) {
            throw new RuntimeException("Failed to connect to the database", e);
        }
    }
    public static void close(Connection connection) {
        if (connection != null) {
            try {
                connection.close();
            } catch (SQLException e) {
                throw new RuntimeException("Failed to close the connection", e);
            }
        }
    }

}

