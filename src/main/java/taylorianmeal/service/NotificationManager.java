package taylorianmeal.service;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import taylorianmeal.DatabaseManager;

import java.sql.*;
import com.sendgrid.*;
import com.sendgrid.helpers.mail.Mail;
import com.sendgrid.helpers.mail.objects.Email;
import com.sendgrid.helpers.mail.objects.Content;
import taylorianmeal.DatabaseManager.UserProfile;

/**
 * NotificationManager – helper for all CRUD operations on the <b>notifications</b> table.
 *
 * <p>Available API:
 * <ul>
 *   <li>{@link #add(int, String, String)} – insert a new notification (throws SQLException)</li>
 *   <li>{@link #listForCustomer(int)} – fetch all notifications for a customer (throws SQLException)</li>
 *   <li>{@link #notifyCustomer(int, String, String)} – insert a new notification and handle errors internally</li>
 * </ul>
 */
public final class NotificationManager {

    private NotificationManager() {
        // Utility class – prevent instantiation
    }

    // Optional email credentials are provided through the environment.
    private static final String SENDGRID_API_KEY = System.getenv("SENDGRID_API_KEY");
    private static final String FROM_EMAIL = System.getenv().getOrDefault("SENDGRID_FROM_EMAIL", "demo@example.com");
    private static final String FROM_NAME = "TaylorianMeal";

    /**
     * Send email notification to user
     * @param userId user ID
     * @param title email subject
     * @param message email content
     */
    private static void sendEmail(int userId, String title, String message) {
        if (SENDGRID_API_KEY == null || SENDGRID_API_KEY.isBlank()) return;
        try {
            UserProfile profile = DatabaseManager.fetchUserProfile(userId);
            if (profile == null || profile.email() == null || profile.email().isEmpty()) {
                System.err.println("❌ User email does not exist, cannot send email: userId=" + userId);
                return;
            }
            Email from = new Email(FROM_EMAIL, FROM_NAME);
            Email to = new Email(profile.email(), profile.fullName());
            Content content = new Content("text/plain", message);
            Mail mail = new Mail(from, title, to, content);
            SendGrid sg = new SendGrid(SENDGRID_API_KEY);
            Request request = new Request();
            request.setMethod(Method.POST);
            request.setEndpoint("mail/send");
            request.setBody(mail.build());
            Response response = sg.api(request);
            if (response.getStatusCode() >= 200 && response.getStatusCode() < 300) {
                System.out.println("✅ Email sent to " + profile.email());
            } else {
                System.err.println("❌ Failed to send email: " + response.getStatusCode() + " " + response.getBody());
            }
        } catch (Exception e) {
            System.err.println("❌ Exception while sending email: " + e.getMessage());
        }
    }

    /**
     * Data model returned to the UI layer.
     */
    public record NotificationRow(
            String title,        // e.g. "Order Placed"
            String message,      // e.g. "Your order TM00023 has been placed."
            String createdAt     // local timestamp string
    ) {}

    /**
     * Insert a new notification into the database.
     *
     * @param userId ID of the user to notify
     * @param title      notification title
     * @param message    notification message
     * @throws SQLException if the insert fails
     */
    public static void add(int userId,
                           String title,
                           String message) throws SQLException {

        final String SQL = """
            INSERT INTO notifications (user_id, title, message)
            VALUES (?,?,?)
        """;

        try (Connection conn = DriverManager.getConnection(DatabaseManager.JDBC_URL);
             PreparedStatement ps = conn.prepareStatement(SQL)) {
            ps.setInt   (1, userId);
            ps.setString(2, title);
            ps.setString(3, message);
            ps.executeUpdate();
        }
        // New: send email synchronously
        sendEmail(userId, title, message);
    }

    /**
     * Fetch all notifications for a given user, newest first.
     *
     * @param userId ID of the user
     * @return ObservableList of NotificationRow for UI binding
     * @throws SQLException if the query fails
     */
    public static ObservableList<NotificationRow> listForCustomer(int userId)
            throws SQLException {

        final String SQL = """
            SELECT title,
                   message,
                   datetime(created_at, 'localtime') AS created_at
              FROM notifications
             WHERE user_id = ?
             ORDER BY created_at DESC
        """;

        ObservableList<NotificationRow> list = FXCollections.observableArrayList();

        try (Connection conn = DriverManager.getConnection(DatabaseManager.JDBC_URL);
             PreparedStatement ps = conn.prepareStatement(SQL)) {
            ps.setInt(1, userId);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(new NotificationRow(
                            rs.getString("title"),
                            rs.getString("message"),
                            rs.getString("created_at")
                    ));
                }
            }
        }
        return list;
    }

    /**
     * Convenience method to send a notification without forcing the caller
     * to handle SQLException. Errors are logged to stderr.
     *
     * @param userId ID of the user to notify
     * @param title      notification title
     * @param message    notification message
     */
    public static void notifyCustomer(int userId,
                                      String title,
                                      String message) {
        try {
            add(userId, title, message);
        } catch (SQLException ex) {
            System.err.println("❌ Failed to send notification to user "
                    + userId + ": " + ex.getMessage());
            // Even if database fails, try to send email
            sendEmail(userId, title, message);
        }
    }

    // Query platform announcements
    public static ObservableList<NotificationRow> listPlatformNotices(int userId) throws SQLException {
        final String SQL = """
            SELECT 'Platform Notice' AS title,
                   message,
                   datetime(created_at, 'localtime') AS created_at
              FROM user_inbox
             WHERE user_id = ?
             ORDER BY created_at DESC
        """;
        ObservableList<NotificationRow> list = FXCollections.observableArrayList();
        try (Connection conn = DriverManager.getConnection(DatabaseManager.JDBC_URL);
             PreparedStatement ps = conn.prepareStatement(SQL)) {
            ps.setInt(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(new NotificationRow(
                            rs.getString("title"),
                            rs.getString("message"),
                            rs.getString("created_at")
                    ));
                }
            }
        }
        return list;
    }
}