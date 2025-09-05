package org.example;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class DatabaseTest {
    private static final String DB_URL = "jdbc:sqlite:secure_journal.db";

    public static void main(String[] args) {
        // Try connecting to SQLite
        try (Connection conn = DriverManager.getConnection(DB_URL)) {
            if (conn != null) {
                System.out.println("✅ Connected to SQLite database!");

                // Create tables if they don't exist
                try (Statement stmt = conn.createStatement()) {
                    String userTable = """
                        CREATE TABLE IF NOT EXISTS users (
                            id INTEGER PRIMARY KEY AUTOINCREMENT,
                            username TEXT UNIQUE NOT NULL,
                            password_hash TEXT NOT NULL
                        );
                        """;

                    String entryTable = """
                        CREATE TABLE IF NOT EXISTS journal_entries (
                            id INTEGER PRIMARY KEY AUTOINCREMENT,
                            user_id INTEGER NOT NULL,
                            title TEXT NOT NULL,
                            content TEXT NOT NULL,
                            created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                            FOREIGN KEY(user_id) REFERENCES users(id)
                        );
                        """;

                    stmt.execute(userTable);
                    stmt.execute(entryTable);

                    System.out.println("✅ Tables created (if not already).");
                }
            }
        } catch (SQLException e) {
            System.out.println("❌ Database connection failed: " + e.getMessage());
        }
    }
}

