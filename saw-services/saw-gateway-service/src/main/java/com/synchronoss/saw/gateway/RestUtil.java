package com.synchronoss.saw.gateway;

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
import org.apache.http.ssl.SSLContextBuilder;
import org.springframework.http.HttpStatus;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

public class RestUtil {

  public static boolean isError(HttpStatus status) {
    HttpStatus.Series series = status.series();
    return (HttpStatus.Series.CLIENT_ERROR.equals(series)
        || HttpStatus.Series.SERVER_ERROR.equals(series));
  }

  public static RestTemplate restTemplate(String trustStore, String trustPassword, String keyStore,
      String keyPassword) throws Exception {

    SSLContext sslContext = SSLContextBuilder.create()
        .loadKeyMaterial(new File(keyStore), keyPassword.toCharArray(), keyPassword.toCharArray())
        .loadTrustMaterial(new File(trustStore), trustPassword.toCharArray()).build();

    HttpClient client = HttpClients.custom().setSSLContext(sslContext).build();
    HttpComponentsClientHttpRequestFactory factory =
        new HttpComponentsClientHttpRequestFactory(client);
    RestTemplate restTemplate = new RestTemplate(factory);
    return restTemplate;
  }

  private static CloseableHttpClient client;

  public static HttpClient getHttpsClient(String keyStore, String keyPassword, String trustStore,
      String trustPassword) throws Exception {
    if (client != null) {
      return client;
    }

    SSLContext sslcontext = getSSLContext(keyStore, keyPassword, trustStore, trustPassword);
    SSLConnectionSocketFactory factory =
        new SSLConnectionSocketFactory(sslcontext, new NoopHostnameVerifier());
    PoolingHttpClientConnectionManager cm = new PoolingHttpClientConnectionManager();
    client = HttpClients.custom().setConnectionManager(cm).setSSLSocketFactory(factory).build();

    return client;
  }

  private static SSLContext getSSLContext(String keyStore, String keyPassword, String trustStore,
      String trustPassword) throws KeyStoreException, NoSuchAlgorithmException,
      CertificateException, IOException, KeyManagementException, UnrecoverableKeyException {
    SSLContext sslContext = SSLContextBuilder.create()
        .loadTrustMaterial(new File(trustStore), trustPassword.toCharArray()).build();
    return sslContext;
  }
}
