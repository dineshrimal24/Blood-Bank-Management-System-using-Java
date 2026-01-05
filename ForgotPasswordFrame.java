package projectiv;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;

public class ForgotPasswordFrame extends JFrame {
    private JTextField emailUsernameField;
    private JLabel question1Label, question2Label, question3Label;
    private JTextField answer1Field, answer2Field, answer3Field;
    private JPasswordField newPasswordField, confirmNewPasswordField;
    private JButton verifyEmailButton, verifyAnswersButton, resetPasswordButton;
    private JButton showPassword1Button, showPassword2Button;
    private boolean password1Visible = false;
    private boolean password2Visible = false;

    private String verifiedEmail = null;
    private String sq1, sq2, sq3;

    private static final String DB_URL = "jdbc:mysql://localhost:3306/bloodbank";
    private static final String DB_USER = "root";
    private static final String DB_PASS = "";

    private static final Color PRIMARY_COLOR = new Color(220, 53, 69);
    private static final Color ACCENT_COLOR = new Color(40, 167, 69);
    private static final Font TITLE_FONT = new Font("Segoe UI", Font.BOLD, 32);
    private static final Font LABEL_FONT = new Font("Segoe UI", Font.BOLD, 14);
    private static final Font INPUT_FONT = new Font("Segoe UI", Font.PLAIN, 14);

    public ForgotPasswordFrame() {
        setTitle("Forgot Password - Password Recovery");
        setSize(600, 750);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);

