package com.synchronoss.querybuilder.chart;

import static java.util.Collections.emptyMap;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.TimeUnit;
import org.apache.commons.io.IOUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.nio.entity.NStringEntity;
import org.elasticsearch.client.Response;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestClientBuilder;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import com.synchronoss.SAWRetryTestCasesRunner;
import com.synchronoss.querybuilder.EntityType;
import com.synchronoss.querybuilder.SAWElasticSearchQueryBuilder;
import pl.allegro.tech.embeddedelasticsearch.EmbeddedElastic;
import pl.allegro.tech.embeddedelasticsearch.IndexSettings;
import pl.allegro.tech.embeddedelasticsearch.PopularProperties;

@RunWith(SAWRetryTestCasesRunner.class)
@Ignore("SIP-4852 --> SIP-5024, it will be moved to integration test cases in future sprint")
public class ChartQueryBuilderTestChangeLater {

  private  EmbeddedElastic embeddedElastic = null;
  private  URL esSettingsResource = null;
  private  URL  chartMappingResource = null;
  private  RestClient client = null;
  private  final String INDEX_NAME = "mbt_index_today";
  private final String  TYPE_NAME = "mbt_record"; 
  private final String CLUSTER_NAME = "test_cluster";
  private  URL schemaResource = null;

  
 @Before
  public void resourceInitialized() throws IOException, InterruptedException{
    ClassLoader classLoader = getClass().getClassLoader();
    chartMappingResource = classLoader.getResource("mbt_mappings.json");
    esSettingsResource = classLoader.getResource("es_index_settings.json");
    InputStream mappingStream = new FileInputStream(chartMappingResource.getFile());
    InputStream settingStream = new FileInputStream(esSettingsResource.getFile());
    schemaResource = classLoader.getResource("schema/chart_querybuilder_schema.json");
    System.setProperty("schema.chart", schemaResource.getPath());
   
    embeddedElastic = EmbeddedElastic.builder()
        .withElasticVersion("6.2.0")
        .withSetting(PopularProperties.TRANSPORT_TCP_PORT, 9350)
        .withSetting(PopularProperties.HTTP_PORT, 9351)
        .withSetting(PopularProperties.CLUSTER_NAME, CLUSTER_NAME)
        .withIndex(INDEX_NAME, IndexSettings.builder()
        .withType(TYPE_NAME, mappingStream)
         .withSettings(settingStream)
        .build()).withStartTimeout(5, TimeUnit.MINUTES)
        .build().start();
    client = RestClient.builder(new HttpHost("localhost", 9351, "http"))
        .setRequestConfigCallback(new RestClientBuilder.RequestConfigCallback() {
            @Override
            public RequestConfig.Builder customizeRequestConfig(RequestConfig.Builder requestConfigBuilder) {
                return requestConfigBuilder.setConnectTimeout(5000)
                        .setSocketTimeout(60000);
            }
        })
        .setMaxRetryTimeoutMillis(60000)
        .build();
  }
  
  
  @After
  public void resourceReleased() throws IOException {
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
  public void queryWithDataFields() throws UnsupportedOperationException, IOException {
    ClassLoader classLoader = getClass().getClassLoader();
    URL inputFile = classLoader.getResource("queries/chart_type_data_3_datafields.json");
    InputStream inputStream;
    String jsonString = null;
    try {
      inputStream = new FileInputStream(inputFile.getFile());
      jsonString = IOUtils.toString(inputStream, StandardCharsets.UTF_8.name());
    } catch (IOException e) {
      assertThat(e.getMessage(), is("IOException"));    
    }
    SAWElasticSearchQueryBuilder sawElasticSearchQueryBuilder = new SAWElasticSearchQueryBuilder(null);
    SearchSourceBuilder query = sawElasticSearchQueryBuilder.getSearchSourceBuilder(EntityType.CHART, jsonString,3);
    String endpoint = INDEX_NAME + "/" + TYPE_NAME + "/" + "_search?size=0";
    HttpEntity requestPaylod = new NStringEntity(query.toString(), ContentType.APPLICATION_JSON);
    Response response = client.performRequest(HttpPost.METHOD_NAME, endpoint, emptyMap(), requestPaylod);
    HttpEntity entity = response.getEntity();
    Assert.assertTrue(entity.getContent()!=null);
    
  }
  
  @Test
  public void queryWithDataFieldsAndFilter() throws IOException
  {
    ClassLoader classLoader = getClass().getClassLoader();
    URL inputFile = classLoader.getResource("queries/chart_type_data_3_datafields_filters.json");
    InputStream inputStream;
    String jsonString = null;
    try {
      inputStream = new FileInputStream(inputFile.getFile());
      jsonString = IOUtils.toString(inputStream, StandardCharsets.UTF_8.name());
    } catch (IOException e) {
      assertThat(e.getMessage(), is("IOException"));    
    }
    SAWElasticSearchQueryBuilder sawElasticSearchQueryBuilder = new SAWElasticSearchQueryBuilder(null);
    SearchSourceBuilder query = sawElasticSearchQueryBuilder.getSearchSourceBuilder(EntityType.CHART, jsonString,3);
    String endpoint = INDEX_NAME + "/" + TYPE_NAME + "/" + "_search?size=0";
    HttpEntity requestPaylod = new NStringEntity(query.toString(), ContentType.APPLICATION_JSON);
    Response response = client.performRequest(HttpPost.METHOD_NAME, endpoint, emptyMap(), requestPaylod);
    HttpEntity entity = response.getEntity();
    Assert.assertTrue(entity.getContent()!=null);
   }

  @Test
  public void queryWithDataFieldsFiltersAndSorts() throws IOException
  {
    ClassLoader classLoader = getClass().getClassLoader();
    URL inputFile = classLoader.getResource("queries/chart_type_data_3_datafields_filters_sorts.json");
    InputStream inputStream;
    String jsonString = null;
    try {
      inputStream = new FileInputStream(inputFile.getFile());
      jsonString = IOUtils.toString(inputStream, StandardCharsets.UTF_8.name());
    } catch (IOException e) {
      assertThat(e.getMessage(), is("IOException"));    
    }
    SAWElasticSearchQueryBuilder sawElasticSearchQueryBuilder = new SAWElasticSearchQueryBuilder(null);
    SearchSourceBuilder query = sawElasticSearchQueryBuilder.getSearchSourceBuilder(EntityType.CHART, jsonString,3);
    String endpoint = INDEX_NAME + "/" + TYPE_NAME + "/" + "_search?size=0";
    HttpEntity requestPaylod = new NStringEntity(query.toString(), ContentType.APPLICATION_JSON);
    Response response = client.performRequest(HttpPost.METHOD_NAME, endpoint, emptyMap(), requestPaylod);
    HttpEntity entity = response.getEntity();
    Assert.assertTrue(entity.getContent()!=null);
    }
  
  @Test
  public void queryWithDataFieldsFiltersAndSortsWithDataSecurityKey() throws IOException
  {
    ClassLoader classLoader = getClass().getClassLoader();
    URL inputFile = classLoader.getResource("queries/chart_type_data_3_datafields_filters_sorts.json");
    String dataSecurityKey = "{\"dataSecurityKey\":[{\"name\":\"ORDER_STATE.raw\",\"values\":[\"KA\",\"Alabama\",\"Hawaii\"]},{\"name\":\"TRANSACTION_ID\",\"values\":[\"015cd74a-08dc-494f-8b71-f1cbd546fc31\"]}]}";
    InputStream inputStream;
    String jsonString = null;
    try {
      inputStream = new FileInputStream(inputFile.getFile());
      jsonString = IOUtils.toString(inputStream, StandardCharsets.UTF_8.name());
    } catch (IOException e) {
      assertThat(e.getMessage(), is("IOException"));    
    }
    SAWElasticSearchQueryBuilder sawElasticSearchQueryBuilder = new SAWElasticSearchQueryBuilder(null);
    SearchSourceBuilder query = sawElasticSearchQueryBuilder.getSearchSourceBuilder(EntityType.CHART, jsonString,dataSecurityKey,3);
    String endpoint = INDEX_NAME + "/" + TYPE_NAME + "/" + "_search?size=0";
    HttpEntity requestPaylod = new NStringEntity(query.toString(), ContentType.APPLICATION_JSON);
    Response response = client.performRequest(HttpPost.METHOD_NAME, endpoint, emptyMap(), requestPaylod);
    HttpEntity entity = response.getEntity();
    Assert.assertTrue(entity.getContent()!=null);
  }
  
}
  
  
  

