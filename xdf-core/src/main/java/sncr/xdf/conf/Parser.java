
package sncr.xdf.conf;

import java.util.ArrayList;
import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;


/**
 * Parser specific properties
 * 
 */
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
        super();
        this.file = file;
        this.fields = fields;
        this.lineSeparator = lineSeparator;
        this.delimiter = delimiter;
        this.quoteChar = quoteChar;
        this.quoteEscape = quoteEscape;
        this.headerSize = headerSize;
        this.numberOfFiles = numberOfFiles;
    }

    public String getFile() {
        return file;
    }

    public void setFile(String file) {
        this.file = file;
    }

    public Parser withFile(String file) {
        this.file = file;
        return this;
    }

    public List<Field> getFields() {
        return fields;
    }

    public void setFields(List<Field> fields) {
        this.fields = fields;
    }

    public Parser withFields(List<Field> fields) {
        this.fields = fields;
        return this;
    }

    public String getLineSeparator() {
        return lineSeparator;
    }

    public void setLineSeparator(String lineSeparator) {
        this.lineSeparator = lineSeparator;
    }

    public Parser withLineSeparator(String lineSeparator) {
        this.lineSeparator = lineSeparator;
        return this;
    }

    public String getDelimiter() {
        return delimiter;
    }

    public void setDelimiter(String delimiter) {
        this.delimiter = delimiter;
    }

    public Parser withDelimiter(String delimiter) {
        this.delimiter = delimiter;
        return this;
    }

    public String getQuoteChar() {
        return quoteChar;
    }

    public void setQuoteChar(String quoteChar) {
        this.quoteChar = quoteChar;
    }

    public Parser withQuoteChar(String quoteChar) {
        this.quoteChar = quoteChar;
        return this;
    }

    public String getQuoteEscape() {
        return quoteEscape;
    }

    public void setQuoteEscape(String quoteEscape) {
        this.quoteEscape = quoteEscape;
    }

    public Parser withQuoteEscape(String quoteEscape) {
        this.quoteEscape = quoteEscape;
        return this;
    }

    public Integer getHeaderSize() {
        return headerSize;
    }

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

    public Parser withNumberOfFiles(Integer numberOfFiles) {
        this.numberOfFiles = numberOfFiles;
        return this;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this).append("file", file).append("fields", fields).append("lineSeparator", lineSeparator).append("delimiter", delimiter).append("quoteChar", quoteChar).append("quoteEscape", quoteEscape).append("headerSize", headerSize).append("numberOfFiles", numberOfFiles).toString();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder().append(headerSize).append(quoteChar).append(numberOfFiles).append(file).append(quoteEscape).append(delimiter).append(fields).append(lineSeparator).toHashCode();
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
        return new EqualsBuilder().append(headerSize, rhs.headerSize).append(quoteChar, rhs.quoteChar).append(numberOfFiles, rhs.numberOfFiles).append(file, rhs.file).append(quoteEscape, rhs.quoteEscape).append(delimiter, rhs.delimiter).append(fields, rhs.fields).append(lineSeparator, rhs.lineSeparator).isEquals();
    }

}
