package com.synchronoss.saw.batch.entities.repositories;

import io.swagger.annotations.Api;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.synchronoss.saw.batch.entities.BisRouteEntity;


@Api(value = "This end point provides a way to communicate with route entity", tags = "Route Enity")
@Repository
public interface BisRouteDataRestRepository
    extends JpaRepository<BisRouteEntity, Long> {
  Page<BisRouteEntity> findByBisChannelSysId(Long bisChannelSysId, Pageable pageable);

}
