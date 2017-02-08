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
import org.springframework.security.oauth2.provider.authentication.BearerTokenExtractor;
import org.springframework.security.oauth2.provider.authentication.TokenExtractor;
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
public class WebSecurityConfig extends
ResourceServerConfigurerAdapter {
	private static final Logger logger = LoggerFactory
			.getLogger(WebSecurityConfig.class);
	
	private TokenExtractor tokenExtractor = new BearerTokenExtractor();

  @Override
public void configure(HttpSecurity http) throws Exception {/*
	  logger.debug(this.getClass().getName() + " - configure - START");
    http
      .headers()
        .addHeaderWriter(new StaticHeadersWriter("Access-Control-Allow-Origin", "*"))
        .addHeaderWriter(new StaticHeadersWriter("Access-Control-Allow-Headers", "Origin, X-Requested-With, Content-Type, Accept"));
        
    
    http
    .csrf().disable()
    .authorizeRequests()
      .antMatchers(HttpMethod.OPTIONS,"*publishReport*//**").permitAll()//allow CORS option calls
      .antMatchers(HttpMethod.OPTIONS,"**//*scheduleReport*//**").permitAll()//allow CORS option calls
      .antMatchers(HttpMethod.OPTIONS,"**//*viewReport*//**").permitAll()//allow CORS option calls
      .antMatchers(HttpMethod.OPTIONS,"**//*showScheduleSunnary*//**").permitAll()//allow CORS option calls
      
      .antMatchers("/resources/**").permitAll();*//*
  */
	  
	  
		/*http.addFilterAfter(new OncePerRequestFilter() {
			@Override
			protected void doFilterInternal(HttpServletRequest request,
					HttpServletResponse response, FilterChain filterChain)
					throws ServletException, IOException {
				// We don't want to allow access to a resource with no token so clear
				// the security context in case it is actually an OAuth2Authentication
				if (tokenExtractor.extract(request) == null) {
					//SecurityContextHolder.clearContext();
				}
				filterChain.doFilter(request, response);
			}
		}, AbstractPreAuthenticatedProcessingFilter.class);
		http.csrf().disable();
		http.authorizeRequests().anyRequest().authenticated();*/
	  
	  
		http
	      .headers()
	        .addHeaderWriter(new StaticHeadersWriter("Access-Control-Allow-Origin", "*"))
	        .addHeaderWriter(new StaticHeadersWriter("Access-Control-Allow-Headers", "Origin, X-Requested-With, Content-Type, Accept, Authorization"))
	        .addHeaderWriter(new StaticHeadersWriter("Access-Control-Allow-Methods", "POST, GET, OPTIONS, DELETE, OPTIONS"));
		http.csrf().disable();
		//http.authorizeRequests().anyRequest().authenticated();
		// http.requestMatchers().antMatchers("/resource/**").and().authorizeRequests().anyRequest().authenticated().antMatchers("/").permitAll();
		
		 http.anonymous().and().authorizeRequests()
		   .antMatchers(HttpMethod.OPTIONS,"/verifyAppStatus**").permitAll();
		 
		 http.authorizeRequests().antMatchers(HttpMethod.OPTIONS,"/publishReport/**").permitAll()//.authenticated()
		      .antMatchers("/scheduleReport/**").permitAll()//.authenticated()
		      .antMatchers("/viewReport/**").permitAll()//.authenticated()
		      .antMatchers("/showScheduleSummary/**").permitAll()//.authenticated()
		      .antMatchers("/getListOfReportCategories/**").permitAll()//.authenticated()
		      .antMatchers("/deleteScheduledTask/**").permitAll()//.authenticated()
              .antMatchers("/executeDesignerReport/**").permitAll()//.authenticated()
		      .antMatchers("/getViewList/**").permitAll()//.authenticated()
			  .antMatchers("/getCurrentDateTime/**").permitAll();//.authenticated();
	}
  
  @Bean
	public AccessTokenConverter accessTokenConverter() {
		return new DefaultAccessTokenConverter();
	}
  
  @Primary
  @Bean
	public RemoteTokenServices razorremoteTokenServices(final @Value("${spring.oauth2.server.url}") String checkTokenUrl,
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