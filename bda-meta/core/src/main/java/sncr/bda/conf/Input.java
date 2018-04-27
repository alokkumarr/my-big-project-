
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
    @SerializedName("dstype")
    @Expose
    private Input.Dstype dstype = Input.Dstype.fromValue("base");
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
    @SerializedName("parent")
    @Expose
    private String parent;

    /**
     * No args constructor for use in serialization
     * 
     */
    public Input() {
    }

    /**
     * 
     * @param parent
     * @param dstype
     * @param catalog
     * @param name
     * @param format
     * @param project
     * @param dataSet
     * @param fileMask
     */
    public Input(String name, String dataSet, String fileMask, Input.Format format, Input.Dstype dstype, String catalog, String project, String parent) {
        this.name = name;
        this.dataSet = dataSet;
        this.fileMask = fileMask;
        this.format = format;
        this.dstype = dstype;
        this.catalog = catalog;
        this.project = project;
        this.parent = parent;
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

    public Input withName(String name) {
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
     *     The dstype
     */
    public Input.Dstype getDstype() {
        return dstype;
    }

    /**
     * Type of data source
     * 
     * @param dstype
     *     The dstype
     */
    public void setDstype(Input.Dstype dstype) {
        this.dstype = dstype;
    }

    public Input withDstype(Input.Dstype dstype) {
        this.dstype = dstype;
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

    public Input withParent(String parent) {
        this.parent = parent;
        return this;
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder().append(name).append(dataSet).append(fileMask).append(format).append(dstype).append(catalog).append(project).append(parent).toHashCode();
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
        return new EqualsBuilder().append(name, rhs.name).append(dataSet, rhs.dataSet).append(fileMask, rhs.fileMask).append(format, rhs.format).append(dstype, rhs.dstype).append(catalog, rhs.catalog).append(project, rhs.project).append(parent, rhs.parent).isEquals();
    }

    @Generated("org.jsonschema2pojo")
    public static enum Dstype {

        @SerializedName("base")
        BASE("base"),
        @SerializedName("partition")
        PARTITION("partition");
        private final String value;
        private static Map<String, Input.Dstype> constants = new HashMap<String, Input.Dstype>();

        static {
            for (Input.Dstype c: values()) {
                constants.put(c.value, c);
            }
        }

        private Dstype(String value) {
            this.value = value;
        }

        @Override
        public String toString() {
            return this.value;
        }

        public static Input.Dstype fromValue(String value) {
            Input.Dstype constant = constants.get(value);
            if (constant == null) {
                throw new IllegalArgumentException(value);
            } else {
                return constant;
            }
        }

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
