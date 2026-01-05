package projectiv;

import java.util.regex.Pattern;

public class EmailValidator {
    // Email validation pattern
    private static final Pattern EMAIL_PATTERN = Pattern.compile(
            "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$"
    );
    /**^ - Start of string
     [A-Za-z0-9+_.-]+ - Username part (letters, numbers, +, _, ., -)
     @ - Required @ symbol
     [A-Za-z0-9.-]+ - Domain name
     \\. - Required dot before extension
     [A-Za-z]{2,} - Top-level domain (minimum 2 characters)
     $ - End of string/**

    /**
     * Validates if the provided email address has correct format
     * @param email The email address to validate
     * @return true if email format is valid, false otherwise
     */
    public static boolean isValidEmail(String email) {
        if (email == null || email.trim().isEmpty()) {
            return false;
        }
        return EMAIL_PATTERN.matcher(email.trim()).matches();
    }

    /**
     * Gets a user-friendly error message for invalid email
     * @param email The email that was invalid
     * @return Error message string
     */
    public static String getEmailErrorMessage(String email) {
        if (email == null || email.trim().isEmpty()) {
            return "Email address is required.";
        }
        if (!email.contains("@")) {
            return "Email must contain '@' symbol.";
        }
        if (!email.substring(email.indexOf("@")).contains(".")) {
            return "Email must contain a domain (e.g., @example.com).";
        }
        return "Invalid email format. Please use format: name@example.com";
    }
}