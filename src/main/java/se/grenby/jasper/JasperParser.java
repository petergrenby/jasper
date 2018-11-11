package se.grenby.jasper;

import se.grenby.jasper.parser.*;
import se.grenby.jasper.schema.*;
import se.grenby.jasper.json.JsonDataObject;

import java.util.*;
import java.util.concurrent.atomic.AtomicReference;

public class JasperParser {

    private static final char STRING_DELIMITER = '\"';
    private static final char MAP_BEGIN_DELIMITER = '{';
    private static final char MAP_KEY_VALUE_DELIMITER = ':';
    private static final char MAP_END_DELIMITER = '}';
    private static final char LIST_BEGIN_DELIMITER = '[';
    private static final char LIST_END_DELIMITER = ']';
    private static final char ITEM_DELIMITER = ',';

    private final Set<Character> delimiters = new HashSet<>();

    private final JSchemaObject rootSchema;

    public JasperParser(JSchemaObject rootSchema) {
        this.rootSchema = rootSchema;

        delimiters.add(STRING_DELIMITER);
        delimiters.add(MAP_BEGIN_DELIMITER);
        delimiters.add(MAP_KEY_VALUE_DELIMITER);
        delimiters.add(MAP_END_DELIMITER);
        delimiters.add(LIST_BEGIN_DELIMITER);
        delimiters.add(LIST_END_DELIMITER);
        delimiters.add(ITEM_DELIMITER);
    }

