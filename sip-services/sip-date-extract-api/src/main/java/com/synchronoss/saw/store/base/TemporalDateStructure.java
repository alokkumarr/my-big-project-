package com.synchronoss.saw.store.base;

public class TemporalDateStructure {

  /**
   * original text identified in the source text
   **/
  private String originalText;
  /**
   * formatted date time found in the text, this is fully qualified formatted
   * string representation of date
   * */
  private String dateTimeString;
  /**
   * format in which {@linkplain dateTimeString} is represented
   **/
  private String conDateFormat;
  /**
   * format in which date is present
   */
  private String identifiedDateFormat;
  /**
   * start of identified text fragment in the source text
   */
  private int start;
  /**
   * end of identified text fragment in the source text
   */
  private int end;

  public String getOriginalText() {
      return originalText;
  }

  public void setOriginalText(String originalText) {
      this.originalText = originalText;
  }

  public String getDateTimeString() {
      return dateTimeString;
  }

  public void setDateTimeString(String dateTimeString) {
      this.dateTimeString = dateTimeString;
  }

  public String getConDateFormat() {
      return conDateFormat;
  }

  public void setConDateFormat(String conDateFormat) {
      this.conDateFormat = conDateFormat;
  }

  public int getStart() {
      return start;
  }

  public void setStart(int start) {
      this.start = start;
  }

  public int getEnd() {
      return end;
  }

  public void setEnd(int end) {
      this.end = end;
  }
  
  
  public String getIdentifiedDateFormat() {
      return identifiedDateFormat;
  }

  public void setIdentifiedDateFormat(String identifiedDateFormat) {
      this.identifiedDateFormat = identifiedDateFormat;
  }

  @Override
  public String toString() {
      return "TemporalDateStructure [originalText=" + originalText + ", dateTimeString=" + dateTimeString + ", conDateFormat=" + conDateFormat + ", identifiedDateFormat="
              + identifiedDateFormat + ", start=" + start + ", end=" + end + "]";
  }
}
