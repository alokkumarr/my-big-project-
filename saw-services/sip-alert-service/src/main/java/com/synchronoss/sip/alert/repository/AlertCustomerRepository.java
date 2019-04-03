package com.synchronoss.sip.alert.repository;

import com.synchronoss.sip.alert.entities.AlertCustomerDetails;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AlertCustomerRepository extends JpaRepository<AlertCustomerDetails, Long> {

}
