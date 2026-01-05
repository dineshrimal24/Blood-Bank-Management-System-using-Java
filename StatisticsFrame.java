package projectiv;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.List;

public class StatisticsFrame extends JFrame {
    private JLabel totalDonorsLabel;
    private JLabel todayDonationsLabel;
    private JLabel monthDonationsLabel;
    private JLabel totalBloodUnitsLabel;
    private JButton refreshButton;
    private JButton backButton;
    private JPanel bloodGroupPanel;
    private JPanel graphPanel;

    // Professional color scheme
    private static final Color PRIMARY_COLOR = new Color(220, 53, 69);
    private static final Color ACCENT_BLUE = new Color(0, 123, 255);
    private static final Color ACCENT_GREEN = new Color(40, 167, 69);
    private static final Color ACCENT_PURPLE = new Color(111, 66, 193);
    private static final Color ACCENT_ORANGE = new Color(253, 126, 20);
    private static final Color BACKGROUND_LIGHT = new Color(245, 245, 245);

    public StatisticsFrame() {
        setTitle("Statistics Dashboard");
        setSize(1100, 900);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setResizable(true);
        initComponents();
        loadStatistics();
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

        // Center Panel - Statistics Cards
        JPanel centerPanel = createCenterPanel();
        JScrollPane scrollPane = new JScrollPane(centerPanel);
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

        // Title with icon
        JPanel titlePanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 30, 25));
        titlePanel.setOpaque(false);

        JLabel iconLabel = new JLabel("📊");
        iconLabel.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 36));
        titlePanel.add(iconLabel);

        JLabel titleLabel = new JLabel("Statistics & Analytics Dashboard");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 28));
        titleLabel.setForeground(Color.WHITE);
        titlePanel.add(titleLabel);

        headerPanel.add(titlePanel, BorderLayout.WEST);

        return headerPanel;
    }

    private JPanel createCenterPanel() {
        JPanel centerPanel = new JPanel();
        centerPanel.setLayout(new BoxLayout(centerPanel, BoxLayout.Y_AXIS));
        centerPanel.setBackground(BACKGROUND_LIGHT);
        centerPanel.setBorder(BorderFactory.createEmptyBorder(20, 30, 20, 30));

        // Overview Cards at the top
        JPanel overviewPanel = createOverviewPanel();
        centerPanel.add(overviewPanel);
        centerPanel.add(Box.createVerticalStrut(20));

        // NEW LAYOUT: Graph on LEFT, Blood Group Distribution on RIGHT
        JPanel middleSection = new JPanel(new GridLayout(1, 2, 20, 0));
        middleSection.setBackground(BACKGROUND_LIGHT);
        middleSection.setMaximumSize(new Dimension(1040, 400));

        // LEFT: Monthly Trend Graph
        JPanel graphSection = createGraphSection();
        middleSection.add(graphSection);

        // RIGHT: Blood Group Distribution
        JPanel bloodGroupSection = createBloodGroupSection();
        middleSection.add(bloodGroupSection);

        centerPanel.add(middleSection);

        return centerPanel;
    }

    private JPanel createOverviewPanel() {
        JPanel overviewPanel = new JPanel(new GridLayout(2, 2, 20, 20));
        overviewPanel.setBackground(BACKGROUND_LIGHT);
        overviewPanel.setMaximumSize(new Dimension(1040, 300));

        // Card 1: Total Donors
        totalDonorsLabel = new JLabel("0");
        overviewPanel.add(
                createStatCard("Total Donors", totalDonorsLabel, "👥", ACCENT_BLUE)
        );

        // Card 2: Today's Donations
        todayDonationsLabel = new JLabel("0");
        overviewPanel.add(createStatCard("Today's Donations", todayDonationsLabel, "⭐", ACCENT_GREEN)
        );

        // Card 3: This Month
        monthDonationsLabel = new JLabel("0");
        overviewPanel.add(
                createStatCard("This Month", monthDonationsLabel, "🗓️", ACCENT_PURPLE)
        );

        // Card 4: Total Blood Units
        totalBloodUnitsLabel = new JLabel("0");
        overviewPanel.add(
                createStatCard("Total Blood Units", totalBloodUnitsLabel, "🩸", PRIMARY_COLOR)
        );
        return overviewPanel;
    }

    private JPanel createStatCard(String title, JLabel valueLabel, String emoji, Color accentColor) {
        JPanel card = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                // Card background
                g2d.setColor(Color.WHITE);
                g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 15, 15);

                // Accent bar at top
                g2d.setColor(accentColor);
                g2d.fillRoundRect(0, 0, getWidth(), 8, 15, 15);

                // Shadow
                g2d.setColor(new Color(0, 0, 0, 15));
                g2d.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 15, 15);
            }
        };
        card.setLayout(new BorderLayout());
        card.setOpaque(false);
        card.setBorder(BorderFactory.createEmptyBorder(20, 25, 20, 25));

        // Icon and title
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        topPanel.setOpaque(false);

        JLabel iconLabel = new JLabel(emoji);
        iconLabel.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 32));
        topPanel.add(iconLabel);

        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
        titleLabel.setForeground(new Color(100, 100, 100));
        topPanel.add(titleLabel);

        card.add(topPanel, BorderLayout.NORTH);

        // Value
        valueLabel.setFont(new Font("Segoe UI", Font.BOLD, 42));
        valueLabel.setForeground(accentColor);
        valueLabel.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0));
        card.add(valueLabel, BorderLayout.CENTER);

        return card;
    }

    private JPanel createGraphSection() {
        JPanel section = new JPanel();
        section.setLayout(new BoxLayout(section, BoxLayout.Y_AXIS));
        section.setBackground(Color.WHITE);
        section.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200), 1),
                BorderFactory.createEmptyBorder(20, 25, 20, 25)
        ));
        // Removed setMaximumSize to allow it to expand

        // Title
        JLabel sectionTitle = new JLabel("📈 Monthly Donation Trends");
        sectionTitle.setFont(new Font("Segoe UI", Font.BOLD, 18)); // Slightly smaller font
        sectionTitle.setForeground(new Color(33, 37, 41));
        sectionTitle.setAlignmentX(Component.LEFT_ALIGNMENT);
        section.add(sectionTitle);
        section.add(Box.createVerticalStrut(15));

        // Graph Panel
        graphPanel = new JPanel();
        graphPanel.setBackground(Color.WHITE);
        graphPanel.setPreferredSize(new Dimension(450, 280)); // Adjusted size
        graphPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        section.add(graphPanel);

        return section;
    }
    private JPanel createBloodGroupSection() {
        JPanel section = new JPanel();
        section.setLayout(new BoxLayout(section, BoxLayout.Y_AXIS));
        section.setBackground(Color.WHITE);
        section.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200), 1),
                BorderFactory.createEmptyBorder(20, 25, 20, 25)
        ));
        // Removed setMaximumSize to allow it to expand

        // Title
        JLabel sectionTitle = new JLabel("Blood Group Distribution");
        sectionTitle.setFont(new Font("Segoe UI", Font.BOLD, 18)); // Slightly smaller font
        sectionTitle.setForeground(new Color(33, 37, 41));
        sectionTitle.setAlignmentX(Component.LEFT_ALIGNMENT);
        section.add(sectionTitle);
        section.add(Box.createVerticalStrut(15));

        // Blood group bars
        bloodGroupPanel = new JPanel();
        bloodGroupPanel.setLayout(new GridLayout(4, 2, 15, 15));
        bloodGroupPanel.setBackground(Color.WHITE);
        bloodGroupPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        section.add(bloodGroupPanel);

        return section;
    }
    private void createBloodGroupBar(String bloodGroup, int count, int maxCount) {
        JPanel barPanel = new JPanel();
        barPanel.setLayout(new BorderLayout(10, 5));
        barPanel.setBackground(Color.WHITE);

        // Blood group label
        JLabel bgLabel = new JLabel(bloodGroup);
        bgLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        bgLabel.setPreferredSize(new Dimension(50, 25));
        barPanel.add(bgLabel, BorderLayout.WEST);

        // Progress bar
        JPanel barContainer = new JPanel(new BorderLayout());
        barContainer.setBackground(new Color(240, 240, 240));
        barContainer.setPreferredSize(new Dimension(0, 25));

        double percentage = maxCount > 0 ? (double) count / maxCount : 0;

        JPanel bar = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                GradientPaint gradient = new GradientPaint(
                        0, 0, PRIMARY_COLOR,
                        getWidth(), 0, new Color(185, 28, 46)
                );
                g2d.setPaint(gradient);
                g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 5, 5);
            }
        };
        bar.setOpaque(false);
        bar.setPreferredSize(new Dimension((int)(barContainer.getPreferredSize().width * percentage), 25));

        barContainer.add(bar, BorderLayout.WEST);
        barPanel.add(barContainer, BorderLayout.CENTER);

        // Count label
        JLabel countLabel = new JLabel(count + " donors");
        countLabel.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        countLabel.setForeground(new Color(100, 100, 100));
        countLabel.setPreferredSize(new Dimension(90, 25));
        barPanel.add(countLabel, BorderLayout.EAST);

        bloodGroupPanel.add(barPanel);
    }

    private JPanel createBottomPanel() {
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 20));
        bottomPanel.setBackground(BACKGROUND_LIGHT);

        refreshButton = new JButton("Refresh Statistics");
        styleButton(refreshButton, ACCENT_BLUE);

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
        button.setPreferredSize(new Dimension(220, 45));
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
        refreshButton.addActionListener(e -> loadStatistics());
        backButton.addActionListener(e -> dispose());
    }

    private void loadStatistics() {
        try {
            // Total Donors
            int totalDonors = getTotalDonors();
            totalDonorsLabel.setText(String.valueOf(totalDonors));

            // Today's Donations
            int todayDonations = getTodayDonations();
            todayDonationsLabel.setText(String.valueOf(todayDonations));

            // This Month's Donations
            int monthDonations = getMonthDonations();
            monthDonationsLabel.setText(String.valueOf(monthDonations));

            // Total Blood Units
            int totalUnits = getTotalBloodUnits();
            totalBloodUnitsLabel.setText(totalUnits + " units");

            // Blood Group Distribution
            Map<String, Integer> bloodGroupCounts = getBloodGroupDistribution();
            displayBloodGroupDistribution(bloodGroupCounts);

            // Monthly Trend Graph
            Map<YearMonth, Integer> monthlyData = getMonthlyDonationTrend();
            displayMonthlyTrendGraph(monthlyData);

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this,
                    "Error loading statistics: " + ex.getMessage(),
                    "Database Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private int getTotalDonors() throws SQLException {
        String query = "SELECT COUNT(*) as count FROM donors";
        try (Connection conn = DatabaseUtil.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
            if (rs.next()) {
                return rs.getInt("count");
            }
        }
        return 0;
    }

    private int getTodayDonations() throws SQLException {
        String today = LocalDate.now().toString();
        String query = "SELECT COUNT(*) as count FROM donation_history WHERE donation_date = ?";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setString(1, today);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("count");
                }
            }
        }
        return 0;
    }

    private int getMonthDonations() throws SQLException {
        String firstDayOfMonth = LocalDate.now().withDayOfMonth(1).toString();
        String query = "SELECT COUNT(*) as count FROM donation_history WHERE donation_date >= ?";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setString(1, firstDayOfMonth);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("count");
                }
            }
        }
        return 0;
    }

    private int getTotalBloodUnits() throws SQLException {
        String query = "SELECT SUM(units_available) as total FROM blood_inventory";
        try (Connection conn = DatabaseUtil.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
            if (rs.next()) {
                return rs.getInt("total");
            }
        }
        return 0;
    }

    private Map<String, Integer> getBloodGroupDistribution() throws SQLException {
        Map<String, Integer> distribution = new HashMap<>();
        String query = "SELECT blood_group, COUNT(*) as count FROM donors GROUP BY blood_group";

        try (Connection conn = DatabaseUtil.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            while (rs.next()) {
                distribution.put(rs.getString("blood_group"), rs.getInt("count"));
            }
        }

        // Ensure all blood groups are present
        String[] allGroups = {"A+", "A-", "B+", "B-", "AB+", "AB-", "O+", "O-"};
        for (String group : allGroups) {
            distribution.putIfAbsent(group, 0);
        }

        return distribution;
    }

    private Map<YearMonth, Integer> getMonthlyDonationTrend() throws SQLException {
        Map<YearMonth, Integer> monthlyData = new LinkedHashMap<>();

        // Get last 12 months of data
        YearMonth currentMonth = YearMonth.now();
        for (int i = 11; i >= 0; i--) {
            YearMonth month = currentMonth.minusMonths(i);
            monthlyData.put(month, 0);
        }

        String query = "SELECT donation_date FROM donation_history WHERE donation_date >= ?";
        String startDate = currentMonth.minusMonths(11).atDay(1).toString();

        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setString(1, startDate);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    LocalDate date = LocalDate.parse(rs.getString("donation_date"));
                    YearMonth yearMonth = YearMonth.from(date);
                    monthlyData.put(yearMonth, monthlyData.getOrDefault(yearMonth, 0) + 1);
                }
            }
        }

        return monthlyData;
    }

    private void displayBloodGroupDistribution(Map<String, Integer> distribution) {
        bloodGroupPanel.removeAll();

        // Find max count for scaling bars
        int maxCount = distribution.values().stream().mapToInt(Integer::intValue).max().orElse(1);

        // Display in order
        String[] groups = {"A+", "A-", "B+", "B-", "AB+", "AB-", "O+", "O-"};
        for (String group : groups) {
            createBloodGroupBar(group, distribution.get(group), maxCount);
        }

        bloodGroupPanel.revalidate();
        bloodGroupPanel.repaint();
    }

    private void displayMonthlyTrendGraph(Map<YearMonth, Integer> monthlyData) {
        graphPanel.removeAll();
        graphPanel.setLayout(new BorderLayout());

        LineGraphPanel lineGraph = new LineGraphPanel(monthlyData);
        graphPanel.add(lineGraph, BorderLayout.CENTER);

        graphPanel.revalidate();
        graphPanel.repaint();
    }

    // Inner class for line graph visualization
    private class LineGraphPanel extends JPanel {
        private Map<YearMonth, Integer> data;
        private List<YearMonth> months;
        private List<Integer> values;
        private int maxValue;

        public LineGraphPanel(Map<YearMonth, Integer> data) {
            this.data = data;
            this.months = new ArrayList<>(data.keySet());
            this.values = new ArrayList<>(data.values());
            this.maxValue = values.stream().mapToInt(Integer::intValue).max().orElse(10);
            if (maxValue == 0) maxValue = 10; // Avoid division by zero
            setBackground(Color.WHITE);
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2d = (Graphics2D) g;
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            int width = getWidth();
            int height = getHeight();
            int padding = 50;
            int graphWidth = width - 2 * padding;
            int graphHeight = height - 2 * padding;

            // Draw axes
            g2d.setColor(new Color(200, 200, 200));
            g2d.setStroke(new BasicStroke(2));
            g2d.drawLine(padding, height - padding, width - padding, height - padding); // X-axis
            g2d.drawLine(padding, padding, padding, height - padding); // Y-axis

            // Draw grid lines
            g2d.setColor(new Color(240, 240, 240));
            g2d.setStroke(new BasicStroke(1));
            for (int i = 0; i <= 5; i++) {
                int y = height - padding - (graphHeight * i / 5);
                g2d.drawLine(padding, y, width - padding, y);
            }

            // Draw Y-axis labels
            g2d.setColor(new Color(100, 100, 100));
            g2d.setFont(new Font("Segoe UI", Font.PLAIN, 11));
            for (int i = 0; i <= 5; i++) {
                int y = height - padding - (graphHeight * i / 5);
                int value = (maxValue * i / 5);
                String label = String.valueOf(value);
                g2d.drawString(label, padding - 35, y + 5);
            }

            if (months.isEmpty()) return;

            // Calculate points
            int pointSpacing = graphWidth / Math.max(months.size() - 1, 1);
            List<Point> points = new ArrayList<>();

            for (int i = 0; i < values.size(); i++) {
                int x = padding + (i * graphWidth / Math.max(months.size() - 1, 1));
                int value = values.get(i);
                int y = height - padding - (int) ((double) value / maxValue * graphHeight);
                points.add(new Point(x, y));
            }

            // Draw line
            g2d.setColor(PRIMARY_COLOR);
            g2d.setStroke(new BasicStroke(3));
            for (int i = 0; i < points.size() - 1; i++) {
                Point p1 = points.get(i);
                Point p2 = points.get(i + 1);
                g2d.drawLine(p1.x, p1.y, p2.x, p2.y);
            }

            // Draw gradient fill under line
            if (points.size() >= 2) {
                int[] xPoints = new int[points.size() + 2];
                int[] yPoints = new int[points.size() + 2];

                for (int i = 0; i < points.size(); i++) {
                    xPoints[i] = points.get(i).x;
                    yPoints[i] = points.get(i).y;
                }
                xPoints[points.size()] = points.get(points.size() - 1).x;
                yPoints[points.size()] = height - padding;
                xPoints[points.size() + 1] = points.get(0).x;
                yPoints[points.size() + 1] = height - padding;

                GradientPaint gradientFill = new GradientPaint(
                        0, padding, new Color(220, 53, 69, 100),
                        0, height - padding, new Color(220, 53, 69, 20)
                );
                g2d.setPaint(gradientFill);
                g2d.fillPolygon(xPoints, yPoints, xPoints.length);
            }

            // Draw points and labels
            g2d.setColor(PRIMARY_COLOR);
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMM");
            for (int i = 0; i < points.size(); i++) {
                Point p = points.get(i);

                // Draw point
                g2d.fillOval(p.x - 5, p.y - 5, 10, 10);
                g2d.setColor(Color.WHITE);
                g2d.fillOval(p.x - 3, p.y - 3, 6, 6);
                g2d.setColor(PRIMARY_COLOR);

                // Draw value above point
                g2d.setFont(new Font("Segoe UI", Font.BOLD, 11));
                String valueStr = String.valueOf(values.get(i));
                int strWidth = g2d.getFontMetrics().stringWidth(valueStr);
                g2d.drawString(valueStr, p.x - strWidth / 2, p.y - 10);

                // Draw month label
                g2d.setColor(new Color(100, 100, 100));
                g2d.setFont(new Font("Segoe UI", Font.PLAIN, 10));
                String monthLabel = months.get(i).format(formatter);
                int labelWidth = g2d.getFontMetrics().stringWidth(monthLabel);
                g2d.drawString(monthLabel, p.x - labelWidth / 2, height - padding + 20);
                g2d.setColor(PRIMARY_COLOR);
            }
        }
    }
}
