'use strict';

const Constants = require('../../Constants');
class AlertModel {
  getBasicAlert(name, desc, severity) {
    let body = {
      alertRuleName: name,
      alertRuleDescription: desc,
      alertSeverity: severity ? severity : 'CRITICAL',
      notification: {
        email: {
          recipients: ['abc@abc.com']
        }
      },
      activeInd: true,
      datapodId: 'workbench::sample-elasticsearch',
      datapodName: 'sample-elasticsearch',
      categoryId: 'Default',
      metricsColumn: 'long',
      aggregationType: 'avg',
      operator: 'GT',
      thresholdValue: 10,
      otherThresholdValue: null,
      lookbackColumn: 'date',
      lookbackPeriod: '11-minute',
      triggerOnLookback: false,
      attributeName: 'string',
      attributeValue: 'string 1',
      monitoringType: 'AGGREGATION_METRICS',
      product: 'SAWD000001'
    };
    return body;
  }
}
module.exports = AlertModel;
