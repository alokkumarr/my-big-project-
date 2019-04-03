package com.synchronoss.sip.alert.repository;

import com.synchronoss.sip.alert.entities.DatapodDetails;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AlertDatapodRepository extends JpaRepository<DatapodDetails, String> {}
