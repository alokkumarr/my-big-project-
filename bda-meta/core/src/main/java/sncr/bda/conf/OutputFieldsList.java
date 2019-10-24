package sncr.bda.conf;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;
import javax.annotation.Generated;

@Generated("org.jsonschema2pojo")
public class OutputFieldsList {

    @SerializedName("name")
    @Expose
    private String name;
    @SerializedName("format")
    @Expose
    private String format;
    @SerializedName("destinationName")
    @Expose
    private String destinationName;

    /**
     * No args constructor for use in serialization
     *
     */
    public OutputFieldsList() {
    }

    /**
     *
     * @param name
     * @param format
     * @param type
     */
    public OutputFieldsList(String name, String format, String destinationName) {
        this.name = name;
        this.format = format;
        this.destinationName = destinationName;
    }

    /**
     *
     * @return
     *     The name
     */
    public String getName() {
        return name;
    }

    /**
     *
     * @param name
     *     The name
     */
    public void setName(String name) {
        this.name = name;
    }

    public OutputFieldsList withName(String name) {
        this.name = name;
        return this;
    }

    /**
     *
     * @return
     *     The format
     */
    public String getFormat() {
        return format;
    }

    /**
     *
     * @param format
     *     The format
     */
    public void setFormat(String format) {
        this.format = format;
    }

    public OutputFieldsList withFormat(String format) {
        this.format = format;
        return this;
    }

    /**
     *
     * @return
     *     The destinationName
     */
    public String getDestinationName() {
        return destinationName;
    }

    /**
     *
     * @param destinationName
     *     The destinationName
     */
    public void setDestinationName(String destinationName) {
        this.destinationName = destinationName;
    }

    public OutputFieldsList withDestinationName(String destinationName) {
        this.destinationName = destinationName;
        return this;
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder().append(name).append(format).toHashCode();
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }
        if ((other instanceof OutputFieldsList) == false) {
            return false;
        }
        OutputFieldsList rhs = ((OutputFieldsList) other);
        return new EqualsBuilder().append(name, rhs.name).append(format, rhs.format).isEquals();
    }

}
