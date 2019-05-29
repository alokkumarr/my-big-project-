package com.synchronoss.saw.storage.proxy.model;

import com.fasterxml.jackson.databind.JsonNode;

public class AnalysisDefMigration {
	private String type;
	private JsonNode queryBuilder;

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public JsonNode getQueryBuilder() {
		return queryBuilder;
	}

	public void setQueryBuilder(JsonNode queryBuilder) {
		this.queryBuilder = queryBuilder;
	}
}
