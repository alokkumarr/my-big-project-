package synchronoss.data.generic.model.transformation;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import synchronoss.data.generic.model.GenericJsonModel;

import java.util.HashMap;
import java.util.Map;

import static synchronoss.data.generic.model.GenericJsonModel.*;

/**
 * Created by asor0002 on 10/5/2016.
 */
public class DecodeValue implements Transform {

    Map<String, String> values;
    private String from;
    private String to;
    private GenericJsonModel.FieldType toType;
    private String defaultValue;
    private String defaultDecodeValue;

    public DecodeValue(String fromField, String toField, GenericJsonModel.FieldType toType, String defaultVal, JsonObject transformerSpec ){
        this.from = fromField;
        this.to = toField;
        this.toType = toType;
        this.defaultValue = defaultVal;
        JsonArray values = transformerSpec.getAsJsonArray(CONF_MAP_MAPPINGS);
        this.values = new HashMap<>(values.size());
        values.forEach((v) -> {

                if(v.getAsJsonObject().has(CONF_MAP_FROM))
                    this.values.put(v.getAsJsonObject().get(CONF_MAP_FROM).getAsString(), v.getAsJsonObject().get(CONF_MAP_TO).getAsString());
                if(v.getAsJsonObject().has(CONF_MAP_OTHERWISE))
                    this.defaultDecodeValue = v.getAsJsonObject().get(CONF_MAP_OTHERWISE).getAsString();
            }
        );
    }
    public void transform(JsonObject src, JsonObject dst){
        JsonElement value = null;
        // Check if source field has been configured and it exists in source document
        if (from != null && !from.isEmpty() && src.has(from)) {
            String valueToDecode = src.get(from).getAsString();
            String finalValue = values.containsKey(valueToDecode) ? values.get(valueToDecode) : defaultDecodeValue;
            value = GenericJsonModel.createValue(finalValue, toType);

        } else {
            // No source field specified
            if(src.has(to)){
                // Field with the same value has been found in source document
                value = GenericJsonModel.createValue(src.get(to).getAsString(), toType);

            } else {
                // Field with the same name has not been found in source document
                if(defaultValue != null && !defaultValue.isEmpty()) {
                    // No source data except default value
                    value = GenericJsonModel.createValue(defaultValue, toType);
                } else {
                    // Nothing else to do
                }
            }
        }
        if(value != null){
            dst.add(to, value);
        }
    }
}