    public JsonDataObject parse(String json) {
        AtomicReference<JsonDataObject> rootObject = new AtomicReference<>();
        ParserElementStack stack = new ParserElementStack();

        json.chars().forEach(c -> {
            char ch = (char) c;
            if (stack.empty()) {
                if (ch == MAP_BEGIN_DELIMITER) {
                    stack.pushMap((JSchemaMap) rootSchema);
                } else if (ch == LIST_BEGIN_DELIMITER) {
                    stack.pushList((JSchemaList) rootSchema);
                } else if (!Character.isWhitespace(c)) {
                    throw new RuntimeException("char " + ch + " top " + stack.peek().getParserElement());
                }
            } else {
                if (stack.isTopPrimitive()) {
                    if (delimiters.contains(ch) || Character.isWhitespace(ch)) {
                        ParserElementPrimitiveWrapper primitive = (ParserElementPrimitiveWrapper) stack.pop();
                        if (stack.isTopKeyValue()) {
                            ParserElementKeyValueWrapper keyValue = (ParserElementKeyValueWrapper) stack.pop();
                            ParserElementMapWrapper map = (ParserElementMapWrapper) stack.peek();
                            JSchemaValue sv = (JSchemaValue) keyValue.getSchema();
                            map.getMap().put(keyValue.getKey(), sv.parseValue(primitive.getText()));
                        } else if (stack.isTopList()) {
                            ParserElementListWrapper list = (ParserElementListWrapper) stack.peek();
                            JSchemaValue sv = (JSchemaValue) list.getSchema().getSubType();
                            list.getList().add(sv.parseValue(primitive.getText()));
                        }
                    } else {
                        ((ParserElementTextWrapper) stack.peek()).addChar(ch);
                    }
                }

                if (stack.isTopMap()) {
                    switch (ch) {
                        case STRING_DELIMITER: {
                            stack.pushKey();
                            break;
                        }
                        case MAP_END_DELIMITER: {
                            ParserElementMapWrapper map = (ParserElementMapWrapper) stack.pop();
                            if (stack.empty()) {
                                rootObject.set(map.getMap());
                            } else {
                                if (stack.isTopKeyValue()) {
                                    ParserElementKeyValueWrapper keyValue = (ParserElementKeyValueWrapper) stack.pop();
                                    ParserElementMapWrapper upperMap = (ParserElementMapWrapper) stack.peek();
                                    upperMap.getMap().put(keyValue.getKey(), map.getMap());
                                } else if (stack.isTopList()) {
                                    ParserElementListWrapper list = (ParserElementListWrapper) stack.peek();
                                    list.getList().add(map.getMap());
                                } else {
                                    throw new RuntimeException("char " + ch + " top " + stack.peek().getParserElement());
                                }
                            }

                            break;
                        }
                        case ITEM_DELIMITER: {
                            break;
                        }
                        case MAP_BEGIN_DELIMITER:
                        case LIST_BEGIN_DELIMITER:
                        case MAP_KEY_VALUE_DELIMITER:
                        case LIST_END_DELIMITER: {
                            throw new RuntimeException("char " + ch + " top " + stack.peek().getParserElement());
                        }
                        default:
                            if (!Character.isWhitespace(ch))
                                throw new RuntimeException("char " + ch + " top " + stack.peek().getParserElement());
                    }
                } else if (stack.isTopList()) {
                    switch (ch) {
                        case STRING_DELIMITER: {
                            stack.pushText();
                            break;
                        }
                        case MAP_BEGIN_DELIMITER: {
                            JSchemaList schemaList = ((ParserElementListWrapper) stack.peek()).getSchema();
                            stack.pushMap((JSchemaMap) schemaList.getSubType());
                            break;
                        }
                        case LIST_BEGIN_DELIMITER: {
                            JSchemaList schemaList = ((ParserElementListWrapper) stack.peek()).getSchema();
                            stack.pushList((JSchemaList) schemaList.getSubType());
                            break;
                        }
                        case LIST_END_DELIMITER: {
                            ParserElementListWrapper list = (ParserElementListWrapper) stack.pop();
                            if (stack.empty()) {
                                rootObject.set(list.getList());
                            } else {
                                if (stack.isTopKeyValue()) {
                                    ParserElementKeyValueWrapper keyValue = (ParserElementKeyValueWrapper) stack.pop();
                                    ParserElementMapWrapper map = (ParserElementMapWrapper) stack.peek();
                                    map.getMap().put(keyValue.getKey(), list.getList());
                                } else if (stack.isTopList()) {
                                    ParserElementListWrapper upperList = (ParserElementListWrapper) stack.peek();
                                    upperList.getList().add(list.getList());
                                } else {
                                    throw new RuntimeException();
                                }
                            }

                            break;
                        }
                        case ITEM_DELIMITER: {
                            break;
                        }
                        case MAP_KEY_VALUE_DELIMITER:
                        case MAP_END_DELIMITER:
                            throw new RuntimeException("char " + ch + " top " + stack.peek().getParserElement());
                        default:
                            if (!Character.isWhitespace(ch)) {
                                if (ch != 'n') {
                                    JSchemaList schemaList = ((ParserElementListWrapper) stack.peek()).getSchema();
                                    stack.pushPrimitive((JSchemaValue) schemaList.getSubType());
                                    ((ParserElementTextWrapper) stack.peek()).addChar(ch);
                                } else {
                                    stack.pushNull();
                                    ((ParserElementNullWrapper) stack.peek()).valid(ch);
                                }
                            }
                    }
                } else if (stack.isTopKeyValue()) {
                    if (ch == STRING_DELIMITER) {
                        stack.pushText();
                    } else if (ch == MAP_KEY_VALUE_DELIMITER) {
                        ParserElementKeyValueWrapper kv = (ParserElementKeyValueWrapper) stack.peek();
                        if (kv.getKey() == null)
                            throw new RuntimeException("Char " + ch + " with stack " + stack.toString());
                    } else if (ch == MAP_BEGIN_DELIMITER) {
                        JSchemaObject so = ((ParserElementKeyValueWrapper) stack.peek()).getSchema();
                        stack.pushMap((JSchemaMap) so);
                    } else if (ch == LIST_BEGIN_DELIMITER) {
                        JSchemaObject so = ((ParserElementKeyValueWrapper) stack.peek()).getSchema();
                        stack.pushList((JSchemaList) so);
                    } else if (delimiters.contains(ch)) {
                        throw new RuntimeException("Char " + ch + " with stack " + stack.toString());
                    } else {
                        if (!Character.isWhitespace(ch)) {
                            if (Character.isDigit(ch) || ch == 't' || ch == 'f') {
                                JSchemaObject so = ((ParserElementKeyValueWrapper) stack.peek()).getSchema();
                                stack.pushPrimitive((JSchemaValue) so);
                                ((ParserElementTextWrapper) stack.peek()).addChar(ch);
                            } else if (ch == 'n') {
                                stack.pushNull();
                                ((ParserElementNullWrapper) stack.peek()).valid(ch);
                            } else {
                                throw new RuntimeException("Char " + ch + " with stack " + stack.toString());
                            }
                        }
                    }
                } else if (stack.isTopKey()) {
                    if (ch == STRING_DELIMITER) {
                        ParserElementKeyWrapper key = (ParserElementKeyWrapper) stack.pop();
                        ParserElementMapWrapper map = (ParserElementMapWrapper) stack.peek();
                        JSchemaObject so = map.getSchema().getPropertyType(key.getText());
                        stack.pushKeyValue(key.getText(), so);
                    } else if (delimiters.contains(ch)) {
                        throw new RuntimeException("Char " + ch + " with stack " + stack.toString());
                    } else {
                        ((ParserElementTextWrapper) stack.peek()).addChar(ch);
                    }
                } else if (stack.isTopText()) {
                    if (ch == STRING_DELIMITER) {
                        ParserElementTextWrapper text = (ParserElementTextWrapper) stack.pop();
                        if (stack.isTopKeyValue()) {
                            ParserElementKeyValueWrapper keyValue = (ParserElementKeyValueWrapper) stack.pop();
                            ParserElementMapWrapper map = (ParserElementMapWrapper) stack.peek();
                            if (keyValue.getSchema() instanceof JSchemaText) {
                                map.getMap().put(keyValue.getKey(), text.getText());
                            } else {
                                throw new RuntimeException();
                            }
                        } else if (stack.isTopList()) {
                            ParserElementListWrapper list = (ParserElementListWrapper) stack.peek();
                            if (list.getSchema().getSubType() instanceof JSchemaText) {
                                list.getList().add(text.getText());
                            } else {
                                throw new RuntimeException();
                            }
                        }
                    } else if (delimiters.contains(ch)) {
                        throw new RuntimeException("Char " + ch + " with stack " + stack.toString());
                    } else {
                        ((ParserElementTextWrapper) stack.peek()).addChar(ch);
                    }

                } else if (stack.isTopNull()) {
                    ParserElementNullWrapper nullWrapper = ((ParserElementNullWrapper) stack.peek());
                    if (nullWrapper.valid(ch)) {
                        if (nullWrapper.finished()) {
                            stack.pop();
                            if (stack.isTopKeyValue()) {
                                ParserElementKeyValueWrapper keyValue = (ParserElementKeyValueWrapper) stack.pop();
                                ParserElementMapWrapper map = (ParserElementMapWrapper) stack.peek();
                                if (keyValue.getSchema() != null) {
                                    map.getMap().put(keyValue.getKey(), null);
                                }
                            } else {
                                throw new RuntimeException("Char " + ch + " with stack " + stack.toString());
                            }
                        }
                    } else {
                        throw new RuntimeException("Char " + ch + " with stack " + stack.toString());
                    }
                }
            }

        });
        return rootObject.get();
    }


}
