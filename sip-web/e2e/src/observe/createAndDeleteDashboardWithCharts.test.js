var testDataReader = require('../testdata/testDataReader.js');
const using = require('jasmine-data-provider');
const commonFunctions = require('../javascript/helpers/commonFunctions.js');
const protractorConf = require('../../protractor.conf');
const categories = require('../javascript/data/categories');
const subCategories = require('../javascript/data/subCategories');
let AnalysisHelper = require('../../v2/helpers/api/AnalysisHelper');
const Constants = require('../javascript/api/Constants');
const globalVariables = require('../javascript/helpers/globalVariables');
const loginPage = require('../javascript/pages/loginPage.po.js');
const DashboardFunctions = require('../javascript/helpers/observe/DashboardFunctions');
const ObserveHelper = require('../../v2/helpers/api/ObserveHelper');
let APICommonHelpers = require('../../v2/helpers/api/APICommonHelpers');
const chai = require('chai');
const assert = chai.assert;
const logger = require('../../v2/conf/logger')(__filename);

describe('Create & delete dashboard tests: createAndDeleteDashboardWithCharts.test.js', () => {
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
    host = APICommonHelpers.getApiUrl(browser.baseUrl);
    token = APICommonHelpers.generateToken(host);
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
        if(currentAnalysis.analysisId){
          new AnalysisHelper().deleteAnalysis(host, token, protractorConf.config.customerCode, currentAnalysis.analysisId);
        }
      });
      //reset the array
      analysesDetails = [];

      //delete dashboard if ui failed.
      if(dashboardId) {
        new ObserveHelper().deleteDashboard(host, token, dashboardId);
      }
      commonFunctions.logOutByClearingLocalStorage();
      done();
    }, protractorConf.timeouts.pageResolveTimeout);
  });

  using(testDataReader.testData['DASHBOARDWITHCHARTS']['dashboardWithChartsDataProvider'], function(data, description) {
    it('should able to create & delete dashboard with charts: ' + description +' testDataMetaInfo: '+ JSON.stringify({test:description,feature:'DASHBOARDWITHCHARTS', dp:'dashboardWithChartsDataProvider'}), () => {
      try {
        if(!token) {
          logger.error('token cannot be null');
          expect(token).toBeTruthy();
          assert.isNotNull(token, 'token cannot be null');
        }
        let currentTime = new Date().getTime();
        let user = data.user;
        let type = Constants.CHART;
        let subType = data.chartType.split(':')[1];

        let dashboardFunctions = new DashboardFunctions();

        let name = 'AT ' + data.chartType + ' ' + globalVariables.e2eId + '-' + currentTime;
        let description = 'AT Description:' + data.chartType + ' for e2e ' + globalVariables.e2eId + '-' + currentTime;
        let analysis = dashboardFunctions.addAnalysisByApi(host, token, name, description, type, subType);
        expect(analysis).toBeTruthy();
        assert.isNotNull(analysis, 'analysis cannot be null');
        analysesDetails.push(analysis);

        loginPage.loginAs(user);

        dashboardFunctions.goToObserve();
        let dashboardName = 'AT Dashboard Name' + currentTime;
        let dashboardDescription = 'AT Dashboard description ' + currentTime;

        dashboardId = dashboardFunctions.addNewDashBoardFromExistingAnalysis(dashboardName, dashboardDescription, analysisCategoryName, analysisSubCategoryName, subCategoryName, analysesDetails);
        dashboardFunctions.verifyDashboard(dashboardName, name, true);

      } catch (e) {
        console.log(e);
      }
    });
  });
});
