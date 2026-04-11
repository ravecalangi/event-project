package attendance;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;

// Admin dashboard — manages users, subjects, and enrollments
class AdminDashboard extends JFrame {

    private JTextField        txtFullName, txtUsername;
    private JPasswordField    txtPassword;
    private JComboBox<String> cmbRole;
    private JButton           btnAdd, btnUpdate, btnDelete, btnLogout, btnManageSubjects, btnManageEnrollments;
    private JTable            tblUsers;
    private DefaultTableModel tableModel;

    public AdminDashboard() {
        setTitle("Admin Dashboard — Attendance System");
        setSize(1100, 720);
        setMinimumSize(new Dimension(800, 550));
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(true);
        setLayout(new BorderLayout());

        Sidebar sidebar = new Sidebar();
        sidebar.addLogo("Attendance System", "Admin Panel");
        btnManageSubjects    = sidebar.addNavItem("Subjects");
        btnManageEnrollments = sidebar.addNavItem("Enrollments");
        sidebar.add(btnManageSubjects);
        sidebar.add(btnManageEnrollments);
        sidebar.add(Box.createVerticalGlue());
        btnLogout = sidebar.addNavItem("Logout");
        btnLogout.setForeground(new Color(0xFCA5A5));
        sidebar.add(btnLogout);
        add(sidebar, BorderLayout.WEST);

        JPanel main = new JPanel(new BorderLayout(0, 0));
        main.setBackground(Theme.BG);
        main.setBorder(BorderFactory.createEmptyBorder(28, 28, 28, 28));

        JPanel titleRow = new JPanel(new BorderLayout());
        titleRow.setOpaque(false);
        JLabel pageTitle = UI.heading("User Management");
        pageTitle.setFont(new Font("Segoe UI", Font.BOLD, 20));
        JLabel pageSub = UI.label("Add, edit, or remove faculty and student accounts");
        JPanel titleText = new JPanel();
        titleText.setOpaque(false);
        titleText.setLayout(new BoxLayout(titleText, BoxLayout.Y_AXIS));
        titleText.add(pageTitle);
        titleText.add(Box.createVerticalStrut(2));
        titleText.add(pageSub);
        titleRow.add(titleText, BorderLayout.WEST);
        main.add(titleRow, BorderLayout.NORTH);

        JPanel formCard = UI.card();
        formCard.setLayout(new GridBagLayout());
        formCard.setBorder(BorderFactory.createEmptyBorder(20, 20, 16, 20));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets  = new Insets(4, 6, 4, 6);
        gbc.fill    = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;

        txtFullName = UI.textField(14);
        txtUsername = UI.textField(14);
        txtPassword = UI.passField(14);
        cmbRole     = UI.comboBox(new String[]{"faculty", "student"});

        gbc.gridx = 0; gbc.gridy = 0; formCard.add(UI.label("Full Name"), gbc);
        gbc.gridx = 1;               formCard.add(UI.label("Username"),   gbc);
        gbc.gridx = 2;               formCard.add(UI.label("Password"),   gbc);
        gbc.gridx = 3;               formCard.add(UI.label("Role"),       gbc);
        gbc.gridy = 1;
        gbc.gridx = 0; formCard.add(txtFullName, gbc);
        gbc.gridx = 1; formCard.add(txtUsername, gbc);
        gbc.gridx = 2; formCard.add(txtPassword, gbc);
        gbc.gridx = 3; formCard.add(cmbRole,     gbc);

        btnAdd    = UI.btnPrimary("Add");
        btnUpdate = UI.button("Update", new Color(0x059669), Color.WHITE);
        btnDelete = UI.btnDanger("Delete");

        JPanel btnRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 0));
        btnRow.setOpaque(false);
        btnRow.add(btnAdd);
        btnRow.add(btnUpdate);
        btnRow.add(btnDelete);

        gbc.gridx = 0; gbc.gridy = 2; gbc.gridwidth = 4;
        gbc.insets = new Insets(12, 6, 0, 6);
        formCard.add(btnRow, gbc);

        tableModel = new DefaultTableModel(new String[]{"ID", "Full Name", "Username", "Role"}, 0) {
            public boolean isCellEditable(int r, int c) { return false; }
        };
        tblUsers = new JTable(tableModel);
        UI.styleTable(tblUsers);
        JScrollPane sp = UI.scrollPane(tblUsers);
        sp.setPreferredSize(new Dimension(0, 380));

        gbc.gridx = 0; gbc.gridy = 3; gbc.gridwidth = 4;
        gbc.insets  = new Insets(16, 6, 0, 6);
        gbc.fill    = GridBagConstraints.BOTH;
        gbc.weighty = 1.0;
        formCard.add(sp, gbc);

        JPanel center = new JPanel(new BorderLayout());
        center.setOpaque(false);
        center.setBorder(BorderFactory.createEmptyBorder(16, 0, 0, 0));
        center.add(formCard, BorderLayout.CENTER);
        main.add(center, BorderLayout.CENTER);
        add(main, BorderLayout.CENTER);

        tblUsers.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                int row = tblUsers.getSelectedRow();
                if (row != -1) {
                    txtFullName.setText(tableModel.getValueAt(row, 1).toString());
                    txtUsername.setText(tableModel.getValueAt(row, 2).toString());
                    txtPassword.setText("");
                    cmbRole.setSelectedItem(tableModel.getValueAt(row, 3).toString());
                }
            }
        });

        btnAdd.addActionListener(e -> addUser());
        btnUpdate.addActionListener(e -> updateUser());
        btnDelete.addActionListener(e -> deleteUser());
        btnLogout.addActionListener(e -> { new LoginForm().setVisible(true); dispose(); });
        btnManageSubjects.addActionListener(e -> new SubjectForm().setVisible(true));
        btnManageEnrollments.addActionListener(e -> new EnrollmentForm().setVisible(true));

        loadUsers();
    }

    void loadUsers() {
        try (Connection con = DBConnection.getConnection();
             Statement st = con.createStatement();
             ResultSet rs = st.executeQuery("SELECT id, full_name, username, role FROM users")) {
            tableModel.setRowCount(0);
            while (rs.next())
                tableModel.addRow(new Object[]{
                    rs.getInt("id"), rs.getString("full_name"),
                    rs.getString("username"), rs.getString("role")
                });
        } catch (Exception e) { JOptionPane.showMessageDialog(this, "Error: " + e.getMessage()); }
    }

    boolean validateFields(String username, String password) {
        String uErr = Validator.validateUsername(username);
        String pErr = Validator.validatePassword(password);
        if (uErr != null || pErr != null) {
            JOptionPane.showMessageDialog(this,
                Validator.buildErrorMessage(uErr, pErr), "Validation Error", JOptionPane.WARNING_MESSAGE);
            return false;
        }
        return true;
    }

    void addUser() {
        String fullName = txtFullName.getText().trim();
        String username = txtUsername.getText().trim();
        String password = new String(txtPassword.getPassword());
        String role     = cmbRole.getSelectedItem().toString();

        if (fullName.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Full Name is required.", "Validation Error", JOptionPane.WARNING_MESSAGE);
            return;
        }
        if (!validateFields(username, password)) return;

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(
                 "INSERT INTO users (full_name, username, password, role) VALUES (?,?,?,?)")) {
            ps.setString(1, fullName);
            ps.setString(2, username);
            ps.setString(3, password);
            ps.setString(4, role);
            ps.executeUpdate();
            JOptionPane.showMessageDialog(this, "User added successfully!");
            txtFullName.setText("");
            txtUsername.setText("");
            txtPassword.setText("");
            loadUsers();
        } catch (Exception e) { JOptionPane.showMessageDialog(this, "Error: " + e.getMessage()); }
    }

    void updateUser() {
        int row = tblUsers.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Please select a user to update.", "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }
        String username = txtUsername.getText().trim();
        String password = new String(txtPassword.getPassword());
        String uErr = Validator.validateUsername(username);
        String pErr = password.isEmpty() ? null : Validator.validatePassword(password);
        if (uErr != null || pErr != null) {
            JOptionPane.showMessageDialog(this,
                Validator.buildErrorMessage(uErr, pErr), "Validation Error", JOptionPane.WARNING_MESSAGE);
            return;
        }
        int id = (int) tableModel.getValueAt(row, 0);
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(
                 "UPDATE users SET full_name=?, username=?, password=?, role=? WHERE id=?")) {
            ps.setString(1, txtFullName.getText());
            ps.setString(2, username);
            ps.setString(3, password);
            ps.setString(4, cmbRole.getSelectedItem().toString());
            ps.setInt(5, id);
            ps.executeUpdate();
            JOptionPane.showMessageDialog(this, "User updated successfully!");
            loadUsers();
        } catch (Exception e) { JOptionPane.showMessageDialog(this, "Error: " + e.getMessage()); }
    }

    void deleteUser() {
        int row = tblUsers.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Please select a user to delete.", "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }
        int id     = (int) tableModel.getValueAt(row, 0);
        String role = tableModel.getValueAt(row, 3).toString();
        int confirm = JOptionPane.showConfirmDialog(this,
            "Are you sure you want to delete this user?\nAll related records will also be deleted.",
            "Confirm Delete", JOptionPane.YES_NO_OPTION);
        if (confirm == 0) {
            try (Connection con = DBConnection.getConnection()) {
                try (PreparedStatement ps1 = con.prepareStatement("DELETE FROM attendance WHERE student_id=?")) {
                    ps1.setInt(1, id); ps1.executeUpdate();
                }
                if (role.equals("faculty")) {
                    try (PreparedStatement ps2 = con.prepareStatement("UPDATE subjects SET faculty_id=NULL WHERE faculty_id=?")) {
                        ps2.setInt(1, id); ps2.executeUpdate();
                    }
                }
                if (role.equals("student")) {
                    try (PreparedStatement ps3 = con.prepareStatement("DELETE FROM enrollments WHERE student_id=?")) {
                        ps3.setInt(1, id); ps3.executeUpdate();
                    }
                }
                try (PreparedStatement ps4 = con.prepareStatement("DELETE FROM users WHERE id=?")) {
                    ps4.setInt(1, id); ps4.executeUpdate();
                }
                JOptionPane.showMessageDialog(this, "User deleted successfully!");
                loadUsers();
            } catch (Exception e) { JOptionPane.showMessageDialog(this, "Error: " + e.getMessage()); }
        }
    }
}