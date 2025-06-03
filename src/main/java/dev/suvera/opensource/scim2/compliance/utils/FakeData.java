package dev.suvera.opensource.scim2.compliance.utils;

import com.github.javafaker.Address;
import com.github.javafaker.Faker;
import com.github.javafaker.Name;
import com.github.javafaker.PhoneNumber;
import dev.suvera.opensource.scim2.compliance.data.*;
import dev.suvera.opensource.scim2.compliance.data.json.SchemaExtensionName;
import dev.suvera.opensource.scim2.compliance.data.json.ScimAttribute;

import java.text.SimpleDateFormat;
import java.util.*;

/**
 * author: suvera
 * date: 9/7/2020 2:57 PM
 */
public class FakeData {

    public static final Faker faker = new Faker();
    public static final SimpleDateFormat isoFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");

    public static Map<String, Object> generateUser(
        Schemas schemas,
        ResourceTypes resourceTypes
    ) {
        Schema schema = schemas.getSchema(ScimConstants.SCHEMA_USER);
        ResourceType resourceType = resourceTypes.getResourceBySchema(ScimConstants.SCHEMA_USER);

        Map<String, Boolean> commonAttributes = new HashMap<>() {{
            put("userName", true);
            put("externalId", true);
            put("displayName", true);
            put("nickName", true);
            put("active", true);
            put("title", true);
            put("phoneNumbers", true);
            put("addresses", true);
            put("emails", true);
            put("name", true);
            put("password", true);
        }};

        Xmap root = Xmap.q();

        Name name = faker.name();
        root.k("userName", name.username().replaceAll("[^a-z-A-Z_0-9]+", ""));

        if (schema.hasAttribute("externalId")) {
            root.k("externalId", name.username());
        }

        if (schema.hasAttribute("displayName")) {
            root.k("displayName", name.name());
        }
        if (schema.hasAttribute("nickName")) {
            root.k("nickName", faker.funnyName().name());
        }

        if (schema.hasAttribute("active")) {
            root.k("active", true);
        }
        if (schema.hasAttribute("title")) {
            root.k("title", name.title());
        }

        if (schema.hasAttribute("phoneNumbers")) {
            PhoneNumber phone = faker.phoneNumber();
            root.k(
                "phoneNumbers",
                Collections.singletonList(
                    Xmap.q()
                        .k("value", phone.cellPhone())
                        .k("primary", true)
                        .get()
                )
            );
        }

        if (schema.hasAttribute("addresses")) {
            ScimAttribute addresses = schema.getAttribute("addresses");
            Object attrData;
            if ("complex".equals(addresses.getType())) {
                List<ScimAttribute> subAttrs = addresses.getSubAttributes();
                if (subAttrs == null) {
                    subAttrs = List.of(new ScimAttribute("formatted", "string"),
                        new ScimAttribute("streetAddress", "string"),
                        new ScimAttribute("locality", "string"),
                        new ScimAttribute("region", "string"),
                        new ScimAttribute("postalCode", "string"),
                        new ScimAttribute("country", "string"),
                        new ScimAttribute("primary", "boolean"));
                }
                Address address = faker.address();
                Xmap q = Xmap.q();
                // Loop through all the attributes of the address
                for (ScimAttribute attr : subAttrs) {
                    switch (attr.getName()) {
                        case "formatted":
                            q.k("formatted", address.fullAddress());
                            break;
                        case "streetAddress":
                            q.k("streetAddress", address.streetAddress());
                            break;
                        case "locality":
                            q.k("locality", address.cityName());
                            break;
                        case "region":
                            q.k("region", address.state());
                            break;
                        case "postalCode":
                            q.k("postalCode", address.zipCode());
                            break;
                        case "country":
                            q.k("country", address.country());
                            break;
                        case "primary":
                            q.k("primary", true);
                            break;
                        default:
                            attrData = getAttributeData(attr);
                            if (attrData != null) {
                                q.k(attr.getName(), attrData);
                            }

                            break;
                    }
                }
                root.k("addresses", Collections.singletonList(q.get()));
            } else {
                attrData = getAttributeData(addresses);
                if (attrData != null) {
                    root.k("addresses", attrData);
                }
            }
        }

        if (schema.hasAttribute("emails")) {
            root.k(
                "emails",
                Collections.singletonList(
                    Xmap.q()
                        .k("value", name.username() + "@opensource.suvera.dev")
                        .k("primary", true)
                        .get()
                )
            );
        }

        if (schema.hasAttribute("name")) {
            Object attrData;
            ScimAttribute nameAttr = schema.getAttribute("name");
            if ("complex".equals(nameAttr.getType())) {
                List<ScimAttribute> subAttrs = nameAttr.getSubAttributes();
                if (subAttrs == null) {
                    subAttrs = List.of(new ScimAttribute("familyName", "string"),
                        new ScimAttribute("givenName", "string"),
                        new ScimAttribute("formatted", "string"),
                        new ScimAttribute("honorificPrefix", "string"));
                }
                Xmap q = Xmap.q();
                for (ScimAttribute attr : subAttrs) {
                    switch (attr.getName()) {
                        case "familyName":
                            q.k("familyName", name.lastName());
                            break;
                        case "givenName":
                            q.k("givenName", name.firstName());
                            break;
                        case "formatted":
                            q.k("formatted", name.fullName());
                            break;
                        case "honorificPrefix":
                            q.k("honorificPrefix", name.prefix());
                            break;
                        default:
                            attrData = getAttributeData(attr);
                            if (attrData != null) {
                                q.k(attr.getName(), attrData);
                            }
                            break;
                    }
                }
                root.k("name", q.get());

            } else {
                root.k("name", name.fullName());
            }
        }

        if (schema.hasAttribute("password")) {
            root.k("password", UUID.randomUUID().toString());
        }

        for (ScimAttribute attr : schema.getAttributes()) {
            if (commonAttributes.containsKey(attr.getName())) {
                continue;
            }

            if (!attr.isRequired() || attr.getName().equals("manager")) {
                continue;
            }
            Object attrData = getAttributeData(attr);
            if (attrData != null) {
                root.k(attr.getName(), attrData);
            }
        }


        List<String> schemaList = new ArrayList<>();
        schemaList.add(resourceType.getSchema());
        if (resourceType.getSchemaExtensions() != null) {
            for (SchemaExtensionName ext : resourceType.getSchemaExtensions()) {
                Map<String, Object> q = getExtensionData(ext.getSchema(), schemas, resourceTypes);
                if (q == null) {
                    continue;
                }
                schemaList.add(ext.getSchema());
                root.k(ext.getSchema(), q);
            }
        }
        root.k("schemas", schemaList);

        return root.get();
    }

