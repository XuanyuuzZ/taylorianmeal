package taylorianmeal.view.pages;

import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import taylorianmeal.view.components.Navigation;
import taylorianmeal.view.pages.dashboard.MerchantDashboard;

/**
 * Simple success screen shown after a merchant submits a technical report.
 */
public final class MerchantTechnicalReportSubmittedPage {

    public static void display() {
        Label lbl = new Label("Report submitted successfully!");
        Button back = new Button("Back to Dashboard");
        back.setOnAction(e -> MerchantDashboard.display());

        VBox root = new VBox(20, lbl, back);
        root.setAlignment(Pos.CENTER);
        root.setPrefSize(400, 200);

        Navigation.stage.setScene(new Scene(root));
        Navigation.stage.setTitle("Submission Success");
        Navigation.stage.show();
    }

    private MerchantTechnicalReportSubmittedPage() {}
}