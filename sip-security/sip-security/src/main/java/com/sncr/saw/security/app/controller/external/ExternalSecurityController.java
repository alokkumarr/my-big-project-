package com.sncr.saw.security.app.controller.external;

import com.sncr.saw.security.app.properties.NSSOProperties;
import com.sncr.saw.security.app.repository.ProductModuleRepository;
import com.sncr.saw.security.app.repository.RoleRepository;
import com.sncr.saw.security.app.repository.UserRepository;
import com.sncr.saw.security.common.bean.Role;
import com.sncr.saw.security.common.bean.RoleCategoryPrivilege;
import com.sncr.saw.security.common.bean.Valid;
import com.sncr.saw.security.common.bean.repo.ProductModuleDetails;
import com.sncr.saw.security.common.bean.repo.admin.RolesList;
import com.sncr.saw.security.common.bean.repo.admin.role.RoleDetails;
import com.sncr.saw.security.common.util.JWTUtils;
import com.synchronoss.bda.sip.jwt.token.RoleType;
import io.swagger.annotations.Api;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author alok.kumarr
 * @since 3.5.0
 */
@Api(value = "The controller provides to perform admin security operation in synchronoss analytics platform ")
@RestController
@RequestMapping("/sip-security/external")
public class ExternalSecurityController {

	@Autowired
	private RoleRepository roleRepository;
	@Autowired
	private UserRepository userRepository;
	@Autowired
	private NSSOProperties nSSOProperties;
	@Autowired
	private ProductModuleRepository productModuleRepository;

	@RequestMapping(value = "/createRoleCategoryPrivilege", method = RequestMethod.POST)
	public Object createRoleCategoryPrivilege(HttpServletRequest request,
																						HttpServletResponse response,
																						@RequestBody RoleCategoryPrivilege roleCategoryPrivilege) {

		RoleCategoryPrivilege categoryPrivilege = new RoleCategoryPrivilege();
		if (roleCategoryPrivilege == null) {
			response.setStatus(HttpStatus.BAD_REQUEST.value());
			categoryPrivilege.setMessage("Body is missing.");
			return categoryPrivilege;
		}

		String jwtToken = JWTUtils.getToken(request);
		String[] extractValuesFromToken = JWTUtils.parseToken(jwtToken, nSSOProperties.getJwtSecretKey());
		String roleType = extractValuesFromToken[3];
		String masterLoginId = extractValuesFromToken[4];
		if (masterLoginId != null && !userRepository.validateUser(masterLoginId) && !RoleType.ADMIN.equals(roleType)) {
			response.setStatus(HttpStatus.UNAUTHORIZED.value());
			categoryPrivilege.setMessage("You are not authorized to perform this operation.");
		}

		String moduleName = roleCategoryPrivilege.getModuleName();
		String productName = roleCategoryPrivilege.getProductName();
		categoryPrivilege.setModuleName(moduleName);
		categoryPrivilege.setProductName(productName);

		List<ProductModuleDetails> moduleNameList = productModuleRepository.getModuleProductName(masterLoginId);
		Long customerSysId = null;
		if (moduleNameList != null && !moduleNameList.isEmpty()) {
			customerSysId = moduleNameList.stream().findFirst().get().getCustomerSysId();
			Set<String> moduleNames = moduleNameList.stream().map(pmd -> pmd.getModuleName()).collect(Collectors.toSet());
			Set<String> productNames = moduleNameList.stream().map(pmd -> pmd.getProductName()).collect(Collectors.toSet());
			boolean hasValidNames = productNames.contains(productName) && moduleNames.contains(moduleName);
			if (!hasValidNames) {
				response.setStatus(HttpStatus.UNAUTHORIZED.value());
				categoryPrivilege.setMessage("Product and Module does not exist for this user.");
			}
		}

		Role inputRole = roleCategoryPrivilege.getRole();
		if (inputRole != null && customerSysId != null) {
			if (inputRole.getCustomerCode().isEmpty() || inputRole.getRoleType().isEmpty()) {
				response.setStatus(HttpStatus.BAD_REQUEST.value());
				categoryPrivilege.setMessage("Customer Code and Role Name can't be blank or empty.");
			} else if (inputRole.isAutoCreate() && !roleRepository.validateRoleByIdAndCustomerCode(customerSysId, inputRole)) {
				// build role details bean from input
				RoleDetails role = buildRoleDetails(masterLoginId, customerSysId, inputRole);
				RolesList roleList = new RolesList();
				try {
					if (role != null) {
						Valid valid = userRepository.addRole(role);
						if (valid.getValid()) {
							roleList.setRoles(userRepository.getRoles(role.getCustSysId()));
							roleList.setValid(true);
						} else {
							roleList.setValid(false);
							roleList.setValidityMessage("Role could not be added. " + valid.getError());
						}
					} else {
						roleList.setValid(false);
						roleList.setValidityMessage("Mandatory request params are missing");
					}
				} catch (Exception e) {
					roleList.setValid(false);
					String message = (e instanceof DataAccessException) ? "Database error." : "Error.";
					roleList.setValidityMessage(message + " Please contact server Administrator");
					roleList.setError(e.getMessage());
					return roleList;
				}
				return roleList;
			} else {
				categoryPrivilege.setMessage("Role already exist in the system.");
			}
		}
		return categoryPrivilege;
	}

	private RoleDetails buildRoleDetails(String masterLoginId, Long customerSysId, Role inputRole) {
		RoleDetails role = new RoleDetails();
		role.setActiveStatusInd(Boolean.valueOf(inputRole.getActiveStatusInd()) ? "1" : "0");
		role.setCustomerCode(inputRole.getCustomerCode());
		role.setMasterLoginId(masterLoginId);
		role.setRoleName(inputRole.getRoleName());
		role.setRoleDesc(inputRole.getRoleDesc());
		role.setRoleType(RoleType.fromValue(inputRole.getRoleType()));
		role.setCustSysId(customerSysId);
		return role;
	}
}
