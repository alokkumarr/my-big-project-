package com.sncr.saw.security.app.admin;

import com.sncr.saw.security.app.model.Customer;
import com.sncr.saw.security.app.repository.impl.CustomerProductModuleFeatureRepositoryDaoImpl;
import com.sncr.saw.security.app.repository.impl.CustomerProductModuleRepositoryDaoImpl;
import com.sncr.saw.security.app.repository.impl.CustomerProductRepositoryDaoImpl;
import com.sncr.saw.security.app.repository.impl.CustomerRepositoryDaoImpl;
import com.sncr.saw.security.app.repository.impl.ModuleRepositoryDaoImpl;
import com.sncr.saw.security.app.repository.impl.ProductModuleRepositoryDaoImpl;
import com.sncr.saw.security.app.repository.impl.ProductRepositoryDaoImpl;
import com.sncr.saw.security.app.repository.impl.RoleRepositoryDaoImpl;
import com.sncr.saw.security.app.repository.impl.UserRepositoryImpl;
import com.sncr.saw.security.app.service.OnBoardService;
import com.sncr.saw.security.common.bean.Role;
import com.sncr.saw.security.common.bean.User;
import java.util.Map;
import java.util.Scanner;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;
import org.springframework.shell.standard.ShellOption;

/**
 * Created by Pawan
 *
 */


@ShellComponent
class SawSecurityShell {

	@Autowired
	public OnBoardService onboard;

	@ShellMethod("Onboard the customer")
	public void onboardCustomer() {
		try {
			CustomerRepositoryDaoImpl custDao = onboard.getCustomerDao();
			// check if connection is working fine only then proceed
			if (custDao.testSql()==1) {

				// customer creation
				Long created_cust_id = customerCreation(custDao);
				System.out.println("Generated CUSTOMER_SYS_ID: " + created_cust_id);

				// role creation
				RoleRepositoryDaoImpl roleDao = onboard.getRolesDao();
				Long created_role_id = roleDao.createNewRoleDao(created_cust_id);
				System.out.println("Generated ROLE_SYS_ID: " + created_role_id);

				// user creation
				Long created_user_id = userCreation(created_role_id, created_cust_id);

				// product creation
				Map<Integer, String> products_created_ids = createProducts();

				// module creation
				Map<Integer, String> modules_created_ids = createModules();

				// product modules linkage creation
				Map<Integer, String> prod_module_linkage_ids = createProductModuleLinkages();

				// customer product linkages
				Map<Integer, String> cust_prod_linkage_ids = createCustomerProductLinkages();

				// customer product module linkages
				Map<Integer, String> cust_prod_mod_linkage_ids = createCustomerProductModuleLinkages();

				// customer product module feature linkages
				Map<Integer, String> cust_prod_mod_feature_linkage_ids = createCustomerProductModuleFeatureLinkages();

			} else {
				// connection is not working fine
				// needs to be sysout only
				System.out.println("Please check your connection");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private Long customerCreation(CustomerRepositoryDaoImpl custDao) {
		// customer creation
		Customer customer = new Customer();
		Scanner scanner = new Scanner(System.in);
		System.out.print("Enter CUSTOMER_CODE: ");
		customer.setCustCode(scanner.next());
		System.out.print("Enter COMPANY_NAME: ");
		customer.setCompanyName(scanner.next());
		System.out.print("Enter COMPANY_BUSINESS: ");
		customer.setCompanyBusiness(scanner.next());
		System.out.print("Enter LANDING_PROD_SYS_ID: ");
		customer.setLandingProdSysId(scanner.nextLong());
		customer.setActiveStatusInd(1);
		customer.setCreatedBy("admin");
		// default keep 30
		customer.setPasswordExpiryDate(30);
		System.out.println("Enter DOMAIN_NAME: ");
		customer.setDomainName(scanner.next());
		return custDao.createNewCustomerDao(customer);
	}

	private Long userCreation(Long roleId, Long custId) {
		User user = new User();
		Scanner scanner = new Scanner(System.in);
		System.out.print(" Enter USER_ID: ");
		user.setUserId(scanner.nextLong());
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
		System.out.println("Enter MASTER_LOGIN: ");
		user.setMasterLoginId(scanner.next());
		// modify this
		return 1L;
	}

	public Map<Integer, String> createProducts() {
		ProductRepositoryDaoImpl product = onboard.getProductsDao();
		return product.createProductForOnboarding();
	}

	public Map<Integer, String> createModules() {
		ModuleRepositoryDaoImpl module = onboard.getModulesDao();
		return module.createModuleForOnboarding();
	}

	public Map<Integer, String> createProductModuleLinkages() {
		ProductModuleRepositoryDaoImpl prodModule = onboard.getProdModulesDao();
		return prodModule.createProductModuleLinkageForOnboarding();
	}

	public Map<Integer, String> createCustomerProductLinkages() {
		CustomerProductRepositoryDaoImpl custProd = onboard.getCustProductsDao();
		return custProd.createCustomerProductLinkageForOnboarding();
	}

	public Map<Integer, String> createCustomerProductModuleLinkages() {
		CustomerProductModuleRepositoryDaoImpl custProdMod = onboard.getCustProdModulesDao();
		return custProdMod.createCustomerProductModuleLinkageForOnboarding();
	}

	public Map<Integer, String> createCustomerProductModuleFeatureLinkages() {
		CustomerProductModuleFeatureRepositoryDaoImpl custProdModFeatures = onboard.getCustProdModuleFeaturesDao();
		custProdModFeatures.createCustomerProductModuleFeatureLinkageForOnboarding();
	}

}
