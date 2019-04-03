package com.synchronoss.sip.alert.repository;

import com.synchronoss.sip.alert.entities.AlertTriggerDetailsLog;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AlertTriggerLog extends JpaRepository<AlertTriggerDetailsLog,Long> {

}
