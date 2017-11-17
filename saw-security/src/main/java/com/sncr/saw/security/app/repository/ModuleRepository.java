package com.sncr.saw.security.app.repository;

import com.sncr.saw.security.common.bean.Module;

import java.util.Map;

public interface ModuleRepository {

    // returns how many products got created
    Map<Integer, String> createModuleForOnboarding();

    // updates one product
    boolean updateModule(Module prod);

    // deletes product based on "ID"
    boolean deleteModule(Long moduleId);

    // returns product based on "ID"
    Module getModule(Long moduleId);
}
