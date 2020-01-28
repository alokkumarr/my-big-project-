package com.sncr.saw.security.app.controller;

import static com.synchronoss.sip.utils.SipCommonUtils.validatePrivilege;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.sncr.saw.security.app.properties.NSSOProperties;
import com.sncr.saw.security.app.repository.DataSecurityKeyRepository;
import com.sncr.saw.security.app.repository.PreferenceRepository;
import com.sncr.saw.security.app.repository.UserRepository;
import com.sncr.saw.security.app.service.SecurityService;
import com.sncr.saw.security.app.service.TicketHelper;
import com.sncr.saw.security.app.sso.SSORequestHandler;
import com.sncr.saw.security.app.sso.SSOResponse;
import com.sncr.saw.security.common.bean.ChangePasswordDetails;
import com.sncr.saw.security.common.bean.CustProdModule;
import com.sncr.saw.security.common.bean.CustomerProductSubModule;
import com.sncr.saw.security.common.bean.LoginDetails;
import com.sncr.saw.security.common.bean.Preference;
import com.sncr.saw.security.common.bean.RandomHashcode;
import com.sncr.saw.security.common.bean.RefreshToken;
import com.sncr.saw.security.common.bean.ResetPwdDtls;
import com.sncr.saw.security.common.bean.ResetValid;
import com.sncr.saw.security.common.bean.User;
import com.sncr.saw.security.common.bean.UserPreferences;
import com.sncr.saw.security.common.bean.UserDetails;
import com.sncr.saw.security.common.bean.Valid;
import com.sncr.saw.security.common.bean.repo.UserCustomerMetaData;
import com.sncr.saw.security.common.bean.repo.admin.CategoryList;
import com.sncr.saw.security.common.bean.repo.admin.DeleteCategory;
import com.sncr.saw.security.common.bean.repo.admin.DeletePrivilege;
import com.sncr.saw.security.common.bean.repo.admin.DeleteRole;
import com.sncr.saw.security.common.bean.repo.admin.DeleteUser;
import com.sncr.saw.security.common.bean.repo.admin.ModuleDropDownList;
import com.sncr.saw.security.common.bean.repo.admin.PrivilegesList;
import com.sncr.saw.security.common.bean.repo.admin.ProductDropDownList;
import com.sncr.saw.security.common.bean.repo.admin.RolesDropDownList;
import com.sncr.saw.security.common.bean.repo.admin.RolesList;
import com.sncr.saw.security.common.bean.repo.admin.SubCategoryWithPrivilegeList;
import com.sncr.saw.security.common.bean.repo.admin.UserDetailsResponse;
import com.sncr.saw.security.common.bean.repo.admin.UsersDetailsList;
import com.sncr.saw.security.common.bean.repo.admin.UsersList;
import com.sncr.saw.security.common.bean.repo.admin.category.CategoryDetails;
import com.sncr.saw.security.common.bean.repo.admin.privilege.AddPrivilegeDetails;
import com.sncr.saw.security.common.bean.repo.admin.privilege.PrivilegeDetails;
import com.sncr.saw.security.common.bean.repo.admin.role.RoleDetails;
import com.sncr.saw.security.common.bean.repo.analysis.AnalysisSummary;
import com.sncr.saw.security.common.bean.repo.analysis.AnalysisSummaryList;
import com.sncr.saw.security.common.bean.repo.dsk.AttributeValues;
import com.sncr.saw.security.common.bean.repo.dsk.DskDetails;
import com.sncr.saw.security.common.bean.repo.dsk.DskValidity;
import com.sncr.saw.security.common.bean.repo.dsk.SecurityGroups;
import com.sncr.saw.security.common.bean.repo.dsk.UserAssignment;
import com.sncr.saw.security.common.util.JWTUtils;
import com.sncr.saw.security.common.util.PasswordValidation;
import com.synchronoss.bda.sip.dsk.BooleanCriteria;
import com.synchronoss.bda.sip.dsk.DskGroupPayload;
import com.synchronoss.bda.sip.dsk.SipDskAttribute;
import com.synchronoss.bda.sip.dsk.SipDskAttributeModel;
import com.synchronoss.bda.sip.jwt.TokenParser;
import com.synchronoss.bda.sip.jwt.token.RoleType;
import com.synchronoss.bda.sip.jwt.token.Ticket;
import com.synchronoss.sip.utils.Privileges;
import com.synchronoss.sip.utils.SipCommonUtils;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import java.io.IOException;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Properties;
import java.util.UUID;
import javax.mail.Message;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMultipart;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author gsan0003
 *
 */
@RestController
@RequestMapping("/sip-security")
@Api(value="sip-security", description = "SIP Security APIs")
public class SecurityController {
	private static final Logger logger = LoggerFactory.getLogger(SecurityController.class);

	@Autowired
	private UserRepository userRepository;
	@Autowired
	private NSSOProperties nSSOProperties;

    @Autowired
    private TicketHelper tHelper;

	@Autowired
	SSORequestHandler ssoRequestHandler;

	@Autowired
    PreferenceRepository preferenceRepository;

	@Autowired
    DataSecurityKeyRepository dataSecurityKeyRepository;

    @Autowired SecurityService securityService;

	private final ObjectMapper mapper = new ObjectMapper();

  private final static String AdminRole = "ADMIN";
  private final static String ALERTS = "ALERTS";
  private final static String UNAUTHORIZED_USER = "You are not authorized user to perform this operation.";

	@RequestMapping(value = "/doAuthenticate", method = RequestMethod.POST)
	public LoginResponse doAuthenticate(@RequestBody LoginDetails loginDetails) {

		logger.info("Ticket will be created..");
		logger.info("Token Expiry :" + nSSOProperties.getValidityMins());
		Ticket ticket = new Ticket();
		User user = null;
		ticket.setMasterLoginId(loginDetails.getMasterLoginId());
		ticket.setValid(false);
		RefreshToken rToken = null;

		try {
			boolean[] ret = userRepository.authenticateUser(loginDetails.getMasterLoginId(),
					loginDetails.getPassword());

			boolean isUserAuthentic = ret[0];
			boolean isPassWordActive = ret[1];
			boolean isAccountLocked = ret[2];
			if (isUserAuthentic) {
				if (isPassWordActive) {
					user = new User();
					user.setMasterLoginId(loginDetails.getMasterLoginId());
					user.setValidMins((nSSOProperties.getValidityMins() != null
							? Long.parseLong(nSSOProperties.getValidityMins()) : 60));
					ticket = tHelper.createTicket(user, false);
				} else {
					ticket.setValidityReason("Password Expired");
				}
			} else if (isAccountLocked) {
        ticket.setValidityReason(
            String.format("Account has been locked!!, Please try after sometime"));
            } else {
				ticket.setValidityReason("Invalid User Credentials");
			}
			rToken = new RefreshToken();
			rToken.setValid(true);
			rToken.setMasterLoginId(loginDetails.getMasterLoginId());
			rToken.setValidUpto(System.currentTimeMillis() + (nSSOProperties.getRefreshTokenValidityMins() != null
							? Long.parseLong(nSSOProperties.getRefreshTokenValidityMins()) : 1440) * 60 * 1000);

		} catch (DataAccessException de) {
			logger.error("Exception occured creating ticket ", de, null);
			ticket.setValidityReason("Database error. Please contact server Administrator.");
			ticket.setError(de.getMessage());
			return new LoginResponse(Jwts.builder().setSubject(loginDetails.getMasterLoginId()).claim("ticket", ticket)
					.setIssuedAt(new Date()).signWith(SignatureAlgorithm.HS256, nSSOProperties.getJwtSecretKey()).compact());
		} catch (Exception e) {
			logger.error("Exception occured creating ticket ", e, null);
			return null;
		}

		return new LoginResponse(Jwts.builder().setSubject(loginDetails.getMasterLoginId()).claim("ticket", ticket)
				.setIssuedAt(new Date()).signWith(SignatureAlgorithm.HS256, nSSOProperties.getJwtSecretKey()).compact(),Jwts.builder().setSubject(loginDetails.getMasterLoginId()).claim("ticket", rToken)
				.setIssuedAt(new Date()).signWith(SignatureAlgorithm.HS256, nSSOProperties.getJwtSecretKey()).compact(),ticket.isValid(),ticket.getValidityReason());
	}

	   @RequestMapping(value = "/auth/customer/details", method = RequestMethod.POST)
	    public UserCustomerMetaData getCustomerDetailsbyUser(HttpServletResponse response, @RequestHeader("Authorization") String token) {
	        logger.info("Authenticating & getting the customerDetails starts here");
	        token = token.replaceAll("Bearer", "").trim();
	        UserCustomerMetaData userRelatedMetaData = new UserCustomerMetaData();
	        Claims body = Jwts.parser().setSigningKey(nSSOProperties.getJwtSecretKey()).parseClaimsJws(token).getBody();
	        ObjectMapper objectMapper = new ObjectMapper();
	        objectMapper.configure(JsonParser.Feature.ALLOW_UNQUOTED_FIELD_NAMES, true);
	        objectMapper.enable(DeserializationFeature.FAIL_ON_READING_DUP_TREE_KEY);
	        JsonNode node = null;
	        try {
	        node = mapper.readTree(mapper.writeValueAsString(body.get("ticket")));
	        userRelatedMetaData.setCustCode(node.get("custCode").asText());
	        Object dsk = node.get("dataSecurityKey");
	            if (dsk!=null) {
	              userRelatedMetaData.setDataSKey(dsk);
	            }
	            if (node.get("roleCode").asText()!=null) {userRelatedMetaData.setRoleCode(node.get("roleCode").asText());}
	            if (node.get("roleType").asText()!=null) {userRelatedMetaData.setRoleType(node.get("roleType").asText());}
	            if (node.get("userFullName").asText()!=null) {userRelatedMetaData.setUserFullName(node.get("userFullName").asText());}
	            userRelatedMetaData.setUserName(node.get("masterLoginId").asText());
	            userRelatedMetaData.setValid(true);
	        } catch (DataAccessException de) {
	            logger.error("Exception occured creating ticket ", de, null);
	            userRelatedMetaData.setValid(false);
	            setUnauthorized(response);
	        } catch (Exception e) {
	            logger.error("Exception occured creating ticket ", e);
	            userRelatedMetaData.setValid(false);
              setUnauthorized(response);
	        }
	        logger.info("Authenticating & getting the customerDetails ends here");
	        return userRelatedMetaData;
	    }
	/**
	 *
	 * @param token
	 * @return
	 */
	@RequestMapping(value = "/authentication", method = RequestMethod.GET)
	public SSOResponse SSOAuthentication(@RequestParam("jwt") String token , HttpServletRequest request, HttpServletResponse response) {
        response.setHeader("Cache-Control","private");
       return ssoRequestHandler.processSSORequest(token);
	}

