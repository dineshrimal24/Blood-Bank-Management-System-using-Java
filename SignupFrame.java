package projectiv;

import javax.swing.*;
import java.awt.*;
import java.sql.*;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.regex.Pattern;

public class SignupFrame extends JFrame {
    private JTextField nameField, usernameField, emailField, dobField;
    private JPasswordField passwordField, confirmPasswordField;
    private JButton showPasswordButton1, showPasswordButton2;
    private boolean password1Visible = false;
    private boolean password2Visible = false;

    // Security Questions
    private JComboBox<String> securityQuestion1Combo, securityQuestion2Combo, securityQuestion3Combo;
    private JTextField securityAnswer1Field, securityAnswer2Field, securityAnswer3Field;

    private static final String DB_URL = "jdbc:mysql://localhost:3306/bloodbank";
    private static final String DB_USER = "root";
    private static final String DB_PASS = "";
    private static final Pattern DATE_PATTERN = Pattern.compile("^\\d{4}-\\d{2}-\\d{2}$");

    private static final Font TITLE_FONT = new Font("Segoe UI", Font.BOLD, 36);
    private static final Font LABEL_FONT = new Font("Segoe UI", Font.BOLD, 14);
    private static final Font INPUT_FONT = new Font("Segoe UI", Font.PLAIN, 14);
    private static final Font BUTTON_FONT = new Font("Segoe UI", Font.BOLD, 16);
    private static final Font WELCOME_FONT = new Font("Segoe UI", Font.BOLD, 36);

    private static final String[] SECURITY_QUESTIONS = {
            "What was the name of your first pet?",
            "What is your mother's maiden name?",
            "What city were you born in?",
            "What was the name of your elementary school?",
            "What is your favorite book?",
            "What was your childhood nickname?",
            "What is the name of your favorite teacher?",
            "What street did you grow up on?",
            "What is your favorite food?",
            "What was the make of your first car?"
    };

    public SignupFrame() {
        setTitle("Sign Up - Blood Bank Management");
        setSize(1000, 900);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new GridLayout(1, 2));

        JPanel leftPanel = createLeftPanel();
        JPanel rightPanel = createRightPanel();

