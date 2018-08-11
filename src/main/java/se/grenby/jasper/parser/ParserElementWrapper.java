package se.grenby.jasper.parser;

public abstract class ParserElementWrapper {

    private final ParserElement parserElement;

    protected ParserElementWrapper(ParserElement parserElement) {
        this.parserElement = parserElement;
    }

    public boolean isMap() {
        return parserElement == ParserElement.MAP;
    }

    public boolean isList() {
        return parserElement == ParserElement.LIST;
    }

    public boolean isKey() {
        return parserElement == ParserElement.KEY;
    }

    public boolean isKeyValue() {
        return parserElement == ParserElement.KEY_VALUE;
    }

    public boolean isText() {
        return parserElement == ParserElement.TEXT;
    }

    public boolean isPrimitive() {
        return parserElement == ParserElement.PRIMITIVE;
    }

    public boolean isNull() {
        return parserElement == ParserElement.NULL;
    }

    public ParserElement getParserElement() {
        return parserElement;
    }

}
