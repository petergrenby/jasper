package se.grenby.jasper.schema;

public class JSchemaValue extends JSchemaObject {

    private enum Type {
        BOOLEAN, BYTE, SHORT, INTEGER, LONG, FLOAT, DOUBLE, BIGDECIMAL
    }

    private final Type type;

    private JSchemaValue(Type type) {
        this.type = type;
    }

    public static final JSchemaValue JS_BOOLEAN = new JSchemaValue(Type.BOOLEAN);
    public static final JSchemaValue JS_BYTE = new JSchemaValue(Type.BYTE);
    public static final JSchemaValue JS_SHORT = new JSchemaValue(Type.SHORT);
    public static final JSchemaValue JS_INTEGER= new JSchemaValue(Type.INTEGER);
    public static final JSchemaValue JS_LONG = new JSchemaValue(Type.LONG);
    public static final JSchemaValue JS_FLOAT = new JSchemaValue(Type.FLOAT);
    public static final JSchemaValue JS_DOUBLE = new JSchemaValue(Type.DOUBLE);
    public static final JSchemaValue JS_BIGDECIMAL = new JSchemaValue(Type.BIGDECIMAL);

    public Object parseValue(String text) {
        switch (type) {
            case BOOLEAN:
                return Boolean.parseBoolean(text);
            case BYTE:
                return Byte.parseByte(text);
            case SHORT:
                return Short.parseShort(text);
            case INTEGER:
                return Integer.parseInt(text);
            case LONG:
                return Long.parseLong(text);
            case FLOAT:
                return Float.parseFloat(text);
            case DOUBLE:
                return Double.parseDouble(text);
            case BIGDECIMAL:
                throw new RuntimeException("Not supported yet.");
                default:
                return null;
        }
    }
}
