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
     * @param message (String) Additional message that should be shown in the Content field.
     */
    public static void aioobe(ArrayIndexOutOfBoundsException aioobe, String message){
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Array Index Out Of Bounds Exception");
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
        alert.setTitle("Index Out Of Bounds Exception");
        alert.setHeaderText("Runtime Error");
        alert.setContentText(ioobe + "\n\n" + message);

        alert.showAndWait();
    }

    /**
     * Warns the user of an Illegal Argument Exception
     *
     * @param ioobe (IllegalArgumentException)
     * @param message (String) Additional message that should be shown in the Content field.
     */
    public static void iae(IllegalArgumentException ioobe, String message){
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("Illegal Argument Exception");
        alert.setContentText("No pattern found. Please import a pattern or create your own.");
        alert.show();
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

    /**
     * Returns an IO Warning when importing files from URL/RLE, and exporting.
     *
     * @param iowa (IOWarningAlert)
     */
    public static void ioe(IOException ioe) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("IO Exception");

        alert.show();
    }
}
