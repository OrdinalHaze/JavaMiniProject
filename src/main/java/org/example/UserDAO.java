package org.example;

import java.sql.*;

/**
 * UserDAO - create table if needed, register, login, getUserId.
 */
public class UserDAO {

    public UserDAO() {
        String sql = """
            CREATE TABLE IF NOT EXISTS users (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                username TEXT UNIQUE NOT NULL,
                password_hash TEXT NOT NULL,
                created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
            );
            """;
        try (Connection conn = Database.connect();
             Statement st = conn.createStatement()) {
            st.execute(sql);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public boolean register(String username, String password) {
        if (username == null || username.isBlank() || password == null || password.isBlank()) return false;
        String hashed = PasswordUtils.hashPassword(password);
        String sql = "INSERT INTO users(username, password_hash) VALUES(?, ?)";
        try (Connection conn = Database.connect();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, username.trim());
            ps.setString(2, hashed);
            ps.executeUpdate();
            return true;
        } catch (SQLException e) {
            // likely duplicate username
            // e.printStackTrace();
            return false;
        }
    }

    public boolean login(String username, String password) {
        if (username == null || username.isBlank() || password == null) return false;
        String sql = "SELECT password_hash FROM users WHERE username = ?";
        try (Connection conn = Database.connect();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, username.trim());
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    String storedHash = rs.getString("password_hash");
                    return storedHash.equals(PasswordUtils.hashPassword(password));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public int getUserId(String username) {
        if (username == null || username.isBlank()) return -1;
        String sql = "SELECT id FROM users WHERE username = ?";
        try (Connection conn = Database.connect();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, username.trim());
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return rs.getInt("id");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1;
    }
}

