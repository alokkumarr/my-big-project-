package com.synchronoss.sip.utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * This generic class can be used as to access internal services.
 *
 * @author spau0004
 */
@Service
public class SipInternalServiceClient {

  private static final Logger logger = LoggerFactory.getLogger(SipInternalServiceClient.class);
  private String dataLocation;

  @Autowired
  private RestUtil restUtil;
  
  
  public String getDataLocation() {
    return dataLocation;
  }

  public void setDataLocation(String dataLocation) {
    this.dataLocation = dataLocation;
  }

  /**
   * This method will be used to access the semantic service to get the details of semantic a
   * particular semantic node.
   * @throws Exception  exception.
   */
  public Object retrieveObject(Object object, String url) throws Exception {
    logger.trace("request from retrieveObject :" + object);
    Object node = null;
    ObjectMapper mapper = new ObjectMapper();
    HttpClient client = restUtil.getHttpClient();
    HttpGet request = new HttpGet(url);
    HttpResponse response = client.execute(request);
    BufferedReader rd =
        new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
    StringBuffer result = new StringBuffer();
    String line = "";
    while ((line = rd.readLine()) != null) {
      result.append(line);
    }
    node = mapper.readValue(result.toString(), object.getClass());
    ObjectNode rootNode = (ObjectNode) node;
    if (rootNode.get("repository") != null) {
      setDataLocation(rootNode.get("repository").get("physicalLocation").asText());
    }
    logger.trace("response object :" + mapper.writeValueAsString(node));
    return node;
  }
}
