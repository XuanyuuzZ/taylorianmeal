package taylorianmeal.view.pages;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;

import java.sql.*;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

import taylorianmeal.view.components.Navigation;
import taylorianmeal.DatabaseManager;
import taylorianmeal.service.OrderManager;
import taylorianmeal.view.pages.order.PaymentSuccessfulPage;
import taylorianmeal.view.pages.order.PreOrderPage;

/**
 * OldTownMenuPage – displays the menu for Old Town White Coffee, building the UI
 * dynamically from the database.
 */
public class OldTownMenuPage {

    // Primary key of Old Town restaurant in 'restaurants' table
    private static final int RESTAURANT_ID = 1;
    // Stores the current customer ID for placing orders
    private static int customerId;

    // Cart state: maps menu item IDs to quantities
    private static final Map<Integer, Integer> cartMap = new HashMap<>();
    private static final VBox orderList = new VBox(10);
    private static double subtotal = 0;

    // Labels for order summary
    private static final Label subtotalLabel = new Label("Subtotal: 0.00 RM");
    private static final Label taxLabel      = new Label("Tax (6%): 0.00 RM");
    private static final Label totalLabel    = new Label("Total: 0.00 RM");

    /**
     * Entry point: show the Old Town menu page for the given customer.
     *
     * @param custId the ID of the logged-in customer
     */
    public static void display(int custId) {
        customerId = custId;
        cartMap.clear();
        orderList.getChildren().clear();
        refreshTotal();

        BorderPane root = new BorderPane();
        root.setPadding(new Insets(20));
        root.setStyle("-fx-background-color:#f9f9f9;");

        // Header: logo and restaurant information
        HBox header = new HBox(20);
        header.setAlignment(Pos.CENTER_LEFT);
        ImageView logo = new ImageView(
                new Image(OldTownMenuPage.class.getResourceAsStream("/images/oldtown.png")));
        logo.setFitHeight(50);
        logo.setPreserveRatio(true);
        VBox info = new VBox(5,
                bold("Old Town White Coffee", 24),
                new Label("Location: G-4"),
                new Label("Opening Hours: Mon–Sat 7:30 AM–9:00 PM"));
        header.getChildren().addAll(logo, info);
        root.setTop(header);

        // Left: load menu items from the database
        VBox menuBox = new VBox(15);
        menuBox.getChildren().addAll(loadMenuItems());
        VBox left = new VBox(20, bold("Menu", 18), menuBox);
        left.setPrefWidth(600);
        root.setLeft(left);

        // Right: order summary pane
        root.setRight(buildSummaryPane());

        // Bottom: back button
        Button back = new Button("Back");
        back.setOnAction(e -> PreOrderPage.display());
        HBox bottomBox = new HBox(back);
        bottomBox.setAlignment(Pos.CENTER_LEFT);
        bottomBox.setPadding(new Insets(10, 0, 0, 20));
        root.setBottom(bottomBox);

        Navigation.stage.setScene(new Scene(root, 1000, 800));
        Navigation.stage.setTitle("Old Town – Menu");
    }

    /**
     * Constructs the order summary pane on the right side.
     */
    private static VBox buildSummaryPane() {
        VBox pane = new VBox(10);
        pane.setPrefWidth(300);

        DatePicker dp = new DatePicker();
        ComboBox<String> timeBox = new ComboBox<>(
                FXCollections.observableArrayList("12:00 PM", "12:30 PM", "01:00 PM"));
        timeBox.setValue("12:30 PM");

        ComboBox<String> payBox = new ComboBox<>(
                FXCollections.observableArrayList("Online Banking", "Cash", "Touch ’n Go"));
        payBox.setValue("Online Banking");

        TextArea remarks = new TextArea();
        remarks.setPromptText("Enter any notes…");

        Button place = new Button("Place Order");
        place.setStyle("-fx-background-color:red; -fx-text-fill:white;");
        place.setOnAction(e -> placeOrder(dp, timeBox));

        pane.getChildren().addAll(
                bold("Order Summary", 16),
                orderList,
                subtotalLabel,
                taxLabel,
                totalLabel,
                new Label("Pickup Date:"), dp,
                new Label("Pickup Time:"), timeBox,
                new Label("Payment Method:"), payBox,
                new Label("Remarks:"), remarks,
                place
        );
        return pane;
    }

