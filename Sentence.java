package tasks.task2.variant10;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Class represents a sentence as a structural unit of the text.
 * Class instance substitutes all the char sequences, that
 * match PATTERN class constant.
 *
 * @author Sergey Terletskiy
 * @version 1.0 29/11/2015
 */
public class Sentence extends TextToken {

    /**
     * Pattern, that is used for searching matching sub-strings.
     */
    public static final Pattern PATTERN = Pattern.compile("([^\\s\\p{Cntrl}]|\\s)+?(([.!?]+(\\s+|$))|(\\n+(?=( {3,}|\\t+)\\p{javaUpperCase})))");

    /**
     * Sub-class, that overrides PunctMark.#parse(String inputSeq) method,
     * excluding input sequence check from the default steps of parse procedure.
     * Class instances are used in conditions, where input sequence is guaranteed
     * matches the PunctMark.PATTERN.
     */
    private static class PunctMarkCheckFree extends PunctMark {

        /**
         * Overrides the sequence of steps to parse TextToken object
         * from input sequence, that are determined in PunctMark#parse(String inputSeq) method.
         * Excludes input sequence check from the default steps of parse procedure.
         *
         * @param inputSeq input String object to be parsed.
         * @return result of parsing.
         * @see TextToken#parse(String inputSeq)
         */
        @Override
        public PunctMark parse(String inputSeq) {
            subTokens = splitToSubTokens(inputSeq);
            return this;
        }
    }

    /**
     * Sub-class, that overrides Word.#parse(String inputSeq) method,
     * excluding input sequence check from the default steps of parse procedure.
     * Class instances are used in conditions, where input sequence is guaranteed
     * matches the Word.PATTERN.
     */
    private static class WordCheckFree extends Word {

        /**
         * Overrides the sequence of steps to parse TextToken object
         * from input sequence, that are determined in Word#parse(String inputSeq) method.
         * Excludes input sequence check from the default steps of parse procedure.
         *
         * @param inputSeq input String object to be parsed.
         * @return result of parsing.
         * @see TextToken#parse(String inputSeq)
         */
        @Override
        public Word parse(String inputSeq) {
            subTokens = splitToSubTokens(inputSeq);
            return this;
        }
    }

    public Sentence() {
    }

    @Override
    public boolean equals(Object other) {
        return this == other
                || other != null
                && other instanceof Sentence
                && this.getPattern().pattern().equals(((Sentence) other).getPattern().pattern())
                && Objects.equals(this.subTokens, ((TextToken) other).subTokens);
    }

    @Override
    public int hashCode() {
        return Objects.hash(PATTERN.pattern(), subTokens);
    }


    /**
     * Method calculates occurrence quantity of passed TextToken-object in the list
     * of sub-tokens of this instance. Method can not be invoked before #parse(String inputSeq)
     * method invocation.
     *
     * @param token input TextToken-object, which occurrence quantity to be calculated.
     * @return occurrence quantity.
     * @throws IllegalArgumentException in case, if method was invoked before #parse(String inputSeq)
     *                                  method invocation.
     * @see #parse(String inputSeq)
     */
    public int occurFreq(TextToken token) {
        checkIfParsed();
        return (cache.get(token) == null) ? 0 : Collections.frequency(subTokens, token);
    }

    /**
     * Overrides super-class method making it public accessible.
     *
     * @param inputSeq input String object to be parsed.
     * @return Sentence object - result of parsing.
     * @see TextToken#parse(String inputSeq)
     */
    public Sentence parse(String inputSeq) {
        return (Sentence) super.parse(inputSeq);
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
     * sequence of sub-tokens. Method successively creates Word and PuncMarc instances
     * from the matched sub-strings of input string. Method is used in default implementation
     * of TextToken#parse(String inputSeq) method.
     *
     * @param input input sequence to be split.
     * @return List object, containing sequence of split sub-tokens.
     * @see TextToken#parse(String inputSeq)
     */
    @Override
    protected List<TextToken> splitToSubTokens(String input) {
        List<TextToken> elements = new ArrayList<>();
        Matcher pmMatcher = PunctMark.PATTERN.matcher(input);
        Matcher wMatcher = Word.PATTERN.matcher(input);
        int startIndex = 0;
        boolean b1, b2;
        while ((b1 = pmMatcher.find(startIndex)) | (b2 = wMatcher.find(startIndex))) {
            elements.add(b1 ^ b2
                    ?
                    b1
                            ? toCache(new PunctMarkCheckFree().parse(pmMatcher.group()))
                            : toCache(new WordCheckFree().parse(wMatcher.group()))
                    :
                    pmMatcher.start() < wMatcher.start()
                            ? toCache(new PunctMarkCheckFree().parse(pmMatcher.group()))
                            : toCache(new WordCheckFree().parse(wMatcher.group()))
            );
            startIndex = (elements.get(elements.size() - 1) instanceof PunctMark)
                    ? pmMatcher.end()
                    : wMatcher.end();
        }
        return elements;
    }

    /**
     * Checks if the list of sub-tokens of this instance is not null.
     * Method is used in #occurFreq(TextToken token) method to prevent
     * invocation of the method before the parse procedure took place.
     *
     * @throws IllegalArgumentException if list of sub-tokens of this
     *                                  instance is null.
     */
    private void checkIfParsed() {
        if (subTokens == null) {
            throw new IllegalArgumentException();
        }
    }
}
