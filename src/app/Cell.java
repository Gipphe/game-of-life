package app;

public class Cell {
    private byte state;
    Cell() {
        state = 0;
    }
    Cell(byte val) {
        state = val;
    }
    Cell(Cell cell) {
        this(cell.getState());
    }
    byte getState() {
        return state;
    }
    void setState(byte newState) {
        state = newState;
    }
}
