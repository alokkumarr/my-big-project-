package com.synchronoss.saw.alert;

import com.synchronoss.saw.alert.entities.DatapodDetails;
import com.synchronoss.saw.alert.utils.AssertAnnotationsUtils;
import com.synchronoss.saw.alert.utils.ReflectionUtils;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.Id;
import javax.persistence.Table;
import org.hibernate.annotations.CreationTimestamp;
import org.junit.Assert;
import org.junit.Test;

/**
 * This Test class contains the unit test cases for DatapodDetails Entity class.
 */
public class DatapodDetailsTests implements EntityClassTests {

  @Override
  @Test
  public void typeAsertAnnotationsTest() {

    AssertAnnotationsUtils.assertType(
        DatapodDetails.class, Entity.class, EntityListeners.class, Table.class);
  }

  @Override
  @Test
  public void fieldsAsertsAnotationTest() {
    AssertAnnotationsUtils.assertField(DatapodDetails.class, "datapodId", Id.class, Column.class);
    AssertAnnotationsUtils.assertField(DatapodDetails.class, "datapodName", Column.class);
    AssertAnnotationsUtils.assertField(DatapodDetails.class, "alertCustomerSysId", Column.class);
    AssertAnnotationsUtils.assertField(
        DatapodDetails.class, "createdTime", CreationTimestamp.class, Column.class);
    AssertAnnotationsUtils.assertField(DatapodDetails.class, "createdBy", Column.class);
  }

  @Override
  @Test
  public void methodAsertsAnnotationsTest() {
    AssertAnnotationsUtils.assertMethod(DatapodDetails.class, "getDatapodId");
    AssertAnnotationsUtils.assertMethod(DatapodDetails.class, "getDatapodName");
    AssertAnnotationsUtils.assertMethod(DatapodDetails.class, "getAlertCustomerSysId");
    AssertAnnotationsUtils.assertMethod(DatapodDetails.class, "getCreatedTime");
    AssertAnnotationsUtils.assertMethod(DatapodDetails.class, "getCreatedBy");
  }

  @Override
  @Test
  public void entityTest() {
    // setup
    Entity a = ReflectionUtils.getClassAnnotation(DatapodDetails.class, Entity.class);
    // assert
    Assert.assertEquals("", a.name());
  }

  @Override
  @Test
  public void tableTest() {
    // setup
    Table t = ReflectionUtils.getClassAnnotation(DatapodDetails.class, Table.class);
    // assert
    Assert.assertEquals("DATAPOD_DETAILS", t.name());
  }
}
