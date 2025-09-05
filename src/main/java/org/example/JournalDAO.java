package org.example;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class JournalDAO {

    public JournalDAO() {
        String sql = """
            CREATE TABLE IF NOT EXISTS entries (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                user_id INTEGER NOT NULL,
                title TEXT,
                content TEXT,
                tags TEXT,
                created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                FOREIGN KEY(user_id) REFERENCES users(id) ON DELETE CASCADE
            );
            """;
        try (Connection conn = Database.connect();
             Statement st = conn.createStatement()) {
            st.execute(sql);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Insert new entry
    public boolean saveEntry(int userId, String title, String content, String tags) {
        String sql = "INSERT INTO entries(user_id, title, content, tags) VALUES(?, ?, ?, ?)";
        try (Connection conn = Database.connect();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, userId);
            ps.setString(2, title);
            ps.setString(3, content);
            ps.setString(4, tags);
            ps.executeUpdate();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // Update existing entry
    public boolean updateEntry(int entryId, String title, String content, String tags) {
        String sql = "UPDATE entries SET title = ?, content = ?, tags = ?, created_at = CURRENT_TIMESTAMP WHERE id = ?";
        try (Connection conn = Database.connect();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, title);
            ps.setString(2, content);
            ps.setString(3, tags);
            ps.setInt(4, entryId);
            int updated = ps.executeUpdate();
            return updated > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // Delete entry
    public boolean deleteEntry(int entryId) {
        String sql = "DELETE FROM entries WHERE id = ?";
        try (Connection conn = Database.connect();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, entryId);
            int deleted = ps.executeUpdate();
            return deleted > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // Fetch entries for user (most recent first)
    public List<JournalEntry> getEntries(int userId) {
        List<JournalEntry> list = new ArrayList<>();
        String sql = "SELECT id, title, content, tags, created_at FROM entries WHERE user_id = ? ORDER BY created_at DESC";
        try (Connection conn = Database.connect();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(new JournalEntry(
                            rs.getInt("id"),
                            rs.getString("title"),
                            rs.getString("content"),
                            rs.getString("tags"),
                            rs.getString("created_at")
                    ));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    // Search by title/content/tags
    public List<JournalEntry> searchEntries(int userId, String keyword) {
        List<JournalEntry> list = new ArrayList<>();
        String sql = "SELECT id, title, content, tags, created_at FROM entries WHERE user_id = ? AND " +
                "(title LIKE ? OR content LIKE ? OR tags LIKE ?) ORDER BY created_at DESC";
        String like = "%" + keyword + "%";
        try (Connection conn = Database.connect();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, userId);
            ps.setString(2, like);
            ps.setString(3, like);
            ps.setString(4, like);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(new JournalEntry(
                            rs.getInt("id"),
                            rs.getString("title"),
                            rs.getString("content"),
                            rs.getString("tags"),
                            rs.getString("created_at")
                    ));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    // Helper for export (same as getEntries but kept for clarity)
    public List<JournalEntry> getAllEntriesRaw(int userId) {
        return getEntries(userId);
    }

    public JournalEntry getEntryById(int id) {
        String sql = "SELECT id, title, content, tags, created_at FROM entries WHERE id = ?";
        try (Connection conn = Database.connect();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new JournalEntry(
                            rs.getInt("id"),
                            rs.getString("title"),
                            rs.getString("content"),
                            rs.getString("tags"),
                            rs.getString("created_at")
                    );
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
}
