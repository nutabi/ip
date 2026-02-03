package ibatun.ui;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.stage.Stage;

public class IbatunGui extends Application {
    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Ibatun GUI");

        Label label = new Label("Welcome to Ibatun!");
        Scene scene = new Scene(label);

        primaryStage.setScene(scene);
        primaryStage.show();
    }
}
