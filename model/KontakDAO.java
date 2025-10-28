package model;

import database.DatabaseConnection;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class KontakDAO {

    // Method mengambil semua data kontak
    public List<Kontak> getAllContacts() throws SQLException {
        List<Kontak> contacts = new ArrayList<>();
        String sql = "SELECT * FROM contacts";
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                Kontak contact = new Kontak(
                        rs.getInt("id"),
                        rs.getString("nama"),
                        rs.getString("nomor_telepon"),
                        rs.getString("kategori")
                );
                contacts.add(contact);
            }
        }
        return contacts;
    }

    // Method menambahkan kontak
    public void addContact(Kontak contact) throws SQLException {
        String sql = "INSERT INTO contacts (nama, nomor_telepon, kategori) VALUES (?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, contact.getNama());
            pstmt.setString(2, contact.getNomorTelepon());
            pstmt.setString(3, contact.getKategori());
            pstmt.executeUpdate();
        }
    }

    // Method memperbarui kontak
    public void updateContact(Kontak contact) throws SQLException {
        String sql = "UPDATE contacts SET nama = ?, nomor_telepon = ?, kategori = ? WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, contact.getNama());
            pstmt.setString(2, contact.getNomorTelepon());
            pstmt.setString(3, contact.getKategori());
            pstmt.setInt(4, contact.getId());
            pstmt.executeUpdate();
        }
    }

    // Method menghapus kontak
    public void deleteContact(int id) throws SQLException {
        String sql = "DELETE FROM contacts WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, id);
            pstmt.executeUpdate();
        }
    }

    // Method pencarian kontak
    public List<Kontak> searchContacts(String keyword) throws SQLException {
        List<Kontak> contacts = new ArrayList<>();
        String sql = "SELECT * FROM contacts WHERE nama LIKE ? OR nomor_telepon LIKE ? OR kategori LIKE ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            String likeKeyword = "%" + keyword + "%";
            pstmt.setString(1, likeKeyword);
            pstmt.setString(2, likeKeyword);
            pstmt.setString(3, likeKeyword);

            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                Kontak contact = new Kontak(
                        rs.getInt("id"),
                        rs.getString("nama"),
                        rs.getString("nomor_telepon"),
                        rs.getString("kategori")
                );
                contacts.add(contact);
            }
        }
        return contacts;
    }

    // Method cek duplikat nomor telepon
    public boolean isDuplicatePhoneNumber(String nomorTelepon, Integer excludeId) throws SQLException {
        String sql = "SELECT COUNT(*) FROM contacts WHERE nomor_telepon = ?";
        if (excludeId != null) {
            sql += " AND id != ?";
        }

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, nomorTelepon);
            if (excludeId != null) {
                pstmt.setInt(2, excludeId);
            }

            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
        }
        return false;
    }
}
