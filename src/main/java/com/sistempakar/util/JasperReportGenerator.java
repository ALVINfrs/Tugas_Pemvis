package com.sistempakar.util;

import com.sistempakar.db.DBConnection;
import net.sf.jasperreports.engine.*;
import net.sf.jasperreports.view.JasperViewer;

import javax.swing.*;
import java.io.InputStream;
import java.sql.Connection;
import java.util.HashMap;
import java.util.Map;

public class JasperReportGenerator {

    /**
     * Men-generate dan menampilkan laporan rekapitulasi konsultasi
     */
    public static void showLaporanRekap(String kelas, String status, String tahunAjaran, String semester, java.util.Date startDate, java.util.Date endDate) {
        try {
            InputStream reportStream = JasperReportGenerator.class.getResourceAsStream("/reports/LaporanRekapKonsultasi.jrxml");
            if (reportStream == null) {
                JOptionPane.showMessageDialog(null, "File JRXML LaporanRekapKonsultasi tidak ditemukan!", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            JasperReport jasperReport = JasperCompileManager.compileReport(reportStream);

            Map<String, Object> parameters = new HashMap<>();
            parameters.put("paramKelas", kelas);
            parameters.put("paramStatus", status);
            parameters.put("paramTahun", tahunAjaran);
            parameters.put("paramSemester", semester);
            parameters.put("paramStartDate", new java.sql.Timestamp(startDate.getTime()));
            parameters.put("paramEndDate", new java.sql.Timestamp(endDate.getTime() + 24L*60*60*1000 - 1));

            Connection conn = DBConnection.getConnection();
            JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, parameters, conn);

            JasperViewer viewer = new JasperViewer(jasperPrint, false);
            viewer.setTitle("Laporan Rekapitulasi Konsultasi - JasperReports");
            viewer.setVisible(true);

        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Gagal mencetak JasperReport: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Men-generate laporan detail individu (Rapor)
     */
    public static void showLaporanDetailSiswa(int konsultasiId) {
        try {
            System.out.println("[JasperReport] Memulai generate Detail Rapor untuk konsultasi ID: " + konsultasiId);

            InputStream reportStream = JasperReportGenerator.class.getResourceAsStream("/reports/DetailRaporSiswa.jrxml");
            if (reportStream == null) {
                String errMsg = "File JRXML DetailRaporSiswa tidak ditemukan di classpath /reports/DetailRaporSiswa.jrxml!";
                System.err.println("[JasperReport] ERROR: " + errMsg);
                SwingUtilities.invokeLater(() ->
                    JOptionPane.showMessageDialog(null, errMsg, "Error - File JRXML Tidak Ditemukan", JOptionPane.ERROR_MESSAGE)
                );
                return;
            }
            System.out.println("[JasperReport] File JRXML ditemukan, mulai kompilasi...");

            JasperReport jasperReport = JasperCompileManager.compileReport(reportStream);
            System.out.println("[JasperReport] Kompilasi JRXML berhasil.");

            Map<String, Object> parameters = new HashMap<>();
            parameters.put("paramKonsultasiId", konsultasiId);

            Connection conn = DBConnection.getConnection();
            if (conn == null || conn.isClosed()) {
                String errMsg = "Koneksi database tidak tersedia atau sudah ditutup!";
                System.err.println("[JasperReport] ERROR: " + errMsg);
                SwingUtilities.invokeLater(() ->
                    JOptionPane.showMessageDialog(null, errMsg, "Error - Database", JOptionPane.ERROR_MESSAGE)
                );
                return;
            }
            System.out.println("[JasperReport] Koneksi DB OK, mulai fill report...");

            JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, parameters, conn);
            System.out.println("[JasperReport] Fill report berhasil. Jumlah halaman: " + jasperPrint.getPages().size());

            // JasperViewer HARUS ditampilkan di Event Dispatch Thread (EDT)
            SwingUtilities.invokeLater(() -> {
                JasperViewer viewer = new JasperViewer(jasperPrint, false);
                viewer.setTitle("Detail Rapor Siswa - JasperReports");
                viewer.setExtendedState(javax.swing.JFrame.MAXIMIZED_BOTH);
                viewer.setVisible(true);
                viewer.toFront();
                viewer.requestFocus();
                System.out.println("[JasperReport] JasperViewer ditampilkan.");
            });

        } catch (Exception e) {
            e.printStackTrace();
            String fullError = "Gagal mencetak JasperReport Detail Siswa.\n\n"
                    + "Error: " + e.getClass().getSimpleName() + "\n"
                    + "Pesan: " + e.getMessage() + "\n\n"
                    + "Silakan periksa console untuk stack trace lengkap.";
            SwingUtilities.invokeLater(() ->
                JOptionPane.showMessageDialog(null, fullError, "Error - JasperReport", JOptionPane.ERROR_MESSAGE)
            );
        }
    }

    /**
     * Men-generate laporan komparasi peminatan antar tahun ajaran
     */
    public static void showLaporanKomparasi(String tahun1, String tahun2) {
        try {
            InputStream reportStream = JasperReportGenerator.class.getResourceAsStream("/reports/LaporanKomparasiTahun.jrxml");
            if (reportStream == null) {
                JOptionPane.showMessageDialog(null, "File JRXML LaporanKomparasiTahun tidak ditemukan!", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            JasperReport jasperReport = JasperCompileManager.compileReport(reportStream);

            Map<String, Object> parameters = new HashMap<>();
            parameters.put("paramTahun1", tahun1);
            parameters.put("paramTahun2", tahun2);

            Connection conn = DBConnection.getConnection();
            JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, parameters, conn);

            JasperViewer viewer = new JasperViewer(jasperPrint, false);
            viewer.setTitle("Laporan Komparasi Peminatan Antar Tahun - JasperReports");
            viewer.setVisible(true);

        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Gagal mencetak JasperReport Komparasi: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}
