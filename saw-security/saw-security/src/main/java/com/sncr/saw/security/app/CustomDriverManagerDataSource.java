package com.sncr.saw.security.app;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

import com.sncr.saw.security.common.util.Ccode;


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
	public String getPassword(){	
		String password = this.password;
        return base64Decode(password);
    }
	
	
	/**
	 * @param token
	 * @return
	 */
	public static String base64Decode(String token) {
		return Ccode.cdecode(token);
	}
	
}