package taylorianmeal.service;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.sql.*;
import taylorianmeal.DatabaseManager;

/**
 * ComplaintManager – all DB operations related to customer complaints
 *
 * • add(...)            → create a new complaint record
 * • listForCustomer(...)→ fetch all complaints belonging to one customer (for TableView)
 * • updateStatus(...)   → change the status after staff review
 */
public final class ComplaintManager {

    /** Row model used by the JavaFX front-end */
    public record ComplaintRow(
            long   id,          // complaint PK
            long   orderId,     // related order
            String restaurant,  // restaurant name
            String issueType,   // type/category
            String status,      // OPEN / IN-PROGRESS / CLOSED …
            String createdAt    // local time string
    ) {}

    /** Create a new complaint */
    public static void add(int customerId,
                           long orderId,
                           String issueType,
                           String description) throws SQLException {

        final String SQL = """
            INSERT INTO complaints (order_id, customer_id, issue_type, description)
            VALUES (?,?,?,?)
        """;

        try (Connection c = DriverManager.getConnection(DatabaseManager.JDBC_URL);
             PreparedStatement ps = c.prepareStatement(SQL)) {

            ps.setLong  (1, orderId);
            ps.setInt   (2, customerId);
            ps.setString(3, issueType);
            ps.setString(4, description);
            ps.executeUpdate();
        }
    }

    /** Return an observable list of all complaints for the given user */
    public static ObservableList<ComplaintRow> listForCustomer(int customerId) throws SQLException {

        final String SQL = """
            SELECT cp.id,
                   cp.order_id,
                   r.name AS restaurant,
                   cp.issue_type,
                   cp.status,
                   datetime(cp.created_at,'localtime') AS created_at
              FROM complaints cp
              JOIN orders      o ON cp.order_id   = o.id
              JOIN restaurants r ON o.restaurant_id = r.id
             WHERE cp.customer_id = ?
             ORDER BY cp.created_at DESC
        """;

        ObservableList<ComplaintRow> list = FXCollections.observableArrayList();

        try (Connection c = DriverManager.getConnection(DatabaseManager.JDBC_URL);
             PreparedStatement ps = c.prepareStatement(SQL)) {

            ps.setInt(1, customerId);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(new ComplaintRow(
                            rs.getLong("id"),
                            rs.getLong("order_id"),
                            rs.getString("restaurant"),
                            rs.getString("issue_type"),
                            rs.getString("status"),
                            rs.getString("created_at")
                    ));
                }
            }
        }
        return list;
    }

    /** Update the processing status (for staff/admin use) */
    public static void updateStatus(long complaintId, String newStatus) throws SQLException {
        try (Connection c = DriverManager.getConnection(DatabaseManager.JDBC_URL);
             PreparedStatement ps =
                     c.prepareStatement("UPDATE complaints SET status = ? WHERE id = ?")) {

            ps.setString(1, newStatus);
            ps.setLong  (2, complaintId);
            ps.executeUpdate();
        }
    }

    /** Utility class – no instantiation */
    private ComplaintManager() {}
}
