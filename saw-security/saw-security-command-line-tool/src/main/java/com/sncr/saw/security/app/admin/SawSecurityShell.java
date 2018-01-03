package com.sncr.saw.security.app.admin;

import com.sncr.saw.security.app.model.Customer;
import com.sncr.saw.security.app.repository.impl.CustomerProductModuleFeatureRepositoryDaoImpl;
import com.sncr.saw.security.app.repository.impl.CustomerProductModuleRepositoryDaoImpl;
import com.sncr.saw.security.app.repository.impl.CustomerProductRepositoryDaoImpl;
import com.sncr.saw.security.app.repository.impl.CustomerRepositoryDaoImpl;
import com.sncr.saw.security.app.repository.impl.PrivilegeRepositoryDao;
import com.sncr.saw.security.app.repository.impl.RoleRepositoryDaoImpl;
import com.sncr.saw.security.app.repository.impl.UserRepositoryImpl;
import com.sncr.saw.security.app.service.OnBoardService;
import com.sncr.saw.security.common.bean.User;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;

/**
 * Created by Pawan
 */


@ShellComponent
class SawSecurityShell {

  private static final Logger logger = LoggerFactory.getLogger(SawSecurityShell.class);

  @Autowired
  public OnBoardService onboard;

  @ShellMethod("Onboard the customer")
  public void onboardCustomer() {
    try {
      CustomerRepositoryDaoImpl custDao = onboard.getCustomerDao();
      // check if connection is working fine only then proceed
      if (custDao.testSql() == 1) {

        // display all the products
        onboard.getProductsDao().displayProducts();

        // customer creation
        Long created_cust_id = customerCreation(custDao);
        System.out.println("Generated CUSTOMER_SYS_ID: " + created_cust_id);
        logger.info("Created user with ID: " + created_cust_id);

        // customer product linkages
        onboard.getProductsDao().displayProducts();

        // create customer product linkages
        // it's just going to be 1
        Map<Integer, String> cust_prod_linkage_ids = createCustomerProductLinkages(created_cust_id);
        String created_cust_prod_id = cust_prod_linkage_ids.get(1);
        System.out.println(created_cust_prod_id);
        System.out.println("Generated CUST_PROD_SYS_ID: " + created_cust_prod_id);
        logger.info("Created CUST_PROD entry with ID: " + created_cust_prod_id);

        // display product modules
        onboard.getProdModulesDao().displayProductModules();

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
        System.out.println("Generated User ID for current user is: " + createdAdminUserSysId);
        logger.info("Created Admin user with ID: " + createdAdminUserSysId);

        // At the very end add just one privilege for admin user by admin user
        Long createdPrivilegeSysId = createPrivilegeForAdminUser(
            Long.parseLong(created_cust_prod_id),
            Long.parseLong(cust_prod_mod_linkage_ids.get(1)),
            Long.parseLong(cust_prod_mod_feature_linkage_ids.get(1)),
            1L);
        logger.info("Generated Privilege ID for Admin user: " + createdPrivilegeSysId);

      } else {
        // connection is not working fine
        System.out.println("Please check your connection");
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
    System.out.print("Enter CUSTOMER_CODE: (UNIQUE CODE TO IDENTIFY your company / division) ");
    customer.setCustCode(scanner.next());
    System.out.print("Enter COMPANY NAME: ");
    customer.setCompanyName(scanner.next());
    System.out.print("Enter COMPANY BUSINESS: ");
    customer.setCompanyBusiness(scanner.next());
    boolean truth = false;
    Long prod_sys_id = -1L;
    while (truth == false) {
      // do it until they enter existing product ID
      System.out.print("Enter PRODUCT ID from above for default landing page: ");
      prod_sys_id = scanner.nextLong();
      truth = onboard.getProductsDao().checkProductExistance(prod_sys_id);
      if (!truth) {
        System.out.print("Entered PRODUCT ID does not exist!!!");
      }
    }
    customer.setLandingProdSysId(prod_sys_id);
    customer.setActiveStatusInd(1);
    customer.setCreatedBy("admin");
    // default keep 30
    customer.setPasswordExpiryDate(30);
    System.out.print("Enter DOMAIN_NAME: ");
    customer.setDomainName(scanner.next());
    return custDao.createNewCustomerDao(customer);
  }

  private Long createAdminUser(Long roleId, Long custId) {
    System.out.println("====== USERS TABLE for ADMIN USER ======");
    User user = new User();
    UserRepositoryImpl userdao = onboard.getUsersDao();
    Scanner scanner = new Scanner(System.in);
    System.out.println("Enter MASTER_LOGIN: ");
    user.setMasterLoginId(scanner.next());
    System.out.print(" Enter EMAIL: ");
    user.setEmail(scanner.next());
    user.setRoleId(roleId);
    user.setCustomerId(custId);
    System.out.print("Enter PASSWORD: ");
    user.setPassword(scanner.next());
    System.out.println("Enter FIRST_NAME: ");
    user.setFirstName(scanner.next());
    System.out.println("Enter MIDDLE_NAME: ");
    user.setMiddleName(scanner.next());
    System.out.println("Enter LAST_NAME: ");
    user.setLastName(scanner.next());
    user.setActiveStatusInd("1");
    return userdao.createAdminUserForOnboarding(user);
  }

  public Map<Integer, String> createCustomerProductLinkages(Long custId) {
    System.out.println("====== CUSTOMER_PRODUCTS TABLE ======");
    CustomerProductRepositoryDaoImpl custProd = onboard.getCustProductsDao();
    Scanner scanner = new Scanner(System.in);
    boolean truth = false;
    Long prod_sys_id = -1L;
    while (truth == false) {
      // do it until they enter existing product ID
      System.out.print("Enter PRODUCT_SYS_ID: ");
      prod_sys_id = scanner.nextLong();
      truth = onboard.getProductsDao().checkProductExistance(prod_sys_id);
      if (!truth) {
        System.out.println("Entered PRODUCT_SYS_ID does not exist!!!");
      }
    }
    return custProd.createCustomerProductLinkageForOnboarding(custId, prod_sys_id);
  }

  public Map<Integer, String> createCustomerProductModuleLinkages(Long custProdId,
      Long custId) {
    System.out.println("====== CUSTOMER PRODUCT MODULES ======");
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
        System.out.println("Enter MODULE_ID (from above shown values): ");
        prodModId = scanner.nextLong();
        truth = onboard.getProdModulesDao().checkProductModuleExistance(prodModId);
        if (!truth) {
          System.out.println("Entered PROD_MOD_SYS_ID does not exist!!!");
        }
      }
      results.put(count, custProdMod
          .createCustomerProductModuleLinkageForOnboarding(custProdId, prodModId, custId)
          .get(1));
      count++;
      System.out.print("Enter more? (yes/no): ");
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


}