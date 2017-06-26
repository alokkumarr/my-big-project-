package synchronoss.data.countly.model;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.nio.ByteBuffer;
import java.util.Map;

/**
 * Created by srya0001 on 4/28/2016.
 */

@ApiModel(value = "Crash Data", description =  "The class stores all crash report attributes including crash report itself as base64 string")
public class Crash extends BaseEvent {

    public static byte Signature = 'C';

    public String incident_id;        //UUID to identify a particular crash, the field is required for ES

    @ApiModelProperty(
            value = "_os",
            name = "Phone Operating System",
            notes = "Generic name, example: Android",
            access = "public",
            dataType = "string")
    public String _os;

    @ApiModelProperty(
            value = "_os_version",
            name = "OS Version",
            notes = "Full version code",
            access = "public",
            dataType = "string" )
    public String _os_version;

    @ApiModelProperty(
            value = "_manufacture",
            name = "Manufacture",
            notes = "Example: Samsung, may not be provided for ios or be constant, like Apple",
            access = "public",
            dataType = "string")
    public String _manufacture;

    @ApiModelProperty(
            value = "_device",
            name = "Device Name",
            notes = "Example: Samsung Galaxy",
            access = "public",
            dataType = "string")
    public String _device;

    @ApiModelProperty(
            value = "_resolution",
            name = "Resolution",
            notes = "Phone screen resolution, example: 1200x800",
            access = "public",
            dataType = "string")
    public String _resolution;

    @ApiModelProperty(
            value = "_app_version",
            name = "Application version",
            access = "public",
            dataType = "string")
    public String _app_version;

    @ApiModelProperty(
            value = "_cpu",
            name = "CPU",
            notes = "Type of cpu used on device (for ios will be based on device), example = armv7",
            access = "public",
            dataType = "string")
    public String _cpu;

    @ApiModelProperty(
            value = "_opengl",
            name = "OpenGL",
            notes = "version of Open GL supported, example: 2.1",
            access = "public",
            dataType = "string")
    public String _opengl;

    @ApiModelProperty(
            value = "_orientation",
            name = "Orientation",
            notes = "in which device was held, landscape, portrait, etc",
            access = "public",
            dataType = "string")
    public String _orientation;

    @ApiModelProperty(
            value = "_error",
            name = "Error Stack",
            notes = "Some error stack here, can provide multiple separated by blank new line",
            access = "public",
            dataType = "string")
    public String _error;

    @ApiModelProperty(
            value = "_name",
            name = "Error",
            notes = "optional if provided by OS/Platform, else will use first line of stack, example: 'Null Pointer exception'",
            access = "public",
            dataType = "string")
    public String _name;

    @ApiModelProperty(
            value = "_ram_current",
            name = "RAM current",
            notes = "Current RAM size in megabytes, Example: 1024",
            access = "public",
            dataType = "integer")
    public String _ram_current;

    @ApiModelProperty(
            value = "_ram_total",
            name = "RAM total",
            notes = "Total RAM size in megabytes, Example: 4096",
            access = "public",
            dataType = "integer")
    public String _ram_total;

    @ApiModelProperty(
            value = "_disk_current",
            name = "Disk current",
            notes = "Used disk size in megabytes, Example: 3000",
            access = "public",
            dataType = "integer")
    public String _disk_current;

    @ApiModelProperty(
            value = "_disk_total",
            name = "RAM total",
            notes = "Total disk size in megabytes, Example: 10240",
            access = "public",
            dataType = "integer")
    public String _disk_total;

    @ApiModelProperty(
            value = "_bat",
            name = "Battery Level",
            notes = "battery level from 0 to 100",
            access = "public",
            dataType = "integer")
    public String _bat;

    @ApiModelProperty(
            value = "_bat_current",
            name = "Battery State",
            notes = "Alternative to _bat field: current battery state",
            access = "public",
            dataType = "integer")
    public String _bat_current;

