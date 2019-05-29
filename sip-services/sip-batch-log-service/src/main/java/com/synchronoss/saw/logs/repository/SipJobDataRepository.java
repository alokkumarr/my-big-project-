package com.synchronoss.saw.logs.repository;

import com.synchronoss.saw.logs.entities.BisJobEntity;

import java.util.List;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;




public interface SipJobDataRepository 
    extends JpaRepository<BisJobEntity, Long> {
  List<BisJobEntity> findByChannelType(String jobType, Pageable pageable);
  
  List<BisJobEntity> findByBisChannelSysIdAndBisRouteSysId(Long channelSysId, 
      Long routeSysId,  Pageable pageable);

  List<BisJobEntity> findByBisChannelSysId(Long channelId, Pageable pageable);

}
