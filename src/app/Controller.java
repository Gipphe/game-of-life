package app;

import RLE.ParsedPattern;
import RLE.Parser;
import javafx.animation.AnimationTimer;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Point2D;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.*;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import model.board.ArrayListBoard;
import model.board.Board;
import model.board.Cell;
import rules.RuleException;
import rules.RuleSet;
import rules.RulesCollection;
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
import static javafx.scene.input.KeyCode.SHIFT;

/**
 * Main controller handling the game window and its inputs and outputs.
 */
public class Controller implements Initializable {
    /**
     * Actual game board that is being run.
     */
    public Board board;

    /**
     * Canvas controller handling displaying the board.
     */
    CanvasController canvasController;

    /**
     * The animation timer handling drawing the board on each nextGeneration.
     */
    private AnimationTimer timer;

    /**
     * Interval between each {@code AnimationTimer} tick.
     */
    private double frameInterval;

    /**
     * Collection of default patterns available from the drop-down box.
     */
    private Pattern[] patterns = PatternCollection.getCollection();

    /**
     * Value to paste onto the cells dragged over by the user when clicking and dragging the mouse over the canvas.
     * Is set to the inverse of the value of the first cell clicked.
     */
    private boolean onDragValue;

    /**
     * Panning speed when using WASD to pan.
     */
    private byte moveSpeed = 1;

    /**
     * Hover drop-down menu in the menu bar for selecting rules.
     */
    @FXML
    private Menu rulesMenu;

    /**
     * Color picker for selecting color for alive cells.
     */
    @FXML
    public ColorPicker aliveColorPicker;

    /**
     * Color picker for selecting color for dead cells.
     */
    @FXML
    public ColorPicker deadColorPicker;

    /**
     * Main canvas the game is drawn upon.
     */
    @FXML
    private Canvas canvas;

    /**
     * Start/Stop button that starts and halts the {@code AnimationTimer}.
     */
    @FXML
    private ToggleButton startStopButton;

    /**
     * Button for toggling between dynamic board and torus board (wrapping board).
     */
    @FXML
    private ToggleButton dynamicBoardButton;

    /**
     * Speed adjustment slider for controlling the animation speed of the game.
     */
    @FXML
    private Slider speedSlider;

    /**
     * Toggle button for multithreading functionality.
     */
    @FXML
    private ToggleButton multithreadingButton;

    /**
     * Drop-down menu for selecting one of the default patterns. Pattern is then inserted onto the middle of the board.
     */
    @FXML
    private ComboBox<String> comboBox;

    /**
     * Main parent pane for the entire window.
     */
    @FXML
    private BorderPane borderPane;

    /**
     * Wrapper pane that expands to fill available space, allowing the containing canvas to expand to available space
     * by binding the canvas' {@code widthProperty} and {@code heightProperty} to this pane's {@code widthProperty} and
     * {@code heightProperty}.
     */
    @FXML
    private Pane canvasWrapper;

    /**
     * Text field for writing the generation count.
     * @see Board#getGenCount()
     */
    @FXML
    private Text genCount;

    /**
     * Text field for writing the currently alive cells count.
     * @see Board#getAliveCount()
     */
    @FXML
    private Text aliveCount;

    /**
     * Inserts a premade pattern to the middle of the board.
     * @param premadePattern Name of the pattern to insert.
     */
    private void setPremadePattern(String premadePattern) {
        for (Pattern pattern : patterns) {
            if (pattern.getName().equals(premadePattern)) {
                board.insertPattern(pattern.getPattern());

                break;
            }
        }
    }

    /**
     * Displays a file browser for the user to select a file to be imported into the model.
     */
    public void importFile() {
        FileHandler fileHandler = new FileHandler();
        try {
            String data = fileHandler.readGameBoardFromDisk();
            if (data.length() == 0) return;
            ParsedPattern pattern = Parser.toPattern(data);
            String rule = pattern.getRule();
            if (!board.getRuleSet().isEqual(rule)) {
                throw new RuleException(board.getRuleSet().getRuleString(), rule);
            }
            byte[][] newBoard = pattern.getPattern();
            board.insertPattern(newBoard);
            canvasController.resetPanningPointers(board);
            canvasController.recalculateTableBounds(board);
            canvasController.draw(board);
        } catch (IOException e) {
            iowa(e);

            e.printStackTrace();
        }
    }

    /**
     * Displays a field for the user to input a URI pointing to an Parser file on the network, which is subsequently
     * downloaded and inserted into the model.
     */
    public void importURL() {
        FileHandler fileHandler = new FileHandler();
        try {
            String data = fileHandler.readGameBoardFromURL();
            if (data.length() == 0) return;
            ParsedPattern pattern = Parser.toPattern(data);
            String rule = pattern.getRule();
            if (!board.getRuleSet().isEqual(rule)) {
                throw new RuleException(board.getRuleSet().getRuleString(), rule);
            }
            byte[][] newBoard = pattern.getPattern();
            board.insertPattern(newBoard);
            canvasController.resetPanningPointers(board);
            canvasController.recalculateTableBounds(board);
            canvasController.draw(board);
        } catch (IOException e) {
            iowa(e);

            e.printStackTrace();
        }
    }

