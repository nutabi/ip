package ibatun.ui;

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

public class IbatunGui extends Application {
    private Image ibatunImage = new Image(IbatunGui.class.getResourceAsStream("/images/ibatunPic.png"));
    private Image userImage = new Image(IbatunGui.class.getResourceAsStream("/images/userPic.png"));
    private Scene primaryScene;
    private VBox viewBox;

    @Override
    public void start(Stage primaryStage) {
        setupStage(primaryStage);
        setupPrimaryScene();

        // Fun stuff here
        DialogBox db = new DialogBox(ibatunImage, "Hello!");
        DialogBox db2 = new DialogBox(userImage, "Hi!", true);
        viewBox.getChildren().addAll(db, db2);

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
        ScrollPane pane = new ScrollPane(viewBox);
        pane.setStyle("-fx-control-inner-background: #f5f5f5;");
        pane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        pane.setFitToWidth(true);
        return pane;
    }

    private Node setupInputBox() {
        TextField userInput = new TextField();
        userInput.setPromptText("Type your message here...");
        userInput.setStyle("-fx-padding: 8; -fx-font-size: 14;");

        Button sendButton = new Button("Send");
        sendButton.setStyle("-fx-padding: 8 20; -fx-font-size: 14; -fx-min-width: 70;");

        HBox inputBox = new HBox(userInput, sendButton);
        inputBox.setStyle("-fx-padding: 10; -fx-spacing: 10;");
        HBox.setHgrow(userInput, Priority.ALWAYS);
        return inputBox;
    }
}
