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
import taylorianmeal.LoginSession;
import taylorianmeal.view.components.Navigation;
import taylorianmeal.view.pages.dashboard.AdminDashboard;
import taylorianmeal.view.pages.dashboard.MerchantDashboard;
import taylorianmeal.view.pages.dashboard.StudentDashboard;

/**
 * LoginPage
 * ---------
 * A universal login interface that supports three roles:
 *
 *   • Customer  → StudentDashboard.display()
 *   • Merchant  → MerchantDashboard.display()
 *   • Admin     → AdminDashboard.display()
 *
 * Flow:
 *   1. Validate that username / password are not empty
 *   2. Call DatabaseManager.authenticateUser(username, password)
 *      – returns the stored role or null if credentials are wrong
 *   3. Ensure the user’s stored role matches the role chosen
 *      in the ComboBox (prevents “login as the wrong role”)
 *   4. Retrieve the user’s numeric ID with DatabaseManager.fetchUserId()
 *   5. Store that ID in the LoginSession singleton
 *   6. Redirect to the correct dashboard
 */
public final class LoginPage {

    /* =========================================================
     * Public entry-point – call this to open the login window
     * ========================================================= */
    public static void display() {

        /* ---------- Header: brand logo ---------- */
        ImageView logo = new ImageView(
                new Image(LoginPage.class.getResourceAsStream("/images/taylorianmeal.jpeg")));
        logo.setFitHeight(40);
        logo.setPreserveRatio(true);

        Label brand = new Label("TaylorianMeal");
        brand.setFont(Font.font("Arial", 24));
        brand.setStyle("-fx-text-fill:red; -fx-font-weight:bold;");

        HBox topBar = new HBox(10, logo, brand);
        topBar.setAlignment(Pos.CENTER_LEFT);

        /* ---------- Form controls ---------- */
        TextField tfUser = new TextField();
        tfUser.setPromptText("Username");
        tfUser.setPrefWidth(300);

        PasswordField tfPass = new PasswordField();
        tfPass.setPromptText("Password");
        tfPass.setPrefWidth(300);

        ComboBox<String> roleCb = new ComboBox<>();
        roleCb.getItems().addAll("Customer", "Merchant", "Admin");
        roleCb.setValue("Customer");
        roleCb.setPrefWidth(300);

        /* ---------- Action buttons ---------- */
        Button btnLogin = new Button("Login");
        btnLogin.setPrefWidth(100);
        btnLogin.setStyle("-fx-background-color:red; -fx-text-fill:white; -fx-font-weight:bold;");

        Button btnReg = new Button("Register");
        btnReg.setPrefWidth(100);
        btnReg.setStyle("-fx-background-color:black; -fx-text-fill:white; -fx-font-weight:bold;");

        /* ---------- Login handler ---------- */
        btnLogin.setOnAction(e -> {
            String username     = tfUser.getText().trim();
            String password     = tfPass.getText().trim();
            String selectedRole = roleCb.getValue();

            if (username.isEmpty() || password.isEmpty()) {
                alert("Please enter username and password.", Alert.AlertType.WARNING);
                return;
            }

            /* 1 ▸ authenticate user & fetch role */
            String roleFromDB = DatabaseManager.authenticateUser(username, password);
            if (roleFromDB == null) {
                alert("Invalid username or password!", Alert.AlertType.ERROR);
                return;
            }

            /* 2 ▸ ensure the user selected the correct role */
            if (!roleFromDB.equals(selectedRole)) {
                alert("Role mismatch – please choose the correct role.", Alert.AlertType.INFORMATION);
                return;
            }

            /* 3 ▸ retrieve numeric user-ID */
            int userId = DatabaseManager.fetchUserId(username);
            if (userId == -1) {
                alert("Failed to retrieve user ID.", Alert.AlertType.ERROR);
                return;
            }
            LoginSession.get().setUserId(userId);

            /* 4 ▸ route to the proper dashboard */
            switch (roleFromDB) {
                case "Customer" -> StudentDashboard.display();
                case "Merchant" -> MerchantDashboard.display();
                case "Admin"    -> AdminDashboard.display();   // <-- updated
                default         -> alert("Unknown role.", Alert.AlertType.ERROR);
            }
        });

        /* ---------- Register handler ---------- */
        btnReg.setOnAction(e -> RegisterPage.display());

        /* ---------- Assemble layout ---------- */
        VBox form = new VBox(15,
                topBar,
                new Label("Username"), tfUser,
                new Label("Password"), tfPass,
                new Label("Login as"), roleCb,
                new CheckBox("Remember me"),
                new VBox(10, btnLogin, btnReg)
        );
        form.setPadding(new Insets(25));
        form.setAlignment(Pos.TOP_LEFT);
        form.setStyle("-fx-background-color:white;");

        Scene scene = new Scene(form, 450, 500);
        Navigation.stage.setScene(scene);
        Navigation.stage.setTitle("Login Page");
        Navigation.stage.show();
    }

    /* ---------- Utility: show an alert dialog ---------- */
    private static void alert(String msg, Alert.AlertType type) {
        Alert a = new Alert(type);
        a.setHeaderText(null);
        a.setContentText(msg);
        a.showAndWait();
    }

    /** Block instantiation */
    private LoginPage() {}
}