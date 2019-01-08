package com.sncr.saw.security.app.repository.impl;

import com.sncr.saw.security.app.repository.ModulePrivilegeRepository;
import com.sncr.saw.security.common.bean.repo.ModulePrivileges;
import com.sncr.saw.security.common.bean.repo.PrivilegesForModule;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@Repository
public class ModulePrivilegeRepositoryDaoImpl implements ModulePrivilegeRepository {

    private static final Logger logger = LoggerFactory
        .getLogger(CustomerProductModuleFeatureRepositoryDaoImpl.class);
    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public ModulePrivilegeRepositoryDaoImpl(JdbcTemplate jdbcTemplate){this.jdbcTemplate = jdbcTemplate;}

    @Override
    public List<ModulePrivileges> getModulePrivileges() {
        String fetchSql = "Select mp.MODULE_PRIV_SYS_ID, m.MODULE_NAME, pc.PRIVILEGE_CODES_SYS_ID, pc.PRIVILEGE_CODES_NAME FROM MODULE_PRIVILEGES mp Inner Join Modules m on " +
            " (mp.MODULE_SYS_ID = m.MODULE_SYS_ID ) " +
            " inner join privilege_codes pc on " +
            " (mp.PRIVILEGE_CODES_SYS_ID = pc.PRIVILEGE_CODES_SYS_ID) ";

        List<ModulePrivileges> modulePrivilegesList = new ArrayList<>();
        try{
            modulePrivilegesList = jdbcTemplate.query(fetchSql, ps -> {}, resultSet -> {
                List<ModulePrivileges> tempModulePrivilegesList = new ArrayList<>();
                while (resultSet.next())    {
                    ModulePrivileges modulePrivileges = new ModulePrivileges();
                    modulePrivileges.setModulePrivSysId(resultSet.getLong("MODULE_PRIV_SYS_ID"));
                    modulePrivileges.setModuleName(resultSet.getString("MODULE_NAME"));
                    modulePrivileges.setPrivilegeCodeSysId(resultSet.getLong("PRIVILEGE_CODES_SYS_ID"));
                    modulePrivileges.setPrivilegeCodeName(resultSet.getString("PRIVILEGE_CODES_NAME"));
                    tempModulePrivilegesList.add(modulePrivileges);
                }
                logger.trace("Success reading Module Privileges");
                return tempModulePrivilegesList;
            });
        }
        catch (Exception e){
            logger.error(e.getMessage());
        }

        return modulePrivilegesList;
    }

    @Override
    public PrivilegesForModule getPrivilegeByModule(Long moduleSysId) {
        PrivilegesForModule privilegesForModule = new PrivilegesForModule();
        String fetchSql = "Select pc.PRIVILEGE_CODES_SYS_ID, pc.PRIVILEGE_CODES_NAME FROM MODULE_PRIVILEGES mp " +
            " Inner Join Modules m on (mp.MODULE_SYS_ID = m.MODULE_SYS_ID ) " +
            " inner join privilege_codes pc on (mp.PRIVILEGE_CODES_SYS_ID = pc.PRIVILEGE_CODES_SYS_ID) " +
            " where m.MODULE_SYS_ID=? ";
        HashMap<Long,String>  privilegeList = null;
        try{
            privilegeList = jdbcTemplate.query(fetchSql, ps -> { ps.setLong(1,moduleSysId); }, resultSet -> {
                HashMap<Long,String> privilege = new HashMap<>();
                while (resultSet.next())    {
                    privilege.put(resultSet.getLong("PRIVILEGE_CODES_SYS_ID"),resultSet.getString("PRIVILEGE_CODES_NAME"));
                }
                logger.trace("Success reading Module Privileges");
                return privilege;
            });
        }
        catch (Exception e){
            logger.error(e.getMessage());
        }
        privilegesForModule.setPriviliges(privilegeList);
        return privilegesForModule;
    }

}
