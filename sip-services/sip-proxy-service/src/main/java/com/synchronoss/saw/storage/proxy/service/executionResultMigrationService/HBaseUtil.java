package com.synchronoss.saw.storage.proxy.service.executionResultMigrationService;

import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.client.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.io.IOException;
/**
 * @author Alok.KumarR
 * @since 3.3.0
 */

@Component
public class HBaseUtil {

  private static final Logger LOGGER = LoggerFactory.getLogger(HBaseUtil.class);

  Connection connection = null;

  public Connection getConnection() {
    try {
      connection = ConnectionFactory.createConnection();
    } catch (IOException ex) {
      LOGGER.error(ex.getMessage());
      LOGGER.info("Error occurred during hbase connection");
    }
    return connection;
  }

  public Table getTable(Connection connection, String tablePath) throws IOException {
    TableName tn = TableName.valueOf(tablePath);
    return connection.getTable(tn);
  }

  public ResultScanner getResultScanner(Table table) throws IOException {
    Scan scan = new Scan();
    return table.getScanner(scan);
  }

  public void closeConnection() {
    try {
      if (connection != null) {
        connection.close();
      }
    } catch (IOException ex) {
      LOGGER.error(ex.getMessage());
    }
  }
}
