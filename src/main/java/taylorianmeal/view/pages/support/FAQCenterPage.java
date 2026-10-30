package taylorianmeal.view.pages.support;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import taylorianmeal.view.components.Navigation;
import taylorianmeal.view.pages.dashboard.StudentDashboard;

import java.util.*;
import java.util.stream.Collectors;

public class FAQCenterPage {

    // FAQ item data structure
    private static class FAQItem {
        String question;
        String answer;
        FAQItem(String question, String answer) {
            this.question = question;
            this.answer = answer;
        }
    }
    // FAQ section data structure
    private static class FAQSection {
        String title;
        List<FAQItem> items;
        FAQSection(String title, List<FAQItem> items) {
            this.title = title;
            this.items = items;
        }
    }

    // All FAQ data
    private static final List<FAQSection> faqSections = Arrays.asList(
        new FAQSection("1. Pre-Ordering", Arrays.asList(
            new FAQItem("Q: How do I pre-order?",
                "A: Go to Pre-Order, select a restaurant, choose your meal, pick a time, and confirm payment."),
            new FAQItem("Q: Can I cancel a pre-order?",
                "A: ❌ Pre-orders cannot be cancelled once confirmed.")
        )),
        new FAQSection("2. Pickup & Delivery", Arrays.asList(
            new FAQItem("Q: Can I change my pickup time?",
                "A: ❌ Pickup time cannot be modified after confirmation."),
            new FAQItem("Q: What happens if I’m late?",
                "A: Meals are held for 30 minutes after your scheduled time.")
        )),
        new FAQSection("3. Payment & Refund", Arrays.asList(
            new FAQItem("Q: What payment methods are supported?",
                "A: We support Online Banking, cash payment, and Touch 'n Go eWallet."),
            new FAQItem("Q: Can I get a refund if there's an issue with my order?",
                "A: Yes. Refund decisions are made based on platform evaluation and you will be notified.")
        )),
        new FAQSection("4. Menu & Ordering", Arrays.asList(
            new FAQItem("Q: Can I view product details before ordering?",
                "A: Yes. All meals come with descriptions. You can check ingredients and information beforehand."),
            new FAQItem("Q: Can I choose my preferred pickup time?",
                "A: Yes. You can select your preferred pickup time during the ordering process.")
        )),
        new FAQSection("5. Account Management", Arrays.asList(
            new FAQItem("Q: How can I update my personal information?",
                "A: Go to the Profile page to update your name, email, phone number, and password."),
            new FAQItem("Q: How can I check the status of my order?",
                "A: You can view your order status in the Inbox page.")
        )),
        new FAQSection("6. Issue Reporting", Arrays.asList(
            new FAQItem("Q: Where can I report issues related to the platform or my order?",
                "A: Go to the Help Desk page, select either Technical Issue or Order Issue, fill in the form and submit.")
        ))
    );

    public static void display() {
        BorderPane root = new BorderPane();
        root.setStyle("-fx-background-color: #f9f9f9;");
        root.setPadding(new Insets(20));

        // === Top Bar with Logo ===
        HBox topBar = new HBox(10);
        topBar.setAlignment(Pos.CENTER_LEFT);
        ImageView logo = new ImageView(new Image(FAQCenterPage.class.getResourceAsStream("/images/taylorianmeal.jpeg")));
        logo.setFitHeight(40);
        logo.setPreserveRatio(true);
        Label title = new Label("TaylorianMeal");
        title.setFont(new Font("Arial", 22));
        title.setStyle("-fx-text-fill: red; -fx-font-weight: bold;");
        topBar.getChildren().addAll(logo, title);

        // === Main Title, Subtitle, Search ===
        Label mainTitle = new Label("TaylorianMeal – Frequently Asked Questions (FAQ)");
        mainTitle.setFont(Font.font("Arial", 26));
        mainTitle.setStyle("-fx-font-weight: bold;");
        Label subtitle = new Label("Find quick answers to common questions about pre-ordering, payments, and pickup");
        subtitle.setFont(Font.font("Arial", 14));
        TextField searchField = new TextField();
        searchField.setPromptText("Enter the key word...");
        searchField.setPrefWidth(400);
        VBox topSection = new VBox(10, topBar, mainTitle, subtitle, searchField);
        topSection.setPadding(new Insets(0, 0, 20, 0));
        root.setTop(topSection);

        // === FAQ Sections (dynamic content) ===
        VBox faqContent = new VBox(20);
        faqContent.setPadding(new Insets(10, 20, 10, 20));
        updateFAQContent(faqContent, faqSections, ""); // show all at first
        ScrollPane scrollPane = new ScrollPane(faqContent);
        scrollPane.setFitToWidth(true);
        scrollPane.setStyle("-fx-background-color: transparent;");
        root.setCenter(scrollPane);

        // === Search bar logic ===
        searchField.textProperty().addListener((obs, oldVal, newVal) -> {
            updateFAQContent(faqContent, faqSections, newVal);
        });

        // === Back Button at Bottom ===
        Button backBtn = new Button("Back");
        backBtn.setStyle("-fx-background-color: black; -fx-text-fill: white; -fx-font-weight: bold;");
        backBtn.setOnAction(e -> StudentDashboard.display());
        HBox bottomBox = new HBox(backBtn);
        bottomBox.setAlignment(Pos.CENTER);
        bottomBox.setPadding(new Insets(20));
        root.setBottom(bottomBox);

        Scene scene = new Scene(root, 800, 600);
        Navigation.stage.setScene(scene);
        Navigation.stage.setTitle("FAQ");
        Navigation.stage.show();
    }

    // Update FAQ content based on search keyword
    private static void updateFAQContent(VBox faqContent, List<FAQSection> allSections, String keyword) {
        faqContent.getChildren().clear();
        String lowerKeyword = keyword == null ? "" : keyword.toLowerCase();
        for (FAQSection section : allSections) {
            List<FAQItem> filtered = section.items.stream()
                .filter(item -> item.question.toLowerCase().contains(lowerKeyword) ||
                                item.answer.toLowerCase().contains(lowerKeyword))
                .collect(Collectors.toList());
            if (!filtered.isEmpty()) {
                Label sectionLabel = new Label(section.title);
                sectionLabel.setFont(Font.font("Arial", 18));
                sectionLabel.setStyle("-fx-font-weight: bold;");
                faqContent.getChildren().add(sectionLabel);
                VBox sectionBox = new VBox(10);
                for (FAQItem item : filtered) {
                    sectionBox.getChildren().add(faqItem(item.question, item.answer));
                }
                faqContent.getChildren().add(sectionBox);
            }
        }
    }

    // Helper to create a FAQ item node
    private static VBox faqItem(String question, String answer) {
        VBox box = new VBox(3);
        Label q = new Label(question);
        q.setStyle("-fx-font-weight: bold;");
        Label a = new Label(answer);
        a.setWrapText(true);
        box.getChildren().addAll(q, a);
        return box;
    }
}
