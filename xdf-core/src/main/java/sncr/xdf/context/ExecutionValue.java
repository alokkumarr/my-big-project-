
package sncr.xdf.context;

import javax.annotation.Generated;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;

@Generated("org.jsonschema2pojo")
public class ExecutionValue {

    @SerializedName("target")
    @Expose
    private String target;
    @SerializedName("name")
    @Expose
    private String name;
    @SerializedName("value")
    @Expose
    private Object value;

    /**
     * No args constructor for use in serialization
     * 
     */
    public ExecutionValue() {
    }

    /**
     * 
     * @param name
     * @param value
     * @param target
     */
    public ExecutionValue(String target, String name, Object value) {
        this.target = target;
        this.name = name;
        this.value = value;
    }

    /**
     * 
     * @return
     *     The target
     */
    public String getTarget() {
        return target;
    }

    /**
     * 
     * @param target
     *     The target
     */
    public void setTarget(String target) {
        this.target = target;
    }

    public ExecutionValue withTarget(String target) {
        this.target = target;
        return this;
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

    public ExecutionValue withName(String name) {
        this.name = name;
        return this;
    }

    /**
     * 
     * @return
     *     The value
     */
    public Object getValue() {
        return value;
    }

    /**
     * 
     * @param value
     *     The value
     */
    public void setValue(Object value) {
        this.value = value;
    }

    public ExecutionValue withValue(Object value) {
        this.value = value;
        return this;
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder().append(target).append(name).append(value).toHashCode();
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }
        if ((other instanceof ExecutionValue) == false) {
            return false;
        }
        ExecutionValue rhs = ((ExecutionValue) other);
        return new EqualsBuilder().append(target, rhs.target).append(name, rhs.name).append(value, rhs.value).isEquals();
    }

}
