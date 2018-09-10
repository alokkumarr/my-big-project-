var testDataReader = require('../../e2e-tests/testdata/testDataReader.js');
const using = require('jasmine-data-provider');
const commonFunctions = require('../../javascript/helpers/commonFunctions.js');
const protractorConf = require('../../../../conf/protractor.conf');
const categories = require('../../javascript/data/categories');
const subCategories = require('../../javascript/data/subCategories');
let AnalysisHelper = require('../../javascript/api/AnalysisHelper');
let ApiUtils = require('../../javascript/api/APiUtils');
const Constants = require('../../javascript/api/Constants');
const globalVariables = require('../../javascript/helpers/globalVariables');
const loginPage = require('../../javascript/pages/loginPage.po.js');
const DashboardFunctions = require('../../javascript/helpers/observe/DashboardFunctions');
const ObserveHelper = require('../../javascript/api/ObserveHelper');
const PromptFilterFunctions = require('../../javascript/helpers/PromptFilterFunctions');

describe('Global filters in dashboard with charts tests: dashboardGlobalFilter.test.js', () => {
  const defaultCategory = categories.privileges.name;
  const categoryName = categories.observe.name;
  const subCategoryName = subCategories.observeSubCategory.name;
  const analysisCategoryName = categories.analyses.name;
  const analysisSubCategoryName = subCategories.createAnalysis.name;

  let analysesDetails = [];
  let host;
  let token;
  let dashboardId;

  beforeAll(function() {
    host = new ApiUtils().getHost(browser.baseUrl);
    token = new AnalysisHelper().getToken(host);
    jasmine.DEFAULT_TIMEOUT_INTERVAL = protractorConf.timeouts.extendedDefaultTimeoutInterval;

  });

  beforeEach(function(done) {
    setTimeout(function() {
      done();
    }, protractorConf.timeouts.pageResolveTimeout);
  });

  afterEach(function(done) {
    setTimeout(function() {
      //Delete analysis
      analysesDetails.forEach(function(currentAnalysis) {
        new AnalysisHelper().deleteAnalysis(host, token, protractorConf.config.customerCode, currentAnalysis.analysisId);
      });
      //reset the array
      analysesDetails = [];

      //delete dashboard if ui failed.
      let oh = new ObserveHelper();
      oh.deleteDashboard(host, token, dashboardId);
      commonFunctions.logOutByClearingLocalStorage();
      done();
    }, protractorConf.timeouts.pageResolveTimeout);
  });

  using(testDataReader.testData['DASHBOARD_GLOBAL_FILTERS']['dashboardGlobalFiltersWithCharts'], function(data, description) {
    it('should able apply global filters in dashboard with charts: ' + description +' testDataMetaInfo: '+ JSON.stringify({test:description,feature:'DASHBOARD_GLOBAL_FILTERS', dp:'dashboardGlobalFiltersWithCharts'}), () => {
      try {

        let currentTime = new Date().getTime();
        let user = data.user;
        let type = Constants.CHART;
        let subType = data.chartType.split(':')[1];

        let dashboardFunctions = new DashboardFunctions();

        let name = 'AT ' + data.chartType + ' ' + globalVariables.e2eId + '-' + currentTime;
        let description = 'AT Description:' + data.chartType + ' for e2e ' + globalVariables.e2eId + '-' + currentTime;
        let analysis = dashboardFunctions.addAnalysisByApi(host, token, name, description, type, subType, data.filters);
        analysesDetails.push(analysis);

        loginPage.loginAs(user);

        dashboardFunctions.goToObserve();
        let dashboardName = 'AT Dashboard Name' + currentTime;
        let dashboardDescription = 'AT Dashboard description ' + currentTime;

        dashboardId = dashboardFunctions.addNewDashBoardFromExistingAnalysis(dashboardName, dashboardDescription, analysisCategoryName, analysisSubCategoryName, subCategoryName, analysesDetails);
        dashboardFunctions.applyAndVerifyGlobalFilters(data.dashboardGlobalFilters);
        browser.refresh();
        dashboardFunctions.deleteDashboard(dashboardName);

      } catch (e) {
        console.log(e);
      }
    });
  });
});
