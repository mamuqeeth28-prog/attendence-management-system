package com.attendance.util;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.JTableHeader;
import java.awt.*;

/**
 * Modern Swing UI Utility class providing styling, components, cards, and theme colors.
 */
public class UIUtils {

    // Palette Colors
    public static final Color PRIMARY = new Color(37, 99, 235);      // Vibrant Blue
    public static final Color PRIMARY_DARK = new Color(29, 78, 216); // Darker Blue
    public static final Color SECONDARY = new Color(71, 85, 105);    // Slate Gray
    public static final Color SUCCESS = new Color(16, 185, 129);     // Emerald Green
    public static final Color DANGER = new Color(239, 68, 68);       // Crimson Red
    public static final Color WARNING = new Color(245, 158, 11);     // Amber Yellow
    public static final Color INFO = new Color(6, 182, 212);         // Cyan
    
    public static final Color BG_LIGHT = new Color(248, 250, 252);   // Background Light Slate
    public static final Color CARD_BG = Color.WHITE;
    public static final Color TEXT_DARK = new Color(15, 23, 42);
    public static final Color TEXT_MUTED = new Color(100, 116, 139);

    // Modern Fonts
    public static final Font FONT_TITLE = new Font("Segoe UI", Font.BOLD, 22);
    public static final Font FONT_SUBTITLE = new Font("Segoe UI", Font.BOLD, 16);
    public static final Font FONT_BOLD = new Font("Segoe UI", Font.BOLD, 13);
    public static final Font FONT_REGULAR = new Font("Segoe UI", Font.PLAIN, 13);
    public static final Font FONT_CARD_VALUE = new Font("Segoe UI", Font.BOLD, 26);

    /**
     * Creates a styled modern button.
     */
    public static JButton createButton(String text, Color bgColor, Color fgColor) {
        JButton button = new JButton(text);
        button.setFont(FONT_BOLD);
        button.setBackground(bgColor);
        button.setForeground(fgColor);
        button.setFocusPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setMargin(new Insets(8, 16, 8, 16));
        button.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(bgColor.darker(), 1, true),
                BorderFactory.createEmptyBorder(6, 12, 6, 12)
        ));
        return button;
    }

    /**
     * Creates a card panel with a subtle border and background color.
     */
    public static JPanel createCardPanel() {
        JPanel panel = new JPanel();
        panel.setBackground(CARD_BG);
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(226, 232, 240), 1, true),
                new EmptyBorder(15, 20, 15, 20)
        ));
        return panel;
    }

    /**
     * Creates a metric summary card with icon symbol, title, and big numbers.
     */
    public static JPanel createStatCard(String title, String initialValue, Color accentColor, String symbol) {
        JPanel card = createCardPanel();
        card.setLayout(new BorderLayout(10, 5));

        JLabel lblSymbol = new JLabel(symbol);
        lblSymbol.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 28));
        lblSymbol.setForeground(accentColor);

        JPanel textPanel = new JPanel();
        textPanel.setOpaque(false);
        textPanel.setLayout(new BoxLayout(textPanel, BoxLayout.Y_AXIS));

        JLabel lblTitle = new JLabel(title.toUpperCase());
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 11));
        lblTitle.setForeground(TEXT_MUTED);

        JLabel lblValue = new JLabel(initialValue);
        lblValue.setFont(FONT_CARD_VALUE);
        lblValue.setForeground(accentColor);
        lblValue.setName("cardValueLabel");

        textPanel.add(lblTitle);
        textPanel.add(Box.createVerticalStrut(4));
        textPanel.add(lblValue);

        card.add(lblSymbol, BorderLayout.WEST);
        card.add(textPanel, BorderLayout.CENTER);

        return card;
    }

    /**
     * Style a JTable with sleek header and alternating row colors.
     */
    public static void styleTable(JTable table) {
        table.setFont(FONT_REGULAR);
        table.setRowHeight(32);
        table.setGridColor(new Color(241, 245, 249));
        table.setSelectionBackground(new Color(224, 242, 254));
        table.setSelectionForeground(TEXT_DARK);
        table.setShowHorizontalLines(true);
        table.setShowVerticalLines(false);

        JTableHeader header = table.getTableHeader();
        header.setFont(FONT_BOLD);
        header.setBackground(new Color(241, 245, 249));
        header.setForeground(TEXT_DARK);
        header.setReorderingAllowed(false);
        header.setPreferredSize(new Dimension(0, 38));

        // Center alignment for status and action columns
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(SwingConstants.CENTER);
        
        for (int i = 0; i < table.getColumnCount(); i++) {
            if (table.getColumnName(i).toLowerCase().contains("status") || 
                table.getColumnName(i).toLowerCase().contains("gender") ||
                table.getColumnName(i).toLowerCase().contains("date")) {
                table.getColumnModel().getColumn(i).setCellRenderer(centerRenderer);
            }
        }
    }

    /**
     * Shows a styled alert dialog.
     */
    public static void showInfoDialog(Component parent, String message, String title) {
        JOptionPane.showMessageDialog(parent, message, title, JOptionPane.INFORMATION_MESSAGE);
    }

    public static void showErrorDialog(Component parent, String message, String title) {
        JOptionPane.showMessageDialog(parent, message, title, JOptionPane.ERROR_MESSAGE);
    }

    public static boolean showConfirmDialog(Component parent, String message, String title) {
        int result = JOptionPane.showConfirmDialog(parent, message, title, JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
        return result == JOptionPane.YES_OPTION;
    }
}
