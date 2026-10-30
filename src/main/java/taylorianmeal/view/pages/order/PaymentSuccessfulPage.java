package taylorianmeal.view.pages.order;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import taylorianmeal.LoginSession;
import taylorianmeal.view.components.Navigation;
import taylorianmeal.view.pages.dashboard.StudentDashboard;

/**
 * PaymentSuccessfulPage
 * ---------------------
 * Displays confirmation screen after a successful payment.
 *
 * Entry Points:
 *  • display(int customerId, String restaurant, long orderId, String pickupDate,
 *            String pickupTime, double totalPaid)
 *      – shows details and returns to StudentDashboard with the given customerId
 *  • display()
 *      – placeholder overload for legacy calls (uses default values)
 *  • display(String restaurant, long orderId, String pickupDate,
 *            String pickupTime, double totalPaid)
 *      – overload matching OldTownMenuPage call; returns to dashboard without ID
 */
public class PaymentSuccessfulPage {

    /**
     * Shows the payment confirmation page including order details and back navigation.
     *
     * @param customerId  ID of the customer (passed back to dashboard)
     * @param restaurant  Name of the restaurant
     * @param orderId     Generated order ID
     * @param pickupDate  Scheduled pickup date
     * @param pickupTime  Scheduled pickup time
     * @param totalPaid   Total amount paid
     */
    public static void display(int customerId,
                               String restaurant,
                               long orderId,
                               String pickupDate,
                               String pickupTime,
                               double totalPaid) {
        // Outer layout container
        VBox layout = new VBox(20);
        layout.setPadding(new Insets(30));
        layout.setStyle("-fx-background-color:#ffffff;");

        // Top bar with logo and app title
        ImageView logo = new ImageView(
                new Image(PaymentSuccessfulPage.class.getResourceAsStream(
                        "/images/taylorianmeal.jpeg")));
        logo.setFitHeight(40);
        logo.setPreserveRatio(true);

        Label appTitle = new Label("TaylorianMeal");
        appTitle.setFont(Font.font("Arial", 22));
        appTitle.setStyle("-fx-text-fill:red; -fx-font-weight:bold;");

        HBox topBar = new HBox(10, logo, appTitle);
        topBar.setAlignment(Pos.CENTER_LEFT);

        // Success icon and messages
        ImageView icon = new ImageView(
                new Image(PaymentSuccessfulPage.class.getResourceAsStream(
                        "/images/success.png")));
        icon.setFitHeight(80);
        icon.setPreserveRatio(true);

        Label title = new Label("Payment Successful!");
        title.setFont(Font.font("Arial", 28));
        title.setStyle("-fx-font-weight:bold;");

        Label msg = new Label(
                "Thank you for your order. Your payment has been processed successfully.");
        msg.setFont(Font.font(16));
        msg.setWrapText(true);
        msg.setMaxWidth(600);
        msg.setAlignment(Pos.CENTER);

        // Order details grid
        GridPane details = new GridPane();
        details.setAlignment(Pos.CENTER);
        details.setVgap(10);
        details.setHgap(20);
        details.setPadding(new Insets(20));
        details.setStyle(
                "-fx-background-color:#ffffff; " +
                        "-fx-border-color:#cccccc; " +
                        "-fx-border-radius:10; " +
                        "-fx-background-radius:10;");

        details.add(new Label("Restaurant:"), 0, 0);
        details.add(new Label(restaurant), 1, 0);
        details.add(new Label("Order ID:"), 0, 1);
        details.add(new Label(String.valueOf(orderId)), 1, 1);
        details.add(new Label("Pickup Date:"), 0, 2);
        details.add(new Label(pickupDate), 1, 2);
        details.add(new Label("Pickup Time:"), 0, 3);
        details.add(new Label(pickupTime), 1, 3);
        details.add(new Label("Total Paid:"), 0, 4);
        details.add(new Label(String.format("RM %.2f", totalPaid)), 1, 4);

        // Back button returns to StudentDashboard with ID
        Button back = new Button("Back to Dashboard");
        back.setStyle("-fx-background-color:black; -fx-text-fill:white; -fx-font-weight:bold;");
        back.setOnAction(e -> StudentDashboard.display(customerId));

        VBox center = new VBox(20, icon, title, msg, details, back);
        center.setAlignment(Pos.CENTER);

        // Assemble and show scene
        layout.getChildren().addAll(topBar, center);
        Navigation.stage.setScene(new Scene(layout, 700, 550));
        Navigation.stage.setTitle("Payment Successful");
        Navigation.stage.show();
    }

    /**
     * Legacy overload – placeholder defaults for backward compatibility.
     */
    public static void display() {
        display(-1, "Unknown Restaurant", 0L, "N/A", "N/A", 0.0);
    }

    /**
     * Overload matching OldTownMenuPage call – gets customer ID from session.
     */
    public static void display(String restaurant,
                               long orderId,
                               String pickupDate,
                               String pickupTime,
                               double totalPaid) {
        // Get customer ID from session instead of using -1
        int customerId = LoginSession.get().getUserId();
        display(customerId, restaurant, orderId, pickupDate, pickupTime, totalPaid);
    }
}