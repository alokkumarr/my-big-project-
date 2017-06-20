/**
 * 
 */
package com.sncr.saw.security.app;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.context.embedded.tomcat.TomcatEmbeddedServletContainerFactory;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.boot.web.support.SpringBootServletInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;

import com.sncr.saw.security.common.bean.JwtFilter;

/**
 * @author gsan0003
 *
 */

@SpringBootApplication
//@EnableDiscoveryClient
public class NSSOApplicationMicro extends SpringBootServletInitializer {

	@Bean
	public TomcatEmbeddedServletContainerFactory tomcatEmbeddedServletContainerFactory() {
		return new TomcatEmbeddedServletContainerFactory();
	}

	@Override
	protected SpringApplicationBuilder configure(SpringApplicationBuilder builder) {
		// TODO Auto-generated method stub
		return builder.sources(NSSOApplicationMicro.class);
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
		ConfigurableApplicationContext context = SpringApplication.run(NSSOApplicationMicro.class, args);
		@SuppressWarnings("unused")
		WebSecurityConfig config = context.getBean(WebSecurityConfig.class);

	}
}
