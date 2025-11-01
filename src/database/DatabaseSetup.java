package database;

import java.sql.Connection;
import java.sql.Statement;
import java.sql.SQLException;

public class DatabaseSetup {

    public static void main(String[] args) {
        // Perintah SQL untuk membuat tabel 'contacts' jika belum ada
        // Menggunakan 'nomor_telepon' (dengan underscore) untuk konsistensi
        String sql = "CREATE TABLE IF NOT EXISTS contacts ("
                + "id INTEGER PRIMARY KEY AUTOINCREMENT,"
                + "nama TEXT NOT NULL,"
                + "nomor_telepon TEXT NOT NULL UNIQUE," // Menambahkan UNIQUE untuk mencegah duplikat di level DB
                + "kategori TEXT"
                + ");";

        // Menggunakan try-with-resources untuk memastikan koneksi dan statement ditutup otomatis
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement()) {
            
            // Eksekusi perintah SQL
            stmt.execute(sql);
            System.out.println("Tabel 'contacts' berhasil dibuat atau sudah ada.");
            
        } catch (SQLException e) {
            System.out.println("Error saat membuat tabel: " + e.getMessage());
        }
    }
}
