package com.synchronoss.saw.observe;

import com.fasterxml.jackson.core.JsonParser;
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
import com.synchronoss.saw.observe.model.store.Query;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

@Component
public class ObserveUtils {

  public final String schemaFileName = "payload-schema.json";

  /**
   * getObserveNode.
   *
   * @param json Json
   * @param node node
   * @return Observe
   * @throws JsonProcessingException when json parsing error
   * @throws IOException when failed to read json string
   */
  public static Observe getObserveNode(String json, String node)
      throws JsonProcessingException, IOException {
    ObjectMapper objectMapper = new ObjectMapper();
    objectMapper.configure(JsonParser.Feature.ALLOW_UNQUOTED_FIELD_NAMES, true);
    objectMapper.enable(DeserializationFeature.FAIL_ON_READING_DUP_TREE_KEY);
    JsonNode objectNode = objectMapper.readTree(json);
    JsonNode contentNode = objectNode.get(node);
    JsonNode observeNode = contentNode.get("observe").get(0);
    String jsonObserve = "{ \"observe\" :" + observeNode.toString() + "}";
    JsonNode observeNodeIndependent = objectMapper.readTree(jsonObserve);
    ObserveNode observeTreeNode =
        objectMapper.treeToValue(observeNodeIndependent, ObserveNode.class);
    Observe observeTree = observeTreeNode.getObserve();
    return observeTree;
  }

  /**
   * node2JsonString.
   *
   * @param node Observe Object
   * @param basePath basePath
   * @param id object Id
   * @param action Node Action
   * @param category category Id
   * @return String JsonString
   * @throws JsonProcessingException when failed to parse json.
   */
  public static String node2JsonString(
      Observe node, String basePath, String id, Action action, Category category)
      throws JsonProcessingException {
    MetaDataStoreStructure metaDataStoreStructure = new MetaDataStoreStructure();

    if (node != null) {
      metaDataStoreStructure.setSource(node);
    }
    if (id != null) {
      metaDataStoreStructure.setId(id);
    }
    metaDataStoreStructure.setAction(action);
    metaDataStoreStructure.setCategory(category);
    metaDataStoreStructure.setXdfRoot(basePath);
    List<MetaDataStoreStructure> listOfMetadata = new ArrayList<>();
    listOfMetadata.add(metaDataStoreStructure);
    ObjectMapper objectMapper = new ObjectMapper();
    return objectMapper.writeValueAsString(listOfMetadata);
  }

  /**
   * node2JsonString.
   *
   * @param node Observe Object
   * @param basePath basePath
   * @param id object Id
   * @param action Node action
   * @param category category Id
   * @param query Observe query
   * @return String returns Json String
   * @throws JsonProcessingException when failed to parse json
   */
  public static String node2JsonString(
      Observe node, String basePath, String id, Action action, Category category, Query query)
      throws JsonProcessingException {
    MetaDataStoreStructure metaDataStoreStructure = new MetaDataStoreStructure();

    if (node != null) {
      metaDataStoreStructure.setSource(node);
    }
    if (id != null) {
      metaDataStoreStructure.setId(id);
    }
    if (query != null) {
      metaDataStoreStructure.setQuery(query);
    }
    metaDataStoreStructure.setAction(action);
    metaDataStoreStructure.setCategory(category);
    metaDataStoreStructure.setXdfRoot(basePath);
    List<MetaDataStoreStructure> listOfMetadata = new ArrayList<>();
    listOfMetadata.add(metaDataStoreStructure);
    ObjectMapper objectMapper = new ObjectMapper();
    return objectMapper.writeValueAsString(listOfMetadata);
  }

  /**
   * nodeMetaDataStoreStructure.
   *
   * @param node Observe Object
   * @param basePath basePath
   * @param output output String
   * @param id Object Id
   * @param action Node action
   * @param category category Id
   * @return MetaDataStoreStructure List returns list of MetaDataStoreStructure
   * @throws JsonProcessingException when failed to parse json.
   */
  public static List<MetaDataStoreStructure> nodeMetaDataStoreStructure(
      Observe node, String basePath, String output, String id, Action action, Category category)
      throws JsonProcessingException {
    MetaDataStoreStructure metaDataStoreStructure = new MetaDataStoreStructure();
    if (node != null) {
      metaDataStoreStructure.setSource(node);
    }
    if (id != null) {
      metaDataStoreStructure.setId(id);
    }
    metaDataStoreStructure.setAction(action);
    metaDataStoreStructure.setCategory(category);
    metaDataStoreStructure.setXdfRoot(basePath);
    List<MetaDataStoreStructure> listOfMetadata = new ArrayList<>();
    listOfMetadata.add(metaDataStoreStructure);
    return listOfMetadata;
  }

  /**
   * prepareResponse.
   *
   * @param node node
   * @param message message
   * @return ObserveResponse
   */
  public static ObserveResponse prepareResponse(Observe node, String message) {
    ObserveResponse createresponse = new ObserveResponse();
    createresponse.setMessage(message);
    createresponse.setId(node.get_id());
    createresponse.setId(node.getEntityId());
    Content content = new Content();
    List<Observe> listOfObserve = new ArrayList<>();
    listOfObserve.add(node);
    content.setObserve(listOfObserve);
    createresponse.setContents(content);
    return createresponse;
  }

  /**
   * getMapper.
   *
   * @return ObjectMapper
   */
  public ObjectMapper getMapper() {
    ObjectMapper objectMapper = new ObjectMapper();
    objectMapper.configure(DeserializationFeature.UNWRAP_ROOT_VALUE, false);
    return objectMapper;
  }

  /**
   * createOkResponse.
   *
   * @param body body
   * @param <T> Type
   * @return ResponseEntity
   */
  public <T> ResponseEntity<T> createOkResponse(T body) {
    return createResponse(body, HttpStatus.OK);
  }

  /**
   * createResponse.
   *
   * @param result result
   * @param <T> type
   * @return ResponseEntity
   */
  public <T> ResponseEntity<T> createResponse(ResponseEntity<T> result) {

    ResponseEntity<T> response = createResponse(result.getBody(), result.getStatusCode());
    return response;
  }

  /**
   * createResponse.
   *
   * @param body body
   * @param httpStatus httpStatus
   * @param <T> type
   * @return ResponseEntity
   */
  public <T> ResponseEntity<T> createResponse(T body, HttpStatus httpStatus) {
    return new ResponseEntity<>(body, httpStatus);
  }

  /**
   * getClassPathResources.
   *
   * @param filename filename
   * @return Resource
   */
  public Resource getClassPathResources(String filename) {
    return new ClassPathResource(filename);
  }

  /**
   * jsonSchemaValidate.
   *
   * @param jsonDataString jsonDataString
   * @param filename filename
   * @return Boolean jsonSchema validation.
   * @throws IOException when File not found.
   * @throws ProcessingException when failed to parse json.
   */
  public Boolean jsonSchemaValidate(String jsonDataString, String filename)
      throws IOException, ProcessingException {
    final JsonNode data = JsonLoader.fromString(jsonDataString);
    final JsonNode schema = JsonLoader.fromURL(this.getClassPathResources(filename).getURL());
    final JsonSchemaFactory factory = JsonSchemaFactory.byDefault();
    JsonValidator validator = factory.getValidator();
    ProcessingReport report = validator.validate(schema, data);
    return report.isSuccess();
  }
}
