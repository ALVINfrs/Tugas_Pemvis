package com.sistempakar;
import net.sf.jasperreports.engine.JasperCompileManager;
public class TestJRXML {
    public static void main(String[] args) {
        try {
            JasperCompileManager.compileReportToFile("src/main/resources/reports/DetailRaporSiswa.jrxml", "src/main/resources/reports/DetailRaporSiswa.jasper");
            System.out.println("JRXML compiled successfully!");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
