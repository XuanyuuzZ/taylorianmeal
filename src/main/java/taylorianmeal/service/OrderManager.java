package taylorianmeal.service;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import taylorianmeal.DatabaseManager;

import java.sql.*;
import java.util.Map;

/**
 * OrderManager – central helper for every order-related DB operation.
 * ---------------------------------------------------------------
 * Responsibilities:
 *  • placeOrder(...)         create record(s) in orders / order_items + notification
 *  • updateStatus(...)       update order status text
 *  • listOrdersForCustomer   fetch and return orders for TableView binding
 *  • listOrdersForRestaurant fetch and return orders for a merchant's restaurant
 */
public final class OrderManager {

    private OrderManager() {
        // Utility class: prevent instantiation
    }

    /**
     * Place a new order and create order_items + notification in one transaction.
     *
     * @param customerId    ID of the customer placing the order
     * @param restaurantId  ID of the restaurant
     * @param items         Map of item_id -> quantity
     * @return generated order ID
     * @throws SQLException if any database error occurs
     */
    public static long placeOrder(int customerId,
                                  int restaurantId,
                                  Map<Integer, Integer> items) throws SQLException {

        final String INSERT_ORDER =
                "INSERT INTO orders (customer_id, restaurant_id, total_amount) VALUES (?,?,?)";
        final String INSERT_ITEM  =
                "INSERT INTO order_items (order_id, item_id, quantity, subtotal) VALUES (?,?,?,?)";

        try (Connection conn = DriverManager.getConnection(DatabaseManager.JDBC_URL)) {
            conn.setAutoCommit(false);

            // 1) Calculate total amount
            double total = 0.0;
            for (Map.Entry<Integer, Integer> entry : items.entrySet()) {
                double price = getPrice(entry.getKey(), conn);
                total += price * entry.getValue();
            }

            // 2) Insert into orders
            long orderId;
            try (PreparedStatement ps = conn.prepareStatement(INSERT_ORDER, Statement.RETURN_GENERATED_KEYS)) {
                ps.setInt(1, customerId);
                ps.setInt(2, restaurantId);
                ps.setDouble(3, total);
                ps.executeUpdate();
                try (ResultSet keys = ps.getGeneratedKeys()) {
                    if (!keys.next()) throw new SQLException("Failed to retrieve order ID");
                    orderId = keys.getLong(1);
                }
            }

            // 3) Insert into order_items
            try (PreparedStatement ps = conn.prepareStatement(INSERT_ITEM)) {
                for (Map.Entry<Integer, Integer> entry : items.entrySet()) {
                    double price = getPrice(entry.getKey(), conn);
                    ps.setLong(1, orderId);
                    ps.setInt(2, entry.getKey());
                    ps.setInt(3, entry.getValue());
                    ps.setDouble(4, price * entry.getValue());
                    ps.addBatch();
                }
                ps.executeBatch();
            }

            // 4) Add notification
            String orderCode = String.format("TM%05d", orderId);
            String message   = "Your order " + orderCode + " has been placed successfully.";
            addNotification(conn, customerId, "Order Placed", message);

            conn.commit();
            return orderId;
        }
    }

    /**
     * Update the status of an existing order.
     *
     * @param orderId    ID of the order
     * @param newStatus  new status text
     * @throws SQLException if update fails
     */
    public static void updateStatus(long orderId, String newStatus) throws SQLException {
        final String SQL = "UPDATE orders SET status = ? WHERE id = ?";
        try (Connection c = DriverManager.getConnection(DatabaseManager.JDBC_URL);
             PreparedStatement ps = c.prepareStatement(SQL)) {
            ps.setString(1, newStatus);
            ps.setLong(2, orderId);
            ps.executeUpdate();
        }
    }

