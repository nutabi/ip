package ibatun.ui;

import ibatun.errors.IbatunException;
import ibatun.handling.Router;
import ibatun.storage.JsonStore;
import ibatun.storage.TaskStore;
import javafx.application.Application;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.Image;
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
    private InputBar inputBar;

    /**
     * Constructs the Ibatun GUI application.
     */
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
        root.getStyleClass().add("app-root");
        VBox.setVgrow(viewBoxNode, Priority.ALWAYS);

        primaryScene = new Scene(root);
        primaryScene.getStylesheets().add(IbatunGui.class.getResource("/ibatun/ui/ibatun.css").toExternalForm());
    }

    private Node setupViewBox() {
        viewBox = new VBox();
        viewBox.getStyleClass().add("chat-view");
        ScrollPane scrollPane = new ScrollPane(viewBox);
        scrollPane.getStyleClass().add("chat-scroll");
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scrollPane.setFitToWidth(true);

        // Automatically scroll to bottom when content changes
        viewBox.heightProperty().addListener((observable) -> scrollPane.setVvalue(1.0));

        return scrollPane;
    }

    private Node setupInputBox() {
        inputBar = new InputBar(this::handleOnSend);
        return inputBar;
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
