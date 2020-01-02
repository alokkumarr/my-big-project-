package com.sncr.saw.security.app.repository;

import com.sncr.saw.security.common.bean.Valid;
import com.sncr.saw.security.common.bean.repo.dsk.*;

import com.synchronoss.bda.sip.dsk.DskGroupPayload;
import com.synchronoss.bda.sip.dsk.SipDskAttribute;
import java.util.List;

public interface DataSecurityKeyRepository {
    public DskValidity addSecurityGroups(SecurityGroups securityGroups, String createdBy, Long custId);
    public DskValidity updateSecurityGroups(Long securityGroupId,List<String> groupNames,Long custId);
    public Valid deleteSecurityGroups(Long securityGroupId);
    public List<SecurityGroups> fetchSecurityGroupNames(Long custId);

    public Valid addDskGroupAttributeModelAndValues(Long securityGroupId, SipDskAttribute dskAttribute);
    public Valid deleteDskGroupAttributeModel(Long securityGroupId, Long customerId);

    public Valid addSecurityGroupDskAttributeValues(Long securityGroupId, AttributeValues attributeValues);
    public List<String> fetchSecurityGroupDskAttributes(Long securityGroupId);
    public Valid deleteSecurityGroupDskAttributeValues(List<String> dskList);
    public List<DskDetails> fetchDskAllAttributeValues(Long securityGroupId);
    public DskGroupPayload fetchDskGroupAttributeModel (Long securityGroupId, Long customerId);
    public List<DskGroupPayload> fetchAllDskGroupForCustomer(Long customerId);
    public Valid updateUser(String securityGroupName,Long userSysId, Long custId);
    public List<UserAssignment> getAllUserAssignments(Long custId);
    public Valid updateAttributeValues(Long securityGroupId,AttributeValues attributeValues);
}
