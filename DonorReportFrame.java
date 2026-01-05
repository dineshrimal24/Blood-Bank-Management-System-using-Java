package projectiv;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.io.FileOutputStream;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import projectiv.IconUtil;


// iText 5 imports - MAKE SURE YOU HAVE itextpdf-5.5.13.3.jar
import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Document;
import com.itextpdf.text.Element;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
// DO NOT import com.itextpdf.text.Font to avoid conflict with java.awt.Font

public class DonorReportFrame extends JFrame {
    private JButton generateAllButton;
    private JButton generateByBloodGroupButton;
    private JButton generateByPhoneButton;
    private JButton backButton;
    private JComboBox<String> bloodGroupCombo;
    private JTextField phoneField;

    public DonorReportFrame() {
        setTitle("Donor Report");
        setSize(700, 550);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setResizable(false);

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
        mainPanel.setBorder(BorderFactory.createEmptyBorder(30, 40, 30, 40));
        mainPanel.setBackground(new Color(245, 245, 245));

        // Title
        JLabel titleLabel = new JLabel("Donor Report Generator");
        titleLabel.setFont(new java.awt.Font("Arial", java.awt.Font.BOLD, 24));
        titleLabel.setForeground(new Color(139, 0, 0));
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        mainPanel.add(titleLabel);
        mainPanel.add(Box.createVerticalStrut(30));

        // Generate All Donors Report
        JPanel allPanel = createReportPanel();
        JLabel allLabel = new JLabel("Generate Complete Donor Report");
        allLabel.setFont(new java.awt.Font("Arial", java.awt.Font.BOLD, 14));
        generateAllButton = new JButton("Generate All Donors PDF");
        styleButton(generateAllButton, new Color(139, 0, 0));
        allPanel.add(allLabel);
        allPanel.add(Box.createVerticalStrut(10));
        allPanel.add(generateAllButton);
        mainPanel.add(allPanel);
        mainPanel.add(Box.createVerticalStrut(20));

        // Generate by Blood Group
        JPanel bloodGroupPanel = createReportPanel();
        JLabel bloodGroupLabel = new JLabel("Generate Report by Blood Group");
        bloodGroupLabel.setFont(new java.awt.Font("Arial", java.awt.Font.BOLD, 14));
        bloodGroupCombo = new JComboBox<>(new String[]{"A+", "A-", "B+", "B-", "AB+", "AB-", "O+", "O-"});
        bloodGroupCombo.setFont(new java.awt.Font("Arial", java.awt.Font.PLAIN, 14));
        bloodGroupCombo.setMaximumSize(new Dimension(150, 30));
        generateByBloodGroupButton = new JButton("Generate Blood Group PDF");
        styleButton(generateByBloodGroupButton, new Color(70, 130, 180));
        bloodGroupPanel.add(bloodGroupLabel);
        bloodGroupPanel.add(Box.createVerticalStrut(10));
        bloodGroupPanel.add(bloodGroupCombo);
        bloodGroupPanel.add(Box.createVerticalStrut(10));
        bloodGroupPanel.add(generateByBloodGroupButton);
        mainPanel.add(bloodGroupPanel);
        mainPanel.add(Box.createVerticalStrut(20));

        // Generate by Phone Number
        JPanel phonePanel = createReportPanel();
        JLabel phoneLabel = new JLabel("Generate Report for Specific Donor (Phone)");
        phoneLabel.setFont(new java.awt.Font("Arial", java.awt.Font.BOLD, 14));
        phoneField = new JTextField(15);
        phoneField.setFont(new java.awt.Font("Arial", java.awt.Font.PLAIN, 14));
        phoneField.setMaximumSize(new Dimension(200, 30));
        generateByPhoneButton = new JButton("Generate Single Donor PDF");
        styleButton(generateByPhoneButton, new Color(34, 139, 34));
        phonePanel.add(phoneLabel);
        phonePanel.add(Box.createVerticalStrut(10));
        phonePanel.add(phoneField);
        phonePanel.add(Box.createVerticalStrut(10));
        phonePanel.add(generateByPhoneButton);
        mainPanel.add(phonePanel);
        mainPanel.add(Box.createVerticalStrut(30));

        // Back button
        backButton = new JButton("Back to Dashboard");
        styleButton(backButton, new Color(100, 100, 100));
        backButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        mainPanel.add(backButton);

        add(mainPanel);
    }

