package attendance;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

// Faculty dashboard — view enrolled students and mark attendance
class FacultyDashboard extends JFrame {

    int loggedInId;
    private JLabel            lblWelcome;
    private JComboBox<String> cmbSubject, cmbStatus;
    private JButton           btnMark, btnLogout;
    private JTable            tblStudents;
    private DefaultTableModel tableModel;

    public FacultyDashboard(int userId) {
        this.loggedInId = userId;
        setTitle("Faculty Dashboard — Attendance System");
        setSize(960, 700);
        setMinimumSize(new Dimension(720, 520));
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(true);
        setLayout(new BorderLayout());

        Sidebar sidebar = new Sidebar();
        sidebar.addLogo("Attendance System", "Faculty Panel");
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
        JLabel sub = UI.label("Mark student attendance for your subjects");
        JPanel titlePanel = new JPanel();
        titlePanel.setOpaque(false);
        titlePanel.setLayout(new BoxLayout(titlePanel, BoxLayout.Y_AXIS));
        titlePanel.add(lblWelcome);
        titlePanel.add(Box.createVerticalStrut(2));
        titlePanel.add(sub);
        main.add(titlePanel, BorderLayout.NORTH);

        JPanel ctrlCard = UI.card();
        ctrlCard.setLayout(new GridBagLayout());
        ctrlCard.setBorder(BorderFactory.createEmptyBorder(18, 20, 18, 20));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets  = new Insets(4, 6, 4, 6);
        gbc.fill    = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1;

        cmbSubject = UI.comboBox();
        cmbStatus  = UI.comboBox(new String[]{"present", "late", "absent"});
        btnMark    = UI.btnPrimary("Mark Attendance");

        gbc.gridx = 0; gbc.gridy = 0; ctrlCard.add(UI.label("Subject"), gbc);
        gbc.gridx = 1;               ctrlCard.add(UI.label("Status"),  gbc);
        gbc.gridx = 2; gbc.weightx = 0.5; ctrlCard.add(new JLabel(""), gbc);
        gbc.gridy = 1; gbc.weightx = 1;
        gbc.gridx = 0; ctrlCard.add(cmbSubject, gbc);
        gbc.gridx = 1; ctrlCard.add(cmbStatus,  gbc);
        gbc.gridx = 2; gbc.weightx = 0.5; ctrlCard.add(btnMark, gbc);

        tableModel = new DefaultTableModel(new String[]{"ID", "Student Name"}, 0) {
            public boolean isCellEditable(int r, int c) { return false; }
        };
        tblStudents = new JTable(tableModel);
        UI.styleTable(tblStudents);
        JScrollPane sp = UI.scrollPane(tblStudents);

        JPanel tableCard = UI.card();
        tableCard.setLayout(new BorderLayout(0, 10));
        tableCard.setBorder(BorderFactory.createEmptyBorder(16, 16, 16, 16));
        tableCard.add(UI.heading("Enrolled Students"), BorderLayout.NORTH);
        tableCard.add(sp, BorderLayout.CENTER);

        JPanel center = new JPanel(new BorderLayout(0, 14));
        center.setOpaque(false);
        center.setBorder(BorderFactory.createEmptyBorder(16, 0, 0, 0));
        center.add(ctrlCard, BorderLayout.NORTH);
        center.add(tableCard, BorderLayout.CENTER);
        main.add(center, BorderLayout.CENTER);
        add(main, BorderLayout.CENTER);

        btnLogout.addActionListener(e -> { new LoginForm().setVisible(true); dispose(); });
        btnMark.addActionListener(e -> markAttendance());
        cmbSubject.addActionListener(e -> loadStudents());

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
             PreparedStatement ps = con.prepareStatement("SELECT subject_name FROM subjects WHERE faculty_id=?")) {
            ps.setInt(1, loggedInId);
            try (ResultSet rs = ps.executeQuery()) {
                cmbSubject.removeAllItems();
                while (rs.next()) cmbSubject.addItem(rs.getString("subject_name"));
            }
        } catch (Exception e) { JOptionPane.showMessageDialog(this, "Error: " + e.getMessage()); }
    }

    void loadStudents() {
        if (cmbSubject.getSelectedItem() == null) return;
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(
                 "SELECT u.id, u.full_name FROM users u " +
                 "JOIN enrollments e ON u.id = e.student_id " +
                 "JOIN subjects s ON e.subject_id = s.id " +
                 "WHERE s.subject_name=? AND u.role='student'")) {
            ps.setString(1, cmbSubject.getSelectedItem().toString());
            try (ResultSet rs = ps.executeQuery()) {
                tableModel.setRowCount(0);
                while (rs.next())
                    tableModel.addRow(new Object[]{rs.getInt("id"), rs.getString("full_name")});
            }
        } catch (Exception e) { JOptionPane.showMessageDialog(this, "Error: " + e.getMessage()); }
    }

    void markAttendance() {
        int row = tblStudents.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Please select a student first.", "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }
        int studentId  = (int) tableModel.getValueAt(row, 0);
        String timeIn24 = LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss"));
        String timeIn12 = LocalTime.now().format(DateTimeFormatter.ofPattern("hh:mm:ss a"));

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(
                 "INSERT INTO attendance (student_id, subject_id, date, time_in, status) " +
                 "VALUES (?, (SELECT id FROM subjects WHERE subject_name=?), CURDATE(), ?, ?)")) {
            ps.setInt(1, studentId);
            ps.setString(2, cmbSubject.getSelectedItem().toString());
            ps.setString(3, timeIn24);
            ps.setString(4, cmbStatus.getSelectedItem().toString());
            ps.executeUpdate();
            JOptionPane.showMessageDialog(this, "Attendance marked successfully!  Time: " + timeIn12);
        } catch (Exception e) { JOptionPane.showMessageDialog(this, "Error: " + e.getMessage()); }
    }
}