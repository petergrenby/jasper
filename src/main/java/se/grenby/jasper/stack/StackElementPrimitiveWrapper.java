package se.grenby.jasper.stack;

import se.grenby.jasper.schema.JSchemaValue;

public class StackElementPrimitiveWrapper extends StackElementTextWrapper {

    private final JSchemaValue schema;

    public StackElementPrimitiveWrapper(JSchemaValue schema) {
        super(StackElement.PRIMITIVE);
        this.schema = schema;
    }

    public JSchemaValue getSchema() {
        return schema;
    }
}
