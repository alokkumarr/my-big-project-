package com.sncr.saw.security.app.repository;

import com.sncr.saw.security.app.model.DskGroupPayload;
import com.sncr.saw.security.app.model.SipDskAttribute;
import com.sncr.saw.security.common.bean.Valid;
import com.sncr.saw.security.common.bean.repo.dsk.*;

import java.util.List;

 interface DataSecurityKeyRepository {
    DskValidity addSecurityGroups(SecurityGroups securityGroups, String createdBy, Long custId);
    DskValidity updateSecurityGroups(Long securityGroupId,List<String> groupNames,Long custId);
    Valid deleteSecurityGroups(Long securityGroupId);
    List<SecurityGroups> fetchSecurityGroupNames(Long custId);

    Valid addDskGroupAttributeModelAndValues(Long securityGroupId, SipDskAttribute dskAttribute);
    Valid deleteDskGroupAttributeModel(Long securityGroupId, Long customerId);

    Valid addSecurityGroupDskAttributeValues(Long securityGroupId, AttributeValues attributeValues);
    List<String> fetchSecurityGroupDskAttributes(Long securityGroupId);
     Valid deleteSecurityGroupDskAttributeValues(List<String> dskList);
     List<DskDetails> fetchDskAllAttributeValues(Long securityGroupId);
    DskGroupPayload fetchDskGroupAttributeModel (Long securityGroupId, Long customerId);
     Valid updateUser(String securityGroupName,Long userSysId, Long custId);
     List<UserAssignment> getAllUserAssignments(Long custId);
     Valid updateAttributeValues(Long securityGroupId,AttributeValues attributeValues);
}
