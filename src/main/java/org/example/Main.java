package org.example;

import java.sql.Connection;

public class Main {

    public static void main(String[] args) {
        DBConnection dbConnection = new DBConnection();
        Connection connection = null;

        try {
            connection = dbConnection.getConnection();
            System.out.println("Connection successful");
            // SQL operations here
        } finally {
            dbConnection.close(connection);
        }


    }
}
