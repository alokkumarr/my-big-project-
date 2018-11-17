package com.synchronoss.saw.batch.model;

import java.io.IOException;
import java.io.OptionalDataException;
import java.io.Serializable;
import java.util.Date;
import java.util.List;


public class BisSchedulerRequest implements Serializable {

  private static final long serialVersionUID = 1123775037678080763L;
  private String channelId;
  private String routeId;

  private String entityId;
  private String description;
  private String userFullName;
  private String channelType;
  private String jobName;
  private String jobGroup;

  private Date jobScheduleTime;
  private String cronExpression;
  private List<String> emailList;
  private String fileType;
  private Date endDate;

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

  /**
   * Customiation during Serialization write process.
   *
   * @param out outputstream
   * @throws IOException io exception
   */
  private void writeObject(java.io.ObjectOutputStream out) throws IOException {

    out.writeObject(channelType);
    out.writeObject(cronExpression);
    out.writeObject(description);
    out.writeObject(emailList);
    if (endDate != null) {
      out.writeObject(endDate);
    }
    out.writeObject(entityId);
    out.writeObject(fileType);
    out.writeObject(jobGroup);
    out.writeObject(jobName);
    out.writeObject(jobScheduleTime);
    out.writeObject(userFullName);
  }

  /**
   * Customization during seralization read process.
   *
   * @param in inputstream
   * @throws IOException exception
   * @throws ClassNotFoundException exception
   */
  private void readObject(java.io.ObjectInputStream in) throws IOException, ClassNotFoundException {
    channelType = (String) in.readObject();
    cronExpression = (String) in.readObject();
    description = (String) in.readObject();
    emailList = (List<String>) in.readObject();
    try {
      /*
       * End date is optional data field and it will contains null value for existing schedules
       * generated prior to sip v2.6.0 , handle the Optional Data Exception explicitly to identify
       * the end of stream
       */
      Object endDt = in.readObject();
      if (endDt instanceof Date) {
        endDate = (Date) endDt;
      }

    } catch (OptionalDataException e) {
      /* catch block to avoid serialization for newly added fields. */
    }
    entityId = (String) in.readObject();
    fileType = (String) in.readObject();
    jobGroup = (String) in.readObject();
    jobName = (String) in.readObject();
    jobScheduleTime = (Date) in.readObject();
    userFullName = (String) in.readObject();

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

}
