package com.sncr.saw.security.app.repository;

import java.util.Map;

public interface ProductModuleRepository {

  // returns how many products got created
  Map<Integer, String> createProductModuleLinkageForOnboarding();

  // display product modules
  void displayProductModules();

  // check if product exist
  boolean checkProductModuleExistance(Long prodModId);
}
