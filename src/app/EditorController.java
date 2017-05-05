package app;

import RLE.ParsedPattern;
import RLE.Parser;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Point2D;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.transform.Affine;
import javafx.stage.Modality;
import javafx.stage.Stage;
import lieng.GIFWriter;
import model.board.ArrayListBoard;
import model.board.Board;
import model.board.Cell;
import rules.RuleSet;
import view.BoardCoordinate;
import view.CanvasController;

import java.io.IOException;
import java.net.URL;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.ResourceBundle;

import static app.AlertLibrary.iowa;

/**
 * Controller class for handling the pattern editor window and its inputs and outputs.
 */
public class EditorController extends Stage implements Initializable {
    /**
     * The main board to display in the pattern editor.
     */
    private Board editorBoard;

    /**
     * Color for representing alive cells in the pattern editor.
     */
    private Color aliveColor = Color.BLACK;

    /**
     * Color for representing dead cells in the pattern editor.
     */
    private Color deadColor = Color.WHITE;

    /**
     * Main graphics context for drawing on the canvas.
     */
    private GraphicsContext gc;

    /**
     * Rule set of the pattern contained in the editor.
     */
    private RuleSet ruleSet;

    /**
     * Value to paste onto the cells dragged over by the user when clicking and dragging the mouse over the canvas.
     * Is set to the inverse of the value of the first cell clicked.
     */
    private boolean onDragValue;

    /**
     * Parent controller this editor controller was initialized from.
     */
    private Controller mainController;

    /**
     * Canvas controller handling displaying the board.
     */
    private CanvasController canvasController;

    private int gifCellSize = 20;
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
    private Pane canvasWrapper;
    @FXML
    private Canvas canvas;
    @FXML
    private Canvas strip;
    @FXML
    private BorderPane editorBorderPane;

