package taylorianmeal.view.pages;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import taylorianmeal.service.NotificationManager;
import taylorianmeal.view.pages.order.OrderStatusHistoryPage;
import taylorianmeal.view.components.Navigation;

/**
 * ReportOrderProblemPage
 * ----------------------
 * • Allows the customer to report an issue for a specific order.
 * • Minimal validation; on submit, inserts a stub record into the
 *   “tickets” table (create that table if you haven’t) and shows a
 *   confirmation alert.
 *
 * display(int customerId, long orderId)
 * -------------------------------------
 *   @param customerId The owner of the order
 *   @param orderId    Raw numeric ID ( *not* the “TM00017” string )
 */
public final class ReportOrderProblemPage {

    /* =========================================================
     *  Entry-point
     * ========================================================= */
    public static void display(int customerId, long orderId) {

        /* (Optional) fetch extra order info if you want them shown here */

        VBox root = new VBox(25);
        root.setPadding(new Insets(30));
        root.setStyle("-fx-background-color:#f9f9f9;");

        // ── top-bar ──
        HBox topBar = new HBox(10);
        topBar.setAlignment(Pos.CENTER_LEFT);
        ImageView logo = new ImageView(
                new Image(ReportOrderProblemPage.class.getResourceAsStream("/images/taylorianmeal.jpeg")));
        logo.setFitHeight(40); logo.setPreserveRatio(true);

        Label brand = new Label("TaylorianMeal");
        brand.setFont(Font.font("Arial", 22));
        brand.setStyle("-fx-text-fill:red; -fx-font-weight:bold;");
        topBar.getChildren().addAll(logo, brand);

        // ── title ──
        Label pageTitle = new Label("Report Order Problem");
        pageTitle.setFont(Font.font("Arial", 26));
        pageTitle.setStyle("-fx-font-weight:bold;");

        // ── order info box ──
        VBox orderInfo = new VBox(4,
                new Label("Order ID : TM" + String.format("%05d", orderId)),
                new Label("Customer  : #" + customerId)
        );
        orderInfo.setPadding(new Insets(15));
        orderInfo.setStyle("""
            -fx-background-color:white;
            -fx-border-color:#cccccc;
            -fx-border-radius:10;
            -fx-background-radius:10;
        """);

        // ── issue selection ──
        ToggleGroup tg = new ToggleGroup();
        VBox issues = new VBox(8,
                rb("Wrong Item Delivered", tg),
                rb("Delayed Order",         tg),
                rb("Food Quality Issue",    tg),
                rb("Payment Issue",         tg),
                rb("Missing Item",          tg),
                rb("Other",                 tg)
        );
        ((RadioButton) tg.getToggles().get(0)).setSelected(true); // default

        VBox issueBox = new VBox(10,
                new Label("What issue are you experiencing?"), issues);
        issueBox.setPadding(new Insets(15));
        issueBox.setStyle("""
            -fx-background-color:white;
            -fx-border-color:#cccccc;
            -fx-border-radius:10;
            -fx-background-radius:10;
        """);

        // ── additional notes ──
        TextArea notes = new TextArea();
        notes.setPromptText("Additional notes (optional) …");
        notes.setWrapText(true); notes.setPrefRowCount(4);

        // ── buttons ──
        Button submit = new Button("Submit");
        submit.setStyle("-fx-background-color:red; -fx-text-fill:white; -fx-font-weight:bold;");

        submit.setOnAction(e -> {
            String selected = ((RadioButton) tg.getSelectedToggle()).getText();
            try {
                // Insert into issues table
                try (java.sql.Connection conn = java.sql.DriverManager.getConnection(taylorianmeal.DatabaseManager.JDBC_URL);
                     java.sql.PreparedStatement ps = conn.prepareStatement(
                         "INSERT INTO issues (title, role, reporter, status, date, description) VALUES (?, ?, ?, ?, ?, ?)"
                     )) {
                    ps.setString(1, selected); // title
                    ps.setString(2, "customer"); // role
                    ps.setString(3, String.valueOf(customerId)); // reporter
                    ps.setString(4, "OPEN"); // status
                    ps.setString(5, java.time.LocalDate.now().toString()); // date
                    ps.setString(6, notes.getText()); // description
                    ps.executeUpdate();
                }
                // Notification
                NotificationManager.add(
                        customerId,
                        "Issue Submitted",
                        "Your \"" + selected + "\" report is received."
                );
                alert("Thank you – your issue has been submitted.", Alert.AlertType.INFORMATION);
                OrderStatusHistoryPage.display(customerId);
            } catch (Exception ex) {
                ex.printStackTrace();
                alert("Submit failed: " + ex.getMessage(), Alert.AlertType.ERROR);
            }
        });

        Button back = new Button("Back");
        back.setStyle("-fx-background-color:black; -fx-text-fill:white; -fx-font-weight:bold;");
        back.setOnAction(e -> OrderStatusHistoryPage.display(customerId));

        HBox btnRow = new HBox(20, submit, back);
        btnRow.setAlignment(Pos.CENTER_RIGHT);

        // ── assemble ──
        root.getChildren().addAll(topBar, pageTitle, orderInfo, issueBox,
                new Label("Additional Notes:"), notes, btnRow);

        Scene scene = new Scene(new ScrollPane(root), 740, 600);
        Navigation.stage.setScene(scene);
        Navigation.stage.setTitle("Report Order Problem");
        Navigation.stage.show();
    }

    /* =========================================================
     *  Helpers
     * ========================================================= */

    private static RadioButton rb(String text, ToggleGroup g) {
        RadioButton r = new RadioButton(text); r.setToggleGroup(g); return r;
    }

    private static void alert(String msg, Alert.AlertType t) {
        Alert a = new Alert(t); a.setHeaderText(null); a.setContentText(msg); a.showAndWait();
    }

    private ReportOrderProblemPage() {} // no instances
}