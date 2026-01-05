package projectiv;

import java.io.*;
import java.util.Properties;

/**
 * System-wide settings for the Blood Bank Management System
 * Settings are persisted to a properties file
 */
public class SystemSettings {
    private static final String SETTINGS_FILE = "bloodbank_settings.properties";

    // Default values
    private static int minDonationInterval = 90; // days
    private static int minDonorAge = 18;
    private static int maxDonorAge = 65;
    private static int freshBloodThreshold = 2; // days - blood is fresh if <= 2 days old

    static {
        loadSettings();
    }

    // Getters
    public static int getMinDonationInterval() {
        return minDonationInterval;
    }

    public static int getMinDonorAge() {
        return minDonorAge;
    }

    public static int getMaxDonorAge() {
        return maxDonorAge;
    }

    public static int getFreshBloodThreshold() {
        return freshBloodThreshold;
    }

    // Setters
    public static void setMinDonationInterval(int days) {
        minDonationInterval = days;
    }

    public static void setMinDonorAge(int age) {
        minDonorAge = age;
    }

    public static void setMaxDonorAge(int age) {
        maxDonorAge = age;
    }

    public static void setFreshBloodThreshold(int days) {
        freshBloodThreshold = days;
    }

    /**
     * Get stock status based on blood age (in days since last update)
     * @param daysSinceUpdate number of days since blood was last updated
     * @return "Fresh Blood" if <= freshBloodThreshold days, "Old Blood" otherwise
     */
    public static String getBloodAgeStatus(int daysSinceUpdate) {
        if (daysSinceUpdate <= freshBloodThreshold) {
            return "Fresh Blood";
        } else {
            return "Old Blood";
        }
    }

    /**
     * Load settings from properties file
     */
    public static void loadSettings() {
        Properties props = new Properties();
        File settingsFile = new File(SETTINGS_FILE);

        if (settingsFile.exists()) {
            try (FileInputStream fis = new FileInputStream(settingsFile)) {
                props.load(fis);

                minDonationInterval = Integer.parseInt(props.getProperty("minDonationInterval", "90"));
                minDonorAge = Integer.parseInt(props.getProperty("minDonorAge", "18"));
                maxDonorAge = Integer.parseInt(props.getProperty("maxDonorAge", "65"));
                freshBloodThreshold = Integer.parseInt(props.getProperty("freshBloodThreshold", "2"));

                System.out.println("Settings loaded successfully from " + SETTINGS_FILE);
            } catch (IOException | NumberFormatException e) {
                System.err.println("Error loading settings, using defaults: " + e.getMessage());
                resetToDefaults();
            }
        } else {
            System.out.println("Settings file not found, using default values");
        }
    }

    /**
     * Save settings to properties file
     */
    public static boolean saveSettings() {
        Properties props = new Properties();

        props.setProperty("minDonationInterval", String.valueOf(minDonationInterval));
        props.setProperty("minDonorAge", String.valueOf(minDonorAge));
        props.setProperty("maxDonorAge", String.valueOf(maxDonorAge));
        props.setProperty("freshBloodThreshold", String.valueOf(freshBloodThreshold));

        try (FileOutputStream fos = new FileOutputStream(SETTINGS_FILE)) {
            props.store(fos, "Blood Bank Management System Settings");
            System.out.println("Settings saved successfully to " + SETTINGS_FILE);
            return true;
        } catch (IOException e) {
            System.err.println("Error saving settings: " + e.getMessage());
            return false;
        }
    }

    /**
     * Reset all settings to default values
     */
    public static void resetToDefaults() {
        minDonationInterval = 90;
        minDonorAge = 18;
        maxDonorAge = 65;
        freshBloodThreshold = 2;
    }

    /**
     * Print current settings (for debugging)
     */
    public static void printSettings() {
        System.out.println("=== Blood Bank System Settings ===");
        System.out.println("Min Donation Interval: " + minDonationInterval + " days");
        System.out.println("Min Donor Age: " + minDonorAge);
        System.out.println("Max Donor Age: " + maxDonorAge);
        System.out.println("Fresh Blood Threshold: " + freshBloodThreshold + " days");
        System.out.println("================================");
    }
}