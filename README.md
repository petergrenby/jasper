# Jasper - The simplistic JSON parser

JSON as follows:
```
{
  "firstName": "John",
  "lastName": "Smith",
  "isAlive": true,
  "age": 27,
  "address": {
    "streetAddress": "21 2nd Street",
    "city": "New York",
    "state": "NY",
    "postalCode": "10021-3100"
  },
  "phoneNumbers": [
    {
      "type": "home",
      "number": "212 555-1234"
    },
    {
      "type": "office",
      "number": "646 555-4567"
    },
    {
      "type": "mobile",
      "number": "123 456-7890"
    }
  ],
  "children": [],
  "spouse": null
}
```

Parsed like this:
```
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
```