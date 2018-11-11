package se.grenby.jasper.stack;

public class StackElementNullWrapper extends StackElementWrapper {

    private static final String NULL = "null";
    private static final char[] NULL_CHARS = NULL.toCharArray();
    private int index = 0;

    public StackElementNullWrapper() {
        super(StackElement.NULL);
    }

    public boolean validate(char ch) {
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
