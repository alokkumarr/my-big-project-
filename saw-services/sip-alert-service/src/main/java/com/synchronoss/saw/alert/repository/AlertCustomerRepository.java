package com.synchronoss.saw.alert.repository;

import com.synchronoss.saw.alert.entities.AlertCustomerDetails;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface AlertCustomerRepository extends JpaRepository<AlertCustomerDetails, Long> {
  @Query(
      value = "SELECT * from ALERT_CUSTOMER_DETAILS ACD where ACD.CUSTOMER_CODE = ?1",
      nativeQuery = true)
  Optional<AlertCustomerDetails> findByCustomerCode(String customerCode);
}
