package rules;

import model.state.ReferenceState;
import model.state.State;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

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
        State alive = new ReferenceState(true);
        assertEquals(false, alive.equals(conway.getNewState(ReferenceState.getAlive(), 1)), "Alive cell dies of loneliness");
        assertEquals(false, alive.equals(conway.getNewState(ReferenceState.getAlive(), 4)), "Alive cell dies of overpopulation");
        assertEquals(false, alive.equals(conway.getNewState(ReferenceState.getDead(), 2)), "Dead cell remains dead with too few neighbors");
        assertEquals(false, alive.equals(conway.getNewState(ReferenceState.getDead(), 4)), "Dead cell remains dead with too many neighbors");
        assertEquals(true, alive.equals(conway.getNewState(ReferenceState.getAlive(), 2)), "Alive cell remains alive with 2 neighbors");
        assertEquals(true, alive.equals(conway.getNewState(ReferenceState.getAlive(), 3)), "Alive cell remains alive with 3 neighbors");
        assertEquals(true, alive.equals(conway.getNewState(ReferenceState.getDead(), 3)), "Dead cell comes to life with 3 neighbors");
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
        State alive = new ReferenceState(true);
        RuleSet highlife = new RuleSet("Highlife", surviveRangs, birthStateRanges);
        assertEquals(false, alive.equals(highlife.getNewState(ReferenceState.getAlive(), 1)), "Alive cell dies of loneliness");
        assertEquals(false, alive.equals(highlife.getNewState(ReferenceState.getAlive(), 4)), "Alive cell dies of overpopulation");
        assertEquals(false, alive.equals(highlife.getNewState(ReferenceState.getDead(), 2)), "Dead cell remains dead with too few neighbors");
        assertEquals(false, alive.equals(highlife.getNewState(ReferenceState.getDead(), 4)), "Dead cell remains dead with too many neighbors");
        assertEquals(true, alive.equals(highlife.getNewState(ReferenceState.getAlive(), 2)), "Alive cell remains alive with 2 neighbors");
        assertEquals(true, alive.equals(highlife.getNewState(ReferenceState.getAlive(), 3)), "Alive cell remains alive with 3 neighbors");
        assertEquals(true, alive.equals(highlife.getNewState(ReferenceState.getDead(), 3)), "Dead cell comes to life with 3 neighbors");
        assertEquals(true, alive.equals(highlife.getNewState(ReferenceState.getDead(), 6)), "Dead cell comes to life with 6 neighbors");
    }

}