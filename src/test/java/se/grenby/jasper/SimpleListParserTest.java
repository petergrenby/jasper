package se.grenby.jasper;

import org.junit.Test;
import se.grenby.jasper.json.JsonDataList;
import se.grenby.jasper.json.JsonDataMap;
import se.grenby.jasper.json.JsonDataObject;
import se.grenby.jasper.schema.JSchemaList;
import se.grenby.jasper.schema.JSchemaMap;
import se.grenby.jasper.schema.JSchemaText;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class SimpleListParserTest {
    private static final String SIMPLE_JSON_LIST_TEXT = "[\n" +
            " \"text\"\n" +
            "]\n";

    @Test
    public void testSchema() {
        JSchemaList l = new JSchemaList(JSchemaText.JS_TEXT);

        JasperParser jp = new JasperParser(l);
        JsonDataObject jso = jp.parse(SIMPLE_JSON_LIST_TEXT);

        assertTrue(jso instanceof JsonDataList);
        JsonDataList jsl = (JsonDataList) jso;
        assertEquals("text", jsl.iterator().next());
    }
}
