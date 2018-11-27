package com.synchronoss.saw.logs.repository;

import com.synchronoss.saw.logs.entities.BisFileLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;



@Repository
public interface BisFileLogsRepository extends JpaRepository<BisFileLog, String> {

  BisFileLog findByPid(String pid);

  @Query("SELECT COUNT(pid)>0 from BisFileLog Logs where Logs.fileName = :fileName "
      + "and Logs.mflFileStatus != 'FAILED' and Logs.mflFileStatus != 'INPROGRESS' ")
  boolean isFileNameExists(@Param("fileName") String fileName);

}
