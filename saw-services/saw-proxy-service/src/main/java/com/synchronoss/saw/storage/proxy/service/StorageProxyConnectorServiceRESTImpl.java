package com.synchronoss.saw.storage.proxy.service;

import static java.util.Collections.emptyMap;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.validation.constraints.NotNull;
import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.ContentType;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.nio.client.HttpAsyncClientBuilder;
import org.apache.http.nio.entity.NStringEntity;
import org.apache.http.util.EntityUtils;
import org.elasticsearch.client.Response;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestClientBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.common.base.Preconditions;
import com.synchronoss.saw.storage.proxy.model.StorageProxy;
import com.synchronoss.saw.storage.proxy.model.StorageProxy.Action;
import com.synchronoss.saw.storage.proxy.model.StorageProxy.Storage;
import com.synchronoss.saw.storage.proxy.model.StoreField;
import com.synchronoss.saw.storage.proxy.model.response.ClusterAliasesResponse;
import com.synchronoss.saw.storage.proxy.model.response.ClusterIndexResponse;
import com.synchronoss.saw.storage.proxy.model.response.CountESResponse;
import com.synchronoss.saw.storage.proxy.model.response.CreateAndDeleteESResponse;
import com.synchronoss.saw.storage.proxy.model.response.SearchESResponse;

@Service
public class StorageProxyConnectorServiceRESTImpl implements StorageConnectorService {

  private static final Logger logger = LoggerFactory.getLogger(StorageProxyConnectorServiceRESTImpl.class);
  @Value("${elastic-xpack.cluster-active}")
  @NotNull
  private Boolean active = false;

  @Value("${elastic-xpack.cluster-username}")
  private String username;

  @Value("${elastic-xpack.cluster-password}")
  private String password;

  @Value("${elastic-search.cluster-name}")
  @NotNull
  private String clusterName;

  @Value("${elastic-search.transport-hosts}")
  @NotNull
  private String[] hosts;

  @Value("${elastic-search.transport-ports}")
  @NotNull
  private String[] ports;
  
  private final String SEARCH = "_search";
  private final String COUNT = "_count";
  
  
  @Override
  public SearchESResponse<?> searchDocuments(String query, StorageProxy proxyDetails) throws Exception {
    Preconditions.checkArgument(query != null && !"".equals(query), "query cannnot be null.");
    logger.debug("Query:", query);
    Response response = null;
    RestClient client = null;
    SearchESResponse<?> searchResponse = null;
    String endpoint = proxyDetails.getObjectType()!=null? proxyDetails.getIndexName() + "/" + proxyDetails.getObjectType() + "/" + SEARCH : 
      proxyDetails.getIndexName()  + "/" + SEARCH;
    try{
        HttpEntity requestPaylod = new NStringEntity(query, ContentType.APPLICATION_JSON);
        client = prepareRESTESConnection();
        response = client.performRequest(HttpPost.METHOD_NAME, endpoint, emptyMap(), requestPaylod);
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.configure(JsonParser.Feature.ALLOW_UNQUOTED_FIELD_NAMES, true);
        objectMapper.enable(DeserializationFeature.FAIL_ON_READING_DUP_TREE_KEY);
        HttpEntity entity = response.getEntity();
        searchResponse = objectMapper.readValue(entity.getContent(), SearchESResponse.class);
        client.close();
    }
    finally{
      if (client !=null){
        client.close();
      }
    }
    logger.trace("Search Response", searchResponse.toString());
    return searchResponse;
  }

 
  @Override
  public CreateAndDeleteESResponse deleteDocumentById(String id, StorageProxy proxyDetails) throws Exception {
    Preconditions.checkArgument(id != null && !"".equals(id), "Entity Id to be deleted cannnot be null.");
    logger.debug("Id to be deleted", id);
    Response response = null;
    RestClient client = null;
    CreateAndDeleteESResponse createAndDeleteESResponse = null;
    String endpoint = proxyDetails.getIndexName() + "/" + proxyDetails.getObjectType() + "/" + id;
    try{
        client = prepareRESTESConnection();
        response = client.performRequest(HttpDelete.METHOD_NAME, endpoint, emptyMap());
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.configure(JsonParser.Feature.ALLOW_UNQUOTED_FIELD_NAMES, true);
        objectMapper.enable(DeserializationFeature.FAIL_ON_READING_DUP_TREE_KEY);
        HttpEntity entity = response.getEntity();
        createAndDeleteESResponse = objectMapper.readValue(entity.getContent(), CreateAndDeleteESResponse.class);
        client.close();
    }
    finally{
      if (client !=null){
        client.close();
      }
    }
    logger.trace("Delete Response", createAndDeleteESResponse.toString());
    return createAndDeleteESResponse;
  }

