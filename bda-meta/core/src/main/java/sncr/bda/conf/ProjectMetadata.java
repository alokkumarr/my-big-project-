package sncr.bda.conf;

import javax.annotation.Generated;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;

/**
 * XDF application section
 *
 */
@Generated("org.jsonschema2pojo")
public class ProjectMetadata {

    @SerializedName("_id")
    @Expose
    private String projectId;

    @SerializedName("description")
    @Expose
    private String description;

    @SerializedName("allowableTags")
    @Expose
    private String[] allowableTags;

    @SerializedName("projectLevelParameters")
    @Expose
    private Object[] projectLevelParameters;

    public String getProjectId() {
        return projectId;
    }

    public void setProjectId(String projectId) {
        this.projectId = projectId;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String[] getAllowableTags() {
        return allowableTags;
    }

    public void setAllowableTags(String[] allowableTags) {
        this.allowableTags = allowableTags;
    }

    public Object[] getProjectLevelParameters() {
        return projectLevelParameters;
    }

    public void setProjectLevelParameters(Object[] projectLevelParameters) {
        this.projectLevelParameters = projectLevelParameters;
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder()
            .append(projectId)
            .append(description)
            .append(allowableTags)
            .append(projectLevelParameters)
            .toHashCode();
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }
        if ((other instanceof ProjectMetadata) == false) {
            return false;
        }
        ProjectMetadata rhs = ((ProjectMetadata) other);
        return new EqualsBuilder()
            .append(projectId, rhs.projectId)
            .append(description, rhs.description)
            .append(allowableTags, rhs.allowableTags)
            .append(projectLevelParameters, rhs.projectLevelParameters)
            .isEquals();
    }


}
