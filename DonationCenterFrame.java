package projectiv;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;
import java.awt.event.*;
import projectiv.IconUtil;


public class DonationCenterFrame extends JFrame {
    private JTable centerTable;
    private DefaultTableModel tableModel;
    private JTextField searchField;
    private JComboBox<String> districtCombo;
    private JButton searchButton;
    private JButton showAllButton;
    private JButton backButton;

    // Professional color scheme
    private static final Color PRIMARY_COLOR = new Color(220, 53, 69);
    private static final Color ACCENT_BLUE = new Color(0, 123, 255);
    private static final Color ACCENT_GREEN = new Color(40, 167, 69);
    private static final Color BACKGROUND_LIGHT = new Color(245, 245, 245);

    // Donation centers data for Kathmandu Valley
    private static final Object[][] CENTERS_DATA = {
            {"Nepal Red Cross Society", "Kathmandu", "Kalimati", "01-4272761", "Open Daily", "7:00 AM - 5:00 PM", "All Blood Groups"},
            {"Central Blood Transfusion Service", "Kathmandu", "Teku", "01-4253396", "Open Daily", "24/7", "All Blood Groups"},
            {"Bir Hospital Blood Bank", "Kathmandu", "Tundikhel", "01-4221119", "Open Daily", "24/7", "All Blood Groups"},
            {"Teaching Hospital Blood Bank", "Kathmandu", "Maharajgunj", "01-4412303", "Open Daily", "24/7", "All Blood Groups"},
            {"Tribhuvan University Teaching Hospital", "Kathmandu", "Maharajgunj", "01-4412505", "Open Daily", "8:00 AM - 8:00 PM", "All Blood Groups"},
            {"Paropakar Maternity Hospital", "Kathmandu", "Thapathali", "01-4260879", "Open Daily", "24/7", "All Blood Groups"},
            {"Patan Hospital Blood Bank", "Lalitpur", "Lagankhel", "01-5522278", "Open Daily", "24/7", "All Blood Groups"},
            {"Nepal Medical College", "Kathmandu", "Jorpati", "01-4911008", "Mon-Sat", "9:00 AM - 5:00 PM", "All Blood Groups"},
            {"Kathmandu Medical College", "Kathmandu", "Sinamangal", "01-4470042", "Mon-Sat", "8:00 AM - 6:00 PM", "All Blood Groups"},
            {"Grande International Hospital", "Kathmandu", "Dhapasi", "01-5159266", "Open Daily", "24/7", "All Blood Groups"},
            {"Mediciti Hospital", "Lalitpur", "Sainbu", "01-4217766", "Open Daily", "24/7", "All Blood Groups"},
            {"Norvic International Hospital", "Kathmandu", "Thapathali", "01-4258554", "Open Daily", "24/7", "All Blood Groups"},
            {"Nepal Cancer Hospital", "Lalitpur", "Harisiddhi", "01-5250810", "Mon-Fri", "9:00 AM - 5:00 PM", "All Blood Groups"},
            {"Bhaktapur Hospital", "Bhaktapur", "Dudhpati", "01-6610798", "Open Daily", "24/7", "All Blood Groups"},
            {"Siddhi Memorial Hospital", "Bhaktapur", "Bhaktapur", "01-6632102", "Open Daily", "10:00 AM - 6:00 PM", "All Blood Groups"},
            {"Civil Service Hospital", "Kathmandu", "Minbhawan", "01-4411550", "Mon-Fri", "9:00 AM - 5:00 PM", "All Blood Groups"},
            {"Army Hospital", "Kathmandu", "Chhauni", "01-4270066", "Mon-Fri", "10:00 AM - 4:00 PM", "All Blood Groups"},
            {"Police Hospital", "Kathmandu", "Maharajgunj", "01-4412777", "Mon-Fri", "9:00 AM - 5:00 PM", "All Blood Groups"},
            {"Manmohan Cardiothoracic Center", "Kathmandu", "Maharajgunj", "01-4371322", "Open Daily", "24/7", "All Blood Groups"},
            {"National Trauma Center", "Kathmandu", "Mahankal", "01-4244116", "Open Daily", "24/7", "All Blood Groups"}
    };

