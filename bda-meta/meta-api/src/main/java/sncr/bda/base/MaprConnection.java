package sncr.bda.base;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import com.fasterxml.jackson.databind.node.ObjectNode;
import org.ojai.Document;
import org.ojai.DocumentStream;
import org.ojai.store.Connection;
import org.ojai.store.DocumentStore;
import org.ojai.store.DriverManager;
import org.ojai.store.Query;
import org.ojai.store.SortOrder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MaprConnection {

  private static final Logger LOGGER = LoggerFactory.getLogger(MaprConnection.class);

  private DocumentStore store;
  private Connection connection;
  private static final String OJAI_MAPR = "ojai:mapr:";
  protected static final String METASTORE = "services/metadata";

  public MaprConnection(String basePath, String tableName) {
    // Create an OJAI connection to MapR cluster
    connection = DriverManager.getConnection(OJAI_MAPR);
    store = connection.getStore(basePath + File.separator + METASTORE + File.separator + tableName);
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
    ObjectMapper objectMapper = new ObjectMapper();
    for (final Document document : stream) {
      try {
        resultSet.add(objectMapper.readTree(document.asJsonString()));
      } catch (IOException e) {
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
        Query query =
            buildDataQuery(connection, columnName, executionId);
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
              Object obj =  objectMapper.readTree(objectList.toString());
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
