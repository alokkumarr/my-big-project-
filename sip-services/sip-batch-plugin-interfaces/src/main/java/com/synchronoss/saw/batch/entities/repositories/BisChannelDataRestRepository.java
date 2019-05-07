package com.synchronoss.saw.batch.entities.repositories;

import com.synchronoss.saw.batch.entities.BisChannelEntity;
import io.swagger.annotations.Api;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Api(value = "This end point provides a way to communicate with channel entity",
    tags = "Channel Enity")
@Repository
public interface BisChannelDataRestRepository extends JpaRepository<BisChannelEntity, Long> {

}
