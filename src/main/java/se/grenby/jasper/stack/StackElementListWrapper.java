package se.grenby.jasper.stack;

import se.grenby.jasper.json.JsonDataList;
import se.grenby.jasper.schema.JSchemaList;

public class StackElementListWrapper extends StackElementWrapper {

    private final JsonDataList list = new JsonDataList();
    private final JSchemaList schema;

    public StackElementListWrapper(JSchemaList schema) {
        super(StackElement.LIST);
        this.schema = schema;
    }

    public JsonDataList getList() {
        return list;
    }

    public JSchemaList getSchema() {
        return schema;
    }
}
