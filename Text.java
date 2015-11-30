
package tasks.task2.variant10;

import java.io.*;
import java.util.*;
import java.util.regex.Pattern;

/**
 * Class represents a structural unit containing
 * sequence of sentences.
 *
 * @author Sergey Terletskiy
 * @version 1.0 29/11/2015
 */
public class Text extends TextToken {

    /**
     * Class represents a navigation tool for searching Sentence instances in source,
     * which role can be performed by a String or a Reader instance.
     */
    private class SentenceIterator extends TextToken.Iterator<Sentence> {

        /**
         * Constructor, that specifies a String instance as a source
         * to search in.
         */
        SentenceIterator(String src) {
            super(src);
        }

        /**
         * Constructor, that specifies a Reader instance as a source
         * to search in.
         */
        SentenceIterator(Reader src) {
            super(src);
        }

        /**
         * Determines type of an object, that is returned by super.next() method invocation.
         * Method returns Sentence sub-class, that overrides Sentence#parse(String inputSeq)
         * and TextToken#toCache(TextToken token) methods allowing to parse a Sentence
         * without invocation input check method and allowing to store cached sub-tokens
         * of a sentence in external cache i.e. in the cache of Text.this instance.
         *
         * @param inputSeq input sequence for parsing by a newly created object.
         * @return Sentence object, that is returned by #next() method.
         * @see TextToken.Iterator#next()
         */
        @Override
        protected Sentence getInstance(String inputSeq) {
            return (inputSeq == null)
                    ? null
                    : new Sentence() {

                /**
                 * Overrides super-class method allowing to parse a Sentence
                 * without invocation input check method
                 *
                 * @param inputSeq input String object to be parsed.
                 * @return Sentence object - result of parsing.
                 * @see Sentence#parse(String inputSeq)
                 */
                @Override
                public Sentence parse(String inputSeq) {
                    subTokens = splitToSubTokens(inputSeq);
                    return this;
                }

                /**
                 * Overrides super-class method allowing to store cached sub-tokens
                 * of a sentence in external cache i.e. in the cache of Text.this instance.
                 *
                 * @param token TextToken reference to be added to cache.
                 * @return result of caching: passed reference if passed reference is a new one in cache;
                 * or cached reference if reference to an equal object is already in cache.
                 */
                @Override
                protected TextToken toCache(TextToken token) {
                    TextToken cached = Text.this.cache.get(token);
                    if (cached != null) {
                        super.toCache(cached);
                        return cached;
                    }
                    Text.this.cache.put(token, token);
                    super.toCache(token);
                    return token;
                }
            }.parse(inputSeq);
        }
    }

    /**
     * Instance of SentenceIterator, that is used for parsing Sentence
     * instances from source
     */
    private SentenceIterator iter;

    /**
     * Constructor, that specifies a String instance as a source
     * to search in.
     */
    public Text(String src) {
        this.cache = new HashMap<>();
        this.iter = new SentenceIterator(src);
    }

    /**
     * Constructor, that specifies a Reader instance as a source
     * to search in.
     */
    public Text(Reader src) {
        this.cache = new HashMap<>();
        this.iter = new SentenceIterator(src);
    }

    /**
     * Gets Sentence instance stored in the internal list of sub-tokens.
     * Method can not be invoked before #parse() method invocation.
     *
     * @param index index of a sentence in parsed text.
     * @return Sentence instance, stored in internal list of sub-tokens with the passed index.
     * @throws IllegalArgumentException in case, if index lies within inappropriate borders.
     * @throws IllegalStateException    in case, if method was invoked before #parse()
     *                                  method invocation.
     */
    public Sentence getSentence(int index) {
        checkIfParsed();
        if (index < 0 || index >= subTokens.size()) {
            throw new IllegalArgumentException();
        }
        return (Sentence) subTokens.get(index);
    }

    /**
     * Method calculates occurrence quantity of TextToken instances from the passed list.
     * Method can not be invoked before #parse() method invocation.
     *
     * @param tokens list of TextToken instances which occurrence quantity to be estimated.
     * @return map instance, which uses TextToken instance from input list as a key and Integer value
     * of occurrence quantity as a value.
     * @throws IllegalStateException in case, if method was invoked before #parse()
     *                               method invocation.
     */
    public Map<TextToken, Integer> occurFreq(List<TextToken> tokens) {
        checkIfParsed();
        Map<TextToken, Integer> res = new HashMap<>();
        for (TextToken token : tokens) {
            if (cache.get(token) == null) {
                res.put(token, 0);
            } else {
                int freq = 0;
                for (TextToken sub : subTokens) {
                    freq += ((Sentence) sub).occurFreq(token);
                }
                res.put(token, freq);
            }
        }
        return res;
    }

