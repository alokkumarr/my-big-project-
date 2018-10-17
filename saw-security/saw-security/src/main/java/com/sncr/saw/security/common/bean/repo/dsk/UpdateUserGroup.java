package com.sncr.saw.security.common.bean.repo.dsk;

import java.io.Serializable;

public class UpdateUserGroup implements Serializable {
    private static final long serialVersionUID = 7684700765435735559L;
    private String userId;
    private String groupName;

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }
}
