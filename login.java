package projectiv;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;

public class login extends JFrame {
    private JTextField emailField;
    private JPasswordField passwordField;
    private JButton showPasswordButton;
    private boolean passwordVisible = false;

    private static final String DB_URL = "jdbc:mysql://localhost:3306/bloodbank";
    private static final String DB_USER = "root";
    private static final String DB_PASS = "";

    // Professional color scheme
    private static final Color PRIMARY_COLOR = new Color(220, 53, 69);
    private static final Color SECONDARY_COLOR = new Color(108, 117, 125);
    private static final Color ACCENT_COLOR = new Color(40, 167, 69);
    private static final Color BACKGROUND_LIGHT = new Color(248, 249, 250);
    private static final Color TEXT_DARK = new Color(33, 37, 41);

    // Professional fonts
    private static final Font TITLE_FONT = new Font("Segoe UI", Font.BOLD, 44);
    private static final Font LABEL_FONT = new Font("Segoe UI", Font.BOLD, 15);
    private static final Font INPUT_FONT = new Font("Segoe UI", Font.PLAIN, 15);
    private static final Font BUTTON_FONT = new Font("Segoe UI", Font.BOLD, 16);
    private static final Font WELCOME_FONT = new Font("Segoe UI", Font.BOLD, 42);
    private static final Font SUBTITLE_FONT = new Font("Segoe UI", Font.PLAIN, 14);
    private static final Font TAGLINE_FONT = new Font("Segoe UI", Font.ITALIC, 16);
    private static final Font LINK_FONT = new Font("Segoe UI", Font.PLAIN, 12);

    public login() {
        setTitle("Blood Bank Management System - Login");
        setSize(1100, 700);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new GridLayout(1, 2));

        // Left panel with gradient and blood drop icon
        JPanel leftPanel = createLeftPanel();

        // Right panel with modern login form
        JPanel rightPanel = createRightPanel();

