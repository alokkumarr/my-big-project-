package com.synchronoss.saw.batch.extensions;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.synchronoss.saw.batch.exceptions.SipNestedRuntimeException;
import com.synchronoss.saw.batch.model.BisConnectionTestPayload;
import com.synchronoss.saw.batch.model.BisDataMetaInfo;
import com.synchronoss.saw.batch.model.BisIngestionPayload;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;

/**
 * This class defines the specification for plug in implementation.
 * 
 * @author spau0004
 */
public abstract class SipPluginContract {

  private static final Logger logger = LoggerFactory.getLogger(SipPluginContract.class);

  /**
   * This method is to test connect the route.
   */
  public abstract String connectRoute(Long entityId) throws SipNestedRuntimeException;

  /**
   * This method is to test connect the source.
   */
  public abstract String connectChannel(Long entityId) throws SipNestedRuntimeException;

  /**
   * This method is to test connect the route.
   */
  public abstract String immediateConnectRoute(BisConnectionTestPayload payload)
      throws SipNestedRuntimeException, IOException;

  /**
   * This method is to test connect the source.
   */
  public abstract String immediateConnectChannel(BisConnectionTestPayload payload)
      throws SipNestedRuntimeException;

  /**
   * This method is to test connect the source.
   */
  public List<BisDataMetaInfo> immediateTransfer(BisConnectionTestPayload payload)
      throws SipNestedRuntimeException {
    logger.info("It has been left empty intentionally because "
        + "it will be overriden on the respective plugin module if required");
    return new ArrayList<>();
  }

  /**
   * * This method is to test connect the source.
   */
  public List<BisDataMetaInfo> transferData(Long channelId, Long routeId)
      throws SipNestedRuntimeException {
    logger.info("It has been left empty intentionally because it "
        + "will be overriden on the respective plugin module if required");
    return new ArrayList<>();
  }

  /**
   * This method are the requires to complete the transfer.
   */
  protected HttpStatus pullContent(BisIngestionPayload input) throws Exception {
    HttpStatus status = HttpStatus.OK;
    if (input.getLog().booleanValue()) {
      // if (!checkDuplicateFile(""))
      // logData(input);
    } else {
      logger
          .info("logging the trace for the entity " + input.getEntityId() + " in application log");
      ObjectMapper objectMapper = new ObjectMapper();
      objectMapper.configure(JsonParser.Feature.ALLOW_UNQUOTED_FIELD_NAMES, true);
      objectMapper.enable(DeserializationFeature.FAIL_ON_READING_DUP_TREE_KEY);
      logger.info("Entity Id :" + input.getEntityId());
      logger
          .info("Content transferring from the channel " + objectMapper.writeValueAsString(input));
    }
    return status;
  }

  /**
   * This method gives the batch id.
   */
  protected String getBatchId() {
    DateFormat dtFormat = new SimpleDateFormat("MMddyyyyhhmmss");
    return dtFormat.format(new Date());
  }


}
