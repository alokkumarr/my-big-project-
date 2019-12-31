package com.sncr.saw.security.app.repository.impl;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.HashMap;
import java.util.Map;

import com.sncr.saw.security.common.bean.repo.ProductModuleDetails;
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

  @Test
  public void fetchModuleProductDetail() throws Exception {
    ProductModuleDetails details = new ProductModuleDetails();
    details.setModuleId(1L);
    details.setProductId(2L);
    details.setCustomerSysId(1L);
    when(prodModDao.fetchModuleProductDetail("sawadmin@synchronoss.com", "SAW Demo","OBSERVE")).thenReturn(details);
    assertEquals("1", details.getModuleId().toString());
    assertEquals("2", details.getProductId().toString());
  }
}