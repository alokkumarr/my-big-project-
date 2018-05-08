package synchronoss.data.countly.model;


import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.nio.ByteBuffer;
import java.util.IllegalFormatException;

/**
 * Created by srya0001 on 4/28/2016.
 */
@ApiModel(value = "Event Data", description = "Class composes several parts of event to store all flavors of incoming events")
public class Event extends  BaseEvent{

    public static byte Signature = 'E';

    @ApiModelProperty( value = "metrics",
            name = "Device Metrics",
            notes = "(optional, can only be used with begin_session) JSON object as string to provide metrics to track with the user",
            access = "public",
            dataType = "string")
    public Metrics      metrics;

    @ApiModelProperty( value = "events",
            name = "Event Data",
            notes = "(optional) JSON array as string containing event objects",
            access = "public",
            dataType = "string")
    public EventData    eventData;

    @ApiModelProperty( value = "user_details",
            name = "User Details",
            notes = "(optional) JSON object as string containing information about users",
            access = "public",
            dataType = "string")
    public UserDetails  userDetails;

    public Event() {

    }

    @Override
    public String toString() {
        return    "\nBase Event:\n" + super.toString()
                + "\nMetrics: \n" + (metrics == null ? "<no metrics data>" : metrics.toString())
                + "\nEvent: \n" + (eventData == null ? "<no event data>" : eventData.toString())
                + "\nUser data: \n" + (userDetails == null ? "<no user details>" : userDetails.toString());
    }

    public Event(byte[] serialized){
        this.setFromBytes(serialized);
    }

    @Override
    public byte[] getBytes() {
        byte[] base_bytes = super.getBytes();
        byte[] metric_bytes = metrics == null ? null : metrics.getBytes();
        byte[] event_bytes = eventData == null ? null : eventData.getBytes();
        byte[] user_bytes = userDetails == null ? null : userDetails.getBytes();

        int size = 1  // Size of signature
                + base_bytes.length + 4
                + (metric_bytes == null ? 0 : metric_bytes.length) + 4
                + (event_bytes == null ? 0 : event_bytes.length) + 4
                + (user_bytes == null ? 0 : user_bytes.length) + 4;

        ByteBuffer output = ByteBuffer.allocate(size);
        output.put(Signature);
        SerializationHelper.put(output, base_bytes);
        SerializationHelper.put(output, metric_bytes);
        SerializationHelper.put(output, event_bytes);
        SerializationHelper.put(output, user_bytes);

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

        int baseSize = input.getInt();
        byte[] base_bytes = new byte[baseSize];
        input.get(base_bytes);
        super.setFromBytes(base_bytes);

        int metricSize = input.getInt();
        if(metricSize > 0 ) {
            byte[] metrics_bytes = new byte[metricSize];
            input.get(metrics_bytes);
            this.metrics = new Metrics(metrics_bytes);
        } else
            this.metrics = null;

        int eventSize = input.getInt();
        if(eventSize > 0){
            byte[] event_bytes = new byte[eventSize];
            input.get(event_bytes);
            this.eventData = new EventData(event_bytes);
        } else
            this.eventData = null;

        int userDetailsSize = input.getInt();
        if(userDetailsSize > 0){
            byte[] user_bytes = new byte[userDetailsSize];
            input.get(user_bytes );
            this.userDetails = new UserDetails(user_bytes );
        } else
            this.userDetails = null;
    }

    public static boolean canDeserialize(byte[] serialized){
        return serialized[0] == Signature;
    }
}
