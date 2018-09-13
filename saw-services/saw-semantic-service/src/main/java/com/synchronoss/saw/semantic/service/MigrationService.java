package com.synchronoss.saw.semantic.service;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import org.apache.commons.io.FilenameUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.common.base.Preconditions;
import com.synchronoss.saw.semantic.SAWSemanticUtils;
import com.synchronoss.saw.semantic.exceptions.CreateEntitySAWException;
import com.synchronoss.saw.semantic.exceptions.DeleteEntitySAWException;
import com.synchronoss.saw.semantic.exceptions.JSONValidationSAWException;
import com.synchronoss.saw.semantic.exceptions.ReadEntitySAWException;
import com.synchronoss.saw.semantic.model.DataSemanticObjects;
import com.synchronoss.saw.semantic.model.MetaDataObjects;
import com.synchronoss.saw.semantic.model.request.BinarySemanticNode;
import com.synchronoss.saw.semantic.model.request.SemanticNode;
import sncr.bda.cli.MetaDataStoreRequestAPI;
import sncr.bda.datasets.conf.DataSetProperties;
import sncr.bda.store.generic.schema.Action;
import sncr.bda.store.generic.schema.Category;
import sncr.bda.store.generic.schema.MetaDataStoreStructure;

/**
 * This class is used to migrate the existing HBase Binary data to MapRDBStore.
 * It is independent migration class.
 * @author spau0004
 */
public class MigrationService {

  private static final Logger logger = LoggerFactory.getLogger(MigrationService.class);
  
