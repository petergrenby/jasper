package se.grenby.jasper.parser;

import se.grenby.jasper.schema.JSchemaValue;

public class ParserElementPrimitiveWrapper extends ParserElementTextWrapper {

    private final JSchemaValue schema;

    public ParserElementPrimitiveWrapper(JSchemaValue schema) {
        super(ParserElement.PRIMITIVE);
        this.schema = schema;
    }

    public JSchemaValue getSchema() {
        return schema;
    }
}
