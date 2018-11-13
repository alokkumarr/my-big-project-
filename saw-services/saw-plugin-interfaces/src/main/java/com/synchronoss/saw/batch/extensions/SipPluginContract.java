package com.synchronoss.saw.batch.extensions;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;

import com.fasterxml.jackson.annotation.ObjectIdGenerators.UUIDGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.synchronoss.saw.batch.exceptions.SipNestedRuntimeException;
import com.synchronoss.saw.batch.model.BisConnectionTestPayload;
import com.synchronoss.saw.batch.model.BisDataMetaInfo;
import com.synchronoss.saw.batch.model.BisIngestionPayload;
import com.synchronoss.saw.batch.utils.IntegrationUtils;
import com.synchronoss.saw.logs.entities.BisFileLog;
import com.synchronoss.saw.logs.repository.BisFileLogsRepository;

/**
 * This class defines the specification for plug in implementation.
 * @author spau0004
 */
public abstract class SipPluginContract 
{
	@Autowired
	private BisFileLogsRepository bisFileLogsRepository;
	
	private static final Logger logger = LoggerFactory.getLogger(SipPluginContract.class);
	
	/**
	 * This method is to test connect the route
	 * @param entityId
	 * @return
	 * @throws SipNestedRuntimeException
	 */
	public abstract HttpStatus connectRoute(Long entityId) throws SipNestedRuntimeException; 
	/**
	 * 	 * This method is to test connect the source
	 * @param entityId
	 * @return
	 * @throws SipNestedRuntimeException
	 */
	public abstract HttpStatus connectChannel(Long entityId) throws SipNestedRuntimeException;
	/**
	 * This method is to test connect the route
	 * @param entityId
	 * @return
	 * @throws SipNestedRuntimeException
	 */
	public abstract HttpStatus immediateConnectRoute(BisConnectionTestPayload payload) throws SipNestedRuntimeException; 
	/**
	 * 	 * This method is to test connect the source
	 * @param entityId
	 * @return
	 * @throws SipNestedRuntimeException
	 */
	public abstract HttpStatus immediateConnectChannel(BisConnectionTestPayload payload) throws SipNestedRuntimeException;

	/**
	 * 	 * This method is to test connect the source
	 * @param entityId
	 * @return
	 * @throws SipNestedRuntimeException
	 */
	public List<BisDataMetaInfo> immediateTransfer(BisConnectionTestPayload payload) throws SipNestedRuntimeException 
	{
		logger.info("It has been left empty intentionally because it will be overriden on the respective plugin module if required");
		return new ArrayList<>();
	}
	/**
	 * 	 * This method is to test connect the source
	 * @param entityId
	 * @return
	 * @throws SipNestedRuntimeException
	 */
	public List<BisDataMetaInfo> transferData(Long channelId, Long routeId) throws SipNestedRuntimeException 
	{
		logger.info("It has been left empty intentionally because it will be overriden on the respective plugin module if required");
		return new ArrayList<>();
	}
	
	/**
	 * This method are the requires to complete the transfer.
	 * @param entityId
	 * @param logging
	 * @param dataDetails
	 * @return Object
	 * @throws Exception 
	 */
	protected HttpStatus pullContent(BisIngestionPayload input) throws Exception 
	{
		HttpStatus status = HttpStatus.OK;
			if (input.getLog().booleanValue()) {
				//if (!checkDuplicateFile(""))
				//	logData(input);
			} else {
				logger.info("logging the trace for the entity " + input.getEntityId() + " in application log");
				ObjectMapper objectMapper = new ObjectMapper();
			    objectMapper.configure(JsonParser.Feature.ALLOW_UNQUOTED_FIELD_NAMES, true);
			    objectMapper.enable(DeserializationFeature.FAIL_ON_READING_DUP_TREE_KEY);
			    logger.info("Entity Id :" + input.getEntityId());
			    logger.info("Content transferring from the channel " + objectMapper.writeValueAsString(input));
			}
		return status;
	}
	
