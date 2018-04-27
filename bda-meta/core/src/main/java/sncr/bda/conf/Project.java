
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
public class Project {

    @SerializedName("name")
    @Expose
    private String name;
    @SerializedName("root")
    @Expose
    private String root;

    /**
     * No args constructor for use in serialization
     * 
     */
    public Project() {
    }

    /**
     * 
     * @param root
     * @param name
     */
    public Project(String name, String root) {
        this.name = name;
        this.root = root;
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

    public Project withName(String name) {
        this.name = name;
        return this;
    }

    /**
     * 
     * @return
     *     The root
     */
    public String getRoot() {
        return root;
    }

    /**
     * 
     * @param root
     *     The root
     */
    public void setRoot(String root) {
        this.root = root;
    }

    public Project withRoot(String root) {
        this.root = root;
        return this;
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder().append(name).append(root).toHashCode();
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }
        if ((other instanceof Project) == false) {
            return false;
        }
        Project rhs = ((Project) other);
        return new EqualsBuilder().append(name, rhs.name).append(root, rhs.root).isEquals();
    }

}
