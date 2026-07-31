package com.attendance.util;

import javax.swing.*;
import javax.swing.table.TableModel;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

/**
 * Utility for exporting JTable or data structures to CSV files.
 */
public class CSVExporter {

    public static boolean exportTableToCSV(JTable table, File file) {
        try (FileWriter writer = new FileWriter(file)) {
            TableModel model = table.getModel();
            int colCount = model.getColumnCount();
            int rowCount = model.getRowCount();

            // Write Headers
            for (int col = 0; col < colCount; col++) {
                writer.write(escapeCSV(model.getColumnName(col)));
                if (col < colCount - 1) writer.write(",");
            }
            writer.write("\n");

            // Write Rows
            for (int row = 0; row < rowCount; row++) {
                for (int col = 0; col < colCount; col++) {
                    Object val = model.getValueAt(row, col);
                    writer.write(escapeCSV(val != null ? val.toString() : ""));
                    if (col < colCount - 1) writer.write(",");
                }
                writer.write("\n");
            }

            writer.flush();
            return true;
        } catch (IOException e) {
            System.err.println("CSV Export Error: " + e.getMessage());
            return false;
        }
    }

    private static String escapeCSV(String data) {
        if (data == null) return "";
        String escapedData = data.replaceAll("\"", "\"\"");
        if (escapedData.contains(",") || escapedData.contains("\n") || escapedData.contains("\"")) {
            escapedData = "\"" + escapedData + "\"";
        }
        return escapedData;
    }
}
