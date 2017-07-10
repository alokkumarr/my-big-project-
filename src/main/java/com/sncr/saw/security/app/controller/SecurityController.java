/**
 * 
 */
package com.sncr.saw.security.app.controller;

import java.security.SecureRandom;
import java.util.Date;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.mail.Message;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMultipart;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.google.gson.Gson;
import com.sncr.saw.security.app.properties.NSSOProperties;
import com.sncr.saw.security.app.repository.UserRepository;
import com.sncr.saw.security.common.bean.ChangePasswordDetails;
import com.sncr.saw.security.common.bean.CustProdModule;
import com.sncr.saw.security.common.bean.LoginDetails;
import com.sncr.saw.security.common.bean.RandomHashcode;
import com.sncr.saw.security.common.bean.ResetPwdDtls;
import com.sncr.saw.security.common.bean.ResetValid;
import com.sncr.saw.security.common.bean.Ticket;
import com.sncr.saw.security.common.bean.User;
import com.sncr.saw.security.common.bean.Valid;
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
import com.sncr.saw.security.common.bean.repo.admin.UsersList;
import com.sncr.saw.security.common.bean.repo.admin.category.CategoryDetails;
import com.sncr.saw.security.common.bean.repo.admin.privilege.PrivilegeDetails;
import com.sncr.saw.security.common.bean.repo.admin.role.RoleDetails;
import com.sncr.saw.security.common.bean.repo.analysis.Analysis;
import com.sncr.saw.security.common.bean.repo.analysis.AnalysisSummaryList;
import com.sncr.saw.security.common.util.TicketHelper;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

/**
 * @author gsan0003
 *
 */
@RestController
public class SecurityController {
	private static final Logger logger = LoggerFactory.getLogger(SecurityController.class);

	@Autowired
	public UserRepository userRepository;
	@Autowired
	NSSOProperties nSSOProperties;

	@RequestMapping(value = "/doAuthenticate", method = RequestMethod.POST)
	public LoginResponse doAuthenticate(@RequestBody LoginDetails loginDetails) {

		logger.info("Ticket will be created..");
		logger.info("Token Expiry :" + nSSOProperties.getValidityMins());

		Ticket ticket = null;
		User user = null;
		TicketHelper tHelper = new TicketHelper(userRepository);
		ticket = new Ticket();
		ticket.setMasterLoginId(loginDetails.getMasterLoginId());
		ticket.setValid(false);
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
							? Long.parseLong(nSSOProperties.getValidityMins()) : 720));
					ticket = tHelper.createTicket(user, false);
				} else {
					ticket.setValidityReason("Password Expired");
				}
			} else {
				ticket.setValidityReason("Invalid User Credentials");
			}
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
				.setIssuedAt(new Date()).signWith(SignatureAlgorithm.HS256, "sncrsaw2").compact());
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
		try {
			user = new User();
			user.setMasterLoginId(loginDetails.getMasterLoginId());
			user.setValidMins((nSSOProperties.getValidityMins() != null
					? Long.parseLong(nSSOProperties.getValidityMins()) : 720));
			ticket = tHelper.createDefaultTicket(user, true);

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
			message = "New password should be minimum of 8 charactar.";
		} else if (oldPass.equals(newPass)) {
			message = "Old password and new password should not be same.";
		} else if (loginId.equals(newPass)) {
			message = "User Name can't be assigned as password.";
		}
		Pattern pCaps = Pattern.compile("[A-Z]");
		Matcher m = pCaps.matcher(newPass);
		if (!m.find()) {
			message = "Password should contain atleast 1 uppercase charactar.";
		}
		Pattern pSpeChar = Pattern.compile("[~!@#$%^&*?<>]");
		m = pSpeChar.matcher(newPass);
		if (!m.find()) {
			message = "Password should contain atleast 1 special charactar.";
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
		try {
			if (message == null) {
				message = userRepository.rstchangePassword(loginId, newPass);
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
		public String token;

		public LoginResponse(final String token) {
			this.token = token;
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
	 * @param validateToken
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
	public Valid createAnalysis(@RequestBody Analysis analysis) {
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
	public Valid updateAnalysis(@RequestBody Analysis analysis) {
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
	public Valid deleteAnalysis(@RequestBody Analysis analysis) {
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
	 * @param user
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
	 * @param user
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
	 * @param user
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
	 * @param user
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
	 * @param user
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
						roleList.setValidityMessage("Role could not be deleted as Privileges(s) exists for this role.");				
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
	 * @param user
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
	 * @param privilege
	 * @return
	 */
	@RequestMapping(value = "/auth/admin/cust/manage/privileges/add", method = RequestMethod.POST)
	public PrivilegesList addPrivilege(@RequestBody PrivilegeDetails privilege) {
		PrivilegesList privList = new PrivilegesList();
		Valid valid = null;
		try {
			if (privilege != null) {
				valid = userRepository.addPrivilege(privilege);
				if (valid.getValid()) {
					privList.setPrivileges(userRepository.getPrivileges(privilege.getCustomerId()));
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
	 * @param customerId
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
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		SecurityController sc = new SecurityController();
		System.out.println(sc.randomString(160));
	}
}
