package com.synchronoss.saw.gateway;

import com.synchronoss.bda.sip.SipXssRequestFilter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.web.context.request.async.WebAsyncManagerIntegrationFilter;
import org.springframework.security.web.header.writers.StaticHeadersWriter;



@EnableWebSecurity(debug = false)
@Configuration
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {
  private static final Logger logger = LoggerFactory.getLogger(WebSecurityConfig.class);

 
  @Autowired
  private SipXssRequestFilter filter;
  
  @Override
  protected void configure(HttpSecurity http) throws Exception {
    logger.trace(this.getClass().getName() + " - configure - START");
    http.addFilterBefore(filter, WebAsyncManagerIntegrationFilter.class);
    http.headers().frameOptions().disable();
    http.csrf().disable();
    
    http.authorizeRequests()
        .antMatchers(HttpMethod.OPTIONS, "/**").permitAll()// allow CORS option calls
        .antMatchers(HttpMethod.GET, "/**").permitAll()// allow CORS option calls
        .antMatchers(HttpMethod.POST, "/**").permitAll()// allow CORS option calls
        .antMatchers(HttpMethod.PUT, "/**").permitAll()// allow CORS option calls
        .antMatchers(HttpMethod.DELETE, "/**").permitAll()// allow CORS option calls
        .antMatchers("/resources/**").permitAll();

    http.headers()
        .addHeaderWriter(new StaticHeadersWriter("Access-Control-Allow-Headers",
            "Origin, X-Requested-With, Content-Type, Accept, Authorization"))
        .addHeaderWriter(new StaticHeadersWriter("Access-Control-Allow-Methods",
            "POST, GET, DELETE, PUT, OPTIONS"));
    logger.trace(this.getClass().getName() + " - configure - END");
  }

}
