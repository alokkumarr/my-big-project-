package com.synchronoss.saw.export.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

@ApiModel("This model payload holds the details of email to be sent"
		+ " in generic email api.")
public class EmailDetails {
  
  @ApiModelProperty(value = "indicates email subject line", dataType = "String",
		  allowEmptyValue = false)
  @JsonProperty("subject")
  String subject;
  
  @ApiModelProperty(value = "Recipient email id. Use comma(,) delimer in case of "
  		+ "multiple recipients", dataType = "String", allowEmptyValue = false)
  @JsonProperty("recipients")
  String recipients;
  
  @ApiModelProperty(value = "Email message content", dataType = "String",
		  allowEmptyValue = true)
  @JsonProperty("content")
  String content;
  
  @ApiModelProperty(value = "file path of attachment to be sent in email",
		  dataType = "String", allowEmptyValue = true)
  @JsonProperty("attachmentFilePath")
  String attachmentFilePath;
  
  
  
  public String getAttachmentFilePath() {
    return attachmentFilePath;
  }
  public void setAttachmentFilePath(String attachmentFilePath) {
    this.attachmentFilePath = attachmentFilePath;
  }
  public String getSubject() {
    return subject;
  }
  public void setSubject(String subject) {
    this.subject = subject;
  }
  public String getRecipients() {
    return recipients;
  }
  public void setRecipients(String recipients) {
    this.recipients = recipients;
  }
  public String getContent() {
    return content;
  }
  public void setContent(String content) {
    this.content = content;
  }

}