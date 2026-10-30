package taylorianmeal.view.pages.support;

import dev.langchain4j.memory.chat.MessageWindowChatMemory;
import dev.langchain4j.model.chat.ChatModel;
import dev.langchain4j.model.openai.OpenAiChatModel;
import dev.langchain4j.service.AiServices;
import dev.langchain4j.service.SystemMessage;
import javafx.application.Platform;
import javafx.concurrent.Task;
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

/**
 * JavaFX page that embeds a LangChain4j-powered chatbot
 * into the existing TaylorianMeal UI style.
 */
public class ChatBotPage {

    public interface Assistant {
        @SystemMessage("""
            You are a helpful customer service assistant for TaylorianMeal...
            """)
        String chat(String userMessage);
    }

    private static Assistant assistant;

    public static void display() {
        String configuredKey = System.getenv("OPENAI_API_KEY");
        if (configuredKey == null || configuredKey.isBlank()) {
            new javafx.scene.control.Alert(javafx.scene.control.Alert.AlertType.INFORMATION,
                    "Set OPENAI_API_KEY in your environment to enable the optional chatbot.").showAndWait();
            return;
        }
        initAssistant();

        BorderPane root = new BorderPane();
        root.setPadding(new Insets(20));
        root.setStyle("-fx-background-color:#f9f9f9;");

        VBox centerContent = new VBox(15);
        centerContent.setAlignment(Pos.TOP_LEFT);

        // Top bar
        HBox topBar = new HBox(10);
        topBar.setAlignment(Pos.CENTER_LEFT);
        ImageView logo = new ImageView(new Image(
                ChatBotPage.class.getResourceAsStream("/images/taylorianmeal.jpeg")));
        logo.setFitHeight(40);
        logo.setPreserveRatio(true);
        Label title = new Label("TaylorianMeal");
        title.setFont(Font.font("Arial", 20));
        title.setStyle("-fx-text-fill:red;-fx-font-weight:bold;");
        topBar.getChildren().addAll(logo, title);

        // Chat title
        Label chatTitle = new Label("Customer Service Chat");
        chatTitle.setFont(Font.font("Arial", 26));
        chatTitle.setStyle("-fx-font-weight:bold;");

        // Connected banner
        Label connected = new Label("✅ Connected to TaylorianMeal Customer Service");
        connected.setStyle(
                "-fx-background-color:red;-fx-text-fill:white;-fx-font-weight:bold;-fx-font-size:16px;"
        );
        connected.setPadding(new Insets(10));
        connected.setMaxWidth(Double.MAX_VALUE);
        connected.setAlignment(Pos.CENTER);

        // Chat history
        VBox chatBox = new VBox(15);
        chatBox.setPadding(new Insets(10));
        chatBox.setStyle("-fx-background-color:white;-fx-background-radius:10;-fx-border-radius:10;");

        ScrollPane scrollPane = new ScrollPane(chatBox);
        scrollPane.setFitToWidth(true);
        scrollPane.setStyle("-fx-background-color:transparent;");

        // Initial greeting
        addBotMessage(chatBox,
                "🤖 Welcome to TaylorianMeal Customer Service!\n\n"
                        + "I'm here to help you with:\n"
                        + "• Pre-ordering questions\n"
                        + "• Payment and refunds\n"
                        + "• Pickup and delivery\n"
                        + "• Account management\n"
                        + "• Technical issues\n\n"
                        + "How can I assist you today?"
        );

        // Input area
        HBox inputBox = new HBox(10);
        inputBox.setAlignment(Pos.CENTER_LEFT);
        TextField inputField = new TextField();
        inputField.setPromptText("Type a message…");
        inputField.setPrefWidth(500);
        Button sendBtn = new Button("➤");
        sendBtn.setStyle(
                "-fx-background-color:red;-fx-text-fill:white;-fx-font-weight:bold;-fx-font-size:16px;"
        );

        Runnable sendAction = () -> {
            String userMsg = inputField.getText().trim();
            if (userMsg.isEmpty()) return;

            addUserMessage(chatBox, userMsg);
            inputField.clear();
            scrollToBottom(scrollPane);

            Task<String> task = new Task<>() {
                @Override protected String call() {
                    return assistant.chat(userMsg);
                }
            };
            task.setOnSucceeded(ev -> {
                // display whole response in one message
                addBotMessage(chatBox, task.getValue());
                scrollToBottom(scrollPane);
            });
            task.setOnFailed(ev -> {
                addBotMessage(chatBox,
                        "⚠️ Sorry, something went wrong: " + task.getException().getMessage());
                scrollToBottom(scrollPane);
            });
            new Thread(task, "llm-call").start();
        };

        sendBtn.setOnAction(e -> sendAction.run());
        inputField.setOnAction(e -> sendAction.run());
        inputBox.getChildren().addAll(inputField, sendBtn);

        centerContent.getChildren().addAll(topBar, chatTitle, connected, scrollPane, inputBox);
        root.setCenter(centerContent);

        // Back button
        Button backBtn = new Button("Back");
        backBtn.setStyle(
                "-fx-background-color:black;-fx-text-fill:white;-fx-font-weight:bold;-fx-background-radius:10;"
        );
        backBtn.setOnAction(e -> StudentDashboard.display());
        HBox bottom = new HBox(backBtn);
        bottom.setAlignment(Pos.CENTER_RIGHT);
        bottom.setPadding(new Insets(10, 0, 0, 0));
        root.setBottom(bottom);

        Scene scene = new Scene(root, 700, 500);
        Navigation.stage.setScene(scene);
        Navigation.stage.setTitle("Chat with Bot");
        Navigation.stage.show();
    }

    private static void initAssistant() {
        if (assistant != null) return;
        String apiKey = System.getenv("OPENAI_API_KEY");
        ChatModel model = OpenAiChatModel.builder()
                .apiKey(apiKey)
                .modelName("gpt-4o-mini")
                .maxTokens(2048)
                .build();
        assistant = AiServices.builder(Assistant.class)
                .chatModel(model)
                .chatMemory(MessageWindowChatMemory.withMaxMessages(10))
                .build();
    }

    private static void addUserMessage(VBox box, String text) {
        Label lbl = new Label(text);
        lbl.setWrapText(true);
        lbl.setStyle("-fx-background-color:#e1f5fe;-fx-padding:8;-fx-background-radius:10;");
        lbl.maxWidthProperty().bind(box.widthProperty().subtract(40));
        HBox hbox = new HBox(lbl);
        hbox.setAlignment(Pos.CENTER_RIGHT);
        VBox.setMargin(hbox, new Insets(5, 0, 5, 0));
        box.getChildren().add(hbox);
    }

    private static void addBotMessage(VBox box, String text) {
        Label lbl = new Label(text);
        lbl.setWrapText(true);
        lbl.setStyle("-fx-background-color:#eeeeee;-fx-padding:10;-fx-background-radius:10;");
        lbl.maxWidthProperty().bind(box.widthProperty().subtract(40));
        HBox hbox = new HBox(lbl);
        hbox.setAlignment(Pos.CENTER_LEFT);
        VBox.setMargin(hbox, new Insets(5, 0, 5, 0));
        box.getChildren().add(hbox);
    }

    private static void scrollToBottom(ScrollPane sp) {
        Platform.runLater(() -> sp.setVvalue(1.0));
    }
}
