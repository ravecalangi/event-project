package attendance;

import javax.swing.*;

// Main entry point
public class Attendance {
    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel("javax.swing.plaf.nimbus.NimbusLookAndFeel");
        } catch (Exception ignored) {}

        UIManager.put("Panel.background",       new java.awt.Color(0xF8F7F4));
        UIManager.put("OptionPane.background",  new java.awt.Color(0xF8F7F4));
        UIManager.put("Button.focus",           new java.awt.Color(0,0,0,0));

        SwingUtilities.invokeLater(() -> new LoginForm().setVisible(true));
    }
}