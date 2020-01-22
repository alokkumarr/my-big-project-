package com.sncr.saw.security.app.model.response;

/**
 * @author alok.kumarr
 * @since 3.5.0
 */
public class CustomerBrandResponse {

  private String brandColor;
  private String brandLogoName;
  private String message;

  public String getBrandColor() {
    return brandColor;
  }

  public void setBrandColor(String brandColor) {
    this.brandColor = brandColor;
  }

  public String getBrandLogoName() {
    return brandLogoName;
  }

  public void setBrandLogoName(String brandLogoName) {
    this.brandLogoName = brandLogoName;
  }

  public String getMessage() {
    return message;
  }

  public void setMessage(String message) {
    this.message = message;
  }
}
