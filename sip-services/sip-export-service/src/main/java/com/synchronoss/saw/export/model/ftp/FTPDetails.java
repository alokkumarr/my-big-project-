package com.synchronoss.saw.export.model.ftp;

public class FTPDetails {

    private String customerName;
    private String alias;
    private String host;
    private int port;
    private String username;
    private String password;
    private String privatekeyFile;
    private String passPhrase;

    // location is for the destination ftp / sftp "folder"
    // scheduled exports will be dumped at this location.
    private String location;

    // type is used for
    private String type;

    public String getAlias() {
        return alias;
    }

    public void setAlias(String alias) {
        this.alias = alias;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public String getPrivatekeyFile() {
        return privatekeyFile;
    }

    public void setPrivatekeyFile(String privatekeyFile) {
        this.privatekeyFile = privatekeyFile;
    }

    public String getPassPhrase() {
        return passPhrase;
    }

    public void setPassPhrase(String passPhrase) {
        this.passPhrase = passPhrase;
    }
}
