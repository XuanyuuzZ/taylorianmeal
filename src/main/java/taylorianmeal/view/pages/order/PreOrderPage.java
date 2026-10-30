package taylorianmeal.view.pages.order;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import taylorianmeal.LoginSession;
import taylorianmeal.view.components.Navigation;
import taylorianmeal.view.pages.OldTownMenuPage;
import taylorianmeal.view.pages.dashboard.StudentDashboard;

/**
 * PreOrderPage – displays a single restaurant card for Old Town White Coffee
 * and allows the user to start an order.
 *
 * Layout:
 *  1. Header with application logo and brand name.
 *  2. Title and subtitle describing the restaurant.
 *  3. A single restaurant card showing name, location, hours, and a button
 *     to proceed to the menu.
 *  4. A Back button to return to the student dashboard.
 */
public class PreOrderPage {

    /** Prevent instantiation */
    private PreOrderPage() {}

    /**
     * Display the Pre-Order page for Old Town White Coffee.
     * Assumes that restaurant_id = 1 in the database corresponds to Old Town.
     */
    public static void display() {
        // Root container with vertical spacing
        VBox root = new VBox(20);
        root.setPadding(new Insets(20));
        root.setStyle("-fx-background-color:#f8f8f8;");

        // Header: logo image + brand label
        HBox header = new HBox(10);
        header.setAlignment(Pos.CENTER_LEFT);
        ImageView logo = new ImageView(
                new Image(PreOrderPage.class.getResourceAsStream(
                        "/images/taylorianmeal.jpeg")));
        logo.setFitHeight(40);
        logo.setPreserveRatio(true);
        Label brand = new Label("TaylorianMeal");
        brand.setFont(Font.font("Arial", 24));
        brand.setStyle("-fx-text-fill:red; -fx-font-weight:bold;");
        header.getChildren().addAll(logo, brand);

        // Page title and subtitle
        Label title = new Label("Pre-order: Old Town White Coffee");
        title.setFont(Font.font("Arial", 22));
        Label subtitle = new Label("Enjoy authentic Malaysian coffee & snacks");
        subtitle.setStyle("-fx-font-size:14px;");

        // Restaurant card container
        VBox card = new VBox(8);
        card.setPrefSize(350, 150);
        card.setPadding(new Insets(10));
        card.setStyle(
                "-fx-background-color:#ffffff;" +
                        "-fx-border-color:#ddd;" +
                        "-fx-border-radius:10;" +
                        "-fx-background-radius:10;"
        );

        // Card header: restaurant image + name
        ImageView shopImg = new ImageView(
                new Image(PreOrderPage.class.getResourceAsStream(
                        "/images/oldtown.png"), 50, 50, true, true));
        Label nameLabel = new Label("Old Town White Coffee");
        nameLabel.setStyle("-fx-font-weight:bold; -fx-font-size:16px;");
        HBox cardHeader = new HBox(10, shopImg, nameLabel);
        cardHeader.setAlignment(Pos.CENTER_LEFT);

        // Location and operating hours
        Label locationLabel = new Label("Location: G-4");
        Label hoursLabel    = new Label("Hours: Mon–Sat 7:30 AM–9:00 PM");

        // Pre-Order button navigates to OldTownMenuPage with restaurant_id = 1
        Button preOrderBtn = new Button("Pre-Order Now");
        preOrderBtn.setStyle(
                "-fx-background-color:red;" +
                        "-fx-text-fill:white;" +
                        "-fx-font-weight:bold;");
        preOrderBtn.setOnAction(e -> OldTownMenuPage.display(LoginSession.get().getUserId()));

        // Assemble card
        card.getChildren().addAll(
                cardHeader,
                locationLabel,
                hoursLabel,
                preOrderBtn
        );

        // Back button to return to the student dashboard
        Button backBtn = new Button("Back");
        backBtn.setStyle(
                "-fx-background-color:black;" +
                        "-fx-text-fill:white;" +
                        "-fx-font-weight:bold;");
        backBtn.setOnAction(e -> StudentDashboard.display());
        HBox backBox = new HBox(backBtn);
        backBox.setAlignment(Pos.CENTER_RIGHT);
        backBox.setPadding(new Insets(10, 0, 0, 0));

        // Add all elements to the root
        root.getChildren().addAll(
                header,
                title,
                subtitle,
                card,
                backBox
        );

        // Show the scene
        Scene scene = new Scene(root, 1000, 600);
        Navigation.stage.setScene(scene);
        Navigation.stage.setTitle("Pre-Order Page");
        Navigation.stage.show();
    }
}
