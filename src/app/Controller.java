package app;

import RLE.ParsedPattern;
import RLE.Parser;
import javafx.scene.layout.BorderPane;
import javafx.stage.Modality;
import javafx.geometry.Point2D;
import javafx.scene.control.*;
import javafx.scene.layout.Pane;
import model.*;
import model.cell.ByteCell;
import model.cell.Cell;
import javafx.animation.AnimationTimer;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.canvas.Canvas;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;
import javafx.scene.paint.Color;
import rules.RuleException;
import rules.RuleSet;
import rules.RulesCollection;
import view.CanvasController;
import view.BoardCoordinate;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

import static app.AlertLibrary.iowa;
import static javafx.scene.input.KeyCode.SHIFT;

public class Controller implements Initializable {
    public Board board;
    public CanvasController canvasController;
    private AnimationTimer timer;
    private double frameInterval;
    private Pattern[] patterns = PatternCollection.getCollection();


    /**
     * Value to paste onto the cells dragged over by the user when clicking and dragging the mouse over the canvas.
     * Is set to the inverse of the value of the first cell clicked.
     */
    private boolean onDragValue;
    private byte moveSpeed = 1;

    @FXML
    private Menu rulesMenu;
    @FXML
    public ColorPicker aliveColorPicker;
    @FXML
    public ColorPicker deadColorPicker;
    @FXML
    private Canvas canvas;
    @FXML
    private ToggleButton startStopButton;
    @FXML
    private ToggleButton dynamicBoardButton;
    @FXML
    private Slider tickSlider;
    @FXML
    private ComboBox<String> comboBox;
    @FXML
    private BorderPane borderPane;
    @FXML
    private Pane canvasWrapper;

    public void testButton() {
        nextGenerationConcurrentPrintPerformance();
//        nextGenerationPrintPerformance();
        canvasController.draw(board);
    }

    public void nextGenerationPrintPerformance() {
        long x1 = System.currentTimeMillis();
        for (int i = 0; i<1; i++){
            board.nextGeneration();
        }
        long deltaX = System.currentTimeMillis() - x1;
        System.out.println("Counting time (ms): " + deltaX);
    }

    public void nextGenerationConcurrentPrintPerformance() {
        long x1 = System.currentTimeMillis();
        for (int i = 0; i<1; i++){
            board.nextGenerationConcurrent();
        }
        long deltaX = System.currentTimeMillis() - x1;
        System.out.println("Counting time (ms): " + deltaX);
    }

    private void setPremadePattern(String premadePattern) {
        for (Pattern pattern : patterns) {
            if (pattern.getName().equals(premadePattern)) {
                board.insertPattern(pattern.getPattern());

                break;
            }
        }
    }

