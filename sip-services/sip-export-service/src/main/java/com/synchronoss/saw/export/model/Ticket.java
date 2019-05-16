package com.synchronoss.saw.export.model;

import java.util.List;

public class Ticket {

private String userId;
private String userFullName;
private List dataSecurityKey;
private String roleType;

    /**
     * Gets userId
     *
     * @return value of userId
     */
    public String getUserId() {
        return userId;
    }

    /**
     * Sets userId
     */
    public void setUserId(String userId) {
        this.userId = userId;
    }

    /**
     * Gets userFullName
     *
     * @return value of userFullName
     */
    public String getUserFullName() {
        return userFullName;
    }

    /**
     * Sets userFullName
     */
    public void setUserFullName(String userFullName) {
        this.userFullName = userFullName;
    }

    /**
     * Gets dataSecurityKey
     *
     * @return value of dataSecurityKey
     */
    public List getDataSecurityKey() {
        return dataSecurityKey;
    }

    /**
     * Sets dataSecurityKey
     */
    public void setDataSecurityKey(List dataSecurityKey) {
        this.dataSecurityKey = dataSecurityKey;
    }

    /**
     * Gets roleType
     *
     * @return value of roleType
     */
    public String getRoleType() {
        return roleType;
    }

    /**
     * Sets roleType
     */
    public void setRoleType(String roleType) {
        this.roleType = roleType;
    }
}
