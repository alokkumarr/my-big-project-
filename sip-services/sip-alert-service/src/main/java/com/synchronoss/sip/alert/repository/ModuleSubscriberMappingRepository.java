package com.synchronoss.sip.alert.repository;

import com.synchronoss.sip.alert.modal.ModuleName;
import com.synchronoss.sip.alert.modal.ModuleSubscriberMapping;
import java.util.List;
import org.springframework.data.repository.CrudRepository;

public interface ModuleSubscriberMappingRepository
    extends CrudRepository<ModuleSubscriberMapping, String> {
  List<ModuleSubscriberMapping> findAllByModuleIdAndModuleName(
      String moduleId, ModuleName moduleName);
}
