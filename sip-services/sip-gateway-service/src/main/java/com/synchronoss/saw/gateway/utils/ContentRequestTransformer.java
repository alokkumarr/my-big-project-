package com.synchronoss.saw.gateway.utils;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.charset.UnsupportedCharsetException;
import java.util.stream.Collectors;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import org.apache.http.client.methods.RequestBuilder;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.synchronoss.bda.sip.SipValidateSerializerModifier;
import com.synchronoss.bda.sip.exception.SipNotProcessedSipEntityException;


public class ContentRequestTransformer extends ProxyRequestTransformer {

  Logger logger = LoggerFactory.getLogger(this.getClass());

  @Override
  public RequestBuilder transform(HttpServletRequest request)
      throws URISyntaxException, IOException, UnsupportedCharsetException, ServletException {
    RequestBuilder requestBuilder = predecessor.transform(request);
    String requestContent = request.getReader().lines().collect(Collectors.joining(""));
    logger.trace("Request Content: " + requestContent);
    SimpleModule simpleModule = new SimpleModule();
    simpleModule.setSerializerModifier(new SipValidateSerializerModifier());
    ObjectMapper objectMapper = new ObjectMapper();
    objectMapper.registerModule(simpleModule);
    try {
      objectMapper.writeValueAsString(objectMapper.readTree(requestContent));
    } catch (JsonMappingException ex) {
      throw new SipNotProcessedSipEntityException(requestContent + " is not valid.");
    }
    if (!requestContent.isEmpty()) {
      StringEntity entity = new StringEntity(requestContent, ContentType.APPLICATION_JSON);
      requestBuilder.setEntity(entity);
    }
    return requestBuilder;
  }
}
