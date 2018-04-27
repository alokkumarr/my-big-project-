package synchronoss.data.countly.model;

import com.google.common.collect.Multiset;
import org.apache.log4j.Logger;
import com.google.gson.*;
import org.apache.spark.sql.types.DataTypes;
import org.apache.spark.sql.types.StructField;
import org.apache.spark.sql.types.StructType;

import java.util.Map;

/**
 * Created by asor0002 on 11/3/2016.
 * This class will create and execute set of transformations for Countly records
 */
public class CountlyModel {
    private static final Logger logger = Logger.getLogger(CountlyModel.class);

    private static String[] mandatoryFields = { "app_key", "device_id", "UID", "received_ts"};
    private static String[] optionalFields = {"begin_session", "session_duration", "end_session", "ip_address",
            "timestamp", "hour", "dow", "country_code", "city", "location", "old_device_id"};
    private static String[] metricsFields = {"_os", "_os_version", "_device", "_resolution", "_carrier", "_app_version",
            "_density", "_locale", "_store"};
    private static String[] userDetailsFields = {"name", "username", "email", "organization", "phone", "picture",
            "gender", "byear"};
    private static String[] userDetailsMaps = {"custom"};
    private static String[] eventMandatoryFields = {"key", "count"};
    private static String[] eventFields = { "sum", "dur", "timestamp",  "hour", "dow" };
    private static String[] eventMaps = {"segmentation"};

    private static StructField[] fields = {
            DataTypes.createStructField("app_key",          DataTypes.StringType, false),
            DataTypes.createStructField("device_id",        DataTypes.StringType, false),
            DataTypes.createStructField("begin_session",    DataTypes.StringType, true),
            DataTypes.createStructField("session_duration", DataTypes.StringType, true),
            DataTypes.createStructField("end_session",      DataTypes.StringType, true),
            DataTypes.createStructField("ip_address",       DataTypes.StringType, true),
            DataTypes.createStructField("timestamp",        DataTypes.StringType, true),
            DataTypes.createStructField("hour",             DataTypes.StringType, true),
            DataTypes.createStructField("dow",              DataTypes.StringType, true),
            DataTypes.createStructField("country_code",     DataTypes.StringType, true),
            DataTypes.createStructField("city",             DataTypes.StringType, true),
            DataTypes.createStructField("location",         DataTypes.StringType, true),
            DataTypes.createStructField("old_device_id",    DataTypes.StringType, true),
            DataTypes.createStructField("name",             DataTypes.StringType, true),
            DataTypes.createStructField("username",         DataTypes.StringType, true),
            DataTypes.createStructField("email",            DataTypes.StringType, true),
            DataTypes.createStructField("organization",     DataTypes.StringType, true),
            DataTypes.createStructField("phone",            DataTypes.StringType, true),
            DataTypes.createStructField("picture",          DataTypes.StringType, true),
            DataTypes.createStructField("gender",           DataTypes.StringType, true),
            DataTypes.createStructField("byear",            DataTypes.StringType, true),
            DataTypes.createStructField("custom",           DataTypes.createMapType(
                    DataTypes.StringType, DataTypes.StringType, true), true),
            DataTypes.createStructField("_os",              DataTypes.StringType, true),
            DataTypes.createStructField("_os_version",      DataTypes.StringType, true),
            DataTypes.createStructField("_device",          DataTypes.StringType, true),
            DataTypes.createStructField("_resolution",      DataTypes.StringType, true),
            DataTypes.createStructField("_carrier",         DataTypes.StringType, true),
            DataTypes.createStructField("_app_version",     DataTypes.StringType, true),
            DataTypes.createStructField("_density",         DataTypes.StringType, true),
            DataTypes.createStructField("_locale",          DataTypes.StringType, true),
            DataTypes.createStructField("_store",           DataTypes.StringType, true),
            DataTypes.createStructField("key",              DataTypes.StringType, true),
            DataTypes.createStructField("count",            DataTypes.IntegerType, true),
            DataTypes.createStructField("event_sum",        DataTypes.DoubleType, true),
            DataTypes.createStructField("event_dur",        DataTypes.DoubleType, true),
            DataTypes.createStructField("segmentation",     DataTypes.createMapType(
                    DataTypes.StringType, DataTypes.StringType, true), true),
            DataTypes.createStructField("event_timestamp",  DataTypes.StringType, true),
            DataTypes.createStructField("event_hour",       DataTypes.IntegerType, true),
            DataTypes.createStructField("event_dow",        DataTypes.IntegerType, true),
            DataTypes.createStructField("_has_metrics",     DataTypes.BooleanType, false),
            DataTypes.createStructField("_has_user_details", DataTypes.BooleanType, false),
            DataTypes.createStructField("_type",            DataTypes.StringType, false),
            DataTypes.createStructField("UID",              DataTypes.StringType, false),
            DataTypes.createStructField("received_ts",      DataTypes.StringType, false)

    };

