package com.razor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer;
import org.springframework.security.oauth2.config.annotation.web.configuration.ResourceServerConfigurerAdapter;
import org.springframework.security.oauth2.provider.token.AccessTokenConverter;
import org.springframework.security.oauth2.provider.token.DefaultAccessTokenConverter;
import org.springframework.security.oauth2.provider.token.RemoteTokenServices;
import org.springframework.security.web.header.writers.StaticHeadersWriter;



/**
* @author sunil.belakeri
*
* 
 */
@EnableWebSecurity
@EnableResourceServer
@Configuration
public class WebSecurityConfig extends  ResourceServerConfigurerAdapter {
	private static final Logger logger = LoggerFactory.getLogger(WebSecurityConfig.class);
	
	
  @Override
  public void configure(HttpSecurity http) throws Exception {
         
/*    http
      .headers()
        .addHeaderWriter(new StaticHeadersWriter("Access-Control-Allow-Origin", "*"))
        .addHeaderWriter(new StaticHeadersWriter("Access-Control-Allow-Headers", "Origin, X-Requested-With, Content-Type, Accept"));
        
    
    http
    .csrf().disable()
    .authorizeRequests()
       .antMatchers(HttpMethod.OPTIONS,"*fileUpload*//**").permitAll()//allow CORS option calls
       .antMatchers(HttpMethod.OPTIONS,"**//*publishManualFile*//**").permitAll()//allow CORS option calls
       .antMatchers(HttpMethod.OPTIONS,"**//*getPublishedReport*//**").permitAll()//allow CORS option calls
       .antMatchers(HttpMethod.OPTIONS,"**//*downloadFile*//**").permitAll()//allow CORS option calls
       .antMatchers(HttpMethod.OPTIONS,"**//*deletePublishedReport*//**").permitAll()//allow CORS option calls
       .antMatchers(HttpMethod.OPTIONS,"**//*copyPublishedReport*//**").permitAll()//allow CORS option calls
      
      .antMatchers("/resources/**").permitAll();*/
      
		http
	      .headers()
	        .addHeaderWriter(new StaticHeadersWriter("Access-Control-Allow-Origin", "*"))
	        .addHeaderWriter(new StaticHeadersWriter("Access-Control-Allow-Headers", "Origin, X-Requested-With, Content-Type, Accept, Authorization"))
	        .addHeaderWriter(new StaticHeadersWriter("Access-Control-Allow-Methods", "POST, GET, OPTIONS, DELETE, OPTIONS"));
		http.csrf().disable();
		//http.authorizeRequests().anyRequest().authenticated();
		
		
		http.anonymous().and().authorizeRequests()
		   .antMatchers(HttpMethod.OPTIONS,"/verifyAppStatus**").permitAll();
		
		http.anonymous().and().authorizeRequests()
		   .antMatchers(HttpMethod.POST,"/fileUpload**").permitAll();
		
		http.anonymous().and().authorizeRequests()
		   .antMatchers(HttpMethod.OPTIONS,"/fileUpload**").permitAll();
		 
		 http.authorizeRequests().antMatchers("/getPublishedReport/**").permitAll()//.authenticated()
		      .antMatchers("/downloadFile/**").permitAll()//.authenticated()
		      .antMatchers("/deletePublishedReport/**").permitAll()//.authenticated()
		      .antMatchers("/copyPublishedReport/**").permitAll()//.authenticated()
		      .antMatchers("/publishManualFile/**").permitAll();//.authenticated();
		
  }
  
  @Bean
 	public AccessTokenConverter accessTokenConverter() {
 		return new DefaultAccessTokenConverter();
 	}
   
  @Primary
  @Bean
 	public RemoteTokenServices razorRemoteTokenServices(final @Value("${spring.oauth2.server.url}") String checkTokenUrl,
			final @Value("${spring.oauth2.client.client-id}") String clientId,
			final @Value("${spring.oauth2.client.client-secret}") String clientSecret) {
 		final RemoteTokenServices remoteTokenServices = new RemoteTokenServices();
 		remoteTokenServices.setCheckTokenEndpointUrl(checkTokenUrl);
 		remoteTokenServices.setClientId(clientId);
 		remoteTokenServices.setClientSecret(clientSecret);
 		remoteTokenServices.setAccessTokenConverter(accessTokenConverter());
 		logger.debug("*#*#*#*#*#*#*#*#*#*#*#*#*#*#*#*#*#*#*#*#*#*#*#*#*#*#*#*#*#*#*#*#*#*#*#*#*#*#*#*#*#*#*#*#*#*#");
		logger.debug("WebSecurityConfig - remoteTokenServices - checkTokenUrl - "+checkTokenUrl);
		logger.debug("WebSecurityConfig - remoteTokenServices - clientId - "+clientId);
		logger.debug("WebSecurityConfig - remoteTokenServices - clientSecret "+clientSecret);
		System.out.println("*#*#*#*#*#*#*#*#*#*#*#*#*#*#*#*#*#*#*#*#*#*#*#*#*#*#*#*#*#*#*#*#*#*#*#*#*#*#*#*#*#*#*#*#*#*#");
		System.out.println("WebSecurityConfig - remoteTokenServices - checkTokenUrl - "+checkTokenUrl);
		System.out.println("WebSecurityConfig - remoteTokenServices - clientId - "+clientId);
		System.out.println("WebSecurityConfig - remoteTokenServices - clientSecret "+clientSecret);
 		return remoteTokenServices;
 	}
   
}
