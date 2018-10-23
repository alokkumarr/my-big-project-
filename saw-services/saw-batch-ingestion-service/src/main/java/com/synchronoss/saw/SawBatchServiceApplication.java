package com.synchronoss.saw;

import com.synchronoss.saw.entities.audit.AuditingDateTimeProvider;
import com.synchronoss.saw.entities.audit.CurrentTimeDateTimeService;
import com.synchronoss.saw.entities.audit.DateTimeService;
import info.faljse.SDNotify.SDNotify;
import java.util.List;
import java.util.Map;
import javax.persistence.EntityManagerFactory;
import org.apache.coyote.http11.AbstractHttp11Protocol;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.ExitCodeEvent;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.boot.web.embedded.tomcat.TomcatConnectorCustomizer;
import org.springframework.boot.web.embedded.tomcat.TomcatServletWebServerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.event.EventListener;
import org.springframework.data.auditing.DateTimeProvider;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.orm.jpa.JpaTransactionManager;
import ro.fortsoft.pf4j.PluginManager;
import ro.fortsoft.pf4j.PluginWrapper;

@EnableJpaAuditing(dateTimeProviderRef = "dateTimeProvider")
@SpringBootApplication
@ComponentScan("com.synchronoss")
public class SawBatchServiceApplication implements ApplicationRunner {
  private static final Logger LOG = LoggerFactory.getLogger(SawBatchServiceApplication.class);


  @Autowired
  private PluginManager pluginManager;
  @Autowired
  private ApplicationContext context;

  /**
   * This is the entry method of the class.
   */
  public static void main(String[] args) {
    ConfigurableApplicationContext ctx =
        SpringApplication.run(SawBatchServiceApplication.class, args);
    LOG.info(ctx.getApplicationName() + " has started.");

  }

  @Override
  public void run(ApplicationArguments args) throws Exception {


    Map<String, SIPExtensionPoint> map = context.getBeansOfType(SIPExtensionPoint.class);
    for (String key : map.keySet()) {
      System.err.println(map.get(key));
    }
    List<PluginWrapper> list = pluginManager.getPlugins();
    for (PluginWrapper pluginWrapper : list) {
      System.out.println(pluginWrapper.getPluginId());
      List<?> extensions = pluginManager.getExtensions(pluginWrapper.getPluginId());
      for (Object extension : extensions) {
        LOG.info("Extension Loaded" + extension);

      }
      List<PluginWrapper> startedPlugins = pluginManager.getStartedPlugins();
      for (PluginWrapper plugin : startedPlugins) {
        String pluginId = plugin.getDescriptor().getPluginId();
        System.out.println(
            String.format("Extensions instances added by plugin '%s' for extension point '%s':",
                pluginId, SIPExtensionPoint.class.getName()));
        List<?> extensionsPoints = pluginManager.getExtensions(pluginId);
        for (Object extension : extensionsPoints) {
          LOG.info("Extension Started" + extension);
        }
      }

    }
    pluginManager.stopPlugins();
  }

  /**
   * TomcatServletWebServerFactory has been overridden.
   */
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

  @EventListener
  public void onApplicationEvent(ApplicationReadyEvent event) {
    LOG.info("Notifying service manager about start-up completion");
    SDNotify.sendNotify();
  }

  @EventListener
  public void exitEvent(ExitCodeEvent event) {
    LOG.info("Application exiting : " + event.getExitCode());

  }
  /**
   * Initializing Transaction Manager.
   * @param entityManagerFactory entity manager.
   * @return {@link JpaTransactionManager}.
   */
  
  @Bean
  JpaTransactionManager transactionManager(EntityManagerFactory entityManagerFactory) {
    JpaTransactionManager transactionManager = new JpaTransactionManager();
    transactionManager.setEntityManagerFactory(entityManagerFactory);
    return transactionManager;
  }

  @Bean
  DateTimeService currentTimeDateTimeService() {
    return new CurrentTimeDateTimeService();
  } 

  @Bean
  DateTimeProvider dateTimeProvider(DateTimeService dateTimeService) {
    return new AuditingDateTimeProvider(dateTimeService);
  }


}
