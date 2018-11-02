package com.synchronoss.saw.extensions;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.synchronoss.saw.exceptions.SipNestedRuntimeException;
import com.synchronoss.saw.model.BisIngestionPayload;

/**
 * This class defines the specification for plug in implementation.
 * @author spau0004
 */
public abstract class SipPluginContract 
{
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
				if (!checkDuplicateFile(input))
					logData(input);
			} else {
				logger.info("logging the trace for the entity " + input.getEntityId() + " in application log");
				ObjectMapper objectMapper = new ObjectMapper();
			    objectMapper.configure(JsonParser.Feature.ALLOW_UNQUOTED_FIELD_NAMES, true);
			    objectMapper.enable(DeserializationFeature.FAIL_ON_READING_DUP_TREE_KEY);
			    logger.info("Entity Id :" + input.getEntityId());
			    logger.info("Content transferring from the channel " + objectMapper.writeValueAsString(input));
			    logger.info("transferData " + objectMapper.writeValueAsString(transferData(input)));
			}
		return status;
	}
	
   /**
    * This method is an implementation of logging into database for incoming data
    * @param entity
    * @return Object
    * @throws SipNestedRuntimeException
    */
	private Object logData(Object entity) throws SipNestedRuntimeException{ 
		// Integrate with logging API
		logger.info("Integrate with API is pending");
		return "logdata";
		}

	/**
	 * This method is implementation of transfer data by channel type
	 * @param Id
	 * @return Object
	 * @throws SipNestedRuntimeException
	 */
	public abstract HttpStatus transferData(BisIngestionPayload input) throws Exception;

	/**
	 * This method is to check the duplicate file against the database logs.
	 * @param fileName
	 * @return boolean
	 * @throws SipNestedRuntimeException
	 */
	private boolean checkDuplicateFile (Object fileName) throws SipNestedRuntimeException{ 
		// Integrate with logging API
		logger.info("Integrate with API is pending");
		return true;};
		
	/**
	 * This method gives the batch id
	 * @return String
	 */
	protected String getBatchId() {	
		DateFormat dtFormat = new SimpleDateFormat("MMddyyyyHHmmss");
		Date currentDate = Calendar.getInstance().getTime();        
		return dtFormat.format(currentDate);
	}
	
}
