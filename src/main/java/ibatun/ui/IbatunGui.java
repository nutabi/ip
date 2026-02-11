package ibatun.ui;

import ibatun.storage.JsonStore;
import ibatun.storage.TaskStore;
import ibatun.errors.IbatunException;
import ibatun.handling.Router;
import javafx.application.Application;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

/**
 * The graphical user interface for the Ibatun application.
 *
 * @author Binh
 * @version 1.0
 */
public class IbatunGui extends Application {
    private Image ibatunImage;
    private Image userImage;
    private Scene primaryScene;
    private VBox viewBox;
    private TaskStore store;
    private Router router;

    public IbatunGui() {
        this.ibatunImage = new Image(IbatunGui.class.getResourceAsStream("/images/ibatunPic.png"));
        this.userImage = new Image(IbatunGui.class.getResourceAsStream("/images/userPic.png"));
        try {
            this.store = new JsonStore("data.local.json");
        } catch (IbatunException e) {
            handleOnRespond(e.getMessage());
        }
        this.router = new Router(store, this::handleOnRespond);
    }

    @Override
    public void start(Stage primaryStage) {
        setupStage(primaryStage);
        setupPrimaryScene();

        // Greet
        handleOnRespond("Hello! I'm Ibatun. How can I assist you today?");

        primaryStage.setScene(primaryScene);
        primaryStage.show();
    }

    private void setupStage(Stage stage) {
        // Set up stage properties here
        stage.setTitle("Ibatun GUI");
        stage.setWidth(600);
        stage.setHeight(700);
    }

    private void setupPrimaryScene() {
        Node viewBoxNode = setupViewBox();
        Node inputBox = setupInputBox();
        VBox root = new VBox(viewBoxNode, inputBox);
        root.setStyle("-fx-padding: 10;");
        VBox.setVgrow(viewBoxNode, Priority.ALWAYS);

        primaryScene = new Scene(root);
    }

    private Node setupViewBox() {
        viewBox = new VBox();
        viewBox.setStyle("-fx-padding: 10; -fx-spacing: 10;");
        ScrollPane scrollPane = new ScrollPane(viewBox);
        scrollPane.setStyle("-fx-control-inner-background: #f5f5f5;");
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scrollPane.setFitToWidth(true);

        // Automatically scroll to bottom when content changes
        viewBox.heightProperty().addListener((observable) -> scrollPane.setVvalue(1.0));

        return scrollPane;
    }

    private Node setupInputBox() {
        TextField userInput = new TextField();
        userInput.setPromptText("Type your message here...");
        userInput.setStyle("-fx-padding: 8; -fx-font-size: 14;");
        userInput.setOnAction(event -> {
            String command = userInput.getText();
            if (!command.trim().isEmpty()) {
                handleOnSend(command);
                userInput.clear();
            }
        });

        Button sendButton = new Button("Send");
        sendButton.setStyle("-fx-padding: 8 20; -fx-font-size: 14; -fx-min-width: 70;");
        sendButton.setOnAction(event -> {
            String command = userInput.getText();
            if (!command.trim().isEmpty()) {
                handleOnSend(command);
                userInput.clear();
            }
        });

        HBox inputBox = new HBox(userInput, sendButton);
        inputBox.setStyle("-fx-padding: 10; -fx-spacing: 10;");
        HBox.setHgrow(userInput, Priority.ALWAYS);
        return inputBox;
    }

    private void handleOnRespond(String response) {
        addDialog(response, false);
    }

    private void handleOnSend(String command) {
        addDialog(command, true);
        if (!router.route(command.split(" "))) {
            // Exit
            System.exit(0);
        }
    }

    private void addDialog(String message, boolean isUser) {
        DialogBox dialog;
        if (isUser) {
            dialog = new DialogBox(userImage, message, true);
        } else {
            dialog = new DialogBox(ibatunImage, message);
        }
        viewBox.getChildren().add(dialog);
    }
}
