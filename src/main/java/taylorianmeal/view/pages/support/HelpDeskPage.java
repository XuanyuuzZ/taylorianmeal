package taylorianmeal.view.pages.support;

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
import taylorianmeal.view.pages.ReportOrderIssuePage;
import taylorianmeal.view.pages.ReportTechnicalProblemPage;
import taylorianmeal.view.pages.dashboard.StudentDashboard;

public class HelpDeskPage {

    public static void display() {
        VBox root = new VBox(20);
        root.setPadding(new Insets(30));
        root.setAlignment(Pos.TOP_CENTER);
        root.setStyle("-fx-background-color: white;");

        // === Top Bar ===
        HBox topBar = new HBox(10);
        topBar.setAlignment(Pos.CENTER_LEFT);

        ImageView logo = new ImageView(new Image(HelpDeskPage.class.getResourceAsStream("/images/taylorianmeal.jpeg")));
        logo.setFitHeight(40);
        logo.setPreserveRatio(true);

        Label title = new Label("TaylorianMeal");
        title.setFont(Font.font("Arial", 20));
        title.setStyle("-fx-text-fill: red; -fx-font-weight: bold;");

        topBar.getChildren().addAll(logo, title);

        // === HelpDesk Title ===
        Label helpTitle = new Label("HelpDesk");
        helpTitle.setFont(Font.font("Arial", 28));
        helpTitle.setStyle("-fx-font-weight: bold;");

        // === Buttons ===
        HBox buttonRow = new HBox(30);
        buttonRow.setAlignment(Pos.CENTER);

        Button orderIssues = new Button("Order Issues");
        styleRedButton(orderIssues);
        orderIssues.setOnAction(e -> ReportOrderIssuePage.display());

        Button techIssues = new Button("Technical Issues");
        styleRedButton(techIssues);
        techIssues.setOnAction(e -> ReportTechnicalProblemPage.display());

        buttonRow.getChildren().addAll(orderIssues, techIssues);

        // === Back Button ===
        Button backBtn = new Button("Back");
        backBtn.setStyle("-fx-background-color: black; -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 12;");
        backBtn.setOnAction(e -> StudentDashboard.display()); // Change this if not using StudentDashboard

        root.getChildren().addAll(topBar, helpTitle, buttonRow, backBtn);

        Scene scene = new Scene(root, 700, 500);
        Navigation.stage.setScene(scene);
        Navigation.stage.setTitle("HelpDesk");
        Navigation.stage.show();
    }

    private static void styleRedButton(Button btn) {
        String normalStyle = "-fx-background-color: #e63946; " +
                "-fx-text-fill: white; " +
                "-fx-font-weight: bold; " +
                "-fx-background-radius: 20; " +
                "-fx-font-size: 16px;";
        String hoverStyle = "-fx-background-color: #d62828; " + // slightly darker on hover
                "-fx-text-fill: white; " +
                "-fx-font-weight: bold; " +
                "-fx-background-radius: 20; " +
                "-fx-font-size: 16px;"; // same font size

        btn.setStyle(normalStyle);
        btn.setPrefSize(180, 120);
        btn.setFont(Font.font("Arial", 16));

        // Manual hover behavior with no font size change
        btn.setOnMouseEntered(e -> btn.setStyle(hoverStyle));
        btn.setOnMouseExited(e -> btn.setStyle(normalStyle));
    }

}
