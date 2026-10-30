package taylorianmeal.view.pages;

import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import taylorianmeal.LoginSession;
import taylorianmeal.service.NotificationManager;
import taylorianmeal.view.components.Navigation;
import taylorianmeal.view.pages.auth.LoginPage;
import taylorianmeal.view.pages.dashboard.StudentDashboard;

/**
 * InboxPage – shows notification cards for the logged-in customer.
 *
 * Entry-point: {@link #display()} – grabs current user ID from LoginSession.
 * On load, it queries NotificationManager.listForCustomer(userId)
 * and renders each record as a visual card.
 */
public class InboxPage {

    /** Call this to open the Inbox. User ID is pulled from LoginSession. */
    public static void display() {

        /* --------------------------------------------------
         * 1.  Fetch current customer ID (returns –1 if not set)
         * -------------------------------------------------- */
        int userId = LoginSession.get().getUserId();
        if (userId < 0) {
            // No user in session – go back to login screen
            LoginPage.display();
            return;
        }

        /* --------------------------------------------------
         * 2.  Root layout
         * -------------------------------------------------- */
        BorderPane root = new BorderPane();
        root.setPadding(new Insets(20));
        root.setStyle("-fx-background-color:#f9f9f9;");

        VBox content = new VBox(15);
        content.setAlignment(Pos.TOP_LEFT);

        /* ---------- Top-bar: logo + brand ---------- */
        HBox topBar = new HBox(10);
        topBar.setAlignment(Pos.CENTER_LEFT);

        ImageView logo = new ImageView(
                new Image(InboxPage.class.getResourceAsStream("/images/taylorianmeal.jpeg")));
        logo.setFitHeight(40); logo.setPreserveRatio(true);

        Label brand = new Label("TaylorianMeal");
        brand.setFont(Font.font("Arial", 20));
        brand.setStyle("-fx-text-fill:red; -fx-font-weight:bold;");

        topBar.getChildren().addAll(logo, brand);

        /* ---------- Page title ---------- */
        Label title = new Label("Inbox");
        title.setFont(Font.font("Arial", 26));
        title.setStyle("-fx-font-weight:bold;");

        /* ---------- Notification list ---------- */
        VBox messageBox = buildMessageBox(userId);   // ← query DB & build cards

        ScrollPane scroller = new ScrollPane(messageBox);
        scroller.setFitToWidth(true);
        scroller.setPrefHeight(300);
        scroller.setStyle("-fx-background-color:transparent;");

        content.getChildren().addAll(topBar, title, scroller);
        root.setCenter(content);

        /* ---------- Back button ---------- */
        Button back = new Button("Back");
        back.setStyle("""
            -fx-background-color:black;
            -fx-text-fill:white;
            -fx-font-weight:bold;
            """);
        back.setOnAction(e -> StudentDashboard.display());   // back to dashboard

        HBox bottom = new HBox(back);
        bottom.setAlignment(Pos.CENTER_RIGHT);
        bottom.setPadding(new Insets(10, 0, 0, 0));
        root.setBottom(bottom);

        /* ---------- Show scene ---------- */
        Navigation.stage.setScene(new Scene(root, 600, 450));
        Navigation.stage.setTitle("Inbox");
        Navigation.stage.show();
    }

    /* =====================================================================
     *  Private helpers
     * ===================================================================== */

    /** Query DB and convert each notification into a visual card. */
    private static VBox buildMessageBox(int userId) {
        VBox box = new VBox(10);
        box.setPadding(new Insets(10));

        try {
            ObservableList<NotificationManager.NotificationRow> list =
                    NotificationManager.listForCustomer(userId);
            ObservableList<NotificationManager.NotificationRow> notices =
                    NotificationManager.listPlatformNotices(userId);

            list.addAll(notices); // Merge
            list.sort(java.util.Comparator.comparing(NotificationManager.NotificationRow::createdAt).reversed());

            System.out.println("通知总数: " + list.size());
            for (var n : list) System.out.println(n.title() + " | " + n.message() + " | " + n.createdAt());

            if (list.isEmpty()) {
                Label empty = new Label("No notifications yet.");
                empty.setStyle("-fx-text-fill:gray;");
                box.getChildren().add(empty);
            } else {
                for (var n : list) box.getChildren().add(createCard(n));
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            Label err = new Label("Failed to load notifications.");
            err.setStyle("-fx-text-fill:red;");
            box.getChildren().add(err);
        }
        return box;
    }

    /** Build one visual card from a DB row. */
    private static HBox createCard(NotificationManager.NotificationRow n) {
        HBox card = new HBox(10);
        card.setAlignment(Pos.CENTER_LEFT);
        card.setPadding(new Insets(10));
        card.setStyle("""
            -fx-background-color:white;
            -fx-border-color:#cccccc;
            -fx-background-radius:10;
            -fx-border-radius:10;
            """);

        ImageView icon;
        java.io.InputStream iconStream;
        if (n.title().equals("Platform Notice")) {
            iconStream = InboxPage.class.getResourceAsStream("/images/notice.png");
        } else {
            iconStream = InboxPage.class.getResourceAsStream("/images/bell.png");
        }
        if (iconStream != null) {
            icon = new ImageView(new Image(iconStream));
        } else {
            icon = new ImageView(); // fallback: empty icon
        }
        icon.setFitWidth(24); icon.setFitHeight(24);

        Label ttl = new Label(n.title());
        ttl.setFont(Font.font("Arial", javafx.scene.text.FontWeight.BOLD, 14));
        ttl.setStyle("-fx-font-weight:bold;");

        Label msg = new Label(n.message());
        msg.setFont(Font.font("Arial", 12));
        msg.setStyle("-fx-text-fill:#555555;");

        Label ts  = new Label(n.createdAt());
        ts.setFont(Font.font("Arial", 10));
        ts.setStyle("-fx-text-fill:gray;");

        VBox text = new VBox(2, ttl, msg, ts);
        card.getChildren().addAll(icon, text);
        return card;
    }
}