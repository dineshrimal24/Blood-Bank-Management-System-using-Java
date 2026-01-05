package projectiv;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import java.io.File;
import java.net.URL;

public class SettingsFrame extends JFrame {
    // Database Settings
    private JTextField dbHostField;
    private JTextField dbPortField;
    private JTextField dbNameField;
    private JTextField dbUserField;
    private JPasswordField dbPasswordField;

    // System Settings
    private JSpinner minDonationIntervalSpinner;
    private JSpinner minDonorAgeSpinner;
    private JSpinner maxDonorAgeSpinner;
    private JSpinner freshBloodThresholdSpinner;

    // Buttons
    private JButton testConnectionButton;
    private JButton saveButton;
    private JButton resetButton;
    private JButton backButton;

    // Professional color scheme
    private static final Color PRIMARY_COLOR = new Color(220, 53, 69);
    private static final Color ACCENT_BLUE = new Color(0, 123, 255);
    private static final Color ACCENT_GREEN = new Color(40, 167, 69);
    private static final Color BACKGROUND_LIGHT = new Color(245, 245, 245);

    public SettingsFrame() {
        setTitle("System Settings");
        setSize(900, 700);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setResizable(true);

        initComponents();
        loadCurrentSettings();
        addListeners();

        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosed(WindowEvent e) {
                try {
                    DashboardFrame dashboardFrame = new DashboardFrame();
                    dashboardFrame.setVisible(true);
                } catch (Exception ignored) { }
            }
        });
    }

    private void initComponents() {
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BorderLayout());
        mainPanel.setBackground(BACKGROUND_LIGHT);

        // Header Panel
        JPanel headerPanel = createHeaderPanel();
        mainPanel.add(headerPanel, BorderLayout.NORTH);

        // Center Panel - Settings
        JScrollPane scrollPane = new JScrollPane(createCenterPanel());
        scrollPane.setBorder(null);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        mainPanel.add(scrollPane, BorderLayout.CENTER);

        // Bottom Panel - Buttons
        JPanel bottomPanel = createBottomPanel();
        mainPanel.add(bottomPanel, BorderLayout.SOUTH);

        add(mainPanel);
    }

    private JPanel createHeaderPanel() {
        JPanel headerPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                GradientPaint gradient = new GradientPaint(
                        0, 0, PRIMARY_COLOR,
                        getWidth(), 0, new Color(185, 28, 46)
                );
                g2d.setPaint(gradient);
                g2d.fillRect(0, 0, getWidth(), getHeight());
            }
        };
        headerPanel.setPreferredSize(new Dimension(0, 100));
        headerPanel.setLayout(new BorderLayout());

        JPanel titlePanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 30, 0));
        titlePanel.setOpaque(false);
        titlePanel.setBorder(BorderFactory.createEmptyBorder(30, 0, 30, 0));

        JLabel iconLabel = createIconLabel();
        titlePanel.add(iconLabel);

        JLabel titleLabel = new JLabel("System Settings");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 28));
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setVerticalAlignment(SwingConstants.CENTER);
        titlePanel.add(titleLabel);

        headerPanel.add(titlePanel, BorderLayout.WEST);

        return headerPanel;
    }

    private JLabel createIconLabel() {
        String resourcePath = "/icons/settings.png";
        URL resource = getClass().getResource(resourcePath);

        if (resource != null) {
            ImageIcon icon = new ImageIcon(resource);
            Image scaled = icon.getImage().getScaledInstance(36, 36, Image.SCALE_SMOOTH);
            return new JLabel(new ImageIcon(scaled));
        }

        File fallback = new File("src/icons/settings.png");
        if (fallback.exists()) {
            ImageIcon icon = new ImageIcon(fallback.getAbsolutePath());
            Image scaled = icon.getImage().getScaledInstance(36, 36, Image.SCALE_SMOOTH);
            return new JLabel(new ImageIcon(scaled));
        }

        JLabel label = new JLabel("⚙️");
        label.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 36));
        label.setVerticalAlignment(SwingConstants.CENTER);
        return label;
    }

    private JPanel createCenterPanel() {
        JPanel centerPanel = new JPanel();
        centerPanel.setLayout(new BoxLayout(centerPanel, BoxLayout.Y_AXIS));
        centerPanel.setBackground(BACKGROUND_LIGHT);
        centerPanel.setBorder(BorderFactory.createEmptyBorder(20, 30, 20, 30));

        // Database Settings Section
        centerPanel.add(createDatabaseSettingsPanel());
        centerPanel.add(Box.createVerticalStrut(20));

        // System Settings Section
        centerPanel.add(createSystemSettingsPanel());
        centerPanel.add(Box.createVerticalStrut(20));

        return centerPanel;
    }

    private JPanel createDatabaseSettingsPanel() {
        JPanel section = new JPanel();
        section.setLayout(new BoxLayout(section, BoxLayout.Y_AXIS));
        section.setBackground(Color.WHITE);
        section.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200), 1),
                BorderFactory.createEmptyBorder(20, 25, 20, 25)
        ));
        section.setMaximumSize(new Dimension(840, 400));

        JLabel sectionTitle = new JLabel("Database Configuration");
        sectionTitle.setFont(new Font("Segoe UI", Font.BOLD, 20));
        sectionTitle.setForeground(new Color(33, 37, 41));
        sectionTitle.setAlignmentX(Component.LEFT_ALIGNMENT);
        section.add(sectionTitle);
        section.add(Box.createVerticalStrut(20));

        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBackground(Color.WHITE);
        formPanel.setAlignmentX(Component.LEFT_ALIGNMENT);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.WEST;

        gbc.gridx = 0; gbc.gridy = 0; gbc.weightx = 0;
        formPanel.add(createLabel("Database Host:"), gbc);
        gbc.gridx = 1; gbc.weightx = 1;
        dbHostField = createTextField("localhost");
        formPanel.add(dbHostField, gbc);

        gbc.gridx = 0; gbc.gridy = 1; gbc.weightx = 0;
        formPanel.add(createLabel("Port:"), gbc);
        gbc.gridx = 1; gbc.weightx = 1;
        dbPortField = createTextField("3306");
        formPanel.add(dbPortField, gbc);

        gbc.gridx = 0; gbc.gridy = 2; gbc.weightx = 0;
        formPanel.add(createLabel("Database Name:"), gbc);
        gbc.gridx = 1; gbc.weightx = 1;
        dbNameField = createTextField("bloodbank");
        formPanel.add(dbNameField, gbc);

        gbc.gridx = 0; gbc.gridy = 3; gbc.weightx = 0;
        formPanel.add(createLabel("Username:"), gbc);
        gbc.gridx = 1; gbc.weightx = 1;
        dbUserField = createTextField("root");
        formPanel.add(dbUserField, gbc);

        gbc.gridx = 0; gbc.gridy = 4; gbc.weightx = 0;
        formPanel.add(createLabel("Password:"), gbc);
        gbc.gridx = 1; gbc.weightx = 1;
        dbPasswordField = new JPasswordField(20);
        dbPasswordField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        dbPasswordField.setPreferredSize(new Dimension(0, 35));
        dbPasswordField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200), 1),
                BorderFactory.createEmptyBorder(5, 10, 5, 10)
        ));
        formPanel.add(dbPasswordField, gbc);

        section.add(formPanel);
        section.add(Box.createVerticalStrut(15));

        testConnectionButton = new JButton("Test Connection");
        styleSmallButton(testConnectionButton, ACCENT_BLUE);
        testConnectionButton.setAlignmentX(Component.LEFT_ALIGNMENT);
        section.add(testConnectionButton);

        return section;
    }

    private JPanel createSystemSettingsPanel() {
        JPanel section = new JPanel();
        section.setLayout(new BoxLayout(section, BoxLayout.Y_AXIS));
        section.setBackground(Color.WHITE);
        section.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200), 1),
                BorderFactory.createEmptyBorder(20, 25, 20, 25)
        ));
        section.setMaximumSize(new Dimension(840, 350));

        JLabel sectionTitle = new JLabel("System Configuration");
        sectionTitle.setFont(new Font("Segoe UI", Font.BOLD, 20));
        sectionTitle.setForeground(new Color(33, 37, 41));
        sectionTitle.setAlignmentX(Component.LEFT_ALIGNMENT);
        section.add(sectionTitle);
        section.add(Box.createVerticalStrut(20));

        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBackground(Color.WHITE);
        formPanel.setAlignmentX(Component.LEFT_ALIGNMENT);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.WEST;

        gbc.gridx = 0; gbc.gridy = 0; gbc.weightx = 0;
        formPanel.add(createLabel("Minimum Donation Interval (days):"), gbc);
        gbc.gridx = 1; gbc.weightx = 1;
        minDonationIntervalSpinner = createSpinner(90, 30, 365, 1);
        formPanel.add(minDonationIntervalSpinner, gbc);

        gbc.gridx = 0; gbc.gridy = 1; gbc.weightx = 0;
        formPanel.add(createLabel("Minimum Donor Age:"), gbc);
        gbc.gridx = 1; gbc.weightx = 1;
        minDonorAgeSpinner = createSpinner(18, 16, 25, 1);
        formPanel.add(minDonorAgeSpinner, gbc);

        gbc.gridx = 0; gbc.gridy = 2; gbc.weightx = 0;
        formPanel.add(createLabel("Maximum Donor Age:"), gbc);
        gbc.gridx = 1; gbc.weightx = 1;
        maxDonorAgeSpinner = createSpinner(65, 50, 80, 1);
        formPanel.add(maxDonorAgeSpinner, gbc);

        gbc.gridx = 0; gbc.gridy = 3; gbc.weightx = 0;
        formPanel.add(createLabel("Fresh Blood Threshold (days):"), gbc);
        gbc.gridx = 1; gbc.weightx = 1;
        freshBloodThresholdSpinner = createSpinner(2, 1, 7, 1);
        formPanel.add(freshBloodThresholdSpinner, gbc);

        // Add info label
        gbc.gridx = 0; gbc.gridy = 4; gbc.gridwidth = 2;
        JLabel infoLabel = new JLabel("(Blood ≤ threshold days = Fresh, > threshold days = Old)");
        infoLabel.setFont(new Font("Segoe UI", Font.ITALIC, 11));
        infoLabel.setForeground(new Color(100, 100, 100));
        formPanel.add(infoLabel, gbc);

        section.add(formPanel);

        return section;
    }

    private JLabel createLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("Segoe UI", Font.BOLD, 14));
        label.setForeground(new Color(33, 37, 41));
        return label;
    }

    private JTextField createTextField(String text) {
        JTextField field = new JTextField(text, 20);
        field.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        field.setPreferredSize(new Dimension(0, 35));
        field.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200), 1),
                BorderFactory.createEmptyBorder(5, 10, 5, 10)
        ));
        return field;
    }

    private JSpinner createSpinner(int value, int min, int max, int step) {
        SpinnerNumberModel model = new SpinnerNumberModel(value, min, max, step);
        JSpinner spinner = new JSpinner(model);
        spinner.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        spinner.setPreferredSize(new Dimension(150, 35));

        JComponent editor = spinner.getEditor();
        if (editor instanceof JSpinner.DefaultEditor) {
            ((JSpinner.DefaultEditor) editor).getTextField().setFont(new Font("Segoe UI", Font.PLAIN, 14));
        }

        return spinner;
    }

    private JPanel createBottomPanel() {
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 20));
        bottomPanel.setBackground(BACKGROUND_LIGHT);

        saveButton = new JButton("Save Settings");
        styleButton(saveButton, ACCENT_GREEN);

        resetButton = new JButton("Reset to Defaults");
        styleButton(resetButton, new Color(253, 126, 20));

        backButton = new JButton("Back to Dashboard");
        styleButton(backButton, new Color(108, 117, 125));

        bottomPanel.add(saveButton);
        bottomPanel.add(resetButton);
        bottomPanel.add(backButton);

        return bottomPanel;
    }

    private void styleButton(JButton button, Color color) {
        button.setFont(new Font("Segoe UI", Font.BOLD, 15));
        button.setBackground(color);
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setPreferredSize(new Dimension(200, 45));
        button.setBorderPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));

        button.addMouseListener(new MouseAdapter() {
            Color originalColor = color;

            @Override
            public void mouseEntered(MouseEvent e) {
                button.setBackground(color.darker());
            }

            @Override
            public void mouseExited(MouseEvent e) {
                button.setBackground(originalColor);
            }
        });
    }

    private void styleSmallButton(JButton button, Color color) {
        button.setFont(new Font("Segoe UI", Font.BOLD, 13));
        button.setBackground(color);
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setPreferredSize(new Dimension(160, 35));
        button.setBorderPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));

        button.addMouseListener(new MouseAdapter() {
            Color originalColor = color;

            @Override
            public void mouseEntered(MouseEvent e) {
                button.setBackground(color.darker());
            }

            @Override
            public void mouseExited(MouseEvent e) {
                button.setBackground(originalColor);
            }
        });
    }

    private void addListeners() {
        testConnectionButton.addActionListener(e -> testDatabaseConnection());
        saveButton.addActionListener(e -> saveSettings());
        resetButton.addActionListener(e -> resetToDefaults());
        backButton.addActionListener(e -> dispose());
    }

    private void loadCurrentSettings() {
        dbHostField.setText("localhost");
        dbPortField.setText("3306");
        dbNameField.setText("bloodbank");
        dbUserField.setText("root");
        dbPasswordField.setText("");

        minDonationIntervalSpinner.setValue(SystemSettings.getMinDonationInterval());
        minDonorAgeSpinner.setValue(SystemSettings.getMinDonorAge());
        maxDonorAgeSpinner.setValue(SystemSettings.getMaxDonorAge());
        freshBloodThresholdSpinner.setValue(SystemSettings.getFreshBloodThreshold());
    }

    private void testDatabaseConnection() {
        String host = dbHostField.getText().trim();
        String port = dbPortField.getText().trim();
        String dbName = dbNameField.getText().trim();
        String user = dbUserField.getText().trim();
        String password = new String(dbPasswordField.getPassword());

        String url = "jdbc:mysql://" + host + ":" + port + "/" + dbName;

        try {
            Connection conn = DriverManager.getConnection(url, user, password);
            conn.close();

            JOptionPane.showMessageDialog(this,
                    "✓ Connection successful!\n\nSuccessfully connected to the database.",
                    "Connection Test",
                    JOptionPane.INFORMATION_MESSAGE);
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this,
                    "✗ Connection failed!\n\n" + ex.getMessage(),
                    "Connection Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void saveSettings() {
        int confirm = JOptionPane.showConfirmDialog(this,
                "Are you sure you want to save these settings?\n" +
                        "Note: Database settings require application restart to take effect.",
                "Save Settings",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE);

        if (confirm != JOptionPane.YES_OPTION) {
            return;
        }

        SystemSettings.setMinDonationInterval((int) minDonationIntervalSpinner.getValue());
        SystemSettings.setMinDonorAge((int) minDonorAgeSpinner.getValue());
        SystemSettings.setMaxDonorAge((int) maxDonorAgeSpinner.getValue());
        SystemSettings.setFreshBloodThreshold((int) freshBloodThresholdSpinner.getValue());

        boolean success = SystemSettings.saveSettings();

        if (success) {
            JOptionPane.showMessageDialog(this,
                    "Settings saved successfully!\n\n" +
                            "System Settings:\n" +
                            "  Min Donation Interval: " + minDonationIntervalSpinner.getValue() + " days\n" +
                            "  Min Donor Age: " + minDonorAgeSpinner.getValue() + "\n" +
                            "  Max Donor Age: " + maxDonorAgeSpinner.getValue() + "\n" +
                            "  Fresh Blood Threshold: " + freshBloodThresholdSpinner.getValue() + " days\n\n" +
                            "Settings have been saved to bloodbank_settings.properties",
                    "Success",
                    JOptionPane.INFORMATION_MESSAGE);
        } else {
            JOptionPane.showMessageDialog(this,
                    "Failed to save settings. Please check file permissions.",
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void resetToDefaults() {
        int confirm = JOptionPane.showConfirmDialog(this,
                "Are you sure you want to reset all settings to defaults?",
                "Reset Settings",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE);

        if (confirm == JOptionPane.YES_OPTION) {
            SystemSettings.resetToDefaults();
            loadCurrentSettings();
            JOptionPane.showMessageDialog(this,
                    "Settings have been reset to default values.",
                    "Reset Complete",
                    JOptionPane.INFORMATION_MESSAGE);
        }
    }
}