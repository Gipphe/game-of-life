package app;

public class Board {
    public int xaxis;
    public int yaxis;
    private byte[][] board;

    public Board(int xaxis, int yaxis){
        this.xaxis=xaxis;
        this.yaxis=yaxis;
        board = new byte[yaxis][xaxis];
    }

    public void setBoard(byte[][] newBoard) {
        board = newBoard;
    }

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
     * Makes the coordinate wrap around to the other side of the board if it is out of bounds
     * @param lim int
     * @param val int
     * @return int
     */
    public static int wrap(int lim, int val) {
        if (val >= lim) {
            return val - lim;
        } else if (val < 0) {
            return val + lim;
        }
        return val;
    }

    @Override
    public String toString() {
        String msg = "";
        for (byte[] row : board) {
            String s = "";
            for (int cell : row) {
                String val = cell == 1 ? "1" : "0";
                s = s + val;
            }
            msg = msg + s;
        }
        return msg;
    }

    public byte[][] getBoard() {
        return board;
    }

}
