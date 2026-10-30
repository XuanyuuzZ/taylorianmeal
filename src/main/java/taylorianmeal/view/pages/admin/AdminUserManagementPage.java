package taylorianmeal.view.pages.admin;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import taylorianmeal.DatabaseManager;
import taylorianmeal.view.components.Navigation;
import taylorianmeal.view.pages.AddUserPage;
import taylorianmeal.view.pages.auth.LoginPage;
import taylorianmeal.view.pages.dashboard.AdminDashboard;

import java.sql.SQLException;
import java.util.List;

public class AdminUserManagementPage {

    private static TableView<UserTableRow> userTable;
    private static ObservableList<UserTableRow> userData = FXCollections.observableArrayList();

    /**
     * Displays the unified user management page for admin.
     */
    public static void display() {
        BorderPane root = new BorderPane();
        root.setStyle("-fx-background-color: white;");

        // Top bar with logo and app name
        HBox topBar = createTopBar();
        root.setTop(topBar);

        // Sidebar with navigation buttons
        VBox sidebar = new VBox(15);
        sidebar.setPadding(new Insets(20, 10, 10, 20));
        sidebar.setPrefWidth(170);
        sidebar.setStyle("-fx-background-color: #f8f9fa; -fx-border-color: #e9ecef; -fx-border-width: 0 1 0 0;");

        Label sideTitle = new Label("Admin Panel");
        sideTitle.setFont(Font.font("Arial", 16));
        sideTitle.setStyle("-fx-font-weight: bold; -fx-text-fill: #333;");

        // Navigation buttons
        Button userManagementBtn = createSidebarButton("User Management", true);
        userManagementBtn.setOnAction(e -> AdminUserManagementPage.display());

        Button issueManagementBtn = createSidebarButton("Issue Management", false);
        issueManagementBtn.setOnAction(e -> AdminIssueManagementPage.display());

        Button systemSettingsBtn = createSidebarButton("System Settings", false);
        systemSettingsBtn.setOnAction(e -> AdminSystemSettingsPage.display());

        Button backToDashboardBtn = createSidebarButton("Back to Dashboard", false);
        backToDashboardBtn.setOnAction(e -> AdminDashboard.display());
        backToDashboardBtn.setStyle("-fx-background-color: #6c757d; -fx-text-fill: white; -fx-font-weight: bold; -fx-padding: 8 12;");

        // Logout button at the bottom
        Region spacer = new Region();
        VBox.setVgrow(spacer, Priority.ALWAYS);

        Button logoutBtn = new Button("Logout");
        logoutBtn.setPrefWidth(90);
        logoutBtn.setFont(Font.font("Arial", 12));
        logoutBtn.setStyle("-fx-background-color: red; -fx-text-fill: white; -fx-font-weight: bold;");
        logoutBtn.setOnAction(e -> LoginPage.display());

        HBox logoutBox = new HBox(logoutBtn);
        logoutBox.setAlignment(Pos.CENTER);
        logoutBox.setPadding(new Insets(10, 0, 20, 0));

        sidebar.getChildren().addAll(
                sideTitle,
                userManagementBtn,
                issueManagementBtn,
                systemSettingsBtn,
                backToDashboardBtn,
                spacer,
                logoutBox
        );
        root.setLeft(sidebar);

        // Main content area
        VBox mainContent = new VBox(20);
        mainContent.setPadding(new Insets(30));
        mainContent.setAlignment(Pos.TOP_CENTER);

        // Header with title and refresh button
        HBox headerBox = new HBox(15);
        headerBox.setAlignment(Pos.CENTER_LEFT);

        Label titleLabel = new Label("User Management");
        titleLabel.setFont(Font.font("Arial", 24));
        titleLabel.setStyle("-fx-font-weight: bold; -fx-text-fill: #333;");

        // Only keep the Add Merchant/Admin button in the header
        Button addUserBtn = new Button("Add Merchant/Admin");
        addUserBtn.setStyle("-fx-background-color:red; -fx-text-fill:white; -fx-font-weight:bold;");
        addUserBtn.setOnAction(e -> AddUserPage.display());

        headerBox.getChildren().addAll(titleLabel, addUserBtn);

        // Create the user table
        createUserTable();

        // Remove the refresh button event and its references

        mainContent.getChildren().addAll(headerBox, userTable);
        root.setCenter(mainContent);

        // Load initial user data
        refreshUserData();

        // Set up and show the scene
        Scene scene = new Scene(root, 900, 500);
        Navigation.stage.setScene(scene);
        Navigation.stage.setTitle("User Management - TaylorianMeal");
        Navigation.stage.show();
    }

