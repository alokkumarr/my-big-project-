package com.synchronoss.saw.gateway.utils;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.charset.UnsupportedCharsetException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import org.apache.http.client.methods.RequestBuilder;

public abstract class ProxyRequestTransformer {

  protected ProxyRequestTransformer predecessor;

  public abstract RequestBuilder transform(HttpServletRequest request) throws URISyntaxException, IOException, UnsupportedCharsetException, ServletException;

  public void setPredecessor(ProxyRequestTransformer transformer) {
    this.predecessor = transformer;
  }
}
