package com.example.android.gameoflifeandroid;

class BoundingBox {
    int firstRow;
    int firstCol;
    int lastRow;
    int lastCol;

    /**
     * Constructor for the bounding box.
     *
     * @param firstRow
     * @param firstCol
     * @param lastRow
     * @param lastCol
     */
    BoundingBox(int firstRow, int firstCol, int lastRow, int lastCol) {
        this.firstRow = firstRow;
        this.firstCol = firstCol;
        this.lastRow = lastRow;
        this.lastCol = lastCol;
    }
}

public class Board {
    public int xaxis;
    public int yaxis;
    private byte[][] board;

    /**
     * Constructor for the board object.
     *
     * @param xaxis int length of the board
     * @param yaxis int height of the board
     */
    public Board(int xaxis, int yaxis) {
        this.xaxis=xaxis;
        this.yaxis=yaxis;
        board = new byte[yaxis][xaxis];
    }

    /**
     * Creates and stores the next generation of the current board, then sets the stored board as the board.
     */
    public void nextGeneration() {
        byte[][] newBoard = new byte[board.length][board[0].length];
        for (int y = 0; y < board.length; y++) {
            byte[] row = board[y];
            for (int x = 0; x < row.length; x++) {
                int cell = row[x];

                int numNeighbours = neighbours(x, y);
                byte state = rules(cell, numNeighbours);
                newBoard[y][x] = state;
            }
        }
        board = newBoard;
    }

    /**
     * Decides if a cell has to be dead or alive for the nextGeneration method.
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
     * Method for checking a cells neighbour count.
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
                int lenx = board[oy].length;
                int leny = board.length;

                ny = wrap(leny, ny);
                nx = wrap(lenx, nx);

                if (x == 0 && y == 0) {
                    continue;
                }
                if (board[ny][nx] == 1) {
                    num++;
                }
            }
        }
        return num;
    }

    /**
     * Makes the coordinate wrap around to the other side of the board if it is out of bounds.
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
     * Overrides toString. Transforms the board to a single String of 0/1.
     *
     * @return String of 1 and 0 representing the byte[][] array
     */
    @Override
    public String toString() {
        StringBuilder boardString = new StringBuilder();
        for(int col = 0; col<yaxis; col++) {
            for(int row = 0; row<xaxis; row++) {
                if(board[col][row] == 0) {
                    boardString.append("0 ");
                } else {
                    boardString.append("1 ");
                }
            }
            boardString.append("\n");
        }
        return boardString.toString();
    }

    /**
     * getBoard method for the board
     *
     * @return the current board
     */
    byte[][] getBoard() {
        return board;
    }

    /**
     * setBoard method for the board
     *
     * @param newBoard byte[][] "2D" byte array which will be set as the new board array
     *
     * @return the new board
     */
    public void setBoard(byte[][] newBoard) {
        board = newBoard;
    }

    /**
     * getBoundingBox method for the bounding box
     *
     * @return the bounding box
     */
    public BoundingBox getBoundingBox() {
        BoundingBox bb = new BoundingBox(board.length, board[0].length, 0, 0);
        for(int i = 0; i < board.length; i++) {
            for(int j = 0; j < board[i].length; j++) {
                if (board[i][j] == 0) continue;
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
        System.out.println("board " + board.length);
        System.out.println("board[0] " + board[0].length);
        System.out.println("minrow: " + bb.firstRow);
        System.out.println("maxrow: " + bb.lastRow);
        System.out.println("mincolumn: " + bb.firstCol);
        System.out.println("maxcolumn: " + bb.lastCol);
        return bb;
    }
}