	@RequestMapping(value = "/getNewAccessToken", method = RequestMethod.POST)
  public LoginResponse accessToken(@RequestBody String rToken, HttpServletResponse response) throws ServletException {
    Boolean validity = false;
    try {
      Claims refreshToken = Jwts.parser().setSigningKey(nSSOProperties.getJwtSecretKey()).parseClaimsJws(rToken).getBody();
      // Check if the refresh Token is valid
      Iterator<?> it = ((Map<String, Object>) refreshToken.get("ticket")).entrySet().iterator();
      String masterLoginId = null;
      while (it.hasNext()) {
        Map.Entry<String, Object> pair = (Map.Entry<String, Object>) it.next();
        if (pair.getKey().equals("validUpto")) {
          validity = Long.parseLong(pair.getValue().toString()) > (new Date().getTime());
        }
        if (pair.getKey().equals("masterLoginId")) {
          masterLoginId = pair.getValue().toString();
        }
        it.remove();
      }
      if (!validity) {
        return new LoginResponse(validity, "Token has expired. Please re-login");
      } else {

        logger.info("Ticket will be created..");
        logger.info("Token Expiry :" + nSSOProperties.getValidityMins());

        Ticket ticket = null;
        User user = null;
        ticket = new Ticket();
        ticket.setMasterLoginId(masterLoginId);
        RefreshToken newRToken = null;
        try {
          user = new User();
          user.setMasterLoginId(masterLoginId);
          user.setValidMins((nSSOProperties.getValidityMins() != null
              ? Long.parseLong(nSSOProperties.getValidityMins()) : 60));
          ticket = tHelper.createTicket(user, false);
          newRToken = new RefreshToken();
          newRToken.setValid(true);
          newRToken.setMasterLoginId(masterLoginId);
          newRToken
              .setValidUpto(System.currentTimeMillis() + (nSSOProperties.getRefreshTokenValidityMins() != null
                  ? Long.parseLong(nSSOProperties.getRefreshTokenValidityMins()) : 1440) * 60 * 1000);
        } catch (DataAccessException de) {
          logger.error("Exception occured creating ticket ", de, null);
          ticket.setValidityReason("Database error. Please contact server Administrator.");
          ticket.setValid(false);
          ticket.setError(de.getMessage());
          return new LoginResponse(Jwts.builder().setSubject(masterLoginId).claim("ticket", ticket)
              .setIssuedAt(new Date()).signWith(SignatureAlgorithm.HS256, nSSOProperties.getJwtSecretKey()).compact());
        } catch (Exception e) {
          logger.error("Exception occured creating ticket ", e, null);
          return null;
        }

        ticket.setValid(true);
        return new LoginResponse(
            Jwts.builder().setSubject(masterLoginId).claim("ticket", ticket).setIssuedAt(new Date())
                .signWith(SignatureAlgorithm.HS256, nSSOProperties.getJwtSecretKey()).compact(),
            Jwts.builder().setSubject(masterLoginId).claim("ticket", newRToken).setIssuedAt(new Date())
                .signWith(SignatureAlgorithm.HS256, nSSOProperties.getJwtSecretKey()).compact());
      }

    }catch (Exception ex) {
      response.setStatus(HttpStatus.UNAUTHORIZED.value());
      logger.error("Error occurred while validating the ticket : {}", ex.getMessage());
      return new LoginResponse(validity, UNAUTHORIZED_USER);
    }
  }


	@RequestMapping(value = "/getDefaults", method = RequestMethod.POST)
	public LoginResponse getDefaults(@RequestBody LoginDetails loginDetails) {

		logger.info("Ticket will be created..");
		logger.info("Token Expiry :" + nSSOProperties.getValidityMins());

		Ticket ticket = null;
		User user = null;
		ticket = new Ticket();
		ticket.setMasterLoginId(loginDetails.getMasterLoginId());
		ticket.setValid(false);
		RefreshToken rToken = null;
		try {
			user = new User();
			user.setMasterLoginId(loginDetails.getMasterLoginId());
			user.setValidMins((nSSOProperties.getValidityMins() != null
					? Long.parseLong(nSSOProperties.getValidityMins()) : 60));
			ticket = tHelper.createDefaultTicket(user, true);
			rToken = new RefreshToken();
			rToken.setValid(true);
			rToken.setValidUpto((nSSOProperties.getRefreshTokenValidityMins() != null
							? Long.parseLong(nSSOProperties.getRefreshTokenValidityMins()) : 1440) * 60 * 1000);
		} catch (DataAccessException de) {
			logger.error("Exception occured creating ticket ", de, null);
			ticket.setValidityReason("Database error. Please contact server Administrator.");
			ticket.setError(de.getMessage());
			return new LoginResponse(Jwts.builder().setSubject(loginDetails.getMasterLoginId()).claim("ticket", ticket)
					.setIssuedAt(new Date()).signWith(SignatureAlgorithm.HS256, nSSOProperties.getJwtSecretKey()).compact());
		} catch (Exception e) {
			logger.error("Exception occured creating ticket ", e, null);
			return null;
		}

		return new LoginResponse(Jwts.builder().setSubject(loginDetails.getMasterLoginId()).claim("ticket", ticket)
				.setIssuedAt(new Date()).signWith(SignatureAlgorithm.HS256, nSSOProperties.getJwtSecretKey()).compact(),Jwts.builder().setSubject(loginDetails.getMasterLoginId()).claim("rToken", rToken)
				.setIssuedAt(new Date()).signWith(SignatureAlgorithm.HS256, nSSOProperties.getJwtSecretKey()).compact());
	}

	/**
	 * This method will be Deprecated since its uses user input as ticket Id.
     * Logout ticketid should be extracted from token see method {@link #logout(String)}
	 * @param ticketID
	 * @return
	 */
	@Deprecated
	@RequestMapping(value = "/auth/doLogout", method = RequestMethod.POST)
	public String doLogout(@RequestBody String ticketID) {
		Gson gson = new Gson();
		try {
			return gson.toJson(tHelper.logout(ticketID));
		} catch (DataAccessException de) {
			logger.error("Error while logout {}", de);
			return de.getMessage();
		}
	}

    /**
     *
     * @return
     */
    @RequestMapping(value = "/auth/doLogout", method = RequestMethod.GET)
    public String logout(@RequestHeader("Authorization") String token) {
        token = token.replaceAll("Bearer", "").trim();
        Ticket ticket = null;
        try {
             ticket = TokenParser.retrieveTicket(token);
        } catch (IOException e) {
            logger.error("Error occurred while parsing the Token {}",e);
        }
        try {
            return tHelper.logout(ticket != null ? ticket.getTicketId() : null);
        } catch (DataAccessException de) {
					  logger.error("Error occurred while parsing the Token {}",de);
            return de.getMessage();
        }
    }

	/**
	 *
	 * @param changePasswordDetails
	 * @return
	 */
	@RequestMapping(value = "/auth/changePassword", method = RequestMethod.POST)
	public Valid changePassword(HttpServletRequest request, HttpServletResponse response, @RequestBody ChangePasswordDetails changePasswordDetails) {
		Valid valid = new Valid();
		valid.setValid(false);
		String oldPass = changePasswordDetails.getOldPassword();
		String newPass = changePasswordDetails.getNewPassword();
		String cnfNewPass = changePasswordDetails.getCnfNewPassword();
		String loginId = changePasswordDetails.getMasterLoginId();
		String message = null;
    Ticket ticket = SipCommonUtils.getTicket(request);
    if (ticket != null && !ticket.getMasterLoginId().equalsIgnoreCase(loginId)) {
      valid.setValidityMessage(UNAUTHORIZED_USER);
			response.setStatus(HttpStatus.UNAUTHORIZED.value());
      return valid;
    }

    if (!cnfNewPass.equals(newPass)) {
			message = "'New Password' and 'Verify password' does not match.";
			valid.setValidityMessage(message);
			valid.setValid(false);
			return valid;
		} else if (newPass.equals(oldPass)) {
            message = "Old password and new password should not be same.";
            valid.setValidityMessage(message);
            valid.setValid(false);
            return valid;
        }

		valid = PasswordValidation.validatePassword(newPass,loginId);

		if (message == null && valid.getValid()) {
			try {
				message = userRepository.changePassword(loginId, newPass, oldPass);
				if ("Password Successfully Changed.".equals(message)) {
					valid.setValid(true);
                    valid.setValidityMessage(message);
                    return valid;
				} else if ("Value provided for old Password did not match.".equals(message)) {
				    valid.setValid(false);
				    valid.setValidityMessage(message);
				    return valid;
                }

			} catch (DataAccessException de) {
				logger.error("Database error. Please contact server Administrator. {}", de);
				valid.setValidityMessage("Database error. Please contact server Administrator.");
				valid.setError(de.getMessage());
				return valid;
			}
		}

		return valid;
	}

	/**
	 *
	 * @param resetPwdDtls
	 * @return
	 */
	@RequestMapping(value = "/resetPassword", method = RequestMethod.POST)
	public Valid resetPassword(@RequestBody ResetPwdDtls resetPwdDtls) {
		Valid valid = new Valid();
		valid.setValid(false);
		String message = null;
		try {
			String eMail = userRepository.getUserEmailId(resetPwdDtls.getMasterLoginId());
			if ("Invalid".equals(eMail)) {
				logger.error("Invalid user Id, Unable to perform Reset Password Process. error message:", null, null);
				message = "Invalid user Id";
			} else if ("no email".equals(eMail)) {
				logger.error("Email Id is not configured for the User", null, null);
				message = "Email Id is not configured for the User";
			} else {
				Long createdTime = System.currentTimeMillis();
				String randomString = randomString(160);
				userRepository.insertResetPasswordDtls(resetPwdDtls.getMasterLoginId(), randomString, createdTime,
						createdTime + (24 * 60 * 60 * 1000));
				String resetpwdlk = resetPwdDtls.getProductUrl();
				resetpwdlk = resetpwdlk + "?rhc=" + randomString;
				message = sendResetMail(resetPwdDtls.getMasterLoginId(), eMail, resetpwdlk, createdTime);
				if (message == null) {
					valid.setValid(true);
					message = "Mail sent successfully to " + eMail;
				}
			}
		} catch (DataAccessException de) {
			logger.error("Database error. Please contact server Administrator. {}", de);
			valid.setValidityMessage("Database error. Please contact server Administrator.");
			valid.setError(de.getMessage());
			return valid;
		}
		valid.setValidityMessage(message);
		return valid;
	}

	/**
	 *
	 * @param randomHashcode
	 * @return
	 */
	@RequestMapping(value = "/vfyRstPwd", method = RequestMethod.POST)
	public ResetValid vfyRstPwd(@RequestBody RandomHashcode randomHashcode) {
		// P2: handle expired password scenario
		ResetValid rv = new ResetValid();
		try {
			rv = userRepository.validateResetPasswordDtls(randomHashcode.getRhc());
		} catch (DataAccessException de) {
			rv.setValid(false);
			rv.setValidityReason("Database error. Please contact server Administrator.");
			rv.setError(de.getMessage());
			logger.error("Database error. Please contact server Administrator. {}", de);
			return rv;
		}
		return rv;
	}

