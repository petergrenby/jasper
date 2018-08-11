package se.grenby.jasper.parser;

import se.grenby.jasper.schema.JSchemaObject;

public class ParserElementKeyValueWrapper extends ParserElementWrapper {

    private String key;
    private JSchemaObject schema;


    public ParserElementKeyValueWrapper() {
        super(ParserElement.KEY_VALUE);
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
