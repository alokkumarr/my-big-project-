package com.sncr.saw.security.app.repository;

import com.sncr.saw.security.common.bean.repo.dsk.AttributeValues;
import com.sncr.saw.security.common.bean.repo.dsk.SecurityGroups;
import com.sncr.saw.security.common.bean.repo.dsk.UserAssignment;

import java.util.List;
import java.util.Map;

public interface DataSecurityKeyRepository {
    public Boolean addSecurityGroups(SecurityGroups securityGroups);
    public Boolean updateSecurityGroups(List<SecurityGroups> groupNames);
    public Boolean deleteSecurityGroups(String securityGroupName, String userId);
    public List<SecurityGroups> fetchSecurityGroupNames();
    public Boolean addSecurityGroupDskAttributeValues(AttributeValues attributeValues);
    public List<String> fetchSecurityGroupDskAttributes(String securityGroupName);
    public Boolean deleteSecurityGroupDskAttributeValues(List<String> dskList);
    public List<AttributeValues> fetchDskAllAttributeValues(String securityGroupName);
    public Boolean updateUser(String securityGroupName,String userId);
    public List<UserAssignment> getAlluserAssignments();
    public Boolean updateAttributeValues(AttributeValues attributeValues);
}