   /**
   * This method will get the data from binary store & make it sure.
   * binary & maprDB store are in sync
   * @throws IOException 
   * @throws JsonProcessingException 
   */
  public void convertHBaseBinaryToMaprDBStore (String transportURI, String basePath) throws JsonProcessingException, IOException {
    logger.trace("migration process will begin here");
     RestTemplate restTemplate = new RestTemplate();
     HttpHeaders  requestHeaders = new HttpHeaders();
     // Constructing the request structure to get the list of semantic from Binary Store
     requestHeaders.set("Content-type", MediaType.APPLICATION_JSON_UTF8_VALUE);
     ObjectMapper objectMapper = new ObjectMapper();
     objectMapper.configure(JsonParser.Feature.ALLOW_UNQUOTED_FIELD_NAMES, true);
     objectMapper.enable(DeserializationFeature.FAIL_ON_READING_DUP_TREE_KEY);
     HttpEntity<?> requestEntity = new HttpEntity<Object>(semanticNodeQuery("search"),requestHeaders);
     logger.debug("transportMetadataURIL server URL {}", transportURI + "/md");
     String url = transportURI + "/md";
     ResponseEntity<MetaDataObjects> binarySemanticStoreData = restTemplate.exchange(url, HttpMethod.POST,
         requestEntity, MetaDataObjects.class);
   if ((binarySemanticStoreData.getBody()!=null && (binarySemanticStoreData.getBody().getContents()!=null) && (binarySemanticStoreData.getBody().getContents().size() >0))) {
     logger.trace(objectMapper.writeValueAsString(binarySemanticStoreData));
     List<BinarySemanticNode> binaryNodes = objectMapper.readValue(objectMapper.writeValueAsString(binarySemanticStoreData.getBody().getContents().get(0).getAnalyze()), 
         new TypeReference<List<BinarySemanticNode>>(){});
     SemanticNode  semanticNode = null;
     List<String> listOfDataObjectIds = null;
     
       for (BinarySemanticNode binarySemanticNode : binaryNodes) {
         String id = binarySemanticNode.getId();
         semanticNode = new SemanticNode();
         semanticNode.set_id(id);
         semanticNode.setId(id);
         logger.trace("Checking for the Id in existence : " + id);
         if (!(readSemantic(semanticNode,basePath))) {
         semanticNode.setCustomerCode(binarySemanticNode.getCustomerCode());
         semanticNode.setModule(com.synchronoss.saw.semantic.model.request.SemanticNode.Module.ANALYZE);
         semanticNode.setProjectCode(binarySemanticNode.getProjectCode()!=null ? binarySemanticNode.getProjectCode() : "workbench");
         semanticNode.setUsername("sipadmin@synchronoss.com");
         semanticNode.setMetricName(binarySemanticNode.getMetricName());
         if (binarySemanticNode.getEsRepository()!=null) 
           {semanticNode.setEsRepository(binarySemanticNode.getEsRepository());}
         else {
         // Constructing the repository the way new JSON store will understand
          listOfDataObjectIds =  new ArrayList<>();
         JsonNode repository = objectMapper.readTree(objectMapper.writeValueAsString(binarySemanticNode.getRepository()));
         ArrayNode repositories = (ArrayNode) repository.get("objects");
         ObjectNode newRepoNode = null;
         HttpEntity<?> dataObjectRequestEntity = null;
         ResponseEntity<DataSemanticObjects> binaryDataObjectNode = null;
         JsonNode dataObjectData = null;
         List<Object> repositoryObjects = new ArrayList<>();
         for (JsonNode repositoryNode : repositories) {
           String dataObjectId = repositoryNode.get("EnrichedDataObjectId").asText();
           String dataSetName  = repositoryNode.get("EnrichedDataObjectName").asText();
           logger.trace("dataObjectId : " + dataObjectId);
           logger.trace("dataSetName : " + dataSetName);
           newRepoNode = objectMapper.createObjectNode();
           newRepoNode.put(DataSetProperties.Name.toString(), dataSetName);
           requestHeaders.set("Content-type", MediaType.APPLICATION_JSON_UTF8_VALUE);
           dataObjectRequestEntity = new HttpEntity<Object>(dataObjectQuery(dataObjectId, "read"),requestHeaders);
           logger.trace("dataObjectRequestEntity : " + objectMapper.writeValueAsString(dataObjectRequestEntity));
           logger.debug("transportMetadataURIL server URL {}", transportURI + "/md");
           binaryDataObjectNode = restTemplate.exchange(url, HttpMethod.POST,
               dataObjectRequestEntity, DataSemanticObjects.class);
           dataObjectData = objectMapper.readTree(objectMapper.writeValueAsString(binaryDataObjectNode.getBody().getContents().get(0)));
           logger.trace("dataObjectData : " + objectMapper.writeValueAsString(dataObjectData));
           newRepoNode.put(DataSetProperties.PhysicalLocation.toString(), dataObjectData.get("dataLocation").asText());
           Path file = Paths.get(dataObjectData.get("dataLocation").asText());
           String format = (FilenameUtils.getExtension(file.getFileName().toString()).equals("") || 
           FilenameUtils.getExtension(file.getFileName().toString()) != null) ? FilenameUtils.getExtension(file.getFileName().toString()) : "parquet" ;
           newRepoNode.put(DataSetProperties.Format.toString(), format);
           repositoryObjects.add(newRepoNode);
           listOfDataObjectIds.add(dataObjectId);
         }
         semanticNode.setRepository(repositoryObjects);
       }
         semanticNode.setArtifacts(binarySemanticNode.getArtifacts());
         semanticNode.setSupports(binarySemanticNode.getSupports());
         try {
           logger.trace("semanticNode description which is going to migrate :"+ objectMapper.writeValueAsString(semanticNode));
           addSemantic(semanticNode, basePath);
         }
         catch (Exception ex) {
           logger.trace("Throwing an exception while adding the semantic to the new store");
           throw new CreateEntitySAWException("Exception generated during migration while creating an semantic entity ", ex);
         }
         
        /*finally {
          try {
            requestHeaders.set("Content-type", MediaType.APPLICATION_JSON_UTF8_VALUE);
            HttpEntity<?> deleteSemanticBinaryRequestEntity = new HttpEntity<Object>(semanticNodeQuery("delete"),requestHeaders);
            logger.trace("transportMetadataURIL server URL {}", transportURI + "/md");
            logger.trace("deleteSemanticBinaryRequestEntity {}", objectMapper.writeValueAsString(deleteSemanticBinaryRequestEntity));
            ResponseEntity<?> deleteSemanticBinaryNode = restTemplate.exchange(url, HttpMethod.POST,
                deleteSemanticBinaryRequestEntity, MetaDataObjects.class);
            logger.trace("deleteSemanticBinaryNode status code : "+ deleteSemanticBinaryNode.getStatusCodeValue() + " : " + objectMapper.writeValueAsString(deleteSemanticBinaryNode));
            if (!deleteSemanticBinaryNode.getStatusCode().is2xxSuccessful()) {
              logger.trace("deleteSemanticBinaryNode inside if it is not successful : "+ deleteSemanticBinaryNode.getStatusCodeValue());
              deleteSemantic(semanticNode, basePath);
            }
            
            try {
              for (String dataObjectId: listOfDataObjectIds) 
              {
              logger.trace("dataObjectId will be deleted : "+ dataObjectId);
              requestHeaders.set("Content-type", MediaType.APPLICATION_JSON_UTF8_VALUE);
              HttpEntity<?> deleteDataObjectBinaryRequestEntity = new HttpEntity<Object>(dataObjectQuery(dataObjectId,"delete"),requestHeaders);
              logger.trace("transportMetadataURIL server URL {}", transportURI + "/md");
              logger.trace("deleteDataObjectBinaryRequestEntity {}", objectMapper.writeValueAsString(deleteDataObjectBinaryRequestEntity));
              ResponseEntity<?> deleteDataObjectNode = restTemplate.exchange(url, HttpMethod.POST,
                  deleteDataObjectBinaryRequestEntity, DataSemanticObjects.class);
              logger.trace("deleteDataObjectNode status code : "+ deleteDataObjectNode.getStatusCodeValue() + " : " + objectMapper.writeValueAsString(deleteDataObjectNode));
              if (!deleteDataObjectNode.getStatusCode().is2xxSuccessful()) {
                logger.trace("deleteDataObjectNode inside if it is not successful : "+ deleteDataObjectNode.getStatusCodeValue());
                deleteSemantic(semanticNode, basePath);
                break;
              }
              }
            }
            catch (Exception ex) {
              // Delete an entry from semantic store
              logger.trace("Delete an entry from semantic store String dataObjectId: listOfDataObjectIds...");
              deleteSemantic(semanticNode, basePath);
              throw new CreateEntitySAWException("Exception generated during migration while creating an semantic entity ", ex); 
            }
          }
          catch (Exception ex) {
            // Delete an entry from semantic store
            logger.trace("Delete an entry from semantic store while deleting from deleteSemanticBinaryRequestEntity..");
            deleteSemantic(semanticNode, basePath);
            throw new CreateEntitySAWException("Exception generated during migration while creating an semantic entity ", ex); 
          }
        }*/ // end of finally 
       } // end of Id check if it is there then ignore
         
         
         
        else {
           logger.info("This " + semanticNode.get_id() + " already exists on the store");
         }
      }
     }
     else {
       logger.info("migration has been completed on previous installation");
     }
     logger.trace("migration process will ends here");
    }
  
