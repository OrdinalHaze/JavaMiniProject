package org.example;

public class JournalEntry {
    public int id;
    public String title;
    public String content;
    public String tags;
    public String createdAt;

    public JournalEntry(int id, String title, String content, String tags, String createdAt) {
        this.id = id;
        this.title = title;
        this.content = content;
        this.tags = tags;
        this.createdAt = createdAt;
    }

    @Override
    public String toString() {
        // used for list display if needed
        String shortTitle = (title == null || title.isBlank()) ? "Untitled" : title;
        String tagPart = (tags == null || tags.isBlank()) ? "" : " [" + tags + "]";
        return shortTitle + tagPart + " — " + createdAt;
    }
}

