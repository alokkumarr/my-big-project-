
package sncr.xdf.conf;

import java.util.ArrayList;
import java.util.List;
import javax.annotation.Generated;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;


/**
 * Defines the configuration XDF-UX component
 * 
 */
@Generated("org.jsonschema2pojo")
public class ComponentConfiguration {

    /**
     * List of named input data objects
     * 
     */
    @SerializedName("inputs")
    @Expose
    private List<Input> inputs = new ArrayList<Input>();
    /**
     * List of named output data objects
     * 
     */
    @SerializedName("outputs")
    @Expose
    private List<Output> outputs = new ArrayList<Output>();
    /**
     * Project/Application name (ID)
     * 
     */
    @SerializedName("project")
    @Expose
    private String project;
    /**
     * System parameters specific for component execution
     * 
     */
    @SerializedName("parameters")
    @Expose
    private List<Parameter> parameters = new ArrayList<Parameter>();
    /**
     * Parser specific properties
     * 
     */
    @SerializedName("parser")
    @Expose
    private Parser parser;
    /**
     * SCD2 specific properties
     * 
     */
    @SerializedName("scd2")
    @Expose
    private Scd2 scd2;
    /**
     * SQL script executor specific properties
     * 
     */
    @SerializedName("sql")
    @Expose
    private Sql sql;
    /**
     * Transformer specific properties
     * 
     */
    @SerializedName("transformer")
    @Expose
    private Transformer transformer;
    /**
     * ES Loader specific properties
     * 
     */
    @SerializedName("es-loader")
    @Expose
    private Object esLoader;
    /**
     * ES Reader specific properties
     * 
     */
    @SerializedName("es-reader")
    @Expose
    private Object esReader;
    /**
     * Converter specific properties
     * 
     */
    @SerializedName("converter")
    @Expose
    private Object converter;
    /**
     * DB Loader specific properties
     * 
     */
    @SerializedName("db-loader")
    @Expose
    private Object dbLoader;
    /**
     * Data profiler/analyzer configuration properties
     * 
     */
    @SerializedName("analyzer")
    @Expose
    private Analyzer analyzer;
    /**
     * Zero specific properties
     * 
     */
    @SerializedName("zero")
    @Expose
    private Object zero;

    /**
     * No args constructor for use in serialization
     * 
     */
    public ComponentConfiguration() {
    }

    /**
     * 
     * @param outputs
     * @param dbLoader
     * @param inputs
     * @param converter
     * @param transformer
     * @param project
     * @param scd2
     * @param sql
     * @param zero
     * @param parser
     * @param analyzer
     * @param esLoader
     * @param esReader
     * @param parameters
     */
    public ComponentConfiguration(List<Input> inputs, List<Output> outputs, String project, List<Parameter> parameters, Parser parser, Scd2 scd2, Sql sql, Transformer transformer, Object esLoader, Object esReader, Object converter, Object dbLoader, Analyzer analyzer, Object zero) {
        this.inputs = inputs;
        this.outputs = outputs;
        this.project = project;
        this.parameters = parameters;
        this.parser = parser;
        this.scd2 = scd2;
        this.sql = sql;
        this.transformer = transformer;
        this.esLoader = esLoader;
        this.esReader = esReader;
        this.converter = converter;
        this.dbLoader = dbLoader;
        this.analyzer = analyzer;
        this.zero = zero;
    }

    /**
     * List of named input data objects
     * 
     * @return
     *     The inputs
     */
    public List<Input> getInputs() {
        return inputs;
    }

    /**
     * List of named input data objects
     * 
     * @param inputs
     *     The inputs
     */
    public void setInputs(List<Input> inputs) {
        this.inputs = inputs;
    }

    public ComponentConfiguration withInputs(List<Input> inputs) {
        this.inputs = inputs;
        return this;
    }

    /**
     * List of named output data objects
     * 
     * @return
     *     The outputs
     */
    public List<Output> getOutputs() {
        return outputs;
    }

    /**
     * List of named output data objects
     * 
     * @param outputs
     *     The outputs
     */
    public void setOutputs(List<Output> outputs) {
        this.outputs = outputs;
    }

    public ComponentConfiguration withOutputs(List<Output> outputs) {
        this.outputs = outputs;
        return this;
    }

    /**
     * Project/Application name (ID)
     * 
     * @return
     *     The project
     */
    public String getProject() {
        return project;
    }

    /**
     * Project/Application name (ID)
     * 
     * @param project
     *     The project
     */
    public void setProject(String project) {
        this.project = project;
    }

    public ComponentConfiguration withProject(String project) {
        this.project = project;
        return this;
    }

    /**
     * System parameters specific for component execution
     * 
     * @return
     *     The parameters
     */
    public List<Parameter> getParameters() {
        return parameters;
    }

    /**
     * System parameters specific for component execution
     * 
     * @param parameters
     *     The parameters
     */
    public void setParameters(List<Parameter> parameters) {
        this.parameters = parameters;
    }

    public ComponentConfiguration withParameters(List<Parameter> parameters) {
        this.parameters = parameters;
        return this;
    }

    /**
     * Parser specific properties
     * 
     * @return
     *     The parser
     */
    public Parser getParser() {
        return parser;
    }

