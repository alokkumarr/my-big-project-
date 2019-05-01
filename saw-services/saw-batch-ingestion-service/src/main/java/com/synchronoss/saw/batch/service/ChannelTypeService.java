package com.synchronoss.saw.batch.service;

import com.synchronoss.saw.batch.entities.BisChannelEntity;
import com.synchronoss.saw.batch.entities.BisRouteEntity;
import com.synchronoss.saw.batch.entities.repositories.BisChannelDataRestRepository;
import com.synchronoss.saw.batch.entities.repositories.BisRouteDataRestRepository;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ChannelTypeService {

  @Autowired
  private BisRouteDataRestRepository bisRouteDataRestRepository;

  @Autowired
  private BisChannelDataRestRepository bisChannelRepository;
  
  /**
   * Retrive cahnnel type based on routeId.
   * @param routeId route identifier
   * @return channel type.
   */
  public String findChannelTypeFromRouteId(Long routeId) {
    Optional<BisRouteEntity> routeOptional = bisRouteDataRestRepository
        .findById(routeId);
    String channelType = "";
    if (routeOptional.isPresent()) {
      BisRouteEntity route = routeOptional.get();
      Optional<BisChannelEntity> bisChannelOptional = bisChannelRepository
          .findById(route.getBisChannelSysId());
      if (bisChannelOptional.isPresent()) {
        BisChannelEntity bisChannel = bisChannelOptional.get();
        channelType = bisChannel.getChannelType();
      }
    }
    return channelType;
  }
  
  /**
   * Retrive channel type from channelId.
   * 
   * @param channelId channel identifier.
   * @return channel type
   */
  public String findChannelTypeFromChannelId(Long channelId) {
    String channelType = "";
    Optional<BisChannelEntity> bisChannelOptional = bisChannelRepository
        .findById(channelId);
    if (bisChannelOptional.isPresent()) {
      BisChannelEntity bisChannel = bisChannelOptional.get();
      channelType = bisChannel.getChannelType();
    }

    return channelType;

  }
}
