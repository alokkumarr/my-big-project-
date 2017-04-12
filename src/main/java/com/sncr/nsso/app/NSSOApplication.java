/**
 * 
 */
package com.sncr.nsso.app;

import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.nio.file.Files;
import java.nio.file.Paths;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.context.embedded.tomcat.TomcatEmbeddedServletContainerFactory;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.boot.web.support.SpringBootServletInitializer;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;

import com.sncr.nsso.common.bean.JwtFilter;

/**
 * @author gsan0003
 *
 */

@SpringBootApplication
@EnableDiscoveryClient
public class NSSOApplication extends SpringBootServletInitializer {

	@Bean
	public TomcatEmbeddedServletContainerFactory tomcatEmbeddedServletContainerFactory() {
		return new TomcatEmbeddedServletContainerFactory();
	}

	@Override
	protected SpringApplicationBuilder configure(SpringApplicationBuilder builder) {
		// TODO Auto-generated method stub
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

		// Launch the application
		ConfigurableApplicationContext context = SpringApplication.run(NSSOApplication.class, args);
		@SuppressWarnings("unused")
		WebSecurityConfig config = context.getBean(WebSecurityConfig.class);
		String prcessDetails = ManagementFactory.getRuntimeMXBean().getName();
        try {
        	// TODO dummy path for now
			Files.write(Paths.get("/opt/product/dev/saw-deploy"), prcessDetails.split("@")[0].getBytes() );
		} catch (IOException e) {
			// TODO handle the exception
			e.printStackTrace();
		} 
	}
}
