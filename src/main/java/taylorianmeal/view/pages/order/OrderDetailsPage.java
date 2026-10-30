package taylorianmeal.view.pages.order;

import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Separator;
import javafx.scene.control.TextArea;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import taylorianmeal.model.Issue;
import taylorianmeal.view.components.Navigation;
import taylorianmeal.view.pages.admin.AdminIssueManagementPage;
import taylorianmeal.view.pages.admin.AdminSystemSettingsPage;
import taylorianmeal.view.pages.admin.AdminUserManagementPage;

public class OrderDetailsPage {

    public static void display(Issue issue, ObservableList<Issue> issues) {
        // === Outer container with background ===
        VBox detailsBox = new VBox(10);
        detailsBox.setPadding(new Insets(30));
        detailsBox.setStyle("-fx-background-color: #f7f7f7;");

        // === Title ===
        Label title = new Label("Order Details");
        title.setFont(new Font("Arial", 22));
        title.setStyle("-fx-font-weight: bold;");

        // === White card-style container ===
        VBox details = new VBox(8);
        details.setStyle("-fx-background-color: white; -fx-padding: 15; -fx-background-radius: 10;");
        details.setMaxWidth(600);

        // === Basic order info ===
        Label idLabel = new Label("Order ID: " + issue.getId());
        Label titleLabel = new Label("Title: " + issue.getTitle());
        Label roleLabel = new Label("Role: " + issue.getRole());
        Label reporterLabel = new Label("Reporter: " + issue.getReporter());
        Label statusLabel = new Label("Status: " + issue.getStatus());
        Label dateLabel = new Label("Submitted: " + issue.getDate());
        // === User feedback section ===
        Label userFeedbackTitle = new Label("User Feedback");
        userFeedbackTitle.setStyle("-fx-font-weight: bold;");
        Label userFeedbackContent = new Label("\"" + issue.getDescription() + "\"");
        VBox userFeedbackBox = new VBox(userFeedbackTitle, userFeedbackContent);
        userFeedbackBox.setPadding(new Insets(10, 0, 10, 0));

        // === Admin feedback section (optional, not saved) ===
        VBox adminFeedbackBox = new VBox();
        Label adminLabel = new Label("Admin Feedback:");
        adminLabel.setFont(Font.font("Arial", 14));
        adminLabel.setStyle("-fx-font-weight: bold;");
        TextArea feedbackArea = new TextArea();
        feedbackArea.setWrapText(true);
        feedbackArea.setPromptText("Enter admin feedback...");
        feedbackArea.setPrefRowCount(3);
        adminFeedbackBox.getChildren().addAll(adminLabel, feedbackArea);

        // === Buttons: Approve and Decline ===
        Button approveBtn = new Button("Approve Refund");
        approveBtn.setStyle("-fx-background-color: black; -fx-text-fill: white; -fx-font-weight: bold;");
        approveBtn.setOnAction(e -> {
            // DatabaseManager.updateIssueStatus(issue.getId(), "Refund Approved");
            AdminIssueManagementPage.display();
        });

        Button declineBtn = new Button("Decline Refund");
        declineBtn.setStyle("-fx-background-color: red; -fx-text-fill: white; -fx-font-weight: bold;");
        declineBtn.setOnAction(e -> {
            // DatabaseManager.updateIssueStatus(issue.getId(), "Refund Rejected");
            AdminIssueManagementPage.display();
        });

        HBox buttonRow = new HBox(10, approveBtn, declineBtn);
        buttonRow.setAlignment(Pos.CENTER_RIGHT);
        buttonRow.setPadding(new Insets(10, 0, 0, 0));
        // === Assemble details box ===
        details.getChildren().addAll(
                idLabel, titleLabel, roleLabel, reporterLabel, statusLabel, dateLabel,
                new Separator(), userFeedbackBox,
                new Separator(), adminFeedbackBox,
                buttonRow
        );

        detailsBox.getChildren().addAll(title, details);

        // === Top Bar ===
        HBox topBar = new HBox(10);
        topBar.setPadding(new Insets(20, 30, 10, 30));
        topBar.setAlignment(Pos.CENTER_LEFT);
        ImageView logo = new ImageView(new Image(AdminSystemSettingsPage.class.getResourceAsStream("/taylorianmeal/taylorianmeal.jpeg")));
        logo.setFitHeight(40);
        logo.setPreserveRatio(true);
        Label appName = new Label("TaylorianMeal");
        appName.setFont(new Font("Impact", 24));
        appName.setStyle("-fx-text-fill: red; -fx-font-weight: bold;");
        topBar.getChildren().addAll(logo, appName);

        // === Sidebar ===
        VBox sidebar = AdminUserManagementPage.createSidebar("Issue Management");

        // === Root Layout ===
        BorderPane root = new BorderPane();
        root.setTop(topBar);
        root.setLeft(sidebar);
        root.setCenter(detailsBox);

        // === Final Scene Setup ===
        Scene scene = new Scene(root, 900, 600);
        Navigation.stage.setScene(scene);
        Navigation.stage.setTitle("Order Details");
    }
}
