package se.grenby.jasper.parser;

import se.grenby.jasper.json.JsonDataList;
import se.grenby.jasper.schema.JSchemaList;

public class ParserElementListWrapper extends ParserElementWrapper {

    private final JsonDataList list = new JsonDataList();
    private final JSchemaList schema;

    public ParserElementListWrapper(JSchemaList schema) {
        super(ParserElement.LIST);
        this.schema = schema;
    }

    public JsonDataList getList() {
        return list;
    }

    public JSchemaList getSchema() {
        return schema;
    }
}
