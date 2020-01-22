package com.sncr.saw.security.common.bean.repo;

import java.io.Serializable;
import java.sql.Blob;

/**
 * @author alok.kumarr
 * @since 3.5.0
 */
public class BrandDetails implements Serializable {

  private static final long serialVersionUID = 8604685133559450551L;

  private String brandColor;
  private Blob brandLogo;

  public String getBrandColor() {
    return brandColor;
  }

  public void setBrandColor(String brandColor) {
    this.brandColor = brandColor;
  }

  public Blob getBrandLogo() {
    return brandLogo;
  }

  public void setBrandLogo(Blob brandLogo) {
    this.brandLogo = brandLogo;
  }
}
