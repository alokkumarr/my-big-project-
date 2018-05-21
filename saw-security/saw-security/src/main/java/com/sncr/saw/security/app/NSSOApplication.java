/**
 * 
 */
package com.sncr.saw.security.app;

import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.nio.file.Files;
import java.nio.file.Paths;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.embedded.tomcat.TomcatServletWebServerFactory;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;

import com.sncr.saw.security.common.bean.JwtFilter;

/**
 * @author gsan0003
 *
 */

@SpringBootApplication
//@EnableDiscoveryClient
public class NSSOApplication extends SpringBootServletInitializer {
	
	private static String pid = ManagementFactory.getRuntimeMXBean().getName().split("@")[0];
	private static final String pidPath = "/var/bda/saw-security/run/saw-security.pid";
	
	@Bean
	public TomcatServletWebServerFactory tomcatServletWebServerFactory() {
		return new TomcatServletWebServerFactory();
	}

	@Override
	protected SpringApplicationBuilder configure(SpringApplicationBuilder builder) {
		return builder.sources(NSSOApplication.class);
	}

	@Bean
	public FilterRegistrationBean jwtFilter() {
		final FilterRegistrationBean registrationBean = new FilterRegistrationBean();
		registrationBean.setFilter(new JwtFilter());
		registrationBean.addUrlPatterns("/auth/*");

		return registrationBean;
	}

	/*
	 * @Override protected SpringApplicationBuilder configure(
	 * SpringApplicationBuilder builder) { // TODO Auto-generated method stub
	 * return builder.sources(NSSOApplication.class); }
	 */
	public static void main(String[] args) {
		try {        	
			Files.write(Paths.get(pidPath), pid.getBytes());
		} catch (IOException e) {			
			e.printStackTrace();
		} 
		// Launch the application
		ConfigurableApplicationContext context = SpringApplication.run(NSSOApplication.class, args);
		@SuppressWarnings("unused")
		WebSecurityConfig config = context.getBean(WebSecurityConfig.class);        
	}
}
