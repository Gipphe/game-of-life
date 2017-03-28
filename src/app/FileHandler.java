package app;

import javafx.scene.control.TextInputDialog;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import java.io.*;
import java.net.URL;
import java.net.URLConnection;
import java.util.Optional;

public class FileHandler {

    /**
     * Reads the file line by line and make it into a string.
     * @param br
     * @return a string.
     * @throws IOException
     */
    public String readGameBoard(BufferedReader br) throws IOException {
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
     * Opens a file chooser where it is possible to enter a RLE file.
     * @return RLE file on the board, if it exists.
     * @throws IOException
     */
    public String readGameBoardFromDisk() throws IOException {
        FileChooser fs = new FileChooser();
        fs.setTitle("Open");
        fs.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Text Files", "*.RLE"));
        File f = fs.showOpenDialog(new Stage());
        BufferedReader br = new BufferedReader(new FileReader(f));

        return readGameBoard(br);
    }

    /**
     * Opens a text input dialog where it is possible to enter a URL.
     * @return URL on the board, if it exists.
     * @throws IOException
     */
    public String readGameBoardFromURL() throws IOException {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Open");
        dialog.setHeaderText(null);
        dialog.setContentText("Enter a URL:");

        Optional<String> result = dialog.showAndWait();

        URL destination = new URL(result.get());
        URLConnection conn = destination.openConnection();
        BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));

        return readGameBoard(br);
    }
}
