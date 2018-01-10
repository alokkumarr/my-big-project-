package com.synchronoss.saw.storage.proxy;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

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
import com.synchronoss.saw.storage.proxy.model.Content;
import com.synchronoss.saw.storage.proxy.model.StorageProxy;
import com.synchronoss.saw.storage.proxy.model.StorageProxyNode;
import com.synchronoss.saw.storage.proxy.model.StorageProxyResponse;

@Component
public class StorageProxyUtils {
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

  public static Boolean jsonSchemaValidate(StorageProxyNode proxy, String path)
      throws IOException, ProcessingException {
    // Validating JSON against schema
    Boolean result = true;
    ObjectMapper objectMapper = new ObjectMapper();
    objectMapper.configure(JsonParser.Feature.ALLOW_UNQUOTED_FIELD_NAMES, true);
    objectMapper.enable(DeserializationFeature.FAIL_ON_READING_DUP_TREE_KEY);
    JsonSchemaFactory factory = JsonSchemaFactory.byDefault();
    JsonValidator validator = factory.getValidator();
    final JsonNode data = JsonLoader.fromString(objectMapper.writeValueAsString(proxy.getProxy()));
    final JsonNode schema = JsonLoader.fromFile(new File(path));
    ProcessingReport report = validator.validate(schema, data);
    if (report.isSuccess() == false) {
      result = false;
      throw new ProcessingException(report.toString());
    }
    return result;
 }
  
  public static StorageProxyNode getProxyNode (String json, String node) throws JsonProcessingException, IOException {
    ObjectMapper objectMapper = new ObjectMapper();
    objectMapper.configure(JsonParser.Feature.ALLOW_UNQUOTED_FIELD_NAMES, true);
    objectMapper.enable(DeserializationFeature.FAIL_ON_READING_DUP_TREE_KEY);
    JsonNode objectNode = objectMapper.readTree(json);
    JsonNode contentNode = objectNode.get(node);
    JsonNode proxyNode = contentNode.get("proxy");
    String jsonProxy = "{ \"proxy\" :" + proxyNode.toString() + "}";
    JsonNode proxyNodeIndependent = objectMapper.readTree(jsonProxy);
    StorageProxyNode proxyTreeNode = objectMapper.treeToValue(proxyNodeIndependent, StorageProxyNode.class);
    return proxyTreeNode; 
  }
  
  public static StorageProxyResponse prepareResponse(List<StorageProxy> node, String message){
    StorageProxyResponse createresponse = new StorageProxyResponse();
    createresponse.setMessage(message);
    Content content = new Content();
    content.setProxy(node);
    createresponse.setContents(content);
    return createresponse;
  }
  
}