  @Override
  public CreateAndDeleteESResponse createDocument(String query, StorageProxy proxyDetails) throws Exception {
    Preconditions.checkArgument(query != null && !"".equals(query), "query cannnot be null.");
    Preconditions.checkArgument(proxyDetails.getEntityId() != null && !"".equals(proxyDetails.getEntityId()), "Document Id cannnot be null.");
    logger.debug("Id to be created", proxyDetails.getEntityId());
    Response response = null;
    RestClient client = null;
    CreateAndDeleteESResponse createAndDeleteESResponse = null;
    String endpoint = proxyDetails.getIndexName() + "/" + proxyDetails.getObjectType() + "/" + proxyDetails.getEntityId();
    try{
        HttpEntity requestPaylod = new NStringEntity(query, ContentType.APPLICATION_JSON);
        client = prepareRESTESConnection();
        response = client.performRequest(HttpPut.METHOD_NAME, endpoint, emptyMap(), requestPaylod);
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.configure(JsonParser.Feature.ALLOW_UNQUOTED_FIELD_NAMES, true);
        objectMapper.enable(DeserializationFeature.FAIL_ON_READING_DUP_TREE_KEY);
        objectMapper.disable((DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES));
        HttpEntity entity = response.getEntity();
        createAndDeleteESResponse = objectMapper.readValue(entity.getContent(), CreateAndDeleteESResponse.class);
        client.close();
    }
    finally{
      if (client !=null){
        client.close();
      }
    }
    logger.trace("Create Response", createAndDeleteESResponse.toString());
    return createAndDeleteESResponse;
  }

  @Override
  public CountESResponse countDocument(String query, StorageProxy proxyDetails) throws Exception {
    Response response = null;
    RestClient client = null;
    CountESResponse countResponse = null;
    String endpoint = proxyDetails.getIndexName() + "/" + proxyDetails.getObjectType() + "/" + COUNT;
    try{
        query = (query == null || "".equals(query)) ? "" : query;
        HttpEntity requestPaylod = new NStringEntity(query, ContentType.APPLICATION_JSON);
        client = prepareRESTESConnection();
        response = client.performRequest(HttpPost.METHOD_NAME, endpoint, emptyMap(), requestPaylod);
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.configure(JsonParser.Feature.ALLOW_UNQUOTED_FIELD_NAMES, true);
        objectMapper.enable(DeserializationFeature.FAIL_ON_READING_DUP_TREE_KEY);
        HttpEntity entity = response.getEntity();
        countResponse = objectMapper.readValue(entity.getContent(), CountESResponse.class);
        client.close();
    }
    finally{
      if (client !=null){
        client.close();
      }
    }
    logger.trace("Count Response", countResponse.toString());
    return countResponse;
  }
  
  
  @Override
  public List<ClusterIndexResponse> catClusterIndices(StorageProxy proxyDetails) throws Exception {
    Response response = null;
    RestClient client = null;
    List<ClusterIndexResponse> catClusterIndexResponse = null;
    String endpoint = "_cat" + "/" + "indices?format=json&pretty";
    try{
        client = prepareRESTESConnection();
        response = client.performRequest(HttpGet.METHOD_NAME, endpoint);
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.configure(JsonParser.Feature.ALLOW_UNQUOTED_FIELD_NAMES, true);
        objectMapper.enable(DeserializationFeature.FAIL_ON_READING_DUP_TREE_KEY);
        HttpEntity entity = response.getEntity();
        catClusterIndexResponse = objectMapper.readValue(entity.getContent(), new TypeReference<List<ClusterIndexResponse>>(){});;
    }
    finally{
      if (client !=null){
        client.close();
      }
    }
    logger.trace("Cluster Index Response", catClusterIndexResponse.toString());
    return catClusterIndexResponse;
  }

