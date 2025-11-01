/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package view;

import controller.KontakController;
import java.io.*;
import javax.swing.JFileChooser;
import model.Kontak;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;
import java.sql.SQLException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;


public class PengelolaanKontakFrame extends javax.swing.JFrame {

    private DefaultTableModel model;
    private KontakController controller;
    // <--- SAMPAI SINI

    /**
     * Creates new form PengelolaanKontakFrame
     */
public PengelolaanKontakFrame() {
    initComponents();
    
    controller = new KontakController();
    // Gunakan "ID" sebagai kolom pertama untuk menyimpan ID, tapi kita akan sembunyikan nanti
    model = new DefaultTableModel(new String[]{"ID", "Nama", "Nomor Telepon", "Kategori"}, 0);
    tblKontak.setModel(model);

    // Sembunyikan kolom ID (kolom ke-0)
    tblKontak.getColumnModel().getColumn(0).setMinWidth(0);
    tblKontak.getColumnModel().getColumn(0).setMaxWidth(0);
    tblKontak.getColumnModel().getColumn(0).setWidth(0);

    // Panggil metode untuk memuat kontak saat aplikasi pertama kali dibuka
    loadContacts();
    
    this.setLocationRelativeTo(null);
}

// Salin SEMUA metode di bawah ini ke bagian BAWAH file class Anda
// (Setelah kurung kurawal '}' penutup dari konstruktor, 
//  tapi SEBELUM kurung kurawal '}' terakhir dari class)

/**
 * Memuat atau me-refresh data kontak dari database ke JTable.
 */
private void loadContacts() {
    try {
        model.setRowCount(0); // Kosongkan tabel
        List<Kontak> contacts = controller.getAllContacts();
        
        for (Kontak contact : contacts) {
            model.addRow(new Object[]{
                contact.getId(), // ID tetap diambil untuk keperluan edit/hapus
                contact.getNama(),
                contact.getNomorTelepon(),
                contact.getKategori()
            });
        }
    } catch (SQLException e) {
        showError("Gagal memuat kontak: " + e.getMessage());
    }
}

/**
 * Menampilkan dialog pesan error.
 */
private void showError(String message) {
    JOptionPane.showMessageDialog(this, message, "Error", JOptionPane.ERROR_MESSAGE);
}

/**
 * Membersihkan field input setelah operasi berhasil.
 */
private void clearInputFields() {
    txtNama.setText("");
    txtNomorTelepon.setText("");
    cmbKategori.setSelectedIndex(0);
    tblKontak.clearSelection(); // Hapus seleksi di tabel
}

/**
 * Memvalidasi nomor telepon.
 * @param phoneNumber Nomor telepon yang akan divalidasi.
 * @return true jika valid, false jika tidak.
 */
private boolean validatePhoneNumber(String phoneNumber) {
    if (phoneNumber == null || phoneNumber.trim().isEmpty()) {
        JOptionPane.showMessageDialog(this, "Nomor telepon tidak boleh kosong.", "Validasi Gagal", JOptionPane.WARNING_MESSAGE);
        return false;
    }
    // Hanya boleh berisi angka
    if (!phoneNumber.matches("\\d+")) { 
        JOptionPane.showMessageDialog(this, "Nomor telepon hanya boleh berisi angka.", "Validasi Gagal", JOptionPane.WARNING_MESSAGE);
        return false;
    }
    // Panjang antara 8 hingga 15 karakter
    if (phoneNumber.length() < 8 || phoneNumber.length() > 15) { 
        JOptionPane.showMessageDialog(this, "Nomor telepon harus memiliki panjang antara 8 hingga 15 karakter.", "Validasi Gagal", JOptionPane.WARNING_MESSAGE);
        return false;
    }
    return true;
}

/**
 * Logika untuk menambah kontak baru.
 */
private void addContact() {
    String nama = txtNama.getText().trim();
    String nomorTelepon = txtNomorTelepon.getText().trim();
    String kategori = (String) cmbKategori.getSelectedItem();

    if (nama.isEmpty()) {
        JOptionPane.showMessageDialog(this, "Nama tidak boleh kosong.", "Validasi Gagal", JOptionPane.WARNING_MESSAGE);
        return;
    }
    
    if (!validatePhoneNumber(nomorTelepon)) {
        return; // Validasi nomor telepon gagal
    }

    try {
        // Cek duplikat (excludeId = null karena ini data baru)
        if (controller.isDuplicatePhoneNumber(nomorTelepon, null)) {
            JOptionPane.showMessageDialog(this, "Kontak dengan nomor telepon ini sudah ada.", "Kesalahan", JOptionPane.WARNING_MESSAGE);
            return;
        }

        controller.addContact(nama, nomorTelepon, kategori);
        loadContacts(); // Refresh tabel
        JOptionPane.showMessageDialog(this, "Kontak berhasil ditambahkan!");
        clearInputFields();

    } catch (SQLException ex) {
        showError("Gagal menambahkan kontak: " + ex.getMessage());
    }
}

/**
 * Logika untuk mengedit kontak yang dipilih.
 */
private void editContact() {
    int selectedRow = tblKontak.getSelectedRow();

    if (selectedRow == -1) {
        JOptionPane.showMessageDialog(this, "Pilih kontak yang ingin diperbarui.", "Kesalahan", JOptionPane.WARNING_MESSAGE);
        return;
    }

    // Ambil ID dari kolom pertama (yang disembunyikan)
    int id = (int) model.getValueAt(selectedRow, 0);
    String nama = txtNama.getText().trim();
    String nomorTelepon = txtNomorTelepon.getText().trim();
    String kategori = (String) cmbKategori.getSelectedItem();
    
    if (nama.isEmpty()) {
        JOptionPane.showMessageDialog(this, "Nama tidak boleh kosong.", "Validasi Gagal", JOptionPane.WARNING_MESSAGE);
        return;
    }

    if (!validatePhoneNumber(nomorTelepon)) {
        return; // Validasi gagal
    }

    try {
        // Cek duplikat, tapi kecualikan ID kontak ini sendiri
        if (controller.isDuplicatePhoneNumber(nomorTelepon, id)) {
            JOptionPane.showMessageDialog(this, "Kontak dengan nomor telepon ini sudah ada.", "Kesalahan", JOptionPane.WARNING_MESSAGE);
            return;
        }

        controller.updateContact(id, nama, nomorTelepon, kategori);
        loadContacts(); // Refresh tabel
        JOptionPane.showMessageDialog(this, "Kontak berhasil diperbarui!");
        clearInputFields();

    } catch (SQLException ex) {
        showError("Gagal memperbarui kontak: " + ex.getMessage());
    }
}

/**
 * Mengisi field input berdasarkan data dari baris tabel yang dipilih.
 */
private void populateInputFields(int selectedRow) {
    // Ambil data dari JTable (kolom 1, 2, 3. Kolom 0 adalah ID)
    String nama = model.getValueAt(selectedRow, 1).toString();
    String nomorTelepon = model.getValueAt(selectedRow, 2).toString();
    String kategori = model.getValueAt(selectedRow, 3).toString();

    // Set data ke komponen input
    txtNama.setText(nama);
    txtNomorTelepon.setText(nomorTelepon);
    cmbKategori.setSelectedItem(kategori);
}

/**
 * Logika untuk menghapus kontak yang dipilih.
 */
private void deleteContact() {
    int selectedRow = tblKontak.getSelectedRow();

    if (selectedRow == -1) {
        JOptionPane.showMessageDialog(this, "Pilih kontak yang ingin dihapus.", "Kesalahan", JOptionPane.WARNING_MESSAGE);
        return;
    }
    
    // Konfirmasi penghapusan
    int confirm = JOptionPane.showConfirmDialog(this, 
            "Apakah Anda yakin ingin menghapus kontak ini?", 
            "Konfirmasi Hapus", 
            JOptionPane.YES_NO_OPTION);
    
    if (confirm != JOptionPane.YES_OPTION) {
        return;
    }

    // Ambil ID dari kolom pertama (yang disembunyikan)
    int id = (int) model.getValueAt(selectedRow, 0);
    
    try {
        controller.deleteContact(id);
        loadContacts(); // Refresh tabel
        JOptionPane.showMessageDialog(this, "Kontak berhasil dihapus!");
        clearInputFields();
        
    } catch (SQLException e) {
        showError("Gagal menghapus kontak: " + e.getMessage());
    }
}

/**
 * Logika untuk mencari kontak dan menampilkan hasilnya di tabel.
 */
private void searchContact() {
    String keyword = txtPencarian.getText().trim();

    if (!keyword.isEmpty()) {
        try {
            List<Kontak> contacts = controller.searchContacts(keyword);
            model.setRowCount(0); // Bersihkan tabel

            for (Kontak contact : contacts) {
                model.addRow(new Object[]{
                    contact.getId(),
                    contact.getNama(),
                    contact.getNomorTelepon(),
                    contact.getKategori()
                });
            }

            if (contacts.isEmpty()) {
                // Tidak perlu JOptionPane agar tidak mengganggu pengetikan
                // Cukup tampilkan tabel kosong
            }
            
        } catch (SQLException ex) {
            showError("Gagal mencari kontak: " + ex.getMessage());
        }
    } else {
        // Jika keyword kosong, muat semua kontak
        loadContacts();
    }
}

/**
 * Logika untuk export data di JTable ke file CSV.
 */
private void exportToCSV() {
    JFileChooser fileChooser = new JFileChooser();
    fileChooser.setDialogTitle("Simpan File CSV");
    // Set default nama file
    fileChooser.setSelectedFile(new File("kontak.csv"));

    int userSelection = fileChooser.showSaveDialog(this);

    if (userSelection == JFileChooser.APPROVE_OPTION) {
        File fileToSave = fileChooser.getSelectedFile();
        
        // Tambahkan ekstensi .csv jika pengguna tidak menambahkannya
        if (!fileToSave.getAbsolutePath().endsWith(".csv")) {
            fileToSave = new File(fileToSave.getAbsolutePath() + ".csv");
        }

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(fileToSave))) {
            // Tulis Header CSV
            writer.write("ID,Nama,Nomor Telepon,Kategori\n"); 

            // Tulis data baris per baris
            for (int i = 0; i < model.getRowCount(); i++) {
                writer.write(
                        model.getValueAt(i, 0) + "," +
                        model.getValueAt(i, 1) + "," +
                        model.getValueAt(i, 2) + "," +
                        model.getValueAt(i, 3) + "\n"
                );
            }
            JOptionPane.showMessageDialog(this, "Data berhasil diekspor ke " + fileToSave.getAbsolutePath());
            
        } catch (IOException ex) {
            showError("Gagal menulis file: " + ex.getMessage());
        }
    }
}

