/*
 * Game of Life
 * @author Jonas I.
 * @author Victor B.
 * @author Yanislav Z.
 */
package model.board;

import model.BoundingBox;
import model.Size;
import model.state.State;
import rules.RuleSet;
import view.BoardCoordinate;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;

import static utils.Utils.wrap;

/**
 * ArrayList implementation of Board. Handles the internal table as a two-dimensional ArrayList containing Cell objects.
 * Enables easier dynamic board implementation, but incurs a sizable computation overhead when iterating through the
 * table.
 */
public class ArrayListBoard implements Board {
    /**
     * The current generation of the board. Swapped around with prevGen.
     */
    private List<List<Cell>> thisGen;

    /**
     * The previous generation of the board. Swapped around with thisGen.
     */
    private List<List<Cell>> prevGen;

    /**
     * Set of cells that have the possibility of changing state in the next generation.
     */
    private Set<BoardCoordinate> cellsOfInterest;

    /**
     * Current rule set being used to evaluate cell states each generation.
     */
    private RuleSet ruleSet;

    /**
     * Worker threads.
     */
    private static List<Thread> workers = new ArrayList<>();

    /**
     * Number of threads to employ.
     */
    private int maxThreads = Runtime.getRuntime().availableProcessors();

    /**
     * {@code true} if this {@code Board} is dynamic, {@code false} otherwise.
     */
    private boolean dynamic;

    /**
     * {@code true} is multi threading is enabled, false otherwise.
     */
    private boolean multithreadingEnabled;

    /**
     * Index for initializing threads.
     * @see ArrayListBoard#createWorkers()
     */
    private int threadIndex = 0;

    /**
     * Number of alive cells in this generation.
     */
    private int aliveCount = 0;

    /**
     * Number of generations since this board's creation.
     */
    private int genCount = 0;

    /**
     * Constructor.
     * Accepts the initial sizes of thisGen.
     * @param sizeX Length of the thisGen.
     * @param sizeY Height of the thisGen.
     */
    public ArrayListBoard(int sizeX, int sizeY) {
        initBoard(sizeX, sizeY);
    }

    /**
     * Constructor.
     * Accepts a {@code Board} instance for copying. Returns an equivalent instance of the passed {@code Board} instance.
     * @param board The {@code Board} instance to copy.
     */
    public ArrayListBoard(Board board) {
        initBoard(board.getSizeX(), board.getSizeY());
        List<List<Cell>> boolBoard = board.getThisGen();
        for (int y = 0; y < boolBoard.size(); y++) {
            List<Cell> row = boolBoard.get(y);
            for (int x = 0; x < row.size(); x++) {
                Cell cell = row.get(x);
                thisGen.get(y).get(x).getState().setAlive(cell.getState().isAlive());
            }
        }
        this.dynamic = board.getDynamic();
        this.multithreadingEnabled = board.getMultithreading();
    }

    /**
     * nextGeneration with multi-threading enabled. Reads no. of processors and distributes workload accordingly.
     * Provides a performance increase of roughly 20%.
     */
    @Override
    public void nextGenerationConcurrent() {
        genCount++;
        createWorkers();
        try {
            runWorkers();
        } catch (InterruptedException ie){
            ie.printStackTrace();
        }
        workers.clear();
        threadIndex = 0;
        if (dynamic) {
            postGenerationGrow();
        }
    }

    /**
     * Evaluates the board relative to the passed {@code threadIndex}.
     * @param threadIndex The index of the instantiated thread.
     * @param oldBoard The previous generation to calculate from.
     */
    private void evaluateChunkSize(int threadIndex, List<List<Cell>> oldBoard) {
        int blockSize = oldBoard.size() / maxThreads;
        int toY = blockSize * threadIndex;
        // this if-sentence makes the last thread take care of all remaining rows of the current thisGen, ensuring that
        // the thisGen y-value does NOT need to be a factorial of maxThreads.
        if (threadIndex == maxThreads) {
            toY = oldBoard.size();
        }
        int fromY = blockSize * (threadIndex - 1);

        Set<BoardCoordinate> interestingCells = getCellsOfInterest(oldBoard, fromY, toY);

        for (BoardCoordinate coordinate : interestingCells) {
            Cell cell = oldBoard.get(coordinate.getY()).get(coordinate.getX());
            int numNeighbors = neighbors(oldBoard, coordinate);
            State newState = ruleSet.getNewState(cell.getState(), numNeighbors);
            State cellState = thisGen.get(coordinate.getY()).get(coordinate.getX()).getState();
            if (!cellState.isAlive() && newState.isAlive() || (cellState.isAlive() && newState.isAlive())) {
                aliveCount++;
            }
            cellState.setAlive(newState.isAlive());
        }
    }

