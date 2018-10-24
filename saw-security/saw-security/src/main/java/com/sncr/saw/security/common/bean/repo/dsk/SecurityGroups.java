package com.sncr.saw.security.common.bean.repo.dsk;

import java.io.Serializable;

public class SecurityGroups implements Serializable {

    private static final long serialVersionUID = 7684700123455735559L;

    public Long getSecGroupSysId() {
        return secGroupSysId;
    }

    public void setSecGroupSysId(Long secGroupSysId) {
        this.secGroupSysId = secGroupSysId;
    }

    private Long secGroupSysId;
    private String securityGroupName;
    private String description;

    public String getSecurityGroupName() {
        return securityGroupName;
    }

    public void setSecurityGroupName(String securityGroupName) {
        this.securityGroupName = securityGroupName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

}
