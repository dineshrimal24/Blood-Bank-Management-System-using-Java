package projectiv;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.time.temporal.ChronoUnit;

public class StockIncreaseFrame extends JFrame {
    private JComboBox<String> searchTypeCombo;
    private JTextField searchField;
    private JComboBox<String> bloodGroupSearchCombo;
    private JButton searchButton;
    private JTable resultTable;
    private DefaultTableModel tableModel;
    private JLabel statusLabel;

    private JTextField unitsField;
    private JTextField donationDateField;
    private JLabel currentStockLabel;
    private JLabel selectedDonorLabel;
    private JButton addDonationButton;
    private JButton clearButton;
    private JButton backButton;

    private Donor currentDonor = null;

    public StockIncreaseFrame() {
        setTitle("Stock Increase - Add Blood Donation");
        setSize(1400, 900);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setResizable(true);

        initComponents();
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
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(new Color(245, 245, 245));

        // Header Panel
        JPanel headerPanel = createHeaderPanel();
        mainPanel.add(headerPanel, BorderLayout.NORTH);

        // Center Panel with split layout
        JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
        splitPane.setDividerLocation(400);
        splitPane.setResizeWeight(0.6);

        // Top: Search Panel
        JPanel searchPanel = createSearchPanel();
        splitPane.setTopComponent(new JScrollPane(searchPanel));

        // Bottom: Donation Form Panel
        JPanel donationPanel = createDonationPanel();
        splitPane.setBottomComponent(donationPanel);

        mainPanel.add(splitPane, BorderLayout.CENTER);

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
                        0, 0, new Color(34, 139, 34),
                        getWidth(), 0, new Color(24, 99, 24)
                );
                g2d.setPaint(gradient);
                g2d.fillRect(0, 0, getWidth(), getHeight());
            }
        };
        headerPanel.setPreferredSize(new Dimension(0, 100));
        headerPanel.setLayout(new BorderLayout());

        JPanel titlePanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 30, 25));
        titlePanel.setOpaque(false);

        JLabel iconLabel = new JLabel("📈");
        iconLabel.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 36));
        titlePanel.add(iconLabel);

        JLabel titleLabel = new JLabel("Stock Increase - Add Blood Donation");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 28));
        titleLabel.setForeground(Color.WHITE);
        titlePanel.add(titleLabel);

        headerPanel.add(titlePanel, BorderLayout.WEST);

        return headerPanel;
    }

    private JPanel createSearchPanel() {
        JPanel searchPanel = new JPanel(new BorderLayout());
        searchPanel.setBackground(new Color(245, 245, 245));
        searchPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 10, 20));

        // Top: Search Controls
        JPanel controlPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 10));
        controlPanel.setBackground(Color.WHITE);
        controlPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200), 1),
                BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));

        JLabel searchLabel = new JLabel("Search By:");
        searchLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));

        searchTypeCombo = new JComboBox<>(new String[]{"Phone Number", "Name", "Blood Group"});
        searchTypeCombo.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        searchTypeCombo.setPreferredSize(new Dimension(150, 30));

        searchField = new JTextField(20);
        searchField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        searchField.setPreferredSize(new Dimension(200, 30));

        bloodGroupSearchCombo = new JComboBox<>(new String[]{"A+", "A-", "B+", "B-", "AB+", "AB-", "O+", "O-"});
        bloodGroupSearchCombo.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        bloodGroupSearchCombo.setPreferredSize(new Dimension(100, 30));
        bloodGroupSearchCombo.setVisible(false);

        searchButton = new JButton("Search Donor");
        styleButton(searchButton, new Color(70, 130, 180), 140, 35);

        controlPanel.add(searchLabel);
        controlPanel.add(searchTypeCombo);
        controlPanel.add(new JLabel("Value:"));
        controlPanel.add(searchField);
        controlPanel.add(bloodGroupSearchCombo);
        controlPanel.add(searchButton);

        // Status Label
        statusLabel = new JLabel("Enter search criteria and click Search to find eligible donors");
        statusLabel.setFont(new Font("Segoe UI", Font.ITALIC, 12));
        statusLabel.setForeground(new Color(100, 100, 100));
        statusLabel.setBorder(BorderFactory.createEmptyBorder(10, 15, 5, 15));

        // Table for results
        String[] columnNames = {"ID", "Name", "Father Name", "Blood Group", "Phone", "Age",
                "Last Donation", "Eligibility Status"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        resultTable = new JTable(tableModel);
        resultTable.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        resultTable.setRowHeight(30);
        resultTable.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 12));
        resultTable.getTableHeader().setBackground(new Color(34, 139, 34));
        resultTable.getTableHeader().setForeground(Color.WHITE);
        resultTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        // Set column widths
        resultTable.getColumnModel().getColumn(0).setPreferredWidth(40);
        resultTable.getColumnModel().getColumn(1).setPreferredWidth(120);
        resultTable.getColumnModel().getColumn(2).setPreferredWidth(120);
        resultTable.getColumnModel().getColumn(3).setPreferredWidth(80);
        resultTable.getColumnModel().getColumn(4).setPreferredWidth(100);
        resultTable.getColumnModel().getColumn(5).setPreferredWidth(50);
        resultTable.getColumnModel().getColumn(6).setPreferredWidth(100);
        resultTable.getColumnModel().getColumn(7).setPreferredWidth(150);

        JScrollPane scrollPane = new JScrollPane(resultTable);
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(200, 200, 200), 1));

        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setOpaque(false);
        topPanel.add(controlPanel, BorderLayout.NORTH);
        topPanel.add(statusLabel, BorderLayout.CENTER);

        searchPanel.add(topPanel, BorderLayout.NORTH);
        searchPanel.add(scrollPane, BorderLayout.CENTER);

        return searchPanel;
    }

    private JPanel createDonationPanel() {
        JPanel donationPanel = new JPanel();
        donationPanel.setLayout(new BoxLayout(donationPanel, BoxLayout.Y_AXIS));
        donationPanel.setBackground(new Color(245, 245, 245));
        donationPanel.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));

        // Selected Donor Info Panel
        JPanel infoPanel = new JPanel();
        infoPanel.setLayout(new BoxLayout(infoPanel, BoxLayout.Y_AXIS));
        infoPanel.setBackground(Color.WHITE);
        infoPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200), 1),
                BorderFactory.createEmptyBorder(15, 15, 15, 15)
        ));
        infoPanel.setMaximumSize(new Dimension(1160, 100));

        selectedDonorLabel = new JLabel("No donor selected - Select a donor from the table above");
        selectedDonorLabel.setFont(new Font("Segoe UI", Font.PLAIN, 13));

        currentStockLabel = new JLabel("Current Blood Stock: --");
        currentStockLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        currentStockLabel.setForeground(new Color(34, 139, 34));

        infoPanel.add(selectedDonorLabel);
        infoPanel.add(Box.createVerticalStrut(10));
        infoPanel.add(currentStockLabel);

        donationPanel.add(infoPanel);
        donationPanel.add(Box.createVerticalStrut(15));

        // Donation Details Panel
        JPanel detailsPanel = new JPanel(new GridBagLayout());
        detailsPanel.setBackground(Color.WHITE);
        detailsPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200), 1),
                BorderFactory.createEmptyBorder(15, 15, 15, 15)
        ));
        detailsPanel.setMaximumSize(new Dimension(1160, 150));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.WEST;

        // Units Field
        gbc.gridx = 0; gbc.gridy = 0;
        JLabel unitsLabel = new JLabel("Blood Units to Add (1 unit = 450ml):");
        unitsLabel.setFont(new Font("Segoe UI", Font.BOLD, 13));
        detailsPanel.add(unitsLabel, gbc);

        gbc.gridx = 1;
        unitsField = new JTextField(10);
        unitsField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        unitsField.setPreferredSize(new Dimension(150, 30));
        unitsField.setEnabled(false);
        unitsField.setText("1");
        detailsPanel.add(unitsField, gbc);

        // Date Field
        gbc.gridx = 2;
        JLabel dateLabel = new JLabel("Donation Date (YYYY-MM-DD):");
        dateLabel.setFont(new Font("Segoe UI", Font.BOLD, 13));
        detailsPanel.add(dateLabel, gbc);

        gbc.gridx = 3;
        donationDateField = new JTextField(12);
        donationDateField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        donationDateField.setPreferredSize(new Dimension(150, 30));
        donationDateField.setEnabled(false);
        donationDateField.setText(LocalDate.now().toString());
        detailsPanel.add(donationDateField, gbc);

        // Add Donation Button
        gbc.gridx = 4;
        addDonationButton = new JButton("Add Donation");
        styleButton(addDonationButton, new Color(34, 139, 34), 150, 35);
        addDonationButton.setEnabled(false);
        detailsPanel.add(addDonationButton, gbc);

        donationPanel.add(detailsPanel);

        return donationPanel;
    }

    private JPanel createBottomPanel() {
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 15));
        bottomPanel.setBackground(new Color(245, 245, 245));

        clearButton = new JButton("Clear All");
        styleButton(clearButton, new Color(108, 117, 125), 140, 40);

        backButton = new JButton("Back to Dashboard");
        styleButton(backButton, new Color(108, 117, 125), 180, 40);

        bottomPanel.add(clearButton);
        bottomPanel.add(backButton);

        return bottomPanel;
    }

    private void styleButton(JButton button, Color color, int width, int height) {
        button.setFont(new Font("Segoe UI", Font.BOLD, 14));
        button.setBackground(color);
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setPreferredSize(new Dimension(width, height));
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
        searchTypeCombo.addActionListener(e -> {
            String searchType = (String) searchTypeCombo.getSelectedItem();
            if ("Blood Group".equals(searchType)) {
                searchField.setVisible(false);
                bloodGroupSearchCombo.setVisible(true);
            } else {
                searchField.setVisible(true);
                bloodGroupSearchCombo.setVisible(false);
            }
        });

        unitsField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyTyped(KeyEvent e) {
                char c = e.getKeyChar();
                if (!Character.isDigit(c) && c != KeyEvent.VK_BACK_SPACE) {
                    e.consume();
                }
            }
        });

        searchButton.addActionListener(e -> performSearch());
        searchField.addActionListener(e -> performSearch());

        resultTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                selectDonorFromTable();
            }
        });

        addDonationButton.addActionListener(e -> addDonation());
        clearButton.addActionListener(e -> clearAll());
        backButton.addActionListener(e -> dispose());
    }

    private void performSearch() {
        String searchType = (String) searchTypeCombo.getSelectedItem();
        String searchValue = searchField.getText().trim();

        if ("Blood Group".equals(searchType)) {
            searchValue = (String) bloodGroupSearchCombo.getSelectedItem();
        } else if (searchValue.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Please enter a search value.",
                    "Input Required",
                    JOptionPane.WARNING_MESSAGE);
            searchField.requestFocus();
            return;
        }

        tableModel.setRowCount(0);

        try {
            ResultSet rs = null;

            switch (searchType) {
                case "Phone Number":
                    rs = searchByPhone(searchValue);
                    break;
                case "Name":
                    rs = DatabaseUtil.searchDonor(searchValue);
                    break;
                case "Blood Group":
                    rs = DatabaseUtil.searchDonorsByBloodGroup(searchValue);
                    break;
            }

            if (rs != null) {
                int count = 0;
                while (rs.next()) {
                    count++;
                    int donorId = rs.getInt("id");
                    String name = rs.getString("name");
                    String fatherName = rs.getString("father_name");
                    String bloodGroup = rs.getString("blood_group");
                    String phone = rs.getString("phone");
                    int age = rs.getInt("age");
                    String lastDonationDate = rs.getString("last_donation_date");

                    String eligibilityStatus = checkEligibility(lastDonationDate);

                    Object[] row = {
                            donorId,
                            name,
                            fatherName,
                            bloodGroup,
                            phone,
                            age,
                            lastDonationDate != null ? lastDonationDate : "Never",
                            eligibilityStatus
                    };
                    tableModel.addRow(row);
                }

                if (count == 0) {
                    statusLabel.setText("No donors found matching your search criteria.");
                    statusLabel.setForeground(Color.RED);
                } else {
                    statusLabel.setText("Found " + count + " donor(s). Double-click or select to add donation.");
                    statusLabel.setForeground(new Color(0, 128, 0));
                }

                rs.close();
            }

        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this,
                    "Database error: " + ex.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            statusLabel.setText("Error occurred during search.");
            statusLabel.setForeground(Color.RED);
        }
    }

    private ResultSet searchByPhone(String phone) {
        String searchSQL = "SELECT * FROM donors WHERE phone LIKE ?";
        try {
            Connection conn = DatabaseUtil.getConnection();
            PreparedStatement pstmt = conn.prepareStatement(searchSQL);
            pstmt.setString(1, "%" + phone + "%");
            return pstmt.executeQuery();
        } catch (SQLException e) {
            System.err.println("Error searching by phone: " + e.getMessage());
            return null;
        }
    }

    private String checkEligibility(String lastDonationDate) {
        if (lastDonationDate == null || lastDonationDate.isEmpty()) {
            return "✓ Eligible (Never donated)";
        }

        try {
            LocalDate lastDonation = LocalDate.parse(lastDonationDate);
            long daysSince = ChronoUnit.DAYS.between(lastDonation, LocalDate.now());
            int minInterval = SystemSettings.getMinDonationInterval();

            if (daysSince >= minInterval) {
                return "✓ Eligible (" + daysSince + " days since last donation)";
            } else {
                long daysRemaining = minInterval - daysSince;
                return "✗ Not Eligible (Wait " + daysRemaining + " more days)";
            }
        } catch (DateTimeParseException ex) {
            return "⚠ Unknown (Invalid date)";
        }
    }

    private void selectDonorFromTable() {
        int selectedRow = resultTable.getSelectedRow();
        if (selectedRow < 0) return;

        int donorId = (int) tableModel.getValueAt(selectedRow, 0);
        String eligibility = (String) tableModel.getValueAt(selectedRow, 7);

        // Check if donor is eligible
        if (eligibility.startsWith("✗")) {
            JOptionPane.showMessageDialog(this,
                    "This donor is not eligible for donation yet.\n" +
                            eligibility.replace("✗ ", ""),
                    "Not Eligible",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            currentDonor = DatabaseUtil.getDonorByIdObject(donorId);

            if (currentDonor != null) {
                // Get current blood stock
                int currentStock = BloodInventoryUtil.getBloodStock(currentDonor.getBloodGroup());

                // Display donor info
                String info = String.format(
                        "Selected: %s | Blood Group: %s | Phone: %s | Last Donation: %s",
                        currentDonor.getName(),
                        currentDonor.getBloodGroup(),
                        currentDonor.getPhone(),
                        currentDonor.getLastDonationDate().isEmpty() ? "Never" : currentDonor.getLastDonationDate()
                );
                selectedDonorLabel.setText(info);
                currentStockLabel.setText(String.format("Current %s Blood Stock: %d units",
                        currentDonor.getBloodGroup(), currentStock));

                // Enable donation controls
                unitsField.setEnabled(true);
                donationDateField.setEnabled(true);
                addDonationButton.setEnabled(true);
                unitsField.requestFocus();
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this,
                    "Error loading donor details: " + ex.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void addDonation() {
        if (currentDonor == null) {
            JOptionPane.showMessageDialog(this,
                    "Please select a donor from the table first.",
                    "No Donor Selected",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        // ========== CRITICAL FIX: RE-VALIDATE ELIGIBILITY ==========
        // Re-check eligibility before allowing donation
        String eligibilityStatus = checkEligibility(currentDonor.getLastDonationDate());
        if (!eligibilityStatus.startsWith("✓")) {
            JOptionPane.showMessageDialog(this,
                    "This donor is not eligible to donate blood at this time.\n\n" +
                            eligibilityStatus + "\n\n" +
                            "The donor's eligibility may have changed since selection.\n" +
                            "Please refresh the search to see updated eligibility status.",
                    "Donation Not Allowed",
                    JOptionPane.ERROR_MESSAGE);
            // Clear the selection and disable donation controls
            clearDonationForm();
            return;
        }
        // ===========================================================

        String unitsText = unitsField.getText().trim();
        if (unitsText.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Please enter the number of units.",
                    "Input Required",
                    JOptionPane.WARNING_MESSAGE);
            unitsField.requestFocus();
            return;
        }

        String dateText = donationDateField.getText().trim();
        if (dateText.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Please enter the donation date.",
                    "Input Required",
                    JOptionPane.WARNING_MESSAGE);
            donationDateField.requestFocus();
            return;
        }

        try {
            int units = Integer.parseInt(unitsText);
            if (units <= 0) {
                JOptionPane.showMessageDialog(this,
                        "Units must be a positive number.",
                        "Invalid Input",
                        JOptionPane.ERROR_MESSAGE);
                unitsField.requestFocus();
                return;
            }

            LocalDate donationDate = LocalDate.parse(dateText);
            if (donationDate.isAfter(LocalDate.now())) {
                JOptionPane.showMessageDialog(this,
                        "Donation date cannot be in the future.",
                        "Invalid Date",
                        JOptionPane.ERROR_MESSAGE);
                donationDateField.requestFocus();
                return;
            }

            // Add the donation
            boolean success = BloodInventoryUtil.addBloodDonation(
                    currentDonor.getId(),
                    currentDonor.getBloodGroup(),
                    units,
                    donationDate
            );

            if (success) {
                int newStock = BloodInventoryUtil.getBloodStock(currentDonor.getBloodGroup());
                JOptionPane.showMessageDialog(this,
                        String.format("Blood donation recorded successfully!\n\n" +
                                        "Donor: %s\n" +
                                        "Blood Group: %s\n" +
                                        "Units Added: %d\n" +
                                        "New Stock: %d units",
                                currentDonor.getName(),
                                currentDonor.getBloodGroup(),
                                units,
                                newStock),
                        "Success",
                        JOptionPane.INFORMATION_MESSAGE);
                clearDonationForm();
                // Refresh search to update eligibility status
                performSearch();
            } else {
                JOptionPane.showMessageDialog(this,
                        "Failed to record donation. Please try again.",
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
            }

        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this,
                    "Please enter a valid number for units.",
                    "Invalid Input",
                    JOptionPane.ERROR_MESSAGE);
            unitsField.requestFocus();
        } catch (DateTimeParseException ex) {
            JOptionPane.showMessageDialog(this,
                    "Invalid date format. Please use YYYY-MM-DD.",
                    "Invalid Date",
                    JOptionPane.ERROR_MESSAGE);
            donationDateField.requestFocus();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this,
                    "Error recording donation: " + ex.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void clearDonationForm() {
        unitsField.setText("1");
        donationDateField.setText(LocalDate.now().toString());
        selectedDonorLabel.setText("No donor selected - Select a donor from the table above");
        currentStockLabel.setText("Current Blood Stock: --");
        currentDonor = null;
        unitsField.setEnabled(false);
        donationDateField.setEnabled(false);
        addDonationButton.setEnabled(false);
        resultTable.clearSelection();
    }

    private void clearAll() {
        searchField.setText("");
        bloodGroupSearchCombo.setSelectedIndex(0);
        searchTypeCombo.setSelectedIndex(0);
        tableModel.setRowCount(0);
        statusLabel.setText("Enter search criteria and click Search to find eligible donors");
        statusLabel.setForeground(new Color(100, 100, 100));
        clearDonationForm();
        searchField.requestFocus();
    }
}