    /**
     * Retrieves the next threadIndex.
     */
    private synchronized int getThreadIndex() {
        threadIndex++;
        return threadIndex;
    }

    /**
     * Creates workers and assigns their tasks.
     */
    private void createWorkers() {
        List<List<Cell>> temp = thisGen;
        thisGen = prevGen;
        prevGen = temp;
        thisGen = killBoard(thisGen);

        for(int i = 1; i <= maxThreads; i++) {
            workers.add(new Thread(() -> {
                int threadIndex = getThreadIndex();
                evaluateChunkSize(threadIndex, prevGen);
            }));
        }
    }

    /**
     * Runs all available workers.
     * @throws InterruptedException Throws if thread execution was interrupted.
     */
    private static void runWorkers() throws InterruptedException {
        for(Thread t : workers) {
            t.start();
        }

        for(Thread t : workers) {
            t.join();
        }
    }

    /**
     * Initializes thisGen and prevGen as empty tables with the passed sizes.
     * @param sizeX Number of columns to initialize with.
     * @param sizeY Number of rows to initialize with.
     */
    private void initBoard(int sizeX, int sizeY) {
        ruleSet = rules.RulesCollection.getByName("Conway");
        cellsOfInterest = new HashSet<>();

        prevGen = new ArrayList<>(sizeY);
        thisGen = new ArrayList<>(sizeY);
        for (int i = 0; i < sizeY; i++) {
            List<Cell> row = new ArrayList<>(sizeX);
            List<Cell> nextRow = new ArrayList<>(sizeX);
            thisGen.add(row);
            prevGen.add(nextRow);
            for (int j = 0; j < sizeX; j++) {
                row.add(new ByteCell());
                nextRow.add(new ByteCell());
            }
        }
    }

    /**
     * Doubles the number of rows of both thisGen and prevGen.
     */
    private void doubleRows() {
        int currRowCount = thisGen.size();
        for (int i = 0; i < currRowCount; i++) {
            int cols = thisGen.get(0).size();
            List<Cell> row = new ArrayList<>(cols);
            List<Cell> prevRow = new ArrayList<>(cols);
            for (int j = 0; j < cols; j++) {
                row.add(new ByteCell());
                prevRow.add(new ByteCell());
            }
            thisGen.add(row);
            prevGen.add(prevRow);
        }
    }

    /**
     * Doubles the number of columns of both thisGen and prevGen.
     */
    private void doubleCols() {
        int currColCount = thisGen.get(0).size();
        for (int i = 0; i < thisGen.size(); i++) {
            List<Cell> row = thisGen.get(i);
            List<Cell> prevRow = prevGen.get(i);
            for (int x = 0; x < currColCount; x++) {
                row.add(new ByteCell());
                prevRow.add(new ByteCell());
            }
        }
    }

    /**
     * Inserts a pattern into both thisGen and prevGen, placing it in the middle.
     * @param pattern Pattern to insert into the thisGen.
     */
    @Override
    public void insertPattern(byte[][] pattern) {
        while (pattern.length > thisGen.size()) {
            doubleRows();
        }
        while (pattern[0].length > thisGen.get(0).size()) {
            doubleCols();
        }

        // Sets the top left corner to begin inserting cells on the board.
        int originRow = (thisGen.size() / 2) - (pattern.length / 2);
        int originCol = (thisGen.get(0).size() / 2) - (pattern[0].length / 2);

        for (int y = 0; y < pattern.length; y++) {
            byte[] row = pattern[y];
            for (int x = 0; x < row.length; x++) {
                boolean cellAlive = row[x] == 1;

                int relY = originRow + y;
                int relX = originCol + x;

                thisGen.get(relY).get(relX).getState().setAlive(cellAlive);
                prevGen.get(relY).get(relX).getState().setAlive(cellAlive);
            }
        }
        if (dynamic) {
            postGenerationGrow();
        }
    }

