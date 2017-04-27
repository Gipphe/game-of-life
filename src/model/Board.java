package model;

import java.util.ArrayList;
import java.util.List;
import rules.RuleSet;
import rules.RulesCollection;

public class Board {
    private ArrayList<ArrayList<Cell>> board;
    private RuleSet ruleSet;

    /**
     * Constructor accepting the initial sizes of the board.
     *
     * @param sizeX Length of the board.
     * @param sizeY Height of the board.
     */
    public Board(int sizeX, int sizeY){
        initBoard(sizeX, sizeY);
    }

    /**
     * Initializes the board with the passed sizes.
     *
     * @param sizeX Number of columns to initialize with.
     * @param sizeY Number of rows to initialize with.
     */
    private void initBoard(int sizeX, int sizeY) {
        ruleSet = rules.RulesCollection.getByName("Conway");

        board = new ArrayList<>(sizeY);
        for (int i = 0; i < sizeY; i++) {
            ArrayList<Cell> row = new ArrayList<>(sizeX);
            board.add(row);
            for (int j = 0; j < sizeX; j++) {
                Cell cell = new Cell();
                row.add(cell);
            }
        }
    }

    /**
     * Doubles the current number of rows.
     */
    private void doubleRows() {
        int currRowCount = board.size();
        int targetRowCount = currRowCount * 2;
        for (int y = 0; y < targetRowCount; y++) {
            try {
                board.get(y);
            } catch (IndexOutOfBoundsException e) {
                int cols = board.get(0).size();
                ArrayList<Cell> row = new ArrayList<>(cols);
                for (int i = 0; i < cols; i++) {
                    row.add(new Cell(0));
                }
                board.add(row);
            }
        }
    }

    /**
     * Doubles the current number of columns.
     */
    private void doubleCols() {
        int currColCount = board.get(0).size();
        int targetColCount = currColCount * 2;
        for (ArrayList<Cell> row : board) {
            for (int x = 0; x < targetColCount; x++) {
                try {
                    row.get(x);
                } catch (IndexOutOfBoundsException e) {
                    row.add(new Cell());
                }
            }
        }
    }

    /**
     * Inserts a pattern, starting from the top-left corner of the board (0,0).
     *
     * @param pattern Pattern to insert into the board.
     */
    public void insertPattern(byte[][] pattern) {
        while (pattern.length > board.size()) {
            doubleRows();
        }
        while (pattern[0].length > board.get(0).size()) {
            doubleCols();
        }
        int originRow = (board.size() / 2) - (pattern.length / 2);
        int originCol = (board.get(0).size() / 2) - (pattern[0].length / 2);
        for (int y = 0; y < pattern.length; y++) {
            byte[] row = pattern[y];
            for (int x = 0; x < row.length; x++) {
                byte cell = row[x];

                int relY = originRow + y;
                int relX = originCol + x;
                ArrayList<Cell> activeRow = board.get(relY);
                Cell activeCell = activeRow.get(relX);
                activeCell.setState(cell);
            }
        }
    }

    /**
     * Returns the state of a cell at the given coordinates.
     *
     * @param x The column of the cell.
     * @param y The row of the cell.
     * @return The state of the cell.
     */
    public byte getValue(int x, int y){
        return board.get(y).get(x).getState();
    }

    /**
     * Sets the state of a cell at the given coordinates.
     *
     * @param x The column of the cell.
     * @param y The row of the cell.
     * @param state The new state for the cell.
     */
    public void setValue(int x, int y, byte state){
        board.get(y).get(x).setState(state);
    }

    /**
     * Returns a clone of the current board, where no changes to the cloned board will affect the original board.
     *
     * @param oldBoard Board to copy.
     * @return A clone of the passed board.
     */
    private static ArrayList<ArrayList<Cell>> cloneBoard(ArrayList<ArrayList<Cell>> oldBoard) {
        int oldSizeY = oldBoard.size();
        int oldSizeX = oldBoard.get(0).size();
        ArrayList<ArrayList<Cell>> newBoard = new ArrayList<>(oldSizeY);
        for (ArrayList<Cell> oldRow : oldBoard) {
            ArrayList<Cell> newRow = new ArrayList<>(oldSizeX);
            newBoard.add(newRow);
            for (Cell cell : oldRow) {
                newRow.add(new Cell(cell.getState()));
            }
        }
        return newBoard;
    }