    /**
     * Creates the top bar with logo and app name.
     */
    public static HBox createTopBar() {
        HBox topBar = new HBox(10);
        topBar.setPadding(new Insets(15, 25, 8, 25));
        topBar.setAlignment(Pos.CENTER_LEFT);
        topBar.setStyle("-fx-background-color: #f8f9fa; -fx-border-color: #e9ecef; -fx-border-width: 0 0 1 0;");

        ImageView logo = new ImageView(
                new Image(
                        AdminUserManagementPage.class.getResourceAsStream(
                                "/images/taylorianmeal.jpeg"
                        )
                )
        );
        logo.setFitHeight(32);
        logo.setPreserveRatio(true);

        Label appName = new Label("TaylorianMeal");
        appName.setFont(Font.font("Arial", 20));
        appName.setStyle("-fx-text-fill: red; -fx-font-weight: bold;");

        topBar.getChildren().addAll(logo, appName);
        return topBar;
    }

    /**
     * Creates a sidebar navigation button.
     */
    public static Button createSidebarButton(String text, boolean active) {
        Button btn = new Button(text);
        btn.setPrefWidth(150);
        btn.setAlignment(Pos.CENTER_LEFT);
        btn.setFont(Font.font("Arial", 12));
        btn.setStyle(active
                ? "-fx-background-color: #007bff; -fx-text-fill: white; -fx-font-weight: bold; -fx-padding: 8 12;"
                : "-fx-background-color: transparent; -fx-text-fill: #333; -fx-font-weight: normal; -fx-padding: 8 12;");
        return btn;
    }

    /**
     * Creates the sidebar for use in other pages if needed.
     */
    public static VBox createSidebar(String activePage) {
        VBox sidebar = new VBox(20);
        sidebar.setPadding(new Insets(30, 10, 10, 30));
        sidebar.setPrefWidth(200);
        sidebar.setStyle("-fx-background-color: #f4f4f4;");

        Button userBtn = createSidebarButton("User Management", activePage.equals("User Management"));
        userBtn.setOnAction(e -> AdminUserManagementPage.display());

        Button issueBtn = createSidebarButton("Issue Management", activePage.equals("Issue Management"));
        issueBtn.setOnAction(e -> AdminIssueManagementPage.display());

        Button settingsBtn = createSidebarButton("System Settings", activePage.equals("System Settings"));
        settingsBtn.setOnAction(e -> AdminSystemSettingsPage.display());

        sidebar.getChildren().addAll(userBtn, issueBtn, settingsBtn);
        return sidebar;
    }

