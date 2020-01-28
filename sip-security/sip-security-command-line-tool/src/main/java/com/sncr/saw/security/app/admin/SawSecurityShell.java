package com.sncr.saw.security.app.admin;

import com.sncr.saw.security.app.model.Customer;
import com.sncr.saw.security.app.repository.impl.*;
import com.sncr.saw.security.app.service.OnBoardService;
import com.sncr.saw.security.common.bean.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

/**
 * Created by Pawan
 */

/** -- Depricated
 * This File is no longer used. A separate file OnBoardShell.java is written to address the needs of onBoarding customer.
 * This File will be removed in future by a separate ticket while cleaning up Saw_security.
 */
//@ShellComponent
class SawSecurityShell {

  private static final Logger logger = LoggerFactory.getLogger(SawSecurityShell.class);

 // @Autowired
  private OnBoardService onboard;

 // @ShellMethod("Onboard the customer")
  public void onboardCustomer() {
    try {
      CustomerRepositoryDaoImpl custDao = onboard.getCustomerDao();
      // check if connection is working fine only then proceed
      if (custDao.testSql() == 1) {

        // display all the products
        display(onboard.getProductsDao().getProductList());

        // customer creation
        Long created_cust_id = customerCreation(custDao);
        logger.info("Created user with ID: " + created_cust_id);

        // customer product linkages
        display(onboard.getProductsDao().getProductList());

        // create customer product linkages
        // it's just going to be 1
        Map<Integer, String> cust_prod_linkage_ids = createCustomerProductLinkages(created_cust_id);
        String created_cust_prod_id = cust_prod_linkage_ids.get(1);
        logger.info("Created CUST_PROD entry with ID: " + created_cust_prod_id);

        // display product modules
        display(onboard.getProdModulesDao().getProductModules());

        // customer product module linkages
        Map<Integer, String> cust_prod_mod_linkage_ids = createCustomerProductModuleLinkages(
            Long.parseLong(created_cust_prod_id), created_cust_id);

        // customer product module feature linkages
        // to create canned and my analysis
        Map<Integer, String> cust_prod_mod_feature_linkage_ids = createCustomerProductModuleFeatureLinkages(
            Long.parseLong(cust_prod_mod_linkage_ids.get(1)));

        //create admin role
        Long createdAdminRoleSysId = createAdminRole(created_cust_id);
        logger.info("Created Admin Role for above customer with ID: " + createdAdminRoleSysId);

        // create Admin user
        Long createdAdminUserSysId = createAdminUser(createdAdminRoleSysId, created_cust_id);
        logger.info("Created Admin user with ID: " + createdAdminUserSysId);

        // At the very end add just one privilege for admin user by admin user
//        Long createdPrivilegeSysId = createPrivilegeForAdminUser(
//            Long.parseLong(created_cust_prod_id),
//            Long.parseLong(cust_prod_mod_linkage_ids.get(1)),
//            Long.parseLong(cust_prod_mod_feature_linkage_ids.get(1)),
//            createdAdminRoleSysId);
        Long createdPrivilegeSysId = createPrivilegeForAdminUser(
            Long.parseLong(created_cust_prod_id),
            0L,
            0L,
            createdAdminRoleSysId);
        logger.info("Generated Privilege ID for Admin user: " + createdPrivilegeSysId);

      } else {
        // connection is not working fine
        logger.info("Can not connect to database");
      }
    } catch (Exception e) {
      logger.error(e.toString());
    }
  }
// Test out any shell related things here
//  @ShellMethod("Dummy Command")
//  public void dummyCommand() {
//    onboard.getCustProdModulesDao().displayCustProdModules(1L);
//  }

  private Long customerCreation(CustomerRepositoryDaoImpl custDao) {

    System.out.println("====== CUSTOMERS INFORMATION ======");
    // customer creation
    Customer customer = new Customer();
    Scanner scanner = new Scanner(System.in);
    logger.info("Enter CUSTOMER_CODE: (UNIQUE CODE TO IDENTIFY your company / division) ");
    customer.setCustCode(scanner.next());
    logger.info("Enter COMPANY NAME: ");
    customer.setCompanyName(scanner.next());
    logger.info("Enter COMPANY BUSINESS: ");
    customer.setCompanyBusiness(scanner.next());
    boolean truth = false;
    Long prod_sys_id = -1L;
    while (truth == false) {
      // do it until they enter existing product ID
      logger.info("Enter PRODUCT ID from above for default landing page: ");
      prod_sys_id = scanner.nextLong();
      truth = onboard.getProductsDao().checkProductExistance(prod_sys_id);
      if (!truth) {
        logger.info("Entered PRODUCT ID does not exist!!!");
      }
    }
    customer.setLandingProdSysId(prod_sys_id);
    customer.setActiveStatusInd(1);
    customer.setCreatedBy("admin");
    // default keep 30
    customer.setPasswordExpiryDate(30);
    logger.info("Enter DOMAIN_NAME: ");
    customer.setDomainName(scanner.next());
    return custDao.createNewCustomerDao(customer);
  }

