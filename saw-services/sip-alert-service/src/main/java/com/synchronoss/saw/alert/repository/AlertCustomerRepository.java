package com.synchronoss.saw.alert.repository;

import com.synchronoss.saw.alert.entities.AlertCustomerDetails;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AlertCustomerRepository extends JpaRepository<AlertCustomerDetails, Long> {}
