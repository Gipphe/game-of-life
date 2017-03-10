package app;

import javafx.animation.AnimationTimer;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Alert;
import javafx.scene.control.ColorPicker;
import javafx.scene.control.Slider;
import javafx.scene.control.ToggleButton;
import javafx.scene.paint.Color;

import java.net.URL;
import java.util.ResourceBundle;

public class Controller implements Initializable {
    private Grid grid;
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

    public void toggleStartStop() {
        if (startStopButton.selectedProperty().getValue()) {
            startStopButton.setText("Stop");
            start();
        } else {
            startStopButton.setText("Start");
            stop();
        }
    }

    public void resetGame(){
        this.grid= new Grid(grid.yaxis, grid.xaxis);
        aliveColor = Color.BLACK;
        deadColor = Color.WHITE;
        draw(gc);
    }

    private void start() {
        timer = getAnimationTimer(gc);
        timer.start();
    }

    private void stop() {
        timer.stop();
    }

    private AnimationTimer getAnimationTimer(GraphicsContext gcd) {
        final GraphicsContext gc = gcd;
        return new AnimationTimer() {
            private long past;

            @Override
            public void handle(long now) {
                if (now - past < frameInterval) return;
                past = now;

                grid.nextGeneration();
                draw(gc);
            }
        };
    }

    private void draw(GraphicsContext gc) {

        byte[][] gameGrid = grid.getGrid();

        for (int y = 0; y < gameGrid.length; y++) {
            byte[] row = gameGrid[y];

            for (int x = 0; x < grid.getGrid()[0].length; x++) {
                byte cell = row[x];
                if (cell == 1) gc.setFill(aliveColor);
                else gc.setFill(deadColor);
                gc.fillRect(x * scale, y * scale, scale-1, scale-1);
            }
        }
    }

    public void setAliveColor() {
        aliveColor = aliveColorPicker.getValue();
        draw(gc);
    }
    public void setDeadColor() {
        deadColor = deadColorPicker.getValue();
        draw(gc);
    }

    public void exit() {
        System.out.println("Goodbye");
        System.exit(0);
    }

    private void setFrameInterval(int newValue) {
        int mult = 1000000;
        int max = 1100 * mult;
        int min = 100 * mult;
        int step = (max - min) / 10;

        frameInterval = Math.min(max, (newValue * step) + min);
    }

    private void setScale(byte newValue) {
        this.scale=newValue;
    }

    public void nextFrame() {
        grid.nextGeneration();
        draw(gc);
    }

    private int wrap(int lim, int coord) {
        if (coord >= lim) {
            return coord - lim;
        } else if (coord < 0) {
            return coord + lim;
        }
        return coord;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        int yaxis = 20;
        int xaxis = 20;
        grid = new Grid(yaxis, xaxis);
        byte[][] foo = new byte[yaxis][xaxis];

        try {
            Pattern shape = PatternCollection.getCollection()[5];
        byte[][] pattern = shape.getPattern();
        int midy = foo.length / 2 - 3;
        int midx = foo[0].length / 2 - 3;
            for (byte[] cell : pattern) {
                int rely = midy + cell[0];
                int relx = midx + cell[1];
                rely = wrap(foo.length, rely);
                relx = wrap(foo[0].length, relx);
                foo[rely][relx] = 1;
            }
        } catch(ArrayIndexOutOfBoundsException aioobe) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Runtime Error");
            alert.setHeaderText("Pattern out of bounds!");
            alert.setContentText(aioobe + "\n\nContinuing application without initial pattern");

            alert.showAndWait();
        }
        grid.setGrid(foo);

        tickSlider.valueProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                setFrameInterval(newValue.intValue());
            }
        });

        scaleSlider.valueProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                gc.clearRect(0,0,scale*grid.yaxis,scale*grid.xaxis);
                setScale(newValue.byteValue());
                draw(gc);
            }
        });

        gc = canvas.getGraphicsContext2D();
        draw(gc);
    }
}
