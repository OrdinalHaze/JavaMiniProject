package org.example;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class Database {
    private static final String URL = "jdbc:sqlite:secure_journal.db";

    // Use this throughout the project
    public static Connection connect() throws SQLException {
        return DriverManager.getConnection(URL);
    }
}