        initComponents();
        setStep1Enabled(true);
        setStep2Enabled(false);
        setStep3Enabled(false);
    }

    private void initComponents() {
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.setBackground(new Color(245, 245, 245));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(30, 40, 30, 40));

        // Title
        JLabel titleLabel = new JLabel("Password Recovery");
        titleLabel.setFont(TITLE_FONT);
        titleLabel.setForeground(PRIMARY_COLOR);
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        mainPanel.add(titleLabel);
        mainPanel.add(Box.createVerticalStrut(10));

        JLabel subtitleLabel = new JLabel("Recover your account by answering security questions");
        subtitleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        subtitleLabel.setForeground(new Color(100, 100, 100));
        subtitleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        mainPanel.add(subtitleLabel);
        mainPanel.add(Box.createVerticalStrut(30));

        // Step 1: Email Verification
        JPanel step1Panel = createStepPanel("Step 1: Verify Your Email/Username");
        JLabel emailLabel = new JLabel("Email or Username:");
        emailLabel.setFont(LABEL_FONT);
        emailUsernameField = new JTextField(30);
        emailUsernameField.setFont(INPUT_FONT);
        emailUsernameField.setMaximumSize(new Dimension(400, 35));

        verifyEmailButton = new JButton("Verify Account");
        styleButton(verifyEmailButton, new Color(0, 123, 255));
        verifyEmailButton.addActionListener(e -> verifyEmail());

        step1Panel.add(emailLabel);
        step1Panel.add(Box.createVerticalStrut(8));
        step1Panel.add(emailUsernameField);
        step1Panel.add(Box.createVerticalStrut(15));
        step1Panel.add(verifyEmailButton);

        mainPanel.add(step1Panel);
        mainPanel.add(Box.createVerticalStrut(20));

        // Step 2: Security Questions
        JPanel step2Panel = createStepPanel("Step 2: Answer Security Questions");

        question1Label = new JLabel("Question 1: ");
        question1Label.setFont(new Font("Segoe UI", Font.BOLD, 12));
        answer1Field = new JTextField(30);
        answer1Field.setFont(INPUT_FONT);
        answer1Field.setMaximumSize(new Dimension(400, 35));

        question2Label = new JLabel("Question 2: ");
        question2Label.setFont(new Font("Segoe UI", Font.BOLD, 12));
        answer2Field = new JTextField(30);
        answer2Field.setFont(INPUT_FONT);
        answer2Field.setMaximumSize(new Dimension(400, 35));

        question3Label = new JLabel("Question 3: ");
        question3Label.setFont(new Font("Segoe UI", Font.BOLD, 12));
        answer3Field = new JTextField(30);
        answer3Field.setFont(INPUT_FONT);
        answer3Field.setMaximumSize(new Dimension(400, 35));

        verifyAnswersButton = new JButton("Verify Answers");
        styleButton(verifyAnswersButton, ACCENT_COLOR);
        verifyAnswersButton.addActionListener(e -> verifyAnswers());

        step2Panel.add(question1Label);
        step2Panel.add(Box.createVerticalStrut(8));
        step2Panel.add(answer1Field);
        step2Panel.add(Box.createVerticalStrut(15));
        step2Panel.add(question2Label);
        step2Panel.add(Box.createVerticalStrut(8));
        step2Panel.add(answer2Field);
        step2Panel.add(Box.createVerticalStrut(15));
        step2Panel.add(question3Label);
        step2Panel.add(Box.createVerticalStrut(8));
        step2Panel.add(answer3Field);
        step2Panel.add(Box.createVerticalStrut(20));
        step2Panel.add(verifyAnswersButton);

        mainPanel.add(step2Panel);
        mainPanel.add(Box.createVerticalStrut(20));

        // Step 3: Reset Password
        JPanel step3Panel = createStepPanel("Step 3: Set New Password");

        JLabel newPasswordLabel = new JLabel("New Password:");
        newPasswordLabel.setFont(LABEL_FONT);

        JPanel passwordPanel1 = createPasswordPanel();
        newPasswordField = new JPasswordField(25);
        newPasswordField.setFont(INPUT_FONT);
        newPasswordField.setBounds(5, 0, 330, 35);
        newPasswordField.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
        passwordPanel1.add(newPasswordField);

        showPassword1Button = new JButton("👁");
        showPassword1Button.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 18));
        showPassword1Button.setBounds(340, 0, 40, 35);
        showPassword1Button.setBorderPainted(false);
        showPassword1Button.setContentAreaFilled(false);
        showPassword1Button.setFocusPainted(false);
        showPassword1Button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        showPassword1Button.addActionListener(e -> togglePassword1());
        passwordPanel1.add(showPassword1Button);

        JLabel confirmPasswordLabel = new JLabel("Confirm New Password:");
        confirmPasswordLabel.setFont(LABEL_FONT);

        JPanel passwordPanel2 = createPasswordPanel();
        confirmNewPasswordField = new JPasswordField(25);
        confirmNewPasswordField.setFont(INPUT_FONT);
        confirmNewPasswordField.setBounds(5, 0, 330, 35);
        confirmNewPasswordField.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
        passwordPanel2.add(confirmNewPasswordField);

        showPassword2Button = new JButton("👁");
        showPassword2Button.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 18));
        showPassword2Button.setBounds(340, 0, 40, 35);
        showPassword2Button.setBorderPainted(false);
        showPassword2Button.setContentAreaFilled(false);
        showPassword2Button.setFocusPainted(false);
        showPassword2Button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        showPassword2Button.addActionListener(e -> togglePassword2());
        passwordPanel2.add(showPassword2Button);

        resetPasswordButton = new JButton("Reset Password");
        styleButton(resetPasswordButton, PRIMARY_COLOR);
        resetPasswordButton.addActionListener(e -> resetPassword());

        step3Panel.add(newPasswordLabel);
        step3Panel.add(Box.createVerticalStrut(8));
        step3Panel.add(passwordPanel1);
        step3Panel.add(Box.createVerticalStrut(15));
        step3Panel.add(confirmPasswordLabel);
        step3Panel.add(Box.createVerticalStrut(8));
        step3Panel.add(passwordPanel2);
        step3Panel.add(Box.createVerticalStrut(20));
        step3Panel.add(resetPasswordButton);

        mainPanel.add(step3Panel);
        mainPanel.add(Box.createVerticalStrut(20));

        // Back to Login button
        JButton backButton = new JButton("Back to Login");
        backButton.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        backButton.setForeground(new Color(70, 130, 180));
        backButton.setBorderPainted(false);
        backButton.setContentAreaFilled(false);
        backButton.setFocusPainted(false);
        backButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        backButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        backButton.addActionListener(e -> dispose());
        mainPanel.add(backButton);

        add(new JScrollPane(mainPanel));
    }

    private JPanel createStepPanel(String title) {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200), 1),
                BorderFactory.createEmptyBorder(20, 20, 20, 20)
        ));
        panel.setMaximumSize(new Dimension(520, 400));

        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
        titleLabel.setForeground(new Color(33, 37, 41));
        titleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.add(titleLabel);
        panel.add(Box.createVerticalStrut(20));

        return panel;
    }

    private JPanel createPasswordPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(null);
        panel.setBackground(Color.WHITE);
        panel.setPreferredSize(new Dimension(380, 35));
        panel.setMaximumSize(new Dimension(380, 35));
        panel.setBorder(BorderFactory.createLineBorder(new Color(200, 200, 200), 1));
        return panel;
    }

    private void styleButton(JButton button, Color color) {
        button.setFont(new Font("Segoe UI", Font.BOLD, 14));
        button.setBackground(color);
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setPreferredSize(new Dimension(180, 40));
        button.setMaximumSize(new Dimension(180, 40));
        button.setBorderPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setAlignmentX(Component.CENTER_ALIGNMENT);
    }

    private void togglePassword1() {
        password1Visible = !password1Visible;
        if (password1Visible) {
            newPasswordField.setEchoChar((char) 0);
            showPassword1Button.setText("👁‍🗨");
        } else {
            newPasswordField.setEchoChar('•');
            showPassword1Button.setText("👁");
        }
    }

    private void togglePassword2() {
        password2Visible = !password2Visible;
        if (password2Visible) {
            confirmNewPasswordField.setEchoChar((char) 0);
            showPassword2Button.setText("👁‍🗨");
        } else {
            confirmNewPasswordField.setEchoChar('•');
            showPassword2Button.setText("👁");
        }
    }

    private void setStep1Enabled(boolean enabled) {
        emailUsernameField.setEnabled(enabled);
        verifyEmailButton.setEnabled(enabled);
    }

    private void setStep2Enabled(boolean enabled) {
        question1Label.setEnabled(enabled);
        question2Label.setEnabled(enabled);
        question3Label.setEnabled(enabled);
        answer1Field.setEnabled(enabled);
        answer2Field.setEnabled(enabled);
        answer3Field.setEnabled(enabled);
        verifyAnswersButton.setEnabled(enabled);
    }

    private void setStep3Enabled(boolean enabled) {
        newPasswordField.setEnabled(enabled);
        confirmNewPasswordField.setEnabled(enabled);
        resetPasswordButton.setEnabled(enabled);
    }

    private void verifyEmail() {
        String emailOrUsername = emailUsernameField.getText().trim();

        if (emailOrUsername.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Please enter your email or username.",
                    "Input Required",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASS)) {
            String sql = "SELECT email, security_question1, security_question2, security_question3 FROM users WHERE email = ? OR username = ?";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, emailOrUsername);
            ps.setString(2, emailOrUsername);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                verifiedEmail = rs.getString("email");
                sq1 = rs.getString("security_question1");
                sq2 = rs.getString("security_question2");
                sq3 = rs.getString("security_question3");

                question1Label.setText("Question 1: " + sq1);
                question2Label.setText("Question 2: " + sq2);
                question3Label.setText("Question 3: " + sq3);

                setStep1Enabled(false);
                setStep2Enabled(true);

                JOptionPane.showMessageDialog(this,
                        "Account found! Please answer the security questions.",
                        "Success",
                        JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(this,
                        "No account found with that email or username.",
                        "Not Found",
                        JOptionPane.ERROR_MESSAGE);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this,
                    "Database error: " + ex.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void verifyAnswers() {
        String ans1 = answer1Field.getText().trim();
        String ans2 = answer2Field.getText().trim();
        String ans3 = answer3Field.getText().trim();

        if (ans1.isEmpty() || ans2.isEmpty() || ans3.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Please answer all three security questions.",
                    "Input Required",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASS)) {
            String sql = "SELECT * FROM users WHERE email = ? AND " +
                    "security_answer1 = SHA2(?, 256) AND " +
                    "security_answer2 = SHA2(?, 256) AND " +
                    "security_answer3 = SHA2(?, 256)";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, verifiedEmail);
            ps.setString(2, ans1.toLowerCase());
            ps.setString(3, ans2.toLowerCase());
            ps.setString(4, ans3.toLowerCase());
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                setStep2Enabled(false);
                setStep3Enabled(true);

                JOptionPane.showMessageDialog(this,
                        "Security questions verified! You can now reset your password.",
                        "Success",
                        JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(this,
                        "One or more answers are incorrect.\n\n" +
                                "SECURITY WARNING: If you cannot answer these questions correctly, " +
                                "you will not be able to access your account.\n\n" +
                                "Please contact the system administrator for assistance.",
                        "Verification Failed",
                        JOptionPane.ERROR_MESSAGE);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this,
                    "Database error: " + ex.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void resetPassword() {
        String newPass = new String(newPasswordField.getPassword());
        String confirmPass = new String(confirmNewPasswordField.getPassword());

        if (newPass.isEmpty() || confirmPass.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Please enter and confirm your new password.",
                    "Input Required",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        if (newPass.length() < 6) {
            JOptionPane.showMessageDialog(this,
                    "Password must be at least 6 characters long.",
                    "Invalid Password",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (!newPass.equals(confirmPass)) {
            JOptionPane.showMessageDialog(this,
                    "Passwords do not match!",
                    "Password Mismatch",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASS)) {
            String sql = "UPDATE users SET password_hash = SHA2(?, 256) WHERE email = ?";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, newPass);
            ps.setString(2, verifiedEmail);
            int updated = ps.executeUpdate();

            if (updated > 0) {
                JOptionPane.showMessageDialog(this,
                        "Password reset successful!\n\nYou can now login with your new password.",
                        "Success",
                        JOptionPane.INFORMATION_MESSAGE);
                dispose();
                new login().setVisible(true);
            } else {
                JOptionPane.showMessageDialog(this,
                        "Failed to reset password. Please try again.",
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this,
                    "Database error: " + ex.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }
}