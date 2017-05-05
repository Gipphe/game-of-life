package view;

import javafx.geometry.Point2D;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import model.board.Board;
import model.board.Cell;

import java.util.List;

import static utils.Utils.limit;

/**
 * Controller for handling all actions, draws and method calls on the canvas itself.
 */
public class CanvasController {
    /**
     * The graphics context to wrap around.
     */
    private GraphicsContext gc;
    /**
     * The canvas within the aforementioned graphics context.
     */
    private Canvas canvas;
    /**
     * Cell width and height. Cells are always square in shape.
     */
    private double cellScale = 5;
    /**
     * Border width between each cell.
     */
    private int borderWidth = 1;
    /**
     * Panning pointer along the Y-axis.
     */
    private double panningY = 0;
    private double panningYLowerBound = 0;
    private double panningYUpperBound = 0;
    /**
     * Panning pointer along the X-axis.
     */
    private double panningX = 0;
    private double panningXLowerBound = 0;
    private double panningXUpperBound = 0;
    /**
     * Alive cells are
     */
    private Color aliveColor = Color.BLACK;
    private Color deadColor = Color.WHITE;
    private int zoomLevel = 1;

    private int firstRowIndex;
    private int firstColIndex;

    private int boardWidth;
    private int boardHeight;

    /**
     * Last X-coordinate for mouse panning.
     */
    private double lastMouseX = 0;
    /**
     * Last Y-coordinate for mouse panning.
     */
    private double lastMouseY = 0;

    /**
     * Constructor.
     * @param canvas The canvas to wrap around and manipulate.
     */
    public CanvasController(Canvas canvas) {
        this.gc = canvas.getGraphicsContext2D();
        this.canvas = canvas;
    }

    public void initialize(Board board) {
        recalculateTableBounds(board);
        resetPanningPointers(board);
        setZoomLevel(5);
        draw(board);
    }
    private int offsetRow = 0;
    private int offsetCol = 0;
    /**
     * Draws the current board onto the GraphicsContext, using the aliveColor method for the value 1,
     * and deadColor method for the value 0.
     * @param board The board to display.
     */
    public void draw(Board board) {
        // Clear the canvas first.
        gc.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());

        List<List<Boolean>> gameBoard = board.getEnumerable();
        double cellWithBorder = cellScale - borderWidth;

        recalculateOffset();