	private JavaMailSender javaMailSender() {
		System.setProperty("java.net.preferIPv4Stack", "true");
		JavaMailSenderImpl mailSender = new JavaMailSenderImpl();
		Properties mailProperties = new Properties();
		mailSender.setJavaMailProperties(mailProperties);
		mailSender.setHost(nSSOProperties.getMailHost());
		mailSender.setPort(nSSOProperties.getMailPort());

		mailSender.setUsername(nSSOProperties.getMailUserName());
		if (nSSOProperties.getMailPassword().length != 0) {
			mailSender.setPassword(new String(nSSOProperties.getMailPassword()));
		}
		return mailSender;
	}

	/**
	 *
	 * @param changePasswordDetails
	 * @return
	 */
	@RequestMapping(value = "/rstChangePassword", method = RequestMethod.POST)
	public Valid rstChangePassword(@RequestBody ChangePasswordDetails changePasswordDetails) {
		Valid valid = new Valid();
		valid.setValid(false);
		String message = null;
		// String oldPass = changePasswordDetails.getOldPassword();
		String newPass = changePasswordDetails.getNewPassword();
		String cnfNewPass = changePasswordDetails.getCnfNewPassword();
		String loginId = changePasswordDetails.getMasterLoginId();
		String rhc = changePasswordDetails.getRfc();

		if (!cnfNewPass.equals(newPass)) {
			valid.setValid(false);
			valid.setValidityMessage("'New Password' and 'Verify password' does not match.");
			return valid;
		}

		valid = PasswordValidation.validatePassword(newPass,loginId);

        // Validate the hash key with userID before proceeding.
        ResetValid resetValid =  userRepository.validateResetPasswordDtls(rhc);
        if (!(resetValid.getValid() || (resetValid.getMasterLoginID()!=null &&
            resetValid.getMasterLoginID().equalsIgnoreCase(loginId))))
            message= "Reset link is not valid or expired ";

		try {
			if (message == null && valid.getValid()) {
				message = userRepository.rstchangePassword(loginId, newPass,rhc);
				if (message == null) {
					message = "Password Successfully Changed.";
					valid.setValidityMessage(message);
					valid.setValid(true);
				}
			}
		} catch (DataAccessException de) {
			valid.setValidityMessage("Database error. Please contact server Administrator.");
			valid.setError(de.getMessage());
			return valid;
		}

		return valid;
	}

	/**
	 *
	 * @param ticketId
	 * @return
	 */
	@RequestMapping(value = "/auth/reCreateTicket", method = RequestMethod.POST)
	public String reCreateTicket(@RequestBody String ticketId) {
		logger.info("ReCreating process start for ticket", ticketId, null);
		Ticket ticket = tHelper.reCreateTicket(ticketId,
				nSSOProperties.getValidityMins() != null ? Long.parseLong(nSSOProperties.getValidityMins()) : 720);
		Gson gson = new Gson();
		return gson.toJson(ticket);
	}

	@SuppressWarnings("unused")
	private static class LoginResponse {

		public String aToken;
		public String rToken;
		public boolean validity;
		public String message;

		public LoginResponse(final String aToken) {
			this.aToken = aToken;
		}

		public LoginResponse(final String aToken, final String rToken) {
			this.aToken = aToken;
			this.rToken = rToken;
		}

		public LoginResponse(boolean validity, String message) {
			this.validity = validity;
			this.message = message;
		}

		public LoginResponse(final String aToken, final  String rToken, boolean validity,
            String message) {
            this.aToken = aToken;
            this.rToken = rToken;
		    this.validity = validity;
            this.message = message;
        }
	}

	@SuppressWarnings("unused")
	private static class UserLogin {
		public String masterLoginId;
		public String password;
	}

	final String reqChars = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz._";
	SecureRandom random = null;

	private String randomString(int len) {
		random = new SecureRandom();
		StringBuilder sb = new StringBuilder(len);
		for (int i = 0; i < len; i++)
			sb.append(reqChars.charAt(random.nextInt(reqChars.length())));
		return sb.toString();
	}

	private String sendResetMail(String masterLoginId, String eMail, String resetpwdlk, long createdTime) {
		String errorMessage = null;
		JavaMailSender javaMailSender = javaMailSender();

		// create a message
		javax.mail.internet.MimeMessage simpleMessage = javaMailSender.createMimeMessage();

		MimeBodyPart mbp = new MimeBodyPart();
		InternetAddress toAddress = null;
		InternetAddress fromAddress = null;
		try {
			String from = nSSOProperties.getMailFrom();
			fromAddress = new InternetAddress(from);

		} catch (AddressException e) {
			errorMessage = "Invalid 'From Address' value [" + nSSOProperties.getMailFrom() + "].";
			return errorMessage;
		}
		try {
			toAddress = new InternetAddress(eMail);
			InternetAddress[] toAddressA = { toAddress };
			simpleMessage.setRecipients(Message.RecipientType.TO, toAddressA);
		} catch (Exception e) {
			// logger.error("MailSender - sendMail - Exception -",e);
			errorMessage = "Invalid 'To Address' value [" + eMail + "].";
			return errorMessage;
		}
		try {
			simpleMessage.setFrom(fromAddress);
			String subject = nSSOProperties.getMailSubject();
			simpleMessage.setSubject(subject);
			String text = "";
			text += "You recently requested a password reset for your Synchronoss Application. To create a new password, click on the link below, this link will expire after 24 hours:";
			text += "<br><br><a href=\"" + resetpwdlk + "\"> Reset My Password </a> ";
			text += "<br><br>This request was made on : " + new Date(createdTime);
			text += "<br><br>Regards, ";
			text += "<br>Synchronoss Application Support";
			text += "<br><br>********************************************************";
			text += "<br>Please do not reply to this message. Mail sent to this address cannot be answered.";
			// simpleMessage.setText(text);

			mbp.setContent(text, "text/html");
			MimeMultipart multipart = new MimeMultipart();
			multipart.addBodyPart(mbp);
			simpleMessage.setContent(multipart);
			javaMailSender.send(simpleMessage);
		} catch (Exception e) {
			errorMessage = "Error occured while sending mail. Please try again or contact Administrator.";
			logger.error(e.getMessage(), masterLoginId, null, e);
		}

		return errorMessage;
	}

	/**
	 * This method return whether the token is valid or not
	 *
	 * @param
	 * @return
	 */
	@RequestMapping(value = "/auth/validateToken", method = RequestMethod.POST)
	public Valid validateToken() {
		Valid valid = new Valid();
		valid.setValid(true);
		valid.setValidityMessage("Token is valid");
		return valid;
	}

	/**
	 * No longer being used , mark as deprecated .
	 * @param analysis
	 * @return
	 */
	@Deprecated
	@RequestMapping(value = "/auth/analysis/createAnalysis", method = RequestMethod.POST)
	public Valid createAnalysis(@RequestBody AnalysisSummary analysis) {
		Valid valid = new Valid();
		try {
			if (!analysis.getAnalysisName().isEmpty() && analysis.getAnalysisId() != null
					&& analysis.getFeatureId() != null && !analysis.getUserId().isEmpty()) {
				if (userRepository.createAnalysis(analysis)) {
					valid.setValid(true);
					valid.setValidityMessage("Analysis created successfully");
				} else {
					valid.setValid(false);
					valid.setError("Analysis not created");
				}
			} else {
				valid.setValid(false);
				valid.setError("Mandatory request params are missing");
			}
		} catch (DataAccessException de) {
			valid.setValid(false);
			valid.setValidityMessage("Database error. Please contact server Administrator.");
			valid.setError(de.getMessage());
			return valid;
		} catch (Exception e) {
			valid.setValid(false);
			valid.setValidityMessage("Error. Please contact server Administrator.");
			valid.setError(e.getMessage());
			return valid;
		}
		return valid;
	}

	/**
	 *  No longer being used , mark as deprecated .
	 * @param analysis
	 * @return
	 */
	@Deprecated
	@RequestMapping(value = "/auth/analysis/update", method = RequestMethod.POST)
	public Valid updateAnalysis(@RequestBody AnalysisSummary analysis) {
		Valid valid = new Valid();
		try {
			if (!analysis.getAnalysisName().isEmpty() && analysis.getAnalysisId() != null
					&& analysis.getFeatureId() != null && !analysis.getUserId().isEmpty()) {
				if (userRepository.updateAnalysis(analysis)) {
					valid.setValid(true);
					valid.setValidityMessage("Analysis is updated successfully");
				} else {
					valid.setValid(false);
					valid.setError("Analysis could not be updated");
				}
			} else {
				valid.setValid(false);
				valid.setError("Mandatory request params are missing");
			}
		} catch (DataAccessException de) {
			valid.setValid(false);
			valid.setValidityMessage("Database error. Please contact server Administrator.");
			valid.setError(de.getMessage());
			return valid;
		} catch (Exception e) {
			valid.setValid(false);
			valid.setValidityMessage("Error. Please contact server Administrator.");
			valid.setError(e.getMessage());
			return valid;
		}
		return valid;
	}

	/**
	 *  No longer being used , mark as deprecated .
	 * @param analysis
	 * @return
	 */
	@Deprecated
	@RequestMapping(value = "/auth/analysis/delete", method = RequestMethod.POST)
	public Valid deleteAnalysis(@RequestBody AnalysisSummary analysis) {
		Valid valid = new Valid();
		try {
			if (analysis.getAnalysisId() != null) {
				if (userRepository.deleteAnalysis(analysis)) {
					valid.setValid(true);
					valid.setValidityMessage("Analysis deleted successfully");
				} else {
					valid.setValid(false);
					valid.setError("Analysis could not be deleted");
				}
			} else {
				valid.setValid(false);
				valid.setError("Mandatory request params are missing");
			}
		} catch (DataAccessException de) {
			valid.setValid(false);
			valid.setValidityMessage("Database error. Please contact server Administrator.");
			valid.setError(de.getMessage());
			return valid;
		} catch (Exception e) {
			valid.setValid(false);
			valid.setValidityMessage("Error. Please contact server Administrator.");
			valid.setError(e.getMessage());
			return valid;
		}
		return valid;
	}

	/**
	 *  No longer being used , mark as deprecated .
	 * @param featureId
	 * @return
	 */
	@Deprecated
	@RequestMapping(value = "/auth/analysis/fetch/{featureId}", method = RequestMethod.GET)
	public AnalysisSummaryList getAnalysisByFeatureID(@PathVariable("featureId") Long featureId) {
		return userRepository.getAnalysisByFeatureID(featureId);
	}

	/**
	 *
	 * @param customerId
	 * @return
	 */
	@RequestMapping(value = "/auth/admin/cust/manage/users/fetch", method = RequestMethod.POST)
	public UsersList getUsers(HttpServletRequest request, @RequestBody Long customerId) {
		UsersList userList = new UsersList();
		try {
      Ticket ticket = SipCommonUtils.getTicket(request);
      if (securityService.haveValidCustomerId(ticket, customerId)) {
				userList.setUsers(userRepository.getUsers(customerId));
				userList.setValid(true);
			} else {
				userList.setValid(false);
				userList.setError("Mandatory request params are missing");
			}
		} catch (Exception e) {
			userList.setValid(false);
			String message = (e instanceof DataAccessException) ? "Database error." : "Error.";
			userList.setValidityMessage(message + " Please contact server Administrator");
			userList.setError(e.getMessage());
			return userList;
		}
		return userList;
	}

