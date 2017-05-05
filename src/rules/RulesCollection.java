package rules;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents all registered rule sets.
 */
public class RulesCollection {
    private static final List<RuleSet> collection;
    static {
        // Conway's rule set.
        StateRange[] surviveStateRanges = new StateRange[] { new StateRange(2, 3) };
        StateRange[] birthStateRanges = new StateRange[] { new StateRange(3) };
        RuleSet conway = new RuleSet("Conway", surviveStateRanges, birthStateRanges);

        // Highlife
        StateRange[] highSStateRanges = new StateRange[] { new StateRange(2, 3) };
        StateRange[] highBStateRanges = new StateRange[] { new StateRange(3), new StateRange(6) };
        RuleSet highlife = new RuleSet("Highlife", highSStateRanges, highBStateRanges);

        // Assembling collection.
        collection = new ArrayList<>();
        collection.add(conway);
        collection.add(highlife);
    }
    @Contract(pure = true)
    public static List<RuleSet> getCollection() {
        return collection;
    }
    @Nullable
    public static RuleSet getByName(String name) {
        for (RuleSet ruleSet : collection) {
            if (ruleSet.getName().equals(name)) return ruleSet;
        }
        return null;
    }
}
