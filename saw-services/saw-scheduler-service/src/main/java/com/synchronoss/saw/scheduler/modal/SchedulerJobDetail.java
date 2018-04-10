package com.synchronoss.saw.scheduler.modal;

import org.springframework.format.annotation.DateTimeFormat;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/*** Important Note :
 *  @Since   SAW2.5.0.
 *  Section readObject and writeObject has added in this class due to the serialization issue after
 *  adding new field in this class. Before editing this class Please read below Instruction carefully.
 *  1) Any new field addition in this class should be inserted in last.
 *  2) Do not modify the existing order of fields in readObject and writeObject method, add the fields at the end.
 *  3) If any dataType changes for existing fields, needs to be handled in readObject section using if else condition
 *  to make this class backward compatible.
 *  4) Do not change the serialVersionUID while editing this class, changing this value will cause serialization issue.
 * Note : This class objects are getting stored in Database as blob part scheduled job definition.
 */
public class SchedulerJobDetail implements Serializable {

    private static final long serialVersionUID =8510739855197957265l;

   private String analysisID;

    private String analysisName;

   private String description;

   private String metricName;

   private String userFullName;

   private String type;

   private String jobName;

   private String jobGroup;

   private String categoryID;

   @DateTimeFormat(pattern = "yyyy/MM/dd HH:mm")
   private Date jobScheduleTime;

   private String cronExpression;

   private String activeTab;

   private String activeRadio;

   private List<String> emailList;

   private List<String> ftp;

   private String fileType ;

    /**
     * Gets analysisID
     *
     * @return value of analysisID
     */
    public String getAnalysisID() {
        return analysisID;
    }

    /**
     * Sets analysisID
     */
    public void setAnalysisID(String analysisID) {
        this.analysisID = analysisID;
    }

    /**
     * Gets analysisName
     *
     * @return value of analysisName
     */
    public String getAnalysisName() {
        return analysisName;
    }

    /**
     * Sets analysisName
     */
    public void setAnalysisName(String analysisName) {
        this.analysisName = analysisName;
    }

    /**
     * Gets description
     *
     * @return value of description
     */
    public String getDescription() {
        return description;
    }

    /**
     * Sets description
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * Gets metricName
     *
     * @return value of metricName
     */
    public String getMetricName() {
        return metricName;
    }

    /**
     * Sets metricName
     */
    public void setMetricName(String metricName) {
        this.metricName = metricName;
    }

    /**
     * Gets userFullName
     *
     * @return value of userFullName
     */
    public String getUserFullName() {
        return userFullName;
    }

    /**
     * Sets userFullName
     */
    public void setUserFullName(String userFullName) {
        this.userFullName = userFullName;
    }

    /**
     * Gets type
     *
     * @return value of type
     */
    public String getType() {
        return type;
    }

    /**
     * Sets type
     */
    public void setType(String type) {
        this.type = type;
    }

    /**
     * Gets jobName
     *
     * @return value of jobName
     */
    public String getJobName() {
        return jobName;
    }

    /**
     * Sets jobName
     */
    public void setJobName(String jobName) {
        this.jobName = jobName;
    }

    /**
     * Gets jobGroup
     *
     * @return value of jobGroup
     */
    public String getJobGroup() {
        return jobGroup;
    }

    /**
     * Sets jobGroup
     */
    public void setJobGroup(String jobGroup) {
        this.jobGroup = jobGroup;
    }

    /**
     * Gets categoryID
     *
     * @return value of categoryID
     */
    public String getCategoryID() {
        return categoryID;
    }

    /**
     * Sets categoryID
     */
    public void setCategoryID(String categoryID) {
        this.categoryID = categoryID;
    }

    /**
     * Gets jobScheduleTime
     *
     * @return value of jobScheduleTime
     */
    public Date getJobScheduleTime() {
        return jobScheduleTime;
    }

    /**
     * Sets jobScheduleTime
     */
    public void setJobScheduleTime(Date jobScheduleTime) {
        this.jobScheduleTime = jobScheduleTime;
    }

    /**
     * Gets cronExpression
     *
     * @return value of cronExpression
     */
    public String getCronExpression() {
        return cronExpression;
    }

    /**
     * Sets cronExpression
     */
    public void setCronExpression(String cronExpression) {
        this.cronExpression = cronExpression;
    }

    /**
     * Gets activeTab
     *
     * @return value of activeTab
     */
    public String getActiveTab() {
        return activeTab;
    }

    /**
     * Sets activeTab
     */
    public void setActiveTab(String activeTab) {
        this.activeTab = activeTab;
    }

    /**
     * Gets activeRadio
     *
     * @return value of activeRadio
     */
    public String getActiveRadio() {
        return activeRadio;
    }

    /**
     * Sets activeRadio
     */
    public void setActiveRadio(String activeRadio) {
        this.activeRadio = activeRadio;
    }

    /**
     * Gets emailList
     *
     * @return value of emailList
     */
    public List<String> getEmailList() {
        return emailList;
    }

    /**
     * Sets emailList
     */
    public void setEmailList(List<String> emailList) {
        this.emailList = emailList;
    }

    public List<String> getFtp() {
        return ftp;
    }

    public void setFtp(List<String> ftp) {
        this.ftp = ftp;
    }

    /**
     * Gets fileType
     *
     * @return value of fileType
     */
    public String getFileType() {
        return fileType;
    }

    /**
     * Sets fileType
     */
    public void setFileType(String fileType) {
        this.fileType = fileType;
    }


    /**
     *
     * @param out
     * @throws IOException
     */
    private void writeObject(java.io.ObjectOutputStream out)
        throws IOException {
        out.writeObject(activeRadio);
        out.writeObject(activeTab);
        out.writeObject(analysisID);
        out.writeObject(analysisName);
        out.writeObject(categoryID);
        out.writeObject(cronExpression);
        out.writeObject(description);
        out.writeObject(emailList);
        out.writeObject(fileType);
        if (ftp!=null || ftp.size()>0)
        out.writeObject(ftp);
        out.writeObject(jobGroup);
        out.writeObject(jobName);
        out.writeObject(jobScheduleTime);
        out.writeObject(metricName);
        out.writeObject(type);
        out.writeObject(userFullName);

    }

    /**
     *
     * @param in
     * @throws IOException
     * @throws ClassNotFoundException
     */
    private void readObject(java.io.ObjectInputStream in)
        throws IOException, ClassNotFoundException {

        activeRadio = (String) in.readObject();
        activeTab = (String) in.readObject();
        analysisID = (String) in.readObject();
        analysisName = (String) in.readObject();
        categoryID = (String) in.readObject();
        cronExpression = (String) in.readObject();
        description = (String) in.readObject();
        emailList = (List<String>) in.readObject();
        fileType = (String) in.readObject();
        Object obj = in.readObject();
        if(obj instanceof List) {
            ftp = (List<String>) obj;
            jobGroup = (String) in.readObject();
            jobName = (String) in.readObject();
            jobScheduleTime = (Date) in.readObject();
            metricName = (String) in.readObject();
            type = (String) in.readObject();
            userFullName = (String) in.readObject();
        }else
        {
            jobGroup = (String) obj;
            jobName = (String) in.readObject();
            jobScheduleTime = (Date) in.readObject();
            metricName = (String) in.readObject();
            type = (String) in.readObject();
            userFullName = (String) in.readObject();
        }
    }
}
