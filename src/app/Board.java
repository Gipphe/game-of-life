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
        ArrayList<ArrayList<Cell>> clone = new ArrayList<ArrayList<Cell>>(oldBoard.size());
        for (ArrayList<Cell> oldRow : oldBoard) {
            ArrayList<Cell> newRow = new ArrayList<Cell>();
            clone.add(newRow);
            for (Cell cell : oldRow) {
                newRow.add(new Cell(cell));
            }
        }
        return clone;
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

                    int numNeighbours = neighbours(x, y);
                    byte state = rules(cell.getState(), numNeighbours);
                    board.get(y).get(x).setState(state);
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
     * @param ox int x-position of cell
     * @param oy int y-position of cell
     * @return number of neighbours
     */
    private int neighbours(int ox, int oy) {
        int num = 0;
        for (int y = -1; y < 2; y++) {
            for (int x = -1; x < 2; x++) {
                int nx = ox + x;
                int ny = oy + y;
                int lenx = board.get(oy).size();
                int leny = board.size();

                ny = wrap(leny, ny);
                nx = wrap(lenx, nx);

                if (x == 0 && y == 0) {
                    continue;
                }
                if (board.get(ny).get(nx).getState() == 1) {
                    num++;
                }
            }
        }
        return num;
    }

    /**
     * Makes the coordinate wrap around to the other side of the board if it is out of bounds
     * @param lim int
     * @param val int
     * @return int
     * !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!! METHOD TO BE ADDED TO UTILITIES CLASS !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
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
     * Returns a String only showing the pattern between the bounding box.
     *
     * @return String of 1 and 0 representing the byte[][] array between bounding box.
     */
    public String patternToString() {
        BoundingBox bb = getBoundingBox();
        StringBuilder sb = new StringBuilder();
        for(int i = bb.firstRow; i <= bb.lastRow; i++) {
            for(int j = bb.firstCol; j <= bb.lastCol; j++) {
                if (board[i][j] == 1) {
                    sb.append("1");
                } else {
                    sb.append("0");
                }
            }
        } return sb.toString();
    }

    /**
     * Overrides toString. Transforms the board to a single String of 0/1
     *
     * @return String of 1 and 0 representing the byte[][] array
     */
    @Override
    public String toString() {
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
        } return sb.toString();
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
