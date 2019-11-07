package com.synchronoss.saw.gateway.utils;


import java.net.URI;
import java.net.URISyntaxException;
import java.util.NoSuchElementException;
import java.util.Optional;
import javax.servlet.http.HttpServletRequest;
import org.apache.http.client.methods.RequestBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
  Logger logger = LoggerFactory.getLogger(this.getClass());

  public URLRequestTransformer(ApiGatewayProperties apiGatewayProperties) {
    this.apiGatewayProperties = apiGatewayProperties;
  }

  @Override
  public RequestBuilder transform(HttpServletRequest request) throws URISyntaxException {
    String requestURI = request.getRequestURI();
    logger.trace("requestURI start here: " + requestURI);
    URI uri;
    if (request.getQueryString() != null && !request.getQueryString().isEmpty()) {
      uri = new URI(getServiceUrl(requestURI, request) + "?" + request.getQueryString());
    } else {
      uri = new URI(getServiceUrl(requestURI, request));
    }
    logger.trace("requestURI ends here: " + requestURI);
    RequestBuilder rb = RequestBuilder.create(request.getMethod());
    rb.setUri(uri);
    return rb;
  }

  @ExceptionHandler(value = NoHandlerFoundException.class)
  private String getServiceUrl(String requestUri, HttpServletRequest httpServletRequest) {
    logger.trace("Request Url: " + requestUri);
    Optional<Endpoint> endpoint =
        apiGatewayProperties.getEndpoints().stream()
            .filter(e -> requestUri.matches(e.getPath())
                && e.getMethod() == RequestMethod.valueOf(httpServletRequest.getMethod()))
            .findFirst();
    String uri = requestUri;
    /*
     * If accessing REST API definition, remove external path prefix before forwarding as it is at
     * the root path of the upstream service
     */
    if (uri.endsWith(API_DOCS_PATH)) {
      uri = API_DOCS_PATH;
    }
    String endPoint = null;
    try {
      endPoint = endpoint.get().getLocation() + uri;
    } catch (NullPointerException ex) {
      throw new NullPointerException(ex.getMessage());
    } catch (NoSuchElementException ex) {
      throw new NoSuchElementException(ex.getMessage());
    }
    logger.trace("Destination Url: " + endPoint);
    return endPoint;
  }
}
