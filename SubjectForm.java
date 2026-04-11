package attendance;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;

// Subject management form — add, update, delete subjects and assign faculty
class SubjectForm extends JFrame {

    private JTextField        txtSubjectName;
    private JComboBox<String> cmbFaculty;
    private JButton           btnAdd, btnUpdate, btnDelete, btnClose;
    private JTable            tblSubjects;
    private DefaultTableModel tableModel;

    public SubjectForm() {
        setTitle("Subject Management");
        setSize(860, 620);
        setMinimumSize(new Dimension(640, 480));
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(true);
        setLayout(new BorderLayout());

        JPanel main = new JPanel(new BorderLayout(0, 16));
        main.setBackground(Theme.BG);
        main.setBorder(BorderFactory.createEmptyBorder(24, 24, 24, 24));

        JPanel titleRow = new JPanel(new BorderLayout());
        titleRow.setOpaque(false);
        titleRow.add(UI.heading("Subject Management"), BorderLayout.WEST);
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

        txtSubjectName = UI.textField(16);
        cmbFaculty     = UI.comboBox();

        gbc.gridx = 0; gbc.gridy = 0; formCard.add(UI.label("Subject Name"),   gbc);
        gbc.gridx = 1;               formCard.add(UI.label("Assign Faculty"), gbc);
        gbc.gridy = 1;
        gbc.gridx = 0; formCard.add(txtSubjectName, gbc);
        gbc.gridx = 1; formCard.add(cmbFaculty,     gbc);

        btnAdd    = UI.btnPrimary("Add");
        btnUpdate = UI.button("Update", new Color(0x059669), Color.WHITE);
        btnDelete = UI.btnDanger("Delete");

        JPanel btnRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 0));
        btnRow.setOpaque(false);
        btnRow.add(btnAdd);
        btnRow.add(btnUpdate);
        btnRow.add(btnDelete);

        gbc.gridx = 0; gbc.gridy = 2; gbc.gridwidth = 2;
        gbc.insets = new Insets(12, 6, 0, 6);
        formCard.add(btnRow, gbc);

        tableModel = new DefaultTableModel(new String[]{"ID", "Subject Name", "Faculty"}, 0) {
            public boolean isCellEditable(int r, int c) { return false; }
        };
        tblSubjects = new JTable(tableModel);
        UI.styleTable(tblSubjects);
        JScrollPane sp = UI.scrollPane(tblSubjects);
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

        tblSubjects.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                int row = tblSubjects.getSelectedRow();
                if (row != -1) {
                    txtSubjectName.setText(tableModel.getValueAt(row, 1).toString());
                    String fn = tableModel.getValueAt(row, 2) != null ? tableModel.getValueAt(row, 2).toString() : "";
                    for (int i = 0; i < cmbFaculty.getItemCount(); i++) {
                        if (cmbFaculty.getItemAt(i).contains(fn)) { cmbFaculty.setSelectedIndex(i); break; }
                    }
                }
            }
        });

        btnAdd.addActionListener(e -> addSubject());
        btnUpdate.addActionListener(e -> updateSubject());
        btnDelete.addActionListener(e -> deleteSubject());
        btnClose.addActionListener(e -> dispose());

        loadFacultyCombo();
        loadSubjects();
    }

    void loadFacultyCombo() {
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement("SELECT id, full_name FROM users WHERE role='faculty'");
             ResultSet rs = ps.executeQuery()) {
            cmbFaculty.removeAllItems();
            while (rs.next()) cmbFaculty.addItem(rs.getInt("id") + " - " + rs.getString("full_name"));
        } catch (Exception e) { JOptionPane.showMessageDialog(this, "Error: " + e.getMessage()); }
    }

    void loadSubjects() {
        try (Connection con = DBConnection.getConnection();
             Statement st = con.createStatement();
             ResultSet rs = st.executeQuery(
                 "SELECT s.id, s.subject_name, u.full_name FROM subjects s LEFT JOIN users u ON s.faculty_id = u.id")) {
            tableModel.setRowCount(0);
            while (rs.next())
                tableModel.addRow(new Object[]{rs.getInt("id"), rs.getString("subject_name"), rs.getString("full_name")});
        } catch (Exception e) { JOptionPane.showMessageDialog(this, "Error: " + e.getMessage()); }
    }

    void addSubject() {
        String name = txtSubjectName.getText().trim();
        if (name.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter a subject name.", "Validation Error", JOptionPane.WARNING_MESSAGE);
            return;
        }
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement("INSERT INTO subjects (subject_name, faculty_id) VALUES (?, ?)")) {
            ps.setString(1, name);
            ps.setInt(2, Integer.parseInt(cmbFaculty.getSelectedItem().toString().split(" - ")[0]));
            ps.executeUpdate();
            JOptionPane.showMessageDialog(this, "Subject added successfully!");
            txtSubjectName.setText("");
            loadSubjects();
        } catch (Exception e) { JOptionPane.showMessageDialog(this, "Error: " + e.getMessage()); }
    }

    void updateSubject() {
        int row = tblSubjects.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Please select a subject to update.", "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }
        String name = txtSubjectName.getText().trim();
        if (name.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter a subject name.", "Validation Error", JOptionPane.WARNING_MESSAGE);
            return;
        }
        int id = (int) tableModel.getValueAt(row, 0);
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement("UPDATE subjects SET subject_name=?, faculty_id=? WHERE id=?")) {
            ps.setString(1, name);
            ps.setInt(2, Integer.parseInt(cmbFaculty.getSelectedItem().toString().split(" - ")[0]));
            ps.setInt(3, id);
            ps.executeUpdate();
            JOptionPane.showMessageDialog(this, "Subject updated successfully!");
            txtSubjectName.setText("");
            loadSubjects();
        } catch (Exception e) { JOptionPane.showMessageDialog(this, "Error: " + e.getMessage()); }
    }

    void deleteSubject() {
        int row = tblSubjects.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Please select a subject to delete.", "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }
        int id = (int) tableModel.getValueAt(row, 0);
        int confirm = JOptionPane.showConfirmDialog(this,
            "Are you sure you want to delete this subject?", "Confirm Delete", JOptionPane.YES_NO_OPTION);
        if (confirm == 0) {
            try (Connection con = DBConnection.getConnection();
                 PreparedStatement ps = con.prepareStatement("DELETE FROM subjects WHERE id=?")) {
                ps.setInt(1, id);
                ps.executeUpdate();
                JOptionPane.showMessageDialog(this, "Subject deleted successfully!");
                loadSubjects();
            } catch (Exception e) { JOptionPane.showMessageDialog(this, "Error: " + e.getMessage()); }
        }
    }
}