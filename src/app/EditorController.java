package app;

import com.sun.corba.se.impl.orbutil.graph.Graph;
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
import javafx.scene.transform.Affine;
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
    private Button saveGifButton;
    @FXML
    private Button saveRleButton;
    @FXML
    private Button closeButton;
    @FXML
    private Button updateStrip;
    @FXML
    private Canvas canvas;
    @FXML
    private Canvas strip;

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
    void onSaveGifButtonAction(ActionEvent event) {

    }

    @FXML
    void onSaveRleButtonAction(ActionEvent event) {
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

    @FXML
    void updateStrip(ActionEvent event) {
        Board clonedBoard = new Board(0, 0);
        try {
            clonedBoard = editorBoard.clone();
        }catch (CloneNotSupportedException ignoredCNSE){

        }

        GraphicsContext gcs = strip.getGraphicsContext2D();
        gcs.clearRect(0, 0, strip.widthProperty().doubleValue(), strip.heightProperty().doubleValue());
        Affine xform = new Affine();
        double tx = 5;

        for (int nextGenerationCounter = 0; nextGenerationCounter < 5; nextGenerationCounter++){
            xform.setTx(tx);
            gcs.setTransform(xform);
            clonedBoard.nextGeneration();   //We have not used multi-threading here as a user-generated pattern is probably not all that large, and thus would benefit from a single-thread nextGen-call.
            drawToStrip(gcs, clonedBoard);
        }

        xform.setTx(0.0);
        gcs.setTransform(xform);
    }

    public void drawToStrip(GraphicsContext gcs, Board clonedBoard){
        ArrayList<ArrayList<Cell>> gameBoard = clonedBoard.getBoard();
        int stripCellWidth = 0;

        if (gameBoard.size() > gameBoard.get(0).size()) {
            stripCellWidth = 140 / gameBoard.size();
        } else {
            stripCellWidth = 140 / gameBoard.get(0).size();
        }

        gcs.getCanvas().setHeight(stripCellWidth * gameBoard.size());
        gcs.getCanvas().setWidth(stripCellWidth * gameBoard.get(0).size());

        for (int y = 0; y < gameBoard.size(); y++) {
            ArrayList<Cell> row = gameBoard.get(y);

            for (int x = 0; x < clonedBoard.getBoard().get(0).size(); x++) {
                Cell cell = row.get(x);

                if (cell.getState() == 1) {
                    gcs.setFill(aliveColor);
                } else {
                    gcs.setFill(deadColor);
                }

                gcs.fillRect(x * stripCellWidth, y * stripCellWidth, stripCellWidth, stripCellWidth);
            }
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
        int y = (int) event.getY() / cellWidth;

        if (editorBoard.getValue(x, y) == 0) {
            editorBoard.setValue(x, y, (byte) 1);
            if (x == 0) {
                editorBoard.addColLeft();
            }
            if (x == editorBoard.getSizeX()-1) {
                editorBoard.addColRight();
            }
            if (y == 0) {
                editorBoard.addRowTop();
            }
            if (y == editorBoard.getSizeY()-1) {
                editorBoard.addRowBottom();
            }
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
            if (x == 0) {
                if (onDragValue == 1){
                    editorBoard.addColLeft();
                }
            }
            if (x == editorBoard.getSizeX()-1) {
                if (onDragValue == 1) {
                    editorBoard.addColRight();
                }
            }
            if (y == 0) {
                if (onDragValue == 1) {
                    editorBoard.addRowTop();
                }
            }
            if (y == editorBoard.getSizeY()-1) {
                if (onDragValue == 1) {
                    editorBoard.addRowBottom();
                }
            }
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
        Platform.runLater(() -> saveGifButton.requestFocus());
        List<RuleSet> ruleSets = RulesCollection.getCollection();

        draw();
    }
}
