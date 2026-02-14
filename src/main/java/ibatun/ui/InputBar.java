package ibatun.ui;

import java.util.function.Consumer;

import javafx.beans.binding.Bindings;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;

/**
 * Provides an input bar for sending commands to the app.
 */
public class InputBar extends HBox {
    private final TextField userInput;
    private final Button sendButton;

    /**
     * Constructs an input bar with a callback to handle send events.
     *
     * @param onSend Callback invoked with the command text when send is triggered
     */
    public InputBar(Consumer<String> onSend) {
        this.userInput = new TextField();
        this.sendButton = new Button("Send it");

        getStyleClass().add("input-bar");
        userInput.getStyleClass().add("input-field");
        sendButton.getStyleClass().add("send-button");

        setSpacing(10.0);
        HBox.setHgrow(userInput, Priority.ALWAYS);

        userInput.setPromptText("Type a command... I promise to laugh with you, not at you.");

        sendButton
                .disableProperty()
                .bind(Bindings
                        .createBooleanBinding(() -> userInput.getText().trim().isEmpty(), userInput.textProperty()));

        Runnable sendAction = () -> {
            String command = userInput.getText();
            if (!command.trim().isEmpty()) {
                onSend.accept(command);
                userInput.clear();
            }
        };

        userInput.setOnAction(event -> sendAction.run());
        sendButton.setOnAction(event -> sendAction.run());

        getChildren().addAll(userInput, sendButton);
    }
}
