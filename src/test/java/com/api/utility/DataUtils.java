package com.api.utility;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.UUID;

/**
 * Utility class for test data generation, string manipulations, and numeric calculations.
 */
public class DataUtils {

    private DataUtils() {
        // Utility class
    }

    /**
     * Generates a unique dynamic email with timestamp to avoid duplicate user registration conflicts.
     * Example: testuser1718882910291@yopmail.com
     */
    public static String generateDynamicEmail(String prefix) {
        String base = (prefix == null || prefix.isBlank() || prefix.contains("dynamic")) ? "testuser" : prefix;
        return base + System.currentTimeMillis() + "@yopmail.com";
    }

    /**
     * Rounds a double to 2 decimal places using HALF_UP rounding.
     */
    public static double roundPrice(double value) {
        return BigDecimal.valueOf(value)
                .setScale(2, RoundingMode.HALF_UP)
                .doubleValue();
    }

    /**
     * Generates a short random alphanumeric string.
     */
    public static String generateRandomString(int length) {
        String random = UUID.randomUUID().toString().replace("-", "");
        if (length > random.length()) {
            return random;
        }
        return random.substring(0, length);
    }
}
