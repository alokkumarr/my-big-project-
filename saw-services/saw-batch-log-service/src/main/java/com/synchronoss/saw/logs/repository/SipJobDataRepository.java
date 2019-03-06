package com.synchronoss.saw.logs.repository;

import com.synchronoss.saw.logs.entities.SipJobEntity;
import org.springframework.data.jpa.repository.JpaRepository;


public interface SipJobDataRepository 
    extends JpaRepository<SipJobEntity, Long> {

}
