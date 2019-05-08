package com.synchronoss.saw.alert;

import com.synchronoss.saw.alert.entities.AlertCustomerDetails;
import com.synchronoss.saw.alert.utils.AssertAnnotationsUtils;
import com.synchronoss.saw.alert.utils.ReflectionUtils;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import org.junit.Assert;
import org.junit.Test;

/**
 * This Test class contains the unit test cases for AlertCustomerDetails Entity class.
 */
public class AlertCustomerDetailsTests implements EntityClassTests {

  @Override
  @Test
  public void typeAsertAnnotationsTest() {

    AssertAnnotationsUtils.assertType(
        AlertCustomerDetails.class, Entity.class, EntityListeners.class, Table.class);
  }

  @Override
  @Test
  public void fieldsAsertsAnotationTest() {
    AssertAnnotationsUtils.assertField(
        AlertCustomerDetails.class,
        "alertCustomerSysId",
        Id.class,
        GeneratedValue.class,
        Column.class);
    AssertAnnotationsUtils.assertField(AlertCustomerDetails.class, "customerCode", Column.class);
    AssertAnnotationsUtils.assertField(AlertCustomerDetails.class, "productCode", Column.class);
    AssertAnnotationsUtils.assertField(AlertCustomerDetails.class, "activeInd", Column.class);
    AssertAnnotationsUtils.assertField(AlertCustomerDetails.class, "createdBy", Column.class);
    AssertAnnotationsUtils.assertField(AlertCustomerDetails.class, "createdTime", Column.class);
    AssertAnnotationsUtils.assertField(AlertCustomerDetails.class, "modifiedTime", Column.class);
    AssertAnnotationsUtils.assertField(AlertCustomerDetails.class, "modifiedBy", Column.class);
  }

  @Override
  @Test
  public void methodAsertsAnnotationsTest() {
    AssertAnnotationsUtils.assertMethod(AlertCustomerDetails.class, "getAlertCustomerSysId");
    AssertAnnotationsUtils.assertMethod(AlertCustomerDetails.class, "getProductCode");
    AssertAnnotationsUtils.assertMethod(AlertCustomerDetails.class, "getActiveInd");
    AssertAnnotationsUtils.assertMethod(AlertCustomerDetails.class, "getCreatedBy");
    AssertAnnotationsUtils.assertMethod(AlertCustomerDetails.class, "getCreatedTime");
    AssertAnnotationsUtils.assertMethod(AlertCustomerDetails.class, "getModifiedTime");
    AssertAnnotationsUtils.assertMethod(AlertCustomerDetails.class, "getModifiedBy");
  }

  @Override
  @Test
  public void entityTest() {
    // setup
    Entity a = ReflectionUtils.getClassAnnotation(AlertCustomerDetails.class, Entity.class);
    // assert
    Assert.assertEquals("", a.name());
  }

  @Override
  @Test
  public void tableTest() {
    // setup
    Table t = ReflectionUtils.getClassAnnotation(AlertCustomerDetails.class, Table.class);
    // assert
    Assert.assertEquals("ALERT_CUSTOMER_DETAILS", t.name());
  }
}
