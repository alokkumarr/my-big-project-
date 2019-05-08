package com.synchronoss.saw.scheduler.repository;

import com.synchronoss.saw.scheduler.entities.QrtzTriggers;
import org.springframework.data.jpa.repository.JpaRepository;

public interface QuartzRepository  extends JpaRepository<QrtzTriggers, String> {
  QrtzTriggers findByJobName(String jobName);

}
