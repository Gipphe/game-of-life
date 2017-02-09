package app;

import javafx.animation.AnimationTimer;
import javafx.fxml.FXML;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.ColorPicker;
import javafx.scene.control.ToggleButton;
import javafx.scene.paint.Color;

import java.awt.*;



public class Controller {
    private Grid grid = new Grid(15,15);
    private byte scale = 15;
    private AnimationTimer timer;
    private Color aliveColor = Color.BLACK;
    private Color deadColor = Color.WHITE;

    @FXML
    private ColorPicker aliveColorPicker;
    @FXML
    private ColorPicker deadColorPicker;
    @FXML
    private Canvas canvas;
    @FXML
    private ToggleButton startStopButton;

    public void toggleStartStop() {
        if (startStopButton.selectedProperty().getValue()) {
            start();
        } else {
            stop();
        }
    };

    public void resetGame(){
        this.grid= new Grid(15,15);
        aliveColor = Color.BLACK;
        deadColor = Color.WHITE;
    }

    public void start() {
        final GraphicsContext gc = canvas.getGraphicsContext2D();
        timer = new AnimationTimer() {
            private long past;
            @Override
            public void handle(long now) {
                if (now - past < 500000000) return;
                past = now;

                grid.tick();
                drawGame(gc);
            }
        };
        timer.start();
    }
    public void stop() {
        timer.stop();
    }

    private void drawGame(GraphicsContext gc) {
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
        GraphicsContext gc = canvas.getGraphicsContext2D();
        drawGame(gc);
    }
    public void setDeadColor() {
        deadColor = deadColorPicker.getValue();
        GraphicsContext gc = canvas.getGraphicsContext2D();
        drawGame(gc);
    }

    public void exit() {
        System.out.println("Goodbye");
        System.exit(0);
    }
}
