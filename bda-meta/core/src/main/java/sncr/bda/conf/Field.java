
package sncr.bda.conf;

import javax.annotation.Generated;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;

@Generated("org.jsonschema2pojo")
public class Field {

    @SerializedName("name")
    @Expose
    private String name;
    @SerializedName("type")
    @Expose
    private String type;
    @SerializedName("format")
    @Expose
    private String format;

    @SerializedName("model")
    @Expose
    private String model;
    @SerializedName("definition.file")
    @Expose
    private String definitionFile;

    @SerializedName("sourceIndex")
    @Expose
    private Integer sourceIndex;

    @SerializedName("sourceFieldName")
    @Expose
    private String sourceFieldName;

    @SerializedName("defaultValue")
    @Expose
    private String defaultValue;

    @SerializedName("isFlatteningEnabled")
    @Expose
    private boolean isFlatteningEnabled = false;

    /**
     * No args constructor for use in serialization
     *
     */
    public Field() {
    }

    /**
     *
     * @param name
     * @param type
     * @param format
     * @param model
     * @param definitionFile
     * @param sourceIndex
     * @param sourceFieldName
     * @param defaultValue
     */
    public Field(String name,
                 String type,
                 String format,
                 String model,
                 String definitionFile,
                 Integer sourceIndex,
                 String sourceFieldName,
                 String defaultValue,
                 boolean isFlatteningEnabled) {
        this.name = name;
        this.type = type;
        this.format = format;
        this.model = model;
        this.definitionFile = definitionFile;
        this.sourceIndex = sourceIndex;
        this.sourceFieldName = sourceFieldName;
        this.defaultValue = defaultValue;
        this.isFlatteningEnabled = isFlatteningEnabled;
    }

    /**
     *
     * @return The name
     */
    public String getName() {
        return name;
    }

    /**
     *
     * @param name The name
     */
    public void setName(String name) {
        this.name = name;
    }

    public Field withName(String name) {
        this.name = name;
        return this;
    }

    /**
     *
     * @return The type
     */
    public String getType() {
        return type;
    }

    /**
     *
     * @param type The type
     */
    public void setType(String type) {
        this.type = type;
    }

    public Field withType(String type) {
        this.type = type;
        return this;
    }

    /**
     *
     * @return The format
     */
    public String getFormat() {
        return format;
    }

    /**
     *
     * @param format The format
     */
    public void setFormat(String format) {
        this.format = format;
    }

    public Field withFormat(String format) {
        this.format = format;
        return this;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public Field withModel(String model) {
        this.model = model;
        return this;
    }

    public String getDefinitionFile() {
        return definitionFile;
    }

    public void setDefinitionFile(String definitionFile) {
        this.definitionFile = definitionFile;
    }

    public Field withDefinitionFile(String definitionFile) {
        this.definitionFile = definitionFile;
        return this;
    }

    public Integer getSourceIndex() {
        return sourceIndex;
    }

    public void setSourceIndex(Integer sourceIndex) {
        this.sourceIndex = sourceIndex;
    }

    public Field withSourceIndex(Integer sourceIndex) {
        this.sourceIndex = sourceIndex;
        return this;
    }

    public String getSourceFieldName() {
        return sourceFieldName;
    }

    public void setSourceFieldName(String sourceFieldName) {
        this.sourceFieldName = sourceFieldName;
    }

    public Field withSourceFieldName(String sourceFieldName) {
        this.sourceFieldName = sourceFieldName;
        return this;
    }

    public String getDefaultValue() {
        return defaultValue;
    }

    public void setDefaultValue(String defaultValue) {
        this.defaultValue = defaultValue;
    }

    public Field withDefaultValue(String defaultValue) {
        this.defaultValue = defaultValue;
        return this;
    }

    public boolean isFlatteningEnabled() {
        return isFlatteningEnabled;
    }

    public void setFlatteningEnabled(boolean isFlatteningEnabled) {
        this.isFlatteningEnabled = isFlatteningEnabled;
    }

    public Field withFlatteningEnabled(boolean isFlatteningEnabled) {
        this.isFlatteningEnabled = isFlatteningEnabled;
        return this;
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }

    @Override
    public int hashCode() {

        return new HashCodeBuilder()
            .append(name)
            .append(type)
            .append(format)
            .append(model)
            .append(definitionFile)
            .append(sourceIndex)
            .append(sourceFieldName)
            .append(defaultValue)
            .append(isFlatteningEnabled)
            .toHashCode();
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }
        if ((other instanceof Field) == false) {
            return false;
        }
        Field rhs = ((Field) other);
        return new EqualsBuilder()
            .append(name, rhs.name)
            .append(type, rhs.type)
            .append(format, rhs.format)
            .append(model, rhs.model)
            .append(definitionFile, rhs.definitionFile)
            .append(sourceIndex, rhs.sourceIndex)
            .append(sourceFieldName, rhs.sourceFieldName)
            .append(defaultValue, rhs.defaultValue)
            .append(isFlatteningEnabled, rhs.isFlatteningEnabled)
            .isEquals();
    }
}
