package taylorianmeal.model;

/**
 * UserRole
 * --------
 * Enum representing the different roles a user can have in the system.
 */
public enum UserRole {
    CUSTOMER("Customer"),
    MERCHANT("Merchant"),
    ADMIN("Admin");
    
    private final String displayName;
    
    UserRole(String displayName) {
        this.displayName = displayName;
    }
    
    public String getDisplayName() {
        return displayName;
    }
    
    public static UserRole fromString(String role) {
        for (UserRole userRole : values()) {
            if (userRole.displayName.equalsIgnoreCase(role)) {
                return userRole;
            }
        }
        throw new IllegalArgumentException("Unknown role: " + role);
    }
} 