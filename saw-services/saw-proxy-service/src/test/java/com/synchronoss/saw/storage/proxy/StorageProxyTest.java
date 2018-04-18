package com.synchronoss.saw.storage.proxy;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static org.hamcrest.CoreMatchers.is;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.InetAddress;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.TimeUnit;
import org.apache.commons.io.IOUtils;
import org.elasticsearch.client.Client;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.elasticsearch.transport.client.PreBuiltTransportClient;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.synchronoss.saw.storage.proxy.controller.StorageProxyController;
import com.synchronoss.saw.storage.proxy.model.StorageProxy;
import com.synchronoss.saw.storage.proxy.service.StorageProxyService;
import com.synchronoss.saw.storage.proxy.service.StorageProxyServiceImpl;
import pl.allegro.tech.embeddedelasticsearch.EmbeddedElastic;
import pl.allegro.tech.embeddedelasticsearch.IndexSettings;
import pl.allegro.tech.embeddedelasticsearch.PopularProperties;

@RunWith(SpringRunner.class)
@TestPropertySource(locations = "application-test.properties")
@WebMvcTest(value = StorageProxyController.class, secure = false)
public class StorageProxyTest {

  private EmbeddedElastic embeddedElastic = null;
  private URL esSettingsResource = null;
  private URL indexMappingResource = null;
  private Client client = null;
  private final String INDEX_NAME = "mct_index_today";
  private final String TYPE_NAME = "content_type";
  private final String CLUSTER_NAME = "test_cluster";
  private static final Logger logger = LoggerFactory.getLogger(StorageProxyTest.class);

  @Autowired
  private MockMvc mockMvc;
  
  @Autowired
  private StorageProxyService storageProxy;

  @MockBean
  private StorageProxyServiceImpl  storageProxyImpl;
 

  @SuppressWarnings("resource")
  @Before
  public void resourceInitialized() throws IOException, InterruptedException {
    ClassLoader classLoader = getClass().getClassLoader();
    indexMappingResource = classLoader.getResource("content_mappings.json");
    esSettingsResource = classLoader.getResource("es_index_settings.json");
    InputStream mappingStream = new FileInputStream(indexMappingResource.getFile());
    InputStream settingStream = new FileInputStream(esSettingsResource.getFile());
    embeddedElastic = EmbeddedElastic.builder().withElasticVersion("5.4.0")
        .withSetting(PopularProperties.TRANSPORT_TCP_PORT, 9350)
        .withSetting(PopularProperties.HTTP_PORT, 9351)
        .withSetting(PopularProperties.CLUSTER_NAME, CLUSTER_NAME)
        .withIndex(INDEX_NAME, IndexSettings.builder().withType(TYPE_NAME, mappingStream)
            .withSettings(settingStream).build())
        .withStartTimeout(1, TimeUnit.MINUTES).build().start();
    Settings settings = Settings.builder().put("cluster.name", CLUSTER_NAME).build();
    client = new PreBuiltTransportClient(settings).addTransportAddress(
        new InetSocketTransportAddress(InetAddress.getByName("localhost"), 9350));
  }

  @After
  public void resourceReleased() {
    try {
      if (client != null) {
        client.close();
      }
      if (embeddedElastic != null) {
        embeddedElastic.stop();
      }
    } finally {
      client.close();
    }
  }

 @Test
  public void proxyServiceTest() throws Exception {
    ClassLoader classLoader = getClass().getClassLoader();
    URL inputFile = classLoader.getResource("proxy-request/action-search.json");
    InputStream inputStream;
    String jsonString = null;
    try {
      inputStream = new FileInputStream(inputFile.getFile());
      jsonString = IOUtils.toString(inputStream, StandardCharsets.UTF_8.name());
      ObjectMapper objectMapper = new ObjectMapper();
      objectMapper.configure(JsonParser.Feature.ALLOW_UNQUOTED_FIELD_NAMES, true);
      objectMapper.enable(DeserializationFeature.FAIL_ON_READING_DUP_TREE_KEY);
      StorageProxy requestNode = objectMapper.readValue(jsonString, StorageProxy.class);
      StorageProxy responseNode = storageProxy.execute(requestNode);
      logger.debug(objectMapper.writeValueAsString(responseNode));
    } catch (IOException e) {
      assertThat(e.getMessage(), is("IOException"));
    }
  }
  
  
  @Test
  public void proxyServiceRESTTest() throws Exception {
    ClassLoader classLoader = getClass().getClassLoader();
    URL inputFile = classLoader.getResource("proxy-request/action-search.json");
    InputStream inputStream;
    String jsonString = null;
    inputStream = new FileInputStream(inputFile.getFile());
    jsonString = IOUtils.toString(inputStream, StandardCharsets.UTF_8.name());
    ObjectMapper objectMapper = new ObjectMapper();
    objectMapper.configure(JsonParser.Feature.ALLOW_UNQUOTED_FIELD_NAMES, true);
    objectMapper.enable(DeserializationFeature.FAIL_ON_READING_DUP_TREE_KEY);
    StorageProxy requestNode = objectMapper.readValue(jsonString, StorageProxy.class);
    Mockito.when(storageProxyImpl.execute(Mockito.any(StorageProxy.class))).
          thenReturn(requestNode);
      RequestBuilder requestBuilder = MockMvcRequestBuilders
              .post("/internal/proxy/storage/")
              .accept(MediaType.APPLICATION_JSON_UTF8_VALUE)
              .content(jsonString)
              .contentType(MediaType.APPLICATION_JSON_UTF8_VALUE)
              .header(HttpHeaders.LOCATION, "http://localhost/internal/proxy/storage");
      MvcResult result = mockMvc.perform(requestBuilder).andReturn();
      MockHttpServletResponse response = result.getResponse();
      assertEquals(HttpStatus.ACCEPTED.value(), response.getStatus());
  } 

}