  @Override
  public List<ClusterAliasesResponse> catClusterAliases(StorageProxy proxyDetails) throws Exception {
    Response response = null;
    RestClient client = null;
    List<ClusterAliasesResponse> catClusterIndexResponse = null;
    String endpoint = "_cat" + "/" + "aliases?format=json&pretty";
    try{
        client = prepareRESTESConnection();
        response = client.performRequest(HttpGet.METHOD_NAME, endpoint);
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.configure(JsonParser.Feature.ALLOW_UNQUOTED_FIELD_NAMES, true);
        objectMapper.enable(DeserializationFeature.FAIL_ON_READING_DUP_TREE_KEY);
        HttpEntity entity = response.getEntity();
        catClusterIndexResponse = objectMapper.readValue(entity.getContent(), new TypeReference<List<ClusterAliasesResponse>>(){});;
    }
    finally{
      if (client !=null){
        client.close();
      }
    }
    logger.trace("Cluster Index Response", catClusterIndexResponse.toString());
    return catClusterIndexResponse;
  }
  
  @Override
  public List<StoreField> getMappingbyIndex(StorageProxy proxyDetails) throws Exception {
    Response response = null;
    RestClient client = null;
    List<StoreField> storeFields =new ArrayList<>();
    ArrayNode bucketNode = null;
    final String AGGREGATION_NAME = "typeAgg";
    final String BUCKETS = "buckets";
    final String PROPERTIES = "properties";
    final String KEY = "key";
    
    Preconditions.checkNotNull(proxyDetails.getIndexName(), "Index Name cannot be null");
    String endpoint = proxyDetails.getIndexName() + "/" + "_mappings";
    try{
        client = prepareRESTESConnection();
        response = client.performRequest(HttpGet.METHOD_NAME, endpoint);
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.configure(JsonParser.Feature.ALLOW_UNQUOTED_FIELD_NAMES, true);
        objectMapper.enable(DeserializationFeature.FAIL_ON_READING_DUP_TREE_KEY);
        HttpEntity entity = response.getEntity();
        JsonNode mappingNode  = objectMapper.readTree(entity.getContent());
        logger.trace("mappingNode: " + objectMapper.writeValueAsString(mappingNode));
        // The below query to figure the type name dynamically for ES 6.x
        String typeAggregationQuery= "{\"aggs\":{\"typeAgg\":{\"terms\":{\"field\":\"_type\",\"size\":1}}},\"size\":0}";
        SearchESResponse<?> typeAggregationResult = searchDocuments(typeAggregationQuery, proxyDetails);
        ObjectNode rootNode = (ObjectNode) mappingNode;
        logger.trace("rootNode: " + objectMapper.writeValueAsString(rootNode));
        ObjectNode mappingDataNode = (ObjectNode)rootNode.get(proxyDetails.getIndexName()).get("mappings");
        logger.trace("mappingDataNode: " + objectMapper.writeValueAsString(mappingDataNode));
        if (typeAggregationResult.getAggregations()!=null) {
          JsonNode aggregationNode  = objectMapper.readTree(objectMapper.writeValueAsString(typeAggregationResult.getAggregations()));
          logger.trace("aggregationNode: " + objectMapper.writeValueAsString(aggregationNode));
          bucketNode = (ArrayNode) aggregationNode.get(AGGREGATION_NAME).get(BUCKETS);
          logger.trace("bucketNode: " + objectMapper.writeValueAsString(bucketNode));
          // As per as ES.6.x, there will be no more than 1 type per index
          String typeName = bucketNode.get(0).get(KEY).asText();
          ObjectNode typeNode  = (ObjectNode) mappingDataNode.get(typeName).get(PROPERTIES);
          logger.trace("typeNode: " + objectMapper.writeValueAsString(typeNode));
          Map<?,?> objectDefination = new HashMap<String, String>();
          objectDefination = objectMapper.readValue(objectMapper.writeValueAsBytes(typeNode), HashMap.class);
          Iterator<?> itr =   objectDefination.keySet().iterator();
          while (itr.hasNext()) {
            String attributeName = itr.next().toString();
            logger.trace("attributeName : " + attributeName);
            logger.trace("attributeValue :" + objectDefination.get(attributeName).toString());
            StoreField storeField = objectMapper.readValue(objectMapper.writeValueAsString(objectDefination.get(attributeName)),StoreField.class);
            storeField.setColumnName(attributeName);
            storeFields.add(storeField);
          }
        }
    }
    finally{
      if (client !=null){
        client.close();
      }
    }
    logger.trace("Cluster Index Response", storeFields.toString());
    return storeFields;
  }

