package com.sncr.saw.security.app;

import com.sncr.saw.security.app.properties.NSSOProperties;
import com.sncr.saw.security.app.service.TicketHelper;
import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.nio.file.Files;
import java.nio.file.Paths;
import org.apache.coyote.http11.AbstractHttp11Protocol;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.embedded.tomcat.TomcatConnectorCustomizer;
import org.springframework.boot.web.embedded.tomcat.TomcatServletWebServerFactory;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;

import com.sncr.saw.security.common.util.JwtFilter;

/**
 * @author gsan0003
 *
 */

@SpringBootApplication
public class NSSOApplication extends SpringBootServletInitializer {

  private static final Logger LOGGER = LoggerFactory.getLogger(NSSOApplication.class);
	
	private static String pid = ManagementFactory.getRuntimeMXBean().getName().split("@")[0];
	private static final String pidPath = "/var/bda/saw-security/run/saw-security.pid";

	/**
     * TomcatServletWebServerFactory has been overridden.
     */

    @Autowired
    private NSSOProperties nSSOProperties;
    @Autowired private TicketHelper ticketHelper;

 	@Override
	protected SpringApplicationBuilder configure(SpringApplicationBuilder builder) {
		return builder.sources(NSSOApplication.class);
	}

	public static void main(String[] args) {
		try {        	
			Files.write(Paths.get(pidPath), pid.getBytes());
		} catch (IOException e) {
      LOGGER.error("Error while reading file.");
		}
		// Launch the application
		ConfigurableApplicationContext context = SpringApplication.run(NSSOApplication.class, args);
		@SuppressWarnings("unused")
		WebSecurityConfig config = context.getBean(WebSecurityConfig.class);        
	}
}
