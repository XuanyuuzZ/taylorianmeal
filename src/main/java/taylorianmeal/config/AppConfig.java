package taylorianmeal.config;

/**
 * AppConfig
 * ---------
 * Configuration constants for the application.
 */
public class AppConfig {
    
    // Database configuration
    public static final String DATABASE_URL = "jdbc:sqlite:taylorianmeal.db";
    
    // Application settings
    public static final String APP_NAME = "TaylorianMeal";
    public static final String APP_VERSION = "1.0.0";
    
    // UI settings
    public static final int DEFAULT_WINDOW_WIDTH = 800;
    public static final int DEFAULT_WINDOW_HEIGHT = 600;
    public static final int LOGIN_WINDOW_WIDTH = 450;
    public static final int LOGIN_WINDOW_HEIGHT = 500;
    
    // Default admin account
    public static final String DEFAULT_ADMIN_USERNAME = "admin";
    public static final String DEFAULT_ADMIN_PASSWORD = "Admin@123";
    public static final String DEFAULT_ADMIN_FULLNAME = "System Admin";
    
    // File paths
    public static final String IMAGES_PATH = "/taylorianmeal/images/";
    public static final String CSS_PATH = "/taylorianmeal/css/";
    
    // Order status constants
    public static final String ORDER_STATUS_PENDING = "Pending";
    public static final String ORDER_STATUS_CONFIRMED = "Confirmed";
    public static final String ORDER_STATUS_PREPARING = "Preparing";
    public static final String ORDER_STATUS_READY = "Ready";
    public static final String ORDER_STATUS_DELIVERED = "Delivered";
    public static final String ORDER_STATUS_CANCELLED = "Cancelled";
    
    // Issue status constants
    public static final String ISSUE_STATUS_OPEN = "Open";
    public static final String ISSUE_STATUS_IN_PROGRESS = "In Progress";
    public static final String ISSUE_STATUS_RESOLVED = "Resolved";
    public static final String ISSUE_STATUS_CLOSED = "Closed";
    
    // Notification types
    public static final String NOTIFICATION_TYPE_ORDER = "Order";
    public static final String NOTIFICATION_TYPE_SYSTEM = "System";
    public static final String NOTIFICATION_TYPE_ISSUE = "Issue";
    
    private AppConfig() {
        // Utility class - prevent instantiation
    }
} 