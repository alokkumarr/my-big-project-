package com.synchronoss.saw.export.controller;

import com.synchronoss.saw.export.ApiExportProperties;
import org.apache.http.client.HttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.PostConstruct;


/**
 * @author pman0003
 *
 */
@RestController
public class DataExportController {

  Logger logger = LoggerFactory.getLogger(this.getClass());

  @Autowired
  private ApiExportProperties apiExportProperties;
  
  @Value("${security.service.host}")
  private String apiExportOtherProperties;

  private HttpClient httpClient;

  @PostConstruct
  public void init() {
    PoolingHttpClientConnectionManager cm = new PoolingHttpClientConnectionManager();
    httpClient = HttpClients.custom()
            .setConnectionManager(cm)
            .build();
  }
}
