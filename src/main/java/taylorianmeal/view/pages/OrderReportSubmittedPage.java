package taylorianmeal.view.pages;

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
import taylorianmeal.view.components.Navigation;
import taylorianmeal.view.pages.dashboard.StudentDashboard;

public class OrderReportSubmittedPage {

    public static void display() {
        VBox root = new VBox(30);
        root.setPadding(new Insets(30));
        root.setStyle("-fx-background-color: #f8f8f8;");

        // === Top Header: Logo + App Name ===
        HBox topBar = new HBox(10);
        topBar.setAlignment(Pos.CENTER_LEFT);

        ImageView logo = new ImageView(new Image(OrderReportSubmittedPage.class.getResourceAsStream("/taylorianmeal/taylorianmeal.jpeg")));
        logo.setFitHeight(40);
        logo.setPreserveRatio(true);

        Label appTitle = new Label("TaylorianMeal");
        appTitle.setFont(new Font("Arial", 22));
        appTitle.setStyle("-fx-text-fill: red; -fx-font-weight: bold;");

        topBar.getChildren().addAll(logo, appTitle);

        // === Success Icon ===
        ImageView successIcon = new ImageView(new Image(OrderReportSubmittedPage.class.getResourceAsStream("/taylorianmeal/success.png")));
        successIcon.setFitHeight(80);
        successIcon.setPreserveRatio(true);

        // === Center Message ===
        Label title = new Label("Report Submitted Successfully!");
        title.setFont(new Font("Arial", 26));
        title.setStyle("-fx-font-weight: bold;");
        title.setAlignment(Pos.CENTER);

        Label message = new Label("Thank you for reporting the issue. The merchant will review your report shortly.");
        message.setFont(new Font(16));
        message.setWrapText(true);
        message.setAlignment(Pos.CENTER);
        message.setMaxWidth(600);

        VBox centerBox = new VBox(15, successIcon, title, message);
        centerBox.setAlignment(Pos.CENTER);
        centerBox.setPadding(new Insets(20, 0, 0, 0));

        // === Bottom Back Button (Center Aligned) ===
        Button backBtn = new Button("Back to Dashboard");
        backBtn.setStyle("-fx-background-color: black; -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 10;");
        backBtn.setOnAction(e -> StudentDashboard.display());

        HBox bottomBar = new HBox(backBtn);
        bottomBar.setAlignment(Pos.CENTER);
        bottomBar.setPadding(new Insets(20, 0, 0, 0));

        // === Final Layout ===
        root.getChildren().addAll(topBar, centerBox, bottomBar);

        Scene scene = new Scene(root, 650, 450);
        Navigation.stage.setScene(scene);
        Navigation.stage.setTitle("Report Submitted");
        Navigation.stage.show();
    }
}
