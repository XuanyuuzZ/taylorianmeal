package taylorianmeal.view.pages.dashboard;

import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import taylorianmeal.LoginSession;
import taylorianmeal.service.NotificationManager;
import taylorianmeal.service.OrderManager;
import taylorianmeal.view.components.Navigation;
import taylorianmeal.view.pages.InboxPage;
import taylorianmeal.view.pages.ProfilePage;
import taylorianmeal.view.pages.auth.LoginPage;
import taylorianmeal.view.pages.order.OrderStatusHistoryPage;
import taylorianmeal.view.pages.order.PreOrderPage;
import taylorianmeal.view.pages.support.ChatBotPage;
import taylorianmeal.view.pages.support.FAQCenterPage;
import taylorianmeal.view.pages.support.HelpDeskPage;

/**
 * StudentDashboard – landing page for student (customer) users.
 *
 * Entry points
 * ------------
 * • display(int id) – explicit call (unit tests or other pages)
 * • display()       – auto-fetch id from LoginSession
 *
 * Layout changes:
 * --------------
 * - Four main buttons arranged in 2×2 grid under the welcome text.
 * - “Need help?” card is now under that grid in the center column.
 * - Left column is now empty (to preserve three-column spacing), and its white background removed.
 * - Right column unchanged.
 * - Top bar: logo on left, spacer, then FAQ + Logout on right.
 * - All three columns and the contentBox are left-aligned.
 */
public final class StudentDashboard {

