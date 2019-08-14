package com.synchronoss.saw.apipull.service;

/* SncrHttpClient is an abstract class defining necessary properties for HTTP Client */

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

public abstract class SncrBaseHttpClient implements BaseHttpClient {

  String host;
  String apiEndPoint;
  Map<String, String> queryParams;
  Map<String, Object> headerParams;
  String url = "";
  CloseableHttpClient client;

  /**
   * Parent class constructor accepts host name, which is a mandatory parameter.
   *
   * @param host
   */
  public SncrBaseHttpClient(String host) {
    this.host = host;

    client = HttpClients.createDefault();

    headerParams = new HashMap<>();
    queryParams = new HashMap<>();
  }

  /**
   * Function used to build a query url
   *
   * @param apiEndPoint - it is a one path communication channel
   * @param queryParams - are the user generated params
   * @return
   */
  protected String generateUrl(String apiEndPoint, Map<String, String> queryParams) {
    if (!CollectionUtils.isEmpty(queryParams)) {
      url += host + "/" + apiEndPoint + "?" + generateQueryParameters(queryParams);
    } else if (!StringUtils.isEmpty(apiEndPoint)) {
      url += host + "/" + apiEndPoint;
    } else {
      url += host;
    }
    return url;
  }

  /**
   * Function validates the hostname
   *
   * @param hostName
   * @return - true if valid else false
   */
  public static boolean isValidHostName(String hostName) {
    boolean ret = true;
    if (hostName == null) {
      return false;
    }
    hostName = hostName.trim();
    if ("".equals(hostName) || hostName.indexOf(" ") != -1) {
      ret = false;
    } else {
      // Use regular expression to verify host name.
      Pattern p = Pattern.compile("[a-zA-Z0-9\\.\\-\\_]+");
      Matcher m = p.matcher(hostName);
      ret = m.matches();

      if (ret) {
        String tmp = hostName;
        if (hostName.length() > 15) {
          tmp = hostName.substring(0, 15);
        }
        // Use another regular expression to verify the first 15 charactor.
        p = Pattern.compile("((.)*[a-zA-Z\\-\\_]+(.)*)+");
        m = p.matcher(tmp);
        ret = m.matches();
      }
    }
    return ret;
  }

  protected String generateQueryParameters(Map<String, String> queryParameters) {
    setQueryParams(queryParameters);
    StringBuffer formattedQueryParam = new StringBuffer();
    Set s = queryParameters.entrySet();
    Iterator itr = s.iterator();
    while (itr.hasNext() == true) {
      formattedQueryParam.append(itr.next().toString()).append("&");
    }

    return (formattedQueryParam.substring(0, formattedQueryParam.length() - 1));
  }

  public void setApiEndPoint(String apiEndPoint) {
    this.apiEndPoint = apiEndPoint;
  }

  public void setQueryParams(Map<String, String> queryParams) {
    this.queryParams = queryParams;
  }

  public void setQueryParam(String key, String value) {
    if (this.queryParams == null) {
      this.queryParams = new HashMap<>();
    }

    this.queryParams.put(key, value);
  }

  public void setHeaderParams(Map<String, Object> headerParams) {
    this.headerParams = headerParams;
  }

  public void setHeaderParams(String key, Object value) {
    if (this.headerParams == null) {
      this.headerParams = new HashMap<>();
    }

    this.headerParams.put(key, value);
  }

  /**
   * Function iterates takes key,value pairs and updates the header params
   *
   * @param key - A valid key
   * @param value - Value
   */
  public void setHeaderParam(String key, Object value) {
    if (this.headerParams == null) {
      this.headerParams = new HashMap<>();
    }

    this.headerParams.put(key, value);
  }
}
