package tasks.task2.variant10;

import java.util.List;
import java.util.Objects;
import java.util.regex.Pattern;

public class Word extends TextToken {

    public static final Pattern PATTERN = Pattern.compile("([+-]?(([0-9]?[\\.,][0-9]+)|([0-9]+([\\.,][0-9]*)?)))" +
            "|(\\p{javaLetterOrDigit}+(-\\p{javaLetterOrDigit}+)?)+");
//            "|([\\P{Cntrl}&&\\P{Punct}&&\\P{Space}]+)");

    private String content;

    public Word(String input) {
        super(input);
    }

    private Word() {
    }

    @Override
    public boolean equals(Object other) {
        return this == other
                || super.equals(other)
                && this.content.equals(((Word) other).content);
    }

    @Override
    public int hashCode() {
        return Objects.hash(PATTERN.pattern(), content);
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
//        input = input.replace("\n", "");
//        System.out.println("word input = " + input);
        this.content = input;
        return null;
    }
}