    private static Map<String, Object> getExtensionData(
        String schemaName,
        Schemas schemas,
        ResourceTypes resourceTypes
    ) {
        Schema schema = schemas.getSchema(schemaName);
        Xmap root = getAttributesData(schema.getAttributes());
        if (root == null) {
            return null;
        }
        return root.get();
    }

    private static Xmap getAttributesData(Collection<ScimAttribute> attributes) {
        if (attributes == null) {
            return null;
        }
        Xmap root = Xmap.q();


        for (ScimAttribute attr : attributes) {
            if (attr == null || attr.getName() == null || attr.getName().equals("manager") || attr.getName().equals("$ref")) {
                continue;
            }

            Object attrData = getAttributeData(attr);
            if (attrData == null) {
                continue;
            }
            root.k(attr.getName(), attrData);
        }
        if (root.get().isEmpty()) {
            return null;
        }
        return root;
    }

    private static Object getAttributeData(ScimAttribute attr) {
        boolean hasEnum = (attr.getCanonicalValues() != null && !attr.getCanonicalValues().isEmpty());
        Object value;
        Xmap xmlValue;

        switch (attr.getType()) {
            case "reference":
                if (attr.getName().equals("$ref")) {
                    // TODO: Implement reference data
                    value = null;
                } else {
                    value = faker.internet().avatar();
                }
                break;

            case "complex":
                xmlValue = getAttributesData(attr.getSubAttributes());
                if (xmlValue == null) {
                    return null;
                } else {
                    value = xmlValue.get();
                }
                break;

            case "boolean":
                value = true;
                break;

            case "datetime":
            case "dateTime":
                isoFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
                value = isoFormat.format(new Date());
                break;

            case "binary":
                value = Base64.getEncoder().encodeToString(faker.lorem().fixedString(10).getBytes());
                break;

            case "decimal":
                if (hasEnum) {
                    value = Double.valueOf(attr.getCanonicalValues().get(0));
                } else {
                    value = faker.number().randomDouble(2, 0, 100);
                }
                break;

            case "integer":
                if (hasEnum) {
                    value = Long.valueOf(0L + attr.getCanonicalValues().get(0));
                } else {
                    value = faker.number().numberBetween(0, 99999);
                }
                break;

            default:
                if (hasEnum) {
                    value = attr.getCanonicalValues().get(0);
                } else {
                    if (attr.getName().equals("employeeNumber")) {
                        value = "E" + faker.number().numberBetween(100000, 999999);
                    } else {
                        value = faker.lorem().fixedString(10);
                    }
                }
                break;
        }

        if (attr.isMultiValued()) {
            return Collections.singletonList(value);
        } else {
            return value;
        }
    }
}
