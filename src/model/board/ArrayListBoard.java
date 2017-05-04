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

public class ArrayListBoard implements Board {
    private List<List<Cell>> thisGen;
    private List<List<Cell>> prevGen;
    private Set<BoardCoordinate> aliveCells;
    private RuleSet ruleSet;

    private static List<Thread> workers = new ArrayList<>();
    private int parallelLevel = Runtime.getRuntime().availableProcessors();
    private boolean dynamic;

    private int threadIndex = 0;

    /**
     * Constructor accepting the initial sizes of the thisGen.
     *
     * @param sizeX Length of the thisGen.
     * @param sizeY Height of the thisGen.
     */
    public ArrayListBoard(int sizeX, int sizeY) {
        initBoard(sizeX, sizeY);
    }

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
    }

    /**
     * nextGeneration with multi-threading enabled. Reads no. of processors and distributes workload accordingly.
     * Provides a performance increase of TODO Add multi-threading performance increase percentage
     */
    @Override
    public void nextGenerationConcurrent() {
        createWorkers();
        try {
            runWorkers();
        }catch (InterruptedException ie){
            ie.printStackTrace();
        }
        workers.clear();
        threadIndex = 0;
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
        }
    }

    private int getThreadIndex() {
        threadIndex++;
        return threadIndex;
    }

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

    // kjør trådobjektene
    private static void runWorkers() throws InterruptedException {
        for(Thread t : workers) {
            t.start();
        }

        // vent på at alle trådene har kjørt ferdig før vi returnerer
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
    }

    private List<List<Cell>> killBoard(List<List<Cell>> board) {
        for (List<Cell> row : board) {
            for (Cell cell : row) {
                cell.kill();
            }
        }
        return board;
    }

    public void clearBoard() {
        killBoard(thisGen);
    }

    private Set<BoardCoordinate> growSelection(BoardCoordinate coordinate) {
        Set<BoardCoordinate> result = new HashSet<>();

        for (int y = -1; y <= 1; y++) {
            int actualY = coordinate.getY() + y;
            for (int x = -1; x <= 1; x++) {
                int actualX = coordinate.getX() + x;

                if (actualX > 0 &&
                        actualX < getSizeX() &&
                        actualY > 0 &&
                        actualY < getSizeY()) {
                    result.add(new BoardCoordinate(actualY, actualX));
                }
            }
        }
        return result;
    }

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
        List<List<Cell>> temp = thisGen;
        thisGen = prevGen;
        prevGen = temp;
        thisGen = killBoard(thisGen);

        aliveCells = getCellsOfInterest(prevGen, 0, getSizeY());

        for (BoardCoordinate coordinate : aliveCells) {
            int numNeighbors = neighbours(prevGen, coordinate);
            Cell cell = prevGen.get(coordinate.getY()).get(coordinate.getX());
            State newState = ruleSet.getNewState(cell.getState(), numNeighbors);
            thisGen.get(coordinate.getY()).get(coordinate.getX()).getState().setAlive(newState.isAlive());
        }
    }

    public void tester() {
        System.out.println("thisGen.size() = " + thisGen.size());
        System.out.println("thisGen.get(0).size = " + thisGen.get(0).size());
    }

    private List<Cell> getEmptyRow() {
        List<Cell> row = new ArrayList<>(thisGen.get(0).size());
        for (int i = 0; i < thisGen.get(0).size(); i++) {
            row.add(new ByteCell());
        }
        return row;
    }

    public void addRowBottom(){
        thisGen.add(getEmptyRow());
        prevGen.add(getEmptyRow());
    }

    public void addRowTop(){
        thisGen.add(0, getEmptyRow());
        prevGen.add(0, getEmptyRow());
    }

    public void addColRight(){
        for (int i = 0; i < thisGen.size(); i++) {
            List<Cell> row = thisGen.get(i);
            List<Cell> prevRow = prevGen.get(i);
            row.add(new ByteCell());
            prevRow.add(new ByteCell());
        }
    }

    public void addColLeft(){
        for (int i = 0; i < thisGen.size(); i++) {
            List<Cell> row = thisGen.get(i);
            List<Cell> prevRow = prevGen.get(i);
            row.add(0, new ByteCell());
            prevRow.add(0, new ByteCell());
        }
    }

    /**
     * Method for checking a specific cell's neighbour count.
     *
     * @return Number of neighbours.
     */
    private int neighbours(List<List<Cell>> board, BoardCoordinate coordinate) {
        int cellY = coordinate.getY();
        int cellX = coordinate.getX();
        int boardColLen = board.get(0).size();
        int boardRowLen = board.size();
        int num = 0;

        Boolean isAlive = board.get(cellY).get(cellX).getState().isAlive();
        if (dynamic && isAlive){
            if (cellX == 0) {
                addColLeft();
                callResizeListeners();
            }
            if (cellX == boardColLen - 1) {
                addColRight();
                callResizeListeners();
            }
            if (cellY == 0) {
                addRowTop();
                callResizeListeners();
            }
            if (cellY == boardRowLen - 1) {
                addRowBottom();
                callResizeListeners();
            }
        }

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
                if (neighborX < 0 || neighborY < 0 || neighborX > getSizeX() - 1 || neighborY > getSizeY()) {
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
     *
     * @return String of 1 and 0 representing the byte[][] array.
     */
    @Override
    public String toString() {
        return toString(thisGen);
    }

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
     *
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


    public Board patternToBoard() {
        BoundingBox bb = getBoundingBox();
        Board patternBoard = new ArrayListBoard(bb.getSizeX(), bb.getSizeY());

        for(int row = bb.getFirstRow(); row <= bb.getLastRow(); row++) {
            for(int col = bb.getFirstCol(); col <= bb.getLastCol(); col++) {
                System.out.println("row: " + row + "col: " + col);
                patternBoard.setCellAlive(row, col, thisGen.get(row).get(col).getState().isAlive());
            }
        }
        return patternBoard;
    }

    /**
     * Creates a bounding box within which the current state of the thisGen is of interest (boundary of alive cells).
     *
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

    private List<Consumer<Size>> listeners = new ArrayList<>();

    @Override
    public void addResizeListener(Consumer<Size> runner) {
        listeners.add(runner);
    }

    private void callResizeListeners() {
        for (Consumer<Size> runner : listeners) {
            runner.accept(new Size(getSizeY(), getSizeX()));
        }
    }

    @Override
    public void setRuleSet(RuleSet ruleSet) {
        this.ruleSet = ruleSet;
    }

    /**
     *
     * @return The current rule set.
     */
    @Override
    public RuleSet getRuleSet() {
        return ruleSet;
    }

    /**
     * Returns the number of columns in the thisGen.
     *
     * @return The number of columns.
     */
    @Override
    public int getSizeX() {
        return thisGen.get(0).size();
    }

    /**
     * Returns the number of rows in the thisGen.
     *
     * @return The number of rows.
     */
    @Override
    public int getSizeY() {
        return thisGen.size();
    }

    @Override
    public List<List<Boolean>> getEnumerable() {
        List<List<Boolean>> result = new ArrayList<>(getSizeY());
        for (int y = 0; y < getSizeY(); y++) {
            List<Boolean> row = new ArrayList<>(getSizeX());
            result.add(row);
            for (int x = 0; x < getSizeX(); x++) {
                row.add(thisGen.get(y).get(x).getState().isAlive());
            }
        }
        return result;
    }

    @Override
    public List<List<Cell>> getThisGen() {
        return thisGen;
    }

    @Override
    public boolean getDynamic() {
        return dynamic;
    }

    @Override
    public void setDynamic(boolean dynamic) {
        this.dynamic = dynamic;
    }


    @Override
    public void setCellAlive(int y, int x, boolean alive) {
        thisGen.get(y).get(x).getState().setAlive(alive);
    }

    @Override
    public boolean getCellAlive(int y, int x) {
        return thisGen.get(y).get(x).getState().isAlive();
    }
}
