package com.sistempakar.db;

import java.sql.ResultSet;

/**
 * Class untuk menjalankan migrasi dan injeksi database secara otomatis.
 * Mencegah error jika tabel belum memiliki kolom baru sesuai revisi dosen.
 */
public class DatabaseMigration {
    
    public static void run() {
        try {
            System.out.println("⚙️ Memeriksa struktur database (Database Migration)...");
            
            boolean hasTahunAjaran = false;
            boolean hasSemester = false;
            
            // Cek keberadaan kolom
            ResultSet rs1 = DBConnection.executeQuery("SHOW COLUMNS FROM siswa LIKE 'tahun_ajaran'");
            if (rs1.next()) hasTahunAjaran = true;
            if (rs1.getStatement() != null) rs1.getStatement().close();
            
            ResultSet rs2 = DBConnection.executeQuery("SHOW COLUMNS FROM siswa LIKE 'semester'");
            if (rs2.next()) hasSemester = true;
            if (rs2.getStatement() != null) rs2.getStatement().close();
            
            // Tambah kolom jika belum ada
            if (!hasTahunAjaran) {
                System.out.println("➕ Menambahkan kolom 'tahun_ajaran' ke tabel siswa...");
                DBConnection.executeUpdate("ALTER TABLE siswa ADD COLUMN tahun_ajaran VARCHAR(20)");
            }
            
            if (!hasSemester) {
                System.out.println("➕ Menambahkan kolom 'semester' ke tabel siswa...");
                DBConnection.executeUpdate("ALTER TABLE siswa ADD COLUMN semester VARCHAR(10)");
            }
            
            // INJEKSI: Update data lama jika kolom baru saja ditambahkan / ada nilai NULL
            if (!hasTahunAjaran || !hasSemester) {
                System.out.println("💉 Menginjeksi data siswa lama dengan Tahun Ajaran 2023/2024 Semester Ganjil...");
                DBConnection.executeUpdate("UPDATE siswa SET tahun_ajaran = '2023/2024', semester = 'Ganjil' WHERE tahun_ajaran IS NULL OR semester IS NULL");
            }
            
            // --- Fitur Tahun Ajaran Dinamis ---
            System.out.println("⚙️ Memeriksa tabel master_tahun_ajaran...");
            DBConnection.executeUpdate("CREATE TABLE IF NOT EXISTS master_tahun_ajaran (id INT AUTO_INCREMENT PRIMARY KEY, nama_tahun VARCHAR(20) UNIQUE)");
            
            // Injeksi data awal jika kosong
            ResultSet rsTahun = DBConnection.executeQuery("SELECT COUNT(*) AS total FROM master_tahun_ajaran");
            if (rsTahun.next() && rsTahun.getInt("total") == 0) {
                System.out.println("➕ Menambahkan data awal Tahun Ajaran...");
                DBConnection.executeUpdate("INSERT INTO master_tahun_ajaran (nama_tahun) VALUES ('2023/2024'), ('2024/2025'), ('2025/2026')");
            }
            if (rsTahun.getStatement() != null) rsTahun.getStatement().close();
            
            System.out.println("✅ Database Migration selesai dan aman!");
            
        } catch (Exception e) {
            System.err.println("❌ Gagal menjalankan Database Migration: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
