package taylorianmeal.view.pages.dashboard;

import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import taylorianmeal.DatabaseManager;
import taylorianmeal.LoginSession;
import taylorianmeal.service.NotificationManager;
import taylorianmeal.service.OrderManager;
import taylorianmeal.view.components.Navigation;
import taylorianmeal.view.pages.auth.LoginPage;

import java.sql.*;

/**
 * MerchantDashboard
 * -----------------
 * Home screen for merchants.
 *
 * Left   : navigation bar
 * Centre : dynamic content panel
 *          1) Order Management  (live DB)
 *          2) Menu Management   (add / delete items)
 *          3) Help-Desk         (report technical issue → DB)
 *
 * NOTE  · Only one restaurant is hard-coded (id = 1, “Old Town” demo).
 *       · Replace RESTAURANT_ID with a lookup if you have multiple restaurants.
 */
public final class MerchantDashboard {

    /* ------------ demo constant ------------ */
    private static final int RESTAURANT_ID = 1;   // PK of “Old Town”

    /* =====================================================================
     *  Top-level entry
     * ===================================================================== */
    public static void display() {
        BorderPane root = new BorderPane();
        root.setStyle("-fx-background-color:#f9f9f9;");

        /* ---------- sidebar ---------- */
        VBox sidebar = buildSidebar();
        root.setLeft(sidebar);

        /* ---------- centre (scroll) ---------- */
        VBox centre = new VBox(10);
        centre.setPadding(new Insets(20));

        ScrollPane sc = new ScrollPane(centre);
        sc.setFitToWidth(true);
        sc.setStyle("-fx-background-color:transparent;");
        root.setCenter(sc);

        /* default view = orders */
        showOrderManagement(centre);

        /* nav button handlers */
        ((Button) sidebar.lookup("#orderBtn"))
                .setOnAction(e -> { centre.getChildren().clear(); showOrderManagement(centre); });

        ((Button) sidebar.lookup("#menuBtn"))
                .setOnAction(e -> { centre.getChildren().clear(); showMenuPage(centre); });

        ((Button) sidebar.lookup("#helpBtn"))
                .setOnAction(e -> { centre.getChildren().clear(); showHelpDeskForm(centre); });

        Navigation.stage.setScene(new Scene(root, 900, 600));
        Navigation.stage.setTitle("Merchant Dashboard");
        Navigation.stage.show();
    }

    /* =====================================================================
     *  Sidebar
     * ===================================================================== */
    private static VBox buildSidebar() {
        VBox box = new VBox(20);
        box.setPadding(new Insets(20));
        box.setPrefWidth(220);
        box.setAlignment(Pos.TOP_CENTER);
        box.setStyle("-fx-background-color:white;");

        ImageView logo = new ImageView(
                new Image(MerchantDashboard.class.getResourceAsStream("/images/taylorianmeal.jpeg")));
        logo.setFitHeight(40); logo.setPreserveRatio(true);

        Label brand = new Label("TaylorianMeal");
        brand.setFont(Font.font("Arial", 20));
        brand.setStyle("-fx-text-fill:red; -fx-font-weight:bold;");

        VBox heading = new VBox(logo, brand); heading.setAlignment(Pos.CENTER);

        Button orderBtn = navButton("Order Management"); orderBtn.setId("orderBtn");
        Button menuBtn  = navButton("Menu");              menuBtn .setId("menuBtn");
        Button helpBtn  = navButton("Help Desk");         helpBtn .setId("helpBtn");

        Region spacer = new Region(); VBox.setVgrow(spacer, Priority.ALWAYS);

        Button logout = new Button("Logout");
        logout.setPrefWidth(110);
        logout.setStyle("-fx-background-color:red; -fx-text-fill:white;");
        logout.setOnAction(e -> LoginPage.display());

        // Only add navigation buttons, spacer, and logout (no refresh button)
        box.getChildren().addAll(heading, orderBtn, menuBtn, helpBtn, spacer, logout);
        return box;
    }

    /* helper for sidebar button style */
    private static Button navButton(String txt) {
        Button b = new Button(txt);
        b.setPrefWidth(200);
        b.setAlignment(Pos.CENTER_LEFT);
        b.setStyle("-fx-background-color:transparent; -fx-font-size:16px; -fx-font-weight:bold;");
        b.setOnMouseEntered(e -> b.setStyle("-fx-background-color:#eeeeee; -fx-font-size:16px; -fx-font-weight:bold;"));
        b.setOnMouseExited (e -> b.setStyle("-fx-background-color:transparent; -fx-font-size:16px; -fx-font-weight:bold;"));
        return b;
    }

