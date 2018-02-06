package com.sncr.saw.security.app.repository.impl;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.junit.Before;
import org.junit.Test;

public class RoleRepositoryDaoImplTest {

  private RoleRepositoryDaoImpl roleRepoDao;

  @Before
  public void setUp() throws Exception {
    roleRepoDao = mock(RoleRepositoryDaoImpl.class);
  }

  @Test
  public void createNewAdminRoleDao() throws Exception {
    // stubbed environment
    Long custId = 10L;
    when(roleRepoDao.createNewAdminRoleDao(custId)).thenReturn(100L);
    assertEquals(100L, roleRepoDao.createNewAdminRoleDao(custId));
  }

}