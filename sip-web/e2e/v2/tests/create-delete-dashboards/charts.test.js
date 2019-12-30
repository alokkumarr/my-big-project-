const using = require('jasmine-data-provider');
const testDataReader = require('../../testdata/testDataReader.js');
const protractorConf = require('../../conf/protractor.conf');
const logger = require('../../conf/logger')(__filename);
const commonFunctions = require('../../pages/utils/commonFunctions');
const globalVariables = require('../../helpers/data-generation/globalVariables');
const APICommonHelpers = require('../../helpers/api/APICommonHelpers');
const AnalysisHelper = require('../../helpers/api/AnalysisHelper');
const ObserveHelper = require('../../helpers/api/ObserveHelper');
const chai = require('chai');
const CHART = require('../../helpers/Constants').CHART;
const assert = chai.assert;
const categories = require('../../helpers/data-generation/categories');
const subCategories = require('../../helpers/data-generation/subCategories');
const LoginPage = require('../../pages/LoginPage');
const ObservePage = require('../../pages/ObservePage');
const HeaderPage = require('../../pages/components/Header');
const DashboardDesigner = require('../../pages/DashboardDesigner');
const users = require('../../helpers/data-generation/users');

describe('Running create and delete dashboards with charts in create-delete-dashboards/charts.test.js', () => {
  const subCategoryName =
    subCategories.createSubCategories.observeSubCategory.name;
  const analysisCategoryName = categories.analyses.name;
  const analysisSubCategoryName =
    subCategories.createSubCategories.createAnalysis.name;

  let host;
  let token;
  let analysesDetails = [];
  let dashboardId;

  beforeAll(() => {
    logger.info('Starting create-delete-dashboards/charts.test.js.....');
    host = APICommonHelpers.getApiUrl(browser.baseUrl);
    token = APICommonHelpers.generateToken(
      host,
      users.admin.loginId,
      users.anyUser.password
    );
    jasmine.DEFAULT_TIMEOUT_INTERVAL = protractorConf.timeouts.timeoutInterval;
  });

  beforeEach(done => {
    setTimeout(() => {
      done();
    }, protractorConf.timeouts.pageResolveTimeout);
  });

  afterEach(function(done) {
    setTimeout(function() {
      //Delete analysis
      analysesDetails.forEach(function(currentAnalysis) {
        if (currentAnalysis.analysisId) {
          new AnalysisHelper().deleteAnalysis(
            host,
            token,
            protractorConf.config.customerCode,
            currentAnalysis.analysisId,
            CHART
          );
        }
      });
      //reset the array
      analysesDetails = [];
      //delete dashboard if ui failed.
      if (dashboardId) {
        new ObserveHelper().deleteDashboard(host, token, dashboardId);
      }
      commonFunctions.clearLocalStorage();
      done();
    }, protractorConf.timeouts.pageResolveTimeout);
  });

  using(
    testDataReader.testData['CHART_DASHBOARD']['dashboard']
      ? testDataReader.testData['CHART_DASHBOARD']['dashboard']
      : {},
    (data, id) => {
      it(`${id}:${data.description}`, () => {
        logger.info(`Executing test case with id: ${id}`);
        try {
          if (!token) {
            logger.error('token cannot be null');
            assert.isNotNull(token, 'token cannot be null');
          }
          const currentTime = new Date().getTime();
          const subType = data.chartType.split(':')[1];
          const name = `e2e ${currentTime}`;
          const description = `e2e description ${currentTime}`;
          const dashboardName = 'AT Dashboard Name' + currentTime;
          const dashboardDescription =
            'AT Dashboard description ' + currentTime;

          let analysis = new ObserveHelper().addAnalysisByApi(
            host,
            token,
            name,
            description,
            CHART,
            subType
          );
          expect(analysis).toBeTruthy();
          assert.isNotNull(analysis, 'analysis cannot be null');
          analysesDetails.push(analysis);

          new LoginPage().loginAs(data.user);

          const headerPage = new HeaderPage();
          headerPage.clickOnModuleLauncher();
          headerPage.clickOnObserveLink();

          const observePage = new ObservePage();
          observePage.clickOnAddDashboardButton();

          const dashboardDesigner = new DashboardDesigner();
          dashboardDesigner.clickOnAddWidgetButton();
          dashboardDesigner.clickOnExistingAnalysisLink();
          dashboardDesigner.clickOnCategoryOrMetricName(analysisCategoryName);
          dashboardDesigner.clickOnCategoryOrMetricName(
            analysisSubCategoryName
          );
          dashboardDesigner.addRemoveAnalysisById(analysesDetails);
          dashboardDesigner.clickonSaveButton();
          dashboardDesigner.setDashboardName(dashboardName);
          dashboardDesigner.setDashboardDescription(dashboardDescription);
          dashboardDesigner.clickOnCategorySelect();
          dashboardDesigner.clickOnSubCategorySelect(subCategoryName);
          dashboardDesigner.clickOnSaveDialogButton();
          dashboardDesigner.verifySaveButton();

          commonFunctions.getDashboardId().then(id => {
            dashboardId = id;
          });
          observePage.verifyDashboardTitle(name);
          observePage.verifyDashboardTitle(dashboardName);
          observePage.verifyAddedAnalysisName(name);
          observePage.displayDashboardAction('Refresh');
          observePage.displayDashboardAction('Delete');
          observePage.displayDashboardAction('Edit');
          observePage.displayDashboardAction('Filter');
          browser.sleep(4000); // Below condition was failing if browser was not put to sleep.
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
        feature: 'CHART_DASHBOARD',
        dataProvider: 'dashboard'
      };
    }
  );
});