    public DonationCenterFrame() {
        setTitle("Donation Centers - Kathmandu Valley");
        setSize(1200, 800);  // Increased from 700 to 800
        setLocationRelativeTo(null);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setResizable(true);  // Changed to true for better flexibility

        initComponents();
        loadAllCenters();
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

        // Search Panel
        JPanel searchPanel = createSearchPanel();
        mainPanel.add(searchPanel, BorderLayout.CENTER);

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
        headerPanel.setPreferredSize(new Dimension(0, 120));
        headerPanel.setLayout(new BorderLayout());

        // Title with icon
        JPanel titlePanel = new JPanel();
        titlePanel.setLayout(new BoxLayout(titlePanel, BoxLayout.Y_AXIS));
        titlePanel.setOpaque(false);
        titlePanel.setBorder(BorderFactory.createEmptyBorder(20, 30, 10, 30));

        JLabel iconLabel = new JLabel("🏥");
        iconLabel.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 40));
        iconLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel titleLabel = new JLabel("Blood Donation Centers in Kathmandu Valley");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 28));
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel subtitleLabel = new JLabel("Find nearby blood donation centers and their contact information");
        subtitleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        subtitleLabel.setForeground(new Color(255, 255, 255, 230));
        subtitleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        titlePanel.add(iconLabel);
        titlePanel.add(Box.createVerticalStrut(5));
        titlePanel.add(titleLabel);
        titlePanel.add(Box.createVerticalStrut(5));
        titlePanel.add(subtitleLabel);

        headerPanel.add(titlePanel, BorderLayout.WEST);

        return headerPanel;
    }

    private JPanel createSearchPanel() {
        JPanel searchPanel = new JPanel(new BorderLayout());
        searchPanel.setBackground(BACKGROUND_LIGHT);
        searchPanel.setBorder(BorderFactory.createEmptyBorder(20, 30, 20, 30));

        // Control Panel with increased height
        JPanel controlPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 15));
        controlPanel.setBackground(Color.WHITE);
        controlPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200), 1),
                BorderFactory.createEmptyBorder(20, 20, 20, 20)  // Increased padding from 15 to 20
        ));
        controlPanel.setPreferredSize(new Dimension(0, 90));  // Set explicit height

        JLabel searchLabel = new JLabel("Search by Name:");
        searchLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));

        searchField = new JTextField(25);
        searchField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        searchField.setPreferredSize(new Dimension(300, 40));  // Increased height from 35 to 40

        JLabel districtLabel = new JLabel("Filter by District:");
        districtLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));

        districtCombo = new JComboBox<>(new String[]{"All Districts", "Kathmandu", "Lalitpur", "Bhaktapur"});
        districtCombo.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        districtCombo.setPreferredSize(new Dimension(150, 40));  // Increased height from 35 to 40

        searchButton = new JButton("Search");
        styleButton(searchButton, ACCENT_BLUE);

        showAllButton = new JButton("Show All");
        styleButton(showAllButton, ACCENT_GREEN);

        controlPanel.add(searchLabel);
        controlPanel.add(searchField);
        controlPanel.add(districtLabel);
        controlPanel.add(districtCombo);
        controlPanel.add(searchButton);
        controlPanel.add(showAllButton);

        searchPanel.add(controlPanel, BorderLayout.NORTH);

        // Table Panel
        JPanel tablePanel = new JPanel(new BorderLayout());
        tablePanel.setBackground(BACKGROUND_LIGHT);
        tablePanel.setBorder(BorderFactory.createEmptyBorder(25, 0, 0, 0));  // Increased from 20 to 25

        String[] columnNames = {"Center Name", "District", "Location", "Phone", "Operating Days", "Timing", "Blood Groups"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        centerTable = new JTable(tableModel);
        centerTable.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        centerTable.setRowHeight(40);
        centerTable.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 13));
        centerTable.getTableHeader().setBackground(PRIMARY_COLOR);
        centerTable.getTableHeader().setForeground(Color.WHITE);
        centerTable.getTableHeader().setPreferredSize(new Dimension(0, 45));
        centerTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        centerTable.setShowGrid(true);
        centerTable.setGridColor(new Color(230, 230, 230));

        // Center align all columns
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(JLabel.CENTER);
        for (int i = 0; i < centerTable.getColumnCount(); i++) {
            centerTable.getColumnModel().getColumn(i).setCellRenderer(centerRenderer);
        }

        // Left align name and location columns
        DefaultTableCellRenderer leftRenderer = new DefaultTableCellRenderer();
        leftRenderer.setHorizontalAlignment(JLabel.LEFT);
        centerTable.getColumnModel().getColumn(0).setCellRenderer(leftRenderer);
        centerTable.getColumnModel().getColumn(2).setCellRenderer(leftRenderer);

        // Set column widths
        centerTable.getColumnModel().getColumn(0).setPreferredWidth(200); // Name
        centerTable.getColumnModel().getColumn(1).setPreferredWidth(100); // District
        centerTable.getColumnModel().getColumn(2).setPreferredWidth(120); // Location
        centerTable.getColumnModel().getColumn(3).setPreferredWidth(120); // Phone
        centerTable.getColumnModel().getColumn(4).setPreferredWidth(100); // Days
        centerTable.getColumnModel().getColumn(5).setPreferredWidth(140); // Timing
        centerTable.getColumnModel().getColumn(6).setPreferredWidth(120); // Blood Groups

        JScrollPane scrollPane = new JScrollPane(centerTable);
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(200, 200, 200), 1));
        scrollPane.setPreferredSize(new Dimension(0, 400));  // Set explicit height for table area

        tablePanel.add(scrollPane, BorderLayout.CENTER);

        // Info Panel
        JPanel infoPanel = new JPanel();
        infoPanel.setBackground(new Color(255, 243, 205));
        infoPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(255, 193, 7), 2),
                BorderFactory.createEmptyBorder(15, 20, 15, 20)
        ));
        infoPanel.setLayout(new FlowLayout(FlowLayout.LEFT, 10, 0));
        infoPanel.setPreferredSize(new Dimension(0, 60));  // Set explicit height

        JLabel infoIcon = new JLabel("ℹ️");
        infoIcon.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 20));

        JLabel infoText = new JLabel("<html><b>Important:</b> Please call ahead to confirm operating hours and blood availability. Some centers may have specific requirements for donors.</html>");
        infoText.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        infoText.setForeground(new Color(133, 100, 4));

        infoPanel.add(infoIcon);
        infoPanel.add(infoText);

        tablePanel.add(infoPanel, BorderLayout.SOUTH);

        searchPanel.add(tablePanel, BorderLayout.CENTER);

        return searchPanel;
    }
    private JPanel createBottomPanel() {
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 20));
        bottomPanel.setBackground(BACKGROUND_LIGHT);

        backButton = new JButton("Back to Dashboard");
        styleButton(backButton, new Color(108, 117, 125));

        bottomPanel.add(backButton);

        return bottomPanel;
    }

    private void styleButton(JButton button, Color color) {
        button.setFont(new Font("Segoe UI", Font.BOLD, 14));
        button.setBackground(color);
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setPreferredSize(new Dimension(160, 40));
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
        searchButton.addActionListener(e -> performSearch());
        showAllButton.addActionListener(e -> loadAllCenters());
        backButton.addActionListener(e -> dispose());
        searchField.addActionListener(e -> performSearch());

        // Double-click to show details
        centerTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    int row = centerTable.getSelectedRow();
                    if (row >= 0) {
                        showCenterDetails(row);
                    }
                }
            }
        });
    }

    private void loadAllCenters() {
        tableModel.setRowCount(0);
        for (Object[] center : CENTERS_DATA) {
            tableModel.addRow(center);
        }
        searchField.setText("");
        districtCombo.setSelectedIndex(0);
    }

    private void performSearch() {
        String searchText = searchField.getText().trim().toLowerCase();
        String selectedDistrict = (String) districtCombo.getSelectedItem();

        tableModel.setRowCount(0);
        int count = 0;

        for (Object[] center : CENTERS_DATA) {
            String name = center[0].toString().toLowerCase();
            String district = center[1].toString();
            String location = center[2].toString().toLowerCase();

            boolean matchesSearch = searchText.isEmpty() ||
                    name.contains(searchText) ||
                    location.contains(searchText);
            boolean matchesDistrict = selectedDistrict.equals("All Districts") ||
                    district.equals(selectedDistrict);

            if (matchesSearch && matchesDistrict) {
                tableModel.addRow(center);
                count++;
            }
        }

        if (count == 0) {
            JOptionPane.showMessageDialog(this,
                    "No donation centers found matching your search criteria.",
                    "No Results",
                    JOptionPane.INFORMATION_MESSAGE);
        }
    }

    private void showCenterDetails(int row) {
        String name = tableModel.getValueAt(row, 0).toString();
        String district = tableModel.getValueAt(row, 1).toString();
        String location = tableModel.getValueAt(row, 2).toString();
        String phone = tableModel.getValueAt(row, 3).toString();
        String days = tableModel.getValueAt(row, 4).toString();
        String timing = tableModel.getValueAt(row, 5).toString();
        String bloodGroups = tableModel.getValueAt(row, 6).toString();

        String details = String.format(
                "<html><div style='font-family: Segoe UI; padding: 10px;'>" +
                        "<h2 style='color: #dc3545; margin-top: 0;'>%s</h2>" +
                        "<hr style='border: 1px solid #ddd;'>" +
                        "<p><b>📍 Location:</b> %s, %s</p>" +
                        "<p><b>📞 Phone:</b> %s</p>" +
                        "<p><b>📅 Operating Days:</b> %s</p>" +
                        "<p><b>🕐 Timing:</b> %s</p>" +
                        "<p><b>🩸 Blood Groups:</b> %s</p>" +
                        "<hr style='border: 1px solid #ddd;'>" +
                        "<p style='color: #666; font-size: 11px;'><i>Note: Please call ahead to confirm current operating hours and blood availability.</i></p>" +
                        "</div></html>",
                name, location, district, phone, days, timing, bloodGroups
        );

        JLabel detailsLabel = new JLabel(details);
        detailsLabel.setPreferredSize(new Dimension(450, 300));

        JOptionPane.showMessageDialog(this,
                detailsLabel,
                "Center Details",
                JOptionPane.INFORMATION_MESSAGE);
    }
}