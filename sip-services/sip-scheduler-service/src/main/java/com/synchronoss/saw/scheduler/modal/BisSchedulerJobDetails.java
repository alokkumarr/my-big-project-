package com.synchronoss.saw.scheduler.modal;

import java.io.IOException;
import java.io.OptionalDataException;
import java.io.Serializable;
import java.util.Date;
import java.util.List;
import org.springframework.format.annotation.DateTimeFormat;

/**
 * Important Note : . Section readObject and writeObject has added in this class due to the
 * serialization issue after adding new field in this class. Before editing this class Please read
 * below Instruction carefully. 1) Any new field addition in this class should be inserted in last.
 * 2) Do not modify the existing order of fields in readObject and writeObject method, add the
 * fields at the end. 3) If any dataType changes for existing fields, needs to be handled in
 * readObject section using if else condition to make this class backward compatible. 4) Do not
 * change the serialVersionUID while editing this class, changing this value will cause
 * serialization issue. Note : This class objects are getting stored in Database as blob part
 * scheduled job definition.
 */
public class BisSchedulerJobDetails implements Serializable {
  /**
   * Id to distinguish object during read/write process.
   */
  private static final long serialVersionUID = 5064023705368082645L;

  private String channelId;
  private String routeId;


  private String entityId;
  private String description;
  private String userFullName;
  private String channelType;
  private String jobName;
  private String jobGroup;

  // TODO: DateTimeFormat has no effect here, can be removed.
  @DateTimeFormat(pattern = "yyyy/MM/dd HH:mm")
  private Date jobScheduleTime;
  private String cronExpression;
  private List<String> emailList;
  private String fileType;

  // TODO: DateTimeFormat has no effect here, can be removed.
  @DateTimeFormat(pattern = "yyyy/MM/dd HH:mm")
  private Date endDate;

  private String timezone;

  public String getEntityId() {
    return entityId;
  }

  public void setEntityId(String entityId) {
    this.entityId = entityId;
  }

  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public String getUserFullName() {
    return userFullName;
  }

  public void setUserFullName(String userFullName) {
    this.userFullName = userFullName;
  }

  public List<String> getEmailList() {
    return emailList;
  }

  public void setEmailList(List<String> emailList) {
    this.emailList = emailList;
  }

  public String getFileType() {
    return fileType;
  }

  public void setFileType(String fileType) {
    this.fileType = fileType;
  }

  public String getCronExpression() {
    return cronExpression;
  }

  public void setCronExpression(String cronExpression) {
    this.cronExpression = cronExpression;
  }

  public String getJobName() {
    return jobName;
  }

  public Date getJobScheduleTime() {
    return jobScheduleTime;
  }

  public void setJobScheduleTime(Date jobScheduleTime) {
    this.jobScheduleTime = jobScheduleTime;
  }

  public Date getEndDate() {
    return endDate;
  }

  public void setEndDate(Date endDate) {
    this.endDate = endDate;
  }

  public void setJobName(String jobName) {
    this.jobName = jobName;
  }

  public String getJobGroup() {
    return jobGroup;
  }

  public void setJobGroup(String jobGroup) {
    this.jobGroup = jobGroup;
  }

  
  public String getChannelType() {
    return channelType;
  }

  public void setChannelType(String channelType) {
    this.channelType = channelType;
  }
  
  
  public String getRouteId() {
	return routeId;
  }

  public void setRouteId(String routeId) {
	this.routeId = routeId;
  }

  public String getChannelId() {
	return channelId;
  }

  public void setChannelId(String channelId) {
    this.channelId = channelId;
  }

  public void setTimezone(String timeone) {
      this.timezone = timeone;
  }

  public String getTimezone() {
      return this.timezone;
  }
}
