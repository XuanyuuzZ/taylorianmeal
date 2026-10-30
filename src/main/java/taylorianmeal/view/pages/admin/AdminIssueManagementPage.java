package taylorianmeal.view.pages.admin;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Modality;
import javafx.stage.Stage;
import taylorianmeal.DatabaseManager;
import taylorianmeal.model.Issue;
import taylorianmeal.model.TechnicalIssue;
import taylorianmeal.view.components.Navigation;
import taylorianmeal.view.pages.auth.LoginPage;
import taylorianmeal.view.pages.dashboard.AdminDashboard;

public class AdminIssueManagementPage {

    private TableView<Issue> orderIssueTable;
    private TableView<TechnicalIssue> technicalIssueTable;
    private BorderPane root;
    private VBox mainContent;
    private HBox tabBar;
    private Button orderIssueTab;
    private Button technicalIssueTab;

    public AdminIssueManagementPage() {
        root = new BorderPane();

        // Brand bar at the top
        HBox brandBar = new HBox(10);
        brandBar.setPadding(new Insets(20, 30, 10, 30));
        brandBar.setAlignment(Pos.CENTER_LEFT);
        ImageView logo = new ImageView(
                new Image(getClass().getResourceAsStream("/images/taylorianmeal.jpeg"))
        );
        logo.setFitHeight(40);
        logo.setPreserveRatio(true);
        Label appName = new Label("TaylorianMeal");
        appName.setFont(Font.font("Impact", 24));
        appName.setStyle("-fx-text-fill: red; -fx-font-weight: bold;");
        brandBar.getChildren().addAll(logo, appName);
        root.setTop(brandBar);

        // Sidebar (identical to User Management)
        VBox sidebar = new VBox(15);
        sidebar.setPadding(new Insets(20, 10, 10, 20));
        sidebar.setPrefWidth(170);
        sidebar.setStyle("-fx-background-color: #f8f9fa; -fx-border-color: #e9ecef; -fx-border-width: 0 1 0 0;");

        Label sideTitle = new Label("Admin Panel");
        sideTitle.setFont(Font.font("Arial", 16));
        sideTitle.setStyle("-fx-font-weight: bold; -fx-text-fill: #333;");

        Button userManagementBtn = AdminUserManagementPage.createSidebarButton("User Management", false);
        userManagementBtn.setOnAction(e -> AdminUserManagementPage.display());
        Button issueManagementBtn = AdminUserManagementPage.createSidebarButton("Issue Management", true);
        issueManagementBtn.setOnAction(e -> AdminIssueManagementPage.display());
        Button systemSettingsBtn = AdminUserManagementPage.createSidebarButton("System Settings", false);
        systemSettingsBtn.setOnAction(e -> AdminSystemSettingsPage.display());
        Button backToDashboardBtn = AdminUserManagementPage.createSidebarButton("Back to Dashboard", false);
        backToDashboardBtn.setOnAction(e -> AdminDashboard.display());
        backToDashboardBtn.setStyle("-fx-background-color: #6c757d; -fx-text-fill: white; -fx-font-weight: bold; -fx-padding: 8 12;");

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

        // Tab bar and tables
        tabBar = createTabBar();
        createOrderIssueTable();
        createTechnicalIssueTable();

        mainContent = new VBox(tabBar, orderIssueTable);
        mainContent.setAlignment(Pos.TOP_LEFT);
        mainContent.setPadding(new Insets(30, 0, 0, 0));
        root.setCenter(mainContent);
    }

    private HBox createTabBar() {
        orderIssueTab = new Button("Order Issues");
        technicalIssueTab = new Button("Technical Issues");

        orderIssueTab.setFont(Font.font("Arial", FontWeight.BOLD, 16));
        technicalIssueTab.setFont(Font.font("Arial", FontWeight.NORMAL, 16));

        orderIssueTab.setStyle("-fx-background-color: transparent; -fx-underline: true; -fx-text-fill: #d32f2f;");
        technicalIssueTab.setStyle("-fx-background-color: transparent; -fx-text-fill: #333;");

        orderIssueTab.setOnAction(e -> {
            highlightTab(orderIssueTab, technicalIssueTab);
            showOrderIssueTable();
        });
        technicalIssueTab.setOnAction(e -> {
            highlightTab(technicalIssueTab, orderIssueTab);
            showTechnicalIssueTable();
        });

        HBox tabBar = new HBox(30, orderIssueTab, technicalIssueTab);
        tabBar.setAlignment(Pos.CENTER_LEFT);
        tabBar.setPadding(new Insets(0, 0, 20, 0));
        return tabBar;
    }

