
package sncr.bda.conf;

import javax.annotation.Generated;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;

@Generated("org.jsonschema2pojo")
public class PivotFields {

    @SerializedName("groupByColumns")
    @Expose
    private String[] groupByColumns;

    @SerializedName("pivotColumn")
    @Expose
    private String pivotColumn;

    @SerializedName("aggregateColumn")
    @Expose
    private String aggregateColumn;

    /**
     * No args constructor for use in serialization
     *
     */
    public PivotFields() {}

    /**
     *
     * @param name
     * @param format
     * @param type
     */
    public PivotFields(String[] groupByColumns, String pivotColumn, String pivotValueColumn, String aggregateFunction) {
        this.groupByColumns = groupByColumns;
        this.pivotColumn = pivotColumn;
        this.aggregateColumn = aggregateColumn;
    }

    public String[] getGroupByColumns() {
        return groupByColumns;
    }

    public void setGroupByColumns(String[] groupByColumns) {
        this.groupByColumns = groupByColumns;
    }

    public String getPivotColumn() {
        return pivotColumn;
    }

    public void setPivotColumn(String pivotColumn) {
        this.pivotColumn = pivotColumn;
    }

    public String getAggregateColumn() {
        return aggregateColumn;
    }

    public void setAggregateColumn(String aggregateColumn) {
        this.aggregateColumn = aggregateColumn;
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }
        if ((other instanceof Field) == false) {
            return false;
        }
        PivotFields rhs = ((PivotFields) other);
        return new EqualsBuilder()
            .append(groupByColumns, rhs.groupByColumns)
            .append(pivotColumn, rhs.pivotColumn)
            .append(aggregateColumn, rhs.aggregateColumn)
            .isEquals();
    }
}
