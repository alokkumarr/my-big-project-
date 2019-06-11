package sncr.bda.conf;

import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;

import javax.annotation.Generated;


/**
 * ES Loader specific properties
 *
 */
public class ESLoader {

    /**
     * Index to which the data has to be loaded
     * (Required)
     *
     */
    @SerializedName("destinationIndexName")
    @Expose
    private String destinationIndexName;
    /**
     * Name of the file with index mapping (definition), optional
     *
     */
    @SerializedName("indexMappingfile")
    @Expose
    private String indexMappingfile;
    /**
     * ES mapping id for the document. This data will be used for _id value
     *
     */
    @SerializedName("documentIDField")
    @Expose
    private String documentIDField;
    /**
     * Used to filter data from the given dataset using conditional statements (E.g.: col1='STR1' AND length > 10)
     *
     */
    @SerializedName("filterString")
    @Expose
    private String filterString;
    /**
     * List of aliases and corresponding loading modes
     *
     */
    @SerializedName("aliases")
    @Expose
    private List<Alias> aliases = null;
    /**
     * List of host names or IP addresses
     * (Required)
     *
     */
    @SerializedName("esNodes")
    @Expose
    private List<String> esNodes = null;
    /**
     * Name of the ElasticSearch cluster. By default, the cluster name will be elasticsearch
     *
     */
    @SerializedName("esClusterName")
    @Expose
    private String esClusterName;
    /**
     * Connecting port for ElasticSearch server
     *
     */
    @SerializedName("esPort")
    @Expose
    private int esPort;
    /**
     * Key column in the dataset to which the record in ES will be matched
     *
     */
    @SerializedName("esMappingId")
    @Expose
    private String esMappingId;
    /**
     * User name for ElasticSearch
     *
     */
    @SerializedName("esUser")
    @Expose
    private String esUser;
    /**
     * Password for the username mentioned for the ElasticSearch
     *
     */
    @SerializedName("esPass")
    @Expose
    private String esPass;

    /**
     * Is SSL enabled for ElasticSearch
     *
     */
    @SerializedName("esSslEnabled")
    @Expose
    private Boolean esSslEnabled;

    /**
     * Key store path for the ElasticSearch
     *
     */
    @SerializedName("keyStorePath")
    @Expose
    private String keyStorePath;

    /**
     * Store password for the ElasticSearch
     *
     */
    @SerializedName("storePassword")
    @Expose
    private String storePassword;

    /**
     * No args constructor for use in serialization
     *
     */
    public ESLoader() {
    }

    /**
     *
     * @param esClusterName
     * @param destinationIndexName
     * @param esPass
     * @param esUser
     * @param esNodes
     * @param esPort
     * @param documentIDField
     * @param filterString
     * @param indexMappingfile
     * @param aliases
     * @param esMappingId
     */
    public ESLoader(String destinationIndexName, String indexMappingfile, String documentIDField,
                    String filterString, List<Alias> aliases, List<String> esNodes,
                    String esClusterName, int esPort, String esMappingId,
                    String esUser, String esPass) {
        super();
        this.destinationIndexName = destinationIndexName;
        this.indexMappingfile = indexMappingfile;
        this.documentIDField = documentIDField;
        this.filterString = filterString;
        this.aliases = aliases;
        this.esNodes = esNodes;
        this.esClusterName = esClusterName;
        this.esPort = esPort;
        this.esMappingId = esMappingId;
        this.esUser = esUser;
        this.esPass = esPass;
    }

    /**
     * Index to which the data has to be loaded
     * (Required)
     *
     */
    public String getDestinationIndexName() {
        return destinationIndexName;
    }

    /**
     * Index to which the data has to be loaded
     * (Required)
     *
     */
    public void setDestinationIndexName(String destinationIndexName) {
        this.destinationIndexName = destinationIndexName;
    }

    /**
     * Name of the file with index mapping (definition), optional
     *
     */
    public String getIndexMappingfile() {
        return indexMappingfile;
    }

    /**
     * Name of the file with index mapping (definition), optional
     *
     */
    public void setIndexMappingfile(String indexMappingfile) {
        this.indexMappingfile = indexMappingfile;
    }

    /**
     * ES mapping id for the document. This data will be used for _id value
     *
     */
    public String getDocumentIDField() {
        return documentIDField;
    }

    /**
     * ES mapping id for the document. This data will be used for _id value
     *
     */
    public void setDocumentIDField(String documentIDField) {
        this.documentIDField = documentIDField;
    }

    /**
     * Used to filter data from the given dataset using conditional statements (E.g.: col1='STR1' AND length > 10)
     *
     */
    public String getFilterString() {
        return filterString;
    }