    /**
     * Fetch all orders for a given customer and map to OrderRecord list.
     *
     * @param customerId ID of the customer
     * @return observable list of OrderRecord for TableView
     * @throws SQLException if query fails
     */
    public static ObservableList<OrderRecord> listOrdersForCustomer(int customerId) throws SQLException {
        final String SQL =
                "SELECT r.name AS restaurant, o.id AS order_id, date(o.placed_time) AS date_only, o.status " +
                        "FROM orders o JOIN restaurants r ON o.restaurant_id = r.id " +
                        "WHERE o.customer_id = ? ORDER BY o.placed_time DESC";

        ObservableList<OrderRecord> data = FXCollections.observableArrayList();
        try (Connection c = DriverManager.getConnection(DatabaseManager.JDBC_URL);
             PreparedStatement ps = c.prepareStatement(SQL)) {
            ps.setInt(1, customerId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    String rest = rs.getString("restaurant");
                    long   id   = rs.getLong("order_id");
                    String code = String.format("TM%05d", id);
                    String date = rs.getString("date_only");
                    String st   = rs.getString("status");
                    data.add(new OrderRecord(rest, code, date, st));
                }
            }
        }
        return data;
    }

    /**
     * Fetch all orders for a given restaurant and map to OrderRecord list.
     *
     * @param restaurantId ID of the restaurant
     * @return observable list of OrderRecord for TableView
     * @throws SQLException if query fails
     */
    public static ObservableList<OrderRecord> listOrdersForRestaurant(int restaurantId) throws SQLException {
        final String SQL =
                "SELECT r.name AS restaurant, o.id AS order_id, date(o.placed_time) AS date_only, o.status " +
                        "FROM orders o JOIN restaurants r ON o.restaurant_id = r.id " +
                        "WHERE o.restaurant_id = ? ORDER BY o.placed_time DESC";

        ObservableList<OrderRecord> data = FXCollections.observableArrayList();
        try (Connection c = DriverManager.getConnection(DatabaseManager.JDBC_URL);
             PreparedStatement ps = c.prepareStatement(SQL)) {
            ps.setInt(1, restaurantId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    String rest = rs.getString("restaurant");
                    long   id   = rs.getLong("order_id");
                    String code = String.format("TM%05d", id);
                    String date = rs.getString("date_only");
                    String st   = rs.getString("status");
                    data.add(new OrderRecord(rest, code, date, st));
                }
            }
        }
        return data;
    }

    /**
     * Helper: fetch price of a menu item by ID using the given connection.
     */
    private static double getPrice(int itemId, Connection conn) throws SQLException {
        final String SQL = "SELECT price FROM menu_items WHERE id = ?";
        try (PreparedStatement ps = conn.prepareStatement(SQL)) {
            ps.setInt(1, itemId);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? rs.getDouble("price") : 0.0;
            }
        }
    }

    /**
     * Helper: insert a notification row in the same transaction.
     */
    private static void addNotification(Connection conn,
                                        int customerId,
                                        String title,
                                        String message) throws SQLException {
        final String SQL = "INSERT INTO notifications (user_id, title, message) VALUES (?,?,?)";
        try (PreparedStatement ps = conn.prepareStatement(SQL)) {
            ps.setInt(1, customerId);
            ps.setString(2, title);
            ps.setString(3, message);
            ps.executeUpdate();
        }
    }

    /**
     * OrderRecord – bean model for TableView row.
     */
    public static class OrderRecord {
        private final SimpleStringProperty restaurant;
        private final SimpleStringProperty orderId;
        private final SimpleStringProperty date;
        private final SimpleStringProperty status;

        public OrderRecord(String restaurant,
                           String orderId,
                           String date,
                           String status) {
            this.restaurant = new SimpleStringProperty(restaurant);
            this.orderId    = new SimpleStringProperty(orderId);
            this.date       = new SimpleStringProperty(date);
            this.status     = new SimpleStringProperty(status);
        }
        public String getRestaurant() { return restaurant.get(); }
        public String getOrderId()    { return orderId.get();    }
        public String getDate()       { return date.get();       }
        public String getStatus()     { return status.get();     }

        // Expose status property for UI updates
        public SimpleStringProperty statusProperty() { return status; }
    }
}