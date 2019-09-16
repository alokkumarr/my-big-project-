package com.synchronoss.saw.alert.service.evaluator;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.synchronoss.saw.alert.modal.AlertResult;
import com.synchronoss.saw.alert.modal.AlertRuleDetails;
import com.synchronoss.saw.alert.modal.AlertState;
import com.synchronoss.saw.alert.service.AlertNotifier;
import com.synchronoss.saw.model.SIPDSL;
import com.synchronoss.saw.model.SipQuery;
import com.synchronoss.sip.utils.RestUtil;
import java.util.List;
import java.util.UUID;
import javax.annotation.PostConstruct;
import javax.validation.constraints.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import sncr.bda.base.MaprConnection;

@Service
public class AlertEvaluationImpl implements AlertEvaluation {

  private static final Logger logger = LoggerFactory.getLogger(AlertEvaluationImpl.class);
  @Autowired AlertNotifier alertNotifier;
  private RestTemplate restTemplate;
  @Autowired private RestUtil restUtil;

  @Value("${metastore.base}")
  @NotNull
  private String basePath;

  @Value("${metastore.alertRulesTable}")
  @NotNull
  private String alertRulesMetadata;

  @Value("${sip-analysis-proxy-url}")
  private String proxyAnalysisUrl;

  @Value("${metastore.alertResults}")
  @NotNull
  private String alertResults;

  @Value("${max-number-of-alert-per-rules}")
  private String maxNumberOfAlertPerRules;

  /**
   * Init method for listener.
   *
   * @throws Exception if unbale to create the stream.
   */
  @PostConstruct
  public void init() throws Exception {
    restTemplate = restUtil.restTemplate();
  }

  @Override
  public Boolean evaluateAlert(String dataPodId, Long requestTime) {
    MaprConnection connection = new MaprConnection(basePath, alertResults);
    System.out.println("Evaluating the Alert");
    List<AlertRuleDetails> alertRuleDetailsList = fetchAlertDetailsByDataPod(dataPodId);
    for (AlertRuleDetails alertRuleDetails : alertRuleDetailsList) {
      AlertResult alertResult = new AlertResult();
      alertResult.setAlertRuleName(alertRuleDetails.getAlertRuleName());
      alertResult.setAlertRuleDescription(alertRuleDetails.getAlertRuleDescription());
      alertResult.setAlertSeverity(alertRuleDetails.getAlertSeverity());
      alertResult.setAlertRulesSysId(alertRuleDetails.getAlertRulesSysId());
      alertResult.setAlertState(AlertState.ALARM);
      alertResult.setThresholdValue(alertRuleDetails.getThresholdValue());
      alertResult.setCategoryId(alertRuleDetails.getCategoryId());
      alertResult.setStartTime(requestTime);
      String alertResultId = UUID.randomUUID().toString();
      alertResult.setAlertTriggerSysId(alertResultId);
      alertResult.setCustomerCode(alertRuleDetails.getCustomerCode());
      alertResult.setSipQuery(alertRuleDetails.getSipQuery());
      List<?> alertResultList = evaluateAlertRules(alertRuleDetails.getSipQuery());
      if (alertResultList.size() > 0) {
        alertResult.setAlertCount(alertResultList.size());
        connection.insert(alertResultId, alertResult);
        logger.info("Send Notification for Alert: " + alertRuleDetails.getAlertRuleName());
        alertNotifier.sendNotification(alertRuleDetails);
      }
    }
    return true;
  }

  /**
   * Evaluate the alert Rules.
   *
   * @param sipQuery sipQuery
   * @return List
   */
  private List<?> evaluateAlertRules(SipQuery sipQuery) {
    SIPDSL sipdsl = new SIPDSL();
    sipdsl.setType("alert");
    sipdsl.setSipQuery(sipQuery);
    String url = proxyAnalysisUrl + "/fetch?size=" + maxNumberOfAlertPerRules;
    logger.info("Execute URL for dispatch :" + url);
    HttpEntity<?> requestEntity = new HttpEntity<>(sipdsl);
    return restTemplate.postForObject(url, requestEntity, List.class);
  }

  private List<AlertRuleDetails> fetchAlertDetailsByDataPod(String dataPodId) {
    MaprConnection connection = new MaprConnection(basePath, alertRulesMetadata);
    ObjectMapper objectMapper = new ObjectMapper();
    ObjectNode node = objectMapper.createObjectNode();
    ObjectNode objectNode = node.putObject(MaprConnection.EQ);
    objectNode.put("datapodId", dataPodId);
    List<AlertRuleDetails> alertRuleDetails =
        connection.runMaprDbQueryWithFilter(
            node.toString(), null, null, null, AlertRuleDetails.class);
    return alertRuleDetails;
  }
}