    private static StructType schema = DataTypes.createStructType(fields);

    public static String transform(String strSrc) throws Exception {
        // Countly is a relatively stable format with fixed structure
        // We will hardcode transformation since changes are not required frequently
        JsonObject src = new JsonParser().parse(strSrc).getAsJsonObject();
        JsonObject dst = new JsonObject();

        // Ordinary mandatory fields
        for(String s : mandatoryFields) copyField(s , src, s, dst, true);

        // Ordinary optional fields
        for(String s : optionalFields) copyField(s , src, s, dst, false);

        // Nested structures
        if(src.has("metrics")) {
            JsonObject metrics = src.getAsJsonObject("metrics");
            for(String s :  metricsFields) copyField(s, metrics, s, dst, false);
            dst.addProperty("_has_metrics", true);
        } else {
            dst.addProperty("_has_metrics", false);
        }
        if(src.has("user_details")){
            JsonObject userDetails = src.getAsJsonObject("user_details");
            for(String s : userDetailsFields) copyField(s, userDetails, s, dst, false);
            for(String s : userDetailsMaps) copyMap(s, userDetails, dst);
            dst.addProperty("_has_user_details", true);
        } else
            dst.addProperty("_has_user_details", false);

        if(src.has("event")) {
            JsonObject events = src.getAsJsonObject("event");
            for(String s : eventMandatoryFields) copyField(s, events, s, dst, true);
            for(String s : eventFields) copyField(s, events, "event_" + s, dst, false);
            for(String s : eventMaps) copyMap(s, events, dst);
            //for(String s : eventMaps) copyMapAsFields(s, s.substring(0,3) + "_", events, dst);
            dst.addProperty("_type", "event");
        } else {
            dst.addProperty("_type", "non-event");
        }
        return dst.toString();
    }

    private static void copyField(String fieldName, JsonObject src, String toFieldName, JsonObject dst, boolean mandatory)
        throws Exception {
        if(src.has(fieldName)) dst.add( toFieldName, src.getAsJsonPrimitive(fieldName));
        else {
            if(mandatory) throw new Exception("Missing field :" + fieldName);
        }
    }

    private static void copyMapAsFields(String fieldName, String fieldPrefix, JsonObject src, JsonObject dst){
        if(src.has(fieldName) && src.get(fieldName).isJsonObject()) {
            JsonObject map = src.get(fieldName).getAsJsonObject();
            for( Map.Entry<String, JsonElement> e : map.entrySet()){
                dst.add( fieldPrefix + e.getKey(), e.getValue().getAsJsonPrimitive());
            }
        }
    }

    private static void copyMap(String fieldName, JsonObject src, JsonObject dst){
        if(src.has(fieldName)) dst.add( fieldName, src.getAsJsonObject(fieldName));
    }

    public static StructType createGlobalSchema() throws RuntimeException {
        return schema;
    }

    public static void main(String [] args){
        String JSON = "{'UID':'09ce14b9-c90d-4a66-90f6-b9e1af94a4d2','app_key':'1e04d731840fed1bc68fd4b015be5360b5da6ef4','device_id':'5700b84d16305929a4db2a60a0c3295b88e436c3','timestamp':'1481564864','event':{'sum':0,'count':1,'segmentation':{'origin':'target','addkey':'transfer_type','session_id':'33FA1176-DA9D-4912-8FA7-B085A72DAA29','time_zone':'EST','opco':'trc','addvalue':'p2p'},'key':'MCTAdditional','timestamp':1481564864}}";

        String transformed = null;
        try {
             transformed = CountlyModel.transform(JSON);
        } catch (Exception e){
            System.err.println(e.getMessage());
        }
        System.out.println(transformed);
    }
}
