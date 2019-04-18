package com.synchronoss.saw.batch.ingestion.sftp.connection;

import java.io.File;
import java.time.Duration;
import java.time.ZonedDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.context.request.async.DeferredResult;

import com.synchronoss.saw.batch.AsyncConfiguration;
import com.synchronoss.saw.batch.exception.SftpProcessorException;
import com.synchronoss.saw.batch.extensions.SipPluginContract;
import com.synchronoss.saw.batch.model.BisConnectionTestPayload;
import com.synchronoss.saw.batch.model.BisDataMetaInfo;
import com.synchronoss.saw.batch.connections.SIPTransferService;
import com.synchronoss.saw.logs.constants.SourceType;

import io.swagger.annotations.ApiParam;

@Service
public class SFTPTransferService implements SIPTransferService{
  
  private static final Logger logger = LoggerFactory.getLogger(SFTPTransferService.class);
  
  @Autowired
  @Qualifier("sftpService")
  private SipPluginContract sftpServiceImpl;

  @Autowired
  @Qualifier(AsyncConfiguration.TASK_EXECUTOR_CONTROLLER)
  private Executor transactionPostExecutor;
  
  @Override
  public List<BisDataMetaInfo> immediateTransfer(BisConnectionTestPayload requestBody) {
	    List<BisDataMetaInfo> response = null;
	    logger.info("Executor::: " + this.transactionPostExecutor);
	    try {
	      if (requestBody.getBatchSize() > 0) {
	        sftpServiceImpl.setBatchSize(requestBody.getBatchSize());
	      }
	      if (Long.valueOf(requestBody.getChannelId()) > 0L
	          && Long.valueOf(requestBody.getRouteId()) > 0L) {
	        response = sftpServiceImpl.transferData(Long.valueOf(requestBody.getChannelId()),
	            Long.valueOf(requestBody.getRouteId()), null, false, SourceType.REGULAR.name());
	      } else {
	        response = sftpServiceImpl.immediateTransfer(requestBody);
	      }
	      for (BisDataMetaInfo info : response) {
	        if (info.getDestinationPath() != null) {
	          File folderWhereFilesDumped = new File(info.getDestinationPath());
	          if (folderWhereFilesDumped.exists() && folderWhereFilesDumped.isDirectory()) {
	            logger.trace("Thread with Name :" + Thread.currentThread().getName()
	                + "has completed & created folder to put the files in "
	                + info.getDestinationPath());
	            File[] listOfFiles = folderWhereFilesDumped.listFiles();
	            if (listOfFiles != null && listOfFiles.length == 0) {
	              folderWhereFilesDumped.delete();
	              logger.trace("Thread with Name :" + Thread.currentThread().getName()
	                  + "has completed & created folder to put the files in "
	                  + info.getDestinationPath()
	                  + " & it is empty so it has been deleted after completion of the thread.");
	            }
	          }
	        }
	      }
	    } catch (Exception e) {
	      throw new SftpProcessorException("Exception occured while transferring the file", e);
	    }
	    return response;
	    }

