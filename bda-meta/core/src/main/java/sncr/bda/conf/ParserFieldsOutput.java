package sncr.bda.conf;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;
import javax.annotation.Generated;

@Generated("org.jsonschema2pojo")
public class ParserFieldsOutput {

    @SerializedName("name")
    @Expose
    private String name;
    @SerializedName("type")
    @Expose
    private String type;
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
    public ParserFieldsOutput() {
    }

    /**
     *
     * @param name
     * @param format
     * @param type
     */
    public ParserFieldsOutput(String name, String type, String format, String destinationName) {
        this.name = name;
        this.type = type;
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

    public ParserFieldsOutput withName(String name) {
        this.name = name;
        return this;
    }

    /**
     *
     * @return
     *     The type
     */
    public String getType() {
        return type;
    }

    /**
     *
     * @param type
     *     The type
     */
    public void setType(String type) {
        this.type = type;
    }

    public ParserFieldsOutput withType(String type) {
        this.type = type;
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

    public ParserFieldsOutput withFormat(String format) {
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

    public ParserFieldsOutput withDestinationName(String destinationName) {
        this.destinationName = destinationName;
        return this;
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder().append(name).append(type).append(format).toHashCode();
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }
        if ((other instanceof ParserFieldsOutput) == false) {
            return false;
        }
        ParserFieldsOutput rhs = ((ParserFieldsOutput) other);
        return new EqualsBuilder().append(name, rhs.name).append(type, rhs.type).append(format, rhs.format).isEquals();
    }

}
