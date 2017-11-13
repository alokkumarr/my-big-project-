
package sncr.xdf.conf;

import java.util.HashMap;
import java.util.Map;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;

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
    @SerializedName("fileMask")
    @Expose
    private String fileMask;
    /**
     * Data format
     * 
     */
    @SerializedName("format")
    @Expose
    private sncr.xdf.conf.Input.Format format = sncr.xdf.conf.Input.Format.fromValue(null);
    /**
     * mode of the partition
     * 
     */
    @SerializedName("mode")
    @Expose
    private Output.Mode mode = Output.Mode.fromValue(null);
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
     * @param fileMask
     * @param dataSource
     */
    public Output(String name, String dataSet, String fileMask, sncr.xdf.conf.Input.Format format, Output.Mode mode, Integer numberOfFiles, DataSource dataSource, String catalog, Metadata metadata) {
        super();
        this.name = name;
        this.dataSet = dataSet;
        this.fileMask = fileMask;
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
     */
    public String getName() {
        return name;
    }

    /**
     * Parameter name
     * (Required)
     * 
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
     */
    public String getDataSet() {
        return dataSet;
    }

    /**
     * Data object name
     * 
     */
    public void setDataSet(String dataSet) {
        this.dataSet = dataSet;
    }

    public Output withDataSet(String dataSet) {
        this.dataSet = dataSet;
        return this;
    }

    public String getFileMask() {
        return fileMask;
    }

    public void setFileMask(String fileMask) {
        this.fileMask = fileMask;
    }

    public Output withFileMask(String fileMask) {
        this.fileMask = fileMask;
        return this;
    }

    /**
     * Data format
     * 
     */
    public sncr.xdf.conf.Input.Format getFormat() {
        return format;
    }

    /**
     * Data format
     * 
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
     */
    public Output.Mode getMode() {
        return mode;
    }

    /**
     * mode of the partition
     * 
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

    public Output withNumberOfFiles(Integer numberOfFiles) {
        this.numberOfFiles = numberOfFiles;
        return this;
    }

    /**
     * mode of the partition
     * 
     */
    public DataSource getDataSource() {
        return dataSource;
    }

    /**
     * mode of the partition
     * 
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
     */
    public String getCatalog() {
        return catalog;
    }

    /**
     * Location in selected container
     * 
     */
    public void setCatalog(String catalog) {
        this.catalog = catalog;
    }

    public Output withCatalog(String catalog) {
        this.catalog = catalog;
        return this;
    }

    public Metadata getMetadata() {
        return metadata;
    }

    public void setMetadata(Metadata metadata) {
        this.metadata = metadata;
    }

    public Output withMetadata(Metadata metadata) {
        this.metadata = metadata;
        return this;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this).append("name", name).append("dataSet", dataSet).append("fileMask", fileMask).append("format", format).append("mode", mode).append("numberOfFiles", numberOfFiles).append("dataSource", dataSource).append("catalog", catalog).append("metadata", metadata).toString();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder().append(mode).append(numberOfFiles).append(metadata).append(catalog).append(name).append(format).append(dataSet).append(fileMask).append(dataSource).toHashCode();
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
        return new EqualsBuilder().append(mode, rhs.mode).append(numberOfFiles, rhs.numberOfFiles).append(metadata, rhs.metadata).append(catalog, rhs.catalog).append(name, rhs.name).append(format, rhs.format).append(dataSet, rhs.dataSet).append(fileMask, rhs.fileMask).append(dataSource, rhs.dataSource).isEquals();
    }

    public enum Mode {

        @SerializedName("replace")
        REPLACE(null),
        @SerializedName("append")
        APPEND(null);
        private final sncr.xdf.conf.Mode value;
        private final static Map<sncr.xdf.conf.Mode, Output.Mode> CONSTANTS = new HashMap<sncr.xdf.conf.Mode, Output.Mode>();

        static {
            for (Output.Mode c: values()) {
                CONSTANTS.put(c.value, c);
            }
        }

        private Mode(sncr.xdf.conf.Mode value) {
            this.value = value;
        }

        public sncr.xdf.conf.Mode value() {
            return this.value;
        }

        public static Output.Mode fromValue(sncr.xdf.conf.Mode value) {
            Output.Mode constant = CONSTANTS.get(value);
            if (constant == null) {
                throw new IllegalArgumentException((value +""));
            } else {
                return constant;
            }
        }

    }

}
