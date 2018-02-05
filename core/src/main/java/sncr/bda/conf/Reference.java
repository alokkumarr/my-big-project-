
package sncr.bda.conf;

import javax.annotation.Generated;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;

@Generated("org.jsonschema2pojo")
public class Reference {

    @SerializedName("dataSet")
    @Expose
    private String dataSet;
    @SerializedName("refKey")
    @Expose
    private String refKey;
    @SerializedName("dataKey")
    @Expose
    private String dataKey;

    /**
     * No args constructor for use in serialization
     * 
     */
    public Reference() {
    }

    /**
     * 
     * @param dataKey
     * @param refKey
     * @param dataSet
     */
    public Reference(String dataSet, String refKey, String dataKey) {
        this.dataSet = dataSet;
        this.refKey = refKey;
        this.dataKey = dataKey;
    }

    /**
     * 
     * @return
     *     The dataSet
     */
    public String getDataSet() {
        return dataSet;
    }

    /**
     * 
     * @param dataSet
     *     The dataSet
     */
    public void setDataSet(String dataSet) {
        this.dataSet = dataSet;
    }

    public Reference withDataSet(String dataSet) {
        this.dataSet = dataSet;
        return this;
    }

    /**
     * 
     * @return
     *     The refKey
     */
    public String getRefKey() {
        return refKey;
    }

    /**
     * 
     * @param refKey
     *     The refKey
     */
    public void setRefKey(String refKey) {
        this.refKey = refKey;
    }

    public Reference withRefKey(String refKey) {
        this.refKey = refKey;
        return this;
    }

    /**
     * 
     * @return
     *     The dataKey
     */
    public String getDataKey() {
        return dataKey;
    }

    /**
     * 
     * @param dataKey
     *     The dataKey
     */
    public void setDataKey(String dataKey) {
        this.dataKey = dataKey;
    }

    public Reference withDataKey(String dataKey) {
        this.dataKey = dataKey;
        return this;
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder().append(dataSet).append(refKey).append(dataKey).toHashCode();
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }
        if ((other instanceof Reference) == false) {
            return false;
        }
        Reference rhs = ((Reference) other);
        return new EqualsBuilder().append(dataSet, rhs.dataSet).append(refKey, rhs.refKey).append(dataKey, rhs.dataKey).isEquals();
    }

}
