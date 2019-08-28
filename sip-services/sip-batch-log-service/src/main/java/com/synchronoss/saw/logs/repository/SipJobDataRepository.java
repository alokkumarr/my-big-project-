package com.synchronoss.saw.logs.repository;

import com.synchronoss.saw.batch.entities.BisChannelEntity;
import com.synchronoss.saw.batch.entities.BisRouteEntity;
import com.synchronoss.saw.logs.entities.BisJobEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SipJobDataRepository extends JpaRepository<BisJobEntity, Long> {

  Page<BisJobEntity> findByChannelType(String channelType, Pageable pageable);

  Page<BisJobEntity> findByChannelEntity(BisChannelEntity entity, Pageable pageable);

  Page<BisJobEntity> findByChannelEntityAndRoutelEntity(
      BisChannelEntity entity, BisRouteEntity routeEntity, Pageable pageable);
}