    /**
     * Queries the menu_items table for this restaurant and builds a list of UI cards.
     */
    private static ObservableList<VBox> loadMenuItems() {
        System.out.println("[DEBUG] loadMenuItems() called");

        ObservableList<VBox> list = FXCollections.observableArrayList();
        final String sql = "SELECT id, item_name, description, price "
                + "FROM menu_items WHERE restaurant_id = ?";

        try (Connection c = DriverManager.getConnection(DatabaseManager.JDBC_URL);
             PreparedStatement ps = c.prepareStatement(sql)) {

            System.out.println("[DEBUG] Using DB file: " +
                    new java.io.File(DatabaseManager.JDBC_URL.replace("jdbc:sqlite:", "")).getAbsolutePath());

            ps.setInt(1, RESTAURANT_ID);
            try (ResultSet rs = ps.executeQuery()) {
                int count = 0;
                while (rs.next()) {
                    count++;
                    int id       = rs.getInt("id");
                    String name  = rs.getString("item_name");
                    String desc  = rs.getString("description");
                    double price = rs.getDouble("price");

                    System.out.println("[INFO] Loaded item: " + name);
                    list.add(menuCard(id, name, desc, price));
                }
                System.out.println("[INFO] Finished query, loaded " + count + " items");
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            System.out.println("[ERROR] Failed to query menu_items");
        }
        return list;
    }

    /**
     * Builds a single menu card with item details and an "Add to Cart" button.
     */
    private static VBox menuCard(int id, String name, String desc, double price) {
        Label nameL  = bold(name, 14);
        Label descL  = new Label(desc);
        descL.setStyle("-fx-text-fill:gray;");
        Label priceL = new Label(String.format("%.2f RM", price));

        Button add = new Button("Add to Cart");
        add.setStyle("-fx-background-color:#007bff; -fx-text-fill:white;");
        add.setOnAction(e -> addItem(id, name, price));

        VBox card = new VBox(5, nameL, descL, new HBox(20, priceL, add));
        card.setPadding(new Insets(10));
        card.setStyle("-fx-background-color:#fff; -fx-border-color:#ccc; -fx-background-radius:10;");
        return card;
    }

    /**
     * Adds one unit of the selected item to the cart and updates the summary.
     */
    private static void addItem(int id, String name, double price) {
        cartMap.merge(id, 1, Integer::sum);

        HBox row = new HBox(10);
        Label lbl = new Label(name + " - " + String.format("%.2f RM", price));
        Button rm  = new Button("Remove");
        rm.setStyle("-fx-background-color:#dc3545; -fx-text-fill:white;");
        rm.setOnAction(e -> {
            orderList.getChildren().remove(row);
            cartMap.compute(id, (k, v) -> v > 1 ? v - 1 : null);
            subtotal -= price;
            refreshTotal();
        });
        row.getChildren().addAll(lbl, rm);
        orderList.getChildren().add(row);

        subtotal += price;
        refreshTotal();
    }

    /**
     * Recalculates and refreshes subtotal, tax, and total labels.
     */
    private static void refreshTotal() {
        double tax = subtotal * 0.06;
        totalLabel.setText(String.format("Total: %.2f RM", subtotal + tax));
        taxLabel.setText(String.format("Tax (6%%): %.2f RM", tax));
        subtotalLabel.setText(String.format("Subtotal: %.2f RM", subtotal));
    }

    /**
     * Places the order by calling OrderManager, then shows the payment success page.
     */
    private static void placeOrder(DatePicker dp, ComboBox<String> timeBox) {
        if (cartMap.isEmpty()) {
            alert("Please add items before placing order.", Alert.AlertType.INFORMATION);
            return;
        }
        try {
            long id = OrderManager.placeOrder(customerId, RESTAURANT_ID, cartMap);

            String date = dp.getValue() == null ? "N/A"
                    : dp.getValue().format(DateTimeFormatter.ISO_DATE);
            double tax = subtotal * 0.06;
            double tot = subtotal + tax;

            // Clear cart after success
            cartMap.clear();
            orderList.getChildren().clear();
            subtotal = 0;
            refreshTotal();

            // Pass customerId so StudentDashboard can reload this user's tickets
            PaymentSuccessfulPage.display(customerId, "Old Town", id, date, timeBox.getValue(), tot);
        } catch (SQLException ex) {
            ex.printStackTrace();
            alert("Database error: " + ex.getMessage(), Alert.AlertType.ERROR);
        }
    }

    /** Helper: create a bold label of given font size. */
    private static Label bold(String txt, int size) {
        Label l = new Label(txt);
        l.setFont(Font.font("Arial", size));
        l.setStyle("-fx-font-weight:bold;");
        return l;
    }

    /** Helper: show an alert dialog. */
    private static void alert(String message, Alert.AlertType type) {
        Alert a = new Alert(type);
        a.setHeaderText(null);
        a.setContentText(message);
        a.showAndWait();
    }
}