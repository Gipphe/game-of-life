package app;

import javafx.scene.control.Alert;

import java.io.IOException;

public class AlertLibrary {

    public AlertLibrary(){
    }

    /**
     * Returns an exception notifying the user that the program is trying to change the value of an arrays cell which is beyond the compass of the array.
     *
     * @param aioobe (ArrayIndexOutOfBoundsException)
     */
    public static void aioobe(ArrayIndexOutOfBoundsException aioobe, String message){
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Runtime Error");
        alert.setHeaderText("Pattern out of bounds!");
        alert.setContentText(aioobe + "\n\n" + message);

        alert.showAndWait();
    }

    /**
     * Returns an exception notifying the user that the program is trying to change the value of a cell which is beyond the compass of the array.
     *
     * @param ioobe (IndexOutOfBoundsException)
     * @param message (String) Additional message that should be shown in the Content field.
     */
    public static void ioobe(IndexOutOfBoundsException ioobe, String message){
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Information Message");
        alert.setHeaderText("Runtime Error");
        alert.setContentText(ioobe + "\n\n" + message);

        alert.showAndWait();
    }

    /**
     * Returns an IO Warning when importing files from URL/RLE, and exporting.
     *
     * @param iowa (IOWarningAlert)
     */
    public static void iowa(IOException iowa) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("404 Not found");
        alert.setContentText("File not found.");
        alert.show();
    }

}
