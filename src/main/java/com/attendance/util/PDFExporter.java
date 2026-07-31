package com.attendance.util;

import com.lowagie.text.*;
import com.lowagie.text.pdf.*;

import javax.swing.JTable;
import javax.swing.table.TableModel;
import java.awt.Color;
import java.io.File;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Utility for exporting reports to styled PDF documents using OpenPDF.
 */
public class PDFExporter {

    public static boolean exportTableToPDF(JTable table, String reportTitle, File file) {
        Document document = new Document(PageSize.A4.rotate(), 36, 36, 36, 36);
        try {
            PdfWriter.getInstance(document, new FileOutputStream(file));
            document.open();

            // Font configurations
            Font titleFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 18, com.lowagie.text.Font.BOLD, new Color(37, 99, 235));
            Font subTitleFont = FontFactory.getFont(FontFactory.HELVETICA, 10, com.lowagie.text.Font.ITALIC, Color.GRAY);
            Font headerFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 10, com.lowagie.text.Font.BOLD, Color.WHITE);
            Font cellFont = FontFactory.getFont(FontFactory.HELVETICA, 9, com.lowagie.text.Font.NORMAL, Color.BLACK);

            // Document Header
            Paragraph title = new Paragraph("ATTENDANCE MANAGEMENT SYSTEM", titleFont);
            title.setAlignment(Element.ALIGN_CENTER);
            document.add(title);

            Paragraph reportSub = new Paragraph("Report: " + reportTitle, FontFactory.getFont(FontFactory.HELVETICA_BOLD, 14, Color.DARK_GRAY));
            reportSub.setAlignment(Element.ALIGN_CENTER);
            reportSub.setSpacingAfter(5);
            document.add(reportSub);

            String dateStr = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
            Paragraph timestamp = new Paragraph("Generated on: " + dateStr, subTitleFont);
            timestamp.setAlignment(Element.ALIGN_CENTER);
            timestamp.setSpacingAfter(15);
            document.add(timestamp);

            // Table Creation
            TableModel model = table.getModel();
            int colCount = model.getColumnCount();
            int rowCount = model.getRowCount();

            PdfPTable pdfTable = new PdfPTable(colCount);
            pdfTable.setWidthPercentage(100);
            pdfTable.setSpacingBefore(10f);
            pdfTable.setSpacingAfter(10f);

            // Add Header Cells
            for (int i = 0; i < colCount; i++) {
                PdfPCell headerCell = new PdfPCell(new Phrase(model.getColumnName(i), headerFont));
                headerCell.setBackgroundColor(new Color(37, 99, 235));
                headerCell.setHorizontalAlignment(Element.ALIGN_CENTER);
                headerCell.setPadding(6);
                pdfTable.addCell(headerCell);
            }

            // Add Data Cells
            for (int row = 0; row < rowCount; row++) {
                for (int col = 0; col < colCount; col++) {
                    Object val = model.getValueAt(row, col);
                    String strVal = (val != null) ? val.toString() : "";
                    PdfPCell cell = new PdfPCell(new Phrase(strVal, cellFont));
                    cell.setPadding(5);

                    // Alternating Row Colors
                    if (row % 2 == 1) {
                        cell.setBackgroundColor(new Color(245, 247, 250));
                    }

                    // Highlight status values
                    if ("Present".equalsIgnoreCase(strVal)) {
                        cell.setPhrase(new Phrase(strVal, FontFactory.getFont(FontFactory.HELVETICA_BOLD, 9, new Color(16, 185, 129))));
                    } else if ("Absent".equalsIgnoreCase(strVal)) {
                        cell.setPhrase(new Phrase(strVal, FontFactory.getFont(FontFactory.HELVETICA_BOLD, 9, new Color(239, 68, 68))));
                    } else if ("Late".equalsIgnoreCase(strVal)) {
                        cell.setPhrase(new Phrase(strVal, FontFactory.getFont(FontFactory.HELVETICA_BOLD, 9, new Color(245, 158, 11))));
                    }

                    pdfTable.addCell(cell);
                }
            }

            document.add(pdfTable);

            // Footer
            Paragraph footer = new Paragraph("End of Report", subTitleFont);
            footer.setAlignment(Element.ALIGN_CENTER);
            footer.setSpacingBefore(20);
            document.add(footer);

            document.close();
            return true;
        } catch (Exception e) {
            System.err.println("PDF Export Error: " + e.getMessage());
            return false;
        }
    }
}
