package com.sistempakar.util;

import com.itextpdf.text.*;
import com.itextpdf.text.pdf.*;
import com.itextpdf.text.pdf.draw.LineSeparator;
import com.sistempakar.db.DBConnection;
import java.io.FileOutputStream;
import java.sql.*;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Utility for generating highly detailed, narrative-heavy PDF reports.
 */
public class ReportGenerator {

    private static final Font FONT_TITLE = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 22, new BaseColor(20, 35, 75));
    private static final Font FONT_SUBTITLE = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 14, BaseColor.DARK_GRAY);
    private static final Font FONT_HEADER = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 10, BaseColor.WHITE);
    private static final Font FONT_NORMAL = FontFactory.getFont(FontFactory.HELVETICA, 11, BaseColor.BLACK);
    private static final Font FONT_NARRATIVE = FontFactory.getFont(FontFactory.HELVETICA, 10, new BaseColor(40, 40, 40));
    private static final Font FONT_BOLD = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 10, BaseColor.BLACK);
    private static final Font FONT_SMALL = FontFactory.getFont(FontFactory.HELVETICA, 9, BaseColor.GRAY);
    private static final BaseColor COLOR_PRIMARY = new BaseColor(30, 45, 90);
    private static final BaseColor COLOR_SUCCESS = new BaseColor(40, 167, 69);
    private static final BaseColor COLOR_WARNING = new BaseColor(255, 193, 7);
    private static final BaseColor COLOR_DANGER = new BaseColor(220, 53, 69);

    public static void generatePremiumReport(int konsultasiId, String savePath) throws Exception {
        Document document = new Document(PageSize.A4, 50, 50, 50, 50);
        PdfWriter writer = PdfWriter.getInstance(document, new FileOutputStream(savePath));
        
        writer.setPageEvent(new PdfPageEventHelper() {
            @Override
            public void onEndPage(PdfWriter writer, Document document) {
                PdfContentByte cb = writer.getDirectContent();
                Phrase footer = new Phrase("Sistem Pakar Rekomendasi Jurusan v1.0 | Laporan Eksklusif | Hal. " + writer.getPageNumber(), FONT_SMALL);
                ColumnText.showTextAligned(cb, Element.ALIGN_CENTER, footer, 
                    (document.right() - document.left()) / 2 + document.leftMargin(), 
                    document.bottom() - 20, 0);
            }
        });

        document.open();

        ResultSet rs = DBConnection.executeQuery(
            "SELECT k.*, s.nis, s.nama as s_nama, s.kelas, s.jurusan_sma, s.jenis_kelamin, s.tempat_lahir, s.tanggal_lahir, " +
            "s.email as s_email, s.telepon as s_telepon, s.nama_ortu, s.hobi, s.prestasi, s.cita_cita, " +
            "j1.id as r1_id, j1.nama as r1_nama, j1.rumpun as r1_rumpun, j1.deskripsi as r1_desk, j1.prospek_kerja as r1_prospek, " +
            "j2.id as r2_id, j2.nama as r2_nama, j2.rumpun as r2_rumpun, j2.deskripsi as r2_desk, j2.prospek_kerja as r2_prospek, " +
            "j3.id as r3_id, j3.nama as r3_nama, j3.rumpun as r3_rumpun, j3.deskripsi as r3_desk, j3.prospek_kerja as r3_prospek, " +
            "c.nama as c_nama, " +
            "s.nilai_matematika, s.nilai_ipa, s.nilai_ips, s.nilai_bahasa_ind, s.nilai_bahasa_ing, s.nilai_seni, s.nilai_olahraga, s.nilai_komputer " +
            "FROM konsultasi k " +
            "JOIN siswa s ON k.siswa_id = s.id " +
            "JOIN konselor c ON k.konselor_id = c.id " +
            "LEFT JOIN jurusan_kuliah j1 ON k.rekomendasi_utama_id = j1.id " +
            "LEFT JOIN jurusan_kuliah j2 ON k.rekomendasi_alt1_id = j2.id " +
            "LEFT JOIN jurusan_kuliah j3 ON k.rekomendasi_alt2_id = j3.id " +
            "WHERE k.id = ?", konsultasiId
        );

        if (!rs.next()) throw new Exception("Data konsultasi tidak ditemukan");

        double valMtk = rs.getDouble("nilai_matematika");
        double valIpa = rs.getDouble("nilai_ipa");
        double valIps = rs.getDouble("nilai_ips");
        double valBind = rs.getDouble("nilai_bahasa_ind");
        double valBing = rs.getDouble("nilai_bahasa_ing");
        double valSeni = rs.getDouble("nilai_seni");
        
        int skorTech = rs.getInt("skor_teknologi");
        int skorSci = rs.getInt("skor_sains");
        int skorSoc = rs.getInt("skor_sosial");
        int skorArt = rs.getInt("skor_seni");
        int skorBiz = rs.getInt("skor_bisnis");
        int skorLang = rs.getInt("skor_bahasa");
        int skorHealth = rs.getInt("skor_kesehatan");

        double avgNilai = (valMtk + valIpa + valIps + valBind + valBing) / 5.0;
        double estUtbk = avgNilai * 8.5; 

        // ==========================================
        // COVER PAGE
        // ==========================================
        for(int i=0; i<5; i++) document.add(new Paragraph("\n"));
        Paragraph coverKop = new Paragraph("KEMENTERIAN PENDIDIKAN, KEBUDAYAAN, RISET, DAN TEKNOLOGI\nSMAN 1 CONTOH KOTA", FontFactory.getFont(FontFactory.HELVETICA_BOLD, 14, BaseColor.GRAY));
        coverKop.setAlignment(Element.ALIGN_CENTER);
        document.add(coverKop);
        
        document.add(new Chunk(new LineSeparator(2, 60, COLOR_PRIMARY, Element.ALIGN_CENTER, -5)));
        for(int i=0; i<3; i++) document.add(new Paragraph("\n"));
        
        Paragraph docTitle = new Paragraph("RAPOR SISWA\nHASIL ASESMEN MINAT BAKAT\n& EVALUASI AKADEMIK KOMPREHENSIF", FONT_TITLE);
        docTitle.setAlignment(Element.ALIGN_CENTER);
        docTitle.setSpacingAfter(40);
        document.add(docTitle);
        
        Paragraph pNamaCover = new Paragraph(rs.getString("s_nama").toUpperCase(), FontFactory.getFont(FontFactory.HELVETICA_BOLD, 28, COLOR_PRIMARY));
        pNamaCover.setAlignment(Element.ALIGN_CENTER);
        document.add(pNamaCover);
        
        Paragraph pNisCover = new Paragraph("Nomor Induk Siswa: " + rs.getString("nis") + "\nKelas: " + rs.getString("kelas") + " " + rs.getString("jurusan_sma"), FontFactory.getFont(FontFactory.HELVETICA, 16, BaseColor.DARK_GRAY));
        pNisCover.setAlignment(Element.ALIGN_CENTER);
        pNisCover.setSpacingBefore(10);
        document.add(pNisCover);
        
        for(int i=0; i<6; i++) document.add(new Paragraph("\n"));
        
        Paragraph pDocDate = new Paragraph("Diterbitkan secara resmi oleh Sistem Pakar pada:\n" + new SimpleDateFormat("dd MMMM yyyy").format(new Date()), FontFactory.getFont(FontFactory.HELVETICA_OBLIQUE, 12, BaseColor.GRAY));
        pDocDate.setAlignment(Element.ALIGN_CENTER);
        document.add(pDocDate);
        
        // ==========================================
        // SECTION I: PENDAHULUAN & PROFIL
        // ==========================================
        document.newPage();
        addSectionTitle(document, "I. PENDAHULUAN & PROFIL SISWA");
        
        Paragraph pIntro = new Paragraph("Laporan ini merupakan hasil evaluasi komprehensif yang dihasilkan oleh Sistem Pakar Rekomendasi Jurusan. Asesmen ini menggabungkan dua variabel utama: evaluasi nilai akademik historis (rapor) dan hasil tes psikologi minat bakat. Tujuannya adalah memberikan panduan strategis berbasis data bagi siswa untuk merencanakan masa depan pendidikan tinggi yang selaras dengan potensi kognitif dan ketertarikan profesional mereka.", FONT_NARRATIVE);
        pIntro.setSpacingAfter(15);
        pIntro.setAlignment(Element.ALIGN_JUSTIFIED);
        document.add(pIntro);

        PdfPTable tableSiswa = new PdfPTable(4);
        tableSiswa.setWidthPercentage(100);
        tableSiswa.setWidths(new float[]{1.5f, 3.5f, 1.5f, 3.5f});
        tableSiswa.setSpacingBefore(10);
        tableSiswa.setSpacingAfter(20);
        
        addInfoRow(tableSiswa, "Nama Lengkap", rs.getString("s_nama"));
        addInfoRow(tableSiswa, "NIS", rs.getString("nis"));
        String ttl = rs.getString("tempat_lahir") + ", " + (rs.getDate("tanggal_lahir") != null ? new SimpleDateFormat("dd MMMM yyyy").format(rs.getDate("tanggal_lahir")) : "-");
        addInfoRow(tableSiswa, "Tempat, Tgl Lahir", ttl);
        addInfoRow(tableSiswa, "Jenis Kelamin", "L".equals(rs.getString("jenis_kelamin")) ? "Laki-laki" : "Perempuan");
        addInfoRow(tableSiswa, "Kelas / Jurusan", rs.getString("kelas") + " / " + rs.getString("jurusan_sma"));
        addInfoRow(tableSiswa, "Cita-cita", rs.getString("cita_cita") != null ? rs.getString("cita_cita") : "-");
        PdfPCell cSpan1 = new PdfPCell(new Phrase("Hobi / Prestasi", FONT_BOLD)); cSpan1.setBorder(Rectangle.NO_BORDER);
        PdfPCell cSpan2 = new PdfPCell(new Phrase(": " + (rs.getString("hobi") != null ? rs.getString("hobi") : "-") + " / " + (rs.getString("prestasi") != null ? rs.getString("prestasi") : "-"), FONT_NORMAL));
        cSpan2.setBorder(Rectangle.NO_BORDER); cSpan2.setColspan(3);
        tableSiswa.addCell(cSpan1); tableSiswa.addCell(cSpan2);
        document.add(tableSiswa);

        String narasiProfil = "Berdasarkan data profil, siswa ini memiliki latar belakang ketertarikan pada bidang " + (rs.getString("hobi") != null ? rs.getString("hobi") : "umum") + " dan bercita-cita menjadi " + (rs.getString("cita_cita") != null ? rs.getString("cita_cita") : "profesional yang handal") + ". Visi dan hobi ini menjadi fondasi awal yang penting dalam menentukan arah karir. Keterlibatan aktif dalam kegiatan di luar kurikulum formal juga dapat dilihat dari catatan prestasi yang telah dicapai, yang menunjukkan dedikasi dan kemampuan manajemen waktu yang baik.";
        Paragraph pProfil = new Paragraph(narasiProfil, FONT_NARRATIVE);
        pProfil.setAlignment(Element.ALIGN_JUSTIFIED);
        document.add(pProfil);

        // ==========================================
        // SECTION II: EVALUASI AKADEMIK MENDALAM
        // ==========================================
        document.newPage();
        addSectionTitle(document, "II. EVALUASI NILAI AKADEMIK KOMPREHENSIF");
        document.add(new Paragraph("Tabel berikut menyajikan rekapitulasi nilai rata-rata mata pelajaran inti selama masa studi. Nilai-nilai ini menjadi indikator utama kapasitas serap ilmu pengetahuan teoritis yang sangat krusial untuk kesuksesan di Perguruan Tinggi.", FONT_NARRATIVE));
        
        PdfPTable tableRapor = new PdfPTable(4);
        tableRapor.setWidthPercentage(100);
        tableRapor.setWidths(new float[]{3, 2, 2, 3});
        tableRapor.setSpacingBefore(15);
        tableRapor.setSpacingAfter(15);
        
        addCell(tableRapor, "Mata Pelajaran", FONT_HEADER, COLOR_PRIMARY);
        addCell(tableRapor, "Nilai Rata-rata", FONT_HEADER, COLOR_PRIMARY);
        addCell(tableRapor, "Predikat", FONT_HEADER, COLOR_PRIMARY);
        addCell(tableRapor, "Kategori", FONT_HEADER, COLOR_PRIMARY);
        
        String[] mapel = {"Matematika", "Ilmu Pengetahuan Alam (IPA)", "Ilmu Pengetahuan Sosial (IPS)", "Bahasa Indonesia", "Bahasa Inggris", "Seni Budaya"};
        double[] vals = {valMtk, valIpa, valIps, valBind, valBing, valSeni};
        
        for (int i=0; i<mapel.length; i++) {
            double val = vals[i];
            String predikat = val >= 85 ? "A (Sangat Baik)" : val >= 75 ? "B (Baik)" : val >= 65 ? "C (Cukup)" : "D (Kurang)";
            String kategori = val >= 85 ? "Sangat Memadai" : val >= 75 ? "Memadai" : "Perlu Perhatian Khusus";
            
            addCell(tableRapor, mapel[i], FONT_NORMAL, BaseColor.WHITE);
            addCell(tableRapor, String.valueOf(val), FONT_BOLD, BaseColor.WHITE);
            addCell(tableRapor, predikat, FONT_NORMAL, BaseColor.WHITE);
            addCell(tableRapor, kategori, val >= 75 ? FONT_NORMAL : FONT_BOLD, val >= 75 ? BaseColor.WHITE : new BaseColor(255, 230, 230));
        }
        document.add(tableRapor);
        
        Paragraph pAnalisisAkademik = new Paragraph(generateAcademicNarrative(valMtk, valIpa, valIps, valBind, valBing, valSeni), FONT_NARRATIVE);
        pAnalisisAkademik.setAlignment(Element.ALIGN_JUSTIFIED);
        document.add(pAnalisisAkademik);

        // ==========================================
        // SECTION III: BEDAH PSIKOLOGI (MINAT BAKAT)
        // ==========================================
        document.newPage();
        addSectionTitle(document, "III. BEDAH PSIKOLOGI: ASESMEN MINAT & BAKAT");
        document.add(new Paragraph("Hasil asesmen psikometri di bawah ini memetakan kecenderungan minat profesional siswa ke dalam 7 klaster utama. Pengukuran dilakukan menggunakan instrumen yang divalidasi, dengan skor maksimal 32 poin per kategori.", FONT_NARRATIVE));
        
        PdfPTable tableSkor = new PdfPTable(4);
        tableSkor.setWidthPercentage(100);
        tableSkor.setWidths(new float[]{3, 1, 4, 2});
        tableSkor.setSpacingBefore(15);
        tableSkor.setSpacingAfter(15);
        
        addCell(tableSkor, "Kategori Klaster", FONT_HEADER, COLOR_PRIMARY);
        addCell(tableSkor, "Skor", FONT_HEADER, COLOR_PRIMARY);
        addCell(tableSkor, "Intensitas & Bar (Maks 32)", FONT_HEADER, COLOR_PRIMARY);
        addCell(tableSkor, "Interpretasi", FONT_HEADER, COLOR_PRIMARY);
        
        String[] cats = {"Teknologi & Komputasi", "Sains & Riset", "Ilmu Sosial & Humaniora", "Seni, Desain & Kreatif", "Bisnis & Manajemen", "Bahasa & Komunikasi", "Medis & Kesehatan"};
        int[] scores = {skorTech, skorSci, skorSoc, skorArt, skorBiz, skorLang, skorHealth};
        
        for (int i=0; i<cats.length; i++) {
            int skor = scores[i];
            double pct = (skor / 32.0) * 100;
            String level = skor >= 24 ? "Dominan (Tinggi)" : skor >= 16 ? "Menengah (Cukup)" : "Pasif (Rendah)"; 
            
            int blocks = (int)Math.round((skor / 32.0) * 10);
            StringBuilder bar = new StringBuilder();
            for(int b=0; b<10; b++) bar.append(b < blocks ? "█" : "░");
            
            addCell(tableSkor, cats[i], FONT_NORMAL, BaseColor.WHITE);
            addCell(tableSkor, skor + "/32", FONT_BOLD, BaseColor.WHITE);
            addCell(tableSkor, bar.toString() + " " + String.format(Locale.US, "%.1f%%", pct), FONT_NORMAL, BaseColor.WHITE);
            
            BaseColor lvlColor = skor >= 24 ? COLOR_SUCCESS : skor >= 16 ? COLOR_WARNING : COLOR_DANGER;
            PdfPCell cLvl = new PdfPCell(new Phrase(level, FontFactory.getFont(FontFactory.HELVETICA_BOLD, 9, lvlColor)));
            cLvl.setHorizontalAlignment(Element.ALIGN_CENTER);
            cLvl.setVerticalAlignment(Element.ALIGN_MIDDLE);
            tableSkor.addCell(cLvl);
        }
        document.add(tableSkor);

        Paragraph pAnalisisPsikologi = new Paragraph(generatePsychologicalNarrative(skorTech, skorSci, skorSoc, skorArt, skorBiz, skorLang, skorHealth), FONT_NARRATIVE);
        pAnalisisPsikologi.setAlignment(Element.ALIGN_JUSTIFIED);
        document.add(pAnalisisPsikologi);

        // ==========================================
        // SECTION IV: STRATEGI PTN
        // ==========================================
        String[] rLabels = {"PRIORITAS UTAMA", "ALTERNATIF PERTAMA", "ALTERNATIF KEDUA"};
        String[] rIds = {"r1_id", "r2_id", "r3_id"};
        String[] rNames = {"r1_nama", "r2_nama", "r3_nama"};
        String[] rRumpuns = {"r1_rumpun", "r2_rumpun", "r3_rumpun"};
        String[] rDescs = {"r1_desk", "r2_desk", "r3_desk"};
        String[] rProspeks = {"r1_prospek", "r2_prospek", "r3_prospek"};
        NumberFormat nf = NumberFormat.getCurrencyInstance(new Locale("id", "ID"));

        for (int i = 0; i < 3; i++) {
            int jurId = rs.getInt(rIds[i]);
            if (jurId == 0 || rs.getString(rNames[i]) == null) continue;

            document.newPage();
            addSectionTitle(document, "IV. STRATEGI JURUSAN: " + rLabels[i]);
            
            String jurusanNama = rs.getString(rNames[i]);
            String rumpun = rs.getString(rRumpuns[i]);

            PdfPTable tableRek = new PdfPTable(1);
            tableRek.setWidthPercentage(100);
            tableRek.setSpacingBefore(10);
            
            PdfPCell rekCell = new PdfPCell();
            rekCell.setPadding(15);
            rekCell.setBackgroundColor(new BaseColor(245, 248, 255));
            rekCell.setBorderColor(COLOR_PRIMARY);
            rekCell.setBorderWidthLeft(4f);
            
            Paragraph pRek = new Paragraph(jurusanNama.toUpperCase(), FontFactory.getFont(FontFactory.HELVETICA_BOLD, 18, COLOR_PRIMARY));
            pRek.setSpacingAfter(10);
            rekCell.addElement(pRek);
            
            rekCell.addElement(new Paragraph("Rumpun Keilmuan: " + rumpun, FONT_BOLD));
            
            Paragraph pDesk = new Paragraph("Deskripsi Akademik:\n" + rs.getString(rDescs[i]), FONT_NARRATIVE);
            pDesk.setSpacingBefore(10);
            rekCell.addElement(pDesk);
            
            Paragraph pProspek = new Paragraph("Peluang & Prospek Karir Masa Depan:\n" + (rs.getString(rProspeks[i]) != null ? rs.getString(rProspeks[i]) : "-"), FONT_NARRATIVE);
            pProspek.setSpacingBefore(10);
            rekCell.addElement(pProspek);

            String analisisPakar = generateAnalisisPakar(rumpun, valMtk, valIpa, valIps, valBind, valBing, valSeni);
            Paragraph pAnalisis = new Paragraph("\n🧠 Bedah Pakar Akademik:\n" + analisisPakar, FontFactory.getFont(FontFactory.HELVETICA_OBLIQUE, 10, new BaseColor(60, 60, 60)));
            rekCell.addElement(pAnalisis);

            tableRek.addCell(rekCell);
            document.add(tableRek);

            document.add(new Paragraph("\n"));
            document.add(new Paragraph("Daftar Perguruan Tinggi Negeri (PTN) yang menyediakan program studi ini beserta kalkulasi peluang personal berbasis data historis UTBK/SNBP:", FONT_NARRATIVE));

            PdfPTable tableUniv = new PdfPTable(7);
            tableUniv.setWidthPercentage(100);
            tableUniv.setSpacingBefore(10);
            tableUniv.setWidths(new float[]{3.5f, 2f, 1.5f, 1.5f, 1.5f, 2.5f, 1.5f});
            
            addCell(tableUniv, "Universitas", FONT_HEADER, COLOR_PRIMARY);
            addCell(tableUniv, "Provinsi", FONT_HEADER, COLOR_PRIMARY);
            addCell(tableUniv, "Akreditasi", FONT_HEADER, COLOR_PRIMARY);
            addCell(tableUniv, "PG SNBP", FONT_HEADER, COLOR_PRIMARY);
            addCell(tableUniv, "PG SNBT", FONT_HEADER, COLOR_PRIMARY);
            addCell(tableUniv, "Biaya/SMT", FONT_HEADER, COLOR_PRIMARY);
            addCell(tableUniv, "Peluang", FONT_HEADER, COLOR_PRIMARY);

            ResultSet rsU = DBConnection.executeQuery(
                "SELECT u.nama as u_nama, u.provinsi, uj.akreditasi_prodi, uj.passing_grade_snbp, uj.passing_grade_snbt, uj.biaya_kuliah " +
                "FROM universitas_jurusan uj " +
                "JOIN universitas u ON uj.universitas_id = u.id " +
                "WHERE uj.jurusan_id = ? ORDER BY uj.passing_grade_snbt DESC LIMIT 7", 
                jurId
            );
            
            boolean hasUniv = false;
            double topPgSnbt = 0;
            double topPgSnbp = 0;
            String topUnivName = "";
            double bestPeluang = -1.0;
            String bestUnivName = "";
            String bestStatus = "";

            while (rsU.next()) {
                hasUniv = true;
                String univName = rsU.getString("u_nama");
                double pgSnbp = rsU.getDouble("passing_grade_snbp");
                double pgSnbt = rsU.getDouble("passing_grade_snbt");
                
                if(topPgSnbt == 0) {
                    topPgSnbt = pgSnbt; topPgSnbp = pgSnbp; topUnivName = univName;
                }

                double diff = estUtbk - pgSnbt;
                double peluang; String status; BaseColor col;
                
                if (diff >= 20) { peluang = Math.min(95.0, 80.0 + (diff * 0.15)); status = "Aman"; col = COLOR_SUCCESS; }
                else if (diff >= 0) { peluang = Math.min(79.9, 60.0 + (diff * 0.5)); status = "Cukup"; col = COLOR_SUCCESS; }
                else if (diff >= -30) { peluang = Math.max(35.0, 60.0 + diff); status = "Berisiko"; col = COLOR_WARNING; }
                else { peluang = Math.max(2.0, 35.0 + (diff * 0.3)); status = "Sulit"; col = COLOR_DANGER; }
                
                if (peluang > bestPeluang) {
                    bestPeluang = peluang;
                    bestUnivName = univName;
                    bestStatus = status;
                }
                
                addCell(tableUniv, univName, FONT_SMALL, BaseColor.WHITE);
                addCell(tableUniv, rsU.getString("provinsi"), FONT_SMALL, BaseColor.WHITE);
                addCell(tableUniv, rsU.getString("akreditasi_prodi"), FONT_SMALL, BaseColor.WHITE);
                addCell(tableUniv, String.valueOf(pgSnbp), FONT_SMALL, BaseColor.WHITE);
                addCell(tableUniv, String.valueOf(pgSnbt), FONT_SMALL, BaseColor.WHITE);
                addCell(tableUniv, nf.format(rsU.getLong("biaya_kuliah")).replace(",00", ""), FONT_SMALL, BaseColor.WHITE);
                
                PdfPCell cStat = new PdfPCell(new Phrase(String.format(Locale.US, "%.1f%%\n(%s)", peluang, status), FontFactory.getFont(FontFactory.HELVETICA_BOLD, 8, col)));
                cStat.setHorizontalAlignment(Element.ALIGN_CENTER);
                cStat.setVerticalAlignment(Element.ALIGN_MIDDLE);
                tableUniv.addCell(cStat);
            }
            rsU.close();
            
            if(hasUniv) {
                document.add(tableUniv);
                String strategi = generateStrategiTembus(estUtbk, topPgSnbt, topPgSnbp, topUnivName);
                Paragraph pStrat = new Paragraph("🎯 Taktik Eksekusi SNBP/SNBT:\n" + strategi, FontFactory.getFont(FontFactory.HELVETICA_BOLD, 10, COLOR_PRIMARY));
                pStrat.setSpacingBefore(15);
                pStrat.setAlignment(Element.ALIGN_JUSTIFIED);
                document.add(pStrat);
                
                String riskAndProspect = generateMarketAndRiskAnalysis(rumpun, topUnivName);
                Paragraph pRisk = new Paragraph("\n⚠️ Analisis Risiko, Persaingan & Prospek Pasar:\n" + riskAndProspect, FontFactory.getFont(FontFactory.HELVETICA, 10, new BaseColor(80, 80, 80)));
                pRisk.setAlignment(Element.ALIGN_JUSTIFIED);
                document.add(pRisk);
                
                String peluangAnalysis = generatePeluangAndCareerAnalysis(jurusanNama, rumpun, bestPeluang, bestUnivName, bestStatus);
                Paragraph pPeluangText = new Paragraph("\n📈 Insight Peluang Tertinggi (Safety Net):\n" + peluangAnalysis, FontFactory.getFont(FontFactory.HELVETICA, 10, new BaseColor(0, 100, 0)));
                pPeluangText.setAlignment(Element.ALIGN_JUSTIFIED);
                document.add(pPeluangText);
            }
        }

        // ==========================================
        // SECTION V: CATATAN & PENGESAHAN
        // ==========================================
        document.newPage(); 
        addSectionTitle(document, "V. CATATAN EKSEKUTIF KONSELOR & PENGESAHAN");
        
        PdfPTable tableCatatan = new PdfPTable(1);
        tableCatatan.setWidthPercentage(100);
        tableCatatan.setSpacingBefore(10);
        
        PdfPCell catCell = new PdfPCell();
        catCell.setPadding(20);
        catCell.setBackgroundColor(new BaseColor(250, 250, 250));
        
        catCell.addElement(new Paragraph("📌 Pandangan & Evaluasi Guru Bimbingan Konseling:", FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12, COLOR_PRIMARY)));
        Paragraph pCat = new Paragraph(rs.getString("catatan_konselor") != null ? rs.getString("catatan_konselor") : "Siswa diharapkan dapat menggunakan hasil laporan komprehensif ini sebagai batu loncatan untuk lebih fokus mengejar ketertinggalan dan mempertahankan keunggulan pada mata pelajaran yang linear dengan rencana studi lanjut.", FONT_NARRATIVE);
        pCat.setSpacingBefore(10); pCat.setSpacingAfter(20);
        catCell.addElement(pCat);
        
        catCell.addElement(new Paragraph("💡 Tips Pembelajaran Mental & Akademik:", FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12, COLOR_PRIMARY)));
        String tips = rs.getString("tips_belajar") != null ? rs.getString("tips_belajar") : "";
        tips += "\n• Manajemen Waktu: Susun jadwal harian yang ketat antara belajar konsep UTBK dan pendalaman materi sekolah.\n• Tryout Rutin: Biasakan menghadapi soal HOTS (High Order Thinking Skills) setidaknya 2 minggu sekali untuk mengasah intuisi pengerjaan.\n• Mental Resiliensi: Kegagalan simulasi bukanlah akhir, jadikan hal tersebut sebagai evaluasi langsung untuk menambal kelemahan materi.";
        Paragraph pTips = new Paragraph(tips, FONT_NARRATIVE);
        pTips.setSpacingBefore(10); pTips.setSpacingAfter(20);
        catCell.addElement(pTips);
        
        tableCatatan.addCell(catCell);
        document.add(tableCatatan);

        document.add(new Paragraph("\n\n\n"));
        PdfPTable tableTtd = new PdfPTable(3);
        tableTtd.setWidthPercentage(100);
        
        PdfPCell tSiswa = new PdfPCell(new Phrase("Siswa Teruji,\n\n\n\n\n\n(" + rs.getString("s_nama") + ")\nNIS. " + rs.getString("nis"), FONT_NORMAL));
        tSiswa.setBorder(Rectangle.NO_BORDER); tSiswa.setHorizontalAlignment(Element.ALIGN_CENTER);
        
        PdfPCell tSpace = new PdfPCell(new Phrase(" ", FONT_NORMAL));
        tSpace.setBorder(Rectangle.NO_BORDER);
        
        PdfPCell tKonselor = new PdfPCell(new Phrase("Mengesahkan,\nKonselor / Guru BK Utama\n\n\n\n\n\n(" + rs.getString("c_nama") + ")", FONT_NORMAL));
        tKonselor.setBorder(Rectangle.NO_BORDER); tKonselor.setHorizontalAlignment(Element.ALIGN_CENTER);
        
        tableTtd.addCell(tSiswa);
        tableTtd.addCell(tSpace);
        tableTtd.addCell(tKonselor);
        
        document.add(tableTtd);

        document.close();
        rs.close(); 
    }

    // ==========================================
    // MASSIVE TEXT GENERATORS
    // ==========================================

    private static String generateAcademicNarrative(double mtk, double ipa, double ips, double bind, double bing, double seni) {
        StringBuilder sb = new StringBuilder();
        sb.append("Analisis Mendalam Akademik: \n");
        sb.append("Melihat distribusi nilai di atas, dapat disimpulkan bahwa profil akademik siswa memiliki karakteristik yang unik. ");
        
        double avgSains = (mtk + ipa) / 2.0;
        double avgSoshum = (ips + bind + bing) / 3.0;

        if (avgSains > avgSoshum && avgSains >= 80) {
            sb.append("Siswa menunjukkan keunggulan komparatif yang sangat jelas pada rumpun Ilmu Pengetahuan Alam (Sains) dan Matematika. Kemampuan berpikir logis, analitis, dan pemecahan masalah kuantitatif (problem-solving) berada di atas rata-rata. Hal ini merupakan modal yang sangat fundamental dan berharga apabila siswa memilih untuk terjun ke ranah rekayasa (engineering), kedokteran, atau sains terapan lainnya. Tantangan terbesar saat ini adalah mempertahankan konsistensi ini hingga ujian seleksi akhir.\n\n");
        } else if (avgSoshum > avgSains && avgSoshum >= 80) {
            sb.append("Siswa memperlihatkan dominasi yang luar biasa pada rumpun Sosial dan Humaniora, termasuk penguasaan bahasa. Ini mengindikasikan kecerdasan verbal-linguistik dan kecerdasan interpersonal yang berkembang dengan baik. Kemampuan untuk mencerna literatur yang padat, memahami konteks sosial budaya, serta berkomunikasi secara efektif adalah kekuatan utama. Siswa sangat disarankan mengeksplorasi ranah ilmu komunikasi, hubungan internasional, hukum, atau humaniora tingkat lanjut.\n\n");
        } else if (seni >= 85 && seni > avgSains && seni > avgSoshum) {
            sb.append("Secara spesifik, nilai Seni Budaya yang sangat menonjol menyoroti bakat kreatif, spasial, dan estetika yang luar biasa dari siswa. Kecerdasan semacam ini sangat dibutuhkan dalam industri kreatif modern, arsitektur, maupun desain komunikasi visual. Sangat disarankan bagi siswa untuk mulai menyusun portofolio karya sejak dini sebagai amunisi utama dalam jalur seleksi masuk perguruan tinggi seni atau desain bergengsi.\n\n");
        } else {
            sb.append("Siswa memiliki distribusi nilai yang relatif merata (versatile) antara rumpun Sains, Sosial, dan Bahasa. Pola ekuilibrium ini memungkinkan siswa untuk sangat adaptif (agile) dalam menghadapi lintas disiplin ilmu. Walaupun demikian, ketiadaan satu bidang yang sangat dominan mengharuskan siswa untuk segera memfokuskan energi pada satu klaster spesifik yang sejalan dengan cita-cita, agar dapat bersaing secara tajam di jalur UTBK maupun SNBP.\n\n");
        }

        // Kelemahan
        double min = Math.min(mtk, Math.min(ipa, Math.min(ips, Math.min(bind, Math.min(bing, seni)))));
        sb.append("Terkait area yang membutuhkan atensi ekstra: Mata pelajaran dengan perolehan terendah berada di angka ").append(String.format(Locale.US, "%.1f", min)).append(". ");
        if (min < 75) {
            sb.append("Nilai ini berada di bawah batas kompetensi minimum yang disarankan. Jika mata pelajaran ini linear atau menjadi syarat wajib (prasyarat) dari jurusan yang dituju kelak di bangku kuliah, siswa WAJIB melakukan intervensi akademik secara masif, seperti mengikuti jam tambahan, bimbingan belajar khusus, atau mengulang konsep-konsep fundamental yang tertinggal.");
        } else {
            sb.append("Meskipun secara umum seluruh nilai berada dalam batas aman dan memadai, kelemahan relatif ini tetap harus diawasi agar tidak merusak rata-rata keseluruhan (passing grade) jika siswa berniat mengincar Perguruan Tinggi Negeri (PTN) Top Tier yang memiliki seleksi berbasis rapor (SNBP) yang sangat ketat.");
        }
        
        return sb.toString();
    }

    private static String generatePsychologicalNarrative(int t, int s, int so, int a, int b, int l, int h) {
        StringBuilder sb = new StringBuilder();
        sb.append("Interpretasi Holistik Psikometri: \n");
        sb.append("Asesmen ini mengadopsi kerangka teori kecerdasan majemuk dan ketertarikan okupasional. Berdasarkan grafik distribusi di atas, ");
        
        // Find max
        int max = Math.max(t, Math.max(s, Math.max(so, Math.max(a, Math.max(b, Math.max(l, h))))));
        String maxStr = "";
        if(max == t) maxStr = "Teknologi & Komputasi";
        else if(max == s) maxStr = "Sains & Riset";
        else if(max == so) maxStr = "Sosial & Humaniora";
        else if(max == a) maxStr = "Seni & Desain";
        else if(max == b) maxStr = "Bisnis & Manajemen";
        else if(max == l) maxStr = "Bahasa & Komunikasi";
        else maxStr = "Medis & Kesehatan";

        sb.append("potensi intelektual dan ketertarikan terdalam siswa bermuara pada klaster ").append(maxStr).append(". ");
        
        if (max >= 25) {
            sb.append("Skor yang melampaui batas superior (mendekati nilai absolut 32) pada klaster ini merupakan sinyal yang sangat kuat (hard evidence) bahwa otak siswa secara alami terprogram untuk memproses informasi, menyelesaikan masalah, dan merasa paling termotivasi di bidang tersebut. Individu dengan pola seperti ini biasanya akan mengalami 'flow state' (fokus tingkat tinggi tanpa paksaan) saat dihadapkan pada tugas-tugas atau studi yang berkaitan dengan bidang ini. Jangan sia-siakan bakat alami ini.\n\n");
        } else if (max >= 18) {
            sb.append("Meskipun tidak mencapai level ekstrem, klaster ini tetap menjadi titik tumpu (anchoring point) terkuat bagi siswa. Minat ini cukup stabil dan bisa dikembangkan menjadi keahlian profesional asalkan didukung dengan lingkungan akademis yang tepat serta determinasi yang kuat dari siswa itu sendiri.\n\n");
        } else {
            sb.append("Skor tertinggi siswa masih berada pada level menengah ke bawah. Ini mengindikasikan fase eksplorasi yang belum tuntas (unresolved exploration phase). Siswa mungkin belum benar-benar terekspos pada pengalaman yang memicu 'passion' mereka, atau masih mengalami kebingungan (confusion) dalam menentukan identitas profesionalnya. Perlu bimbingan dan paparan langsung terhadap dunia karir.\n\n");
        }

        sb.append("Sistem kami sangat menyarankan agar pemilihan program studi di Perguruan Tinggi diselaraskan dengan hasil dominan ini. Ketidakselarasan antara minat intrinsik (psikologi) dan ekstrinsik (jurusan/pekerjaan) seringkali berujung pada demotivasi akademik, tingginya angka drop-out di pertengahan semester, atau fenomena krisis karir di kemudian hari.");

        return sb.toString();
    }

    private static String generateAnalisisPakar(String rumpun, double mtk, double ipa, double ips, double bind, double bing, double seni) {
        if(rumpun == null) return "Data sesuai dengan profil Anda.";
        double avgMtkIpa = (mtk + ipa) / 2.0;
        double avgSosBhs = (ips + bind + bing) / 3.0;
        
        double skorDominan; String mapelKuat;
        if (rumpun.contains("Sains") || rumpun.contains("Teknologi") || rumpun.contains("Kesehatan") || rumpun.contains("Teknik")) {
            skorDominan = avgMtkIpa; mapelKuat = "Matematika & IPA";
        } else if (rumpun.contains("Sosial") || rumpun.contains("Bisnis") || rumpun.contains("Bahasa") || rumpun.contains("Ekonomi")) {
            skorDominan = avgSosBhs; mapelKuat = "Sosial, Bahasa & Logika Verbal";
        } else {
            skorDominan = seni > 0 ? seni : avgSosBhs; mapelKuat = "Seni, Estetika & Praktik Spasial";
        }

        if (skorDominan >= 85) return "Rekomendasi ini memiliki landasan empiris yang sangat solid. Kesiapan akademik Anda di sektor " + mapelKuat + " (Rata-rata: " + String.format(Locale.US, "%.1f", skorDominan) + ") sudah berada pada level ekuivalen dengan ekspektasi kurikulum Perguruan Tinggi di jurusan ini. Peluang survival dan keberhasilan studi cumlaude sangat terbuka lebar.";
        if (skorDominan >= 75) return "Rekomendasi ini realistis dan dapat dieksekusi. Performa Anda pada " + mapelKuat + " (Rata-rata: " + String.format(Locale.US, "%.1f", skorDominan) + ") sudah mencapai standar minimum kelayakan. Meski begitu, kompetisi di PTN mengharuskan Anda memompa lagi pemahaman konsep dasar agar tidak kewalahan saat memasuki semester awal perkuliahan.";
        return "Terdapat gap (kesenjangan) kompetensi yang cukup berisiko. Jurusan ini menuntut penguasaan mutlak di bidang " + mapelKuat + ", sementara nilai Anda saat ini belum merefleksikan kesiapan tersebut. Jika Anda bersikeras mengejar jalur ini, Anda wajib melakukan transformasi cara belajar yang radikal terhitung sejak hari ini.";
    }

    private static String generateStrategiTembus(double estUtbk, double pgSnbt, double pgSnbp, String topUniv) {
        if (pgSnbt == 0) return "Sistem kami tidak memiliki data histori passing grade untuk PTN tersebut. Fokuskan energi 100% pada ujian Mandiri institusi.";
        StringBuilder sb = new StringBuilder();
        
        sb.append("Menembus ").append(topUniv).append(" membutuhkan kalkulasi matematis yang presisi. Target Passing Grade SNBT nasional untuk institusi ini berada di kisaran ").append(String.format(Locale.US, "%.0f", pgSnbt)).append(" poin. ");
        
        if (estUtbk >= pgSnbt) {
            sb.append("Simulasi menunjukkan estimasi kemampuan rasional Anda saat ini (").append(String.format(Locale.US, "%.0f", estUtbk)).append(" poin) SUDAH BERADA DI ZONA AMAN. Ini adalah berita fantastis. Namun, ingat bahwa UTBK menerapkan sistem pembobotan IRT (Item Response Theory). Anda tidak boleh lengah; fokuskan strategi Anda pada subtes Literasi Bahasa Indonesia, Literasi Bahasa Inggris, dan Penalaran Matematika tingkat lanjut untuk mengamankan persentil atas.");
        } else {
            double gap = pgSnbt - estUtbk;
            sb.append("Berdasarkan simulasi, estimasi kekuatan akademik Anda (").append(String.format(Locale.US, "%.0f", estUtbk)).append(" poin) masih DEFISIT SEBESAR ").append(String.format(Locale.US, "%.0f", gap)).append(" poin. Anda berada di ZONA MERAH. Waktu adalah musuh terbesar Anda saat ini. Tinggalkan metode belajar pasif (membaca/menghafal). Mulailah berlatih dengan simulasi TryOut CBT berwaktu secara brutal, minimal 3 kali seminggu, lalu lakukan evaluasi komprehensif pada soal-soal yang salah (bedah soal).");
        }
        
        if (pgSnbp > 0) {
            sb.append("\n\nSedangkan untuk jalur undangan (SNBP), konstelasi persaingan bergantung pada indeks sekolah dan tren grafik nilai Anda dari semester 1 hingga 5. Rata-rata yang kami rekomendasikan adalah menyentuh ekuilibrium ").append(String.format(Locale.US, "%.0f", pgSnbp)).append(". Pastikan juga Anda memiliki sertifikat kejuaraan minimal tingkat Provinsi untuk memuluskan langkah.");
        }
        return sb.toString();
    }

    private static String generateMarketAndRiskAnalysis(String rumpun, String topUniv) {
        StringBuilder sb = new StringBuilder();
        sb.append("Memutuskan untuk bertarung memperebutkan kursi di ").append(topUniv).append(" bukanlah sebuah keputusan yang bisa diambil dengan sebelah mata. ");
        sb.append("Institusi ini memiliki tingkat keketatan (acceptance rate) yang sangat brutal, seringkali menolak lebih dari 90% pendaftarnya. ");
        
        if (rumpun != null && (rumpun.contains("Sains") || rumpun.contains("Teknologi") || rumpun.contains("Teknik") || rumpun.contains("Kesehatan") || rumpun.contains("Medis"))) {
            sb.append("Secara khusus, kompetisi pada rumpun eksakta dan medis adalah ajang pertempuran bagi siswa-siswa elit dari seluruh penjuru negeri. Risiko utama dari kegagalan menembus jurusan ini adalah 'gap year', yang secara psikologis dapat sangat menguras tenaga. Namun, jika Anda berhasil menembus dan bertahan dari kurikulumnya yang terkenal menekan (high-pressure), masa depan Anda akan terjamin. Lulusan dari rumpun ini merupakan talenta langka (scarce resource) yang sangat diburu oleh perusahaan multinasional, raksasa teknologi (Big Tech), maupun institusi riset biomedis global. Gaji (starting salary) lulusannya diproyeksikan akan selalu berada di atas rata-rata inflasi, dan mereka kebal terhadap resesi karena keahlian mereka berbasis pada hard-skill komputasional dan saintifik murni.");
        } else if (rumpun != null && (rumpun.contains("Bisnis") || rumpun.contains("Ekonomi") || rumpun.contains("Manajemen"))) {
            sb.append("Kompetisi pada rumpun bisnis dan manajemen di institusi elit ini sangatlah ketat karena merupakan pilihan paling populer di kalangan siswa lintas jurusan (saintek maupun soshum). Risiko utamanya adalah jebakan 'red ocean', di mana Anda harus bersaing dengan ratusan ribu sarjana ekonomi lainnya saat mencari kerja. Untuk keluar dari jebakan ini, sekadar IPK tinggi tidak akan cukup; Anda wajib membangun jejaring (networking) kelas atas sejak hari pertama ospek dan mengikuti kompetisi bisnis internasional. Jika berhasil, lulusan kampus ini biasanya akan langsung direkrut ke dalam jalur akselerasi (Management Trainee) oleh bank-bank terkemuka, konsultan MBB (McKinsey, BCG, Bain), atau startup unicorn dengan valuasi triliunan.");
        } else {
            sb.append("Memasuki rumpun ilmu Sosial, Humaniora, Seni, atau Linguistik di institusi papan atas memberikan kebanggaan tersendiri, namun menuntut kemampuan kritis dan analisis wacana yang sangat mendalam. Risiko yang paling sering dihadapi mahasiswa di ranah ini adalah kesulitan adaptasi dengan industri yang berubah cepat akibat disrupsi AI (Artificial Intelligence). Kompetitor Anda ke depan bukan hanya sesama manusia, melainkan juga algoritma mesin. Oleh karena itu, prospek kerja Anda bergantung sepenuhnya pada seberapa mahir Anda memadukan empati manusia, kreativitas out-of-the-box, dan pemahaman budaya—hal-hal yang tidak bisa direplikasi oleh AI. Lulusan yang mampu beradaptasi akan memonopoli sektor industri kreatif, hubungan internasional, jurnalisme investigatif, serta perumusan kebijakan publik (public policy) tingkat nasional.");
        }
        
        sb.append(" Kesimpulannya: Anda sedang mengambil jalan yang penuh risiko (high risk), namun menyimpan potensi keuntungan luar biasa (astronomical reward). Siapkan mental baja, karena pertarungan sesungguhnya baru akan dimulai setelah Anda menerima surat kelulusan UTBK.");
        
        return sb.toString();
    }

    private static String generatePeluangAndCareerAnalysis(String jurusanNama, String rumpun, double bestPeluang, String bestUnivName, String bestStatus) {
        StringBuilder sb = new StringBuilder();
        sb.append("Rasionalisasi Kalkulasi Peluang:\n");
        sb.append("Peluang personal yang tertera pada tabel di atas dihitung menggunakan algoritma deterministik yang membandingkan Estimasi Poin UTBK rasional Anda dengan data historis Passing Grade SNBT nasional. ");
        sb.append("Semakin besar defisit poin Anda, semakin kecil peluang kelulusan (eksponensial), dan sebaliknya. Variabel akreditasi prodi dan tingkat persaingan regional juga menjadi faktor pembobot tersembunyi.\n\n");
        
        sb.append("Analisis Peluang Tertinggi (Safety Net):\n");
        sb.append("Berdasarkan data saat ini, institusi dengan peluang kelulusan paling realistis bagi Anda adalah ").append(bestUnivName).append(" dengan probabilitas tembus ").append(String.format(Locale.US, "%.1f", bestPeluang)).append("% (Kategori: ").append(bestStatus).append("). ");
        
        sb.append("Jika Anda menjadikan kampus ini sebagai pelabuhan akhir (safety net), ini adalah langkah taktis yang sangat cerdas. Anda dapat menghindari persaingan berdarah di Top Nasional tanpa harus mengorbankan kualitas pendidikan (asumsi akreditasi prodi tetap memadai). ");
        
        sb.append("Di masa depan, saat Anda melamar pekerjaan, portofolio skill, sertifikasi, dan pengalaman magang akan jauh lebih dilihat oleh HRD dibandingkan sekadar almamater semata. Lulusan ").append(jurusanNama).append(" dari ").append(bestUnivName).append(" diproyeksikan memiliki traksi yang kuat untuk mengisi lowongan ");
        
        if (rumpun != null && (rumpun.contains("Sains") || rumpun.contains("Teknologi") || rumpun.contains("Teknik") || rumpun.contains("Sistem"))) {
            sb.append("sebagai Data Engineer, Software Architect, System Analyst, atau R&D Specialist di perusahaan manufaktur dan startup unicorn. Gajinya sangat kompetitif di bursa tenaga kerja.");
        } else if (rumpun != null && (rumpun.contains("Bisnis") || rumpun.contains("Ekonomi") || rumpun.contains("Manajemen"))) {
            sb.append("di sektor perbankan (Officer Development Program), FMCG (Fast Moving Consumer Goods), analis keuangan, atau Business Development Executive. Jalur akselerasi seperti Management Trainee juga sangat terbuka jika Anda memiliki TOEFL di atas 550 dan pengalaman organisasi eksekutif kampus.");
        } else if (rumpun != null && (rumpun.contains("Kesehatan") || rumpun.contains("Medis") || rumpun.contains("Keperawatan"))) {
            sb.append("di rumah sakit rujukan nasional, klinik spesialis, kementerian kesehatan, atau peneliti klinis di industri farmasi. Kebutuhan tenaga medis pasca-pandemi tidak akan pernah surut (recession-proof career).");
        } else {
            sb.append("di kementerian negara, korporasi multinasional, media massa tingkat nasional, firma konsultan politik, atau sebagai spesialis komunikasi korporat (Corporate Communication). Kemampuan literasi dan public speaking Anda adalah komoditas bernilai tinggi yang diincar korporasi.");
        }
        
        sb.append(" Saran Taktis Terakhir: Jika status peluang Anda masih 'Berisiko' atau 'Sulit' bahkan di institusi dengan peluang tertinggi ini, Anda WAJIB MENGUBAH TOTAL kebiasaan belajar Anda. Jangan menunda lagi.");
        
        return sb.toString();
    }

    private static void addSectionTitle(Document doc, String title) throws DocumentException {
        Paragraph p = new Paragraph(title, FontFactory.getFont(FontFactory.HELVETICA_BOLD, 14, COLOR_PRIMARY));
        p.setSpacingBefore(20);
        p.setSpacingAfter(5);
        doc.add(p);
        LineSeparator line = new LineSeparator(2, 100, BaseColor.LIGHT_GRAY, Element.ALIGN_CENTER, -2);
        doc.add(new Chunk(line));
        doc.add(new Paragraph("\n"));
    }

    private static void addInfoRow(PdfPTable table, String key, String value) {
        PdfPCell cell1 = new PdfPCell(new Phrase(key, FONT_BOLD));
        cell1.setBorder(Rectangle.NO_BORDER);
        cell1.setPadding(6);
        table.addCell(cell1);
        
        PdfPCell cell2 = new PdfPCell(new Phrase(": " + value, FONT_NORMAL));
        cell2.setBorder(Rectangle.NO_BORDER);
        cell2.setPadding(6);
        table.addCell(cell2);
    }

    private static void addCell(PdfPTable table, String text, Font font, BaseColor bg) {
        PdfPCell cell = new PdfPCell(new Phrase(text, font));
        cell.setBackgroundColor(bg);
        cell.setPadding(8);
        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
        cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
        table.addCell(cell);
    }
}