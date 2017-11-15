package com.sncr.saw.security.app.admin;

import com.sncr.saw.security.app.model.Customer;
import com.sncr.saw.security.app.repository.impl.CustomerRepositoryDaoImpl;
import com.sncr.saw.security.app.repository.impl.RoleRepositoryDaoImpl;
import com.sncr.saw.security.app.repository.impl.UserRepositoryImpl;
import com.sncr.saw.security.common.bean.Role;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;
import org.springframework.shell.standard.ShellOption;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.Scanner;

/**
 * Created by Pawan
 *
 */


@ShellComponent
class SawSecurityShell {

	@Autowired
	public CustomerRepositoryDaoImpl custDao;

	@Autowired
	public RoleRepositoryDaoImpl roleDao;

	@Autowired
	public UserRepositoryImpl usr;

	public Customer customer;
	public Role role;

	@ShellMethod("Create New Customer")
	public boolean createNewCustomer()
	{
		Scanner ss = new Scanner(System.in);
		String custCode;				// NOT NULL
		String companyName;				// NOT NULL
		String companyBusiness = null;	// default NULL
		Long landingProdSysId;			// NOT NULL
		Integer activeStatusInd;		// NOT NULL
		String createdBy;				// NOT NULL
		String inactivatedBy = null;	// default NULL
		String modifiedBy = null;		// default NULL
		Integer passwordExpiryDate;		// NOT NULL
		String domainName;				// NOT NULL

		// input logic
		System.out.print("Customer Code: ");
		custCode = ss.next();

		// do not allow the user to enter invalid cust code
		while(!custDao.isValidCustCode(custCode)) {
			System.out.println("Please re-enter customer code as special characters are not allowed.");
			System.out.print("Customer Code: ");
			custCode = ss.next();
		}

		System.out.print("Company Name: ");
		companyName = ss.next();
		System.out.print("Company Business: ");
		companyBusiness = ss.next();


		// logic to create new customer
		if (custDao.isValidCustCode(custCode)) {
			try {
				// set the customer object
				customer = new Customer();
				customer.setCustCode(custCode);
				customer.setCompanyName(companyName);
				customer.setCompanyBusiness(companyBusiness);
				customer.setLandingProdSysId(landingProdSysId);
				customer.setActiveStatusInd(activeStatusInd);
				customer.setCreatedBy(createdBy);
				customer.setInactivatedBy(inactivatedBy);
				customer.setModifiedBy(modifiedBy);
				customer.setPasswordExpiryDate(passwordExpiryDate);
				customer.setDomainName(domainName);

				long generatedCustID = custDao.createNewCustomerDao(customer);
				System.out.println("Generated Customer ID: " + generatedCustID);

			} catch (Exception e) {
				// ToDo: replace with logger logic
				e.printStackTrace();
			} finally {
				//destroy the created object
				customer = null;
			}

		} else {
			// some error shit
		}
		return true;
	}

	@ShellMethod("Create roles for customer's users")
	public boolean createRoles(
			// use custSysId from output of create-new-customer 's output.
			@ShellOption() Long custSysId
			/*@ShellOption() String roleName,
			@ShellOption() String roleCode,
			@ShellOption(defaultValue = ShellOption.NULL) String roleDesc,
			@ShellOption() String roleType,
			@ShellOption(defaultValue = ShellOption.NULL) String dataSecurityKey,
			@ShellOption() String activeStatusInd,
			@ShellOption() String createdBy,
			@ShellOption(defaultValue = ShellOption.NULL) String inactivatedBy,
			@ShellOption(defaultValue = ShellOption.NULL) String modifiedBy*/
	)
	{
		// logic to create roles for customer
		// ROLE_CODE is unique so take care of it
		// Also DATA_SECURITY_KEY is by default NULL
		try {
			/*role = new Role();
			role.setCustSysId(custSysId);
			role.setRoleName(roleName);
			role.setRoleCode(roleCode);
			role.setRoleDesc(roleDesc);
			role.setRoleType(roleType);
			role.setDataSecurityKey(dataSecurityKey);
			role.setActiveStatusInd(activeStatusInd);
			role.setCreatedBy(createdBy);
			role.setInactivatedBy(inactivatedBy);
			role.setModifiedBy(modifiedBy);*/
			long generatedRoleID = roleDao.createNewRoleDao(custSysId);
			System.out.println("Generated Role ID: "+ generatedRoleID);
		} catch (Exception e) {
			// Replace with logger logic
			e.printStackTrace();
		} finally {
			role = null;
		}
		return true;
	}

	@ShellMethod("Create new User")
	public boolean createNewUser()
	{
		// logic to create new user
		// only one user needs to be created if it's command line
		return true;
	}

	@ShellMethod("Associate respective products with customer")
	public boolean createNewCustProduct()
	{
		// logic to create new Product
		return true;
	}

	@ShellMethod("Create Product Feature for customer")
	public boolean createProductModuleFeatures(Long custId)
	{
		// logic to create product module features
		// take care of feature code as it's unique
		return true;
	}



	@ShellMethod("Create Privileges for customer")
	public boolean createPrivileges()
	{
		return true;
	}


}