    /**
     * Constructor.
     * Initializes with the passed rule set, board subset and parent controller.
     * @param ruleSet Rule set to initialize with.
     * @param receivedBoard Subset of the parent controller's board.
     * @param mainController Parent controller.
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

    /**
     * Saves the contained pattern to an RLE file.
     */
    public void onSaveRleButtonAction() {
        String ruleString = ruleSet.getRuleString();

        FileHandler fileHandler = new FileHandler();
        try {
            List<List<Cell>> currentBoard = editorBoard.getThisGen();
            byte[][] newArray = new byte[currentBoard.size()][currentBoard.get(0).size()];
            for (int y = 0; y < currentBoard.size(); y++) {
                List<Cell> row = currentBoard.get(y);
                for (int x = 0; x < row.size(); x++) {
                    Cell cell = row.get(x);
                    newArray[y][x] = cell.getState().isAlive() ? (byte) 1 : 0;
                }
            }
            DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm");
            Date date = new Date();
            ParsedPattern pp = new ParsedPattern(
                    name.getText(),
                    author.getText(),
                    description.getText(),
                    dateFormat.format(date),
                    editorBoard.getRuleSet().getRuleString(),
                    newArray
            );
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
     */
    public void onSaveAndCloseButtonAction() {
        Pattern editorBoardExport = new Pattern("Editor Board", editorBoard.getThisGen());
        mainController.clearBoard();
        mainController.board.insertPattern(editorBoardExport.getPattern());
        mainController.canvasController.draw(mainController.board);
        close();
    }

    /**
     * Closes the pattern editor.
     */
    public void onCloseButtonAction() {
        close();
    }

    /**
     * Exports the contained pattern as a GIF.
     * @throws Exception Throws if clone fails or if there is an IO error.
     */
    public void onSaveGifButtonAction() throws Exception {
        int counter = 0;
        int gifWidth = editorBoard.getSizeX();
        int gifHeight = editorBoard.getSizeY();
        FileHandler fileHandler = new FileHandler();
        String gifFilepath = fileHandler.writeToGif();
        Alert waitAlert = gifWaitAlert();
        waitAlert.show();
        int gifTimeBetweenFramesMS = 500;
        lieng.GIFWriter gifWriter = new GIFWriter(gifWidth *gifCellSize+1, gifHeight *gifCellSize+1, gifFilepath, gifTimeBetweenFramesMS);
        gifWriter.setBackgroundColor(gifDeadColor);
        Board clonedBoard = new ArrayListBoard(editorBoard);
        writeGOLSequenceToGif(gifWriter, clonedBoard, counter);
        gifWriter.close();
        waitAlert.close();
    }

    /**
     * Constructs a modal informing the user that the GIF is being exported.
     * @return The alert box.
     */
    private Alert gifWaitAlert() {
        Alert waitAlert = new Alert(Alert.AlertType.INFORMATION);
        waitAlert.setTitle("Loading (...)");
        waitAlert.setContentText("Saving your pattern to GIF, please wait!");
        waitAlert.setHeaderText("Creating GIF");
        waitAlert.initModality(Modality.WINDOW_MODAL);
        return waitAlert;
    }

    /**
     * Writes a number of generations of the passed board to a GIF image.
     * @param gifWriter GIF writer to utilize.
     * @param boardToGif The board to write as a GIF.
     * @param counter Number of generations to write.
     * @throws IOException Throws if there is an error in writing the GIF to disk.
     */
    public void writeGOLSequenceToGif(lieng.GIFWriter gifWriter, Board boardToGif, int counter) throws IOException{
        if (counter > 10) {
            return;
        }
        List<List<Cell>> gameBoardToGif = boardToGif.getThisGen();
        for (int y = 0; y < gameBoardToGif.size(); y++) {
            for (int x = 0; x < gameBoardToGif.get(0).size(); x++) {
                if (gameBoardToGif.get(y).get(x).getState().isAlive()){
                    gifWriter.fillRect(
                            x * gifCellSize,
                            x * gifCellSize + gifCellSize,
                            y * gifCellSize,
                            y * gifCellSize + gifCellSize,
                            gifAliveColor
                    );
                } else {
                    gifWriter.fillRect(
                            x * gifCellSize,
                            x * gifCellSize + gifCellSize,
                            y * gifCellSize,
                            y * gifCellSize + gifCellSize,
                            gifDeadColor
                    );
                }
            }
        }
        gifWriter.insertAndProceed();
        boardToGif.nextGeneration();
        writeGOLSequenceToGif(gifWriter, boardToGif, counter + 1);
    }

    /**
     * Updates the current strip with the next 10 generations of the current pattern in the editor.
     */
    public void updateStrip() {
        Board clonedBoard = new ArrayListBoard(editorBoard);
        double stripCellWidth;
        double stripCellHeight = 225;
        Affine xTransform = new Affine();
        double xPadding = 5;
        double transform = xPadding;

        if (clonedBoard.getSizeY() > clonedBoard.getSizeX()) {
            stripCellWidth = stripCellHeight / clonedBoard.getSizeY();
        } else {
            stripCellWidth = stripCellHeight / clonedBoard.getSizeX();
        }
        GraphicsContext gc = strip.getGraphicsContext2D();
        clearStrip();

        for (int nextGenCounter = 0; nextGenCounter < 10; nextGenCounter++){
            xTransform.setTx(transform);
            gc.setTransform(xTransform);
            if (nextGenCounter != 0) {
                clonedBoard.nextGenerationConcurrent();
            }
            drawToStrip(gc, clonedBoard, stripCellWidth);
            transform += stripCellHeight + xPadding;
        }

        xTransform.setTx(0.0);
        gc.setTransform(xTransform);
    }
    private void clearStrip() {
        strip.getGraphicsContext2D().clearRect(0, 0, strip.getWidth(), strip.getHeight());
    }

    /**
     * Draws a passed board as a series of generations to a canvas.
     * @param gcs Graphics context of the canvas where the Strip should be drawn
     * @param board Board to draw onto strip.
     * @param stripCellWidth Width of each strip "snapshot".
     */
    public void drawToStrip(GraphicsContext gcs, Board board, double stripCellWidth) {
        List<List<Cell>> gameBoard = board.getThisGen();
        for (int y = 0; y < gameBoard.size(); y++) {
            List<Cell> row = gameBoard.get(y);

            for (int x = 0; x < gameBoard.get(0).size(); x++) {
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
        //Draws separator line between each drawn generation
        gcs.fillRect(gameBoard.get(0).size() * stripCellWidth, 0, 5, 230);
    }

    /**
     * Draws the current model onto the GraphicsContext, using the aliveColor method for the value 1,
     * and deadColor method for the value 0.
     */
    private void draw() {
        GraphicsContext gcd = gc;

        List<List<Cell>> gameBoard = editorBoard.getThisGen();
        int borderWidth = 1;
        int cellWidth = 20;
        int cellWithBorder = cellWidth - borderWidth;
        gcd.getCanvas().setHeight(cellWidth * gameBoard.size());
        gcd.getCanvas().setWidth(cellWidth * gameBoard.get(0).size());

        for (int y = 0; y < gameBoard.size(); y++) {
            List<Cell> row = gameBoard.get(y);

            for (int x = 0; x < row.size(); x++) {
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
     * Expands the editor board if the user tries drawing over current bounds.
     * @param coordinate Contains click coordinate.
     */
    public void patternEditorExpander(BoardCoordinate coordinate) {
        if (coordinate.getX() == 0){
            editorBoard.addColLeft();
            canvasController.recalculateTableBounds(editorBoard);
            canvasController.draw(editorBoard);
        }
        if (coordinate.getY() == 0){
            editorBoard.addRowTop();
            canvasController.recalculateTableBounds(editorBoard);
            canvasController.draw(editorBoard);
        }
        if (coordinate.getX() == editorBoard.getSizeX()-1){
            editorBoard.addColRight();
            canvasController.recalculateTableBounds(editorBoard);
            canvasController.draw(editorBoard);
        }
        if (coordinate.getY() == editorBoard.getSizeY()-1){
            editorBoard.addRowBottom();
            canvasController.recalculateTableBounds(editorBoard);
            canvasController.draw(editorBoard);
        }
    }

    /**
     * Toggles alive/dead cells on click.
     * @param event Mouse event where the click happened.
     */
    @FXML
    public void onClick(MouseEvent event) {
        if (event.isSecondaryButtonDown()) {
            canvasController.setLastMouseX(event.getX());
            canvasController.setLastMouseY(event.getY());
            return;
        }

        BoardCoordinate coord = canvasController.getPointOnTable(new Point2D(event.getX(), event.getY()));
        if (coord.getX() < 0 ||
            coord.getX() >= editorBoard.getSizeX() ||
            coord.getY() < 0 ||
            coord.getY() >= editorBoard.getSizeY()) {
                return;
        }
        patternEditorExpander(coord);

        boolean cell = editorBoard.getCellAlive(coord.getY(), coord.getX());
        if (!cell) {
            editorBoard.setCellAlive(coord.getY(), coord.getX(), true);
            onDragValue = true;
            canvasController.draw(editorBoard);
        } else {
            editorBoard.setCellAlive(coord.getY(), coord.getX(), false);
            onDragValue = false;
            canvasController.draw(editorBoard);
        }
        updateStrip();
    }

    /**
     * Toggles alive/dead cells on drag, setting all cells that are dragged over to the state determined by the initial
     * state of the cell where the click that began the drag happened.
     * @param event Mouse event where the drag event was fired.
     */
    @FXML
    public void onDrag(MouseEvent event) throws IndexOutOfBoundsException {
        if (event.isSecondaryButtonDown()) {
            canvasController.panXY(event);
            canvasController.recalculateTableBounds(editorBoard);
            canvasController.draw(editorBoard);
            return;
        }

        BoardCoordinate coord = canvasController.getPointOnTable(new Point2D(event.getX(), event.getY()));
        patternEditorExpander(coord);
        editorBoard.setCellAlive(coord.getY(), coord.getX(), onDragValue);
        canvasController.draw(editorBoard);
        updateStrip();
    }

    /**
     * Zooms the board in and out on scrolling.
     * @param event The scroll event passed by the canvas.
     */
    @FXML
    public void scrollEvent(ScrollEvent event) {
        if (event.getDeltaY() > 0) {
            canvasController.zoomIn();
            canvasController.recalculateTableBounds(editorBoard);
            canvasController.draw(editorBoard);
        } else {
            canvasController.zoomOut();
            canvasController.recalculateTableBounds(editorBoard);
            canvasController.draw(editorBoard);
        }
    }

    /**
     * Initializes this controller by initializing the game board itself, adds two rows and columns to the each side
     * of the board, and sets up the {@code canvasController}. Also registers listeners for various key events that
     * alter the state of the program.
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
        updateStrip();
        canvasController = new CanvasController(canvas);

        // Bind canvas' width and height to its parent Pane's width and height, and make it redraw on resize.
        canvas.widthProperty().bind(canvasWrapper.widthProperty());
        canvas.heightProperty().bind(canvasWrapper.heightProperty());
        canvas.widthProperty().addListener(e -> {
            canvasController.recalculateTableBounds(editorBoard);
            canvasController.draw(editorBoard);
        });
        canvas.heightProperty().addListener(e -> {
            canvasController.recalculateTableBounds(editorBoard);
            canvasController.draw(editorBoard);
        });

        canvasController.initialize(editorBoard);
    }
}
