package tasks.task2.variant10;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Sentence extends TextToken {

    public static final Pattern PATTERN = Pattern.compile("([^\\s\\p{Cntrl}]|\\s)+?(([.!?]+\\s+)|(\\n+(?=( {3,}|\\t+)\\p{javaUpperCase})))");
//    public static final Pattern PATTERN = Pattern.compile("([^\\s\\p{Cntrl}]|\\s)+?[.!?]+\\s+");
//    public static final Pattern PATTERN = Pattern.compile("((([+-]?(([0-9]?(\\.|,)[0-9]+)|([0-9]+((\\.|,)[0-9]*)?)))|\\p{javaLetterOrDigit}+)|(\\p{Punct})|(\\s))+?[.!?]+\\s+");
//    public static final Pattern PATTERN = Pattern.compile("((" + Word.PATTERN + ")|(" + PunctMark.PATTERN + ")|\\s)+?(([.!?]+\\s+)|(?=\n\\s*[A-ZА-ЯЁ]+))");
//    public static final Pattern PATTERN = Pattern.compile("((" + Word.PATTERN.pattern() + ")|(" + PunctMark.PATTERN.pattern() + ")|\\s)+?[.!?]+\\s+");

    public Sentence(String input) {
        super(input);
    }

    private Sentence() {
    }

    @Override
    public boolean equals(Object other) {
        return this == other
                || super.equals(other);
    }

    @Override
    public int hashCode() {
        return Objects.hash(PATTERN.pattern(), subTokens);
    }

    /**
     * Method calculates occurrence quantity of passed input SentenceElement-object in current Sentence
     * (i.e. in the private List of SentenceElement-objects that represent Sentence).
     *
     * @param el Input SentenceElement-object, which occurrence quantity to be calculated.
     * @return Map, which uses SentenceElement of current Sentence as a key and Integer value
     * of occurrence quantity in the current Sentence as a value.
     */

    public int occurFreq(TextToken el) {
        return Collections.frequency(getSubTokens(), el);
    }

    /**
     * Method calculates occurrence quantity of each SentenceElement of Sentence.
     *
     * @return Map, which uses SentenceElement of current Sentence as a key and Integer value
     * of occurrence quantity in the current Sentence as a value.
     */

    public Map<TextToken, Integer> occurFreq() {
        Map<TextToken, Integer> toReturn = new HashMap<>();
        int count;
        List<TextToken> subTokens = getSubTokens();
        for (int i = 0; i < subTokens.size(); i++) {
            if (toReturn.containsKey(subTokens.get(i))) {
                continue;
            }
            count = 1;
            for (int j = i + 1; j < subTokens.size(); j++) {
                if (subTokens.get(i).equals(subTokens.get(j))) {
                    count++;
                }
            }
            toReturn.put(subTokens.get(i), count);
        }
        return toReturn;
    }

    @Override
    protected Pattern getPattern() {
        return PATTERN;
    }

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
                    b1 ? new PunctMark(pmMatcher.group()) : new Word(wMatcher.group())
                    :
                    pmMatcher.start() < wMatcher.start() ? new PunctMark(pmMatcher.group()) : new Word(wMatcher.group())
            );
            startIndex = (elements.get(elements.size() - 1) instanceof PunctMark)
                    ? pmMatcher.end() : wMatcher.end();
        }
        return elements;
    }

/*    @Override
    public String toString() {
        StringBuilder toReturn = new StringBuilder("");
        for (SentenceElement se : content) {
            toReturn.append(se + " ");
        }
        if (toReturn.length() > 0) {
            toReturn.setLength(toReturn.length() - 1);
        }
        return toReturn.toString();
    }*/
}
