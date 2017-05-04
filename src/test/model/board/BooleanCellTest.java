package model.board;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@Deprecated
class BooleanCellTest {
    @Test
    void initializes_as_dead() {
        BooleanCell result = new BooleanCell();
        assertEquals("cell is dead", result.toString());
    }
    @Test
    void initializes_as_passed() {
        BooleanCell result = new BooleanCell(true);
        assertEquals("cell is alive", result.toString());
        result = new BooleanCell(false);
        assertEquals("cell is dead", result.toString());
    }
    @Test
    void is_resurrected_on_resurrect() {
        BooleanCell result = new BooleanCell().resurrect();
        assertEquals("cell is alive", result.toString());
    }
    @Test
    void is_killed_on_kill() {
        BooleanCell result = new BooleanCell().resurrect();
        assertEquals("cell is alive", result.toString());
        result.kill();
        assertEquals("cell is dead", result.toString());
    }
}