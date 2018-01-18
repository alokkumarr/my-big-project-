
package sncr.bda.conf;

import java.util.ArrayList;
import java.util.List;
import javax.annotation.Generated;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;


/**
 * Data profiler/analyzer configuration properties
 * 
 */
@Generated("org.jsonschema2pojo")
public class Analyzer {

    /**
     * Id of elastic search document containing analyzer metadata
     * 
     */
    @SerializedName("esId")
    @Expose
    private String esId;
    /**
     * Field separator
     * 
     */
    @SerializedName("fieldSeparator")
    @Expose
    private String fieldSeparator;
    /**
     * Indicates if source data contains header
     * 
     */
    @SerializedName("header")
    @Expose
    private Boolean header;
    /**
     * Array of timestamp masks used to define fields as timestamps/dates
     * 
     */
    @SerializedName("timestampMasks")
    @Expose
    private List<String> timestampMasks = new ArrayList<String>();

    /**
     * No args constructor for use in serialization
     * 
     */
    public Analyzer() {
    }

    /**
     * 
     * @param fieldSeparator
     * @param esId
     * @param header
     * @param timestampMasks
     */
    public Analyzer(String esId, String fieldSeparator, Boolean header, List<String> timestampMasks) {
        this.esId = esId;
        this.fieldSeparator = fieldSeparator;
        this.header = header;
        this.timestampMasks = timestampMasks;
    }

    /**
     * Id of elastic search document containing analyzer metadata
     * 
     * @return
     *     The esId
     */
    public String getEsId() {
        return esId;
    }

    /**
     * Id of elastic search document containing analyzer metadata
     * 
     * @param esId
     *     The esId
     */
    public void setEsId(String esId) {
        this.esId = esId;
    }

    public Analyzer withEsId(String esId) {
        this.esId = esId;
        return this;
    }

    /**
     * Field separator
     * 
     * @return
     *     The fieldSeparator
     */
    public String getFieldSeparator() {
        return fieldSeparator;
    }

    /**
     * Field separator
     * 
     * @param fieldSeparator
     *     The fieldSeparator
     */
    public void setFieldSeparator(String fieldSeparator) {
        this.fieldSeparator = fieldSeparator;
    }

    public Analyzer withFieldSeparator(String fieldSeparator) {
        this.fieldSeparator = fieldSeparator;
        return this;
    }

    /**
     * Indicates if source data contains header
     * 
     * @return
     *     The header
     */
    public Boolean getHeader() {
        return header;
    }

    /**
     * Indicates if source data contains header
     * 
     * @param header
     *     The header
     */
    public void setHeader(Boolean header) {
        this.header = header;
    }

    public Analyzer withHeader(Boolean header) {
        this.header = header;
        return this;
    }

    /**
     * Array of timestamp masks used to define fields as timestamps/dates
     * 
     * @return
     *     The timestampMasks
     */
    public List<String> getTimestampMasks() {
        return timestampMasks;
    }

    /**
     * Array of timestamp masks used to define fields as timestamps/dates
     * 
     * @param timestampMasks
     *     The timestampMasks
     */
    public void setTimestampMasks(List<String> timestampMasks) {
        this.timestampMasks = timestampMasks;
    }

    public Analyzer withTimestampMasks(List<String> timestampMasks) {
        this.timestampMasks = timestampMasks;
        return this;
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder().append(esId).append(fieldSeparator).append(header).append(timestampMasks).toHashCode();
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }
        if ((other instanceof Analyzer) == false) {
            return false;
        }
        Analyzer rhs = ((Analyzer) other);
        return new EqualsBuilder().append(esId, rhs.esId).append(fieldSeparator, rhs.fieldSeparator).append(header, rhs.header).append(timestampMasks, rhs.timestampMasks).isEquals();
    }

}
