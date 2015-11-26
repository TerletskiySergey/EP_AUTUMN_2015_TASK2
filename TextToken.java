package tasks.task2.variant10;


import java.io.*;
import java.lang.reflect.Constructor;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public abstract class TextToken {

    protected static class TextTokenIterator implements Iterator<TextToken> {
        private StringBuilder buf;
        private Constructor<? extends TextToken> defConst;
        private BufferedReader fileReader;
        private Matcher mat;

        public TextTokenIterator(String src, Class<? extends TextToken> type) {
            this.buf = (src == null) ? new StringBuilder() : new StringBuilder(src);
            this.defConst = getTokenConstructor(type);
            this.mat = getTokenMatcher();
        }

        public TextTokenIterator(File src, Class<? extends TextToken> type) {
            try {
                this.fileReader = new BufferedReader(new FileReader(src));
            } catch (FileNotFoundException ex) {
                ex.printStackTrace();
                this.fileReader = null;
            }
            this.buf = new StringBuilder();
            this.defConst = getTokenConstructor(type);
            this.mat = getTokenMatcher();
        }

        @Override
        public boolean hasNext() {
            if (mat == null) {
                return false;
            }
            if (fileReader == null) {
                return mat.find();
            }
            return mat.find() || lookAhead();
        }

        @Override
        public TextToken next() {
            TextToken toReturn = getInstance(mat.group());
            mat.reset(buf.delete(mat.start(), mat.end()));
            return toReturn;
        }

        private TextToken getInstance(String input) {
            if (defConst == null) {
                return null;
            }
            try {
                TextToken toReturn = defConst.newInstance();
                toReturn.subTokens = toReturn.splitToSubTokens(input);
                return toReturn;
            } catch (Exception ex) {
                ex.printStackTrace();
                return null;
            }
        }

        private Constructor<? extends TextToken> getTokenConstructor(Class<? extends TextToken> type) {
            if (type == null || type == TextToken.class) {
                return null;
            }
            try {
                Constructor<? extends TextToken> toReturn = type.getDeclaredConstructor();
                toReturn.setAccessible(true);
                return toReturn;
            } catch (Exception ex) {
                ex.printStackTrace();
                return null;
            }
        }

        private Matcher getTokenMatcher() {
            TextToken inst = getInstance("");
            return (inst == null) ? null : inst.getPattern().matcher(buf);
        }

        private boolean lookAhead() {
            String str;
            try {
                while ((str = fileReader.readLine()) != null) {
                    buf.append(str).append("\n");
                    if (mat.reset(buf).find()) {
                        return true;
                    }
                }
                buf.setLength(0);
                return false;
            } catch (IOException ex) {
                ex.printStackTrace();
                return false;
            }
        }
    }

    protected List<TextToken> subTokens;

    public TextToken(String inputSeq) {
        this.subTokens = splitToSubTokens(inputSeq);
    }

    protected TextToken() {
    }

    @Override
    public boolean equals(Object other) {
        return other != null
                && this.getClass() == other.getClass()
                && Objects.equals(this.subTokens, ((TextToken) other).subTokens);
    }

    public List<TextToken> getSubTokens() {
        List<TextToken> toReturn = new ArrayList<>();
        if (subTokens != null) {
            Collections.copy(toReturn, subTokens);
        }
        return toReturn;
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

    protected abstract Pattern getPattern();

    protected abstract List<TextToken> splitToSubTokens(String input);

    public static void main(String[] args) throws NoSuchFieldException {
        TextTokenIterator it = new TextTokenIterator(new File("test1.txt"), Sentence.class);
        while (it.hasNext()) {
            System.out.println(it.next());
        }
    }

}