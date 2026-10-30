package taylorianmeal;

import taylorianmeal.model.Issue;
import taylorianmeal.model.TechnicalIssue;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;


/**
 * DatabaseManager
 * ---------------
 * • Creates / upgrades the SQLite schema (including merchant links)
 * • Seeds demo data (restaurants, menu items, a "oldtown" merchant, and linkage)
 * • Provides user-oriented helpers (insert / authenticate / fetch & update profile)
 * • Ensures at least one administrator account exists on first launch
 *
 * ► One single SQLite file named <code>taylorianmeal.db</code> is created in the
 *   working directory. Change {@link #JDBC_URL} to point elsewhere if desired.
 */
public final class DatabaseManager {

    /** JDBC connection string – points to the DB file in the project root */
    public static final String JDBC_URL = "jdbc:sqlite:taylorianmeal.db";

    /* =======================================================================
     * 1) Database initialization
     * ======================================================================= */

    /** Ensures at least one admin account exists (creates one if none found) */
    public static void ensureBuiltInAdmin(String username,
                                          String password,
                                          String fullName) {
        final String QUERY = "SELECT 1 FROM users WHERE role = 'Admin' LIMIT 1";
        try (Connection conn = DriverManager.getConnection(JDBC_URL);
             PreparedStatement ps = conn.prepareStatement(QUERY);
             ResultSet rs = ps.executeQuery()) {
            if (!rs.next()) {
                // No admin found – create one
                final String INSERT = """
                    INSERT INTO users (full_name, email, username, password, role)
                    VALUES (?, ?, ?, ?, 'Admin')
                """;
                try (PreparedStatement insertPs = conn.prepareStatement(INSERT)) {
                    insertPs.setString(1, fullName);
                    insertPs.setString(2, username + "@taylorianmeal.com");
                    insertPs.setString(3, username);
                    insertPs.setString(4, password);
                    insertPs.executeUpdate();
                    System.out.println("✅  Built-in admin '" + username + "' created.");
                }
            }
        } catch (SQLException ex) {
            System.err.println("❌  Admin check/creation failed: " + ex.getMessage());
        }
    }

    /** Creates the database schema (tables, indexes, etc.) */
    public static void initDatabase() {
        try (Connection conn = DriverManager.getConnection(JDBC_URL)) {
            // Users table
            conn.createStatement().execute("""
                CREATE TABLE IF NOT EXISTS users (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    full_name TEXT NOT NULL,
                    email TEXT NOT NULL,
                    username TEXT UNIQUE NOT NULL,
                    password TEXT NOT NULL,
                    telephone TEXT,
                    role TEXT NOT NULL DEFAULT 'Customer'
                )
            """);

            // Restaurants table
            conn.createStatement().execute("""
                CREATE TABLE IF NOT EXISTS restaurants (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    name TEXT NOT NULL,
                    description TEXT,
                    location TEXT,
                    merchant_id INTEGER,
                    FOREIGN KEY (merchant_id) REFERENCES users (id)
                )
            """);

            // Menu items table
            conn.createStatement().execute("""
                CREATE TABLE IF NOT EXISTS menu_items (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    restaurant_id INTEGER NOT NULL,
                    name TEXT NOT NULL,
                    description TEXT,
                    price DECIMAL(10,2) NOT NULL,
                    category TEXT,
                    FOREIGN KEY (restaurant_id) REFERENCES restaurants (id)
                )
            """);

            // Orders table
            conn.createStatement().execute("""
                CREATE TABLE IF NOT EXISTS orders (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    customer_id INTEGER NOT NULL,
                    restaurant_id INTEGER NOT NULL,
                    order_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                    status TEXT DEFAULT 'Pending',
                    total_amount DECIMAL(10,2) NOT NULL,
                    FOREIGN KEY (customer_id) REFERENCES users (id),
                    FOREIGN KEY (restaurant_id) REFERENCES restaurants (id)
                )
            """);

            // Order items table
            conn.createStatement().execute("""
                CREATE TABLE IF NOT EXISTS order_items (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    order_id INTEGER NOT NULL,
                    menu_item_id INTEGER NOT NULL,
                    quantity INTEGER NOT NULL,
                    price DECIMAL(10,2) NOT NULL,
                    FOREIGN KEY (order_id) REFERENCES orders (id),
                    FOREIGN KEY (menu_item_id) REFERENCES menu_items (id)
                )
            """);

            // Notifications table
            conn.createStatement().execute("""
                CREATE TABLE IF NOT EXISTS notifications (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    user_id INTEGER NOT NULL,
                    title TEXT NOT NULL,
                    message TEXT NOT NULL,
                    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                    is_read BOOLEAN DEFAULT FALSE,
                    FOREIGN KEY (user_id) REFERENCES users (id)
                )
            """);

            // Complaints table
            conn.createStatement().execute("""
                CREATE TABLE IF NOT EXISTS complaints (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    user_id INTEGER NOT NULL,
                    order_id INTEGER,
                    subject TEXT NOT NULL,
                    description TEXT NOT NULL,
                    status TEXT DEFAULT 'Open',
                    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                    FOREIGN KEY (user_id) REFERENCES users (id),
                    FOREIGN KEY (order_id) REFERENCES orders (id)
                )
            """);

            // Technical issues table
            conn.createStatement().execute("""
                CREATE TABLE IF NOT EXISTS technical_issues (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    reporter_id INTEGER NOT NULL,
                    issue_type TEXT NOT NULL,
                    description TEXT NOT NULL,
                    status TEXT DEFAULT 'Open',
                    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                    FOREIGN KEY (reporter_id) REFERENCES users (id)
                )
            """);

            // Add this to your initDatabase() method
            conn.createStatement().execute("""
            CREATE TABLE IF NOT EXISTS issues (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                title TEXT NOT NULL,
                role TEXT NOT NULL,
                reporter TEXT NOT NULL,
                status TEXT NOT NULL,
                date TEXT NOT NULL,
                description TEXT
            )
        """);

            System.out.println("✅  Database schema initialized.");
        } catch (SQLException ex) {
            System.err.println("❌  Database initialization failed: " + ex.getMessage());
        }
    }

