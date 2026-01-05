package projectiv;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;

public class BloodInventoryFrame extends JFrame {
    private JTable inventoryTable;
    private DefaultTableModel tableModel;
    private JLabel totalUnitsLabel;
    private JButton refreshButton;
    private JButton backButton;

    // Professional color scheme
    private static final Color PRIMARY_COLOR = new Color(220, 53, 69);
    private static final Color FRESH_GREEN = new Color(40, 167, 69);
    private static final Color OLD_ORANGE = new Color(253, 126, 20);
    private static final Color BACKGROUND_LIGHT = new Color(245, 245, 245);

    public BloodInventoryFrame() {
        setTitle("Blood Inventory Management");
        setSize(1000, 650);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setResizable(true);

        initComponents();
        loadInventoryData();
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

        // Center Panel - Table
        JPanel centerPanel = createCenterPanel();
        mainPanel.add(centerPanel, BorderLayout.CENTER);

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

        // Title
        JLabel titleLabel = new JLabel("Blood Inventory Dashboard");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 28));
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setBorder(BorderFactory.createEmptyBorder(20, 30, 10, 30));
        headerPanel.add(titleLabel, BorderLayout.WEST);

        // Total Units Label
        totalUnitsLabel = new JLabel("Total Units: 0");
        totalUnitsLabel.setFont(new Font("Segoe UI", Font.BOLD, 20));
        totalUnitsLabel.setForeground(Color.WHITE);
        totalUnitsLabel.setBorder(BorderFactory.createEmptyBorder(20, 30, 10, 30));
        headerPanel.add(totalUnitsLabel, BorderLayout.EAST);

        return headerPanel;
    }

    private JPanel createCenterPanel() {
        JPanel centerPanel = new JPanel(new BorderLayout());
        centerPanel.setBackground(BACKGROUND_LIGHT);
        centerPanel.setBorder(BorderFactory.createEmptyBorder(20, 30, 20, 30));

        // Info Panel
        JPanel infoPanel = new JPanel();
        infoPanel.setBackground(Color.WHITE);
        infoPanel.setLayout(new FlowLayout(FlowLayout.LEFT, 20, 15));
        infoPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200), 1),
                BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));

        // Legend
        JLabel legendLabel = new JLabel("Blood Age Status:");
        legendLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        infoPanel.add(legendLabel);

        int threshold = SystemSettings.getFreshBloodThreshold();
        infoPanel.add(createLegendItem("Fresh Blood (≤" + threshold + " days)", FRESH_GREEN));
        infoPanel.add(createLegendItem("Old Blood (>" + threshold + " days)", OLD_ORANGE));

        centerPanel.add(infoPanel, BorderLayout.NORTH);

        // Table
        String[] columnNames = {"Blood Group", "Units Available", "Age Status", "Days Old", "Last Updated"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        inventoryTable = new JTable(tableModel);
        inventoryTable.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        inventoryTable.setRowHeight(50);
        inventoryTable.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 15));
        inventoryTable.getTableHeader().setBackground(PRIMARY_COLOR);
        inventoryTable.getTableHeader().setForeground(Color.WHITE);
        inventoryTable.getTableHeader().setPreferredSize(new Dimension(0, 45));
        inventoryTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        inventoryTable.setShowGrid(true);
        inventoryTable.setGridColor(new Color(230, 230, 230));

        // Center align all columns
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(JLabel.CENTER);
        for (int i = 0; i < inventoryTable.getColumnCount(); i++) {
            inventoryTable.getColumnModel().getColumn(i).setCellRenderer(centerRenderer);
        }

        // Custom cell renderer for color-coded status
        inventoryTable.getColumnModel().getColumn(2).setCellRenderer(new StatusCellRenderer());

        // Set column widths
        inventoryTable.getColumnModel().getColumn(0).setPreferredWidth(120);
        inventoryTable.getColumnModel().getColumn(1).setPreferredWidth(150);
        inventoryTable.getColumnModel().getColumn(2).setPreferredWidth(130);
        inventoryTable.getColumnModel().getColumn(3).setPreferredWidth(100);
        inventoryTable.getColumnModel().getColumn(4).setPreferredWidth(200);

        JScrollPane scrollPane = new JScrollPane(inventoryTable);
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(200, 200, 200), 1));
        centerPanel.add(scrollPane, BorderLayout.CENTER);

        return centerPanel;
    }

    private JLabel createLegendItem(String text, Color color) {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
        panel.setBackground(Color.WHITE);

        JPanel colorBox = new JPanel();
        colorBox.setPreferredSize(new Dimension(20, 20));
        colorBox.setBackground(color);
        colorBox.setBorder(BorderFactory.createLineBorder(Color.GRAY, 1));

        JLabel label = new JLabel(text);
        label.setFont(new Font("Segoe UI", Font.PLAIN, 12));

        panel.add(colorBox);
        panel.add(label);

        JLabel returnLabel = new JLabel();
        returnLabel.setLayout(new BorderLayout());
        returnLabel.add(panel);
        return returnLabel;
    }

    private JPanel createBottomPanel() {
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 20));
        bottomPanel.setBackground(BACKGROUND_LIGHT);

        refreshButton = new JButton("Refresh Data");
        styleButton(refreshButton, new Color(0, 123, 255));

        backButton = new JButton("Back to Dashboard");
        styleButton(backButton, new Color(108, 117, 125));

        bottomPanel.add(refreshButton);
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

    private void addListeners() {
        refreshButton.addActionListener(e -> loadInventoryData());
        backButton.addActionListener(e -> dispose());
    }

    private void loadInventoryData() {
        tableModel.setRowCount(0);
        int totalUnits = 0;

        try {
            ResultSet rs = BloodInventoryUtil.getAllBloodInventory();

            if (rs != null) {
                while (rs.next()) {
                    String bloodGroup = rs.getString("blood_group");
                    int units = rs.getInt("units_available");
                    String lastUpdated = rs.getString("last_updated");

                    int daysOld = BloodAgeUtil.calculateDaysSinceUpdate(lastUpdated);
                    String status = BloodAgeUtil.getBloodStatus(daysOld);

                    totalUnits += units;

                    Object[] row = {
                            bloodGroup,
                            units + " units",
                            status,
                            daysOld + " days",
                            lastUpdated
                    };
                    tableModel.addRow(row);
                }
                rs.close();
            }

            totalUnitsLabel.setText("Total Units: " + totalUnits);

        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this,
                    "Error loading inventory data: " + ex.getMessage(),
                    "Database Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    // Custom cell renderer for status column
    class StatusCellRenderer extends DefaultTableCellRenderer {
        public StatusCellRenderer() {
            setHorizontalAlignment(JLabel.CENTER);
            setFont(new Font("Segoe UI", Font.BOLD, 13));
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