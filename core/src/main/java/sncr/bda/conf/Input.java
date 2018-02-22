
package sncr.bda.conf;

import java.util.HashMap;
import java.util.Map;
import javax.annotation.Generated;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;

@Generated("org.jsonschema2pojo")
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
    private Input.Format format = Input.Format.fromValue("parquet");
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

    public Input withName(String name) {
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

    public Input withDataSet(String dataSet) {
        this.dataSet = dataSet;
        return this;
    }

    /**
     * 
     * @return
     *     The fileMask
     */
    public String getFileMask() {
        return fileMask;
    }

    /**
     * 
     * @param fileMask
     *     The fileMask
     */
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
     * @return
     *     The format
     */
    public Input.Format getFormat() {
        return format;
    }

    /**
     * Data format
     * 
     * @param format
     *     The format
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
     * @return
     *     The datasource
     */
    public String getDatasource() {
        return datasource;
    }

    /**
     * Type of data source
     * 
     * @param datasource
     *     The datasource
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
     * @return
     *     The catalog
     */
    public String getCatalog() {
        return catalog;
    }

    /**
     * Catalog in data source
     * 
     * @param catalog
     *     The catalog
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
     * @return
     *     The project
     */
    public String getProject() {
        return project;
    }

    /**
     * Project of input object
     * 
     * @param project
     *     The project
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
        return ToStringBuilder.reflectionToString(this);
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder().append(name).append(dataSet).append(fileMask).append(format).append(datasource).append(catalog).append(project).toHashCode();
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
        return new EqualsBuilder().append(name, rhs.name).append(dataSet, rhs.dataSet).append(fileMask, rhs.fileMask).append(format, rhs.format).append(datasource, rhs.datasource).append(catalog, rhs.catalog).append(project, rhs.project).isEquals();
    }

    @Generated("org.jsonschema2pojo")
    public static enum Format {

        @SerializedName("parquet")
        PARQUET("parquet"),
        @SerializedName("json")
        JSON("json"),
        @SerializedName("csv")
        CSV("csv"),
        @SerializedName("maprdb")
        MAPRDB("maprdb"),
        @SerializedName("es")
        ES("es");
        private final String value;
        private static Map<String, Input.Format> constants = new HashMap<String, Input.Format>();

        static {
            for (Input.Format c: values()) {
                constants.put(c.value, c);
            }
        }

        private Format(String value) {
            this.value = value;
        }

        @Override
        public String toString() {
            return this.value;
        }

        public static Input.Format fromValue(String value) {
            Input.Format constant = constants.get(value);
            if (constant == null) {
                throw new IllegalArgumentException(value);
            } else {
                return constant;
            }
        }

    }

}
