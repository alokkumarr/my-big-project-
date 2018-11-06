package com.synchronoss.saw.scheduler.modal;


import java.io.IOException;
import java.io.OptionalDataException;
import java.io.Serializable;
import java.util.Date;
import java.util.List;

import org.springframework.format.annotation.DateTimeFormat;

/*** Important Note :
 *  .
 *  Section readObject and writeObject has added in this class due to the serialization issue after
 *  adding new field in this class. Before editing this class Please read below Instruction carefully.
 *  1) Any new field addition in this class should be inserted in last.
 *  2) Do not modify the existing order of fields in readObject and writeObject method, add the fields at the end.
 *  3) If any dataType changes for existing fields, needs to be handled in readObject section using if else condition
 *  to make this class backward compatible.
 *  4) Do not change the serialVersionUID while editing this class, changing this value will cause serialization issue.
 * Note : This class objects are getting stored in Database as blob part scheduled job definition.
 */
public class BISSchedulerJobDetails implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 5064023705368082645L;
	
	
	private String entityId;
	private String description;
	private String userFullName;
	private String channelType;
    private String jobName;
    private String jobGroup;
   
    @DateTimeFormat(pattern = "yyyy/MM/dd HH:mm")
    private Date jobScheduleTime;
    private String cronExpression;
    private List<String> emailList;
    private String fileType ;
    @DateTimeFormat(pattern = "yyyy/MM/dd HH:mm")
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
    *
    * @param out
    * @throws IOException
    */
   private void writeObject(java.io.ObjectOutputStream out)
       throws IOException {
	   
	   out.writeObject(channelType);
	   out.writeObject(cronExpression);
	   out.writeObject(description);
	   out.writeObject(emailList);
	   if(endDate!=null) {
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
    *
    * @param in
    * @throws IOException
    * @throws ClassNotFoundException
    */
   private void readObject(java.io.ObjectInputStream in)
       throws IOException, ClassNotFoundException {
	   channelType = (String) in.readObject();
	   cronExpression = (String) in.readObject();
   	   description = (String) in.readObject();
       emailList = (List<String>) in.readObject();
       try {
           /* End date is optional data field and it will contains null value for existing schedules
           generated prior to sip v2.6.0 , handle the Optional Data Exception explicitly to identify the end of stream*/
           Object endDt = in.readObject();
           if (endDt instanceof Date)
           endDate = (Date) endDt;
       }
       catch (OptionalDataException e)
       {/* catch block to avoid serialization for newly added fields.*/ }
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
}