    /**
     * Parser specific properties
     * 
     * @param parser
     *     The parser
     */
    public void setParser(Parser parser) {
        this.parser = parser;
    }

    public ComponentConfiguration withParser(Parser parser) {
        this.parser = parser;
        return this;
    }

    /**
     * SCD2 specific properties
     * 
     * @return
     *     The scd2
     */
    public Scd2 getScd2() {
        return scd2;
    }

    /**
     * SCD2 specific properties
     * 
     * @param scd2
     *     The scd2
     */
    public void setScd2(Scd2 scd2) {
        this.scd2 = scd2;
    }

    public ComponentConfiguration withScd2(Scd2 scd2) {
        this.scd2 = scd2;
        return this;
    }

    /**
     * SQL script executor specific properties
     * 
     * @return
     *     The sql
     */
    public Sql getSql() {
        return sql;
    }

    /**
     * SQL script executor specific properties
     * 
     * @param sql
     *     The sql
     */
    public void setSql(Sql sql) {
        this.sql = sql;
    }

    public ComponentConfiguration withSql(Sql sql) {
        this.sql = sql;
        return this;
    }

    /**
     * Transformer specific properties
     * 
     * @return
     *     The transformer
     */
    public Transformer getTransformer() {
        return transformer;
    }

    /**
     * Transformer specific properties
     * 
     * @param transformer
     *     The transformer
     */
    public void setTransformer(Transformer transformer) {
        this.transformer = transformer;
    }

    public ComponentConfiguration withTransformer(Transformer transformer) {
        this.transformer = transformer;
        return this;
    }

    /**
     * ES Loader specific properties
     * 
     * @return
     *     The esLoader
     */
    public Object getEsLoader() {
        return esLoader;
    }

    /**
     * ES Loader specific properties
     * 
     * @param esLoader
     *     The es-loader
     */
    public void setEsLoader(Object esLoader) {
        this.esLoader = esLoader;
    }

    public ComponentConfiguration withEsLoader(Object esLoader) {
        this.esLoader = esLoader;
        return this;
    }

    /**
     * ES Reader specific properties
     * 
     * @return
     *     The esReader
     */
    public Object getEsReader() {
        return esReader;
    }

    /**
     * ES Reader specific properties
     * 
     * @param esReader
     *     The es-reader
     */
    public void setEsReader(Object esReader) {
        this.esReader = esReader;
    }

    public ComponentConfiguration withEsReader(Object esReader) {
        this.esReader = esReader;
        return this;
    }

    /**
     * Converter specific properties
     * 
     * @return
     *     The converter
     */
    public Object getConverter() {
        return converter;
    }

    /**
     * Converter specific properties
     * 
     * @param converter
     *     The converter
     */
    public void setConverter(Object converter) {
        this.converter = converter;
    }

    public ComponentConfiguration withConverter(Object converter) {
        this.converter = converter;
        return this;
    }

    /**
     * DB Loader specific properties
     * 
     * @return
     *     The dbLoader
     */
    public Object getDbLoader() {
        return dbLoader;
    }

    /**
     * DB Loader specific properties
     * 
     * @param dbLoader
     *     The db-loader
     */
    public void setDbLoader(Object dbLoader) {
        this.dbLoader = dbLoader;
    }

    public ComponentConfiguration withDbLoader(Object dbLoader) {
        this.dbLoader = dbLoader;
        return this;
    }

    /**
     * Data profiler/analyzer configuration properties
     * 
     * @return
     *     The analyzer
     */
    public Analyzer getAnalyzer() {
        return analyzer;
    }

    /**
     * Data profiler/analyzer configuration properties
     * 
     * @param analyzer
     *     The analyzer
     */
    public void setAnalyzer(Analyzer analyzer) {
        this.analyzer = analyzer;
    }

    public ComponentConfiguration withAnalyzer(Analyzer analyzer) {
        this.analyzer = analyzer;
        return this;
    }

    /**
     * Zero specific properties
     * 
     * @return
     *     The zero
     */
    public Object getZero() {
        return zero;
    }

    /**
     * Zero specific properties
     * 
     * @param zero
     *     The zero
     */
    public void setZero(Object zero) {
        this.zero = zero;
    }

    public ComponentConfiguration withZero(Object zero) {
        this.zero = zero;
        return this;
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder().append(inputs).append(outputs).append(project).append(parameters).append(parser).append(scd2).append(sql).append(transformer).append(esLoader).append(esReader).append(converter).append(dbLoader).append(analyzer).append(zero).toHashCode();
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }
        if ((other instanceof ComponentConfiguration) == false) {
            return false;
        }
        ComponentConfiguration rhs = ((ComponentConfiguration) other);
        return new EqualsBuilder().append(inputs, rhs.inputs).append(outputs, rhs.outputs).append(project, rhs.project).append(parameters, rhs.parameters).append(parser, rhs.parser).append(scd2, rhs.scd2).append(sql, rhs.sql).append(transformer, rhs.transformer).append(esLoader, rhs.esLoader).append(esReader, rhs.esReader).append(converter, rhs.converter).append(dbLoader, rhs.dbLoader).append(analyzer, rhs.analyzer).append(zero, rhs.zero).isEquals();
    }

}
