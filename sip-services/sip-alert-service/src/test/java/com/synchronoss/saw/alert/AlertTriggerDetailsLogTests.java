package com.synchronoss.saw.alert;

import com.synchronoss.saw.alert.entities.AlertTriggerDetailsLog;
import com.synchronoss.saw.alert.utils.AssertAnnotationsUtils;
import com.synchronoss.saw.alert.utils.ReflectionUtils;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import org.junit.Assert;
import org.junit.Test;

/**
 * This Test class contains the unit test cases for AlertTriggerDetailsLog Entity class.
 */
public class AlertTriggerDetailsLogTests implements EntityClassTests {

  @Override
  @Test
  public void typeAsertAnnotationsTest() {

    AssertAnnotationsUtils.assertType(
        AlertTriggerDetailsLog.class, Entity.class, EntityListeners.class, Table.class);
  }

  @Override
  @Test
  public void fieldsAsertsAnotationTest() {
    AssertAnnotationsUtils.assertField(
        AlertTriggerDetailsLog.class,
        "alertTriggerSysId",
        Id.class,
        GeneratedValue.class,
        Column.class);
    AssertAnnotationsUtils.assertField(
        AlertTriggerDetailsLog.class, "alertRulesSysId", Column.class);
    AssertAnnotationsUtils.assertField(
        AlertTriggerDetailsLog.class, "alertState", Enumerated.class, Column.class);
    AssertAnnotationsUtils.assertField(AlertTriggerDetailsLog.class, "startTime", Column.class);
    AssertAnnotationsUtils.assertField(
        AlertTriggerDetailsLog.class, "thresholdValue", Column.class);
    AssertAnnotationsUtils.assertField(AlertTriggerDetailsLog.class, "metricValue", Column.class);
  }

  @Override
  @Test
  public void methodAsertsAnnotationsTest() {
    AssertAnnotationsUtils.assertMethod(AlertTriggerDetailsLog.class, "getAlertTriggerSysId");
    AssertAnnotationsUtils.assertMethod(AlertTriggerDetailsLog.class, "getAlertRulesSysId");
    AssertAnnotationsUtils.assertMethod(AlertTriggerDetailsLog.class, "getAlertState");
    AssertAnnotationsUtils.assertMethod(AlertTriggerDetailsLog.class, "getStartTime");
    AssertAnnotationsUtils.assertMethod(AlertTriggerDetailsLog.class, "getThresholdValue");
    AssertAnnotationsUtils.assertMethod(AlertTriggerDetailsLog.class, "getMetricValue");
  }

  @Override
  @Test
  public void entityTest() {
    // setup
    Entity a = ReflectionUtils.getClassAnnotation(AlertTriggerDetailsLog.class, Entity.class);
    // assert
    Assert.assertEquals("", a.name());
  }

  @Override
  @Test
  public void tableTest() {
    // setup
    Table t = ReflectionUtils.getClassAnnotation(AlertTriggerDetailsLog.class, Table.class);
    // assert
    Assert.assertEquals("ALERT_TRIGGER_DETAILS_LOG", t.name());
  }
}
