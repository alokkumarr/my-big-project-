package synchronoss.data.countly.model;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.nio.ByteBuffer;
import java.util.Map;

/**
 * Created by srya0001 on 4/28/2016.
 */

@ApiModel(value = "Extended Event Data", description = "JSON array containing event objects. Each event object can have below properties")
public class EventData {


    @ApiModelProperty(
            value = "level_success_count",
            name = "level_success_count",
            access = "public",
            dataType = "integer")
    public String   level_success_count;

    @ApiModelProperty(
            value = "level_fail_count",
            name = "level_fail_count",
            access = "public",
            dataType = "integer")
    public String   level_fail_count;

    @ApiModelProperty(
            value = "key",
            name = "key",
            access = "public",
            required = true,
            dataType = "string")
    public String   key;

    @ApiModelProperty(
            value = "count",
            name = "Count",
            access = "public",
            required = true,
            dataType = "integer")
    public String   count;

    @ApiModelProperty(
            value = "sum",
            name = "Summary",
            notes = "(optional) day of the week (0-sunday, 1 - monday, ... 6 - saturday)",
            access = "public",
            dataType = "double")
    public String   sum;

    @ApiModelProperty(
            value = "dur",
            name = "Duration",
            notes = "You can report what views did user view and for how long (duration).",
            access = "public",
            dataType = "double")
    public String   dur;

    @ApiModelProperty(
            value = "timestamp",
            name = "Timestamp",
            notes = "(optional) 10 digit UTC timestamp for recording past data",
            access = "public",
            dataType = "long")
    public String   timestamp;

    @ApiModelProperty(
            value = "dow",
            name = "Day of Week",
            notes = "(optional) day of the week (0-sunday, 1 - monday, ... 6 - saturday)",
            access = "public",
            dataType = "integer")
    public String   dow;

    @ApiModelProperty(
            value = "hour",
            name = "Event hour",
            notes = "(optional) current user local hour (0 - 23)",
            access = "public",
            dataType = "integer")
    public String   hour;

    @ApiModelProperty(
            value = "segmentation",
            name = "Segmentation",
            notes = "(Optional, Dictionary Object), example:  {app_version: 1.0, country: Turkey}",
            access = "public",
            dataType = "integer")
    public Map<String,String> segmentation;



    public EventData() {

    }

    public EventData(byte[] serialized){
        this.setFromBytes(serialized);
    }

    public String toString() {

        String seg = "segmentation:";
        if(segmentation != null) {
            if(segmentation.size() > 0 ) {
                for (Map.Entry<String, String> e : this.segmentation.entrySet()) {
                    String tmp = "[" + (e.getKey() == null ? "<null>" : e.getKey()) + ":"
                            + (e.getValue() == null ? "<null>" : e.getValue()) + "] ";
                    seg += tmp;
                }
            } else
                seg += "<empty>";
        } else {
            seg += ": <null>";
        }

        return
            "level_success_count: "   + (level_success_count == null ? "<null>" : level_success_count )+
            ", level_fail_count: "    + (level_fail_count == null ? "<null>" : level_fail_count )+
            ", key: "                 + (key == null ? "<null>" : key )+
            ", count: "               + (count == null ? "<null>" : count )+
            ", sum: "                 + (sum == null ? "<null>" : sum )+
            ", dur: "                 + (dur == null ? "<null>" : dur )+
            ", timestamp: "           + (timestamp == null ? "<null>" : timestamp )+
            ", dow: "                 + (dow == null ? "<null>" : dow )+
            ", hour: "                + (hour == null ? "<null>" : hour ) +
            "," + seg;
    }

    public byte[] getBytes() {

        ByteBuffer segByteBuffer = SerializationHelper.serializeMap(segmentation);

        byte[] level_success_count_bytes = level_success_count == null ? null : level_success_count.getBytes();
        byte[] level_fail_count_bytes = level_fail_count == null ? null : level_fail_count.getBytes();
        byte[] key_bytes = key == null ? null : key.getBytes();
        byte[] count_bytes = count == null ? null : count.getBytes();
        byte[] sum_bytes = sum == null ? null : sum.getBytes();
        byte[] dur_bytes = dur == null ? null : dur.getBytes();
        byte[] timestamp_bytes = timestamp == null ? null : timestamp.getBytes();
        byte[] dow_bytes = dow == null ? null : dow.getBytes();
        byte[] hour_bytes = hour == null ? null : hour.getBytes();


        int size =
            (level_success_count_bytes == null ? 0 : level_success_count_bytes.length) + 4 +
            (level_fail_count_bytes == null ? 0 : level_fail_count_bytes.length) + 4 +
            (key_bytes == null ? 0 : key_bytes.length) + 4 +
            (count_bytes == null ? 0 : count_bytes.length) + 4 +
            (sum_bytes == null ? 0 : sum_bytes.length) + 4 +
            (dur_bytes == null ? 0 : dur_bytes.length) + 4 +
            (timestamp_bytes == null ? 0 : timestamp_bytes.length) + 4 +
            (dow_bytes == null ? 0 : dow_bytes.length) + 4 +
            (hour_bytes == null ? 0 : hour_bytes.length) + 4 +
            (segByteBuffer == null ? 0 : segByteBuffer.array().length ) + 4;

        ByteBuffer output =  ByteBuffer.allocate(size);

        SerializationHelper.put(output, level_success_count_bytes);
        SerializationHelper.put(output, level_fail_count_bytes);
        SerializationHelper.put(output, key_bytes);
        SerializationHelper.put(output, count_bytes);
        SerializationHelper.put(output, sum_bytes);
        SerializationHelper.put(output, dur_bytes);
        SerializationHelper.put(output, timestamp_bytes);
        SerializationHelper.put(output, dow_bytes);
        SerializationHelper.put(output, hour_bytes);
        if(segByteBuffer != null){
            output.putInt(segByteBuffer.array().length);
            output.put(segByteBuffer.array());
        }   else output.putInt(0);

        return output.array();

    }
    public void setFromBytes(byte[] serialized) {
        ByteBuffer input = ByteBuffer.wrap(serialized);
        level_success_count = SerializationHelper.getString(input);
        level_fail_count = SerializationHelper.getString(input);
        key = SerializationHelper.getString(input);
        count = SerializationHelper.getString(input);
        sum = SerializationHelper.getString(input);
        dur = SerializationHelper.getString(input);
        timestamp = SerializationHelper.getString(input);
        dow = SerializationHelper.getString(input);
        hour = SerializationHelper.getString(input);
        int mapBytesSize = input.getInt();
        if(mapBytesSize > 0){
            byte[] map_bytes = new byte[mapBytesSize];
            input.get(map_bytes);
            ByteBuffer mapByteBuffer =  ByteBuffer.wrap(map_bytes);
            segmentation = SerializationHelper.getMap(mapByteBuffer);
        } else
            segmentation = null;

    }

}
