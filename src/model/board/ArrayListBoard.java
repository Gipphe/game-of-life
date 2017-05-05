/*
 * Game of Life
 * @author Jonas I.
 * @author Victor B.
 * @author Yanislav Z.
 */
package model.board;

import java.util.*;

import model.BoundingBox;
import model.Size;
import model.state.State;

import java.util.function.Consumer;
import rules.RuleSet;
import view.BoardCoordinate;

import static utils.Utils.wrap;

/**
 * ArrayList implementation of Board. Handles the internal table as a two-dimensional ArrayList containing Cell objects.
 * Enables easier dynamic board implementation, but incurs a sizable computation overhead when iterating through the
 * table.
 */
public class ArrayListBoard implements Board {
    private List<List<Cell>> thisGen;
    private List<List<Cell>> prevGen;
    private Set<BoardCoordinate> aliveCells;
    private RuleSet ruleSet;

    private static List<Thread> workers = new ArrayList<>();
    private int parallelLevel = Runtime.getRuntime().availableProcessors();
    private boolean dynamic;
    private boolean multithreadingEnabled;

    private int threadIndex = 0;

    /**
     * Number of alive cells in this generation.
     */
    private int aliveCount = 0;

    /**
     * Number of dead cells since this board started.
     */
    private long deadCount = 0;

    /**
     * Number of generations since this board's creation.
     */
    private int genCount = 0;

    /**
     * The genCount where the enumerable was last created.
     */
    private int lastGetEnumerableGen = -1;

    /**
     * The cached enumerable two-dimensional Boolean List.
     */
    private List<List<Boolean>> enumerable;

    /**
     * Constructor accepting the initial sizes of the thisGen.
     *
     * @param sizeX Length of the thisGen.
     * @param sizeY Height of the thisGen.
     */
    public ArrayListBoard(int sizeX, int sizeY) {
        initBoard(sizeX, sizeY);
    }

    /**
     * Constructor for ArrayListBoard
     * @param board
     */
    public ArrayListBoard(Board board) {
        initBoard(board.getSizeX(), board.getSizeY());
        List<List<Boolean>> boolBoard = board.getEnumerable();
        for (int y = 0; y < boolBoard.size(); y++) {
            List<Boolean> row = boolBoard.get(y);
            for (int x = 0; x < row.size(); x++) {
                Boolean cell = row.get(x);
                thisGen.get(y).get(x).getState().setAlive(cell);
            }
        }
        this.dynamic = board.getDynamic();
        this.multithreadingEnabled = board.getMultithreading();
    }

    /**
     * nextGeneration with multi-threading enabled. Reads no. of processors and distributes workload accordingly.
     * Provides a performance increase of TODO Add multi-threading performance increase percentage
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
     * Calculates how big of a chunk each thread should take care of, calculates prevGen.() and sets the updated cells to the board
     * and sets the updated cells to the thisGen.
     *
     * @param threadIndex The index of the instantiated thread.
     * @param oldBoard A clone of the old thisGen.
     */
    private void evaluateChunkSize(int threadIndex, List<List<Cell>> oldBoard) {
        int blockSize = oldBoard.size() / parallelLevel;
        int toY = blockSize * threadIndex;
        // this if-sentence makes the last thread take care of all remaining rows of the current thisGen, ensuring that
        // the thisGen y-value does NOT need to be a factorial of parallelLevel.
        if (threadIndex == parallelLevel) {
            toY = oldBoard.size();
        }
        int fromY = blockSize * (threadIndex - 1);

        Set<BoardCoordinate> interestingCells = getCellsOfInterest(oldBoard, fromY, toY);

        for (BoardCoordinate aliveCoordinate : interestingCells) {
            Cell cell = oldBoard.get(aliveCoordinate.getY()).get(aliveCoordinate.getX());
            int numNeighbors = neighbours(oldBoard, aliveCoordinate);
            State newState = ruleSet.getNewState(cell.getState(), numNeighbors);
            thisGen.get(aliveCoordinate.getY()).get(aliveCoordinate.getX()).getState().setAlive(newState.isAlive());
            if (newState.isAlive()) aliveCount++;
            else deadCount++;
        }
    }

    /**
     * "Counter" method for threads. Gives each thread an index
     * @return
     */
    private synchronized int getThreadIndex() {
        threadIndex++;
        return threadIndex;
    }