    /**
     * Iterates through all cells in the board, counting their alive neighbors and applying the rule set to them.
     */
    public void nextGeneration() {
        ArrayList<ArrayList<Cell>> oldBoard = cloneBoard(board);
        for (int y = 0; y < oldBoard.size(); y++) {
            List<Cell> row = oldBoard.get(y);
            for (int x = 0; x < row.size(); x++) {
                Cell cell = row.get(x);

                int numNeighbours = neighbours(oldBoard, x, y);
                byte newState = ruleSet.getNewState(cell.getState(), numNeighbours);
                board.get(y).get(x).setState(newState);
            }
        }
    }

    /**
     * Method for checking a specific cell's neighbour count.
     *
     * @param board Board to check cell neighbor count on.
     * @param cellX X-position of the cell.
     * @param cellY Y-position of the cell.
     * @return Number of neighbours.
     */
    private int neighbours(ArrayList<ArrayList<Cell>> board, int cellX, int cellY) {
        int lenX = board.get(0).size();
        int lenY = board.size();
        int num = 0;
        for (int relativeY = -1; relativeY < 2; relativeY++) {
            for (int relativeX = -1; relativeX < 2; relativeX++) {
                if (relativeX == 0 && relativeY == 0) {
                    continue;
                }

                int neighborX = cellX + relativeX;
                int neighborY = cellY + relativeY;

                neighborY = wrap(lenY, neighborY);
                neighborX = wrap(lenX, neighborX);

                if (board.get(neighborY).get(neighborX).getState() == 1) {
                    num++;
                }
            }
        }
        return num;
    }

    /**
     * Returns a corresponding value within [0, lim] for val, wrapping it around the boundaries of the set.
     *
     * @param lim Upper limit for val.
     * @param val Value to limit.
     * @return Value within [0, lim].
     */
    private static int wrap(int lim, int val) {
        if (val >= lim) {
            return val - lim;
        } else if (val < 0) {
            return val + lim;
        }
        return val;
    }

    /**
     * Transforms the board into a single String of 0s and 1s.
     *
     * @return String of 1 and 0 representing the byte[][] array.
     */
    @Override
    public String toString() {
        return toString(board);
    }
    private String toString(ArrayList<ArrayList<Cell>> board) {
        if (board.size() == 0) {
            return "";
        }

        StringBuilder sb = new StringBuilder();
        for(ArrayList<Cell> row : board) {
            for(Cell cell : row) {
                if (cell.getState() == 1) {
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
        System.out.println(bb.toString());
        StringBuilder sb = new StringBuilder();

        for(int row = bb.getFirstRow(); row <= bb.getLastRow(); row++) {
            for(int col = bb.getFirstCol(); col <= bb.getLastCol(); col++) {
                sb.append(board.get(row).get(col).getState());
            }
        }
        return sb.toString();
    }

    /**
     * Getter for the board.
     *
     * @return The current board.
     */
    public ArrayList<ArrayList<Cell>> getBoard() {
        return board;
    }

    /**
     * Creates a bounding box within which the current state of the board is of interest (boundary of alive cells).
     *
     * @return The BoundingBox representing the area of interest.
     */
    private BoundingBox getBoundingBox() {
        BoundingBox bb = new BoundingBox(board.size(), board.get(0).size(), 0, 0);
        for(int i = 0; i < board.size(); i++) {
            for(int j = 0; j < board.get(i).size(); j++) {
                if (board.get(i).get(j).getState() == 0) continue;

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

    public void setRuleSet(String ruleSetName) {
        this.ruleSet = RulesCollection.getByName(ruleSetName);
    }

    /**
     * Returns the number of columns in the board.
     *
     * @return The number of columns.
     */
    public int getSizeX() {
        return board.get(0).size();
    }

    /**
     * Returns the number of rows in the board.
     *
     * @return The number of rows.
     */
    public int getSizeY() {
        return board.size();
    }
}