    @ApiModelProperty(
            value = "_bat_total",
            name = "Battery Scale",
            notes = "Alternative to _bat field: Battery scale",
            access = "public",
            dataType = "integer")
    public String _bat_total;

    @ApiModelProperty(
            value = "_run",
            name = "Run",
            notes = "Running time since app start in seconds, e.g.: 180",
            access = "public",
            dataType = "integer")
    public String _run;

    @ApiModelProperty(
            value = "_online",
            name = "Online Indicator",
            notes = "true if device is connected to the internet (WiFi or 3G), false or  not provided if not connected",
            access = "public",
            dataType = "boolean")
    public String _online;

    @ApiModelProperty(
            value = "_root",
            name = "Jail Broken Indicator",
            notes = "true if device is rooted/jailbroken, false or not provided if not",
            access = "public",
            dataType = "boolean")
    public String _root;

    @ApiModelProperty(
            value = "_muted",
            name = "Mute Indicator",
            notes = "true if volume is off, device is in muted state",
            access = "public",
            dataType = "boolean")
    public String _muted;

    @ApiModelProperty(
            value = "_background",
            name = "Background Indicator",
            notes = "True if app was in background when it crashed",
            access = "public",
            dataType = "boolean")
    public String _background;

    @ApiModelProperty(
            value = "_nonfatal",
            name = "Fatal Indicator",
            notes = "True if handled exception, false or not provided if unhandled crash",
            access ="public",
            dataType = "boolean")
    public String _nonfatal;

    @ApiModelProperty(
            value = "_logs",
            name = "Logs",
            notes = "Some additional logs provided, if any",
            access ="public",
            dataType = "string")
    public String _logs;

    @ApiModelProperty(
            value = "_error_details",
            name = "Crash Report",
            notes = "Base64 encoded crash report string",
            access ="public",
            dataType = "string")
    public byte[]  _error_details;


    public Map<String, String> custom_values;

    public Crash(String uuid)
    {
        incident_id = uuid;
    }


    public Crash(){

    }

    public Crash(byte[] serialized){
        this.setFromBytes(serialized);
    }