  /**
   * This will generate query for the dataObject to get the dlLocation
   * @param dataObjectId
   * @return String.
   */
  private String dataObjectQuery(String dataObjectId, String operation) {
    return "{\"contents\":{\"keys\":[{\"id\":\""
        + dataObjectId
        + "\"}],\"action\":\""
        + operation
        + "\",\"context\":\"DataObject\"}}";
  }
  
  /**
   * This will generate query for semanticNode  
   * @param operation
   * @return String
   */
  private String semanticNodeQuery(String operation) {
    return "{\"contents\":{\"keys\":[{\"type\":\"semantic\",\"module\":\"ANALYZE\"}],\"action\":\""
        + operation + "\",\"select\":\"everything\",\"context\":\"Semantic\"}}";
  }
  
  private void addSemantic(SemanticNode node, String basePath)
      throws JSONValidationSAWException, CreateEntitySAWException {
    logger.trace("Adding semantic with an Id : {}", node.get_id());
    node.setCreatedBy(node.getUsername());
    ObjectMapper mapper = new ObjectMapper();
    try {
      List<MetaDataStoreStructure> structure = SAWSemanticUtils.node2JSONObject(node, basePath,
          node.get_id(), Action.create, Category.Semantic);
      logger.trace("addSemantic : Before invoking request to MaprDB JSON store :{}",
          mapper.writeValueAsString(structure));
      MetaDataStoreRequestAPI requestMetaDataStore = new MetaDataStoreRequestAPI(structure);
      requestMetaDataStore.process();
    } catch (Exception ex) {
      logger.error("Problem on the storage while creating an entity", ex);
      throw new CreateEntitySAWException("Problem on the storage while creating an entity.", ex);
    }
    logger.trace("Response : " + node.toString());
  }

