package projectiv;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.net.URL;

/**
 * Utility class for loading icons with fallback mechanism
 * Fixes emoji rendering issues across different platforms
 */
public class IconUtil {

    /**
     * Creates an icon label with proper fallback mechanism
     * @param iconFileName The icon file name (e.g., "blood-donor.png")
     * @param fallbackText Fallback text/emoji if icon not found
     * @param size Size of the icon (width and height)
     * @return JLabel with icon or fallback
     */
    public static JLabel createIconLabel(String iconFileName, String fallbackText, int size) {
        // Try to load from resources first
        String resourcePath = "/icons/" + iconFileName;
        URL resource = IconUtil.class.getResource(resourcePath);

        if (resource != null) {
            ImageIcon icon = new ImageIcon(resource);
            Image scaled = icon.getImage().getScaledInstance(size, size, Image.SCALE_SMOOTH);
            return new JLabel(new ImageIcon(scaled));
        }

        // Try to load from file system
        File fallback = new File("src/icons/" + iconFileName);
        if (fallback.exists()) {
            ImageIcon icon = new ImageIcon(fallback.getAbsolutePath());
            Image scaled = icon.getImage().getScaledInstance(size, size, Image.SCALE_SMOOTH);
            return new JLabel(new ImageIcon(scaled));
        }

        // Use Unicode symbol as last resort (better than garbled emoji)
        JLabel label = new JLabel(getUnicodeSymbol(iconFileName, fallbackText));
        label.setFont(new Font("Dialog", Font.BOLD, size));
        label.setForeground(Color.WHITE);
        return label;
    }

    /**
     * Get Unicode symbol based on icon type
     */
    private static String getUnicodeSymbol(String iconFileName, String fallbackText) {
        // Map common icon names to Unicode symbols that render well
        if (iconFileName.contains("blood") || iconFileName.contains("donor")) {
            return "\u2665"; // Heart symbol
        } else if (iconFileName.contains("search")) {
            return "\u2315"; // Telephone recorder symbol (search-like)
        } else if (iconFileName.contains("update")) {
            return "\u21BB"; // Clockwise reload
        } else if (iconFileName.contains("delete") || iconFileName.contains("garbage")) {
            return "\u2421"; // Delete symbol
        } else if (iconFileName.contains("warehouse") || iconFileName.contains("stock")) {
            return "\u25A0"; // Black square (box)
        } else if (iconFileName.contains("decrease")) {
            return "\u25BC"; // Down arrow
        } else if (iconFileName.contains("test") || iconFileName.contains("analytics")) {
            return "\u2637"; // Chart symbol
        } else if (iconFileName.contains("settings")) {
            return "\u2699"; // Gear symbol
        } else if (iconFileName.contains("logout")) {
            return "\u2192"; // Right arrow
        }

        // Return first character of fallback text as last resort
        return fallbackText != null && !fallbackText.isEmpty()
                ? String.valueOf(fallbackText.charAt(0))
                : "\u25CF"; // Bullet point
    }

    /**
     * Create icon for specific functionality
     */
    public static JLabel createBloodDonorIcon(int size) {
        return createIconLabel("blood-donor.png", "♥", size);
    }

    public static JLabel createSearchIcon(int size) {
        return createIconLabel("search-interface-symbol.png", "⌕", size);
    }

    public static JLabel createUpdateIcon(int size) {
        return createIconLabel("update.png", "↻", size);
    }

    public static JLabel createDeleteIcon(int size) {
        return createIconLabel("garbage.png", "␡", size);
    }

    public static JLabel createWarehouseIcon(int size) {
        return createIconLabel("warehouse.png", "■", size);
    }

    public static JLabel createDecreaseIcon(int size) {
        return createIconLabel("decrease.png", "▼", size);
    }

    public static JLabel createBloodTestIcon(int size) {
        return createIconLabel("blood-test.png", "☇", size);
    }

    public static JLabel createAnalyticsIcon(int size) {
        return createIconLabel("analytics.png", "☇", size);
    }

    public static JLabel createSettingsIcon(int size) {
        return createIconLabel("settings.png", "⚙", size);
    }

    public static JLabel createLogoutIcon(int size) {
        return createIconLabel("logout (2).png", "→", size);
    }

    /**
     * Creates a styled header icon label (for header panels)
     */
    public static JLabel createHeaderIcon(String iconType, int size) {
        String symbol;
        switch (iconType) {
            case "blood": symbol = "♥"; break;
            case "donor": symbol = "♥"; break;
            case "search": symbol = "⌕"; break;
            case "update": symbol = "↻"; break;
            case "delete": symbol = "␡"; break;
            case "warehouse": symbol = "■"; break;
            case "increase": symbol = "▲"; break;
            case "decrease": symbol = "▼"; break;
            case "test": symbol = "☇"; break;
            case "analytics": symbol = "☇"; break;
            case "hospital": symbol = "☩"; break;
            case "inventory": symbol = "☐"; break;
            case "statistics": symbol = "☇"; break;
            case "settings": symbol = "⚙"; break;
            case "center": symbol = "☩"; break;
            default: symbol = "●";
        }

        JLabel label = new JLabel(symbol);
        label.setFont(new Font("Dialog", Font.BOLD, size));
        label.setForeground(Color.WHITE);
        return label;
    }
}