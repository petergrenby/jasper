package se.grenby.jasper;

import se.grenby.jasper.parser.ParserCharacterWrapper;
import se.grenby.jasper.parser.ParserJsonWrapper;
import se.grenby.jasper.stack.*;
import se.grenby.jasper.schema.*;
import se.grenby.jasper.json.JsonDataObject;

public class JasperParser {

    private final JSchemaObject rootSchema;

    public JasperParser(JSchemaObject rootSchema) {
        this.rootSchema = rootSchema;
    }

    public JsonDataObject parse(String json) {
        ParserJsonWrapper jClob = new ParserJsonWrapper(json);

        JsonDataObject rootObject = null;
        ParserStack stack = new ParserStack();

        while (jClob.hasNext()) {
            ParserCharacterWrapper jc = jClob.next();
            if (stack.empty()) {
                if (jc.isMapBeginDelimiter()) {
                    stack.pushMap((JSchemaMap) rootSchema);
                } else if (jc.isListBeginDelimiter()) {
                    stack.pushList((JSchemaList) rootSchema);
                } else if (!jc.isWhitespace()) {
                    throw new RuntimeException("char " + jc.charValue() + " top " + stack.peek().getStackElement());
                }
            } else {
                if (stack.isTopPrimitive()) {
                    if (jc.isDelimiter() || jc.isWhitespace()) {
                        StackElementPrimitiveWrapper primitive = (StackElementPrimitiveWrapper) stack.pop();
                        if (stack.isTopKeyValue()) {
                            StackElementKeyValueWrapper keyValue = (StackElementKeyValueWrapper) stack.pop();
                            StackElementMapWrapper map = (StackElementMapWrapper) stack.peek();
                            JSchemaValue sv = (JSchemaValue) keyValue.getSchema();
                            map.getMap().put(keyValue.getKey(), sv.parseValue(primitive.getText()));
                        } else if (stack.isTopList()) {
                            StackElementListWrapper list = (StackElementListWrapper) stack.peek();
                            JSchemaValue sv = (JSchemaValue) list.getSchema().getSubType();
                            list.getList().add(sv.parseValue(primitive.getText()));
                        }
                    } else {
                        ((StackElementTextWrapper) stack.peek()).addChar(jc.charValue());
                    }
                }

                if (stack.isTopMap()) {
                    if (jc.isStringDelimiter()) {
                        stack.pushKey();
                    } else if (jc.isMapEndDelimiter()) {
                        StackElementMapWrapper map = (StackElementMapWrapper) stack.pop();
                        if (stack.empty()) {
                            rootObject = map.getMap();
                        } else {
                            if (stack.isTopKeyValue()) {
                                StackElementKeyValueWrapper keyValue = (StackElementKeyValueWrapper) stack.pop();
                                StackElementMapWrapper upperMap = (StackElementMapWrapper) stack.peek();
                                upperMap.getMap().put(keyValue.getKey(), map.getMap());
                            } else if (stack.isTopList()) {
                                StackElementListWrapper list = (StackElementListWrapper) stack.peek();
                                list.getList().add(map.getMap());
                            } else {
                                throw new RuntimeException("char " + jc.charValue() + " top " + stack.peek().getStackElement());
                            }
                        }
                    } else if (!jc.isItemDelimiter() && !jc.isWhitespace()) {
                        throw new RuntimeException("char " + jc.charValue() + " top " + stack.peek().getStackElement());
                    }
                } else if (stack.isTopList()) {
                    if (jc.isStringDelimiter()) {
                        stack.pushText();
                    } else if (jc.isMapBeginDelimiter()) {
                        JSchemaList schemaList = ((StackElementListWrapper) stack.peek()).getSchema();
                        stack.pushMap((JSchemaMap) schemaList.getSubType());
                    } else if (jc.isListBeginDelimiter()) {
                        JSchemaList schemaList = ((StackElementListWrapper) stack.peek()).getSchema();
                        stack.pushList((JSchemaList) schemaList.getSubType());
                    } else if (jc.isListEndDelimiter()) {
                        StackElementListWrapper list = (StackElementListWrapper) stack.pop();
                        if (stack.empty()) {
                            rootObject = list.getList();
                        } else {
                            if (stack.isTopKeyValue()) {
                                StackElementKeyValueWrapper keyValue = (StackElementKeyValueWrapper) stack.pop();
                                StackElementMapWrapper map = (StackElementMapWrapper) stack.peek();
                                map.getMap().put(keyValue.getKey(), list.getList());
                            } else if (stack.isTopList()) {
                                StackElementListWrapper upperList = (StackElementListWrapper) stack.peek();
                                upperList.getList().add(list.getList());
                            } else {
                                throw new RuntimeException();
                            }
                        }
                    } else if (jc.isMapKeyValueDelimiter() || jc.isMapEndDelimiter()) {
                        throw new RuntimeException("char " + jc.charValue() + " top " + stack.peek().getStackElement());
                    } else if (!jc.isItemDelimiter() && !jc.isWhitespace()) {
                        if (jc.charValue() != 'n') {
                            JSchemaList schemaList = ((StackElementListWrapper) stack.peek()).getSchema();
                            stack.pushPrimitive((JSchemaValue) schemaList.getSubType(), jc.charValue());
                        } else {
                            stack.pushNull(jc.charValue());
                        }
                    }
                } else if (stack.isTopKeyValue()) {
                    if (jc.isStringDelimiter()) {
                        stack.pushText();
                    } else if (jc.isMapKeyValueDelimiter()) {
                        StackElementKeyValueWrapper kv = (StackElementKeyValueWrapper) stack.peek();
                        if (kv.getKey() == null)
                            throw new RuntimeException("Char " + jc.charValue() + " with stack " + stack.toString());
                    } else if (jc.isMapBeginDelimiter()) {
                        JSchemaObject so = ((StackElementKeyValueWrapper) stack.peek()).getSchema();
                        stack.pushMap((JSchemaMap) so);
                    } else if (jc.isListBeginDelimiter()) {
                        JSchemaObject so = ((StackElementKeyValueWrapper) stack.peek()).getSchema();
                        stack.pushList((JSchemaList) so);
                    } else if (jc.isDelimiter()) {
                        throw new RuntimeException("Char " + jc.charValue() + " with stack " + stack.toString());
                    } else {
                        if (!jc.isWhitespace()) {
                            if (jc.isDigit() || jc.isFirstCharInBoolean()) {
                                JSchemaObject so = ((StackElementKeyValueWrapper) stack.peek()).getSchema();
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
                        StackElementKeyWrapper key = (StackElementKeyWrapper) stack.pop();
                        StackElementMapWrapper map = (StackElementMapWrapper) stack.peek();
                        JSchemaObject so = map.getSchema().getPropertyType(key.getText());
                        stack.pushKeyValue(key.getText(), so);
                    } else if (jc.isDelimiter()) {
                        throw new RuntimeException("Char " + jc.charValue() + " with stack " + stack.toString());
                    } else {
                        ((StackElementTextWrapper) stack.peek()).addChar(jc.charValue());
                    }
                } else if (stack.isTopText()) {
                    if (jc.isStringDelimiter()) {
                        StackElementTextWrapper text = (StackElementTextWrapper) stack.pop();
                        if (stack.isTopKeyValue()) {
                            StackElementKeyValueWrapper keyValue = (StackElementKeyValueWrapper) stack.pop();
                            StackElementMapWrapper map = (StackElementMapWrapper) stack.peek();
                            if (keyValue.getSchema() instanceof JSchemaText) {
                                map.getMap().put(keyValue.getKey(), text.getText());
                            } else {
                                throw new RuntimeException();
                            }
                        } else if (stack.isTopList()) {
                            StackElementListWrapper list = (StackElementListWrapper) stack.peek();
                            if (list.getSchema().getSubType() instanceof JSchemaText) {
                                list.getList().add(text.getText());
                            } else {
                                throw new RuntimeException();
                            }
                        }
                    } else if (jc.isDelimiter()) {
                        throw new RuntimeException("Char " + jc.charValue() + " with stack " + stack.toString());
                    } else {
                        ((StackElementTextWrapper) stack.peek()).addChar(jc.charValue());
                    }
                } else if (stack.isTopNull()) {
                    StackElementNullWrapper nullWrapper = ((StackElementNullWrapper) stack.peek());
                    if (nullWrapper.validate(jc.charValue())) {
                        if (nullWrapper.finished()) {
                            stack.pop();
                            if (stack.isTopKeyValue()) {
                                StackElementKeyValueWrapper keyValue = (StackElementKeyValueWrapper) stack.pop();
                                StackElementMapWrapper map = (StackElementMapWrapper) stack.peek();
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

        return rootObject;
    }


}
