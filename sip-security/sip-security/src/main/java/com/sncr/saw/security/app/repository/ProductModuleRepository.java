package com.sncr.saw.security.app.repository;

import com.sncr.saw.security.common.bean.repo.ProductModuleDetails;

import java.util.List;
import java.util.Map;

public interface ProductModuleRepository {
  Map<Integer, String> createProductModuleLinkageForOnboarding();
  List getProductModules();
  boolean checkProductModuleExistance(Long prodModId);
  List<ProductModuleDetails> getModuleProductName(String masterLoginId);
}
