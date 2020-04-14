package com.synchronoss.bda.sip.config;

import com.synchronoss.sip.utils.Ccode;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

@Configuration
public class CustomDriverManagerDataSource extends DriverManagerDataSource {

  @Value("${spring.datasource.password}")
  private String password;

  @Value("${spring.datasource.url}")
  private String url;

  @Value("${spring.datasource.username}")
  private String username;

  public String getUrl() {
    return this.url;
  }

  public String getUsername() {
    return this.username;
  }

  @Override
  public String getPassword() {
    return base64Decode(password);
  }

  /**
   * This method is used for decrypting the encrypted parameter.
   *
   * @param token parameter that is encrypted
   * @return
   */
  public static String base64Decode(String token) {
    return Ccode.cdecode(token);
  }
}
