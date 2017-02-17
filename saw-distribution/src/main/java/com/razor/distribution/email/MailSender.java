package com.razor.distribution.email;

import java.io.File;
import java.util.List;

import javax.activation.DataHandler;
import javax.activation.FileDataSource;
import javax.mail.Address;
import javax.mail.Message;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

@Component
public class MailSender {
	private static final Logger logger = LoggerFactory.getLogger(MailSender.class);
	@Autowired
	private JavaMailSender javaMailSender;

	@Autowired
	MailUtility mailUtility;
	public void sendMail(String recipients, String subject, String message,
			List<String> attachFiles) {
		logger.debug("User activity started here:" + this.getClass().getName()	+ " MailUtility  method");
		logger.debug("recipients :" + recipients + " attachFiles " + attachFiles);
		try {
			System.setProperty("java.net.preferIPv4Stack", "true");
			// create a message
			MimeMessage msg = javaMailSender.createMimeMessage();

			String[] emailIds = recipients.split(",");
			InternetAddress[] addressTo = new InternetAddress[emailIds.length];
			for (int i = 0, length = emailIds.length; i < length; i++) {
				addressTo[i] = new javax.mail.internet.InternetAddress(
						emailIds[i]);
			}
			msg.setRecipients(Message.RecipientType.TO, addressTo);

			// Setting the Subject and Content Type
			msg.setSubject(subject);
			Address fromAddress = new InternetAddress(mailUtility.getFrom());
			msg.setFrom(fromAddress);
			MimeBodyPart mimebodypart = new MimeBodyPart();
			mimebodypart.setContent(message, "text/html");

			// attach message BODY
			MimeMultipart mimemultipart = new MimeMultipart();
			mimemultipart.addBodyPart(mimebodypart);

			// attach FILE
			if (null != attachFiles && !attachFiles.isEmpty()) {
				for (int i = 0; i < attachFiles.size(); i++) {
					mimebodypart = new MimeBodyPart();
					String fileName = (String) attachFiles.get(i);
					try {
						FileDataSource filedatasource = new FileDataSource(
								fileName);
						mimebodypart.setDataHandler(new DataHandler(
								filedatasource));
					} catch (Exception exception3) {
					}
					mimebodypart.setFileName(new File(fileName).getName());
					mimemultipart.addBodyPart(mimebodypart);
					fileName = null;
				}
			}
			msg.setContent(mimemultipart);
			javaMailSender.send(msg);
			logger.debug("User activity started here:" + this.getClass().getName()	+ " Mail Send completed");
		} catch (Exception e) {
			logger.error("MailSender - sendMail - Exception -",e);
		}
	}
	
	public boolean isValid()
	{
		if (StringUtils.isEmpty(mailUtility.getSubject())) {
			logger.debug("Exception occured at " + this.getClass().getName()+ " Subject is Empty ! please check mail.properties");
			return false;
		} else if (StringUtils.isEmpty(mailUtility.getFrom())) {
			logger.debug("Exception occured at " + this.getClass().getName() + " From email id is Empty ! please check mail.properties");
			return false;
		} else if (StringUtils.isEmpty(mailUtility.getHost())) {
			logger.debug("Exception occured at " + this.getClass().getName() + " From Host id is Empty ! please check mail.properties");
			return false;
		} else if (StringUtils.isEmpty(mailUtility.getPort())) {
			logger.debug("Exception occured at " + this.getClass().getName() + " From Port id is Empty ! please check mail.properties");
			return false;
		}
		return true;
	}

}
