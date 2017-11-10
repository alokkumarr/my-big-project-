package com.sncr.saw.security.app.repository;
import com.sncr.saw.security.common.bean.Customer;

/**
 * Created by pawan.
 */
public interface RoleRepository {
    long createNewRoleDao(Long custId);
}
