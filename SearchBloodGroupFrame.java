package projectiv;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import projectiv.IconUtil;


public class SearchBloodGroupFrame extends JFrame {
    private JComboBox<String> bloodGroupCombo;
    private JButton searchButton;
    private JButton clearButton;
    private JButton backButton;
    private JTable resultTable;
    private DefaultTableModel tableModel;
    private JLabel statusLabel;

    public SearchBloodGroupFrame() {
        setTitle("Search by Blood Group");
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

    // Constructor to auto-search for a specific blood group
    public SearchBloodGroupFrame(String bloodGroup) {
        this();
        bloodGroupCombo.setSelectedItem(bloodGroup);
        performSearch();
    }

    private void initComponents() {
        // Top panel with search controls
        JPanel topPanel = new JPanel();
        topPanel.setLayout(new FlowLayout(FlowLayout.LEFT, 15, 15));
        topPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        topPanel.setBackground(new Color(240, 240, 240));

        JLabel searchLabel = new JLabel("Select Blood Group:");
        searchLabel.setFont(new Font("Arial", Font.BOLD, 14));

        bloodGroupCombo = new JComboBox<>(new String[]{"A+", "A-", "B+", "B-", "AB+", "AB-", "O+", "O-"});
        bloodGroupCombo.setFont(new Font("Arial", Font.PLAIN, 14));

        searchButton = new JButton("Search");
        searchButton.setFont(new Font("Arial", Font.BOLD, 14));
        searchButton.setBackground(new Color(139, 0, 0));
        searchButton.setForeground(Color.WHITE);
        searchButton.setFocusPainted(false);

        clearButton = new JButton("Clear");
        clearButton.setFont(new Font("Arial", Font.BOLD, 14));
        clearButton.setBackground(new Color(100, 100, 100));
        clearButton.setForeground(Color.WHITE);
        clearButton.setFocusPainted(false);

        backButton = new JButton("Back");
        backButton.setFont(new Font("Arial", Font.BOLD, 14));
        backButton.setBackground(new Color(70, 130, 180));
        backButton.setForeground(Color.WHITE);
        backButton.setFocusPainted(false);

        topPanel.add(searchLabel);
        topPanel.add(bloodGroupCombo);
        topPanel.add(searchButton);
        topPanel.add(clearButton);
        topPanel.add(backButton);

        // Status label
        statusLabel = new JLabel("Select a blood group and click Search");
        statusLabel.setFont(new Font("Arial", Font.ITALIC, 12));
        statusLabel.setForeground(new Color(100, 100, 100));
        statusLabel.setBorder(BorderFactory.createEmptyBorder(5, 15, 5, 15));

        // Table for results
        String[] columnNames = {"ID", "Name", "Father Name", "Age", "Gender", "Blood Group",
                "Phone", "City", "Last Donation", "Permanent Address"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        resultTable = new JTable(tableModel);
        resultTable.setFont(new Font("Arial", Font.PLAIN, 12));
        resultTable.setRowHeight(25);
        resultTable.getTableHeader().setFont(new Font("Arial", Font.BOLD, 12));
        resultTable.getTableHeader().setBackground(new Color(139, 0, 0));
        resultTable.getTableHeader().setForeground(Color.WHITE);
        resultTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        // Set column widths
        resultTable.getColumnModel().getColumn(0).setPreferredWidth(40);  // ID
        resultTable.getColumnModel().getColumn(1).setPreferredWidth(120); // Name
        resultTable.getColumnModel().getColumn(2).setPreferredWidth(120); // Father
        resultTable.getColumnModel().getColumn(3).setPreferredWidth(40);  // Age
        resultTable.getColumnModel().getColumn(4).setPreferredWidth(60);  // Gender
        resultTable.getColumnModel().getColumn(5).setPreferredWidth(80);  // Blood
        resultTable.getColumnModel().getColumn(6).setPreferredWidth(100); // Phone
        resultTable.getColumnModel().getColumn(7).setPreferredWidth(100); // City
        resultTable.getColumnModel().getColumn(8).setPreferredWidth(100); // Last Donation
        resultTable.getColumnModel().getColumn(9).setPreferredWidth(200); // Address

        JScrollPane scrollPane = new JScrollPane(resultTable);
        scrollPane.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Layout
        Container c = getContentPane();
        c.setLayout(new BorderLayout());
        c.add(topPanel, BorderLayout.NORTH);

        // Center panel with status and table
        JPanel centerPanel = new JPanel(new BorderLayout());
        centerPanel.add(statusLabel, BorderLayout.NORTH);
        centerPanel.add(scrollPane, BorderLayout.CENTER);
        c.add(centerPanel, BorderLayout.CENTER);
    }

    private void addListeners() {
        searchButton.addActionListener(e -> performSearch());
        clearButton.addActionListener(e -> clearSearch());
        backButton.addActionListener(e -> dispose());

        // Add double-click to view donor details
        resultTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    int row = resultTable.getSelectedRow();
                    if (row >= 0) {
                        int donorId = (int) tableModel.getValueAt(row, 0);
                        viewDonorDetails(donorId);
                    }
                }
            }
        });
    }

    private void performSearch() {
        String bloodGroup = (String) bloodGroupCombo.getSelectedItem();

        // Clear existing results
        tableModel.setRowCount(0);

        try {
            ResultSet rs = DatabaseUtil.searchDonorsByBloodGroup(bloodGroup);

            if (rs != null) {
                int count = 0;
                while (rs.next()) {
                    count++;
                    Object[] row = {
                            rs.getInt("id"),
                            rs.getString("name"),
                            rs.getString("father_name"),
                            rs.getInt("age"),
                            rs.getString("gender"),
                            rs.getString("blood_group"),
                            rs.getString("phone"),
                            rs.getString("donated_city"),
                            rs.getString("last_donation_date") != null ?
                                    rs.getString("last_donation_date") : "Never",
                            rs.getString("permanent_address")
                    };
                    tableModel.addRow(row);
                }

                if (count == 0) {
                    statusLabel.setText("No donors found with blood group: " + bloodGroup);
                    statusLabel.setForeground(Color.RED);
                } else {
                    statusLabel.setText("Found " + count + " donor(s) with blood group: " + bloodGroup);
                    statusLabel.setForeground(new Color(0, 128, 0));
                }

                rs.close();
            } else {
                statusLabel.setText("Error performing search.");
                statusLabel.setForeground(Color.RED);
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

    private void clearSearch() {
        bloodGroupCombo.setSelectedIndex(0);
        tableModel.setRowCount(0);
        statusLabel.setText("Select a blood group and click Search");
        statusLabel.setForeground(new Color(100, 100, 100));
    }

    private void viewDonorDetails(int donorId) {
        try {
            Donor donor = DatabaseUtil.getDonorByIdObject(donorId);
            if (donor != null) {
                String details = String.format(
                        "Donor Details:\n\n" +
                                "ID: %d\n" +
                                "Name: %s\n" +
                                "Father Name: %s\n" +
                                "Age: %d\n" +
                                "Gender: %s\n" +
                                "Blood Group: %s\n" +
                                "Phone: %s\n" +
                                "City: %s\n" +
                                "Last Donation: %s\n" +
                                "Permanent Address: %s\n" +
                                "Temporary Address: %s",
                        donor.getId(),
                        donor.getName(),
                        donor.getFatherName(),
                        donor.getAge(),
                        donor.getGender(),
                        donor.getBloodGroup(),
                        donor.getPhone(),
                        donor.getDonatedCity(),
                        donor.getLastDonationDate().isEmpty() ? "Never" : donor.getLastDonationDate(),
                        donor.getPermanentAddress(),
                        donor.getTemporaryAddress()
                );

                JTextArea textArea = new JTextArea(details);
                textArea.setEditable(false);
                textArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
                JScrollPane scrollPane = new JScrollPane(textArea);
                scrollPane.setPreferredSize(new Dimension(400, 300));

                JOptionPane.showMessageDialog(this, scrollPane, "Donor Details", JOptionPane.INFORMATION_MESSAGE);
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this,
                    "Error loading donor details: " + ex.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }
}