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
import taylorianmeal.LoginSession;
import taylorianmeal.service.NotificationManager;
import taylorianmeal.service.TechnicalIssueManager;
import taylorianmeal.view.components.Navigation;
import taylorianmeal.view.pages.auth.LoginPage;

/**
 * ReportTechnicalProblemPage
 * --------------------------
 * Allows the current user to file a *technical* ticket (UI bug, crash, etc.).
 *
 * • Persists the ticket into table {@code technical_issues} via TechnicalIssueManager.add(...)
 * • Pushes an optional inbox notification
 * • Shows {@link TechnicalReportSubmittedPage} on success
 * • “Back” returns to the Help-Desk main menu (Navigation.goToHelpDesk)
 */
public final class ReportTechnicalProblemPage {

    /** Static entry-point: build and show the UI */
    public static void display() {

        /* ---------- Root VBox ---------- */
        VBox root = new VBox(18);
        root.setPadding(new Insets(22));
        root.setStyle("-fx-background-color:white;");

        /* ---------- Brand bar (logo + app-name) ---------- */
        HBox brandBar = new HBox(10);
        brandBar.setAlignment(Pos.CENTER_LEFT);

        ImageView logo = new ImageView(
                new Image(ReportTechnicalProblemPage.class.getResourceAsStream(
                        "/images/taylorianmeal.jpeg")));
        logo.setFitHeight(40);
        logo.setPreserveRatio(true);

        Label brand = new Label("TaylorianMeal");
        brand.setFont(Font.font("Arial", 20));
        brand.setStyle("-fx-text-fill:red; -fx-font-weight:bold;");

        brandBar.getChildren().addAll(logo, brand);

        /* ---------- Page title ---------- */
        Label title = new Label("Report Technical Problem");
        title.setFont(Font.font("Arial", 26));
        title.setStyle("-fx-font-weight:bold;");

        /* ---------- Form controls ---------- */
        Label issueLbl = new Label("Issue Type");
        ComboBox<String> cbIssue = new ComboBox<>();
        cbIssue.getItems().addAll(
                "Loading Problem",
                "Button Not Working",
                "Crash / Error",
                "Other");
        cbIssue.setValue("Loading Problem");

        Label descLbl = new Label("Description");
        TextArea taDesc = new TextArea();
        taDesc.setPromptText("Describe the problem …");
        taDesc.setPrefRowCount(4);

        /* ---------- Action buttons ---------- */
        Button btnSubmit = new Button("Submit");
        btnSubmit.setStyle(
                "-fx-background-color:red; -fx-text-fill:white; -fx-font-weight:bold;");

        Button btnBack = new Button("Back");
        btnBack.setStyle(
                "-fx-background-color:black; -fx-text-fill:white; -fx-font-weight:bold;");

        HBox btnRow = new HBox(12, btnSubmit, btnBack);
        btnRow.setAlignment(Pos.CENTER);

        /* ---------- Event handlers ---------- */
        btnBack.setOnAction(e -> Navigation.goToHelpDesk());

        btnSubmit.setOnAction(e -> {
            String type = cbIssue.getValue();
            String desc = taDesc.getText().trim();

            if (desc.isEmpty()) {
                alert("Please enter a description.", Alert.AlertType.WARNING);
                return;
            }

            int uid = LoginSession.get().getUserId();
            if (uid <= 0) {
                alert("Not logged in. Please re-login.", Alert.AlertType.ERROR);
                LoginPage.display();
                return;
            }

            try {
                // persist the ticket
                TechnicalIssueManager.add(uid, type, desc);

                // optional inbox notification
                NotificationManager.add(
                        uid,
                        "Technical Issue Submitted",
                        "Your report (“" + type + "”) was received. Thank you!"
                );

                // success page
                TechnicalReportSubmittedPage.display();
            } catch (Exception ex) {
                ex.printStackTrace();
                alert("Failed to submit: " + ex.getMessage(), Alert.AlertType.ERROR);
            }
        });

        /* ---------- Assemble form ---------- */
        VBox form = new VBox(10,
                issueLbl, cbIssue,
                descLbl, taDesc,
                btnRow);
        form.setMaxWidth(420);
        form.setPadding(new Insets(20));

        root.getChildren().addAll(brandBar, title, form);

        /* ---------- Show scene ---------- */
        Navigation.stage.setScene(new Scene(root, 700, 520));
        Navigation.stage.setTitle("Help-Desk · Technical Issue");
        Navigation.stage.show();
    }

    /* ---------- Utility: simple alert wrapper ---------- */
    private static void alert(String msg, Alert.AlertType type) {
        Alert a = new Alert(type);
        a.setHeaderText(null);
        a.setContentText(msg);
        a.showAndWait();
    }

    /** Hide constructor (static-only class) */
    private ReportTechnicalProblemPage() {}
}