        add(leftPanel);
        add(rightPanel);
    }

    private JPanel createLeftPanel() {
        JPanel leftPanel = new JPanel();
        leftPanel.setBackground(new Color(230, 240, 247));
        leftPanel.setLayout(new GridBagLayout());
        JLabel welcomeLabel = new JLabel("<html><div style='text-align: center;'>Welcome To<br>Blood Bank<br>Management</div></html>");
        welcomeLabel.setFont(WELCOME_FONT);
        welcomeLabel.setForeground(new Color(139, 0, 0));
        leftPanel.add(welcomeLabel);
        return leftPanel;
    }

    private JPanel createRightPanel() {
        JPanel rightPanel = new JPanel();
        rightPanel.setBackground(Color.WHITE);
        rightPanel.setLayout(null);

        // Use a scroll pane to fit all fields
        JPanel formPanel = new JPanel();
        formPanel.setLayout(null);
        formPanel.setBackground(Color.WHITE);
        formPanel.setPreferredSize(new Dimension(480, 1100));

        JLabel signupTitle = new JLabel("Create Account");
        signupTitle.setFont(TITLE_FONT);
        signupTitle.setForeground(new Color(139, 0, 0));
        signupTitle.setBounds(90, 20, 350, 45);
        formPanel.add(signupTitle);

        int yPos = 80;
        int spacing = 75;

        // Full Name
        addFormField(formPanel, "Full Name *", nameField = new JTextField(), yPos);
        yPos += spacing;

        // Username with example
        addFormField(formPanel, "Username *", usernameField = new JTextField(), yPos);
        addExampleLabel(formPanel, "e.g., john_doe123", yPos);
        yPos += spacing;

        // Email with example
        addFormField(formPanel, "Email Address *", emailField = new JTextField(), yPos);
        addExampleLabel(formPanel, "e.g., john.doe@example.com", yPos);
        yPos += spacing;

        // Date of Birth with example
        addFormField(formPanel, "Date of Birth (YYYY-MM-DD) *", dobField = new JTextField(), yPos);
        addExampleLabel(formPanel, "e.g., 2000-01-15", yPos);
        yPos += spacing;

        // Password with eye button
        JLabel passwordLabel = new JLabel("Password *");
        passwordLabel.setFont(LABEL_FONT);
        passwordLabel.setBounds(50, yPos, 150, 25);
        formPanel.add(passwordLabel);

        JPanel passwordPanel1 = createPasswordPanel();
        passwordPanel1.setBounds(50, yPos + 28, 380, 35);
        passwordField = new JPasswordField();
        passwordField.setFont(INPUT_FONT);
        passwordField.setBounds(5, 0, 330, 35);
        passwordField.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
        passwordPanel1.add(passwordField);

        showPasswordButton1 = new JButton("👁");
        showPasswordButton1.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 18));
        showPasswordButton1.setBounds(340, 0, 40, 35);
        showPasswordButton1.setBorderPainted(false);
        showPasswordButton1.setContentAreaFilled(false);
        showPasswordButton1.setFocusPainted(false);
        showPasswordButton1.setCursor(new Cursor(Cursor.HAND_CURSOR));
        showPasswordButton1.addActionListener(e -> togglePassword1());
        passwordPanel1.add(showPasswordButton1);

        formPanel.add(passwordPanel1);
        addExampleLabel(formPanel, "Minimum 6 characters", yPos);
        yPos += spacing;

        // Confirm Password with eye button
        JLabel confirmPasswordLabel = new JLabel("Confirm Password *");
        confirmPasswordLabel.setFont(LABEL_FONT);
        confirmPasswordLabel.setBounds(50, yPos, 200, 25);
        formPanel.add(confirmPasswordLabel);

        JPanel passwordPanel2 = createPasswordPanel();
        passwordPanel2.setBounds(50, yPos + 28, 380, 35);
        confirmPasswordField = new JPasswordField();
        confirmPasswordField.setFont(INPUT_FONT);
        confirmPasswordField.setBounds(5, 0, 330, 35);
        confirmPasswordField.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
        passwordPanel2.add(confirmPasswordField);

        showPasswordButton2 = new JButton("👁");
        showPasswordButton2.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 18));
        showPasswordButton2.setBounds(340, 0, 40, 35);
        showPasswordButton2.setBorderPainted(false);
        showPasswordButton2.setContentAreaFilled(false);
        showPasswordButton2.setFocusPainted(false);
        showPasswordButton2.setCursor(new Cursor(Cursor.HAND_CURSOR));
        showPasswordButton2.addActionListener(e -> togglePassword2());
        passwordPanel2.add(showPasswordButton2);

        formPanel.add(passwordPanel2);
        yPos += spacing;

        // Security Questions Section
        JLabel securityTitle = new JLabel("Security Questions (for password recovery)");
        securityTitle.setFont(new Font("Segoe UI", Font.BOLD, 16));
        securityTitle.setForeground(new Color(139, 0, 0));
        securityTitle.setBounds(50, yPos, 400, 25);
        formPanel.add(securityTitle);
        yPos += 40;

        // Security Question 1
        addSecurityQuestion(formPanel, "Question 1 *",
                securityQuestion1Combo = new JComboBox<>(SECURITY_QUESTIONS),
                securityAnswer1Field = new JTextField(), yPos);
        yPos += spacing;

        // Security Question 2
        addSecurityQuestion(formPanel, "Question 2 *",
                securityQuestion2Combo = new JComboBox<>(SECURITY_QUESTIONS),
                securityAnswer2Field = new JTextField(), yPos);
        yPos += spacing;

        // Security Question 3
        addSecurityQuestion(formPanel, "Question 3 *",
                securityQuestion3Combo = new JComboBox<>(SECURITY_QUESTIONS),
                securityAnswer3Field = new JTextField(), yPos);
        yPos += 80;

        // Signup Button
        JButton signupBtn = new JButton("Create Account");
        signupBtn.setFont(BUTTON_FONT);
        signupBtn.setBackground(new Color(139, 0, 0));
        signupBtn.setForeground(Color.WHITE);
        signupBtn.setFocusPainted(false);
        signupBtn.setBounds(50, yPos, 380, 45);
        signupBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        signupBtn.addActionListener(e -> signup());
        formPanel.add(signupBtn);

        yPos += 55;

        // Login link
        JLabel loginLink = new JLabel("Already have an account? Login");
        loginLink.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        loginLink.setForeground(new Color(70, 130, 180));
        loginLink.setBounds(130, yPos, 250, 25);
        loginLink.setCursor(new Cursor(Cursor.HAND_CURSOR));
        loginLink.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                new login().setVisible(true);
                dispose();
            }
        });
        formPanel.add(loginLink);

        // Wrap in scroll pane
        JScrollPane scrollPane = new JScrollPane(formPanel);
        scrollPane.setBounds(0, 0, 500, 900);
        scrollPane.setBorder(null);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        rightPanel.add(scrollPane);

        return rightPanel;
    }

    private JPanel createPasswordPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(null);
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200), 1),
                BorderFactory.createEmptyBorder(0, 0, 0, 0)
        ));
        return panel;
    }

    private void addFormField(JPanel panel, String labelText, JTextField field, int yPos) {
        JLabel label = new JLabel(labelText);
        label.setFont(LABEL_FONT);
        label.setBounds(50, yPos, 300, 25);
        panel.add(label);

        field.setFont(INPUT_FONT);
        field.setBounds(50, yPos + 28, 380, 35);
        field.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200), 1),
                BorderFactory.createEmptyBorder(5, 10, 5, 10)
        ));
        panel.add(field);
    }

    private void addExampleLabel(JPanel panel, String text, int yPos) {
        JLabel example = new JLabel(text);
        example.setFont(new Font("Segoe UI", Font.ITALIC, 11));
        example.setForeground(new Color(108, 117, 125));
        example.setBounds(50, yPos + 66, 380, 15);
        panel.add(example);
    }

    private void addSecurityQuestion(JPanel panel, String labelText, JComboBox<String> combo, JTextField field, int yPos) {
        JLabel label = new JLabel(labelText);
        label.setFont(LABEL_FONT);
        label.setBounds(50, yPos, 150, 25);
        panel.add(label);

        combo.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        combo.setBounds(50, yPos + 28, 380, 30);
        panel.add(combo);

        JLabel answerLabel = new JLabel("Answer:");
        answerLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        answerLabel.setBounds(50, yPos + 63, 100, 20);
        panel.add(answerLabel);

        field.setFont(INPUT_FONT);
        field.setBounds(120, yPos + 60, 310, 30);
        field.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200), 1),
                BorderFactory.createEmptyBorder(5, 10, 5, 10)
        ));
        panel.add(field);
    }

    private void togglePassword1() {
        password1Visible = !password1Visible;
        if (password1Visible) {
            passwordField.setEchoChar((char) 0);
            showPasswordButton1.setText("👁‍🗨");
        } else {
            passwordField.setEchoChar('•');
            showPasswordButton1.setText("👁");
        }
    }

    private void togglePassword2() {
        password2Visible = !password2Visible;
        if (password2Visible) {
            confirmPasswordField.setEchoChar((char) 0);
            showPasswordButton2.setText("👁‍🗨");
        } else {
            confirmPasswordField.setEchoChar('•');
            showPasswordButton2.setText("👁");
        }
    }

    private void signup() {
        String name = nameField.getText().trim();
        String username = usernameField.getText().trim();
        String email = emailField.getText().trim();
        String dob = dobField.getText().trim();
        String pass = new String(passwordField.getPassword());
        String confirmPass = new String(confirmPasswordField.getPassword());

        String sq1 = (String) securityQuestion1Combo.getSelectedItem();
        String sa1 = securityAnswer1Field.getText().trim();
        String sq2 = (String) securityQuestion2Combo.getSelectedItem();
        String sa2 = securityAnswer2Field.getText().trim();
        String sq3 = (String) securityQuestion3Combo.getSelectedItem();
        String sa3 = securityAnswer3Field.getText().trim();

        // Basic validation
        if (name.isEmpty() || username.isEmpty() || email.isEmpty() || dob.isEmpty() || pass.isEmpty()) {
            JOptionPane.showMessageDialog(this, "All fields are required!", "Validation Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Email format validation
        if (!EmailValidator.isValidEmail(email)) {
            JOptionPane.showMessageDialog(this,
                    EmailValidator.getEmailErrorMessage(email),
                    "Invalid Email Format",
                    JOptionPane.ERROR_MESSAGE);
            emailField.requestFocus();
            return;
        }

        // Security questions validation
        if (sa1.isEmpty() || sa2.isEmpty() || sa3.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please answer all three security questions!", "Validation Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (sq1.equals(sq2) || sq2.equals(sq3) || sq1.equals(sq3)) {
            JOptionPane.showMessageDialog(this, "Please select three different security questions!", "Validation Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Date validation
        if (!DATE_PATTERN.matcher(dob).matches()) {
            JOptionPane.showMessageDialog(this, "Invalid date format. Use YYYY-MM-DD (e.g., 2000-01-15)", "Validation Error", JOptionPane.ERROR_MESSAGE);
            dobField.requestFocus();
            return;
        }

        try {
            LocalDate birthDate = LocalDate.parse(dob);
            LocalDate today = LocalDate.now();

            if (birthDate.isAfter(today)) {
                JOptionPane.showMessageDialog(this, "Date of birth cannot be in the future!", "Validation Error", JOptionPane.ERROR_MESSAGE);
                dobField.requestFocus();
                return;
            }

            int age = today.getYear() - birthDate.getYear();
            if (age < 13) {
                JOptionPane.showMessageDialog(this, "You must be at least 13 years old to register.", "Validation Error", JOptionPane.ERROR_MESSAGE);
                dobField.requestFocus();
                return;
            }
        } catch (DateTimeParseException ex) {
            JOptionPane.showMessageDialog(this, "Invalid date. Please check the date format.", "Validation Error", JOptionPane.ERROR_MESSAGE);
            dobField.requestFocus();
            return;
        }

        // Password validation
        if (!pass.equals(confirmPass)) {
            JOptionPane.showMessageDialog(this, "Passwords do not match!", "Validation Error", JOptionPane.ERROR_MESSAGE);
            confirmPasswordField.requestFocus();
            return;
        }

        if (pass.length() < 6) {
            JOptionPane.showMessageDialog(this, "Password must be at least 6 characters long!", "Validation Error", JOptionPane.ERROR_MESSAGE);
            passwordField.requestFocus();
            return;
        }

        // Database insertion
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASS)) {
            String sql = "INSERT INTO users (full_name, username, email, date_of_birth, password_hash, security_question1, security_answer1, security_question2, security_answer2, security_question3, security_answer3) VALUES (?, ?, ?, ?, SHA2(?, 256), ?, SHA2(?, 256), ?, SHA2(?, 256), ?, SHA2(?, 256))";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, name);
            ps.setString(2, username);
            ps.setString(3, email);
            ps.setDate(4, Date.valueOf(dob));
            ps.setString(5, pass);
            ps.setString(6, sq1);
            ps.setString(7, sa1.toLowerCase());
            ps.setString(8, sq2);
            ps.setString(9, sa2.toLowerCase());
            ps.setString(10, sq3);
            ps.setString(11, sa3.toLowerCase());
            ps.executeUpdate();

            JOptionPane.showMessageDialog(this,
                    "Account created successfully!\nPlease login to continue.\n\nIMPORTANT: Remember your security answers for password recovery!",
                    "Success",
                    JOptionPane.INFORMATION_MESSAGE);
            new login().setVisible(true);
            dispose();
        } catch (SQLIntegrityConstraintViolationException ex) {
            JOptionPane.showMessageDialog(this,
                    "Username or Email already exists!\nPlease use a different username/email.",
                    "Registration Error",
                    JOptionPane.ERROR_MESSAGE);
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this,
                    "Database error: " + ex.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }
}