/**
 * 
 */
package com.sncr.saw.security.app;

import com.sncr.saw.security.app.repository.impl.CustomerRepositoryDaoImpl;
import com.sncr.saw.security.app.repository.impl.UserRepositoryImpl;
import com.sncr.saw.security.common.bean.Customer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.context.embedded.tomcat.TomcatEmbeddedServletContainerFactory;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.boot.web.support.SpringBootServletInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;

import com.sncr.saw.security.common.bean.JwtFilter;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;
import org.springframework.shell.standard.ShellOption;

/**
 * @author gsan0003
 *
 */


@ShellComponent
class SawSecurityShell {

	@Autowired
	public CustomerRepositoryDaoImpl custDao;

	@Autowired
	public UserRepositoryImpl usr;

	public Customer customer;

	@ShellMethod("Get All users for a customer")
	public void getAllUsers(
			@ShellOption Long custId)
	{
		usr.getUsers(custId).forEach(System.out::println);
	}

	@ShellMethod("Create New Customer")
	public boolean createNewCustomer(
			@ShellOption() String custCode,
			@ShellOption() String companyName,
			@ShellOption(defaultValue = ShellOption.NULL) String companyBusiness,
			@ShellOption() Long landingProdSysId,
			@ShellOption() Integer activeStatusInd,
			@ShellOption() String createdBy,
			@ShellOption(defaultValue = ShellOption.NULL) String inactivatedBy,
			@ShellOption(defaultValue = ShellOption.NULL) String modifiedBy,
			@ShellOption() Integer passwordExpiryDate,
			@ShellOption() String domainName
	)
	{
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

	@ShellMethod("Create roles for customer's users")
	public boolean createRoles()
	{
		// logic to create roles for customer
		// ROLE_CODE is unique so take care of it
		// Also DATA_SECURITY_KEY is by default NULL
		return true;
	}

	@ShellMethod("Create Privileges for customer")
	public boolean createPrivileges()
	{
		return true;
	}

}


@SpringBootApplication
//@EnableDiscoveryClient
public class NSSOApplicationMicro extends SpringBootServletInitializer {

	@Bean
	public TomcatEmbeddedServletContainerFactory tomcatEmbeddedServletContainerFactory() {
		return new TomcatEmbeddedServletContainerFactory();
	}

	@Override
	protected SpringApplicationBuilder configure(SpringApplicationBuilder builder) {
		// TODO Auto-generated method stub
		return builder.sources(NSSOApplicationMicro.class);
	}

	@Bean
	public FilterRegistrationBean jwtFilter() {
		final FilterRegistrationBean registrationBean = new FilterRegistrationBean();
		registrationBean.setFilter(new JwtFilter());
		registrationBean.addUrlPatterns("/auth/*");

		return registrationBean;
	}

	/*
	 * @Override protected SpringApplicationBuilder configure(
	 * SpringApplicationBuilder builder) { // TODO Auto-generated method stub
	 * return builder.sources(NSSOApplication.class); }
	 */
	public static void main(String[] args) {

		// Launch the application
		ConfigurableApplicationContext context = SpringApplication.run(NSSOApplicationMicro.class, args);
		@SuppressWarnings("unused")
		WebSecurityConfig config = context.getBean(WebSecurityConfig.class);

	}
}
