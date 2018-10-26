package com.sncr.saw.security.app.repository;

import com.sncr.saw.security.common.bean.Valid;
import com.sncr.saw.security.common.bean.repo.dsk.AttributeValues;
import com.sncr.saw.security.common.bean.repo.dsk.DskDetails;
import com.sncr.saw.security.common.bean.repo.dsk.SecurityGroups;
import com.sncr.saw.security.common.bean.repo.dsk.UserAssignment;

import java.util.List;

public interface DataSecurityKeyRepository {
    public Valid addSecurityGroups(SecurityGroups securityGroups,String createdBy,Long custId);
    public Valid updateSecurityGroups(Long securityGroupId,List<String> groupNames,Long custId);
    public Valid deleteSecurityGroups(Long securityGroupId);
    public List<SecurityGroups> fetchSecurityGroupNames(Long custId);
    public Valid addSecurityGroupDskAttributeValues(Long securityGroupId, AttributeValues attributeValues);
    public List<String> fetchSecurityGroupDskAttributes(Long securityGroupId);
    public Valid deleteSecurityGroupDskAttributeValues(List<String> dskList);
    public List<DskDetails> fetchDskAllAttributeValues(Long securityGroupId);
    public Valid updateUser(String securityGroupName,Long userSysId, Long custId);
    public List<UserAssignment> getAllUserAssignments(Long custId);
    public Valid updateAttributeValues(Long securityGroupId,AttributeValues attributeValues);
}
