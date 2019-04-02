package com.synchronoss.saw.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;

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

  @JsonProperty("precision")
  public Integer getPrecision() {
    return precision;
  }

  @JsonProperty("precision")
  public void setPrecision(Integer precision) {
    this.precision = precision;
  }

  @JsonProperty("comma")
  public Boolean getComma() {
    return comma;
  }

  @JsonProperty("comma")
  public void setComma(Boolean comma) {
    this.comma = comma;
  }

  @JsonProperty("currency")
  public String getCurrency() {
    return currency;
  }

  @JsonProperty("currency")
  public void setCurrency(String currency) {
    this.currency = currency;
  }

  @JsonProperty("currencySymbol")
  public String getCurrencySymbol() {
    return currencySymbol;
  }

  @JsonProperty("currencySymbol")
  public void setCurrencySymbol(String currencySymbol) {
    this.currencySymbol = currencySymbol;
  }

  @JsonProperty("percentage")
  public Boolean getPercentage() {
    return percentage;
  }

  @JsonProperty("percentage")
  public void setPercentage(Boolean percentage) {
    this.percentage = percentage;
  }

  @Override
  public String toString() {
    return new ToStringBuilder(this)
        .append("precision", precision)
        .append("comma", comma)
        .append("currency", currency)
        .append("currencySymbol", currencySymbol)
        .append("percentage", percentage)
        .toString();
  }

  @Override
  public int hashCode() {
    return new HashCodeBuilder()
        .append(precision)
        .append(comma)
        .append(currency)
        .append(currencySymbol)
        .append(percentage)
        .toHashCode();
  }

  @Override
  public boolean equals(Object other) {
    if (other == this) {
      return true;
    }
    if ((other instanceof Field) == false) {
      return false;
    }
    Format rhs = ((Format) other);
    return new EqualsBuilder()
        .append(precision, rhs.precision)
        .append(comma, rhs.comma)
        .append(currency, rhs.currency)
        .append(currencySymbol, rhs.currencySymbol)
        .append(percentage, rhs.percentage)
        .isEquals();
  }
}
