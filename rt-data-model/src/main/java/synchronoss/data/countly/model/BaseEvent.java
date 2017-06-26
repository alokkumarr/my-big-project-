package synchronoss.data.countly.model;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.nio.ByteBuffer;

/**
 * Created by srya0001 on 4/28/2016.
 */

@ApiModel(value = "Base Event Data", subTypes = {Crash.class, Event.class}, discriminator = "type", description = "Base event class descibes common properties used in final classes: Event and Crash" )
public abstract class BaseEvent {

    @ApiModelProperty( value = "app_key",
                       name = "Application Key",
                       notes = "APP_KEY of an app for which to report",
                       required = true,
                       access = "public",
                       dataType = "string")
    public String  app_key;

    @ApiModelProperty(
            value = "device_id",
            name = "Device ID",
            notes = " Your generated or device specific unique device ID to identify user",
            required = true,
            access = "public",
            dataType = "string")
    public String  device_id;

    @ApiModelProperty(
            value = "begin_session",
            name = "Begin Session Indicator",
            notes = "Should provide value 1 to indicate session start",
            access = "public",
            dataType = "string")
    public String  begin_session;

    @ApiModelProperty(
            value = "session_duration",
            name = "Session Duration",
            notes = "provides session duration in seconds, can be used as heartbeat to update current sessions duration, recommended time every 60 seconds",
            access = "public",
            dataType = "string")
    public String  session_duration;

    @ApiModelProperty(
            name = "End Session Indicator",
            value = "end_session",
            notes = "Should provide value 1 to indicate session end",
            access = "public",
            dataType = "string")
    public String  end_session;

    @ApiModelProperty(
            value = "ip_address",
            name = "End Session Indicator",
            notes = "(optional, can only be used with begin_session) IP address of user to determine user location, if not provided, countly will try to establish ip address based on connection data",
            access = "public",
            dataType = "string")
    public String  ip_address;

    @ApiModelProperty(
            value = "timestamp",
            name = "Event timestamp",
            notes = "(optional) 10 digit UTC timestamp for recording past data",
            access = "public",
            dataType = "string")
    public String  timestamp;

    @ApiModelProperty(
            value = "hour",
            name = "Event hour",
            notes = "(optional) current user local hour (0 - 23)",
            access = "public",
            dataType = "integer")
    public String  hour;

    @ApiModelProperty(
            value = "dow",
            name = "Day of Week",
            notes = "(optional) day of the week (0-sunday, 1 - monday, ... 6 - saturday)",
            access = "public",
            dataType = "integer")
    public String  dow;

    @ApiModelProperty(
            value = "country_code",
            name = "Country Code",
            notes = "(optional) For country you only need to provide ISO standard two letter country code",
            access = "public",
            dataType = "string")
    public String  country_code;

    @ApiModelProperty(
            value = "city",
            name = "City Name",
            notes = "(optional) Name of the user's city",
            access = "public",
            dataType = "string")
    public String  city;

    @ApiModelProperty(
            value = "location",
            name = "Location if GEO terms",
            notes = "(optional) users lat,lng",
            access = "public",
            dataType = "string")

    public String  location;

    @ApiModelProperty(
            value = "old_device_id",
            name = "Old Device ID",
            notes = "(optional) provide when changing device ID, so server would merge the data",
            access = "public",
            dataType = "string")
    public String  old_device_id;

    public String  sdk_version;

    public String toString(){
       return
                "app_key: "            + (app_key == null ? "<null>" : app_key)+
                ", device_id: "        + (device_id == null ? "<null>" : device_id)+
                ", begin_session: "    + (begin_session == null ? "<null>" : begin_session) +
                ", session_duration: " + (session_duration == null ? "<null>" : session_duration) +
                ", end_session: "      + (end_session == null ? "<null>" : end_session) +
                ", ip_address: "       + (ip_address == null ? "<null>" : ip_address)+
                ", timestamp: "        + (timestamp == null ? "<null>" : timestamp)+
                ", hour: "             + (hour == null ? "<null>" : hour)+
                ", dow: "              + (dow == null ? "<null>" : dow)+
                ", country_code: "     + (country_code == null ? "<null>" : country_code)+
                ", city: "             + (city == null ? "<null>" : city)+
                ", location: "         + (location == null ? "<null>" : location) +
                ", sdk_version: "      + (sdk_version == null ? "<null>" : sdk_version) +
                ", old_device_id: "    + (old_device_id == null ? "<null>" : old_device_id);
    }

