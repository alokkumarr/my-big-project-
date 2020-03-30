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
const PIVOT = require('../../helpers/Constants').PIVOT;
const assert = chai.assert;
const categories = require('../../helpers/data-generation/categories');
const subCategories = require('../../helpers/data-generation/subCategories');
const LoginPage = require('../../pages/LoginPage');
const ObservePage = require('../../pages/ObservePage');
const HeaderPage = require('../../pages/components/Header');
const DashboardDesigner = require('../../pages/DashboardDesigner');
const DashboardHeader = require('../../pages/DashboardHeader');
const users = require('../../helpers/data-generation/users');

describe('Running create and delete dashboards with charts in dashboards/createAndDeleteDashboardsWithPivotWithGlobalFilter.test.js', () => {
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
    logger.info(
      'Starting dashboards/createAndDeleteDashboardsWithPivotWitGlobalFilter.test.js.....'
    );
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

  afterEach(done => {
    setTimeout(() => {
      //Delete analysis
      analysesDetails.forEach(currentAnalysis => {
        if (currentAnalysis.analysisId) {
          new AnalysisHelper().deleteAnalysis(
            host,
            token,
            currentAnalysis.analysisId
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
    testDataReader.testData['PIVOT_GLOBAL_FILTER']['dashboard']
      ? testDataReader.testData['PIVOT_GLOBAL_FILTER']['dashboard']
      : {},
    (data, id) => {
      it(`${id}:${data.description}`, () => {
        try {
          if (!token) {
            logger.error('token can not be null');
            assert.isNotNull(token, 'token can not be null');
          }

          const currentTime = new Date().getTime();
          const name = `e2e ${currentTime}`;
          const description = `e2e description ${currentTime}`;
          let dashboardName = 'AT Dashboard Name' + currentTime;
          let dashboardDescription = 'AT Dashboard description ' + currentTime;

          let analysis = new ObserveHelper().addAnalysisByApi(
            host,
            token,
            name,
            description,
            PIVOT,
            null, // No subtype of Pivot.
            data.filters
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

          browser.sleep(2000); // Some time browser is not able to load the global filter button
          const dashboardHeader = new DashboardHeader();
          dashboardHeader.clickOnOpenGlobalFilterButton();

          headerPage.hideProgressBar();
          dashboardHeader.applyAndVerifyGlobalFilters(
            data.dashboardGlobalFilters
          );

          browser.sleep(2000); // Some time browser is not able to load the global filter button
          dashboardHeader.clickOnOpenGlobalFilterButton();
          dashboardHeader.verifyAppliedGlobalFilters(
            data.dashboardGlobalFilters
          );
          browser.refresh();

          observePage.clickOnDeleteDashboardButton();

          dashboardDesigner.clickOnDashboardConfirmDeleteButton();
          observePage.verifyDashboardTitleIsDeleted(dashboardName);
        } catch (e) {
          logger.error(e);
        }
      }).result.testInfo = {
        testId: id,
        data: data,
        feature: 'PIVOT_GLOBAL_FILTER',
        dataProvider: 'dashboard'
      };
    }
  );
});
