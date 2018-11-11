# Jasper - The simplistic JSON-parser
Jasper is a simplistic JSON-parser made to be small on both code size and footprint. The idea with **Jasper** is NOT to be a another reflection based JSON parser and object-reflection-mapper. Instead it is based on the idea to opinionated, very opinionated, so that it can become simple and easy to use. So there no reflection support what so ever and instead it is based on the idea of schema.

## Schema
A schema defines both the structure of JSON it will parser and also what Java-types it will cast primitive values to. The schema could be created like this:
```
JSchemaMap schema = new JSchemaMap();
schema.text("firstName");
schema.text("lastName");
schema.val("isAlive", JS_BOOLEAN);
schema.val("age", JS_INTEGER);
schema.map("address", new JSchemaMap()
        .text("streetAddress")
        .text("city")
        .text("state")
        .text("postalCode"));
schema.list("phoneNumbers", new JSchemaMap()
        .text("type")
        .text("number"));
schema.text("spouse");
```
The schema would be valid for the following JSON:
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
## Parse JSON
Parsing the JSON would look like this:
```
JasperParser jp = new JasperParser(schema);
JsonDataObject jso = jp.parse(JSON_TEXT);
```