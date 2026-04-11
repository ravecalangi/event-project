package attendance;

import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;

// Factory methods for creating styled UI components
class UI {

    static JButton button(String text, Color bg, Color fg) {
        JButton b = new JButton(text) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                if (getModel().isPressed()) g2.setColor(bg.darker());
                else if (getModel().isRollover()) g2.setColor(bg.brighter());
                else g2.setColor(bg);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        b.setFont(Theme.FONT_BTN);
        b.setForeground(fg);
        b.setBackground(bg);
        b.setOpaque(false);
        b.setContentAreaFilled(false);
        b.setBorderPainted(false);
        b.setFocusPainted(false);
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        b.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        return b;
    }

    static JButton btnPrimary(String text) { return button(text, Theme.ACCENT, Color.WHITE); }
    static JButton btnDanger(String text)  { return button(text, Theme.DANGER, Color.WHITE); }
    static JButton btnDark(String text)    { return button(text, Theme.PRIMARY, Color.WHITE); }
    static JButton btnGhost(String text)   { return button(text, Theme.BORDER, Theme.TEXT); }

    static JTextField textField(int cols) {
        JTextField f = new JTextField(cols);
        f.setFont(Theme.FONT_BODY);
        f.setForeground(Theme.TEXT);
        f.setBorder(BorderFactory.createCompoundBorder(
            new RoundedBorder(8, Theme.BORDER),
            BorderFactory.createEmptyBorder(8, 12, 8, 12)
        ));
        f.setBackground(Theme.CARD);
        return f;
    }

    static JPasswordField passField(int cols) {
        JPasswordField f = new JPasswordField(cols);
        f.setFont(Theme.FONT_BODY);
        f.setForeground(Theme.TEXT);
        f.setBorder(BorderFactory.createCompoundBorder(
            new RoundedBorder(8, Theme.BORDER),
            BorderFactory.createEmptyBorder(8, 12, 8, 12)
        ));
        f.setBackground(Theme.CARD);
        return f;
    }

    static <T> JComboBox<T> comboBox(T[] items) {
        JComboBox<T> c = new JComboBox<>(items);
        c.setFont(Theme.FONT_BODY);
        c.setBackground(Theme.CARD);
        c.setForeground(Theme.TEXT);
        c.setBorder(new RoundedBorder(8, Theme.BORDER));
        return c;
    }

    static <T> JComboBox<T> comboBox() {
        JComboBox<T> c = new JComboBox<>();
        c.setFont(Theme.FONT_BODY);
        c.setBackground(Theme.CARD);
        c.setForeground(Theme.TEXT);
        c.setBorder(new RoundedBorder(8, Theme.BORDER));
        return c;
    }

    static JLabel label(String text) {
        JLabel l = new JLabel(text);
        l.setFont(Theme.FONT_LABEL);
        l.setForeground(Theme.TEXT_MUTED);
        return l;
    }

    static JLabel heading(String text) {
        JLabel l = new JLabel(text);
        l.setFont(Theme.FONT_H2);
        l.setForeground(Theme.TEXT);
        return l;
    }

    static void styleTable(JTable t) {
        t.setFont(Theme.FONT_TABLE);
        t.setRowHeight(36);
        t.setShowGrid(false);
        t.setIntercellSpacing(new Dimension(0, 0));
        t.setSelectionBackground(new Color(0xDDE3FC));
        t.setSelectionForeground(Theme.TEXT);
        t.setBackground(Theme.CARD);
        t.setForeground(Theme.TEXT);
        t.setFillsViewportHeight(true);

        t.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                    boolean isSelected, boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                if (!isSelected) {
                    c.setBackground(row % 2 == 0 ? Theme.CARD : Theme.ROW_ALT);
                    c.setForeground(Theme.TEXT);
                } else {
                    c.setBackground(new Color(0xDDE3FC));
                    c.setForeground(Theme.TEXT);
                }
                setBorder(BorderFactory.createEmptyBorder(0, 12, 0, 12));
                return c;
            }
        });

        JTableHeader header = t.getTableHeader();
        header.setFont(Theme.FONT_TH);
        header.setBackground(Theme.HEADER_BG);
        header.setForeground(Theme.HEADER_FG);
        header.setPreferredSize(new Dimension(header.getWidth(), 38));
        header.setBorder(BorderFactory.createEmptyBorder());

        header.setDefaultRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                    boolean isSelected, boolean hasFocus, int row, int column) {
                JLabel l = (JLabel) super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                l.setBackground(Theme.HEADER_BG);
                l.setForeground(Theme.HEADER_FG);
                l.setFont(Theme.FONT_TH);
                l.setBorder(BorderFactory.createEmptyBorder(0, 12, 0, 12));
                l.setOpaque(true);
                return l;
            }
        });
    }

    static JScrollPane scrollPane(JTable t) {
        JScrollPane sp = new JScrollPane(t);
        sp.setBorder(new RoundedBorder(10, Theme.BORDER));
        sp.setBackground(Theme.CARD);
        sp.getViewport().setBackground(Theme.CARD);
        return sp;
    }

    static JPanel card() {
        JPanel p = new JPanel() {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(Theme.CARD);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 16, 16);
                g2.setColor(Theme.BORDER);
                g2.setStroke(new BasicStroke(1f));
                g2.drawRoundRect(0, 0, getWidth()-1, getHeight()-1, 16, 16);
                g2.dispose();
            }
        };
        p.setOpaque(false);
        return p;
    }
}