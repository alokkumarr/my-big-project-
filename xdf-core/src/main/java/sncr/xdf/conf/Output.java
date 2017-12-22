
package sncr.xdf.conf;

import java.util.HashMap;
import java.util.Map;
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
     * (Required)
     * 
     */
    @SerializedName("name")
    @Expose
    private String name;
    /**
     * Data object name
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
    private sncr.xdf.conf.Input.Format format = sncr.xdf.conf.Input.Format.fromValue("parquet");
    /**
     * mode of the partition
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
     * mode of the partition
     * 
     */
    @SerializedName("dataSource")
    @Expose
    private DataSource dataSource = null;
    /**
     * Location in selected container
     * 
     */
    @SerializedName("catalog")
    @Expose
    private String catalog = "data";
    @SerializedName("metadata")
    @Expose
    private Metadata metadata;

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
     * @param metadata
     * @param catalog
     * @param name
     * @param format
     * @param dataSet
     * @param dataSource
     */
    public Output(String name, String dataSet, sncr.xdf.conf.Input.Format format, Output.Mode mode, Integer numberOfFiles, DataSource dataSource, String catalog, Metadata metadata) {
        this.name = name;
        this.dataSet = dataSet;
        this.format = format;
        this.mode = mode;
        this.numberOfFiles = numberOfFiles;
        this.dataSource = dataSource;
        this.catalog = catalog;
        this.metadata = metadata;
    }

    /**
     * Parameter name
     * (Required)
     * 
     * @return
     *     The name
     */
    public String getName() {
        return name;
    }

    /**
     * Parameter name
     * (Required)
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
     * 
     * @return
     *     The dataSet
     */
    public String getDataSet() {
        return dataSet;
    }

    /**
     * Data object name
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
    public sncr.xdf.conf.Input.Format getFormat() {
        return format;
    }

    /**
     * Data format
     * 
     * @param format
     *     The format
     */
    public void setFormat(sncr.xdf.conf.Input.Format format) {
        this.format = format;
    }

    public Output withFormat(sncr.xdf.conf.Input.Format format) {
        this.format = format;
        return this;
    }

    /**
     * mode of the partition
     * 
     * @return
     *     The mode
     */
    public Output.Mode getMode() {
        return mode;
    }

    /**
     * mode of the partition
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
     * mode of the partition
     * 
     * @return
     *     The dataSource
     */
    public DataSource getDataSource() {
        return dataSource;
    }

    /**
     * mode of the partition
     * 
     * @param dataSource
     *     The dataSource
     */
    public void setDataSource(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public Output withDataSource(DataSource dataSource) {
        this.dataSource = dataSource;
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
     *     The metadata
     */
    public Metadata getMetadata() {
        return metadata;
    }

    /**
     * 
     * @param metadata
     *     The metadata
     */
    public void setMetadata(Metadata metadata) {
        this.metadata = metadata;
    }

    public Output withMetadata(Metadata metadata) {
        this.metadata = metadata;
        return this;
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder().append(name).append(dataSet).append(format).append(mode).append(numberOfFiles).append(dataSource).append(catalog).append(metadata).toHashCode();
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
        return new EqualsBuilder().append(name, rhs.name).append(dataSet, rhs.dataSet).append(format, rhs.format).append(mode, rhs.mode).append(numberOfFiles, rhs.numberOfFiles).append(dataSource, rhs.dataSource).append(catalog, rhs.catalog).append(metadata, rhs.metadata).isEquals();
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
