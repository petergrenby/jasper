package se.grenby.jasper.stack;

public abstract class StackElementWrapper {

    private final StackElement stackElement;

    protected StackElementWrapper(StackElement stackElement) {
        this.stackElement = stackElement;
    }

    public boolean isMap() {
        return stackElement == StackElement.MAP;
    }

    public boolean isList() {
        return stackElement == StackElement.LIST;
    }

    public boolean isKey() {
        return stackElement == StackElement.KEY;
    }

    public boolean isKeyValue() {
        return stackElement == StackElement.KEY_VALUE;
    }

    public boolean isText() {
        return stackElement == StackElement.TEXT;
    }

    public boolean isPrimitive() {
        return stackElement == StackElement.PRIMITIVE;
    }

    public boolean isNull() {
        return stackElement == StackElement.NULL;
    }

    public StackElement getStackElement() {
        return stackElement;
    }

}
