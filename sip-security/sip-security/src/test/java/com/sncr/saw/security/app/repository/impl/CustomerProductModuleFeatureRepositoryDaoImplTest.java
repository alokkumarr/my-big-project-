package com.sncr.saw.security.app.repository.impl;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.HashMap;
import java.util.Map;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class CustomerProductModuleFeatureRepositoryDaoImplTest {

  private CustomerProductModuleFeatureRepositoryDaoImpl custProdModFeatureDao;

  @Before
  public void setUp() throws Exception {
    custProdModFeatureDao = mock(CustomerProductModuleFeatureRepositoryDaoImpl.class);
  }

  @Test
  public void createCustomerProductModuleFeatureLinkageForOnboarding() throws Exception {
    Long custProdModId = 1L;
    Map<Integer, String> result = new HashMap<Integer, String>();
    result.put(1, "1");
    when(
        custProdModFeatureDao.createCustomerProductModuleFeatureLinkageForOnboarding(custProdModId))
        .thenReturn(result);
    assertEquals(result, custProdModFeatureDao
        .createCustomerProductModuleFeatureLinkageForOnboarding(custProdModId));
  }

}