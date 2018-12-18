package com.synchronoss.saw.gateway.utils;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.charset.UnsupportedCharsetException;
import java.util.Enumeration;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;

import org.apache.http.client.methods.RequestBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HeadersRequestTransformer extends ProxyRequestTransformer {

  private UserRelatedMetaData userRelatedMetaData =  null;
	Logger logger = LoggerFactory.getLogger(this.getClass());
  @Override
  public RequestBuilder transform(HttpServletRequest request) throws URISyntaxException, IOException, UnsupportedCharsetException, ServletException {
    RequestBuilder requestBuilder = predecessor.transform(request);
    Enumeration<String> headerNames = request.getHeaderNames();
    while (headerNames.hasMoreElements()) {
      String headerName = headerNames.nextElement();
      String headerValue = request.getHeader(headerName);
      if (headerName.equals("authorization")) {
    	requestBuilder.addHeader(headerName, headerValue);
    	requestBuilder.addHeader("x-customerCode", userRelatedMetaData.getCustCode());
    	requestBuilder.addHeader("x-roleCode", userRelatedMetaData.getRoleCode());
    	requestBuilder.addHeader("x-roleType", userRelatedMetaData.getRoleType());
    	requestBuilder.addHeader("x-userName", userRelatedMetaData.getUserName());
        logger.debug("HeaderValue",headerValue);
      }
    }
    return requestBuilder;
  }
  public UserRelatedMetaData getUserRelatedMetaData() {
    return userRelatedMetaData;
  }
  public void setUserRelatedMetaData(UserRelatedMetaData userRelatedMetaData) {
    this.userRelatedMetaData = userRelatedMetaData;
  }
  
  
}
