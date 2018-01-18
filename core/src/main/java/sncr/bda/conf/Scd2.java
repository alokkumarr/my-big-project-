
package sncr.bda.conf;

import java.util.LinkedHashSet;
import java.util.Set;
import javax.annotation.Generated;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;


/**
 * SCD2 specific properties
 * 
 */
@Generated("org.jsonschema2pojo")
public class Scd2 {

    /**
     * List of key fields in data source to identify processed record(s)
     * 
     */
    @SerializedName("keys")
    @Expose
    private Set<String> keys = new LinkedHashSet<String>();
    /**
     * List of trackable fields
     * 
     */
    @SerializedName("trackableFields")
    @Expose
    private Set<String> trackableFields = new LinkedHashSet<String>();
    /**
     * Name of the field containing record version (must be comparable)
     * 
     */
    @SerializedName("versionField")
    @Expose
    private String versionField;
    /**
     * If version field is date/time the field must contain Java date/time format
     * 
     */
    @SerializedName("versionFieldFormat")
    @Expose
    private String versionFieldFormat;
    /**
     * Number of files for all output objects
     * 
     */
    @SerializedName("numberOfFiles")
    @Expose
    private Integer numberOfFiles = 1;

    /**
     * No args constructor for use in serialization
     * 
     */
    public Scd2() {
    }

    /**
     * 
     * @param numberOfFiles
     * @param keys
     * @param versionFieldFormat
     * @param trackableFields
     * @param versionField
     */
    public Scd2(Set<String> keys, Set<String> trackableFields, String versionField, String versionFieldFormat, Integer numberOfFiles) {
        this.keys = keys;
        this.trackableFields = trackableFields;
        this.versionField = versionField;
        this.versionFieldFormat = versionFieldFormat;
        this.numberOfFiles = numberOfFiles;
    }

    /**
     * List of key fields in data source to identify processed record(s)
     * 
     * @return
     *     The keys
     */
    public Set<String> getKeys() {
        return keys;
    }

    /**
     * List of key fields in data source to identify processed record(s)
     * 
     * @param keys
     *     The keys
     */
    public void setKeys(Set<String> keys) {
        this.keys = keys;
    }

    public Scd2 withKeys(Set<String> keys) {
        this.keys = keys;
        return this;
    }

    /**
     * List of trackable fields
     * 
     * @return
     *     The trackableFields
     */
    public Set<String> getTrackableFields() {
        return trackableFields;
    }

    /**
     * List of trackable fields
     * 
     * @param trackableFields
     *     The trackableFields
     */
    public void setTrackableFields(Set<String> trackableFields) {
        this.trackableFields = trackableFields;
    }

    public Scd2 withTrackableFields(Set<String> trackableFields) {
        this.trackableFields = trackableFields;
        return this;
    }

    /**
     * Name of the field containing record version (must be comparable)
     * 
     * @return
     *     The versionField
     */
    public String getVersionField() {
        return versionField;
    }

    /**
     * Name of the field containing record version (must be comparable)
     * 
     * @param versionField
     *     The versionField
     */
    public void setVersionField(String versionField) {
        this.versionField = versionField;
    }

    public Scd2 withVersionField(String versionField) {
        this.versionField = versionField;
        return this;
    }

    /**
     * If version field is date/time the field must contain Java date/time format
     * 
     * @return
     *     The versionFieldFormat
     */
    public String getVersionFieldFormat() {
        return versionFieldFormat;
    }

    /**
     * If version field is date/time the field must contain Java date/time format
     * 
     * @param versionFieldFormat
     *     The versionFieldFormat
     */
    public void setVersionFieldFormat(String versionFieldFormat) {
        this.versionFieldFormat = versionFieldFormat;
    }

    public Scd2 withVersionFieldFormat(String versionFieldFormat) {
        this.versionFieldFormat = versionFieldFormat;
        return this;
    }

    /**
     * Number of files for all output objects
     * 
     * @return
     *     The numberOfFiles
     */
    public Integer getNumberOfFiles() {
        return numberOfFiles;
    }

    /**
     * Number of files for all output objects
     * 
     * @param numberOfFiles
     *     The numberOfFiles
     */
    public void setNumberOfFiles(Integer numberOfFiles) {
        this.numberOfFiles = numberOfFiles;
    }

    public Scd2 withNumberOfFiles(Integer numberOfFiles) {
        this.numberOfFiles = numberOfFiles;
        return this;
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder().append(keys).append(trackableFields).append(versionField).append(versionFieldFormat).append(numberOfFiles).toHashCode();
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }
        if ((other instanceof Scd2) == false) {
            return false;
        }
        Scd2 rhs = ((Scd2) other);
        return new EqualsBuilder().append(keys, rhs.keys).append(trackableFields, rhs.trackableFields).append(versionField, rhs.versionField).append(versionFieldFormat, rhs.versionFieldFormat).append(numberOfFiles, rhs.numberOfFiles).isEquals();
    }

}
