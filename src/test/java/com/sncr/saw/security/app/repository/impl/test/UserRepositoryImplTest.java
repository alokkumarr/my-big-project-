package com.sncr.saw.security.app.repository.impl.test;

import com.sncr.saw.security.app.repository.UserRepository;
import com.sncr.saw.security.common.bean.Category;
import com.sncr.saw.security.common.bean.Module;
import com.sncr.saw.security.common.bean.Product;
import com.sncr.saw.security.common.bean.ResetValid;
import com.sncr.saw.security.common.bean.Role;
import com.sncr.saw.security.common.bean.Ticket;
import com.sncr.saw.security.common.bean.User;
import com.sncr.saw.security.common.bean.Valid;
import com.sncr.saw.security.common.bean.repo.admin.category.CategoryDetails;
import com.sncr.saw.security.common.bean.repo.admin.category.SubCategoryDetails;
import com.sncr.saw.security.common.bean.repo.admin.privilege.AddPrivilegeDetails;
import com.sncr.saw.security.common.bean.repo.admin.privilege.PrivilegeDetails;
import com.sncr.saw.security.common.bean.repo.admin.privilege.SubCategoriesPrivilege;
import com.sncr.saw.security.common.bean.repo.admin.role.RoleDetails;
import com.sncr.saw.security.common.bean.repo.analysis.AnalysisSummary;
import com.sncr.saw.security.common.bean.repo.analysis.AnalysisSummaryList;
import com.sncr.saw.security.common.util.DateUtil;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class UserRepositoryImplTest {

	private static UserRepository userRepositoryDAO;
	
	private static User user1;
	private static User user2;
	private static List<User> userList = new ArrayList<User>();
	
	private static RoleDetails role1;
	private static RoleDetails role2;
	private static List<RoleDetails> roleList = new ArrayList<RoleDetails>();
	
	private static PrivilegeDetails privilege1;
	private static PrivilegeDetails privilege2;
    private static AddPrivilegeDetails addPrivilege1;
    private static AddPrivilegeDetails addPrivilege2;
	private static List<PrivilegeDetails> privilegeList = new ArrayList<PrivilegeDetails>();
	
	private static CategoryDetails category1;
	private static CategoryDetails category2;
	private static CategoryDetails subCategory1;	
	private static List<CategoryDetails> categoriesList = new ArrayList<CategoryDetails>();
	
	private static SubCategoryDetails subCategory11;
	private static SubCategoryDetails subCategory12;
	private static List<SubCategoryDetails> subCategoriesList = new ArrayList<SubCategoryDetails>();
	
	private static AnalysisSummary analysisSummary1;
	private static AnalysisSummary analysisSummary2;
	private static List<AnalysisSummary> listOfAnalysisSummary = new ArrayList<AnalysisSummary>();
	private static AnalysisSummaryList analysisSummaryList = new AnalysisSummaryList();
	
	private static Role roleDD1;
	private static Role roleDD2;
	private static List<Role> roleDDList = new ArrayList<Role>();
	
	private static Role roleType1;
	private static Role roleType2;
	private static List<Role> roleTypeList = new ArrayList<Role>();
	
	private static Product product1;
	private static Product product2;
	private static List<Product> productsList = new ArrayList<Product>();
	
	private static Module module1;
	private static Module module2;
	private static List<Module> modulesList = new ArrayList<Module>();
	
	private static Category category11;
	private static Category category12;
	private static List<Category> categoryList = new ArrayList<Category>();
	
	@BeforeClass
	public static void setUp() {
		
		// Create Mock
		userRepositoryDAO = mock(UserRepository.class);
				
		
		// ****************User Management*************************
		user1 = new User();
		user1.setActiveStatusInd("Active");
		user1.setMasterLoginId("1");
		user1.setEmail("Shwetha.Somayaji@synchronoss.com");
		user1.setRoleId(1l);
		user1.setCustomerId(1l);
		user1.setPassword("Sawsyncnewuser1!");
		user1.setFirstName("Shwetha");
		user1.setMiddleName("P");
		user1.setLastName("Somayaji");
		user1.setUserId(1l);

		user2 = new User();
		user2.setActiveStatusInd("Active");
		user2.setMasterLoginId("2");
		user2.setEmail("Karthik.ts@synchronoss.com");
		user2.setRoleId(1l);
		user2.setCustomerId(1l);
		user2.setPassword("Sawsyncnewuser1!");
		user2.setFirstName("Karthik");
		user2.setMiddleName("T");
		user2.setLastName("S");
		user2.setUserId(2l);
		// End of ****************User Management*************************
		
		// ****************Role Management*************************
		role1 = new RoleDetails();
		role1.setRoleSysId(1l);
		role1.setRoleName("ANALYST");
		role1.setActiveStatusInd("Active");
		role1.setCustomerCode("1");
		role1.setMasterLoginId("1");
		role1.setRoleDesc("Analyst");
		role1.setRoleSysId(1l);
		role1.setCustSysId(1l);
		role1.setMyAnalysis(true);
		
		role2 = new RoleDetails();
		role2.setRoleSysId(2l);
		role2.setRoleName("REPORT USER");
		role2.setActiveStatusInd("Active");
		role2.setCustomerCode("1");
		role2.setMasterLoginId("2");
		role2.setRoleDesc("Report User");
		role2.setRoleSysId(2l);
		role2.setCustSysId(1l);
		role2.setMyAnalysis(true);
		// End of ****************Role Management*************************
	    
		// ****************Privilege Management*************************
		privilege1 = new PrivilegeDetails();
		privilege1.setCustomerId(1l);
		privilege1.setProductId(1l);
		privilege1.setRoleId(1l);
		privilege1.setModuleId(1l);
		privilege1.setCategoryId(1l);
		privilege1.setCategoryType("parents");
		privilege1.setMasterLoginId("1");
		privilege1.setPrivilegeCode(128l);
		privilege1.setPrivilegeDesc("All");
		privilege1.setPrivilegeId(1l);
		addPrivilege1 = new AddPrivilegeDetails();
		SubCategoriesPrivilege subCategoriesPrivilege = new SubCategoriesPrivilege();
		subCategoriesPrivilege.setPrivilegeCode(128l);
		subCategoriesPrivilege.setPrivilegeDesc("All");
		subCategoriesPrivilege.setPrivilegeId(11L);
		List <SubCategoriesPrivilege > list = new ArrayList<>();
		list.add(subCategoriesPrivilege);
		addPrivilege1.setCustomerId(1l);
		addPrivilege1.setProductId(1l);
		addPrivilege1.setRoleId(1l);
		addPrivilege1.setModuleId(1l);
		addPrivilege1.setCategoryId(1l);
		addPrivilege1.setCategoryType("parents");
		addPrivilege1.setMasterLoginId("1");
		addPrivilege1.setSubCategoriesPrivilege(list);

        privilege2 = new PrivilegeDetails();
		privilege2.setCustomerId(1l);
		privilege2.setProductId(1l);
		privilege2.setRoleId(1l);
		privilege2.setModuleId(1l);
		privilege2.setCategoryId(2l);
		privilege2.setCategoryType("child");
		privilege2.setMasterLoginId("2");
		privilege2.setPrivilegeCode(128l);
		privilege2.setPrivilegeDesc("All");
		privilege2.setPrivilegeId(2l);

		addPrivilege2 = new AddPrivilegeDetails();
		SubCategoriesPrivilege subCategoriesPrivilege2 = new SubCategoriesPrivilege();
		subCategoriesPrivilege.setPrivilegeCode(128l);
		subCategoriesPrivilege.setPrivilegeDesc("All");
		subCategoriesPrivilege.setPrivilegeId(11L);
		List <SubCategoriesPrivilege > list2 = new ArrayList<>();
		list.add(subCategoriesPrivilege2);
		addPrivilege2.setCustomerId(1l);
		addPrivilege2.setProductId(1l);
		addPrivilege2.setRoleId(1l);
		addPrivilege2.setModuleId(1l);
		addPrivilege2.setCategoryId(1l);
		addPrivilege2.setCategoryType("child");
		addPrivilege2.setMasterLoginId("1");
		addPrivilege2.setSubCategoriesPrivilege(list);
		// End of ****************Privilege Management*************************
		
		// ****************Categories Management*************************
		category1 = new CategoryDetails();
		category1.setCategoryId(1l);
		category1.setCategoryName("My Analysis");
		category1.setCategoryDesc("My Analysis");
		category1.setCategoryType("parent");
		category1.setCategoryCode("F00000001");
		category1.setActiveStatusInd(1l);
		category1.set_default("/myAnalysis");
		category1.setCustomerId(1l);
		category1.setModuleId(1l);
		category1.setProductId(1l);
		category1.setSubCategoryInd(false);
		category1.setMasterLoginId("1l");
		
		category2 = new CategoryDetails();
		category2.setCategoryId(2l);
		category2.setCategoryName("Canned Analysis");
		category2.setCategoryDesc("Canned Analysis");
		category2.setCategoryType("parent");
		category2.setCategoryCode("F00000002");
		category2.setActiveStatusInd(1l);
		category2.set_default("/cannedAnalysis");
		category2.setCustomerId(1l);
		category2.setModuleId(1l);
		category2.setProductId(1l);
		category2.setSubCategoryInd(false);
		category2.setMasterLoginId("1l");
		// End of ****************Categories Management*************************
		
				
		analysisSummary1 = new AnalysisSummary();
		analysisSummary1.setAnalysisId(1l);
		analysisSummary1.setAnalysisName("My Analysis");
		analysisSummary1.setActiveStatusInd(1);
		analysisSummary1.setFeatureId(1l);
		analysisSummary1.setCreatedBy("sawadmin@synchronoss.com");
		analysisSummary1.setCreatedDate(DateUtil.convertStringToDate(new Date().toString(), "yyyy-MM-dd HH:mm:ss"));
		
		analysisSummary2 = new AnalysisSummary();
		analysisSummary2.setAnalysisId(2l);
		analysisSummary2.setAnalysisName("Canned Analysiss");
		analysisSummary2.setActiveStatusInd(1);
		analysisSummary2.setFeatureId(1l);
		analysisSummary2.setCreatedBy("sawadmin@synchronoss.com");
		analysisSummary2.setCreatedDate(DateUtil.convertStringToDate(new Date().toString(), "yyyy-MM-dd HH:mm:ss"));
		
		listOfAnalysisSummary.add(analysisSummary1);
		listOfAnalysisSummary.add(analysisSummary2);
		analysisSummaryList.setValid(true);
		analysisSummaryList.setValidityMessage("Artifacts List Successfully Populated.");
		analysisSummaryList.setArtifactSummaryList(listOfAnalysisSummary);
		
		roleDD1 = new Role();
		roleDD1.setRoleId(1l);
		roleDD1.setRoleName("ANALYST");
		
		roleDD2 = new Role();
		roleDD2.setRoleId(1l);
		roleDD2.setRoleName("ANALYST");		
		
		roleType1 = new Role();
		roleType1.setRoleId(1l);
		roleType1.setRoleName("ADMIN");
		
		roleType2 = new Role();
		roleType2.setRoleId(1l);
		roleType2.setRoleName("USER");	
		
		product1 = new Product();
		product1.setProductId(1l);
		product1.setProductName("ATT");
		
		product2 = new Product();
		product2.setProductId(1l);
		product2.setProductName("ATT");
		

	}
	
	@Test
	public void testAuthenticateUser() {
		String masterLoginId = "SAWADMIN@Synchronoss.com";
		String password = "Sawsyncnewuser1!";
		boolean isAuthenticated = true;
		boolean isPasswordActive = true;
		boolean[] valid = { isAuthenticated, isPasswordActive };
		
		when(userRepositoryDAO.authenticateUser(masterLoginId, password)).thenReturn(valid); // Stubbing the methods of mocked userRepo with mocked data.
		boolean[] isValid = userRepositoryDAO.authenticateUser(masterLoginId, password);
		assertEquals(true, isValid[0]);
		assertEquals(true, isValid[1]);		
	}	
	
	@Test
	public void TestRstchangePassword() {
		String masterLoginId = "SAWADMIN@Synchronoss.com";
		String newPassword = "Sawsyncnewuser2!";
		String valid = "Valid";
		
		when(userRepositoryDAO.rstchangePassword(masterLoginId, newPassword)).thenReturn(valid); // Stubbing the methods of mocked userRepo with mocked data.
		String isValid = userRepositoryDAO.rstchangePassword(masterLoginId, newPassword);
		assertEquals(isValid, valid);
	}
	
	@Test
	public void TestChangePassword() {
		String masterLoginId = "SAWADMIN@Synchronoss.com";
		String newPassword = "Sawsyncnewuser3!";
		String oldPassowrd = "Sawsyncnewuser2!";
		String valid = "Password Successfully Changed";
		
		when(userRepositoryDAO.changePassword(masterLoginId, newPassword, oldPassowrd)).thenReturn(valid); // Stubbing the methods of mocked userRepo with mocked data.
		String isValid = userRepositoryDAO.changePassword(masterLoginId, newPassword, oldPassowrd);
		assertEquals(isValid, valid);		
	}
	
	@Test
	public void TestValidateResetPasswordDtls() {
		ResetValid resetValid = new ResetValid();
		resetValid.setValid(true);
		resetValid.setMasterLoginID("1");
		String randomHash = "W1uLrs.eovTyXWzJz823fotuwGVKvIOMlEyPcCpd9swmpNLo2OgKVbwetvJFO4bi1BIcIKNiRi5_9RXJ1FoblMaOToNvEjwCrs2ccdwv1ckDMKqT1s53U_xPOxuXug_GNB0sjlfq_a7CkGrjpDbVrmCJK7W6afnH";
		
		when(userRepositoryDAO.validateResetPasswordDtls(randomHash)).thenReturn(resetValid); // Stubbing the methods of mocked userRepo with mocked data.
		ResetValid isResetValid = userRepositoryDAO.validateResetPasswordDtls(randomHash);
		assertEquals(true, isResetValid.getValid());	
	}
	
	@Test
	public void TestGetTicketDetails() {
		Ticket ticket = new Ticket();
		ticket.setMasterLoginId("Sawadmin@synchronoss.com");
		ticket.setDefaultProdID("1");
		ticket.setRoleType("ADMIN");
		ticket.setUserFullName("SAW ADMIN");
		ticket.setWindowId("1");
		String ticketId = "1";
		
		when(userRepositoryDAO.getTicketDetails(ticketId)).thenReturn(ticket); // Stubbing the methods of mocked userRepo with mocked data.
		Ticket ticket2 = userRepositoryDAO.getTicketDetails(ticketId);
		assertEquals("1", ticket2.getWindowId());	
	}
	
	@Test
	public void TestGetUserEmailId() {
		String userID = "1";
		String emailID = "shwetha.somayaji@synchronoss.com";
		
		when(userRepositoryDAO.getUserEmailId(userID)).thenReturn(emailID); // Stubbing the methods of mocked userRepo with mocked data.
		String email = userRepositoryDAO.getUserEmailId(userID);
		assertEquals(email, emailID);	
	}
	
	@Test
	public void TestCreateAnalysis() {		
		Boolean valid = true;
		
		when(userRepositoryDAO.createAnalysis(analysisSummary1)).thenReturn(valid); // Stubbing the methods of mocked userRepo with mocked data.
		Boolean isValid = userRepositoryDAO.createAnalysis(analysisSummary1);
		assertEquals(isValid, valid);	
		
	}
	
	@Test
	public void TestGetAnalysisByFeatureID() {		
		long featureId = 1;
		
		when(userRepositoryDAO.getAnalysisByFeatureID(featureId)).thenReturn(analysisSummaryList); // Stubbing the methods of mocked userRepo with mocked data.
		AnalysisSummaryList analysisList = userRepositoryDAO.getAnalysisByFeatureID(featureId);
		assertEquals("My Analysis", analysisList.getArtifactSummaryList().get(0).getAnalysisName());	
	}

	@Test
	public void TestUpdateAnalysis() {
		analysisSummary1.setAnalysisName("Public Folder");
		Boolean valid = true;
		long featureId = 1;
		when(userRepositoryDAO.updateAnalysis(analysisSummary1)).thenReturn(valid); // Stubbing the methods of mocked userRepo with mocked data.
		Boolean isValid = userRepositoryDAO.updateAnalysis(analysisSummary1);
		assertEquals(valid, isValid);
		
		when(userRepositoryDAO.getAnalysisByFeatureID(featureId)).thenReturn(analysisSummaryList); // Stubbing the methods of mocked userRepo with mocked data.
		AnalysisSummaryList analysisList = userRepositoryDAO.getAnalysisByFeatureID(featureId);
		assertEquals("Public Folder", analysisList.getArtifactSummaryList().get(0).getAnalysisName());
	}
	
	@Test
	public void TestDeleteAnalysis() {
		analysisSummary1.setActiveStatusInd(0);;
		Boolean valid = true;
		long featureId = 1;
		when(userRepositoryDAO.deleteAnalysis(analysisSummary1)).thenReturn(valid); // Stubbing the methods of mocked userRepo with mocked data.
		Boolean isValid = userRepositoryDAO.deleteAnalysis(analysisSummary1);
		assertEquals(valid, isValid);
		
		when(userRepositoryDAO.getAnalysisByFeatureID(featureId)).thenReturn(analysisSummaryList); // Stubbing the methods of mocked userRepo with mocked data.
		AnalysisSummaryList analysisList = userRepositoryDAO.getAnalysisByFeatureID(featureId);
		assertEquals(new Integer(0) , analysisList.getArtifactSummaryList().get(0).getActiveStatusInd());
	}
	
	@Test
	public void TestGetRolesDropDownList() {
		long customerId = 1;
		when(userRepositoryDAO.getRolesDropDownList(customerId)).thenReturn(Arrays.asList(roleDD1,roleDD2)); // Stubbing the methods of mocked userRepo with mocked data.
		roleDDList = userRepositoryDAO.getRolesDropDownList(customerId);
		assertEquals(2, roleDDList.size());
	}
	
	@Test
	public void TestGetRoletypesDropDownList() {
		when(userRepositoryDAO.getRoletypesDropDownList()).thenReturn(Arrays.asList(roleType1,roleType2)); // Stubbing the methods of mocked userRepo with mocked data.
		roleTypeList = userRepositoryDAO.getRoletypesDropDownList();
		assertEquals(2, roleTypeList.size());
	}
	
	@Test
	public void TestCheckUserExists() {
		long roleId = 1;
		boolean valid = true;
		when(userRepositoryDAO.checkUserExists(roleId)).thenReturn(valid); // Stubbing the methods of mocked userRepo with mocked data.
		boolean exists = userRepositoryDAO.checkUserExists(roleId);
		assertEquals(exists, valid);
	}
	
	@Test
	public void TestCheckPrivExists() {
		long roleId = 1;
		boolean valid = true;
		when(userRepositoryDAO.checkPrivExists(roleId)).thenReturn(valid); // Stubbing the methods of mocked userRepo with mocked data.
		boolean exists = userRepositoryDAO.checkPrivExists(roleId);
		assertEquals(exists, valid);
	}
	
	
	@Test
	public void TestGetProductsDropDownList() {
		long customerId = 1;
		when(userRepositoryDAO.getProductsDropDownList(customerId)).thenReturn(Arrays.asList(product1,product2)); // Stubbing the methods of mocked userRepo with mocked data.
		productsList = userRepositoryDAO.getProductsDropDownList(customerId);
		assertEquals(2, productsList.size());
	}
	
	@Test
	public void TestGetModulesDropDownList() {
		long customerId = 1;
		long productId = 1;
		when(userRepositoryDAO.getModulesDropDownList(customerId, productId)).thenReturn(Arrays.asList(module1,module2)); // Stubbing the methods of mocked userRepo with mocked data.
		modulesList = userRepositoryDAO.getModulesDropDownList(customerId, productId);
		assertEquals(2, modulesList.size());
	}
	
	@Test
	public void TestGetCategoriesDropDownList() {
		long customerId = 1;
		long moduleId = 1;
		when(userRepositoryDAO.getCategoriesDropDownList(customerId, moduleId, true)).thenReturn(Arrays.asList(category11,category12)); // Stubbing the methods of mocked userRepo with mocked data.
		categoryList = userRepositoryDAO.getCategoriesDropDownList(customerId, moduleId, true);
		assertEquals(2, categoryList.size());
	}
	
	@Test
	public void TestCheckCatExists() {
		boolean valid = true;
		when(userRepositoryDAO.checkCatExists(category1)).thenReturn(valid); // Stubbing the methods of mocked userRepo with mocked data.
		boolean exists = userRepositoryDAO.checkCatExists(category1);
		assertEquals(exists, valid);
	}
	
	@Test
	public void TestCheckSubCatExists() {
		boolean valid = true;
		when(userRepositoryDAO.checkSubCatExists(subCategory1)).thenReturn(valid); // Stubbing the methods of mocked userRepo with mocked data.
		boolean exists = userRepositoryDAO.checkSubCatExists(subCategory1);
		assertEquals(exists, valid);
	}
	
	@Test
	public void TestGetSubCategories() {
		long categoryId = 1;
		String categoryCode = "F00000001";
		when(userRepositoryDAO.getSubCategories(categoryId,categoryCode)).thenReturn(Arrays.asList(subCategory11,subCategory12)); // Stubbing the methods of mocked userRepo with mocked data.
		subCategoriesList = userRepositoryDAO.getSubCategories(categoryId,categoryCode);
		assertEquals(2, subCategoriesList.size());
	}
	
	@Test
	public void testAddUser() {
		Valid validUser1 = new Valid();
		validUser1.setValid(true);
		when(userRepositoryDAO.addUser(user1)).thenReturn(validUser1); // Stubbing the methods of mocked userRepo with mocked data.
		
		Valid isbn = userRepositoryDAO.addUser(user1);
		assertNotNull(isbn);
		assertEquals(true, isbn.getValid());
		if(isbn.getValid()){
			userList.add(user1);
		}
		
		when(userRepositoryDAO.getUsers(1l)).thenReturn(userList);
		List<User> allUsers = userRepositoryDAO.getUsers(1l);
		assertEquals(1, allUsers.size());
		
		Valid validUser2 = new Valid();
		validUser2.setValid(true);
		when(userRepositoryDAO.addUser(user2)).thenReturn(validUser2); // Stubbing the methods of mocked userRepo with mocked data.
		isbn = userRepositoryDAO.addUser(user2);
		assertNotNull(isbn);
		assertEquals(true, isbn.getValid());
		if(isbn.getValid()){
			userList.add(user2);
		}
		
		when(userRepositoryDAO.getUsers(1l)).thenReturn(userList);
		allUsers = userRepositoryDAO.getUsers(1l);
		assertEquals(2, allUsers.size());
	}

	@Test
	public void testGetUser() {
		when(userRepositoryDAO.getUsers(1l)).thenReturn(Arrays.asList(user1,user2)); // Stubbing the methods of mocked userRepo with mocked data.
		
		List<User> allUsers = userRepositoryDAO.getUsers(1l);
		assertEquals(2, allUsers.size());
		User user1 = allUsers.get(0);
		User user2 = allUsers.get(1);
		assertEquals("Shwetha", user1.getFirstName());
		assertEquals("Karthik", user2.getFirstName());
	}
	
	@Test
	public void testUpdateUser(){
		Valid validUpdateUser = new Valid();
		validUpdateUser.setValid(true);
		when(userRepositoryDAO.updateUser(user2)).thenReturn(validUpdateUser); // Stubbing the methods of mocked userRepo with mocked data.
		
		user2.setActiveStatusInd("inactivate");
		Valid isbn = userRepositoryDAO.updateUser(user2);
		if(isbn.getValid()){
			when(userRepositoryDAO.getUsers(1l)).thenReturn(Arrays.asList(user1,user2)); // Stubbing the methods of mocked userRepo with mocked data.
		}
		
		List<User> allUsers = userRepositoryDAO.getUsers(1l);
		User user2 = allUsers.get(1);
		assertEquals("Karthik", user2.getFirstName());
		assertEquals("inactivate", user2.getActiveStatusInd());
	}
	
	@Test
	public void testDeleteUser(){
		
		when(userRepositoryDAO.deleteUser(user2.getUserId(), user2.getMasterLoginId())).thenReturn(true); // Stubbing the methods of mocked userRepo with mocked data.
		
		boolean flag = userRepositoryDAO.deleteUser(2l, "2");
		assertEquals(true, flag);
		if(flag){
			when(userRepositoryDAO.getUsers(1l)).thenReturn(Arrays.asList(user1)); // Stubbing the methods of mocked userRepo with mocked data.
		}
		
		List<User> allUsers = userRepositoryDAO.getUsers(1l);
		assertEquals(1, allUsers.size());
		assertEquals(new Long(1), allUsers.get(0).getUserId());
	}
	
	@Test
	public void testAddRole() {
		Valid validRole1 = new Valid();
		validRole1.setValid(true);		
		when(userRepositoryDAO.addRole(role1)).thenReturn(validRole1); // Stubbing the methods of mocked RoleuserRepo with mocked data.
		Valid isbn = userRepositoryDAO.addRole(role1);
		assertNotNull(isbn);
		assertEquals(true, isbn.getValid());
		if(isbn.getValid()){
			roleList.add(role1);
		}
		when(userRepositoryDAO.getRoles(1l)).thenReturn(roleList); // Stubbing the methods of mocked RoleuserRepo with mocked data.
		List<RoleDetails> allRoles = userRepositoryDAO.getRoles(1l);
		assertEquals(1, allRoles.size());
		
		Valid validRole2 = new Valid();
		validRole2.setValid(true);
		when(userRepositoryDAO.addRole(role2)).thenReturn(validRole2); // Stubbing the methods of mocked RoleuserRepo with mocked data.
		isbn = userRepositoryDAO.addRole(role2);
		assertNotNull(isbn);
		assertEquals(true, isbn.getValid());
		if(isbn.getValid()){
			roleList.add(role2);
		}
		when(userRepositoryDAO.getRoles(1l)).thenReturn(roleList); // Stubbing the methods of mocked RoleuserRepo with mocked data.
		allRoles = userRepositoryDAO.getRoles(1l);
		assertEquals(2, allRoles.size());
		
	}

	@Test
	public void testGetRole() {
		when(userRepositoryDAO.getRoles(1l)).thenReturn(Arrays.asList(role1,role2)); // Stubbing the methods of mocked RoleuserRepo with mocked data.
		
		List<RoleDetails> allRoles = userRepositoryDAO.getRoles(1l);
		assertEquals(2, allRoles.size());
		RoleDetails role1 = allRoles.get(0);
		RoleDetails role2 = allRoles.get(1);
		assertEquals("ANALYST", role1.getRoleName());
		assertEquals("REPORT USER", role2.getRoleName());
	}
	
	@Test
	public void testUpdateRole(){
		Valid validUpdatedRole = new Valid();
		validUpdatedRole.setValid(true);			
		when(userRepositoryDAO.updateRole(role2)).thenReturn(validUpdatedRole);
		
		role2.setActiveStatusInd("inactivate");
		Valid isbn = userRepositoryDAO.updateRole(role2);
		if(isbn.getValid()){
			when(userRepositoryDAO.getRoles(1l)).thenReturn(Arrays.asList(role1,role2)); // Stubbing the methods of mocked userRepo with mocked data.
		}
		
		List<RoleDetails> allRoles = userRepositoryDAO.getRoles(1l);
		assertEquals(2, allRoles.size());
		RoleDetails role2 = allRoles.get(1);
		assertEquals("REPORT USER", role2.getRoleName());
		assertEquals("inactivate", role2.getActiveStatusInd());
	}
	
	@Test
	public void testDeleteRole(){
		when(userRepositoryDAO.deleteRole(role2.getRoleSysId(), role2.getMasterLoginId())).thenReturn(true); // Stubbing the methods of mocked RoleuserRepo with mocked data.
		
		boolean flag = userRepositoryDAO.deleteRole(2l, "2");
		assertEquals(true, flag);
		if(flag){
			when(userRepositoryDAO.getRoles(1l)).thenReturn(Arrays.asList(role1)); // Stubbing the methods of mocked userRepo with mocked data.
		}
		List<RoleDetails> allRoles = userRepositoryDAO.getRoles(1l);
		assertEquals(1, allRoles.size());
		
	}
	
	@Test
	public void testAddPrivilege() {
		Valid validPrivilege1 = new Valid();
		validPrivilege1.setValid(true);	
		when(userRepositoryDAO.upsertPrivilege(addPrivilege1)).thenReturn(validPrivilege1); // Stubbing the methods of mocked PrivilegeuserRepo with mocked data.
		Valid isbn = userRepositoryDAO.upsertPrivilege(addPrivilege1);
		assertNotNull(isbn);
		assertEquals(true, isbn.getValid());
		if(isbn.getValid()){
			privilegeList.add(privilege1);
		}
		when(userRepositoryDAO.getPrivileges(1l)).thenReturn(privilegeList); // Stubbing the methods of mocked PrivilegeuserRepo with mocked data.
		List<PrivilegeDetails> allPrivilege = userRepositoryDAO.getPrivileges(1l);
		assertEquals(1, allPrivilege.size());
		
		Valid validPrivilege2 = new Valid();
		validPrivilege2.setValid(true);	
		when(userRepositoryDAO.upsertPrivilege(addPrivilege2)).thenReturn(validPrivilege2); // Stubbing the methods of mocked PrivilegeuserRepo with mocked data.
		isbn = userRepositoryDAO.upsertPrivilege(addPrivilege2);
		assertNotNull(isbn);
		assertEquals(true, isbn.getValid());
		if(isbn.getValid()){
			privilegeList.add(privilege2);
		}
		when(userRepositoryDAO.getPrivileges(1l)).thenReturn(privilegeList); // Stubbing the methods of mocked PrivilegeuserRepo with mocked data.
		allPrivilege = userRepositoryDAO.getPrivileges(1l);
		assertEquals(2, allPrivilege.size());
	}

	@Test
	public void testGetPrivilege() {
		when(userRepositoryDAO.getPrivileges(1l)).thenReturn(Arrays.asList(privilege1, privilege2)); // Stubbing the methods of mocked PrivilegeuserRepo with mocked data.
		
		List<PrivilegeDetails> allPrivilege = userRepositoryDAO.getPrivileges(1l);
		assertEquals(2, allPrivilege.size());
		PrivilegeDetails privilege1 = allPrivilege.get(0);
		PrivilegeDetails privilege2 = allPrivilege.get(1);
		assertEquals(new Long(1), privilege1.getPrivilegeId());
		assertEquals(new Long(2), privilege2.getPrivilegeId());
	}
	
	@Test
	public void testUpdatePrivilege(){
		Valid validUpdatedPrivilege = new Valid();
		validUpdatedPrivilege.setValid(true);
		when(userRepositoryDAO.updatePrivilege(privilege2)).thenReturn(validUpdatedPrivilege);
		privilege2.setModuleId(2l);
		Valid isbn = userRepositoryDAO.updatePrivilege(privilege2);
		if(isbn.getValid()){
			when(userRepositoryDAO.getPrivileges(1l)).thenReturn(Arrays.asList(privilege1, privilege2)); // Stubbing the methods of mocked PrivilegeuserRepo with mocked data.
		}
		List<PrivilegeDetails> allPrivilege = userRepositoryDAO.getPrivileges(1l);
		assertEquals(2, allPrivilege.size());
		PrivilegeDetails privilege2 = allPrivilege.get(1);
		assertEquals(new Long(2), privilege2.getPrivilegeId());
		assertEquals(new Long(2), privilege2.getModuleId());
	}
	
	@Test
	public void testDeletePrivilege(){
		when(userRepositoryDAO.deletePrivilege(privilege2.getPrivilegeId())).thenReturn(true);
		
		boolean flag = userRepositoryDAO.deletePrivilege(2l);
		assertEquals(true, flag);
		if(flag){
			when(userRepositoryDAO.getPrivileges(1l)).thenReturn(Arrays.asList(privilege1)); // Stubbing the methods of mocked PrivilegeuserRepo with mocked data.
		}
		List<PrivilegeDetails> allPrivilege = userRepositoryDAO.getPrivileges(1l);
		assertEquals(1, allPrivilege.size());
	}
	
	@Test
	public void testAddCategory() {
		Valid validCategory1 = new Valid();
		validCategory1.setValid(true);		
		when(userRepositoryDAO.addCategory(category1)).thenReturn(validCategory1);// Stubbing the methods of mocked CategoriesuserRepo with mocked data.
		Valid isbn = userRepositoryDAO.addCategory(category1);
		assertNotNull(isbn);
		assertEquals(true, isbn.getValid());
		if(isbn.getValid()){
			categoriesList.add(category1);
		}
		when(userRepositoryDAO.getCategories(1l)).thenReturn(categoriesList);// Stubbing the methods of mocked CategoriesuserRepo with mocked data.
		List<CategoryDetails> allCategories = userRepositoryDAO.getCategories(1l);
		assertEquals(1, allCategories.size());
		
		Valid validCategory2 = new Valid();
		validCategory2.setValid(true);	
		when(userRepositoryDAO.addCategory(category2)).thenReturn(validCategory2);// Stubbing the methods of mocked CategoriesuserRepo with mocked data.
		isbn = userRepositoryDAO.addCategory(category2);
		assertNotNull(isbn);
		assertEquals(true, isbn.getValid());
		assertEquals(true, isbn.getValid());
		if(isbn.getValid()){
			categoriesList.add(category2);
		}
		when(userRepositoryDAO.getCategories(1l)).thenReturn(categoriesList);// Stubbing the methods of mocked CategoriesuserRepo with mocked data.
		allCategories = userRepositoryDAO.getCategories(1l);
		assertEquals(2, allCategories.size());
	}

	@Test
	public void testGetCategory() {
		when(userRepositoryDAO.getCategories(1l)).thenReturn(Arrays.asList(category1, category2));// Stubbing the methods of mocked CategoriesuserRepo with mocked data.
		List<CategoryDetails> allCategories = userRepositoryDAO.getCategories(1l);
		assertEquals(2, allCategories.size());
		CategoryDetails category1 = allCategories.get(0);
		CategoryDetails category2 = allCategories.get(1);
		assertEquals(1l, category1.getCategoryId());
		assertEquals(2l, category2.getCategoryId());
	}
	
	@Test
	public void testUpdateCategory(){
		Valid validUpdatedCategory = new Valid();
		validUpdatedCategory.setValid(true);
		when(userRepositoryDAO.updateCategory(category2)).thenReturn(validUpdatedCategory);// Stubbing the methods of mocked CategoriesuserRepo with mocked data.
		category2.setModuleId(2l);
		Valid isbn = userRepositoryDAO.updateCategory(category2);
		if(isbn.getValid()){
			when(userRepositoryDAO.getCategories(1l)).thenReturn(Arrays.asList(category1, category2));// Stubbing the methods of mocked CategoriesuserRepo with mocked data.
		}
		List<CategoryDetails> allCategories = userRepositoryDAO.getCategories(1l);
		assertEquals(2, allCategories.size());
		CategoryDetails category2 = allCategories.get(1);
		assertEquals(2l, category2.getCategoryId());
		assertEquals(2l, category2.getModuleId());
	}
	
	@Test
	public void testDeleteCategory(){
		when(userRepositoryDAO.deleteCategory(category2.getCategoryId())).thenReturn(true);// Stubbing the methods of mocked CategoriesuserRepo with mocked data.
	
		boolean flag = userRepositoryDAO.deleteCategory(2l);
		assertEquals(true, flag);
		if(flag){
			when(userRepositoryDAO.getCategories(1l)).thenReturn(Arrays.asList(category1));// Stubbing the methods of mocked CategoriesuserRepo with mocked data.
		}
		List<CategoryDetails> allCategories = userRepositoryDAO.getCategories(1l);
		assertEquals(1, allCategories.size());
	}
}