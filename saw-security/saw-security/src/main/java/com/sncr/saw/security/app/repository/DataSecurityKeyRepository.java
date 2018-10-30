package com.sncr.saw.security.app.repository;

import com.sncr.saw.security.common.bean.Valid;
import com.sncr.saw.security.common.bean.repo.dsk.*;

import java.util.List;

public interface DataSecurityKeyRepository {
    public DskValidity addSecurityGroups(SecurityGroups securityGroups, String createdBy);
    public DskValidity updateSecurityGroups(Long securityGroupId,List<String> groupNames);
    public Valid deleteSecurityGroups(Long securityGroupId);
    public List<SecurityGroups> fetchSecurityGroupNames();
    public Valid addSecurityGroupDskAttributeValues(Long securityGroupId, AttributeValues attributeValues);
    public List<String> fetchSecurityGroupDskAttributes(Long securityGroupId);
    public Valid deleteSecurityGroupDskAttributeValues(List<String> dskList);
    public List<DskDetails> fetchDskAllAttributeValues(Long securityGroupId);
    public Valid updateUser(String securityGroupName,Long userSysId);
    public List<UserAssignment> getAllUserAssignments();
    public Valid updateAttributeValues(Long securityGroupId,AttributeValues attributeValues);
}
