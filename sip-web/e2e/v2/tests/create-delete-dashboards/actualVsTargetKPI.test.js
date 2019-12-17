const using = require('jasmine-data-provider');
const testDataReader = require('../../testdata/testDataReader');
const protractorConf = require('../../conf/protractor.conf');
const logger = require('../../conf/logger')(__filename);
const commonFunctions = require('../../pages/utils/commonFunctions');
const globalVariables = require('../../helpers/data-generation/globalVariables');
const APICommonHelpers = require('../../helpers/api/APICommonHelpers');
const ObserveHelper = require('../../helpers/api/ObserveHelper');
const chai = require('chai');
const dataSets = require('../../helpers/data-generation/datasets');
const assert = chai.assert;
const subCategories = require('../../helpers/data-generation/subCategories');
const LoginPage = require('../../pages/LoginPage');
const ObservePage = require('../../pages/ObservePage');
const HeaderPage = require('../../pages/components/Header');
const DashboardDesigner = require('../../pages/DashboardDesigner');
const users = require('../../helpers/data-generation/users');

describe('Running create and delete dashboards with ActualVSTarget KPIS in create-delete-dashboards/actualVSTargetKPI.test', () => {
  const subCategoryName =
    subCategories.createSubCategories.observeSubCategory.name;
  let host;
  let token;
  let dashboardId;
  const metricName = dataSets.pivotChart;

  beforeAll(function() {
    logger.info(
      'Starting create-delete-dashboards/actualVSTargetKPI.test.js.....'
    );
    host = APICommonHelpers.getApiUrl(browser.baseUrl);
    token = APICommonHelpers.generateToken(
      host,
      users.admin.loginId,
      users.anyUser.password
    );

    jasmine.DEFAULT_TIMEOUT_INTERVAL = protractorConf.timeouts.timeoutInterval;
  });

  beforeEach(function(done) {
    setTimeout(function() {
      done();
    }, protractorConf.timeouts.pageResolveTimeout);
  });

  afterEach(function(done) {
    setTimeout(function() {
      //delete dashboard if ui failed.
      if (dashboardId) {
        new ObserveHelper().deleteDashboard(host, token, dashboardId);
      }
      commonFunctions.clearLocalStorage();
      done();
    }, protractorConf.timeouts.pageResolveTimeout);
  });

  using(
    testDataReader.testData['ACTUAL_VS_TARGET']['dashboard']
      ? testDataReader.testData['ACTUAL_VS_TARGET']['dashboard']
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

          const currentTime = new Date().getTime();
          const dashboardName = 'AT Dashboard Name' + currentTime;
          const dashboardDescription =
            'AT Dashboard description ' + currentTime;
          const kpiName =
            'AT kpi' + data.kpiInfo.column + globalVariables.e2eId;

          const headerPage = new HeaderPage();
          headerPage.clickOnModuleLauncher();
          headerPage.clickOnObserveLink();

          const observePage = new ObservePage();
          observePage.clickOnAddDashboardButton();

          const dashboardDesigner = new DashboardDesigner();
          dashboardDesigner.clickOnAddWidgetButton();
          dashboardDesigner.clickOnActualVsTargetKPILink();
          dashboardDesigner.clickOnCategoryOrMetricName(metricName);
          dashboardDesigner.clickOnKpiColumnByName(
            data.kpiInfo.column.toLowerCase()
          );
          dashboardDesigner.fillKPINameDetails(kpiName);
          dashboardDesigner.clickOnDateFieldSelect();
          dashboardDesigner.clickOnDateOrAggregationElement(data.kpiInfo.date);
          dashboardDesigner.clickOnDatePreSelect();
          dashboardDesigner.clickOnDateOrAggregationElement(
            data.kpiInfo.filter
          );
          dashboardDesigner.clickOnAggregationSelect();
          dashboardDesigner.clickOnDateOrAggregationElement(
            data.kpiInfo.primaryAggregation
          );
          dashboardDesigner.fillMeasure1Input(data.kpiInfo.measure1);
          dashboardDesigner.fillMeasure2Input(data.kpiInfo.measure2);
          dashboardDesigner.fillMetricTargetInput(data.kpiInfo.metricTarget);
          dashboardDesigner.clickOnBandColor(data.kpiInfo.bandColor);
          dashboardDesigner.clickOnApplyKPIButton();
          dashboardDesigner.clickonSaveButton();
          dashboardDesigner.setDashboardName(dashboardName);
          dashboardDesigner.setDashboardDescription(dashboardDescription);
          dashboardDesigner.clickOnCategorySelect();
          dashboardDesigner.clickOnSubCategorySelect(subCategoryName);
          dashboardDesigner.clickOnSaveDialogButton();
          dashboardDesigner.verifySaveButton();

          dashboardId = commonFunctions.getDashboardId();

          observePage.verifyDashboardTitle(dashboardName);
          observePage.verifyKpiByName(kpiName);
          observePage.verifyFilterByName(data.kpiInfo.filter);
          observePage.displayDashboardAction('Refresh');
          observePage.displayDashboardAction('Edit');
          observePage.displayDashboardAction('Delete');
          observePage.displayDashboardAction('Filter');
          browser.sleep(4000);
          observePage.verifyBrowserURLContainsText('?dashboard');
          observePage.clickOnDeleteDashboardButton();

          dashboardDesigner.clickOnDashboardConfirmDeleteButton();

          observePage.verifyDashboardTitleIsDeleted(dashboardName);
        } catch (e) {
          logger.error(e);
        }
      }).result.testInfo = {
        testId: id,
        data: data,
        feature: 'ACTUAL_VS_TARGET',
        dataProvider: 'dashboard'
      };
    }
  );
});
