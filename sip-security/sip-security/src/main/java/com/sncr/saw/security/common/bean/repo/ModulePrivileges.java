package com.sncr.saw.security.common.bean.repo;

import java.io.Serializable;

public class ModulePrivileges implements Serializable {
    private static final long serialVersionUID = 7684700543251635559L;

    private long moduleSysId;
    private long modulePrivSysId;

    public boolean isValid() {
        return valid;
    }

    public void setValid(boolean valid) {
        this.valid = valid;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    private boolean valid;

    private String message;

    public long getModuleSysId() {
        return this.moduleSysId;
    }

    public void setModuleSysId(long moduleSysId) {
        this.moduleSysId = moduleSysId;
    }

    public long getModulePrivSysId() {
        return modulePrivSysId;
    }

    public void setModulePrivSysId(long modulePrivSysId) {
        this.modulePrivSysId = modulePrivSysId;
    }

    public String getModuleName() {
        return moduleName;
    }

    public void setModuleName(String moduleName) {
        this.moduleName = moduleName;
    }

    public long getPrivilegeCodeSysId() {
        return privilegeCodeSysId;
    }

    public void setPrivilegeCodeSysId(long privilegeCodeSysId) {
        this.privilegeCodeSysId = privilegeCodeSysId;
    }

    public String getPrivilegeCodeName() {
        return privilegeCodeName;
    }

    public void setPrivilegeCodeName(String privilegeCodeName) {
        this.privilegeCodeName = privilegeCodeName;
    }

    private String moduleName;
    private long privilegeCodeSysId;
    private String privilegeCodeName;

}
