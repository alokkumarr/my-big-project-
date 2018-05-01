package synchronoss.data.generic.model;

/**
 * Created by Alexey Sorokin on 10/5/2016.
 * This class will create and execute set of transformations for generic records
 */

import com.google.gson.*;
import org.apache.log4j.Logger;
import org.apache.spark.sql.Column;
import org.apache.spark.sql.types.DataType;
import org.apache.spark.sql.types.DataTypes;
import org.apache.spark.sql.types.StructField;
import org.apache.spark.sql.types.StructType;
import synchronoss.data.generic.model.transformation.CopyField;
import synchronoss.data.generic.model.transformation.DecodeValue;
import synchronoss.data.generic.model.transformation.Transform;

import java.util.*;
import java.util.concurrent.atomic.AtomicReference;

public class GenericJsonModel {
    private static final Logger logger = Logger.getLogger(GenericJsonModel.class);

    public static final String CONF_OBJECTS = "objects";
    public static final String CONF_TRANSFORMATIONS = "transformations";
    public static final String CONF_FIELDS = "fields";
    public static final String CONF_NAME = "name";
    public static final String CONF_SOURCE = "source";
    public static final String CONF_TYPE = "type";
    public static final String CONF_DEFAULT = "default";
    public static final String CONF_TRANSFORM = "transform";

    public static final String CONF_MAP_MAPPINGS = "mappings";
    public static final String CONF_MAP_FROM = "from";
    public static final String CONF_MAP_TO = "to";
    public static final String CONF_MAP_OTHERWISE = "otherwise";


    public enum FieldType {
        STRING,
        LONG,
        DOUBLE,
        DATE,
        BOOLEAN
    }


    public static Map<String, List<Transform>> createTransformationsList(String definitionsJson) throws RuntimeException {

        JsonObject jo = new JsonParser().parse(definitionsJson).getAsJsonObject();
        JsonArray objects = jo.get(CONF_OBJECTS).getAsJsonArray();
        AtomicReference<JsonObject> rules = new AtomicReference<>(null);
        if (jo.has(CONF_TRANSFORMATIONS)) {
            rules.set(jo.get(CONF_TRANSFORMATIONS).getAsJsonObject());
        }

        Map<String, List<Transform>> ret = new HashMap<>(objects.size());
        objects.forEach((v) -> {
                    JsonObject object = v.getAsJsonObject();
                    JsonArray fields = object.getAsJsonArray(CONF_FIELDS);
                    List<Transform> transformatoins = new ArrayList<>(fields.size());
                    fields.forEach((f) ->
                            transformatoins.add(create(f.getAsJsonObject(), rules.get()))
                    );
                    ret.put(object.get(CONF_NAME).getAsString(), transformatoins);
                }
        );
        return ret;
    }

    private static Transform create(JsonObject transformDef, JsonObject rules) throws RuntimeException {
        Transform ret = null;
        if (transformDef.has(CONF_NAME)) {
            String fieldName = transformDef.get(CONF_NAME).getAsString();
            FieldType fieldType = FieldType.STRING;
            if (transformDef.has(CONF_TYPE)) {
                fieldType = GenericJsonModel.FieldType.valueOf(transformDef.get(CONF_TYPE).getAsString());
            }
            String defaultVal = null;
            if (transformDef.has(CONF_DEFAULT)) {
                defaultVal = transformDef.get(CONF_DEFAULT).getAsString();
            }
            String sourceField = null;
            if (transformDef.has(CONF_SOURCE)) {
                sourceField = transformDef.get(CONF_SOURCE).getAsString();
            }

            if (transformDef.has(CONF_TRANSFORM)) {
                // Get transformation name
                String transformerName = transformDef.get(CONF_TRANSFORM).getAsString();

                // Check if transformation is declared
                if (rules != null && rules.has(transformerName)) {
                    JsonObject transformer = rules.get(transformerName).getAsJsonObject();
                    if (transformer.get(CONF_TYPE).getAsString().equals("map")) {
                        ret = new DecodeValue(sourceField, fieldName, fieldType, defaultVal, transformer);
                    }
                } else {
                    throw new RuntimeException("Transformation not found: " + transformerName);
                }
            } else {
                ret = new CopyField(sourceField, fieldName, fieldType, defaultVal);
            }
        } else
            throw new RuntimeException("Invalid transformation definition: " + transformDef.toString());
        return ret;
    }

