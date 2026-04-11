package attendance;

import javax.swing.*;
import java.awt.*;
import java.sql.*;

// Login screen — authenticates user and routes to correct dashboard
class LoginForm extends JFrame {

    private JTextField        txtUsername;
    private JPasswordField    txtPassword;
    private JTextField        txtPasswordVisible;
    private JCheckBox         chkShowPassword;
    private JComboBox<String> cmbRole;
    private JButton           btnLogin;

    public LoginForm() {
        setTitle("Attendance System — Login");
        setSize(560, 780);
        setMinimumSize(new Dimension(420, 620));
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(true);

        JPanel bg = new JPanel(new GridBagLayout()) {
            @Override protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                GradientPaint gp = new GradientPaint(0, 0, Theme.ACCENT, getWidth(), 0, Theme.ACCENT2);
                g2.setPaint(gp);
                g2.fillRect(0, 0, getWidth(), 5);
            }
        };
        bg.setBackground(Theme.BG);
        setContentPane(bg);

        JPanel card = UI.card();
        card.setLayout(new GridBagLayout());

        GridBagConstraints c = new GridBagConstraints();
        c.fill = GridBagConstraints.HORIZONTAL;
        c.weightx = 1.0;
        c.gridx = 0;

        AttendanceIcon icon = new AttendanceIcon(72);
        JPanel iconWrapper = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 0));
        iconWrapper.setOpaque(false);
        iconWrapper.setBorder(BorderFactory.createEmptyBorder(8, 0, 0, 0));
        iconWrapper.add(icon);

        JLabel lblTitle = new JLabel("Attendance Management System", SwingConstants.CENTER);
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 22));
        lblTitle.setForeground(Theme.TEXT);

        JLabel lblSub = new JLabel("Sign in to continue", SwingConstants.CENTER);
        lblSub.setFont(Theme.FONT_LABEL);
        lblSub.setForeground(Theme.TEXT_MUTED);

        txtUsername        = UI.textField(20);
        txtPassword        = UI.passField(20);
        txtPasswordVisible = UI.textField(20);
        txtPasswordVisible.setVisible(false);

        chkShowPassword = new JCheckBox("Show Password");
        chkShowPassword.setFont(Theme.FONT_SMALL);
        chkShowPassword.setForeground(Theme.TEXT_MUTED);
        chkShowPassword.setOpaque(false);
        chkShowPassword.setFocusPainted(false);
        chkShowPassword.addActionListener(e -> {
            if (chkShowPassword.isSelected()) {
                txtPasswordVisible.setText(new String(txtPassword.getPassword()));
                txtPassword.setVisible(false);
                txtPasswordVisible.setVisible(true);
            } else {
                txtPassword.setText(txtPasswordVisible.getText());
                txtPasswordVisible.setVisible(false);
                txtPassword.setVisible(true);
            }
            card.revalidate();
            card.repaint();
        });

        JPanel passPanel = new JPanel(new GridBagLayout());
        passPanel.setOpaque(false);
        GridBagConstraints pc = new GridBagConstraints();
        pc.fill = GridBagConstraints.HORIZONTAL;
        pc.weightx = 1.0;
        pc.gridx = 0;
        pc.gridy = 0; passPanel.add(txtPassword, pc);
        pc.gridy = 1; passPanel.add(txtPasswordVisible, pc);
        pc.gridy = 2; pc.insets = new Insets(4, 2, 0, 0); passPanel.add(chkShowPassword, pc);

        cmbRole  = UI.comboBox(new String[]{"admin", "faculty", "student"});
        btnLogin = UI.btnPrimary("Sign In");
        btnLogin.setFont(new Font("Segoe UI", Font.BOLD, 14));

        c.gridy = 0;  c.insets = new Insets(40, 44, 0, 44);  card.add(iconWrapper, c);
        c.gridy = 1;  c.insets = new Insets(16, 44, 0, 44);  card.add(lblTitle, c);
        c.gridy = 2;  c.insets = new Insets(6,  44, 0, 44);  card.add(lblSub, c);
        JSeparator sep = new JSeparator();
        sep.setForeground(Theme.BORDER);
        c.gridy = 3;  c.insets = new Insets(24, 44, 0, 44);  card.add(sep, c);
        c.gridy = 4;  c.insets = new Insets(20, 44, 4, 44);  card.add(UI.label("Username"), c);
        c.gridy = 5;  c.insets = new Insets(0,  44, 0, 44);  card.add(txtUsername, c);
        c.gridy = 6;  c.insets = new Insets(16, 44, 4, 44);  card.add(UI.label("Password"), c);
        c.gridy = 7;  c.insets = new Insets(0,  44, 0, 44);  card.add(passPanel, c);
        c.gridy = 8;  c.insets = new Insets(16, 44, 4, 44);  card.add(UI.label("Role"), c);
        c.gridy = 9;  c.insets = new Insets(0,  44, 0, 44);  card.add(cmbRole, c);
        c.gridy = 10; c.insets = new Insets(24, 44, 40, 44); card.add(btnLogin, c);

        JPanel cardWrapper = new JPanel(new GridBagLayout()) {
            @Override public Dimension getPreferredSize() {
                Dimension d = super.getPreferredSize();
                return new Dimension(Math.min(620, d.width), d.height);
            }
            @Override public Dimension getMaximumSize() {
                return new Dimension(620, Integer.MAX_VALUE);
            }
        };
        cardWrapper.setOpaque(false);
        GridBagConstraints wc = new GridBagConstraints();
        wc.fill = GridBagConstraints.HORIZONTAL;
        wc.weightx = 1.0;
        cardWrapper.add(card, wc);

        GridBagConstraints bgc = new GridBagConstraints();
        bgc.fill = GridBagConstraints.NONE;
        bgc.anchor = GridBagConstraints.CENTER;
        bgc.weightx = 1.0;
        bgc.weighty = 1.0;
        bgc.insets = new Insets(20, 20, 20, 20);
        bg.add(cardWrapper, bgc);

        btnLogin.addActionListener(e -> login());
        txtPassword.addActionListener(e -> login());
        txtPasswordVisible.addActionListener(e -> login());
    }

    String getPassword() {
        return chkShowPassword.isSelected()
            ? txtPasswordVisible.getText()
            : new String(txtPassword.getPassword());
    }

    // Uses SwingWorker to prevent UI freeze during DB call
    void login() {
        String username = txtUsername.getText().trim();
        String password = getPassword();
        String role     = cmbRole.getSelectedItem().toString();

        btnLogin.setEnabled(false);
        btnLogin.setText("Signing in...");

        new SwingWorker<Integer, Void>() {
            String errorMsg = null;

            @Override
            protected Integer doInBackground() {
                try (Connection con = DBConnection.getConnection();
                     PreparedStatement ps = con.prepareStatement(
                         "SELECT id FROM users WHERE username=? AND password=? AND role=?")) {
                    ps.setString(1, username);
                    ps.setString(2, password);
                    ps.setString(3, role);
                    try (ResultSet rs = ps.executeQuery()) {
                        if (rs.next()) return rs.getInt("id");
                    }
                } catch (Exception ex) {
                    errorMsg = ex.getMessage();
                }
                return -1;
            }

            @Override
            protected void done() {
                btnLogin.setEnabled(true);
                btnLogin.setText("Sign In");
                if (errorMsg != null) {
                    JOptionPane.showMessageDialog(LoginForm.this,
                        "Connection error: " + errorMsg, "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                try {
                    int userId = get();
                    if (userId != -1) {
                        if (role.equals("admin"))        new AdminDashboard().setVisible(true);
                        else if (role.equals("faculty")) new FacultyDashboard(userId).setVisible(true);
                        else                             new StudentDashboard(userId).setVisible(true);
                        LoginForm.this.dispose();
                    } else {
                        JOptionPane.showMessageDialog(LoginForm.this,
                            "Invalid username, password, or role.", "Login Failed", JOptionPane.ERROR_MESSAGE);
                    }
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(LoginForm.this, "Error: " + ex.getMessage());
                }
            }
        }.execute();
    }
}