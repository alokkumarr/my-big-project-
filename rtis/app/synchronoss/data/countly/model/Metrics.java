package synchronoss.data.countly.model;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.nio.ByteBuffer;

/**
 * Created by srya0001 on 4/28/2016.
 */
@ApiModel(value = "Metric Data", discriminator = "type", description = "JSON object containing key, value pairs. metrics can only be sent together with begin_session")
public class Metrics {

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
            value = "_device",
            name = "Device Name",
            notes = "Example: Samsung Galaxy",
            access = "public",
            dataType = "string")
    public String _device;

    @ApiModelProperty(
            value = "_carrier",
            name = "Carrier",
            notes = "Mobile Operator, example: Vodafone",
            access = "public",
            dataType = "string")
    public String _carrier;

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
            value = "_density",
            name = "Density",
            access = "public",
            dataType = "string",
            notes = "Example: MDPI" )
    public String _density;

    @ApiModelProperty(
            value = "_locale",
            name = "Locale",
            access = "public",
            dataType = "string",
            notes = "Example: en_US" )
    public String _locale;

    @ApiModelProperty(
            value = "_store",
            name = "Store",
            access = "public",
            dataType = "string",
            notes = "Example: com.android.vending" )
    public String _store;

    public Metrics() {

    }

    public String toString() {
        return
                "_os: "            + (_os == null ? "<null>" : _os) +
                ", _os_version: "  + (_os_version == null ? "<null>" : _os_version) +
                ", _device: "      + (_device == null ? "<null>" : _device) +
                ", _resolution: "  + (_resolution == null ? "<null>" : _resolution) +
                ", _carrier: "     + (_app_version == null ? "<null>" : _app_version) +
                ", _app_version: " + (_density == null ? "<null>" : _density) +
                ", _density: "     + (_locale == null ? "<null>" : _locale) +
                ", _locale: "      + (_locale == null ? "<null>" : _locale) +
                ", _store: "       + (_store == null ? "<null>" : _store);

    }

    public Metrics(byte[] serialized){
        this.setFromBytes(serialized);
    }

    public byte[] getBytes() {

        byte[] _os_bytes = _os == null ? null : _os.getBytes();
        byte[] _os_version_bytes = _os_version == null ? null : _os_version.getBytes();
        byte[] _device_bytes = _device == null ? null : _device.getBytes();
        byte[] _resolution_bytes = _resolution == null ? null : _resolution.getBytes();
        byte[] _carrier_bytes = _carrier == null ? null : _carrier.getBytes();
        byte[] _app_version_bytes = _app_version == null ? null : _app_version.getBytes();
        byte[] _density_bytes = _density == null ? null : _density.getBytes();
        byte[] _locale_bytes = _locale == null ? null : _locale.getBytes();
        byte[] _store_bytes = _store == null ? null : _store.getBytes();

        int size = (_os_bytes == null ? 0 : _os_bytes.length) + 4
                + (_os_version_bytes == null ? 0 : _os_version_bytes.length) + 4
                + (_device_bytes == null ? 0 : _device_bytes.length) + 4
                + (_resolution_bytes == null ? 0 : _resolution_bytes.length) + 4
                + (_carrier_bytes == null ? 0 : _carrier_bytes.length) + 4
                + (_app_version_bytes == null ? 0 : _app_version_bytes.length) + 4
                + (_density_bytes == null ? 0 : _density_bytes.length) + 4
                + (_locale_bytes == null ? 0 : _locale_bytes.length) + 4
                + (_store_bytes == null ? 0 : _store_bytes.length) + 4;

        ByteBuffer output = ByteBuffer.allocate(size);

        SerializationHelper.put(output, _os_bytes);
        SerializationHelper.put(output, _os_version_bytes);
        SerializationHelper.put(output, _device_bytes);
        SerializationHelper.put(output, _resolution_bytes);
        SerializationHelper.put(output, _carrier_bytes);
        SerializationHelper.put(output, _app_version_bytes);
        SerializationHelper.put(output, _density_bytes);
        SerializationHelper.put(output, _locale_bytes);
        SerializationHelper.put(output, _store_bytes);
        return output.array();
    }

    public void setFromBytes(byte[] serialized) {
        // Prepare byte buffer
        ByteBuffer input = ByteBuffer.wrap(serialized);

        // Deserialize data
        _os = SerializationHelper.getString(input);
        _os_version = SerializationHelper.getString(input);
        _device = SerializationHelper.getString(input);
        _resolution = SerializationHelper.getString(input);
        _carrier = SerializationHelper.getString(input);
        _app_version = SerializationHelper.getString(input);
        _density = SerializationHelper.getString(input);
        _locale = SerializationHelper.getString(input);
        _store = SerializationHelper.getString(input);
    }
}




