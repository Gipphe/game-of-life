package RLE;

/**
 * Container for an RLE to ArrayListBoard parsing's output, containing the parsed pattern and the rule the pattern is designed
 * for. Can be expanded to include metadata such as author, creation date etc.
 */
public class ParsedPattern {
    /**
     * Name of this pattern.
     */
    private final String name;

    /**
     * Author of this pattern.
     */
    private final String author;

    /**
     * Description of this pattern.
     */
    private final String description;

    /**
     * The rule designation that this pattern is designed for.
     */
    private final String rule;

    /**
     * The pattern itself, represented as a two-dimensional byte array, with 0 for dead and 1 for alive, for simplicity.
     */
    private final byte[][] pattern;

    /**
     * The date this pattern was created.
     */
    private final String date;

    /**
     * Constructor.
     * @param name The name of this parsed pattern.
     * @param author The author of this parsed pattern.
     * @param description The description for this pattern.
     * @param date The date this pattern was created.
     * @param rule The rule string this pattern was designed for.
     * @param pattern The pattern itself.
     */
    public ParsedPattern(String name, String author, String description, String date, String rule, byte[][] pattern) {
        if (rule.length() <= 0) {
            throw new RLEException("Rule must be defined");
        }
        this.name = name;
        this.author = author;
        this.description = description;
        this.rule = rule;
        this.pattern = pattern;
        this.date = date;
    }

    /**
     * Constructor
     * @param rule The rule this pattern is designed for.
     * @param pattern The pattern itself.
     */
    ParsedPattern(String rule, byte[][] pattern) {
        if (rule.length() <= 0) {
            throw new RLEException("Rule must be defined");
        }
        this.name = "";
        this.author = "";
        this.description = "";
        this.date = "";
        this.rule = rule;
        this.pattern = pattern;
    }

    /**
     * @return The rule this pattern is designed for.
     */
    public String getRule() {
        return rule;
    }

    /**
     * @return The pattern itself.
     */
    public byte[][] getPattern() {
        return pattern;
    }

    /**
     * @return The name of the pattern.
     */
    String getName() {
        return name;
    }

    /**
     * @return The name of the author.
     */
    String getAuthor() {
        return author;
    }

    /**
     * @return The description of the pattern.
     */
    String getDescription() {
        return description;
    }

    /**
     * @return The date this pattern was created.
     */
    String getDate() {
        return date;
    }
}
