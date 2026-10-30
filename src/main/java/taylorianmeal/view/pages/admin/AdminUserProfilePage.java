package taylorianmeal.view.pages.admin;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import taylorianmeal.DatabaseManager;
import taylorianmeal.view.components.Navigation;

/**
 * AdminUserProfilePage
 * --------------------
 * Displays and allows editing of a user's profile.
 * Only full name, email, and telephone can be edited.
 * Username and role are read-only.
 */
public class AdminUserProfilePage {

    /**
     * Displays the profile page for the given user.
     * @param user The user to view/edit.
     */
    public static void display(AdminUserManagementPage.UserTableRow user) {
        BorderPane root = new BorderPane();
        root.setStyle("-fx-background-color: white;");

        // Top bar and sidebar
        root.setTop(AdminUserManagementPage.createTopBar());
        root.setLeft(AdminUserManagementPage.createSidebar("User Management"));

        // Main content
        VBox centerContent = new VBox(20);
        centerContent.setPadding(new Insets(30));

        // Header
        Label header = new Label("User Profile");
        header.setFont(Font.font("Arial", 24));
        header.setStyle("-fx-font-weight: bold; -fx-text-fill: red;");

        // Description
        Label description = new Label("View and edit user information. Username and role cannot be modified.");
        description.setFont(Font.font("Arial", 14));
        description.setStyle("-fx-text-fill: #666;");

        // Form container
        VBox formContainer = new VBox(15);
        formContainer.setPadding(new Insets(20));
        formContainer.setStyle("-fx-background-color: #f8f9fa; -fx-border-color: #e9ecef; -fx-border-radius: 5;");

        // Editable fields
        TextField fullNameField = new TextField(user.getFullName());
        fullNameField.setPrefWidth(300);
        fullNameField.setFont(Font.font("Arial", 12));

        TextField emailField = new TextField(user.getEmail());
        emailField.setPrefWidth(300);
        emailField.setFont(Font.font("Arial", 12));

        TextField telephoneField = new TextField(user.getTelephone());
        telephoneField.setPrefWidth(300);
        telephoneField.setFont(Font.font("Arial", 12));

        // Read-only fields (disabled and styled)
        TextField usernameField = new TextField(user.getUsername());
        usernameField.setEditable(false);
        usernameField.setDisable(true);
        usernameField.setStyle("-fx-background-color: #e9ecef; -fx-text-fill: #6c757d;");
        usernameField.setPrefWidth(300);
        usernameField.setFont(Font.font("Arial", 12));

        TextField roleField = new TextField(user.getRole());
        roleField.setEditable(false);
        roleField.setDisable(true);
        roleField.setStyle("-fx-background-color: #e9ecef; -fx-text-fill: #6c757d;");
        roleField.setPrefWidth(300);
        roleField.setFont(Font.font("Arial", 12));

        // Form layout
        GridPane form = new GridPane();
        form.setHgap(15);
        form.setVgap(15);
        form.setAlignment(Pos.CENTER_LEFT);

        // Labels with consistent styling
        Label nameLabel = new Label("Full Name:");
        nameLabel.setFont(Font.font("Arial", 14));
        nameLabel.setStyle("-fx-font-weight: bold; -fx-text-fill: #333;");

        Label emailLabel = new Label("Email:");
        emailLabel.setFont(Font.font("Arial", 14));
        emailLabel.setStyle("-fx-font-weight: bold; -fx-text-fill: #333;");

        Label phoneLabel = new Label("Telephone:");
        phoneLabel.setFont(Font.font("Arial", 14));
        phoneLabel.setStyle("-fx-font-weight: bold; -fx-text-fill: #333;");

        Label usernameLabel = new Label("Username:");
        usernameLabel.setFont(Font.font("Arial", 14));
        usernameLabel.setStyle("-fx-font-weight: bold; -fx-text-fill: #333;");

        Label roleLabel = new Label("Role:");
        roleLabel.setFont(Font.font("Arial", 14));
        roleLabel.setStyle("-fx-font-weight: bold; -fx-text-fill: #333;");

        form.add(nameLabel, 0, 0);
        form.add(fullNameField, 1, 0);

        form.add(emailLabel, 0, 1);
        form.add(emailField, 1, 1);

        form.add(phoneLabel, 0, 2);
        form.add(telephoneField, 1, 2);

        form.add(usernameLabel, 0, 3);
        form.add(usernameField, 1, 3);

        form.add(roleLabel, 0, 4);
        form.add(roleField, 1, 4);

        // Buttons with red theme
        Button saveBtn = new Button("Save Changes");
        saveBtn.setFont(Font.font("Arial", 12));
        saveBtn.setStyle("-fx-background-color: red; -fx-text-fill: white; -fx-font-weight: bold; -fx-padding: 8 16;");
        saveBtn.setOnAction(e -> {
            String newFullName = fullNameField.getText() == null ? "" : fullNameField.getText().trim();
            String newEmail = emailField.getText() == null ? "" : emailField.getText().trim();
            String newTelephone = telephoneField.getText() == null ? "" : telephoneField.getText().trim();

            if (newFullName.isEmpty() || newEmail.isEmpty()) {
                showAlert("Validation Error", "Full name and email cannot be empty.", Alert.AlertType.WARNING);
                return;
            }

            try {
                DatabaseManager.updateUserProfile(user.getId(), newFullName, newEmail, newTelephone);
                showAlert("Success", "Profile updated successfully.", Alert.AlertType.INFORMATION);
                AdminUserManagementPage.display(); // Return to user management page
            } catch (Exception ex) {
                showAlert("Error", "Failed to update profile: " + ex.getMessage(), Alert.AlertType.ERROR);
            }
        });

        Button cancelBtn = new Button("Cancel");
        cancelBtn.setFont(Font.font("Arial", 12));
        cancelBtn.setStyle("-fx-background-color: #6c757d; -fx-text-fill: white; -fx-font-weight: bold; -fx-padding: 8 16;");
        cancelBtn.setOnAction(e -> AdminUserManagementPage.display());

        HBox buttonBox = new HBox(10, saveBtn, cancelBtn);
        buttonBox.setAlignment(Pos.CENTER_LEFT);

        formContainer.getChildren().addAll(form, buttonBox);
        centerContent.getChildren().addAll(header, description, formContainer);
        root.setCenter(centerContent);

        Scene scene = new Scene(root, 900, 500);
        Navigation.stage.setScene(scene);
        Navigation.stage.setTitle("User Profile - " + user.getUsername());
        Navigation.stage.show();
    }

    /**
     * Utility to show an alert dialog.
     */
    private static void showAlert(String title, String content, Alert.AlertType type) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}