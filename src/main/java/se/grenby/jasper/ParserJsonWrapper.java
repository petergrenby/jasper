package se.grenby.jasper;


public class ParserJsonWrapper {

    private final char[] jsonChars;
    private int index = 0;

    public ParserJsonWrapper(String json) {
        jsonChars = json.toCharArray();
    }

    public ParserCharacterWrapper next() {
        if (hasNext()) {
            char ch = jsonChars[index];
            index++;
            return new ParserCharacterWrapper(ch);
        } else {
            throw new IndexOutOfBoundsException();
        }
    }

    public boolean hasNext() {
        return index + 1 < jsonChars.length;
    }

}
