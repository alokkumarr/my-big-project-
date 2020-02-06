package com.sncr.saw.security.common.util;

import org.junit.Assert;
import org.junit.Test;

/*
 * Single test for the Maven Surefire plug-in to generate test
 * reports.  This is needed to enable the test report reading in the
 * Bamboo plan.  Remove when proper tests have been added to project.
 */
public class SampleTest {
  @Test
  public void testSample() {
    return;
  }

  @Test
  public void testEmailValidation() {
    Assert.assertTrue(SecurityUtils.isEmailValid("abc@abc.cz"));
    Assert.assertTrue(SecurityUtils.isEmailValid("435@coco.foo.cz"));
    Assert.assertTrue(SecurityUtils.isEmailValid("A@something"));
    Assert.assertTrue(SecurityUtils.isEmailValid("A@some-thing.foo"));
    Assert.assertTrue(SecurityUtils.isEmailValid("1@A"));
    Assert.assertFalse(SecurityUtils.isEmailValid("A@some_thing.foo"));
    Assert.assertFalse(SecurityUtils.isEmailValid("@some_thing.foo"));
    Assert.assertFalse(SecurityUtils.isEmailValid("abc@"));
    Assert.assertFalse(SecurityUtils.isEmailValid("abc@."));
    Assert.assertFalse(SecurityUtils.isEmailValid("abc@.foo"));
    Assert.assertFalse(SecurityUtils.isEmailValid("abc@foo."));
    Assert.assertFalse(SecurityUtils.isEmailValid("abc@foo..bar"));
  }
}
