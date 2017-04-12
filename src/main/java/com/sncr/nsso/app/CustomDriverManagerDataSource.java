package com.sncr.nsso.app;

import org.apache.tomcat.util.codec.binary.Base64;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
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
		String key = "Saw12345Saw12345"; // 128 bit key
		String initVector = "RandomInitVector"; // 16 bytes IV
		byte[] original = null;
		try {
            IvParameterSpec iv = new IvParameterSpec(initVector.getBytes("UTF-8"));
            SecretKeySpec skeySpec = new SecretKeySpec(key.getBytes("UTF-8"), "AES");

            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5PADDING");
            cipher.init(Cipher.DECRYPT_MODE, skeySpec, iv);

            original = cipher.doFinal(Base64.decodeBase64(token));
        } catch (Exception ex) {
            ex.printStackTrace();
        }		
	    return new String(original);
	}
	
}