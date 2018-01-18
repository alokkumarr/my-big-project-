
package sncr.bda.conf;

import javax.annotation.Generated;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;

@Generated("org.jsonschema2pojo")
public class Metadata {

    /**
     * Parameter name
     * 
     */
    @SerializedName("createdBy")
    @Expose
    private String createdBy = "nobody";
    /**
     * Description of the data set
     * 
     */
    @SerializedName("description")
    @Expose
    private String description = "__none__";
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
     * No args constructor for use in serialization
     * 
     */
    public Metadata() {
    }

    /**
     * 
     * @param subCategory
     * @param createdBy
     * @param description
     * @param category
     */
    public Metadata(String createdBy, String description, String category, String subCategory) {
        this.createdBy = createdBy;
        this.description = description;
        this.category = category;
        this.subCategory = subCategory;
    }

    /**
     * Parameter name
     * 
     * @return
     *     The createdBy
     */
    public String getCreatedBy() {
        return createdBy;
    }

    /**
     * Parameter name
     * 
     * @param createdBy
     *     The createdBy
     */
    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    public Metadata withCreatedBy(String createdBy) {
        this.createdBy = createdBy;
        return this;
    }

    /**
     * Description of the data set
     * 
     * @return
     *     The description
     */
    public String getDescription() {
        return description;
    }

    /**
     * Description of the data set
     * 
     * @param description
     *     The description
     */
    public void setDescription(String description) {
        this.description = description;
    }

    public Metadata withDescription(String description) {
        this.description = description;
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

    public Metadata withCategory(String category) {
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

    public Metadata withSubCategory(String subCategory) {
        this.subCategory = subCategory;
        return this;
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder().append(createdBy).append(description).append(category).append(subCategory).toHashCode();
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
        return new EqualsBuilder().append(createdBy, rhs.createdBy).append(description, rhs.description).append(category, rhs.category).append(subCategory, rhs.subCategory).isEquals();
    }

}
