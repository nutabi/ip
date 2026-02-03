package ibatun;

import ibatun.ui.IbatunGui;
import javafx.application.Application;

/**
 * Launcher class to start the Ibatun GUI application.
 *
 * @author Binh
 * @version 1.0
 */
public class GuiLauncher {
    public static void main(String[] args) {
        Application.launch(IbatunGui.class, args);
    }
}
