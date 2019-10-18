package com.sncr.saw.security.app;

import com.sncr.saw.security.app.properties.NSSOProperties;
import com.sncr.saw.security.app.service.TicketHelper;
import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.nio.file.Files;
import java.nio.file.Paths;
import org.apache.coyote.http11.AbstractHttp11Protocol;
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
	
	private static String pid = ManagementFactory.getRuntimeMXBean().getName().split("@")[0];
	private static final String pidPath = "/var/bda/saw-security/run/saw-security.pid";

	/**
     * TomcatServletWebServerFactory has been overridden.
     */

    @Autowired
    private NSSOProperties nSSOProperties;
    @Autowired private TicketHelper ticketHelper;

    @Bean
    public TomcatServletWebServerFactory tomcatEmbedded() {
        TomcatServletWebServerFactory tomcat = new TomcatServletWebServerFactory();
        tomcat.addConnectorCustomizers((TomcatConnectorCustomizer) connector -> {
            if ((connector.getProtocolHandler() instanceof AbstractHttp11Protocol<?>)) {
                ((AbstractHttp11Protocol<?>) connector.getProtocolHandler()).setMaxSwallowSize(-1);
            }
        });
        return tomcat;
    }

 	@Override
	protected SpringApplicationBuilder configure(SpringApplicationBuilder builder) {
		return builder.sources(NSSOApplication.class);
	}

    @SuppressWarnings({"unchecked", "rawtypes"})
    @Bean
    public FilterRegistrationBean<?> jwtFilter() {
      final FilterRegistrationBean registrationBean = new FilterRegistrationBean();
      registrationBean.setFilter(new JwtFilter(nSSOProperties.getJwtSecretKey(),ticketHelper));
      registrationBean.addUrlPatterns("/auth/*");
  
      return registrationBean;
    }

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
