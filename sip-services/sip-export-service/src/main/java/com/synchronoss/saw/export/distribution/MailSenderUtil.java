package com.synchronoss.saw.export.distribution;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.mail.javamail.JavaMailSender;

import javax.activation.DataHandler;
import javax.activation.FileDataSource;
import javax.mail.Message;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import java.io.File;

public class MailSenderUtil {
	private static final Logger logger = LoggerFactory.getLogger(MailSenderUtil.class);

	private JavaMailSender mailSender;

	public MailSenderUtil(JavaMailSender mailSender)
	{
		this.mailSender=mailSender;
	}

	public void sendMail(String recipients, String subject, String message,
						 String attachFile) {
		logger.debug("User activity started here:" + this.getClass().getName()	+ " MailUtility  method");
		logger.debug("recipients :" + recipients + " attachFiles " + attachFile);
		try {
			System.setProperty("java.net.preferIPv4Stack", "true");
			// create a message
			MimeMessage msg = mailSender.createMimeMessage();

			String[] emailIds = recipients.split(",");
			InternetAddress[] addressTo = new InternetAddress[emailIds.length];
			for (int i = 0, length = emailIds.length; i < length; i++) {
				addressTo[i] = new InternetAddress(
						emailIds[i]);
			}
			msg.setRecipients(Message.RecipientType.TO, addressTo);

			// Setting the Subject and Content Type
			msg.setSubject(subject);
			MimeBodyPart mimebodypart = new MimeBodyPart();
			mimebodypart.setContent(message, "text/html");

			// attach message BODY
			MimeMultipart mimemultipart = new MimeMultipart();
			mimemultipart.addBodyPart(mimebodypart);

			// attach FILE
			if (null != attachFile && !attachFile.isEmpty()) {

					mimebodypart = new MimeBodyPart();

						FileDataSource filedatasource = new FileDataSource(
								attachFile);
						mimebodypart.setDataHandler(new DataHandler(
								filedatasource));

					mimebodypart.setFileName(new File(attachFile).getName());
					mimemultipart.addBodyPart(mimebodypart);
				}
			msg.setContent(mimemultipart);
			mailSender.send(msg);
			logger.debug("activity started here:" + this.getClass().getName()	+ " Mail Send completed");
		} catch (Exception e) {
			logger.error("MailSenderUtil - sendMail - Exception -",e);
		}
	}
}
