
package sncr.bda.conf;

import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import javax.annotation.Generated;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;

@Generated("org.jsonschema2pojo")
public class Output {

    /**
     * Parameter name
     * 
     */
    @SerializedName("name")
    @Expose
    private String name;
    /**
     * Data object name
     * (Required)
     * 
     */
    @SerializedName("dataSet")
    @Expose
    private String dataSet;
    /**
     * Data format
     * 
     */
    @SerializedName("format")
    @Expose
    private sncr.bda.conf.Input.Format format = sncr.bda.conf.Input.Format.fromValue("parquet");
    /**
     * mode of the partition
     * (Required)
     * 
     */
    @SerializedName("mode")
    @Expose
    private Output.Mode mode = Output.Mode.fromValue("replace");
    /**
     * Number of files for all output objects
     * 
     */
    @SerializedName("numberOfFiles")
    @Expose
    private Integer numberOfFiles = 1;
    /**
     * Type of data source
     * (Required)
     * 
     */
    @SerializedName("dstype")
    @Expose
    private sncr.bda.conf.Input.Dstype dstype = sncr.bda.conf.Input.Dstype.fromValue("base");
    /**
     * Partitioning keys
     * 
     */
    @SerializedName("partitionKeys")
    @Expose
    private Set<String> partitionKeys = new LinkedHashSet<String>();
    /**
     * Location in selected container
     * 
     */
    @SerializedName("catalog")
    @Expose
    private String catalog = "data";
    @SerializedName("parent")
    @Expose
    private String parent;
    @SerializedName("userdata")
    @Expose
    private Object userdata;

    /**
     * No args constructor for use in serialization
     * 
     */
    public Output() {
    }

    /**
     * 
     * @param mode
     * @param numberOfFiles
     * @param parent
     * @param userdata
     * @param dstype
     * @param catalog
     * @param name
     * @param format
     * @param partitionKeys
     * @param dataSet
     */
    public Output(String name, String dataSet, sncr.bda.conf.Input.Format format, Output.Mode mode, Integer numberOfFiles, sncr.bda.conf.Input.Dstype dstype, Set<String> partitionKeys, String catalog, String parent, Object userdata) {
        this.name = name;
        this.dataSet = dataSet;
        this.format = format;
        this.mode = mode;
        this.numberOfFiles = numberOfFiles;
        this.dstype = dstype;
        this.partitionKeys = partitionKeys;
        this.catalog = catalog;
        this.parent = parent;
        this.userdata = userdata;
    }

    /**
     * Parameter name
     * 
     * @return
     *     The name
     */
    public String getName() {
        return name;
    }

    /**
     * Parameter name
     * 
     * @param name
     *     The name
     */
    public void setName(String name) {
        this.name = name;
    }

    public Output withName(String name) {
        this.name = name;
        return this;
    }

    /**
     * Data object name
     * (Required)
     * 
     * @return
     *     The dataSet
     */
    public String getDataSet() {
        return dataSet;
    }

    /**
     * Data object name
     * (Required)
     * 
     * @param dataSet
     *     The dataSet
     */
    public void setDataSet(String dataSet) {
        this.dataSet = dataSet;
    }

    public Output withDataSet(String dataSet) {
        this.dataSet = dataSet;
        return this;
    }

    /**
     * Data format
     * 
     * @return
     *     The format
     */
    public sncr.bda.conf.Input.Format getFormat() {
        return format;
    }

    /**
     * Data format
     * 
     * @param format
     *     The format
     */
    public void setFormat(sncr.bda.conf.Input.Format format) {
        this.format = format;
    }

    public Output withFormat(sncr.bda.conf.Input.Format format) {
        this.format = format;
        return this;
    }

    /**
     * mode of the partition
     * (Required)
     * 
     * @return
     *     The mode
     */
    public Output.Mode getMode() {
        return mode;
    }

