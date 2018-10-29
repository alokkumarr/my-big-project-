package com.sncr.saw.security.app.repository;

import com.sncr.saw.security.common.bean.Valid;
import com.sncr.saw.security.common.bean.repo.dsk.*;

import java.util.List;

public interface DataSecurityKeyRepository {
    public DskValidity addSecurityGroups(SecurityGroups securityGroups, String createdBy, Long custId);
    public DskValidity updateSecurityGroups(Long securityGroupId,List<String> groupNames,Long custId);
    public DskValidity deleteSecurityGroups(Long securityGroupId);
    public List<SecurityGroups> fetchSecurityGroupNames(Long custId);
    public DskValidity addSecurityGroupDskAttributeValues(Long securityGroupId, AttributeValues attributeValues);
    public List<String> fetchSecurityGroupDskAttributes(Long securityGroupId);
    public DskValidity deleteSecurityGroupDskAttributeValues(List<String> dskList);
    public List<DskDetails> fetchDskAllAttributeValues(Long securityGroupId);
    public DskValidity updateUser(String securityGroupName,Long userSysId, Long custId);
    public List<UserAssignment> getAllUserAssignments(Long custId);
    public DskValidity updateAttributeValues(Long securityGroupId,AttributeValues attributeValues);
}
