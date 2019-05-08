package com.sncr.saw.security.app.repository.impl;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.HashMap;
import java.util.Map;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class CustomerProductModuleRepositoryDaoImplTest {

  private CustomerProductModuleRepositoryDaoImpl custPRodModRepoDao;

  @Before
  public void setUp() throws Exception {
    custPRodModRepoDao = mock(CustomerProductModuleRepositoryDaoImpl.class);
  }

  @Test
  public void createCustomerProductModuleLinkageForOnboarding() throws Exception {
    Long custProdId = 1L;
    Long prodModId = 1L;
    Long custId = 1L;
    Map<Integer, String> result = new HashMap<Integer, String>();
    result.put(1, "1");
    when(custPRodModRepoDao
        .createCustomerProductModuleLinkageForOnboarding(custProdId, prodModId, custId))
        .thenReturn(result);
    assertEquals(result, custPRodModRepoDao
        .createCustomerProductModuleLinkageForOnboarding(custProdId, prodModId, custId));
  }

}