/**
 * Menampilkan panduan format CSV sebelum import.
 */
private void showCSVGuide() {
    String guideMessage = "Format CSV untuk impor data:\n"
            + "- Header wajib: ID,Nama,Nomor Telepon,Kategori\n"
            + "- Urutan kolom harus sama persis.\n"
            + "- ID dapat dikosongkan (misal: ,,Nama,08123,Teman) jika kontak baru.\n"
            + "- Nama dan Nomor Telepon wajib diisi.\n\n"
            + "Contoh isi file CSV:\n"
            + "ID,Nama,Nomor Telepon,Kategori\n"
            + ",Andi,08123456789,Teman\n"
            + ",Budi Doremi,08567890123,Keluarga\n\n"
            + "Kontak dengan Nomor Telepon yang sudah ada akan DILEWATI.\n"
            + "Pastikan file CSV sesuai format sebelum melakukan impor.";
    JOptionPane.showMessageDialog(this, guideMessage, "Panduan Format CSV", JOptionPane.INFORMATION_MESSAGE);
}

/**
 * Memvalidasi header file CSV.
 */
private boolean validateCSVHeader(String header) {
    return header != null && header.trim().equalsIgnoreCase("ID,Nama,Nomor Telepon,Kategori");
}

/**
 * Logika untuk import data dari file CSV ke database.
 */
