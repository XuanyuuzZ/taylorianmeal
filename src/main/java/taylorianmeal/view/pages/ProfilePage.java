package taylorianmeal.view.pages;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import taylorianmeal.DatabaseManager;
import taylorianmeal.LoginSession;
import taylorianmeal.service.NotificationManager;
import taylorianmeal.view.components.Navigation;
import taylorianmeal.view.pages.auth.LoginPage;
import taylorianmeal.view.pages.dashboard.StudentDashboard;

/**
 * ProfilePage – view and edit customer profile.
 *
 * • Entry 1: display(int userId)   – pass id explicitly
 * • Entry 2: display()            – wrapper using LoginSession
 */
public class ProfilePage {

    /* ---------------------------------------------------------
     * Public entry-points
     * --------------------------------------------------------- */

    /** Preferred: call with explicit user ID */
    public static void display(int userId) {

        /* 1. Load profile from DB */
        DatabaseManager.UserProfile p;
        try {
            p = DatabaseManager.fetchUserProfile(userId);
            if (p == null) {
                alert("Profile not found (id = " + userId + ")", Alert.AlertType.ERROR);
                return;
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            alert("Database error: " + ex.getMessage(), Alert.AlertType.ERROR);
            return;
        }

        /* 2. Root container */
        BorderPane root = new BorderPane();
        root.setPadding(new Insets(20));
        root.setStyle("-fx-background-color:#f9f9f9;");

        /* ─── Top-bar: logo + brand only ─── */
        HBox top = new HBox(10);
        top.setAlignment(Pos.CENTER_LEFT);

        ImageView logo = new ImageView(
                new Image(ProfilePage.class.getResourceAsStream("/images/taylorianmeal.jpeg")));
        logo.setFitHeight(40);
        logo.setPreserveRatio(true);

        Label brand = new Label("TaylorianMeal");
        brand.setFont(Font.font("Arial", 22));
        brand.setStyle("-fx-text-fill:red; -fx-font-weight:bold;");

        top.getChildren().addAll(logo, brand);

        /* ─── Form title ─── */
        Label title = new Label("Profile");
        title.setFont(Font.font("Arial", 26));
        title.setStyle("-fx-font-weight:bold;");

        /* ─── Details grid ─── */
        GridPane g = new GridPane();
        g.setPadding(new Insets(20));
        g.setVgap(12);
        g.setHgap(10);
        g.setStyle("""
                -fx-background-color:white;
                -fx-border-color:#cccccc;
                -fx-border-radius:10;
                -fx-background-radius:10;
                """);

        ImageView avatar = new ImageView(
                new Image(ProfilePage.class.getResourceAsStream("/images/user.jpg")));
        avatar.setFitWidth(64);
        avatar.setFitHeight(64);

        TextField tfName = new TextField(p.fullName());
        TextField tfUser = new TextField(p.username());   tfUser.setEditable(false);
        TextField tfMail = new TextField(p.email());      tfMail.setEditable(false);
        TextField tfTel  = new TextField(p.telephone());

        g.add(avatar, 0, 0, 1, 4);
        g.add(new Label("Full Name:"), 1, 0); g.add(tfName, 2, 0);
        g.add(new Label("Username:"),  1, 1); g.add(tfUser, 2, 1);
        g.add(new Label("E-mail:"),    1, 2); g.add(tfMail, 2, 2);
        g.add(new Label("Telephone:"), 1, 3); g.add(tfTel,  2, 3);

        /* ─── Buttons (Save + Back) ─── */
        Button btnSave = new Button("Save");
        btnSave.setStyle("-fx-background-color:red; -fx-text-fill:white; -fx-font-weight:bold;");
        btnSave.setOnAction(e -> {
            if (tfName.getText().trim().isEmpty() || tfTel.getText().trim().isEmpty()) {
                alert("Name and Telephone cannot be blank.", Alert.AlertType.WARNING);
                return;
            }
            try {
                DatabaseManager.updateUserProfile(
                        userId,
                        tfName.getText().trim(),
                        tfMail.getText().trim(),
                        tfTel.getText().trim()
                );
                NotificationManager.add(
                        userId,
                        "Profile Updated",
                        "Your profile information has been saved."
                );
                alert("Saved successfully!", Alert.AlertType.INFORMATION);
            } catch (Exception ex) {
                ex.printStackTrace();
                alert("Save failed: " + ex.getMessage(), Alert.AlertType.ERROR);
            }
        });

        Button btnBack = new Button("Back");
        btnBack.setStyle("-fx-background-color:black; -fx-text-fill:white; -fx-font-weight:bold;");
        btnBack.setOnAction(e -> StudentDashboard.display());

        HBox btnRow = new HBox(10, btnSave, btnBack);           // ← side-by-side
        btnRow.setAlignment(Pos.CENTER_RIGHT);
        btnRow.setPadding(new Insets(10, 0, 0, 0));

        VBox center = new VBox(15, title, g, btnRow);

        /* ─── Assemble ─── */
        root.setTop(top);
        root.setCenter(center);

        Scene scene = new Scene(root, 700, 430);
        Navigation.stage.setScene(scene);
        Navigation.stage.setTitle("Profile");
        Navigation.stage.show();
    }

    /** Wrapper that uses LoginSession for Scheme B */
    public static void display() {
        int id = LoginSession.get().getUserId();
        if (id <= 0) {
            alert("Not logged in – please re-login.", Alert.AlertType.ERROR);
            LoginPage.display();
        } else {
            display(id);
        }
    }

    /* Utility: simple alert */
    private static void alert(String msg, Alert.AlertType t) {
        Alert a = new Alert(t);
        a.setHeaderText(null);
        a.setContentText(msg);
        a.showAndWait();
    }
}