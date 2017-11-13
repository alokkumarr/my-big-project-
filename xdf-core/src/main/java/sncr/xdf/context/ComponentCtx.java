
package sncr.xdf.context;

import java.util.ArrayList;
import java.util.List;
import javax.annotation.Generated;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;


/**
 * Session/Batch scope execution context
 * <p>
 * Defines the execution context of component
 * 
 */
@Generated("org.jsonschema2pojo")
public class ComponentCtx {

    /**
     * System parameters specific for component execution
     * 
     */
    @SerializedName("executionValue")
    @Expose
    private List<ExecutionValue> executionValue = new ArrayList<ExecutionValue>();

    /**
     * No args constructor for use in serialization
     * 
     */
    public ComponentCtx() {
    }

    /**
     * 
     * @param executionValue
     */
    public ComponentCtx(List<ExecutionValue> executionValue) {
        this.executionValue = executionValue;
    }

    /**
     * System parameters specific for component execution
     * 
     * @return
     *     The executionValue
     */
    public List<ExecutionValue> getExecutionValue() {
        return executionValue;
    }

    /**
     * System parameters specific for component execution
     * 
     * @param executionValue
     *     The executionValue
     */
    public void setExecutionValue(List<ExecutionValue> executionValue) {
        this.executionValue = executionValue;
    }

    public ComponentCtx withExecutionValue(List<ExecutionValue> executionValue) {
        this.executionValue = executionValue;
        return this;
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder().append(executionValue).toHashCode();
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }
        if ((other instanceof ComponentCtx) == false) {
            return false;
        }
        ComponentCtx rhs = ((ComponentCtx) other);
        return new EqualsBuilder().append(executionValue, rhs.executionValue).isEquals();
    }

}
