package attendance;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

// Student dashboard — view personal attendance records filtered by subject
class StudentDashboard extends JFrame {

    int loggedInId;
    private JLabel            lblWelcome;
    private JComboBox<String> cmbSubject;
    private JButton           btnLogout;
    private JTable            tblAttendance;
    private DefaultTableModel tableModel;

    public StudentDashboard(int userId) {
        this.loggedInId = userId;
        setTitle("Student Dashboard — Attendance System");
        setSize(960, 700);
        setMinimumSize(new Dimension(720, 520));
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(true);
        setLayout(new BorderLayout());

        Sidebar sidebar = new Sidebar();
        sidebar.addLogo("Attendance System", "Student Portal");
        sidebar.add(Box.createVerticalGlue());
        btnLogout = sidebar.addNavItem("Logout");
        btnLogout.setForeground(new Color(0xFCA5A5));
        sidebar.add(btnLogout);
        add(sidebar, BorderLayout.WEST);

        JPanel main = new JPanel(new BorderLayout(0, 16));
        main.setBackground(Theme.BG);
        main.setBorder(BorderFactory.createEmptyBorder(28, 28, 28, 28));

        lblWelcome = new JLabel("Welcome!");
        lblWelcome.setFont(new Font("Segoe UI", Font.BOLD, 20));
        lblWelcome.setForeground(Theme.TEXT);
        JLabel sub = UI.label("View your attendance records below");
        JPanel titlePanel = new JPanel();
        titlePanel.setOpaque(false);
        titlePanel.setLayout(new BoxLayout(titlePanel, BoxLayout.Y_AXIS));
        titlePanel.add(lblWelcome);
        titlePanel.add(Box.createVerticalStrut(2));
        titlePanel.add(sub);
        main.add(titlePanel, BorderLayout.NORTH);

        JPanel filterCard = UI.card();
        filterCard.setLayout(new GridBagLayout());
        filterCard.setBorder(BorderFactory.createEmptyBorder(16, 20, 16, 20));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets  = new Insets(4, 6, 4, 6);
        gbc.fill    = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1;

        cmbSubject = UI.comboBox();
        gbc.gridx = 0; gbc.gridy = 0; filterCard.add(UI.label("Filter by Subject"), gbc);
        gbc.gridy = 1; filterCard.add(cmbSubject, gbc);
        gbc.gridx = 1; gbc.gridy = 0; gbc.weightx = 2; filterCard.add(new JLabel(""), gbc);

        tableModel = new DefaultTableModel(new String[]{"Date", "Time In", "Subject", "Status"}, 0) {
            public boolean isCellEditable(int r, int c) { return false; }
        };
        tblAttendance = new JTable(tableModel);
        UI.styleTable(tblAttendance);

        // Color-coded status column
        tblAttendance.getColumnModel().getColumn(3).setCellRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                    boolean isSelected, boolean hasFocus, int row, int col) {
                JLabel l = (JLabel) super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, col);
                if (!isSelected) {
                    switch (String.valueOf(value).toLowerCase()) {
                        case "present": l.setForeground(new Color(0x059669)); break;
                        case "late":    l.setForeground(Theme.WARNING);       break;
                        default:        l.setForeground(Theme.DANGER);        break;
                    }
                    l.setFont(Theme.FONT_BTN);
                    l.setBackground(row % 2 == 0 ? Theme.CARD : Theme.ROW_ALT);
                }
                setBorder(BorderFactory.createEmptyBorder(0, 12, 0, 12));
                return l;
            }
        });

        JScrollPane sp = UI.scrollPane(tblAttendance);
        JPanel tableCard = UI.card();
        tableCard.setLayout(new BorderLayout(0, 10));
        tableCard.setBorder(BorderFactory.createEmptyBorder(16, 16, 16, 16));
        tableCard.add(UI.heading("Attendance Records"), BorderLayout.NORTH);
        tableCard.add(sp, BorderLayout.CENTER);

        JPanel center = new JPanel(new BorderLayout(0, 14));
        center.setOpaque(false);
        center.setBorder(BorderFactory.createEmptyBorder(16, 0, 0, 0));
        center.add(filterCard, BorderLayout.NORTH);
        center.add(tableCard, BorderLayout.CENTER);
        main.add(center, BorderLayout.CENTER);
        add(main, BorderLayout.CENTER);

        btnLogout.addActionListener(e -> { new LoginForm().setVisible(true); dispose(); });
        cmbSubject.addActionListener(e -> loadAttendance());

        loadWelcome();
        loadSubjects();
    }

    void loadWelcome() {
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement("SELECT full_name FROM users WHERE id=?")) {
            ps.setInt(1, loggedInId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) lblWelcome.setText("Welcome, " + rs.getString("full_name") + "!");
            }
        } catch (Exception e) { JOptionPane.showMessageDialog(this, "Error: " + e.getMessage()); }
    }

    void loadSubjects() {
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(
                 "SELECT s.subject_name FROM subjects s JOIN enrollments e ON s.id = e.subject_id WHERE e.student_id=?")) {
            ps.setInt(1, loggedInId);
            try (ResultSet rs = ps.executeQuery()) {
                cmbSubject.removeAllItems();
                cmbSubject.addItem("All Subjects");
                while (rs.next()) cmbSubject.addItem(rs.getString("subject_name"));
            }
        } catch (Exception e) { JOptionPane.showMessageDialog(this, "Error: " + e.getMessage()); }
    }

    void loadAttendance() {
        if (cmbSubject.getSelectedItem() == null) return;
        String selected = cmbSubject.getSelectedItem().toString();
        String sql = selected.equals("All Subjects")
            ? "SELECT a.date, a.time_in, s.subject_name, a.status FROM attendance a " +
              "JOIN subjects s ON a.subject_id = s.id WHERE a.student_id=? ORDER BY a.date DESC, a.time_in DESC"
            : "SELECT a.date, a.time_in, s.subject_name, a.status FROM attendance a " +
              "JOIN subjects s ON a.subject_id = s.id WHERE a.student_id=? AND s.subject_name=? ORDER BY a.date DESC, a.time_in DESC";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, loggedInId);
            if (!selected.equals("All Subjects")) ps.setString(2, selected);
            try (ResultSet rs = ps.executeQuery()) {
                tableModel.setRowCount(0);
                while (rs.next()) {
                    String rawTime = rs.getString("time_in");
                    String displayTime = "--";
                    if (rawTime != null) {
                        try {
                            LocalTime lt = LocalTime.parse(rawTime, DateTimeFormatter.ofPattern("HH:mm:ss"));
                            displayTime = lt.format(DateTimeFormatter.ofPattern("hh:mm:ss a"));
                        } catch (Exception ex) {
                            displayTime = rawTime;
                        }
                    }
                    tableModel.addRow(new Object[]{
                        rs.getString("date"),
                        displayTime,
                        rs.getString("subject_name"),
                        rs.getString("status")
                    });
                }
            }
        } catch (Exception e) { JOptionPane.showMessageDialog(this, "Error: " + e.getMessage()); }
    }
}