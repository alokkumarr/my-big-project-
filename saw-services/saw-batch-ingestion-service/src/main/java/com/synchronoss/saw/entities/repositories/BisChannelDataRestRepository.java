package com.synchronoss.saw.entities.repositories;

import com.synchronoss.saw.entities.BisChannelEntity;
import io.swagger.annotations.Api;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

@Api(value = "This end point provides a way to communicate with channel entity",
    tags = "Channel Enity")
@RepositoryRestResource(path = "channels", collectionResourceRel = "channels")
public interface BisChannelDataRestRepository extends
    PagingAndSortingRepository<BisChannelEntity, Long> {

}
