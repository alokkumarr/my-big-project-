package com.sncr.saw.security.app.repository.impl;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.sncr.saw.security.common.bean.Role;
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

  @Test
  public void validateRoleByIdAndCustomerCode() throws Exception {
    Long custId = 10L;
    Role role = new Role();
    role.setRoleCode("TESTCODE01");
    role.setRoleType("ADMIN");
    when(roleRepoDao.validateRoleByIdAndCustomerCode(custId, role)).thenReturn(true);
  }
}