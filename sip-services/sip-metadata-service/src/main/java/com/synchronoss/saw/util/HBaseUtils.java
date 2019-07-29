package com.synchronoss.saw.util;

import java.io.IOException;

import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.client.Connection;
import org.apache.hadoop.hbase.client.ConnectionFactory;
import org.apache.hadoop.hbase.client.Table;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HBaseUtils {

	private static final Logger LOGGER = LoggerFactory.getLogger(HBaseUtils.class);

	Connection connection = null;

	/**
	 * Create hbase connection to read binary store data.
	 *
	 * @return connection
	 */
	public Connection getConnection() {
		try {
			connection = ConnectionFactory.createConnection();
		} catch (IOException ex) {
			LOGGER.info("Error occurred during hbase connection", ex);
		}
		return connection;
	}

	/**
	 * Get the hbase table to perform operation on binary store.
	 *
	 * @param connection The connection for Hbase
	 * @param tablePath  Table location for binary store
	 * @return table Operation to be performed on the table
	 * @throws IOException Throw the exception while get table
	 */
	public Table getTable(Connection connection, String tablePath) throws IOException {
		TableName tn = TableName.valueOf(tablePath);
		return connection.getTable(tn);
	}

	/**
	 * Close the connection once all operation performed.
	 */
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