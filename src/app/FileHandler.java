package app;

import javafx.scene.control.TextInputDialog;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.*;
import java.net.URL;
import java.net.URLConnection;
import java.util.Optional;

class FileHandler {

    /**
     * Reads a BufferedReader into a String.
     *
     * @param br BufferedReader to read into a String.
     * @return The entire contents of the BufferedReader.
     * @throws IOException Throws an exception if the IO operation fails.
     */
    private String bufferToString(BufferedReader br) throws IOException {
        String str;
        StringBuilder sb = new StringBuilder();

        while ((str = br.readLine()) != null) {
            sb.append(str);
            sb.append('\n');
        }
        br.close();

        return sb.toString();
    }

    /**
     * Opens a file chooser where it is possible to enter a Parser file.
     *
     * @return Parser String representing the fetched file's contents, if it exists.
     * @throws IOException Throws an exception if the IO operation fails.
     */
    String readGameBoardFromDisk() throws IOException {
        FileChooser fs = new FileChooser();
        fs.setTitle("Open");
        fs.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Text Files", "*.Parser"));
        File f = fs.showOpenDialog(new Stage());
        BufferedReader br = new BufferedReader(new FileReader(f));

        return bufferToString(br);
    }

    /**
     * Opens a text input dialog where it is possible to enter a URL.
     *
     * @return Parser String representing the fetched file's contents, if it exists.
     * @throws IOException Throws an exception if the IO operation fails.
     */
    String readGameBoardFromURL() throws IOException {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Open");
        dialog.setHeaderText(null);
        dialog.setContentText("Enter a URL:");

        Optional<String> result = dialog.showAndWait();

        if (!result.isPresent()) {
            return "!";
        }

        URL destination = new URL(result.get());
        URLConnection conn = destination.openConnection();
        BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));

        return bufferToString(br);
    }
}