    public static JsonElement createValue(String value, GenericJsonModel.FieldType Type) {
        JsonElement ret = null;
        switch (Type) {
            case STRING:
                ret = new JsonPrimitive(value);
                break;
            case LONG:
                ret = new JsonPrimitive(Long.parseLong(value));
                break;
            case DOUBLE:
                ret = new JsonPrimitive(Double.parseDouble(value));
                break;
            case BOOLEAN:
                ret = new JsonPrimitive(Boolean.parseBoolean(value));
                break;
            case DATE:
                // TBD - use string as of now
                ret = new JsonPrimitive(value);
                break;
        }
        return ret;
    }

    public static void transform(JsonObject src, JsonObject dst, String objectType, Map<String, List<Transform>> transformationsList) {
        List<Transform> transformations = transformationsList.get(objectType);
        transformations.forEach((t) ->
                t.transform(src, dst)
        );
    }

    public static String
    transform(String strSrc, String routingFieldName, Set<String> types, Map<String, List<Transform>> transformationsList)
        throws Exception {
        JsonObject src = new JsonParser().parse(strSrc).getAsJsonObject();
        return transform(src, routingFieldName, types, transformationsList);
    }

    public static String
    transform(JsonObject src, String routingFieldName, Set<String> types, Map<String, List<Transform>> transformationsList)
            throws Exception {

        JsonObject dst = new JsonObject();

        if (src.has(routingFieldName)) {
            String objectType = src.get(routingFieldName).getAsString();

            if(types.contains(objectType)){
                List<Transform> transformations = transformationsList.get(objectType);
                if (transformations != null) {
                    transformations.forEach((t) ->
                            t.transform(src, dst)
                    );
                    return dst.toString();
                } else {
                    return src.toString();
                }
            } else {
                throw new Exception("Unsupported object type " + objectType);
            }
        } else
            throw new Exception("Object type field (" + routingFieldName + ") has not been found in source record \n" + src.toString());
    }

    public static StructType createGlobalSchema(String definitionsJson) throws RuntimeException {
        Map<String, StructField> fieldmap = new HashMap<>();
        JsonObject jo = new JsonParser().parse(definitionsJson).getAsJsonObject();
        JsonArray objects = jo.get(CONF_OBJECTS).getAsJsonArray();

        objects.forEach((v) -> {
            JsonObject object = v.getAsJsonObject();
            JsonArray fields = object.getAsJsonArray(CONF_FIELDS);
            fields.forEach((f) -> {
                        String name = f.getAsJsonObject().get(CONF_NAME).getAsString();
                        FieldType type =
                                f.getAsJsonObject().has(CONF_TYPE) ?
                                        FieldType.valueOf(f.getAsJsonObject().get(CONF_TYPE).getAsString()) : FieldType.STRING;
                        fieldmap.put(name, DataTypes.createStructField(name, convertType(type), true));
                    });
                }
        );

        return DataTypes.createStructType(fieldmap.values().toArray(new StructField[0]));
    }

    public static Set<String> getObjectTypeList(String definitionsJson){
        Set<String> objectTypes = new HashSet<>();
        JsonObject jo = new JsonParser().parse(definitionsJson).getAsJsonObject();
        JsonArray objects = jo.get(CONF_OBJECTS).getAsJsonArray();

        objects.forEach((v) -> {
            JsonObject object = v.getAsJsonObject();
            objectTypes.add(object.getAsJsonObject().get(CONF_NAME).getAsString());
        });
        return objectTypes;
    }

