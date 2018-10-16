package com.sncr.saw.security.app.repository;

import com.sncr.saw.security.common.bean.Valid;
import com.sncr.saw.security.common.bean.repo.dsk.AttributeValues;
import com.sncr.saw.security.common.bean.repo.dsk.DskDetails;
import com.sncr.saw.security.common.bean.repo.dsk.SecurityGroups;
import com.sncr.saw.security.common.bean.repo.dsk.UserAssignment;

import java.util.List;

public interface DataSecurityKeyRepository {
    public Valid addSecurityGroups(SecurityGroups securityGroups,String createdBy);
    public Valid updateSecurityGroups(List<String> groupNames);
    public Boolean deleteSecurityGroups(String securityGroupName, String userId);
    public List<SecurityGroups> fetchSecurityGroupNames();
    public Boolean addSecurityGroupDskAttributeValues(AttributeValues attributeValues);
    public List<String> fetchSecurityGroupDskAttributes(String securityGroupName);
    public Boolean deleteSecurityGroupDskAttributeValues(List<String> dskList);
    public List<DskDetails> fetchDskAllAttributeValues(String securityGroupName);
    public Boolean updateUser(String securityGroupName,String userId);
    public List<UserAssignment> getAlluserAssignments();
    public Boolean updateAttributeValues(AttributeValues attributeValues);
}
