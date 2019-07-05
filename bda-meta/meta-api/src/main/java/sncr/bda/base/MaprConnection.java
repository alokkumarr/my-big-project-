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

  DocumentStore store;
  Connection connection;
  protected static final String METASTORE = "services/metadata";

  public MaprConnection(String basePath, String tableName) {
    // Create an OJAI connection to MapR cluster
    connection = DriverManager.getConnection("ojai:mapr:");
    store = connection.getStore(basePath + File.separator + METASTORE + File.separator + tableName);
  }

  public List<JsonNode> runMaprDBQuery(
      String[] select, String filter, String orderBy, Integer limit) {
    final Query query =
        connection
            .newQuery()
            .select(select)
            .orderBy(orderBy, SortOrder.DESC)
            .limit(limit)
            .where(filter)
            .build();

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

  /**
   * Fetch paginated data from MapR db.
   *
   * @param executionId
   * @param page
   * @param size
   * @return Object
   */
  public Object fetchPagingData(String executionId, Integer page, Integer size) {
    if ((page != null && page > 0) && (size != null && size > 0)) {
      try {
        ObjectMapper objectMapper = new ObjectMapper();
        Query query = getExecutionDataQuery(executionId, page, size);
        if (query != null) {
          final DocumentStream stream = store.find(query);
          for (final Document document : stream) {
            List<Object> objectList =
                document.getList("data").stream()
                    .filter(Objects::nonNull)
                    .collect(Collectors.toList());

            return objectMapper.readTree(objectList.toString());
          }
        }
      } catch (Exception ex) {
        LOGGER.error("Error while fetching paginated data :" + ex.getMessage());
      }
    }
    return null;
  }

  /**
   * This query is to build paginated data queries to fetch record from mapr db based upon the page
   * number and page size.
   *
   * @param executionId
   * @param page
   * @param size
   * @return query
   */
  public Query getExecutionDataQuery(String executionId, Integer page, Integer size) {
    try {
      // pagination logic
      int startIndex, endIndex;
      if (page != 0 && page > 1) {
        startIndex = (page - 1) * size;
        endIndex = startIndex + size;
      } else {
        startIndex = (page - 1);
        endIndex = startIndex + size;
      }
      LOGGER.info("Start Index : " + startIndex + " End Index :" + endIndex);

      String[] select = new String[size];
      for (int i = 0; startIndex < endIndex; i++) {
        select[i] = String.format("data[%s]", startIndex);
        ++startIndex;
      }

      ObjectMapper objectMapper = new ObjectMapper();
      ObjectNode node = objectMapper.createObjectNode();
      ObjectNode objectNode = node.putObject("$eq");
      objectNode.put("executionId", executionId);

      // build execution data query
      final Query query = connection.newQuery().select(select).where(node.toString()).build();
      LOGGER.info("Query for the paginating data :" + query.asJsonString());
      return query;
    } catch (Exception ex) {
      LOGGER.error("Error while building execution data query :" + ex);
    }
    return null;
  }
}