        for (int y = 0; y < rowDiff; y++) {
            List<Boolean> row = gameBoard.get(firstRowIndex + y);

            for (int x = 0; x < colDiff; x++) {
                Boolean cellState = row.get(firstColIndex + x);

                if (cellState) {
                    gc.setFill(aliveColor);
                } else {
                    gc.setFill(deadColor);
                }

                double posX = (offsetCol + x) * cellScale;
                double poxY = (offsetRow + y) * cellScale;
                gc.fillRect(posX, poxY, cellWithBorder, cellWithBorder);
            }
        }
    }

    private void recalculateOffset() {
        offsetRow = 0;
        offsetCol = 0;

        if (panningX < middleColOnTable) {
            offsetCol = (int) Math.round(middleColOnTable - panningX);
        }
        if (panningY < middleRowOnTable) {
            offsetRow = (int) Math.round(middleRowOnTable - panningY);
        }
        if (colDiff == boardWidth) {
            offsetCol = (int) Math.round(((double) canvasWidthInCells - (double) boardWidth) / 2);
        }
        if (rowDiff == boardHeight) {
            offsetRow = (int) Math.round(((double) canvasHeightInCells - (double) boardHeight) / 2);
        }
    }

    /**
     * The canvas width as measured in table cells.
     */
    private int canvasWidthInCells;
    /**
     * The canvas height as measured in table cells.
     */
    private int canvasHeightInCells;
    /**
     * Half of the canvas width in table cells.
     * @see CanvasController#canvasWidthInCells
     */
    private double halfCanvasWidthInCells;
    /**
     * Half of the canvas height in table cells.
     * @see CanvasController#canvasHeightInCells
     */
    private double halfCanvasHeightInCells;
    /**
     * Number of columns to display.
     */
    private int colDiff;
    /**
     * Number of rows to display.
     */
    private int rowDiff;

    /**
     * The middle column of the table.
     */
    private int middleColOnTable;

    /**
     * The middle row of the table.
     */
    private int middleRowOnTable;

    /**
     * Recalculates the first row, first column, canvas width in table cells, canvas height in table cells, the half
     * points on the table, number of columns and rows to display, and the panning bounds. This method is called
     * whenever the canvas zooms or pans, or when the board has a pattern inserted or it resized.
     * @param board The board to do all calculations relative to.
     */
    public void recalculateTableBounds(Board board) {
        // Get board dimensions.
        boardWidth = board.getSizeX();
        boardHeight = board.getSizeY();

        // Get canvas dimensions.
        double canvasWidth = canvas.getWidth();
        double canvasHeight = canvas.getHeight();

        // Get canvas dimensions in number of cells on the table.
        canvasWidthInCells = (int) Math.round(canvasWidth / cellScale);
        canvasHeightInCells = (int) Math.round(canvasHeight / cellScale);
        // Get half of canvas dimensions.
        halfCanvasWidthInCells = canvasWidthInCells / 2;
        halfCanvasHeightInCells = canvasHeightInCells / 2;

        // Set first drawn row and column, and last drawn row and column.
        firstRowIndex = (int) Math.round(panningY - halfCanvasHeightInCells);
        firstColIndex = (int) Math.round(panningX - halfCanvasWidthInCells);
        int lastRowIndex = (int) Math.round(panningY + halfCanvasHeightInCells);
        int lastColIndex = (int) Math.round(panningX + halfCanvasWidthInCells);

        // Allows the displayed columns and rows to overflow, making for a cleaner look along the right and bottom edge.
        int overflow = 1;

        // Limit col and row indexes to within displayable limits.
        firstRowIndex = limit(Integer.MAX_VALUE, 0, firstRowIndex);
        firstColIndex = limit(Integer.MAX_VALUE, 0, firstColIndex);
        lastRowIndex = limit(boardHeight, Integer.MIN_VALUE, lastRowIndex + overflow);
        lastColIndex = limit(boardWidth, Integer.MIN_VALUE, lastColIndex + overflow);

        // Get middle point of the table.
        middleColOnTable = (int) Math.round(((double) lastColIndex - (double) firstColIndex) / 2);
        middleRowOnTable = (int) Math.round(((double) lastRowIndex - (double) firstRowIndex) / 2);

        // Number of rows and columns to display.
        colDiff = lastColIndex - firstColIndex;
        rowDiff = lastRowIndex - firstRowIndex;

        // Half of rows and columns to display.
        double halfRowDiff = Math.floor((double) rowDiff / 2);
        double halfColDiff = Math.floor((double) colDiff / 2);

        // Correctional margin to ensure all cells are displayed when panning into the bounding border.
        int boundMargin = 1;

        // Get upper and lower bounds for panning along the X-axis.
        panningXLowerBound = halfColDiff - boundMargin;
        panningXUpperBound = boardWidth - halfColDiff + boundMargin;

        // Get upper and lower bounds for panning along the Y-axis.
        panningYLowerBound = halfRowDiff - boundMargin;
        panningYUpperBound = boardHeight - halfRowDiff + boundMargin;

        // Set panning, and let setPanning handle limiting.
        setPanningX(panningX);
        setPanningY(panningY);
    }

    /**
     * Adjusts the cell scale according to the zoom level.
     * @param zoomLevel Zoom level to set cell scale relative to. Is an integer between 1 and 10.
     */
    private void setZoomLevel(int zoomLevel) {
        int maxZoom = 10;
        int minZoom = 0;
        zoomLevel = limit(maxZoom, minZoom, zoomLevel);

        this.zoomLevel = zoomLevel;

        int min = 1;

        cellScale = min + (zoomLevel * 2);
        if (cellScale < 5) {
            borderWidth = 0;
        } else {
            borderWidth = 1;
        }
    }

    /**
     * Zoom in one zoom level.
     */
    public void zoomIn() {
        setZoomLevel(zoomLevel + 1);
    }

    /**
     * Zooms out one zoom level.
     */
    public void zoomOut() {
        setZoomLevel(zoomLevel - 1);
    }

    /**
     *
     * @param aliveColor The new color to represent alive cells with.
     */
    public void setAliveColor(Color aliveColor) {
        this.aliveColor = aliveColor;
    }

    /**
     *
     * @param deadColor The new color to represent dead cells with.
     */
    public void setDeadColor(Color deadColor) {
        this.deadColor = deadColor;
    }

    /**
     * Resets the panning pointers to the middle of the board.
     * @param board The board to set the panning pointers to the middle of.
     */
    public void resetPanningPointers(Board board) {
        int rows = board.getSizeY();
        int cols = board.getSizeX();

        panningX = cols / 2;
        panningY = rows / 2;
    }

    /**
     * Sets the panningX coordinate to the passed value, limited by the panningXUpperBound and panningXLowerBound.
     * If the bounds are inverted or equal, panningX is set to the middle of the board.
     * @param panningX Size to pan to along the Y-axis.
     */
    private void setPanningX(double panningX) {
        if (panningXLowerBound >= panningXUpperBound) {
            panningX = halfCanvasWidthInCells;
        } else {
            panningX = limit(panningXUpperBound, panningXLowerBound, panningX);
        }
        this.panningX = panningX;
    }

    /**
     * Sets the panningY coordinate to the passed value, limited by the panningYUpperBound and panningYLowerBound.
     * If the bounds are inverted or equal, panningY is set to the middle of the board.
     * @param panningY Size to pan to along the Y-axis.
     */
    private void setPanningY(double panningY) {
        if (panningYLowerBound >= panningYUpperBound) {
            panningY = halfCanvasHeightInCells;
        } else {
            panningY = limit(panningYUpperBound, panningYLowerBound, panningY);
        }
        this.panningY = panningY;
    }

    public void setLastMouseX(double lastMouseX) {
        this.lastMouseX = lastMouseX;
    }
    public void setLastMouseY(double lastMouseY) {
        this.lastMouseY = lastMouseY;
    }

    /**
     * Pans the board along the direction of the event on subsequent calls.
     * @param event Event data to glean pan direction and length from.
     */
    public void panXY(MouseEvent event) {
        double deltaCols = (lastMouseX - event.getX()) / cellScale;
        double deltaRows = (lastMouseY - event.getY()) / cellScale;

        setPanningX(panningX + deltaCols);
        setPanningY(panningY + deltaRows);
        lastMouseX = event.getX();
        lastMouseY = event.getY();
    }

    public void panDown(Board board, double deltaRows) {
        setPanningY(panningY + deltaRows);
        recalculateTableBounds(board);
        draw(board);
    }

    public void panUp(Board board, double deltaRows) {
        setPanningY(panningY - deltaRows);
        recalculateTableBounds(board);
        draw(board);
    }

    public void panRight(Board board, double deltaCols) {
        setPanningX(panningX + deltaCols);
        recalculateTableBounds(board);
        draw(board);
    }

    public void panLeft(Board board, double deltaCols) {
        setPanningX(panningX - deltaCols);
        recalculateTableBounds(board);
        draw(board);
    }

    /**
     * Calculates the board coordinate of the passed canvas coordinate.
     * @param point The canvas coordinate to find on the board.
     * @return The board coordinate of the passed canvas coordinate.
     */
    public BoardCoordinate getPointOnTable(Point2D point) {
        recalculateOffset();
        int y = (int) Math.round((point.getY() - (cellScale / 2)) / cellScale) + firstRowIndex - offsetRow;
        int x = (int) Math.round((point.getX() - (cellScale / 2)) / cellScale) + firstColIndex - offsetCol;
        return new BoardCoordinate(y, x);
    }

    /**
     *
     * @return The current cell scale. Cell scale is bound to the zoom level of the board.
     */
    public double getCellScale() {
        return cellScale;
    }

}
