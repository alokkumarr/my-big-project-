package com.sncr.saw.security.app.repository.impl;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.HashMap;
import java.util.Map;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class CustomerProductRepositoryDaoImplTest {

  private CustomerProductRepositoryDaoImpl custProdRepo;

  @Before
  public void setUp() throws Exception {
    // setup
    custProdRepo = mock(CustomerProductRepositoryDaoImpl.class);
  }


  @Test
  public void createCustomerProductLinkageForOnboarding() throws Exception {
    Long custId = 10L;
    Long prodSysId = 100L;
    Map<Integer, String> result = new HashMap<Integer, String>();
    result.put(1,"50");
    when(custProdRepo.createCustomerProductLinkageForOnboarding(custId, prodSysId)).thenReturn(result);
    assertEquals(result, custProdRepo.createCustomerProductLinkageForOnboarding(custId, prodSysId));
  }

}