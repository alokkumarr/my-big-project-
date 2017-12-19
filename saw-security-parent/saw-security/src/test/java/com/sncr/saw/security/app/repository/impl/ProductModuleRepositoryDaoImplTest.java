package com.sncr.saw.security.app.repository.impl;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.HashMap;
import java.util.Map;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class ProductModuleRepositoryDaoImplTest {

  private ProductModuleRepositoryDaoImpl prodModDao;

  @Before
  public void setUp() throws Exception {
    prodModDao = mock(ProductModuleRepositoryDaoImpl.class);
  }


  @Test
  public void createProductModuleLinkageForOnboarding() throws Exception {
    Map<Integer, String> result = new HashMap<Integer, String>();
    result.put(1, "10");
    when(prodModDao.createProductModuleLinkageForOnboarding()).thenReturn(result);
    assertEquals(result, prodModDao.createProductModuleLinkageForOnboarding());

  }

}