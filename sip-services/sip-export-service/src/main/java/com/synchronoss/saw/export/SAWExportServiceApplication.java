package com.synchronoss.saw.export;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.synchronoss.sip.utils.SipCommonUtils;
import info.faljse.SDNotify.SDNotify;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.event.EventListener;
import org.springframework.core.task.TaskExecutor;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;



@SpringBootApplication
@ComponentScan(basePackages = {"com.synchronoss.saw.export", "com.synchronoss.sip.utils"})
@EnableAsync
public class SAWExportServiceApplication {

  @Value("${ftp.details.file}")
  private String ftpDetailsFile;

  @Value("${ftp.details.privatekeyDir}")
  private String privateKeyDir;

  private static final Logger LOG = LoggerFactory.getLogger(SAWExportServiceApplication.class);

  @Bean(name="workExecutor")
  public TaskExecutor taskExecutor() {
      ThreadPoolTaskExecutor taskExecutor = new ThreadPoolTaskExecutor();
      taskExecutor.setMaxPoolSize(10);
      taskExecutor.setQueueCapacity(10);
      taskExecutor.afterPropertiesSet();
      return taskExecutor;
  }

  public static void main(String[] args) throws IOException {
    ConfigurableApplicationContext ctx =
        SpringApplication.run(SAWExportServiceApplication.class, args);
    LOG.info(ctx.getApplicationName() + " has started.");
  }
  
  public boolean validatePrivateKeys()
      throws IOException {
    boolean status = false;

    ObjectMapper mapper = new ObjectMapper();
    String normalizedFtpDetailsPath = SipCommonUtils.normalizePath(ftpDetailsFile);
    String content = new String(Files.readAllBytes(Paths.get(normalizedFtpDetailsPath)));

    ObjectNode ftpDetails = (ObjectNode) mapper.readTree(content);

    ArrayNode ftpList = (ArrayNode) ftpDetails.path("ftpList");

    for (JsonNode ftpJsonNode : ftpList) {
      ObjectNode ftpNode = (ObjectNode) ftpJsonNode;
      JsonNode privateKeyNode = ftpNode.get("privatekeyFile");

      if (privateKeyNode != null) {
        String privateKeyFile = ftpNode.get("privatekeyFile").asText();

        String privateKeyPath = privateKeyDir + File.separator + privateKeyFile;

        if (!checkIfPrivateKeyExists(privateKeyPath)) {
          throw new IOException("Private key " + privateKeyPath + " not found");
        }
      }
    }

    return status;
  }

  private static boolean checkIfPrivateKeyExists(String privateKeyFullPath) {
    String normalizedPrivateKeyFullPath = SipCommonUtils.normalizePath(privateKeyFullPath);
    File privateKeyFile = new File(normalizedPrivateKeyFullPath);
    return privateKeyFile.exists();
  }

  @EventListener
  public void onApplicationEvent(ApplicationReadyEvent event) {
    LOG.info("Notifying service manager about start-up completion");
    SDNotify.sendNotify();
  }
}
