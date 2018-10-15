/**
 * 
 */
package com.sncr.saw.security.app.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.gson.Gson;
import com.sncr.saw.security.app.properties.NSSOProperties;
import com.sncr.saw.security.app.repository.DataSecurityKeyRepository;
import com.sncr.saw.security.app.repository.PreferenceRepository;
import com.sncr.saw.security.app.repository.UserRepository;
import com.sncr.saw.security.app.sso.SSORequestHandler;
import com.sncr.saw.security.app.sso.SSOResponse;
import com.sncr.saw.security.common.bean.*;
import com.sncr.saw.security.common.bean.repo.admin.*;
import com.sncr.saw.security.common.bean.repo.admin.category.CategoryDetails;
import com.sncr.saw.security.common.bean.repo.admin.privilege.AddPrivilegeDetails;
import com.sncr.saw.security.common.bean.repo.admin.privilege.PrivilegeDetails;
import com.sncr.saw.security.common.bean.repo.admin.role.RoleDetails;
import com.sncr.saw.security.common.bean.repo.analysis.AnalysisSummary;
import com.sncr.saw.security.common.bean.repo.analysis.AnalysisSummaryList;
import com.sncr.saw.security.common.bean.repo.dsk.AttributeValues;
import com.sncr.saw.security.common.bean.repo.dsk.SecurityGroups;
import com.sncr.saw.security.common.bean.repo.dsk.UserAssignment;
import com.sncr.saw.security.common.util.JWTUtils;
import com.sncr.saw.security.common.util.TicketHelper;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.web.bind.annotation.*;

import javax.mail.Message;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMultipart;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.security.SecureRandom;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author gsan0003
 *
 */
@RestController
public class SecurityController {
	private static final Logger logger = LoggerFactory.getLogger(SecurityController.class);

	@Autowired
	private UserRepository userRepository;
	@Autowired
	private NSSOProperties nSSOProperties;

	@Autowired
	SSORequestHandler ssoRequestHandler;

	@Autowired
    PreferenceRepository preferenceRepository;

	@Autowired
    DataSecurityKeyRepository dataSecurityKeyRepository;

	private final ObjectMapper mapper = new ObjectMapper();

	@RequestMapping(value = "/doAuthenticate", method = RequestMethod.POST)
	public LoginResponse doAuthenticate(@RequestBody LoginDetails loginDetails) {

		logger.info("Ticket will be created..");
		logger.info("Token Expiry :" + nSSOProperties.getValidityMins());

		Ticket ticket = new Ticket();
		User user = null;
		TicketHelper tHelper = new TicketHelper(userRepository);
		ticket.setMasterLoginId(loginDetails.getMasterLoginId());
		ticket.setValid(false);
		RefreshToken rToken = null;
		try {
			boolean[] ret = userRepository.authenticateUser(loginDetails.getMasterLoginId(),
					loginDetails.getPassword());

			boolean isUserAuthentic = ret[0];
			boolean isPassWordActive = ret[1];
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
					.setIssuedAt(new Date()).signWith(SignatureAlgorithm.HS256, "sncrsaw2").compact());
		} catch (Exception e) {
			logger.error("Exception occured creating ticket ", e, null);
			return null;
		}

		return new LoginResponse(Jwts.builder().setSubject(loginDetails.getMasterLoginId()).claim("ticket", ticket)
				.setIssuedAt(new Date()).signWith(SignatureAlgorithm.HS256, "sncrsaw2").compact(),Jwts.builder().setSubject(loginDetails.getMasterLoginId()).claim("ticket", rToken)
				.setIssuedAt(new Date()).signWith(SignatureAlgorithm.HS256, "sncrsaw2").compact());
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
	public LoginResponse accessToken(@RequestBody String rToken) throws ServletException {

		Claims refreshToken = Jwts.parser().setSigningKey("sncrsaw2").parseClaimsJws(rToken).getBody();
		// Check if the refresh Token is valid
		Iterator<?> it = ((Map<String, Object>) refreshToken.get("ticket")).entrySet().iterator();
		Boolean validity = false;
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
			TicketHelper tHelper = new TicketHelper(userRepository);
			ticket = new Ticket();
			ticket.setMasterLoginId(masterLoginId);
			ticket.setValid(false);
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
				ticket.setError(de.getMessage());
				return new LoginResponse(Jwts.builder().setSubject(masterLoginId).claim("ticket", ticket)
						.setIssuedAt(new Date()).signWith(SignatureAlgorithm.HS256, "sncrsaw2").compact());
			} catch (Exception e) {
				logger.error("Exception occured creating ticket ", e, null);
				return null;
			}

