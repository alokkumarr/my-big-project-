const using = require('jasmine-data-provider');
const testDataReader = require('../../testdata/testDataReader.js');
const protractorConf = require('../../conf/protractor.conf');
const logger = require('../../conf/logger')(__filename);
const commonFunctions = require('../../pages/utils/commonFunctions');
const assert = require('chai').assert;
let APICommonHelpers = require('../../helpers/api/APICommonHelpers');
const HeaderPage = require('../../pages/components/Header');
const AlertsHelper = require('../../helpers/api/AlertsHelper');
const LoginPage = require('../../pages/LoginPage');
const AlertDashboard = require('../../pages/alerts/AlertDashboard');

describe('Executing create and delete chart tests from charts/createAndDelete.test.js', () => {
  let host;
  let token;
  let alertHelper;
  let alertId;
  beforeAll(() => {
    logger.info('Starting alerts/editDeleteAlerts.test.js.....');
    host = APICommonHelpers.getApiUrl(browser.baseUrl);
    token = APICommonHelpers.generateToken(host);
    alertHelper = new AlertsHelper();
    jasmine.DEFAULT_TIMEOUT_INTERVAL = protractorConf.timeouts.timeoutInterval;
  });

  beforeEach(done => {
    setTimeout(() => {
      done();
    }, protractorConf.timeouts.pageResolveTimeout);
  });

  afterEach(function(done) {
    setTimeout(function() {
      if (alertId) {
        logger.info(`Deleting Alert : ` + JSON.stringify(alertId));
        alertHelper.deleteAlert(host, token, alertId);
      }

      commonFunctions.clearLocalStorage();
      done();
    }, protractorConf.timeouts.pageResolveTimeout);
  });

  using(
    testDataReader.testData['EDITDELETEALERTS']['alerts']
      ? testDataReader.testData['EDITDELETEALERTS']['alerts']
      : {},
    (data, id) => {
      it(`${id}:${data.description}`, () => {
        logger.info(`Executing test case with id: ${id}`);
        if (!token) {
          logger.error('token cannot be null');
          expect(token).toBeTruthy();
          assert.isNotNull(token, 'token cannot be null');
        }
        //Create new alert.
        const time = new Date().getTime();
        data.alertInfo.alertName = data.alertInfo.alertName + time;
        data.alertInfo.alertDescription =
          data.alertInfo.alertDescription + time;
        let response = alertHelper.addAlerts(
          host,
          token,
          data.alertInfo.alertName,
          data.alertInfo.alertDescription
        );
        alertId = response.alert.alertRulesSysId;
        expect(alertId).toBeTruthy();
        assert.isNotNull(alertId, 'alert id should not be null');
        new LoginPage().loginAs(data.user);

        let updatedAlertName = 'up ' + data.alertInfo.alertName;
        let updatedAlertDescription = 'up ' + data.alertInfo.alertDescription;

        const headerPage = new HeaderPage();
        headerPage.clickOnModuleLauncher();
        headerPage.clickOnAlertsLink();
        headerPage.openCategoryMenu();
        headerPage.clickOnConfigureAlert();

        const alertPage = new AlertDashboard();
        alertPage.clickOnEditAlertButton(data.alertInfo.alertName);
        // Alert name section
        alertPage.fillAlertName(updatedAlertName);
        alertPage.clickOnAlertSeverity(data.alertInfo.severityLevel);
        alertPage.clickOnAlertStatus(data.alertInfo.alertStatus);
        alertPage.fillAlertDescription(updatedAlertDescription);
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
        alertPage.clickOnUpdateAlertButton();

        alertPage.validateAddedAlerts(updatedAlertName); // Verify updated alert by name.
        alertPage.validateAddedAlerts(updatedAlertDescription); // Verify updated alert by description.

        // Delete Updated alert.
        alertPage.clickOnDeleteAlertIcon(updatedAlertName);
        alertPage.clickOnConfirmDeleteAlert();
      }).result.testInfo = {
        testId: id,
        data: data,
        feature: 'EDITDELETEALERTS',
        dataProvider: 'alerts'
      };
    }
  );
});