    private void highlightTab(Button active, Button inactive) {
        active.setFont(Font.font("Arial", FontWeight.BOLD, 16));
        active.setStyle("-fx-background-color: transparent; -fx-underline: true; -fx-text-fill: #d32f2f;");
        inactive.setFont(Font.font("Arial", FontWeight.NORMAL, 16));
        inactive.setStyle("-fx-background-color: transparent; -fx-text-fill: #333;");
    }

    private void createOrderIssueTable() {
        orderIssueTable = new TableView<>();
        orderIssueTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_ALL_COLUMNS);
        orderIssueTable.setPrefSize(700, 400);

        TableColumn<Issue, String> idCol = new TableColumn<>("ID");
        idCol.setCellValueFactory(cell -> cell.getValue().idProperty());

        TableColumn<Issue, String> titleCol = new TableColumn<>("Title");
        titleCol.setCellValueFactory(cell -> cell.getValue().titleProperty());

        TableColumn<Issue, String> roleCol = new TableColumn<>("Role");
        roleCol.setCellValueFactory(cell -> cell.getValue().roleProperty());

        TableColumn<Issue, String> reporterCol = new TableColumn<>("Reporter");
        reporterCol.setCellValueFactory(cell -> cell.getValue().reporterProperty());

        TableColumn<Issue, String> statusCol = new TableColumn<>("Status");
        statusCol.setCellValueFactory(cell -> cell.getValue().statusProperty());

        TableColumn<Issue, String> dateCol = new TableColumn<>("Date");
        dateCol.setCellValueFactory(cell -> cell.getValue().dateProperty());

