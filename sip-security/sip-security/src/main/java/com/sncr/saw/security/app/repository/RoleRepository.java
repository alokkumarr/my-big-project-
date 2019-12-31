package com.sncr.saw.security.app.repository;

import com.sncr.saw.security.common.bean.Role;
import com.sncr.saw.security.common.bean.repo.admin.role.RoleDetails;


/**
 * Created by pawan.
 */
public interface RoleRepository {
    long createNewAdminRoleDao(Long custId);
    RoleDetails fetchRoleByIdAndCustomerCode(Long loginId, Role role);
}
