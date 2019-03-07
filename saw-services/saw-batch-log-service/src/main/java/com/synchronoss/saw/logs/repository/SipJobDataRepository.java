package com.synchronoss.saw.logs.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.synchronoss.saw.logs.entities.SipJobEntity;


public interface SipJobDataRepository 
    extends JpaRepository<SipJobEntity, Long> {
  List<SipJobEntity> findByjobType(String jobType);

}
