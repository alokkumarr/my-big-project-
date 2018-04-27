package sncr.xdf.esloader.esloadercommon;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

/**
 * Created by skbm0001 on 29/1/2018.
 */
public class ESConfig {
    private String esHost;
    private String esClusterName;
    private String esUser;
    private String esPassword;
    private int esPort;

    private String esIndex;

    private String dataLocation;

    public ESConfig(String esHost, String esUser, String esPassword, String esIndex) {
        this.esHost = esHost;
        this.esUser = esUser;
        this.esPassword = esPassword;

        this.esIndex = esIndex;
    }

    public ESConfig(String esHost, String esUser, String esPassword, int esPort, String esIndex) {
        this.esHost = esHost;
        this.esUser = esUser;
        this.esPassword = esPassword;
        this.esPort = esPort;

        this.esIndex = esIndex;
    }

    public String getEsHost() {
        return esHost;
    }

    public void setEsHost(String esHost) {
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
                .toHashCode();
    }
}
