package com.synchronoss.saw.logs.repository;

import com.synchronoss.saw.logs.entities.BisFileLogs;
import javax.persistence.NamedQuery;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;



@Repository
public interface FileLogsRepository extends JpaRepository<BisFileLogs, String> {

  BisFileLogs findByPid(String pid);

  @Query("SELECT COUNT(pid)>0 from BisFileLogs Logs where Logs.fileName = :fileName ")
  boolean isFileNameExists(@Param("fileName") String fileName);

}
