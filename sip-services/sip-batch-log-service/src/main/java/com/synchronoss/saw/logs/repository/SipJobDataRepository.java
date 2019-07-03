package com.synchronoss.saw.logs.repository;

import com.synchronoss.saw.logs.entities.BisJobEntity;
import com.synchronoss.saw.logs.models.JobDetails;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface SipJobDataRepository extends JpaRepository<BisJobEntity, Long> {

  @Query(
      value =
          "SELECT Bj.JOB_ID AS jobId, Bj.JOB_NAME AS jobName,"
              + " Bj.START_TIME AS startTime, Bj.END_TIME AS endTime,"
              + " Bj.JOB_STATUS AS jobStatus, Bj.TOTAL_FILES_COUNT AS totalCount,"
              + " Bj.SUCCESS_FILES_COUNT AS successCount, Bj.FILE_PATTERN AS filePattern,"
              + " Bj.CHANNEL_TYPE AS sourceType, Bj.CREATED_DATE AS createdDate,"
              + " Bj.CREATED_BY AS createdBy, Bj.UPDATED_DATE AS updatedDate,"
              + " Bj.UPDATED_BY AS updatedBy, (Bj.END_TIME - Bj.START_TIME) AS duration,"
              + " JSON_UNQUOTE(JSON_EXTRACT(Ch.CHANNEL_METADATA,'$.channelName')) AS channelName,"
              + " JSON_UNQUOTE(JSON_EXTRACT(Br.ROUTE_METADATA,'$.routeName')) AS routeName"
              + " from BIS_JOB Bj"
              + " join BIS_CHANNEL Ch"
              + " ON Bj.BIS_CHANNEL_SYS_ID = Ch.BIS_CHANNEL_SYS_ID"
              + " Join bis_route Br"
              + " ON Bj.BIS_ROUTE_SYS_ID = Br.BIS_ROUTE_SYS_ID"
              + " WHERE Bj.BIS_CHANNEL_SYS_ID = :channelSysId AND"
              + " Bj.BIS_ROUTE_SYS_ID = :routeSysId"
              + " ORDER BY :column :sort ",
      countQuery =
          "SELECT count(*)"
              + " from BIS_JOB Bj"
              + " join BIS_CHANNEL Ch"
              + " ON Bj.BIS_CHANNEL_SYS_ID = Ch.BIS_CHANNEL_SYS_ID"
              + " Join bis_route Br"
              + " ON Bj.BIS_ROUTE_SYS_ID = Br.BIS_ROUTE_SYS_ID"
              + " WHERE Bj.BIS_CHANNEL_SYS_ID = :channelSysId"
              + " AND Bj.BIS_ROUTE_SYS_ID = :routeSysId",
      nativeQuery = true)
  Page<JobDetails> findByBisChannelSysIdAndBisRouteSysId(
      @Param("channelSysId") Long channelSysId,
      @Param("routeSysId") Long routeSysId,
      Pageable pageable,
      @Param("sort") String sort,
      @Param("column") String column);

  @Query(
      value =
          "SELECT Bj.JOB_ID AS jobId, Bj.JOB_NAME AS jobName,"
              + " Bj.START_TIME AS startTime, Bj.END_TIME AS endTime,"
              + " Bj.JOB_STATUS AS jobStatus, Bj.TOTAL_FILES_COUNT AS totalCount,"
              + " Bj.SUCCESS_FILES_COUNT AS successCount, Bj.FILE_PATTERN AS filePattern,"
              + " Bj.CHANNEL_TYPE AS sourceType, Bj.CREATED_DATE AS createdDate,"
              + " Bj.CREATED_BY AS createdBy, Bj.UPDATED_DATE AS updatedDate,"
              + " Bj.UPDATED_BY AS updatedBy, (Bj.END_TIME - Bj.START_TIME) AS duration,"
              + " JSON_UNQUOTE(JSON_EXTRACT(Ch.CHANNEL_METADATA,'$.channelName')) AS channelName,"
              + " JSON_UNQUOTE(JSON_EXTRACT(Br.ROUTE_METADATA,'$.routeName')) AS routeName"
              + " from BIS_JOB Bj"
              + " join BIS_CHANNEL Ch"
              + " ON Bj.BIS_CHANNEL_SYS_ID = Ch.BIS_CHANNEL_SYS_ID"
              + " Join bis_route Br"
              + " ON Bj.BIS_ROUTE_SYS_ID = Br.BIS_ROUTE_SYS_ID"
              + " WHERE Bj.BIS_CHANNEL_SYS_ID = :channelId"
              + " ORDER BY :column :sort ",
      countQuery =
          "SELECT count(*)"
              + " from BIS_JOB Bj"
              + " join BIS_CHANNEL Ch"
              + " ON Bj.BIS_CHANNEL_SYS_ID = Ch.BIS_CHANNEL_SYS_ID"
              + " Join bis_route Br"
              + " ON Bj.BIS_ROUTE_SYS_ID = Br.BIS_ROUTE_SYS_ID"
              + " WHERE Bj.BIS_CHANNEL_SYS_ID = :channelId",
      nativeQuery = true)
  Page<JobDetails> findByBisChannelSysId(
      @Param("channelId") Long channelId,
      Pageable pageable,
      @Param("sort") String sort,
      @Param("column") String column);

  @Query(
      value =
          "SELECT Bj.JOB_ID AS jobId, Bj.JOB_NAME AS jobName,"
              + " Bj.START_TIME AS startTime, Bj.END_TIME AS endTime,"
              + " Bj.JOB_STATUS AS jobStatus, Bj.TOTAL_FILES_COUNT AS totalCount,"
              + " Bj.SUCCESS_FILES_COUNT AS successCount, Bj.FILE_PATTERN AS filePattern,"
              + " Bj.CHANNEL_TYPE AS sourceType, Bj.CREATED_DATE AS createdDate,"
              + " Bj.CREATED_BY AS createdBy, Bj.UPDATED_DATE AS updatedDate,"
              + " Bj.UPDATED_BY AS updatedBy, (Bj.END_TIME - Bj.START_TIME) AS duration,"
              + " JSON_UNQUOTE(JSON_EXTRACT(Ch.CHANNEL_METADATA,'$.channelName')) AS channelName,"
              + " JSON_UNQUOTE(JSON_EXTRACT(Br.ROUTE_METADATA,'$.routeName')) AS routeName"
              + " from BIS_JOB Bj"
              + " join BIS_CHANNEL Ch"
              + " ON Bj.BIS_CHANNEL_SYS_ID = Ch.BIS_CHANNEL_SYS_ID"
              + " Join bis_route Br"
              + " ON Bj.BIS_ROUTE_SYS_ID = Br.BIS_ROUTE_SYS_ID"
              + " WHERE Bj.CHANNEL_TYPE = :channelType"
              + " ORDER BY :column :sort ",
      countQuery =
          "SELECT count(*)"
              + " from BIS_JOB Bj"
              + " join BIS_CHANNEL Ch"
              + " ON Bj.BIS_CHANNEL_SYS_ID = Ch.BIS_CHANNEL_SYS_ID"
              + " Join bis_route Br"
              + " ON Bj.BIS_ROUTE_SYS_ID = Br.BIS_ROUTE_SYS_ID"
              + " WHERE Bj.CHANNEL_TYPE = :channelType",
      nativeQuery = true)
  Page<JobDetails> findByChannelType(
      @Param("channelType") String channelType,
      Pageable pageable,
      @Param("sort") String sort,
      @Param("column") String column);
}
