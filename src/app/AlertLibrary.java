package app;

import javafx.scene.control.Alert;

public class AlertLibrary {

    public AlertLibrary(){
    }

    /**
     * Returns an exception notifying the user that the program is trying to change the value of an arrays cell which is beyond the compass of the array.
     *
     * @param aioobe (ArrayIndexOutOfBoundsException)
     */
    public static void aioobe(ArrayIndexOutOfBoundsException aioobe){
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Runtime Error");
        alert.setHeaderText("Pattern out of bounds!");
        alert.setContentText(aioobe + "\n\nContinuing application without initial pattern");

        alert.showAndWait();
    }

    /**
     * Returns an exception notifying the user that the program is trying to change the value of a cell which is beyond the compass of the array.
     *
     * @param aioobe (IndexOutOfBoundsException)
     */
    public static void ioobe(IndexOutOfBoundsException ioobe, String message){
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Information Message");
        alert.setHeaderText("Runtime Error");
        alert.setContentText(ioobe + "\n\n" + message);

        alert.showAndWait();
    }

}
