package com.sncr.nsso.app;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.web.header.writers.StaticHeadersWriter;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;

/**
 * @author girija.sankar
 *
 * 
 */
@EnableWebSecurity
@Configuration
public class WebSecurityConfig extends
   WebSecurityConfigurerAdapter {
	private static final Logger logger = LoggerFactory
			.getLogger(WebSecurityConfig.class);

  @Override
  protected void configure(HttpSecurity http) throws Exception {
	  logger.debug(this.getClass().getName() + " - configure - START");
     
	http
    .headers()
      .addHeaderWriter(new StaticHeadersWriter("Access-Control-Allow-Origin", "*"))
      .addHeaderWriter(new StaticHeadersWriter("Access-Control-Allow-Headers", "Origin, X-Requested-With, Content-Type, Accept, Authorization"))
      .addHeaderWriter(new StaticHeadersWriter("Access-Control-Allow-Methods", "POST, GET, OPTIONS, DELETE, OPTIONS"));    
    	
  
    http.csrf().disable();
     
    http.authorizeRequests() .antMatchers(HttpMethod.OPTIONS,"**/doAuthenticate/**").permitAll()//allow CORS option calls
      .antMatchers(HttpMethod.OPTIONS,"**/auth/reCreateTicket/**").permitAll()//allow CORS option calls
      .antMatchers(HttpMethod.OPTIONS,"**/resetPassword/**").permitAll()//allow CORS option calls
      .antMatchers(HttpMethod.OPTIONS,"**/rstChangePassword/**").permitAll()//allow CORS option calls
      .antMatchers(HttpMethod.OPTIONS,"**/vfyRstPwd/**").permitAll()//allow CORS option calls
      .antMatchers(HttpMethod.OPTIONS,"**/auth/changePassword/**").permitAll()//allow CORS option calls
      .antMatchers(HttpMethod.OPTIONS,"**/auth/doLogout/**").permitAll()//allow CORS option calls
      .antMatchers(HttpMethod.OPTIONS,"**/auth/**").permitAll()//allow CORS option calls
      .antMatchers(HttpMethod.OPTIONS,"**/user/login/**").permitAll()//allow CORS option calls
      .antMatchers(HttpMethod.OPTIONS,"**/auth/validateToken/**").permitAll()//allow CORS option calls
      .antMatchers(HttpMethod.OPTIONS,"**/auth/redirect/**").permitAll()//allow CORS option calls
      .antMatchers("/resources/**").permitAll();
  }
  
  public void addResourceHandlers(ResourceHandlerRegistry registry) {
      registry.addResourceHandler("swagger-ui.html")
        .addResourceLocations("classpath:/META-INF/resources/");
   
      registry.addResourceHandler("/webjars/**")
        .addResourceLocations("classpath:/META-INF/resources/webjars/");
  }
}