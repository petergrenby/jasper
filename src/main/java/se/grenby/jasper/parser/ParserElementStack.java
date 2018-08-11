package se.grenby.jasper.parser;

import se.grenby.jasper.schema.JSchemaList;
import se.grenby.jasper.schema.JSchemaMap;
import se.grenby.jasper.schema.JSchemaValue;

import java.util.Stack;
import java.util.stream.Collectors;

public class ParserElementStack {

    private final Stack<ParserElementWrapper> parsedStack = new Stack<>();

    public ParserElementStack() {

    }

    public void pushMap(JSchemaMap schemaMap) {
        parsedStack.push(new ParserElementMapWrapper(schemaMap));
    }

    public void pushList(JSchemaList schemaList) {
        parsedStack.push(new ParserElementListWrapper(schemaList));
    }

    public void pushKeyValue() {
        parsedStack.push(new ParserElementKeyValueWrapper());
        parsedStack.push(new ParserElementKeyWrapper());
    }

    public void pushText() {
        parsedStack.push(new ParserElementTextWrapper());
    }

    public void pushPrimitive(JSchemaValue schemaValue) {
        parsedStack.push(new ParserElementPrimitiveWrapper(schemaValue));

    }

    public void pushNull() {
        parsedStack.push(new ParserElementNullWrapper());

    }

    public boolean isTopMap() {
        return parsedStack.peek().isMap();
    }

    public boolean isTopList() {
        return parsedStack.peek().isList();
    }

    public boolean isTopKeyValue() {
        return parsedStack.peek().isKeyValue();
    }

    public boolean isTopKey() {
        return parsedStack.peek().isKey();
    }

    public boolean isTopText() {
        return parsedStack.peek().isText();
    }

    public boolean isTopPrimitive() {
        return parsedStack.peek().isPrimitive();
    }

    public boolean isTopNull() {
        return parsedStack.peek().isNull();
    }

    public ParserElementWrapper pop() {
        return parsedStack.pop();
    }

    public ParserElementWrapper peek() {
        return parsedStack.peek();
    }

    public ParserElementWrapper peek2() {
        return parsedStack.elementAt(parsedStack.size() - 2);
    }

    public boolean empty() {
        return parsedStack.empty();
    }

    public String toString() {
        return parsedStack.stream()
                .map(e -> e.getParserElement().toString())
                .collect(Collectors.joining(", "));
    }

}
