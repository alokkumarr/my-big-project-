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

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Properties;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.integration.file.remote.session.CachingSessionFactory;
import org.springframework.integration.file.remote.session.SessionFactory;
import org.springframework.integration.file.remote.session.SessionFactoryLocator;
import org.springframework.integration.sftp.session.DefaultSftpSessionFactory;
import org.springframework.stereotype.Component;



@SuppressWarnings("rawtypes")
@Component
public class RuntimeSessionFactoryLocator implements SessionFactoryLocator {

  private final Map<Long, DefaultSftpSessionFactory> sessionFactoryMap = new HashMap<>();

  @Autowired
  private BisChannelDataRestRepository bisChannelDataRestRepository;
    
    
  @Override
  public SessionFactory<LsEntry> getSessionFactory(Object key) {
    Long id = Long.valueOf(key.toString());
    DefaultSftpSessionFactory sessionFactory = sessionFactoryMap.get(id);
    if (sessionFactory == null) {
      sessionFactory = generateSessionFactory(id);
      sessionFactoryMap.put(id, sessionFactory);
    }
    return new CachingSessionFactory<LsEntry>(sessionFactory);
  }

  private DefaultSftpSessionFactory generateSessionFactory(Long key) {
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
        String portNumber = rootNode.get("portNo").asText();
        defaultSftpSessionFactory = new DefaultSftpSessionFactory(true);
        String userName = rootNode.get("userName").asText();
        String password = rootNode.get("password").asText();
        defaultSftpSessionFactory.setHost(hostname);
        defaultSftpSessionFactory.setPort(Integer.valueOf(portNumber));
        defaultSftpSessionFactory.setUser(userName);
        defaultSftpSessionFactory.setPassword(password);
        defaultSftpSessionFactory.setAllowUnknownKeys(true);
        Properties prop = new Properties();
        prop.setProperty("StrictHostKeyChecking", "no");
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

  public void invalidateSessionFactoryMap() {
    sessionFactoryMap.clear();
  }

  public void remove(Object key) {
    sessionFactoryMap.remove(key);
  }
}