package sncr.bda.base;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.ojai.Document;
import org.ojai.DocumentStream;
import org.ojai.store.Connection;
import org.ojai.store.DocumentStore;
import org.ojai.store.DriverManager;
import org.ojai.store.Query;
import org.ojai.store.SortOrder;

public class MaprConnection {

  protected static final String METASTORE = "services/metadata";
  Connection connection;
  DocumentStore store;

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
            throw new RuntimeException ("error occurred while reading the documents",e);
        }
    }
    return resultSet;
  }

  public Boolean deleteMaprDBQuery(String[] select, String filter) {
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
}