    /**
     * Method calculates occurrence quantity of TextToken instances from the passed list
     * in each sentence of parsed text. Method can not be invoked before #parse() method invocation.
     *
     * @param tokens list of TextToken instances which occurrence frequency to be estimated.
     * @return map instance, which uses TextToken instance from input list as a key and a map instance
     * as a value. Key of a value-map represents index of sentence, in which the occurrence took place,
     * value of a value-map represents occurrence quantity.
     * @throws IllegalStateException in case, if method was invoked before #parse()
     *                               method invocation.
     */
    public Map<TextToken, Map<Integer, Integer>> occurFreqPerSentence(List<TextToken> tokens) {
        checkIfParsed();
        Map<TextToken, Map<Integer, Integer>> res = new HashMap<>();
        for (TextToken token : tokens) {
            res.put(token, new HashMap<>());
            for (int i = 0; i < subTokens.size(); i++) {
                int occur = ((Sentence) subTokens.get(i)).occurFreq(token);
                if (occur > 0) {
                    res.get(token).put(i, occur);
                }
            }
        }
        return res;
    }

    /**
     * Parses source text to the sequence of Sentence instances.
     * Logic of parsing is determined in overridden #splitToSubTokens(String input)
     * method.
     *
     * @return this instance
     */
    public Text parse() {
        return (Text) super.parse(null);
    }

    /**
     * Determines the logic of splitting of source data to the sequence of sub-tokens.
     * Method successively searches and creates Sentence instances. Method is used in default
     * implementation of TextToken#parse(String inputSeq) method.
     *
     * @param input input sequence to be split. In current implementation doesn't match.
     * @return List object, containing sequence of split Sentence instances - sub-tokens of this instance.
     * @see TextToken#parse(String inputSeq)
     */
    @Override
    protected List<TextToken> splitToSubTokens(String input) {
        List<TextToken> toReturn = new ArrayList<>();
        while (iter.hasNext()) {
            toReturn.add(iter.next());
        }
        iter = null;
        return toReturn;
    }

    /**
     * Returns quantity of parsed Sentence instances.
     * Method can not be invoked before #parse() method invocation.
     *
     * @return quantity of parsed Sentence instances.
     */
    public int size() {
        checkIfParsed();
        return subTokens.size();
    }

    /**
     * Method sorts passed List of TextToken instances according to their occurrence quantity
     * in the source data.
     *
     * @param tokens List of TextToken instances to be sorted.
     */
    public void sortByOccurFreq(List<TextToken> tokens) {
        checkIfParsed();
        Map<TextToken, Integer> freqMap = occurFreq(tokens);
        Collections.sort(tokens, (o1, o2) -> freqMap.get(o1).compareTo(freqMap.get(o2)));
    }

    /**
     * Stub method, that is used for proper execution of TextToken#parse(String inputSeq)
     * method.
     */
    @Override
    protected Pattern getPattern() {
        return null;
    }

    /**
     * Checks if the list of sub-tokens of this instance is not null.
     * Method is used to prevent invocation of the method before
     * the parse procedure took place.
     *
     * @throws IllegalArgumentException if list of sub-tokens of this
     *                                  instance is null.
     */
    private void checkIfParsed() {
        if (subTokens == null) {
            throw new IllegalStateException("source not parsed yet");
        }
    }

    public static void main(String[] args) throws FileNotFoundException {
        BufferedReader br = new BufferedReader(new FileReader("src/tasks/task2/variant10/resources/srcText.txt"));
        Text text = new Text(br);
        text.parse();
        TextToken w1 = new Word().parse("в");
        TextToken w2 = new Word().parse("на");
        TextToken w3 = new PunctMark().parse(";");
        List<TextToken> list = Arrays.asList(w1, w2, w3);
        System.out.println(text.occurFreqPerSentence(list));
        System.out.println(text.occurFreq(list));
        text.sortByOccurFreq(list);
        System.out.println(list);
        System.out.println(text.getSentence(14));
    }
}