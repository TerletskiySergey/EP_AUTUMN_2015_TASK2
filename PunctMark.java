package tasks.task2.variant10;

import java.util.List;
import java.util.Objects;
import java.util.regex.Pattern;

public class PunctMark extends TextToken {

    public static final Pattern PATTERN = Pattern.compile("[\\p{Punct}]");

    private String content;

    public PunctMark(String input) {
        super(input);
    }

    private PunctMark(){}

    @Override
    public boolean equals(Object other) {
        return this == other
                || super.equals(other)
                && this.content.equals(((PunctMark) other).content);
    }

    @Override
    public int hashCode() {
        return Objects.hash(PATTERN.pattern(), subTokens, content);
    }

    @Override
    public String toString() {
        return content;
    }

    @Override
    protected Pattern getPattern() {
        return PATTERN;
    }

    @Override
    protected List<TextToken> splitToSubTokens(String input) {
        this.content = input;
        return null;
    }
}