  private Long createAdminUser(Long roleId, Long custId) {
    logger.info("====== USERS TABLE for ADMIN USER ======");
    User user = new User();
    UserRepositoryImpl userdao = onboard.getUsersDao();
    Scanner scanner = new Scanner(System.in);
    logger.info("Enter MASTER_LOGIN: ");
    user.setMasterLoginId(scanner.next());
    logger.info(" Enter EMAIL: ");
    user.setEmail(scanner.next());
    user.setRoleId(roleId);
    user.setCustomerId(custId);
    logger.info("Enter PASSWORD: ");
    user.setPassword(scanner.next());
    logger.info("Enter FIRST_NAME: ");
    user.setFirstName(scanner.next());
    logger.info("Enter MIDDLE_NAME: ");
    user.setMiddleName(scanner.next());
    logger.info("Enter LAST_NAME: ");
    user.setLastName(scanner.next());
    user.setActiveStatusInd("1");
    return userdao.createAdminUserForOnboarding(user);
  }

  public Map<Integer, String> createCustomerProductLinkages(Long custId) {
    logger.info("====== CUSTOMER_PRODUCTS TABLE ======");
    CustomerProductRepositoryDaoImpl custProd = onboard.getCustProductsDao();
    Scanner scanner = new Scanner(System.in);
    boolean truth = false;
    Long prod_sys_id = -1L;
    while (truth == false) {
      // do it until they enter existing product ID
      logger.info("Enter PRODUCT_SYS_ID: ");
      prod_sys_id = scanner.nextLong();
      truth = onboard.getProductsDao().checkProductExistance(prod_sys_id);
      if (!truth) {
        logger.info("Entered PRODUCT_SYS_ID does not exist!!!");
      }
    }
    return custProd.createCustomerProductLinkageForOnboarding(custId, prod_sys_id);
  }

  public Map<Integer, String> createCustomerProductModuleLinkages(Long custProdId,
      Long custId) {
    logger.info("====== CUSTOMER PRODUCT MODULES ======");
    CustomerProductModuleRepositoryDaoImpl custProdMod = onboard.getCustProdModulesDao();
    Map<Integer, String> results = new HashMap<Integer, String>();
    Scanner scanner = new Scanner(System.in);
    boolean addMoreModules = true;
    Integer count = 1;

    while (addMoreModules) {
      boolean truth = false;
      Long prodModId = -1L;
      while (truth == false) {
        // do until they enter correct product module ID
        logger.info("Enter MODULE_ID (from above shown values): ");
        prodModId = scanner.nextLong();
        truth = onboard.getProdModulesDao().checkProductModuleExistance(prodModId);
        if (!truth) {
          logger.info("Entered PROD_MOD_SYS_ID does not exist!!!");
        }
      }
      results.put(count, custProdMod
          .createCustomerProductModuleLinkageForOnboarding(custProdId, prodModId, custId)
          .get(1));
      count++;
      logger.info("Enter more? (yes/no): ");
      String temp = scanner.next();
      if (temp.equals("yes") || temp.equals("y") || temp.equals("Y") || temp.equals("YES")) {
        addMoreModules = true;
      } else {
        addMoreModules = false;
      }
    }

    return results;
  }

  public Map<Integer, String> createCustomerProductModuleFeatureLinkages(Long custProdModId) {
    System.out.println("====== ASSOCIATING DEFAULT FEATURES ======");
    CustomerProductModuleFeatureRepositoryDaoImpl custProdModFeatures = onboard
        .getCustProdModuleFeaturesDao();
    return custProdModFeatures
        .createCustomerProductModuleFeatureLinkageForOnboarding(custProdModId);
  }

  public long createAdminRole(Long custId) {
    System.out.println("====== CREATING ADMIN ROLE ======");
    RoleRepositoryDaoImpl adminRole = onboard
        .getRolesDao();
    return adminRole.createNewAdminRoleDao(custId);
  }

  public long createPrivilegeForAdminUser(Long custProdSysId, Long custProdModSysId,
      Long custProdModFeatureSysId, Long roleSysId) {
    System.out.println("====== CREATING PRIVILEGES FOR ADMIN ======");
    PrivilegeRepositoryDao priv = onboard.getPrivRepoDao();
    return priv
        .createNewPrivilegeDao(custProdSysId, custProdModSysId, custProdModFeatureSysId, roleSysId);
  }

  public void display(List rs) {
    for (Object x:rs) {
      System.out.println(x);
    }
  }


}