    /**
     * mode of the partition
     * (Required)
     * 
     * @param mode
     *     The mode
     */
    public void setMode(Output.Mode mode) {
        this.mode = mode;
    }

    public Output withMode(Output.Mode mode) {
        this.mode = mode;
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

    public Output withNumberOfFiles(Integer numberOfFiles) {
        this.numberOfFiles = numberOfFiles;
        return this;
    }

    /**
     * Type of data source
     * (Required)
     * 
     * @return
     *     The dstype
     */
    public sncr.bda.conf.Input.Dstype getDstype() {
        return dstype;
    }

    /**
     * Type of data source
     * (Required)
     * 
     * @param dstype
     *     The dstype
     */
    public void setDstype(sncr.bda.conf.Input.Dstype dstype) {
        this.dstype = dstype;
    }

    public Output withDstype(sncr.bda.conf.Input.Dstype dstype) {
        this.dstype = dstype;
        return this;
    }

    /**
     * Partitioning keys
     * 
     * @return
     *     The partitionKeys
     */
    public Set<String> getPartitionKeys() {
        return partitionKeys;
    }

    /**
     * Partitioning keys
     * 
     * @param partitionKeys
     *     The partitionKeys
     */
    public void setPartitionKeys(Set<String> partitionKeys) {
        this.partitionKeys = partitionKeys;
    }

    public Output withPartitionKeys(Set<String> partitionKeys) {
        this.partitionKeys = partitionKeys;
        return this;
    }

    /**
     * Location in selected container
     * 
     * @return
     *     The catalog
     */
    public String getCatalog() {
        return catalog;
    }

    /**
     * Location in selected container
     * 
     * @param catalog
     *     The catalog
     */
    public void setCatalog(String catalog) {
        this.catalog = catalog;
    }

    public Output withCatalog(String catalog) {
        this.catalog = catalog;
        return this;
    }

    /**
     * 
     * @return
     *     The parent
     */
    public String getParent() {
        return parent;
    }

    /**
     * 
     * @param parent
     *     The parent
     */
    public void setParent(String parent) {
        this.parent = parent;
    }

    public Output withParent(String parent) {
        this.parent = parent;
        return this;
    }

    /**
     * 
     * @return
     *     The userdata
     */
    public Object getUserdata() {
        return userdata;
    }

    /**
     * 
     * @param userdata
     *     The userdata
     */
    public void setUserdata(Object userdata) {
        this.userdata = userdata;
    }

    public Output withUserdata(Object userdata) {
        this.userdata = userdata;
        return this;
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder().append(name).append(dataSet).append(format).append(mode).append(numberOfFiles).append(dstype).append(partitionKeys).append(catalog).append(parent).append(userdata).toHashCode();
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }
        if ((other instanceof Output) == false) {
            return false;
        }
        Output rhs = ((Output) other);
        return new EqualsBuilder().append(name, rhs.name).append(dataSet, rhs.dataSet).append(format, rhs.format).append(mode, rhs.mode).append(numberOfFiles, rhs.numberOfFiles).append(dstype, rhs.dstype).append(partitionKeys, rhs.partitionKeys).append(catalog, rhs.catalog).append(parent, rhs.parent).append(userdata, rhs.userdata).isEquals();
    }

    @Generated("org.jsonschema2pojo")
    public static enum Mode {

        @SerializedName("replace")
        REPLACE("replace"),
        @SerializedName("append")
        APPEND("append");
        private final String value;
        private static Map<String, Output.Mode> constants = new HashMap<String, Output.Mode>();

        static {
            for (Output.Mode c: values()) {
                constants.put(c.value, c);
            }
        }

        private Mode(String value) {
            this.value = value;
        }

        @Override
        public String toString() {
            return this.value;
        }

        public static Output.Mode fromValue(String value) {
            Output.Mode constant = constants.get(value);
            if (constant == null) {
                throw new IllegalArgumentException(value);
            } else {
                return constant;
            }
        }

    }

}
