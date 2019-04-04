package com.synchronoss.saw.alert.repository;

import com.synchronoss.saw.alert.entities.AlertTriggerDetailsLog;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AlertTriggerLog extends JpaRepository<AlertTriggerDetailsLog, Long> {}
