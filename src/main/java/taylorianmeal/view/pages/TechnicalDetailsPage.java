package taylorianmeal.view.pages;

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

/**
 * TechnicalDetailsPage
 * --------------------
 * Displays a single technical issue.
 * Uses new Issue API: getId(), getDescription(), getStatus(), setStatus().
 */
public class TechnicalDetailsPage {

    public static void display(Issue issue, ObservableList<Issue> issues) {

        /* === Outer container === */
        VBox detailsBox = new VBox(10);
        detailsBox.setPadding(new Insets(30));
        detailsBox.setStyle("-fx-background-color:#f7f7f7;");

        /* === Title === */
        Label title = new Label("Technical Details");
        title.setFont(Font.font("Arial", 22));
        title.setStyle("-fx-font-weight:bold;");

        /* === White card === */
        VBox card = new VBox(8);
        card.setStyle("-fx-background-color:white; -fx-padding:15; -fx-background-radius:10;");
        card.setMaxWidth(600);

        /* === Basic labels (placeholder fields except ID) === */
        Label techId    = new Label("Technical ID: " + issue.getId());
        Label username  = new Label("Username: Taylorian");
        Label email     = new Label("Customer Email: Taylorian@example.com");
        Label submitted = new Label("Issue Submitted: 25 April 2025, 11:03 AM");
        Label type      = new Label("Issue Type: Loading Problem");

        /* === User feedback === */
        Label userFbTitle   = new Label("User Feedback");
        userFbTitle.setStyle("-fx-font-weight:bold;");
        Label userFbContent = new Label("\"" + issue.getDescription() + "\"");

        VBox userFbBox = new VBox(userFbTitle, userFbContent);
        userFbBox.setPadding(new Insets(10,0,10,0));

        /* === Admin feedback textarea (not yet stored in Issue) === */
        VBox adminBox = new VBox();
        Label adminLbl = new Label("Admin Feedback:");
        adminLbl.setStyle("-fx-font-weight:bold;");
        TextArea feedbackArea = new TextArea();         // no initial value
        feedbackArea.setWrapText(true);
        feedbackArea.setPromptText("Enter your feedback here...");
        feedbackArea.setPrefRowCount(3);
        adminBox.getChildren().addAll(adminLbl, feedbackArea);

        /* === Buttons === */
        Button confirm = new Button("Confirm");
        confirm.setStyle("-fx-background-color:black; -fx-text-fill:white; -fx-font-weight:bold;");
        confirm.setOnAction(e -> {

            // TODO: persist status / admin feedback to DB if needed
            AdminIssueManagementPage.display();
        });

        Button back = new Button("Back");
        back.setStyle("-fx-background-color:red; -fx-text-fill:white; -fx-font-weight:bold;");
        back.setOnAction(e -> AdminIssueManagementPage.display());

        HBox btnRow = new HBox(10, confirm, back);
        btnRow.setAlignment(Pos.CENTER_RIGHT);
        btnRow.setPadding(new Insets(10,0,0,0));

        /* === Assemble card === */
        card.getChildren().addAll(
                techId, username, email, submitted, type,
                new Separator(), userFbBox,
                new Separator(), adminBox,
                btnRow
        );

        detailsBox.getChildren().addAll(title, card);

        /* === Top bar === */
        HBox topBar = new HBox(10);
        topBar.setPadding(new Insets(20,30,10,30));
        topBar.setAlignment(Pos.CENTER_LEFT);
        ImageView logo = new ImageView(new Image(
                AdminSystemSettingsPage.class.getResourceAsStream("/taylorianmeal/taylorianmeal.jpeg")));
        logo.setFitHeight(40);
        logo.setPreserveRatio(true);
        Label appName = new Label("TaylorianMeal");
        appName.setFont(Font.font("Impact", 24));
        appName.setStyle("-fx-text-fill:red; -fx-font-weight:bold;");
        topBar.getChildren().addAll(logo, appName);

        /* === Sidebar === */
        VBox sidebar = AdminUserManagementPage.createSidebar("Issue Management");

        /* === Root layout === */
        BorderPane root = new BorderPane();
        root.setTop(topBar);
        root.setLeft(sidebar);
        root.setCenter(detailsBox);

        /* === Scene === */
        Scene scene = new Scene(root, 900, 600);
        Navigation.stage.setScene(scene);
        Navigation.stage.setTitle("Technical Details");
    }

    private TechnicalDetailsPage() {}
}