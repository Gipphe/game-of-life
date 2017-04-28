package app;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class ControllerEditor extends Stage implements Initializable {
    @FXML
    private TextField patternName;
    @FXML
    private TextField author;
    @FXML
    private TextField desciption;
    @FXML
    private TextField rules;
    @FXML
    private Button saveButton;
    @FXML
    private Button closeButton;

    public ControllerEditor() {
        FileChooser fs = new FileChooser();
        fs.setTitle("Pattern Editor");
        fs.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Text Files", "*.RLE", "*.rle"));

        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("PatternEditor.fxml"));
        fxmlLoader.setController(this);

        try
        {
            setScene(new Scene(fxmlLoader.load()));
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    @FXML
    void onCloseButtonAction(ActionEvent event) {
        close();
    }

    @FXML
    void onSaveButtonAction(ActionEvent event) {
        FileChooser fs = new FileChooser();
        fs.setTitle("Save file");
        fs.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Text Files", "*.RLE", "*.rle"));
        File f = fs.showSaveDialog(new Stage());
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        Platform.runLater(() -> saveButton.requestFocus());
    }
}
