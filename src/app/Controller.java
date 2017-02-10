package app;

import javafx.animation.AnimationTimer;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.ColorPicker;
import javafx.scene.control.Slider;
import javafx.scene.control.ToggleButton;
import javafx.scene.paint.Color;

import java.net.URL;
import java.util.ResourceBundle;


public class Controller implements Initializable {
    private Grid grid = new Grid(15,15);
    private byte scale = 15;
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

    public void toggleStartStop() {
        if (startStopButton.selectedProperty().getValue()) {
            startStopButton.setText("Stop");
            start();
        } else {
            startStopButton.setText("Start");
            stop();
        }
    };

    public void resetGame(){
        this.grid= new Grid(15,15);
        aliveColor = Color.BLACK;
        deadColor = Color.WHITE;
        draw(gc);
    }

    public void start() {
        timer = getAnimationTimer(gc);
        timer.start();
    }
    public void stop() {
        timer.stop();
    }

    private AnimationTimer getAnimationTimer(GraphicsContext gcd) {
        final GraphicsContext gc = gcd;
        final AnimationTimer timer = new AnimationTimer() {
            private long past;

            @Override
            public void handle(long now) {
                System.out.println(frameInterval);
                if (now - past < frameInterval) return;
                past = now;

                grid.nextGeneration();
                draw(gc);
            }
        };
        return timer;
    }

    private void draw(GraphicsContext gc) {
        Canvas canvas = gc.getCanvas();
        byte[][] gameGrid = grid.getGrid();

        for (int y = 0; y < gameGrid.length; y++) {
            byte[] row = gameGrid[y];
            for (int x = 0; x < row.length; x++) {
                byte cell = row[x];
                if (cell == 1) {
                    gc.setFill(aliveColor);
                } else {
                    gc.setFill(deadColor);
                }
                gc.fillRect(x * scale, y * scale, scale, scale);
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

        int val = Math.min(max, (newValue * step) + min);
        frameInterval = val;
        System.out.println("newValue: " + newValue);
        System.out.println("Max: " + max);
        System.out.println("Min: " + min);
        System.out.println("Step: " + step);
        System.out.println("Val: " + val);
    }

    public void nextFrame() {
        grid.nextGeneration();
        draw(gc);
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        tickSlider.valueProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                setFrameInterval(newValue.intValue());
            }
        });
        gc = canvas.getGraphicsContext2D();
        draw(gc);
    }
}
