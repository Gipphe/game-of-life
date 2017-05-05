package app;

import javafx.scene.control.Alert;

import java.io.IOException;

public final class AlertLibrary {

    private AlertLibrary() {}

    /**
     * Displays an alert box with the passed IO exception's message.
     *
     * @param error IO exception to display.
     */
    public static void iowa(IOException error) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("IO Error");
        alert.setContentText(error.getMessage());

        alert.show();
    }
}