    /* =======================================================================
     * 2) Demo data seeding
     * ======================================================================= */

    /** Seeds the database with demo restaurants, menu items, and a merchant */
    public static void seedInitialData() {
        try (Connection conn = DriverManager.getConnection(JDBC_URL)) {
            // Check if data already exists
            try (ResultSet rs = conn.createStatement().executeQuery("SELECT COUNT(*) FROM restaurants")) {
                if (rs.next() && rs.getInt(1) > 0) {
                    System.out.println("✅  Demo data already exists, skipping.");
                    return;
                }
            }

            // Create a merchant user for OldTown
            conn.createStatement().execute("""
                INSERT INTO users (full_name, email, username, password, telephone, role) 
                VALUES ('OldTown Manager', 'oldtown@taylorianmeal.com', 'oldtown', 'password123', '012-3456789', 'Merchant')
            """);

            // Get the merchant ID
            int merchantId;
            try (ResultSet rs = conn.createStatement().executeQuery("SELECT id FROM users WHERE username = 'oldtown'")) {
                rs.next();
                merchantId = rs.getInt("id");
            }

            // Create OldTown restaurant
            conn.createStatement().execute("""
                INSERT INTO restaurants (name, description, location, merchant_id) 
                VALUES ('OldTown White Coffee', 'Traditional Malaysian coffee and local cuisine', 'Taylor''s University Campus', ?)
            """.replace("?", String.valueOf(merchantId)));

            // Get restaurant ID
            int restaurantId;
            try (ResultSet rs = conn.createStatement().executeQuery("SELECT id FROM restaurants WHERE name = 'OldTown White Coffee'")) {
                rs.next();
                restaurantId = rs.getInt("id");
            }

            // Add menu items
            String[] menuItems = {
                    "INSERT INTO menu_items (restaurant_id, name, description, price, category) VALUES (?, 'White Coffee', 'Traditional Malaysian white coffee', 5.50, 'Beverages')",
                    "INSERT INTO menu_items (restaurant_id, name, description, price, category) VALUES (?, 'Nasi Lemak', 'Traditional Malaysian rice dish with coconut milk', 8.90, 'Main Course')",
                    "INSERT INTO menu_items (restaurant_id, name, description, price, category) VALUES (?, 'Black Tea', 'Refreshing black tea', 4.50, 'Beverages')",
                    "INSERT INTO menu_items (restaurant_id, name, description, price, category) VALUES (?, 'Chicken Rice', 'Hainanese chicken rice', 12.90, 'Main Course')"
            };

            for (String sql : menuItems) {
                conn.createStatement().execute(sql.replace("?", String.valueOf(restaurantId)));
            }

            System.out.println("✅  Demo data seeded successfully.");
        } catch (SQLException ex) {
            System.err.println("❌  Demo data seeding failed: " + ex.getMessage());
        }
    }

