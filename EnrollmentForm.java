package attendance;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;

// Enrollment management form — enroll or remove students from subjects
class EnrollmentForm extends JFrame {

    private JComboBox<String> cmbStudent, cmbSubject;
    private JButton           btnEnroll, btnDelete, btnClose;
    private JTable            tblEnrollments;
    private DefaultTableModel tableModel;

    public EnrollmentForm() {
        setTitle("Enrollment Management");
        setSize(820, 620);
        setMinimumSize(new Dimension(620, 480));
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(true);
        setLayout(new BorderLayout());

        JPanel main = new JPanel(new BorderLayout(0, 16));
        main.setBackground(Theme.BG);
        main.setBorder(BorderFactory.createEmptyBorder(24, 24, 24, 24));

        JPanel titleRow = new JPanel(new BorderLayout());
        titleRow.setOpaque(false);
        titleRow.add(UI.heading("Enrollment Management"), BorderLayout.WEST);
        btnClose = UI.btnGhost("Close");
        titleRow.add(btnClose, BorderLayout.EAST);
        main.add(titleRow, BorderLayout.NORTH);

        JPanel formCard = UI.card();
        formCard.setLayout(new GridBagLayout());
        formCard.setBorder(BorderFactory.createEmptyBorder(18, 20, 16, 20));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets  = new Insets(4, 6, 4, 6);
        gbc.fill    = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1;

        cmbStudent = UI.comboBox();
        cmbSubject = UI.comboBox();

        gbc.gridx = 0; gbc.gridy = 0; formCard.add(UI.label("Student"), gbc);
        gbc.gridx = 1;               formCard.add(UI.label("Subject"), gbc);
        gbc.gridy = 1;
        gbc.gridx = 0; formCard.add(cmbStudent, gbc);
        gbc.gridx = 1; formCard.add(cmbSubject, gbc);

        btnEnroll = UI.btnPrimary("Enroll");
        btnDelete = UI.btnDanger("Remove");

        JPanel btnRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 0));
        btnRow.setOpaque(false);
        btnRow.add(btnEnroll);
        btnRow.add(btnDelete);

        gbc.gridx = 0; gbc.gridy = 2; gbc.gridwidth = 2;
        gbc.insets = new Insets(12, 6, 0, 6);
        formCard.add(btnRow, gbc);

        tableModel = new DefaultTableModel(new String[]{"ID", "Student", "Subject"}, 0) {
            public boolean isCellEditable(int r, int c) { return false; }
        };
        tblEnrollments = new JTable(tableModel);
        UI.styleTable(tblEnrollments);
        JScrollPane sp = UI.scrollPane(tblEnrollments);
        sp.setPreferredSize(new Dimension(0, 340));

        gbc.gridx = 0; gbc.gridy = 3; gbc.gridwidth = 2;
        gbc.insets  = new Insets(16, 6, 0, 6);
        gbc.fill    = GridBagConstraints.BOTH;
        gbc.weighty = 1.0;
        formCard.add(sp, gbc);

        JPanel center = new JPanel(new BorderLayout());
        center.setOpaque(false);
        center.setBorder(BorderFactory.createEmptyBorder(12, 0, 0, 0));
        center.add(formCard, BorderLayout.CENTER);
        main.add(center, BorderLayout.CENTER);
        add(main);

        btnEnroll.addActionListener(e -> enrollStudent());
        btnDelete.addActionListener(e -> removeEnrollment());
        btnClose.addActionListener(e -> dispose());

        loadStudentCombo();
        loadSubjectCombo();
        loadEnrollments();
    }

    void loadStudentCombo() {
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement("SELECT id, full_name FROM users WHERE role='student'");
             ResultSet rs = ps.executeQuery()) {
            cmbStudent.removeAllItems();
            while (rs.next()) cmbStudent.addItem(rs.getInt("id") + " - " + rs.getString("full_name"));
        } catch (Exception e) { JOptionPane.showMessageDialog(this, "Error: " + e.getMessage()); }
    }

    void loadSubjectCombo() {
        try (Connection con = DBConnection.getConnection();
             Statement st = con.createStatement();
             ResultSet rs = st.executeQuery("SELECT id, subject_name FROM subjects")) {
            cmbSubject.removeAllItems();
            while (rs.next()) cmbSubject.addItem(rs.getInt("id") + " - " + rs.getString("subject_name"));
        } catch (Exception e) { JOptionPane.showMessageDialog(this, "Error: " + e.getMessage()); }
    }

    void loadEnrollments() {
        try (Connection con = DBConnection.getConnection();
             Statement st = con.createStatement();
             ResultSet rs = st.executeQuery(
                 "SELECT e.id, u.full_name, s.subject_name FROM enrollments e " +
                 "JOIN users u ON e.student_id = u.id JOIN subjects s ON e.subject_id = s.id")) {
            tableModel.setRowCount(0);
            while (rs.next())
                tableModel.addRow(new Object[]{rs.getInt("id"), rs.getString("full_name"), rs.getString("subject_name")});
        } catch (Exception e) { JOptionPane.showMessageDialog(this, "Error: " + e.getMessage()); }
    }

    void enrollStudent() {
        if (cmbStudent.getSelectedItem() == null || cmbSubject.getSelectedItem() == null) {
            JOptionPane.showMessageDialog(this, "Please select a student and a subject.", "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }
        int studentId = Integer.parseInt(cmbStudent.getSelectedItem().toString().split(" - ")[0]);
        int subjectId = Integer.parseInt(cmbSubject.getSelectedItem().toString().split(" - ")[0]);

        try (Connection con = DBConnection.getConnection()) {
            // Check for duplicate enrollment
            try (PreparedStatement check = con.prepareStatement(
                     "SELECT id FROM enrollments WHERE student_id=? AND subject_id=?")) {
                check.setInt(1, studentId);
                check.setInt(2, subjectId);
                try (ResultSet rs = check.executeQuery()) {
                    if (rs.next()) {
                        JOptionPane.showMessageDialog(this,
                            "This student is already enrolled in the selected subject.",
                            "Duplicate Entry", JOptionPane.WARNING_MESSAGE);
                        return;
                    }
                }
            }
            try (PreparedStatement ps = con.prepareStatement(
                     "INSERT INTO enrollments (student_id, subject_id) VALUES (?, ?)")) {
                ps.setInt(1, studentId);
                ps.setInt(2, subjectId);
                ps.executeUpdate();
            }
            JOptionPane.showMessageDialog(this, "Student enrolled successfully!");
            loadEnrollments();
        } catch (Exception e) { JOptionPane.showMessageDialog(this, "Error: " + e.getMessage()); }
    }

    void removeEnrollment() {
        int row = tblEnrollments.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Please select an enrollment to remove.", "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }
        int id = (int) tableModel.getValueAt(row, 0);
        int confirm = JOptionPane.showConfirmDialog(this,
            "Are you sure you want to remove this enrollment?", "Confirm Remove", JOptionPane.YES_NO_OPTION);
        if (confirm == 0) {
            try (Connection con = DBConnection.getConnection();
                 PreparedStatement ps = con.prepareStatement("DELETE FROM enrollments WHERE id=?")) {
                ps.setInt(1, id);
                ps.executeUpdate();
                JOptionPane.showMessageDialog(this, "Enrollment removed successfully!");
                loadEnrollments();
            } catch (Exception e) { JOptionPane.showMessageDialog(this, "Error: " + e.getMessage()); }
        }
    }
}