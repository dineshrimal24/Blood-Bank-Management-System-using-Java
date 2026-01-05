package projectiv;

import projectiv.BloodInventoryUtil;
import projectiv.DatabaseUtil;

import javax.swing.SwingUtilities;
import javax.swing.JOptionPane;

public class Main {
    public static void main(String[] args) {

        SwingUtilities.invokeLater(() -> {
            try {
                DatabaseUtil.initializeDatabase();
                BloodInventoryUtil.initializeBloodInventory();

                System.out.println("Database initialized successfully!");

                login loginFrame = new login();
                loginFrame.setVisible(true);

                System.out.println("Blood Bank Management System started successfully!");

            } catch (Exception e) {
                e.printStackTrace();

                JOptionPane.showMessageDialog(null,
                        "Failed to initialize database:\n" + e.getMessage() +
                                "\n\nMake sure MySQL is running and database 'bloodbank' exists!",
                        "Database Error",
                        JOptionPane.ERROR_MESSAGE);

                System.exit(1);
            }
        });
    }
}
