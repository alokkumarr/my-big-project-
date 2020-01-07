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
const AlertPage = require('../../pages/alerts/alertDashboard');

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
        let response = alertHelper.addAlerts(host, token, data.editAlertInfo);
        alertId = response.alert.alertRulesSysId;
        expect(alertId).toBeTruthy();
        assert.isNotNull(alertId, 'alert id should not be null');

        new LoginPage().loginAs(data.user);

        let updatedAlertName = 'Updated ' + data.editAlertInfo.alertName;
        let updatedAlertDescription =
          'Updated ' + data.editAlertInfo.alertDescription;

        const headerPage = new HeaderPage();
        headerPage.clickOnModuleLauncher();
        headerPage.clickOnAlertsLink();
        headerPage.openCategoryMenu();
        headerPage.clickOnConfigureAlert();

        const alertPage = new AlertPage();
        alertPage.clickOnEditAlertButton();
        alertPage.fillAlertName(updatedAlertName);
        alertPage.fillAlertDescription(updatedAlertDescription);
        alertPage.clickOnToMetricSelectionButton();
        alertPage.clickOnToAlertRules();
        alertPage.clickOnToAddStep();
        alertPage.clickOnUpdateAlertButton();

        alertPage.validateAddedAlerts(updatedAlertName); // Verify updated alert by name.
        alertPage.validateAddedAlerts(updatedAlertDescription); // Verify updated alert by description.

        // Delete Updated alert.
        alertPage.clickOnDeleteAlertIcon();
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