  @Override
  public DeferredResult<ResponseEntity<List<BisDataMetaInfo>>> cronTransferasync(BisConnectionTestPayload requestBody,
      BindingResult result) {
	    logger.info("Executor::: " + this.transactionPostExecutor);
	    ZonedDateTime startTime = ZonedDateTime.now();
	    logger.trace("Servlet Thread Started -{}", startTime);
	    if (result.hasErrors()) {
	      throw new SftpProcessorException("Exception occured while transferring the file");
	    }
	    if (requestBody.getBatchSize() > 0) {
	      sftpServiceImpl.setBatchSize(requestBody.getBatchSize());
	    }
	    DeferredResult<ResponseEntity<List<BisDataMetaInfo>>> deferredResult = new DeferredResult<>();
	    if (Long.valueOf(requestBody.getChannelId()) > 0L
	        && Long.valueOf(requestBody.getRouteId()) > 0L) {
	      CompletableFuture
	          .supplyAsync(() -> sftpServiceImpl.transferData(Long.valueOf(requestBody.getChannelId()),
	              Long.valueOf(requestBody.getRouteId()), null, false,
	              SourceType.REGULAR.name()), transactionPostExecutor)
	          .whenComplete((p, throwable) -> {
	            logger.trace("Current Thread Name :{}", Thread.currentThread().getName());
	            if (throwable != null) {
	              logger.error("Exception occured while completion of the thread "
	                  + Thread.currentThread().getName(), throwable);
	            }
	            logger.trace("Current Thread Name :{}", Thread.currentThread().getName());
	            for (BisDataMetaInfo info : p) {
	              if (info.getDestinationPath() != null) {
	                File folderWhereFilesDumped = new File(info.getDestinationPath());
	                if (folderWhereFilesDumped.exists() && folderWhereFilesDumped.isDirectory()) {
	                  logger.trace("Thread with Name :" + Thread.currentThread().getName()
	                      + "has completed & created folder to put the files in "
	                      + info.getDestinationPath());
	                  File[] listOfFiles = folderWhereFilesDumped.listFiles();
	                  if (listOfFiles != null && listOfFiles.length == 0) {
	                    folderWhereFilesDumped.delete();
	                    logger.trace("Thread with Name :" + Thread.currentThread().getName()
	                        + "has completed & created folder to put the files in "
	                        + info.getDestinationPath()
	                        + " & it is empty so it has been deleted after completion of the thread.");
	                  }
	                }
	              }
	            }
	            deferredResult.setResult(ResponseEntity.ok(p));
	          });
	    } else {
	      CompletableFuture.supplyAsync(() -> sftpServiceImpl.immediateTransfer(requestBody),
	          transactionPostExecutor).whenComplete((p, throwable) -> {
	            logger.trace("Current Thread Name :{}", Thread.currentThread().getName());
	            if (throwable != null) {
	              logger.error("Exception occured while completion of the thread "
	                  + Thread.currentThread().getName(), throwable);
	            }
	            for (BisDataMetaInfo info : p) {
	              if (info.getDestinationPath() != null) {
	                File folderWhereFilesDumped = new File(info.getDestinationPath());
	                if (folderWhereFilesDumped.exists() && folderWhereFilesDumped.isDirectory()) {
	                  logger.trace("Thread with Name :" + Thread.currentThread().getName()
	                      + "has completed & created folder to put the files in "
	                      + info.getDestinationPath());
	                  File[] listOfFiles = folderWhereFilesDumped.listFiles();
	                  if (listOfFiles != null && listOfFiles.length == 0) {
	                    folderWhereFilesDumped.delete();
	                    logger.trace("Thread with Name :" + Thread.currentThread().getName()
	                        + "has completed & created folder to put the files in "
	                        + info.getDestinationPath()
	                        + " & it is empty so it has been deleted after completion of the thread.");
	                  }
	                }
	              }
	            }
	            deferredResult.setResult(ResponseEntity.ok(p));
	          });
	    }
	    ZonedDateTime endTime = ZonedDateTime.now();
	    long durationInMillis = Duration.between(startTime, endTime).toMillis();
	    logger.trace("Servlet thread released-{}", endTime);
	    logger.info("Time taken for the POST Transaction IO to complete(in millis) - {}",
	        durationInMillis);

	    logger.trace("Number of Active Threads :{}", Thread.activeCount());
	    if (durationInMillis > 10000L) {
	      logger.info("IO for POST was Blocked for thatn 10 Second-{} ", durationInMillis);
	    }

	    return deferredResult;
	    }
  
  public Map<String, Boolean> isDestinationExist(@ApiParam(
      value = "Payload structure which to be used to initiate the transfer",
      required = true) @Valid @RequestBody(required = true) BisConnectionTestPayload requestBody) {
    logger.trace("Checking for data path: " + requestBody.getDestinationLocation());
    boolean result = false;
    try {
      result = sftpServiceImpl.isDataExists(requestBody.getDestinationLocation());
    } catch (Exception e) {
      logger.trace("Exception occurred while checking the data location" + e);
      throw new SftpProcessorException("Exception occurred while checking the data location", e);
    }
    Map<String, Boolean> responseMap = new HashMap<String, Boolean>();
    responseMap.put("status", result);
    return responseMap;
  }

}
