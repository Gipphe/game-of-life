package app;

import javafx.fxml.FXML;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.ToggleButton;
import javafx.scene.paint.Color;

public class Controller {
    private Grid grid = new Grid();
    private byte scale = 15;

    @FXML
    private Canvas canvas;
    @FXML
    private ToggleButton startStopButton;


    public void start() {
        GraphicsContext gc = canvas.getGraphicsContext2D();
        drawGame(gc);
        grid.tick();
        System.out.println(grid);
    }
    private void drawGrid(GraphicsContext gc) {
        Canvas canvas = gc.getCanvas();
        double maxHeight = canvas.getHeight();
        double maxWidth = canvas.getWidth();
        double steps = maxWidth / scale;
        for (int i = 0; i < steps; i++) {
            int step = i * scale;
            gc.strokeLine(step,0, step, maxHeight);
        }
        steps = maxHeight / scale;
        for (int i = 0; i < steps; i++) {
            int step = i * scale;
            gc.strokeLine(0, step, maxWidth, step);
        }
    }
    private void drawGame(GraphicsContext gc) {
        Canvas canvas = gc.getCanvas();
        double maxHeight = canvas.getHeight();
        double maxWidth = canvas.getWidth();
        byte[][] gameGrid = grid.getGrid();

        for (int y = 0; y < gameGrid.length; y++) {
            byte[] row = gameGrid[y];
            for (int x = 0; x < row.length; x++) {
                byte cell = row[x];
                if (cell == 1) {
                    gc.setFill(Color.BLACK);
                } else {
                    gc.setFill(Color.WHITE);
                }
                gc.fillRect(x * scale, y * scale, scale, scale);
            }
        }
    }
    public void stop() {

    }

    public void exit() {
        System.out.println("Goodbye");
        System.exit(0);
    }
}
