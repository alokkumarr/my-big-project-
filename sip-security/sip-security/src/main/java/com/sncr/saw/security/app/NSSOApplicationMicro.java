/**
 * 
 */
package com.sncr.saw.security.app;

import com.sncr.saw.security.app.properties.NSSOProperties;
import com.sncr.saw.security.app.service.TicketHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.embedded.tomcat.TomcatServletWebServerFactory;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.boot.web.embedded.tomcat.TomcatConnectorCustomizer;
import com.sncr.saw.security.common.util.JwtFilter;
import org.apache.coyote.http11.AbstractHttp11Protocol;


@SpringBootApplication
public class NSSOApplicationMicro extends SpringBootServletInitializer {

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
		// TODO Auto-generated method stub
		return builder.sources(NSSOApplicationMicro.class);
	}

	@Bean
	public FilterRegistrationBean jwtFilter() {
		final FilterRegistrationBean registrationBean = new FilterRegistrationBean();
		registrationBean.setFilter(new JwtFilter(nSSOProperties.getJwtSecretKey(), ticketHelper));
		registrationBean.addUrlPatterns("/sip-security/auth/*");

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
