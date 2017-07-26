package com.synchronoss;

import java.io.IOException;
import java.util.Map;
import org.apache.http.HttpHost;
import org.apache.logging.log4j.Logger;
import org.elasticsearch.client.Response;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.common.logging.ESLoggerFactory;
import org.elasticsearch.common.xcontent.XContentParser;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.test.ESTestCase;
import org.junit.AfterClass;
import org.junit.BeforeClass;

public abstract class AbstractITCase extends ESTestCase {
  
  protected static final Logger staticLogger = ESLoggerFactory.getLogger("it");
  protected final static int HTTP_TEST_PORT = 9400;
  protected static RestClient client;

  @BeforeClass
  public void startRestClient() {
      client = RestClient.builder(new HttpHost("localhost", HTTP_TEST_PORT)).build();
      try {
          Response response = client.performRequest("GET", "/");
          Map<String, Object> responseMap = entityAsMap(response);
          assertThat(responseMap, org.hamcrest.Matchers.hasEntry("tagline", "You Know, for Search"));
          staticLogger.info("Integration tests ready to start... Cluster is running.");
      } catch (IOException e) {
          // If we have an exception here, let's ignore the test
          staticLogger.warn("Integration tests are skipped: [{}]", e.getMessage());
          //assumeThat("Integration tests are skipped", e.getMessage(), not(containsString("Connection refused")));
          staticLogger.error("Full error is", e);
          fail("Something wrong is happening. REST Client seemed to raise an exception.");
      }
  }

  @AfterClass
  public void stopRestClient() throws IOException {
      if (client != null) {
          client.close();
          client = null;
      }
      staticLogger.info("Stopping integration tests against an external cluster");
  }
  
  /**
   * Convert the entity from a {@link Response} into a map of maps.
   */
  public Map<String, Object> entityAsMap(Response response) throws IOException {
      XContentType xContentType = XContentType.fromMediaTypeOrFormat(response.getEntity().getContentType().getValue());
      try (XContentParser parser = createParser(xContentType.xContent(), 
          response.getEntity().getContent())) 
      {
          return parser.map();
      }
  }

}
