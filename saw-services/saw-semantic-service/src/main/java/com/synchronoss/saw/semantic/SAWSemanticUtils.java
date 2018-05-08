package com.synchronoss.saw.semantic;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;
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
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.synchronoss.saw.semantic.model.NodeCategory;
import com.synchronoss.saw.semantic.model.request.SemanticNode;
import sncr.bda.store.generic.schema.Action;
import sncr.bda.store.generic.schema.Category;
import sncr.bda.store.generic.schema.MetaDataStoreStructure;
import sncr.bda.store.generic.schema.Query;

@Component
public class SAWSemanticUtils {
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

  private static Set<String> collectHeaders(List<Map<String, Object>> flatJson) {
    Set<String> headers = new LinkedHashSet<String>();
    for (Map<String, Object> map : flatJson) {
        headers.addAll(map.keySet());
    }
    return headers;
}
  
  public static List<MetaDataStoreStructure> node2JSONObject(NodeCategory node, String basePath, String Id, Action action, Category category) throws JsonProcessingException {
    MetaDataStoreStructure metaDataStoreStructure = new MetaDataStoreStructure();
    if (node != null) {
      metaDataStoreStructure.setSource(node);
    }
    if (Id != null) {
      metaDataStoreStructure.setId(Id);
    }
    metaDataStoreStructure.setAction(action);
    metaDataStoreStructure.setCategory(category);
    metaDataStoreStructure.setXdfRoot(basePath);
    List<MetaDataStoreStructure> listOfMetadata = new ArrayList<>();
    listOfMetadata.add(metaDataStoreStructure);
    return listOfMetadata;
  }
  
  public static String node2JsonString(SemanticNode node, String basePath, String Id, Action action, Category category, Query query) 
      throws JsonProcessingException
  {
    ObjectMapper objectMapper = new ObjectMapper();
    MetaDataStoreStructure metaDataStoreStructure = new MetaDataStoreStructure();
   
    if(node !=null){
      metaDataStoreStructure.setSource(node);
    }
    if (Id !=null){
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
  
  public static void copyFileUsingFileChannels(File source, File dest) throws IOException {
    FileChannel inputChannel = null;
    FileChannel outputChannel = null;
    FileInputStream fileInputStream = null;
    FileOutputStream fileOutputStream = null;
    try {
      fileInputStream = new FileInputStream(source);
      fileOutputStream = new FileOutputStream(dest);
      inputChannel = fileInputStream.getChannel();
      outputChannel = fileOutputStream.getChannel();
      outputChannel.transferFrom(inputChannel, 0, inputChannel.size());
    } finally {
      inputChannel.close();
      outputChannel.close();
      fileInputStream.close();
      fileOutputStream.close();
    }
  }
  
}
