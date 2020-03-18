package sncr.xdf.ngcomponent.spark;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.spark.sql.types.StructType;

import java.io.Serializable;

public class NGStructType implements Serializable {

    StructType structType = null;
    NGStructField[] ngFields = null;
    boolean isSkipFieldsEnabled = false;

    public NGStructType(){
        structType = new StructType();
    }

    public NGStructType(NGStructField[] fields, boolean isSchemaIndexBased){
        structType = new StructType(fields);
        ngFields = fields;
        this.isSkipFieldsEnabled = isSkipFieldsEnabled;
    }

    public StructType getStructType() {
        return structType;
    }

    public void setStructType(StructType structType) {
        this.structType = structType;
    }

    public NGStructField[] getNgFields() {
        return ngFields;
    }

    public void setNgFields(NGStructField[] ngFields) {
        this.ngFields = ngFields;
    }

    public boolean isSkipFieldsEnabled() {
        return isSkipFieldsEnabled;
    }

    public void setSkipFieldsEnabled(boolean isSkipFieldsEnabled) {
        this.isSkipFieldsEnabled = isSkipFieldsEnabled;
    }

    public NGStructType add(NGStructField field){
        if(field != null){
            structType.add(field);
            if(ngFields == null){
                ngFields = new NGStructField[1];
                ngFields[0] = field;
            }else{
                ngFields = ArrayUtils.add(ngFields, field);
            }
        }
        return this;
    }

    public int length(){
        return structType.length();
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder().append(structType).append(ngFields).append(isSkipFieldsEnabled).toHashCode();
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }
        if ((other instanceof NGStructType) == false) {
            return false;
        }
        NGStructType rhs = ((NGStructType) other);
        return new EqualsBuilder()
            .append(structType, rhs.structType)
            .append(ngFields, rhs.ngFields)
            .append(isSkipFieldsEnabled, rhs.isSkipFieldsEnabled)
            .isEquals();
    }
}
