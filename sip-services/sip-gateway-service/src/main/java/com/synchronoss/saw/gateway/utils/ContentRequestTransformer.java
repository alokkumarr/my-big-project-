package com.synchronoss.saw.gateway.utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.synchronoss.bda.sip.exception.SipNotProcessedSipEntityException;
import com.synchronoss.sip.utils.RestUtil;
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


public class ContentRequestTransformer extends ProxyRequestTransformer {

  Logger logger = LoggerFactory.getLogger(this.getClass());

  @Override
  public RequestBuilder transform(HttpServletRequest request)
      throws URISyntaxException, IOException, UnsupportedCharsetException, ServletException {
    RequestBuilder requestBuilder = predecessor.transform(request);
    String requestContent = request.getReader().lines().collect(Collectors.joining(""));
    logger.trace("Request Content: " + requestContent);
    ObjectMapper objectMapper = new ObjectMapper();
    try {
      RestUtil.validateNodeValue(objectMapper.readTree(requestContent));
    } catch (IOException ex) {
      throw new SipNotProcessedSipEntityException(requestContent + " is not valid.");
    }
    if (!requestContent.isEmpty()) {
      StringEntity entity = new StringEntity(requestContent, ContentType.APPLICATION_JSON);
      requestBuilder.setEntity(entity);
    }
    return requestBuilder;
  }
}
