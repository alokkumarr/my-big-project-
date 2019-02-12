package com.synchronoss.saw.logs.repository;

import com.synchronoss.saw.logs.entities.BisFileLog;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface BisFileLogsRepository extends JpaRepository<BisFileLog, String> {

  BisFileLog findByPid(String pid);
  
  default Sort orderByCreatedDate() {
    return new Sort(Sort.Direction.DESC, "createdDate");
  }

  @Query("SELECT COUNT(pid)>0 from BisFileLog Logs where Logs.fileName = :fileName "
      + "and (Logs.mflFileStatus = 'SUCCESS' and Logs.bisProcessState = 'DATA_RECEIVED') ")
  boolean isFileNameExists(@Param("fileName") String fileName);

  @Query("SELECT Logs from BisFileLog Logs where Logs.fileName = :fileName "
      + "and (Logs.mflFileStatus = 'FAILED' and Logs.bisProcessState = 'DUPLICATE') ")
  List<String> isFileNameExistsPid(@Param("fileName") String fileName, Pageable pageable);
  
  @Query("SELECT COUNT(pid)>0 from BisFileLog Logs where Logs.routeSysId = :routeId "
      + "and Logs.channelSysId = :channelSysId "
      + "and ( (Logs.mflFileStatus = 'INPROGRESS' and Logs.bisProcessState = 'DATA_INPROGRESS')"
      + "or (Logs.mflFileStatus = 'FAILED' and  Logs.bisProcessState = 'HOST_NOT_REACHABLE') )")
  boolean isChannelAndRouteIdExists(@Param("routeId") Long routeId,
      @Param("channelSysId") Long channelSysId);

  List<BisFileLog> findByRouteSysId(long routeSysId, Sort sort);

  @Query("SELECT Logs from BisFileLog Logs where (TIMEDIFF(NOW(), Logs.checkpointDate))/60 "
      + "> :noOfMinutes and ( (Logs.mflFileStatus = 'INPROGRESS'  "
      + "and Logs.bisProcessState = 'DATA_INPROGRESS')  "
      + "or (Logs.mflFileStatus = 'FAILED' and  Logs.bisProcessState = 'HOST_NOT_REACHABLE') )")
  Page<BisFileLog> retryIds(@Param("noOfMinutes") Integer noOfMinutes, Pageable pageable);

  @Modifying(clearAutomatically = true)
  @Query("UPDATE BisFileLog Logs SET Logs.mflFileStatus = :fileStatus, "
      + "Logs.bisProcessState = :processStatus WHERE Logs.pid = :pid")
  Integer updateBislogsStatus(@Param("fileStatus") String fileStatus,
      @Param("processStatus") String processStatus, @Param("pid") String pid);

  @Query("SELECT COUNT(pid) from BisFileLog Logs where (TIMEDIFF(NOW(),Logs.checkpointDate))/60 "
      + " > :noOfMinutes and ( (Logs.mflFileStatus = 'INPROGRESS'  "
      + "and Logs.bisProcessState = 'DATA_INPROGRESS') "
      + "or (Logs.mflFileStatus = 'FAILED' and  Logs.bisProcessState = 'HOST_NOT_REACHABLE') )")
  Integer countOfRetries(@Param("noOfMinutes") Integer noOfMinutes);


}
