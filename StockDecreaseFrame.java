package projectiv;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;

public class StockDecreaseFrame extends JFrame {
    private JComboBox<String> searchTypeCombo;
    private JTextField searchField;
    private JComboBox<String> bloodGroupSearchCombo;
    private JButton searchButton;
    private JButton checkAllStockButton;
    private JTable stockTable;
    private DefaultTableModel stockTableModel;
    private JLabel statusLabel;

    private JComboBox<String> selectedBloodGroupCombo;
    private JTextField unitsField;
    private JTextField patientNameField;
    private JTextField hospitalNameField;
    private JTextField issueDateField;
    private JTextArea remarksArea;
    private JLabel selectedStockLabel;
    private JButton issueBloodButton;
    private JButton clearButton;
    private JButton backButton;

    public StockDecreaseFrame() {
        setTitle("Stock Decrease - Issue Blood");
        setSize(1400, 900);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setResizable(true);

        initComponents();
        addListeners();
        loadAllStock();

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

        JPanel headerPanel = createHeaderPanel();
        mainPanel.add(headerPanel, BorderLayout.NORTH);

        JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
        splitPane.setDividerLocation(350);
        splitPane.setResizeWeight(0.5);

        JPanel stockPanel = createStockPanel();
        splitPane.setTopComponent(new JScrollPane(stockPanel));

        JPanel issuePanel = createIssuePanel();
        splitPane.setBottomComponent(issuePanel);

        mainPanel.add(splitPane, BorderLayout.CENTER);

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
                        0, 0, new Color(220, 53, 69),
                        getWidth(), 0, new Color(185, 28, 46)
                );
                g2d.setPaint(gradient);
                g2d.fillRect(0, 0, getWidth(), getHeight());
            }
        };
        headerPanel.setPreferredSize(new Dimension(0, 100));
        headerPanel.setLayout(new BorderLayout());

        JPanel titlePanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 30, 25));
        titlePanel.setOpaque(false);

        JLabel iconLabel = new JLabel("📉");
        iconLabel.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 36));
        titlePanel.add(iconLabel);

        JLabel titleLabel = new JLabel("Stock Decrease - Issue Blood to Patients");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 28));
        titleLabel.setForeground(Color.WHITE);
        titlePanel.add(titleLabel);

        headerPanel.add(titlePanel, BorderLayout.WEST);

        return headerPanel;
    }

    private JPanel createStockPanel() {
        JPanel stockPanel = new JPanel(new BorderLayout());
        stockPanel.setBackground(new Color(245, 245, 245));
        stockPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 10, 20));

        JPanel controlPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 10));
        controlPanel.setBackground(Color.WHITE);
        controlPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200), 1),
                BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));

        JLabel searchLabel = new JLabel("Search Stock By:");
        searchLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));

        searchTypeCombo = new JComboBox<>(new String[]{"All Blood Groups", "Specific Blood Group", "Fresh Blood", "Old Blood"});
        searchTypeCombo.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        searchTypeCombo.setPreferredSize(new Dimension(180, 30));

        searchField = new JTextField(15);
        searchField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        searchField.setPreferredSize(new Dimension(150, 30));
        searchField.setVisible(false);

        bloodGroupSearchCombo = new JComboBox<>(new String[]{"A+", "A-", "B+", "B-", "AB+", "AB-", "O+", "O-"});
        bloodGroupSearchCombo.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        bloodGroupSearchCombo.setPreferredSize(new Dimension(100, 30));
        bloodGroupSearchCombo.setVisible(false);

        searchButton = new JButton("Search");
        styleButton(searchButton, new Color(70, 130, 180), 120, 35);

        checkAllStockButton = new JButton("View All Stock");
        styleButton(checkAllStockButton, new Color(40, 167, 69), 140, 35);

        controlPanel.add(searchLabel);
        controlPanel.add(searchTypeCombo);
        controlPanel.add(searchField);
        controlPanel.add(bloodGroupSearchCombo);
        controlPanel.add(searchButton);
        controlPanel.add(checkAllStockButton);

        statusLabel = new JLabel("Showing all blood groups in inventory");
        statusLabel.setFont(new Font("Segoe UI", Font.ITALIC, 12));
        statusLabel.setForeground(new Color(100, 100, 100));
        statusLabel.setBorder(BorderFactory.createEmptyBorder(10, 15, 5, 15));

        String[] columnNames = {"Blood Group", "Units Available", "Age Status", "Days Old", "Last Updated"};
        stockTableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        stockTable = new JTable(stockTableModel);
        stockTable.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        stockTable.setRowHeight(35);
        stockTable.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 13));
        stockTable.getTableHeader().setBackground(new Color(220, 53, 69));
        stockTable.getTableHeader().setForeground(Color.WHITE);
        stockTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(JLabel.CENTER);
        for (int i = 0; i < stockTable.getColumnCount(); i++) {
            stockTable.getColumnModel().getColumn(i).setCellRenderer(centerRenderer);
        }

        stockTable.getColumnModel().getColumn(2).setCellRenderer(new StatusCellRenderer());

        stockTable.getColumnModel().getColumn(0).setPreferredWidth(100);
        stockTable.getColumnModel().getColumn(1).setPreferredWidth(120);
        stockTable.getColumnModel().getColumn(2).setPreferredWidth(110);
        stockTable.getColumnModel().getColumn(3).setPreferredWidth(90);
        stockTable.getColumnModel().getColumn(4).setPreferredWidth(150);

        JScrollPane scrollPane = new JScrollPane(stockTable);
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(200, 200, 200), 1));

        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setOpaque(false);
        topPanel.add(controlPanel, BorderLayout.NORTH);
        topPanel.add(statusLabel, BorderLayout.CENTER);

        stockPanel.add(topPanel, BorderLayout.NORTH);
        stockPanel.add(scrollPane, BorderLayout.CENTER);

        return stockPanel;
    }

    private JPanel createIssuePanel() {
        JPanel issuePanel = new JPanel();
        issuePanel.setLayout(new BoxLayout(issuePanel, BoxLayout.Y_AXIS));
        issuePanel.setBackground(new Color(245, 245, 245));
        issuePanel.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));

        JPanel infoPanel = new JPanel();
        infoPanel.setLayout(new BoxLayout(infoPanel, BoxLayout.Y_AXIS));
        infoPanel.setBackground(Color.WHITE);
        infoPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200), 1),
                BorderFactory.createEmptyBorder(15, 15, 15, 15)
        ));
        infoPanel.setMaximumSize(new Dimension(1160, 80));

        JLabel infoTitle = new JLabel("Blood Issue Form - Enter patient and hospital details");
        infoTitle.setFont(new Font("Segoe UI", Font.BOLD, 14));

        selectedStockLabel = new JLabel("Select blood group below to check availability");
        selectedStockLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        selectedStockLabel.setForeground(new Color(100, 100, 100));

        infoPanel.add(infoTitle);
        infoPanel.add(Box.createVerticalStrut(8));
        infoPanel.add(selectedStockLabel);

        issuePanel.add(infoPanel);
        issuePanel.add(Box.createVerticalStrut(15));

        JPanel detailsPanel = new JPanel(new GridBagLayout());
        detailsPanel.setBackground(Color.WHITE);
        detailsPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200), 1),
                BorderFactory.createEmptyBorder(15, 15, 15, 15)
        ));
        detailsPanel.setPreferredSize(new Dimension(1160, 320));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.WEST;

        gbc.gridx = 0; gbc.gridy = 0;
        JLabel bgLabel = new JLabel("Blood Group:");
        bgLabel.setFont(new Font("Segoe UI", Font.BOLD, 13));
        detailsPanel.add(bgLabel, gbc);

        gbc.gridx = 1;
        selectedBloodGroupCombo = new JComboBox<>(new String[]{"A+", "A-", "B+", "B-", "AB+", "AB-", "O+", "O-"});
        selectedBloodGroupCombo.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        selectedBloodGroupCombo.setPreferredSize(new Dimension(120, 30));
        detailsPanel.add(selectedBloodGroupCombo, gbc);

        gbc.gridx = 2;
        JLabel unitsLabel = new JLabel("Units to Issue:");
        unitsLabel.setFont(new Font("Segoe UI", Font.BOLD, 13));
        detailsPanel.add(unitsLabel, gbc);

        gbc.gridx = 3;
        unitsField = new JTextField(10);
        unitsField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        unitsField.setPreferredSize(new Dimension(100, 30));
        detailsPanel.add(unitsField, gbc);

        gbc.gridx = 0; gbc.gridy = 1;
        JLabel patientLabel = new JLabel("Patient Name:");
        patientLabel.setFont(new Font("Segoe UI", Font.BOLD, 13));
        detailsPanel.add(patientLabel, gbc);

        gbc.gridx = 1; gbc.gridwidth = 3;
        patientNameField = new JTextField(30);
        patientNameField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        patientNameField.setPreferredSize(new Dimension(400, 30));
        detailsPanel.add(patientNameField, gbc);

        gbc.gridx = 0; gbc.gridy = 2; gbc.gridwidth = 1;
        JLabel hospitalLabel = new JLabel("Hospital Name:");
        hospitalLabel.setFont(new Font("Segoe UI", Font.BOLD, 13));
        detailsPanel.add(hospitalLabel, gbc);

        gbc.gridx = 1; gbc.gridwidth = 3;
        hospitalNameField = new JTextField(30);
        hospitalNameField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        hospitalNameField.setPreferredSize(new Dimension(400, 30));
        detailsPanel.add(hospitalNameField, gbc);

        gbc.gridx = 0; gbc.gridy = 3; gbc.gridwidth = 1;
        JLabel dateLabel = new JLabel("Issue Date:");
        dateLabel.setFont(new Font("Segoe UI", Font.BOLD, 13));
        detailsPanel.add(dateLabel, gbc);

        gbc.gridx = 1;
        issueDateField = new JTextField(12);
        issueDateField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        issueDateField.setPreferredSize(new Dimension(150, 30));
        issueDateField.setText(LocalDate.now().toString());
        detailsPanel.add(issueDateField, gbc);

        gbc.gridx = 2; gbc.gridwidth = 1;
        JLabel remarksLabel = new JLabel("Remarks:");
        remarksLabel.setFont(new Font("Segoe UI", Font.BOLD, 13));
        detailsPanel.add(remarksLabel, gbc);

        gbc.gridx = 3;
        remarksArea = new JTextArea(2, 20);
        remarksArea.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        remarksArea.setLineWrap(true);
        remarksArea.setWrapStyleWord(true);
        JScrollPane remarksScroll = new JScrollPane(remarksArea);
        remarksScroll.setPreferredSize(new Dimension(200, 60));
        detailsPanel.add(remarksScroll, gbc);

        gbc.gridx = 0; gbc.gridy = 4; gbc.gridwidth = 4;
        gbc.anchor = GridBagConstraints.CENTER;
        issueBloodButton = new JButton("Issue Blood to Patient");
        styleButton(issueBloodButton, new Color(220, 53, 69), 200, 40);
        detailsPanel.add(issueBloodButton, gbc);

        gbc.gridy = 5;
        gbc.weighty = 1.0;
        detailsPanel.add(Box.createVerticalStrut(20), gbc);

        JScrollPane detailsScrollPane = new JScrollPane(detailsPanel);
        detailsScrollPane.setBorder(null);
        detailsScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        detailsScrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        detailsScrollPane.getVerticalScrollBar().setUnitIncrement(16);

        issuePanel.add(detailsScrollPane);

        return issuePanel;
    }

    private JPanel createBottomPanel() {
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 15));
        bottomPanel.setBackground(new Color(245, 245, 245));

        clearButton = new JButton("Clear Form");
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
            if ("Specific Blood Group".equals(searchType)) {
                searchField.setVisible(false);
                bloodGroupSearchCombo.setVisible(true);
            } else {
                searchField.setVisible(false);
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

        selectedBloodGroupCombo.addActionListener(e -> updateStockLabel());
        searchButton.addActionListener(e -> performSearch());
        checkAllStockButton.addActionListener(e -> loadAllStock());

        stockTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                selectBloodGroupFromTable();
            }
        });

        issueBloodButton.addActionListener(e -> issueBlood());
        clearButton.addActionListener(e -> clearForm());
        backButton.addActionListener(e -> dispose());
    }

    private void performSearch() {
        String searchType = (String) searchTypeCombo.getSelectedItem();
        stockTableModel.setRowCount(0);

        int freshThreshold = SystemSettings.getFreshBloodThreshold();

        try {
            ResultSet rs = BloodInventoryUtil.getAllBloodInventory();

            if (rs != null) {
                int count = 0;
                while (rs.next()) {
                    String bloodGroup = rs.getString("blood_group");
                    int units = rs.getInt("units_available");
                    String lastUpdated = rs.getString("last_updated");

                    int daysOld = BloodAgeUtil.calculateDaysSinceUpdate(lastUpdated);
                    String status = BloodAgeUtil.getBloodStatus(daysOld);

                    boolean include = false;

                    switch (searchType) {
                        case "All Blood Groups":
                            include = true;
                            break;
                        case "Specific Blood Group":
                            String selected = (String) bloodGroupSearchCombo.getSelectedItem();
                            include = bloodGroup.equals(selected);
                            break;
                        case "Fresh Blood":
                            include = daysOld <= freshThreshold;
                            break;
                        case "Old Blood":
                            include = daysOld > freshThreshold;
                            break;
                    }

                    if (include) {
                        count++;
                        Object[] row = {bloodGroup, units + " units", status, daysOld + " days", lastUpdated};
                        stockTableModel.addRow(row);
                    }
                }

                if (count == 0) {
                    statusLabel.setText("No blood groups found matching the filter: " + searchType);
                    statusLabel.setForeground(Color.RED);
                } else {
                    statusLabel.setText("Showing " + count + " blood group(s) - Filter: " + searchType);
                    statusLabel.setForeground(new Color(0, 128, 0));
                }

                rs.close();
            }

        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this,
                    "Error loading stock data: " + ex.getMessage(),
                    "Database Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void loadAllStock() {
        searchTypeCombo.setSelectedIndex(0);
        performSearch();
    }

    private void updateStockLabel() {
        String bloodGroup = (String) selectedBloodGroupCombo.getSelectedItem();
        try {
            int stock = BloodInventoryUtil.getBloodStock(bloodGroup);
            selectedStockLabel.setText(String.format("Available %s Stock: %d units",
                    bloodGroup, stock));

            if (stock == 0) {
                selectedStockLabel.setForeground(Color.RED);
            } else if (stock < 10) {
                selectedStockLabel.setForeground(new Color(220, 53, 69));
            } else {
                selectedStockLabel.setForeground(new Color(40, 167, 69));
            }
        } catch (Exception ex) {
            selectedStockLabel.setText("Error checking stock: " + ex.getMessage());
            selectedStockLabel.setForeground(Color.RED);
        }
    }

    private void selectBloodGroupFromTable() {
        int selectedRow = stockTable.getSelectedRow();
        if (selectedRow < 0) return;

        String bloodGroup = ((String) stockTableModel.getValueAt(selectedRow, 0));
        selectedBloodGroupCombo.setSelectedItem(bloodGroup);
        updateStockLabel();
    }

    private void issueBlood() {
        String bloodGroup = (String) selectedBloodGroupCombo.getSelectedItem();
        String unitsText = unitsField.getText().trim();
        String patientName = patientNameField.getText().trim();
        String hospitalName = hospitalNameField.getText().trim();
        String dateText = issueDateField.getText().trim();
        String remarks = remarksArea.getText().trim();

        if (unitsText.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter the number of units.", "Input Required", JOptionPane.WARNING_MESSAGE);
            unitsField.requestFocus();
            return;
        }
        if (patientName.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter patient name.", "Input Required", JOptionPane.WARNING_MESSAGE);
            patientNameField.requestFocus();
            return;
        }
        if (dateText.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter issue date.", "Input Required", JOptionPane.WARNING_MESSAGE);
            issueDateField.requestFocus();
            return;
        }

        try {
            int units = Integer.parseInt(unitsText);
            if (units <= 0) {
                JOptionPane.showMessageDialog(this, "Units must be a positive number.", "Invalid Input", JOptionPane.ERROR_MESSAGE);
                unitsField.requestFocus();
                return;
            }

            LocalDate issueDate = LocalDate.parse(dateText);
            if (issueDate.isAfter(LocalDate.now())) {
                JOptionPane.showMessageDialog(this, "Issue date cannot be in the future.", "Invalid Date", JOptionPane.ERROR_MESSAGE);
                issueDateField.requestFocus();
                return;
            }

            boolean success = BloodInventoryUtil.issueBlood(
                    bloodGroup, units, patientName, hospitalName, issueDate, remarks
            );

            if (success) {
                int newStock = BloodInventoryUtil.getBloodStock(bloodGroup);
                JOptionPane.showMessageDialog(this,
                        String.format("Blood issued successfully!\n\n" +
                                        "Blood Group: %s\n" +
                                        "Units Issued: %d\n" +
                                        "Patient: %s\n" +
                                        "Hospital: %s\n" +
                                        "Remaining Stock: %d units",
                                bloodGroup, units, patientName,
                                hospitalName.isEmpty() ? "N/A" : hospitalName, newStock),
                        "Success",
                        JOptionPane.INFORMATION_MESSAGE);
                clearForm();
                loadAllStock();
            } else {
                int available = BloodInventoryUtil.getBloodStock(bloodGroup);
                JOptionPane.showMessageDialog(this,
                        String.format("Insufficient stock!\nRequested: %d units\nAvailable: %d units",
                                units, available),
                        "Insufficient Stock",
                        JOptionPane.ERROR_MESSAGE);
            }

        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Please enter a valid number for units.", "Invalid Input", JOptionPane.ERROR_MESSAGE);
            unitsField.requestFocus();
        } catch (DateTimeParseException ex) {
            JOptionPane.showMessageDialog(this, "Invalid date format. Please use YYYY-MM-DD.", "Invalid Date", JOptionPane.ERROR_MESSAGE);
            issueDateField.requestFocus();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error issuing blood: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void clearForm() {
        selectedBloodGroupCombo.setSelectedIndex(0);
        unitsField.setText("");
        patientNameField.setText("");
        hospitalNameField.setText("");
        issueDateField.setText(LocalDate.now().toString());
        remarksArea.setText("");
        updateStockLabel();
        stockTable.clearSelection();
        unitsField.requestFocus();
    }

    class StatusCellRenderer extends DefaultTableCellRenderer {
        public StatusCellRenderer() {
            setHorizontalAlignment(JLabel.CENTER);
            setFont(new Font("Segoe UI", Font.BOLD, 12));
        }

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value,
                                                       boolean isSelected, boolean hasFocus, int row, int column) {
            Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);

            String status = value.toString();

            if (status.equals("Fresh Blood")) {
                c.setBackground(new Color(212, 237, 218));
                c.setForeground(new Color(21, 87, 36));
            } else if (status.equals("Old Blood")) {
                c.setBackground(new Color(255, 243, 205));
                c.setForeground(new Color(133, 100, 4));
            }

            if (isSelected) {
                c.setBackground(table.getSelectionBackground());
                c.setForeground(table.getSelectionForeground());
            }

            return c;
        }
    }
}