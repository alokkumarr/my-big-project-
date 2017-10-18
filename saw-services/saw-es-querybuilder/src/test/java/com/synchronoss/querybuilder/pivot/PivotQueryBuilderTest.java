package com.synchronoss.querybuilder.pivot;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.InetAddress;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.TimeUnit;
import org.apache.commons.io.IOUtils;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.Client;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.elasticsearch.rest.RestStatus;
import org.elasticsearch.transport.client.PreBuiltTransportClient;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import com.synchronoss.querybuilder.EntityType;
import com.synchronoss.querybuilder.SAWElasticSearchQueryBuilder;
import pl.allegro.tech.embeddedelasticsearch.EmbeddedElastic;
import pl.allegro.tech.embeddedelasticsearch.IndexSettings;
import pl.allegro.tech.embeddedelasticsearch.PopularProperties;

public class PivotQueryBuilderTest {

  private  EmbeddedElastic embeddedElastic = null;
  private  URL esSettingsResource = null;
  private  URL  pivotMappingResource = null;
  private  Client client = null;
  private  final String INDEX_NAME = "mct_index_today";
  private final String  TYPE_NAME = "content_type"; 
  private final String CLUSTER_NAME = "test_cluster";
  private  URL schemaResource = null;

  
 @Before
  public void resourceInitialized() throws IOException, InterruptedException{
    ClassLoader classLoader = getClass().getClassLoader();
    pivotMappingResource = classLoader.getResource("content_mappings.json");
    esSettingsResource = classLoader.getResource("es_index_settings.json");
    InputStream mappingStream = new FileInputStream(pivotMappingResource.getFile());
    InputStream settingStream = new FileInputStream(esSettingsResource.getFile());
    schemaResource = classLoader.getResource("schema/pivot_querybuilder_schema.json");
    System.setProperty("schema.pivot", schemaResource.getPath());
   
    embeddedElastic = EmbeddedElastic.builder()
        .withElasticVersion("5.4.0")
        .withSetting(PopularProperties.TRANSPORT_TCP_PORT, 9350)
        .withSetting(PopularProperties.CLUSTER_NAME, CLUSTER_NAME)
        .withIndex(INDEX_NAME, IndexSettings.builder()
        .withType(TYPE_NAME, mappingStream)
         .withSettings(settingStream)
        .build()).withStartTimeout(1, TimeUnit.MINUTES)
        .build().start();    
    Settings settings = Settings.builder().put("cluster.name", CLUSTER_NAME).build();
    client = new PreBuiltTransportClient(settings)
        .addTransportAddress(new InetSocketTransportAddress(InetAddress.getByName("localhost"), 9350));
  }
  
  
  @After
  public void resourceReleased() {
    try{
    if (client !=null) {
      client.close();
    }
    if (embeddedElastic !=null){
      embeddedElastic.stop();
    }
    }
    finally{
      client.close();
    }
  }
  
  @Test
  public void queryAllCriteria() {
    ClassLoader classLoader = getClass().getClassLoader();
    URL inputFile = classLoader.getResource("queries/pivot_type_all_data.json");
    InputStream inputStream;
    String jsonString = null;
    try {
      inputStream = new FileInputStream(inputFile.getFile());
      jsonString = IOUtils.toString(inputStream, StandardCharsets.UTF_8.name());
    } catch (IOException e) {
      assertThat(e.getMessage(), is("IOException"));    
    }
    SAWElasticSearchQueryBuilder sawElasticSearchQueryBuilder = new SAWElasticSearchQueryBuilder();
    SearchResponse response = client.prepareSearch(INDEX_NAME).setTypes(TYPE_NAME)
    .setQuery(sawElasticSearchQueryBuilder.getSearchSourceBuilder(EntityType.PIVOT, jsonString).query()).execute().actionGet();
    Assert.assertTrue(response.status().equals(RestStatus.OK));
  }
  
  @Test
  public void queryWith3RowFields() {
    ClassLoader classLoader = getClass().getClassLoader();
    URL inputFile = classLoader.getResource("queries/pivot_type_3_RowFields_data.json");
    InputStream inputStream;
    String jsonString = null;
    try {
      inputStream = new FileInputStream(inputFile.getFile());
      jsonString = IOUtils.toString(inputStream, StandardCharsets.UTF_8.name());
    } catch (IOException e) {
      assertThat(e.getMessage(), is("IOException"));    
    }
    SAWElasticSearchQueryBuilder sawElasticSearchQueryBuilder = new SAWElasticSearchQueryBuilder();
    SearchResponse response = client.prepareSearch(INDEX_NAME).setTypes(TYPE_NAME)
    .setQuery(sawElasticSearchQueryBuilder.getSearchSourceBuilder(EntityType.PIVOT, jsonString).query()).execute().actionGet();
    Assert.assertTrue(response.status().equals(RestStatus.OK));
  }
  
  @Test
  public void queryWith3RowFieldsDataSecurityKey() {
    ClassLoader classLoader = getClass().getClassLoader();
    URL inputFile = classLoader.getResource("queries/pivot_type_3_RowFields_data.json");
    String dataSecurityKey = "{\"dataSecurityKey\":[{\"name\":\"ORDER_STATE.raw\",\"values\":[\"KA\",\"Alabama\",\"Hawaii\"]},{\"name\":\"TRANSACTION_ID\",\"values\":[\"015cd74a-08dc-494f-8b71-f1cbd546fc31\"]}]}";
    InputStream inputStream;
    String jsonString = null;
    try {
      inputStream = new FileInputStream(inputFile.getFile());
      jsonString = IOUtils.toString(inputStream, StandardCharsets.UTF_8.name());
    } catch (IOException e) {
      assertThat(e.getMessage(), is("IOException"));    
    }
    SAWElasticSearchQueryBuilder sawElasticSearchQueryBuilder = new SAWElasticSearchQueryBuilder();
    SearchResponse response = client.prepareSearch(INDEX_NAME).setTypes(TYPE_NAME)
    .setQuery(sawElasticSearchQueryBuilder.getSearchSourceBuilder(EntityType.PIVOT, jsonString,dataSecurityKey).query()).execute().actionGet();
    Assert.assertTrue(response.status().equals(RestStatus.OK));
  }

  }
  
  
  

