package taylorianmeal.util;

import java.util.regex.Pattern;

/**
 * ValidationUtils
 * ---------------
 * Utility class for input validation across the application.
 */
public class ValidationUtils {
    
    // Email validation pattern
    private static final Pattern EMAIL_PATTERN = Pattern.compile(
        "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$"
    );
    
    // Phone number validation pattern (basic)
    private static final Pattern PHONE_PATTERN = Pattern.compile(
        "^[+]?[0-9\\s\\-()]{10,15}$"
    );
    
    /**
     * Validate email format
     */
    public static boolean isValidEmail(String email) {
        if (email == null || email.trim().isEmpty()) {
            return false;
        }
        return EMAIL_PATTERN.matcher(email.trim()).matches();
    }
    
    /**
     * Validate phone number format
     */
    public static boolean isValidPhone(String phone) {
        if (phone == null || phone.trim().isEmpty()) {
            return false;
        }
        return PHONE_PATTERN.matcher(phone.trim()).matches();
    }
    
    /**
     * Validate username (alphanumeric, 3-20 characters)
     */
    public static boolean isValidUsername(String username) {
        if (username == null || username.trim().isEmpty()) {
            return false;
        }
        String trimmed = username.trim();
        return trimmed.length() >= 3 && trimmed.length() <= 20 && 
               trimmed.matches("^[A-Za-z0-9_]+$");
    }
    
    /**
     * Validate password strength (at least 6 characters)
     */
    public static boolean isValidPassword(String password) {
        return password != null && password.length() >= 6;
    }
    
    /**
     * Validate full name (non-empty, reasonable length)
     */
    public static boolean isValidFullName(String fullName) {
        if (fullName == null || fullName.trim().isEmpty()) {
            return false;
        }
        String trimmed = fullName.trim();
        return trimmed.length() >= 2 && trimmed.length() <= 50;
    }
    
    /**
     * Validate price (positive number)
     */
    public static boolean isValidPrice(double price) {
        return price > 0;
    }
    
    /**
     * Validate price string
     */
    public static boolean isValidPrice(String priceStr) {
        try {
            double price = Double.parseDouble(priceStr);
            return isValidPrice(price);
        } catch (NumberFormatException e) {
            return false;
        }
    }
    
    /**
     * Validate quantity (positive integer)
     */
    public static boolean isValidQuantity(int quantity) {
        return quantity > 0;
    }
    
    /**
     * Validate quantity string
     */
    public static boolean isValidQuantity(String quantityStr) {
        try {
            int quantity = Integer.parseInt(quantityStr);
            return isValidQuantity(quantity);
        } catch (NumberFormatException e) {
            return false;
        }
    }
    
    /**
     * Check if string is not null and not empty
     */
    public static boolean isNotEmpty(String str) {
        return str != null && !str.trim().isEmpty();
    }
    
    /**
     * Sanitize input string (remove leading/trailing whitespace)
     */
    public static String sanitize(String input) {
        return input != null ? input.trim() : "";
    }
} 