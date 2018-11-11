package se.grenby.jasper.stack;

public class StackElementTextWrapper extends StackElementWrapper {

    private final StringBuilder text = new StringBuilder();

    public StackElementTextWrapper() {
        super(StackElement.TEXT);
    }

    protected StackElementTextWrapper(StackElement stackElement) {
        super(stackElement);
    }

    public void addChar(char ch) {
        text.append(ch);
    }

    public String getText() {
        return text.toString();
    }

}
