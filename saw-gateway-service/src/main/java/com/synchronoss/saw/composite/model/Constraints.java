package com.synchronoss.saw.composite.model;

import java.util.Arrays;

public enum Constraints {
	ANALYZE("analyze"), OBSERVE("observe"), ALERT("alert"), MENU("menu"), SEMANTIC("semantic"), REPORT("report"), CHART(
			"chart"), PIVOT("pivot");

	private String s;

	private Constraints(String s) {
		this.s = s;
	}

	public Constraints find(Constraints val) {
      return Arrays.stream(Constraints.values()).filter(e -> e.s.equals(val.s)).reduce((t1, t2) -> t1)
              .<IllegalStateException>orElseThrow(() -> {
                  throw new IllegalStateException(String.format("Unsupported type %s.", val));
              });
  }
	
}
