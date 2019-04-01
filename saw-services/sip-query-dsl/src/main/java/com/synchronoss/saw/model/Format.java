package com.synchronoss.saw.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonInclude(Include.NON_NULL)
public class Format {
  @JsonProperty("precision")
  Integer precision;

  @JsonProperty("comma")
  Boolean comma;

  @JsonProperty("currency")
  String currency;

  @JsonProperty("currencySymbol")
  String currencySymbol;

  @JsonProperty("percentage")
  Boolean percentage;

  public Integer getPrecision() {
    return precision;
  }

  public void setPrecision(Integer precision) {
    this.precision = precision;
  }

  public Boolean getComma() {
    return comma;
  }

  public void setComma(Boolean comma) {
    this.comma = comma;
  }

  public String getCurrency() {
    return currency;
  }

  public void setCurrency(String currency) {
    this.currency = currency;
  }

  public String getCurrencySymbol() {
    return currencySymbol;
  }

  public void setCurrencySymbol(String currencySymbol) {
    this.currencySymbol = currencySymbol;
  }

  public Boolean getPercentage() {
    return percentage;
  }

  public void setPercentage(Boolean percentage) {
    this.percentage = percentage;
  }
}
