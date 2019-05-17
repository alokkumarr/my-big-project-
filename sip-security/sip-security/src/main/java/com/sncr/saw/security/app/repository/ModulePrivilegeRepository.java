package com.sncr.saw.security.app.repository;

import com.sncr.saw.security.common.bean.repo.ModulePrivileges;
import com.sncr.saw.security.common.bean.repo.PrivilegesForModule;

import java.util.List;

public interface ModulePrivilegeRepository {
    public List<ModulePrivileges> getModulePrivileges();
    public PrivilegesForModule getPrivilegeByModule(Long moduleSysId);
}
