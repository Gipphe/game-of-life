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
import model.Board;
import rules.RuleSet;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class EditorController extends Stage implements Initializable {
    @FXML
    private TextField name;
    @FXML
    private TextField author;
    @FXML
    private TextField desciption;
    @FXML
    private Button saveButton;
    @FXML
    private Button closeButton;
    @FXML
    private Button updateStrip;

    private RuleSet ruleSet;

    public EditorController(RuleSet ruleSet, Board recievedPattern) {
        setTitle("Pattern Editor");

        this.ruleSet = ruleSet;

        FXMLLoader loader = new FXMLLoader(getClass().getResource("Editor.fxml"));
        loader.setController(this);

        try
        {
            setScene(new Scene(loader.load()));
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
        String ruleString = ruleSet.getRuleString();

        FileChooser fs = new FileChooser();
        fs.setTitle("Save file");
        fs.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Text Files", "*.RLE", "*.rle"));
        File f = fs.showSaveDialog(new Stage());

        if (f == null) {
            return;
        }
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        Platform.runLater(() -> saveButton.requestFocus());
    }
}
