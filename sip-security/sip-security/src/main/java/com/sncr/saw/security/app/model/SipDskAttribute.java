package com.sncr.saw.security.app.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.util.List;

@JsonInclude(Include.NON_NULL)
public class SipDskAttribute {
  @JsonProperty("columnName")
  private String columnName;

  @JsonProperty("model")
  private Model model;

  @JsonProperty("booleanCriteria")
  private BooleanCriteria booleanCriteria;

  @JsonProperty("booleanQuery")
  private List<SipDskAttribute> booleanQuery;

  public String getColumnName() {
    return columnName;
  }

  public void setColumnName(String columnName) {
    this.columnName = columnName;
  }

  public Model getModel() {
    return model;
  }

  public void setModel(Model model) {
    this.model = model;
  }

  public BooleanCriteria getBooleanCriteria() {
    return booleanCriteria;
  }

  public void setBooleanCriteria(BooleanCriteria booleanCriteria) {
    this.booleanCriteria = booleanCriteria;
  }

  public List<SipDskAttribute> getBooleanQuery() {
    return booleanQuery;
  }

  public void setBooleanQuery(List<SipDskAttribute> booleanQuery) {
    this.booleanQuery = booleanQuery;
  }

  public static void main(String[] args) throws IOException {
      String json = "{\n"
          + "  \"booleanCriteria\": \"AND\",\n"
          + "  \"booleanQuery\": [\n"
          + "    {\n"
          + "      \"columnName\": \"Field1\",\n"
          + "      \"model\": {\n"
          + "        \"operator\": \"ISIN\",\n"
          + "        \"values\": [\n"
          + "          \"abc\"\n"
          + "        ]\n"
          + "      }\n"
          + "    },\n"
          + "    {\n"
          + "      \"columnName\": \"Field2\",\n"
          + "      \"model\": {\n"
          + "        \"operator\": \"ISIN\",\n"
          + "        \"values\": [\n"
          + "          \"pqr\"\n"
          + "        ]\n"
          + "      }\n"
          + "    },\n"
          + "    {\n"
          + "      \"booleanCriteria\": \"OR\",\n"
          + "      \"booleanQuery\": [\n"
          + "        {\n"
          + "          \"columnName\": \"Field3\",\n"
          + "          \"model\": {\n"
          + "            \"operator\": \"ISIN\",\n"
          + "            \"values\": [\n"
          + "              \"456\"\n"
          + "            ]\n"
          + "          }\n"
          + "        },\n"
          + "        {\n"
          + "          \"columnName\": \"Field4\",\n"
          + "          \"model\": {\n"
          + "            \"operator\": \"ISIN\",\n"
          + "            \"values\": [\n"
          + "              \"123\"\n"
          + "            ]\n"
          + "          }\n"
          + "        }\n"
          + "      ]\n"
          + "    }\n"
          + "  ]\n"
          + "}";

      ObjectMapper mapper = new ObjectMapper();

      SipDskAttribute dskModel = mapper.readValue(json, SipDskAttribute.class);

    System.out.println(dskModel);
  }
}
