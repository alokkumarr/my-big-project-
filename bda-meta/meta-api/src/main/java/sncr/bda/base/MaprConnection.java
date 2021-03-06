package sncr.bda.base;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import org.apache.commons.lang.StringUtils;
import org.ojai.Document;
import org.ojai.DocumentStream;
import org.ojai.store.Connection;
import org.ojai.store.DocumentStore;
import org.ojai.store.DriverManager;
import org.ojai.store.Query;
import org.ojai.store.SortOrder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sncr.bda.store.generic.schema.Sort;

public class MaprConnection {

  public static final String EQ = "$eq";
  public static final String AND = "$and";
  public static final String GTE = "$ge";
  public static final String LTE = "$le";
  public static final String GT = "$gt";
  public static final String LT = "$lt";
  public static final String BTW = "$between";
  protected static final String METASTORE = "services/metadata";
  private static final Logger LOGGER = LoggerFactory.getLogger(MaprConnection.class);
  private static final String OJAI_MAPR = "ojai:mapr:";
  private DocumentStore store;
  private Connection connection;
  private ObjectMapper objectMapper = new ObjectMapper();

  public MaprConnection(String basePath, String tableName) {
    // Create an OJAI connection to MapR cluster
    connection = DriverManager.getConnection(OJAI_MAPR);
    String storeName = basePath + File.separator + METASTORE + File.separator + tableName;
    if (!connection.storeExists(storeName)) {
      connection.createStore(storeName);
    }
    store = connection.getStore(storeName);
  }

  /**
   * This method will insert the document in maprDB.
   *
   * @param id Document Id
   * @param rowData Row Data
   */
  public void insert(String id, Object rowData) {
    Document document = connection.newDocument(rowData);
    store.insert(id, document);
  }

  /**
   * This method will update the document in maprDB.
   *
   * @param id Document Id
   * @param rowData Row Data
   */
  public void update(String id, Object rowData) {
    Document document = connection.newDocument(rowData);
    store.replace(id, document);
  }

  public void insertOrUpdate(String id, Object rowData) {
    Document document = connection.newDocument(rowData);
    store.insertOrReplace(id, document);
  }

  /**
   * Find by document ID.
   *
   * @param documentId
   * @return
   */
  public JsonNode findById(String documentId) {
    Document document = store.findById(documentId);
    try {
      return objectMapper.readTree(document.asJsonString());
    } catch (IOException e) {
      LOGGER.error("error occured while reading the documents :{}", e);
      throw new RuntimeException("error occurred while reading the documents", e);
    }
  }

  /**
   * by document ID.
   *
   * @param documentId
   * @return
   */
  public boolean deleteById(String documentId) {
    store.delete(documentId);
    return true;
  }

  /**
   * Run mapr db query with specific fields.
   *
   * @param select
   * @param filter
   * @param orderBy
   * @param limit
   * @return list
   */
  public List<JsonNode> runMaprDBQuery(
      String[] select, String filter, String orderBy, Integer limit) {
    final Query query = getQuery(select, filter, orderBy, limit);
    final DocumentStream stream = store.find(query);
    List<JsonNode> resultSet = new ArrayList<>();
    for (final Document document : stream) {
      try {
        resultSet.add(objectMapper.readTree(document.asJsonString()));
      } catch (IOException e) {
        LOGGER.error("error occured while reading the documents :{}", e);
        throw new RuntimeException("error occurred while reading the documents", e);
      }
    }
    return resultSet;
  }

  private Query getQuery(String[] select, String filter, String orderBy, Integer limit) {
    Query query = null;
    if (orderBy != null && limit != null) {
      query = connection
          .newQuery()
          .select(select)
          .orderBy(orderBy, SortOrder.DESC)
          .limit(limit)
          .where(filter)
          .build();
    } else {
      query = connection
          .newQuery()
          .select(select)
          .where(filter)
          .build();
    }

    return query;
  }

  /**
   * Run mapr db query with specific fields.
   *
   * @param filter
   * @param pageNumber
   * @param pageSize
   * @param orderBy
   * @param classType
   * @return list
   */
  public <T> List<T> runMaprDbQueryWithFilter(
      String filter, Integer pageNumber, Integer pageSize, String orderBy, Class<T> classType) {
    List<T> resultSet = new ArrayList<>();
    Query query;
    if (pageNumber != null && pageSize != null) {
      int documentsToskip = (pageNumber - 1) * pageSize;
      query =
          connection
              .newQuery()
              .orderBy(orderBy, SortOrder.DESC)
              .offset(documentsToskip)
              .limit(pageSize)
              .where(filter)
              .build();
    } else {
      query = connection.newQuery().where(filter).build();
    }
    LOGGER.debug("Mapr Query with filer:{}", query);
    objectMapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
    DocumentStream stream = store.find(query);
    for (final Document document : stream) {
      try {
        resultSet.add(objectMapper.readValue(document.asJsonString(), classType));
      } catch (IOException e) {
        LOGGER.error("error occured while reading the documents :{}", e);
        throw new RuntimeException("error occurred while reading the documents", e);
      }
    }
    return resultSet;
  }

