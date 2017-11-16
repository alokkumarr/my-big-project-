package com.sncr.saw.security.app.repository;

import java.util.Map;

public interface ProductModuleRepository {
  // returns how many products got created
  Map<Integer, String> createProductModuleLinkageForOnboarding();
}
