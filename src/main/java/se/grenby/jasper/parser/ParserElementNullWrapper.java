package se.grenby.jasper.parser;

public class ParserElementNullWrapper extends ParserElementWrapper {

    private static final char[] NULL_CHARS = "null".toCharArray();
    private int index = 0;

    public ParserElementNullWrapper() {
        super(ParserElement.NULL);
    }

    public boolean valid(char ch) {
        if (ch == NULL_CHARS[index]) {
            index++;
            return true;
        } else {
            return false;
        }
    }

    public boolean finished() {
        if (index >= 4) {
            return true;
        } else {
            return false;
        }
    }
}
