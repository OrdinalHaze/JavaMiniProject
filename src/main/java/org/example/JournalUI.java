package org.example;

import javax.swing.*;
import java.awt.*;
import java.io.FileWriter;
import java.util.List;

public class JournalUI extends JFrame {
    private final int userId;
    private final String username;
    private final JournalDAO journalDAO = new JournalDAO();

    private final DefaultListModel<String> listModel = new DefaultListModel<>();
    private final JList<JournalEntry> entryJList = new JList<>(new DefaultListModel<>()); // we will set model later
    private java.util.List<JournalEntry> currentEntries = java.util.List.of();

    public JournalUI(int userId, String username) {
        this.userId = userId;
        this.username = username;

        setTitle("Secure Journal — " + username);
        setSize(900, 600);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JTabbedPane tabs = new JTabbedPane();

        // ---------------- Write tab ----------------
        JPanel writePanel = new JPanel(new BorderLayout(8, 8));
        writePanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JTextField titleField = new JTextField("Untitled");
        JTextField tagsField = new JTextField();
        JTextArea contentArea = new JTextArea();
        contentArea.setLineWrap(true);
        contentArea.setWrapStyleWord(true);

        JPanel top = new JPanel(new GridLayout(2, 1, 6, 6));
        top.add(titleField);
        top.add(tagsField);

        JButton saveBtn = new JButton("Save Entry");
        saveBtn.addActionListener(e -> {
            String title = titleField.getText();
            String tags = tagsField.getText();
            String content = contentArea.getText();
            if (content == null || content.isBlank()) {
                JOptionPane.showMessageDialog(this, "Cannot save empty entry", "Warning", JOptionPane.WARNING_MESSAGE);
                return;
            }
            boolean ok = journalDAO.saveEntry(userId, title, content, tags);
            if (ok) {
                JOptionPane.showMessageDialog(this, "Entry saved");
                titleField.setText("Untitled");
                tagsField.setText("");
                contentArea.setText("");
                loadEntries(); // refresh view
            } else {
                JOptionPane.showMessageDialog(this, "Failed to save entry", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        writePanel.add(top, BorderLayout.NORTH);
        writePanel.add(new JScrollPane(contentArea), BorderLayout.CENTER);
        writePanel.add(saveBtn, BorderLayout.SOUTH);

        tabs.addTab("✍️ Write Entry", writePanel);

        // ---------------- View/Search tab ----------------
        JPanel viewPanel = new JPanel(new BorderLayout(8, 8));
        viewPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Left: list
        DefaultListModel<String> titlesModel = new DefaultListModel<>();
        JList<String> titlesList = new JList<>(titlesModel);
        titlesList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        // Right: viewer
        JTextArea viewer = new JTextArea();
        viewer.setEditable(false);
        viewer.setLineWrap(true);
        viewer.setWrapStyleWord(true);

        // Buttons
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JTextField searchField = new JTextField(25);
        JButton searchBtn = new JButton("🔍 Search");
        JButton refreshBtn = new JButton("🔄 Refresh");
        topPanel.add(new JLabel("Search:"));
        topPanel.add(searchField);
        topPanel.add(searchBtn);
        topPanel.add(refreshBtn);

        JPanel actionPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton editBtn = new JButton("Edit");
        JButton deleteBtn = new JButton("Delete");
        JButton exportTxtBtn = new JButton("Export TXT");
        JButton exportCsvBtn = new JButton("Export CSV");
        actionPanel.add(editBtn);
        actionPanel.add(deleteBtn);
        actionPanel.add(exportTxtBtn);
        actionPanel.add(exportCsvBtn);

        JSplitPane split = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, new JScrollPane(titlesList), new JScrollPane(viewer));
        split.setDividerLocation(300);

        viewPanel.add(topPanel, BorderLayout.NORTH);
        viewPanel.add(split, BorderLayout.CENTER);
        viewPanel.add(actionPanel, BorderLayout.SOUTH);

        tabs.addTab("📖 View / Search", viewPanel);

        // Load entries helper
        Runnable loadEntries = () -> {
            titlesModel.clear();
            currentEntries = journalDAO.getEntries(userId);
            for (JournalEntry e : currentEntries) {
                titlesModel.addElement(e.toString());
            }
        };

        // Initial load
        loadEntries.run();

        // Selection listener
        titlesList.addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                int idx = titlesList.getSelectedIndex();
                if (idx >= 0 && idx < currentEntries.size()) {
                    JournalEntry entry = currentEntries.get(idx);
                    String text = "Title: " + (entry.title == null ? "" : entry.title) + "\n" +
                            "Tags: " + (entry.tags == null ? "" : entry.tags) + "\n" +
                            "Created: " + (entry.createdAt == null ? "" : entry.createdAt) + "\n\n" +
                            entry.content;
                    viewer.setText(text);
                } else {
                    viewer.setText("");
                }
            }
        });

        // Refresh button
        refreshBtn.addActionListener(e -> loadEntries.run());

        // Search button
        searchBtn.addActionListener(e -> {
            String kw = searchField.getText().trim();
            titlesModel.clear();
            if (kw.isBlank()) {
                currentEntries = journalDAO.getEntries(userId);
            } else {
                currentEntries = journalDAO.searchEntries(userId, kw);
            }
            for (JournalEntry je : currentEntries) titlesModel.addElement(je.toString());
        });

        // Edit button (open simple dialog)
        editBtn.addActionListener(e -> {
            int idx = titlesList.getSelectedIndex();
            if (idx < 0 || idx >= currentEntries.size()) {
                JOptionPane.showMessageDialog(this, "Select an entry to edit", "Info", JOptionPane.INFORMATION_MESSAGE);
                return;
            }
            JournalEntry selected = currentEntries.get(idx);
            JDialog dlg = new JDialog(this, "Edit Entry", true);
            dlg.setSize(700, 500);
            dlg.setLocationRelativeTo(this);

            JTextField tTitle = new JTextField(selected.title);
            JTextField tTags = new JTextField(selected.tags);
            JTextArea tContent = new JTextArea(selected.content);
            tContent.setLineWrap(true);
            tContent.setWrapStyleWord(true);
            JButton saveEdit = new JButton("Save");

            saveEdit.addActionListener(ae -> {
                boolean ok = journalDAO.updateEntry(selected.id, tTitle.getText(), tContent.getText(), tTags.getText());
                if (ok) {
                    JOptionPane.showMessageDialog(dlg, "Updated");
                    dlg.dispose();
                    loadEntries.run();
                } else {
                    JOptionPane.showMessageDialog(dlg, "Update failed", "Error", JOptionPane.ERROR_MESSAGE);
                }
            });

            JPanel topEdit = new JPanel(new GridLayout(2, 1));
            topEdit.add(tTitle);
            topEdit.add(tTags);

            dlg.add(topEdit, BorderLayout.NORTH);
            dlg.add(new JScrollPane(tContent), BorderLayout.CENTER);
            dlg.add(saveEdit, BorderLayout.SOUTH);
            dlg.setVisible(true);
        });

        // Delete button
        deleteBtn.addActionListener(e -> {
            int idx = titlesList.getSelectedIndex();
            if (idx < 0 || idx >= currentEntries.size()) {
                JOptionPane.showMessageDialog(this, "Select an entry to delete", "Info", JOptionPane.INFORMATION_MESSAGE);
                return;
            }
            JournalEntry sel = currentEntries.get(idx);
            int confirm = JOptionPane.showConfirmDialog(this, "Delete selected entry?", "Confirm", JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                if (journalDAO.deleteEntry(sel.id)) {
                    JOptionPane.showMessageDialog(this, "Deleted");
                    loadEntries.run();
                } else {
                    JOptionPane.showMessageDialog(this, "Delete failed", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        // Export TXT
        exportTxtBtn.addActionListener(e -> {
            JFileChooser fc = new JFileChooser();
            fc.setSelectedFile(new java.io.File("journal_backup.txt"));
            if (fc.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
                try (FileWriter fw = new FileWriter(fc.getSelectedFile())) {
                    List<JournalEntry> all = journalDAO.getAllEntriesRaw(userId);
                    for (JournalEntry je : all) {
                        fw.write("Title: " + je.title + "\n");
                        fw.write("Tags: " + je.tags + "\n");
                        fw.write("Created: " + je.createdAt + "\n\n");
                        fw.write(je.content + "\n");
                        fw.write("---------------------------------------------------\n\n");
                    }
                    JOptionPane.showMessageDialog(this, "Exported TXT");
                } catch (Exception ex) {
                    ex.printStackTrace();
                    JOptionPane.showMessageDialog(this, "Export failed", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        // Export CSV
        exportCsvBtn.addActionListener(e -> {
            JFileChooser fc = new JFileChooser();
            fc.setSelectedFile(new java.io.File("journal_backup.csv"));
            if (fc.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
                try (FileWriter fw = new FileWriter(fc.getSelectedFile())) {
                    fw.write("Title,Tags,Created At,Content\n");
                    List<JournalEntry> all = journalDAO.getAllEntriesRaw(userId);
                    for (JournalEntry je : all) {
                        String safe = je.content == null ? "" : je.content.replace("\"", "\"\"");
                        String t = je.title == null ? "" : je.title.replace("\"", "\"\"");
                        String tg = je.tags == null ? "" : je.tags.replace("\"", "\"\"");
                        fw.write("\"" + t + "\",\"" + tg + "\",\"" + je.createdAt + "\",\"" + safe + "\"\n");
                    }
                    JOptionPane.showMessageDialog(this, "Exported CSV");
                } catch (Exception ex) {
                    ex.printStackTrace();
                    JOptionPane.showMessageDialog(this, "Export failed", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        add(tabs);
    }

    private void loadEntries() {
        // not used (we used lambda in constructor), but kept if you want to call externally
    }
}
