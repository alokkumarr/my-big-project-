package sncr.bda.conf;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class Alert {
  /**
   * DataPod name (Optional) Datapod was introduced as optional parameter for backward
   * compatibility.
   */
  @SerializedName("datapod")
  private String datapod;

  @SerializedName("fields")
  private List<String> fields;

  /**
   * Gets datapod.
   *
   * @return value of datapod.
   */
  public String getDatapod() {
    return datapod;
  }

  /** Sets datapod. */
  public void setDatapod(String datapod) {
    this.datapod = datapod;
  }

  /**
   * Gets fields
   *
   * @return value of fields
   */
  public List<String> getFields() {
    return fields;
  }

  /** Sets fields */
  public void setFields(List<String> fields) {
    this.fields = fields;
  }
}