        add(leftPanel);
        add(rightPanel);
    }

    private JPanel createLeftPanel() {
        JPanel leftPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);

                // Create gradient background
                GradientPaint gradient = new GradientPaint(
                        0, 0, new Color(220, 53, 69),
                        0, getHeight(), new Color(185, 28, 46)
                );
                g2d.setPaint(gradient);
                g2d.fillRect(0, 0, getWidth(), getHeight());

                // Add subtle pattern overlay
                g2d.setColor(new Color(255, 255, 255, 20));
                for (int i = 0; i < getWidth(); i += 40) {
                    for (int j = 0; j < getHeight(); j += 40) {
                        g2d.fillOval(i, j, 4, 4);
                    }
                }
            }
        };

        leftPanel.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridwidth = GridBagConstraints.REMAINDER;
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.insets = new Insets(10, 20, 10, 20);

        // Blood drop icon
        JLabel iconLabel = new JLabel("💉");
        iconLabel.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 80));
        leftPanel.add(iconLabel, gbc);

        // Welcome text
        JLabel welcomeLabel = new JLabel("<html><div style='text-align: center;'>" +
                "<h1 style='color: white; font-family: Segoe UI; font-size: 42px; margin: 0;'>Welcome To</h1>" +
                "<h2 style='color: white; font-family: Segoe UI; font-size: 38px; margin: 5px 0;'>Blood Bank</h2>" +
                "<h2 style='color: white; font-family: Segoe UI; font-size: 38px; margin: 0;'>Management System</h2>" +
                "</div></html>");
        welcomeLabel.setForeground(Color.WHITE);
        leftPanel.add(welcomeLabel, gbc);

        // Tagline
        JLabel tagline = new JLabel("<html><div style='text-align: center; color: rgba(255,255,255,0.9);'>" +
                "Saving Lives Through Better Management</div></html>");
        tagline.setFont(TAGLINE_FONT);
        tagline.setForeground(new Color(255, 255, 255, 200));
        leftPanel.add(tagline, gbc);

        return leftPanel;
    }

    private JPanel createRightPanel() {
        JPanel rightPanel = new JPanel();
        rightPanel.setBackground(BACKGROUND_LIGHT);
        rightPanel.setLayout(null);

        // Login heading
        JLabel loginTitle = new JLabel("Login");
        loginTitle.setFont(TITLE_FONT);
        loginTitle.setForeground(TEXT_DARK);
        loginTitle.setBounds(60, 50, 200, 55);
        rightPanel.add(loginTitle);

        // Subtitle
        JLabel subtitle = new JLabel("Please enter your credentials");
        subtitle.setFont(SUBTITLE_FONT);
        subtitle.setForeground(SECONDARY_COLOR);
        subtitle.setBounds(60, 105, 300, 20);
        rightPanel.add(subtitle);

        int yPos = 170;

        // Email label
        JLabel emailLabel = new JLabel("Email Address");
        emailLabel.setFont(LABEL_FONT);
        emailLabel.setForeground(TEXT_DARK);
        emailLabel.setBounds(60, yPos, 150, 25);
        rightPanel.add(emailLabel);

        // Email field with placeholder
        emailField = new JTextField();
        emailField.setFont(INPUT_FONT);
        emailField.setBounds(60, yPos + 30, 350, 45);
        emailField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(206, 212, 218), 2),
                BorderFactory.createEmptyBorder(8, 15, 8, 15)
        ));
        emailField.setBackground(Color.WHITE);
        rightPanel.add(emailField);

        // Email example label
        JLabel emailExample = new JLabel("e.g., john.doe@example.com");
        emailExample.setFont(LINK_FONT);
        emailExample.setForeground(new Color(108, 117, 125));
        emailExample.setBounds(60, yPos + 78, 350, 15);
        rightPanel.add(emailExample);

        yPos += 110;

        // Password label
        JLabel passwordLabel = new JLabel("Password");
        passwordLabel.setFont(LABEL_FONT);
        passwordLabel.setForeground(TEXT_DARK);
        passwordLabel.setBounds(60, yPos, 150, 25);
        rightPanel.add(passwordLabel);

        // Password field container with eye button
        JPanel passwordPanel = new JPanel();
        passwordPanel.setLayout(null);
        passwordPanel.setBounds(60, yPos + 30, 350, 45);
        passwordPanel.setBorder(BorderFactory.createLineBorder(new Color(206, 212, 218), 2));
        passwordPanel.setBackground(Color.WHITE);

        passwordField = new JPasswordField();
        passwordField.setFont(INPUT_FONT);
        passwordField.setBounds(10, 5, 290, 35);
        passwordField.setBorder(BorderFactory.createEmptyBorder(8, 5, 8, 5));
        passwordField.setBackground(Color.WHITE);
        passwordPanel.add(passwordField);

        // Eye button for password visibility
        showPasswordButton = new JButton("👁");
        showPasswordButton.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 20));
        showPasswordButton.setBounds(305, 5, 40, 35);
        showPasswordButton.setBorderPainted(false);
        showPasswordButton.setContentAreaFilled(false);
        showPasswordButton.setFocusPainted(false);
        showPasswordButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        showPasswordButton.setToolTipText("Show/Hide Password");
        passwordPanel.add(showPasswordButton);

        rightPanel.add(passwordPanel);

        yPos += 80;

        // Forgot Password link
        JLabel forgotPasswordLink = new JLabel("<html><u>Forgot Password?</u></html>");
        forgotPasswordLink.setFont(LINK_FONT);
        forgotPasswordLink.setForeground(new Color(0, 123, 255));
        forgotPasswordLink.setBounds(60, yPos, 150, 20);
        forgotPasswordLink.setCursor(new Cursor(Cursor.HAND_CURSOR));
        rightPanel.add(forgotPasswordLink);

        yPos += 35;

        // Login button
        JButton loginBtn = new JButton("Login") {
            @Override
            protected void paintComponent(Graphics g) {
                if (getModel().isPressed()) {
                    g.setColor(new Color(185, 28, 46));
                } else if (getModel().isRollover()) {
                    g.setColor(new Color(200, 35, 51));
                } else {
                    g.setColor(PRIMARY_COLOR);
                }
                g.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
                super.paintComponent(g);
            }
        };
        loginBtn.setFont(BUTTON_FONT);
        loginBtn.setForeground(Color.WHITE);
        loginBtn.setBounds(60, yPos, 170, 48);
        loginBtn.setFocusPainted(false);
        loginBtn.setBorderPainted(false);
        loginBtn.setContentAreaFilled(false);
        loginBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        rightPanel.add(loginBtn);

        // Sign Up button
        JButton signupBtn = new JButton("Sign Up") {
            @Override
            protected void paintComponent(Graphics g) {
                if (getModel().isPressed()) {
                    g.setColor(new Color(30, 140, 58));
                } else if (getModel().isRollover()) {
                    g.setColor(new Color(35, 153, 64));
                } else {
                    g.setColor(ACCENT_COLOR);
                }
                g.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
                super.paintComponent(g);
            }
        };
        signupBtn.setFont(BUTTON_FONT);
        signupBtn.setForeground(Color.WHITE);
        signupBtn.setBounds(240, yPos, 170, 48);
        signupBtn.setFocusPainted(false);
        signupBtn.setBorderPainted(false);
        signupBtn.setContentAreaFilled(false);
        signupBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        rightPanel.add(signupBtn);

        // Footer text
        JLabel footerLabel = new JLabel("© 2025 Blood Bank Management System");
        footerLabel.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        footerLabel.setForeground(SECONDARY_COLOR);
        footerLabel.setBounds(60, 600, 350, 20);
        rightPanel.add(footerLabel);

        // Event listeners
        showPasswordButton.addActionListener(e -> togglePasswordVisibility());
        loginBtn.addActionListener(e -> loginUser());
        signupBtn.addActionListener(e -> {
            new SignupFrame().setVisible(true);
            dispose();
        });
        forgotPasswordLink.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                openForgotPassword();
            }
        });
        passwordField.addActionListener(e -> loginUser());

        return rightPanel;
    }

    private void togglePasswordVisibility() {
        passwordVisible = !passwordVisible;
        if (passwordVisible) {
            passwordField.setEchoChar((char) 0);
            showPasswordButton.setText("👁‍🗨");
        } else {
            passwordField.setEchoChar('•');
            showPasswordButton.setText("👁");
        }
    }

    private void openForgotPassword() {
        new ForgotPasswordFrame().setVisible(true);
    }

    private void loginUser() {
        String user = emailField.getText().trim();
        String pass = new String(passwordField.getPassword());

        if (user.isEmpty() || pass.isEmpty()) {
            showStyledMessage("Please enter email and password.", "Input Required", JOptionPane.WARNING_MESSAGE);
            return;
        }

        // Validate email format
        if (!EmailValidator.isValidEmail(user)) {
            showStyledMessage(
                    EmailValidator.getEmailErrorMessage(user),
                    "Invalid Email Format",
                    JOptionPane.ERROR_MESSAGE
            );
            emailField.requestFocus();
            return;
        }

        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASS)) {
            String sql = "SELECT * FROM users WHERE email=? AND password_hash=SHA2(?, 256)";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, user);
            ps.setString(2, pass);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                showStyledMessage("Login successful! Welcome to Blood Bank Management System.", "Success", JOptionPane.INFORMATION_MESSAGE);
                new DashboardFrame().setVisible(true);
                dispose();
            } else {
                showStyledMessage("Invalid email or password. Please try again.", "Login Failed", JOptionPane.ERROR_MESSAGE);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            showStyledMessage("Database connection error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    private void showStyledMessage(String message, String title, int messageType) {
        UIManager.put("OptionPane.background", BACKGROUND_LIGHT);
        UIManager.put("Panel.background", BACKGROUND_LIGHT);
        UIManager.put("OptionPane.messageFont", new Font("Segoe UI", Font.PLAIN, 13));
        JOptionPane.showMessageDialog(this, message, title, messageType);
    }

    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }
        SwingUtilities.invokeLater(() -> new login().setVisible(true));
    }
}