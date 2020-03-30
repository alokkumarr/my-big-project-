package com.sncr.saw.security.common.bean.external.response;

public class Id3User {

    private String userId;
    private long userSysId;
    private long customerSysId;
    private String customerCode;
    private String userName;
    private String roleType;
    private boolean id3Enabled;

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public long getUserSysId() {
        return userSysId;
    }

    public void setUserSysId(long userSysId) {
        this.userSysId = userSysId;
    }

    public long getCustomerSysId() {
        return customerSysId;
    }

    public void setCustomerSysId(long customerSysId) {
        this.customerSysId = customerSysId;
    }

    public String getCustomerCode() {
        return customerCode;
    }

    public void setCustomerCode(String customerCode) {
        this.customerCode = customerCode;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getRoleType() {
        return roleType;
    }

    public void setRoleType(String roleType) {
        this.roleType = roleType;
    }

    public boolean isId3Enabled() {
        return id3Enabled;
    }

    public void setId3Enabled(boolean id3Enabled) {
        this.id3Enabled = id3Enabled;
    }
}
