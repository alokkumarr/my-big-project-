package com.synchronoss.saw.batch.sftp.integration;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.jcraft.jsch.ChannelSftp.LsEntry;
import com.synchronoss.saw.batch.entities.BisChannelEntity;
import com.synchronoss.saw.batch.entities.repositories.BisChannelDataRestRepository;
import com.synchronoss.saw.batch.exception.SftpProcessorException;
import com.synchronoss.saw.batch.utils.IntegrationUtils;
import com.synchronoss.saw.batch.utils.SipObfuscation;
import java.io.IOException;
import java.util.Optional;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.integration.file.remote.session.CachingSessionFactory;
import org.springframework.integration.file.remote.session.SessionFactory;
import org.springframework.integration.file.remote.session.SessionFactoryLocator;
import org.springframework.integration.sftp.session.DefaultSftpSessionFactory;
import org.springframework.stereotype.Component;



@SuppressWarnings("rawtypes")
@Component
public class RuntimeSessionFactoryLocator implements SessionFactoryLocator {

  private static final Logger logger = 
      LoggerFactory.getLogger(RuntimeSessionFactoryLocator.class);
  

  @Autowired
  private BisChannelDataRestRepository bisChannelDataRestRepository;
    
    
  @Override
  public SessionFactory<LsEntry> getSessionFactory(Object key) {
    Long id = Long.valueOf(key.toString());
    DefaultSftpSessionFactory sessionFactory = null;
    if (sessionFactory == null) {
      try {
        sessionFactory = generateSessionFactory(id);
      } catch (Exception e) {
        logger.error("Exception occurred while generating the session", e);
      }
    }
    CachingSessionFactory<LsEntry> cache = new CachingSessionFactory<>(sessionFactory, 10);
    cache.setSessionWaitTimeout(5000);
    return cache;
  }

  private DefaultSftpSessionFactory generateSessionFactory(Long key) throws Exception {
    Optional<BisChannelEntity> entity = bisChannelDataRestRepository.findById(key);
    DefaultSftpSessionFactory defaultSftpSessionFactory = null;
    if (entity.isPresent()) {
      ObjectMapper objectMapper = new ObjectMapper();
      objectMapper.configure(JsonParser.Feature.ALLOW_UNQUOTED_FIELD_NAMES, true);
      objectMapper.enable(DeserializationFeature.FAIL_ON_READING_DUP_TREE_KEY);
      BisChannelEntity bisChannelEntity = entity.get();
      JsonNode nodeEntity = null;
      ObjectNode rootNode = null;
      try {
        nodeEntity = objectMapper.readTree(bisChannelEntity.getChannelMetadata());
        rootNode = (ObjectNode) nodeEntity;
        String hostname = rootNode.get("hostName").asText();
        defaultSftpSessionFactory = new DefaultSftpSessionFactory(true);
        String portNumber = rootNode.get("portNo").asText();
        SipObfuscation obfuscator = new SipObfuscation(IntegrationUtils.secretKey);
        String password = obfuscator.decrypt(rootNode.get("password").asText());
        //String password = rootNode.get("password").asText();
        defaultSftpSessionFactory.setHost(hostname);
        defaultSftpSessionFactory.setPort(Integer.valueOf(portNumber));
        String userName = rootNode.get("userName").asText();        
        defaultSftpSessionFactory.setUser(userName);
        defaultSftpSessionFactory.setPassword(password);
        defaultSftpSessionFactory.setAllowUnknownKeys(true);
        defaultSftpSessionFactory.setTimeout(6000);
        Properties prop = new Properties();
        prop.setProperty("StrictHostKeyChecking", "no");
        prop.setProperty("bufferSize", "100000");
        //prop.setProperty("PreferredAuthentications", "password");
        defaultSftpSessionFactory.setSessionConfig(prop);
      } catch (IOException e) {
        throw new SftpProcessorException("for the given id + " + key + ""
      + " details does not exist");
      }
    } else {
      throw new SftpProcessorException("for the given id + "
       + "" + key + " details does not exist");
    }
    return defaultSftpSessionFactory;
  }
}