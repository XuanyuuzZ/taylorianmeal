package taylorianmeal.view.pages.order;

import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import taylorianmeal.LoginSession;
import taylorianmeal.service.OrderManager;
import taylorianmeal.view.components.Navigation;
import taylorianmeal.view.pages.ReportOrderProblemPage;
import taylorianmeal.view.pages.auth.LoginPage;
import taylorianmeal.view.pages.dashboard.StudentDashboard;

/**
 * OrderStatusHistoryPage
 * ----------------------
 * • Lists every order that belongs to the logged-in customer.
 * • Each white “card” includes a red “Report Problem” button that opens
 *   ReportOrderProblemPage for that specific order.
 *
 * Entry points
 * ------------
 * 1) display(int customerId)   – preferred (caller passes id explicitly)
 * 2) display()                 – wrapper that gets id from LoginSession
 */
public final class OrderStatusHistoryPage {

    /* =========================================================
     *  Public entry-points
     * ========================================================= */

    /** Preferred call – customer ID is supplied by the caller. */
    public static void display(int customerId) {

        /* 1 — query database for this customer’s orders */
        ObservableList<OrderManager.OrderRecord> rows;
        try {
            rows = OrderManager.listOrdersForCustomer(customerId);
        } catch (Exception ex) {
            ex.printStackTrace();
            alert("Database error: " + ex.getMessage(), Alert.AlertType.ERROR);
            return;
        }

        /* 2 — build UI */
        VBox root = new VBox(25);
        root.setPadding(new Insets(30));
        root.setStyle("-fx-background-color:#f9f9f9;");

        // ── top-bar ──
        HBox topBar = new HBox(10);
        topBar.setAlignment(Pos.CENTER_LEFT);
        ImageView logo = new ImageView(
                new Image(OrderStatusHistoryPage.class.getResourceAsStream("/images/taylorianmeal.jpeg")));
        logo.setFitHeight(40);
        logo.setPreserveRatio(true);

        Label brand = new Label("TaylorianMeal");
        brand.setFont(Font.font("Arial", 22));
        brand.setStyle("-fx-text-fill:red; -fx-font-weight:bold;");
        topBar.getChildren().addAll(logo, brand);

        // ── page title ──
        Label title = new Label("Order Status & History List");
        title.setFont(Font.font("Arial", 26));
        title.setStyle("-fx-font-weight:bold;");

        // ── order list ──
        VBox cardBox = new VBox(18);
        if (rows.isEmpty()) {
            Label empty = new Label("No orders yet.");
            empty.setStyle("-fx-text-fill:gray;");
            cardBox.getChildren().add(empty);
        } else {
            for (OrderManager.OrderRecord r : rows) {
                cardBox.getChildren().add(orderCard(customerId, r));
            }
        }
        ScrollPane scroller = new ScrollPane(cardBox);
        scroller.setFitToWidth(true);
        scroller.setStyle("-fx-background-color:transparent;");

        // ── back button ──
        Button back = new Button("Back");
        back.setStyle("-fx-background-color:black; -fx-text-fill:white; -fx-font-weight:bold;");
        back.setOnAction(e -> StudentDashboard.display(customerId));

        HBox backRow = new HBox(back);
        backRow.setAlignment(Pos.CENTER_RIGHT);
        backRow.setPadding(new Insets(10, 0, 0, 0));

        root.getChildren().addAll(topBar, title, scroller, backRow);

        Scene scene = new Scene(root, 780, 600);
        Navigation.stage.setScene(scene);
        Navigation.stage.setTitle("Order Status & History");
        Navigation.stage.show();
    }

    /** Convenience wrapper – gets customer id from LoginSession. */
    public static void display() {
        int uid = LoginSession.get().getUserId();
        if (uid <= 0) {
            alert("Not logged in – please log in again.", Alert.AlertType.ERROR);
            LoginPage.display();
        } else {
            display(uid);
        }
    }

    /* =========================================================
     *  Private helpers
     * ========================================================= */

    /** Builds a single white “order card”. */
    private static VBox orderCard(int customerId, OrderManager.OrderRecord r) {

        VBox card = new VBox(6);
        card.setPadding(new Insets(15));
        card.setStyle("""
            -fx-background-color:white;
            -fx-border-color:#cccccc;
            -fx-border-radius:10;
            -fx-background-radius:10;
        """);

        card.getChildren().addAll(
                keyValue("Order ID:",     r.getOrderId()),
                keyValue("Restaurant:",   r.getRestaurant()),
                keyValue("Date:",         r.getDate()),
                keyValue("Status:",       r.getStatus())
        );

        /* “Report problem” button -> open the problem page */
        Button report = new Button("Report Problem");
        report.setStyle("-fx-background-color:red; -fx-text-fill:white; -fx-font-weight:bold;");

        long numericOrderId = Long.parseLong(r.getOrderId().substring(2)); // strip “TM”
        report.setOnAction(e ->
                ReportOrderProblemPage.display(customerId, numericOrderId)
        );

        card.getChildren().add(report);
        return card;
    }

    /** Small helper that prints “key : value” on one line. */
    private static HBox keyValue(String key, String value) {
        Label k = new Label(key); k.setStyle("-fx-font-weight:bold;");
        Label v = new Label(value);
        return new HBox(8, k, v);
    }

    private static void alert(String msg, Alert.AlertType type) {
        Alert a = new Alert(type); a.setHeaderText(null); a.setContentText(msg); a.showAndWait();
    }

    private OrderStatusHistoryPage() {}  // no instances
}