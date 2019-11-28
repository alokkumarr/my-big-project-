package com.sncr.saw.security.app.repository;

/**
 * Created by pawan.
 */
public interface RoleRepository {
    long createNewAdminRoleDao(Long custId);
    boolean validateRoleByIdAndCustomerCode(Long loginId, String customerCode);
}
