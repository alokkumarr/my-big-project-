var testDataReader = require('../testdata/testDataReader.js');
const using = require('jasmine-data-provider');
const commonFunctions = require('../javascript/helpers/commonFunctions.js');
const protractorConf = require('../../protractor.conf');
const subCategories = require('../javascript/data/subCategories');
let APICommonHelpers = require('../../v2/helpers/api/APICommonHelpers');
const globalVariables = require('../javascript/helpers/globalVariables');
const loginPage = require('../javascript/pages/loginPage.po.js');
const DashboardFunctions = require('../javascript/helpers/observe/DashboardFunctions');
const ObserveHelper = require('../../v2/helpers/api/ObserveHelper');
const dataSets = require('../javascript/data/datasets');
const chai = require('chai');
const assert = chai.assert;
const logger = require('../../v2/conf/logger')(__filename);

describe('Create & delete dashboard tests: createAndDeleteDashboardWithActualVsTargetKpi.test.js', () => {

  const subCategoryName = subCategories.observeSubCategory.name;
  let host;
  let token;
  let dashboardId;
  const metricName = dataSets.pivotChart;

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
      //delete dashboard if ui failed.
      if(dashboardId) {
        new ObserveHelper().deleteDashboard(host, token, dashboardId);
      }
      commonFunctions.logOutByClearingLocalStorage();
      done();
    }, protractorConf.timeouts.pageResolveTimeout);
  });

  using(testDataReader.testData['DASHBOARD_WITH_ACTUAL_VS_TARGET_KPI']['dashboard_with_actual_vs_target_DataProvider'], function(data, description) {
    it('should able to create & delete dashboard with charts: ' + description +' testDataMetaInfo: '+ JSON.stringify({test:description,feature:'DASHBOARD_WITH_ACTUAL_VS_TARGET_KPI', dp:'dashboard_with_actual_vs_target_DataProvider'}), () => {
      try {
        if(!token) {
          logger.error('token cannot be null');
          expect(token).toBeTruthy();
          assert.isNotNull(token, 'token cannot be null');
        }
        let currentTime = new Date().getTime();
        let user = data.user;
        let dashboardFunctions = new DashboardFunctions();
        loginPage.loginAs(user);

        dashboardFunctions.goToObserve();
        let dashboardName = 'AT Dashboard Name' + currentTime;
        let dashboardDescription = 'AT Dashboard description ' + currentTime;
        let kpiName = 'AT kpi'+data.kpiInfo.column+ globalVariables.e2eId;

        dashboardId = dashboardFunctions.addNewDashBoardForActualVsTargetKPI(dashboardName, dashboardDescription, subCategoryName, metricName, data.kpiInfo, kpiName);
        dashboardFunctions.verifyActualVsTargetKPIAndDelete(dashboardName, kpiName, data.kpiInfo, true);

      } catch (e) {
        logger.error(e);
      }
    });
  });
});
