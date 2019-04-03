package com.synchronoss.sip.alert.repository;

import com.synchronoss.sip.alert.entities.AlertRulesDetails;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AlertRulesRepository extends JpaRepository<AlertRulesDetails, Long> {}
