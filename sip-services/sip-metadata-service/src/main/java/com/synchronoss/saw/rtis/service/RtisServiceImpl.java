package com.synchronoss.saw.rtis.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.gson.JsonElement;
import com.synchronoss.saw.exceptions.SipCreateEntityException;
import com.synchronoss.saw.rtis.metadata.RtisMetadata;
import com.synchronoss.saw.rtis.model.request.PrimaryStreams;
import com.synchronoss.saw.rtis.model.request.RtisConfiguration;
import com.synchronoss.saw.rtis.model.request.SecondaryStreams;
import com.synchronoss.saw.util.SipMetadataUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import sncr.bda.base.MaprConnection;

/**
 * ServiceImpl class for rtis config service.
 *
 * @author alok.kumarr
 * @since 3.4.0
 */
@Service
public class RtisServiceImpl implements RtisService {

  private static final Logger LOGGER = LoggerFactory.getLogger(RtisServiceImpl.class);
  private static final String BOOTSTRAP_SERVER = "localhost:9092";

  @Value("${metastore.base}")
  @NotNull
  private String basePath;

  private RtisMetadata rtisMetadataStore;
  private ObjectMapper mapper = new ObjectMapper();
  private final String tableName = "rtisConfiguration";

  @Override
  public void createConfig(RtisConfiguration configuration) {
    try {
      configuration.setBootstrapServers(BOOTSTRAP_SERVER);
      if (configuration.getId().equalsIgnoreCase("countly")) {
        configuration.setClazz("synchronoss.handlers.countly.CountlyGenericBridge");
        configuration.setKeySerializer("org.apache.kafka.common.serialization.StringSerializer");
        configuration.setValueSerializer("org.apache.kafka.common.serialization.StringSerializer");
      } else {
        configuration.setClazz("synchronoss.handlers.GenericEventHandler");
        configuration.setKeySerializer("org.apache.kafka.common.serialization.ByteArraySerializer");
        configuration.setValueSerializer(
            "org.apache.kafka.common.serialization.ByteArraySerializer");
      }

      List<SecondaryStreams> secondaryStreams = new ArrayList<>();
      List<PrimaryStreams> primaryStreams = configuration.getPrimaryStreams();
      if (primaryStreams != null && !primaryStreams.isEmpty()) {
        primaryStreams.forEach(primaryStream -> {
          SecondaryStreams streams = new SecondaryStreams();
          streams.setQueue(primaryStream.getQueue());
          streams.setTopic(primaryStream.getTopic());
          secondaryStreams.add(streams);
        });
      }
      configuration.setSecondaryStreams(secondaryStreams);
      JsonElement config =
          SipMetadataUtils.toJsonElement(mapper.writeValueAsString(configuration));
      final String configId = UUID.randomUUID().toString();
      rtisMetadataStore = new RtisMetadata(tableName, basePath);
      rtisMetadataStore.create(configId, config);
    } catch (Exception ex) {
      LOGGER.error("Exception occurred while creating configuration", ex);
      throw new SipCreateEntityException("Exception occurred while creating configuration");
    }
  }

  @Override
  public Object fetchAppKeys(@NotNull(message = "Customer code cannot be null")
                             @Valid String customerCode) {
    try {
      new RtisMetadata(tableName, basePath);
      MaprConnection maprConnection = new MaprConnection(basePath, tableName);

      String[] fields = {"app_key"};
      ObjectNode node = getJsonNodes("customerCode", customerCode);

      return maprConnection.runMaprDBQuery(fields, node.toString(), null, null);
    } catch (Exception ex) {
      LOGGER.error("Error occurred while fetching the app keys data", ex);
    }
    return null;
  }


  @Override
  public Object fetchConfigByAppKeys(@NotNull(message = "Application key cannot be null")
                                     @Valid String appKey) {
    try {
      new RtisMetadata(tableName, basePath);
      MaprConnection maprConnection = new MaprConnection(basePath, tableName);

      String[] fields = {"*"};
      ObjectMapper objectMapper = new ObjectMapper();
      ObjectNode node = getJsonNodes("app_key", appKey);

      List<JsonNode> nodeList =
          maprConnection.runMaprDBQuery(fields, node.toString(), null, null);
      return nodeList != null && !nodeList.isEmpty() ? buildConfig(objectMapper, nodeList) : null;
    } catch (Exception ex) {
      LOGGER.error("Error occurred while fetching the app keys data", ex);
    }
    return null;
  }