    private JPanel createReportPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200), 1),
                BorderFactory.createEmptyBorder(15, 15, 15, 15)
        ));
        panel.setMaximumSize(new Dimension(500, 150));
        return panel;
    }

    private void styleButton(JButton button, Color color) {
        button.setFont(new java.awt.Font("Arial", java.awt.Font.BOLD, 14));
        button.setBackground(color);
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setAlignmentX(Component.CENTER_ALIGNMENT);
        button.setMaximumSize(new Dimension(250, 35));
    }

    private void addListeners() {
        generateAllButton.addActionListener(e -> generateAllDonorsReport());
        generateByBloodGroupButton.addActionListener(e -> generateBloodGroupReport());
        generateByPhoneButton.addActionListener(e -> generateSingleDonorReport());
        backButton.addActionListener(e -> dispose());
    }

    private void generateAllDonorsReport() {
        try {
            String fileName = "All_Donors_Report_" +
                    new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date()) + ".pdf";

            Connection conn = DatabaseUtil.getConnection();
            String query = "SELECT * FROM donors ORDER BY name ASC";
            PreparedStatement stmt = conn.prepareStatement(query);
            ResultSet rs = stmt.executeQuery();

            Document document = new Document(PageSize.A4.rotate(), 20, 20, 40, 40);
            PdfWriter.getInstance(document, new FileOutputStream(fileName));
            document.open();

            // Title
            com.itextpdf.text.Font titleFont = new com.itextpdf.text.Font(
                    com.itextpdf.text.Font.FontFamily.HELVETICA, 18, com.itextpdf.text.Font.BOLD, BaseColor.WHITE);
            Paragraph title = new Paragraph("BLOOD BANK - ALL DONORS REPORT", titleFont);
            title.setAlignment(Element.ALIGN_CENTER);
            PdfPCell titleCell = new PdfPCell(new Phrase(title));
            titleCell.setBackgroundColor(new BaseColor(139, 0, 0));
            titleCell.setPadding(10);
            titleCell.setBorder(Rectangle.NO_BORDER);
            PdfPTable titleTable = new PdfPTable(1);
            titleTable.setWidthPercentage(100);
            titleTable.addCell(titleCell);
            document.add(titleTable);
            document.add(new Paragraph(" "));

            // Date
            com.itextpdf.text.Font dateFont = new com.itextpdf.text.Font(
                    com.itextpdf.text.Font.FontFamily.HELVETICA, 10, com.itextpdf.text.Font.ITALIC);
            Paragraph date = new Paragraph("Generated on: " +
                    new SimpleDateFormat("dd MMM yyyy, hh:mm a").format(new Date()), dateFont);
            date.setAlignment(Element.ALIGN_CENTER);
            document.add(date);
            document.add(new Paragraph(" "));

            // Table
            PdfPTable table = new PdfPTable(11); // Changed from 10 to 11 columns
            table.setWidthPercentage(100);
            table.setWidths(new float[]{0.7f, 0.5f, 1.5f, 1.5f, 0.6f, 0.8f, 0.8f, 1.2f, 1.2f, 1.2f, 2f});

            // Header
            String[] headers = {"Donor ID", "ID", "Name", "Father Name", "Age", "Gender", "Blood",
                    "Phone", "City", "Last Donation", "Address"};
            com.itextpdf.text.Font headerFont = new com.itextpdf.text.Font(
                    com.itextpdf.text.Font.FontFamily.HELVETICA, 9, com.itextpdf.text.Font.BOLD, BaseColor.WHITE);
            for (String header : headers) {
                PdfPCell cell = new PdfPCell(new Phrase(header, headerFont));
                cell.setBackgroundColor(new BaseColor(139, 0, 0));
                cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                cell.setPadding(5);
                table.addCell(cell);
            }

            // Data
            com.itextpdf.text.Font dataFont = new com.itextpdf.text.Font(
                    com.itextpdf.text.Font.FontFamily.HELVETICA, 8);
            int count = 0;
            while (rs.next()) {
                count++;
                addTableCell(table, rs.getString("donor_id"), dataFont, Element.ALIGN_CENTER);
                addTableCell(table, String.valueOf(rs.getInt("id")), dataFont, Element.ALIGN_CENTER);
                addTableCell(table, rs.getString("name"), dataFont, Element.ALIGN_LEFT);
                addTableCell(table, rs.getString("father_name"), dataFont, Element.ALIGN_LEFT);
                addTableCell(table, String.valueOf(rs.getInt("age")), dataFont, Element.ALIGN_CENTER);
                addTableCell(table, rs.getString("gender"), dataFont, Element.ALIGN_CENTER);
                addTableCell(table, rs.getString("blood_group"), dataFont, Element.ALIGN_CENTER);
                addTableCell(table, rs.getString("phone"), dataFont, Element.ALIGN_LEFT);
                addTableCell(table, rs.getString("donated_city"), dataFont, Element.ALIGN_LEFT);
                String lastDonation = rs.getString("last_donation_date");
                addTableCell(table, lastDonation != null ? lastDonation : "Never", dataFont, Element.ALIGN_CENTER);
                addTableCell(table, rs.getString("permanent_address"), dataFont, Element.ALIGN_LEFT);
            }


            if (count == 0) {
                document.close();
                conn.close();
                new File(fileName).delete();
                JOptionPane.showMessageDialog(this,
                        "No donors found in the database!",
                        "Empty Report",
                        JOptionPane.INFORMATION_MESSAGE);
                return;
            }

            document.add(table);
            document.add(new Paragraph(" "));

            com.itextpdf.text.Font footerFont = new com.itextpdf.text.Font(
                    com.itextpdf.text.Font.FontFamily.HELVETICA, 9, com.itextpdf.text.Font.ITALIC);
            Paragraph footer = new Paragraph("Total Donors: " + count +
                    " | Report generated by Blood Bank Management System", footerFont);
            footer.setAlignment(Element.ALIGN_CENTER);
            document.add(footer);

            document.close();
            conn.close();

            openPDF(fileName);
            JOptionPane.showMessageDialog(this,
                    "Report generated successfully!\nFile: " + fileName,
                    "Success",
                    JOptionPane.INFORMATION_MESSAGE);

        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this,
                    "Error generating report: " + ex.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void generateBloodGroupReport() {
        String bloodGroup = (String) bloodGroupCombo.getSelectedItem();

        try {
            String fileName = "Donors_" + bloodGroup.replace("+", "Pos").replace("-", "Neg") +
                    "_" + new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date()) + ".pdf";

            ResultSet rs = DatabaseUtil.searchDonorsByBloodGroup(bloodGroup);

            Document document = new Document(PageSize.A4.rotate(), 20, 20, 40, 40);
            PdfWriter.getInstance(document, new FileOutputStream(fileName));
            document.open();

            // Title
            com.itextpdf.text.Font titleFont = new com.itextpdf.text.Font(
                    com.itextpdf.text.Font.FontFamily.HELVETICA, 18, com.itextpdf.text.Font.BOLD, BaseColor.WHITE);
            Paragraph title = new Paragraph("BLOOD BANK - " + bloodGroup + " DONORS REPORT", titleFont);
            title.setAlignment(Element.ALIGN_CENTER);
            PdfPCell titleCell = new PdfPCell(new Phrase(title));
            titleCell.setBackgroundColor(new BaseColor(139, 0, 0));
            titleCell.setPadding(10);
            titleCell.setBorder(Rectangle.NO_BORDER);
            PdfPTable titleTable = new PdfPTable(1);
            titleTable.setWidthPercentage(100);
            titleTable.addCell(titleCell);
            document.add(titleTable);
            document.add(new Paragraph(" "));

            com.itextpdf.text.Font dateFont = new com.itextpdf.text.Font(
                    com.itextpdf.text.Font.FontFamily.HELVETICA, 10, com.itextpdf.text.Font.ITALIC);
            Paragraph date = new Paragraph("Generated on: " +
                    new SimpleDateFormat("dd MMM yyyy, hh:mm a").format(new Date()), dateFont);
            date.setAlignment(Element.ALIGN_CENTER);
            document.add(date);
            document.add(new Paragraph(" "));

            PdfPTable table = new PdfPTable(10);
            table.setWidthPercentage(100);
            table.setWidths(new float[]{0.5f, 1.5f, 1.5f, 0.6f, 0.8f, 0.8f, 1.2f, 1.2f, 1.2f, 2f});

            String[] headers = {"ID", "Name", "Father Name", "Age", "Gender", "Blood",
                    "Phone", "City", "Last Donation", "Address"};
            com.itextpdf.text.Font headerFont = new com.itextpdf.text.Font(
                    com.itextpdf.text.Font.FontFamily.HELVETICA, 9, com.itextpdf.text.Font.BOLD, BaseColor.WHITE);
            for (String header : headers) {
                PdfPCell cell = new PdfPCell(new Phrase(header, headerFont));
                cell.setBackgroundColor(new BaseColor(139, 0, 0));
                cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                cell.setPadding(5);
                table.addCell(cell);
            }

            com.itextpdf.text.Font dataFont = new com.itextpdf.text.Font(
                    com.itextpdf.text.Font.FontFamily.HELVETICA, 8);
            int count = 0;
            if (rs != null) {
                while (rs.next()) {
                    count++;
                    addTableCell(table, String.valueOf(rs.getInt("id")), dataFont, Element.ALIGN_CENTER);
                    addTableCell(table, rs.getString("name"), dataFont, Element.ALIGN_LEFT);
                    addTableCell(table, rs.getString("father_name"), dataFont, Element.ALIGN_LEFT);
                    addTableCell(table, String.valueOf(rs.getInt("age")), dataFont, Element.ALIGN_CENTER);
                    addTableCell(table, rs.getString("gender"), dataFont, Element.ALIGN_CENTER);
                    addTableCell(table, rs.getString("blood_group"), dataFont, Element.ALIGN_CENTER);
                    addTableCell(table, rs.getString("phone"), dataFont, Element.ALIGN_LEFT);
                    addTableCell(table, rs.getString("donated_city"), dataFont, Element.ALIGN_LEFT);
                    String lastDonation = rs.getString("last_donation_date");
                    addTableCell(table, lastDonation != null ? lastDonation : "Never", dataFont, Element.ALIGN_CENTER);
                    addTableCell(table, rs.getString("permanent_address"), dataFont, Element.ALIGN_LEFT);
                }
                rs.close();
            }

            if (count == 0) {
                document.close();
                new File(fileName).delete();
                JOptionPane.showMessageDialog(this,
                        "No donors found with blood group " + bloodGroup + "!",
                        "Empty Report",
                        JOptionPane.INFORMATION_MESSAGE);
                return;
            }

            document.add(table);
            document.add(new Paragraph(" "));

            com.itextpdf.text.Font footerFont = new com.itextpdf.text.Font(
                    com.itextpdf.text.Font.FontFamily.HELVETICA, 9, com.itextpdf.text.Font.ITALIC);
            Paragraph footer = new Paragraph("Total " + bloodGroup + " Donors: " + count +
                    " | Report generated by Blood Bank Management System", footerFont);
            footer.setAlignment(Element.ALIGN_CENTER);
            document.add(footer);

            document.close();

            openPDF(fileName);
            JOptionPane.showMessageDialog(this,
                    "Report generated successfully!\nFile: " + fileName,
                    "Success",
                    JOptionPane.INFORMATION_MESSAGE);

        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this,
                    "Error generating report: " + ex.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void generateSingleDonorReport() {
        String phone = phoneField.getText().trim();

        if (phone.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Please enter a phone number.",
                    "Input Required",
                    JOptionPane.WARNING_MESSAGE);
            phoneField.requestFocus();
            return;
        }

        try {
            Donor donor = DatabaseUtil.getDonorByPhone(phone);

            if (donor == null) {
                JOptionPane.showMessageDialog(this,
                        "No donor found with phone number: " + phone,
                        "Not Found",
                        JOptionPane.ERROR_MESSAGE);
                return;
            }

            String fileName = "Donor_" + donor.getName().replace(" ", "_") +
                    "_" + new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date()) + ".pdf";

            Document document = new Document(PageSize.A4, 40, 40, 50, 50);
            PdfWriter.getInstance(document, new FileOutputStream(fileName));
            document.open();

            // Title
            com.itextpdf.text.Font titleFont = new com.itextpdf.text.Font(
                    com.itextpdf.text.Font.FontFamily.HELVETICA, 20, com.itextpdf.text.Font.BOLD, BaseColor.WHITE);
            Paragraph title = new Paragraph("DONOR INFORMATION REPORT", titleFont);
            title.setAlignment(Element.ALIGN_CENTER);
            PdfPCell titleCell = new PdfPCell(new Phrase(title));
            titleCell.setBackgroundColor(new BaseColor(139, 0, 0));
            titleCell.setPadding(15);
            titleCell.setBorder(Rectangle.NO_BORDER);
            PdfPTable titleTable = new PdfPTable(1);
            titleTable.setWidthPercentage(100);
            titleTable.addCell(titleCell);
            document.add(titleTable);
            document.add(new Paragraph(" "));
            document.add(new Paragraph(" "));

            com.itextpdf.text.Font labelFont = new com.itextpdf.text.Font(
                    com.itextpdf.text.Font.FontFamily.HELVETICA, 12, com.itextpdf.text.Font.BOLD);
            com.itextpdf.text.Font valueFont = new com.itextpdf.text.Font(
                    com.itextpdf.text.Font.FontFamily.HELVETICA, 12);

            PdfPTable infoTable = new PdfPTable(2);
            infoTable.setWidthPercentage(100);
            infoTable.setWidths(new float[]{1f, 2f});

            addInfoRow(infoTable, "Donor ID:", donor.getDonorId(), labelFont, valueFont);
            addInfoRow(infoTable, "Full Name:", donor.getName(), labelFont, valueFont);
            addInfoRow(infoTable, "Father Name:", donor.getFatherName(), labelFont, valueFont);
            addInfoRow(infoTable, "Age:", String.valueOf(donor.getAge()), labelFont, valueFont);
            addInfoRow(infoTable, "Gender:", donor.getGender(), labelFont, valueFont);
            addInfoRow(infoTable, "Blood Group:", donor.getBloodGroup(), labelFont, valueFont);
            addInfoRow(infoTable, "Phone Number:", donor.getPhone(), labelFont, valueFont);
            addInfoRow(infoTable, "Donated City:", donor.getDonatedCity(), labelFont, valueFont);
            addInfoRow(infoTable, "Last Donation Date:",
                    donor.getLastDonationDate().isEmpty() ? "Never" : donor.getLastDonationDate(),
                    labelFont, valueFont);
            addInfoRow(infoTable, "Permanent Address:", donor.getPermanentAddress(), labelFont, valueFont);
            addInfoRow(infoTable, "Temporary Address:", donor.getTemporaryAddress(), labelFont, valueFont);

            document.add(infoTable);
            document.add(new Paragraph(" "));
            document.add(new Paragraph(" "));

            com.itextpdf.text.Font footerFont = new com.itextpdf.text.Font(
                    com.itextpdf.text.Font.FontFamily.HELVETICA, 9, com.itextpdf.text.Font.ITALIC);
            Paragraph footer = new Paragraph("Generated on: " +
                    new SimpleDateFormat("dd MMM yyyy, hh:mm a").format(new Date()) +
                    " | Blood Bank Management System", footerFont);
            footer.setAlignment(Element.ALIGN_CENTER);
            document.add(footer);

            document.close();

            openPDF(fileName);
            JOptionPane.showMessageDialog(this,
                    "Report generated successfully!\nFile: " + fileName,
                    "Success",
                    JOptionPane.INFORMATION_MESSAGE);
            phoneField.setText("");

        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this,
                    "Error generating report: " + ex.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void addTableCell(PdfPTable table, String text, com.itextpdf.text.Font font, int alignment) {
        PdfPCell cell = new PdfPCell(new Phrase(text, font));
        cell.setHorizontalAlignment(alignment);
        cell.setPadding(4);
        table.addCell(cell);
    }

    private void addInfoRow(PdfPTable table, String label, String value,
                            com.itextpdf.text.Font labelFont, com.itextpdf.text.Font valueFont) {
        PdfPCell labelCell = new PdfPCell(new Phrase(label, labelFont));
        labelCell.setBackgroundColor(new BaseColor(240, 240, 240));
        labelCell.setPadding(8);
        labelCell.setBorder(Rectangle.BOX);
        table.addCell(labelCell);

        PdfPCell valueCell = new PdfPCell(new Phrase(value, valueFont));
        valueCell.setPadding(8);
        valueCell.setBorder(Rectangle.BOX);
        table.addCell(valueCell);
    }

    private void openPDF(String fileName) {
        try {
            File pdfFile = new File(fileName);
            if (pdfFile.exists()) {
                if (Desktop.isDesktopSupported()) {
                    Desktop.getDesktop().open(pdfFile);
                }
            }
        } catch (Exception e) {
            System.err.println("Could not open PDF: " + e.getMessage());
        }
    }
}