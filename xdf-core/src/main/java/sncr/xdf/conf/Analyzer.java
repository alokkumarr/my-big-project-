
package sncr.xdf.conf;

import java.util.ArrayList;
import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;


/**
 * Data profiler/analyzer configuration properties
 * 
 */
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
        super();
        this.esId = esId;
        this.fieldSeparator = fieldSeparator;
        this.header = header;
        this.timestampMasks = timestampMasks;
    }

    /**
     * Id of elastic search document containing analyzer metadata
     * 
     */
    public String getEsId() {
        return esId;
    }

    /**
     * Id of elastic search document containing analyzer metadata
     * 
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
     */
    public String getFieldSeparator() {
        return fieldSeparator;
    }

    /**
     * Field separator
     * 
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
     */
    public Boolean getHeader() {
        return header;
    }

    /**
     * Indicates if source data contains header
     * 
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
     */
    public List<String> getTimestampMasks() {
        return timestampMasks;
    }

    /**
     * Array of timestamp masks used to define fields as timestamps/dates
     * 
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
        return new ToStringBuilder(this).append("esId", esId).append("fieldSeparator", fieldSeparator).append("header", header).append("timestampMasks", timestampMasks).toString();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder().append(header).append(fieldSeparator).append(timestampMasks).append(esId).toHashCode();
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
        return new EqualsBuilder().append(header, rhs.header).append(fieldSeparator, rhs.fieldSeparator).append(timestampMasks, rhs.timestampMasks).append(esId, rhs.esId).isEquals();
    }

}