  /**
   * Build RTIS configuration object with same structure which requires to perform RTIS events.
   *
   * @param objectMapper mapper object
   * @param nodeList     list of nodes
   * @return Object config object
   */
  private Object buildConfig(ObjectMapper objectMapper, List<JsonNode> nodeList) {
    ArrayNode arrayNode = mapper.createArrayNode();
    nodeList.forEach(jNode -> {
      try {
        JsonElement config = SipMetadataUtils.toJsonElement(mapper.writeValueAsString(jNode));
        ObjectNode node = mapper.createObjectNode();
        RtisConfiguration configuration =
            objectMapper.readValue(config.toString(), RtisConfiguration.class);
        node.put("class", configuration.getClazz());
        node.put("app_key", configuration.getAppKey());
        node.put("key.serializer", configuration.getKeySerializer());
        node.put("timeout.ms", configuration.getTimeoutMs().intValue());
        node.put("batch.size", configuration.getBatchSize().intValue());
        node.put("value.serializer", configuration.getValueSerializer());
        node.put("bootstrap.servers", configuration.getBootstrapServers());
        node.set("streams_1", buildStreams(configuration.getPrimaryStreams(), null));
        node.set("streams_2", buildStreams(null, configuration.getSecondaryStreams()));
        node.put("block.on.buffer.full", configuration.getBlockOnBufferFull().booleanValue());
        arrayNode.add(node);
      } catch (IOException ex) {
        LOGGER.error("Error occurred while building the config", ex);
      }
    });
    return arrayNode;
  }

  /**
   * Build stream with topic and queues for RTIS on demand config process.
   *
   * @param primaryStreams   primary stream
   * @param secondaryStreams secondary stream
   * @return nodes of array
   */
  private ArrayNode buildStreams(List<PrimaryStreams> primaryStreams,
                                 List<SecondaryStreams> secondaryStreams) {
    ArrayNode steamList = mapper.createArrayNode();
    if (primaryStreams != null && !primaryStreams.isEmpty()) {
      primaryStreams.forEach(primaryStream -> {
        ObjectNode streamNode = mapper.createObjectNode();
        streamNode.put("topic", primaryStream.getTopic());
        streamNode.put("queue", primaryStream.getQueue());
        steamList.add(streamNode);
      });
    } else if (secondaryStreams != null && !secondaryStreams.isEmpty()) {
      secondaryStreams.forEach(secondaryStream -> {
        ObjectNode streamNode = mapper.createObjectNode();
        streamNode.put("topic", secondaryStream.getTopic());
        streamNode.put("queue", secondaryStream.getQueue());
        steamList.add(streamNode);
      });
    }
    return steamList;
  }

  @Override
  public Boolean deleteConfiguration(@NotNull(message = "Application key cannot be null")
                                     @Valid String appKey) {
    try {
      new RtisMetadata(tableName, basePath);
      MaprConnection maprConnection = new MaprConnection(basePath, tableName);

      String[] fields = {"*"};
      ObjectNode node = getJsonNodes("app_key", appKey);

      return maprConnection.deleteByMaprDBQuery(fields, node.toString());
    } catch (Exception ex) {
      LOGGER.error("Error occurred while fetching the app keys data", ex);
    }
    return null;
  }

  /**
   * Build a query node which need to be executed on mapr db.
   *
   * @param columnName  column name for the query
   * @param columnValue column value for the query
   * @return ObjectNode
   */
  private ObjectNode getJsonNodes(String columnName, String columnValue) {
    ObjectMapper objectMapper = new ObjectMapper();
    ObjectNode node = objectMapper.createObjectNode();
    ObjectNode objectNode = node.putObject("$eq");
    objectNode.put(columnName, columnValue);
    return node;
  }
}