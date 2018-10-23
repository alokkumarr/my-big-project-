package com.synchronoss.saw.entities.repositories;

import com.synchronoss.saw.entities.BisRouteEntity;
import io.swagger.annotations.Api;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

@Api(value = "This end point provides a way to communicate with route entity", tags = "Route Enity")

@RepositoryRestResource(path = "routes", collectionResourceRel = "routes")
public interface BisRouteDataRestRepository
    extends PagingAndSortingRepository<BisRouteEntity, Long> {

}
