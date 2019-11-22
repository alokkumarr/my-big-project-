package com.synchronoss.saw.util;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.common.base.Preconditions;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.synchronoss.bda.sip.jwt.TokenParser;
import com.synchronoss.bda.sip.jwt.token.ProductModuleFeature;
import com.synchronoss.bda.sip.jwt.token.ProductModules;
import com.synchronoss.bda.sip.jwt.token.Products;
import com.synchronoss.bda.sip.jwt.token.Ticket;
import com.synchronoss.saw.analysis.modal.Analysis;
import com.synchronoss.saw.semantic.model.DataSet;
import com.synchronoss.saw.semantic.model.request.SemanticNode;
import com.synchronoss.sip.utils.Privileges;
import com.synchronoss.sip.utils.Privileges.PrivilegeNames;
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
import javax.servlet.http.HttpServletRequest;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import sncr.bda.datasets.conf.DataSetProperties;
import sncr.bda.store.generic.schema.Action;
import sncr.bda.store.generic.schema.Category;
import sncr.bda.store.generic.schema.MetaDataStoreStructure;
import sncr.bda.store.generic.schema.Query;

@Component
public class SipMetadataUtils {

  public static final String COMMA = ",";
  public static final String PATH_SEARCH = "action.content.";

  private static final Logger logger = LoggerFactory.getLogger(SipMetadataUtils.class);

  /**
   * collectHeaders.
   *
   * @param flatJson Json Object.
   * @return Set
   */
  private static Set<String> collectHeaders(List<Map<String, Object>> flatJson) {
    Set<String> headers = new LinkedHashSet<String>();
    for (Map<String, Object> map : flatJson) {
      headers.addAll(map.keySet());
    }
    return headers;
  }

  /**
   * This is used to set the audit information.
   *
   * @param node {@link SemanticNode}
   * @param headers {@link Map}
   * @return {@link SemanticNode}
   */
  public static SemanticNode setAuditInformation(SemanticNode node, Map<String, String> headers) {
    logger.trace("Setting audit information starts here");
    if (headers.get("x-customercode") != null) {
      node.setCustomerCode(headers.get("x-customercode"));
      logger.trace("x-customercode:" + headers.get("x-customercode"));
    }
    if (headers.get("x-userName") != null) {
      node.setUsername(headers.get("x-userName"));
      node.setCreatedBy(headers.get("x-userName"));
      logger.trace("x-userName:" + headers.get("x-userName"));
    }
    logger.trace("Setting audit information ends here");
    return node;
  }

  /**
   * check SemanticNode MandatoryFields.
   *
   * @param node SemanticNode
   */
  public static void checkSemanticMandatoryFields(SemanticNode node) {
    Preconditions.checkArgument(node != null, "Request body is empty");
    Preconditions.checkArgument(node.getUsername() != null, "username cannot be null");
    Preconditions.checkArgument(node.getCustomerCode() != null, "customer code cannot be null");
    Preconditions.checkArgument(node.getProjectCode() != null, "project code cannot be null");
    Preconditions.checkArgument(node.getArtifacts() != null, "artifacts code cannot be null");
    Preconditions.checkArgument(node.getMetricName() != null, "metric name code cannot be null");
    Preconditions.checkArgument(
        node.getParentDataSetIds() != null && node.getParentDataSetIds().size() != 0,
        "Parent DataSetsId cannot be null");
  }

  /**
   * check Analysis MandatoryFields.
   *
   * @param analysis Analysis
   */
  public static void checkAnalysisMandatoryFields(Analysis analysis) {}

