/**
 * 
 */
package com.sncr.nsso.app.controller;

import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Date;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.google.gson.Gson;
import com.sncr.nsso.app.properties.NSSOProperties;
import com.sncr.nsso.app.repository.UserRepository;
import com.sncr.nsso.common.bean.LoginDetails;
import com.sncr.nsso.common.bean.Ticket;
import com.sncr.nsso.common.bean.User;
import com.sncr.nsso.common.bean.repo.ProductModuleFeature;
import com.sncr.nsso.common.bean.repo.ProductModuleFeaturePrivileges;
import com.sncr.nsso.common.bean.repo.ProductModules;
import com.sncr.nsso.common.bean.repo.Products;
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
		
		boolean[] ret =  { true, true };//isUserAuthentic(loginDetails.getMasterLoginId(), loginDetails.getPassword());

		boolean isUserAuthentic = ret[0];
		boolean isPassWordActive = ret[1];
		Ticket ticket = null;
		User user = null;
		TicketHelper tHelper = new TicketHelper(userRepository);
		try {
			ticket = new Ticket();
			ticket.setMasterLoginId(loginDetails.getMasterLoginId());
			ticket.setValid(false);
			if (isUserAuthentic) {
				if (isPassWordActive) {
					user = new User();
					user.setMasterLoginId(loginDetails.getMasterLoginId());
					user.setValidMins((nSSOProperties.getValidityMins() != null
							? Long.parseLong(nSSOProperties.getValidityMins()) : 720));
					logger.info("Creating ticket for user", null, null);
					ticket = tHelper.createTicket(user, false);
					logger.info("Ticket created succussfully ");
				} else {
					ticket.setValidityReason("Password Expired");
				}
			} else {
				ticket.setValidityReason("Invalid User Credentials");
			}
		} catch (Exception e) {
			logger.error("Exception occured creating ticket ",
					e, null);
			return null;
		}
		
		/**
		
		ArrayList<Products> prods = new ArrayList<Products>();
		ArrayList<ProductModules> prodMods = new ArrayList<ProductModules>();
		ArrayList<ProductModuleFeature> prodModFeatrs = new ArrayList<ProductModuleFeature>();
		ArrayList<ProductModuleFeaturePrivileges> prodModFeatrPrivs = new ArrayList<ProductModuleFeaturePrivileges>();
		
		ProductModuleFeaturePrivileges productModuleFeaturePrivileges = new ProductModuleFeaturePrivileges();
		ProductModuleFeature productModuleFeature = new ProductModuleFeature();
		ProductModules productModules = new ProductModules();
		Products products = new Products();
		
		productModuleFeaturePrivileges.setPrivCode("PRIV1");
		productModuleFeaturePrivileges.setPrivDesc("TEST");
		productModuleFeaturePrivileges.setPrivName("test");
		productModuleFeaturePrivileges.setProdModFeatrName("DASH1");
		prodModFeatrPrivs.add(productModuleFeaturePrivileges);		
		
		productModuleFeature.setDefaultURL("http://dfdfdf");
		productModuleFeature.setProdCode("ST");
		productModuleFeature.setProdModCode("OBSERVE");
		productModuleFeature.setProdModFeatureDesc("Dashboard 1");
		productModuleFeature.setProdModFeatureName("DASH1");
		productModuleFeature.setProdModFeatrPriv(prodModFeatrPrivs);
		prodModFeatrs.add(productModuleFeature);
		
		productModules.setProdCode("ST");
		productModules.setProdModFeature(prodModFeatrs);
		productModules.setProductModCode("OBSERVE");
		productModules.setProductModDesc("observe");
		productModules.setProductModName("Observe");
		prodMods.add(productModules);
		
		products.setProductCode("ST");
		products.setProductDesc("st");
		products.setProductModules(prodMods);
		products.setProductName("ST");
		prods.add(products);
		ticket.setProducts(prods);
		/**End - For testing purpose only**/

		return new LoginResponse(Jwts.builder().setSubject(loginDetails.getMasterLoginId()).claim("ticket", ticket)
				.setIssuedAt(new Date()).signWith(SignatureAlgorithm.HS256, "secretkey").compact());
	}
	
	@RequestMapping(value = "/doLogout", method = RequestMethod.POST)
    public String doLogout(@RequestBody String ticketID) {
    	TicketHelper tHelper = new TicketHelper(userRepository);
    	logger.info("Logout process start for ticket", null, null);
    	Gson gson = new Gson();
     	return gson.toJson(tHelper.logout(ticketID));
    }
    

    private boolean[] isUserAuthentic(String masterLoginId, String password) {
		return userRepository.authenticateUser(masterLoginId, password);
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
    
    public static void main(String[] args) {
    	SecurityController sc = new SecurityController();
    	System.out.println(sc.randomString(160));
	}
    /**
    
    @RequestMapping(value = "/reCreateTicket", method = RequestMethod.POST)
    public String reCreateTicket(@RequestBody String ticketId) {
    	TicketHelper tHelper = new TicketHelper(userRepository);
    	logger.info("ReCreating process start for ticket", ticketId, null);
    	Ticket ticket = tHelper.reCreateTicket(ticketId, nSSOProperties.getValidityMins() != null ? Long.parseLong(nSSOProperties.getValidityMins()) : 720);
	    Gson gson = new Gson();
    	return gson.toJson(ticket);
    }
    
	
    @RequestMapping(value = "/resetPassword", method = RequestMethod.POST)
    public Valid resetPassword(@RequestBody ResetPwdDtls resetPwdDtls) {
    	Valid valid = new Valid();
    	valid.setValid(false);
    	String message = null;
    	logger.info("validating Password Process start for loginId:"+resetPwdDtls.getMasterLoginId());
    	String eMail = userRepository.getUserEmailId(resetPwdDtls.getMasterLoginId());
    	if (eMail.equals("Invalid")) {
			logger.error("Invalid user Id, Unable to perform Reset Password Process. error message:"+resetPwdDtls.getMasterLoginId(), null, null);
			message = "Invalid user Id";
		}else if(eMail.equals("no email")){
			logger.error("Email Id is not configured for the User Id:"+resetPwdDtls.getMasterLoginId(), null, null);
			message = "Email Id is not configured for the User";
		}else{
			Long createdTime = System.currentTimeMillis();
			String randomString = randomString(160);
			userRepository.insertResetPasswordDtls(resetPwdDtls.getMasterLoginId(),randomString,createdTime, createdTime + (24 * 60 * 60 * 1000));
			String resetpwdlk = resetPwdDtls.getProductUrl();
			//resetpwdlk = resetpwdlk+"/vfyRstPwd?rhc="+randomString;
			resetpwdlk = resetpwdlk+"?rhc="+randomString;
			message = sendResetMail(resetPwdDtls.getMasterLoginId(), eMail, resetpwdlk, createdTime);
			if(message == null){
				valid.setValid(true);
				message = "Mail sent successfully to " + eMail	;//+ ". Requesting user is " + resetPwdDtls.getMasterLoginId();
			}
		}
    	valid.setValidityMessage(message);
    	return valid;
    }
    
    @RequestMapping(value = "/vfyRstPwd", method = RequestMethod.POST)
    public ResetValid vfyRstPwd(@RequestBody RandomHashcode randomHashcode) {
		// P2: handle expired password scenario
    	return userRepository.validateResetPasswordDtls(randomHashcode.getRhc());
    }
    
	private String sendResetMail(String masterLoginId, String eMail,
			String resetpwdlk, long createdTime) {
		String errorMessage = null;
		JavaMailSender javaMailSender = javaMailSender();
		
		// create a message
		MimeMessage simpleMessage = javaMailSender.createMimeMessage();
		
        MimeBodyPart mbp = new MimeBodyPart(); 
        InternetAddress toAddress = null;
		InternetAddress fromAddress = null;
		try {
			String from = nSSOProperties.getMailFrom();
			fromAddress = new InternetAddress(from);

		} catch (AddressException e) {
			errorMessage = "Invalid 'From Address' value ["
					+ nSSOProperties.getMailFrom() + "].";
			return errorMessage;
		}
		try {
			toAddress = new InternetAddress(eMail);
			InternetAddress[] toAddressA = {toAddress} ;
			simpleMessage.setRecipients(Message.RecipientType.TO, toAddressA);
		} catch (Exception e) {
			//logger.error("MailSender - sendMail - Exception -",e);
			errorMessage = "Invalid 'To Address' value [" + eMail + "].";
			return errorMessage;
		}
		try {
			simpleMessage.setFrom(fromAddress);
			String subject = nSSOProperties.getMailSubject();
			simpleMessage.setSubject(subject);
			String text = "";
			text += "You recently requested a password reset for your Synchronoss Application. To create a new password, click on the link below, this link will expire after 24 hours:";
			text += "<br><br><a href=\""+resetpwdlk+"\"> Reset My Password </a> ";
			text += "<br><br>This request was made on : " + new Date(createdTime);
			text += "<br><br>Regards, ";
			text += "<br>Synchronoss Application Support";
			text += "<br><br>********************************************************";
			text += "<br>Please do not reply to this message. Mail sent to this address cannot be answered.";
			//simpleMessage.setText(text);

			mbp.setContent(text, "text/html");
			MimeMultipart multipart = new MimeMultipart();
			multipart.addBodyPart(mbp);
			simpleMessage.setContent(multipart);
			javaMailSender.send(simpleMessage);
			logger.info("Mail sent successfully to " + eMail+ " . Requesting user is " + masterLoginId);
		} catch (Exception e) {
			errorMessage = "Error occured while sending mail. Please try again or contact Administrator.";
			logger.error(e.getMessage(), masterLoginId, null, e);
		}

		return errorMessage;
	}

    private JavaMailSender javaMailSender() {
    	System.setProperty("java.net.preferIPv4Stack" , "true");
        JavaMailSenderImpl mailSender = new JavaMailSenderImpl();
        Properties mailProperties = new Properties();
        //mailProperties.put("mail.smtp.auth", auth);
        //mailProperties.put("mail.smtp.starttls.enable", starttls);
        mailSender.setJavaMailProperties(mailProperties);
        mailSender.setHost(nSSOProperties.getMailHost());
        mailSender.setPort(nSSOProperties.getMailPort());
       // mailSender.setProtocol(protocol);
        
        mailSender.setUsername(nSSOProperties.getMailUserName());
        mailSender.setPassword(nSSOProperties.getMailPassword());
        return mailSender;
    }
	
	
    @RequestMapping(value = "/changePassword", method = RequestMethod.POST)
    public Valid changePassword(@RequestBody ChangePasswordDetails changePasswordDetails) {
    	Valid valid = new Valid();
		String message = doChangePassword(changePasswordDetails);
		valid.setValid(false);
		if (message != null && message.equals("Password Successfully Changed.")) {
		    valid.setValid(true);
		}
		valid.setValidityMessage(message);
    	return valid;
    }
    
    @RequestMapping(value = "/rstChangePassword", method = RequestMethod.POST)
    public Valid rstChangePassword(@RequestBody ChangePasswordDetails changePasswordDetails) {
    	Valid valid = new Valid();
    	valid.setValid(false);
    	String message = null; 
		//String oldPass = changePasswordDetails.getOldPassword();
		String newPass = changePasswordDetails.getNewPassword();
		String cnfNewPass = changePasswordDetails.getCnfNewPassword();
		String loginId = changePasswordDetails.getMasterLoginId();

		if (!cnfNewPass.equals(newPass)) {
			message =  "'New Password' and 'Verify password' does not match.";
		} else if (newPass.length() < 8) {
			message =  "New password should be minimum of 8 charactar.";
		}  else if (loginId.equals(newPass)) {
			message =  "User Name can't be assigned as password.";
		}

		Pattern pCaps = Pattern.compile("[A-Z]");
		Matcher m = pCaps.matcher(newPass);
		if (!m.find()) {
			message =  "Password should contain atleast 1 uppercase charactar.";
		}

		Pattern pSpeChar = Pattern.compile("[~!@#$%^&*?<>]");
		m = pSpeChar.matcher(newPass);
		if (!m.find()) {
			message =  "Password should contain atleast 1 special charactar.";
		}
		if(message == null){
			message = userRepository.rstchangePassword(loginId, newPass);
			if(message == null){
				message = "Password Successfully Changed.";
				valid.setValid(true);
			}
		}
		valid.setValidityMessage(message);
		return valid;
	}
    
    
    private String doChangePassword(ChangePasswordDetails changePasswordDetails) {
		String oldPass = changePasswordDetails.getOldPassword();
		String newPass = changePasswordDetails.getNewPassword();
		String cnfNewPass = changePasswordDetails.getCnfNewPassword();
		String loginId = changePasswordDetails.getMasterLoginId();

		if (!cnfNewPass.equals(newPass)) {
			return "'New Password' and 'Verify password' does not match.";
		} else if (newPass.length() < 8) {
			return "New password should be minimum of 8 charactar.";
		} else if (oldPass.equals(newPass)) {
			return "Old password and new password should not be same.";
		} else if (loginId.equals(newPass)) {
			return "User Name can't be assigned as password.";
		}

		Pattern pCaps = Pattern.compile("[A-Z]");
		Matcher m = pCaps.matcher(newPass);
		if (!m.find()) {
			return "Password should contain atleast 1 uppercase charactar.";
		}

		Pattern pSpeChar = Pattern.compile("[~!@#$%^&*?<>]");
		m = pSpeChar.matcher(newPass);
		if (!m.find()) {
			return "Password should contain atleast 1 special charactar.";
		}
		return userRepository.changePassword(loginId, newPass, oldPass);
	}**/

	
}
