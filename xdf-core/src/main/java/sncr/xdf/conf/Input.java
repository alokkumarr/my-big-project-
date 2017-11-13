
package sncr.xdf.conf;

import java.util.HashMap;
import java.util.Map;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;

public class Input {

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
    private Input.Format format = Input.Format.fromValue(null);
    /**
     * Type of data source
     * 
     */
    @SerializedName("datasource")
    @Expose
    private String datasource = "fs";
    /**
     * Catalog in data source
     * 
     */
    @SerializedName("catalog")
    @Expose
    private String catalog = "data";
    /**
     * Project of input object
     * 
     */
    @SerializedName("project")
    @Expose
    private String project;

    /**
     * No args constructor for use in serialization
     * 
     */
    public Input() {
    }

    /**
     * 
     * @param datasource
     * @param catalog
     * @param name
     * @param format
     * @param project
     * @param dataSet
     * @param fileMask
     */
    public Input(String name, String dataSet, String fileMask, Input.Format format, String datasource, String catalog, String project) {
        super();
        this.name = name;
        this.dataSet = dataSet;
        this.fileMask = fileMask;
        this.format = format;
        this.datasource = datasource;
        this.catalog = catalog;
        this.project = project;
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

    public Input withName(String name) {
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

    public Input withDataSet(String dataSet) {
        this.dataSet = dataSet;
        return this;
    }

    public String getFileMask() {
        return fileMask;
    }

    public void setFileMask(String fileMask) {
        this.fileMask = fileMask;
    }

    public Input withFileMask(String fileMask) {
        this.fileMask = fileMask;
        return this;
    }

    /**
     * Data format
     * 
     */
    public Input.Format getFormat() {
        return format;
    }

    /**
     * Data format
     * 
     */
    public void setFormat(Input.Format format) {
        this.format = format;
    }

    public Input withFormat(Input.Format format) {
        this.format = format;
        return this;
    }

    /**
     * Type of data source
     * 
     */
    public String getDatasource() {
        return datasource;
    }

    /**
     * Type of data source
     * 
     */
    public void setDatasource(String datasource) {
        this.datasource = datasource;
    }

    public Input withDatasource(String datasource) {
        this.datasource = datasource;
        return this;
    }

    /**
     * Catalog in data source
     * 
     */
    public String getCatalog() {
        return catalog;
    }

    /**
     * Catalog in data source
     * 
     */
    public void setCatalog(String catalog) {
        this.catalog = catalog;
    }

    public Input withCatalog(String catalog) {
        this.catalog = catalog;
        return this;
    }

    /**
     * Project of input object
     * 
     */
    public String getProject() {
        return project;
    }

    /**
     * Project of input object
     * 
     */
    public void setProject(String project) {
        this.project = project;
    }

    public Input withProject(String project) {
        this.project = project;
        return this;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this).append("name", name).append("dataSet", dataSet).append("fileMask", fileMask).append("format", format).append("datasource", datasource).append("catalog", catalog).append("project", project).toString();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder().append(datasource).append(catalog).append(name).append(format).append(project).append(dataSet).append(fileMask).toHashCode();
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }
        if ((other instanceof Input) == false) {
            return false;
        }
        Input rhs = ((Input) other);
        return new EqualsBuilder().append(datasource, rhs.datasource).append(catalog, rhs.catalog).append(name, rhs.name).append(format, rhs.format).append(project, rhs.project).append(dataSet, rhs.dataSet).append(fileMask, rhs.fileMask).isEquals();
    }

    public enum Format {

        @SerializedName("parquet")
        PARQUET(null),
        @SerializedName("json")
        JSON(null),
        @SerializedName("csv")
        CSV(null),
        @SerializedName("maprdb")
        MAPRDB(null),
        @SerializedName("es")
        ES(null);
        private final sncr.xdf.conf.Format value;
        private final static Map<sncr.xdf.conf.Format, Input.Format> CONSTANTS = new HashMap<sncr.xdf.conf.Format, Input.Format>();

        static {
            for (Input.Format c: values()) {
                CONSTANTS.put(c.value, c);
            }
        }

        private Format(sncr.xdf.conf.Format value) {
            this.value = value;
        }

        public sncr.xdf.conf.Format value() {
            return this.value;
        }

        public static Input.Format fromValue(sncr.xdf.conf.Format value) {
            Input.Format constant = CONSTANTS.get(value);
            if (constant == null) {
                throw new IllegalArgumentException((value +""));
            } else {
                return constant;
            }
        }

    }

}