    /**
     * Fetches all the security Group Names
     * @return List of group names
     */
	@RequestMapping(value = "/auth/admin/security-groups",method = RequestMethod.GET)
        public List<SecurityGroups> getSecurityGroups(HttpServletRequest request,HttpServletResponse response) {
        String jwtToken = JWTUtils.getToken(request);
        String [] extractValuesFromToken = JWTUtils.parseToken(jwtToken, nSSOProperties.getJwtSecretKey());
        Long custId = Long.valueOf(extractValuesFromToken[1]);
	    List<SecurityGroups> groupNames = dataSecurityKeyRepository.fetchSecurityGroupNames(custId);
	    return groupNames;
    }

    /**
     * Adding newly security Group Name to SEC_GROUP.
     * @param securityGroups
     * @return Valid obj containing Boolean, success/failure msg
     */
    @RequestMapping(value = "/auth/admin/security-groups",method = RequestMethod.POST)
    public Object addSecurityGroups(HttpServletRequest request, HttpServletResponse response,@RequestBody SecurityGroups securityGroups)  {
        String jwtToken = JWTUtils.getToken(request);
        String [] extractValuesFromToken = JWTUtils.parseToken(jwtToken,nSSOProperties.getJwtSecretKey());
        String createdBy = extractValuesFromToken[2];
        String roleType = extractValuesFromToken[3];
        Long custId = Long.valueOf(extractValuesFromToken[1]);
        if (!roleType.equalsIgnoreCase(AdminRole)) {
            Valid valid = new Valid();
            response.setStatus(400);
            valid.setValid(false);
            valid.setValidityMessage(ServerResponseMessages.ADD_GROUPS_WITH_NON_ADMIN_ROLE);
            valid.setError(ServerResponseMessages.ADD_GROUPS_WITH_NON_ADMIN_ROLE);
            return valid;
        }
        DskValidity dskValidity = dataSecurityKeyRepository.addSecurityGroups(securityGroups,createdBy,custId);
        if ( dskValidity.getValid().booleanValue() == true )    {
            return dskValidity;
        }
        else {
            response.setStatus(400);
            Valid valid = new Valid();
            valid.setValidityMessage(dskValidity.getValidityMessage());
            valid.setError(dskValidity.getError());
            valid.setValid(dskValidity.getValid());
            return valid;
            // Here we are sending two different Objects, one in case of Success and another object is returned in case of
            // Error. This change has to be retained else all other REST API has to unified since we are following this convention.
        }
    }


    @RequestMapping(value = "/auth/admin/dsk-security-groups",method = RequestMethod.POST)
    @ApiOperation(value="Add DSK security group")
    public DskGroupPayload addDskGroup(HttpServletRequest request, HttpServletResponse response,
        @ApiParam(value="DSK group details")
        @RequestBody
        DskGroupPayload dskGroupPayload) {
      Ticket ticket = SipCommonUtils.getTicket(request);

      Long customerId = Long.valueOf(ticket.getCustID());
      String createdBy = ticket.getUserFullName();
      RoleType roleType = ticket.getRoleType();

      if (roleType != RoleType.ADMIN) {
          DskGroupPayload payload = new DskGroupPayload();
          logger.error("Invalid user");
          response.setStatus(HttpStatus.UNAUTHORIZED.value());
          payload.setValid(false);
          payload.setMessage(ServerResponseMessages.ADD_GROUPS_WITH_NON_ADMIN_ROLE);

          return payload;
      }

      DskGroupPayload responsePayload = null;
      Long securityGroupSysId = null;
      try {
          String securityGroupName = dskGroupPayload.getGroupName();
          String securityGroupDescription = dskGroupPayload.getGroupDescription();
          SipDskAttribute dskAttribute = dskGroupPayload.getDskAttributes();

          SecurityGroups securityGroup = new SecurityGroups();

          if (securityGroupName == null || securityGroupName.length() == 0) {
              responsePayload = new DskGroupPayload();

              responsePayload.setValid(false);
              responsePayload.setMessage("Group name is mandatory");
              response.setStatus(HttpStatus.BAD_REQUEST.value());

              return responsePayload;
          }

          if (dskAttribute == null) {
              responsePayload = new DskGroupPayload();

              responsePayload.setValid(false);
              responsePayload.setMessage("DSK attributes are mandatory");

              response.setStatus(HttpStatus.BAD_REQUEST.value());

              return responsePayload;
          }
          securityGroup.setSecurityGroupName(securityGroupName);
          securityGroup.setDescription(securityGroupDescription);

          DskValidity dskValidity =
              dataSecurityKeyRepository.addSecurityGroups(securityGroup,createdBy,customerId);

          securityGroupSysId = dskValidity.getGroupId();

          if (securityGroupSysId == null) {
              responsePayload = new DskGroupPayload();
              logger.error("Error occurred: " + dskValidity.getError());
              response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
              responsePayload.setValid(false);
              responsePayload.setMessage(dskValidity.getError());

              return responsePayload;
          }

          // Prepare the list og attribute models. This will also validate for missing attributes.
          List<SipDskAttributeModel> attributeModelList = dataSecurityKeyRepository.
              prepareDskAttributeModelList(securityGroupSysId, dskAttribute, Optional.empty());
          Valid valid = dataSecurityKeyRepository
              .addDskGroupAttributeModelAndValues(securityGroupSysId, attributeModelList);

          if (valid.getValid() == true) {
              responsePayload =
                  dataSecurityKeyRepository.fetchDskGroupAttributeModel(securityGroupSysId, customerId);

              responsePayload.setValid(true);
          } else {
              responsePayload = new DskGroupPayload();

              responsePayload.setValid(false);
              responsePayload.setMessage(valid.getError());

              if (securityGroupSysId != null) {
                  dataSecurityKeyRepository.deleteSecurityGroups(securityGroupSysId);
              }

              response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
          }

      } catch (Exception ex) {

          if (securityGroupSysId != null) {
              dataSecurityKeyRepository.deleteSecurityGroups(securityGroupSysId);
          }
          responsePayload = new DskGroupPayload();
          responsePayload.setValid(false);
          response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
          responsePayload.setMessage("Error occurred: " + ex.getMessage());
      }

      return responsePayload;
    }

    @RequestMapping(value = "/auth/admin/dsk-security-groups",method = RequestMethod.GET)
    @ApiOperation(value="Fetch security group details for a customer")
    public Object getAllSecGroupDetailsForCustomer(
        HttpServletRequest request,
        HttpServletResponse response
    ) {
        List<DskGroupPayload> payload = null;

        Ticket ticket = SipCommonUtils.getTicket(request);

        Long customerId = Long.valueOf(ticket.getCustID());
        RoleType roleType = ticket.getRoleType();

        if (roleType != RoleType.ADMIN) {
            Valid valid = new Valid();
            logger.error("Invalid user");
            response.setStatus(HttpStatus.UNAUTHORIZED.value());
            valid.setValid(false);
            valid.setValidityMessage(ServerResponseMessages.WITH_NON_ADMIN_ROLE);
            return valid;
        }

        try {
            payload = dataSecurityKeyRepository.fetchAllDskGroupForCustomer(customerId);
        } catch (Exception ex) {
            logger.error("Error occurred while fetching security group details: "
                + ex.getMessage(), ex);

            Valid valid = new Valid();
            valid.setValidityMessage(ex.getMessage());
            valid.setValid(false);

            response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
        }

        return payload;
    }


    @RequestMapping(value = "/auth/admin/dsk-security-groups/{securityGroupId}",method = RequestMethod.GET)
    @ApiOperation(value="Fetch security group details for a given security group id")
    public DskGroupPayload getSecurityGroupDetails (
        @PathVariable(name = "securityGroupId", required = true) Long securityGroupSysId,
        HttpServletRequest request,
        HttpServletResponse response) {
        DskGroupPayload payload;

        Ticket ticket = SipCommonUtils.getTicket(request);

        RoleType roleType = ticket.getRoleType();

        if (roleType != RoleType.ADMIN) {
            payload = new DskGroupPayload();
            logger.error("Invalid user");
            response.setStatus(HttpStatus.UNAUTHORIZED.value());
            payload.setValid(false);
            payload.setMessage(ServerResponseMessages.WITH_NON_ADMIN_ROLE);

            return payload;
        }


        try {
            Long customerId = Long.valueOf(ticket.getCustID());
            payload = dataSecurityKeyRepository
                .fetchDskGroupAttributeModel(securityGroupSysId, customerId);
            return payload;
        } catch (Exception ex) {
            payload = new DskGroupPayload();
            payload.setValid(false);
            response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
            payload.setMessage("Error occurred: " + ex.getMessage());

            return payload;
        }

    }

    @RequestMapping(value = "/auth/admin/dsk-security-groups/{securityGroupId}",method = RequestMethod.DELETE)
    @ApiOperation(value = "Delete security group attributes")
    public Valid deleteSecurityGroupAttributeModel (
        @PathVariable(name = "securityGroupId", required = true) Long securityGroupSysId,
        HttpServletRequest request,
        HttpServletResponse response) {

        Ticket ticket = SipCommonUtils.getTicket(request);
        RoleType roleType = ticket.getRoleType();

        Valid valid;
        if (roleType != RoleType.ADMIN) {
            valid = new Valid();
            logger.error("Invalid user");
            response.setStatus(HttpStatus.UNAUTHORIZED.value());
            valid.setValid(false);
            valid.setValidityMessage(ServerResponseMessages.WITH_NON_ADMIN_ROLE);

            return valid;
        }

        try {

            Long customerId = Long.valueOf(ticket.getCustID());

            valid = dataSecurityKeyRepository.deleteDskGroupAttributeModel(securityGroupSysId, customerId);

            if (valid.getValid()) {
                // If the deletion of security group attributes is successful, delete the security group
                // also
                valid = dataSecurityKeyRepository.deleteSecurityGroups(securityGroupSysId);
            }
        } catch (Exception ex) {
            valid = new Valid();
            valid.setValid(false);
            valid.setError(ex.getMessage());
            valid.setValidityMessage("Error occurred while deleting security group");
        }

        return valid;
    }