    /**
     * Sets all cells in a board to dead.
     * @param board Board to set cells to dead in.
     * @return The passed board, now with all dead cells.
     */
    private List<List<Cell>> killBoard(List<List<Cell>> board) {
        for (List<Cell> row : board) {
            for (Cell cell : row) {
                cell.kill();
            }
        }
        return board;
    }

    /**
     * Clears all drawn cells from the board.
     */
    public void clearBoard() {
        killBoard(thisGen);
        genCount = 0;
        aliveCount = 0;
    }

    /**
     * Returns the coordinates of all cells surrounding a cell.
     * @param coordinate of the target cell.
     * @return a set of coordinates of neighbour cells.
     */
    private Set<BoardCoordinate> growSelection(BoardCoordinate coordinate) {
        Set<BoardCoordinate> result = new HashSet<>();

        for (int y = -1; y <= 1; y++) {
            int actualY = coordinate.getY() + y;
            for (int x = -1; x <= 1; x++) {
                int actualX = coordinate.getX() + x;

                if (!dynamic) {
                    actualY = wrap(getSizeY(), 0, actualY);
                    actualX = wrap(getSizeX(), 0, actualX);
                }

                if (actualX >= 0 &&
                        actualX < getSizeX() &&
                        actualY >= 0 &&
                        actualY < getSizeY()) {
                    result.add(new BoardCoordinate(actualY, actualX));
                }
            }
        }
        return result;
    }

    /**
     * Retrieves all alive cells and their neighbors. These are the cells that are applicable for dying or resurrection,
     * since both transitions requires alive cells as neighbors.
     * @param board Board to fetch cells of interest in.
     * @param startRow Start from this row.
     * @param endRow End at this row.
     * @return A set of coordinates where there are cells of interest.
     */
    private Set<BoardCoordinate> getCellsOfInterest(List<List<Cell>> board, int startRow, int endRow) {
        Set<BoardCoordinate> result = new HashSet<>();
        for (int y = startRow; y < endRow; y++) {
            List<Cell> row = board.get(y);
            for (int x = 0; x < row.size(); x++) {
                Cell cell = row.get(x);

                if (cell.getState().isAlive()) {
                    result.addAll(growSelection(new BoardCoordinate(y, x)));
                }
            }
        }
        return result;
    }

    /**
     * Iterates through cells of interest in prevGen, counting their alive neighbors and applying the new state for
     * each cell in thisGen.
     */
    @Override
    public void nextGeneration() {
        genCount++;

        List<List<Cell>> temp = thisGen;
        thisGen = prevGen;
        prevGen = temp;
        thisGen = killBoard(thisGen);
        aliveCount = 0;

        cellsOfInterest = getCellsOfInterest(prevGen, 0, getSizeY());

        for (BoardCoordinate coordinate : cellsOfInterest) {
            int numNeighbors = neighbors(prevGen, coordinate);
            Cell cell = prevGen.get(coordinate.getY()).get(coordinate.getX());
            State newState = ruleSet.getNewState(cell.getState(), numNeighbors);
            State cellState = thisGen.get(coordinate.getY()).get(coordinate.getX()).getState();
            if (!cellState.isAlive() && newState.isAlive() || (cellState.isAlive() && newState.isAlive())) {
                aliveCount++;
            }
            cellState.setAlive(newState.isAlive());
        }
        if (dynamic) {
            postGenerationGrow();
        }
    }

    /**
     * Checks board edges for live cells, and expands if any are found.
     */
    private void postGenerationGrow() {
        boolean leftAdded = false;
        boolean rightAdded = false;
        boolean topAdded = false;
        boolean bottomAdded = false;
        int width = thisGen.get(0).size() - 1;
        int height = thisGen.size() - 1;
        for (List<Cell> row : thisGen) {
            Cell leftCell = row.get(0);
            if (leftCell.getState().isAlive() && !leftAdded) {
                addColLeft();
                leftAdded = true;
            }

            Cell rightCell = row.get(width);
            if (rightCell.getState().isAlive() && !rightAdded) {
                addColRight();
                rightAdded = true;
            }

            if (rightAdded && leftAdded) {
                break;
            }
        }
        for (int i = 0; i < thisGen.get(0).size(); i++) {
            Cell topCell = thisGen.get(0).get(i);
            if (topCell.getState().isAlive() && !topAdded) {
                addRowTop();
                topAdded = true;
            }

            Cell bottomCell = thisGen.get(height).get(i);
            if (bottomCell.getState().isAlive() && !bottomAdded) {
                addRowBottom();
                bottomAdded = true;
            }
            if (topAdded && bottomAdded) {
                break;
            }
        }
        if (topAdded || bottomAdded || rightAdded || leftAdded) {
            callPostResizeListeners(new Size(
                    getSizeY(),
                    getSizeX(),
                    topAdded ? 1 : 0,
                    rightAdded ? 1 : 0,
                    bottomAdded ? 1 : 0,
                    leftAdded ? 1 : 0
            ));
        }
    }