  private boolean readSemantic(SemanticNode node, String basePath)
      throws JSONValidationSAWException, ReadEntitySAWException {
    Preconditions.checkArgument(node.get_id() != null, "Id is mandatory attribute.");
    logger.trace("reading semantic from the store with an Id : {}", node.get_id());
    SemanticNode nodeRetrieved = null;
    SemanticNode newSemanticNode = null;
    boolean exists = true;
    ObjectMapper mapper = new ObjectMapper();
    try {
      List<MetaDataStoreStructure> structure = SAWSemanticUtils.node2JSONObject(node, basePath,
          node.get_id(), Action.read, Category.Semantic);
      logger.trace("readSemantic : Before invoking request to MaprDB JSON store :{}", mapper.writeValueAsString(structure));
      MetaDataStoreRequestAPI requestMetaDataStore = new MetaDataStoreRequestAPI(structure);
      requestMetaDataStore.process();
      String jsonStringFromStore = requestMetaDataStore.getResult().toString();
      nodeRetrieved = mapper.readValue(jsonStringFromStore, SemanticNode.class);
      logger.trace("Id: {}", nodeRetrieved.get_id());
      nodeRetrieved.setId(nodeRetrieved.get_id());
      nodeRetrieved.setStatusMessage("Entity has retrieved successfully");
      newSemanticNode = new SemanticNode();
      org.springframework.beans.BeanUtils.copyProperties(nodeRetrieved, newSemanticNode,"_id");
    }catch (Exception ex) {
      exists = false;
    }
    return exists;
  }
  
  
  public void deleteSemantic(SemanticNode node, String basePath)
      throws JSONValidationSAWException, DeleteEntitySAWException {
    Preconditions.checkArgument(node.get_id() != null, "Id is mandatory attribute.");
    logger.trace("Deleting semantic from the store with an Id : {}", node.get_id());
    SemanticNode responseObject = new SemanticNode();
    try {
      List<MetaDataStoreStructure> structure = SAWSemanticUtils.node2JSONObject(node, basePath,
          node.get_id(), Action.delete, Category.Semantic);
      logger.trace("Before invoking request to MaprDB JSON store :{}", structure);
      MetaDataStoreRequestAPI requestMetaDataStore = new MetaDataStoreRequestAPI(structure);
      requestMetaDataStore.process();
      responseObject.setId(node.get_id());
    } catch (Exception ex) {
      throw new DeleteEntitySAWException("Problem on the storage while updating an entity", ex);
    }
    
  }
  
