package projectiv;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import projectiv.IconUtil;


public class DeleteDonorFrame extends JFrame {
    private JComboBox<String> searchTypeCombo;
    private JTextField searchField;
    private JLabel donorInfoLabel;
    private JButton searchButton;
    private JButton deleteButton;
    private JButton clearButton;
    private JButton backButton;

    private int currentDonorId = -1;

    public DeleteDonorFrame() {
        setTitle("Delete Donor");
        setSize(620, 760);
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
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(40, 50, 40, 50));
        mainPanel.setBackground(new Color(245, 245, 245));

        // Title
        JLabel titleLabel = new JLabel("Delete Donor");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 28));
        titleLabel.setForeground(new Color(139, 0, 0));
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        mainPanel.add(titleLabel);
        mainPanel.add(Box.createVerticalStrut(40));

        // Search Panel
        JPanel searchPanel = createStyledPanel();
        searchPanel.setMaximumSize(new Dimension(520, 220));

        JLabel searchLabel = new JLabel("Search Donor By:");
        searchLabel.setFont(new Font("Arial", Font.BOLD, 16));

        searchTypeCombo = new JComboBox<>(new String[]{"Donor ID", "Phone Number"});
        searchTypeCombo.setFont(new Font("Arial", Font.PLAIN, 14));
        searchTypeCombo.setMaximumSize(new Dimension(350, 35));

        JLabel valueLabel = new JLabel("Enter Value:");
        valueLabel.setFont(new Font("Arial", Font.BOLD, 16));

        searchField = new JTextField(20);
        searchField.setFont(new Font("Arial", Font.PLAIN, 16));
        searchField.setMaximumSize(new Dimension(350, 35));

        searchButton = new JButton("Search Donor");
        styleButton(searchButton, new Color(70, 130, 180));

        searchPanel.add(searchLabel);
        searchPanel.add(Box.createVerticalStrut(12));
        searchPanel.add(searchTypeCombo);
        searchPanel.add(Box.createVerticalStrut(15));
        searchPanel.add(valueLabel);
        searchPanel.add(Box.createVerticalStrut(12));
        searchPanel.add(searchField);
        searchPanel.add(Box.createVerticalStrut(15));
        searchPanel.add(searchButton);

        mainPanel.add(searchPanel);
        mainPanel.add(Box.createVerticalStrut(25));

        // Donor Info Panel
        JPanel infoPanel = createStyledPanel();
        infoPanel.setMaximumSize(new Dimension(520, 280));
        donorInfoLabel = new JLabel("No donor selected");
        donorInfoLabel.setFont(new Font("Arial", Font.PLAIN, 14));

        infoPanel.add(donorInfoLabel);
        mainPanel.add(infoPanel);
        mainPanel.add(Box.createVerticalStrut(25));

        // Delete Button
        deleteButton = new JButton("Delete Donor");
        styleButton(deleteButton, new Color(220, 20, 60));
        deleteButton.setEnabled(false);
        deleteButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        mainPanel.add(deleteButton);
        mainPanel.add(Box.createVerticalStrut(30));

        // Bottom Buttons
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 15));
        buttonPanel.setBackground(new Color(245, 245, 245));
        clearButton = new JButton("Clear");
        backButton = new JButton("Back");
        styleButton(clearButton, new Color(100, 100, 100));
        styleButton(backButton, new Color(100, 100, 100));

        buttonPanel.add(clearButton);
        buttonPanel.add(backButton);
        mainPanel.add(buttonPanel);

        add(mainPanel);
    }

    private JPanel createStyledPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200), 1),
                BorderFactory.createEmptyBorder(20, 20, 20, 20)
        ));
        return panel;
    }

    private void styleButton(JButton button, Color color) {
        button.setFont(new Font("Arial", Font.BOLD, 16));
        button.setBackground(color);
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setAlignmentX(Component.CENTER_ALIGNMENT);
        button.setPreferredSize(new Dimension(180, 40));
    }

    private void addListeners() {
        searchButton.addActionListener(e -> searchDonor());
        deleteButton.addActionListener(e -> deleteDonor());
        clearButton.addActionListener(e -> clearForm());
        backButton.addActionListener(e -> dispose());
        searchField.addActionListener(e -> searchDonor());
    }

    private void searchDonor() {
        String searchType = (String) searchTypeCombo.getSelectedItem();
        String searchValue = searchField.getText().trim();

        if (searchValue.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Please enter a " + searchType.toLowerCase() + ".",
                    "Input Required",
                    JOptionPane.WARNING_MESSAGE);
            searchField.requestFocus();
            return;
        }

        try {
            Donor donor = null;

            if ("Phone Number".equals(searchType)) {
                donor = DatabaseUtil.getDonorByPhone(searchValue);
            } else if ("Donor ID".equals(searchType)) {
                int id;
                try {
                    id = Integer.parseInt(searchValue);
                } catch (NumberFormatException e) {
                    JOptionPane.showMessageDialog(this,
                            "Please enter a valid numeric Donor ID.",
                            "Invalid Input",
                            JOptionPane.WARNING_MESSAGE);
                    searchField.requestFocus();
                    return;
                }
                donor = DatabaseUtil.getDonorByIdObject(id);
            }

            if (donor == null) {
                JOptionPane.showMessageDialog(this,
                        "No donor found with " + searchType.toLowerCase() + ": " + searchValue,
                        "Not Found",
                        JOptionPane.ERROR_MESSAGE);
                clearForm();
                return;
            }

            currentDonorId = donor.getId();

            // Display donor info
            String info = String.format(
                    "<html><b>Donor Found:</b><br><br>" +
                            "Donor ID: %s<br>" +
                            "Name: %s<br>" +
                            "Father Name: %s<br>" +
                            "Age: %d<br>" +
                            "Gender: %s<br>" +
                            "Blood Group: %s<br>" +
                            "Phone: %s<br>" +
                            "City: %s</html>",
                    donor.getId(),               // use getId() instead of getDonorId()
                    donor.getName(),
                    donor.getFatherName(),
                    donor.getAge(),
                    donor.getGender(),
                    donor.getBloodGroup(),
                    donor.getPhone(),
                    donor.getDonatedCity()
            );
            donorInfoLabel.setText(info);

            // Enable delete button
            deleteButton.setEnabled(true);

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this,
                    "Error searching donor: " + ex.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void deleteDonor() {
        if (currentDonorId == -1) {
            JOptionPane.showMessageDialog(this,
                    "Please search for a donor first.",
                    "No Donor Selected",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        // Confirmation dialog
        int confirm = JOptionPane.showConfirmDialog(this,
                "Are you sure you want to delete this donor?\n" +
                        "This action cannot be undone!",
                "Confirm Deletion",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE);

        if (confirm != JOptionPane.YES_OPTION) {
            return;
        }

        try {
            boolean success = DatabaseUtil.deleteDonor(currentDonorId);

            if (success) {
                JOptionPane.showMessageDialog(this,
                        "Donor deleted successfully!",
                        "Success",
                        JOptionPane.INFORMATION_MESSAGE);
                clearForm();
            } else {
                JOptionPane.showMessageDialog(this,
                        "Failed to delete donor. Please try again.",
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
            }

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this,
                    "Error deleting donor: " + ex.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void clearForm() {
        searchField.setText("");
        donorInfoLabel.setText("No donor selected");
        currentDonorId = -1;
        deleteButton.setEnabled(false);
        searchTypeCombo.setSelectedIndex(0);
        searchField.requestFocus();
    }
}