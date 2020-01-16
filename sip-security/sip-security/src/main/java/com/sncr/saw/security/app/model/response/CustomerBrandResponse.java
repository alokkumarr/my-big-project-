package com.sncr.saw.security.app.model.response;

/**
 * @author alok.kumarr
 * @since 3.5.0
 */
public class CustomerBrandResponse {

  private String brandColor;
  private String brandLogoName;
  private String brandLogoUrl;
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

  public String getBrandLogoUrl() {
    return brandLogoUrl;
  }

  public void setBrandLogoUrl(String brandLogoUrl) {
    this.brandLogoUrl = brandLogoUrl;
  }

  public String getMessage() {
    return message;
  }

  public void setMessage(String message) {
    this.message = message;
  }
}