  /**
   * Run mapr db query with specific fields.
   *
   * @param filter
   * @param pageNumber
   * @param pageSize
   * @param sorts
   * @param classType
   * @return list
   */
  public <T> List<T> runMaprDbQuery(
      String filter, Integer pageNumber, Integer pageSize, List<Sort> sorts, Class<T> classType) {
    List<T> resultSet = new ArrayList<>();
    Query query = prepareMaprQuery(filter, pageNumber, pageSize, sorts);
    objectMapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
    DocumentStream stream = store.find(query);
    for (final Document document : stream) {
      try {
        resultSet.add(objectMapper.readValue(document.asJsonString(), classType));
      } catch (IOException e) {
        LOGGER.error("error occured while reading the documents :{}", e);
        throw new RuntimeException("error occurred while reading the documents", e);
      }
    }
    return resultSet;
  }

  /**
   * prepares query with filters and sorts.
   *
   * @param filter
   * @param pageNumber
   * @param pageSize
   * @param sorts
   * @return query
   */
  private Query prepareMaprQuery(
      String filter, Integer pageNumber, Integer pageSize, List<Sort> sorts) {
    Query query = connection.newQuery();
    if (sorts != null) {
      for (Sort sort : sorts) {
        SortOrder sortOrder = sort.getOrder();
        sortOrder = sortOrder == null ? SortOrder.DESC : sortOrder;
        String fieldName = sort.getFieldName();
        if (fieldName != null && !StringUtils.isEmpty(fieldName)) {
          query = query.orderBy(fieldName, sortOrder);
        }
      }
    }
    if (pageNumber != null && pageSize != null) {
      int documentsToskip = (pageNumber - 1) * pageSize;
      query = query.offset(documentsToskip).limit(pageSize);
    }
    if (filter != null && !StringUtils.isEmpty(filter)) {
      query = query.where(filter);
    }
    LOGGER.info("Mapr Query :{}", query);
    return query.build();
  }

  /**
   * calculates count for a query with or withour filters.
   *
   * @param filter
   * @return count of no of documents
   */
  public Long runMapDbQueryForCount(String filter) {
    final Query query;
    if (filter != null) {
      query = connection.newQuery().select("_id").where(filter).build();
    } else {
      query = connection.newQuery().select("_id").build();
    }
    Long countOfDocuments = 0L;
    final DocumentStream stream = store.find(query);
    Iterator<Document> itr = stream.iterator();
    while (itr.hasNext()) {
      itr.next();
      countOfDocuments++;
    }
    return countOfDocuments;
  }

  /**
   * selects distinct values of column selected.
   *
   * @param select with column to select
   * @param filter
   * @return set of different select column values
   */
  public Set<String> runMaprQueryForDistinctValues(String select, String filter) {
    Set<String> resultSet = new HashSet<>();
    final Query query;
    if (filter != null) {
      query = connection.newQuery().select(select).where(filter).build();
    } else {
      query = connection.newQuery().select(select).build();
    }
    DocumentStream stream = store.find(query);
    for (final Document document : stream) {
      try {
        JsonNode node = objectMapper.readValue(document.asJsonString(), JsonNode.class);
        String result = node.get(select).asText();
        if (result != null && !StringUtils.isEmpty(result)) {
          resultSet.add(result);
        }
      } catch (IOException e) {
        LOGGER.error("error occured while reading the documents :{}", e);
        throw new RuntimeException("error occurred while reading the documents", e);
      }
    }
    return resultSet;
  }

  /**
   * Fetch paginated data from MapR db.
   *
   * @param executionId
   * @param page
   * @param pageSize
   * @return Object
   */
  public Object fetchPagingData(
      String columnName, String executionId, Integer page, Integer pageSize) {
    if (pageSize != null && pageSize > 0) {
      try {
        ObjectMapper objectMapper = new ObjectMapper();
        Query query = buildDataQuery(connection, columnName, executionId);
        if (query != null) {
          final DocumentStream stream = store.find(query);
            int startIndex = (page-1) * pageSize;
          for (final Document document : stream) {
            List<Object> objectList =
                document.getList(columnName).stream()
                    .skip(startIndex)
                    .limit(pageSize)
                    .filter(Objects::nonNull)
                    .collect(Collectors.toList());
            Object obj = objectMapper.readTree(objectList.toString());
            return obj;
          }
        }
      } catch (Exception ex) {
        LOGGER.error("Error while fetching paginated data : {} ", ex);
      }
    }
    return null;
  }

  /**
   * This query is to build paginated data queries to fetch record from mapr db based upon the page
   * number and page size.
   *
   * @param executionId
   * @return query
   */
  public Query buildDataQuery(
      Connection connection,
      String columnName,
      String executionId) {
    try {
      ObjectMapper objectMapper = new ObjectMapper();
      ObjectNode node = objectMapper.createObjectNode();
      ObjectNode objectNode = node.putObject("$eq");
      objectNode.put("executionId", executionId);

      // build execution data query
      final Query query = connection.newQuery().select(columnName).where(node.toString()).build();
      LOGGER.trace(String.format("Query for the paginating data : %s", query.asJsonString()));
      return query;
    } catch (Exception ex) {
      LOGGER.error("Error while building execution data query : {}", ex);
    }
    return null;
  }

  /**
   * Delete the object based upon custom query.
   *
   * @param select
   * @param filter
   * @return boolean
   */
  public Boolean deleteByMaprDBQuery(String[] select, String filter) {
    final Query query = connection.newQuery().select(select).where(filter).build();
    final DocumentStream stream;
    try {
      stream = store.find(query);
      store.delete(stream);
      return true;
    } catch (Exception e) {
      LOGGER.error("error occured while reading the documents :{}", e);
      throw new RuntimeException("Exception occurred while deleting execution results!!", e);
    }
  }

  @Override
  protected void finalize() {
    store.flush();
    store.close();
    connection.close();
  }
}
