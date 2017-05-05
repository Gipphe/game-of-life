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
        fs.setTitle("Open file");
        fs.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Text Files", "*.RLE", "*.rle"));
        File f = fs.showOpenDialog(new Stage());

        if (f == null) return "";

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
        dialog.setTitle("Open URL");
        dialog.setHeaderText(null);
        dialog.setContentText("Enter a URL:");

        Optional<String> result = dialog.showAndWait();

        if (!result.isPresent()) return "";

        URL destination = new URL(result.get());
        URLConnection conn = destination.openConnection();
        BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));

        return bufferToString(br);
    }

    /**
     * Opens a file chooser where it is possible to save the model.
     *
     * @param RLEString File contents to write to file.
     * @throws IOException Throws an exception if the IO operation fails.
     */
    void writeToFile(String RLEString) throws IOException {
        FileChooser fs = new FileChooser();
        fs.setTitle("Save file");
        fs.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Text Files", "*.RLE", "*.rle"));
        File f = fs.showSaveDialog(new Stage());

        if (f == null) {
            return;
        }

        BufferedWriter bw = new BufferedWriter(new FileWriter(f));
        bw.write(RLEString);
        bw.close();
    }

    /**
     * Opens a file chooser where it is possible to save a GIF file.
     *
     * @return //TODO LEGG TIL TODO
     * @throws IOException Throws an exception if the IO operation fails.
     */
    String writeToGif() throws IOException {
        FileChooser fs = new FileChooser();
        fs.setTitle("Save file");
        fs.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Text Files", "*.GIF", "*.gif"));
        File f = fs.showSaveDialog(new Stage());

        return f.getPath();
    }
}
