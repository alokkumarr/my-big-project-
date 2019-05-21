package com.synchronoss.saw.logs.repository;

import com.synchronoss.saw.logs.entities.BisFileLog;
import java.util.List;

import javax.persistence.LockModeType;
import javax.transaction.Transactional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;



@Repository
public interface BisFileLogsRepository extends JpaRepository<BisFileLog, String> {

  BisFileLog findByPid(String pid);
  
  default Sort orderBy(String column) {
    return new Sort(Sort.Direction.DESC, column);
  }
  
  @Transactional
  @Lock(LockModeType.PESSIMISTIC_WRITE)
  @Query("SELECT COUNT(pid)>0 from BisFileLog Logs where Logs.fileName = :fileName "
      + "and ( (Logs.mflFileStatus = 'SUCCESS' and Logs.bisProcessState = 'DATA_RECEIVED') or "
      + "(Logs.mflFileStatus = 'INPROGRESS' and Logs.bisProcessState = 'DATA_INPROGRESS')) ")
  boolean isFileNameExists(@Param("fileName") String fileName);

  @Query("SELECT Logs from BisFileLog Logs where Logs.fileName = :fileName "
      + "and (Logs.mflFileStatus = 'FAILED' and Logs.bisProcessState = 'DUPLICATE') ")
  Page<BisFileLog> isFileNameExistsPid(@Param("fileName") String fileName, Pageable pageable);

  @Query("SELECT Logs from BisFileLog Logs where Logs.bisProcessState = :status and "
      + " ( (Logs.routeSysId = :routeId and Logs.channelSysId = :channelSysId) "
      + " and (Logs.channelType = :channelType) ) ")
  Page<BisFileLog> isStatusExistsForProcess(@Param("status") String status,
      @Param("channelSysId") Long channelSysId, @Param("routeId") Long routeId,
      @Param("channelType") String channelType, Pageable pageable);

  @Query("SELECT COUNT(pid)>0 from BisFileLog Logs where Logs.routeSysId = :routeId "
      + "and Logs.channelSysId = :channelSysId "
      + "and ( (Logs.mflFileStatus = 'FAILED' and  Logs.bisProcessState = 'HOST_NOT_REACHABLE')"
      + "or (Logs.mflFileStatus = 'FAILED' and  Logs.bisProcessState = 'FAILED') )")
  boolean isChannelAndRouteIdExists(@Param("routeId") Long routeId,
      @Param("channelSysId") Long channelSysId);

  List<BisFileLog> findByRouteSysId(long routeSysId, Sort sort);

  @Query("SELECT Logs from BisFileLog Logs where (TIMEDIFF(NOW(), Logs.checkpointDate))/60 "
      + "> :noOfMinutes and ( (Logs.mflFileStatus = 'FAILED' "
      + "and  Logs.bisProcessState = 'HOST_NOT_REACHABLE')"
      + "or (Logs.mflFileStatus = 'FAILED' and  "
      + "Logs.bisProcessState = 'FAILED') )")
  Page<BisFileLog> retryIds(@Param("noOfMinutes") Integer noOfMinutes, Pageable pageable);

  @Modifying(clearAutomatically = true)
  @Query("UPDATE BisFileLog Logs SET Logs.mflFileStatus = :fileStatus, "
      + "Logs.bisProcessState = :processStatus, Logs.modifiedDate = NOW() WHERE Logs.pid = :pid")
  Integer updateBislogsStatus(@Param("fileStatus") String fileStatus,
      @Param("processStatus") String processStatus, @Param("pid") String pid);

  @Query("SELECT COUNT(pid) from BisFileLog Logs where (TIMEDIFF(NOW(),Logs.checkpointDate))/60 "
      + " > :noOfMinutes and ( (Logs.mflFileStatus = 'FAILED' "
      + "and  Logs.bisProcessState = 'HOST_NOT_REACHABLE') "
      + "or (Logs.mflFileStatus = 'FAILED' and  "
      + "Logs.bisProcessState = 'FAILED') )")
  Integer countOfRetries(@Param("noOfMinutes") Integer noOfMinutes);
  
  @Query("SELECT COUNT(pid) from BisFileLog Logs  where (Logs.mflFileStatus = 'INPROGRESS' " 
      + "and Logs.bisProcessState = 'DATA_INPROGRESS') and "
      + "(TIMEDIFF(NOW(), Logs.checkpointDate)/60)> :minutesForLongProc  ")
  Integer countOfLongRunningTransfers(@Param("minutesForLongProc") Integer minutesForLongProc);
  
  @Query("SELECT COUNT(jobId) from BisJobEntity job  where job.jobStatus = 'INPROGRESS' " 
      + "and ( (TIMEDIFF(NOW(), job.updatedDate)/60)> :minutesForLongJob )")
  Integer countOfLongRunningJobs(@Param("minutesForLongJob") Integer minutesForLongJob);
  
  @Modifying
  @Query("SELECT  Logs from BisFileLog Logs  where (Logs.mflFileStatus = 'INPROGRESS' " 
      + "and Logs.bisProcessState = 'DATA_INPROGRESS') and "
      + "(TIMEDIFF(NOW(), Logs.checkpointDate)/60)> :minutesForLongProc  ")
  List<BisFileLog> selectLongRunningTranfers(@Param("minutesForLongProc") 
      Integer minutesForLongProc);
  
  @Modifying(clearAutomatically = true)
  @Query("UPDATE BisJobEntity job SET job.jobStatus = :jobStatus, "
      + "job.updatedDate = NOW() WHERE  job.jobStatus = 'INPROGRESS' and "
      + "(TIMEDIFF(NOW(), job.updatedDate)/60)> :minutesForLongJob")
  Integer updateBisJob(@Param("jobStatus") String jobStatus, 
      @Param("minutesForLongJob") Integer minutesForLongJob);
  
  @Query("SELECT COUNT(pid) from BisFileLog Logs where ( Logs.source = 'REGULAR' "
        + "and Logs.mflFileStatus = 'INPROGRESS' and Logs.bisProcessState = "
        + "'DATA_INPROGRESS' and Logs.routeSysId = :routeId )")
        Integer countOfInProgress(@Param("routeId") Long routeId);
  
  @Query("SELECT COUNT(pid) from BisFileLog Logs where ( "
      + " (Logs.mflFileStatus = 'SUCCESS' and Logs.bisProcessState = "
      + "'DATA_RECEIVED') or (Logs.mflFileStatus = 'FAILED' and Logs.bisProcessState = "
      + "'DUPLICATE')) and (Logs.job.jobId = :jobId )")
      Long getSuccessCntForJob(@Param("jobId") Long jobId);
  
  @Query("SELECT Logs FROM BisFileLog Logs  JOIN Logs.job BisJobEntity  "
      + "WHERE Logs.mflFileStatus = 'OPEN' and Logs.job.jobStatus = 'INPROGRESS'")
  List<BisFileLog> findFirstOpenLog();
  
  
  Long countByMflFileStatusAndBisProcessStateAndJob_JobId(
      String name, String processState, Long jobId);
  
  Long countByJob_JobId(Long jobId);


}