private void importFromCSV() {
    showCSVGuide(); // Tampilkan panduan
    
    int confirm = JOptionPane.showConfirmDialog(
            this,
            "Apakah Anda yakin file CSV yang dipilih sudah sesuai dengan format?",
            "Konfirmasi Impor CSV",
            JOptionPane.YES_NO_OPTION);

    if (confirm != JOptionPane.YES_OPTION) {
        return;
    }

    JFileChooser fileChooser = new JFileChooser();
    fileChooser.setDialogTitle("Pilih File CSV");
    
    int userSelection = fileChooser.showOpenDialog(this);

    if (userSelection == JFileChooser.APPROVE_OPTION) {
        File fileToOpen = fileChooser.getSelectedFile();
        int errorCount = 0;
        int duplicateCount = 0;
        int successCount = 0;
        StringBuilder errorLog = new StringBuilder("Log Impor:\n");

        try (BufferedReader reader = new BufferedReader(new FileReader(fileToOpen))) {
            String line = reader.readLine(); // Baca header
            
            if (!validateCSVHeader(line)) {
                JOptionPane.showMessageDialog(this, "Format header CSV tidak valid. Pastikan header adalah: ID,Nama,Nomor Telepon,Kategori",
                        "Kesalahan CSV", JOptionPane.ERROR_MESSAGE);
                return;
            }

            int rowCount = 1; // Mulai dari 1 (karena header baris 0)
            while ((line = reader.readLine()) != null) {
                rowCount++;
                String[] data = line.split(",", -1); // -1 untuk include empty string

                if (data.length != 4) {
                    errorCount++;
                    errorLog.append("Baris ").append(rowCount).append(": Format kolom tidak sesuai (harap gunakan 4 kolom).\n");
                    continue;
                }
                
                // data[0] adalah ID, kita abaikan saja saat import baru
                String nama = data[1].trim();
                String nomorTelepon = data[2].trim();
                String kategori = data[3].trim();

                if (nama.isEmpty() || nomorTelepon.isEmpty()) {
                    errorCount++;
                    errorLog.append("Baris ").append(rowCount).append(": Nama atau Nomor Telepon kosong.\n");
                    continue;
                }

                if (!validatePhoneNumber(nomorTelepon)) {
                    errorCount++;
                    errorLog.append("Baris ").append(rowCount).append(": Nomor Telepon tidak valid.\n");
                    continue;
                }

                try {
                    if (controller.isDuplicatePhoneNumber(nomorTelepon, null)) {
                        duplicateCount++;
                        errorLog.append("Baris ").append(rowCount).append(": Kontak sudah ada (dilewati).\n");
                        continue;
                    }
                    // Jika lolos validasi, tambahkan ke DB
                    controller.addContact(nama, nomorTelepon, kategori);
                    successCount++;
                    
                } catch (SQLException ex) {
                    errorCount++;
                    errorLog.append("Baris ").append(rowCount).append(": Gagal menyimpan ke database: ").append(ex.getMessage()).append("\n");
                }
            }

            loadContacts(); // Refresh tabel setelah selesai

            // Tampilkan laporan akhir
            String summary = String.format("Impor Selesai.\n\nBerhasil: %d\nDuplikat (dilewati): %d\nKesalahan: %d\n", 
                                successCount, duplicateCount, errorCount);
            
            if (errorCount > 0 || duplicateCount > 0) {
                JOptionPane.showMessageDialog(this, summary + "\nDetail:\n" + errorLog.toString(), "Laporan Impor", JOptionPane.WARNING_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(this, summary, "Laporan Impor", JOptionPane.INFORMATION_MESSAGE);
            }

        } catch (IOException ex) {
            showError("Gagal membaca file: " + ex.getMessage());
        }
    }
}


    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jPanel2 = new javax.swing.JPanel();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        txtNama = new java.awt.TextField();
        txtNomorTelepon = new java.awt.TextField();
        cmbKategori = new javax.swing.JComboBox<>();
        btnTambah = new javax.swing.JButton();
        btnEdit = new javax.swing.JButton();
        btnHapus = new javax.swing.JButton();
        jPanel3 = new javax.swing.JPanel();
        jLabel5 = new javax.swing.JLabel();
        txtPencarian = new java.awt.TextField();
        jScrollPane1 = new javax.swing.JScrollPane();
        tblKontak = new javax.swing.JTable();
        btnExport = new javax.swing.JButton();
        btnImport = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("Aplikasi Pengelola Kontak");

        jPanel1.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));

        jLabel1.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jLabel1.setText("APLIKASI PENGELOLAAN KONTAK");

        jPanel2.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));

        jLabel2.setText("Kategori");

        jLabel3.setText("Nama Kontak");

        jLabel4.setText("Nomor Telpon");

        cmbKategori.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Keluarga", "Teman", "Kantor" }));

        btnTambah.setText("Tambah");
        btnTambah.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnTambahActionPerformed(evt);
            }
        });

        btnEdit.setText("Edit");
        btnEdit.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnEditActionPerformed(evt);
            }
        });

        btnHapus.setText("Hapus");
        btnHapus.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnHapusActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGap(31, 31, 31)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel3)
                    .addComponent(jLabel4)
                    .addComponent(jLabel2))
                .addGap(67, 67, 67)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(txtNomorTelepon, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(txtNama, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(cmbKategori, 0, 121, Short.MAX_VALUE))
                .addGap(46, 46, 46)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(btnTambah)
                    .addComponent(btnEdit)
                    .addComponent(btnHapus))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jPanel2Layout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {btnEdit, btnHapus, btnTambah});

        jPanel2Layout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {cmbKategori, txtNama, txtNomorTelepon});

        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                .addGap(20, 20, 20)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                        .addComponent(txtNama, javax.swing.GroupLayout.DEFAULT_SIZE, 24, Short.MAX_VALUE)
                        .addComponent(jLabel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addComponent(btnTambah))
                .addGap(18, 18, 18)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(txtNomorTelepon, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jLabel4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(btnEdit, javax.swing.GroupLayout.DEFAULT_SIZE, 24, Short.MAX_VALUE))
                .addGap(18, 18, 18)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jLabel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(cmbKategori, javax.swing.GroupLayout.DEFAULT_SIZE, 24, Short.MAX_VALUE)
                        .addComponent(btnHapus)))
                .addContainerGap(33, Short.MAX_VALUE))
        );

        jPanel3.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));

        jLabel5.setText("Pencarian");

        txtPencarian.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                txtPencarianKeyTyped(evt);
            }
        });

        tblKontak.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Title 1", "Title 2", "Title 3", "Title 4"
            }
        ));
        tblKontak.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tblKontakMouseClicked(evt);
            }
        });
        jScrollPane1.setViewportView(tblKontak);

        btnExport.setText("Export");
        btnExport.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnExportActionPerformed(evt);
            }
        });

        btnImport.setText("Import");
        btnImport.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnImportActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addGap(30, 30, 30)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 431, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addComponent(jLabel5)
                        .addGap(95, 95, 95)
                        .addComponent(txtPencarian, javax.swing.GroupLayout.PREFERRED_SIZE, 118, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnExport)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(btnImport)))
                .addContainerGap(26, Short.MAX_VALUE))
        );

        jPanel3Layout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {btnExport, btnImport});

        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addGap(21, 21, 21)
                        .addComponent(jLabel5, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(txtPencarian, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                .addComponent(btnExport)
                                .addComponent(btnImport)))))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(83, 83, 83))
        );

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jLabel1)
                .addGap(156, 156, 156))
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(27, 27, 27)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap(31, Short.MAX_VALUE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(21, 21, 21)
                .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, 184, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(62, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btnTambahActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnTambahActionPerformed
        addContact();
    }//GEN-LAST:event_btnTambahActionPerformed

    private void btnEditActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnEditActionPerformed
        editContact();
    }//GEN-LAST:event_btnEditActionPerformed

    private void btnHapusActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnHapusActionPerformed
        deleteContact();
    }//GEN-LAST:event_btnHapusActionPerformed

    private void tblKontakMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tblKontakMouseClicked
        int selectedRow = tblKontak.getSelectedRow();
        if (selectedRow != -1) {
            populateInputFields(selectedRow);
        }
    }//GEN-LAST:event_tblKontakMouseClicked

    private void txtPencarianKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtPencarianKeyTyped
        searchContact();
    }//GEN-LAST:event_txtPencarianKeyTyped

    private void btnExportActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnExportActionPerformed
        exportToCSV();
    }//GEN-LAST:event_btnExportActionPerformed

    private void btnImportActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnImportActionPerformed
        importFromCSV();
    }//GEN-LAST:event_btnImportActionPerformed

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(PengelolaanKontakFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(PengelolaanKontakFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(PengelolaanKontakFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(PengelolaanKontakFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new PengelolaanKontakFrame().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnEdit;
    private javax.swing.JButton btnExport;
    private javax.swing.JButton btnHapus;
    private javax.swing.JButton btnImport;
    private javax.swing.JButton btnTambah;
    private javax.swing.JComboBox<String> cmbKategori;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTable tblKontak;
    private java.awt.TextField txtNama;
    private java.awt.TextField txtNomorTelepon;
    private java.awt.TextField txtPencarian;
    // End of variables declaration//GEN-END:variables
}
