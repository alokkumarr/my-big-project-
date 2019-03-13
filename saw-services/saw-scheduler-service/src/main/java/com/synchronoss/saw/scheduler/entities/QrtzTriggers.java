package com.synchronoss.saw.scheduler.entities;

import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.Table;

import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Entity(name = "qrtz_triggers")
@EntityListeners(AuditingEntityListener.class)
@Table(name = "qrtz_triggers", catalog = "saw_scheduler", schema = "")
public class QrtzTriggers {

  @EmbeddedId
  private QrtzTriggersId id;
  @Column(name = "job_name")
  private String jobName;
  @Column(name = "job_group")
  private String jobGroup;
  @Column(name = "description")
  private String descritpion;

  @Column(name = "next_fire_time")
  private long nextFireTime;
  @Column(name = "prev_fire_time")
  private long prevFireTime;
  @Column(name = "priority")
  private int priority;

  @Column(name = "trigger_state")
  private String triggerState;
  @Column(name = "trigger_type")
  private String triggerType;
  @Column(name = "start_time")
  private long startTime;
  @Column(name = "end_time")
  private long endTime;
  @Column(name = "calendar_name")
  private String calandarName;
  @Column(name = "misfire_instr")
  private short misfireInstruction;



  public String getJobName() {
    return jobName;
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

  public String getDescritpion() {
    return descritpion;
  }

  public void setDescritpion(String descritpion) {
    this.descritpion = descritpion;
  }

  public long getNextFireTime() {
    return nextFireTime;
  }

  public void setNextFireTime(long nextFireTime) {
    this.nextFireTime = nextFireTime;
  }

  public long getPrevFireTime() {
    return prevFireTime;
  }

  public void setPrevFireTime(long prevFireTime) {
    this.prevFireTime = prevFireTime;
  }

  public int getPriority() {
    return priority;
  }

  public void setPriority(int priority) {
    this.priority = priority;
  }

  public String getTriggerState() {
    return triggerState;
  }

  public void setTriggerState(String triggerState) {
    this.triggerState = triggerState;
  }

  public String getTriggerType() {
    return triggerType;
  }

  public void setTriggerType(String triggerType) {
    this.triggerType = triggerType;
  }

  public long getStartTime() {
    return startTime;
  }

  public void setStartTime(long startTime) {
    this.startTime = startTime;
  }

  public long getEndTime() {
    return endTime;
  }

  public void setEndTime(long endTime) {
    this.endTime = endTime;
  }

  public String getCalandarName() {
    return calandarName;
  }

  public void setCalandarName(String calandarName) {
    this.calandarName = calandarName;
  }

  public short getMisfireInstruction() {
    return misfireInstruction;
  }

  public void setMisfireInstruction(short misfireInstruction) {
    this.misfireInstruction = misfireInstruction;
  }

  public byte[] getOb_data() {
    return jobData;
  }

  public void setOb_data(byte[] jobData) {
    this.jobData = jobData;
  }

  @Column(name = "job_data")
  private byte[] jobData;

}
