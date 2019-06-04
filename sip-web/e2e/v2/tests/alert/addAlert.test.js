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
const AlertPage = require('../../pages/alerts/alertDashboard');

describe('ALERTS Module tests: addAlert.test.js', () => {
  let token;
  let host;
  let alertHelper;

  beforeAll(() => {
    logger.info('Starting alerts/addAlert.test.js');
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
      let id = alertHelper.getAlertList(host, token);
      alertHelper.deleteAlert(host, token, id);
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
        try {
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

          const alertPage = new AlertPage();
          alertPage.clickOnAddAlertButton();
          alertPage.fillAlertName(data.alertInfo.alertName);
          alertPage.clickOnAlertSeverity(data.alertInfo.severityLevel);
          alertPage.clickOnAlertStatus(data.alertInfo.alertStatus);
          alertPage.fillAlertDescription(data.alertInfo.alertDescription);
          alertPage.clickOnToMetricSelectionButton();
          alertPage.clickOnSelectDataPod(data.alertInfo.alertDataPod);
          alertPage.clickOnSelectMetric(data.alertInfo.alertMetricName);
          alertPage.clickOnToAlertRules();
          alertPage.clickOnAlertAggregationy(data.alertInfo.alertAggregation);
          alertPage.clickOnAlertOperator(data.alertInfo.alertOperatorName);
          alertPage.fillThresholdValue(data.alertInfo.alertThresholdValue);
          alertPage.clickOnToAddStep();
          alertPage.clickOnAddNewAlertButton();
          alertPage.validateAddedAlerts(data.alertInfo.alertName);
        } catch (e) {
          logger.error(e);
        }
      }).result.testInfo = {
        testId: id,
        data: data,
        feature: 'ALERTS',
        dataProvider: 'dashboard'
      };
    }
  );
});
