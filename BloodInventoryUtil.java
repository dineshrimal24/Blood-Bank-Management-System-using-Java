package projectiv;

import java.sql.*;
import java.time.LocalDate;

public class BloodInventoryUtil {

    // Initialize blood inventory tables
    public static void initializeBloodInventory() throws SQLException {
        createBloodInventoryTable();
        createDonationHistoryTable();
        createBloodIssuesTable();
        initializeBloodGroups();
    }

    private static void createBloodInventoryTable() throws SQLException {
        String createTableSQL = """
            CREATE TABLE IF NOT EXISTS blood_inventory (
                id INT AUTO_INCREMENT PRIMARY KEY,
                blood_group VARCHAR(5) NOT NULL UNIQUE,
                units_available INT DEFAULT 0,
                last_updated TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
            )
        """;
        try (Connection conn = DatabaseUtil.getConnection();
             Statement stmt = conn.createStatement()) {
            stmt.execute(createTableSQL);
        }
    }

    private static void createDonationHistoryTable() throws SQLException {
        String createTableSQL = """
            CREATE TABLE IF NOT EXISTS donation_history (
                id INT AUTO_INCREMENT PRIMARY KEY,
                donor_id INT NOT NULL,
                blood_group VARCHAR(5) NOT NULL,
                units_donated INT DEFAULT 1,
                donation_date DATE NOT NULL,
                created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                FOREIGN KEY (donor_id) REFERENCES donors(id) ON DELETE CASCADE
            )
        """;
        try (Connection conn = DatabaseUtil.getConnection();
             Statement stmt = conn.createStatement()) {
            stmt.execute(createTableSQL);
        }
    }

    private static void createBloodIssuesTable() throws SQLException {
        String createTableSQL = """
            CREATE TABLE IF NOT EXISTS blood_issues (
                id INT AUTO_INCREMENT PRIMARY KEY,
                blood_group VARCHAR(5) NOT NULL,
                units_issued INT NOT NULL,
                patient_name VARCHAR(100),
                hospital_name VARCHAR(100),
                issue_date DATE NOT NULL,
                remarks TEXT,
                created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
            )
        """;
        try (Connection conn = DatabaseUtil.getConnection();
             Statement stmt = conn.createStatement()) {
            stmt.execute(createTableSQL);
        }
    }

    private static void initializeBloodGroups() throws SQLException {
        String[] bloodGroups = {"A+", "A-", "B+", "B-", "AB+", "AB-", "O+", "O-"};
        String insertSQL = "INSERT IGNORE INTO blood_inventory (blood_group, units_available) VALUES (?, 0)";

        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(insertSQL)) {
            for (String bg : bloodGroups) {
                pstmt.setString(1, bg);
                pstmt.executeUpdate();
            }
        }
    }

    // Get current stock for a blood group
    public static int getBloodStock(String bloodGroup) throws SQLException {
        String query = "SELECT units_available FROM blood_inventory WHERE blood_group = ?";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setString(1, bloodGroup);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("units_available");
                }
            }
        }
        return 0;
    }

    // Add blood units when donor donates (STOCK INCREASE)
    public static boolean addBloodDonation(int donorId, String bloodGroup, int units, LocalDate donationDate) throws SQLException {
        Connection conn = null;
        try {
            conn = DatabaseUtil.getConnection();
            conn.setAutoCommit(false);

            // 1. Add to donation history
            String historySQL = "INSERT INTO donation_history (donor_id, blood_group, units_donated, donation_date) VALUES (?, ?, ?, ?)";
            try (PreparedStatement ps = conn.prepareStatement(historySQL)) {
                ps.setInt(1, donorId);
                ps.setString(2, bloodGroup);
                ps.setInt(3, units);
                ps.setDate(4, Date.valueOf(donationDate));
                ps.executeUpdate();
            }

            // 2. Update blood inventory (increase stock)
            String inventorySQL = "UPDATE blood_inventory SET units_available = units_available + ? WHERE blood_group = ?";
            try (PreparedStatement ps = conn.prepareStatement(inventorySQL)) {
                ps.setInt(1, units);
                ps.setString(2, bloodGroup);
                ps.executeUpdate();
            }

            // 3. Update donor's last donation date and donation count
            String donorSQL = "UPDATE donors SET last_donation_date = ?, donation_count = donation_count + 1 WHERE id = ?";
            try (PreparedStatement ps = conn.prepareStatement(donorSQL)) {
                ps.setDate(1, Date.valueOf(donationDate));
                ps.setInt(2, donorId);
                ps.executeUpdate();
            }

            conn.commit();
            return true;

        } catch (SQLException e) {
            if (conn != null) {
                try {
                    conn.rollback();
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
            throw e;
        } finally {
            if (conn != null) {
                conn.setAutoCommit(true);
                conn.close();
            }
        }
    }

    // Issue blood to patient (STOCK DECREASE)
    public static boolean issueBlood(String bloodGroup, int units, String patientName,
                                     String hospitalName, LocalDate issueDate, String remarks) throws SQLException {
        // First check if enough units available
        int available = getBloodStock(bloodGroup);
        if (available < units) {
            return false;
        }

        Connection conn = null;
        try {
            conn = DatabaseUtil.getConnection();
            conn.setAutoCommit(false);

            // 1. Record blood issue
            String issueSQL = "INSERT INTO blood_issues (blood_group, units_issued, patient_name, hospital_name, issue_date, remarks) VALUES (?, ?, ?, ?, ?, ?)";
            try (PreparedStatement ps = conn.prepareStatement(issueSQL)) {
                ps.setString(1, bloodGroup);
                ps.setInt(2, units);
                ps.setString(3, patientName);
                ps.setString(4, hospitalName);
                ps.setDate(5, Date.valueOf(issueDate));
                ps.setString(6, remarks);
                ps.executeUpdate();
            }

            // 2. Update blood inventory (decrease stock)
            String inventorySQL = "UPDATE blood_inventory SET units_available = units_available - ? WHERE blood_group = ?";
            try (PreparedStatement ps = conn.prepareStatement(inventorySQL)) {
                ps.setInt(1, units);
                ps.setString(2, bloodGroup);
                ps.executeUpdate();
            }

            conn.commit();
            return true;

        } catch (SQLException e) {
            if (conn != null) {
                try {
                    conn.rollback();
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
            throw e;
        } finally {
            if (conn != null) {
                conn.setAutoCommit(true);
                conn.close();
            }
        }
    }

    // Get all blood inventory
    public static ResultSet getAllBloodInventory() throws SQLException {
        String query = "SELECT * FROM blood_inventory ORDER BY blood_group";
        Connection conn = DatabaseUtil.getConnection();
        Statement stmt = conn.createStatement();
        return stmt.executeQuery(query);
    }

    // Get donation history for a donor
    public static ResultSet getDonorHistory(int donorId) throws SQLException {
        String query = "SELECT * FROM donation_history WHERE donor_id = ? ORDER BY donation_date DESC";
        Connection conn = DatabaseUtil.getConnection();
        PreparedStatement ps = conn.prepareStatement(query);
        ps.setInt(1, donorId);
        return ps.executeQuery();
    }
}