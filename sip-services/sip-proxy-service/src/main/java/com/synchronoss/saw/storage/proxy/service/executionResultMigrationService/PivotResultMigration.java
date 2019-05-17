package com.synchronoss.saw.storage.proxy.service.executionResultMigrationService;

import com.fasterxml.jackson.databind.JsonNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

/**
 * @author Alok.KumarR
 * @since 3.3.0
 */
public class PivotResultMigration extends ResultMigration{

	private static final Logger LOGGER = LoggerFactory.getLogger(PivotResultMigration.class);

	private static final String ROW_FIELD_LEVEL = "row_level_";
	private static final String ROW_FIELDS = "rowFields";

	@Override
	public List<Object> parseData(JsonNode dataNode, JsonNode queryNode) {
		LOGGER.info("Starting parse data. ");
		return parseData(dataNode, queryNode, ROW_FIELD_LEVEL, ROW_FIELDS);
	}
}
