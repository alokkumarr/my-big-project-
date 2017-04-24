package com.synchronoss.saw.composite.model;

import java.util.Arrays;

public enum Constraints {
	ANALYZE("analyze"), OBSERVE("observe"), ALERT("alert"), MENU("menu"), SEMANTIC("semantic"), REPORT("report"), CHART(
			"chart"), PIVOT("pivot");

	private String s;

	private Constraints(String s) {
		this.s = s;
	}

	public static Constraints find(String val) {
		return Arrays.stream(Constraints.values()).filter(e -> e.s.equals(val)).reduce((t1, t2) -> t1)
				.orElseThrow(() -> {
					throw new IllegalStateException(String.format("Unsupported type %s.", val));
				});
	}

}