    /**
     * Displays a file browser for the user to export a file from model.
     */
    public void exportFile() {
        FileHandler fileHandler = new FileHandler();
        try {
            List<List<Cell>> currentBoard = board.getThisGen();
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
            ParsedPattern pp = new ParsedPattern("", "", "", dateFormat.format(date), board.getRuleSet().getRuleString(), newArray);
            String RLEString = Parser.fromPattern(pp);
            fileHandler.writeToFile(RLEString);

        } catch (IOException e) {
            iowa(e);

            e.printStackTrace();
        }
    }

    /**
     * Creates and opens the pattern editor pane.
     */
    public void editor() {
        startStopButton.setText("Start");
        stop();

        EditorController editor;
        try {
            editor = new EditorController(board.getRuleSet(), board.patternToBoard(), this);
        } catch (IllegalArgumentException iae){
            editor = new EditorController(board.getRuleSet(), new ArrayListBoard(10, 10), this);
        }

        editor.initModality(Modality.WINDOW_MODAL);
        editor.initOwner(borderPane.getScene().getWindow());
        editor.showAndWait();
    }


    /**
     * Calls the start or stop method depending on the state of the Start/Stop ToggleButton.
     */
    public void toggleStartStop() {
        if (startStopButton.selectedProperty().getValue()) {
            startStopButton.setText("Stop");
            start();
        } else {
            startStopButton.setText("Start");
            stop();
        }
    }

    /**
     * Toggles the board between being a dynamic board and a torus board (wrapping board).
     */
    public void toggleDynamicBoard() {
        board.setDynamic(!board.getDynamic());
        if (dynamicBoardButton.selectedProperty().getValue()) {
            dynamicBoardButton.setText("Turn off");
        } else {
            dynamicBoardButton.setText("Turn on");
        }
    }

    /**
     * Toggles multithreading
     */
    public void toggleMultithreading() {
        board.setMultithreading(!board.getMultithreading());
        if (multithreadingButton.selectedProperty().getValue()) {
            multithreadingButton.setText("Turn off");
        } else {
            multithreadingButton.setText("Turn on");
        }
    }


    /**
     * Clears the board entirely.
     */
    public void clearBoard() {
        board.clearBoard();
        canvasController.recalculateTableBounds(board);
        canvasController.resetPanningPointers(board);
        canvasController.draw(board);
    }

    /**
     * Starts the simulation.
     */
    private void start() {
        timer.start();
    }

    /**
     * Stops the simulation.
     */
    private void stop() {
        timer.stop();
    }

    /**
     * Creates the main animation timer.
     * @return A new animation timer, which draws the board each animation tick.
     */
    private AnimationTimer getAnimationTimer() {
        return new AnimationTimer() {
            private long past;

            @Override
            public void handle(long now) {
                if (now - past < frameInterval) return;
                past = now;

                if(board.getMultithreading()){
                    board.nextGenerationConcurrent();
                } else {
                    board.nextGeneration();
                }
                recountCellsAndGeneration();
                canvasController.recalculateTableBounds(board);
                canvasController.draw(board);
            }
        };
    }

    /**
     * Handles the user pressing a button on their keyboard.
     *
     * Pans the board if W, A, S or D are pressed.
     * Increases the panning speed if shift is pressed.
     * Toggles the simulation when Q is pressed.
     * Steps one frame forward when E is pressed.
     *
     * @param event Keyboard event.
     */
    public void onKeyPressed(KeyEvent event) {
        if (event.getCode() == SHIFT) {
            moveSpeed = 5;
        }

        switch (event.getCode()) {
            case W:
                canvasController.panUp(board, moveSpeed);
                break;
            case A:
                canvasController.panLeft(board, moveSpeed);
                break;
            case S:
                canvasController.panDown(board, moveSpeed);
                break;
            case D:
                canvasController.panRight(board, moveSpeed);
                break;
            case Q:
                startStopButton.selectedProperty().setValue(!startStopButton.selectedProperty().getValue());
                toggleStartStop();
                break;
            case E:
                nextFrame();
                break;
        }
    }

    /**
     * Sets the panning speed back to its default value on releasing shift.
     * @param event Keyboard event.
     */
    public void onKeyReleased(KeyEvent event) {
        switch (event.getCode()){
            case SHIFT:
                moveSpeed = 1;
        }
    }

    /**
     * Sets the aliveColor value to the current value of the aliveColorPicker.
     */
    public void setAliveColor() {
        canvasController.setAliveColor(aliveColorPicker.getValue());
        canvasController.draw(board);
    }

    /**
     * Sets the deadColor value to the current value of the deadColorPicker.
     */
    public void setDeadColor() {
        canvasController.setDeadColor(deadColorPicker.getValue());
        canvasController.draw(board);
    }

    /**
     * Exits the program.
     */
    public void exit() {
        System.exit(0);
    }

