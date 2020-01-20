package com.sncr.saw.security.common.bean.repo;

import java.io.Serializable;

/**
 * @author alok.kumarr
 * @since 3.5.0
 */
public class BrandDetails implements Serializable {

  private static final long serialVersionUID = 8604685133559450551L;

  private String brandColor;
  private String brandName;

  public String getBrandColor() {
    return brandColor;
  }

  public void setBrandColor(String brandColor) {
    this.brandColor = brandColor;
  }

  public String getBrandName() {
    return brandName;
  }

  public void setBrandName(String brandName) {
    this.brandName = brandName;
  }
}