  public static void main(String[] args) throws JsonParseException, JsonMappingException, IOException {
    String dataLocation  = "{\n" + 
        "    \"contents\": [\n" + 
        "        {\n" + 
        "            \"dataLocation\": \"/var/sip/services/saw-analyze-samples/sample-spark/data-product.ndjson\"\n" + 
        "        }\n" + 
        "    ]\n" + 
        "}";
    
    String mdService = "{\"contents\":[{\"ANALYZE\":[{\"id\":\"bec6547e-a265-4a88-ab7b-4651f2749261\",\"dataSecurityKey\":\"\",\"type\":\"semantic\",\"module\":\"ANALYZE\",\"metric\":\"sample-spark\",\"metricName\":\"Sample (report) - new\",\"customerCode\":\"SYNCHRONOSS\",\"disabled\":\"false\",\"checked\":\"false\",\"supports\":[{\"label\":\"TABLES\",\"category\":\"table\",\"children\":[{\"label\":\"Report\",\"icon\":\"icon-report\",\"type\":\"table:report\"}]}],\"artifacts\":[{\"artifactName\":\"SALES\",\"columns\":[{\"name\":\"string\",\"type\":\"string\",\"columnName\":\"string\",\"displayName\":\"String\",\"aliasName\":\"\",\"table\":\"sales\",\"joinEligible\":true,\"filterEligible\":true},{\"name\":\"long\",\"type\":\"long\",\"columnName\":\"long\",\"displayName\":\"Long\",\"aliasName\":\"\",\"table\":\"sample\",\"joinEligible\":false,\"filterEligible\":true},{\"name\":\"float\",\"type\":\"float\",\"columnName\":\"float\",\"displayName\":\"Float\",\"aliasName\":\"\",\"table\":\"sales\",\"joinEligible\":false,\"filterEligible\":true},{\"name\":\"date\",\"type\":\"date\",\"columnName\":\"date\",\"displayName\":\"Date\",\"aliasName\":\"\",\"table\":\"sales\",\"joinEligible\":false,\"filterEligible\":true},{\"name\":\"integer\",\"type\":\"integer\",\"columnName\":\"integer\",\"displayName\":\"Integer\",\"aliasName\":\"\",\"table\":\"sample\",\"joinEligible\":false,\"filterEligible\":true},{\"name\":\"double\",\"type\":\"double\",\"columnName\":\"double\",\"displayName\":\"Double\",\"aliasName\":\"\",\"table\":\"sales\",\"joinEligible\":false,\"filterEligible\":true}]},{\"artifactName\":\"PRODUCT\",\"columns\":[{\"name\":\"string_2\",\"type\":\"string\",\"columnName\":\"string_2\",\"displayName\":\"String_2\",\"aliasName\":\"\",\"table\":\"product\",\"joinEligible\":true,\"filterEligible\":true},{\"name\":\"long_2\",\"type\":\"long\",\"columnName\":\"long_2\",\"displayName\":\"Long_2\",\"aliasName\":\"\",\"table\":\"sample\",\"joinEligible\":false,\"filterEligible\":true},{\"name\":\"float_2\",\"type\":\"float\",\"columnName\":\"float_2\",\"displayName\":\"Float_2\",\"aliasName\":\"\",\"table\":\"product\",\"joinEligible\":false,\"filterEligible\":true},{\"name\":\"date_2\",\"type\":\"date\",\"columnName\":\"date_2\",\"displayName\":\"Date_2\",\"aliasName\":\"\",\"table\":\"product\",\"joinEligible\":false,\"filterEligible\":true},{\"name\":\"integer_2\",\"type\":\"integer\",\"columnName\":\"integer_2\",\"displayName\":\"Integer_2\",\"aliasName\":\"\",\"table\":\"sample\",\"joinEligible\":false,\"filterEligible\":true},{\"name\":\"double_2\",\"type\":\"double\",\"columnName\":\"double_2\",\"displayName\":\"Double_2\",\"aliasName\":\"\",\"table\":\"sales\",\"joinEligible\":false,\"filterEligible\":true}]}],\"repository\":{\"storageType\":\"DL\",\"objects\":[{\"EnrichedDataObjectId\":\"SALES::json::1536624682955\",\"displayName\":\"Sample Metric\",\"EnrichedDataObjectName\":\"SALES\",\"description\":\"Sample Metric\",\"lastUpdatedTimestamp\":\"undefined\"},{\"EnrichedDataObjectId\":\"PRODUCT::json::1536624689189\",\"displayName\":\"Product\",\"EnrichedDataObjectName\":\"PRODUCT\",\"description\":\"Product\",\"lastUpdatedTimestamp\":\"undefined\"}],\"_number_of_elements\":2}},{\"id\":\"e20dc090-2f88-4f26-9727-77f87a0b2e1b\",\"dataSecurityKey\":\"\",\"type\":\"semantic\",\"module\":\"ANALYZE\",\"metric\":\"sample-elasticsearch\",\"metricName\":\"Sample (pivot/chart) - new\",\"customerCode\":\"SYNCHRONOSS\",\"disabled\":\"false\",\"checked\":\"false\",\"supports\":[{\"label\":\"TABLES\",\"category\":\"table\",\"children\":[{\"label\":\"Pivot\",\"icon\":\"icon-pivot\",\"type\":\"table:pivot\"},{\"label\":\"Report\",\"icon\":\"icon-report\",\"type\":\"table:esReport\"}]},{\"label\":\"CHARTS\",\"category\":\"charts\",\"children\":[{\"label\":\"Column Chart\",\"icon\":\"icon-vert-bar-chart\",\"type\":\"chart:column\"},{\"label\":\"Stacked Chart\",\"icon\":\"icon-vert-bar-chart\",\"type\":\"chart:stack\"},{\"label\":\"Line Chart\",\"icon\":\"icon-vert-bar-chart\",\"type\":\"chart:line\"},{\"label\":\"Bar Chart\",\"icon\":\"icon-vert-bar-chart\",\"type\":\"chart:bar\"},{\"label\":\"Scatter Plot\",\"icon\":\"icon-vert-bar-chart\",\"type\":\"chart:scatter\"},{\"label\":\"Bubble Chart\",\"icon\":\"icon-vert-bar-chart\",\"type\":\"chart:bubble\"},{\"label\":\"Time Series Chart\",\"icon\":\"icon-timeseries-chart\",\"type\":\"chart:tsline\"},{\"label\":\"Time series multi pane\",\"icon\":\"icon-Candlestick-icon\",\"type\":\"chart:tsareaspline\"},{\"label\":\"Area Chart\",\"icon\":\"icon-area-chart\",\"type\":\"chart:area\"},{\"label\":\"Combo Chart\",\"icon\":\"icon-combo-chart\",\"type\":\"chart:combo\"},{\"label\":\"Pie Chart\",\"icon\":\"icon-pie-chart\",\"type\":\"chart:pie\"}]}],\"esRepository\":{\"storageType\":\"ES\",\"indexName\":\"sample\",\"type\":\"sample\"},\"artifacts\":[{\"artifactName\":\"sample\",\"columns\":[{\"name\":\"string\",\"type\":\"string\",\"columnName\":\"string.keyword\",\"displayName\":\"String\",\"aliasName\":\"\",\"table\":\"sample\",\"joinEligible\":false,\"filterEligible\":true},{\"name\":\"long\",\"type\":\"long\",\"columnName\":\"long\",\"displayName\":\"Long\",\"aliasName\":\"\",\"table\":\"sample\",\"joinEligible\":false,\"kpiEligible\":true,\"filterEligible\":true},{\"name\":\"float\",\"type\":\"float\",\"columnName\":\"float\",\"displayName\":\"Float\",\"aliasName\":\"\",\"table\":\"sample\",\"joinEligible\":false,\"kpiEligible\":true,\"filterEligible\":true},{\"name\":\"date\",\"type\":\"date\",\"columnName\":\"date\",\"displayName\":\"Date\",\"aliasName\":\"\",\"table\":\"sample\",\"joinEligible\":false,\"kpiEligible\":true,\"filterEligible\":true},{\"name\":\"integer\",\"type\":\"integer\",\"columnName\":\"integer\",\"displayName\":\"Integer\",\"aliasName\":\"\",\"table\":\"sample\",\"joinEligible\":false,\"kpiEligible\":true,\"filterEligible\":true},{\"name\":\"double\",\"type\":\"double\",\"columnName\":\"double\",\"displayName\":\"Double\",\"aliasName\":\"\",\"table\":\"sales\",\"joinEligible\":false,\"kpiEligible\":true,\"filterEligible\":true}]}],\"repository\":{\"storageType\":\"DL\",\"objects\":[{\"EnrichedDataObjectId\":\"SALES::json::1536624682955\",\"displayName\":\"Sample Metric\",\"EnrichedDataObjectName\":\"SALES\",\"description\":\"Sample Metric\",\"lastUpdatedTimestamp\":\"undefined\"},{\"EnrichedDataObjectId\":\"PRODUCT::json::1536624689189\",\"displayName\":\"Product\",\"EnrichedDataObjectName\":\"PRODUCT\",\"description\":\"Product\",\"lastUpdatedTimestamp\":\"undefined\"}],\"_number_of_elements\":2}}]}]}";
    
    ObjectMapper objectMapper = new ObjectMapper();
    objectMapper.configure(JsonParser.Feature.ALLOW_UNQUOTED_FIELD_NAMES, true);
    objectMapper.enable(DeserializationFeature.FAIL_ON_READING_DUP_TREE_KEY);
    DataSemanticObjects contentDataLocation = objectMapper.readValue(dataLocation, DataSemanticObjects.class);
    MetaDataObjects metaDataObjects = objectMapper.readValue(mdService, MetaDataObjects.class);
    
    List<BinarySemanticNode> binaryNodes = objectMapper.readValue(objectMapper.writeValueAsString(metaDataObjects.getContents().get(0).getAnalyze()), 
        new TypeReference<List<BinarySemanticNode>>(){});
    SemanticNode  semanticNode = null;
    for (BinarySemanticNode binarySemanticNode : binaryNodes) {
      String id = binarySemanticNode.getId();
      semanticNode = new SemanticNode();
      semanticNode.set_id(id);
      semanticNode.setId(id);
      semanticNode.setCustomerCode(binarySemanticNode.getCustomerCode());
      semanticNode.setModule(com.synchronoss.saw.semantic.model.request.SemanticNode.Module.ANALYZE);
      semanticNode.setProjectCode(binarySemanticNode.getProjectCode()!=null ? binarySemanticNode.getProjectCode() : "workbench");
      semanticNode.setUsername("sipadmin@synchronoss.com");
      semanticNode.setMetricName(binarySemanticNode.getMetricName());
      if (binarySemanticNode.getEsRepository()!=null) 
        {semanticNode.setEsRepository(binarySemanticNode.getEsRepository());}
      else {
      JsonNode repository = objectMapper.readTree(objectMapper.writeValueAsString(binarySemanticNode.getRepository()));
      ArrayNode repositories = (ArrayNode) repository.get("objects");
      ObjectNode newRepoNode = null;
      List<Object> repositoryObjects = new ArrayList<>();
      for (JsonNode repositoryNode : repositories) {
        String dataObjectId = repositoryNode.get("EnrichedDataObjectId").asText();
        String dataSetName  = repositoryNode.get("EnrichedDataObjectName").asText();
        logger.trace("dataObjectId : " + dataObjectId);
        logger.trace("dataSetName : " + dataSetName);
        String format = "parquet";
        newRepoNode = objectMapper.createObjectNode();
        newRepoNode.put("name", dataSetName);
        newRepoNode.put("format", format);
        repositoryObjects.add(newRepoNode);
      }
      semanticNode.setRepository(repositoryObjects);
      }
      semanticNode.setArtifacts(binarySemanticNode.getArtifacts());
      semanticNode.setSupports(binarySemanticNode.getSupports());
      System.out.println(objectMapper.writeValueAsString(semanticNode));
    }
    JsonNode data = objectMapper.readTree(objectMapper.writeValueAsString(contentDataLocation.getContents().get(0)));
    System.out.println(data.get("dataLocation"));
    Path file = Paths.get("/var/sip/services/saw-analyze-samples/sample-spark/");
    
    System.out.println(file.getFileName());
    System.out.println((FilenameUtils.getExtension(file.getFileName().toString()).equals(""))? "empty" : "data" );
 
    

  }
}
