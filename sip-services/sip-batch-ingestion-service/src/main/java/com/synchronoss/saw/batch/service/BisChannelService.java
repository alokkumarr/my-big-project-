package com.synchronoss.saw.batch.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.synchronoss.saw.batch.controller.SawBisRouteController;
import com.synchronoss.saw.batch.entities.BisChannelEntity;
import com.synchronoss.saw.batch.entities.repositories.BisChannelDataRestRepository;
import com.synchronoss.saw.batch.exception.ResourceNotFoundException;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class BisChannelService {

  @Autowired
  BisChannelDataRestRepository bisChannelRepository;

  @Autowired
  BisRouteService bisRouteService;

  private static final Logger logger = LoggerFactory.getLogger(BisChannelService.class);
  
  /**
   * Activates or deactivates a channel to temporarily
   * stop or resume.
   */
  public void activateOrDeactivateChannel(Long channelId, boolean isActivate) {

    Optional<BisChannelEntity> bisChannel = bisChannelRepository.findById(channelId);
    logger.trace("Retriving channel");
    if (bisChannel.isPresent()) {
      logger.trace("Retriving channel successful");
      BisChannelEntity bisChannelEntity = bisChannel.get();
      Long status;
      if (isActivate) {
        status = 1L;
      } else {
        status = 0L;
      }
      bisChannelEntity.setStatus(status);
      logger.trace("updating channel status to deactivate");
      bisChannelRepository.saveAndFlush(bisChannelEntity);
      logger.trace("updating channel status to deactivate");
      logger.trace("updating routes");
      bisRouteService.activateOrDeactivateRoutes(channelId, isActivate);
    } else {
      throw new ResourceNotFoundException("No resource found ");
    }
  }
  
  /**
   * Checks if there is any channel already
   * exists with given name.
   */
  public boolean isChannelNameExists(String channelName) {
    List<BisChannelEntity> channelEntities = bisChannelRepository.findAll();
    ObjectMapper objectMapper = new ObjectMapper();
    Optional<BisChannelEntity> channels = channelEntities.stream().filter(bisChannelEntity -> {
      JsonNode metaDataNode;
      JsonNode existingChannel;
      try {
        metaDataNode = objectMapper.readTree(bisChannelEntity.getChannelMetadata());
        existingChannel = metaDataNode.get("channelName");
        if (existingChannel != null && existingChannel.asText().equalsIgnoreCase(channelName)) {
          return true;
        }
      } catch (IOException exception) {
        logger.error(exception.getMessage());
      }
      return false;
    }).findAny();

    return channels.isPresent();
  }

}