    public byte[] getBytes() {

        // Convert strings ti array of bytes
        // We don't want to do this more than one time
        //  - so we will store in variables for future use
        byte[] app_key_bytes =          app_key == null     ? null : app_key.getBytes();
        byte[] device_id_bytes =        device_id == null   ? null : device_id.getBytes();
        byte[] begin_session_bytes =    begin_session == null ? null : begin_session.getBytes();
        byte[] session_duration_bytes = session_duration == null ? null : session_duration.getBytes();
        byte[] end_session_bytes  =     end_session == null ? null : end_session.getBytes();
        byte[] ip_address_bytes =       ip_address == null  ? null : ip_address.getBytes();
        byte[] timestamp_bytes =        timestamp == null ? null : timestamp.getBytes();
        byte[] hour_bytes =             hour == null ? null : hour.getBytes();
        byte[] dow_bytes =              dow == null ? null : dow.getBytes();
        byte[] country_code_bytes =     country_code == null? null : country_code.getBytes();
        byte[] city_bytes =             city == null        ? null : city.getBytes();
        byte[] location_bytes =         location == null    ? null : location.getBytes();
        byte[] old_device_id_bytes =    old_device_id == null ? null : old_device_id.getBytes();
        byte[] sdk_version_bytes  =      sdk_version == null ? null : sdk_version.getBytes();

        // Calculate byte array size required to store object data
        int size = (app_key_bytes == null ? 0 : app_key_bytes.length) + 4
                 + (device_id_bytes == null ? 0 : device_id_bytes.length) + 4
                 + (begin_session_bytes == null ? 0 :begin_session_bytes.length) + 4
                 + (session_duration_bytes == null ? 0 :session_duration_bytes.length) + 4
                 + (end_session_bytes == null ? 0 :end_session_bytes.length) + 4
                 + (ip_address_bytes == null ? 0 : ip_address_bytes.length) + 4
                 + (timestamp_bytes == null ? 0 : timestamp_bytes.length) + 4
                 + (hour_bytes == null ? 0 : hour_bytes.length) + 4
                 + (dow_bytes == null ? 0 : dow_bytes.length) + 4
                 + (country_code_bytes == null ? 0 : country_code_bytes.length) + 4
                 + (city_bytes == null ? 0 : city_bytes.length) + 4
                 + (location_bytes == null ? 0 : location_bytes.length) + 4
                 + (old_device_id_bytes == null ? 0 : old_device_id_bytes.length) + 4
                 + (sdk_version_bytes == null ? 0 : sdk_version_bytes.length) + 4;

        // Create byte buffer with required capacity
        ByteBuffer output = ByteBuffer.allocate(size);

        // Serialize data
        SerializationHelper.put(output, app_key_bytes);
        SerializationHelper.put(output, device_id_bytes);
        SerializationHelper.put(output, begin_session_bytes);
        SerializationHelper.put(output, session_duration_bytes);
        SerializationHelper.put(output, end_session_bytes);
        SerializationHelper.put(output, ip_address_bytes);
        SerializationHelper.put(output, timestamp_bytes);
        SerializationHelper.put(output, hour_bytes);
        SerializationHelper.put(output, dow_bytes);
        SerializationHelper.put(output, country_code_bytes);
        SerializationHelper.put(output, city_bytes);
        SerializationHelper.put(output, location_bytes);
        SerializationHelper.put(output, sdk_version_bytes);

        return output.array();
    }

    public void setFromBytes(byte[] serialized){

        // Prepare byte buffer
        ByteBuffer input = ByteBuffer.wrap(serialized);

        // Deserialize data
        app_key = SerializationHelper.getString(input);
        device_id = SerializationHelper.getString(input);
        begin_session = SerializationHelper.getString(input);
        session_duration = SerializationHelper.getString(input);
        end_session = SerializationHelper.getString(input);
        ip_address  = SerializationHelper.getString(input);
        timestamp = SerializationHelper.getString(input);
        hour = SerializationHelper.getString(input);
        dow = SerializationHelper.getString(input);
        country_code  = SerializationHelper.getString(input);
        city  = SerializationHelper.getString(input);
        location  = SerializationHelper.getString(input);
        old_device_id  = SerializationHelper.getString(input);
        sdk_version = SerializationHelper.getString(input);
    }

    /**
     * Each subclass should be able to identify if serialized data represents subclass
     *
     * @param serialized : serialized data
     * @return : true if serialized data represents subclass
     */
    public static boolean canDeserialize(byte[] serialized){
        throw  new IllegalStateException("Serialized data check hasn't been set up in the subclass");
    }
}
