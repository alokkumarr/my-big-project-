package com.sncr.saw.security.app.repository.impl;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.sncr.saw.security.app.model.Customer;
import org.junit.Before;
import org.junit.Test;

public class CustomerRepositoryDaoImplTest {

  private Customer customer;
  private CustomerRepositoryDaoImpl custDao;

  @Before
  public void setUp() throws Exception {
    customer = mock(Customer.class);
    custDao = mock(CustomerRepositoryDaoImpl.class);
  }

  @Test
  public void isValidCustCode() throws Exception {
    String custCode = "SYNCHRONOSS";
    when(custDao.isValidCustCode(custCode)).thenReturn(true);
    assertEquals(true, custDao.isValidCustCode(custCode));
  }

  @Test
  public void createNewCustomerDao() throws Exception {
    when(custDao.createNewCustomerDao(customer)).thenReturn(10L);
    assertEquals(10L, custDao.createNewCustomerDao(customer));
  }

}