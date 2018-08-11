package se.grenby.jasper;

import org.junit.Test;
import se.grenby.jasper.json.JsonDataObject;
import se.grenby.jasper.schema.JSchemaMap;

import static se.grenby.jasper.schema.JSchemaValue.*;

public class JasperParserTest {
    private final String JSON_TEXT = "{\n" +
            "  \"firstName\": \"John\",\n" +
            "  \"lastName\": \"Smith\",\n" +
            "  \"isAlive\": true,\n" +
            "  \"age\": 27,\n" +
            "  \"address\": {\n" +
            "    \"streetAddress\": \"21 2nd Street\",\n" +
            "    \"city\": \"New York\",\n" +
            "    \"state\": \"NY\",\n" +
            "    \"postalCode\": \"10021-3100\"\n" +
            "  },\n" +
            "  \"phoneNumbers\": [\n" +
            "    {\n" +
            "      \"type\": \"home\",\n" +
            "      \"number\": \"212 555-1234\"\n" +
            "    },\n" +
            "    {\n" +
            "      \"type\": \"office\",\n" +
            "      \"number\": \"646 555-4567\"\n" +
            "    },\n" +
            "    {\n" +
            "      \"type\": \"mobile\",\n" +
            "      \"number\": \"123 456-7890\"\n" +
            "    }\n" +
            "  ],\n" +
            "  \"children\": [],\n" +
            "  \"spouse\": null\n" +
            "}\n";

    @Test
    public void testSchema() {
        JSchemaMap m = new JSchemaMap();
        m.text("firstName");
        m.text("lastName");
        m.val("isAlive", JS_BOOLEAN);
        m.val("age", JS_INTEGER);
        m.map("address", new JSchemaMap()
                .text("streetAddress")
                .text("city")
                .text("state")
                .text("postalCode"));
        m.list("phoneNumbers", new JSchemaMap()
                .text("type")
                .text("number"));
        m.text("spouse");

        JasperParser jp = new JasperParser(m);
        JsonDataObject jso = jp.parse(JSON_TEXT);
        System.out.println(jso.toString());
    }
}