    @RequestMapping(value = "/auth/admin/dsk-security-groups/{securityGroupId}",method = RequestMethod.PUT)
    @ApiOperation(value="Update security group attributes")
    public DskGroupPayload updateSecurityGroupAttributeModel (
        @PathVariable(name = "securityGroupId", required = true) Long securityGroupSysId,
        HttpServletRequest request,
        HttpServletResponse response,
        @ApiParam(value="New attributes for the security group")
        @RequestBody SipDskAttribute sipDskAttributes) {
        DskGroupPayload payload = null;

        Ticket ticket = SipCommonUtils.getTicket(request);
        Long customerId = Long.valueOf(ticket.getCustID());

        RoleType roleType = ticket.getRoleType();

        if (roleType != RoleType.ADMIN) {
            payload = new DskGroupPayload();
            logger.error("Invalid user");
            response.setStatus(HttpStatus.UNAUTHORIZED.value());
            payload.setValid(false);
            payload.setMessage(ServerResponseMessages.WITH_NON_ADMIN_ROLE);

            return payload;
        }

        try {

            if (sipDskAttributes == null) {
                payload = new DskGroupPayload();

                payload.setValid(false);
                payload.setMessage("Invalid request");
                response.setStatus(HttpStatus.BAD_REQUEST.value());

                return payload;
            }

            Valid valid = dataSecurityKeyRepository
                .validateCustomerForSecGroup(securityGroupSysId, customerId);


            if (!valid.getValid()) {
                payload = new DskGroupPayload();

                payload.setValid(false);
                payload.setMessage(valid.getValidityMessage());

                return payload;
            }

      List<SipDskAttributeModel> dskAttributeModelList =
          dataSecurityKeyRepository.prepareDskAttributeModelList(
              securityGroupSysId, sipDskAttributes, Optional.empty());
            // Delete the existing security group attributes
            valid = dataSecurityKeyRepository.deleteDskGroupAttributeModel(securityGroupSysId, customerId);

            if (valid.getValid()) {
                // Add the new security group attributes
                logger.info("Deleted existing DSK attributes");
                valid = dataSecurityKeyRepository
                    .addDskGroupAttributeModelAndValues(securityGroupSysId, dskAttributeModelList);

                if (valid.getValid()) {
                    logger.info("DSK attributes updated successfully");

                    payload = dataSecurityKeyRepository.fetchDskGroupAttributeModel(securityGroupSysId, customerId);
                    payload.setValid(true);
                } else {
                    logger.error("Error occurred: " + valid.getError());
                    payload = new DskGroupPayload();

                    payload.setValid(false);
                    payload.setMessage("Unable to update the dsk attributes");
                }

            } else {
                logger.error("Error occurred: " + valid.getError());
                payload = new DskGroupPayload();

                payload.setValid(false);
                payload.setMessage("Unable to update the dsk attributes");
            }
            // Fetch the attributes and return
        } catch (Exception ex) {
            logger.error("Error occurred while updating the security group attributes: " + ex.getMessage(), ex);

            payload = new DskGroupPayload();
            payload.setValid(false);
            payload.setMessage(ex.getMessage());
            response.setStatus(HttpStatus.BAD_REQUEST.value());
        }

        return payload;
    }

    /**
     * Updating existing Security Group name with new name
     * @param oldNewGroups 1st String resembles new security Group, second -> description of new security group name and third resembles existing.
     * @return Valid obj containing Boolean, success/failure msg
     */
    @RequestMapping(value = "/auth/admin/security-groups/{securityGroupId}/name",method = RequestMethod.PUT)
    public Object updateSecurityGroups(HttpServletRequest request, HttpServletResponse response,@PathVariable(name = "securityGroupId", required = true) Long securityGroupId, @RequestBody List<String> oldNewGroups) {
        String jwtToken = JWTUtils.getToken(request);
        String [] extractValuesFromToken = JWTUtils.parseToken(jwtToken,nSSOProperties.getJwtSecretKey());
        Long custId = Long.valueOf(extractValuesFromToken[1]);
        String roleType = extractValuesFromToken[3];
        if (!roleType.equalsIgnoreCase(AdminRole)) {
            Valid valid = new Valid();
            response.setStatus(400);
            valid.setValid(false);
            valid.setValidityMessage(ServerResponseMessages.MODIFY_GROUP_WITH_NON_ADMIN_ROLE);
            valid.setError(ServerResponseMessages.MODIFY_GROUP_WITH_NON_ADMIN_ROLE);
            return valid;
        }
        DskValidity dskValidity = dataSecurityKeyRepository.updateSecurityGroups(securityGroupId,oldNewGroups,custId);
        if (dskValidity.getValid().booleanValue())  {
            return dskValidity;
        }
        else {
            Valid valid = new Valid();
            response.setStatus(400);
            valid.setValid(false);
            valid.setValidityMessage(dskValidity.getValidityMessage());
            valid.setError(dskValidity.getError());
            return valid;
        }
    }

    /**
     * Deleting security group
     * @param securityGroupId
     * @return Valid obj containing Boolean and success/failure msg
     */
    @RequestMapping(value = "/auth/admin/security-groups/{securityGroupId}",method = RequestMethod.DELETE)
    public Valid deleteSecurityGroups(HttpServletRequest request, HttpServletResponse response,@PathVariable(name = "securityGroupId", required = true) Long securityGroupId)  {
        String jwtToken = JWTUtils.getToken(request);
        String [] extractValuesFromToken = JWTUtils.parseToken(jwtToken,nSSOProperties.getJwtSecretKey());
        String roleType = extractValuesFromToken[3];
        if (!roleType.equalsIgnoreCase(AdminRole)) {
            Valid valid = new Valid();
            response.setStatus(400);
            valid.setValid(false);
            valid.setValidityMessage(ServerResponseMessages.DELETE_GROUP_WITH_NON_ADMIN_ROLE);
            valid.setError(ServerResponseMessages.DELETE_GROUP_WITH_NON_ADMIN_ROLE);
            return valid;
        }
        Valid dskValidity = dataSecurityKeyRepository.deleteSecurityGroups(securityGroupId);
        if ( dskValidity.getValid().booleanValue())    {
            return dskValidity;
        }
        else {
            response.setStatus(400);
            return dskValidity;
        }
    }

    /**
     * Adding Attributes and values to security groups
     * @param attributeValues includes attribute names, dsk values, group name etc.
     * @return Valid obj containing Boolean, suceess/failure msg
     */
    @RequestMapping (value = "/auth/admin/security-groups/{securityGroupId}/dsk-attribute-values", method = RequestMethod.POST)
    public Valid addSecurityGroupDskAttributeValues(HttpServletRequest request, HttpServletResponse response,@PathVariable(name = "securityGroupId", required = true) Long securityGroupId, @RequestBody AttributeValues attributeValues)  {
        String jwtToken = JWTUtils.getToken(request);
        String [] extractValuesFromToken = JWTUtils.parseToken(jwtToken,nSSOProperties.getJwtSecretKey());
        String roleType = extractValuesFromToken[3];
        if (!roleType.equalsIgnoreCase(AdminRole)) {
            Valid valid = new Valid();
            response.setStatus(400);
            valid.setValid(false);
            valid.setValidityMessage(ServerResponseMessages.ADD_ATTRIBUTES_WITH_NON_ADMIN_ROLE);
            valid.setError(ServerResponseMessages.ADD_ATTRIBUTES_WITH_NON_ADMIN_ROLE);
            return valid;
        }
        Valid dskValidity = dataSecurityKeyRepository.addSecurityGroupDskAttributeValues(securityGroupId,attributeValues);

        if ( dskValidity.getValid().booleanValue())    {
            return dskValidity;
        }
        else {
            response.setStatus(400);
            return dskValidity;
        }
    }

    /**
     * Update Values for attributes.
     * @param attributeValues
     * @return Valid obj containing Boolean, suceess/failure msg
     */
    @RequestMapping ( value = "/auth/admin/security-groups/{securityGroupId}/dsk-attribute-values", method =  RequestMethod.PUT)
    public Valid updateAttributeValues(HttpServletRequest request, HttpServletResponse response,@PathVariable(name = "securityGroupId", required = true) Long securityGroupId, @RequestBody AttributeValues attributeValues)    {
        String jwtToken = JWTUtils.getToken(request);
        String [] extractValuesFromToken = JWTUtils.parseToken(jwtToken,nSSOProperties.getJwtSecretKey());
        String roleType = extractValuesFromToken[3];
        if (!roleType.equalsIgnoreCase(AdminRole)) {
            Valid valid = new Valid();
            response.setStatus(400);
            valid.setValid(false);
            valid.setValidityMessage(ServerResponseMessages.MODIFY_ATTRIBUTES_WITH_NON_ADMIN_ROLE);
            valid.setError(ServerResponseMessages.MODIFY_ATTRIBUTES_WITH_NON_ADMIN_ROLE);
            return valid;
        }
        Valid dskValidity = dataSecurityKeyRepository.updateAttributeValues(securityGroupId,attributeValues);
        if ( dskValidity.getValid().booleanValue())    {
            return dskValidity;
        }
        else {
            response.setStatus(400);
            return dskValidity;
        }
    }

    /**
     * Getting attribute list.
     * @param securityGroupId Group ID
     * @return List of Attribute names associated with corresponding security-group.
     */
    @RequestMapping (value = "/auth/admin/security-groups/{securityGroupId}/dsk-attributes", method = RequestMethod.GET)
    public List<String> fetchSecurityGroupDskAttributes(@PathVariable(name = "securityGroupId", required = true) Long securityGroupId)   {
	    return dataSecurityKeyRepository.fetchSecurityGroupDskAttributes(securityGroupId);
    }

    /**
     * Deleting assigned Attribute value pairs.
     * @param
     * @return Valid obj containing Boolean, suceess/failure msg
     */
    @RequestMapping (value = "/auth/admin/security-groups/{securityGroupId}/dsk-attributes/{attributeName}", method = RequestMethod.DELETE)
    public Valid deleteSecurityGroupDskAttribute(HttpServletRequest request, HttpServletResponse response,@PathVariable(name = "securityGroupId", required = true) Long securityGroupId,@PathVariable(name = "attributeName", required = true) String attributeName)   {
        String jwtToken = JWTUtils.getToken(request);
        String [] extractValuesFromToken = JWTUtils.parseToken(jwtToken,nSSOProperties.getJwtSecretKey());
        String roleType = extractValuesFromToken[3];
        if (!roleType.equalsIgnoreCase(AdminRole)) {
            Valid valid = new Valid();
            response.setStatus(400);
            valid.setValid(false);
            valid.setValidityMessage(ServerResponseMessages.DELETE_ATTRIBUTES_WITH_NON_ADMIN_ROLE);
            valid.setError(ServerResponseMessages.DELETE_ATTRIBUTES_WITH_NON_ADMIN_ROLE);
            return valid;
        }
        List<String> dskList = new ArrayList<>();
        dskList.add(0,securityGroupId.toString());
        dskList.add(1,attributeName);
        Valid dskValidity = dataSecurityKeyRepository.deleteSecurityGroupDskAttributeValues(dskList);
        if ( dskValidity.getValid().booleanValue())    {
            return dskValidity;
        }
        else {
            response.setStatus(400);
            return dskValidity;
        }
    }

