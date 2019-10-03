package com.synchronoss.bda.sip;

import com.fasterxml.jackson.databind.BeanDescription;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializationConfig;
import com.fasterxml.jackson.databind.ser.BeanSerializerModifier;

/**
 * This class is used to custom bean modifier class<br/>
 * and check for the intrusion.
 * 
 * @author spau0004
 */

public class SipValidateSerializerModifier extends BeanSerializerModifier {
  @SuppressWarnings("unchecked")
  @Override
  public JsonSerializer<?> modifySerializer(SerializationConfig config, BeanDescription beanDesc,
      JsonSerializer<?> serializer) {
    return new SipJsonValidateSerializer((JsonSerializer<Object>) serializer);
  }

}
