package com.sncr.saw.security.app.repository;

import com.sncr.saw.security.common.bean.Role;

/**
 * Created by pawan.
 */
public interface RoleRepository {
    long createNewAdminRoleDao(Long custId);
    boolean validateRoleByIdAndCustomerCode(Long loginId, String customerCode);
    long createNewRoleDao(Long custId, Role role);
    long addNewRoleType(Role role);
}
