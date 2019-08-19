package com.synchronoss.saw.storage.proxy.controller;

import static com.synchronoss.saw.storage.proxy.service.StorageProxyUtil.getDsks;
import static com.synchronoss.saw.storage.proxy.service.StorageProxyUtil.getTicket;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.synchronoss.bda.sip.jwt.token.Ticket;
import com.synchronoss.bda.sip.jwt.token.TicketDSKDetails;
import com.synchronoss.saw.model.DataSecurityKey;
import com.synchronoss.saw.model.globalfilter.GlobalFilters;
import com.synchronoss.saw.storage.proxy.exceptions.JSONMissingSAWException;
import com.synchronoss.saw.storage.proxy.exceptions.JSONProcessingSAWException;
import com.synchronoss.saw.storage.proxy.exceptions.ReadEntitySAWException;
import com.synchronoss.saw.storage.proxy.service.StorageProxyService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiParam;
import java.io.IOException;
import java.util.Collections;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Api(value = "This controller will provide the global filter execution")
public class SipGlobalFilterController {

  private static final Logger logger = LoggerFactory.getLogger(SipGlobalFilterController.class);

  @Autowired private StorageProxyService proxyService;

  @RequestMapping(
      value = "/filters",
      method = RequestMethod.POST,
      produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
  @ResponseStatus(HttpStatus.OK)
  public Object retrieveGlobalFilter(
      @ApiParam(value = "Storage object to fetch the data for global filters .", required = true)
          @Valid
          @RequestBody
          GlobalFilters globalFilters,
      @RequestParam(name = "size", required = false) Integer size,
      HttpServletRequest request,
      HttpServletResponse response)
      throws JsonProcessingException {
    if (globalFilters == null) {
      throw new JSONMissingSAWException("json body is missing in request body");
    }
    Ticket authTicket = getTicket(request);
    if (authTicket == null) {
      response.setStatus(401);
      logger.error("Invalid authentication token");
      return Collections.singletonList("Invalid authentication token");
    }
    List<TicketDSKDetails> dskList = authTicket.getDataSecurityKey();
    Object responseObject = null;
    ObjectMapper objectMapper = new ObjectMapper();
    objectMapper.configure(JsonParser.Feature.ALLOW_UNQUOTED_FIELD_NAMES, true);
    objectMapper.enable(DeserializationFeature.FAIL_ON_READING_DUP_TREE_KEY);
    DataSecurityKey dataSecurityKey = new DataSecurityKey();
    dataSecurityKey.setDataSecuritykey(getDsks(dskList));
    try {
      logger.trace(
          "Storage Proxy sync request object : {} ",
          objectMapper.writeValueAsString(globalFilters));
      responseObject = proxyService.fetchGlobalFilter(globalFilters, dataSecurityKey);
    } catch (IOException e) {
      logger.error("expected missing on the request body.", e);
      throw new JSONProcessingSAWException("expected missing on the request body");
    } catch (ReadEntitySAWException ex) {
      logger.error("Problem on the storage while reading data from storage.", ex);
      throw new ReadEntitySAWException("Problem on the storage while reading data from storage");
    } catch (Exception e) {
      logger.error("Exception generated while processing incoming json.", e);
      throw new RuntimeException("Exception generated while processing incoming json.");
    }
    logger.trace("response data {}", objectMapper.writeValueAsString(responseObject));
    return responseObject;
  }
}
