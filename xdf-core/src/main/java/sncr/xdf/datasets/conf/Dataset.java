
package sncr.xdf.datasets.conf;

import java.util.ArrayList;
import java.util.List;
import javax.annotation.Generated;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;


/**
 * DataSet descriptor
 * <p>
 * Defines processMap data object
 * 
 */
@Generated("org.jsonschema2pojo")
public class Dataset {

    /**
     * Component name creating the JSON file
     * (Required)
     * 
     */
    @SerializedName("component")
    @Expose
    private String component;
    /**
     * DataSet creation TS
     * (Required)
     * 
     */
    @SerializedName("createdTs")
    @Expose
    private String createdTs = "";
    /**
     * User-creator
     * (Required)
     * 
     */
    @SerializedName("createdBy")
    @Expose
    private String createdBy = "";
    /**
     * DataSet type
     * (Required)
     * 
     */
    @SerializedName("format")
    @Expose
    private String format = "parquet";
    /**
     * Tag to label data objects
     * 
     */
    @SerializedName("tags")
    @Expose
    private List<String> tags = new ArrayList<String>();
    /**
     * Description of the data object
     * (Required)
     * 
     */
    @SerializedName("description")
    @Expose
    private String description = "__none__";

    /**
     * No args constructor for use in serialization
     * 
     */
    public Dataset() {
    }

    /**
     * 
     * @param component
     * @param createdBy
     * @param format
     * @param description
     * @param createdTs
     * @param tags
     */
    public Dataset(String component, String createdTs, String createdBy, String format, List<String> tags, String description) {
        this.component = component;
        this.createdTs = createdTs;
        this.createdBy = createdBy;
        this.format = format;
        this.tags = tags;
        this.description = description;
    }

    /**
     * Component name creating the JSON file
     * (Required)
     * 
     * @return
     *     The component
     */
    public String getComponent() {
        return component;
    }

    /**
     * Component name creating the JSON file
     * (Required)
     * 
     * @param component
     *     The component
     */
    public void setComponent(String component) {
        this.component = component;
    }

    public Dataset withComponent(String component) {
        this.component = component;
        return this;
    }

    /**
     * DataSet creation TS
     * (Required)
     * 
     * @return
     *     The createdTs
     */
    public String getCreatedTs() {
        return createdTs;
    }

    /**
     * DataSet creation TS
     * (Required)
     * 
     * @param createdTs
     *     The createdTs
     */
    public void setCreatedTs(String createdTs) {
        this.createdTs = createdTs;
    }

    public Dataset withCreatedTs(String createdTs) {
        this.createdTs = createdTs;
        return this;
    }

    /**
     * User-creator
     * (Required)
     * 
     * @return
     *     The createdBy
     */
    public String getCreatedBy() {
        return createdBy;
    }

    /**
     * User-creator
     * (Required)
     * 
     * @param createdBy
     *     The createdBy
     */
    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    public Dataset withCreatedBy(String createdBy) {
        this.createdBy = createdBy;
        return this;
    }

    /**
     * DataSet type
     * (Required)
     * 
     * @return
     *     The format
     */
    public String getFormat() {
        return format;
    }

    /**
     * DataSet type
     * (Required)
     * 
     * @param format
     *     The format
     */
    public void setFormat(String format) {
        this.format = format;
    }

    public Dataset withFormat(String format) {
        this.format = format;
        return this;
    }

    /**
     * Tag to label data objects
     * 
     * @return
     *     The tags
     */
    public List<String> getTags() {
        return tags;
    }

    /**
     * Tag to label data objects
     * 
     * @param tags
     *     The tags
     */
    public void setTags(List<String> tags) {
        this.tags = tags;
    }

    public Dataset withTags(List<String> tags) {
        this.tags = tags;
        return this;
    }

    /**
     * Description of the data object
     * (Required)
     * 
     * @return
     *     The description
     */
    public String getDescription() {
        return description;
    }

    /**
     * Description of the data object
     * (Required)
     * 
     * @param description
     *     The description
     */
    public void setDescription(String description) {
        this.description = description;
    }

    public Dataset withDescription(String description) {
        this.description = description;
        return this;
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder().append(component).append(createdTs).append(createdBy).append(format).append(tags).append(description).toHashCode();
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }
        if ((other instanceof Dataset) == false) {
            return false;
        }
        Dataset rhs = ((Dataset) other);
        return new EqualsBuilder().append(component, rhs.component).append(createdTs, rhs.createdTs).append(createdBy, rhs.createdBy).append(format, rhs.format).append(tags, rhs.tags).append(description, rhs.description).isEquals();
    }

}
