package projectiv;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.time.temporal.ChronoUnit;

/**
 * Utility class for calculating blood age and status
 */
public class BloodAgeUtil {

    /**
     * Calculate the number of days since the blood was last updated
     * @param lastUpdatedStr Last updated date string in format YYYY-MM-DD or YYYY-MM-DD HH:MM:SS
     * @return Number of days since last update, or 0 if invalid date
     */
    public static int calculateDaysSinceUpdate(String lastUpdatedStr) {
        if (lastUpdatedStr == null || lastUpdatedStr.trim().isEmpty()) {
            return 0;
        }

        try {
            // Handle both date and datetime formats
            String dateOnly = lastUpdatedStr.split(" ")[0];
            LocalDate lastUpdated = LocalDate.parse(dateOnly);
            LocalDate today = LocalDate.now();
            return (int) ChronoUnit.DAYS.between(lastUpdated, today);
        } catch (DateTimeParseException e) {
            System.err.println("Error parsing date: " + lastUpdatedStr);
            return 0;
        }
    }

    /**
     * Get blood status based on age
     * @param daysSinceUpdate Number of days since blood was last updated
     * @return "Fresh Blood" or "Old Blood"
     */
    public static String getBloodStatus(int daysSinceUpdate) {
        return SystemSettings.getBloodAgeStatus(daysSinceUpdate);
    }

    /**
     * Get blood status directly from last updated date string
     * @param lastUpdatedStr Last updated date string
     * @return "Fresh Blood" or "Old Blood"
     */
    public static String getBloodStatus(String lastUpdatedStr) {
        int days = calculateDaysSinceUpdate(lastUpdatedStr);
        return getBloodStatus(days);
    }
}