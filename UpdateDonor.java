package projectiv;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.time.LocalDate;
import java.time.Period;
import java.time.format.DateTimeParseException;
import java.time.temporal.ChronoUnit;
import java.util.regex.Pattern;

class UpdateDonorFrame extends JFrame {
    private JLabel donorIdLabel;
    private JTextField nameField;
    private JTextField fatherNameField;
    private JTextField dobField;
    private JLabel calculatedAgeLabel;
    private JComboBox<String> genderCombo;
    private JComboBox<String> bloodGroupCombo;
    private JTextField phoneField;
    private JTextField donatedCityField;
    private JTextField lastDonationDateField;
    private JTextArea permanentAddressArea;
    private JTextArea temporaryAddressArea;
    private JButton updateButton;
    private JButton clearButton;
    private JButton backButton;

    private Donor donor;

    private static final Pattern PHONE_PATTERN = Pattern.compile("^[0-9]{10,15}$");
    private static final Pattern NAME_PATTERN = Pattern.compile("^[a-zA-Z\\s]{2,50}$");
    private static final Pattern DATE_PATTERN = Pattern.compile("^\\d{4}-\\d{2}-\\d{2}$");

    public UpdateDonorFrame(Donor donor) {
        this.donor = donor;

        setTitle("Update Donor");
        setSize(620, 820);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setResizable(false);

        initComponents();
        populateFields();
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
        JPanel mainPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);