    // Build list of fields taking into account possible exclusions
    public static List<Column> createColumnList(String exclusionName, String definitionsJson) {
        Set<String> fields = new HashSet<>();
        JsonObject jo = new JsonParser().parse(definitionsJson).getAsJsonObject();
        JsonArray objects = jo.get(CONF_OBJECTS).getAsJsonArray();
        Set<String> fieldsToExclude = new HashSet<>();

        // If list of fields which should be excluded provided and exists in configuration
        if (exclusionName != null && !exclusionName.isEmpty()
                && jo.has(CONF_TRANSFORMATIONS) && jo.get(CONF_TRANSFORMATIONS).getAsJsonObject().has(exclusionName)) {
            // Create list of field names to exclude
            JsonArray jsonFieldsToExclude = jo.get(CONF_TRANSFORMATIONS)
                    .getAsJsonObject().get(exclusionName).getAsJsonObject().get(CONF_FIELDS).getAsJsonArray();
            jsonFieldsToExclude.forEach((f) -> fieldsToExclude.add(f.getAsString()));
        }

        // Create unique list of the fields based on all object definitions
        objects.forEach((v) -> {
            JsonObject object = v.getAsJsonObject();
            JsonArray fieldsSection = object.getAsJsonArray(CONF_FIELDS);
            fieldsSection.forEach((f) -> fields.add(f.getAsJsonObject().get(CONF_NAME).getAsString()));
        });

        // Exclude fields
        if(fieldsToExclude.size() > 0) fields.removeAll(fieldsToExclude);

        // Convert to final list
        List<Column> ret = new ArrayList<>();
        fields.forEach((f)->ret.add(new Column(f)));
        return ret;
    }

    private static DataType convertType(GenericJsonModel.FieldType t) {
        switch (t) {
            case STRING:
                return DataTypes.StringType;
            case BOOLEAN:
                return DataTypes.BooleanType;
            case LONG:
                return DataTypes.LongType;
            case DOUBLE:
                return DataTypes.DateType;
            default:
                return DataTypes.StringType;
        }
    }

    public static JsonObject flatten(String srcString){
        JsonObject src = new JsonParser().parse(srcString).getAsJsonObject();
        JsonObject dst = src;

        // Check mandatory fields
        if( src.has("RECEIVED_TS") &&
            src.has("EVENT_TYPE") &&
            src.has("EVENT_ID") &&
            src.has("APP_MODULE") &&
            src.has("EVENT_DATE") &&
            src.has("APP_KEY") &&
            src.has("APP_VERSION") &&
            src.has("UID") &&
            src.has("payload") &&
            src.entrySet().size() == 9){

            //System.out.println("Need to be flattened");

            String base64data = src.get("payload").getAsString();
            String decoded = new String(Base64.getDecoder().decode(base64data));
            //System.out.println(base64data);
            //System.out.println(decoded);

            // Put mandatory fields into final document
            dst = new JsonParser().parse(decoded).getAsJsonObject();
            dst.addProperty("RECEIVED_TS", src.get("RECEIVED_TS").getAsString());
            dst.addProperty("EVENT_TYPE", src.get("EVENT_TYPE").getAsString());
            dst.addProperty("EVENT_ID", src.get("EVENT_ID").getAsString());
            dst.addProperty("APP_MODULE", src.get("APP_MODULE").getAsString());
            dst.addProperty("EVENT_DATE", src.get("EVENT_DATE").getAsString());
            dst.addProperty("APP_KEY", src.get("APP_KEY").getAsString());
            dst.addProperty("APP_VERSION", src.get("APP_VERSION").getAsString());
            dst.addProperty("UID", src.get("UID").getAsString());
            //System.out.println(dst.toString());
        } else {
            // This should be reported
            logger.error("Structure of the document is incorrect, expecting 9 fields for generic document envelope.");
            logger.error(srcString);
        }
        return dst;
    }

    /*public static void main(String[] args){

            try {
                String jsondefs = new String(Files.readAllBytes(Paths.get(args[0])));
                GenericJsonModel.createColumnList("es-fields-to-exclude", jsondefs);
            } catch(Exception e) {
                logger.error("EXCEPTION :" +  e.getMessage());
            }
    }*/
}