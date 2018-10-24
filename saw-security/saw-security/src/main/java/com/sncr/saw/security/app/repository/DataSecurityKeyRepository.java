package com.sncr.saw.security.app.repository;

import com.sncr.saw.security.common.bean.Valid;
import com.sncr.saw.security.common.bean.repo.dsk.AttributeValues;
import com.sncr.saw.security.common.bean.repo.dsk.DskDetails;
import com.sncr.saw.security.common.bean.repo.dsk.SecurityGroups;
import com.sncr.saw.security.common.bean.repo.dsk.UserAssignment;

import java.util.List;

public interface DataSecurityKeyRepository {
    public Valid addSecurityGroups(SecurityGroups securityGroups,String createdBy);
    public Valid updateSecurityGroups(Long securityGroupId,List<String> groupNames);
    public Boolean deleteSecurityGroups(Long securityGroupId);
    public List<SecurityGroups> fetchSecurityGroupNames();
    public Valid addSecurityGroupDskAttributeValues(Long securityGroupId, AttributeValues attributeValues);
    public List<String> fetchSecurityGroupDskAttributes(Long securityGroupId);
    public Boolean deleteSecurityGroupDskAttributeValues(List<String> dskList);
    public List<DskDetails> fetchDskAllAttributeValues(Long securityGroupId);
    public Boolean updateUser(String securityGroupName,Long userSysId);
    public List<UserAssignment> getAllUserAssignments();
    public Boolean updateAttributeValues(Long securityGroupId,AttributeValues attributeValues);
}
