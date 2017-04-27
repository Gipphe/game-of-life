package rules;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class RuleSetTest {
    @Test
    void should_evaluate_as_expected_with_conway_rules() {
        StateRange[] surviveStateRanges = new StateRange[] {
                new StateRange(2, 3)
        };
        StateRange[] birthStateRanges = new StateRange[] {
                new StateRange(3, 3)
        };
        RuleSet conway = new RuleSet("Conway", surviveStateRanges, birthStateRanges);
        assertEquals(0, conway.getNewState((byte) 1, 1), "Alive cell dies of loneliness");
        assertEquals(0, conway.getNewState((byte) 1, 4), "Alive cell dies of overpopulation");
        assertEquals(0, conway.getNewState((byte) 0, 2), "Dead cell remains dead with too few neighbors");
        assertEquals(0, conway.getNewState((byte) 0, 4), "Dead cell remains dead with too many neighbors");
        assertEquals(1, conway.getNewState((byte) 1, 2), "Alive cell remains alive with 2 neighbors");
        assertEquals(1, conway.getNewState((byte) 1, 3), "Alive cell remains alive with 3 neighbors");
        assertEquals(1, conway.getNewState((byte) 0, 3), "Dead cell comes to life with 3 neighbors");
    }

    @Test
    void should_evaluate_as_expected_with_highlife_rules() {
        StateRange[] surviveRangs = new StateRange[] {
                new StateRange(2, 3)
        };
        StateRange[] birthStateRanges = new StateRange[] {
                new StateRange(3),
                new StateRange(6)
        };
        RuleSet highlife = new RuleSet("Highlife", surviveRangs, birthStateRanges);
        assertEquals(0, highlife.getNewState((byte) 1, 1), "Alive cell dies of loneliness");
        assertEquals(0, highlife.getNewState((byte) 1, 4), "Alive cell dies of overpopulation");
        assertEquals(0, highlife.getNewState((byte) 0, 2), "Dead cell remains dead with too few neighbors");
        assertEquals(0, highlife.getNewState((byte) 0, 4), "Dead cell remains dead with too many neighbors");
        assertEquals(1, highlife.getNewState((byte) 1, 2), "Alive cell remains alive with 2 neighbors");
        assertEquals(1, highlife.getNewState((byte) 1, 3), "Alive cell remains alive with 3 neighbors");
        assertEquals(1, highlife.getNewState((byte) 0, 3), "Dead cell comes to life with 3 neighbors");
        assertEquals(1, highlife.getNewState((byte) 0, 6), "Dead cell comes to life with 6 neighbors");
    }

}