package app;

import RLE.ParsedPattern;
import RLE.Parser;
import com.sun.corba.se.impl.orbutil.graph.Graph;
import javafx.animation.AnimationTimer;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.control.ColorPicker;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.scene.transform.Affine;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import lieng.GIFWriter;
import model.Board;
import model.BoundingBox;
import model.cell.ByteCell;
import model.cell.Cell;
import rules.RuleSet;
import rules.RulesCollection;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Array;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

import static app.AlertLibrary.iowa;

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
    Controller mainController;

    private int gifWidth;
    private int gifHeight;
    private int gifTimeBetweenFramesMS = 1000;
    private int gifCellSize = 20;
    private String gifFilepath = "C:\\Users\\yanis\\Desktop\\file.gif";
    private java.awt.Color gifAliveColor;
    private java.awt.Color gifDeadColor;

    @FXML
    private TextField name;
    @FXML
    private TextField author;
    @FXML
    private TextField description;
    @FXML
    private Button saveGifButton;
    @FXML
    private Button saveRleButton;
    @FXML
    private Button saveAndCloseButton;
    @FXML
    private Button closeButton;
    @FXML
    private Button updateStrip;
    @FXML
    private Canvas canvas;
    @FXML
    private Canvas strip;
    @FXML
    private ColorPicker aliveColorPicker;
    @FXML
    private ColorPicker deadColorPicker;



    /**
     * Constructor for EditorController.
     *
     * @param ruleSet
     * @param receivedBoard
     */
    public EditorController(RuleSet ruleSet, Board receivedBoard, Controller mainController) {
        aliveColor = mainController.aliveColorPicker.getValue();
        deadColor = mainController.deadColorPicker.getValue();
        gifAliveColor = new java.awt.Color(  (float) aliveColor.getRed(),
                (float) aliveColor.getGreen(),
                (float) aliveColor.getBlue(),
                (float) aliveColor.getOpacity() );
        gifDeadColor = new java.awt.Color(   (float) deadColor.getRed(),
                (float) deadColor.getGreen(),
                (float) deadColor.getBlue(),
                (float) deadColor.getOpacity() );

        this.mainController = mainController;
        setTitle("Pattern Editor");
        this.ruleSet = ruleSet;
        editorBoard = receivedBoard;
        FXMLLoader loader = new FXMLLoader(getClass().getResource("Editor.fxml"));
        loader.setController(this);
        super.setMinHeight(750);
        super.setMinWidth(650);

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
    void onSaveRleButtonAction(ActionEvent event) {
        String ruleString = ruleSet.getRuleString();

        FileHandler fileHandler = new FileHandler();
        try {
            List<List<Cell>> currentBoard = editorBoard.getBoard();
            byte[][] newArray = new byte[currentBoard.size()][currentBoard.get(0).size()];
            for (int y = 0; y < currentBoard.size(); y++) {
                List<Cell> row = currentBoard.get(y);
                for (int x = 0; x < row.size(); x++) {
                    Cell cell = row.get(x);
                    newArray[y][x] = cell.getState().isAlive() ? (byte) 1 : 0;
                }
            }
            ParsedPattern pp = new ParsedPattern(name.getText(), author.getText(), description.getText(), editorBoard.getRuleSet().getRuleString(), newArray);
            String RLEString = Parser.fromPattern(pp);
            fileHandler.writeToFile(RLEString);

        } catch (IOException e) {
            iowa(e);

            e.printStackTrace();
        }
    }

    /**
     * Transfers the editor pattern to a Pattern object and passes the pattern
     * to the Controller, placing it in the middle of the game board.
     *
     * @param event
     */
    @FXML
    void onSaveAndCloseButtonAction(ActionEvent event) {
        Pattern editorBoardExport = new Pattern("Editor Board", editorBoard.getBoard());
        mainController.clearBoard();
        mainController.board.insertPattern(editorBoardExport.getPattern());
        mainController.canvasController.draw(mainController.board);
        close();
    }

    /**
     * Closes Editor.fxml.
     *
     * @param event
     */
    @FXML
    void onCloseButtonAction(ActionEvent event) throws CloneNotSupportedException {
        close();
    }

    @FXML
    void onSaveGifButtonAction(ActionEvent event) throws Exception {    //Throws CloneNotSupportedException and IOException
        int counter = 0;

        gifWidth = editorBoard.getSizeX();
        gifHeight = editorBoard.getSizeY();
        FileHandler fileHandler = new FileHandler();
        gifFilepath = fileHandler.writeToGif();
        lieng.GIFWriter gifWriter = new GIFWriter(gifWidth*gifCellSize+1, gifHeight*gifCellSize+1, gifFilepath, gifTimeBetweenFramesMS);  //TODO_DTL make constructor input customizable to user in GUI.
        gifWriter.setBackgroundColor(gifDeadColor);
        Board clonedBoard = editorBoard.clone();
        writeGOLSequenceToGif(gifWriter, clonedBoard, counter);
        System.out.println("FERDIG!");
    }

    void writeGOLSequenceToGif(lieng.GIFWriter gifWriter, Board boardToGif, int counter) throws IOException{
        if (counter > 10) {
            return;
        }
        List<List<Cell>> gameBoardToGif = boardToGif.getBoard();
        System.out.println("Counter: " + counter);
        for (int y = 0; y < gameBoardToGif.size(); y++) {
            for (int x = 0; x < gameBoardToGif.get(0).size(); x++) {
                if (gameBoardToGif.get(y).get(x).getState().isAlive()){
                    gifWriter.fillRect(x*gifCellSize, x*gifCellSize+gifCellSize, y*gifCellSize, y*gifCellSize+gifCellSize, gifAliveColor);
                } else {
                    gifWriter.fillRect(x*gifCellSize, x*gifCellSize+gifCellSize, y*gifCellSize, y*gifCellSize+gifCellSize, gifDeadColor);
                }
            }
        }
        gifWriter.insertAndProceed();
        boardToGif.nextGeneration();
        writeGOLSequenceToGif(gifWriter, boardToGif, counter + 1);            //Recursive method
    }




    @FXML
    void updateStrip(ActionEvent event) {
        Board clonedBoard = new Board(0, 0);
        
        try {
            clonedBoard = editorBoard.clone();
        }catch (CloneNotSupportedException ignoredCNSE){

        }
        double stripCellWidth = 0;
        double stripCellHeight = 225;
        Affine xform = new Affine();
        double xpadding = 5;
        double tx = xpadding;
        if (clonedBoard.getBoard().size() > clonedBoard.getBoard().get(0).size()) {
            stripCellWidth = stripCellHeight / clonedBoard.getBoard().size();
            System.out.println("picked .size");
        } else {
            stripCellWidth = stripCellHeight / clonedBoard.getBoard().get(0).size();
            System.out.println("picked .get(0).size");
        }
        GraphicsContext gcs = strip.getGraphicsContext2D();
        gcs.clearRect(0, 0, strip.widthProperty().doubleValue(), strip.heightProperty().doubleValue());

        for (int nextGenerationCounter = 0; nextGenerationCounter < 10; nextGenerationCounter++){
            xform.setTx(tx);
            gcs.setTransform(xform);
            if (!(nextGenerationCounter == 0)) {
                clonedBoard.nextGeneration();   //We have not used multi-threading here as a user-generated pattern is probably not all that large, and thus would benefit from a single-thread nextGen-call.
            }
            drawToStrip(gcs, clonedBoard, clonedBoard.getBoard(), stripCellWidth);
            tx += stripCellHeight + xpadding;
        }

        xform.setTx(0.0);
        gcs.setTransform(xform);
    }

    public void drawToStrip(GraphicsContext gcs, Board clonedBoard, List<List<Cell>> gameBoard, double stripCellWidth){
        for (int y = 0; y < gameBoard.size(); y++) {
            List<Cell> row = gameBoard.get(y);

            for (int x = 0; x < clonedBoard.getBoard().get(0).size(); x++) {
                Cell cell = row.get(x);

                if (cell.getState().isAlive()) {
                    gcs.setFill(aliveColor);
                } else {
                    gcs.setFill(deadColor);
                }

                gcs.fillRect(x * stripCellWidth, y * stripCellWidth, stripCellWidth, stripCellWidth);
            }
        }
        gcs.setFill(Color.ORANGERED);
        gcs.fillRect(clonedBoard.getBoard().get(0).size()*stripCellWidth, 0, 5, 255); //Draws separator line between each drawn generation
    }

    public void setRule(String name) {
        editorBoard.setRuleSet(name);
    }

    /**
     * Draws the current model onto the GraphicsContext, using the aliveColor method for the value 1,
     * and deadColor method for the value 0.
     */
    private void draw() {
        GraphicsContext gcd = gc;

        List<List<Cell>> gameBoard = editorBoard.getBoard();
        int borderWidth = 1;
        int cellWithBorder = cellWidth - borderWidth;
        gcd.getCanvas().setHeight(cellWidth * gameBoard.size());
        gcd.getCanvas().setWidth(cellWidth * gameBoard.get(0).size());

        for (int y = 0; y < gameBoard.size(); y++) {
            List<Cell> row = gameBoard.get(y);

            for (int x = 0; x < editorBoard.getBoard().get(0).size(); x++) {
                Cell cell = row.get(x);

                if (cell.getState().isAlive()) {
                    gcd.setFill(aliveColor);
                } else {
                    gcd.setFill(deadColor);
                }

                gcd.fillRect(x * cellWidth, y * cellWidth, cellWithBorder, cellWithBorder);
            }
        }
    }

    /**
     * Sets the aliveColor value to the current value of the aliveColorPicker, then draws the GraphicsContext.
     */
    public void setAliveColor() {
        aliveColor = aliveColorPicker.getValue();
        draw();
    }

    /**
     * Sets the deadColor value to the current value of the deadColorPicker, then draws the GraphicsContext.
     */
    public void setDeadColor() {
        deadColor = deadColorPicker.getValue();
        draw();
    }

    /**
     * Handles clicks on the canvas.
     * Toggles alive/dead cells on click.
     *
     * @param event (MouseEvent)
     */
    /*
    @FXML
    public void onClick(MouseEvent event) {
        int x = (int) event.getX() / cellWidth;
        int y = (int) event.getY() / cellWidth;

        if (!editorBoard.getCell(x, y).getState().isAlive()) {
            editorBoard.getCell(x, y).getState().setAlive(true);
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
            editorBoard.getCell(x, y).getState().setAlive(false);;
            onDragValue = 0;
            draw();
        }
    }
    */


    /**
     * Handles "prolonged clicks" - drags.
     * Toggles alive/dead cells on drag.
     *
     * @param event (MouseEvent)
     */

    /*
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
    */

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        editorBoard.addRowTop();
        editorBoard.addRowBottom();
        editorBoard.addColLeft();
        editorBoard.addColRight();
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