    /**
     * Creates and returns an empty row.
     * @return A new empty row.
     */
    private List<Cell> getEmptyRow() {
        List<Cell> row = new ArrayList<>(thisGen.get(0).size());
        for (int i = 0; i < thisGen.get(0).size(); i++) {
            row.add(new ByteCell());
        }
        return row;
    }

    /**
     * Adds an empty row to the bottom of the board.
     */
    public void addRowBottom(){
        thisGen.add(getEmptyRow());
        prevGen.add(getEmptyRow());
    }

    /**
     * Adds an empty row to the top of the board.
     */
    public void addRowTop(){
        thisGen.add(0, getEmptyRow());
        prevGen.add(0, getEmptyRow());
    }

    /**
     * Adds an empty column to the right of the board.
     */
    public void addColRight(){
        for (int i = 0; i < thisGen.size(); i++) {
            List<Cell> row = thisGen.get(i);
            List<Cell> prevRow = prevGen.get(i);
            row.add(new ByteCell());
            prevRow.add(new ByteCell());
        }
    }

    /**
     * Adds an empty column to the left of the board.
     */
    public void addColLeft(){
        for (int i = 0; i < thisGen.size(); i++) {
            List<Cell> prevRow = prevGen.get(i);
            List<Cell> row = thisGen.get(i);
            prevRow.add(0, new ByteCell());
            row.add(0, new ByteCell());
        }
    }

    /**
     * Method for checking a specific cell coordinate's neighbour count.
     * @param board The board to check the cell neighbor count in.
     * @param coordinate The coordinate of the cell to count the neighbors of.
     * @return Number of neighbors.
     */
    private int neighbors(List<List<Cell>> board, BoardCoordinate coordinate) {
        int cellY = coordinate.getY();
        int cellX = coordinate.getX();
        int boardColLen = board.get(0).size();
        int boardRowLen = board.size();
        int num = 0;

        for (int relativeY = -1; relativeY < 2; relativeY++) {
            for (int relativeX = -1; relativeX < 2; relativeX++) {
                if (relativeX == 0 && relativeY == 0) {
                    continue;
                }

                int neighborX = cellX + relativeX;
                int neighborY = cellY + relativeY;

                if (!dynamic) {
                    neighborY = wrap(boardRowLen, 0, neighborY);
                    neighborX = wrap(boardColLen, 0, neighborX);
                }
                if (neighborX < 0 || neighborY < 0 || neighborX > getSizeX() - 1 || neighborY > getSizeY() - 1) {
                    continue;
                }
                if (board.get(neighborY).get(neighborX).getState().isAlive()) {
                    num++;
                }
            }
        }
        return num;
    }

    /**
     * @return {@code thisGen} into a single line {@code String} of 0s and 1s.
     */
    @Override
    public String toString() {
        if (thisGen.size() == 0) {
            return "";
        }

        StringBuilder sb = new StringBuilder();
        for(List<Cell> row : thisGen) {
            for(Cell cell : row) {
                if (cell.getState().isAlive()) {
                    sb.append("1");
                } else {
                    sb.append("0");
                }
            }
        }
        return sb.toString();
    }

    /**
     * Creates and returns a "trimmed" board, only within the {@code BoundingBox}.
     * @return A new {@code Board} containing only the cells within this {@code Board}'s {@code BoundingBox}.
     */
    public Board patternToBoard() {
        BoundingBox bb = getBoundingBox();
        Board patternBoard = new ArrayListBoard(bb.getSizeX(), bb.getSizeY());
        int patternBoardCol = 0;
        int patternBoardRow = 0;

        for(int row = bb.getFirstRow(); row <= bb.getLastRow(); row++) {
            for(int col = bb.getFirstCol(); col <= bb.getLastCol(); col++) {
                patternBoard.setCellAlive(patternBoardRow, patternBoardCol, thisGen.get(row).get(col).getState().isAlive());
                patternBoardCol++;
            }
            patternBoardRow++;
            patternBoardCol = 0;
        }
        return patternBoard;
    }

