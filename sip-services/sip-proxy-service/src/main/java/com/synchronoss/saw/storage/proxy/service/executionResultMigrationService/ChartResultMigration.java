package com.synchronoss.saw.storage.proxy.service.executionResultMigrationService;

import com.fasterxml.jackson.databind.JsonNode;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Alok.KumarR
 * @since 3.3.0
 */
public class ChartResultMigration extends ResultMigration {
  private static final Logger LOGGER = LoggerFactory.getLogger(ChartResultMigration.class);

  private static final String NODE_FIELD = "node_field_";
  private static final String NODE_FIELDS = "nodeFields";

  @Override
  public List<Object> parseData(JsonNode dataNode, JsonNode queryNode) {
    LOGGER.debug("Starting Chart result migration data. ");
    return parseData(dataNode, queryNode, NODE_FIELD, NODE_FIELDS);
  }
}
