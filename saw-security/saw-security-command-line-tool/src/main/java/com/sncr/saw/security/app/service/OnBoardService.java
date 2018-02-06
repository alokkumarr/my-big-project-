package com.sncr.saw.security.app.service;

import com.sncr.saw.security.app.repository.impl.CustomerProductModuleFeatureRepositoryDaoImpl;
import com.sncr.saw.security.app.repository.impl.CustomerProductModuleRepositoryDaoImpl;
import com.sncr.saw.security.app.repository.impl.CustomerProductRepositoryDaoImpl;
import com.sncr.saw.security.app.repository.impl.CustomerRepositoryDaoImpl;
import com.sncr.saw.security.app.repository.impl.ModuleRepositoryDaoImpl;
import com.sncr.saw.security.app.repository.impl.PrivilegeRepositoryDao;
import com.sncr.saw.security.app.repository.impl.ProductModuleRepositoryDaoImpl;
import com.sncr.saw.security.app.repository.impl.ProductRepositoryDaoImpl;
import com.sncr.saw.security.app.repository.impl.RoleRepositoryDaoImpl;
import com.sncr.saw.security.app.repository.impl.UserRepositoryImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class OnBoardService {

  public OnBoardService() {
  }

  @Autowired
  private CustomerRepositoryDaoImpl customerDao;

  @Autowired
  private RoleRepositoryDaoImpl rolesDao;

  @Autowired
  private UserRepositoryImpl usersDao;

  @Autowired
  private ProductRepositoryDaoImpl productsDao;

  @Autowired
  private ModuleRepositoryDaoImpl modulesDao;

  @Autowired
  private ProductModuleRepositoryDaoImpl prodModulesDao;

  @Autowired
  private CustomerProductRepositoryDaoImpl custProductsDao;

  @Autowired
  private CustomerProductModuleRepositoryDaoImpl custProdModulesDao;

  @Autowired
  private CustomerProductModuleFeatureRepositoryDaoImpl custProdModuleFeaturesDao;

  @Autowired
  private PrivilegeRepositoryDao privRepoDao;

  // assuming that privileges and roles will already be there as basic requirement.

  public CustomerRepositoryDaoImpl getCustomerDao() {
    return customerDao;
  }

  public RoleRepositoryDaoImpl getRolesDao() {
    return rolesDao;
  }

  public UserRepositoryImpl getUsersDao() {
    return usersDao;
  }

  public ProductRepositoryDaoImpl getProductsDao() {
    return productsDao;
  }

  public ModuleRepositoryDaoImpl getModulesDao() {
    return modulesDao;
  }

  public ProductModuleRepositoryDaoImpl getProdModulesDao() {
    return prodModulesDao;
  }

  public CustomerProductRepositoryDaoImpl getCustProductsDao() {
    return custProductsDao;
  }

  public CustomerProductModuleRepositoryDaoImpl getCustProdModulesDao() {
    return custProdModulesDao;
  }

  public CustomerProductModuleFeatureRepositoryDaoImpl getCustProdModuleFeaturesDao() {
    return custProdModuleFeaturesDao;
  }

  public PrivilegeRepositoryDao getPrivRepoDao() {
    return privRepoDao;
  }
}
