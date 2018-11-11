package se.grenby.jasper;

import se.grenby.jasper.parser.*;
import se.grenby.jasper.schema.*;
import se.grenby.jasper.json.JsonDataObject;

import java.util.concurrent.atomic.AtomicReference;

public class JasperParser {

    private final JSchemaObject rootSchema;

    public JasperParser(JSchemaObject rootSchema) {
        this.rootSchema = rootSchema;
    }

    public JsonDataObject parse(String json) {
        ParserJsonWrapper jClob = new ParserJsonWrapper(json);

        AtomicReference<JsonDataObject> rootObject = new AtomicReference<>();
        ParserElementStack stack = new ParserElementStack();

        while (jClob.hasNext()) {
            ParserCharacterWrapper jc = jClob.next();
            if (stack.empty()) {
                if (jc.isMapBeginDelimiter()) {
                    stack.pushMap((JSchemaMap) rootSchema);
                } else if (jc.isListBeginDelimiter()) {
                    stack.pushList((JSchemaList) rootSchema);
                } else if (!jc.isWhitespace()) {
                    throw new RuntimeException("char " + jc.charValue() + " top " + stack.peek().getParserElement());
                }
            } else {
                if (stack.isTopPrimitive()) {
                    if (jc.isDelimiter() || jc.isWhitespace()) {
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
                        ((ParserElementTextWrapper) stack.peek()).addChar(jc.charValue());
                    }
                }

                if (stack.isTopMap()) {
                    if (jc.isStringDelimiter()) {
                        stack.pushKey();
                    } else if (jc.isMapEndDelimiter()) {
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
                                throw new RuntimeException("char " + jc.charValue() + " top " + stack.peek().getParserElement());
                            }
                        }
                    } else if (!jc.isItemDelimiter() && !jc.isWhitespace()) {
                        throw new RuntimeException("char " + jc.charValue() + " top " + stack.peek().getParserElement());
                    }
                } else if (stack.isTopList()) {
                    if (jc.isStringDelimiter()) {
                        stack.pushText();
                    } else if (jc.isMapBeginDelimiter()) {
                        JSchemaList schemaList = ((ParserElementListWrapper) stack.peek()).getSchema();
                        stack.pushMap((JSchemaMap) schemaList.getSubType());
                    } else if (jc.isListBeginDelimiter()) {
                        JSchemaList schemaList = ((ParserElementListWrapper) stack.peek()).getSchema();
                        stack.pushList((JSchemaList) schemaList.getSubType());
                    } else if (jc.isListEndDelimiter()) {
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
                    } else if (jc.isMapKeyValueDelimiter() || jc.isMapEndDelimiter()) {
                        throw new RuntimeException("char " + jc.charValue() + " top " + stack.peek().getParserElement());
                    } else if (!jc.isItemDelimiter() && !jc.isWhitespace()) {
                        if (jc.charValue() != 'n') {
                            JSchemaList schemaList = ((ParserElementListWrapper) stack.peek()).getSchema();
                            stack.pushPrimitive((JSchemaValue) schemaList.getSubType(), jc.charValue());
                        } else {
                            stack.pushNull(jc.charValue());
                        }
                    }
                } else if (stack.isTopKeyValue()) {
                    if (jc.isStringDelimiter()) {
                        stack.pushText();
                    } else if (jc.isMapKeyValueDelimiter()) {
                        ParserElementKeyValueWrapper kv = (ParserElementKeyValueWrapper) stack.peek();
                        if (kv.getKey() == null)
                            throw new RuntimeException("Char " + jc.charValue() + " with stack " + stack.toString());
                    } else if (jc.isMapBeginDelimiter()) {
                        JSchemaObject so = ((ParserElementKeyValueWrapper) stack.peek()).getSchema();
                        stack.pushMap((JSchemaMap) so);
                    } else if (jc.isListBeginDelimiter()) {
                        JSchemaObject so = ((ParserElementKeyValueWrapper) stack.peek()).getSchema();
                        stack.pushList((JSchemaList) so);
                    } else if (jc.isDelimiter()) {
                        throw new RuntimeException("Char " + jc.charValue() + " with stack " + stack.toString());
                    } else {
                        if (!jc.isWhitespace()) {
                            if (jc.isDigit() || jc.isFirstCharInBoolean()) {
                                JSchemaObject so = ((ParserElementKeyValueWrapper) stack.peek()).getSchema();
                                stack.pushPrimitive((JSchemaValue) so, jc.charValue());
                            } else if (jc.isFirstCharInNull()) {
                                stack.pushNull(jc.charValue());
                            } else {
                                throw new RuntimeException("Char " + jc.charValue() + " with stack " + stack.toString());
                            }
                        }
                    }
                } else if (stack.isTopKey()) {
                    if (jc.isStringDelimiter()) {
                        ParserElementKeyWrapper key = (ParserElementKeyWrapper) stack.pop();
                        ParserElementMapWrapper map = (ParserElementMapWrapper) stack.peek();
                        JSchemaObject so = map.getSchema().getPropertyType(key.getText());
                        stack.pushKeyValue(key.getText(), so);
                    } else if (jc.isDelimiter()) {
                        throw new RuntimeException("Char " + jc.charValue() + " with stack " + stack.toString());
                    } else {
                        ((ParserElementTextWrapper) stack.peek()).addChar(jc.charValue());
                    }
                } else if (stack.isTopText()) {
                    if (jc.isStringDelimiter()) {
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
                    } else if (jc.isDelimiter()) {
                        throw new RuntimeException("Char " + jc.charValue() + " with stack " + stack.toString());
                    } else {
                        ((ParserElementTextWrapper) stack.peek()).addChar(jc.charValue());
                    }
                } else if (stack.isTopNull()) {
                    ParserElementNullWrapper nullWrapper = ((ParserElementNullWrapper) stack.peek());
                    if (nullWrapper.validate(jc.charValue())) {
                        if (nullWrapper.finished()) {
                            stack.pop();
                            if (stack.isTopKeyValue()) {
                                ParserElementKeyValueWrapper keyValue = (ParserElementKeyValueWrapper) stack.pop();
                                ParserElementMapWrapper map = (ParserElementMapWrapper) stack.peek();
                                if (keyValue.getSchema() != null) {
                                    map.getMap().put(keyValue.getKey(), null);
                                }
                            } else {
                                throw new RuntimeException("Char " + jc.charValue() + " with stack " + stack.toString());
                            }
                        }
                    } else {
                        throw new RuntimeException("Char " + jc.charValue() + " with stack " + stack.toString());
                    }
                }
            }
        }

        return rootObject.get();
    }


}
