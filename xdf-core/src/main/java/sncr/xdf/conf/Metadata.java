
package sncr.xdf.conf;

import java.util.ArrayList;
import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;

public class Metadata {

    /**
     * Parameter name
     * 
     */
    @SerializedName("createdBy")
    @Expose
    private String createdBy = "nobody";
    /**
     * Tag to label data objects
     * 
     */
    @SerializedName("tags")
    @Expose
    private List<String> tags = new ArrayList<String>();
    /**
     * Description of the data object
     * 
     */
    @SerializedName("description")
    @Expose
    private String description = "__none__";

    /**
     * No args constructor for use in serialization
     * 
     */
    public Metadata() {
    }

    /**
     * 
     * @param createdBy
     * @param description
     * @param tags
     */
    public Metadata(String createdBy, List<String> tags, String description) {
        super();
        this.createdBy = createdBy;
        this.tags = tags;
        this.description = description;
    }

    /**
     * Parameter name
     * 
     */
    public String getCreatedBy() {
        return createdBy;
    }

    /**
     * Parameter name
     * 
     */
    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    public Metadata withCreatedBy(String createdBy) {
        this.createdBy = createdBy;
        return this;
    }

    /**
     * Tag to label data objects
     * 
     */
    public List<String> getTags() {
        return tags;
    }

    /**
     * Tag to label data objects
     * 
     */
    public void setTags(List<String> tags) {
        this.tags = tags;
    }

    public Metadata withTags(List<String> tags) {
        this.tags = tags;
        return this;
    }

    /**
     * Description of the data object
     * 
     */
    public String getDescription() {
        return description;
    }

    /**
     * Description of the data object
     * 
     */
    public void setDescription(String description) {
        this.description = description;
    }

    public Metadata withDescription(String description) {
        this.description = description;
        return this;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this).append("createdBy", createdBy).append("tags", tags).append("description", description).toString();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder().append(description).append(createdBy).append(tags).toHashCode();
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }
        if ((other instanceof Metadata) == false) {
            return false;
        }
        Metadata rhs = ((Metadata) other);
        return new EqualsBuilder().append(description, rhs.description).append(createdBy, rhs.createdBy).append(tags, rhs.tags).isEquals();
    }

}