    /**
     * Creates a bounding box within which the current state of the thisGen is of interest (boundary of alive cells).
     * @return The BoundingBox representing the area of interest.
     */
    private BoundingBox getBoundingBox() {
        BoundingBox bb = new BoundingBox(thisGen.size(), thisGen.get(0).size(), 0, 0);
        for(int i = 0; i < thisGen.size(); i++) {
            for(int j = 0; j < thisGen.get(i).size(); j++) {
                if (!thisGen.get(i).get(j).getState().isAlive()) continue;

                if (i < bb.getFirstRow()) {
                    bb.setFirstRow(i);
                }
                if (j < bb.getFirstCol()) {
                    bb.setFirstCol(j);
                }
                if (i > bb.getLastRow()) {
                    bb.setLastRow(i);
                }
                if (j > bb.getLastCol()) {
                    bb.setLastCol(j);
                }
            }
        }
        return bb;
    }

    /**
     * List of listeners to be called after the board resizes.
     */
    private List<Consumer<Size>> postResizeListeners = new ArrayList<>();

    /**
     * Adds the passed {@code Consumer} to the list of post resize listeners.
     * @param runner Runner to add to list of listeners.
     */
    @Override
    public void addPostResizeListener(Consumer<Size> runner) {
        postResizeListeners.add(runner);
    }

    /**
     * Calls all registered post resize listeners with the passed {@code Size} object.
     * @param size Size object to pass to each listener.
     */
    private void callPostResizeListeners(Size size) {
        for (Consumer<Size> runner : postResizeListeners) {
            runner.accept(size);
        }
    }

    /**
     * Setter method for the active board rules.
     * @param ruleSet The requested rule set.
     */
    @Override
    public void setRuleSet(RuleSet ruleSet) {
        this.ruleSet = ruleSet;
    }

    /**
     * Getter method for the active board rules.
     * @return The current rule set.
     */
    @Override
    public RuleSet getRuleSet() {
        return ruleSet;
    }

    /**
     * Returns the number of columns in the thisGen.
     * @return The number of columns.
     */
    @Override
    public int getSizeX() {
        return thisGen.get(0).size();
    }

    /**
     * Returns the number of rows in the thisGen.
     * @return The number of rows.
     */
    @Override
    public int getSizeY() {
        return thisGen.size();
    }

    /**
     * Getter for thisGen.
     * @return Curr
     */
    @Override
    public List<List<Cell>> getThisGen() {
        return thisGen;
    }

    /**
     * @return True if this {@code Board} is currently dynamic.
     */
    @Override
    public boolean getDynamic() {
        return dynamic;
    }

    /**
     * @param dynamic New dynamic status for this {@code Board}.
     */
    @Override
    public void setDynamic(boolean dynamic) {
        this.dynamic = dynamic;
    }

    /**
     * @return True if multithreading is enable, false otherwise.
     */
    @Override
    public boolean getMultithreading() {
        return multithreadingEnabled;
    }

    /**
     * @param multithreadingEnabled Sets multithreading status.
     */
    @Override
    public void setMultithreading(boolean multithreadingEnabled) {
        this.multithreadingEnabled = multithreadingEnabled;
    }

    /**
     * Sets the value of the cell at the passed Y and X coordinates.
     * @param y Y-coordinate of the cell to set.
     * @param x X-coordinate of the cell to set.
     * @param alive New status of the cell.
     */
    @Override
    public void setCellAlive(int y, int x, boolean alive) {
        thisGen.get(y).get(x).getState().setAlive(alive);
    }

    /**
     * Gets the value of the cell at the passed Y and X coordinates.
     * @param y Y-coordinate of the cell to get.
     * @param x X-coordinate of the cell to get.
     */
    @Override
    public boolean getCellAlive(int y, int x) {
        return thisGen.get(y).get(x).getState().isAlive();
    }

    /**
     * @return The number of generation advanced since clearing this board.
     */
    @Override
    public int getGenCount() {
        return genCount;
    }

    /**
     * @return The number of alive cells in this generation.
     */
    @Override
    public int getAliveCount() {
        return aliveCount;
    }
}
