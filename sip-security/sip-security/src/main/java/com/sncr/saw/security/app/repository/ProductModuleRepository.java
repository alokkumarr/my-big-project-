package com.sncr.saw.security.app.repository;

import java.util.List;
import java.util.Map;

public interface ProductModuleRepository {
  Map<Integer, String> createProductModuleLinkageForOnboarding();
  List getProductModules();
  boolean checkProductModuleExistance(Long prodModId);
  boolean validateModuleProductName(String productName, String moduleName,
                                    String masterLoginId);
}
