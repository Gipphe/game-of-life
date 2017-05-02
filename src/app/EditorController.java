package app;

import javafx.animation.AnimationTimer;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import model.Board;
import model.BoundingBox;
import model.Cell;
import rules.RuleSet;
import rules.RulesCollection;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class EditorController extends Stage implements Initializable {
    private Board editorBoard;
    private Color aliveColor = Color.BLACK;
    private Color deadColor = Color.WHITE;
    private GraphicsContext gc;
    private Pattern[] patterns = PatternCollection.getCollection();
    private double pressedX, pressedY;
    int cellWidth = 20;
    private RuleSet ruleSet;
    private byte onDragValue;

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
    @FXML
    private Canvas canvas;

    public EditorController(RuleSet ruleSet, Board receivedBoard) {
        setTitle("Pattern Editor");
        this.ruleSet = ruleSet;
        editorBoard = receivedBoard;
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

    public void setRule(String name) {
        System.out.println(name);
        editorBoard.setRuleSet(name);
    }

    /**
     * Draws the current model onto the GraphicsContext, using the aliveColor method for the value 1,
     * and deadColor method for the value 0.
     */
    private void draw() {
        GraphicsContext gcd = gc;

        ArrayList<ArrayList<Cell>> gameBoard = editorBoard.getBoard();
        int borderWidth = 1;
        int cellWithBorder = cellWidth - borderWidth;
        gcd.getCanvas().setHeight(cellWidth * gameBoard.size());
        gcd.getCanvas().setWidth(cellWidth * gameBoard.get(0).size());



        for (int y = 0; y < gameBoard.size(); y++) {
            ArrayList<Cell> row = gameBoard.get(y);

            for (int x = 0; x < editorBoard.getBoard().get(0).size(); x++) {
                Cell cell = row.get(x);

                if (cell.getState() == 1) {
                    gcd.setFill(aliveColor);
                } else {
                    gcd.setFill(deadColor);
                }

                gcd.fillRect(x * cellWidth, y * cellWidth, cellWithBorder, cellWithBorder);
            }
        }
    }

    /**
     * Handles clicks on the canvas.
     * Toggles alive/dead cells on click.
     *
     * @param event (MouseEvent)
     */
    @FXML
    public void onClick(MouseEvent event) {

        int x = (int) event.getX() / cellWidth;
        int y = (int) event.getY() / 20;

        if (editorBoard.getValue(x, y) == 0) {
            editorBoard.setValue(x, y, (byte) 1);
            onDragValue = 1;
            draw();
        } else {
            editorBoard.setValue(x, y, (byte) 0);
            onDragValue = 0;
            draw();
        }
    }

    /**
     * Handles "prolonged clicks" - drags.
     * Toggles alive/dead cells on drag.
     *
     * @param event (MouseEvent)
     */
    @FXML
    public void onDrag(MouseEvent event) {
        int x = (int)event.getX() /cellWidth;
        int y = (int)event.getY()/  cellWidth;

        try {
            editorBoard.setValue(x, y, onDragValue);
        } catch (IndexOutOfBoundsException ignored) {}

        draw();
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        editorBoard.addRowTop();
        editorBoard.addRowBottom();
        editorBoard.addColLeft();
        editorBoard.addColRight();
        gc = canvas.getGraphicsContext2D();
        Platform.runLater(() -> saveButton.requestFocus());
        List<RuleSet> ruleSets = RulesCollection.getCollection();

        draw();
    }
}
