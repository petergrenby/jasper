package se.grenby.jasper.stack;

import se.grenby.jasper.schema.JSchemaObject;

public class StackElementKeyValueWrapper extends StackElementWrapper {

    private String key;
    private JSchemaObject schema;


    public StackElementKeyValueWrapper() {
        super(StackElement.KEY_VALUE);
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public JSchemaObject getSchema() {
        return schema;
    }

    public void setSchema(JSchemaObject schema) {
        this.schema = schema;
    }
}
