package com.synchronoss.saw.entities.repositories;

import com.synchronoss.saw.entities.BisRouteEntity;
import io.swagger.annotations.Api;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Api(value = "This end point provides a way to communicate with route entity", tags = "Route Enity")
@Repository
public interface BisRouteDataRestRepository
    extends JpaRepository<BisRouteEntity, Long> {
  Page<BisRouteEntity> findByBisChannelSysId(Long bisChannelSysId, Pageable pageable);

}