  @Override
  public StorageProxy getMappingbyAlias(StorageProxy proxyDetails) throws Exception {
    Response response = null;
    RestClient client = null;
    List<StoreField> storeFields =new ArrayList<>();
    ArrayNode bucketNode = null;
    ArrayNode indexbucketNode = null;
    final String TYPE_AGGREGATION_NAME = "typeAgg";
    final String INDEX_AGGREGATION_NAME = "indexAgg";
    final String BUCKETS = "buckets";
    final String PROPERTIES = "properties";
    final String KEY = "key";
    StorageProxy  dataProxy = null;
    Preconditions.checkNotNull(proxyDetails.getIndexName(), "Index Name cannot be null");
    String endpoint = proxyDetails.getIndexName() + "/" + "_mappings";
    ClusterIndexResponse  clusterIndexResponse = null;
    try{
        client = prepareRESTESConnection();
        response = client.performRequest(HttpGet.METHOD_NAME, endpoint);
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.configure(JsonParser.Feature.ALLOW_UNQUOTED_FIELD_NAMES, true);
        objectMapper.enable(DeserializationFeature.FAIL_ON_READING_DUP_TREE_KEY);
        HttpEntity entity = response.getEntity();
        JsonNode mappingNode  = objectMapper.readTree(entity.getContent());
        logger.trace("mappingNode: " + objectMapper.writeValueAsString(mappingNode));
        // The below query to figure the type name dynamically for ES 6.x
        String typeAggregationQuery= "{\"aggs\":{\"typeAgg\":{\"terms\":{\"field\":\"_type\",\"size\":1}}},\"size\":0}";
        String indexAggregationQuery= "{\"aggs\":{\"indexAgg\":{\"terms\":{\"field\":\"_index\",\"size\":1}}},\"size\":0}";
        SearchESResponse<?> typeAggregationResult = searchDocuments(typeAggregationQuery, proxyDetails);
        SearchESResponse<?> indexAggregationResult = searchDocuments(indexAggregationQuery, proxyDetails);
        String indexName = null;
        ObjectNode rootNode = (ObjectNode) mappingNode;
        logger.trace("rootNode: " + objectMapper.writeValueAsString(rootNode));
        if (indexAggregationResult.getAggregations()!=null) {
          JsonNode aggregationNode  = objectMapper.readTree(objectMapper.writeValueAsString(indexAggregationResult.getAggregations()));
          logger.trace("aggregationNodeIndex: " + objectMapper.writeValueAsString(aggregationNode));
          indexbucketNode = (ArrayNode) aggregationNode.get(INDEX_AGGREGATION_NAME).get(BUCKETS);;
          logger.trace("indexBucketNode: " + objectMapper.writeValueAsString(indexbucketNode));
          indexName = indexbucketNode.get(0).get(KEY).asText();
          logger.trace("indexName: " + indexName);
          dataProxy = new StorageProxy();
          dataProxy.setStorage(Storage.ES);
          dataProxy.setRequestBy("workbenchAdmin@synchronoss.com");
          dataProxy.setRequestedTime(new SimpleDateFormat("yyyy-mm-dd hh:mm:ss").format(new Date()));
          dataProxy.setProductCode("WORKBENCH");
          dataProxy.setModuleName("WORKBENCH");
          dataProxy.setAction(Action.COUNT);
          dataProxy.setIndexName(indexName);
          clusterIndexResponse = catClusterIndices(dataProxy).get(0);
        }
       proxyDetails.setCount(clusterIndexResponse.getDocsCount());
       proxyDetails.setSize(clusterIndexResponse.getPriStoreSize());
        ObjectNode mappingDataNode = (ObjectNode)rootNode.get(indexName).get("mappings");
        logger.trace("mappingDataNode: " + objectMapper.writeValueAsString(mappingDataNode));
        if (typeAggregationResult.getAggregations()!=null) {
          JsonNode aggregationNode  = objectMapper.readTree(objectMapper.writeValueAsString(typeAggregationResult.getAggregations()));
          logger.trace("aggregationNodeType: " + objectMapper.writeValueAsString(aggregationNode));
          bucketNode = (ArrayNode) aggregationNode.get(TYPE_AGGREGATION_NAME).get(BUCKETS);
          logger.trace("bucketNode: " + objectMapper.writeValueAsString(bucketNode));
          // As per as ES.6.x, there will be no more than 1 type per index
          String typeName = bucketNode.get(0).get(KEY).asText();
          ObjectNode typeNode  = (ObjectNode) mappingDataNode.get(typeName).get(PROPERTIES);
          logger.trace("typeNode: " + objectMapper.writeValueAsString(typeNode));
          Map<?,?> objectDefination = new HashMap<String, String>();
          objectDefination = objectMapper.readValue(objectMapper.writeValueAsBytes(typeNode), HashMap.class);
          Iterator<?> itr =   objectDefination.keySet().iterator();
          while (itr.hasNext()) {
            String attributeName = itr.next().toString();
            logger.trace("attributeName : " + attributeName);
            logger.trace("attributeValue :" + objectDefination.get(attributeName).toString());
            StoreField storeField = objectMapper.readValue(objectMapper.writeValueAsString(objectDefination.get(attributeName)),StoreField.class);
            storeField.setColumnName(attributeName);
            storeFields.add(storeField);
          } // end of while loop
        }
    }
    finally{
      if (client !=null){
        client.close();
      }
    }
    logger.trace("Cluster Index Response", storeFields.toString());
    List<Object> mappinglistAliases = new ArrayList<>();
    for(StoreField storeField : storeFields) {
      mappinglistAliases.add(storeField);
    }
    proxyDetails.setData(mappinglistAliases);
    return proxyDetails;
  }

