package com.sncr.saw.security.app.repository;

import com.sncr.saw.security.common.bean.Category;
import com.sncr.saw.security.common.bean.CustomerProductSubModule;
import com.sncr.saw.security.common.bean.Module;
import com.sncr.saw.security.common.bean.Product;
import com.sncr.saw.security.common.bean.ResetValid;
import com.sncr.saw.security.common.bean.Role;
import com.sncr.saw.security.common.bean.Ticket;
import com.sncr.saw.security.common.bean.User;
import com.sncr.saw.security.common.bean.Valid;
import com.sncr.saw.security.common.bean.repo.admin.category.CategoryDetails;
import com.sncr.saw.security.common.bean.repo.admin.category.SubCategoryDetails;
import com.sncr.saw.security.common.bean.repo.admin.category.SubCategoryWithPrivilegeDetails;
import com.sncr.saw.security.common.bean.repo.admin.privilege.AddPrivilegeDetails;
import com.sncr.saw.security.common.bean.repo.admin.privilege.PrivilegeDetails;
import com.sncr.saw.security.common.bean.repo.admin.role.RoleDetails;
import com.sncr.saw.security.common.bean.repo.analysis.AnalysisSummary;
import com.sncr.saw.security.common.bean.repo.analysis.AnalysisSummaryList;

import java.util.List;

public interface UserRepository {
	void insertTicketDetails(Ticket ticket) throws Exception;
	boolean[] authenticateUser(String masterLoginId, String password);
	void prepareTicketDetails(User user, Boolean onlyDef);
	void invalidateTicket(String ticketId, String validityMessage);	
	String updateUserPass(String masterLoginId, String newPassEncrp);
	Ticket getTicketDetails(String ticketId);
	String changePassword(String loginId, String newPass, String oldPass);
	String rstchangePassword(String loginId, String newPass);
	String getUserEmailId(String userId);
	void insertResetPasswordDtls(String userId, String randomHash,
			Long createdTime, long validUpto);
	ResetValid validateResetPasswordDtls(String randomHash);
	boolean createAnalysis (AnalysisSummary analysis);
	boolean updateAnalysis(AnalysisSummary analysis);
	boolean deleteAnalysis(AnalysisSummary analysis);
	AnalysisSummaryList getAnalysisByFeatureID (Long featureId);
	List<User> getUsers(Long customerId);
	Valid addUser(User user);
	Valid updateUser(User user);
	boolean deleteUser(Long userId, String masterLoginId);
	List<Role> getRolesDropDownList(Long customerId);
	List<RoleDetails> getRoles(Long customerId);
	List<Role> getRoletypesDropDownList();
	Valid addRole(RoleDetails role);
	boolean deleteRole(Long roleId, String masterLoginId);
	Valid updateRole(RoleDetails role);
	boolean checkUserExists(Long roleId);
	boolean checkPrivExists(Long roleId);
	List<PrivilegeDetails> getPrivileges(Long customerId);
	List<Product> getProductsDropDownList(Long customerId);
	List<Module> getModulesDropDownList(Long customerId, Long productId);
	List<Category> getCategoriesDropDownList(Long customerId, Long moduleId, boolean parent);
	Valid upsertPrivilege(AddPrivilegeDetails addPrivilegeDetails);
	Valid updatePrivilege(PrivilegeDetails privilege);
	boolean deletePrivilege(Long privId);
	List<CategoryDetails> getCategories(Long customerId);
	Valid addCategory(CategoryDetails category);
	boolean checkCatExists(CategoryDetails category);
	boolean deleteCategory(Long categoryId);
	List<SubCategoryDetails> getSubCategories(Long customerId, String featureCode);
	List<SubCategoryWithPrivilegeDetails> getSubCategoriesWithPrivilege(CustomerProductSubModule cpsm);
	Valid updateCategory(CategoryDetails category);
	boolean checkSubCatExists(CategoryDetails category);
	Long createAdminUserForOnboarding(User user);
}