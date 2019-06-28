package sncr.xdf.esloader.esloadercommon;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.util.List;

/**
 * Created by skbm0001 on 29/1/2018.
 */
public class ESConfig {
    private List<String> esHost;
    private String esClusterName;
    private String esUser;
    private String esPassword;
    private int esPort;
    private boolean esSslEnabled;
    private String keyStorePath;
    private String storePassword;
    private String esIndex;

    private String dataLocation;

    public ESConfig(List<String> esHost, String esUser, String esPassword, String esIndex) {
        this.esHost = esHost;
        this.esUser = esUser;
        this.esPassword = esPassword;
        this.esIndex = esIndex;
    }

    public ESConfig(List<String> esHost, String esUser, String esPassword, int esPort, String
        esIndex) {
        this.esHost = esHost;
        this.esUser = esUser;
        this.esPassword = esPassword;
        this.esPort = esPort;
        this.esIndex = esIndex;
    }

    public List<String> getEsHosts() {
        return esHost;
    }

    public void setEsHost(List<String> esHost) {
        this.esHost = esHost;
    }

    public String getEsClusterName() {
        return esClusterName;
    }

    public void setEsClusterName(String esClusterName) {
        this.esClusterName = esClusterName;
    }

    public String getEsUser() {
        return esUser;
    }

    public void setEsUser(String esUser) {
        this.esUser = esUser;
    }

    public String getEsPassword() {
        return esPassword;
    }

    public void setEsPassword(String esPassword) {
        this.esPassword = esPassword;
    }

    public int getEsPort() {
        return esPort;
    }

    public void setEsPort(int esPort) {
        this.esPort = esPort;
    }

    public String getEsIndex() {
        return esIndex;
    }

    public void setEsIndex(String esIndex) {
        this.esIndex = esIndex;
    }

    /**
     * Gets esSslEnabled
     *
     * @return value of esSslEnabled
     */
    public boolean isEsSslEnabled() {
        return esSslEnabled;
    }

    /**
     * Sets esSslEnabled
     */
    public void setEsSslEnabled(boolean esSslEnabled) {
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
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        ESConfig esConfig = (ESConfig) o;

        return new EqualsBuilder()
                .append(esPort, esConfig.esPort)
                .append(esHost, esConfig.esHost)
                .append(esClusterName, esConfig.esClusterName)
                .append(esUser, esConfig.esUser)
                .append(esPassword, esConfig.esPassword)
                .append(esIndex, esConfig.esIndex)
                .append(esSslEnabled,esConfig.esSslEnabled)
                .append(keyStorePath, esConfig.keyStorePath)
                .append(storePassword,esConfig.storePassword)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(esHost)
                .append(esClusterName)
                .append(esUser)
                .append(esPassword)
                .append(esPort)
                .append(esIndex)
                .append(esSslEnabled)
                .append(keyStorePath)
                .append(storePassword)
                .toHashCode();
    }
}
