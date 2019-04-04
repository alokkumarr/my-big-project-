package com.synchronoss.saw.alert.repository;

import com.synchronoss.saw.alert.entities.AlertRulesDetails;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AlertRulesRepository extends JpaRepository<AlertRulesDetails, Long> {}
