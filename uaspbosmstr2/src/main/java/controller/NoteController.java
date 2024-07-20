/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
/**
 *
 * @author USER
 */
package controller;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import model.Note;

public class NoteController {
    private static final String TABLE_NAME = "notes";

    public NoteController() {
        createTable();
    }

    private void createTable() {
        String sql = "CREATE TABLE IF NOT EXISTS " + TABLE_NAME + " ("
                + "id INTEGER PRIMARY KEY AUTOINCREMENT,"
                + "description TEXT NOT NULL,"
                + "timestamp TEXT NOT NULL"
                + ");";
        try (Connection conn = Database.connect();
             Statement stmt = conn.createStatement()) {
            stmt.execute(sql);
        } catch (SQLException e) {
            System.out.println("Error creating table: " + e.getMessage());
        }
    }

    public void addNote(String description) {
        String sql = "INSERT INTO " + TABLE_NAME + " (description, timestamp) VALUES (?, ?)";
        try (Connection conn = Database.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, description);
            pstmt.setString(2, getCurrentTimestamp());
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.out.println("Error adding note: " + e.getMessage());
        }
    }

    public List<Note> getAllNotes() {
        List<Note> notes = new ArrayList<>();
        String sql = "SELECT * FROM " + TABLE_NAME + " ORDER BY timestamp DESC";
        try (Connection conn = Database.connect();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                Note note = new Note(
                        rs.getInt("id"),
                        rs.getString("description"),
                        rs.getString("timestamp")
                );
                notes.add(note);
            }
        } catch (SQLException e) {
            System.out.println("Error retrieving notes: " + e.getMessage());
        }
        return notes;
    }

    public void deleteNote(int id) {
        String sql = "DELETE FROM " + TABLE_NAME + " WHERE id = ?";
        try (Connection conn = Database.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.out.println("Error deleting note: " + e.getMessage());
        }
    }

    private String getCurrentTimestamp() {
        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        return now.format(formatter);
    }
}