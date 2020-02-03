package com.sncr.saw.security.app.model.response;

/**
 * @author alok.kumarr
 * @since 3.5.0
 */
public class CustomerBrandResponse {

  private String brandColor;
  private byte[] brandImage;
  private String message;

  public String getBrandColor() {
    return brandColor;
  }

  public void setBrandColor(String brandColor) {
    this.brandColor = brandColor;
  }

  public String getMessage() {
    return message;
  }

  public void setMessage(String message) {
    this.message = message;
  }

  public byte[] getBrandImage() {
    return brandImage;
  }

  public void setBrandImage(byte[] brandImage) {
    this.brandImage = brandImage;
  }
}
