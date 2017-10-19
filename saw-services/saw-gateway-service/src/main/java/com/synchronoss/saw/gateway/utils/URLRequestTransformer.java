package com.synchronoss.saw.gateway.utils;


import java.net.URI;
import java.net.URISyntaxException;
import java.util.Optional;

import javax.servlet.http.HttpServletRequest;

import org.apache.http.client.methods.RequestBuilder;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.NoHandlerFoundException;

import com.synchronoss.saw.gateway.ApiGatewayProperties;
import com.synchronoss.saw.gateway.ApiGatewayProperties.Endpoint;

public class URLRequestTransformer extends ProxyRequestTransformer {

  private ApiGatewayProperties apiGatewayProperties;

  public URLRequestTransformer(ApiGatewayProperties apiGatewayProperties) {
    this.apiGatewayProperties = apiGatewayProperties;
  }

  @Override
  public RequestBuilder transform(HttpServletRequest request) throws URISyntaxException {
    String requestURI = request.getRequestURI();
    URI uri;
    if (request.getQueryString() != null && !request.getQueryString().isEmpty()) {
      uri = new URI(getServiceUrl(requestURI, request) + "?" + request.getQueryString());
    } else {
      uri = new URI(getServiceUrl(requestURI, request));
    }

    RequestBuilder rb = RequestBuilder.create(request.getMethod());
    rb.setUri(uri);
    return rb;
  }
  
  @ExceptionHandler(value=NoHandlerFoundException.class)
  private String getServiceUrl(String requestURI, HttpServletRequest httpServletRequest)  {
    Optional<Endpoint> endpoint =
            apiGatewayProperties.getEndpoints().stream()
                    .filter(e ->requestURI.matches(e.getPath()) && e.getMethod() == RequestMethod.valueOf(httpServletRequest.getMethod())
                    ).findFirst();
    return endpoint.get().getLocation() + requestURI;
  }
}