    /**
     * Sets the time between each next generation.
     * @param interval The value, from 1 to 10, indicating the requested speed of the simulation.
     */
    private void setFrameInterval(double interval) {
        interval += 1;
        double newValue = - Math.log10(interval) + 1;
        int multiplier = 100000;
        int max = 1750 * multiplier;
        int min = 150 * multiplier;
        int step = max - min;

        frameInterval = min + (step * newValue);
    }

    /**
     * Calls the boards nextGeneration() method and re-draws the board.
     */
    public void nextFrame() {
        board.nextGeneration();
        recountCellsAndGeneration();
        canvasController.draw(board);
    }

    /**
     * Updates the generation counter, alive cells counter and dead cells counter in the window.
     */
    private void recountCellsAndGeneration() {
        genCount.setText(String.valueOf(board.getGenCount()));
        aliveCount.setText(String.valueOf(board.getAliveCount()));
    }

    /**
     * Zooms the board in and out on scrolling.
     * @param event The scroll event passed by the canvas.
     */
    @FXML
    public void scrollEvent(ScrollEvent event) {
        if (event.getDeltaY() > 0) {
            canvasController.zoomIn();
            canvasController.recalculateTableBounds(board);
            canvasController.draw(board);
        } else {
            canvasController.zoomOut();
            canvasController.recalculateTableBounds(board);
            canvasController.draw(board);
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
                coord.getX() >= board.getSizeX() ||
                coord.getY() < 0 ||
                coord.getY() >= board.getSizeY()) {
            return;
        }
        boolean cell = board.getCellAlive(coord.getY(), coord.getX());
        if (!cell) {
            board.setCellAlive(coord.getY(), coord.getX(), true);
            onDragValue = true;
            canvasController.draw(board);
        } else {
            board.setCellAlive(coord.getY(), coord.getX(), false);
            onDragValue = false;
            canvasController.draw(board);
        }
    }

    /**
     * Toggles alive/dead cells on drag, setting all cells that are dragged over to the state determined by the initial
     * state of the cell where the click that began the drag happened.
     * @param event Mouse event where the drag event was fired.
     */
    @FXML
    public void onDrag(MouseEvent event) {
        if (event.isSecondaryButtonDown()) {
            canvasController.panXY(event);
            canvasController.recalculateTableBounds(board);
            canvasController.draw(board);
            return;
        }

        BoardCoordinate coord = canvasController.getPointOnTable(new Point2D(event.getX(), event.getY()));
        if (coord.getX() < 0 ||
                coord.getX() >= board.getSizeX() ||
                coord.getY() < 0 ||
                coord.getY() >= board.getSizeY()) {
            return;
        }
        board.setCellAlive(coord.getY(), coord.getX(), onDragValue);

        canvasController.draw(board);
    }

    /**
     * Initializes this controller by setting up the default colors, initializing the game board itself and sets up the
     * {@code canvasController}. Also registers listeners for various key events that alter the state of the program.
     */
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        int sizeX = 100;
        int sizeY = 100;
        board = new ArrayListBoard(sizeX, sizeY);
        aliveColorPicker.setValue(Color.BLACK);
        deadColorPicker.setValue(Color.WHITE);

        ObservableList<String> patternNames = FXCollections.observableArrayList(PatternCollection.getNames());
        comboBox.setItems(patternNames);

        speedSlider.valueProperty().addListener((observable, oldValue, newValue) -> setFrameInterval(newValue.intValue()));

        comboBox.valueProperty().addListener((observable, oldValue, newValue) -> {
            clearBoard();
            setPremadePattern(newValue);
            canvasController.recalculateTableBounds(board);
            canvasController.draw(board);
        });

        // Init simulation interval with slider's default value.
        setFrameInterval(speedSlider.getValue());

        // Get main GraphicsContext for the canvas.
        canvasController = new CanvasController(canvas);

        // Init main animation timer for the simulation.
        timer = getAnimationTimer();

        List<RuleSet> ruleSets = RulesCollection.getCollection();
        for (RuleSet ruleSet : ruleSets) {
            String name = ruleSet.getName();
            MenuItem menuEntry = new MenuItem();
            menuEntry.setText(name);
            menuEntry.setOnAction((event) -> board.setRuleSet(ruleSet));
            rulesMenu.getItems().add(menuEntry);
        }

        // Bind canvas' width and height to its parent Pane's width and height, and make it redraw on resize.
        canvas.widthProperty().bind(canvasWrapper.widthProperty());
        canvas.heightProperty().bind(canvasWrapper.heightProperty());
        canvas.widthProperty().addListener(e -> {
            canvasController.recalculateTableBounds(board);
            canvasController.draw(board);
        });
        canvas.heightProperty().addListener(e -> {
            canvasController.recalculateTableBounds(board);
            canvasController.draw(board);
        });

        board.addPostResizeListener(size -> {
            canvasController.recalculateTableBounds(board);
            canvasController.correctOffsetForGrowth(size);
        });

        canvasController.initialize(board);
    }
}