    /**
     * Creates the user table and configures its columns and style.
     */
    private static void createUserTable() {
        userTable = new TableView<>();
        userTable.setPrefHeight(300); // Slightly taller for better vertical fit
        userTable.setMaxWidth(550);   // Moderate width
        userTable.setMinWidth(550);   // Fixed width for alignment
        userTable.setStyle("-fx-background-color: white; -fx-border-color: #e9ecef; -fx-border-radius: 5;");

        // ID column
        TableColumn<UserTableRow, Integer> idCol = new TableColumn<>("ID");
        idCol.setCellValueFactory(new PropertyValueFactory<>("id"));
        idCol.setPrefWidth(50);

        // Full Name column
        TableColumn<UserTableRow, String> nameCol = new TableColumn<>("Full Name");
        nameCol.setCellValueFactory(new PropertyValueFactory<>("fullName"));
        nameCol.setPrefWidth(120);

        // Email column
        TableColumn<UserTableRow, String> emailCol = new TableColumn<>("Email");
        emailCol.setCellValueFactory(new PropertyValueFactory<>("email"));
        emailCol.setPrefWidth(140);

        // Username column
        TableColumn<UserTableRow, String> usernameCol = new TableColumn<>("Username");
        usernameCol.setCellValueFactory(new PropertyValueFactory<>("username"));
        usernameCol.setPrefWidth(90);

        // Telephone column
        TableColumn<UserTableRow, String> phoneCol = new TableColumn<>("Telephone");
        phoneCol.setCellValueFactory(new PropertyValueFactory<>("telephone"));
        phoneCol.setPrefWidth(90);

        // Role column
        TableColumn<UserTableRow, String> roleCol = new TableColumn<>("Role");
        roleCol.setCellValueFactory(new PropertyValueFactory<>("role"));
        roleCol.setPrefWidth(60);

        // Actions column
        TableColumn<UserTableRow, Void> actionsCol = new TableColumn<>("Actions");
        actionsCol.setPrefWidth(90);

        actionsCol.setCellFactory(param -> new TableCell<>() {
            private final Button deleteButton = new Button("Delete");
            private final Button viewButton = new Button("View");

            {
                deleteButton.setPrefSize(45, 22);
                deleteButton.setStyle("-fx-background-color: #dc3545; -fx-text-fill: white; -fx-font-size: 10;");

                viewButton.setPrefSize(45, 22);
                viewButton.setStyle("-fx-background-color: #007bff; -fx-text-fill: white; -fx-font-size: 10;");

                HBox buttonBox = new HBox(3);
                buttonBox.getChildren().addAll(viewButton, deleteButton);

                deleteButton.setOnAction(e -> {
                    UserTableRow user = getTableView().getItems().get(getIndex());
                    if (user.getRole().equals("admin")) {
                        Alert alert = new Alert(Alert.AlertType.WARNING);
                        alert.setTitle("Protected User");
                        alert.setHeaderText("Cannot Delete Administrator");
                        alert.setContentText("Administrator accounts cannot be deleted for security reasons.");
                        alert.showAndWait();
                    } else {
                        Alert confirmDialog = new Alert(Alert.AlertType.CONFIRMATION);
                        confirmDialog.setTitle("Confirm Delete");
                        confirmDialog.setHeaderText("Delete User");
                        confirmDialog.setContentText("Are you sure you want to delete user: " + user.getFullName() + "?");

                        confirmDialog.showAndWait().ifPresent(response -> {
                            if (response == ButtonType.OK) {
                                try {
                                    DatabaseManager.deleteUser(user.getId());
                                    refreshUserData();
                                    Alert successAlert = new Alert(Alert.AlertType.INFORMATION);
                                    successAlert.setTitle("Success");
                                    successAlert.setHeaderText("User Deleted");
                                    successAlert.setContentText("User has been successfully deleted.");
                                    successAlert.showAndWait();
                                } catch (SQLException ex) {
                                    Alert errorAlert = new Alert(Alert.AlertType.ERROR);
                                    errorAlert.setTitle("Error");
                                    errorAlert.setHeaderText("Delete Failed");
                                    errorAlert.setContentText("Failed to delete user: " + ex.getMessage());
                                    errorAlert.showAndWait();
                                }
                            }
                        });
                    }
                });

                viewButton.setOnAction(e -> {
                    UserTableRow user = getTableView().getItems().get(getIndex());
                    AdminUserProfilePage.display(user);
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    UserTableRow user = getTableView().getItems().get(getIndex());
                    if (user.getRole().equals("admin")) {
                        deleteButton.setText("Protected");
                        deleteButton.setDisable(true);
                        deleteButton.setStyle("-fx-background-color: #6c757d; -fx-text-fill: white; -fx-font-size: 10;");
                    } else {
                        deleteButton.setText("Delete");
                        deleteButton.setDisable(false);
                        deleteButton.setStyle("-fx-background-color: #dc3545; -fx-text-fill: white; -fx-font-size: 10;");
                    }

                    HBox buttonBox = new HBox(3);
                    buttonBox.getChildren().addAll(viewButton, deleteButton);
                    setGraphic(buttonBox);
                }
            }
        });

        userTable.getColumns().addAll(idCol, nameCol, emailCol, usernameCol, phoneCol, roleCol, actionsCol);
        userTable.setItems(userData);

        // Double-click row to view user profile
        userTable.setRowFactory(tv -> {
            TableRow<UserTableRow> row = new TableRow<>();
            row.setOnMouseClicked(event -> {
                if (event.getClickCount() == 2 && !row.isEmpty()) {
                    UserTableRow user = row.getItem();
                    AdminUserProfilePage.display(user);
                }
            });
            return row;
        });
    }

    /**
     * Refreshes the user data in the table.
     */
    private static void refreshUserData() {
        try {
            List<DatabaseManager.UserInfo> users = DatabaseManager.fetchAllUsers();
            userData.clear();

            for (DatabaseManager.UserInfo user : users) {
                userData.add(new UserTableRow(
                        user.id(),
                        user.fullName(),
                        user.email(),
                        user.username(),
                        user.telephone(),
                        user.role()
                ));
            }
        } catch (SQLException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText("Failed to Load Users");
            alert.setContentText("Error: " + e.getMessage());
            alert.showAndWait();
        }
    }

    /**
     * Data model for a row in the user table.
     */
    public static class UserTableRow {
        private final int id;
        private final String fullName;
        private final String email;
        private final String username;
        private final String telephone;
        private final String role;

        public UserTableRow(int id, String fullName, String email, String username, String telephone, String role) {
            this.id = id;
            this.fullName = fullName;
            this.email = email;
            this.username = username;
            this.telephone = telephone;
            this.role = role;
        }

        public int getId() { return id; }
        public String getFullName() { return fullName; }
        public String getEmail() { return email; }
        public String getUsername() { return username; }
        public String getTelephone() { return telephone; }
        public String getRole() { return role; }
    }
}