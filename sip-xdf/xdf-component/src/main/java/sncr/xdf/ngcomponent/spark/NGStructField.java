package sncr.xdf.ngcomponent.spark;

import org.apache.spark.sql.types.StructField;
import org.apache.spark.sql.types.DataType;
import org.apache.spark.sql.types.Metadata;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;

public class NGStructField extends StructField{

    protected int sourceColumnIndex = -1;
    protected String sourceFieldName = null;
    protected Object defaultValue = null;

    public NGStructField(String name, DataType dataType, boolean nullable, Metadata metadata, int sourceColumnIndex, String sourceFieldName, Object defaultValue){
        super(name, dataType, nullable, metadata);
        this.sourceColumnIndex = sourceColumnIndex;
        this.sourceFieldName = sourceFieldName;
        this.defaultValue = defaultValue;
    }

    public int getSourceColumnIndex() {
        return sourceColumnIndex;
    }

    public void setSourceColumnIndex(int sourceColumnIndex) {
        this.sourceColumnIndex = sourceColumnIndex;
    }
    public String getSourceFieldName() {
        return sourceFieldName;
    }

    public void setSourceFieldName(String sourceFieldName) {
        this.sourceFieldName = sourceFieldName;
    }

    public Object getDefaultValue() {
        return defaultValue;
    }

    public void setDefaultValue(Object defaultValue) {
        this.defaultValue = defaultValue;
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }

    @Override
    public int hashCode() {

        return new HashCodeBuilder()
            .append(name())
            .append(dataType())
            .append(nullable())
            .append(metadata())
            .append(sourceColumnIndex)
            .append(sourceFieldName)
            .append(defaultValue)
            .toHashCode();
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }
        if ((other instanceof NGStructField) == false) {
            return false;
        }
        NGStructField rhs = ((NGStructField) other);
        return new EqualsBuilder()
            .append(name(), rhs.name())
            .append(dataType(), rhs.dataType())
            .append(nullable(), rhs.nullable())
            .append(metadata(), rhs.metadata())
            .append(sourceColumnIndex, rhs.sourceColumnIndex)
            .append(sourceFieldName, rhs.sourceFieldName)
            .append(defaultValue, rhs.defaultValue)
            .isEquals();
    }
}
