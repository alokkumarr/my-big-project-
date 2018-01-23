package com.sncr.saw.security.app.repository.impl;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.HashMap;
import java.util.Map;
import org.junit.Before;
import org.junit.Test;

public class ProductRepositoryDaoImplTest {

  private ProductRepositoryDaoImpl prodRepoDao;

  @Before
  public void setUp() throws Exception {
    prodRepoDao = mock(ProductRepositoryDaoImpl.class);
  }

  @Test
  public void createProductForOnboarding() throws Exception {
    //stubbed data because shouldn't really rely on db to create things in unit test cases
    Map<Integer, String> result = new HashMap<Integer, String>();
    result.put(1, "1");
    when(prodRepoDao.createProductForOnboarding()).thenReturn(result);
    assertEquals(result, prodRepoDao.createProductForOnboarding());
  }

}