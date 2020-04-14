package com.synchronoss.saw.export.controller;

import com.synchronoss.saw.export.distribution.MailSenderUtil;
import com.synchronoss.saw.export.exceptions.JSONValidationSAWException;
import com.synchronoss.saw.export.model.EmailDetails;

import com.synchronoss.sip.utils.SipCommonUtils;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import java.io.File;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;





@RestController
@RequestMapping(value = "/exports")
public class GenericEmailController {

  private static final Logger logger = LoggerFactory
      .getLogger(GenericEmailController.class);

  @Autowired
  private ApplicationContext appContext;

  /**
   * Generic method to send email across sip.
   * 
   */
  @ApiOperation(value = "Send email", nickname = "generic email api", notes = "")
  @ApiResponses(
      value = {@ApiResponse(code = 200, message = "Request has been succeeded without any error"),
          @ApiResponse(code = 404, message = "The resource you were trying to reach is not found"),
          @ApiResponse(code = 500, message = "Server is down. Contact System adminstrator"),
          @ApiResponse(code = 400, message = "Bad request"),
          @ApiResponse(code = 401, message = "Unauthorized"), 
          @ApiResponse(code = 415,
              message = "Unsupported Type. " + "Representation not supported for the resource")})
  @RequestMapping(value = "/email/send", 
      method = RequestMethod.POST)
  @ResponseStatus(HttpStatus.OK)
  public ResponseEntity<Map<String,String>> sendEmail(@RequestBody EmailDetails emailDetails) {

    if ((emailDetails.getSubject() == null || emailDetails.getSubject().isEmpty()) 
        || (emailDetails.getRecipients() == null ||  emailDetails.
        getRecipients().isEmpty())) {
      throw new JSONValidationSAWException("Recipient address "
          + "and subject are required");
    }
    
    if (emailDetails.getAttachmentFilePath() != null) {
      File file = new File(SipCommonUtils.normalizePath(emailDetails.getAttachmentFilePath()));
      if (!file.exists()) {
        throw new JSONValidationSAWException("Attachment file path "
            + "doesnt exists");
      }
    }
    
    logger.trace("Email send request recieved");
    logger.trace("Subject ::" + emailDetails.getSubject());
    logger.trace("Recipients ::" + emailDetails.getRecipients());
    logger.trace("Contect ::" + emailDetails.getContent());

    MailSenderUtil mailSender = new MailSenderUtil(
        appContext.getBean(JavaMailSender.class));
    mailSender.sendMail(emailDetails.getRecipients(), 
        emailDetails.getSubject(), emailDetails.getContent(), emailDetails
        .getAttachmentFilePath());
    Map<String,String> response = new HashMap<String, String>();
    response.put("emailSent", "true");

    logger.trace("Email send request completed");
    
    return ResponseEntity.ok(response);
  }

}
