package se.grenby.jasper.stack;

import se.grenby.jasper.schema.JSchemaList;
import se.grenby.jasper.schema.JSchemaMap;
import se.grenby.jasper.schema.JSchemaObject;
import se.grenby.jasper.schema.JSchemaValue;

import java.util.Stack;
import java.util.stream.Collectors;

public class ParserStack {

    private final Stack<StackElementWrapper> parsedStack = new Stack<>();

    public ParserStack() {

    }

    public void pushMap(JSchemaMap schemaMap) {
        parsedStack.push(new StackElementMapWrapper(schemaMap));
    }

    public void pushList(JSchemaList schemaList) {
        parsedStack.push(new StackElementListWrapper(schemaList));
    }

    public void pushKey() {
        parsedStack.push(new StackElementKeyWrapper());
    }

    public void pushKeyValue(String key, JSchemaObject so) {
        StackElementKeyValueWrapper kv = new StackElementKeyValueWrapper();
        kv.setKey(key);
        kv.setSchema(so);

        parsedStack.push(kv);
    }

    public void pushText() {
        parsedStack.push(new StackElementTextWrapper());
    }

    public void pushPrimitive(JSchemaValue schemaValue, char ch) {
        StackElementPrimitiveWrapper primitiveWrapper = new StackElementPrimitiveWrapper(schemaValue);
        primitiveWrapper.addChar(ch);
        parsedStack.push(primitiveWrapper);
    }

    public void pushNull(char ch) {
        StackElementNullWrapper nullWrapper = new StackElementNullWrapper();
        nullWrapper.validate(ch);
        parsedStack.push(nullWrapper);

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

    public StackElementWrapper pop() {
        return parsedStack.pop();
    }

    public StackElementWrapper peek() {
        return parsedStack.peek();
    }

    public boolean empty() {
        return parsedStack.empty();
    }

    public String toString() {
        return parsedStack.stream()
                .map(e -> e.getStackElement().toString())
                .collect(Collectors.joining(", "));
    }

}