    /**
     * Updating Group reference in user tbl
     * @param
     * @return Valid obj containing Boolean, success/failure msg
     */
    @RequestMapping ( value = "/auth/admin/users/{userSysId}/security-group", method = RequestMethod.PUT)
    public Valid updateUser(HttpServletRequest request, HttpServletResponse response, @PathVariable (name = "userSysId", required = true) Long userSysId, @RequestBody String securityGroupName)  {
        String jwtToken = JWTUtils.getToken(request);
        String [] extractValuesFromToken = JWTUtils.parseToken(jwtToken,nSSOProperties.getJwtSecretKey());
        Long custId = Long.valueOf(extractValuesFromToken[1]);
        String roleType = extractValuesFromToken[3];
        if (!roleType.equalsIgnoreCase(AdminRole)) {
            Valid valid = new Valid();
            response.setStatus(400);
            valid.setValid(false);
            valid.setValidityMessage(ServerResponseMessages.MODIFY_USER_GROUPS_WITH_NON_ADMIN_ROLE);
            valid.setError(ServerResponseMessages.MODIFY_USER_GROUPS_WITH_NON_ADMIN_ROLE);
            return valid;
        }
        Valid dskValidity = dataSecurityKeyRepository.updateUser(securityGroupName,userSysId,custId);
        if ( dskValidity.getValid().booleanValue())    {
            return dskValidity;
        }
        else {
            response.setStatus(400);
            return dskValidity;
        }
    }

    /**
     * Fetching all Dsk attribute values.
     * @param securityGroupId
     * @return List of Attribute-values
     */
    @RequestMapping ( value = "/auth/admin/security-groups/{securityGroupId}/dsk-attribute-values", method = RequestMethod.GET)
    public List<DskDetails> fetchDskAllAttributeValues(@PathVariable(name = "securityGroupId", required = true) Long securityGroupId)    {
        return dataSecurityKeyRepository.fetchDskAllAttributeValues(securityGroupId);
    }

    /**
     * Get user Assignments
     * @return List of all user Assignments
     */
    @RequestMapping ( value = "/auth/admin/user-assignments", method = RequestMethod.GET)
    public List<UserAssignment> getAllUserAssignments(HttpServletRequest request, HttpServletResponse response)  {
        String jwtToken = JWTUtils.getToken(request);
        String [] extractValuesFromToken = JWTUtils.parseToken(jwtToken,nSSOProperties.getJwtSecretKey());
        Long custId = Long.valueOf(extractValuesFromToken[1]);
	    return dataSecurityKeyRepository.getAllUserAssignments(custId);
    }

  /**
   * @param user
   * @return
   */
  @ApiOperation(
      value = " create User API ",
      nickname = "CreateUser",
      notes = "",
      response = UsersList.class)
  @RequestMapping(value = "/auth/admin/cust/manage/users/add", method = RequestMethod.POST)
  public UsersList addUser(HttpServletRequest request,
      @ApiParam(value = "Authorization token") @RequestHeader("Authorization") String authToken,
      @ApiParam(value = "User details to store", required = true) @RequestBody User user) {
    String[] valuesFromToken =
        JWTUtils.parseToken(authToken.substring(7), nSSOProperties.getJwtSecretKey());

    String masterLoginId = valuesFromToken[4];
    UsersList userList = new UsersList();
    Valid valid;
    try {
      Ticket ticket = SipCommonUtils.getTicket(request);
      if (user != null && securityService.haveValidCustomerId(ticket, user.getCustomerId())) {
        Valid validity = PasswordValidation.validatePassword(user.getPassword(), user.getMasterLoginId());
        userList.setValid(validity.getValid());
        userList.setValidityMessage(validity.getValidityMessage());

				if (userList.getValid()) {
					valid = userRepository.addUser(user,masterLoginId);
					if (valid.getValid()) {
						userList.setUsers(userRepository.getUsers(user.getCustomerId()));
						userList.setValid(true);
					} else {
						userList.setValid(false);
						userList.setValidityMessage(valid.getError());
					}
				}
			} else {
				userList.setValid(false);
				userList.setValidityMessage("Mandatory request params are missing");
			}
		} catch (Exception e) {
			userList.setValid(false);
			String message = (e instanceof DataAccessException) ? "Database error." : "Error.";
			userList.setValidityMessage(message + " Please contact server Administrator");
			userList.setError(e.getMessage());
			return userList;
		}
		return userList;
	}

	/**
	 *
	 * @param user
	 * @return
	 */
	@RequestMapping(value = "/auth/admin/cust/manage/users/edit", method = RequestMethod.POST)
	public UsersList updateUser(HttpServletRequest request, @RequestBody User user) {
		UsersList userList = new UsersList();
		Valid valid = null;
		try {
      Ticket ticket = SipCommonUtils.getTicket(request);
      if (user != null && securityService.haveValidCustomerId(ticket, user.getCustomerId())) {
				userList.setValid(true);
				if (user.getPassword() != null) {
				    Valid validity = PasswordValidation.validatePassword(user.getPassword(),user.getMasterLoginId());
					userList.setValid(validity.getValid());
					userList.setValidityMessage(validity.getValidityMessage());
				}

				if (userList.getValid()) {
					valid = userRepository.updateUser(user);
					if (valid.getValid()) {
						userList.setUsers(userRepository.getUsers(user.getCustomerId()));
						userList.setValid(true);
					} else {
						userList.setValid(false);
						userList.setValidityMessage(valid.getError());
					}
				}
			} else {
				userList.setValid(false);
				userList.setError("Mandatory request params are missing");
			}
		} catch (Exception e) {
			userList.setValid(false);
			String message = (e instanceof DataAccessException) ? "Database error." : "Error.";
			userList.setValidityMessage(message + " Please contact server Administrator");
			userList.setError(e.getMessage());
			return userList;
		}
		return userList;
	}

	/**
	 *
	 * @param deleteUser
	 * @return
	 */
	@RequestMapping(value = "/auth/admin/cust/manage/users/delete", method = RequestMethod.POST)
	public UsersList deleteUser(HttpServletRequest request, @RequestBody DeleteUser deleteUser) {
		UsersList userList = new UsersList();
		try {
			Ticket ticket = SipCommonUtils.getTicket(request);
      if (deleteUser.getUserId() != null && deleteUser.getCustomerId() != null && deleteUser.getMasterLoginId() != null
          && securityService.haveValidCustomerId(ticket, deleteUser.getCustomerId())) {
				if (userRepository.deleteUser(deleteUser.getUserId(), deleteUser.getMasterLoginId())) {
					userList.setUsers(userRepository.getUsers(deleteUser.getCustomerId()));
					userList.setValid(true);
				} else {
					userList.setValid(false);
					userList.setValidityMessage("User could not be deleted.");
				}
			} else {
				userList.setValid(false);
				userList.setError("Mandatory request params are missing");
			}
		} catch (Exception e) {
			userList.setValid(false);
			String message = (e instanceof DataAccessException) ? "Database error." : "Error.";
			userList.setValidityMessage(message + " Please contact server Administrator");
			userList.setError(e.getMessage());
			return userList;
		}
		return userList;
	}

	/**
	 *
	 * @param customerId
	 * @return
	 */
	@RequestMapping(value = "/auth/admin/cust/manage/roles/list", method = RequestMethod.POST)
	public RolesDropDownList getRoles(HttpServletRequest request, @RequestBody Long customerId) {
		RolesDropDownList roles = new RolesDropDownList();
		try {
      Ticket ticket = SipCommonUtils.getTicket(request);
      if (customerId != null && securityService.haveValidCustomerId(ticket, customerId)) {
				roles.setRoles(userRepository.getRolesDropDownList(customerId));
				roles.setValid(true);
			} else {
				roles.setValid(false);
				roles.setError("Mandatory request params are missing");
			}
		} catch (Exception e) {
			roles.setValid(false);
			String message = (e instanceof DataAccessException) ? "Database error." : "Error.";
			roles.setValidityMessage(message + " Please contact server Administrator");
			roles.setError(e.getMessage());
			return roles;
		}
		return roles;
	}

	/**
	 *
	 * @param customerId
	 * @return
	 */
	@RequestMapping(value = "/auth/admin/cust/manage/roles/fetch", method = RequestMethod.POST)
	public RolesList getRolesDetails(HttpServletRequest request, @RequestBody Long customerId) {
		RolesList roleList = new RolesList();
		try {
      Ticket ticket = SipCommonUtils.getTicket(request);
      if (customerId != null && securityService.haveValidCustomerId(ticket, customerId)) {
				roleList.setRoles(userRepository.getRoles(customerId));
				roleList.setValid(true);
			} else {
				roleList.setValid(false);
				roleList.setError("Mandatory request params are missing");
			}
		} catch (Exception e) {
			roleList.setValid(false);
			String message = (e instanceof DataAccessException) ? "Database error." : "Error.";
			roleList.setValidityMessage(message + " Please contact server Administrator");
			roleList.setError(e.getMessage());
			return roleList;
		}

		return roleList;
	}

	/**
	 *
	 * @param
	 * @return
	 */
	@RequestMapping(value = "/auth/admin/cust/manage/roles/types/list", method = RequestMethod.POST)
	public RolesDropDownList getRoles() {
		RolesDropDownList roles = new RolesDropDownList();
		try {
			roles.setRoles(userRepository.getRoletypesDropDownList());
			roles.setValid(true);
		} catch (Exception e) {
			roles.setValid(false);
			String message = (e instanceof DataAccessException) ? "Database error." : "Error.";
			roles.setValidityMessage(message + " Please contact server Administrator");
			roles.setError(e.getMessage());
			return roles;
		}
		return roles;
	}