                GradientPaint gradient = new GradientPaint(
                        0, 0, new Color(248, 249, 250),
                        0, getHeight(), new Color(233, 236, 239)
                );
                g2d.setPaint(gradient);
                g2d.fillRect(0, 0, getWidth(), getHeight());
            }
        };
        mainPanel.setLayout(new BorderLayout());

        donorIdLabel = new JLabel("Donor ID: " + donor.getId());
        donorIdLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        donorIdLabel.setForeground(new Color(0, 123, 255));

        nameField = new JTextField(20);
        fatherNameField = new JTextField(20);
        dobField = new JTextField(12);
        calculatedAgeLabel = new JLabel("Age: --");
        calculatedAgeLabel.setFont(new Font("Segoe UI", Font.BOLD, 12));
        calculatedAgeLabel.setForeground(new Color(0, 123, 255));

        genderCombo = new JComboBox<>(new String[]{"Male", "Female", "Other"});
        bloodGroupCombo = new JComboBox<>(new String[]{"A+", "A-", "B+", "B-", "AB+", "AB-", "O+", "O-"});
        phoneField = new JTextField(15);
        donatedCityField = new JTextField(20);
        lastDonationDateField = new JTextField(12);
        permanentAddressArea = new JTextArea(4, 20);
        temporaryAddressArea = new JTextArea(4, 20);

        permanentAddressArea.setLineWrap(true);
        permanentAddressArea.setWrapStyleWord(true);
        temporaryAddressArea.setLineWrap(true);
        temporaryAddressArea.setWrapStyleWord(true);

        updateButton = new JButton("Update");
        clearButton = new JButton("Clear");
        backButton = new JButton("Back");

        JPanel form = new JPanel(new GridBagLayout());
        form.setBackground(Color.WHITE);
        form.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createEmptyBorder(20, 20, 20, 20),
                BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(new Color(200, 200, 200), 1),
                        BorderFactory.createEmptyBorder(20, 20, 20, 20)
                )
        ));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.fill = GridBagConstraints.HORIZONTAL;

        gbc.weightx = 0;
        gbc.anchor = GridBagConstraints.EAST;
        form.add(new JLabel("Donor ID:"), gbc);
        gbc.gridx = 1;
        gbc.weightx = 1;
        gbc.anchor = GridBagConstraints.WEST;
        form.add(donorIdLabel, gbc);
        gbc.gridx = 0;
        gbc.gridy++;

        addLabeledField(form, gbc, "Full Name *:", nameField);
        addLabeledField(form, gbc, "Father Name *:", fatherNameField);

        gbc.weightx = 0;
        gbc.anchor = GridBagConstraints.EAST;
        JLabel dobLabel = new JLabel("Date of Birth (YYYY-MM-DD) *:");
        dobLabel.setToolTipText("Enter date in format: YYYY-MM-DD (e.g., 1990-05-15)");
        form.add(dobLabel, gbc);

        gbc.gridx = 1;
        gbc.weightx = 1;
        gbc.anchor = GridBagConstraints.WEST;
        JPanel dobPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        dobPanel.setBackground(Color.WHITE);
        dobField.setPreferredSize(new Dimension(150, 25));
        dobPanel.add(dobField);
        dobPanel.add(calculatedAgeLabel);
        form.add(dobPanel, gbc);

        gbc.gridx = 0;
        gbc.gridy++;

        addLabeledField(form, gbc, "Gender *:", genderCombo);
        addLabeledField(form, gbc, "Blood Group *:", bloodGroupCombo);
        addLabeledField(form, gbc, "Phone Number *:", phoneField);
        addLabeledField(form, gbc, "Donated City *:", donatedCityField);
        addLabeledField(form, gbc, "Last Donation Date (YYYY-MM-DD):", lastDonationDateField);
        addLabeledArea(form, gbc, "Permanent Address:", permanentAddressArea);
        addLabeledArea(form, gbc, "Temporary Address:", temporaryAddressArea);

        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.CENTER, 14, 14));
        buttons.setOpaque(false);
        buttons.add(updateButton);
        buttons.add(clearButton);
        buttons.add(backButton);

        JPanel centerWrapper = new JPanel(new FlowLayout(FlowLayout.CENTER));
        centerWrapper.setOpaque(false);
        centerWrapper.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        centerWrapper.add(form);

        mainPanel.add(centerWrapper, BorderLayout.CENTER);
        mainPanel.add(buttons, BorderLayout.SOUTH);

        add(mainPanel);
    }

    private void addLabeledField(JPanel panel, GridBagConstraints gbc, String label, JComponent field) {
        gbc.weightx = 0;
        gbc.anchor = GridBagConstraints.EAST;
        panel.add(new JLabel(label), gbc);

        gbc.gridx = 1;
        gbc.weightx = 1;
        gbc.anchor = GridBagConstraints.WEST;
        panel.add(field, gbc);

        gbc.gridx = 0;
        gbc.gridy++;
    }

    private void addLabeledArea(JPanel panel, GridBagConstraints gbc, String label, JTextArea area) {
        gbc.weightx = 0;
        gbc.anchor = GridBagConstraints.NORTHEAST;
        panel.add(new JLabel(label), gbc);

        gbc.gridx = 1;
        gbc.weightx = 1;
        gbc.anchor = GridBagConstraints.WEST;

        JScrollPane scroll = new JScrollPane(area);
        scroll.setPreferredSize(new Dimension(250, 80));

        panel.add(scroll, gbc);

        gbc.gridx = 0;
        gbc.gridy++;
    }

    private void populateFields() {
        nameField.setText(donor.getName());
        fatherNameField.setText(donor.getFatherName());
        dobField.setText(donor.getDateOfBirth());
        calculateAndDisplayAge();
        genderCombo.setSelectedItem(donor.getGender());
        bloodGroupCombo.setSelectedItem(donor.getBloodGroup());
        phoneField.setText(donor.getPhone());
        donatedCityField.setText(donor.getDonatedCity());
        lastDonationDateField.setText(donor.getLastDonationDate());
        permanentAddressArea.setText(donor.getPermanentAddress());
        temporaryAddressArea.setText(donor.getTemporaryAddress());
    }

    private void addListeners() {
        phoneField.addKeyListener(createDigitFilter());

        dobField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                calculateAndDisplayAge();
            }
        });

        updateButton.addActionListener(e -> onUpdate());
        clearButton.addActionListener(e -> populateFields());
        backButton.addActionListener(e -> dispose());
    }

    private KeyAdapter createDigitFilter() {
        return new KeyAdapter() {
            @Override
            public void keyTyped(KeyEvent e) {
                char c = e.getKeyChar();
                if (!Character.isDigit(c) && c != KeyEvent.VK_BACK_SPACE) {
                    e.consume();
                }
            }
        };
    }

    private void calculateAndDisplayAge() {
        String dobText = dobField.getText().trim();

        if (dobText.isEmpty()) {
            calculatedAgeLabel.setText("Age: --");
            calculatedAgeLabel.setForeground(new Color(100, 100, 100));
            return;
        }

        if (!DATE_PATTERN.matcher(dobText).matches()) {
            calculatedAgeLabel.setText("Age: Invalid format");
            calculatedAgeLabel.setForeground(Color.RED);
            return;
        }

        try {
            LocalDate birthDate = LocalDate.parse(dobText);
            LocalDate today = LocalDate.now();

            if (birthDate.isAfter(today)) {
                calculatedAgeLabel.setText("Age: Future date!");
                calculatedAgeLabel.setForeground(Color.RED);
                return;
            }

            Period period = Period.between(birthDate, today);
            int age = period.getYears();

            int minAge = SystemSettings.getMinDonorAge();
            int maxAge = SystemSettings.getMaxDonorAge();

            if (age < minAge || age > maxAge) {
                calculatedAgeLabel.setText(String.format("Age: %d (Not eligible)", age));
                calculatedAgeLabel.setForeground(Color.RED);
            } else {
                calculatedAgeLabel.setText(String.format("Age: %d years", age));
                calculatedAgeLabel.setForeground(new Color(40, 167, 69));
            }

        } catch (DateTimeParseException ex) {
            calculatedAgeLabel.setText("Age: Invalid date");
            calculatedAgeLabel.setForeground(Color.RED);
        }
    }

    private void onUpdate() {
        if (!validateForm()) return;

        String name = nameField.getText().trim();
        String father = fatherNameField.getText().trim();
        String dob = dobField.getText().trim();
        int age = calculateAge(dob);
        String gender = (String) genderCombo.getSelectedItem();
        String bloodGroup = (String) bloodGroupCombo.getSelectedItem();
        String phone = phoneField.getText().trim();
        String donatedCity = donatedCityField.getText().trim();
        String lastDonationDate = lastDonationDateField.getText().trim();
        String permanentAddress = permanentAddressArea.getText().trim();
        String temporaryAddress = temporaryAddressArea.getText().trim();

        if (!validateDonationInterval(lastDonationDate)) return;

        try {
            boolean success = DatabaseUtil.updateDonorWithDOB(
                    donor.getId(), name, father, dob, age, gender, bloodGroup, phone,
                    donatedCity, lastDonationDate, permanentAddress, temporaryAddress
            );

            if (success) {
                JOptionPane.showMessageDialog(this,
                        String.format("Donor updated successfully!\n\n" +
                                        "Donor ID: #%d\n" +
                                        "Name: %s\n" +
                                        "Age: %d years\n" +
                                        "Phone: %s\n" +
                                        "Blood Group: %s",
                                donor.getId(), name, age, phone, bloodGroup),
                        "Success",
                        JOptionPane.INFORMATION_MESSAGE);
                dispose();
            } else {
                JOptionPane.showMessageDialog(this,
                        "Failed to update donor. Please try again.",
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
            }

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this,
                    "Database error: " + ex.getMessage(),
                    "Database Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private int calculateAge(String dobStr) {
        try {
            LocalDate birthDate = LocalDate.parse(dobStr);
            return Period.between(birthDate, LocalDate.now()).getYears();
        } catch (DateTimeParseException ex) {
            return 0;
        }
    }

    private boolean validateDonationInterval(String lastDonationDate) {
        if (lastDonationDate == null || lastDonationDate.isEmpty()) {
            return true;
        }
        try {
            LocalDate last = LocalDate.parse(lastDonationDate);
            LocalDate today = LocalDate.now();
            long daysBetween = ChronoUnit.DAYS.between(last, today);
            if (daysBetween < 0) {
                JOptionPane.showMessageDialog(this,
                        "Last donation date cannot be in the future.",
                        "Date Error",
                        JOptionPane.ERROR_MESSAGE);
                lastDonationDateField.requestFocus();
                return false;
            }
            return true;
        } catch (DateTimeParseException ex) {
            JOptionPane.showMessageDialog(this,
                    "Invalid last donation date format. Use YYYY-MM-DD.",
                    "Date Error",
                    JOptionPane.ERROR_MESSAGE);
            lastDonationDateField.requestFocus();
            return false;
        }
    }

    private boolean validateForm() {
        String name = nameField.getText().trim();
        if (name.isEmpty()) { showError("Please enter the donor's name."); nameField.requestFocus(); return false; }
        if (!NAME_PATTERN.matcher(name).matches()) { showError("Name should contain only letters and spaces (2-50 characters)."); nameField.requestFocus(); return false; }

        String father = fatherNameField.getText().trim();
        if (father.isEmpty()) { showError("Please enter father's name."); fatherNameField.requestFocus(); return false; }
        if (!NAME_PATTERN.matcher(father).matches()) { showError("Father name should contain only letters and spaces (2-50 characters)."); fatherNameField.requestFocus(); return false; }

        String dobText = dobField.getText().trim();
        if (dobText.isEmpty()) { showError("Please enter date of birth."); dobField.requestFocus(); return false; }
        if (!DATE_PATTERN.matcher(dobText).matches()) {
            showError("Date of birth must be in YYYY-MM-DD format (e.g., 1990-05-15).");
            dobField.requestFocus();
            return false;
        }

        try {
            LocalDate birthDate = LocalDate.parse(dobText);
            LocalDate today = LocalDate.now();

            if (birthDate.isAfter(today)) {
                showError("Date of birth cannot be in the future.");
                dobField.requestFocus();
                return false;
            }

            int age = Period.between(birthDate, today).getYears();
            int minAge = SystemSettings.getMinDonorAge();
            int maxAge = SystemSettings.getMaxDonorAge();

            if (age < minAge || age > maxAge) {
                showError(String.format("Donor must be between %d and %d years old. Current age: %d",
                        minAge, maxAge, age));
                dobField.requestFocus();
                return false;
            }
        } catch (DateTimeParseException ex) {
            showError("Invalid date of birth. Please check the date format.");
            dobField.requestFocus();
            return false;
        }

        String phone = phoneField.getText().trim();
        if (phone.isEmpty()) { showError("Please enter phone number."); phoneField.requestFocus(); return false; }
        if (!PHONE_PATTERN.matcher(phone).matches()) { showError("Please enter a valid phone number (10-15 digits)."); phoneField.requestFocus(); return false; }

        String donatedCity = donatedCityField.getText().trim();
        if (donatedCity.isEmpty()) { showError("Please enter donated city."); donatedCityField.requestFocus(); return false; }

        String lastDonationDate = lastDonationDateField.getText().trim();
        if (!lastDonationDate.isEmpty() && !DATE_PATTERN.matcher(lastDonationDate).matches()) {
            showError("Last Donation Date must be in YYYY-MM-DD format."); lastDonationDateField.requestFocus(); return false;
        }
        return true;
    }

    private void showError(String message) {
        JOptionPane.showMessageDialog(this, message, "Validation Error", JOptionPane.ERROR_MESSAGE);
    }
}