    @Override
    public byte[] getBytes() {

        ByteBuffer custByteBuffer = SerializationHelper.serializeMap(custom_values);

        byte[] base_bytes = super.getBytes();

        byte[] incident_id_bytes    = SerializationHelper.getBytes(incident_id);
        byte[] _os_bytes            = SerializationHelper.getBytes(_os);
        byte[] _os_version_bytes    = SerializationHelper.getBytes(_os_version);
        byte[] _manufacture_bytes   = SerializationHelper.getBytes(_manufacture);
        byte[] _device_bytes        = SerializationHelper.getBytes(_device);
        byte[] _resolution_bytes    = SerializationHelper.getBytes(_resolution);
        byte[] _app_version_bytes   = SerializationHelper.getBytes(_app_version);
        byte[] _cpu_bytes           = SerializationHelper.getBytes(_cpu);
        byte[] _opengl_bytes        = SerializationHelper.getBytes(_opengl);
        byte[] _orientation_bytes   = SerializationHelper.getBytes(_orientation);
        byte[] _error_bytes         = SerializationHelper.getBytes(_error);
        byte[] _name_bytes          = SerializationHelper.getBytes(_name);
        byte[] _ram_current_bytes   = SerializationHelper.getBytes(_ram_current);
        byte[] _ram_total_bytes     = SerializationHelper.getBytes(_ram_total);
        byte[] _disk_current_bytes  = SerializationHelper.getBytes(_disk_current);
        byte[] _disk_total_bytes    = SerializationHelper.getBytes(_disk_total);
        byte[] _bat_bytes           = SerializationHelper.getBytes(_bat);
        byte[] _bat_current_bytes   = SerializationHelper.getBytes(_bat_current);
        byte[] _bat_total_bytes     = SerializationHelper.getBytes(_bat_total);
        byte[] _run_bytes           = SerializationHelper.getBytes(_run);
        byte[] _online_bytes        = SerializationHelper.getBytes(_online);
        byte[] _root_bytes          = SerializationHelper.getBytes(_root);
        byte[] _muted_bytes         = SerializationHelper.getBytes(_muted);
        byte[] _background_bytes    = SerializationHelper.getBytes(_background);
        byte[] _nonfatal_bytes      = SerializationHelper.getBytes(_nonfatal);
        byte[] _logs_bytes          = SerializationHelper.getBytes(_logs);


        int size = 1  // Size of signature
            + SerializationHelper.getSerializedSize(base_bytes)
            + SerializationHelper.getSerializedSize(incident_id_bytes)
            + SerializationHelper.getSerializedSize(_os_bytes)
            + SerializationHelper.getSerializedSize(_os_version_bytes)
            + SerializationHelper.getSerializedSize(_manufacture_bytes)
            + SerializationHelper.getSerializedSize(_device_bytes)
            + SerializationHelper.getSerializedSize(_resolution_bytes)
            + SerializationHelper.getSerializedSize(_app_version_bytes)
            + SerializationHelper.getSerializedSize(_cpu_bytes)
            + SerializationHelper.getSerializedSize(_opengl_bytes)
            + SerializationHelper.getSerializedSize(_orientation_bytes)
            + SerializationHelper.getSerializedSize(_error_bytes)
            + SerializationHelper.getSerializedSize(_name_bytes)
            + SerializationHelper.getSerializedSize(_ram_current_bytes)
            + SerializationHelper.getSerializedSize(_ram_total_bytes)
            + SerializationHelper.getSerializedSize(_disk_current_bytes)
            + SerializationHelper.getSerializedSize(_disk_total_bytes)
            + SerializationHelper.getSerializedSize(_bat_bytes)
            + SerializationHelper.getSerializedSize(_bat_current_bytes)
            + SerializationHelper.getSerializedSize(_bat_total_bytes)
            + SerializationHelper.getSerializedSize(_run_bytes)
            + SerializationHelper.getSerializedSize(_online_bytes)
            + SerializationHelper.getSerializedSize(_root_bytes)
            + SerializationHelper.getSerializedSize(_muted_bytes)
            + SerializationHelper.getSerializedSize(_background_bytes)
            + SerializationHelper.getSerializedSize(_nonfatal_bytes)
            + SerializationHelper.getSerializedSize(_logs_bytes)
            + SerializationHelper.getSerializedSize(_error_details)
            + (custByteBuffer == null ? 0 : custByteBuffer.array().length) + 4;

        ByteBuffer output = ByteBuffer.allocate(size);

        output.put(Signature);
        SerializationHelper.put(output, base_bytes);
        SerializationHelper.put(output, incident_id_bytes);
        SerializationHelper.put(output, _os_bytes);
        SerializationHelper.put(output, _os_version_bytes);
        SerializationHelper.put(output, _manufacture_bytes);
        SerializationHelper.put(output, _device_bytes);
        SerializationHelper.put(output, _resolution_bytes);
        SerializationHelper.put(output, _app_version_bytes);
        SerializationHelper.put(output, _cpu_bytes);
        SerializationHelper.put(output, _opengl_bytes);
        SerializationHelper.put(output, _orientation_bytes);
        SerializationHelper.put(output, _error_bytes);
        SerializationHelper.put(output, _name_bytes);
        SerializationHelper.put(output, _ram_current_bytes);
        SerializationHelper.put(output, _ram_total_bytes);
        SerializationHelper.put(output, _disk_current_bytes);
        SerializationHelper.put(output, _disk_total_bytes);
        SerializationHelper.put(output, _bat_bytes);
        SerializationHelper.put(output, _bat_current_bytes);
        SerializationHelper.put(output, _bat_total_bytes);
        SerializationHelper.put(output, _run_bytes);
        SerializationHelper.put(output, _online_bytes);
        SerializationHelper.put(output, _root_bytes);
        SerializationHelper.put(output, _muted_bytes);
        SerializationHelper.put(output, _background_bytes);
        SerializationHelper.put(output, _nonfatal_bytes);
        SerializationHelper.put(output, _logs_bytes);
        SerializationHelper.put(output, _error_details);

        if(custByteBuffer != null){
            output.putInt(custByteBuffer.array().length);
            output.put(custByteBuffer.array());
        } else
            output.putInt(0);
        return output.array();
    }

