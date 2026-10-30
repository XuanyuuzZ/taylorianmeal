package taylorianmeal.view.pages.dashboard;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import taylorianmeal.view.components.Navigation;
import taylorianmeal.view.pages.admin.AdminIssueManagementPage;
import taylorianmeal.view.pages.admin.AdminSystemSettingsPage;
import taylorianmeal.view.pages.admin.AdminUserManagementPage;
import taylorianmeal.view.pages.auth.LoginPage;

import java.io.InputStream;

/**
 * AdminDashboard
 * --------------
 * Main landing page for Admin users.
 */
public class AdminDashboard {

    /**
     * Display the Admin dashboard.
     */
    public static void display() {
        BorderPane root = new BorderPane();
        root.setStyle("-fx-background-color: white;");

        // Top Bar
        HBox topBar = new HBox(10);
        topBar.setPadding(new Insets(15, 25, 8, 25));
        topBar.setAlignment(Pos.CENTER_LEFT);
        topBar.setStyle("-fx-background-color: #f8f9fa; -fx-border-color: #e9ecef; -fx-border-width: 0 0 1 0;");

        ImageView logo = new ImageView(
                new Image(
                        AdminDashboard.class.getResourceAsStream(
                                "/images/taylorianmeal.jpeg"
                        )
                )
        );
        logo.setFitHeight(32);
        logo.setPreserveRatio(true);

        Label appName = new Label("TaylorianMeal");
        appName.setFont(Font.font("Arial", 20));
        appName.setStyle("-fx-text-fill: red; -fx-font-weight: bold;");

        topBar.getChildren().addAll(logo, appName);
        root.setTop(topBar);

        // Sidebar
        VBox sidebar = new VBox(15);
        sidebar.setPadding(new Insets(20, 10, 10, 20));
        sidebar.setPrefWidth(170);
        sidebar.setStyle("-fx-background-color: #f8f9fa; -fx-border-color: #e9ecef; -fx-border-width: 0 1 0 0;");

        Label sideTitle = new Label("Admin Dashboard");
        sideTitle.setFont(Font.font("Arial", 16));
        sideTitle.setStyle("-fx-font-weight: bold; -fx-text-fill: #333;");

        // Spacer to push logout to bottom
        Region spacer = new Region();
        VBox.setVgrow(spacer, Priority.ALWAYS);

        Button logoutBtn = new Button("Logout");
        logoutBtn.setPrefWidth(90);
        logoutBtn.setFont(Font.font("Arial", 12));
        logoutBtn.setStyle("-fx-background-color: red; -fx-text-fill: white; -fx-font-weight: bold;");
        logoutBtn.setOnAction(e -> LoginPage.display());

        HBox logoutBox = new HBox(logoutBtn);
        logoutBox.setAlignment(Pos.CENTER);
        logoutBox.setPadding(new Insets(10, 0, 20, 0));

        sidebar.getChildren().addAll(sideTitle, spacer, logoutBox);
        root.setLeft(sidebar);

        // Main Button Panel
        VBox buttonPanel = new VBox(20);
        buttonPanel.setPadding(new Insets(40));
        buttonPanel.setAlignment(Pos.TOP_LEFT);

        buttonPanel.getChildren().addAll(
                createDashboardButton("User Management", "Adding admins and agents", "/images/user.png"),
                createDashboardButton("Issue Management", "Handle customer order & technical reports", "/images/issue.png"),
                createDashboardButton("System Settings", "Configure system parameters", "/images/settings.png")
        );

        root.setCenter(buttonPanel);

        Scene scene = new Scene(root, 900, 500);
        Navigation.stage.setScene(scene);
        Navigation.stage.setTitle("Admin Dashboard - TaylorianMeal");
        Navigation.stage.show();
    }

    /**
     * Helper to create a dashboard button with icon and text, adding click handler.
     */
    private static HBox createDashboardButton(String title, String subtitle, String iconPath) {
        HBox box = new HBox(15);
        box.setPadding(new Insets(15));
        box.setAlignment(Pos.CENTER_LEFT);
        box.setStyle(
                "-fx-background-color: white; -fx-border-radius: 10; -fx-background-radius: 10; -fx-border-color: #e0e0e0;"
        );
        box.setMinHeight(80);

        // Load icon resource
        InputStream is = AdminDashboard.class.getResourceAsStream(iconPath);
        System.out.println("Loading icon: " + iconPath + " -> " + (is != null));
        ImageView icon;
        if (is != null) {
            icon = new ImageView(new Image(is));
            icon.setFitWidth(32);
            icon.setFitHeight(32);
        } else {
            icon = new ImageView(); // placeholder
        }

        VBox textBox = new VBox(5);
        Label titleLabel = new Label(title);
        titleLabel.setFont(Font.font("Arial", 16));
        titleLabel.setStyle("-fx-font-weight: bold; -fx-text-fill: #333;");
        Label subtitleLabel = new Label(subtitle);
        subtitleLabel.setFont(Font.font("Arial", 12));
        subtitleLabel.setStyle("-fx-text-fill: #666;");
        textBox.getChildren().addAll(titleLabel, subtitleLabel);

        box.getChildren().addAll(icon, textBox);

        box.setOnMouseClicked(e -> {
            if (title.equals("User Management")) {
                AdminUserManagementPage.display();
            } else if (title.equals("Issue Management")) {
                AdminIssueManagementPage.display();
            } else if (title.equals("System Settings")) {
                AdminSystemSettingsPage.display();
            }
        });

        return box;
    }

    private AdminDashboard() {}
}