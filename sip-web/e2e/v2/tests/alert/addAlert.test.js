const using = require('jasmine-data-provider');
const testDataReader = require('../../testdata/testDataReader.js');
const protractorConf = require('../../conf/protractor.conf');
const logger = require('../../conf/logger')(__filename);
const commonFunctions = require('../../pages/utils/commonFunctions');
const chai = require('chai');
const assert = chai.assert;
const AlertsHelper = require('../../helpers/api/AlertsHelper');
const APICommonHelpers = require('../../helpers/api/APICommonHelpers');
const LoginPage = require('../../pages/LoginPage');
const HeaderPage = require('../../pages/components/Header');
const AlertDashboard = require('../../pages/alerts/AlertDashboard');

describe('ALERTS Module tests: addAlert.test.js', () => {
  let token;
  let host;
  let alertName;

  beforeAll(() => {
    logger.info('Starting alerts/addAlert.test.js');
    host = APICommonHelpers.getApiUrl(browser.baseUrl);
    token = APICommonHelpers.generateToken(host);
    jasmine.DEFAULT_TIMEOUT_INTERVAL = protractorConf.timeouts.timeoutInterval;
  });

  beforeEach(done => {
    setTimeout(() => {
      done();
    }, protractorConf.timeouts.pageResolveTimeout);
  });

  afterEach(function(done) {
    setTimeout(function() {
      const alertHelper = new AlertsHelper();
      const alert = alertHelper.getAlertDetailByName(host, token, alertName);
      if (alert) {
        logger.info(`Deleting Alert : ` + JSON.stringify(alert));
        alertHelper.deleteAlert(host, token, alert.alertRulesSysId);
      }
      commonFunctions.clearLocalStorage();
      done();
    }, protractorConf.timeouts.pageResolveTimeout);
  });

  using(
    testDataReader.testData['ALERTS']['dashboard']
      ? testDataReader.testData['ALERTS']['dashboard']
      : {},
    (data, id) => {
      it(`${id}:${data.description}`, () => {
        logger.info(`Executing test case with id: ${id}`);
        if (!token) {
          logger.error('token cannot be null');
          assert.isNotNull(token, 'token cannot be null');
        }
        new LoginPage().loginAs(data.user);

        const headerPage = new HeaderPage();
        headerPage.clickOnModuleLauncher();
        headerPage.clickOnAlertsLink();
        headerPage.openCategoryMenu();
        headerPage.clickOnConfigureAlert();

        const alertPage = new AlertDashboard();
        alertPage.clickOnAddAlertButton();
        // Alert name section
        const time = new Date().getTime();
        alertName = data.alertInfo.alertName + time;
        data.alertInfo.alertName = alertName;
        data.alertInfo.alertDescription =
          data.alertInfo.alertDescription + time;
        alertPage.fillAlertName(data.alertInfo.alertName);
        alertPage.clickOnAlertSeverity(data.alertInfo.severityLevel);
        alertPage.clickOnNotification();
        alertPage.clickOnNotificationMethod(data.alertInfo.notificationMethod);
        alertPage.FillNotificationValue('abc@test.com');
        alertPage.clickOnAlertStatus(data.alertInfo.alertStatus);
        alertPage.fillAlertDescription(data.alertInfo.alertDescription);
        alertPage.clickOnToMetricSelectionButton();
        // metric monitor section
        alertPage.clickOnSelectDataPod(data.alertInfo.alertDataPod);
        alertPage.clickOnSelectMetric(data.alertInfo.alertMetricName);
        alertPage.selectMonitoringType(data.alertInfo.monitoringType);
        alertPage.selectAggregationType(data.alertInfo.alertAggregation);
        alertPage.selectAlertOperator(data.alertInfo.alertOperatorName);
        alertPage.fillThresholdValue(data.alertInfo.alertThresholdValue);
        alertPage.clickOnToAlertRules();
        //Alert rule section
        alertPage.selectLokbackColumn(data.alertInfo.lookBackColumn);
        alertPage.fillLookBackPeriod(data.alertInfo.lookBackPeriod);
        alertPage.selectLookbackPeriodType(data.alertInfo.lookbackPeriodType);
        alertPage.selectAttributeName(data.alertInfo.attributeName);
        alertPage.selectAttributeValue(data.alertInfo.attributeValue);
        alertPage.clickOnToAddStep();
        alertPage.clickOnAddNewAlertButton();
        alertPage.validateAddedAlerts(data.alertInfo.alertName);
        // Delete Updated alert.
        alertPage.clickOnDeleteAlertIcon(data.alertInfo.alertName);
        alertPage.clickOnConfirmDeleteAlert();
      }).result.testInfo = {
        testId: id,
        data: data,
        feature: 'ALERTS',
        dataProvider: 'dashboard'
      };
    }
  );
});
