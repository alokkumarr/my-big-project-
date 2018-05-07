package sncr.bda.store.generic.schema;

import java.util.HashMap;
import java.util.Map;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum Category {
  
  DATA_SET("DataSet"),
  TRANSFORMATION("Transformation"),
  DATA_POD("DataPod"),
  DATA_SEGMENT("DataSegment"),
  PORTALDATASET("PortalDataset"),
  PROJECT("Project"),
  AUDIT_LOG("AuditLog"),
  SEMANTIC("Semantic");  
private final String value;
private final static Map<String, Category> CONSTANTS = new HashMap<String, Category>();

static {
for (Category c: values()) {
CONSTANTS.put(c.value, c);
}
}

private Category(String value) {
this.value = value;
}

@Override
public String toString() {
return this.value;
}

@JsonValue
public String value() {
return this.value;
}

@JsonCreator
public static Category fromValue(String value) {
Category constant = CONSTANTS.get(value);
if (constant == null) {
throw new IllegalArgumentException(value);
} else {
return constant;
}
}

}