    /** Show dashboard for the given customerId. */
    public static void display(int customerId) {
        if (customerId <= 0) {
            // If invalid customer ID, redirect to login
            LoginPage.display();
            return;
        }
        // No longer assign custId

        // 1) Top Bar: Logo + spacer + FAQ + Logout
        ImageView logoIv = new ImageView(
                new Image(StudentDashboard.class.getResourceAsStream(
                        "/images/taylorianmeal.jpeg")));
        logoIv.setFitHeight(40);
        logoIv.setPreserveRatio(true);

        Label brand = new Label("TaylorianMeal");
        brand.setFont(Font.font("Arial", 22));
        brand.setStyle("-fx-text-fill:red; -fx-font-weight:bold;");

        HBox logoBar = new HBox(10, logoIv, brand);
        logoBar.setAlignment(Pos.CENTER_LEFT);

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Button faq = new Button("FAQ");
        faq.setStyle("-fx-background-color:gray; -fx-text-fill:white; -fx-font-weight:bold;");
        faq.setPrefHeight(40);
        faq.setOnAction(e -> FAQCenterPage.display());

        Button logout = new Button("Logout");
        logout.setStyle("-fx-background-color:black; -fx-text-fill:white; -fx-font-weight:bold;");
        logout.setPrefHeight(40);
        logout.setOnAction(e -> LoginPage.display());

        HBox topBar = new HBox(10, logoBar, spacer, faq, logout);
        topBar.setPadding(new Insets(10));
        topBar.setAlignment(Pos.CENTER_LEFT);

        // 2) Left column: (empty spacer to preserve 3-column layout)
        VBox left = new VBox();
        left.setPadding(new Insets(20));
        left.setAlignment(Pos.TOP_LEFT);  // Align left
        // No longer set background color

        // 3) Center column: Welcome + grid + Help-Desk
        Text welcome = new Text("Welcome to TaylorianMeal");
        welcome.setFont(Font.font("Arial", 28));
        welcome.setStyle("-fx-font-weight:bold;");

        Label sub = new Label("""
            Your smart way to pre-order meals on campus.
            Plan ahead, skip the lines, and enjoy your food on your time!
            """);
        sub.setStyle("-fx-font-size:14px;");

        Button btnOrder   = navButton("Pre-order Food", "Start to order food");
        Button btnInbox   = navButton("Inbox Page",     "Check your inbox");
        Button btnProfile = navButton("Profile",        "View & edit profile");
        Button btnChat    = navButton("Chat with Bot",  "Connect with bot service");
        btnOrder.setOnAction(e -> PreOrderPage.display());
        btnInbox.setOnAction(e -> InboxPage.display());
        btnProfile.setOnAction(e -> ProfilePage.display());
        btnChat.setOnAction(e -> ChatBotPage.display());

        GridPane grid = new GridPane();
        grid.setHgap(20);
        grid.setVgap(20);
        grid.setAlignment(Pos.CENTER_LEFT);
        grid.add(btnOrder,   0, 0);
        grid.add(btnInbox,   1, 0);
        grid.add(btnProfile, 0, 1);
        grid.add(btnChat,    1, 1);

        VBox helpBox = new VBox(5,
                new Label("Need help? Reach us:"),
                new Label("📧 support@taylorianmeal.com"),
                new Label("📞 Main Canteen: 03-1234567")
        );
        helpBox.setStyle("""
            -fx-background-color:#ffffff;
            -fx-border-color:#ccc;
            -fx-border-radius:10;
            -fx-padding:10;""");
        Button helpDesk = new Button("HelpDesk");
        helpDesk.setStyle("-fx-background-color:#f0f0f0; -fx-font-weight:bold;");
        helpDesk.setOnAction(e -> HelpDeskPage.display());
        helpBox.getChildren().add(helpDesk);

        VBox center = new VBox(20, welcome, sub, grid, helpBox);
        center.setPadding(new Insets(20));
        center.setAlignment(Pos.TOP_LEFT);

        // 4) Right column: Notifications & Tickets
        VBox right = new VBox(20);
        right.setPadding(new Insets(20));
        right.setPrefWidth(320);
        right.setAlignment(Pos.TOP_LEFT);

        Label notifTitle = sectionTitle("Notifications");
        ListView<NotificationManager.NotificationRow> notifList = new ListView<>();
        notifList.setPrefHeight(250);
        notifList.setCellFactory(lv -> new NotificationCell());
        notifList.setOnMouseClicked(ev -> {
            if (ev.getClickCount() == 1 && !notifList.getItems().isEmpty())
                InboxPage.display();
        });

        Label ticketTitle = sectionTitle("Tickets List");
        TableView<OrderManager.OrderRecord> orderTable = buildOrderTable();
        orderTable.setPrefHeight(200);
        orderTable.setOnMouseClicked(ev -> {
            if (ev.getClickCount() == 1 && !orderTable.getItems().isEmpty())
                OrderStatusHistoryPage.display(customerId);
        });

        Button refresh = new Button("↻");
        refresh.setOnAction(e -> {
            loadNotifications(notifList, customerId);
            loadOrders(orderTable, customerId);
        });

        right.getChildren().addAll(
                notifTitle, notifList,
                ticketTitle, refresh, orderTable
        );

        loadNotifications(notifList, customerId);
        loadOrders(orderTable, customerId);

        // 5) Combine three columns evenly and left-align
        HBox contentBox = new HBox(20, left, center, right);
        contentBox.setAlignment(Pos.TOP_LEFT);
        HBox.setHgrow(left,   Priority.ALWAYS);
        HBox.setHgrow(center, Priority.ALWAYS);
        HBox.setHgrow(right,  Priority.ALWAYS);

        // 6) Assemble root
        BorderPane root = new BorderPane();
        root.setTop(topBar);
        root.setCenter(contentBox);
        root.setStyle("-fx-background-color:#f8f8f8;");

        Scene scene = new Scene(root, 1000, 600);
        Navigation.stage.setScene(scene);
        Navigation.stage.setTitle("Student Dashboard");
        
        // Add focus listener to refresh data when window becomes focused
        Navigation.stage.focusedProperty().addListener((obs, wasFocused, isNowFocused) -> {
            if (isNowFocused) {
                // Refresh data when window gains focus (indicating user returned to this page)
                loadNotifications(notifList, customerId);
                loadOrders(orderTable, customerId);
            }
        });
        
        Navigation.stage.show();
    }

