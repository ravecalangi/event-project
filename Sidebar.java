package attendance;

import javax.swing.*;
import java.awt.*;

// Left-side navigation panel shared by all dashboards
class Sidebar extends JPanel {

    Sidebar() {
        setBackground(Theme.PRIMARY);
        setPreferredSize(new Dimension(220, 0));
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setBorder(BorderFactory.createEmptyBorder(24, 0, 24, 0));
    }

    JButton addNavItem(String label) {
        JButton b = new JButton(label) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                if (getModel().isRollover() || getModel().isSelected()) {
                    g2.setColor(new Color(0x4361EE));
                    g2.fillRoundRect(8, 2, getWidth()-16, getHeight()-4, 8, 8);
                }
                g2.dispose();
                super.paintComponent(g);
            }
        };
        b.setFont(Theme.FONT_BTN);
        b.setForeground(new Color(0xCBD5E1));
        b.setOpaque(false);
        b.setContentAreaFilled(false);
        b.setBorderPainted(false);
        b.setFocusPainted(false);
        b.setHorizontalAlignment(SwingConstants.LEFT);
        b.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        b.setMaximumSize(new Dimension(Integer.MAX_VALUE, 44));
        return b;
    }

    void addLogo(String title, String subtitle) {
        JLabel t = new JLabel(title);
        t.setFont(new Font("Segoe UI", Font.BOLD, 13));
        t.setForeground(Color.WHITE);
        t.setAlignmentX(Component.CENTER_ALIGNMENT);
        t.setBorder(BorderFactory.createEmptyBorder(0, 20, 2, 20));

        JLabel s = new JLabel(subtitle);
        s.setFont(Theme.FONT_SMALL);
        s.setForeground(new Color(0x94A3B8));
        s.setAlignmentX(Component.CENTER_ALIGNMENT);
        s.setBorder(BorderFactory.createEmptyBorder(0, 20, 20, 20));

        add(t);
        add(s);

        JSeparator sep = new JSeparator();
        sep.setForeground(new Color(0x334155));
        sep.setMaximumSize(new Dimension(Integer.MAX_VALUE, 1));
        add(sep);
        add(Box.createVerticalStrut(12));
    }
}