    /* =====================================================================
     * 1) ORDER MANAGEMENT
     * ===================================================================== */
    private static void showOrderManagement(VBox parent) {
        parent.getChildren().clear(); // Clear previous content

        // Section title label
        Label titleLabel = sectionTitle("Order Management");

        // Refresh button styled like 'Mark as ...' but gray, with fixed font size
        Button refreshBtn = new Button("Refresh");
        refreshBtn.setStyle(
            "-fx-background-color: #888888; " + // gray background
            "-fx-text-fill: white; " +
            "-fx-font-weight: bold; " +
            "-fx-background-radius: 8; " +
            "-fx-border-radius: 8; " +
            "-fx-font-size: 16px;" // smaller font size
        );
        refreshBtn.setPrefHeight(32);
        refreshBtn.setMinHeight(32);
        refreshBtn.setPrefWidth(110);
        refreshBtn.setMinWidth(110);
        // Do NOT set refreshBtn.setFont(...), font size is controlled by style
        refreshBtn.setOnAction(e -> showOrderManagement(parent));

        // HBox with title on the left, refresh button on the right
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        HBox topBar = new HBox(titleLabel, spacer, refreshBtn);
        topBar.setAlignment(Pos.CENTER_LEFT);
        topBar.setPadding(new Insets(0, 0, 10, 0));

        // Add top bar to parent
        parent.getChildren().add(topBar);

        ObservableList<OrderManager.OrderRecord> orders;
        try {
            orders = OrderManager.listOrdersForRestaurant(RESTAURANT_ID);
        } catch (SQLException ex) {
            ex.printStackTrace();
            parent.getChildren().add(new Label("Failed to load orders."));
            return;
        }

        // Add each order card to the parent VBox
        orders.forEach(r -> parent.getChildren().add(orderCard(r)));
    }

