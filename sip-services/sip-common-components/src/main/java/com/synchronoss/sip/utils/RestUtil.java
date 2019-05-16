package com.synchronoss.sip.utils;

import java.io.File;
import java.io.IOException;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import javax.net.ssl.SSLContext;
import org.apache.http.client.HttpClient;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.impl.nio.client.CloseableHttpAsyncClient;
import org.apache.http.impl.nio.client.HttpAsyncClients;
import org.apache.http.ssl.SSLContextBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.client.AsyncClientHttpRequestFactory;
import org.springframework.http.client.HttpComponentsAsyncClientHttpRequestFactory;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.client.AsyncRestTemplate;
import org.springframework.web.client.RestTemplate;

@SuppressWarnings("deprecation")
@Service
public class RestUtil {

  private static final Logger logger = LoggerFactory.getLogger(RestUtil.class);

  @Value("${sip.trust.store:}")
  private String trustStore;

  @Value("${sip.trust.password:}")
  private String trustStorePassword;

  @Value("${sip.key.store:}")
  private String keyStore;

  @Value("${sip.key.password:}")
  private String keyStorePassword;

  @Value("${sip.ssl.enable}")
  private Boolean sipSslEnable;

  private CloseableHttpClient client;

  /**
   * creating rest template using SSL connection.
   */
  public RestTemplate restTemplate() {
    logger.trace("restTemplate trustStore: " + trustStore);
    logger.trace("restTemplate keyStore: " + keyStore);
    logger.trace("restTemplate keyStorePassword: " + keyStorePassword);
    logger.trace("restTemplate trustStorePassword: " + trustStore);
    RestTemplate restTemplate = null;
    if (sipSslEnable) {
      SSLContext sslContext = null;
      try {
        sslContext = SSLContextBuilder.create()
            .loadKeyMaterial(new File(keyStore), keyStorePassword.toCharArray(),
                keyStorePassword.toCharArray())
            .loadTrustMaterial(new File(trustStore), trustStorePassword.toCharArray()).build();
      } catch (Exception e) {
        logger.error("Exception :" + e);
      }
      HttpClient client = HttpClients.custom().setSSLContext(sslContext)
          .setSSLHostnameVerifier(new NoopHostnameVerifier()).build();
      HttpComponentsClientHttpRequestFactory factory =
          new HttpComponentsClientHttpRequestFactory(client);
      restTemplate = new RestTemplate(factory);
    } else {
      restTemplate = new RestTemplate();
    }
    return restTemplate;
  }

  /**
   * creating rest template using SSL connection.
   */
  public RestTemplate restTemplate(String keyStore, String keyPassword, String trustStore,
      String trustPassword) {
    logger.trace("restTemplate with parameter trustStore: " + trustStore);
    logger.trace("restTemplate with parameter keyStore: " + keyStore);
    logger.trace("restTemplate with parameter keyStorePassword: " + keyPassword);
    logger.trace("restTemplate with parameter trustStorePassword: " + trustPassword);
    RestTemplate restTemplate = null;
    if (sipSslEnable) {
      SSLContext sslContext = null;
      try {
        sslContext = SSLContextBuilder.create()
            .loadKeyMaterial(new File(keyStore), keyPassword.toCharArray(),
                keyPassword.toCharArray())
            .loadTrustMaterial(new File(trustStore), trustPassword.toCharArray()).build();
      } catch (Exception e) {
        logger.error("Exception :" + e);
      }
      HttpClient client = HttpClients.custom().setSSLContext(sslContext)
          .setSSLHostnameVerifier(new NoopHostnameVerifier()).build();
      HttpComponentsClientHttpRequestFactory factory =
          new HttpComponentsClientHttpRequestFactory(client);
      restTemplate = new RestTemplate(factory);
    } else {
      restTemplate = new RestTemplate();
    }
    return restTemplate;
  }
  
  
  /**
   * creating async rest template using SSL connection. TODO: This method should be changed when
   * AsyncRestTemplate changes to WebClient
   */
  public AsyncRestTemplate asyncRestTemplate() {
    logger.trace("asyncRestTemplate trustStore: " + trustStore);
    logger.trace("asyncRestTemplate keyStore: " + keyStore);
    logger.trace("asyncRestTemplate keyStorePassword: " + keyStorePassword);
    logger.trace("asyncRestTemplate trustStorePassword: " + trustStore);

    AsyncRestTemplate restTemplate = null;
    if (sipSslEnable) {
      SSLContext sslContext = null;
      try {
        sslContext = SSLContextBuilder.create()
            .loadKeyMaterial(new File(keyStore), keyStorePassword.toCharArray(),
                keyStorePassword.toCharArray())
            .loadTrustMaterial(new File(trustStore), trustStorePassword.toCharArray()).build();
      } catch (Exception e) {
        logger.error("Exception :" + e);
      }

      CloseableHttpAsyncClient httpclient = HttpAsyncClients.custom()
          .setSSLHostnameVerifier(new NoopHostnameVerifier()).setSSLContext(sslContext).build();
      AsyncClientHttpRequestFactory reqFactory =
          new HttpComponentsAsyncClientHttpRequestFactory(httpclient);
      restTemplate = new AsyncRestTemplate(reqFactory);
    } else {
      restTemplate = new AsyncRestTemplate();
    }
    return restTemplate;
  }


  /**
   * creating a https client.
   */
  public HttpClient getHttpClient() throws Exception {
    logger.trace("getHttpClient trustStore: " + trustStore);
    logger.trace("getHttpClient keyStore: " + keyStore);
    logger.trace("getHttpClient keyStorePassword: " + keyStorePassword);
    logger.trace("getHttpClient trustStorePassword: " + trustStore);

    if (client != null) {
      return client;
    }
    PoolingHttpClientConnectionManager cm = new PoolingHttpClientConnectionManager();
    if (sipSslEnable) {
      SSLContext sslcontext =
          getSsLContext(keyStore, keyStorePassword, trustStore, trustStorePassword);
      SSLConnectionSocketFactory factory =
          new SSLConnectionSocketFactory(sslcontext, new NoopHostnameVerifier());
      client = HttpClients.custom().setSSLSocketFactory(factory).build();
    } else {
      client = HttpClients.custom().setConnectionManager(cm).build();
    }
    return client;
  }

  /**
   * ssl context using store passcode.
   */
  private SSLContext getSsLContext(String keyStore, String keyPassword, String trustStore,
      String trustPassword) throws KeyStoreException, NoSuchAlgorithmException,
      CertificateException, IOException, KeyManagementException, UnrecoverableKeyException {
    logger.trace("getSsLContext trustStore: " + trustStore);
    logger.trace("getSsLContext keyStore: " + keyStore);
    logger.trace("getSsLContext keyStorePassword: " + keyPassword);
    logger.trace("getSsLContext trustStorePassword: " + trustStore);

    SSLContext sslContext = SSLContextBuilder.create()
        .loadKeyMaterial(new File(keyStore), keyPassword.toCharArray(), keyPassword.toCharArray())
        .loadTrustMaterial(new File(trustStore), trustPassword.toCharArray()).build();
    return sslContext;
  }

}
