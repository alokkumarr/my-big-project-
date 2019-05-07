package com.synchronoss.saw.scheduler.entities;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Embeddable;

@Embeddable
public class QrtzTriggersId implements Serializable {
 
  
  private static final long serialVersionUID = 1L;
  
  
  @Column(name = "sched_name")
  private String scheduleName;
  @Column(name = "trigger_name")
  private String triggerName;
  @Column(name = "trigger_group")
  private String triggerGroup;

  public String getScheduleName() {
    return scheduleName;
  }

  public void setScheduleName(String scheduleName) {
    this.scheduleName = scheduleName;
  }

  public String getTriggerName() {
    return triggerName;
  }

  public void setTriggerName(String triggerName) {
    this.triggerName = triggerName;
  }

  public String getTriggerGroup() {
    return triggerGroup;
  }

  public void setTriggerGroup(String triggerGroup) {
    this.triggerGroup = triggerGroup;
  }

}
