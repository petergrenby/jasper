package se.grenby.jasper;

import org.junit.Test;
import se.grenby.jasper.json.JsonDataMap;
import se.grenby.jasper.json.JsonDataObject;
import se.grenby.jasper.schema.JSchemaMap;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class SimpleMapParserTest {
    private static final String SIMPLE_JSON_MAP_TEXT = "{\n" +
            "  \"field\": \"text\"\n" +
            "}\n";

    @Test
    public void testSchema() {
        JSchemaMap m = new JSchemaMap();
        m.text("field");

        JasperParser jp = new JasperParser(m);
        JsonDataObject jso = jp.parse(SIMPLE_JSON_MAP_TEXT);

        assertTrue(jso instanceof JsonDataMap);
        JsonDataMap jsm = (JsonDataMap) jso;
        assertEquals("text", jsm.getString("field"));
    }
}
