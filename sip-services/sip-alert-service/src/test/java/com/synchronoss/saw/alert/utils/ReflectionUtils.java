package com.synchronoss.saw.alert.utils;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

public class ReflectionUtils {

  /**
   * Get method Annotation based on class.
   *
   * @param c Class
   * @param methodName Method Name
   * @param annotation Annotation
   * @param <T> Type
   * @return T type
   */
  public static <T extends Annotation> T getMethodAnnotation(
      Class<?> c, String methodName, Class<T> annotation) {
    try {
      Method m = c.getDeclaredMethod(methodName);
      return (T) m.getAnnotation(annotation);
    } catch (NoSuchMethodException nsme) {
      throw new RuntimeException(nsme);
    }
  }

  /**
   * Get field Annotation based on class.
   *
   * @param c Class
   * @param fieldName Field Name
   * @param annotation Annotation
   * @param <T> Type
   * @return T type
   */
  public static <T extends Annotation> T getFieldAnnotation(
      Class<?> c, String fieldName, Class<T> annotation) {
    try {
      Field f = c.getDeclaredField(fieldName);
      return (T) f.getAnnotation(annotation);
    } catch (NoSuchFieldException nsme) {
      throw new RuntimeException(nsme);
    }
  }

  public static <T extends Annotation> T getClassAnnotation(Class<?> c, Class<T> annotation) {
    return (T) c.getAnnotation(annotation);
  }
}
