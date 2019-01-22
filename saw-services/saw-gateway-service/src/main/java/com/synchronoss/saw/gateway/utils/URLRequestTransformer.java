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
  /**
   * Request path to REST API definition in OpenAPI format.
   */
  public static final String API_DOCS_PATH = "/v2/api-docs";

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
    String uri = requestURI;
    /* If accessing REST API definition, remove external path prefix
     * before forwarding as it is at the root path of the upstream
     * service */
    if (uri.endsWith(API_DOCS_PATH)) {
      uri = API_DOCS_PATH;
    }
    return endpoint.get().getLocation() + uri;
  }
}
