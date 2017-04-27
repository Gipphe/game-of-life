package rules;

public class RuleException extends RuntimeException {
    private String firstRule;
    private String secondRule;
    private String message;

    @Override
    public String getMessage() {
        return message;
    }

    /**
     * firstRule is one of the two mismatched rules this RuleException represents.
     * @return The first rule of the rule mismatch.
     */
    public String getFirstRule() {
        return firstRule;
    }

    /**
     * secondRule is one of the two mismatched rules this RuleException represents.
     * @return The second rule of the rule mismatch.
     */
    public String getSecondRule() {
        return secondRule;
    }

    public RuleException(String firstRule, String secondRule) {
        super();
        this.firstRule = firstRule;
        this.secondRule = secondRule;
        message = "Rules do not match: " + firstRule + ", " + secondRule;
    }
}
