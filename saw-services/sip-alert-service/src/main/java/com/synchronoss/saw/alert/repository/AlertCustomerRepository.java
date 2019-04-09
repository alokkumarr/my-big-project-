package com.synchronoss.saw.alert.repository;

import com.synchronoss.saw.alert.entities.AlertCustomerDetails;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AlertCustomerRepository extends JpaRepository<AlertCustomerDetails, Long> {
  Optional<AlertCustomerDetails> findByCustomerCode(String customerCode);
}
