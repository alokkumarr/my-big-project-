package com.sncr.saw.security.app.repository;


import com.sncr.saw.security.app.model.Privilege;

/**
 * Created by pawan.
 */

public interface PrivilegeRepository {

  long createNewPrivilegeDao(Long custProdSysId, Long custProdModSysId, Long custProdModFeatureSysId, Long roleSysId);
  void displayPrivileges();
}