    /** Card UI for a single order */
    private static VBox orderCard(OrderManager.OrderRecord rec) {
        VBox card = new VBox(8);
        card.setPadding(new Insets(12));
        card.setStyle("-fx-background-color:white; -fx-border-color:#ccc; -fx-border-radius:10; -fx-background-radius:10;");

        Label info = new Label(
                "Order: " + rec.getOrderId() +
                        "\nDate : " + rec.getDate() +
                        "\nStatus: " + rec.getStatus());
        card.getChildren().add(info);

        String next = switch (rec.getStatus()) {
            case "PLACED"    -> "PREPARING";
            case "PREPARING" -> "FINISHED";
            default          -> null;
        };

        if (next != null) {
            Button btn = new Button("Mark as " + next);
            btn.setStyle("-fx-background-color:red; -fx-text-fill:white;");
            btn.setOnAction(e -> {
                try {
                    long oid = Long.parseLong(rec.getOrderId().substring(2));   // strip "TM"
                    OrderManager.updateStatus(oid, next);
                    rec.statusProperty().set(next);

                    /* notify customer */
                    int cid = fetchCustomerId(oid);
                    if (cid != -1) {
                        NotificationManager.add(
                                cid, "Order " + next,
                                "Your order " + rec.getOrderId() + " is now " + next + ".");
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
                card.getChildren().remove(btn);
            });
            card.getChildren().add(btn);
        }
        return card;
    }

    /* look up customer ID for an order */
    private static int fetchCustomerId(long orderId) throws SQLException {
        try (Connection c = DriverManager.getConnection(DatabaseManager.JDBC_URL);
             PreparedStatement ps = c.prepareStatement("SELECT customer_id FROM orders WHERE id=?")) {
            ps.setLong(1, orderId);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? rs.getInt("customer_id") : -1;
            }
        }
    }

    /* =====================================================================
     * 2) MENU MANAGEMENT  (add / delete)
     * ===================================================================== */
    private static void showMenuPage(VBox parent) {
        parent.getChildren().clear();

        /* header with “Add” */
        Label title = sectionTitle("Menu");
        Button add = new Button("Add Item");
        add.setStyle("-fx-background-color:red; -fx-text-fill:white;");
        add.setOnAction(e -> showAddItemForm(parent));

        HBox bar = new HBox(10, title, new Region(), add);
        HBox.setHgrow(bar.getChildren().get(1), Priority.ALWAYS);
        bar.setAlignment(Pos.CENTER_LEFT);
        parent.getChildren().add(bar);

        /* query menu_items */
        String sql = "SELECT id,item_name,price,description FROM menu_items WHERE restaurant_id=?";
        try (Connection c = DriverManager.getConnection(DatabaseManager.JDBC_URL);
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, RESTAURANT_ID);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    int    id = rs.getInt("id");
                    String n  = rs.getString("item_name");
                    double p  = rs.getDouble("price");
                    String d  = rs.getString("description");

                    Label info = new Label("Name: " + n + "\nPrice: " + p + " RM\nDesc : " + d);
                    info.setWrapText(true);

                    Button del = new Button("Delete");
                    del.setStyle("-fx-background-color:red; -fx-text-fill:white;");
                    del.setOnAction(e -> { deleteMenuItem(id); showMenuPage(parent); });

                    HBox row = new HBox(10, info, del);
                    row.setPadding(new Insets(10));
                    row.setStyle("-fx-background-color:#fff; -fx-border-color:#ccc; -fx-background-radius:8;");
                    HBox.setHgrow(info, Priority.ALWAYS);

                    parent.getChildren().add(row);
                }
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            parent.getChildren().add(new Label("Failed to load menu."));
        }
    }

    /* delete item */
    private static void deleteMenuItem(int id) {
        try (Connection c = DriverManager.getConnection(DatabaseManager.JDBC_URL);
             PreparedStatement ps = c.prepareStatement("DELETE FROM menu_items WHERE id=?")) {
            ps.setInt(1, id);
            ps.executeUpdate();
        } catch (SQLException ex) { ex.printStackTrace(); }
    }

    /* show add-item form */
    private static void showAddItemForm(VBox parent) {
        parent.getChildren().clear();

        TextField name = new TextField(); name.setPromptText("Name");
        TextField price= new TextField(); price.setPromptText("Price (RM)");
        TextField desc = new TextField(); desc.setPromptText("Description");

        Button save = new Button("Save");
        save.setStyle("-fx-background-color:red; -fx-text-fill:white;");
        save.setOnAction(e -> {
            if (name.getText().isBlank()||price.getText().isBlank()||desc.getText().isBlank()) {
                new Alert(Alert.AlertType.WARNING,"Fill all fields").showAndWait();
                return;
            }
            try { Double.parseDouble(price.getText().trim()); }
            catch (NumberFormatException ex){ new Alert(Alert.AlertType.ERROR,"Price must be number").showAndWait(); return; }

            addMenuItem(name.getText().trim(), Double.parseDouble(price.getText().trim()), desc.getText().trim());
            showMenuPage(parent);
        });

        Button cancel = new Button("Cancel"); cancel.setOnAction(e -> showMenuPage(parent));

        VBox form = new VBox(10,
                sectionTitle("Add New Item"),
                new Label("Name"),  name,
                new Label("Price"), price,
                new Label("Description"), desc,
                new HBox(10, cancel, save) {{ setAlignment(Pos.CENTER_LEFT); }});
        form.setPadding(new Insets(20));
        form.setStyle("-fx-background-color:#fff; -fx-border-color:#ccc; -fx-background-radius:8;");

        parent.getChildren().add(form);
    }

    /* insert item */
    private static void addMenuItem(String n,double p,String d){
        try (Connection c = DriverManager.getConnection(DatabaseManager.JDBC_URL);
             PreparedStatement ps = c.prepareStatement(
                     "INSERT INTO menu_items(restaurant_id,item_name,price,description) VALUES(?,?,?,?)")){
            ps.setInt(1, RESTAURANT_ID); ps.setString(2,n); ps.setDouble(3,p); ps.setString(4,d); ps.executeUpdate();
        }catch (SQLException ex){ ex.printStackTrace(); }
    }

    /* =====================================================================
     * 3) HELP DESK  (report → technical_issues)
     * ===================================================================== */
    private static void showHelpDeskForm(VBox parent){
        parent.getChildren().clear();

        ComboBox<String> typeBox = new ComboBox<>();
        typeBox.getItems().addAll("Loading Problem","Display Issue","Button Not Working","Other");
        typeBox.setValue("Loading Problem");

        TextArea description = new TextArea();
        description.setPromptText("Describe the problem…");
        description.setPrefHeight(100);

        Button submit = new Button("Submit");
        submit.setStyle("-fx-background-color:red; -fx-text-fill:white;");
        submit.setOnAction(e -> {
            String issueType = typeBox.getValue();
            String descTxt   = description.getText().trim();
            if (descTxt.isBlank()){
                new Alert(Alert.AlertType.WARNING,"Please enter description").showAndWait();
                return;
            }
            int reporterId = LoginSession.get().getUserId();
            try {
                DatabaseManager.reportTechnicalIssue(reporterId, issueType, descTxt);
                new Alert(Alert.AlertType.INFORMATION,"Issue submitted – thank you.").showAndWait();
                showHelpDeskForm(parent);        // clear
            } catch (SQLException ex) {
                ex.printStackTrace();
                new Alert(Alert.AlertType.ERROR,"DB error: "+ex.getMessage()).showAndWait();
            }
        });

        Button back = new Button("Back"); back.setOnAction(e -> display());

        parent.getChildren().addAll(
                sectionTitle("Report Technical Problem"),
                new Label("Issue Type"), typeBox,
                new Label("Description"), description,
                new HBox(10, submit, back) {{ setAlignment(Pos.CENTER_LEFT); }}
        );
    }

    /* -- styled heading -- */
    private static Label sectionTitle(String t){
        Label l=new Label(t); l.setFont(Font.font("Arial",20)); l.setStyle("-fx-font-weight:bold;"); return l;
    }

    private MerchantDashboard(){/*utility class*/}

}