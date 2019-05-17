package com.synchronoss.saw.logs.repository;

import com.synchronoss.saw.logs.entities.BisJobEntity;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;




public interface SipJobDataRepository 
    extends JpaRepository<BisJobEntity, Long> {
  List<BisJobEntity> findByjobType(String jobType);

}