  /**
   * This method is used to convert node to json format.
   *
   * @param node {@link SemanticNode}.
   * @param basePath base path.
   * @param id unique id.
   * @param action crud operation.
   * @param category {@link
   *     com.synchronoss.saw.observe.model.store.MetaDataStoreStructure.Category}}
   * @return {@link MetaDataStoreStructure}
   * @throws JsonProcessingException exception
   */
  public static List<MetaDataStoreStructure> node2JsonObject(
      SemanticNode node, String basePath, String id, Action action, Category category)
      throws JsonProcessingException {
    MetaDataStoreStructure metaDataStoreStructure = new MetaDataStoreStructure();
    if (node != null) {
      metaDataStoreStructure.setSource(node);
    }
    if (node.get_id() != null || id != null) {
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
   * SemanticNode to JsonString.
   *
   * @param node SemanticNode
   * @param basePath basePath
   * @param id Object Id
   * @param action SemanticNode action
   * @param category category id
   * @param query SemanticNode query
   * @return String SemanticNode as JsonString.
   * @throws JsonProcessingException when json parse error
   */
  public static String node2JsonString(
      SemanticNode node, String basePath, String id, Action action, Category category, Query query)
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
   * Get Separated Columns.
   *
   * @param headers headers
   * @param map map
   * @param separator separator
   * @return String
   */
  private static String getSeperatedColumns(
      Set<String> headers, Map<String, Object> map, String separator) {
    List<Object> items = new ArrayList<Object>();
    for (String header : headers) {
      Object value = map.get(header) == null ? "" : map.get(header);
      items.add(value);
    }

    return StringUtils.join(items.toArray(), separator);
  }

  /**
   * Get TabularFormat.
   *
   * @param flatJson flatJson
   * @param separator separator
   * @return List CSV data
   */
  public static List<Object> getTabularFormat(
      List<Map<String, Object>> flatJson, String separator) {
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

  /**
   * collect Ordered Headers.
   *
   * @param flatJson Json
   * @return Set List of ordered header.
   */
  public static Set<String> collectOrderedHeaders(List<Map<String, Object>> flatJson) {
    Set<String> headers = new TreeSet<String>();
    for (Map<String, Object> map : flatJson) {
      headers.addAll(map.keySet());
    }
    return headers;
  }

  /**
   * copy File Using File Channels.
   *
   * @param source source file
   * @param dest destination file
   * @throws IOException when file not found error
   */
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

  /**
   * Convert to JsonElement.
   *
   * @param jsonString Json String
   * @return JsonElement
   */
  public static JsonElement toJsonElement(String jsonString) {
    logger.trace("toJsonElement Called: String = ", jsonString);
    com.google.gson.JsonParser jsonParser = new com.google.gson.JsonParser();
    JsonElement jsonElement;
    try {
      jsonElement = jsonParser.parse(jsonString);
      logger.info("json element parsed successfully");
      logger.trace("Parsed String = ", jsonElement);
      return jsonElement;
    } catch (JsonParseException jse) {
      logger.error("Can't parse String to Json, JsonParseException occurred!\n");
      logger.error(jse.getStackTrace().toString());
      return null;
    }
  }

  /**
   * removeJSONNode.
   *
   * @param jsonString json String
   * @param attributedToBeRemoved attributed To Be Removed
   * @return Map jsonNode
   * @throws JsonProcessingException when Json processing exception.
   * @throws IOException when Json failed to read.
   */
  public static Map<String, Object> removeJsonNode(Object jsonString, String attributedToBeRemoved)
      throws JsonProcessingException, IOException {
    ObjectMapper objectMapper = new ObjectMapper();
    objectMapper.configure(JsonParser.Feature.ALLOW_UNQUOTED_FIELD_NAMES, true);
    objectMapper.enable(DeserializationFeature.FAIL_ON_READING_DUP_TREE_KEY);
    JsonNode node = objectMapper.readTree(objectMapper.writeValueAsString(jsonString));
    ObjectNode rootNode = (ObjectNode) node;
    Map<String, Object> results = new HashMap<>();
    Iterator<Map.Entry<String, JsonNode>> fields = rootNode.fields();
    while (fields.hasNext()) {
      Map.Entry<String, JsonNode> next = fields.next();
      if (!next.getKey().equals(attributedToBeRemoved)) {
        results.put(next.getKey(), objectMapper.treeToValue(next.getValue(), String.class));
      }
    }
    return results;
  }

  /**
   * Run Semantic.
   *
   * @param args args
   * @throws JsonProcessingException when json parse error.
   * @throws IOException when Json Input error.
   */
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
    String row1 =
        "{\"_id\":\"xda-ux-sr-comp-dev::TRTEST_JEXLREF_SS\",\"system\":{\"user\":\"A_user\","
            + "\"project\":\"xda-ux-sr-comp-dev\",\"type\":\"fs\",\"format\":\"parquet\",\"name\":"
            + "\"TRTEST_JEXLREF_SS\",\"physicalLocation\":\"data\",\"catalog\":\"dout\","
            + "\"numberOfFiles\":\"1\"},\"userData\":{\"createdBy\":\"S.Ryabov\",\"category\":"
            + "\"subcat1\",\"description\":\"Transformer component test case: transformed records\""
            + "},\"transformations\":[],\"asOutput\":\"xda-ux-sr-comp-dev::transformer::165407713\""
            + ",\"asOfNow\":{\"status\":\"SUCCESS\",\"started\":\"20180209-195737\",\"finished\":"
            + "\"20180209-195822\",\"aleId\":\"xda-ux-sr-comp-dev::1518206302595\",\"batchId\":"
            + "\"BJEXLREFSS\"}}";
    String row2 =
        "{\"_id\":\"xda-ux-sr-comp-dev::tc220_1\",\"system\":{\"user\":\"A_user\",\"project\":"
            + "\"xda-ux-sr-comp-dev\",\"type\":\"fs\",\"format\":\"parquet\",\"name\":\"tc220_1\","
            + "\"physicalLocation\":\"hdfs:///data/bda/xda-ux-sr-comp-dev/dl/fs/dout/tc220_1/data"
            + "\",\"catalog\":\"dout\",\"numberOfFiles\":\"2\"},\"userData\":{\"createdBy\":"
            + "\"S.Ryabov\",\"category\":\"subcat1\",\"description\":\"SQL component test case\"},"
            + "\"transformations\":[{\"asOutput\":\"xda-ux-sr-comp-dev::sql::1192296717\"}],"
            + "\"asOutput\":\"xda-ux-sr-comp-dev::sql::522761969\",\"asOfNow\":{\"status\":"
            + "\"SUCCESS\",\"started\":\"20180223-220236\",\"finished\":\"20180223-220316\","
            + "\"aleId\":\"xda-ux-sr-comp-dev::1519423396639\",\"batchId\":\"BSQL10PM\"}}";
    List<DataSet> sets = new ArrayList<>();
    sets.add(objectMapper.readValue(row1, DataSet.class));
    sets.add(objectMapper.readValue(row2, DataSet.class));
    DataSet dataset = sets.get(0);
    System.out.println("Dataset : " + objectMapper.writeValueAsString(dataset));
    JsonNode node = objectMapper.readTree(objectMapper.writeValueAsString(dataset.getSystem()));
    Map<String, Object> results = new HashMap<String, Object>();
    ObjectNode rootNode = (ObjectNode) node;
    Iterator<Map.Entry<String, JsonNode>> fields = rootNode.fields();
    while (fields.hasNext()) {
      Map.Entry<String, JsonNode> next = fields.next();
      if (!next.getKey().equals("physicalLocation")) {
        results.put(next.getKey(), objectMapper.treeToValue(next.getValue(), String.class));
      }
    }
    dataset.setSystem(results);
    System.out.println(objectMapper.writeValueAsString(dataset));
    System.out.println(DataSetProperties.UserData.toString());
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
   * @param <T> type
   * @return ResponseEntity ResponseEntity
   */
  public <T> ResponseEntity<T> createOkResponse(T body) {
    return createResponse(body, HttpStatus.OK);
  }

  /**
   * Clone an existing result as a new one, filtering out http headers that not should be moved on
   * and so on...
   */
  public <T> ResponseEntity<T> createResponse(ResponseEntity<T> result) {

    ResponseEntity<T> response = createResponse(result.getBody(), result.getStatusCode());
    return response;
  }

  /**
   * createResponse.
   *
   * @param body body
   * @param httpStatus HttpStatus
   * @param <T> type
   * @return ResponseEntity ResponseEntity
   */
  public <T> ResponseEntity<T> createResponse(T body, HttpStatus httpStatus) {
    return new ResponseEntity<>(body, httpStatus);
  }

  /**
   * Get Class Path Resources.
   *
   * @param filename filename
   * @return Resource file name.
   */
  public Resource getClassPathResources(String filename) {
    return new ClassPathResource(filename);
  }

  /**
   * This method to validate jwt token then return the validated ticket for further processing.
   *
   * @param request HttpServletRequest
   * @return Ticket
   */
  public static Ticket getTicket(HttpServletRequest request) {
    try {
      String token = getToken(request);
      return TokenParser.retrieveTicket(token);
    } catch (IllegalAccessException | IOException e) {
      logger.error("Error occurred while fetching the alert details", e);
    }
    return null;
  }

  /**
   * Get JWT token details.
   *
   * @param req http Request
   * @return String
   * @throws IllegalAccessException If Authorization not found
   */
  public static String getToken(final HttpServletRequest req) throws IllegalAccessException {

    if (!("OPTIONS".equals(req.getMethod()))) {

      final String authHeader = req.getHeader("Authorization");

      if (authHeader == null || !authHeader.startsWith("Bearer ")) {

        throw new IllegalAccessException("Missing or invalid Authorization header.");
      }

      return authHeader.substring(7); // The part after "Bearer "
    }

    return null;
  }

  /**
   * checks  category is private or not.
   *
   * @param ticket Ticket
   * @param categoryId String
   * @return Boolean.
   */
  public static Boolean checkPrivateCategory(Ticket ticket, String categoryId) {
    for (Products product : ticket.getProducts()) {
      List<ProductModules> productModules = product.getProductModules();
      for (ProductModules prodMod : productModules) {
        List<ProductModuleFeature> productModuleFeature = prodMod.getProdModFeature();
        for (ProductModuleFeature prodModFeat : productModuleFeature) {
          if (prodModFeat.getProdModFeatureName().equalsIgnoreCase("My Analysis")) {
            List<ProductModuleFeature> productModuleSubfeatures =
                prodModFeat.getProductModuleSubFeatures();
            for (ProductModuleFeature prodModSubFeat : productModuleSubfeatures) {
              String cat = String.valueOf(prodModSubFeat.getProdModFeatureID());
              if (categoryId.equalsIgnoreCase(cat)) {
                return true;
              }
            }
          }
        }
      }
    }
    return false;
  }

  /**
   * check  category is authorized for user.
   *
   * @param authTicket ticket
   * @param categoryId category id
   * @return boolean
   */
  public static Boolean checkCategoryAccessible(Ticket authTicket, String categoryId) {
    List<Long> catIds = getCategoryIds(authTicket);
    if (catIds.contains(Long.valueOf(categoryId))) {
      return true;
    }
    return false;
  }

  /**
   * get list of category id.
   *
   * @param authTicket ticket
   * @return List category id list
   */
  public static List<Long> getCategoryIds(Ticket authTicket) {
    List<Long> categoryList = new ArrayList<>();
    List<Products> products = authTicket.getProducts();
    if (products.size() > 0) {
      products.forEach(
          (product) -> {
            List<ProductModules> prodModules = product.getProductModules();
            if (prodModules.size() > 0) {
              prodModules.forEach(
                  productModules -> {
                    List<ProductModuleFeature> prodModFeatures = productModules.getProdModFeature();
                    getCatIds(prodModFeatures, categoryList);
                  });
            }
          });
    }
    return categoryList;
  }

  /**
   * check given category is exist for user.
   *
   * @param productModules productModules
   * @param list category list
   */
  public static void getCatIds(List<ProductModuleFeature> productModules, List list) {
    if (productModules == null) {
      return;
    } else {
      for (ProductModuleFeature p : productModules) {
        list.add(p.getProdModFeatureID());
        getCatIds(p.getProductModuleSubFeatures(), list);
      }
    }
  }
}