        TableColumn<Issue, String> actionCol = new TableColumn<>("Action");
        actionCol.setCellFactory(col -> new TableCell<Issue, String>() {
            private final Button viewBtn = new Button("View");
            {
                viewBtn.setStyle("-fx-background-color: #d32f2f; -fx-text-fill: white; -fx-font-weight: bold;");
                viewBtn.setOnAction(e -> {
                    Issue issue = getTableView().getItems().get(getIndex());
                    showOrderIssueDetail(issue);
                });
            }
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : viewBtn);
            }
        });

        orderIssueTable.getColumns().addAll(idCol, titleCol, roleCol, reporterCol, statusCol, dateCol, actionCol);
        refreshOrderIssues();
    }

    private void createTechnicalIssueTable() {
        technicalIssueTable = new TableView<>();
        technicalIssueTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_ALL_COLUMNS);
        technicalIssueTable.setPrefSize(700, 400);

        TableColumn<TechnicalIssue, String> idCol = new TableColumn<>("ID");
        idCol.setCellValueFactory(cell -> cell.getValue().idProperty());

        TableColumn<TechnicalIssue, String> userCol = new TableColumn<>("User");
        userCol.setCellValueFactory(cell -> cell.getValue().userProperty());

        TableColumn<TechnicalIssue, String> typeCol = new TableColumn<>("Type");
        typeCol.setCellValueFactory(cell -> cell.getValue().typeProperty());

        TableColumn<TechnicalIssue, String> statusCol = new TableColumn<>("Status");
        statusCol.setCellValueFactory(cell -> cell.getValue().statusProperty());

        TableColumn<TechnicalIssue, String> actionCol = new TableColumn<>("Action");
        actionCol.setCellFactory(col -> new TableCell<TechnicalIssue, String>() {
            private final Button viewBtn = new Button("View");
            {
                viewBtn.setStyle("-fx-background-color: #d32f2f; -fx-text-fill: white; -fx-font-weight: bold;");
                viewBtn.setOnAction(e -> {
                    TechnicalIssue issue = getTableView().getItems().get(getIndex());
                    showTechnicalIssueDetail(issue);
                });
            }
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : viewBtn);
            }
        });

        technicalIssueTable.getColumns().addAll(idCol, userCol, typeCol, statusCol, actionCol);
        refreshTechnicalIssues();
    }

    private void showOrderIssueTable() {
        refreshOrderIssues();
        mainContent.getChildren().set(1, orderIssueTable);
    }

    private void showTechnicalIssueTable() {
        refreshTechnicalIssues();
        mainContent.getChildren().set(1, technicalIssueTable);
    }

    private void refreshOrderIssues() {
        orderIssueTable.getItems().setAll(DatabaseManager.getAllOrderIssues());
    }

    private void refreshTechnicalIssues() {
        technicalIssueTable.getItems().setAll(DatabaseManager.getAllTechnicalIssues());
    }

    private void showOrderIssueDetail(Issue issue) {
        Stage dialog = new Stage();
        dialog.initModality(Modality.APPLICATION_MODAL);
        dialog.setTitle("Order Issue Detail");

        VBox vbox = new VBox(15);
        vbox.setPadding(new Insets(25));
        vbox.setStyle("-fx-background-color: white; -fx-background-radius: 10;");

        Label id = new Label("ID: " + issue.getId());
        Label title = new Label("Title: " + issue.getTitle());
        Label role = new Label("Role: " + issue.getRole());
        Label reporter = new Label("Reporter: " + issue.getReporter());
        Label status = new Label("Status: " + issue.getStatus());
        Label date = new Label("Date: " + issue.getDate());
        Label description = new Label("Description: " + issue.getDescription());
        id.setFont(Font.font("Arial", FontWeight.BOLD, 14));
        title.setFont(Font.font("Arial", FontWeight.BOLD, 14));

        Button approveBtn = new Button("Approve Refund");
        Button rejectBtn = new Button("Reject Refund");

        approveBtn.setStyle("-fx-background-color: #d32f2f; -fx-text-fill: white; -fx-font-weight: bold;");
        rejectBtn.setStyle("-fx-background-color: #888; -fx-text-fill: white; -fx-font-weight: bold;");

        approveBtn.setOnAction(e -> {
            DatabaseManager.updateIssueStatus(issue.getId(), "Refund Approved");
            String username = DatabaseManager.fetchUsernameById(issue.getReporter());
            DatabaseManager.sendNotificationToUser(username, "Your refund has been approved.");
            dialog.close();
            refreshOrderIssues();
        });
        rejectBtn.setOnAction(e -> {
            DatabaseManager.updateIssueStatus(issue.getId(), "Refund Rejected");
            String username = DatabaseManager.fetchUsernameById(issue.getReporter());
            DatabaseManager.sendNotificationToUser(username, "Your refund has been rejected.");
            dialog.close();
            refreshOrderIssues();
        });

        HBox btnBox = new HBox(15, approveBtn, rejectBtn);
        btnBox.setAlignment(Pos.CENTER);

        vbox.getChildren().addAll(id, title, role, reporter, status, date, description, btnBox);
        Scene scene = new Scene(vbox, 420, 380);
        dialog.setScene(scene);
        dialog.showAndWait();
    }

    private void showTechnicalIssueDetail(TechnicalIssue issue) {
        Stage dialog = new Stage();
        dialog.initModality(Modality.APPLICATION_MODAL);
        dialog.setTitle("Technical Issue Detail");

        VBox vbox = new VBox(15);
        vbox.setPadding(new Insets(25));
        vbox.setStyle("-fx-background-color: white; -fx-background-radius: 10;");

        Label id = new Label("ID: " + issue.getId());
        Label user = new Label("User: " + issue.getUser());
        Label type = new Label("Type: " + issue.getType());
        Label description = new Label("Description: " + issue.getDescription());
        Label status = new Label("Status: " + issue.getStatus());
        id.setFont(Font.font("Arial", FontWeight.BOLD, 14));
        type.setFont(Font.font("Arial", FontWeight.BOLD, 14));

        Button closeBtn = new Button("Close");
        closeBtn.setStyle("-fx-background-color: #888; -fx-text-fill: white;");
        closeBtn.setOnAction(e -> dialog.close());

        vbox.getChildren().addAll(id, user, type, description, status, closeBtn);
        Scene scene = new Scene(vbox, 400, 300);
        dialog.setScene(scene);
        dialog.showAndWait();
    }

    public void show(Stage stage) {
        stage.setTitle("Issue Management - TaylorianMeal");
        stage.setScene(new Scene(root, 900, 500));
        stage.show();
    }

    public static void display() {
        new AdminIssueManagementPage().show(Navigation.stage);
    }
}