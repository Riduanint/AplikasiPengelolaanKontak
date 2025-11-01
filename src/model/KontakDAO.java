package model;

import database.DatabaseConnection;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Kelas Data Access Object (DAO) untuk mengelola operasi CRUD ke tabel 'contacts'.
 * Menggunakan 'nomor_telepon' (dengan underscore) untuk semua query.
 */
public class KontakDAO {

    // Method untuk mengambil semua data kontak di tabel
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
                        rs.getString("kategori"));
                contacts.add(contact);
            }
        }
        return contacts;
    }

    // Method untuk menambahkan kontak ke tabel
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

    // Method untuk mengupdate kontak ke tabel
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

    // Method untuk menghapus kontak di tabel
    public void deleteContact(int contactId) throws SQLException {
        String sql = "DELETE FROM contacts WHERE id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, contactId);
            pstmt.executeUpdate();
        }
    }

    // Method untuk mencari kontak di tabel
    public List<Kontak> searchContacts(String keyword) throws SQLException {
        List<Kontak> contacts = new ArrayList<>();
        // Mencari berdasarkan nama ATAU nomor telepon
        String sql = "SELECT * FROM contacts WHERE nama LIKE ? OR nomor_telepon LIKE ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            String searchKeyword = "%" + keyword + "%";
            pstmt.setString(1, searchKeyword);
            pstmt.setString(2, searchKeyword);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    Kontak contact = new Kontak(
                            rs.getInt("id"),
                            rs.getString("nama"),
                            rs.getString("nomor_telepon"),
                            rs.getString("kategori"));
                    contacts.add(contact);
                }
            }
        }
        return contacts;
    }

    // Method untuk memeriksa nomor telepon duplikat
    // excludeId digunakan saat UPDATE, agar tidak mendeteksi nomornya sendiri sebagai duplikat
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

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    // Jika COUNT > 0, berarti nomor telepon sudah ada
                    return rs.getInt(1) > 0; 
                }
            }
        }
        return false;
    }
}
