package se.grenby.jasper;

public class ParserCharacterWrapper {
    private static final char STRING_DELIMITER = '\"';
    private static final char MAP_BEGIN_DELIMITER = '{';
    private static final char MAP_KEY_VALUE_DELIMITER = ':';
    private static final char MAP_END_DELIMITER = '}';
    private static final char LIST_BEGIN_DELIMITER = '[';
    private static final char LIST_END_DELIMITER = ']';
    private static final char ITEM_DELIMITER = ',';

    private final char ch;

    public ParserCharacterWrapper(char ch) {
        this.ch = ch;
    }

    public boolean isDelimiter() {
        return ch == STRING_DELIMITER ||
                ch == MAP_BEGIN_DELIMITER ||
                ch == MAP_KEY_VALUE_DELIMITER ||
                ch == MAP_END_DELIMITER ||
                ch == LIST_BEGIN_DELIMITER ||
                ch == LIST_END_DELIMITER ||
                ch == ITEM_DELIMITER;
    }

    public boolean isMapBeginDelimiter() {
        return ch == MAP_BEGIN_DELIMITER;
    }

    public boolean isListBeginDelimiter() {
        return ch == LIST_BEGIN_DELIMITER;
    }

    public boolean isWhitespace() {
        return Character.isWhitespace(ch);
    }

    public boolean isDigit() {
        return Character.isDigit(ch);
    }

    public boolean isMapKeyValueDelimiter() {
        return ch == MAP_KEY_VALUE_DELIMITER;
    }

    public boolean isStringDelimiter() {
        return ch == STRING_DELIMITER;
    }

    public char charValue() {
        return ch;
    }

    public boolean isMapEndDelimiter() {
        return ch == MAP_END_DELIMITER;
    }

    public boolean isItemDelimiter() {
        return ch == ITEM_DELIMITER;
    }

    public boolean isListEndDelimiter() {
        return ch == LIST_END_DELIMITER;
    }

    public boolean isFirstCharInBoolean() {
        return ch == 't' || ch == 'f';
    }

    public boolean isFirstCharInNull() {
        return ch == 'n';
    }
}
