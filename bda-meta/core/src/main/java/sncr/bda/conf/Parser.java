
package sncr.bda.conf;

import java.util.ArrayList;
import java.util.List;
import javax.annotation.Generated;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;


/**
 * Parser specific properties
 * 
 */
@Generated("org.jsonschema2pojo")
public class Parser {

    @SerializedName("file")
    @Expose
    private String file;
    @SerializedName("fields")
    @Expose
    private List<Field> fields = new ArrayList<Field>();
    @SerializedName("lineSeparator")
    @Expose
    private String lineSeparator;
    @SerializedName("delimiter")
    @Expose
    private String delimiter;
    @SerializedName("quoteChar")
    @Expose
    private String quoteChar;
    @SerializedName("quoteEscape")
    @Expose
    private String quoteEscape;
    @SerializedName("headerSize")
    @Expose
    private Integer headerSize;
    /**
     * Number of files for all output objects
     * 
     */
    @SerializedName("numberOfFiles")
    @Expose
    private Integer numberOfFiles = 1;

    /**
     * No args constructor for use in serialization
     * 
     */
    public Parser() {
    }

    /**
     * 
     * @param headerSize
     * @param quoteChar
     * @param numberOfFiles
     * @param file
     * @param quoteEscape
     * @param delimiter
     * @param fields
     * @param lineSeparator
     */
    public Parser(String file, List<Field> fields, String lineSeparator, String delimiter, String quoteChar, String quoteEscape, Integer headerSize, Integer numberOfFiles) {
        this.file = file;
        this.fields = fields;
        this.lineSeparator = lineSeparator;
        this.delimiter = delimiter;
        this.quoteChar = quoteChar;
        this.quoteEscape = quoteEscape;
        this.headerSize = headerSize;
        this.numberOfFiles = numberOfFiles;
    }

    /**
     * 
     * @return
     *     The file
     */
    public String getFile() {
        return file;
    }

    /**
     * 
     * @param file
     *     The file
     */
    public void setFile(String file) {
        this.file = file;
    }

    public Parser withFile(String file) {
        this.file = file;
        return this;
    }

    /**
     * 
     * @return
     *     The fields
     */
    public List<Field> getFields() {
        return fields;
    }

    /**
     * 
     * @param fields
     *     The fields
     */
    public void setFields(List<Field> fields) {
        this.fields = fields;
    }

    public Parser withFields(List<Field> fields) {
        this.fields = fields;
        return this;
    }

    /**
     * 
     * @return
     *     The lineSeparator
     */
    public String getLineSeparator() {
        return lineSeparator;
    }

    /**
     * 
     * @param lineSeparator
     *     The lineSeparator
     */
    public void setLineSeparator(String lineSeparator) {
        this.lineSeparator = lineSeparator;
    }

    public Parser withLineSeparator(String lineSeparator) {
        this.lineSeparator = lineSeparator;
        return this;
    }

    /**
     * 
     * @return
     *     The delimiter
     */
    public String getDelimiter() {
        return delimiter;
    }

    /**
     * 
     * @param delimiter
     *     The delimiter
     */
    public void setDelimiter(String delimiter) {
        this.delimiter = delimiter;
    }

    public Parser withDelimiter(String delimiter) {
        this.delimiter = delimiter;
        return this;
    }

    /**
     * 
     * @return
     *     The quoteChar
     */
    public String getQuoteChar() {
        return quoteChar;
    }

    /**
     * 
     * @param quoteChar
     *     The quoteChar
     */
    public void setQuoteChar(String quoteChar) {
        this.quoteChar = quoteChar;
    }

    public Parser withQuoteChar(String quoteChar) {
        this.quoteChar = quoteChar;
        return this;
    }

    /**
     * 
     * @return
     *     The quoteEscape
     */
    public String getQuoteEscape() {
        return quoteEscape;
    }

    /**
     * 
     * @param quoteEscape
     *     The quoteEscape
     */
    public void setQuoteEscape(String quoteEscape) {
        this.quoteEscape = quoteEscape;
    }

    public Parser withQuoteEscape(String quoteEscape) {
        this.quoteEscape = quoteEscape;
        return this;
    }

    /**
     * 
     * @return
     *     The headerSize
     */
    public Integer getHeaderSize() {
        return headerSize;
    }

    /**
     * 
     * @param headerSize
     *     The headerSize
     */
    public void setHeaderSize(Integer headerSize) {
        this.headerSize = headerSize;
    }

    public Parser withHeaderSize(Integer headerSize) {
        this.headerSize = headerSize;
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

    public Parser withNumberOfFiles(Integer numberOfFiles) {
        this.numberOfFiles = numberOfFiles;
        return this;
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder().append(file).append(fields).append(lineSeparator).append(delimiter).append(quoteChar).append(quoteEscape).append(headerSize).append(numberOfFiles).toHashCode();
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }
        if ((other instanceof Parser) == false) {
            return false;
        }
        Parser rhs = ((Parser) other);
        return new EqualsBuilder().append(file, rhs.file).append(fields, rhs.fields).append(lineSeparator, rhs.lineSeparator).append(delimiter, rhs.delimiter).append(quoteChar, rhs.quoteChar).append(quoteEscape, rhs.quoteEscape).append(headerSize, rhs.headerSize).append(numberOfFiles, rhs.numberOfFiles).isEquals();
    }

}
