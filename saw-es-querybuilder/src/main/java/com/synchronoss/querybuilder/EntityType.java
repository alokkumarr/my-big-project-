package com.synchronoss.querybuilder;

import java.util.Arrays;

public enum EntityType {

	CHART("chart"), PIVOT("pivot");

	private String s;

	private EntityType(String s) {
		this.s = s;
	}

	public EntityType find(EntityType val) {
		return Arrays.stream(EntityType.values()).filter(e -> e.s.equals(val.name())).reduce((t1, t2) -> t1)
				.<IllegalArgumentException>orElseThrow(() -> {
					throw new IllegalStateException(String.format("Unsupported type %s.", val));
				});
	}
}
