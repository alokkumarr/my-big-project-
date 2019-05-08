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


public class ContentRequestTransformer extends ProxyRequestTransformer {

  @Override
  public RequestBuilder transform(HttpServletRequest request) 
		  throws URISyntaxException, IOException, UnsupportedCharsetException, ServletException {
    RequestBuilder requestBuilder = predecessor.transform(request);
    
      String requestContent = request.getReader().lines().collect(Collectors.joining(""));
    if (!requestContent.isEmpty()) {
      StringEntity entity = new StringEntity(requestContent, ContentType.APPLICATION_JSON);
      requestBuilder.setEntity(entity);
    }
    return requestBuilder;
  }
}