    /**
     * Import Parser file.
     *
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
     * Import URL.
     *
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
     * Export a file.
     *
     * Displays a file browser for the user to export a file from model.
     */
    public void exportFile() {
        FileHandler fileHandler = new FileHandler();
        try {
            List<List<Cell>> currentBoard = board.getBoard();
            byte[][] newArray = new byte[currentBoard.size()][currentBoard.get(0).size()];
            for (int y = 0; y < currentBoard.size(); y++) {
                List<Cell> row = currentBoard.get(y);
                for (int x = 0; x < row.size(); x++) {
                    Cell cell = row.get(x);
                    newArray[y][x] = cell.getState().isAlive() ? (byte) 1 : 0;
                }
            }
            ParsedPattern pp = new ParsedPattern("", "", "", board.getRuleSet().getRuleString(), newArray);
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
    public void editor(){
        startStopButton.setText("Start");
        stop();

        EditorController editor;
        try {
            editor = new EditorController(board.getRuleSet(), board.patternToBoard(), this);
        } catch (IllegalArgumentException iae){
            editor = new EditorController(board.getRuleSet(), new Board(10, 10), this);
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
     * Toggles the dynamic state boolean of board depending on dy ToggleButton.
     */
    public void toggleDynamicBoard() {
        board.dynamicBoard = !board.dynamicBoard;
        if (dynamicBoardButton.selectedProperty().getValue()) {
            dynamicBoardButton.setText("Turn off");
        } else {
            dynamicBoardButton.setText("Turn on");
        }
    }


    /**
     * Clears the model entirely.
     */
    public void clearBoard() {
        this.board = new Board(board.getSizeX(), board.getSizeY());
        canvas.setTranslateX(0);
        canvas.setTranslateY(0);
        canvas.setScaleX(1.0);
        canvas.setScaleY(1.0);
        canvasController.setAliveColor(Color.BLACK);
        canvasController.setDeadColor(Color.WHITE);
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
     * Returns a new animation timer to be used
     *
     * @return (AnimationTimer) an Animation timer instance
     */
    private AnimationTimer getAnimationTimer() {
        return new AnimationTimer() {
            private long past;

            @Override
            public void handle(long now) {
                if (now - past < frameInterval) return;
                past = now;

                board.nextGeneration();
                canvasController.draw(board);
            }
        };
    }

    /**
     * Handles the user pressing a button on their keyboard.
     *
     * @param event (KeyEvent)
     */
    public void onKeyPressed(KeyEvent event){
        if(event.getCode() == SHIFT){
            moveSpeed = 5;
        }


        double currYPos = canvas.getTranslateY();
        double currXPos = canvas.getTranslateX();
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

    public void onKeyReleased(KeyEvent event){
        switch (event.getCode()){
            case SHIFT:
                moveSpeed = 1;
        }
    }

    private void logMemory() {
        System.gc();
        Runtime rt = Runtime.getRuntime();
        long usedKB = (rt.totalMemory() - rt.freeMemory()) / 1024;
        System.out.println("memory usage " + usedKB + "KB");
    }

    /**
     * Sets the aliveColor value to the current value of the aliveColorPicker, then draws the GraphicsContext.
     */
    public void setAliveColor() {
        canvasController.setAliveColor(aliveColorPicker.getValue());
        canvasController.draw(board);
    }

    /**
     * Sets the deadColor value to the current value of the deadColorPicker, then draws the GraphicsContext.
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
     *
     * @param interval (int) The value, from 1 to 10, indicating the requested speed of the simulation.
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
     * Calls the boards nextGeneration() method and re-draws the grid.
     */
    public void nextFrame() {
        board.nextGeneration();
        canvasController.draw(board);
    }

    /**
     * Handles scrolling and starts the zoom() method for each.
     * Initializes on each mouse-scroll.
     *
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
     * Handles clicks on the canvas.
     * Toggles alive/dead cells on click.
     *
     * @param event (MouseEvent)
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
        Cell cell = board.getCell(coord.getX(), coord.getY());
        if (!cell.getState().isAlive()) {
            cell.getState().setAlive(true);
            onDragValue = true;
            canvasController.draw(board);
        } else {
            cell.getState().setAlive(false);
            onDragValue = false;
            canvasController.draw(board);
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
        board.getCell(coord.getX(), coord.getY()).getState().setAlive(onDragValue);

        canvasController.draw(board);
    }

    public void setRule(String name) {
        board.setRuleSet(name);
    }

    /**
     * Creates, tests and draws the model onto the GraphicsContext. Contains Listeners for various sliders.
     */
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        int sizeX = 100;
        int sizeY = 100;
        board = new Board(sizeX, sizeY);
        aliveColorPicker.setValue(Color.BLACK);
        deadColorPicker.setValue(Color.WHITE);

        ObservableList<String> patternNames = FXCollections.observableArrayList(PatternCollection.getNames());
        comboBox.setItems(patternNames);

        tickSlider.valueProperty().addListener((observable, oldValue, newValue) -> setFrameInterval(newValue.intValue()));

        comboBox.valueProperty().addListener((observable, oldValue, newValue) -> {
            clearBoard();
            setPremadePattern(newValue);
            canvasController.recalculateTableBounds(board);
            canvasController.draw(board);
        });

        // Init simulation interval with slider's default value.
        setFrameInterval(tickSlider.getValue());

        // Get main GraphicsContext for the canvas.
        canvasController = new CanvasController(canvas);

        // Init main animation timer for the simulation.
        timer = getAnimationTimer();

        List<RuleSet> ruleSets = RulesCollection.getCollection();
        for (RuleSet ruleSet : ruleSets) {
            String name = ruleSet.getName();
            MenuItem menuEntry = new MenuItem();
            menuEntry.setText(name);
            menuEntry.setOnAction((event) -> setRule(name));
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

        board.addResizeListener((size -> canvasController.recalculateTableBounds(board)));

        canvasController.initialize(board);
    }
}
