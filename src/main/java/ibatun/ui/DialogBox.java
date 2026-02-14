package ibatun.ui;

import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Circle;

/**
 * A dialog box consisting of an ImageView to represent the speaker's face and a label containing text from the speaker.
 */
public class DialogBox extends HBox {
    private ImageView picture;
    private Label text;

    /**
     * Constructs a DialogBox with the specified picture and message.
     *
     * @param pic The image to be displayed
     * @param msg The message to be displayed
     */
    public DialogBox(Image pic, String msg) {
        this(pic, msg, false);
    }

    /**
     * Constructs a DialogBox with the specified picture, message, and speaker type.
     *
     * @param pic    The image to be displayed
     * @param msg    The message to be displayed
     * @param isUser Indicates if the speaker is the user
     */
    public DialogBox(Image pic, String msg, boolean isUser) {
        picture = new ImageView(pic);
        picture.setFitHeight(60.0);
        picture.setFitWidth(60.0);
        picture.setPreserveRatio(true);
        picture.getStyleClass().add("dialog-avatar");

        Circle clip = new Circle(30, 30, 30);
        picture.setClip(clip);

        text = new Label(msg);
        text.setWrapText(true);
        text.setMaxWidth(360.0);
        text.getStyleClass().add("dialog-text");

        this.setSpacing(10.0);
        this.getStyleClass().add("dialog-box");

        if (isUser) {
            this.setAlignment(Pos.TOP_RIGHT);
            this.getStyleClass().add("dialog-user");
            this.getChildren().addAll(text, picture);
        } else {
            this.setAlignment(Pos.TOP_LEFT);
            this.getStyleClass().add("dialog-bot");
            this.getChildren().addAll(picture, text);
        }

        VBox.setVgrow(this, Priority.ALWAYS);
    }
}
