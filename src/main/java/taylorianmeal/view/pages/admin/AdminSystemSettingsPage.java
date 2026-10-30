package taylorianmeal.view.pages.admin;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import taylorianmeal.DatabaseManager;
import taylorianmeal.view.components.Navigation;
import taylorianmeal.view.pages.auth.LoginPage;
import taylorianmeal.view.pages.dashboard.AdminDashboard;

import java.sql.SQLException;

import static taylorianmeal.view.pages.admin.AdminUserManagementPage.createSidebarButton;

public class AdminSystemSettingsPage {

    /**
     * Display the System Settings screen.
     * Window size: 900×500. Sidebar identical to other admin pages.
     */
    public static void display() {
        BorderPane root = new BorderPane();
        root.setStyle("-fx-background-color: white;");

        // === Top Bar ===
        HBox topBar = new HBox(10);
        topBar.setPadding(new Insets(15, 25, 8, 25));
        topBar.setAlignment(Pos.CENTER_LEFT);
        topBar.setStyle("-fx-background-color: #f8f9fa; -fx-border-color: #e9ecef; -fx-border-width: 0 0 1 0;");

        ImageView logo = new ImageView(
                new Image(AdminSystemSettingsPage.class.getResourceAsStream("/images/taylorianmeal.jpeg"))
        );
        logo.setFitHeight(32);
        logo.setPreserveRatio(true);

        Label appName = new Label("TaylorianMeal");
        appName.setFont(Font.font("Arial", 20));
        appName.setStyle("-fx-text-fill: red; -fx-font-weight: bold;");
        topBar.getChildren().addAll(logo, appName);
        root.setTop(topBar);

        // === Sidebar ===
        VBox sidebar = new VBox(15);
        sidebar.setPadding(new Insets(20, 10, 10, 20));
        sidebar.setPrefWidth(170);
        sidebar.setStyle("-fx-background-color: #f8f9fa; -fx-border-color: #e9ecef; -fx-border-width: 0 1 0 0;");

        Label sideTitle = new Label("Admin Panel");
        sideTitle.setFont(Font.font("Arial", 16));
        sideTitle.setStyle("-fx-font-weight: bold; -fx-text-fill: #333;");

        Button userManagementBtn = createSidebarButton("User Management", false);
        userManagementBtn.setOnAction(e -> AdminUserManagementPage.display());

        Button issueManagementBtn = createSidebarButton("Issue Management", false);
        issueManagementBtn.setOnAction(e -> AdminIssueManagementPage.display());

        Button settingsBtn = createSidebarButton("System Settings", true);
        settingsBtn.setOnAction(e -> AdminSystemSettingsPage.display());

        Button backToDashboardBtn = createSidebarButton("Back to Dashboard", false);
        backToDashboardBtn.setOnAction(e -> AdminDashboard.display());
        backToDashboardBtn.setStyle("-fx-background-color: #6c757d; -fx-text-fill: white; -fx-font-weight: bold; -fx-padding: 8 12;");

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

        sidebar.getChildren().addAll(
                sideTitle,
                userManagementBtn,
                issueManagementBtn,
                settingsBtn,
                backToDashboardBtn,
                spacer,
                logoutBox
        );
        root.setLeft(sidebar);

        // === Center Content ===
        VBox content = new VBox(20);
        content.setPadding(new Insets(30));

        Label settingsTitle = new Label("System Settings");
        settingsTitle.setFont(Font.font("Arial", FontWeight.BOLD, 20));
        content.getChildren().add(settingsTitle);

        // Platform Notice
        Label noticeLabel = new Label("Platform Notice");
        noticeLabel.setFont(Font.font("Arial", FontWeight.BOLD, 18));

        TextArea noticeArea = new TextArea(
                "TaylorianMeal will undergo maintenance from 12 AM – 2 AM on Saturday."
        );
        noticeArea.setWrapText(true);
        noticeArea.setPrefHeight(100);

        Button saveBtn = new Button("Save");
        saveBtn.setStyle("-fx-background-color: red; -fx-text-fill: white; -fx-font-weight: bold;");
        saveBtn.setOnAction(e -> {
            String notice = noticeArea.getText().trim();
            if (notice.isEmpty()) {
                new Alert(Alert.AlertType.WARNING, "Notice cannot be empty").showAndWait();
            } else {
                try {
                    DatabaseManager.broadcastPlatformNotice(notice);
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
                new Alert(Alert.AlertType.INFORMATION, "Notice sent to all users").showAndWait();
            }
        });

        // Support Info
        Label supportLabel = new Label("Support Info");
        supportLabel.setFont(Font.font("Arial", FontWeight.BOLD, 18));
        Label emailLabel   = new Label("E-mail: support@taylorianmeal.com");
        Label phoneLabel   = new Label("Phone: 03-1234567");

        // Maintenance Mode Status
        Label maintenanceLabel = new Label("Maintenance Mode");
        maintenanceLabel.setFont(Font.font("Arial", FontWeight.BOLD, 18));
        Label statusLabel      = new Label("System is Live");
        statusLabel.setStyle("-fx-text-fill: green; -fx-font-weight: bold;");

        content.getChildren().addAll(
                noticeLabel, noticeArea, saveBtn,
                supportLabel, emailLabel, phoneLabel,
                maintenanceLabel, statusLabel
        );
        root.setCenter(content);

        // === Scene & Stage ===
        Scene scene = new Scene(root, 900, 500);
        Navigation.stage.setScene(scene);
        Navigation.stage.setTitle("System Settings - TaylorianMeal");
        Navigation.stage.show();
    }
}