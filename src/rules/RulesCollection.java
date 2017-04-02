package rules;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.ArrayList;

/**
 * Represents all registered rule sets.
 */
public class RulesCollection {
    private static final List<RuleSet> collection;
    static {
        // Conway's rule set.
        Range[] surviveRanges = new Range[] { new Range(2, 3) };
        Range[] birthRanges = new Range[] { new Range(3) };
        RuleSet conway = new RuleSet("Conway", surviveRanges, birthRanges);

        // Highlife
        Range[] highSRanges = new Range[] { new Range(2, 3) };
        Range[] highBRanges = new Range[] { new Range(3), new Range(6) };
        RuleSet highlife = new RuleSet("Highlife", highSRanges, highBRanges);

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