    /** Convenience entry – uses LoginSession singleton */
    public static void display() {
        int userId = LoginSession.get().getUserId();
        if (userId <= 0) {
            // If no valid user ID, redirect to login
            LoginPage.display();
            return;
        }
        display(userId);
    }

    /** Refresh the dashboard data - can be called from other pages */
    public static void refreshData() {
        // This method can be called to refresh the dashboard data
        // Currently it will recreate the entire page, but in a more sophisticated
        // implementation, you could just refresh the data without recreating the UI
        display(LoginSession.get().getUserId());
    }

    /** Build a 2-line red nav button */
    private static Button navButton(String title, String subtitle) {
        Button b = new Button(title + "\n" + subtitle);
        b.setStyle("""
            -fx-background-color:red;
            -fx-text-fill:white;
            -fx-font-size:15px;
            -fx-font-weight:bold;
            -fx-background-radius:12;""");
        b.setPrefSize(200, 90);
        b.setWrapText(true);
        b.setAlignment(Pos.CENTER);
        return b;
    }

    /** Build section title label */
    private static Label sectionTitle(String txt) {
        Label l = new Label(txt);
        l.setStyle("-fx-font-size:18px; -fx-font-weight:bold;");
        return l;
    }

    /** Build tickets table */
    private static TableView<OrderManager.OrderRecord> buildOrderTable() {
        TableView<OrderManager.OrderRecord> tbl = new TableView<>();
        TableColumn<OrderManager.OrderRecord,String> c1 = new TableColumn<>("Restaurant");
        TableColumn<OrderManager.OrderRecord,String> c2 = new TableColumn<>("Order ID");
        TableColumn<OrderManager.OrderRecord,String> c3 = new TableColumn<>("Date");
        TableColumn<OrderManager.OrderRecord,String> c4 = new TableColumn<>("Status");
        c1.setCellValueFactory(new PropertyValueFactory<>("restaurant"));
        c2.setCellValueFactory(new PropertyValueFactory<>("orderId"));
        c3.setCellValueFactory(new PropertyValueFactory<>("date"));
        c4.setCellValueFactory(new PropertyValueFactory<>("status"));
        tbl.getColumns().addAll(c1, c2, c3, c4);
        return tbl;
    }

    private static void loadNotifications(ListView<NotificationManager.NotificationRow> lv, int customerId) {
        try {
            lv.setItems(NotificationManager.listForCustomer(customerId));
        } catch (Exception ex) {
            ex.printStackTrace();
            lv.setItems(FXCollections.emptyObservableList());
        }
    }

    private static void loadOrders(TableView<OrderManager.OrderRecord> tbl, int customerId) {
        try {
            tbl.setItems(OrderManager.listOrdersForCustomer(customerId));
        } catch (Exception ex) {
            ex.printStackTrace();
            tbl.setItems(FXCollections.emptyObservableList());
        }
    }

    /** Custom ListCell for notifications */
    private static final class NotificationCell
            extends ListCell<NotificationManager.NotificationRow> {
        private final VBox box = new VBox(2);
        private final Label lblTitle = new Label();
        private final Label lblMsg   = new Label();
        private final Label lblTime  = new Label();
        NotificationCell() {
            lblTitle.setStyle("-fx-font-weight:bold;");
            lblMsg .setStyle("-fx-text-fill:#555;");
            lblTime.setStyle("-fx-text-fill:gray; -fx-font-size:10px;");
            box.setPadding(new Insets(6));
            box.getChildren().addAll(lblTitle, lblMsg, lblTime);
        }
        @Override protected void updateItem(NotificationManager.NotificationRow n, boolean empty) {
            super.updateItem(n, empty);
            if (empty || n == null) {
                setGraphic(null);
            } else {
                lblTitle.setText("🔔 " + n.title());
                lblMsg  .setText(n.message());
                lblTime .setText(n.createdAt());
                setGraphic(box);
            }
        }
    }

    /** Prevent instantiation */
    private StudentDashboard() {}
}