
package sncr.xdf.conf;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;

public class Field {

    @SerializedName("name")
    @Expose
    private String name;
    @SerializedName("type")
    @Expose
    private String type;
    @SerializedName("format")
    @Expose
    private String format;

    /**
     * No args constructor for use in serialization
     * 
     */
    public Field() {
    }

    /**
     * 
     * @param name
     * @param format
     * @param type
     */
    public Field(String name, String type, String format) {
        super();
        this.name = name;
        this.type = type;
        this.format = format;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Field withName(String name) {
        this.name = name;
        return this;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Field withType(String type) {
        this.type = type;
        return this;
    }

    public String getFormat() {
        return format;
    }

    public void setFormat(String format) {
        this.format = format;
    }

    public Field withFormat(String format) {
        this.format = format;
        return this;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this).append("name", name).append("type", type).append("format", format).toString();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder().append(name).append(format).append(type).toHashCode();
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }
        if ((other instanceof Field) == false) {
            return false;
        }
        Field rhs = ((Field) other);
        return new EqualsBuilder().append(name, rhs.name).append(format, rhs.format).append(type, rhs.type).isEquals();
    }

}
