
package sncr.xdf.conf;

import java.util.LinkedHashSet;
import java.util.Set;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;


/**
 * SCD2 specific properties
 * 
 */
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
        super();
        this.keys = keys;
        this.trackableFields = trackableFields;
        this.versionField = versionField;
        this.versionFieldFormat = versionFieldFormat;
        this.numberOfFiles = numberOfFiles;
    }

    /**
     * List of key fields in data source to identify processed record(s)
     * 
     */
    public Set<String> getKeys() {
        return keys;
    }

    /**
     * List of key fields in data source to identify processed record(s)
     * 
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
     */
    public Set<String> getTrackableFields() {
        return trackableFields;
    }

    /**
     * List of trackable fields
     * 
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
     */
    public String getVersionField() {
        return versionField;
    }

    /**
     * Name of the field containing record version (must be comparable)
     * 
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
     */
    public String getVersionFieldFormat() {
        return versionFieldFormat;
    }

    /**
     * If version field is date/time the field must contain Java date/time format
     * 
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
     */
    public Integer getNumberOfFiles() {
        return numberOfFiles;
    }

    /**
     * Number of files for all output objects
     * 
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
        return new ToStringBuilder(this).append("keys", keys).append("trackableFields", trackableFields).append("versionField", versionField).append("versionFieldFormat", versionFieldFormat).append("numberOfFiles", numberOfFiles).toString();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder().append(numberOfFiles).append(trackableFields).append(versionField).append(keys).append(versionFieldFormat).toHashCode();
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
        return new EqualsBuilder().append(numberOfFiles, rhs.numberOfFiles).append(trackableFields, rhs.trackableFields).append(versionField, rhs.versionField).append(keys, rhs.keys).append(versionFieldFormat, rhs.versionFieldFormat).isEquals();
    }

}
