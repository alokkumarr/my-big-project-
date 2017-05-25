/**
 * 
 */
package com.sncr.nsso.app.controller;

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
import com.sncr.nsso.app.properties.NSSOProperties;
import com.sncr.nsso.app.repository.UserRepository;
import com.sncr.nsso.common.bean.Analysis;
import com.sncr.nsso.common.bean.AnalysisPrivilegeSummary;
import com.sncr.nsso.common.bean.AnalysisSummary;
import com.sncr.nsso.common.bean.AnalysisSummaryList;
import com.sncr.nsso.common.bean.ChangePasswordDetails;
import com.sncr.nsso.common.bean.LoginDetails;
import com.sncr.nsso.common.bean.RandomHashcode;
import com.sncr.nsso.common.bean.ResetPwdDtls;
import com.sncr.nsso.common.bean.ResetValid;
import com.sncr.nsso.common.bean.Ticket;
import com.sncr.nsso.common.bean.User;
import com.sncr.nsso.common.bean.Valid;
import com.sncr.nsso.common.util.TicketHelper;

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
		logger.info("Token Expiry :" +nSSOProperties.getValidityMins());
	
		Ticket ticket = null;
		User user = null;
		TicketHelper tHelper = new TicketHelper(userRepository);
		ticket = new Ticket();
		ticket.setMasterLoginId(loginDetails.getMasterLoginId());
		ticket.setValid(false);
		try {			
			boolean[] ret = userRepository.authenticateUser(loginDetails.getMasterLoginId(), loginDetails.getPassword());
			
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
		logger.info("Token Expiry :" +nSSOProperties.getValidityMins());
	
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
		if(nSSOProperties.getMailPassword().length != 0) {
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
			message = "New password should be minimum of 8 charactar.";
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
		}
		return valid;
	}
	/**
	 * 
	 * @param analysis
	 * @return
	 */
	@RequestMapping(value = "/auth/analysis/updateAnalysis", method = RequestMethod.POST)
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
		}
		return valid;
	}
	/**
	 * 
	 * @param analysis
	 * @return
	 */
	@RequestMapping(value = "/auth/analysis/deleteAnalysis", method = RequestMethod.POST)
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
		}
		return valid;
	}
	/**
	 * 
	 * @param featureId
	 * @return
	 */
	@RequestMapping(value = "/auth/analysis/getAnalysisByFeatureId/{featureId}", method = RequestMethod.GET)
	public AnalysisSummaryList getAnalysisByFeatureID(@PathVariable("featureId")Long featureId) {				
		return userRepository.getAnalysisByFeatureID(featureId);		          
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
