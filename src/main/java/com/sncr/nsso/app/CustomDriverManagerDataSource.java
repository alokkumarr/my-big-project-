package com.sncr.nsso.app;

import org.apache.tomcat.util.codec.binary.Base64;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

import com.sncr.nsso.common.util.Ccode;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;



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