			return new LoginResponse(
					Jwts.builder().setSubject(masterLoginId).claim("ticket", ticket).setIssuedAt(new Date())
							.signWith(SignatureAlgorithm.HS256, "sncrsaw2").compact(),
					Jwts.builder().setSubject(masterLoginId).claim("ticket", newRToken).setIssuedAt(new Date())
							.signWith(SignatureAlgorithm.HS256, "sncrsaw2").compact());
		}
	}

	@RequestMapping(value = "/getDefaults", method = RequestMethod.POST)
	public LoginResponse getDefaults(@RequestBody LoginDetails loginDetails) {

		logger.info("Ticket will be created..");
		logger.info("Token Expiry :" + nSSOProperties.getValidityMins());

		Ticket ticket = null;
		User user = null;
		TicketHelper tHelper = new TicketHelper(userRepository);
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
					.setIssuedAt(new Date()).signWith(SignatureAlgorithm.HS256, "sncrsaw2").compact());
		} catch (Exception e) {
			logger.error("Exception occured creating ticket ", e, null);
			return null;
		}

		return new LoginResponse(Jwts.builder().setSubject(loginDetails.getMasterLoginId()).claim("ticket", ticket)
				.setIssuedAt(new Date()).signWith(SignatureAlgorithm.HS256, "sncrsaw2").compact(),Jwts.builder().setSubject(loginDetails.getMasterLoginId()).claim("rToken", rToken)
				.setIssuedAt(new Date()).signWith(SignatureAlgorithm.HS256, "sncrsaw2").compact());
	}

	/**
	 * 
	 * @param ticketID
	 * @return
	 */
	@RequestMapping(value = "/auth/doLogout", method = RequestMethod.POST)
	public String doLogout(@RequestBody String ticketID) {
		TicketHelper tHelper = new TicketHelper(userRepository);
		Gson gson = new Gson();
		try {
			return gson.toJson(tHelper.logout(ticketID));
		} catch (DataAccessException de) {
			return de.getMessage();
		}
	}

	/**
	 * 
	 * @param changePasswordDetails
	 * @return
	 */
	@RequestMapping(value = "/auth/changePassword", method = RequestMethod.POST)
	public Valid changePassword(@RequestBody ChangePasswordDetails changePasswordDetails) {
		Valid valid = new Valid();
		valid.setValid(false);
		String oldPass = changePasswordDetails.getOldPassword();
		String newPass = changePasswordDetails.getNewPassword();
		String cnfNewPass = changePasswordDetails.getCnfNewPassword();
		String loginId = changePasswordDetails.getMasterLoginId();
		String message = null;
		if (!cnfNewPass.equals(newPass)) {
			message = "'New Password' and 'Verify password' does not match.";
		} else if (newPass.length() < 8) {
			message = "New password should be minimum of 8 character.";
		} else if (oldPass.equals(newPass)) {
			message = "Old password and new password should not be same.";
		} else if (loginId.equals(newPass)) {
			message = "User Name can't be assigned as password.";
		}
		Pattern pCaps = Pattern.compile("[A-Z]");
		Matcher m = pCaps.matcher(newPass);
		if (!m.find()) {
			message = "Password should contain atleast 1 uppercase character.";
		}
		Pattern pSpeChar = Pattern.compile("[~!@#$%^&*?<>]");
		m = pSpeChar.matcher(newPass);
		if (!m.find()) {
			message = "Password should contain atleast 1 special character.";
		}
		if (message == null) {
			try {
				message = userRepository.changePassword(loginId, newPass, oldPass);
				if (message != null && message.equals("Password Successfully Changed.")) {
					valid.setValid(true);
				}

			} catch (DataAccessException de) {
				valid.setValidityMessage("Database error. Please contact server Administrator.");
				valid.setError(de.getMessage());
				return valid;
			}
		}
		valid.setValidityMessage(message);
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
			if (eMail.equals("Invalid")) {
				logger.error("Invalid user Id, Unable to perform Reset Password Process. error message:", null, null);
				message = "Invalid user Id";
			} else if (eMail.equals("no email")) {
				logger.error("Email Id is not configured for the User", null, null);
				message = "Email Id is not configured for the User";
			} else {
				Long createdTime = System.currentTimeMillis();
				String randomString = randomString(160);
				userRepository.insertResetPasswordDtls(resetPwdDtls.getMasterLoginId(), randomString, createdTime,
						createdTime + (24 * 60 * 60 * 1000));
				String resetpwdlk = resetPwdDtls.getProductUrl();
				// resetpwdlk = resetpwdlk+"/vfyRstPwd?rhc="+randomString;
				resetpwdlk = resetpwdlk + "?rhc=" + randomString;
				message = sendResetMail(resetPwdDtls.getMasterLoginId(), eMail, resetpwdlk, createdTime);
				if (message == null) {
					valid.setValid(true);
					message = "Mail sent successfully to " + eMail;// + ".
																	// Requesting
																	// user is "
																	// +
																	// resetPwdDtls.getMasterLoginId();
				}
			}
		} catch (DataAccessException de) {
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
		ResetValid rv = null;
		try {
			rv = userRepository.validateResetPasswordDtls(randomHashcode.getRhc());
		} catch (DataAccessException de) {
			rv.setValid(false);
			rv.setValidityReason("Database error. Please contact server Administrator.");
			rv.setError(de.getMessage());
			return rv;
		}
		return rv;
	}

	private JavaMailSender javaMailSender() {
		System.setProperty("java.net.preferIPv4Stack", "true");
		JavaMailSenderImpl mailSender = new JavaMailSenderImpl();
		Properties mailProperties = new Properties();
		// mailProperties.put("mail.smtp.auth", auth);
		// mailProperties.put("mail.smtp.starttls.enable", starttls);
		mailSender.setJavaMailProperties(mailProperties);
		mailSender.setHost(nSSOProperties.getMailHost());
		mailSender.setPort(nSSOProperties.getMailPort());
		// mailSender.setProtocol(protocol);

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
			message = "'New Password' and 'Verify password' does not match.";
		} else if (newPass.length() < 8) {
			message = "New password should be minimum of 8 character.";
		} else if (loginId.equals(newPass)) {
			message = "User Name can't be assigned as password.";
		}

		Pattern pCaps = Pattern.compile("[A-Z]");
		Matcher m = pCaps.matcher(newPass);
		if (!m.find()) {
			message = "Password should contain atleast 1 uppercase character.";
		}

		Pattern pSpeChar = Pattern.compile("[~!@#$%^&*?<>]");
		m = pSpeChar.matcher(newPass);
		if (!m.find()) {
			message = "Password should contain atleast 1 special character.";
		}
        // Validate the hash key with userID before proceeding.
        ResetValid resetValid =  userRepository.validateResetPasswordDtls(rhc);
        if (!(resetValid.getValid() || (resetValid.getMasterLoginID()!=null &&
            resetValid.getMasterLoginID().equalsIgnoreCase(loginId))))
            message= "Reset link is not valid or expired ";

		try {
			if (message == null) {
				message = userRepository.rstchangePassword(loginId, newPass,rhc);
				if (message == null) {
					message = "Password Successfully Changed.";
					valid.setValid(true);
				}
			}
		} catch (DataAccessException de) {
			valid.setValidityMessage("Database error. Please contact server Administrator.");
			valid.setError(de.getMessage());
			return valid;
		}
		valid.setValidityMessage(message);
		return valid;
	}

	/**
	 * 
	 * @param ticketId
	 * @return
	 */
	@RequestMapping(value = "/auth/reCreateTicket", method = RequestMethod.POST)
	public String reCreateTicket(@RequestBody String ticketId) {
		TicketHelper tHelper = new TicketHelper(userRepository);
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
	 * 
	 * @param analysis
	 * @return
	 */
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
	 * 
	 * @param analysis
	 * @return
	 */
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
	 * 
	 * @param analysis
	 * @return
	 */
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
	 * 
	 * @param featureId
	 * @return
	 */
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
	public UsersList getUsers(@RequestBody Long customerId) {
		UsersList userList = new UsersList();
		try {
			if (customerId != null) {
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

    @RequestMapping(value = "/getSecurityGroups",method = RequestMethod.GET)
    public List<SecurityGroups> getSecurityGroups() {
        List<SecurityGroups> groupNames = dataSecurityKeyRepository.fetchSecurityGroupNames();
	    return groupNames;
    }

    @RequestMapping(value = "/addSecurityGroups",method = RequestMethod.POST)
    public Valid addSecurityGroups(@RequestBody SecurityGroups securityGroups)  {
	    Valid valid = new Valid();
	    if (dataSecurityKeyRepository.addSecurityGroups(securityGroups))    {
	        valid.setValid(true);
	        valid.setValidityMessage("Security Group successfully created");
        }
        else
        {
            valid.setValid(false);
            valid.setValidityMessage("Error in creating security group");
        }
	    return valid;
    }

    @RequestMapping(value = "/updateSecurityGroups",method = RequestMethod.PUT)
    public Valid updateSecurityGroups(@RequestBody List<SecurityGroups> securityGroups) {
        Valid valid = new Valid();
        if(dataSecurityKeyRepository.updateSecurityGroups(securityGroups))  {
            valid.setValid(true);
            valid.setValidityMessage("Security Group Updated sucessfully");
        }
        else {
            valid.setValid(false);
            valid.setValidityMessage("Error in Updating Group Name");
        }
	    return valid;
    }

    @RequestMapping(value = "/deleteSecurityGroups",method = RequestMethod.DELETE)
    public Valid deleteSecurityGroups(@RequestBody String securityGroupName)  {
	    Valid valid = new Valid();
	    if (dataSecurityKeyRepository.deleteSecurityGroups(securityGroupName))  {
	        valid.setValid(true);
	        valid.setValidityMessage("Successfully deleted security group");
        }
        else {
            valid.setValid(false);
            valid.setValidityMessage("Error in deleting security group");
        }
	    return valid;
    }

    @RequestMapping (value = "/addSecurityGroupDskAttributeValues", method = RequestMethod.POST)
    public Valid addSecurityGroupDskAttributeValues(@RequestBody AttributeValues attributeValues)  {
	    Valid valid = new Valid();
	    if(dataSecurityKeyRepository.addSecurityGroupDskAttributeValues(attributeValues))   {
	        valid.setValid(true);
	        valid.setValidityMessage("Adding Attribute-Values is succesfull");
        }
        else {
	        valid.setValid(false);
	        valid.setValidityMessage("Error in Adding Attribute-Values");
        }
	    return valid;
    }

    @RequestMapping ( value = "/updateAttributeValues", method =  RequestMethod.PUT)
    public Valid updateAttributeValues(@RequestBody AttributeValues attributeValues)    {
	    Valid valid = new Valid();
	    if (dataSecurityKeyRepository.updateAttributeValues(attributeValues))   {
	        valid.setValid(true);
	        valid.setValidityMessage("DSK value Updated sucessfully");
        }
        else {
	        valid.setValid(false);
	        valid.setValidityMessage("Error in updating DSK value");
        }
        return valid;
    }

    @RequestMapping (value = "/getSecurityGroupDskAttributes", method = RequestMethod.POST)
    public List<String> fetchSecurityGroupDskAttributes(@RequestBody String securityGroupName)   {
	    List<String> attributeNames = dataSecurityKeyRepository.fetchSecurityGroupDskAttributes(securityGroupName);
	    return attributeNames;
    }

    @RequestMapping (value = "/deleteSecurityGroupDskAttributeValues", method = RequestMethod.DELETE)
    public Valid deleteSecurityGroupDskAttributeValues(@RequestBody List<String> dskList)   {
        Valid valid = new Valid();
        if (dataSecurityKeyRepository.deleteSecurityGroupDskAttributeValues(dskList))   {
            valid.setValid(true);
            valid.setValidityMessage("Deleted Attribute values");
        }
        else {
            valid.setValid(false);
            valid.setValidityMessage("Error in deleting Attribute Values");
        }
        return valid;
    }

    @RequestMapping ( value = "/updateUser", method = RequestMethod.PUT)
    public Valid updateUser(@RequestBody List<String> groupNameUserId)  {
	    Valid valid = new Valid();
	    if (dataSecurityKeyRepository.updateUser(groupNameUserId.get(0),groupNameUserId.get(1)))    {
	        valid.setValid(true);
	        valid.setValidityMessage("Group Name succesfully updated");
        }
        else {
	        valid.setValid(false);
	        valid.setValidityMessage("Error in updating Group Name");
        }
	    return valid;
    }

    @RequestMapping ( value = "/fetchDskAllAttributeValues", method = RequestMethod.POST)
    public List<AttributeValues> fetchDskAllAttributeValues(@RequestBody String securityGroupName)    {
        List<AttributeValues> res = dataSecurityKeyRepository.fetchDskAllAttributeValues(securityGroupName);
        return  res;
    }

    @RequestMapping ( value = "/getAlluserAssignments", method = RequestMethod.GET)
    public List<UserAssignment> getAlluserAssignments()  {
	    return dataSecurityKeyRepository.getAlluserAssignments();
    }



    /**
	 * 
	 * @param user
	 * @return
	 */
	@RequestMapping(value = "/auth/admin/cust/manage/users/add", method = RequestMethod.POST)
	public UsersList addUser(@RequestBody User user) {
		UsersList userList = new UsersList();
		Valid valid = null;
		try {
			if (user != null) {
				userList.setValid(true);
				if (user.getPassword() != null) {
					if (user.getPassword().length() < 8) {
						userList.setValidityMessage("New password should be minimum of 8 character.");
						userList.setValid(false);
					} else if (user.getMasterLoginId().equals(user.getPassword())) {
						userList.setValidityMessage("User Name can't be assigned as password.");
						userList.setValid(false);
					}
					Pattern pCaps = Pattern.compile("[A-Z]");
					Matcher m = pCaps.matcher(user.getPassword());
					if (!m.find()) {
						userList.setValidityMessage("Password should contain atleast 1 uppercase character.");
						userList.setValid(false);
					}
					Pattern pSpeChar = Pattern.compile("[~!@#$%^&*?<>]");
					m = pSpeChar.matcher(user.getPassword());
					if (!m.find()) {
						userList.setValidityMessage("Password should contain atleast 1 special character.");
						userList.setValid(false);
					}
				} else {
					userList.setValid(false);
				}
				if (userList.getValid()) {
					valid = userRepository.addUser(user);
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
	public UsersList updateUser(@RequestBody User user) {
		UsersList userList = new UsersList();
		Valid valid = null;
		try {
			if (user != null) {
				userList.setValid(true);
				if (user.getPassword() != null) {
					if (user.getPassword().length() < 8) {
						userList.setValidityMessage("New password should be minimum of 8 character.");
						userList.setValid(false);
					} else if (user.getMasterLoginId().equals(user.getPassword())) {
						userList.setValidityMessage("User Name can't be assigned as password.");
						userList.setValid(false);
					}
					Pattern pCaps = Pattern.compile("[A-Z]");
					Matcher m = pCaps.matcher(user.getPassword());
					if (!m.find()) {
						userList.setValidityMessage("Password should contain atleast 1 uppercase character.");
						userList.setValid(false);
					}
					Pattern pSpeChar = Pattern.compile("[~!@#$%^&*?<>]");
					m = pSpeChar.matcher(user.getPassword());
					if (!m.find()) {
						userList.setValidityMessage("Password should contain atleast 1 special character.");
						userList.setValid(false);
					}
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
	public UsersList deleteUser(@RequestBody DeleteUser deleteUser) {
		UsersList userList = new UsersList();
		try {
			if (deleteUser.getUserId() != null && deleteUser.getCustomerId() != null
					&& deleteUser.getMasterLoginId() != null) {
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
	public RolesDropDownList getRoles(@RequestBody Long customerId) {
		RolesDropDownList roles = new RolesDropDownList();
		try {
			if (customerId != null) {
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
	public RolesList getRolesDetails(@RequestBody Long customerId) {
		RolesList roleList = new RolesList();
		try {
			if (customerId != null) {
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
	public RolesList addRole(@RequestBody RoleDetails role) {
		RolesList roleList = new RolesList();
		Valid valid = null;
		try {
			if (role != null) {
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
	public RolesList deleteUser(@RequestBody DeleteRole deleteRole) {
		RolesList roleList = new RolesList();
		roleList.setValid(true);
		try {
			if (deleteRole.getRoleId() != null && deleteRole.getCustomerId() != null
					&& deleteRole.getMasterLoginId() != null) {
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
	public RolesList editRole(@RequestBody RoleDetails role) {
		RolesList roleList = new RolesList();
		Valid valid = null;
		try {
			if (role != null) {
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
	public PrivilegesList getPrivileges(@RequestBody Long customerId) {
		PrivilegesList privList = new PrivilegesList();
		try {
			if (customerId != null) {
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
	public ProductDropDownList getProductsList(@RequestBody Long customerId) {
		ProductDropDownList products = new ProductDropDownList();
		try {
			products.setProducts(userRepository.getProductsDropDownList(customerId));
			products.setValid(true);
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
	public ModuleDropDownList getModulesList(@RequestBody CustProdModule cpm) {
		ModuleDropDownList modules = new ModuleDropDownList();
		try {
			modules.setModules(userRepository.getModulesDropDownList(cpm.getCustomerId(), cpm.getProductId()));
			modules.setValid(true);
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
	 * 
	 * @param cpm
	 * @return
	 */
	
	@RequestMapping(value = "/auth/admin/cust/manage/categories/list", method = RequestMethod.POST)
	public CategoryList getcategoriesList(@RequestBody  CustProdModule cpm) {
		CategoryList categories = new CategoryList();
		try {
			categories.setCategory(userRepository.getCategoriesDropDownList(cpm.getCustomerId(), cpm.getModuleId(), false));
			categories.setValid(true);
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
	 * @param cpsm
	 * @return
	 */
	
	@RequestMapping(value = "/auth/admin/cust/manage/subCategoriesWithPrivilege/list", method = RequestMethod.POST)
	public SubCategoryWithPrivilegeList getSubCategoriesList(@RequestBody CustomerProductSubModule cpsm) {
		SubCategoryWithPrivilegeList subcategories = new SubCategoryWithPrivilegeList();
		try {
			subcategories.setSubCategories(userRepository.getSubCategoriesWithPrivilege(cpsm));
			subcategories.setValid(true);
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
	public PrivilegesList addPrivilege(@RequestBody AddPrivilegeDetails addPrivilegeDetails) {
		PrivilegesList privList = new PrivilegesList();
		Valid valid = null;
		try {
			if (addPrivilegeDetails != null) {
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
	public PrivilegesList updatePrivilege(@RequestBody PrivilegeDetails privilege) {
		PrivilegesList privList = new PrivilegesList();
		Valid valid = null;
		try {
			if (privilege != null) {
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
	public PrivilegesList deletePrivilege(@RequestBody DeletePrivilege privilege) {
		PrivilegesList privList = new PrivilegesList();
		try {
			if (privilege != null) {
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
	public CategoryList getCategories(@RequestBody Long customerId) {
		CategoryList catList = new CategoryList();
		try {
			if (customerId != null) {
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
	public CategoryList addCategories(@RequestBody CategoryDetails category) {
		CategoryList catList = new CategoryList();
		Valid valid = null;
		try {
			if (category != null) {
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
	public CategoryList getcategoriesOnlyList(@RequestBody  CustProdModule cpm) {
		CategoryList categories = new CategoryList();
		try {
			categories.setCategory(userRepository.getCategoriesDropDownList(cpm.getCustomerId(), cpm.getModuleId(),true));
			categories.setValid(true);
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
	public CategoryList deleteCategories(@RequestBody DeleteCategory category) {
		CategoryList catList = new CategoryList();
		try {

			if (userRepository.deleteCategory(category.getCategoryId())) {
				catList.setCategories(userRepository.getCategories(category.getCustomerId()));
				catList.setValid(true);
			} else {
				catList.setValid(false);
				catList.setValidityMessage("Category could not be deleted. ");
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
	public CategoryList deleteSubCategories(@RequestBody DeleteCategory category) {
		CategoryList catList = new CategoryList();
		try {
			if (userRepository.deleteCategory(category.getCategoryId())) {
				catList.setSubCategories(userRepository.getSubCategories(category.getCustomerId(), category.getCategoryCode()));
				catList.setValid(true);
			} else {
				catList.setValid(false);
				catList.setValidityMessage("Category could not be deleted. ");
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
	public CategoryList updateCategories(@RequestBody CategoryDetails category) {
		CategoryList catList = new CategoryList();
		Valid valid = new Valid();
		try {
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
	 * Return response indicating the status of the health of the
	 * service.  Used by load balancer to route requests to healthy
	 * instances of the service to provide high availability.
	 */
	@RequestMapping("/actuator/health")
	public ResponseEntity<ObjectNode> health() {
		String status = "UP";
		HttpStatus httpStatus = HttpStatus.OK;
		try {
			/* Execute sample read-only query to determine if database
			 * is reachable */
			userRepository.getRoletypesDropDownList();
		} catch (Exception e) {
			logger.error("Database health check failed", e);
			status = "DOWN";
			httpStatus = HttpStatus.SERVICE_UNAVAILABLE;
		}
		ObjectNode root = mapper.createObjectNode();
		root.put("status", status);
		return new ResponseEntity<>(root, httpStatus);
	}

    /**
     *
     * @return
     */
	@RequestMapping(value= "/auth/admin/user/preferences/upsert", method = RequestMethod.POST)
    public Object addUserPreferences(HttpServletRequest request, HttpServletResponse response, @RequestBody List<Preference> preferenceList) {
	    UserPreferences userPreferences = new UserPreferences();
	    String jwtToken = JWTUtils.getToken(request);
	    String [] extractValuesFromToken = JWTUtils.parseToken(jwtToken);
        userPreferences.setUserID(extractValuesFromToken[0]);
        userPreferences.setCustomerID(extractValuesFromToken[1]);
        userPreferences.setPreferences(preferenceList);
        return preferenceRepository.upsertPreferences(userPreferences);
    }

    /**
     *
     * @return
     */
    @RequestMapping(value= "/auth/admin/user/preferences/delete", method = RequestMethod.POST)
    public Object deleteUserPreferences(HttpServletRequest request, HttpServletResponse response,
                                        @RequestBody List<Preference> preferenceList,
                                        @RequestParam(value = "inactiveAll",required=false) Boolean inactivateAll) {
        UserPreferences userPreferences = new UserPreferences();
        String jwtToken = JWTUtils.getToken(request);
        String [] extractValuesFromToken = JWTUtils.parseToken(jwtToken);
        userPreferences.setUserID(extractValuesFromToken[0]);
        userPreferences.setCustomerID(extractValuesFromToken[1]);
        userPreferences.setPreferences(preferenceList);
        if (inactivateAll!=null && inactivateAll)
            return preferenceRepository.deletePreferences(userPreferences,inactivateAll);
        else
            return preferenceRepository.deletePreferences(userPreferences,false);
    }

    /**
     *
     * @return
     */
    @RequestMapping(value= "/auth/admin/user/preferences/fetch", method = RequestMethod.GET)
    public Object fetchUserPreferences(HttpServletRequest request, HttpServletResponse response) {
        String jwtToken = JWTUtils.getToken(request);
        String [] extractValuesFromToken = JWTUtils.parseToken(jwtToken);
        return preferenceRepository.fetchPreferences(extractValuesFromToken[0],extractValuesFromToken[1]);
    }
}
