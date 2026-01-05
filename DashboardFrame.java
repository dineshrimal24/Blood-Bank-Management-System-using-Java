package projectiv;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.net.URL;

public class DashboardFrame extends JFrame {

    // Professional color scheme
    private static final Color PRIMARY_COLOR = new Color(220, 53, 69);
    private static final Color SECONDARY_COLOR = new Color(108, 117, 125);
    private static final Color ACCENT_COLOR = new Color(40, 167, 69);
    private static final Color BACKGROUND_LIGHT = new Color(248, 249, 250);
    private static final Color CARD_BACKGROUND = Color.WHITE;
    private static final Color TEXT_DARK = new Color(33, 37, 41);

    public DashboardFrame() {
        setTitle("Blood Bank Management System - Dashboard");
        setSize(1200, 800);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        // Main panel with professional background
        JPanel mainPanel = createMainPanel();

        add(mainPanel);
    }

    private JPanel createMainPanel() {
        JPanel mainPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);

                // Subtle gradient background
                GradientPaint gradient = new GradientPaint(
                        0, 0, new Color(248, 249, 250),
                        0, getHeight(), new Color(233, 236, 239)
                );
                g2d.setPaint(gradient);
                g2d.fillRect(0, 0, getWidth(), getHeight());
            }
        };
        mainPanel.setLayout(new BorderLayout());

        // Top header panel
        JPanel headerPanel = createHeaderPanel();

        // Content panel with cards
        JPanel contentPanel = createContentPanel();

        mainPanel.add(headerPanel, BorderLayout.NORTH);
        mainPanel.add(contentPanel, BorderLayout.CENTER);

        return mainPanel;
    }

    private JPanel createHeaderPanel() {
        JPanel headerPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;

                // Header gradient
                GradientPaint gradient = new GradientPaint(
                        0, 0, PRIMARY_COLOR,
                        getWidth(), 0, new Color(185, 28, 46)
                );
                g2d.setPaint(gradient);
                g2d.fillRect(0, 0, getWidth(), getHeight());

                // Subtle shadow at bottom
                g2d.setPaint(new GradientPaint(0, getHeight() - 5, new Color(0, 0, 0, 50),
                        0, getHeight(), new Color(0, 0, 0, 0)));
                g2d.fillRect(0, getHeight() - 5, getWidth(), 5);
            }
        };
        headerPanel.setPreferredSize(new Dimension(0, 100));
        headerPanel.setLayout(new BorderLayout());
        headerPanel.setOpaque(false);

        // Left side - Title and icon
        JPanel leftHeader = new JPanel(new FlowLayout(FlowLayout.LEFT, 30, 25));
        leftHeader.setOpaque(false);

        JLabel iconLabel = IconUtil.createHeaderIcon("blood", 36);

        leftHeader.add(iconLabel);

        JLabel titleLabel = new JLabel("Blood Bank Dashboard");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 32));
        titleLabel.setForeground(Color.WHITE);
        leftHeader.add(titleLabel);

        // Right side - User info
        JPanel rightHeader = new JPanel(new FlowLayout(FlowLayout.RIGHT, 30, 30));
        rightHeader.setOpaque(false);

        JLabel userLabel = new JLabel("Welcome, Admin");
        userLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        userLabel.setForeground(new Color(255, 255, 255, 230));
        rightHeader.add(userLabel);

        headerPanel.add(leftHeader, BorderLayout.WEST);
        headerPanel.add(rightHeader, BorderLayout.EAST);

        return headerPanel;
    }

    private JPanel createContentPanel() {
        JPanel contentPanel = new JPanel();
        contentPanel.setOpaque(false);
        contentPanel.setLayout(new GridBagLayout());
        contentPanel.setBorder(BorderFactory.createEmptyBorder(30, 40, 30, 40));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.BOTH;
        gbc.insets = new Insets(15, 15, 15, 15);
        gbc.weightx = 1;
        gbc.weighty = 1;

        // Row 1
        gbc.gridx = 0; gbc.gridy = 0;
        contentPanel.add(createModernCard("Add Donor", "blood-donor.png", new Color(220, 53, 69)), gbc);

        gbc.gridx = 1;
        contentPanel.add(createModernCard("Update Donor", "update.png", new Color(0, 123, 255)), gbc);

        gbc.gridx = 2;
        contentPanel.add(createModernCard("Search Donor", "search-interface-symbol.png", new Color(108, 117, 125)), gbc);

        gbc.gridx = 3;
        contentPanel.add(createModernCard("Delete Donor", "garbage.png", new Color(220, 53, 69)), gbc);

        // Row 2
        gbc.gridx = 0; gbc.gridy = 1;
        contentPanel.add(createModernCard("Stock Increase", "warehouse.png", new Color(40, 167, 69)), gbc);

        gbc.gridx = 1;
        contentPanel.add(createModernCard("Stock Decrease", "decrease.png", new Color(255, 193, 7)), gbc);

        gbc.gridx = 2;
        contentPanel.add(createModernCard("Search (Blood Group)", "blood-test.png", new Color(111, 66, 193)), gbc);

        gbc.gridx = 3;
        contentPanel.add(createModernCard("Donor Report", "analytics.png", new Color(23, 162, 184)), gbc);

        // Row 3 - Additional features
        gbc.gridx = 0; gbc.gridy = 2;
        contentPanel.add(createModernCard("Blood Inventory", "📦", new Color(0, 123, 255)), gbc);

        gbc.gridx = 1;
        contentPanel.add(createModernCard("Statistics", "📊", new Color(111, 66, 193)), gbc);

        gbc.gridx = 2;
        contentPanel.add(createModernCard("Donation Centers", "🏥", new Color(255, 87, 34)), gbc); // NEW

        gbc.gridx = 3;
        contentPanel.add(createModernCard("Settings", "⚙️", new Color(108, 117, 125)), gbc);

        // Row 4 - Exit
        gbc.gridx = 0; gbc.gridy = 3; gbc.gridwidth = 4;
        contentPanel.add(createModernCard("Exit", "logout (2).png", new Color(220, 53, 69)), gbc);

        return contentPanel;
    }

    private JPanel createModernCard(String title, String iconFile, Color accentColor) {
        JPanel card = new JPanel() {
            private boolean hover = false;

            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                // Card background with rounded corners
                g2d.setColor(CARD_BACKGROUND);
                g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 15, 15);

                // Accent color bar at top
                g2d.setColor(accentColor);
                g2d.fillRoundRect(0, 0, getWidth(), 8, 15, 15);

                // Hover effect
                if (hover) {
                    g2d.setColor(new Color(0, 0, 0, 20));
                    g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 15, 15);
                }

                // Shadow effect
                g2d.setColor(new Color(0, 0, 0, 15));
                g2d.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 15, 15);
            }

            {
                addMouseListener(new MouseAdapter() {
                    @Override
                    public void mouseEntered(MouseEvent e) {
                        hover = true;
                        setCursor(new Cursor(Cursor.HAND_CURSOR));
                        repaint();
                    }

                    @Override
                    public void mouseExited(MouseEvent e) {
                        hover = false;
                        setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
                        repaint();
                    }

                    @Override
                    public void mouseClicked(MouseEvent e) {
                        handleCardClick(title);
                    }
                });
            }
        };

        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setOpaque(false);
        card.setBorder(BorderFactory.createEmptyBorder(25, 20, 25, 20));

        // Icon
        JLabel iconLabel;
        if (iconFile.length() <= 3) {
            // It's an emoji
            iconLabel = new JLabel(iconFile);
            iconLabel.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 48));
        } else {
            iconLabel = new JLabel(loadIcon(iconFile));
        }
        iconLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Title
        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
        titleLabel.setForeground(TEXT_DARK);
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        card.add(Box.createVerticalGlue());
        card.add(iconLabel);
        card.add(Box.createVerticalStrut(15));
        card.add(titleLabel);
        card.add(Box.createVerticalGlue());

        return card;
    }

    private Icon loadIcon(String iconFile) {
        String resourcePath = "/icons/" + iconFile;
        URL resource = getClass().getResource(resourcePath);
        if (resource != null) {
            ImageIcon icon = new ImageIcon(resource);
            Image scaled = icon.getImage().getScaledInstance(48, 48, Image.SCALE_SMOOTH);
            return new ImageIcon(scaled);
        }

        File fallback = new File("src/icons/" + iconFile);
        if (fallback.exists()) {
            ImageIcon icon = new ImageIcon(fallback.getAbsolutePath());
            Image scaled = icon.getImage().getScaledInstance(48, 48, Image.SCALE_SMOOTH);
            return new ImageIcon(scaled);
        }

        return UIManager.getIcon("OptionPane.informationIcon");
    }

    private void handleCardClick(String title) {
        switch (title) {
            case "Donor Report":
                openFrame(() -> new DonorReportFrame());
                break;

            case "Add Donor":
                openFrame(() -> new AddDonorFrame());
                break;

            case "Update Donor":
            case "Update":
                handleUpdateDonor();
                break;

            case "Search Donor":
                openFrame(() -> new SearchDonorFrame());
                break;

            case "Stock Increase":
                openFrame(() -> new StockIncreaseFrame());
                break;

            case "Stock Decrease":
                openFrame(() -> new StockDecreaseFrame());
                break;

            case "Delete Donor":
                openFrame(() -> new DeleteDonorFrame());
                break;

            case "Search (Blood Group)":
                handleSearchByBloodGroup();
                break;

            case "Blood Inventory":
                openFrame(() -> new BloodInventoryFrame());
                break;

            case "Statistics":
                openFrame(() -> new StatisticsFrame());
                break;

            case "Donation Centers": // NEW CASE
                openFrame(() -> new DonationCenterFrame());
                break;

            case "Settings":
                openFrame(() -> new SettingsFrame());
                break;

            case "Exit":
                int choice = JOptionPane.showConfirmDialog(
                        this,
                        "Are you sure you want to exit?",
                        "Exit Confirmation",
                        JOptionPane.YES_NO_OPTION
                );
                if (choice == JOptionPane.YES_OPTION) {
                    System.exit(0);
                }
                break;
        }
    }

    private void handleUpdateDonor() {
        String input = JOptionPane.showInputDialog(
                this,
                "Enter Donor ID or Phone Number:",
                "Update Donor",
                JOptionPane.QUESTION_MESSAGE
        );

        if (input == null || input.trim().isEmpty()) {
            return;
        }

        try {
            Donor donor = DatabaseUtil.searchDonorByIdOrPhone(input.trim());

            if (donor == null) {
                JOptionPane.showMessageDialog(
                        this,
                        "No donor found with ID or Phone: " + input,
                        "Not Found",
                        JOptionPane.ERROR_MESSAGE
                );
                return;
            }

            UpdateDonorFrame updateFrame = new UpdateDonorFrame(donor);
            updateFrame.setVisible(true);
            this.setVisible(false);

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(
                    this,
                    "Error searching for donor: " + ex.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }

    private void openFrame(FrameSupplier supplier) {
        try {
            JFrame frame = supplier.get();
            frame.setVisible(true);
            this.setVisible(false);
        } catch (Exception e) {
            showStyledMessage("Error: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void handleSearchByBloodGroup() {
        String[] bloodGroups = {"A+", "A-", "B+", "B-", "AB+", "AB-", "O+", "O-"};
        String bloodGroup = (String) JOptionPane.showInputDialog(
                this,
                "Select Blood Group:",
                "Search by Blood Group",
                JOptionPane.QUESTION_MESSAGE,
                null,
                bloodGroups,
                bloodGroups[0]
        );

        if (bloodGroup != null) {
            openFrame(() -> new SearchBloodGroupFrame(bloodGroup));
        }
    }

    private void showStyledMessage(String message, String title, int messageType) {
        JOptionPane.showMessageDialog(this, message, title, messageType);
    }

    @FunctionalInterface
    interface FrameSupplier {
        JFrame get() throws Exception;
    }
}