    /**
     * Creates workers and refers their task.
     */
    private void createWorkers() {
        List<List<Cell>> temp = thisGen;
        thisGen = prevGen;
        prevGen = temp;
        thisGen = killBoard(thisGen);

        for(int i = 1; i <= parallelLevel; i++) {
            workers.add(new Thread(() -> {
                int threadIndex = getThreadIndex();
                evaluateChunkSize(threadIndex, prevGen);
            }));
        }
    }

    /**
     * Runs all available workers
     * @throws InterruptedException
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
     * Initializes the thisGen with the passed sizes.
     *
     * @param sizeX Number of columns to initialize with.
     * @param sizeY Number of rows to initialize with.
     */
    private void initBoard(int sizeX, int sizeY) {
        ruleSet = rules.RulesCollection.getByName("Conway");
        aliveCells = new HashSet<>();

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
     * Doubles the current number of rows.
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
     * Doubles the current number of columns.
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
     * Inserts a pattern, starting from the top-left corner of the thisGen (0,0).
     *
     * @param pattern Pattern to insert into the thisGen.
     */
    @Override
    public void insertPattern(byte[][] pattern) {
        lastGetEnumerableGen = -1;
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
     * Sets all cells in a board to 0.
     * @param board
     * @return a shell-board filled with 0-cells.
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
        deadCount = 0;
        lastGetEnumerableGen = -1;
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
     * Gets all relevant cells (cells that have the ability to be affected- or affect other cells.
     * @param board
     * @param startRow
     * @param endRow
     * @return
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
     * Iterates through all cells in the thisGen, counting their alive neighbors and applying the rule set to them.
     */
    @Override
    public void nextGeneration() {
        genCount++;

        List<List<Cell>> temp = thisGen;
        thisGen = prevGen;
        prevGen = temp;
        thisGen = killBoard(thisGen);
        aliveCount = 0;

        aliveCells = getCellsOfInterest(prevGen, 0, getSizeY());

        for (BoardCoordinate coordinate : aliveCells) {
            int numNeighbors = neighbours(prevGen, coordinate);
            Cell cell = prevGen.get(coordinate.getY()).get(coordinate.getX());
            State newState = ruleSet.getNewState(cell.getState(), numNeighbors);
            thisGen.get(coordinate.getY()).get(coordinate.getX()).getState().setAlive(newState.isAlive());
            if (newState.isAlive()) aliveCount++;
            else deadCount++;
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
     * Displays current generation sizes for testing purposes.
     */
    public void tester() {
        System.out.println("thisGen.size() = " + thisGen.size());
        System.out.println("thisGen.get(0).size = " + thisGen.get(0).size());
    }

    /**
     * Creates and returns an empty row
     * @return row
     */
    private List<Cell> getEmptyRow() {
        List<Cell> row = new ArrayList<>(thisGen.get(0).size());
        for (int i = 0; i < thisGen.get(0).size(); i++) {
            row.add(new ByteCell());
        }
        return row;
    }

    /**
     * Adds an empty row to the bottom of the board
     */
    public void addRowBottom(){
        lastGetEnumerableGen = -1;
        thisGen.add(getEmptyRow());
        prevGen.add(getEmptyRow());
    }

    /**
     * Adds an empty row to the top of the board
     */
    public void addRowTop(){
        lastGetEnumerableGen = -1;
        thisGen.add(0, getEmptyRow());
        prevGen.add(0, getEmptyRow());
    }

    /**
     * Adds an empty column to the right of the board
     */
    public void addColRight(){
        lastGetEnumerableGen = -1;
        for (int i = 0; i < thisGen.size(); i++) {
            List<Cell> row = thisGen.get(i);
            List<Cell> prevRow = prevGen.get(i);
            row.add(new ByteCell());
            prevRow.add(new ByteCell());
        }
    }

    /**
     * Adds an empty column to the left of the board
     */
    public void addColLeft(){
        lastGetEnumerableGen = -1;
        for (int i = 0; i < thisGen.size(); i++) {
            List<Cell> prevRow = prevGen.get(i);
            List<Cell> row = thisGen.get(i);
            prevRow.add(0, new ByteCell());
            row.add(0, new ByteCell());
        }
    }

    /**
     * Method for checking a specific cell's neighbour count.
     * @return Number of neighbours.
     */
    private int neighbours(List<List<Cell>> board, BoardCoordinate coordinate) {
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
     * Transforms the thisGen into a single String of 0s and 1s.
     * @return String of 1 and 0 representing the byte[][] array.
     */
    @Override
    public String toString() {
        return toString(thisGen);
    }

    /**
     * Returns a toString of a 2D List array.
     * @param board to be converted
     * @return String of 1 and 0 representing the List<List<Cell>> array.
     */
    private String toString(List<List<Cell>> board) {
        if (board.size() == 0) {
            return "";
        }

        StringBuilder sb = new StringBuilder();
        for(List<Cell> row : board) {
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
     * Returns a 1/0 String of only the pattern within the bounding box.
     * @return 1s and 0s representing the contained pattern.
     */
    public String patternToString(){
        BoundingBox bb = getBoundingBox();
        StringBuilder sb = new StringBuilder();

        for(int row = bb.getFirstRow(); row <= bb.getLastRow(); row++) {
            for(int col = bb.getFirstCol(); col <= bb.getLastCol(); col++) {
                sb.append(thisGen.get(row).get(col).getState());
            }
        }
        return sb.toString();
    }

    /**
     * Creates and returns a "trimmed" board, only within the BoundingBox.
     * @return patternBoard
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
    public BoundingBox getBoundingBox() {
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
     * TODO Victor
     */
    private List<Consumer<Size>> postResizeListeners = new ArrayList<>();

    /**
     * TODO Victor
     */
    private List<Consumer<Size>> preResizeListeners = new ArrayList<>();

    /**
     * TODO Victor
     * @param runner
     */
    @Override
    public void addPostResizeListener(Consumer<Size> runner) {
        postResizeListeners.add(runner);
    }

    /**
     * TODO Victor
     * @param runner
     */
    @Override
    public void addPreResizeListener(Consumer<Size> runner) {
        preResizeListeners.add(runner);
    }

    /**
     * TODO Victor
     */
    private void callPostResizeListeners() {
        for (Consumer<Size> runner : postResizeListeners) {
            runner.accept(new Size(getSizeY(), getSizeX()));
        }
    }

    /**
     * TODO Victor
     * @param size
     */
    private void callPostResizeListeners(Size size) {
        for (Consumer<Size> runner : postResizeListeners) {
            runner.accept(size);
        }
    }

    /**
     * TODO Victor
     */
    private void callPreResizeListeners() {
        for (Consumer<Size> runner : preResizeListeners) {
            runner.accept(new Size(getSizeY(), getSizeX()));
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
     * TODO Victor
     * @return
     */
    @Override
    public List<List<Boolean>> getEnumerable() {
        if (genCount == lastGetEnumerableGen) {
            return enumerable;
        }
        List<List<Boolean>> result = new ArrayList<>(getSizeY());
        for (int y = 0; y < getSizeY(); y++) {
            List<Boolean> row = new ArrayList<>(getSizeX());
            result.add(row);
            for (int x = 0; x < getSizeX(); x++) {
                row.add(thisGen.get(y).get(x).getState().isAlive());
            }
        }
        this.enumerable = result;
        lastGetEnumerableGen = genCount;
        return result;
    }

    /**
     * Getter for the current dynamic status of the board.
     * @return dynamic
     */
    @Override
    public boolean getDynamic() {
        return dynamic;
    }

    /**
     * Setter for the current dynamic status of the board.
     * @param dynamic
     */
    @Override
    public void setDynamic(boolean dynamic) {
        lastGetEnumerableGen = -1;
        this.dynamic = dynamic;
    }

    /**
     * Getter for multithreading status
     * @return the status of multithreadingEnabled
     */
    @Override
    public boolean getMultithreading() {
        return multithreadingEnabled;
    }

    /**
     * Setter for multithreading
     * @param multithreadingEnabled
     */
    @Override
    public void setMultithreading(boolean multithreadingEnabled) {
        lastGetEnumerableGen = -1;
        this.multithreadingEnabled = multithreadingEnabled;
    }

    /**
     * Sets the value of a cell
     * @param y coordinate
     * @param x coordinate
     * @param alive required status.
     */
    @Override
    public void setCellAlive(int y, int x, boolean alive) {
        lastGetEnumerableGen = -1;
        thisGen.get(y).get(x).getState().setAlive(alive);
    }

    /**
     * Gets the status of a cell.
     * @param y coordinate
     * @param x coordinate
     * @return status of the cell.
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

    /**
     * @return The number of dead cells since clearing this board.
     */
    @Override
    public long getDeadCount() {
        return deadCount;
    }
}