	/**
	 *
	 * @param role
	 * @return
	 */
	@RequestMapping(value = "/auth/admin/cust/manage/roles/add", method = RequestMethod.POST)
	public RolesList addRole(HttpServletRequest request, @RequestBody RoleDetails role) {
		RolesList roleList = new RolesList();
		Valid valid = null;
		try {
      Ticket ticket = SipCommonUtils.getTicket(request);
      if (role != null && securityService.haveValidCustomerId(ticket, role.getCustSysId())) {
				valid = userRepository.addRole(role);
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
	}

	/**
	 *
	 * @param deleteRole
	 * @return
	 */
	@RequestMapping(value = "/auth/admin/cust/manage/roles/delete", method = RequestMethod.POST)
	public RolesList deleteUser(HttpServletRequest request, @RequestBody DeleteRole deleteRole) {
		RolesList roleList = new RolesList();
		roleList.setValid(true);
		try {
      Ticket ticket = SipCommonUtils.getTicket(request);
      if (deleteRole.getRoleId() != null && deleteRole.getCustomerId() != null && deleteRole.getMasterLoginId() != null
          && securityService.haveValidCustomerId(ticket, deleteRole.getCustomerId())) {
				if (userRepository.checkUserExists(deleteRole.getRoleId())) {
					roleList.setValid(false);
					roleList.setValidityMessage("Role could not be deleted as User(s) exists in this role.");
				} else if (userRepository.checkPrivExists(deleteRole.getRoleId())) {
						roleList.setValid(false);
						roleList.setValidityMessage("Role could not be deleted as Privileges(s) exists for this role");
				}

				if (roleList.getValid()) {
					if (userRepository.deleteRole(deleteRole.getRoleId(), deleteRole.getMasterLoginId())) {
						roleList.setRoles(userRepository.getRoles(deleteRole.getCustomerId()));
						roleList.setValid(true);
					}
				}
			} else {
				roleList.setValid(false);
				roleList.setError("Mandatory request params are missing");
			}
		} catch (Exception e) {
			roleList.setValid(false);
			String message = (e instanceof DataAccessException) ? "Database error." : "Error.";
			roleList.setValidityMessage(message + " Please contact server Administrator");
			roleList.setError(e.getMessage());
			return roleList;
		}
		return roleList;
	}

	/**
	 *
	 * @param role
	 * @return
	 */
	@RequestMapping(value = "/auth/admin/cust/manage/roles/edit", method = RequestMethod.POST)
	public RolesList editRole(HttpServletRequest request, @RequestBody RoleDetails role) {
		RolesList roleList = new RolesList();
		Valid valid = null;
		try {
      Ticket ticket = SipCommonUtils.getTicket(request);
      if (role != null && securityService.haveValidCustomerId(ticket, role.getCustSysId())) {
				valid = userRepository.updateRole(role);
				if (valid.getValid()) {
					roleList.setRoles(userRepository.getRoles(role.getCustSysId()));
					roleList.setValid(true);
				} else {
					roleList.setValid(false);
					roleList.setValidityMessage(valid.getError());
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
	}

	/**
	 *
	 * @param customerId
	 * @return
	 */
	@RequestMapping(value = "/auth/admin/cust/manage/privileges/fetch", method = RequestMethod.POST)
	public PrivilegesList getPrivileges(HttpServletRequest request, @RequestBody Long customerId) {
		PrivilegesList privList = new PrivilegesList();
		try {
      Ticket ticket = SipCommonUtils.getTicket(request);
      if (customerId != null && securityService.haveValidCustomerId(ticket, customerId)) {
				privList.setPrivileges(userRepository.getPrivileges(customerId));
				privList.setValid(true);
			} else {
				privList.setValid(false);
				privList.setError("Mandatory request params are missing");
			}
		} catch (Exception e) {
			privList.setValid(false);
			String message = (e instanceof DataAccessException) ? "Database error." : "Error.";
			privList.setValidityMessage(message + " Please contact server Administrator");
			privList.setError(e.getMessage());
			return privList;
		}

		return privList;
	}

	/**
	 *
	 * @param customerId
	 * @return
	 */
	@RequestMapping(value = "/auth/admin/cust/manage/products/list", method = RequestMethod.POST)
	public ProductDropDownList getProductsList(HttpServletRequest request, @RequestBody Long customerId) {
		ProductDropDownList products = new ProductDropDownList();
		try {
      Ticket ticket = SipCommonUtils.getTicket(request);
      if (customerId != null && securityService.haveValidCustomerId(ticket, customerId)) {
        products.setProducts(userRepository.getProductsDropDownList(customerId));
        products.setValid(true);
      } else {
        products.setValid(false);
        products.setError("Customer Id not matched, please correct customer id");
      }
		} catch (Exception e) {
			products.setValid(false);
			String message = (e instanceof DataAccessException) ? "Database error." : "Error.";
			products.setValidityMessage(message + " Please contact server Administrator");
			products.setError(e.getMessage());
			return products;
		}
		return products;
	}

	/**
	 *
	 * @param cpm
	 * @return
	 */
	@RequestMapping(value = "/auth/admin/cust/manage/modules/list", method = RequestMethod.POST)
	public ModuleDropDownList getModulesList(HttpServletRequest request, @RequestBody CustProdModule cpm) {
		ModuleDropDownList modules = new ModuleDropDownList();
		try {
      Ticket ticket = SipCommonUtils.getTicket(request);
      if (securityService.haveValidCustomerId(ticket, cpm.getCustomerId())) {
        modules.setModules(userRepository.getModulesDropDownList(cpm.getCustomerId(), cpm.getProductId()));
        modules.setValid(true);
      } else {
        modules.setError("Customer Id not matched, please correct customer id.");
        modules.setValid(false);
      }
		} catch (Exception e) {
			modules.setValid(false);
			String message = (e instanceof DataAccessException) ? "Database error." : "Error.";
			modules.setValidityMessage(message + " Please contact server Administrator");
			modules.setError(e.getMessage());
			return modules;
		}
		return modules;
	}

	/**
	 * Fetch category by customer product and module
	 * @param cpm
	 * @return
	 */
	@RequestMapping(value = "/auth/admin/cust/manage/categories/list", method = RequestMethod.POST)
	public CategoryList getcategoriesList(HttpServletRequest request, @RequestBody  CustProdModule cpm) {
		CategoryList categories = new CategoryList();
		try {
      Ticket ticket = SipCommonUtils.getTicket(request);
      if (securityService.haveValidCustomerId(ticket, cpm.getCustomerId())) {
        categories.setCategory(userRepository.getCategoriesDropDownList(cpm.getCustomerId(), cpm.getModuleId(), false));
        categories.setValid(true);
      } else {
        categories.setError("Customer Id not matched, please correct customer id.");
        categories.setValid(true);
      }
		} catch (Exception e) {
			categories.setValid(false);
			String message = (e instanceof DataAccessException) ? "Database error." : "Error.";
			categories.setValidityMessage(message + " Please contact server Administrator");
			categories.setError(e.getMessage());
			return categories;
		}
		return categories;
	}

	/**
	 * Fetch sub categories by customer product and module
   * @param cpsm
	 * @return
	 */
	@RequestMapping(value = "/auth/admin/cust/manage/subCategoriesWithPrivilege/list", method = RequestMethod.POST)
	public SubCategoryWithPrivilegeList getSubCategoriesList(HttpServletRequest request, @RequestBody CustomerProductSubModule cpsm) {
		SubCategoryWithPrivilegeList subcategories = new SubCategoryWithPrivilegeList();
		try {
      Ticket ticket = SipCommonUtils.getTicket(request);
      if (securityService.haveValidCustomerId(ticket, cpsm.getCustomerId())) {
        subcategories.setSubCategories(userRepository.getSubCategoriesWithPrivilege(cpsm));
        subcategories.setValid(true);
      } else {
        subcategories.setError("Customer Id not matched, please correct customer id.");
        subcategories.setValid(false);
      }
		} catch (Exception e) {
			subcategories.setValid(false);
			String message = (e instanceof DataAccessException) ? "Database error." : "Error.";
			subcategories.setValidityMessage(message + " Please contact server Administrator");
			subcategories.setError(e.getMessage());
			return subcategories;
		}
		return subcategories;
	}

	/**
	 *
	 * @param addPrivilegeDetails
	 * @return
	 */
	@RequestMapping(value = "/auth/admin/cust/manage/privileges/upsert", method = RequestMethod.POST)
	public PrivilegesList addPrivilege(HttpServletRequest request, @RequestBody AddPrivilegeDetails addPrivilegeDetails) {
		PrivilegesList privList = new PrivilegesList();
		Valid valid = null;
		try {
      Ticket ticket = SipCommonUtils.getTicket(request);
      if (addPrivilegeDetails != null && securityService.haveValidCustomerId(ticket, addPrivilegeDetails.getCustomerId())) {
				valid = userRepository.upsertPrivilege(addPrivilegeDetails);
				if (valid.getValid()) {
					privList.setPrivileges(userRepository.getPrivileges(addPrivilegeDetails.getCustomerId()));
					privList.setValid(true);
				} else {
					privList.setValid(false);
					privList.setValidityMessage("Privilege could not be added. " + valid.getError());
				}
			} else {
				privList.setValid(false);
				privList.setValidityMessage("Mandatory request params are missing");
			}
		} catch (Exception e) {
			privList.setValid(false);
			String message = (e instanceof DataAccessException) ? "Database error." : "Error.";
			privList.setValidityMessage(message + " Please contact server Administrator");
			privList.setError(e.getMessage());
			return privList;
		}
		return privList;
	}

	/**
	 *
	 * @param privilege
	 * @return
	 */
	@RequestMapping(value = "/auth/admin/cust/manage/privileges/edit", method = RequestMethod.POST)
	public PrivilegesList updatePrivilege(HttpServletRequest request, @RequestBody PrivilegeDetails privilege) {
		PrivilegesList privList = new PrivilegesList();
		Valid valid = null;
		try {
      Ticket ticket = SipCommonUtils.getTicket(request);
      if (privilege != null && securityService.haveValidCustomerId(ticket, privilege.getCustomerId())) {
				valid = userRepository.updatePrivilege(privilege);
				if (valid.getValid()) {
					privList.setPrivileges(userRepository.getPrivileges(privilege.getCustomerId()));
					privList.setValid(true);
				} else {
					privList.setValid(false);
					privList.setValidityMessage("Privilege could not be updated. " + valid.getError());
				}
			} else {
				privList.setValid(false);
				privList.setValidityMessage("Mandatory request params are missing");
			}
		} catch (Exception e) {
			privList.setValid(false);
			String message = (e instanceof DataAccessException) ? "Database error." : "Error.";
			privList.setValidityMessage(message + " Please contact server Administrator");
			privList.setError(e.getMessage());
			return privList;
		}
		return privList;
	}

	/**
	 *
	 * @param privilege
	 * @return
	 */
	@RequestMapping(value = "/auth/admin/cust/manage/privileges/delete", method = RequestMethod.POST)
	public PrivilegesList deletePrivilege(HttpServletRequest request, @RequestBody DeletePrivilege privilege) {
		PrivilegesList privList = new PrivilegesList();
		try {
      Ticket ticket = SipCommonUtils.getTicket(request);
      if (privilege != null && securityService.haveValidCustomerId(ticket, privilege.getCustomerId())) {
				if (userRepository.deletePrivilege(privilege.getPrivilegeId())) {
					privList.setPrivileges(userRepository.getPrivileges(privilege.getCustomerId()));
					privList.setValid(true);
				} else {
					privList.setValid(false);
					privList.setValidityMessage("Privilege could not be deleted. ");
				}
			} else {
				privList.setValid(false);
				privList.setValidityMessage("Mandatory request params are missing");
			}
		} catch (Exception e) {
			privList.setValid(false);
			String message = (e instanceof DataAccessException) ? "Database error." : "Error.";
			privList.setValidityMessage(message + " Please contact server Administrator");
			privList.setError(e.getMessage());
			return privList;
		}
		return privList;
	}

	/**
	 *
	 * @param customerId
	 * @return
	 */
	@RequestMapping(value = "/auth/admin/cust/manage/categories/fetch", method = RequestMethod.POST)
	public CategoryList getCategories(HttpServletRequest request, @RequestBody Long customerId) {
		CategoryList catList = new CategoryList();
		try {
      Ticket ticket = SipCommonUtils.getTicket(request);
      if (customerId != null && securityService.haveValidCustomerId(ticket, customerId)) {
				catList.setCategories(userRepository.getCategories(customerId));
				catList.setValid(true);
			} else {
				catList.setValid(false);
				catList.setError("Mandatory request params are missing");
			}
		} catch (Exception e) {
			catList.setValid(false);
			String message = (e instanceof DataAccessException) ? "Database error." : "Error.";
			catList.setValidityMessage(message + " Please contact server Administrator");
			catList.setError(e.getMessage());
			return catList;
		}

		return catList;
	}

	/**
	 *
	 * @param category
	 * @return
	 */
	@RequestMapping(value = "/auth/admin/cust/manage/categories/add", method = RequestMethod.POST)
	public CategoryList addCategories(HttpServletRequest request, @RequestBody CategoryDetails category) {
		CategoryList catList = new CategoryList();
		Valid valid = null;
		try {
      Ticket ticket = SipCommonUtils.getTicket(request);
      if (category != null && securityService.haveValidCustomerId(ticket, category.getCustomerId())) {
			 if(!userRepository.checkIsModulePresent(category.getModuleId(),ALERTS)){
				if (!userRepository.checkCatExists(category)) {
					valid = userRepository.addCategory(category);
					if (valid.getValid()) {
						catList.setCategories(userRepository.getCategories(category.getCustomerId()));
						catList.setValid(true);
					} else {
						catList.setValid(false);
						catList.setValidityMessage("Category could not be added. " + valid.getError());
					}
				} else {
					catList.setValid(false);
					catList.setValidityMessage(
							"Category Name already exists for this Customer Product Module Combination. ");
          }
        } else {
          catList.setValid(false);
          catList.setValidityMessage("Adding Categories and Sub Categories for Alert Module is not allowed. ");
        }
      } else {
				catList.setValid(false);
				catList.setValidityMessage("Mandatory request params are missing");
			}
		} catch (Exception e) {
			catList.setValid(false);
			String message = (e instanceof DataAccessException) ? "Database error." : "Error.";
			catList.setValidityMessage(message + " Please contact server Administrator");
			catList.setError(e.getMessage());
			return catList;
		}
		return catList;
	}

	/**
	 *
	 * @param cpm
	 * @return
	 */

	@RequestMapping(value = "/auth/admin/cust/manage/categories/parent/list", method = RequestMethod.POST)
	public CategoryList getcategoriesOnlyList(HttpServletRequest request, @RequestBody  CustProdModule cpm) {
		CategoryList categories = new CategoryList();
		try {
      Ticket ticket = SipCommonUtils.getTicket(request);
      if (securityService.haveValidCustomerId(ticket, cpm.getCustomerId())) {
        categories.setCategory(userRepository.getCategoriesDropDownList(cpm.getCustomerId(), cpm.getModuleId(),true));
        categories.setValid(true);
      } else {
		    categories.setError("Customer Id not matched, please correct customer id.");
        categories.setValid(false);
      }
		} catch (Exception e) {
			categories.setValid(false);
			String message = (e instanceof DataAccessException) ? "Database error." : "Error.";
			categories.setValidityMessage(message + " Please contact server Administrator");
			categories.setError(e.getMessage());
			return categories;
		}
		return categories;
	}

	/**
	 *
	 * @param category
	 * @return
	 */
	@RequestMapping(value = "/auth/admin/cust/manage/categories/delete", method = RequestMethod.POST)
	public CategoryList deleteCategories(HttpServletRequest request, @RequestBody DeleteCategory category) {
		CategoryList catList = new CategoryList();
		try {
      Ticket ticket = SipCommonUtils.getTicket(request);
      if (securityService.haveValidCustomerId(ticket, category.getCustomerId())) {
        if (userRepository.deleteCategory(category.getCategoryId())) {
          catList.setCategories(userRepository.getCategories(category.getCustomerId()));
          catList.setValid(true);
        } else {
          catList.setValid(false);
          catList.setValidityMessage("Category could not be deleted. ");
        }
      } else {
        catList.setValid(false);
        catList.setValidityMessage("Customer Id not matched, please correct customer id.");
      }
		} catch (Exception e) {
			catList.setValid(false);
			String message = (e instanceof DataAccessException) ? "Database error." : "Error.";
			catList.setValidityMessage(message + " Please contact server Administrator");
			catList.setError(e.getMessage());
			return catList;
		}
		return catList;
	}

	/**
	 *
	 * @param category
	 * @return
	 */
	@RequestMapping(value = "/auth/admin/cust/manage/subcategories/delete", method = RequestMethod.POST)
	public CategoryList deleteSubCategories(HttpServletRequest request, @RequestBody DeleteCategory category) {
		CategoryList catList = new CategoryList();
		try {
      Ticket ticket = SipCommonUtils.getTicket(request);
      if (securityService.haveValidCustomerId(ticket, category.getCustomerId())) {
        if (userRepository.deleteCategory(category.getCategoryId())) {
          catList.setSubCategories(userRepository.getSubCategories(category.getCustomerId(), category.getCategoryCode()));
          catList.setValid(true);
        } else {
          catList.setValid(false);
          catList.setValidityMessage("Category could not be deleted. ");
        }
      } else {
        catList.setValid(false);
        catList.setValidityMessage("Customer Id not matched, please correct customer id.");
      }
		} catch (Exception e) {
			catList.setValid(false);
			String message = (e instanceof DataAccessException) ? "Database error." : "Error.";
			catList.setValidityMessage(message + " Please contact server Administrator");
			catList.setError(e.getMessage());
			return catList;
		}
		return catList;
	}

	/**
	 *
	 * @param category
	 * @return
	 */
	@RequestMapping(value = "/auth/admin/cust/manage/categories/edit", method = RequestMethod.POST)
	public CategoryList updateCategories(HttpServletRequest request, @RequestBody CategoryDetails category) {
		CategoryList catList = new CategoryList();
		Valid valid = new Valid();
		try {
      Ticket ticket = SipCommonUtils.getTicket(request);
      if (securityService.haveValidCustomerId(ticket, category.getCustomerId())) {
        if (category.isIscatNameChanged() && userRepository.checkCatExists(category)) {
          catList.setValid(false);
          catList.setValidityMessage(
              "Category Name already exists for this Customer Product Module Combination. ");
        } else if (userRepository.checkSubCatExists(category)) {
          catList.setValid(false);
          catList.setValidityMessage(
              "Sub Category Name already exists for this Customer Product Module Category Combination. ");
        } else {
          valid = userRepository.updateCategory(category);
          if (valid.getValid()) {
            catList.setCategories(userRepository.getCategories(category.getCustomerId()));
            catList.setValid(true);
          } else {
            catList.setValid(false);
            catList.setValidityMessage("Category could not be edited. ");
          }
        }
      } else {
        catList.setValid(false);
        catList.setValidityMessage("Customer Id not matched, please correct customer id.");
      }
		} catch (Exception e) {
			catList.setValid(false);
			String message = (e instanceof DataAccessException) ? "Database error." : "Error.";
			catList.setValidityMessage(message + " Please contact server Administrator");
			catList.setError(e.getMessage());
			return catList;
		}
		return catList;
	}

    /**
     * @return
     */
	@RequestMapping(value= "/auth/user/preferences/upsert", method = RequestMethod.POST)
    public UserPreferences addUserPreferences(HttpServletRequest request, HttpServletResponse response,
																							@RequestBody List<Preference> preferenceList) {
	    UserPreferences userPreferences = new UserPreferences();
			try {
				Ticket ticket = SipCommonUtils.getTicket(request);
				userPreferences.setUserID(ticket.getUserId().toString());
				userPreferences.setCustomerID(ticket.getCustID());
				Preference preference = preferenceList.stream().
						filter(p -> "defaultDashboardCategory".equalsIgnoreCase(p.getPreferenceName())).findFirst().get();
				Long categoryId = preference != null && preference.getPreferenceValue() != null ?
						Long.valueOf(preference.getPreferenceValue()) : 0L;

				if (validatePrivilege(ticket.getProducts(), categoryId, Privileges.PrivilegeNames.CREATE)) {
					userPreferences.setPreferences(preferenceList);
					userPreferences = preferenceRepository.upsertPreferences(userPreferences);
				} else {
					userPreferences.setMessage(UNAUTHORIZED_USER);
					response.setStatus(HttpStatus.UNAUTHORIZED.value());
					response.sendError(HttpStatus.UNAUTHORIZED.value(),UNAUTHORIZED_USER);
				}
			}catch (Exception ex) {
				userPreferences.setMessage("Please contact server Administrator" + ex.getMessage());
				logger.error("Please contact server Administrator {}", ex);
			}
			return userPreferences;
    }

    /**
     *
     * @return
     */
    @RequestMapping(value= "/auth/user/preferences/delete", method = RequestMethod.POST)
    public UserPreferences deleteUserPreferences(HttpServletRequest request, HttpServletResponse response,
                                        @RequestBody List<Preference> preferenceList,
                                        @RequestParam(value = "inactiveAll",required=false) Boolean inactivateAll) {
        UserPreferences userPreferences = new UserPreferences();
				try {
					Ticket ticket = SipCommonUtils.getTicket(request);
					userPreferences.setUserID(ticket.getUserId().toString());
					userPreferences.setCustomerID(ticket.getCustID());
					Preference preference = preferenceList.stream().
							filter(p -> "defaultDashboardCategory".equalsIgnoreCase(p.getPreferenceName())).findFirst().get();
					Long categoryId = preference != null && preference.getPreferenceValue() != null ?
							Long.valueOf(preference.getPreferenceValue()) : 0L;

					if (validatePrivilege(ticket.getProducts(), categoryId, Privileges.PrivilegeNames.DELETE)) {
						userPreferences.setPreferences(preferenceList);
						if (inactivateAll!=null && inactivateAll) {
							return preferenceRepository.deletePreferences(userPreferences,inactivateAll);
						} else {
							return preferenceRepository.deletePreferences(userPreferences,false);
						}
					} else {
						userPreferences.setMessage(UNAUTHORIZED_USER);
						response.setStatus(HttpStatus.UNAUTHORIZED.value());
						response.sendError(HttpStatus.UNAUTHORIZED.value(),	UNAUTHORIZED_USER);
					}
				}catch (Exception ex) {
					userPreferences.setMessage("Please contact server Administrator" + ex.getMessage());
					logger.error("Please contact server Administrator {}", ex);
				}
			return userPreferences;
    }

    /**
     *
     * @return
     */
    @RequestMapping(value= "/auth/user/preferences/fetch", method = RequestMethod.GET)
    public Object fetchUserPreferences(HttpServletRequest request, HttpServletResponse response) {
        String jwtToken = JWTUtils.getToken(request);
        String [] extractValuesFromToken = JWTUtils.parseToken(jwtToken,nSSOProperties.getJwtSecretKey());
        return preferenceRepository.fetchPreferences(extractValuesFromToken[0],extractValuesFromToken[1]);
    }

  private void setUnauthorized(HttpServletResponse response) {
    try {
      response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
      response.sendError(HttpServletResponse.SC_UNAUTHORIZED, UNAUTHORIZED_USER);
    } catch (IOException ex) {
      logger.error("Error while validating user.");
    }
  }

}
