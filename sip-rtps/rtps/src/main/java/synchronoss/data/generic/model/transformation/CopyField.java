package synchronoss.data.generic.model.transformation;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import synchronoss.data.generic.model.GenericJsonModel;

/**
 * Created by Alexey Sorokin on 10/5/2016.
 * This class conditionally copying field from source document to destination document
 * Transformations supported
 *  - Rename the field
 *  - Create new field with default value
 *  - Copy field from source to destination with same field name
 *  - Convert data type
 */
public class CopyField implements Transform {
    private String from;
    private String to;
    private GenericJsonModel.FieldType toType;
    private String defaultValue;

    public CopyField(String fromField, String toField, GenericJsonModel.FieldType toType, String defaultVal){
        this.from = fromField;
        this.to = toField;
        this.toType = toType;
        this.defaultValue = defaultVal;
    }

    public void transform(JsonObject src, JsonObject dst) {
        JsonElement value = null;
        // Check if source field has been configured and it exists in source document
        if (from != null && !from.isEmpty() && src.has(from)) {
            value = GenericJsonModel.createValue(src.get(from).getAsString(),toType);
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
