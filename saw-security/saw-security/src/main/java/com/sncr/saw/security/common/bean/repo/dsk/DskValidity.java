package com.sncr.saw.security.common.bean.repo.dsk;

import com.sncr.saw.security.common.bean.Valid;

public class DskValidity extends Valid {
    private Long groupId;
    private String groupName;
    private Long attributeId;
    private String attributeName;
    private String description;

    public Long getGroupId() {
        return groupId;
    }

    public void setGroupId(Long groupId) {
        this.groupId = groupId;
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
