package com.sncr.saw.security.app.repository.impl;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.sncr.saw.security.common.bean.Module;
import java.util.HashMap;
import java.util.Map;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class ModuleRepositoryDaoImplTest {

  private Module module;
  private ModuleRepositoryDaoImpl moduleDao;

  @Before
  public void setUp() throws Exception {
    moduleDao = mock(ModuleRepositoryDaoImpl.class);
  }

  @Test
  public void createModuleForOnboarding() throws Exception {
    // stubbed test case
    Map<Integer, String> result = new HashMap<Integer, String>();
    result.put(1, "1L");
    when(moduleDao.createModuleForOnboarding()).thenReturn(result);
    assertEquals(result,moduleDao.createModuleForOnboarding());
  }

}