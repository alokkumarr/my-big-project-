
package sncr.xdf.datasets.conf;

import javax.annotation.Generated;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;


/**
 * DataSet descriptor
 * <p>
 * Defines a data object
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
     * Category of a dataset
     * 
     */
    @SerializedName("category")
    @Expose
    private String category;
    /**
     * Sub category of a dataset
     * 
     */
    @SerializedName("subCategory")
    @Expose
    private String subCategory;
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
     * @param subCategory
     * @param createdBy
     * @param format
     * @param description
     * @param category
     * @param createdTs
     */
    public Dataset(String component, String createdTs, String createdBy, String format, String category, String subCategory, String description) {
        this.component = component;
        this.createdTs = createdTs;
        this.createdBy = createdBy;
        this.format = format;
        this.category = category;
        this.subCategory = subCategory;
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
     * Category of a dataset
     * 
     * @return
     *     The category
     */
    public String getCategory() {
        return category;
    }

    /**
     * Category of a dataset
     * 
     * @param category
     *     The category
     */
    public void setCategory(String category) {
        this.category = category;
    }

    public Dataset withCategory(String category) {
        this.category = category;
        return this;
    }

    /**
     * Sub category of a dataset
     * 
     * @return
     *     The subCategory
     */
    public String getSubCategory() {
        return subCategory;
    }

    /**
     * Sub category of a dataset
     * 
     * @param subCategory
     *     The subCategory
     */
    public void setSubCategory(String subCategory) {
        this.subCategory = subCategory;
    }

    public Dataset withSubCategory(String subCategory) {
        this.subCategory = subCategory;
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
        return new HashCodeBuilder().append(component).append(createdTs).append(createdBy).append(format).append(category).append(subCategory).append(description).toHashCode();
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
        return new EqualsBuilder().append(component, rhs.component).append(createdTs, rhs.createdTs).append(createdBy, rhs.createdBy).append(format, rhs.format).append(category, rhs.category).append(subCategory, rhs.subCategory).append(description, rhs.description).isEquals();
    }

}
