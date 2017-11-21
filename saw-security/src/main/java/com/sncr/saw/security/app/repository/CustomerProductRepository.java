package com.sncr.saw.security.app.repository;

import java.util.Map;

public interface CustomerProductRepository {

  Map<Integer, String> createCustomerProductLinkageForOnboarding(Long custId,
      Long prod_sys_id);
}