    /**
     * Used to filter data from the given dataset using conditional statements (E.g.: col1='STR1' AND length > 10)
     *
     */
    public void setFilterString(String filterString) {
        this.filterString = filterString;
    }

    /**
     * List of aliases and corresponding loading modes
     *
     */
    public List<Alias> getAliases() {
        return aliases;
    }

    /**
     * List of aliases and corresponding loading modes
     *
     */
    public void setAliases(List<Alias> aliases) {
        this.aliases = aliases;
    }

    /**
     * List of host names or IP addresses
     * (Required)
     *
     */
    public List<String> getEsNodes() {
        return esNodes;
    }

    /**
     * List of host names or IP addresses
     * (Required)
     *
     */
    public void setEsNodes(List<String> esNodes) {
        this.esNodes = esNodes;
    }

    /**
     * Name of the ElasticSearch cluster. By default, the cluster name will be elasticsearch
     *
     */
    public String getEsClusterName() {
        return esClusterName;
    }

    /**
     * Name of the ElasticSearch cluster. By default, the cluster name will be elasticsearch
     *
     */
    public void setEsClusterName(String esClusterName) {
        this.esClusterName = esClusterName;
    }

    /**
     * Connecting port for ElasticSearch server
     *
     */
    public int getEsPort() {
        return esPort;
    }

    /**
     * Connecting port for ElasticSearch server
     *
     */
    public void setEsPort(int esPort) {
        this.esPort = esPort;
    }

    /**
     * Key column in the dataset to which the record in ES will be matched
     *
     */
    public String getEsMappingId() {
        return esMappingId;
    }

    /**
     * Key column in the dataset to which the record in ES will be matched
     *
     */
    public void setEsMappingId(String esMappingId) {
        this.esMappingId = esMappingId;
    }

    /**
     * User name for ElasticSearch
     *
     */
    public String getEsUser() {
        return esUser;
    }

    /**
     * User name for ElasticSearch
     *
     */
    public void setEsUser(String esUser) {
        this.esUser = esUser;
    }

    /**
     * Password for the username mentioned for the ElasticSearch
     *
     */
    public String getEsPass() {
        return esPass;
    }

    /**
     * Password for the username mentioned for the ElasticSearch
     *
     */
    public void setEsPass(String esPass) {
        this.esPass = esPass;
    }

    /**
     * Gets esSslEnabled
     *
     * @return value of esSslEnabled
     */
    public Boolean getEsSslEnabled() {
        return esSslEnabled;
    }

    /**
     * Sets esSslEnabled
     */
    public void setEsSslEnabled(Boolean esSslEnabled) {
        this.esSslEnabled = esSslEnabled;
    }

    /**
     * Gets keyStorePath
     *
     * @return value of keyStorePath
     */
    public String getKeyStorePath() {
        return keyStorePath;
    }

    /**
     * Sets keyStorePath
     */
    public void setKeyStorePath(String keyStorePath) {
        this.keyStorePath = keyStorePath;
    }

    /**
     * Gets storePassword
     *
     * @return value of storePassword
     */
    public String getStorePassword() {
        return storePassword;
    }

    /**
     * Sets storePassword
     */
    public void setStorePassword(String storePassword) {
        this.storePassword = storePassword;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this).append("destinationIndexName", destinationIndexName).append("indexMappingfile", indexMappingfile).append("documentIDField", documentIDField).append("filterString", filterString).append("aliases", aliases).append("esNodes", esNodes).append("esClusterName", esClusterName).append("esPort", esPort).append("esMappingId", esMappingId).append("esUser", esUser).append("esPass", esPass).toString();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder()
                .append(esClusterName)
                .append(destinationIndexName)
                .append(esPass)
                .append(esUser)
                .append(esNodes)
                .append(esPort)
                .append(documentIDField)
                .append(filterString)
                .append(indexMappingfile)
                .append(aliases)
                .append(esMappingId)
                .toHashCode();
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }
        if ((other instanceof ESLoader) == false) {
            return false;
        }
        ESLoader rhs = ((ESLoader) other);
        return new EqualsBuilder()
                .append(esClusterName, rhs.esClusterName)
                .append(destinationIndexName, rhs.destinationIndexName)
                .append(esPass, rhs.esPass)
                .append(esUser, rhs.esUser)
                .append(esNodes, rhs.esNodes)
                .append(esPort, rhs.esPort)
                .append(documentIDField, rhs.documentIDField)
                .append(filterString, rhs.filterString)
                .append(indexMappingfile, rhs.indexMappingfile)
                .append(aliases, rhs.aliases)
                .append(esMappingId, rhs.esMappingId).isEquals();
    }

}
