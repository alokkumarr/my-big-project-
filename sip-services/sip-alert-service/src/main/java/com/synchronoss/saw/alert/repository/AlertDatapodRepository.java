package com.synchronoss.saw.alert.repository;

import com.synchronoss.saw.alert.entities.DatapodDetails;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AlertDatapodRepository extends JpaRepository<DatapodDetails, String> {

  Optional<DatapodDetails> findByDatapodId(String datapodId);
}
