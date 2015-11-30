package tasks.task2.variant10;

import java.io.Reader;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Abstract class representing a structural unit of a text.
 * Instance of a class encapsulates sub-tokens, on which this instance
 * can be divided, sub tokens of this instance can be cached by placing
 * references in cache-map, but not obligatory, caching rules and sub-tokens
 * storage rules are set in subclass.
 *
 * @author Sergey Terletskiy
 * @version 1.0 29/11/2015
 */
public abstract class TextToken {

    /**
     * Abstract class representing a navigation tool for searching TextToken instances in source,
     * which role can be performed by a String or a Reader instance.
     */
    protected static abstract class Iterator<T extends TextToken> implements java.util.Iterator<T> {

        /**
         * Indicates if the initialization was successful.
         */
        private boolean isInitSuccess;

        /**
         * Storage for the instance, that will be returned by the #next() method
         * invocation.
         */
        private T nextToken;

        /**
         * Scanner instance, which implements searching of sub-strings in a text
         * corresponding to the token pattern.
         *
         * @see #tokenPat
         */
        private Scanner scan;

        /**
         * Pattern that is used to find tokens.
         */
        private Pattern tokenPat;

        /**
         * Constructor, that specifies a String instance as a source
         * to search in.
         */
        public Iterator(String src) {
            this.tokenPat = getTokenPattern();
            this.scan = (src == null) ? null : new Scanner(src);
            this.isInitSuccess = checkIfInitSuccessful();
            this.nextToken = (isInitSuccess) ? getFirstToken() : null;
        }

        /**
         * Constructor, that specifies a Reader instance as a source
         * to search in.
         */
        public Iterator(Reader src) {
            this.tokenPat = getTokenPattern();
            this.scan = (src == null) ? null : new Scanner(src);
            this.isInitSuccess = checkIfInitSuccessful();
            this.nextToken = (isInitSuccess) ? getFirstToken() : null;
        }

        /**
         * An abstract method, which implementation determines type of an
         * object, that is returned by #next() method invocation.
         *
         * @param inputSeq input sequence for parsing by a newly created object.
         * @return object, that is returned by #next() method.
         * @see #next()
         */
        protected abstract T getInstance(String inputSeq);

        @Override
        public boolean hasNext() {
            return nextToken != null;
        }

        @Override
        public T next() {
            T toReturn = nextToken;
            nextToken = (toReturn == null) ? null : getInstance(scan.findWithinHorizon(tokenPat, 0));
            return toReturn;
        }

        /**
         * Checks if the initialization was successful.
         *
         * @return boolean flag indicating if the initialization was successful.
         */
        private boolean checkIfInitSuccessful() {
            return scan != null && tokenPat != null;
        }

        /**
         * Initializes #nextToken field value that will be returned by the first
         * invocation of #next() method.
         *
         * @return object to which #nextToken field value will be assigned.
         * @see #next()
         */
        private T getFirstToken() {
            return getInstance(scan.findWithinHorizon(tokenPat, 0));
        }

        /**
         * Gets Pattern object which is assigned to #tokenPat field.
         *
         * @return Pattern object which is used for assigning of #tokenPat field.
         * @see #tokenPat
         */
        private Pattern getTokenPattern() {
            return getInstance("").getPattern();
        }
    }

    /**
     * Storage, that can be used for caching of sub-tokens of this instance.
     */
    protected Map<TextToken, TextToken> cache;

    /**
     * Storage, that can be used to store sequence of sub-tokens of this instance.
     */
    protected List<TextToken> subTokens;

    protected TextToken() {
    }

    /**
     * Method, that implements feedback with sub-class.
     * Gets Pattern object, which is associated with this sub-class object.
     *
     * @return Pattern object, which is associated with this sub-class object.
     */
    protected abstract Pattern getPattern();

    /**
     * Determines the logic of splitting of input string sequence to the
     * sequence of sub-tokens. Method is used in default implementation
     * of #parse(String inputSeq) method.
     *
     * @param input input sequence to be split.
     * @return List object, containing sequence of split sub-tokens.
     * @see #parse(String inputSeq)
     */
    protected abstract List<TextToken> splitToSubTokens(String input);

    @Override
    public boolean equals(Object other) {
        return other != null
                && this.getClass() == other.getClass()
                && Objects.equals(this.subTokens, ((TextToken) other).subTokens);
    }

    public List<TextToken> getSubTokens() {
        if (subTokens != null) {
            return new ArrayList<>(subTokens);
        }
        return new ArrayList<>();
    }

    public String toString() {
        if (subTokens != null) {
            StringBuilder sb = new StringBuilder();
            for (TextToken token : subTokens) {
                sb.append(token.toString()).append(" ");
            }
            if (sb.length() != 0) {
                sb.delete(sb.length() - 1, sb.length());
            }
            sb.insert(0, "[")
                    .append("]");
            return sb.toString();
        }
        return super.toString();
    }

    /**
     * Determines the default sequence of steps to parse TextToken object
     * from input sequence.
     *
     * @param inputSeq input string object to be parsed.
     * @return result of parsing.
     */
    protected TextToken parse(String inputSeq) {
        if (subTokens != null) {
            return this;
        }
        inputSeq = filterInput(inputSeq);
        this.subTokens = splitToSubTokens(inputSeq);
        return this;
    }

    /**
     * Determines the default way of adding TextToken object to cache.
     *
     * @param token TextToken reference to be added to cache.
     * @return result of caching: passed reference if passed reference is a new one in cache;
     * or cached reference if reference to an equal object is already in cache.
     */
    protected TextToken toCache(TextToken token) {
        if (cache == null) {
            cache = new HashMap<>();
        }
        TextToken cached = cache.get(token);
        if (cached != null) {
            return cached;
        }
//        if (token.subTokens != null) {
//            for (TextToken sub : token.subTokens) {
//                toCache(sub);
//                sub.cache = null;
//            }
//        }
        cache.put(token, token);
        return token;
    }

    /**
     * Filters inappropriate input string sequences.
     *
     * @param inputSeq input sequence to be checked.
     * @return result of filtering: first sub-string, that matches
     * the pattern, with which this TextToken object is associated;
     * or empty string, if no matches found.
     */
    private String filterInput(String inputSeq) {
        if (getPattern() == null) {
            return null;
        }
        Matcher mat = getPattern().matcher(inputSeq);
        return (mat.find())
                ? mat.group()
                : "";
    }
}