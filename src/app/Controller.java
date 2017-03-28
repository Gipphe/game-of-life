package app;

import javafx.animation.AnimationTimer;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Bounds;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;
import javafx.scene.paint.Color;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;

public class Controller implements Initializable {
    private Board board;
    private AnimationTimer timer;
    private Color aliveColor = Color.BLACK;
    private Color deadColor = Color.WHITE;
    private int frameInterval = 500000000;
    private GraphicsContext gc;
    private Pattern[] patterns = PatternCollection.getCollection();
    private double pressedX, pressedY;
    private byte onDragValue;           //if the user starts their drag on a dead cell, prints alive cells all the way through (and vice versa).

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
    private ComboBox comboBox;


    public void testButton() {
        System.out.println(board.patternToString());
    }


    private ObservableList list = FXCollections.observableArrayList (
            "Clear", "Glider", "Blinker", "Toad", "Beacon", "Pulsar",
            "Pentadecathlon", "LightweightSpaceship");

    public void setPremadePattern(String premadePattern){
        for (int i = 0; i < patterns.length; i++) {
            if (patterns[i].getName() == premadePattern) {
                board.insertPattern(patterns[i].getPattern());

                break;
            }
        }
    }

    /**
     * Import RLE file.
     */
    public void importFile() {
        FileHandler fileHandler = new FileHandler();
        try {
            String data = fileHandler.readGameBoardFromDisk();
            byte[][] newBoard = RLE.toBoard(data);
            board.insertPattern(newBoard);
            draw(gc);
        } catch (IOException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error Dialog");
            alert.setHeaderText("Look, an Error Dialog");
            alert.setContentText(e.getMessage());

            alert.showAndWait();

            e.printStackTrace();
        }
    }

    /**
     * Import URL.
     */
    public void importURL() {
        FileHandler fileHandler = new FileHandler();
        try {
            String data = fileHandler.readGameBoardFromURL();
            byte[][] newBoard = RLE.toBoard(data);
            board.insertPattern(newBoard);
            draw(gc);
        } catch (IOException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error Dialog");
            alert.setHeaderText("Look, an Error Dialog");
            alert.setContentText(e.getMessage());

            alert.showAndWait();
            e.printStackTrace();
        }
    }

    /**
     * Toggles between start() and stop() methods.
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
     * Re-creates and re-draws the initial board.
     */
    public void resetGame(){
        this.board = new Board(board.getSizeX(), board.getSizeY());
        aliveColor = Color.BLACK;
        deadColor = Color.WHITE;
        draw(gc);
    }

    /**
     * Gets and starts the GraphicContext's AnimationTimer.
     */
    private void start() {
        timer = getAnimationTimer(gc);
        timer.start();
    }

    /**
     * Stops the GraphicContext's AnimationTimer.
     */
    private void stop() {
        timer.stop();
    }

    /**
     * Counts down frame intervals, used for determining time between new generations.
     *
     * @param gcd (GraphicsContext) a GraphicsContext instance which gets drawn
     *
     * @return (AnimationTimer) an Animation timer instance
     */
    private AnimationTimer getAnimationTimer(GraphicsContext gcd) {
        final GraphicsContext gc = gcd;
        return new AnimationTimer() {
            private long past;

            @Override
            public void handle(long now) {
                if (now - past < frameInterval) return;
                past = now;

                board.nextGeneration();
                draw(gc);
            }
        };
    }

    /**
     * Draws the current board onto a GraphicsContext, using the aliveColor method for the value 1, and deadColor method for the value 0.
     *
     * @param gc (GraphicsContext) The GraphicsContext on which the board will be drawn
     */
    private void draw(GraphicsContext gc) {

        ArrayList<ArrayList<Cell>> gameBoard = board.getBoard();

        for (int y = 0; y < gameBoard.size(); y++) {
            ArrayList<Cell> row = gameBoard.get(y);

            for (int x = 0; x < board.getBoard().get(0).size(); x++) {
                Cell cell = row.get(x);
                if (cell.getState() == 1) gc.setFill(aliveColor);
                else gc.setFill(deadColor);
                gc.fillRect(x * 20, y * 20, 20-1, 20-1);
            }
        }
    }

    /**
     * Sets the aliveColor value to the current value of the aliveColorPicker, then draws the GraphicsContext.
     */
    public void setAliveColor() {
        aliveColor = aliveColorPicker.getValue();
        draw(gc);
    }

    /**
     * Sets the deadColor value to the current value of the deadColorPicker, then draws the GraphicsContext.
     */
    public void setDeadColor() {
        deadColor = deadColorPicker.getValue();
        draw(gc);
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
     * @param newValue (int) the requested value of the frame interval.
     */
    private void setFrameInterval(int newValue) {
        int mult = 100000;
        int max = 1100 * mult;
        int min = 100 * mult;
        int step = (max - min) / 10;

        frameInterval = Math.min(max, (newValue * step) + min);
    }

    /**
     * Calls the boards nextGeneration() method and re-draws the grid.
     */
    public void nextFrame() {
        board.nextGeneration();
        draw(gc);
    }

    /**
     * !!!!!!!!!!!!!!!!!!!!! METHOD TO BE ADDED TO UTILITIES CLASS !!!!!!!!!!!!!!!!!!!!!!!!
     */
    private int wrap(int lim, int coord) {
        if (coord >= lim) {
            return coord - lim;
        } else if (coord < 0) {
            return coord + lim;
        }
        return coord;
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
    public void zoom(double x, double y, double scrollRate) {
        double oldScale = canvas.getScaleX();
        double newScale = oldScale * scrollRate;
        if(newScale > 4){
            return;
        } else if(newScale < 0.1){
            return;
        }

        double relativeZoom = (newScale / oldScale) - 1;

        Bounds bounds = canvas.localToScene(canvas.getBoundsInLocal());
        double dx = (x - (bounds.getWidth() / 2 + bounds.getMinX()));
        double dy = (y - (bounds.getHeight() / 2 + bounds.getMinY()));

        canvas.setTranslateX(canvas.getTranslateX()-relativeZoom*dx);           //1. handle relative
        canvas.setTranslateY(canvas.getTranslateY()-relativeZoom*dy);           //2. zoom
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
            draw(gc);
        } else {
            board.setValue(x, y, (byte) 0);
            onDragValue = 0;
            draw(gc);
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
        } catch (IndexOutOfBoundsException e) {
            AlertLibrary.ioobe(e, "User holding LMB outside of Canvas.");
        }

        draw(gc);
    }

    /**
     * Creates, tests and draws the board onto the GraphicsContext. Contains Listeners for various sliders.
     */
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        int xaxis = 30;
        int yaxis = 12;
        board = new Board(xaxis, yaxis);
        comboBox.setItems(list);

        tickSlider.valueProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                int newInterval = 10 - newValue.intValue();
                setFrameInterval(newInterval);
            }
        });

        comboBox.valueProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {

                setPremadePattern(newValue);
                gc.clearRect(0,0,20 * board.getSizeX(), 20 * board.getSizeY());
                draw(gc);
            }
        });



        gc = canvas.getGraphicsContext2D();
        draw(gc);
    }
}
