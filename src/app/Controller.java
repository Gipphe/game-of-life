package app;

import RLE.ParsedPattern;
import RLE.Parser;
import model.*;
import model.Cell;
import javafx.animation.AnimationTimer;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Bounds;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.*;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;
import javafx.scene.paint.Color;
import javafx.stage.Screen;
import rules.RuleException;
import rules.RuleSet;
import rules.RulesCollection;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

import static javafx.scene.input.KeyCode.SHIFT;

public class Controller implements Initializable {
    private Board board;
    private AnimationTimer timer;
    private Color aliveColor = Color.BLACK;
    private Color deadColor = Color.WHITE;
    private double frameInterval;
    private GraphicsContext gc;
    private Pattern[] patterns = PatternCollection.getCollection();
    private double pressedX, pressedY;
    int cellWidth = 20;
    
    /**
     * Value to paste onto the cells dragged over by the user when clicking and dragging the mouse over the canvas.
     * Is set to the inverse of the value of the first cell clicked.
     */
    private byte onDragValue;
    private byte moveSpeed = 10;

    @FXML
    private Menu rulesMenu;
    @FXML
    private ColorPicker aliveColorPicker;
    @FXML
    private ColorPicker deadColorPicker;
    @FXML
    private Canvas canvas;
    @FXML
    private ToggleButton startStopButton;
    @FXML
    private Slider tickSlider;
    @FXML
    private ComboBox<String> comboBox;

