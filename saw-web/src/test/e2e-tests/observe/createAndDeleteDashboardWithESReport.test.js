const commonFunctions = require('../../javascript/helpers/commonFunctions.js');
const analyzePage = require('../../javascript/pages/analyzePage.po.js');
const protractorConf = require('../../../../conf/protractor.conf');
const using = require('jasmine-data-provider');
const categories = require('../../javascript/data/categories');
const subCategories = require('../../javascript/data/subCategories');
let AnalysisHelper = require('../../javascript/api/AnalysisHelper');
let ApiUtils = require('../../javascript/api/APiUtils');
const Constants = require('../../javascript/api/Constants');
const globalVariables = require('../../javascript/helpers/globalVariables');
const loginPage = require('../../javascript/pages/loginPage.po.js');
const DashboardFunctions = require('../../javascript/helpers/observe/DashboardFunctions');
const ObserveHelper = require('../../javascript/api/ObserveHelper');
const homePage = require('../../javascript/pages/homePage.po.js');

describe('Create & delete dashboard tests: createAndDeleteDashboardWithESReport.test.js', () => {
  const defaultCategory = categories.privileges.name;
  const categoryName = categories.observe.name;
  const subCategoryName = subCategories.observeSubCategory.name;
  const analysisCategoryName = categories.analyses.name;
  const analysisSubCategoryName = subCategories.createAnalysis.name;

  let analysesDetails = [];
  let host;
  let token;
  let dashboardId;

  const dataProvider = {

    'dashboard with esReport by admin': {
      user: 'admin'
    },
    'dashboard with esReport by user': {
      user: 'userOne'
    }
  };

  beforeAll(function() {
    host = new ApiUtils().getHost(browser.baseUrl);
    token = new AnalysisHelper().getToken(host);
    jasmine.DEFAULT_TIMEOUT_INTERVAL = protractorConf.timeouts.extendedDefaultTimeoutInterval;

  });

  beforeEach(function(done) {
    setTimeout(function() {
      //expect(browser.getCurrentUrl()).toContain('/login');
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
  afterAll(function() {
    //commonFunctions.logOutByClearingLocalStorage();
  });

  using(dataProvider, function(data, description) {
    it('should able to create & delete dashboard with es-report: ' + description, () => {
      try {

        let currentTime = new Date().getTime();
        let user = data.user;
        let type = Constants.ES_REPORT;

        let dashboardFunctions = new DashboardFunctions();

        let name = 'AT ' + Constants.ES_REPORT + ' ' + globalVariables.e2eId + '-' + currentTime;
        let description = 'AT Description:' + Constants.ES_REPORT + ' for e2e ' + globalVariables.e2eId + '-' + currentTime;
        let analysis = dashboardFunctions.addAnalysisByApi(host, token, name, description, type, null);
        analysesDetails.push(analysis);

        loginPage.loginAs(user);

        dashboardFunctions.goToObserve();
        browser.sleep(2000);
        commonFunctions.waitFor.elementToBeNotVisible(homePage.progressbar, protractorConf.timeouts.extendedFluentWait);
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
