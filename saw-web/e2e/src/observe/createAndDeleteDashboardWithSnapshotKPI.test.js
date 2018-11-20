var testDataReader = require('../testdata/testDataReader.js');
const using = require('jasmine-data-provider');
const commonFunctions = require('../javascript/helpers/commonFunctions.js');
const protractorConf = require('../../protractor.conf');
const subCategories = require('../javascript/data/subCategories');
let AnalysisHelper = require('../javascript/api/AnalysisHelper');
let ApiUtils = require('../javascript/api/APiUtils');
const globalVariables = require('../javascript/helpers/globalVariables');
const loginPage = require('../javascript/pages/loginPage.po.js');
const DashboardFunctions = require('../javascript/helpers/observe/DashboardFunctions');
const ObserveHelper = require('../javascript/api/ObserveHelper');
const dataSets = require('../javascript/data/datasets');

describe('Create & delete dashboard tests: createAndDeleteDashboardWithSnapshotKPI.test.js', () => {
  const subCategoryName = subCategories.observeSubCategory.name;

  let host;
  let token;
  let dashboardId;
  const metricName = dataSets.pivotChart;

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
      //delete dashboard if ui failed.
      let oh = new ObserveHelper();
      if(dashboardId) {
        oh.deleteDashboard(host, token, dashboardId);
      }
      commonFunctions.logOutByClearingLocalStorage();
      done();
    }, protractorConf.timeouts.pageResolveTimeout);
  });

  using(testDataReader.testData['DASHBOARDWITHSNAPSHOTKPI']['dashboardWithSnapshotKpiDataProvider'], function(data, description) {
    it('should able to create & delete dashboard for snapshot kpi ' + description +' testDataMetaInfo: '+ JSON.stringify({test:description,feature:'DASHBOARDWITHSNAPSHOTKPI', dp:'dashboardWithSnapshotKpiDataProvider'}), () => {
      try {

        let currentTime = new Date().getTime();
        let user = data.user;

        let dashboardFunctions = new DashboardFunctions();
        loginPage.loginAs(user);

        dashboardFunctions.goToObserve();
        let dashboardName = 'AT Dashboard Name' + currentTime;
        let dashboardDescription = 'AT Dashboard description ' + currentTime;
        let kpiName = 'AT kpi'+data.kpiInfo.column+ globalVariables.e2eId;

        dashboardId = dashboardFunctions.addNewDashBoardForSnapshotKPI(dashboardName, dashboardDescription, subCategoryName, metricName, data.kpiInfo, kpiName);
        dashboardFunctions.verifyKPIAndDelete(dashboardName, kpiName, data.kpiInfo, true);

      } catch (e) {
        console.log(e);
      }
    });
  });
});
