package se.grenby.jasper.parser;

public class ParserElementTextWrapper extends ParserElementWrapper {

    private final StringBuilder text = new StringBuilder();

    public ParserElementTextWrapper() {
        super(ParserElement.TEXT);
    }

    protected ParserElementTextWrapper(ParserElement parserElement) {
        super(parserElement);
    }

    public void addChar(char ch) {
        text.append(ch);
    }

    public String getText() {
        return text.toString();
    }

}
