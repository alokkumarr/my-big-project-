
package sncr.xdf.conf;

import java.util.ArrayList;
import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;


/**
 * Defines the configuration XDF-UX component
 * 
 */
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
     * Partition maker specific properties
     * 
     */
    @SerializedName("partitioner")
    @Expose
    private Object partitioner;
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
     * @param scd2
     * @param sql
     * @param zero
     * @param parser
     * @param analyzer
     * @param partitioner
     * @param esLoader
     * @param esReader
     * @param parameters
     */
    public ComponentConfiguration(List<Input> inputs, List<Output> outputs, List<Parameter> parameters, Parser parser, Scd2 scd2, Sql sql, Object partitioner, Object esLoader, Object esReader, Object converter, Object dbLoader, Analyzer analyzer, Object zero) {
        super();
        this.inputs = inputs;
        this.outputs = outputs;
        this.parameters = parameters;
        this.parser = parser;
        this.scd2 = scd2;
        this.sql = sql;
        this.partitioner = partitioner;
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
     */
    public List<Input> getInputs() {
        return inputs;
    }

    /**
     * List of named input data objects
     * 
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
     */
    public List<Output> getOutputs() {
        return outputs;
    }

    /**
     * List of named output data objects
     * 
     */
    public void setOutputs(List<Output> outputs) {
        this.outputs = outputs;
    }

    public ComponentConfiguration withOutputs(List<Output> outputs) {
        this.outputs = outputs;
        return this;
    }

    /**
     * System parameters specific for component execution
     * 
     */
    public List<Parameter> getParameters() {
        return parameters;
    }

    /**
     * System parameters specific for component execution
     * 
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
     */
    public Parser getParser() {
        return parser;
    }

    /**
     * Parser specific properties
     * 
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
     */
    public Scd2 getScd2() {
        return scd2;
    }

    /**
     * SCD2 specific properties
     * 
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
     */
    public Sql getSql() {
        return sql;
    }

    /**
     * SQL script executor specific properties
     * 
     */
    public void setSql(Sql sql) {
        this.sql = sql;
    }

    public ComponentConfiguration withSql(Sql sql) {
        this.sql = sql;
        return this;
    }

    /**
     * Partition maker specific properties
     * 
     */
    public Object getPartitioner() {
        return partitioner;
    }

    /**
     * Partition maker specific properties
     * 
     */
    public void setPartitioner(Object partitioner) {
        this.partitioner = partitioner;
    }

    public ComponentConfiguration withPartitioner(Object partitioner) {
        this.partitioner = partitioner;
        return this;
    }

    /**
     * ES Loader specific properties
     * 
     */
    public Object getEsLoader() {
        return esLoader;
    }

    /**
     * ES Loader specific properties
     * 
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
     */
    public Object getEsReader() {
        return esReader;
    }

    /**
     * ES Reader specific properties
     * 
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
     */
    public Object getConverter() {
        return converter;
    }

    /**
     * Converter specific properties
     * 
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
     */
    public Object getDbLoader() {
        return dbLoader;
    }

    /**
     * DB Loader specific properties
     * 
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
     */
    public Analyzer getAnalyzer() {
        return analyzer;
    }

    /**
     * Data profiler/analyzer configuration properties
     * 
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
     */
    public Object getZero() {
        return zero;
    }

    /**
     * Zero specific properties
     * 
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
        return new ToStringBuilder(this).append("inputs", inputs).append("outputs", outputs).append("parameters", parameters).append("parser", parser).append("scd2", scd2).append("sql", sql).append("partitioner", partitioner).append("esLoader", esLoader).append("esReader", esReader).append("converter", converter).append("dbLoader", dbLoader).append("analyzer", analyzer).append("zero", zero).toString();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder().append(outputs).append(dbLoader).append(inputs).append(converter).append(scd2).append(sql).append(zero).append(parser).append(analyzer).append(partitioner).append(esLoader).append(esReader).append(parameters).toHashCode();
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
        return new EqualsBuilder().append(outputs, rhs.outputs).append(dbLoader, rhs.dbLoader).append(inputs, rhs.inputs).append(converter, rhs.converter).append(scd2, rhs.scd2).append(sql, rhs.sql).append(zero, rhs.zero).append(parser, rhs.parser).append(analyzer, rhs.analyzer).append(partitioner, rhs.partitioner).append(esLoader, rhs.esLoader).append(esReader, rhs.esReader).append(parameters, rhs.parameters).isEquals();
    }

}
