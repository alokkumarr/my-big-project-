package synchronoss.data.generic.model;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.apache.log4j.Logger;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

import static synchronoss.data.generic.model.GenericJsonModel.*;

/**
 * Created by asor0002 on 10/6/2016.
 *
 */
public class GenericJsonModelValidator {
    private static final Logger logger = Logger.getLogger(GenericJsonModelValidator.class);

    public static boolean validate(String definitionsJson){
        logger.info("Validating...");
        boolean ret = false;
        try {
            JsonObject jo = new JsonParser().parse(definitionsJson).getAsJsonObject();
            // Check if objects are there
            logger.info("Validating objects structure definitions...");
            if(!jo.has(CONF_OBJECTS)){
                throw new Exception("No objects defined - can't find \"" + CONF_OBJECTS + "\" array section");
            }
            if(!jo.get(CONF_OBJECTS).isJsonArray()) {
                throw new Exception("<" + CONF_OBJECTS + "> section should be an array");
            }

            JsonArray objects = jo.get(CONF_OBJECTS).getAsJsonArray();
            AtomicReference<JsonObject> transformations = new AtomicReference<>(null);

            if(jo.has(CONF_TRANSFORMATIONS)) {
                transformations.set(jo.get(CONF_TRANSFORMATIONS).getAsJsonObject());
            }
            logger.info("Found " + objects.size() + " object definitions");

            AtomicInteger counter = new AtomicInteger(1);
            Map<String, GenericJsonModel.FieldType> globalFields = new HashMap<>();
            objects.forEach((v)->{
                if(!v.getAsJsonObject().has(CONF_NAME)){
                    throw new RuntimeException("Object [" + counter.get() + "] missing name (\""+ CONF_NAME + "\")");
                }
                String objectName = v.getAsJsonObject().get(CONF_NAME).getAsString();
                logger.info("Validating \"" + objectName + "\" object");

                if(!v.getAsJsonObject().has(CONF_FIELDS)){
                    throw new RuntimeException("Object " + objectName + " has no field definitions (\""+ CONF_FIELDS + "\" : [...])");
                }
                JsonArray fields = v.getAsJsonObject().get(CONF_FIELDS).getAsJsonArray();
                logger.info("Found " + fields.size() + " field definitions");
                AtomicInteger fieldsCounter = new AtomicInteger(1);
                fields.forEach((f) -> {
                    if(!f.getAsJsonObject().has(CONF_NAME))
                        throw new RuntimeException(objectName + " : field definition [" + fieldsCounter +"] missing name (\"" + CONF_NAME + "\")");
                    String fieldName = f.getAsJsonObject().get(CONF_NAME).getAsString();
                    GenericJsonModel.FieldType ft = GenericJsonModel.FieldType.STRING;
                    if(!f.getAsJsonObject().has(CONF_TYPE))
                        logger.info("Field " + objectName + "." + fieldName + " assumed to be STRING (no \"" + CONF_TYPE + "\" provided)");
                    else {
                        String fieldType =  f.getAsJsonObject().get(CONF_TYPE).getAsString();
                        try {
                            ft = GenericJsonModel.FieldType.valueOf(fieldType);
                        } catch(Exception e) {
                            throw new RuntimeException("Field " + objectName + "." + fieldName + " - invalid field type: " + fieldType + ". Must be STRING, LONG, DOUBLE, BOOLEAN, ot DATE.");
                        }
                    }
                    if(globalFields.containsKey(fieldName)){
                        GenericJsonModel.FieldType globalType = globalFields.get(fieldName);
                        if(!globalType.equals(ft)){
                            throw new RuntimeException(objectName + " : field " + fieldName + "(" + ft + ") has been previously declared as " + globalType
                                    + ". " + fieldName + " should have same type in all data objects.");
                        }
                    }
                    globalFields.put(fieldName, ft);

                    if(f.getAsJsonObject().has(CONF_TRANSFORM)){
                        String transformationName = f.getAsJsonObject().get(CONF_TRANSFORM).getAsString();
                        if(transformations.get() != null && transformations.get().has(transformationName)){
                            JsonObject transformation = transformations.get().get(transformationName).getAsJsonObject();
                            validateMappingTransformation(transformation, transformationName);
                        } else {
                            throw new RuntimeException(objectName + " : field " + fieldName
                                    + " referring non-existent transformation \"" + transformationName + "\"");
                        }
                    }

                    fieldsCounter.incrementAndGet();
                });
                counter.incrementAndGet();
            });

            logger.info("Validating completed successfully.");
            ret = true;
        } catch(Exception e){
            logger.error(e.getMessage());
            ret = false;
        }
        return ret;
    }

    private static void validateMappingTransformation(JsonObject transformation, String name){
        if(!transformation.has(CONF_TYPE) ||  !transformation.get(CONF_TYPE).getAsString().equals("map")){
            throw new RuntimeException(name + " : Missing or invalid transformation type property. Should be: \"" + CONF_TYPE + "\" : \"map\"");
        }
        if(!transformation.has(CONF_MAP_MAPPINGS)){
            throw new RuntimeException(name + " : Missing  mappings section : \"" + CONF_MAP_MAPPINGS + "\" : [...]");
        }
        AtomicInteger count = new AtomicInteger(1);
        JsonArray mappings = transformation.get(CONF_MAP_MAPPINGS).getAsJsonArray();
        mappings.forEach((m) -> {
            if(!m.getAsJsonObject().has(CONF_MAP_OTHERWISE)){
                if(!m.getAsJsonObject().has(CONF_MAP_FROM) || !m.getAsJsonObject().has(CONF_MAP_FROM)){
                    throw new RuntimeException("Transformation \"" + name + "\" : invalid field mapping [" + count.get()
                            + "] - must have \"" + CONF_MAP_OTHERWISE
                            + "\" or combination of \"" + CONF_MAP_FROM + "\" and \""+ CONF_MAP_TO + "\" properties  defined");
                }
            }
            count.incrementAndGet();
        });

    }
}
