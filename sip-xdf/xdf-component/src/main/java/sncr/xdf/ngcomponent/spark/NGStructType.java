package sncr.xdf.ngcomponent.spark;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.spark.sql.types.StructType;
import java.util.Arrays;
import java.util.List;


public class NGStructType{

    StructType structType = null;
    NGStructField[] ngFields = null;

    public NGStructType(){
        structType = new StructType();
    }

    public NGStructType(NGStructField[] fields){
        structType = new StructType(fields);
        if(fields != null && fields.length != 0){
            for(NGStructField field : fields){
                add(field);
            }
        }
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

    public NGStructType add(NGStructField field){
        if(field != null){
            structType.add(field);
            if(ngFields == null){
                ngFields = new NGStructField[1];
                ngFields[0] = field;
            }else{
                List<NGStructField> list = Arrays.asList(ngFields);
                list.add(field);
                ngFields = (NGStructField[]) list.toArray();
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
        return new HashCodeBuilder().append(structType).append(ngFields).toHashCode();
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
            .isEquals();
    }
}
