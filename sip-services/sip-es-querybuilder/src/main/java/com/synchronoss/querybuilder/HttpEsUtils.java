package com.synchronoss.querybuilder;

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
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.ssl.SSLContextBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HttpEsUtils {

  private static final Logger logger = LoggerFactory.getLogger(HttpEsUtils.class);



  /**
   * creating a https client.
   */
  public HttpClient getHttpClient(String trustStore, String trustPassWord, String keyStore, String keyPassword,
      boolean sslEnabled) {
    logger.trace("HttpEsUtils getHttpClient trustStore: " + trustStore);
    logger.trace("HttpEsUtils getHttpClient keyStore: " + keyStore);
    logger.trace("HttpEsUtils getHttpClient keyStorePassword: " + keyPassword);
    logger.trace("HttpEsUtils getHttpClient trustStorePassword: " + trustStore);
    HttpClient client = null;
    PoolingHttpClientConnectionManager cm = new PoolingHttpClientConnectionManager();
    if (sslEnabled) {
      SSLContext sslcontext;
      try {
        sslcontext = getSsLContext(keyStore, keyPassword, trustStore, trustPassWord);
        SSLConnectionSocketFactory factory =
            new SSLConnectionSocketFactory(sslcontext, new NoopHostnameVerifier());
        client = HttpClients.custom().setSSLSocketFactory(factory).build();
      } catch (KeyManagementException | UnrecoverableKeyException | KeyStoreException
          | NoSuchAlgorithmException | CertificateException | IOException e) {
        logger.error("Exception occured: " + e);      }
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
    logger.trace("HttpEsUtils getSsLContext trustStore: " + trustStore);
    logger.trace("HttpEsUtils getSsLContext keyStore: " + keyStore);
    logger.trace("HttpEsUtils getSsLContext keyStorePassword: " + keyPassword);
    logger.trace("HttpEsUtils getSsLContext trustStorePassword: " + trustStore);

    SSLContext sslContext = SSLContextBuilder.create()
        .loadKeyMaterial(new File(keyStore), keyPassword.toCharArray(), keyPassword.toCharArray())
        .loadTrustMaterial(new File(trustStore), trustPassword.toCharArray()).build();
    return sslContext;
  }

}