  private HttpHost[] prepareHostAddresses(String[] hosts, String[] ports) {
    Preconditions.checkArgument(hosts.length == ports.length, "number of hosts is not equal to number of ports been provided");
    HttpHost [] httpHosts = new HttpHost[hosts.length];
    HttpHost httpHost = null;
    for (int i=0; i<hosts.length;i++){
      httpHost = new HttpHost(hosts[i], Integer.parseInt(ports[i]), "http"); 
      httpHosts[i] = httpHost;
    }
    return httpHosts;
  }
  
  private RestClient prepareRESTESConnection() throws Exception {
    RestClient restClient = null;
      if (active){
        final CredentialsProvider credentialsProvider = new BasicCredentialsProvider();
        credentialsProvider.setCredentials(AuthScope.ANY,
                new UsernamePasswordCredentials(username, password));
        restClient = RestClient.builder(prepareHostAddresses(hosts, ports))
                .setHttpClientConfigCallback(new RestClientBuilder.HttpClientConfigCallback() {
                    @Override
                    public HttpAsyncClientBuilder customizeHttpClient(HttpAsyncClientBuilder httpClientBuilder) {
                        return httpClientBuilder.setDefaultCredentialsProvider(credentialsProvider);}
                }).setRequestConfigCallback(new RestClientBuilder.RequestConfigCallback() {
                  @Override
                  public RequestConfig.Builder customizeRequestConfig(RequestConfig.Builder requestConfigBuilder) {
                      return requestConfigBuilder.setConnectTimeout(5000)
                              .setSocketTimeout(60000);
                  }
              }).setMaxRetryTimeoutMillis(60000)
                .build();
      } else {
        restClient = RestClient.builder(prepareHostAddresses(hosts, ports))
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
      return restClient;
  }
  
  
 
  
  public static void main(String[] args) throws IOException {
    //withOutXPACK();
/*    String query = "{\"query\":{\"bool\":{\"must\":[{\"match\":{\"SOURCE_OS.keyword\":{\"query\":\"android\",\"operator\":\"AND\",\"analyzer\":\"standard\",\"prefix_length\":0,\"max_expansions\":50,\"fuzzy_transpositions\":false,\"lenient\":false,\"zero_terms_query\":\"ALL\",\"boost\":1.0}}},{\"match\":{\"TARGET_MANUFACTURER.keyword\":{\"query\":\"motorola\",\"operator\":\"AND\",\"analyzer\":\"standard\",\"prefix_length\":0,\"max_expansions\":50,\"fuzzy_transpositions\":false,\"lenient\":false,\"zero_terms_query\":\"ALL\",\"boost\":1.0}}}],\"disable_coord\":false,\"adjust_pure_negative\":true,\"boost\":1.0}},\"sort\":[{\"TRANSFER_DATE\":{\"order\":\"asc\"}}],\"aggregations\":{\"node_field_1\":{\"date_histogram\":{\"field\":\"TRANSFER_DATE\",\"format\":\"MMM YYYY\",\"interval\":\"1M\",\"offset\":0,\"order\":{\"_key\":\"desc\"},\"keyed\":false,\"min_doc_count\":0},\"aggregations\":{\"AVAILABLE_ITEMS\":{\"sum\":{\"field\":\"AVAILABLE_ITEMS\",\"from\":1,\"size\":1,}}}}}}";
    System.out.println(query.contains("size"));
    System.out.println(query.contains("from"));
    
    String regEx=".*?(size|from).*?(\\d+).*?(from|size).*?(\\d+)";   

    Pattern p = Pattern.compile(regEx, Pattern.CASE_INSENSITIVE | Pattern.DOTALL);
    Matcher m = p.matcher(query);
    if (m.find())
    {
        String word1=m.group(1);
        String int1=m.group(2);
        String word2=m.group(3);
        String int2=m.group(4);
        System.out.print("("+word1.toString()+")"+"("+int1.toString()+")"+"("+word2.toString()+")"+"("+int2.toString()+")"+"\n");
    }*/
    ObjectMapper objectMapper = new ObjectMapper();
    objectMapper.configure(JsonParser.Feature.ALLOW_UNQUOTED_FIELD_NAMES, true);
    objectMapper.enable(DeserializationFeature.FAIL_ON_READING_DUP_TREE_KEY);
  
  String mappingString = "{\"mct_tmo_session\":{\"mappings\":{\"session\":{\"dynamic\":\"strict\",\"properties\":{\"APP_KEY\":{\"type\":\"text\",\"fields\":{\"keyword\":{\"type\":\"keyword\"}}},\"AVAILABLE_BYTES\":{\"type\":\"double\"}}}}}}"; 
  String result ="{\"took\":7,\"timed_out\":false,\"_shards\":{\"total\":5,\"successful\":5,\"skipped\":0,\"failed\":0},\"hits\":{\"total\":538335,\"max_score\":0,\"hits\":[]},\"aggregations\":{\"typeAgg\":{\"doc_count_error_upper_bound\":0,\"sum_other_doc_count\":0,\"buckets\":[{\"key\":\"session\",\"doc_count\":538335}]}}}";
  
  JsonNode mappingNode  = objectMapper.readTree(mappingString);
  ObjectNode rootNode = (ObjectNode) mappingNode;
  ObjectNode mappingDataNode = (ObjectNode)rootNode.get("mct_tmo_session").get("mappings");

  System.out.println(mappingDataNode);
  
  JsonNode aggregationNode  = objectMapper.readTree(result);
  ArrayNode bucketNode = (ArrayNode) aggregationNode.get("aggregations").get("typeAgg").get("buckets");
  List<String> typesOfIndex = new ArrayList<String>();
  for (JsonNode node : bucketNode) {
   typesOfIndex.add(node.get("key").asText());
  }
  System.out.println(typesOfIndex);

  ObjectNode typeNode = null;
  for (String typeName : typesOfIndex) {
     typeNode  = (ObjectNode)mappingDataNode.get(typeName).get("properties");
     System.out.println(typeNode);
  }
  
  Map<?,?> objectDefination = new HashMap<String, String>();
  objectDefination = objectMapper.readValue(objectMapper.writeValueAsBytes(typeNode), HashMap.class);
  
  Iterator<?> itr =   objectDefination.keySet().iterator();
  
  while (itr.hasNext()) {
    String attributeName = itr.next().toString();
    System.out.println(attributeName);
    System.out.println(objectDefination.get(attributeName));
    StoreField storeField = objectMapper.readValue(objectMapper.writeValueAsString(objectDefination.get(attributeName)),StoreField.class);
    storeField.setColumnName(attributeName);
    System.out.println(storeField.toString()); 
  }
  
  
 
    
   }
   private static void withOutXPACK()throws IOException
   {
     RestClient restClient = RestClient.builder(new HttpHost("es-rd601.ana.dev.vaste.sncrcorp.net", 9200, "http"))
         .setRequestConfigCallback(new RestClientBuilder.RequestConfigCallback() {
             @Override
             public RequestConfig.Builder customizeRequestConfig(RequestConfig.Builder requestConfigBuilder) {
                 return requestConfigBuilder.setConnectTimeout(5000)
                         .setSocketTimeout(60000);
             }
         })
         .setMaxRetryTimeoutMillis(60000)
         .build();
     
     //final HttpEntity payload = new  NStringEntity("{\"size\":1,\"query\":{\"bool\":{\"must\":[{\"match\":{\"SOURCE_OS.keyword\":{\"query\":\"android\",\"operator\":\"AND\",\"analyzer\":\"standard\",\"prefix_length\":0,\"max_expansions\":50,\"fuzzy_transpositions\":false,\"lenient\":false,\"zero_terms_query\":\"ALL\",\"boost\":1.0}}},{\"match\":{\"TARGET_MANUFACTURER.keyword\":{\"query\":\"motorola\",\"operator\":\"AND\",\"analyzer\":\"standard\",\"prefix_length\":0,\"max_expansions\":50,\"fuzzy_transpositions\":false,\"lenient\":false,\"zero_terms_query\":\"ALL\",\"boost\":1.0}}}],\"disable_coord\":false,\"adjust_pure_negative\":true,\"boost\":1.0}},\"sort\":[{\"TRANSFER_DATE\":{\"order\":\"asc\"}}],\"aggregations\":{\"node_field_1\":{\"date_histogram\":{\"field\":\"TRANSFER_DATE\",\"format\":\"MMM YYYY\",\"interval\":\"1M\",\"offset\":0,\"order\":{\"_key\":\"desc\"},\"keyed\":false,\"min_doc_count\":0},\"aggregations\":{\"AVAILABLE_ITEMS\":{\"sum\":{\"field\":\"AVAILABLE_ITEMS\"}}}}}}",ContentType.APPLICATION_JSON);
     //final HttpEntity payload = new  NStringEntity("",ContentType.APPLICATION_JSON);
     //final HttpEntity payload = new  NStringEntity("{\"city\":\"Baltimore\"}",ContentType.APPLICATION_JSON);
    // final Response response = restClient.performRequest(HttpPost.METHOD_NAME, "/mct_tmo_session/session/_search", emptyMap(), payload);
    
     final Response response = restClient.performRequest(HttpGet.METHOD_NAME, "/mct_tmo_session/_mappings/");
     
     //Response response = restClient.performRequest(HttpDelete.METHOD_NAME, "lower/lowerCase/AWEQIWg3jV2L1EGZ4Mac", emptyMap());
     ObjectMapper objectMapper = new ObjectMapper();
     objectMapper.configure(JsonParser.Feature.ALLOW_UNQUOTED_FIELD_NAMES, true);
     objectMapper.enable(DeserializationFeature.FAIL_ON_READING_DUP_TREE_KEY);

     HttpEntity entity = response.getEntity();
     String data = EntityUtils.toString(entity);
     System.out.println(data);
     //createAndDeleteESResponse = objectMapper.readValue(entity.getContent(), CreateAndDeleteESResponse.class);

     //SearchESResponse<?> searchResponse = objectMapper.readValue(entity.getContent(), SearchESResponse.class);
     //CreateAndDeleteESResponse createResponse = objectMapper.readValue(entity.getContent(), CreateAndDeleteESResponse.class);
     //System.out.println(createResponse.toString());
     //System.out.println(createResponse.getShards().toString() + ":" + createResponse.getResult());
     String query = "{\"from\":5,\"query\":{\"bool\":{\"must\":[{\"match\":{\"SOURCE_OS.keyword\":{\"query\":\"android\",\"operator\":\"AND\",\"analyzer\":\"standard\",\"prefix_length\":0,\"max_expansions\":50,\"fuzzy_transpositions\":false,\"lenient\":false,\"zero_terms_query\":\"ALL\",\"boost\":1.0}}},{\"match\":{\"TARGET_MANUFACTURER.keyword\":{\"query\":\"motorola\",\"operator\":\"AND\",\"analyzer\":\"standard\",\"prefix_length\":0,\"max_expansions\":50,\"fuzzy_transpositions\":false,\"lenient\":false,\"zero_terms_query\":\"ALL\",\"boost\":1.0}}}],\"disable_coord\":false,\"adjust_pure_negative\":true,\"boost\":1.0}},\"sort\":[{\"TRANSFER_DATE\":{\"order\":\"asc\"}}],\"aggregations\":{\"node_field_1\":{\"date_histogram\":{\"field\":\"TRANSFER_DATE\",\"format\":\"MMM YYYY\",\"interval\":\"1M\",\"offset\":0,\"order\":{\"_key\":\"desc\"},\"keyed\":false,\"min_doc_count\":0},\"aggregations\":{\"AVAILABLE_ITEMS\":{\"sum\":{\"field\":\"AVAILABLE_ITEMS\",\"size\":1}}}}}}";
     System.out.println(query.contains("size"));
     System.out.println(query.contains("from"));
     
     String re1=".*?";  
     
     String re2="(size)";    
     String re3=".*?";   
     String re4="(\\d+)";    
     String re5=".*?";   
     
     String re6="(from)";    
     String re7=".*?";   
     String re8="(\\d+)";    
     
 
     String re9=".*?";  
     String re10="(from)";   
     String re11=".*?";   
     String re12="(\\d+)";    
     String re13=".*?";   
     String re14="(size)";    
     String re15=".*?";   
     String re16="(\\d+)";    
     

     Pattern p = Pattern.compile("["+re1+re2+re3+re4+re5+re6+re7+re8+"|"+re9+re10+re11+re12+re13+re14+re15+re16+"]", Pattern.CASE_INSENSITIVE | Pattern.DOTALL | Pattern.COMMENTS | Pattern.MULTILINE );
     Matcher matcher = p.matcher(query);
     
     
     if (matcher.find())
     {
       String word1=matcher.group(1);
       String int1=matcher.group(2);
       String word2=matcher.group(3);
       String int2=matcher.group(4);
       System.out.print("("+word1.toString()+")"+"("+int1.toString()+")"+"("+word2.toString()+")"+"("+int2.toString()+")"+"\n");  }
     restClient.close();
     }




  
  
 
  
}



