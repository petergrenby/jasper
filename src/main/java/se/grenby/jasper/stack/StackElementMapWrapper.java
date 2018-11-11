package se.grenby.jasper.stack;

import se.grenby.jasper.json.JsonDataMap;
import se.grenby.jasper.schema.JSchemaMap;

public class StackElementMapWrapper extends StackElementWrapper {

    private final JsonDataMap map = new JsonDataMap();
    private final JSchemaMap schema;

    public StackElementMapWrapper(JSchemaMap schema) {
        super(StackElement.MAP);
        this.schema = schema;
    }

    public JsonDataMap getMap() {
        return map;
    }

    public JSchemaMap getSchema() {
        return schema;
    }
}
