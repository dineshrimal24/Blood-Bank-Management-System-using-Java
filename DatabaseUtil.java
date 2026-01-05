package projectiv;

import java.sql.*;

public class DatabaseUtil {
    private static final String DB_URL = "jdbc:mysql://localhost:3306/bloodbank";
    private static final String DB_USER = "root";
    private static final String DB_PASS = "";

    static {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            System.err.println("MySQL JDBC Driver not found: " + e.getMessage());
        }
    }

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(DB_URL, DB_USER, DB_PASS);
    }

    public static void initializeDatabase() throws SQLException {
        createDonorsTable();
    }

    private static void createDonorsTable() throws SQLException {
        String createTableSQL = """
           CREATE TABLE IF NOT EXISTS donors (
            id INT AUTO_INCREMENT PRIMARY KEY,
            name VARCHAR(100) NOT NULL,
            father_name VARCHAR(50) NOT NULL,
            date_of_birth DATE,
            age INT NOT NULL,
            gender VARCHAR(10) NOT NULL,
            blood_group VARCHAR(5) NOT NULL,
            phone VARCHAR(15) UNIQUE NOT NULL,
            donated_city VARCHAR(50) NOT NULL,
            last_donation_date DATE,
            permanent_address TEXT,
            address TEXT,
            temporary_address TEXT,
            donation_count INT DEFAULT 0,
            created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
            updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
           )
        """;
        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement()) {
            stmt.execute(createTableSQL);
            ensureDOBColumn(conn);
            ensureTemporaryAddressColumn(conn);
        }
    }

    private static void ensureDOBColumn(Connection conn) throws SQLException {
        String checkColumnSql = """
            SELECT COUNT(*) FROM information_schema.COLUMNS
            WHERE TABLE_SCHEMA = DATABASE()
              AND TABLE_NAME = 'donors'
              AND COLUMN_NAME = 'date_of_birth'
        """;
        try (PreparedStatement checkStmt = conn.prepareStatement(checkColumnSql);
             ResultSet rs = checkStmt.executeQuery()) {
            if (rs.next() && rs.getInt(1) == 0) {
                try (Statement alter = conn.createStatement()) {
                    alter.execute("ALTER TABLE donors ADD COLUMN date_of_birth DATE AFTER father_name");
                }
            }
        }
    }

    private static void ensureTemporaryAddressColumn(Connection conn) throws SQLException {
        String checkColumnSql = """
            SELECT COUNT(*) FROM information_schema.COLUMNS
            WHERE TABLE_SCHEMA = DATABASE()
              AND TABLE_NAME = 'donors'
              AND COLUMN_NAME = 'temporary_address'
        """;
        try (PreparedStatement checkStmt = conn.prepareStatement(checkColumnSql);
             ResultSet rs = checkStmt.executeQuery()) {
            if (rs.next() && rs.getInt(1) == 0) {
                try (Statement alter = conn.createStatement()) {
                    alter.execute("ALTER TABLE donors ADD COLUMN temporary_address TEXT AFTER permanent_address");
                }
            }
        }
    }

    public static int getNextDonorId() throws SQLException {
        String query = "SELECT AUTO_INCREMENT FROM information_schema.TABLES " +
                "WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = 'donors'";
        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
            if (rs.next()) {
                return rs.getInt(1);
            }
        }
        return 1;
    }

    // NEW: Insert donor with DOB
    public static int insertDonorWithDOB(String name, String fatherName, String dateOfBirth, int age,
                                         String gender, String bloodGroup, String phone, String donatedCity,
                                         String lastDonationDate, String permanentAddress,
                                         String temporaryAddress) throws SQLException {
        String insertSQL = """
            INSERT INTO donors (name, father_name, date_of_birth, age, gender, blood_group, phone, donated_city, 
                               last_donation_date, permanent_address, temporary_address)
            VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
            """;
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(insertSQL, Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setString(1, name);
            pstmt.setString(2, fatherName);

            // Handle DOB
            if (dateOfBirth == null || dateOfBirth.isEmpty()) {
                pstmt.setNull(3, Types.DATE);
            } else {
                pstmt.setDate(3, Date.valueOf(dateOfBirth));
            }

            pstmt.setInt(4, age);
            pstmt.setString(5, gender);
            pstmt.setString(6, bloodGroup);
            pstmt.setString(7, phone);
            pstmt.setString(8, donatedCity);

            if (lastDonationDate == null || lastDonationDate.isEmpty()) {
                pstmt.setNull(9, Types.DATE);
            } else {
                pstmt.setDate(9, Date.valueOf(lastDonationDate));
            }

            pstmt.setString(10, permanentAddress);
            pstmt.setString(11, temporaryAddress);

            int affectedRows = pstmt.executeUpdate();
            if (affectedRows > 0) {
                try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        return generatedKeys.getInt(1);
                    }
                }
            }
        } catch (SQLException e) {
            System.err.println("Error inserting donor: " + e.getMessage());
            throw e;
        }
        return 0;
    }

    // OLD: Keep for backward compatibility (without DOB)
    public static int insertDonorWithId(String name, String fatherName, int age, String gender,
                                        String bloodGroup, String phone, String donatedCity,
                                        String lastDonationDate, String permanentAddress,
                                        String temporaryAddress) throws SQLException {
        return insertDonorWithDOB(name, fatherName, null, age, gender, bloodGroup, phone,
                donatedCity, lastDonationDate, permanentAddress, temporaryAddress);
    }

    public static boolean insertDonor(String name, String fatherName, int age, String gender,
                                      String bloodGroup, String phone, String donatedCity,
                                      String lastDonationDate, String permanentAddress,
                                      String temporaryAddress) {
        try {
            return insertDonorWithId(name, fatherName, age, gender, bloodGroup, phone,
                    donatedCity, lastDonationDate, permanentAddress,
                    temporaryAddress) > 0;
        } catch (SQLException e) {
            return false;
        }
    }

    public static boolean donorExists(String phone) {
        String checkSQL = "SELECT COUNT(*) FROM donors WHERE phone = ?";
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(checkSQL)) {
            pstmt.setString(1, phone);
            try (ResultSet rs = pstmt.executeQuery()) {
                return rs.next() && rs.getInt(1) > 0;
            }
        } catch (SQLException e) {
            System.err.println("Error checking donor existence: " + e.getMessage());
            return false;
        }
    }

    public static boolean donorExistsById(int id) throws SQLException {
        String checkSQL = "SELECT COUNT(*) FROM donors WHERE id = ?";
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(checkSQL)) {
            pstmt.setInt(1, id);
            try (ResultSet rs = pstmt.executeQuery()) {
                return rs.next() && rs.getInt(1) > 0;
            }
        }
    }

    // NEW: Update donor with DOB
    public static boolean updateDonorWithDOB(int id, String name, String fatherName, String dateOfBirth,
                                             int age, String gender, String bloodGroup, String phone,
                                             String donatedCity, String lastDonationDate,
                                             String permanentAddress, String temporaryAddress) {
        String updateSQL = """
            UPDATE donors
            SET name = ?, father_name = ?, date_of_birth = ?, age = ?, gender = ?, blood_group = ?, phone = ?, 
                donated_city = ?, last_donation_date = ?, permanent_address = ?, temporary_address = ?
            WHERE id = ?
            """;
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(updateSQL)) {
            pstmt.setString(1, name);
            pstmt.setString(2, fatherName);

            if (dateOfBirth == null || dateOfBirth.isEmpty()) {
                pstmt.setNull(3, Types.DATE);
            } else {
                pstmt.setDate(3, Date.valueOf(dateOfBirth));
            }

            pstmt.setInt(4, age);
            pstmt.setString(5, gender);
            pstmt.setString(6, bloodGroup);
            pstmt.setString(7, phone);
            pstmt.setString(8, donatedCity);

            if (lastDonationDate == null || lastDonationDate.isEmpty()) {
                pstmt.setNull(9, Types.DATE);
            } else {
                pstmt.setDate(9, Date.valueOf(lastDonationDate));
            }

            pstmt.setString(10, permanentAddress);
            pstmt.setString(11, temporaryAddress);
            pstmt.setInt(12, id);

            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error updating donor: " + e.getMessage());
            return false;
        }
    }

    // OLD: Keep for backward compatibility
    public static boolean updateDonor(int id, String name, String fatherName, int age, String gender,
                                      String bloodGroup, String phone, String donatedCity,
                                      String lastDonationDate, String permanentAddress,
                                      String temporaryAddress) {
        return updateDonorWithDOB(id, name, fatherName, null, age, gender, bloodGroup, phone,
                donatedCity, lastDonationDate, permanentAddress, temporaryAddress);
    }

    public static ResultSet getAllDonors() {
        String selectSQL = "SELECT * FROM donors ORDER BY created_at DESC";
        try {
            Connection conn = getConnection();
            Statement stmt = conn.createStatement();
            return stmt.executeQuery(selectSQL);
        } catch (SQLException e) {
            System.err.println("Error getting all donors: " + e.getMessage());
            return null;
        }
    }

    public static ResultSet searchDonor(String name) {
        String searchSQL = "SELECT * FROM donors WHERE name LIKE ? ORDER BY created_at DESC";
        try {
            Connection conn = getConnection();
            PreparedStatement pstmt = conn.prepareStatement(searchSQL);
            pstmt.setString(1, "%" + name + "%");
            return pstmt.executeQuery();
        } catch (SQLException e) {
            System.err.println("Error searching donor: " + e.getMessage());
            return null;
        }
    }

    public static ResultSet searchDonorsByBloodGroup(String bloodGroup) {
        String searchSQL = "SELECT * FROM donors WHERE blood_group = ? ORDER BY created_at DESC";
        try {
            Connection conn = getConnection();
            PreparedStatement pstmt = conn.prepareStatement(searchSQL);
            pstmt.setString(1, bloodGroup);
            return pstmt.executeQuery();
        } catch (SQLException e) {
            System.err.println("Error searching donors by blood group: " + e.getMessage());
            return null;
        }
    }

    public static boolean deleteDonor(int id) {
        String deleteSQL = "DELETE FROM donors WHERE id = ?";
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(deleteSQL)) {
            pstmt.setInt(1, id);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error deleting donor: " + e.getMessage());
            return false;
        }
    }

    public static ResultSet getDonorById(int id) {
        String selectSQL = "SELECT * FROM donors WHERE id = ?";
        try {
            Connection conn = getConnection();
            PreparedStatement pstmt = conn.prepareStatement(selectSQL);
            pstmt.setInt(1, id);
            return pstmt.executeQuery();
        } catch (SQLException e) {
            System.err.println("Error getting donor by ID: " + e.getMessage());
            return null;
        }
    }

    public static Donor getDonorByIdObject(int id) {
        String sql = "SELECT * FROM donors WHERE id = ?";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapRowToDonor(rs);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error getting donor by ID as object: " + e.getMessage());
        }
        return null;
    }

    public static Donor getDonorByPhone(String phone) throws SQLException {
        String sql = "SELECT * FROM donors WHERE phone = ?";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, phone);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapRowToDonor(rs);
                }
            }
        }
        return null;
    }

    public static Donor searchDonorByIdOrPhone(String searchValue) throws SQLException {
        try {
            int id = Integer.parseInt(searchValue);
            return getDonorByIdObject(id);
        } catch (NumberFormatException e) {
            return getDonorByPhone(searchValue);
        }
    }

    private static Donor mapRowToDonor(ResultSet rs) throws SQLException {
        int id = rs.getInt("id");
        String name = rs.getString("name");
        String fatherName = rs.getString("father_name");

        // Get DOB if exists
        String dateOfBirth = "";
        if (hasColumn(rs, "date_of_birth")) {
            Date dobDate = rs.getDate("date_of_birth");
            if (dobDate != null) {
                dateOfBirth = dobDate.toString();
            }
        }

        int age = rs.getInt("age");
        String gender = rs.getString("gender");
        String bloodGroup = rs.getString("blood_group");
        String phone = rs.getString("phone");
        String donatedCity = rs.getString("donated_city");
        Date lastDateSql = rs.getDate("last_donation_date");
        String lastDonationDate = lastDateSql == null ? "" : lastDateSql.toString();
        String permanentAddress = hasColumn(rs, "permanent_address") ? rs.getString("permanent_address") : "";
        String temporaryAddress = hasColumn(rs, "temporary_address") ? rs.getString("temporary_address") : "";

        return new Donor(id, name, fatherName, dateOfBirth, age, gender, bloodGroup, phone, donatedCity,
                lastDonationDate, permanentAddress, temporaryAddress);
    }

    private static boolean hasColumn(ResultSet rs, String columnLabel) throws SQLException {
        ResultSetMetaData metaData = rs.getMetaData();
        for (int i = 1; i <= metaData.getColumnCount(); i++) {
            if (columnLabel.equalsIgnoreCase(metaData.getColumnLabel(i))) {
                return true;
            }
        }
        return false;
    }
}