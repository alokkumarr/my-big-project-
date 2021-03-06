package com.synchronoss.sip.alert.service.evaluator;

import static com.synchronoss.sip.alert.util.AlertUtils.getEpochFromDateTime;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.synchronoss.saw.model.Filter;
import com.synchronoss.saw.model.Model;
import com.synchronoss.saw.model.SIPDSL;
import com.synchronoss.saw.model.SipQuery;
import com.synchronoss.saw.util.BuilderUtil;
import com.synchronoss.saw.util.DynamicConvertor;
import com.synchronoss.sip.alert.modal.AlertResult;
import com.synchronoss.sip.alert.modal.AlertRuleDetails;
import com.synchronoss.sip.alert.modal.AlertState;
import com.synchronoss.sip.alert.modal.MonitoringType;
import com.synchronoss.sip.alert.modal.Subscriber;
import com.synchronoss.sip.alert.service.AlertNotifier;
import com.synchronoss.sip.alert.service.AlertService;
import com.synchronoss.sip.alert.util.AlertUtils;
import com.synchronoss.sip.utils.RestUtil;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicReference;
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

  @Autowired AlertService alertService;

  @Value("${sip.service.metastore.base}")
  @NotNull
  private String basePath;

  @Value("${sip.service.metastore.alertRulesTable}")
  @NotNull
  private String alertRulesMetadata;

  @Value("${sip.service.analysis-proxy-url}")
  private String proxyAnalysisUrl;

  @Value("${sip.service.metastore.alertResults}")
  @NotNull
  private String alertResults;

  @Value("${sip.service.max-number-of-alert-per-rules}")
  private String maxNumberOfAlertPerRules;

  /**
   * Init method for listener.
   *
   * @throws Exception if unbale to create the stream.
   */
  @PostConstruct
  public void init() {
    restTemplate = restUtil.restTemplate();
  }

  @Override
  public Boolean evaluateAlert(String dataPodId, Long requestTime) {
    MaprConnection connection = new MaprConnection(basePath, alertResults);
    logger.info("Evaluating the Alert");
    List<AlertRuleDetails> alertRuleDetailsList = fetchAlertDetailsByDataPod(dataPodId);
    for (AlertRuleDetails alertRuleDetails : alertRuleDetailsList) {
      try {
        SipQuery sipQuery = getSipQueryWithCalculatedPresetCal(alertRuleDetails.getSipQuery());
        DynamicConvertor dynamicConverter =
            getDynamicConvertorWithCalculatedPreset(alertRuleDetails.getLookbackPeriod());
        if (alertRuleDetails.getTriggerOnLookback() == null
            || !(alertRuleDetails.getTriggerOnLookback())
            || (alertRuleDetails.getTriggerOnLookback()
                && checkAlertResultBasedOnLastTrigger(
                    alertRuleDetails.getAlertRulesSysId(),
                    getEpochFromDateTime(dynamicConverter.getGte())))) {
          logger.info("Evaluating the alert for rule id" + alertRuleDetails.getAlertRulesSysId());
          List<?> alertResultList = evaluateAlertRules(sipQuery);
          List<Object> executionResultList = new ArrayList<>();
          ObjectMapper mapper = new ObjectMapper();
          AtomicReference<Boolean> alert = new AtomicReference<>(true);
          AtomicReference<Double> metricsValue = new AtomicReference<>();
          if (alertResultList.size() > 0) {
            alertResultList.forEach(
                (executionResult) -> {
                  try {
                    Map<String, Object> result = mapper.convertValue(executionResult, Map.class);
                    Object value = result.get(alertRuleDetails.getMetricsColumn());
                    if (value != null
                        && !value.toString().equalsIgnoreCase("null")
                        && value.toString().length() > 0) {
                      Double metricValue = ((Number) value).doubleValue();
                      if (alertRuleDetails.getMonitoringType() != null
                          && alertRuleDetails
                              .getMonitoringType()
                              .equals(MonitoringType.CONTINUOUS_MONITORING)) {
                        if (!AlertUtils.checkThresholdsForRow(
                            alertRuleDetails.getOperator(),
                            alertRuleDetails.getThresholdValue(),
                            alertRuleDetails.getOtherThresholdValue(),
                            metricValue)) {
                          alert.set(false);
                        }
                      }
                      if (alertRuleDetails.getMonitoringType() != null
                          && alertRuleDetails
                              .getMonitoringType()
                              .equals(MonitoringType.AGGREGATION_METRICS)) {
                        logger.trace("Monitoring type is " + MonitoringType.AGGREGATION_METRICS);
                        alert.set(false);
                        if (AlertUtils.checkThresholdsForRow(
                            alertRuleDetails.getOperator(),
                            alertRuleDetails.getThresholdValue(),
                            alertRuleDetails.getOtherThresholdValue(),
                            metricValue)) {
                          alert.set(true);
                        }
                      }
                      metricsValue.set(metricValue);
                      executionResultList.add(value);
                    }
                  } catch (Exception e) {
                    logger.error("Exception occurred while converting the execution result " + e);
                  }
                });
            int executionSize = executionResultList.size();
            if (executionSize > 0 && alert.get()) {
              AlertResult alertResult = buildAlertResult(alertRuleDetails, AlertState.ALARM);
              alertResult.setStartTime(requestTime);
              alertResult.setSipQuery(sipQuery);
              logger.trace(
                  "Threshold has reached for the alert rule id"
                      + alertRuleDetails.getAlertRulesSysId());
              logger.info("Metric Value " + metricsValue.get());
              alertResult.setMetricValue(metricsValue.get());
              if (alertRuleDetails.getMonitoringType() != null
                  && alertRuleDetails
                      .getMonitoringType()
                      .equals(MonitoringType.CONTINUOUS_MONITORING)) {
                alertResult.setAlertCount(1);
              } else {
                alertResult.setAlertCount(executionSize);
              }
              connection.insert(alertResult.getAlertTriggerSysId(), alertResult);
              logger.info("Sending Notification for Alert: " + alertRuleDetails.getAlertRuleName());
              alertNotifier.sendNotification(alertRuleDetails, alertResult.getAlertTriggerSysId());
            } else {
              updateAlertResultAndSubscriptionStatus(alertRuleDetails);
            }
          } else {
            updateAlertResultAndSubscriptionStatus(alertRuleDetails);
          }
        }
      } catch (Exception ex) {
        logger.error(
            "Exception occured while evaluating the alert rule:{} exception :{}",
            alertRuleDetails.getAlertRulesSysId(),
            ex);
      }
    }
    return true;
  }

  private void updateAlertResultAndSubscriptionStatus(AlertRuleDetails alertRuleDetails) {
    List<AlertResult> alertResultList =
        AlertUtils.getLastAlertResultByAlertRuleId(
            alertRuleDetails.getAlertRulesSysId(), basePath, alertResults);
    if (alertResultList.size() > 0) {
      AlertResult alertResult = alertResultList.get(0);
      if (alertResult.getAlertState() == AlertState.ALARM) {
        MaprConnection connection = new MaprConnection(basePath, alertResults);
        alertResult.setAlertState(AlertState.OK);
        connection.update(alertResult.getAlertTriggerSysId(), alertResult);
        updateSubsriberStatusToActive(alertRuleDetails.getAlertRulesSysId());
        // saveAlertTriggerState();
      }
    }
  }

  private void updateSubsriberStatusToActive(String alertRulesSysId) {

    List<Subscriber> inActiveSubscribers =
        alertService.fetchInactiveSubscriberByAlertId(alertRulesSysId);
    inActiveSubscribers.stream()
        .forEach(
            subscriber -> {
              try {
                subscriber.setActive(Boolean.TRUE);
                alertService.createOrUpdateSubscriber(subscriber);
              } catch (Exception e) {
                logger.error("error occured while update the subscriber status");
              }
            });
  }

  /**
   * This method builds alertresult from the alertruledetails.
   *
   * @param alertRuleDetails AlertRuleDetails
   * @return AlertResult
   */
  private AlertResult buildAlertResult(AlertRuleDetails alertRuleDetails, AlertState alertState) {
    AlertResult alertResult = new AlertResult();
    alertResult.setAlertRuleName(alertRuleDetails.getAlertRuleName());
    alertResult.setCustomerCode(alertRuleDetails.getCustomerCode());
    alertResult.setAlertRuleDescription(alertRuleDetails.getAlertRuleDescription());
    alertResult.setAlertSeverity(alertRuleDetails.getAlertSeverity());
    alertResult.setAlertRulesSysId(alertRuleDetails.getAlertRulesSysId());
    alertResult.setAlertState(alertState);
    alertResult.setThresholdValue(alertRuleDetails.getThresholdValue());
    alertResult.setOtherThresholdValue(alertRuleDetails.getOtherThresholdValue());
    alertResult.setOperator(alertRuleDetails.getOperator());
    alertResult.setCategoryId(alertRuleDetails.getCategoryId());
    alertResult.setAttributeValue(alertRuleDetails.getAttributeValue());
    String alertResultId = UUID.randomUUID().toString();
    alertResult.setAlertTriggerSysId(alertResultId);
    return alertResult;
  }

  /**
   * Method calculates lte and gte based on preset and then saves those values in sipQuery.
   *
   * @param sipQuery sipQuery
   * @return sipQuery
   */
  public SipQuery getSipQueryWithCalculatedPresetCal(SipQuery sipQuery) {
    List<Filter> filters = new ArrayList<>();
    for (Filter eachFilter : sipQuery.getFilters()) {
      if (eachFilter.getModel() != null) {
        Model model = eachFilter.getModel();
        DynamicConvertor dynamicConverter;
        if (model.getPresetCal() != null) {
          dynamicConverter =
              BuilderUtil.getDynamicConvertForPresetCal(eachFilter.getModel().getPresetCal());
          Model model1 = new Model();
          model1.setGte(dynamicConverter.getGte());
          model1.setLte(dynamicConverter.getLte());
          eachFilter.setModel(model1);
          filters.add(eachFilter);
        } else if (model.getPreset() != null) {
          dynamicConverter = BuilderUtil.dynamicDecipher(eachFilter.getModel().getPreset().value());
          Model model1 = new Model();
          model1.setGte(dynamicConverter.getGte());
          model1.setLte(dynamicConverter.getLte());
          eachFilter.setModel(model1);
          filters.add(eachFilter);
        } else {
          filters.add(eachFilter);
        }
      } else {
        filters.add(eachFilter);
      }
    }
    sipQuery.setFilters(filters);
    return sipQuery;
  }

  /**
   * Method calculates lte and gte based on preset and then saves those values in lookBackPeriod.
   *
   * @param lookBackPeriod lookBackPeriod.
   * @return DynamicConvertor Dynamic Convertor
   */
  public DynamicConvertor getDynamicConvertorWithCalculatedPreset(String lookBackPeriod) {
    DynamicConvertor dynamicConverter = null;
    if (lookBackPeriod != null) {
      lookBackPeriod = lookBackPeriod.trim();
      final String hypen = "-";
      if (lookBackPeriod.contains(hypen)) {
        dynamicConverter = BuilderUtil.getDynamicConvertForPresetCal(lookBackPeriod);
      } else {
        dynamicConverter = BuilderUtil.dynamicDecipher(lookBackPeriod);
      }
    }

    return dynamicConverter;
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
    logger.info("Execute URL for alert Evaluation :" + url);
    HttpEntity<?> requestEntity = new HttpEntity<>(sipdsl);
    return restTemplate.postForObject(url, requestEntity, List.class);
  }

  /**
   * Fetches the alertRuledetails based on datapodId.
   *
   * @param dataPodId dataPodId
   * @return list
   */
  public List<AlertRuleDetails> fetchAlertDetailsByDataPod(String dataPodId) {
    ObjectMapper objectMapper = new ObjectMapper();
    ObjectNode node = objectMapper.createObjectNode();
    ArrayNode arrayNode = node.putArray(MaprConnection.AND);
    ObjectNode node1 = objectMapper.createObjectNode();
    ObjectNode objectNode = node1.putObject(MaprConnection.EQ);
    objectNode.put("datapodId", dataPodId);
    ObjectNode node2 = objectMapper.createObjectNode();
    ObjectNode objectNode1 = node2.putObject(MaprConnection.EQ);
    objectNode1.put("activeInd", true);
    arrayNode.add(node1);
    arrayNode.add(node2);
    MaprConnection connection = new MaprConnection(basePath, alertRulesMetadata);
    List<AlertRuleDetails> alertRuleDetails =
        connection.runMaprDbQueryWithFilter(
            node.toString(), null, null, null, AlertRuleDetails.class);
    return alertRuleDetails;
  }

  /**
   * Check the last trigger based on lookback period time.
   *
   * @param alertRuleId alertRuleId
   * @return list
   */
  public Boolean checkAlertResultBasedOnLastTrigger(String alertRuleId, Long lastTriggeredWindow) {
    List<AlertResult> alertResultsList =
        AlertUtils.getLastAlertResultByAlertRuleId(alertRuleId, basePath, alertResults);
    if (alertResultsList.size() > 0) {
      // Add one minute grace period to last trigger time for evaluation
      // in case alert Evaluation takes time.
      return (alertResultsList.get(0).getStartTime() <= lastTriggeredWindow + 60000) ? true : false;
    } else {
      return true;
    }
  }
}
