package taylorianmeal.view.pages.auth;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import taylorianmeal.DatabaseManager;
import taylorianmeal.service.NotificationManager;
import taylorianmeal.view.components.Navigation;

/**
 * RegisterPage – customer sign-up UI.
 *
 * • Validates required fields & password match
 * • On success:
 *      └ inserts new user   → DatabaseManager.insertUser(...telephone)
 *      └ writes a welcome notification
 *      └ redirects back to LoginPage
 */
public class RegisterPage {

    /** Static entry-point: render the registration UI */
    public static void display() {

        /* ───────── Header: logo + brand ───────── */
        HBox header = new HBox(10);
        header.setAlignment(Pos.CENTER_LEFT);

        // Load logo image from resources/images/taylorianmeal.jpeg
        ImageView logo = new ImageView(
                new Image(RegisterPage.class.getResourceAsStream("/images/taylorianmeal.jpeg")));
        logo.setFitHeight(40);
        logo.setPreserveRatio(true);

        Label brand = new Label("TaylorianMeal");
        brand.setFont(Font.font("Arial", 24));
        brand.setStyle("-fx-text-fill:red; -fx-font-weight:bold;");
        header.getChildren().addAll(logo, brand);

        /* ───────── Input fields ───────── */
        TextField fullName  = textField("Full Name*",  300);
        TextField email     = textField("Email*",      300);
        TextField telephone = textField("Telephone*",  300);   // ★ NEW
        TextField username  = textField("Username*",   300);

        PasswordField pass1 = passwordField("Password*",         300);
        PasswordField pass2 = passwordField("Confirm Password*", 300);

        CheckBox agreeBox   = new CheckBox("I agree with Terms and Conditions");

        /* ───────── Register button ───────── */
        Button btnRegister = new Button("Register");
        btnRegister.setStyle("-fx-background-color:red; -fx-text-fill:white; -fx-font-weight:bold;");
        btnRegister.setOnAction(e -> {

            // 1) basic validation
            if (isBlank(fullName,email,telephone,username,pass1,pass2)) {
                alert("Please fill in all required fields.", Alert.AlertType.WARNING);      return;
            }
            if (!agreeBox.isSelected()) {
                alert("You must agree to the Terms and Conditions.", Alert.AlertType.ERROR); return;
            }
            if (!pass1.getText().equals(pass2.getText())) {
                alert("Passwords do not match!", Alert.AlertType.ERROR);                    return;
            }

            // 2) insert user → DB (new telephone column)
            DatabaseManager.insertUser(
                    fullName.getText().trim(),
                    email.getText().trim(),
                    username.getText().trim(),
                    pass1.getText(),                    // (hash in production)
                    telephone.getText().trim()
            );

            // 3) fetch new user ID for welcome notification
            int newUid = DatabaseManager.fetchUserId(username.getText().trim());

            // 4) write welcome notification
            try {
                NotificationManager.add(
                        newUid,
                        "Welcome to TaylorianMeal!",
                        "Hi " + fullName.getText().trim() +
                                ", thanks for registering. Enjoy pre-ordering 🙂"
                );
            } catch (Exception ex) {
                ex.printStackTrace();   // non-fatal – continue
            }

            // 5) success → back to Login
            alert("Registration successful! Please log in.", Alert.AlertType.INFORMATION);
            LoginPage.display();
        });

        /* ───────── “Already have account?” link ───────── */
        Hyperlink toLogin = new Hyperlink("Already have an account? Login here");
        toLogin.setOnAction(e -> LoginPage.display());

        VBox buttons = new VBox(5, btnRegister, toLogin);
        buttons.setAlignment(Pos.CENTER);

        /* ───────── Assemble UI ───────── */
        VBox root = new VBox(20,
                header,
                label("Full Name*"),      fullName,
                label("Email*"),          email,
                label("Telephone*"),      telephone,          // ★ NEW
                label("Username*"),       username,
                label("Password*"),       pass1,
                label("Confirm Password*"), pass2,
                agreeBox,
                buttons
        );
        root.setPadding(new Insets(25));
        root.setStyle("-fx-background-color:white;");

        /* ───────── Show scene ───────── */
        Scene scene = new Scene(root, 500, 720);
        Navigation.stage.setScene(scene);
        Navigation.stage.setTitle("Register");
        Navigation.stage.show();
    }

    /* =========================================================
     * Helper factory / utilities
     * ========================================================= */
    private static TextField textField(String prompt, double w) {
        TextField tf = new TextField(); tf.setPromptText(prompt); tf.setPrefWidth(w); return tf;
    }
    private static PasswordField passwordField(String prompt, double w) {
        PasswordField pf = new PasswordField(); pf.setPromptText(prompt); pf.setPrefWidth(w); return pf;
    }
    private static Label label(String txt) { return new Label(txt); }

    /** true ⇒ at least one TextField is blank */
    private static boolean isBlank(TextField... fields) {
        for (TextField f : fields) if (f.getText().trim().isEmpty()) return true;
        return false;
    }

    private static void alert(String msg, Alert.AlertType type) {
        Alert a = new Alert(type); a.setHeaderText(null); a.setContentText(msg); a.showAndWait();
    }
}