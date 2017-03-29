package app;

import java.util.ArrayList;
import java.util.List;

class BoundingBox {
    int firstRow;
    int firstCol;
    int lastRow;
    int lastCol;
    BoundingBox(int firstRow, int firstCol, int lastRow, int lastCol) {
        this.firstRow = firstRow;
        this.firstCol = firstCol;
        this.lastRow = lastRow;
        this.lastCol = lastCol;
    }
}

public class Board {
    private ArrayList<ArrayList<Cell>> board;

    /**
     * Constructor for the board object.
     *
     * @param sizeX int length of the board
     * @param sizeY int height of the board
     */
    public Board(int sizeX, int sizeY){
        initBoard(sizeX, sizeY);
    }

    private void initBoard(int sizeX, int sizeY) {
        board = new ArrayList<ArrayList<Cell>>(sizeY);
        for (int i = 0; i < sizeY; i++) {
            ArrayList<Cell> row = new ArrayList<Cell>(sizeX);
            board.add(row);
            for (int j = 0; j < sizeX; j++) {
                Cell cell = new Cell();
                row.add(cell);
            }
        }
    }

    private void doubleRows() {
        int currRowCount = board.size();
        int targetRowCount = currRowCount * 2;
        for (int y = 0; y < targetRowCount; y++) {
            try {
                board.get(y);
            } catch (IndexOutOfBoundsException e) {
                board.add(new ArrayList<Cell>());
            }
        }
    }
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
    private void doubleBoard() {
        doubleRows();
        doubleCols();
    }

    public void insertPattern(byte[][] pattern) {
        while (pattern.length > board.size() ||
                pattern[0].length > board.get(0).size()) {
            doubleBoard();
        }
        for (int y = 0; y < pattern.length; y++) {
            byte[] row = pattern[y];
            for (int x = 0; x < row.length; x++) {
                byte cell = row[x];

                board.get(y).get(x).setState(cell);
            }
        }
    }

    public void fill(byte state) {
        for (ArrayList<Cell> row : board) {
            for (Cell cell : row) {
                cell.setState(state);
            }
        }
    }

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
     * Creates and stores the next generation of the current board, then sets the stored board as the board
     */
    public void nextGeneration() {
        ArrayList<ArrayList<Cell>> oldBoard = cloneBoard(board);
        for (int y = 0; y < oldBoard.size(); y++) {
            List<Cell> row = oldBoard.get(y);
            for (int x = 0; x < row.size(); x++) {
                Cell cell = row.get(x);

                int numNeighbours = neighbours(oldBoard, x, y);
                byte newState = rules(cell.getState(), numNeighbours);
                board.get(y).get(x).setState(newState);
            }
        }
    }

    /**
     * Decides if a cell has to be dead or alive for the nextGeneration method
     *
     * @param cell int tells if cell is dead (0) or alive (1)
     * @param num int amount of neighbours the target cell has (0 to 8)
     * @return either 1 or 0 in a single byte, signifying if the cell is dead or alive
     */
    private byte rules(int cell, int num) {
        if (cell == 0) {
            if (num == 3) {
                return 1;
            }
        } else {
            if (num > 1 && num < 4) {
                return 1;
            }
        }
        return 0;
    }

    /**
     * Method for checking a cells neighbour count
     *
     * @param cellX int x-position of cell
     * @param cellY int y-position of cell
     * @return number of neighbours
     */
    private int neighbours(ArrayList<ArrayList<Cell>> board, int cellX, int cellY) {
        board.get(cellY).get(cellX).setNeighbors((byte) 0);

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
                    board.get(cellY).get(cellX).incrementNeighbors();
                    num++;
                }
            }
        }
        return num;
    }

    /**
     * Returns a corresponding value within [0, lim] for val, wrapping it around the boundaries of the set.
     * @param lim int Upper limit for val.
     * @param val int Value to limit.
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
     * Overrides toString. Transforms the board to a single String of 0/1
     *
     * @return String of 1 and 0 representing the byte[][] array
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
     * getBoard method for the board
     *
     * @return the current board
     */
    ArrayList<ArrayList<Cell>> getBoard() {
        return board;
    }

    /**
     * setBoard method for the board
     *
     * @param newBoard byte[][] "2D" byte array which will be set as the new board array
     *
     * @return the new board
     */
    public void setBoard(ArrayList<ArrayList<Cell>> newBoard) {
        board = newBoard;
    }

    public BoundingBox getBoundingBox() {
        BoundingBox bb = new BoundingBox(board.size(), board.get(0).size(), 0, 0);
        for(int i = 0; i < board.size(); i++) {
            for(int j = 0; j < board.get(i).size(); j++) {
                if (board.get(i).get(j).getState() == 0) continue;

                if (i < bb.firstRow) {
                    bb.firstRow = i;
                }
                if (i > bb.lastRow) {
                    bb.lastRow = i;
                }
                if (j < bb.firstCol) {
                    bb.firstCol = j;
                }
                if (j > bb.lastCol) {
                    bb.lastCol = j;
                }
            }
        }
        return bb;
    }

    public int getSizeX() {
        return board.get(0).size();
    }

    public int getSizeY() {
        return board.size();
    }
}
