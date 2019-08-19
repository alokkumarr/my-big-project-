package com.synchronoss.saw.alert;

import com.synchronoss.saw.alert.entities.AlertRulesDetails;
import com.synchronoss.saw.alert.utils.AssertAnnotationsUtils;
import com.synchronoss.saw.alert.utils.ReflectionUtils;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import org.hibernate.annotations.CreationTimestamp;
import org.junit.Assert;
import org.junit.Test;

/**
 * This Test class contains the unit test cases for AlertRulesDetails Entity class.
 */
public class AlertRulesDetailsTests implements EntityClassTests {

  @Override
  @Test
  public void typeAsertAnnotationsTest() {

    AssertAnnotationsUtils.assertType(
        AlertRulesDetails.class, Entity.class, EntityListeners.class, Table.class);
  }

  @Override
  @Test
  public void fieldsAsertsAnotationTest() {
    AssertAnnotationsUtils.assertField(
        AlertRulesDetails.class, "alertRulesSysId", Id.class, GeneratedValue.class, Column.class);
    AssertAnnotationsUtils.assertField(AlertRulesDetails.class, "datapodId", Column.class);
    AssertAnnotationsUtils.assertField(AlertRulesDetails.class, "alertName", Column.class);
    AssertAnnotationsUtils.assertField(AlertRulesDetails.class, "alertDescription", Column.class);
    AssertAnnotationsUtils.assertField(AlertRulesDetails.class, "category", Column.class);
    AssertAnnotationsUtils.assertField(
        AlertRulesDetails.class, "alertSeverity", Enumerated.class, Column.class);
    AssertAnnotationsUtils.assertField(AlertRulesDetails.class, "monitoringEntity", Column.class);
    AssertAnnotationsUtils.assertField(
        AlertRulesDetails.class, "aggregation", Enumerated.class, Column.class);
    AssertAnnotationsUtils.assertField(
        AlertRulesDetails.class, "operator", Enumerated.class, Column.class);
    AssertAnnotationsUtils.assertField(AlertRulesDetails.class, "thresholdValue", Column.class);
    AssertAnnotationsUtils.assertField(AlertRulesDetails.class, "activeInd", Column.class);
    AssertAnnotationsUtils.assertField(AlertRulesDetails.class, "createdBy", Column.class);
    AssertAnnotationsUtils.assertField(
        AlertRulesDetails.class, "createdTime", CreationTimestamp.class, Column.class);
    AssertAnnotationsUtils.assertField(AlertRulesDetails.class, "modifiedTime", Column.class);
    AssertAnnotationsUtils.assertField(AlertRulesDetails.class, "modifiedBy", Column.class);
  }

  @Override
  @Test
  public void methodAsertsAnnotationsTest() {
    AssertAnnotationsUtils.assertMethod(AlertRulesDetails.class, "getAlertRulesSysId");
    AssertAnnotationsUtils.assertMethod(AlertRulesDetails.class, "getDatapodId");
    AssertAnnotationsUtils.assertMethod(AlertRulesDetails.class, "getAlertName");
    AssertAnnotationsUtils.assertMethod(AlertRulesDetails.class, "getAlertDescription");
    AssertAnnotationsUtils.assertMethod(AlertRulesDetails.class, "getCategory");
    AssertAnnotationsUtils.assertMethod(AlertRulesDetails.class, "getAlertSeverity");
    AssertAnnotationsUtils.assertMethod(AlertRulesDetails.class, "getMonitoringEntity");
    AssertAnnotationsUtils.assertMethod(AlertRulesDetails.class, "getAggregation");
    AssertAnnotationsUtils.assertMethod(AlertRulesDetails.class, "getOperator");
    AssertAnnotationsUtils.assertMethod(AlertRulesDetails.class, "getThresholdValue");
    AssertAnnotationsUtils.assertMethod(AlertRulesDetails.class, "getActiveInd");
    AssertAnnotationsUtils.assertMethod(AlertRulesDetails.class, "getCreatedBy");
    AssertAnnotationsUtils.assertMethod(AlertRulesDetails.class, "getModifiedTime");
    AssertAnnotationsUtils.assertMethod(AlertRulesDetails.class, "getModifiedBy");
  }

  @Override
  @Test
  public void entityTest() {
    // setup
    Entity a = ReflectionUtils.getClassAnnotation(AlertRulesDetails.class, Entity.class);
    // assert
    Assert.assertEquals("", a.name());
  }

  @Override
  @Test
  public void tableTest() {
    // setup
    Table t = ReflectionUtils.getClassAnnotation(AlertRulesDetails.class, Table.class);
    // assert
    Assert.assertEquals("ALERT_RULES_DETAILS", t.name());
  }
}
