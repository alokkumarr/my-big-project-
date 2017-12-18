package com.synchronoss.saw.observe;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.fge.jackson.JsonLoader;
import com.github.fge.jsonschema.core.exceptions.ProcessingException;
import com.github.fge.jsonschema.core.report.ProcessingReport;
import com.github.fge.jsonschema.main.JsonSchemaFactory;
import com.github.fge.jsonschema.main.JsonValidator;
import com.synchronoss.saw.observe.model.Content;
import com.synchronoss.saw.observe.model.Observe;
import com.synchronoss.saw.observe.model.ObserveNode;
import com.synchronoss.saw.observe.model.ObserveResponse;
import com.synchronoss.saw.observe.model.store.MetaDataStoreStructure;
import com.synchronoss.saw.observe.model.store.MetaDataStoreStructure.Action;
import com.synchronoss.saw.observe.model.store.MetaDataStoreStructure.Category;

@Component
public class ObserveUtils {
  public final String SCHEMA_FILENAME = "payload-schema.json";

  public ObjectMapper getMapper() {
    ObjectMapper objectMapper = new ObjectMapper();
    objectMapper.configure(DeserializationFeature.UNWRAP_ROOT_VALUE, false);
    return objectMapper;
  }

  public <T> ResponseEntity<T> createOkResponse(T body) {
    return createResponse(body, HttpStatus.OK);
  }

  /**
   * Clone an existing result as a new one, filtering out http headers that not should be moved on
   * and so on...
   *
   * @param result
   * @param <T>
   * @return
   */
  public <T> ResponseEntity<T> createResponse(ResponseEntity<T> result) {

    ResponseEntity<T> response = createResponse(result.getBody(), result.getStatusCode());
    return response;
  }

  public <T> ResponseEntity<T> createResponse(T body, HttpStatus httpStatus) {
    return new ResponseEntity<>(body, httpStatus);
  }

  public Resource getClassPathResources(String filename) {
    return new ClassPathResource(filename);
  }

  public Boolean jsonSchemaValidate(String jsonDataString, String filename)
      throws IOException, ProcessingException {
    final JsonNode data = JsonLoader.fromString(jsonDataString);
    final JsonNode schema = JsonLoader.fromURL(this.getClassPathResources(filename).getURL());
    final JsonSchemaFactory factory = JsonSchemaFactory.byDefault();
    JsonValidator validator = factory.getValidator();
    ProcessingReport report = validator.validate(schema, data);
    return report.isSuccess();
  }
  
  public static Observe getObserveNode (String json, String node) throws JsonProcessingException, IOException {
    ObjectMapper objectMapper = new ObjectMapper();
    objectMapper.enable(DeserializationFeature.FAIL_ON_READING_DUP_TREE_KEY);
    JsonNode objectNode = objectMapper.readTree(json);
    JsonNode observeNode = objectNode.get(node);
    String jsonObserve = "{ \"observe\" :" + observeNode.toString() + "}";
    JsonNode observeNodeIndependent = objectMapper.readTree(jsonObserve);
    ObserveNode observeTreeNode = objectMapper.treeToValue(observeNodeIndependent, ObserveNode.class);
    Observe observeTree = observeTreeNode.getObserve();
    return observeTree; 
  }
  
  public static String node2JsonString(Observe node, String basePath, String Id, Action action, Category category) 
      throws JsonProcessingException
  {
    ObjectMapper objectMapper = new ObjectMapper();
    MetaDataStoreStructure metaDataStoreStructure = new MetaDataStoreStructure();
    if(node !=null){
      String jsonObserve = "{ \"observe\" :" + objectMapper.writeValueAsString(node) + "}";
      metaDataStoreStructure.setSource(jsonObserve);
    }
    if (Id !=null){
      metaDataStoreStructure.setId(Id);
    }
    metaDataStoreStructure.setAction(action);
    metaDataStoreStructure.setCategory(category);
    metaDataStoreStructure.setXdfRoot(basePath);
    List<MetaDataStoreStructure> listOfMetadata = new ArrayList<>();
    listOfMetadata.add(metaDataStoreStructure);
   
    return objectMapper.writeValueAsString(listOfMetadata);
  }
  
  public static ObserveResponse prepareResponse(Observe node, String message){
    ObserveResponse createresponse = new ObserveResponse();
    createresponse.setMessage(message);
    createresponse.setId(node.get_id());
    Content content = new Content();
    List<Observe> listOfObserve = new ArrayList<>();
    listOfObserve.add(node);
    content.setObserve(listOfObserve);
    createresponse.setContents(content);
    return createresponse;
  }
  
}
