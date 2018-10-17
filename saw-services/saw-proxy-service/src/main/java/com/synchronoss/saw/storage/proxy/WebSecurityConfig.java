package com.synchronoss.saw.storage.proxy;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

@EnableWebSecurity
@Configuration
public class WebSecurityConfig extends
   WebSecurityConfigurerAdapter {
    private static final Logger logger = LoggerFactory
            .getLogger(WebSecurityConfig.class);

  @Override
  protected void configure(HttpSecurity http) throws Exception {
      logger.debug(this.getClass().getName() + " - configure - START");
        
    http.headers().frameOptions().disable();
        
    
    http
    .csrf().disable()
    .authorizeRequests()
      .antMatchers(HttpMethod.OPTIONS,"/**").permitAll()//allow CORS option calls
      .antMatchers(HttpMethod.GET,"/**").permitAll()//allow CORS option calls
      .antMatchers(HttpMethod.POST,"/**").permitAll()//allow CORS option calls
      .antMatchers(HttpMethod.PUT,"/**").permitAll()//allow CORS option calls
      .antMatchers("/resources/**").permitAll();
  }
  }
  