   /**
    * This method is an implementation of logging into database for incoming data
    * @param entity
    * @return Object
    * @throws SipNestedRuntimeException
    */
	protected void logData(BisDataMetaInfo entity) throws SipNestedRuntimeException{ 
		logger.trace("Integrate with logging API to log with a status start here : " + entity.getProcessState());
		BisFileLog bisLog = new BisFileLog();
		bisLog.setPid(new UUIDGenerator()
                .generateId(entity).toString());
		bisLog.setBisChannelSysId(Long.valueOf(entity.getChannelId()));
		bisLog.setRouteSysId(Long.valueOf(entity.getRouteId()));
		bisLog.setFilePattern(entity.getPattern());
		bisLog.setFileName(entity.getActualDataName());
		bisLog.setRecdFileSize(entity.getDataSizeInBytes());
		bisLog.setMfldProcessDate(new SimpleDateFormat(IntegrationUtils.getRenameDateFormat()).format(new Date()));
		bisLog.setRecdFileName(entity.getReceivedDataName());
		bisLog.setBisChannelType(entity.getChannelType().value());
		bisLog.setMflFileStatus(entity.getProcessState());
		try {
			bisLog.setActualFileRecDate(new SimpleDateFormat(IntegrationUtils.getRenameDateFormat()).parse(
					new SimpleDateFormat(IntegrationUtils.getRenameDateFormat()).format(entity.getActualReceiveDate().toString())));
		} catch (ParseException e) {
			logger.error("Exception occured while parsing the date in logdata", e);
			throw new SipNestedRuntimeException("Exception occured while parsing the date in logdata", e);
		}
		bisFileLogsRepository.save(bisLog);
		logger.trace("Integrate with logging API to log with a status ends here : " + entity.getProcessState() + " with an process Id "+ bisLog.getPid());
		}

	   /**
	    * This method is an implementation of logging into database for incoming data
	    * @param entity
	    * @return Object
	    * @throws SipNestedRuntimeException
	    */
		protected void logDataUpsert(BisDataMetaInfo entity, String pid) throws SipNestedRuntimeException{ 
			logger.trace("Integrate with logging API to update with a status start here : " + entity.getProcessState());
			BisFileLog bisLog = new BisFileLog();
			bisLog.setPid(pid);
			bisLog.setBisChannelSysId(Long.valueOf(entity.getChannelId()));
			bisLog.setRouteSysId(Long.valueOf(entity.getRouteId()));
			bisLog.setFilePattern(entity.getPattern());
			bisLog.setFileName(entity.getActualDataName());
			bisLog.setRecdFileSize(entity.getDataSizeInBytes());
			bisLog.setMfldProcessDate(new SimpleDateFormat(IntegrationUtils.getRenameDateFormat()).format(new Date()));
			bisLog.setRecdFileName(entity.getReceivedDataName());
			bisLog.setBisChannelType(entity.getChannelType().value());
			bisLog.setMflFileStatus(entity.getProcessState());
			//try {
				//bisLog.setActualFileRecDate(new SimpleDateFormat(IntegrationUtils.getRenameDateFormat()).parse(
				//		new SimpleDateFormat(IntegrationUtils.getRenameDateFormat()).format(entity.getActualReceiveDate().toString())));
				bisLog.setActualFileRecDate(entity.getActualReceiveDate());
			//} catch (ParseException e) {
			//	logger.error("Exception occured while parsing the date in logdata", e);
			//	throw new SipNestedRuntimeException("Exception occured while parsing the date in logdata", e);
			//}
			if (bisFileLogsRepository.existsById(pid)) {
				bisFileLogsRepository.deleteById(pid);
				bisFileLogsRepository.save(bisLog);
			}else {
			bisFileLogsRepository.save(bisLog);}
			logger.trace("Integrate with logging API to update with a status ends here : " + entity.getProcessState() + " with an process Id "+ bisLog.getPid());
			}
	
	/**
	 * This method is to check the duplicate file against the database logs.
	 * @param fileName
	 * @return boolean
	 * @throws SipNestedRuntimeException
	 */
	protected boolean checkDuplicateFile (String fileName) throws SipNestedRuntimeException{ 
		logger.trace("Integrate with logging API & checking for the duplicate files : " + fileName);
		return bisFileLogsRepository.isFileNameExists(fileName);};
		
	/**
	 * This method gives the batch id
	 * @return String
	 */
	protected String getBatchId() {	
		DateFormat dtFormat = new SimpleDateFormat("MMddyyyyhhmmss");
		return dtFormat.format(new Date());
	}
	
}
