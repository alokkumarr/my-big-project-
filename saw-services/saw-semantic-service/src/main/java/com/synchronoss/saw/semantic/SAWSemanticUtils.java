package com.synchronoss.saw.semantic;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import org.apache.commons.io.FilenameUtils;
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
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.common.base.Preconditions;
import com.synchronoss.saw.semantic.model.DataSet;
import com.synchronoss.saw.semantic.model.request.SemanticNode;
import sncr.bda.datasets.conf.DataSetProperties;
import sncr.bda.store.generic.schema.Action;
import sncr.bda.store.generic.schema.Category;
import sncr.bda.store.generic.schema.MetaDataStoreStructure;
import sncr.bda.store.generic.schema.Query;

@Component
public class SAWSemanticUtils {
  public final static String COMMA = ",";
  public final static String PATH_SEARCH = "action.content.";

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
  public static void checkMandatoryFields (SemanticNode node) {
    Preconditions.checkArgument(node!=null, "Request body is empty");
    Preconditions.checkArgument(node.getUsername()!=null, "username cannot be null");
    Preconditions.checkArgument(node.getCustomerCode()!=null, "customer code cannot be null");
    Preconditions.checkArgument(node.getProjectCode()!=null, "project code cannot be null");
    Preconditions.checkArgument(node.getArtifacts()!=null, "artifacts code cannot be null");
    Preconditions.checkArgument(node.getMetricName()!=null, "metric name code cannot be null");
   Preconditions.checkArgument(node.getParentDataSetIds()!=null && node.getParentDataSetIds().size()!=0, "Parent DataSetsId cannot be null");
  }
  
  public static List<MetaDataStoreStructure> node2JSONObject(SemanticNode node, String basePath, String Id, Action action, Category category) throws JsonProcessingException {
    MetaDataStoreStructure metaDataStoreStructure = new MetaDataStoreStructure();
    if (node != null) {
      metaDataStoreStructure.setSource(node);
    }
    if (node.get_id()!=null || Id !=null) {
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
  
  public static Map<String, Object> removeJSONNode(Object jsonString,
      String attributedToBeRemoved) throws JsonProcessingException, IOException {
    ObjectMapper objectMapper = new ObjectMapper();
    objectMapper.configure(JsonParser.Feature.ALLOW_UNQUOTED_FIELD_NAMES, true);
    objectMapper.enable(DeserializationFeature.FAIL_ON_READING_DUP_TREE_KEY);
    JsonNode node = objectMapper.readTree(objectMapper.writeValueAsString(jsonString));
    ObjectNode rootNode = (ObjectNode) node;
    Map<String, Object> results = new HashMap<String, Object>();
    Iterator<Map.Entry<String, JsonNode>> fields = rootNode.fields();
    while (fields.hasNext()) {
      Map.Entry<String, JsonNode> next = fields.next();
      if (!next.getKey().equals(attributedToBeRemoved)) {
        results.put(next.getKey(), objectMapper.treeToValue(next.getValue(), String.class));
      }
    }
    return results;
  }

  
  public static void main(String[] args) throws JsonProcessingException, IOException {
    String json = "{\"name\":\"normal.csv\",\"size\":254743,\"d\":false,\"cat\":\"root\"}";
    ObjectMapper objectMapper = new ObjectMapper();
    objectMapper.configure(JsonParser.Feature.ALLOW_UNQUOTED_FIELD_NAMES, true);
    objectMapper.enable(DeserializationFeature.FAIL_ON_READING_DUP_TREE_KEY);
    JsonNode nodeJ = objectMapper.readTree(json);
    System.out.println(nodeJ);
    System.out.println(System.getProperty("java.io.tmpdir"));
    System.out.println("data.csv".substring("data.csv".indexOf('.'), "data.csv".length()));
    System.out.println(FilenameUtils.getFullPathNoEndSeparator("/apps/sncr"));
    String row1 = "{\"_id\":\"xda-ux-sr-comp-dev::TRTEST_JEXLREF_SS\",\"system\":{\"user\":\"A_user\",\"project\":\"xda-ux-sr-comp-dev\",\"type\":\"fs\",\"format\":\"parquet\",\"name\":\"TRTEST_JEXLREF_SS\",\"physicalLocation\":\"data\",\"catalog\":\"dout\",\"numberOfFiles\":\"1\"},\"userData\":{\"createdBy\":\"S.Ryabov\",\"category\":\"subcat1\",\"description\":\"Transformer component test case: transformed records\"},\"transformations\":[],\"asOutput\":\"xda-ux-sr-comp-dev::transformer::165407713\",\"asOfNow\":{\"status\":\"SUCCESS\",\"started\":\"20180209-195737\",\"finished\":\"20180209-195822\",\"aleId\":\"xda-ux-sr-comp-dev::1518206302595\",\"batchId\":\"BJEXLREFSS\"}}";
    String row2= "{\"_id\":\"xda-ux-sr-comp-dev::tc220_1\",\"system\":{\"user\":\"A_user\",\"project\":\"xda-ux-sr-comp-dev\",\"type\":\"fs\",\"format\":\"parquet\",\"name\":\"tc220_1\",\"physicalLocation\":\"hdfs:///data/bda/xda-ux-sr-comp-dev/dl/fs/dout/tc220_1/data\",\"catalog\":\"dout\",\"numberOfFiles\":\"2\"},\"userData\":{\"createdBy\":\"S.Ryabov\",\"category\":\"subcat1\",\"description\":\"SQL component test case\"},\"transformations\":[{\"asOutput\":\"xda-ux-sr-comp-dev::sql::1192296717\"}],\"asOutput\":\"xda-ux-sr-comp-dev::sql::522761969\",\"asOfNow\":{\"status\":\"SUCCESS\",\"started\":\"20180223-220236\",\"finished\":\"20180223-220316\",\"aleId\":\"xda-ux-sr-comp-dev::1519423396639\",\"batchId\":\"BSQL10PM\"}}";
    List<DataSet> sets = new ArrayList<>();
    sets.add(objectMapper.readValue(row1, DataSet.class));
    sets.add(objectMapper.readValue(row2, DataSet.class));
    DataSet dataset = sets.get(0);
    System.out.println("Dataset : " +objectMapper.writeValueAsString(dataset));
    JsonNode node = objectMapper.readTree(objectMapper.writeValueAsString(dataset.getSystem()));
    Map<String, Object> results = new HashMap<String, Object>();
    ObjectNode rootNode = (ObjectNode) node;
    Iterator<Map.Entry<String, JsonNode>> fields = rootNode.fields();
    while (fields.hasNext()) {
      Map.Entry<String, JsonNode> next = fields.next();
      if (!next.getKey().equals("physicalLocation")) {
        results.put(
            next.getKey(), objectMapper.treeToValue(next.getValue(), String.class));
      }
    }
    dataset.setSystem(results);
    System.out.println(objectMapper.writeValueAsString(dataset));
    System.out.println(DataSetProperties.UserData.toString());
  }

  
}
