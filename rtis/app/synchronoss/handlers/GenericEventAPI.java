package synchronoss.handlers;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * Created by srya0001 on 7/20/2016.
 */
@ApiModel(value = "GenericEvent",
        description =  "The generic event contains key-value pairs sent as HTTP request with POST method. The pairs should be in JSON representations. The model describes rather generic rules how to send an event then formalized API")
public class GenericEventAPI{

    @ApiModelProperty(
            value = "All values must be double quoted",
            name = "All keys must be double quoted",
            notes = "The key-value pairs must be presented as flattened JSON: as a map or an array.",
            access = "public",
            dataType = "string")
    public String value;

    @ApiModelProperty(
            value = "UTF-8 String",
            name = "String value",
            notes = "String value is any string of UTF-8 characters in double quotes",
            access = "public",
            dataType = "string")
    public String stringVal;

    @ApiModelProperty(
            value = "Boolean",
            name = "Boolean value",
            notes = "Boolean values are: \"true\", \"false\" in double quotes",
            access = "public",
            dataType = "Boolean")
    public boolean bVal;

    @ApiModelProperty(
            value = "Floating point number",
            name = "Double",
            notes = "Double value, e.g.: \"13.956\"",
            access = "public",
            dataType = "double")
    public double flVal;

    @ApiModelProperty(
            value = "Integer Number",
            name = "Integer",
            notes = "Integer number, e.g.: \"305\"",
            access = "public",
            dataType = "integer")
    public Integer intVal;

    @ApiModelProperty(
            value = "Date and Time in ISO 8601 standard: https://en.wikipedia.org/wiki/ISO_8601",
            name = "Timestamp: YYYY-MM-DDThh:mm:ss+hhtz:mmtz",
            notes = "hhtz:mmtz - timezone hours and minutes, Example: 2016-07-20T20:21:26+00:00",
            access = "public",
            dataType = "timestamp")
    public String timestamp;

    @ApiModelProperty(
            value = "The metadata ( schema ) to store events in parquet format in Synchronoss Datalake should be sent in JSON representation",
            notes = "{\n" +
                    "                \"fields\" : [\n" +
                    "                                {\"name\" : \"Field1\", \"type\" : \"string\"},\n" +
                    "                                {\"name\" : \"Field2\", \"type\" : \"string\"},\n" +
                    "                                {\"name\" : \"Field3\", \"type\" : \"string\"}\n" +
                    "                ]\n" +
                    "}\n",
            name = "The metadata JSON file",
            access = "public",
            dataType = "string")
    public String metadata;

}
