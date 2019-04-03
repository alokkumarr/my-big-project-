package com.synchronoss.sip.alert.entities;

import com.synchronoss.sip.alert.modal.Aggregation;
import com.synchronoss.sip.alert.modal.AlertSeverity;
import com.synchronoss.sip.alert.modal.Operator;
import java.util.Date;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.Id;
import javax.persistence.Table;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Entity
@EntityListeners(AuditingEntityListener.class)
@Table(name = "ALERT_RULES_DETAILS")
public class AlertRulesDetails {

    @Id
    @Column (name = "ALERT_RULES_SYS_ID")
    Long alertRulesSysId;
    @Column (name = "DATAPOD_ID")
    String datapodId;
    @Column (name = "RULE_NAME")
    String ruleName;
    @Column (name = "RULE_DESCRIPTIONS")
    String ruleDescriptions;
    @Column (name = "SEVERITY")
    AlertSeverity alertSeverity;
    @Column (name = "MONITORING_ENTITY")
    String monitoringEntity;
    @Column (name = "AGGREGATION")
    Aggregation aggregation;
    @Column (name = "OPERATOR")
    Operator operator;
    @Column (name = "ACTIVE_IND")
    Boolean activeInd;
    @Column (name = "CREATED_BY")
    String createdBy;
    @Column (name = "CREATED_TIME")
    Date createdTime;
    @Column (name = "MODIFIED_TIME")
    Date modifiedTime;
    @Column (name = "MODIFIED_BY")
    String modifiedBy;

}
