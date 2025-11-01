package database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnection {
    // URL koneksi ke database SQLite
    // "contacts.db" akan dibuat di folder root proyek Anda
    private static final String URL = "jdbc:sqlite:contacts.db";

    /**
     * Membuat dan mengembalikan koneksi ke database.
     * @return Koneksi ke database
     * @throws SQLException Jika terjadi kesalahan koneksi
     */
    public static Connection getConnection() throws SQLException {
        // Mendaftarkan driver JDBC SQLite
        // (Meskipun modern JDBC mungkin tidak memerlukan ini, ini adalah praktik yang baik)
        try {
            Class.forName("org.sqlite.JDBC");
        } catch (ClassNotFoundException e) {
            System.err.println("Driver SQLite JDBC tidak ditemukan. Pastikan library (JAR) sudah ditambahkan.");
            throw new SQLException("Driver SQLite tidak ditemukan", e);
        }
        return DriverManager.getConnection(URL);
    }
}
