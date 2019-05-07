package com.sncr.saw.security.app;

import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.nio.file.Files;
import java.nio.file.Paths;
import org.apache.catalina.connector.Connector;
import org.apache.coyote.http11.AbstractHttp11Protocol;
import org.apache.coyote.http11.Http11NioProtocol;
import org.springframework.beans.factory.annotation.Value;
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

	  @Value("${sip.ssl.enable}")
	  private Boolean sipSslEnable;
	  
	  @Value("${sip.trust.store:}")
	  private String trustStore;

	  @Value("${sip.trust.password:}")
	  private String trustStorePassword; 

	  @Value("${sip.key.store:}")
	  private String keyStore;

	  @Value("${sip.key.password:}")
	  private String keyStorePassword; 
	  
      @Value("${sip.key.alias:}")
      private String keyAlias; 

	/**
     * TomcatServletWebServerFactory has been overridden.
     */

    @Bean
    public TomcatServletWebServerFactory tomcatEmbedded() {
        TomcatServletWebServerFactory tomcat = new TomcatServletWebServerFactory();
        if (sipSslEnable) {
          tomcat.addAdditionalTomcatConnectors(createSslConnector(trustStore,
          trustStorePassword, keyStorePassword, keyStore));
        }
        tomcat.addConnectorCustomizers((TomcatConnectorCustomizer) connector -> {
            if ((connector.getProtocolHandler() instanceof AbstractHttp11Protocol<?>)) {
                ((AbstractHttp11Protocol<?>) connector.getProtocolHandler()).setMaxSwallowSize(-1);
            }
        });
        return tomcat;
    }

    private Connector createSslConnector(String trustStoreLocation,
        String trustPassword, String keyStorePassword, String keyStoreLocation) {
      Connector connector = new Connector("org.apache.coyote.http11.Http11NioProtocol");
      Http11NioProtocol protocol = (Http11NioProtocol) connector.getProtocolHandler();
      try {
        connector.setScheme("https");
        connector.setSecure(true);
        connector.setPort(9001);
        protocol.setSSLEnabled(true);
        protocol.setKeystoreFile(keyStoreLocation);
        protocol.setKeystorePass(keyStorePassword);
        protocol.setTruststoreFile(trustStoreLocation);
        protocol.setTruststorePass(trustPassword);
        protocol.setKeyAlias(keyAlias);
  
      } catch (Exception ex) {
        throw new IllegalStateException(
            "can't access keystore: [" + "keystore" + "] or truststore: [" + "keystore" + "]", ex);
      }
      return connector;
    }

	@Override
	protected SpringApplicationBuilder configure(SpringApplicationBuilder builder) {
		return builder.sources(NSSOApplication.class);
	}

    @SuppressWarnings({"unchecked", "rawtypes"})
    @Bean
    public FilterRegistrationBean<?> jwtFilter() {
      final FilterRegistrationBean registrationBean = new FilterRegistrationBean();
      registrationBean.setFilter(new JwtFilter());
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