    @Override
    public void setFromBytes(byte[] serialized) {
        ByteBuffer input = ByteBuffer.wrap(serialized);

        // Check if we got correct data
        byte eventSignature = input.get();
        if(eventSignature != Signature) {
            throw new IllegalArgumentException("Serialized data doesn't represent " + this.getClass().getName());
        }

        // Base class deserialization
        int baseSize = input.getInt();
        byte[] base_bytes = new byte[baseSize];
        input.get(base_bytes);
        super.setFromBytes(base_bytes);

        //Crash report class members
        // Strings
        incident_id = SerializationHelper.getString(input);
        _os = SerializationHelper.getString(input);
        _os_version = SerializationHelper.getString(input);
        _manufacture = SerializationHelper.getString(input);
        _device = SerializationHelper.getString(input);
        _resolution = SerializationHelper.getString(input);
        _app_version = SerializationHelper.getString(input);
        _cpu = SerializationHelper.getString(input);
        _opengl = SerializationHelper.getString(input);
        _orientation = SerializationHelper.getString(input);
        _error = SerializationHelper.getString(input);
        _name = SerializationHelper.getString(input);
        _ram_current = SerializationHelper.getString(input);
        _ram_total = SerializationHelper.getString(input);
        _disk_current = SerializationHelper.getString(input);
        _disk_total = SerializationHelper.getString(input);
        _bat = SerializationHelper.getString(input);
        _bat_current = SerializationHelper.getString(input);
        _bat_total = SerializationHelper.getString(input);
        _run = SerializationHelper.getString(input);
        _online = SerializationHelper.getString(input);
        _root = SerializationHelper.getString(input);
        _muted = SerializationHelper.getString(input);
        _background = SerializationHelper.getString(input);
        _nonfatal = SerializationHelper.getString(input);
        _logs = SerializationHelper.getString(input);

        //
        int reportSize = input.getInt();
        if(reportSize > 0){
            _error_details = new byte[reportSize];
            input.get(_error_details);
        } else
            _error_details = null;

        int mapBytesSize = input.getInt();
        if(mapBytesSize > 0){
            byte[] map_bytes = new byte[mapBytesSize];
            input.get(map_bytes);
            ByteBuffer mapByteBuffer =  ByteBuffer.wrap(map_bytes);
            custom_values = SerializationHelper.getMap(mapByteBuffer);
        } else
            custom_values = null;
    }

    public String toString() {
        return  String.format(
                            "incident UUID = %s,  _os = %s, _os_version = %s, _manufacture = %s, _device = %s, " +
                            "_resolution = %s, _app_version = %s, _cpu = %s, _opengl = %s, " +
                            "_ram_current = %s, _ram_total = %s, _disk_current = %s, _disk_total = %s, " +
                            "_bat = %s, _bat_current = %s, _bat_total = %s, _orientation = %s, " +
                            "_root = %s, _online = %s, _muted = %s, _background = %s, _name = %s, " +
                            "_error = %s, _run = %s, _nonfatal = %s, _logs = %s, _error_details length = %d",
        incident_id, _os, _os_version, _manufacture, _device, _resolution, _app_version, _cpu,
        _opengl, _ram_current, _ram_total, _disk_current, _disk_total, _bat, _bat_current,
        _bat_total, _orientation, _root, _online, _muted, _background, _name, _error,
        _run, _nonfatal, _logs, ((_error_details!=null)?_error_details.length:0));
    }

    public static boolean canDeserialize(byte[] serialized){
        return serialized[0] == Signature;
    }
}
