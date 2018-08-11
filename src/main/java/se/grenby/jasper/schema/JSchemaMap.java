package se.grenby.jasper.schema;

import java.util.HashMap;
import java.util.Map;

public class JSchemaMap extends JSchemaObject {

    private final Map<String, JSchemaObject> jsMap = new HashMap<>();

    public JSchemaMap() {
    }

    public JSchemaMap val(String key, JSchemaValue val) {
        jsMap.put(key, val);
        return this;
    }

    public JSchemaMap text(String key) {
        jsMap.put(key, JSchemaText.JS_TEXT);
        return this;
    }

    public JSchemaMap map(String key, JSchemaMap map) {
        jsMap.put(key, map);
        return this;
    }

    public JSchemaMap list(String key, JSchemaObject obj) {
        jsMap.put(key, new JSchemaList(obj));
        return this;
    }

    public JSchemaObject getSubType(String key) {
        return jsMap.get(key);
    }
}
