package com.sistempakar.db;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.sql.SQLException;

public class Migration {

    public static void runMigrations(Connection conn) {
        try (Statement stmt = conn.createStatement()) {
            
            // 1. Tambah tahun_ajaran dan semester di tabel siswa
            if (!columnExists(conn, "siswa", "tahun_ajaran")) {
                stmt.execute("ALTER TABLE siswa ADD COLUMN tahun_ajaran VARCHAR(20) DEFAULT '2023/2024'");
                System.out.println("Migrasi: Kolom tahun_ajaran ditambahkan ke tabel siswa.");
            }
            if (!columnExists(conn, "siswa", "semester")) {
                stmt.execute("ALTER TABLE siswa ADD COLUMN semester VARCHAR(20) DEFAULT 'Ganjil'");
                System.out.println("Migrasi: Kolom semester ditambahkan ke tabel siswa.");
            }
            
            // 2. Tambah kolom baru di tabel konsultasi
            if (!columnExists(conn, "konsultasi", "rekomendasi_alt1_id")) {
                stmt.execute("ALTER TABLE konsultasi ADD COLUMN rekomendasi_alt1_id INT");
                System.out.println("Migrasi: Kolom rekomendasi_alt1_id ditambahkan ke tabel konsultasi.");
            }
            if (!columnExists(conn, "konsultasi", "rekomendasi_alt2_id")) {
                stmt.execute("ALTER TABLE konsultasi ADD COLUMN rekomendasi_alt2_id INT");
                System.out.println("Migrasi: Kolom rekomendasi_alt2_id ditambahkan ke tabel konsultasi.");
            }
            if (!columnExists(conn, "konsultasi", "catatan_konselor")) {
                stmt.execute("ALTER TABLE konsultasi ADD COLUMN catatan_konselor TEXT");
                System.out.println("Migrasi: Kolom catatan_konselor ditambahkan ke tabel konsultasi.");
            }
            if (!columnExists(conn, "konsultasi", "tips_belajar")) {
                stmt.execute("ALTER TABLE konsultasi ADD COLUMN tips_belajar TEXT");
                System.out.println("Migrasi: Kolom tips_belajar ditambahkan ke tabel konsultasi.");
            }
            if (!columnExists(conn, "konsultasi", "rekomendasi_kegiatan")) {
                stmt.execute("ALTER TABLE konsultasi ADD COLUMN rekomendasi_kegiatan TEXT");
                System.out.println("Migrasi: Kolom rekomendasi_kegiatan ditambahkan ke tabel konsultasi.");
            }
            
        } catch (Exception e) {
            System.err.println("Error saat menjalankan migrasi: " + e.getMessage());
        }
    }

    private static boolean columnExists(Connection conn, String tableName, String columnName) throws SQLException {
        try (ResultSet rs = conn.getMetaData().getColumns(null, null, tableName, columnName)) {
            return rs.next();
        }
    }
}
