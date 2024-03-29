package com.synchronoss.saw.storage.proxy;

import com.synchronoss.sip.utils.SipCommonUtils;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import org.apache.commons.lang3.StringUtils;
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
import com.google.common.base.Preconditions;
import com.synchronoss.saw.storage.proxy.model.StorageProxy;
import com.synchronoss.saw.storage.proxy.model.StorageProxyNode;
import sncr.bda.store.generic.schema.Action;
import sncr.bda.store.generic.schema.Category;
import sncr.bda.store.generic.schema.MetaDataStoreStructure;
import sncr.bda.store.generic.schema.Query;

@Component
public class StorageProxyUtils {
  public final String SCHEMA_FILENAME = "payload-schema.json";
  public final static String COMMA = ",";

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

  public static Boolean jsonSchemaValidate(StorageProxy proxy, String path)
      throws IOException, ProcessingException {
    String normalizedPath = SipCommonUtils.normalizePath(path);
    // Validating JSON against schema
    Boolean result = true;
    ObjectMapper objectMapper = new ObjectMapper();
    objectMapper.configure(JsonParser.Feature.ALLOW_UNQUOTED_FIELD_NAMES, true);
    objectMapper.enable(DeserializationFeature.FAIL_ON_READING_DUP_TREE_KEY);
    JsonSchemaFactory factory = JsonSchemaFactory.byDefault();
    JsonValidator validator = factory.getValidator();
    final JsonNode data = JsonLoader.fromString(objectMapper.writeValueAsString(proxy));
    final JsonNode schema = JsonLoader.fromFile(new File(normalizedPath));
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
  
  public static StorageProxy prepareResponse(StorageProxy node, String message){
    node.setStatusMessage(message);
    return node;
  }
  
  
  private static Set<String> collectHeaders(List<Map<String, Object>> flatJson) {
    Set<String> headers = new LinkedHashSet<String>();
    for (Map<String, Object> map : flatJson) {
        headers.addAll(map.keySet());
    }
    return headers;
}
  
  private static String getSeperatedColumns(Set<String> headers, Map<String, Object> map, String separator) {
    List<Object> items = new ArrayList<Object>();
    for (String header : headers) {
        Object value = map.get(header) == null ? "" : map.get(header); 
        items.add(value);
    }

    return StringUtils.join(items.toArray(), separator);
}
  
  public static List<Object> getTabularFormat(List<Map<String, Object>> flatJson, String separator) {
    List<Object> data = new ArrayList<>();
    Set<String> headers = collectHeaders(flatJson);
    String csvString = StringUtils.join(headers.toArray(), separator);
    data.add(csvString);
    for (Map<String, Object> map : flatJson) {
        csvString = getSeperatedColumns(headers, map, separator);
        data.add(csvString);
    }
    return data;
}
  
  public static Set<String> collectOrderedHeaders(List<Map<String, Object>> flatJson) {
    Set<String> headers = new TreeSet<String>();
    for (Map<String, Object> map : flatJson) {
        headers.addAll(map.keySet());
    }
    return headers;
}
  
  public static void checkMandatoryFields(StorageProxy node) {
    Preconditions.checkArgument(node != null, "Request body is empty");
    Preconditions.checkArgument(node.getStorage() != null, "storage cannot be null");
    Preconditions.checkArgument(node.getProductCode() != null, "project code cannot be null");
    Preconditions.checkArgument(node.getModuleName() != null, "module name cannot be null");
    Preconditions.checkArgument(node.getRequestBy() != null, "requested By cannot be null");
    Preconditions.checkArgument(node.getContent() != null, "content cannot be null");
  }
  
  public static List<MetaDataStoreStructure> node2JSONObject(StorageProxy node, String basePath, String Id, Action action, Category category) throws JsonProcessingException {
    MetaDataStoreStructure metaDataStoreStructure = new MetaDataStoreStructure();
    if (node != null) {
      metaDataStoreStructure.setSource(node);
    }
    if (Id !=null) {
      metaDataStoreStructure.setId(Id);
    }
    metaDataStoreStructure.setAction(action);
    metaDataStoreStructure.setCategory(category);
    metaDataStoreStructure.setXdfRoot(basePath);
    List<MetaDataStoreStructure> listOfMetadata = new ArrayList<>();
    listOfMetadata.add(metaDataStoreStructure);
    return listOfMetadata;
  }
  public static String node2JSONString(StorageProxy node, String basePath, String Id, Action action, Category category,  Query query) throws JsonProcessingException {
    ObjectMapper objectMapper = new ObjectMapper();
    MetaDataStoreStructure metaDataStoreStructure = new MetaDataStoreStructure();
    if (node != null) {
      metaDataStoreStructure.setSource(node);
    }
    if (Id !=null) {
      metaDataStoreStructure.setId(Id);
    }
    if (query!=null){
      metaDataStoreStructure.setQuery(query);
    }
    metaDataStoreStructure.setAction(action);
    metaDataStoreStructure.setCategory(category);
    metaDataStoreStructure.setXdfRoot(basePath);
    List<MetaDataStoreStructure> listOfMetadata = new ArrayList<>();
    listOfMetadata.add(metaDataStoreStructure);
    return objectMapper.writeValueAsString(listOfMetadata);
  }

}
