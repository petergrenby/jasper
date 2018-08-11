package se.grenby.jasper.parser;

import se.grenby.jasper.json.JsonDataMap;
import se.grenby.jasper.schema.JSchemaMap;

public class ParserElementMapWrapper extends ParserElementWrapper {

    private final JsonDataMap map = new JsonDataMap();
    private final JSchemaMap schema;

    public ParserElementMapWrapper(JSchemaMap schema) {
        super(ParserElement.MAP);
        this.schema = schema;
    }

    public JsonDataMap getMap() {
        return map;
    }

    public JSchemaMap getSchema() {
        return schema;
    }
}