    public void testButton() {
        System.out.println(board.patternToString());
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
            draw();
        } catch (IOException e) {
            showIOWarningAlert(e);

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
            draw();
        } catch (IOException e) {
            showIOWarningAlert(e);

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
            ArrayList<ArrayList<model.Cell>> currentBoard = board.getBoard();
            byte[][] newArray = new byte[currentBoard.size()][currentBoard.get(0).size()];
            for (int y = 0; y < currentBoard.size(); y++) {
                ArrayList<model.Cell> row = currentBoard.get(y);
                for (int x = 0; x < row.size(); x++) {
                    model.Cell cell = row.get(x);
                    newArray[y][x] = cell.getState();
                }
            }
            String RLEString = Parser.fromPattern(newArray);
            fileHandler.writeToFile(RLEString);

        } catch (IOException e) {
            showIOWarningAlert(e);

            e.printStackTrace();
        }
    }

    private void showIOWarningAlert(IOException e) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("404 Not found");
        alert.setContentText("File not found.");
        alert.show();
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
     * Clears the model entirely.
     */
    public void clearBoard(){
        this.board = new Board(board.getSizeX(), board.getSizeY());
        canvas.setTranslateX(0);
        canvas.setTranslateY(0);
        canvas.setScaleX(1.0);
        canvas.setScaleY(1.0);
        aliveColor = Color.BLACK;
        deadColor = Color.WHITE;
        draw();
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
                draw();
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
            moveSpeed = 50;
        }


        double currYPos = canvas.getTranslateY();
        double currXPos = canvas.getTranslateX();
        switch (event.getCode()) {
            case W:
                canvas.setTranslateY(currYPos - moveSpeed);
                break;
            case A:
                canvas.setTranslateX(currXPos - moveSpeed);
                break;
            case S:
                canvas.setTranslateY(currYPos + moveSpeed);
                break;
            case D:
                canvas.setTranslateX(currXPos + moveSpeed);
                break;
            case Q:
                startStopButton.selectedProperty().setValue(!startStopButton.selectedProperty().getValue());
                toggleStartStop();
                break;
        }
    }

    public void onKeyReleased(KeyEvent event){
        switch (event.getCode()){
            case SHIFT:
                moveSpeed = 10;
        }
    }

    /**
     * Draws the current model onto the GraphicsContext, using the aliveColor method for the value 1,
     * and deadColor method for the value 0.
     */
    private void draw() {
        GraphicsContext gcd = gc;

        ArrayList<ArrayList<model.Cell>> gameBoard = board.getBoard();
        int borderWidth = 1;
        int cellWithBorder = cellWidth - borderWidth;
        gcd.getCanvas().setHeight(cellWidth * gameBoard.size());
        gcd.getCanvas().setWidth(cellWidth * gameBoard.get(0).size());



        for (int y = 0; y < gameBoard.size(); y++) {
            ArrayList<model.Cell> row = gameBoard.get(y);

            for (int x = 0; x < board.getBoard().get(0).size(); x++) {
                model.Cell cell = row.get(x);

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
     * Exits the program.
     */
    public void exit() {
        System.exit(0);
    }

    /**
     * Sets the time between each next generation.
     *
     * @param interval int The value, from 1 to 10, indicating the requested speed of the simulation.
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
        draw();
    }

    /**
     * Handles scrolling and starts the zoom() method for each.
     * Initializes on each mouse-scroll.
     *
     * @param event (ScrollEvent)
     */
    @FXML public void scrollEvent(ScrollEvent event){
        double scrollRate = 1.5;
        if (event.getDeltaY() > 0) {
            zoom(event.getSceneX(), event.getSceneY(), scrollRate);
        } else {
            zoom(event.getSceneX(), event.getSceneY(), 1/scrollRate);
        }
    }

    /**
     * Adjusts the scale of the canvas relative to the mouse cursor.
     * If-statement stops scrolling when canvas gets too close/too far.
     *
     * @param x (double) X-position of mouse cursor
     * @param y (double) Y-position of mouse cursor
     * @param scrollRate (double) Either 0.05 or -0.05
     */
    private void zoom(double x, double y, double scrollRate) {
        double oldScale = canvas.getScaleX();
        double newScale = oldScale * scrollRate;
        if (newScale > 4) {
            return;
        } else if (newScale < 0.1) {
            return;
        }

        double relativeZoom = (newScale / oldScale) - 1;

        Bounds bounds = canvas.localToScene(canvas.getBoundsInLocal());
        double dx = (x - (bounds.getWidth() / 2 + bounds.getMinX()));
        double dy = (y - (bounds.getHeight() / 2 + bounds.getMinY()));

        // Handle relative zoom
        canvas.setTranslateX(canvas.getTranslateX() - relativeZoom * dx);
        canvas.setTranslateY(canvas.getTranslateY() - relativeZoom * dy);
        canvas.setScaleX(newScale);
        canvas.setScaleY(newScale);
    }

    /**
     * Handles clicks on the canvas.
     * Toggles alive/dead cells on click.
     *
     * @param event (MouseEvent)
     */
    @FXML
    public void onClick(MouseEvent event) {
        if (!event.isPrimaryButtonDown()){
            pressedX = event.getX();
            pressedY = event.getY();
            return;
        }
        int x = (int) event.getX() / 20;
        int y = (int) event.getY() / 20;

        if (board.getValue(x, y) == 0) {
            board.setValue(x, y, (byte) 1);
            onDragValue = 1;
            draw();
        } else {
            board.setValue(x, y, (byte) 0);
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
        if(!event.isPrimaryButtonDown()) {
            canvas.setTranslateX(canvas.getTranslateX() + event.getX() - pressedX);
            canvas.setTranslateY(canvas.getTranslateY() + event.getY() - pressedY);
            return;
        }
        int x = (int)event.getX()/20;
        int y = (int)event.getY()/20;

        try {
            board.setValue(x, y, onDragValue);
        } catch (IndexOutOfBoundsException ignored) {}

        draw();
    }

    public void setRule(String name) {
        System.out.println(name);
        board.setRuleSet(name);
    }

    /**
     * Sets the canvas width and height relative to no. of cells and size.
     * Used when starting the program and for dynamic model.
     */
    public void resetCanvasSize() {
        canvas.setHeight(board.getSizeY() * cellWidth);
        canvas.setWidth(board.getSizeX() * cellWidth);
    }

    /**
     * Creates, tests and draws the model onto the GraphicsContext. Contains Listeners for various sliders.
     */
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        int sizeX = 30;
        int sizeY = 30;
        board = new Board(sizeX, sizeY);
        aliveColorPicker.setValue(Color.BLACK);
        deadColorPicker.setValue(Color.WHITE);
        resetCanvasSize();

        ObservableList<String> patternNames = FXCollections.observableArrayList(PatternCollection.getNames());
        comboBox.setItems(patternNames);

        tickSlider.valueProperty().addListener((observable, oldValue, newValue) -> setFrameInterval(newValue.intValue()));

        comboBox.valueProperty().addListener((observable, oldValue, newValue) -> {
            clearBoard();
            setPremadePattern(newValue);
            gc.clearRect(0,0,20 * board.getSizeX(), 20 * board.getSizeY());
            draw();
        });

        // Init simulation interval with slider's default value.
        setFrameInterval(tickSlider.getValue());

        // Get main GraphicsContext for the canvas.
        gc = canvas.getGraphicsContext2D();

        // Init main animation timer for the simulation.
        timer = getAnimationTimer();

        List<RuleSet> ruleSets = RulesCollection.getCollection();
        for (RuleSet ruleSet : ruleSets) {
            String name = ruleSet.getName();
            MenuItem menuEntry = new MenuItem();
            menuEntry.setText(name);
            menuEntry.setOnAction((event) -> {
                setRule(name);
            });
            rulesMenu.getItems().add(menuEntry);
        }

        draw();
    }
}
