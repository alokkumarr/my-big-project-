package com.synchronoss.saw.storage.proxy.controller;

import static com.synchronoss.saw.es.QueryBuilderUtil.checkDSKApplicableAnalysis;
import static com.synchronoss.saw.storage.proxy.service.StorageProxyUtil.getArtsNames;
import static com.synchronoss.saw.storage.proxy.service.StorageProxyUtil.getDsks;
import static com.synchronoss.saw.storage.proxy.service.StorageProxyUtil.getSipQuery;
import static com.synchronoss.saw.storage.proxy.service.StorageProxyUtil.getTicket;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.synchronoss.bda.sip.jwt.token.Ticket;
import com.synchronoss.bda.sip.jwt.token.TicketDSKDetails;
import com.synchronoss.saw.model.DataSecurityKey;
import com.synchronoss.saw.model.DataSecurityKeyDef;
import com.synchronoss.saw.model.Field;
import com.synchronoss.saw.model.SipQuery;
import com.synchronoss.saw.model.kpi.KPIBuilder;
import com.synchronoss.saw.storage.proxy.exceptions.JSONMissingSAWException;
import com.synchronoss.saw.storage.proxy.exceptions.JSONProcessingSAWException;
import com.synchronoss.saw.storage.proxy.exceptions.ReadEntitySAWException;
import com.synchronoss.saw.storage.proxy.model.SemanticNode;
import com.synchronoss.saw.storage.proxy.service.StorageProxyService;
import com.synchronoss.saw.storage.proxy.service.StorageProxyUtil;
import com.synchronoss.sip.utils.RestUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiParam;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Api(value = "This controller will provide the KPI execution")
public class SipKpiController {

  private static final Logger logger = LoggerFactory.getLogger(SipKpiController.class);

  @Value("${metadata.service.host}")
  private String metaDataServiceUrl;

  @Autowired private StorageProxyService proxyService;

  @Autowired private RestUtil restUtil;

    private static final String CUSTOMER_CODE = "customerCode";

  public Gson gson = new Gson();

  @RequestMapping(
      value = "/kpi",
      method = RequestMethod.POST,
      produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
  @ResponseStatus(HttpStatus.OK)
  public Object processKpi(
      @ApiParam(value = "Storage object to fetch the data for KPI .", required = true)
          @Valid
          @RequestBody
          KPIBuilder kpiBuilder,
      HttpServletRequest request,
      HttpServletResponse response)
      throws JsonProcessingException {
    if (kpiBuilder == null) {
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
    ArrayList list = (ArrayList) (kpiBuilder.getAdditionalProperties().get("keys"));
    LinkedHashMap hashMap = (LinkedHashMap) (list.get(0));
    String semanticId = hashMap.get("semanticId").toString();
    try {
      logger.trace(
          "Storage Proxy sync request object : {} ", objectMapper.writeValueAsString(kpiBuilder));
      if (kpiBuilder.getAdditionalProperties().get("action").toString().equalsIgnoreCase("fetch")) {
        SemanticNode semanticNode =
            StorageProxyUtil.fetchSemantic(semanticId, metaDataServiceUrl, restUtil);
        return StorageProxyUtil.merge(
            objectMapper.valueToTree(kpiBuilder), objectMapper.valueToTree(semanticNode));
      }
      SipQuery savedQuery = getSipQuery(semanticId, metaDataServiceUrl, request, restUtil);
      DataSecurityKey dataSecurityKeyNode = dataSecurityKey;
      dataSecurityKey = new DataSecurityKey();
      List<DataSecurityKeyDef> dataSecurityKeyDefList =
          dataSecurityKeyNode.getDataSecuritykey() != null
              ? dataSecurityKeyNode.getDataSecuritykey()
              : null;
      String artifactName = savedQuery.getArtifacts().get(0).getArtifactsName();
      List<Field> fields = savedQuery.getArtifacts().get(0).getFields();
      List<DataSecurityKeyDef> dataSecurityKeyDefs = new ArrayList<>();
      for (DataSecurityKeyDef dataSecurityKeyDef : dataSecurityKeyDefList) {
        if (checkDSKApplicableAnalysis(artifactName, fields, dataSecurityKeyDef)) {
          String dskColName = dataSecurityKeyDef.getName();
          dataSecurityKeyDef.setName(dskColName);
          dataSecurityKeyDefs.add(dataSecurityKeyDef);
        }
      }

      dataSecurityKey.setDataSecuritykey(dataSecurityKeyDefs);
        // Customer Code filtering SIP-8381, we can make use of existing DSK to filter based on customer
        // code.
        if (authTicket.getIsJvCustomer() != 1 && authTicket.getFilterByCustomerCode() == 1) {

            DataSecurityKeyDef dataSecurityKeyDef = new DataSecurityKeyDef();
            List<String> artsName = getArtsNames(savedQuery);
            List<DataSecurityKeyDef> customerFilterDsks = new ArrayList<>();
                for (String artifact : artsName) {
                    dataSecurityKeyDef.setName(CUSTOMER_CODE);
                    dataSecurityKeyDef.setValues(Collections.singletonList(authTicket.getCustCode()));
                    customerFilterDsks.add(dataSecurityKeyDef);
                    dataSecurityKey.getDataSecuritykey().addAll(customerFilterDsks);
                }
        }
      logger.debug("Final DataSecurity Object : " + gson.toJson(dataSecurityKey));
      responseObject = proxyService.processKpi(kpiBuilder, dataSecurityKey);
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
