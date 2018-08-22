const commonFunctions = require('../../javascript/helpers/commonFunctions.js');
const analyzePage = require('../../javascript/pages/analyzePage.po.js');
const protractorConf = require('../../../../conf/protractor.conf');
const using = require('jasmine-data-provider');
const subCategories = require('../../javascript/data/subCategories');
let AnalysisHelper = require('../../javascript/api/AnalysisHelper');
let ApiUtils = require('../../javascript/api/APiUtils');
const globalVariables = require('../../javascript/helpers/globalVariables');
const loginPage = require('../../javascript/pages/loginPage.po.js');
const DashboardFunctions = require('../../javascript/helpers/observe/DashboardFunctions');
const ObserveHelper = require('../../javascript/api/ObserveHelper');
const dataSets = require('../../javascript/data/datasets');

describe('Create & delete dashboard tests: createAndDeleteDashboardWithSnapshotKPI.test.js', () => {
  const subCategoryName = subCategories.observeSubCategory.name;

  let host;
  let token;
  let dashboardId;
  const metricName = dataSets.pivotChart;

  const dataProvider = {

    'dashboard with column by admin': {
      user: 'admin',
      kpiInfo: {
          column: 'Long',
          date: 'Date',
          filter: 'This Week',
          primaryAggregation: 'Sum',
          secondaryAggregations: ['Sum', 'Average', 'Minimum', 'Maximum', 'Count'],
          backgroundColor: 'green'
      }
    },
    'dashboard with Float by user': {
      user: 'userOne',
      kpiInfo: {
        column: 'Float',
        date: 'Date',
        filter: 'Yesterday',
        primaryAggregation: 'Average',
        secondaryAggregations: ['Sum', 'Average', 'Minimum', 'Maximum', 'Count'],
        backgroundColor: 'green'
      }
    },
    'dashboard with Integer by admin': {
      user: 'admin',
      kpiInfo: {
        column: 'Integer',
        date: 'Date',
        filter: 'MTD (Month to Date)',
        primaryAggregation: 'Minimum',
        secondaryAggregations: ['Sum', 'Average', 'Minimum', 'Maximum', 'Count'],
        backgroundColor: 'black'
      }
    },
    'dashboard with Double by user': {
      user: 'userOne',
      kpiInfo: {
        column: 'Double',
        date: 'Date',
        filter: 'Last Week',
        primaryAggregation: 'Maximum',
        secondaryAggregations: ['Sum', 'Average', 'Minimum', 'Maximum', 'Count'],
        backgroundColor: 'blue'
      }
    }
  };

  beforeAll(function() {
    host = new ApiUtils().getHost(browser.baseUrl);
    token = new AnalysisHelper().getToken(host);
    jasmine.DEFAULT_TIMEOUT_INTERVAL = protractorConf.timeouts.extendedDefaultTimeoutInterval;

  });

  beforeEach(function(done) {
    setTimeout(function() {
      expect(browser.getCurrentUrl()).toContain('/login');
      done();
    }, protractorConf.timeouts.pageResolveTimeout);
  });

  afterEach(function(done) {
    setTimeout(function() {
      //delete dashboard if ui failed.
      let oh = new ObserveHelper();
      oh.deleteDashboard(host, token, dashboardId);

      analyzePage.main.doAccountAction('logout');
      commonFunctions.logOutByClearingLocalStorage();
      done();
    }, protractorConf.timeouts.pageResolveTimeout);
  });
  afterAll(function() {
    commonFunctions.logOutByClearingLocalStorage();
  });

  using(dataProvider, function(data, description) {
    it('should able to create & delete dashboard for snapshot kpi ' + description, () => {
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
