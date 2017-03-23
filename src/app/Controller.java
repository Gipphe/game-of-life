package app;

import javafx.animation.AnimationTimer;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.*;
import javafx.scene.paint.Color;

import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;

public class Controller implements Initializable {
    private Board board;
    private byte scale = 20;
    private AnimationTimer timer;
    private Color aliveColor = Color.BLACK;
    private Color deadColor = Color.WHITE;
    private int frameInterval = 500000000;
    private GraphicsContext gc;

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
    private Slider scaleSlider;
    @FXML
    private ComboBox comboBox;
    @FXML

    private ObservableList list = FXCollections.observableArrayList (
        "Clear", "Glider", "Blinker", "Toad", "Beacon", "Pulsar",
            "Pentadecathlon", "LightweightSpaceship");

    public void setPremadePattern(String premadePattern){
        Pattern[] patterns = PatternCollection.getCollection();
        for (int i = 0; i < patterns.length; i++) {
            if (patterns[i].getName() == premadePattern) {

                board.insertPattern(patterns[i].getPattern());

                break;
            }
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

                System.out.println(board);
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
                gc.fillRect(x * scale, y * scale, scale-1, scale-1);
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
     * Sets the size of each individual cell.
     *
     * @param newValue (byte) the requested scale in pixels.
     */
    private void setScale(byte newValue) {
        this.scale=newValue;
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
     * Creates, tests and draws the board onto the GraphicsContext. Contains Listeners for various sliders.
     */
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        int xaxis = 20;
        int yaxis = 20;
        board = new Board(xaxis, yaxis);
        comboBox.setItems(list);

        tickSlider.valueProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                setFrameInterval(newValue.intValue());
            }
        });

        scaleSlider.valueProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                gc.clearRect(0,0,scale* board.getSizeX(),scale* board.getSizeY());
                setScale(newValue.byteValue());
                draw(gc);
            }
        });

        comboBox.valueProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {

                setPremadePattern(newValue);
                gc.clearRect(0,0,scale * board.getSizeX(), scale * board.getSizeY());
                draw(gc);
            }
        });

        gc = canvas.getGraphicsContext2D();
        draw(gc);
    }
}
