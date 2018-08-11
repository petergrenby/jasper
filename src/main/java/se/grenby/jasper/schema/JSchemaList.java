package se.grenby.jasper.schema;

public class JSchemaList extends JSchemaObject {

    private final JSchemaObject schemaObject;

    public JSchemaList(JSchemaObject schemaObject) {
        this.schemaObject = schemaObject;
    }

    public JSchemaObject getSubType() {
        return schemaObject;
    }
}
