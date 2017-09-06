package com.synchronoss.saw.gateway.utils;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Enumeration;

import javax.servlet.http.HttpServletRequest;

import org.apache.http.client.methods.RequestBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HeadersRequestTransformer extends ProxyRequestTransformer {

	Logger logger = LoggerFactory.getLogger(this.getClass());
  @Override
  public RequestBuilder transform(HttpServletRequest request) throws URISyntaxException, IOException {
    RequestBuilder requestBuilder = predecessor.transform(request);
    Enumeration<String> headerNames = request.getHeaderNames();
    while (headerNames.hasMoreElements()) {
      String headerName = headerNames.nextElement();
      String headerValue = request.getHeader(headerName);
      if (headerName.equals("authorization")) {
    	requestBuilder.addHeader(headerName, headerValue);
        logger.info("HeaderValue",headerValue);
      }
    }
    return requestBuilder;
  }
}
