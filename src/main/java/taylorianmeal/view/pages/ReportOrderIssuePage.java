package taylorianmeal.view.pages;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import taylorianmeal.view.components.Navigation;
import taylorianmeal.view.pages.order.OrderStatusHistoryPage;
import taylorianmeal.view.pages.support.HelpDeskPage;

public class ReportOrderIssuePage {

    public static void display() {
        VBox root = new VBox(30);
        root.setPadding(new Insets(30));
        root.setAlignment(Pos.CENTER);
        root.setStyle("-fx-background-color: white;");

        // === Top Bar with Logo ===
        HBox topBar = new HBox(10);
        topBar.setAlignment(Pos.CENTER_LEFT);
        topBar.setPadding(new Insets(0, 0, 10, 10));

        ImageView logo = new ImageView(new Image(ReportOrderIssuePage.class.getResourceAsStream("/images/taylorianmeal.jpeg")));
        logo.setFitHeight(40);
        logo.setPreserveRatio(true);

        Label appTitle = new Label("TaylorianMeal");
        appTitle.setFont(Font.font("Arial", 20));
        appTitle.setStyle("-fx-text-fill: red; -fx-font-weight: bold;");

        topBar.getChildren().addAll(logo, appTitle);

        // === Center content with question icon and message ===
        HBox content = new HBox(10);
        content.setAlignment(Pos.CENTER);

        ImageView questionIcon = new ImageView(new Image(ReportOrderIssuePage.class.getResourceAsStream("/images/question.png"))); // Use your own icon path
        questionIcon.setFitWidth(32);
        questionIcon.setFitHeight(32);

        Label msg = new Label("Already encountered an issue? Go to Order History to report your problem. ");
        msg.setFont(Font.font("Arial", 14));

        Hyperlink link = new Hyperlink("Click here");
        link.setFont(Font.font("Arial", 14));
        link.setOnAction(e -> {
            OrderStatusHistoryPage.display();
        });

        HBox textRow = new HBox(5, msg, link);
        textRow.setAlignment(Pos.CENTER);

        content.getChildren().addAll(questionIcon, textRow);

        // === Back Button ===
        Button backBtn = new Button("Back");
        backBtn.setStyle("-fx-background-color: black; -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 12;");
        backBtn.setOnAction(e -> HelpDeskPage.display());

        root.getChildren().addAll(topBar, content, backBtn);

        Scene scene = new Scene(root, 700, 400);
        Navigation.stage.setScene(scene);
        Navigation.stage.setTitle("HelpDesk - Order Issue");
        Navigation.stage.show();
    }
}
