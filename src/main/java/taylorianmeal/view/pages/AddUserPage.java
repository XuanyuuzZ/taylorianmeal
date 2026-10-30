package taylorianmeal.view.pages;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.scene.layout.HBox; // Added import for HBox
import taylorianmeal.DatabaseManager;
import taylorianmeal.view.pages.admin.AdminUserManagementPage;
import taylorianmeal.view.components.Navigation;

public class AddUserPage {
    public static void display() {
        VBox root = new VBox(15);
        root.setPadding(new Insets(30));
        root.setAlignment(Pos.CENTER);

        TextField tfFullName = new TextField();
        tfFullName.setPromptText("Full Name");

        TextField tfUsername = new TextField();
        tfUsername.setPromptText("Username");

        PasswordField tfPassword = new PasswordField();
        tfPassword.setPromptText("Password");

        TextField tfEmail = new TextField();
        tfEmail.setPromptText("Email");

        ComboBox<String> roleBox = new ComboBox<>();
        roleBox.getItems().addAll("Merchant", "Admin");
        roleBox.setValue("Merchant");

        Button submit = new Button("Submit");
        submit.setStyle("-fx-background-color:red; -fx-text-fill:white; -fx-font-weight:bold;");
        submit.setOnAction(e -> {
            String fullName = tfFullName.getText().trim();
            String username = tfUsername.getText().trim();
            String password = tfPassword.getText().trim();
            String email = tfEmail.getText().trim();
            String role = roleBox.getValue();

            if (fullName.isEmpty() || username.isEmpty() || password.isEmpty() || email.isEmpty()) {
                showAlert("All fields are required.", Alert.AlertType.WARNING);
                return;
            }
            try {
                DatabaseManager.addUser(username, password, email, role, fullName);
                showAlert("User added successfully!", Alert.AlertType.INFORMATION);
                AdminUserManagementPage.display(); // Return to management page
            } catch (Exception ex) {
                ex.printStackTrace();
                showAlert("Failed to add user: " + ex.getMessage(), Alert.AlertType.ERROR);
            }
        });

        // Gray cancel button to return to user management page
        Button cancel = new Button("Cancel");
        cancel.setStyle("-fx-background-color: #888888; -fx-text-fill: white; -fx-font-weight: bold;");
        cancel.setOnAction(e -> AdminUserManagementPage.display());

        // HBox for buttons (Submit on the left, Cancel on the right)
        HBox buttonBox = new HBox(10, submit, cancel);
        buttonBox.setAlignment(Pos.CENTER);

        root.getChildren().addAll(
            new Label("Add Merchant/Admin"),
            tfFullName,
            tfUsername, tfPassword, tfEmail, roleBox, buttonBox
        );

        Scene scene = new Scene(root, 400, 350);
        Navigation.stage.setScene(scene);
        Navigation.stage.setTitle("Add User");
        Navigation.stage.show();
    }

    private static void showAlert(String msg, Alert.AlertType type) {
        Alert a = new Alert(type);
        a.setHeaderText(null);
        a.setContentText(msg);
        a.showAndWait();
    }
} 