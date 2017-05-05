package model.board;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

@Deprecated
class ReferenceCellTest {
    @Test
    void initializes_as_dead() {
        ReferenceCell result = new ReferenceCell();
        assertEquals("cell is dead", result.toString());
    }
    @Test
    void initializes_as_passed() {
        ReferenceCell result = new ReferenceCell(true);
        assertEquals("cell is alive", result.toString());
        result = new ReferenceCell(false);
        assertEquals("cell is dead", result.toString());
    }
    @Test
    void is_resurrected_on_resurrect() {
        ReferenceCell result = new ReferenceCell().resurrect();
        assertEquals("cell is alive", result.toString());
    }
    @Test
    void is_killed_on_kill() {
        ReferenceCell result = new ReferenceCell().resurrect();
        assertEquals("cell is alive", result.toString());
        result.kill();
        assertEquals("cell is dead", result.toString());
    }
}