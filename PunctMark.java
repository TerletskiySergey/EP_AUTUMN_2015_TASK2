package tasks.task2.variant10;

import java.util.List;
import java.util.Objects;
import java.util.regex.Pattern;

/**
 * Class represents a punctuation mark as a structural unit of the text.
 * Class instance substitutes all the separately standing chars, that
 * match Pattern class {Punct}.
 *
 * @author Sergey Terletskiy
 * @version 1.0 29/11/2015
 */
public class PunctMark extends TextToken {

    /**
     * Pattern object, that is used for finding matching string sub-sequences.
     */
    public static final Pattern PATTERN = Pattern.compile("[\\p{Punct}]");

    /**
     * String object, that can not be split to sub-tokens.
     */
    private String content;

    public PunctMark() {
    }

    @Override
    public boolean equals(Object other) {
        return this == other
                || other != null
                && other instanceof PunctMark
                && this.getPattern().pattern().equals(((PunctMark) other).getPattern().pattern())
                && this.content.equals(((PunctMark) other).content);
    }

    @Override
    public int hashCode() {
        return Objects.hash(PATTERN.pattern(), subTokens, content);
    }

    /**
     * Overrides the default sequence of steps to parse TextToken object
     * from input sequence, that are determined in TextToken#parse(String inputSeq) method.
     *
     * @param inputSeq input String object to be parsed.
     * @return result of parsing.
     * @see TextToken#parse(String inputSeq)
     */
    @Override
    public PunctMark parse(String inputSeq) {
        if (content != null) {
            return this;
        }
        return (PunctMark) super.parse(inputSeq);
    }

    @Override
    public String toString() {
        return content;
    }

    /**
     * Method, that implements feedback with super-class.
     * Gets Pattern object, which is associated with this object.
     *
     * @return Pattern object, which is associated with this object.
     */
    @Override
    protected Pattern getPattern() {
        return PATTERN;
    }

    /**
     * Determines the logic of splitting of input string sequence to the
     * sequence of sub-tokens. Method is used in default implementation
     * of TextToken#parse(String inputSeq) method.
     *
     * @param input input sequence to be split.
     * @return List object, containing sequence of split sub-tokens.
     * @see TextToken#parse(String inputSeq)
     */
    @Override
    protected List<TextToken> splitToSubTokens(String input) {
        this.content = input;
        return null;
    }
}