    /* =======================================================================
     * 3) Restaurant helpers
     * ======================================================================= */

    /** Fetches restaurant ID by merchant ID (-1 if not found) */
    public static int fetchRestaurantIdByMerchant(int merchantId) {
        final String SQL = "SELECT id FROM restaurants WHERE merchant_id = ?";
        try (Connection conn = DriverManager.getConnection(JDBC_URL);
             PreparedStatement ps = conn.prepareStatement(SQL)) {
            ps.setInt(1, merchantId);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? rs.getInt("id") : -1;
            }
        } catch (SQLException ex) {
            System.err.println("❌  Fetch restaurant ID failed: " + ex.getMessage());
            return -1;
        }
    }

    /* =======================================================================
     * 4) User-related helpers
     * ======================================================================= */
    /** Insert a new user (full set of columns) */
    public static void insertUser(String fullName,
                                  String email,
                                  String username,
                                  String password,
                                  String telephone) {
        final String SQL = """
            INSERT INTO users (full_name, email, username, password, telephone)
            VALUES (?,?,?,?,?)
        """;
        try (Connection conn = DriverManager.getConnection(JDBC_URL);
             PreparedStatement ps = conn.prepareStatement(SQL)) {
            ps.setString(1, fullName);
            ps.setString(2, email);
            ps.setString(3, username);
            ps.setString(4, password);
            ps.setString(5, telephone);
            ps.executeUpdate();
            System.out.println("✅  User '" + username + "' registered.");
        } catch (SQLException ex) {
            System.err.println("❌  Insert user failed: " + ex.getMessage());
        }
    }

    /** Insert a new user with role */
    public static void insertUserWithRole(String fullName,
                                          String email,
                                          String username,
                                          String password,
                                          String telephone,
                                          String role) {
        final String SQL = """
            INSERT INTO users (full_name, email, username, password, telephone, role)
            VALUES (?,?,?,?,?,?)
        """;
        try (Connection conn = DriverManager.getConnection(JDBC_URL);
             PreparedStatement ps = conn.prepareStatement(SQL)) {
            ps.setString(1, fullName);
            ps.setString(2, email);
            ps.setString(3, username);
            ps.setString(4, password);
            ps.setString(5, telephone);
            ps.setString(6, role);
            ps.executeUpdate();
            System.out.println("✅  User '" + username + "' registered with role '" + role + "'.");
        } catch (SQLException ex) {
            System.err.println("❌  Insert user failed: " + ex.getMessage());
            throw new RuntimeException(ex);
        }
    }

    /** Authenticate and return the role (or null if invalid) */
    public static String authenticateUser(String username, String password) {
        final String SQL = "SELECT role FROM users WHERE username = ? AND password = ?";
        try (Connection conn = DriverManager.getConnection(JDBC_URL);
             PreparedStatement ps = conn.prepareStatement(SQL)) {
            ps.setString(1, username);
            ps.setString(2, password);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? rs.getString("role") : null;
            }
        } catch (SQLException ex) {
            System.err.println("❌  Authentication error: " + ex.getMessage());
            return null;
        }
    }

    /** Fetch a user's PK id by username (-1 if not found) */
    public static int fetchUserId(String username) {
        final String SQL = "SELECT id FROM users WHERE username = ?";
        try (Connection conn = DriverManager.getConnection(JDBC_URL);
             PreparedStatement ps = conn.prepareStatement(SQL)) {
            ps.setString(1, username);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? rs.getInt("id") : -1;
            }
        } catch (SQLException ex) {
            System.err.println("❌  Fetch user ID failed: " + ex.getMessage());
            return -1;
        }
    }

    /**
     * Add a new user (merchant or admin) to the users table, with full name.
     */
    public static void addUser(String username, String password, String email, String role, String fullName) throws SQLException {
        String sql = "INSERT INTO users (username, password, email, role, full_name) VALUES (?, ?, ?, ?, ?)";
        try (Connection c = DriverManager.getConnection(JDBC_URL);
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, username);
            ps.setString(2, password); // Should be encrypted in real projects
            ps.setString(3, email);
            ps.setString(4, role);
            ps.setString(5, fullName);
            ps.executeUpdate();
        }
    }

    /* =======================================================================
     * 5) Profile helpers
     * ======================================================================= */
    /** DTO for returning profile information */
    public static record UserProfile(
            String fullName,
            String email,
            String telephone,
            String username
    ) {}

    /**
     * Inserts a new technical issue report
     *
     * @param reporterId  the user_id of the reporter (merchant or customer)
     * @param issueType   the type of issue
     * @param description the issue description
     * @throws SQLException if database operation fails
     */
    public static void reportTechnicalIssue(int reporterId,
                                            String issueType,
                                            String description) throws SQLException {
        final String SQL = """
            INSERT INTO technical_issues (reporter_id, issue_type, description)
            VALUES (?, ?, ?)
        """;
        try (Connection c = DriverManager.getConnection(JDBC_URL);
             PreparedStatement ps = c.prepareStatement(SQL)) {
            ps.setInt(1, reporterId);
            ps.setString(2, issueType);
            ps.setString(3, description);
            ps.executeUpdate();
        }
    }

    /** Reads a user's profile by id (returns null if not found) */
    public static UserProfile fetchUserProfile(int userId) throws SQLException {
        final String SQL = """
            SELECT full_name, email, telephone, username
              FROM users
             WHERE id = ?
        """;
        try (Connection c = DriverManager.getConnection(JDBC_URL);
             PreparedStatement ps = c.prepareStatement(SQL)) {
            ps.setInt(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new UserProfile(
                            rs.getString("full_name"),
                            rs.getString("email"),
                            rs.getString("telephone"),
                            rs.getString("username")
                    );
                }
                return null;
            }
        }
    }

    /** Updates editable profile fields */
    public static void updateUserProfile(int userId,
                                         String fullName,
                                         String email,
                                         String telephone) throws SQLException {
        final String SQL = """
            UPDATE users
               SET full_name = ?, email = ?, telephone = ?
             WHERE id = ?
        """;
        try (Connection c = DriverManager.getConnection(JDBC_URL);
             PreparedStatement ps = c.prepareStatement(SQL)) {
            ps.setString(1, fullName);
            ps.setString(2, email);
            ps.setString(3, telephone);
            ps.setInt(4, userId);
            ps.executeUpdate();
        }
    }

    /* =======================================================================
     * 6) User management helpers
     * ======================================================================= */

    /** DTO for returning user information */
    public static class UserInfo {
        private final int id;
        private final String fullName;
        private final String email;
        private final String username;
        private final String telephone;
        private final String role;

        public UserInfo(int id, String fullName, String email, String username, String telephone, String role) {
            this.id = id;
            this.fullName = fullName;
            this.email = email;
            this.username = username;
            this.telephone = telephone;
            this.role = role;
        }

        public int id() { return id; }
        public String fullName() { return fullName; }
        public String email() { return email; }
        public String username() { return username; }
        public String telephone() { return telephone; }
        public String role() { return role; }
    }

    /**
     * Fetch all users from the database
     * @return List of all users
     * @throws SQLException if database operation fails
     */
    public static java.util.List<UserInfo> fetchAllUsers() throws SQLException {
        final String SQL = """
            SELECT id, full_name, email, username, telephone, role
              FROM users
             ORDER BY role, username
        """;
        java.util.List<UserInfo> users = new java.util.ArrayList<>();

        try (Connection c = DriverManager.getConnection(JDBC_URL);
             PreparedStatement ps = c.prepareStatement(SQL);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                users.add(new UserInfo(
                        rs.getInt("id"),
                        rs.getString("full_name"),
                        rs.getString("email"),
                        rs.getString("username"),
                        rs.getString("telephone"),
                        rs.getString("role")
                ));
            }
        }
        return users;
    }

    /**
     * Delete a user by ID (only for Customer and Merchant roles)
     * @param userId the user ID to delete
     * @throws SQLException if database operation fails or user is Admin
     */
    public static void deleteUser(int userId) throws SQLException {
        final String SQL = "DELETE FROM users WHERE id = ?";
        try (Connection c = DriverManager.getConnection(JDBC_URL);
             PreparedStatement ps = c.prepareStatement(SQL)) {
            ps.setInt(1, userId);
            int affected = ps.executeUpdate();
            if (affected == 0) {
                throw new SQLException("User not found or cannot be deleted");
            }
        }
    }

    /* =======================================================================
     * 7) Hide constructor
     * ======================================================================= */
    private DatabaseManager() { /* static utility class – no instances */ }

    // Fetch all order issues from the 'issues' table
    public static List<Issue> getAllOrderIssues() {
        List<Issue> issues = new ArrayList<>();
        String sql = "SELECT id, title, role, reporter, status, date, description FROM issues";
        try (Connection conn = DriverManager.getConnection(JDBC_URL);
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                issues.add(new Issue(
                        String.valueOf(rs.getInt("id")),
                        rs.getString("title"),
                        rs.getString("role"),
                        rs.getString("reporter"),
                        rs.getString("status"),
                        rs.getString("date"),
                        rs.getString("description")
                ));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return issues;
    }

    // Fetch all technical issues from the 'technical_issues' table
    public static List<TechnicalIssue> getAllTechnicalIssues() {
        List<TechnicalIssue> issues = new ArrayList<>();
        String sql = "SELECT id, reporter_id, issue_type, description, status FROM technical_issues";
        try (Connection conn = DriverManager.getConnection(JDBC_URL);
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                String id = String.valueOf(rs.getInt("id"));
                String reporter = String.valueOf(rs.getInt("reporter_id")); // Or fetch username if needed
                String type = rs.getString("issue_type");
                String description = rs.getString("description");
                String status = rs.getString("status");
                issues.add(new TechnicalIssue(id, reporter, type, description, status));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return issues;
    }

    // Update the status of an order issue by id
    public static void updateIssueStatus(String issueId, String newStatus) {
        String sql = "UPDATE issues SET status = ? WHERE id = ?";
        try (Connection conn = DriverManager.getConnection(JDBC_URL);
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, newStatus);
            ps.setString(2, issueId);
            ps.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Send a notification to a user (by username)
    public static void sendNotificationToUser(String username, String message) {
        // Find user id by username
        int userId = -1;
        String findUserSql = "SELECT id FROM users WHERE username = ?";
        try (Connection conn = DriverManager.getConnection(JDBC_URL);
             PreparedStatement ps = conn.prepareStatement(findUserSql)) {
            ps.setString(1, username);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    userId = rs.getInt("id");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (userId == -1) return;

        // Insert notification
        String insertSql = "INSERT INTO notifications (user_id, title, message) VALUES (?, ?, ?)";
        try (Connection conn = DriverManager.getConnection(JDBC_URL);
             PreparedStatement ps = conn.prepareStatement(insertSql)) {
            ps.setInt(1, userId);
            ps.setString(2, "Order Issue Update");
            ps.setString(3, message);
            ps.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public static void broadcastPlatformNotice(String notice) throws SQLException {
        // Option 2: Directly insert into notifications table
        String sql = "INSERT INTO notifications (user_id, title, message) SELECT id, 'Platform Notice', ? FROM users";
        try (Connection conn = DriverManager.getConnection(JDBC_URL);
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, notice);
            ps.executeUpdate();
        }
    }

    // Query username by user ID
    public static String fetchUsernameById(String id) {
        String username = null;
        String sql = "SELECT username FROM users WHERE id = ?";
        try (Connection conn = DriverManager.getConnection(JDBC_URL);
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    username = rs.getString("username");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return username;
    }
}