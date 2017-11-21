package com.sncr.saw.security.app.repository;

import java.util.Map;

public interface CustomerProductModuleRepository {
  Map<Integer, String> createCustomerProductModuleLinkageForOnboarding(Long custProdId,
      Long prodModId, Long custId);
  void displayCustProdModules(Long custId);
  boolean checkCustProdModExistance(Long custProdModId, Long custId);
}
