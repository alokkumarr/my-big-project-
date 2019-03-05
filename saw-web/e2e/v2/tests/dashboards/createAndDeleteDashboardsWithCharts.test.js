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
const Constants = require('../../helpers/Constants');
const DashboardFunctions = require('../../pages/components/DashboardFunctions')

const assert = chai.assert;
const categories = require('../../helpers/data-generation/categories');
let subCategories = require('../../helpers/data-generation/subCategories');

const LoginPage = require('../../pages/LoginPage');
const ObservePage = require('../../pages/ObservePage');

describe('Running create and delete dashboards with charts in dashboards/createAndDeleteDashboards.test.js', () => {

  const subCategoryName = subCategories.createSubCategories.observeSubCategory.name;
  const analysisCategoryName = categories.analyses.name;
  const analysisSubCategoryName = subCategories.createSubCategories.createAnalysis.name;

  let host;
  let token;
  let analysesDetails = [];
  let dashboardId;

  beforeAll(() => {
    logger.info('Starting dashboards/createAndDeleteDashboards.test.js.....');
    host = APICommonHelpers.getApiUrl(browser.baseUrl);
    token = APICommonHelpers.generateToken(host);
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


  using(testDataReader.testData['CREATEDELETEDASHBOARDSWITHCHARTS']['dashboards']
  ? testDataReader.testData['CREATEDELETEDASHBOARDSWITHCHARTS']['dashboards']
  : {}, (data, id) => {
      it(`${id}:${data.description}`, ()=> {
        logger.info(`Executing test case with id: ${id}`);
        try{
          if(!token) {
            logger.error('token cannot be null');
            assert.isNotNull(token, 'token cannot be null');
          }
          let currentTime = new Date().getTime();
          let type = Constants.CHART;
          let subType = data.chartType.split(':')[1];

          let name = 'AT ' + data.chartType + ' ' + globalVariables.e2eId + '-' + currentTime;
          let description = 'AT Description:' + data.chartType + ' for e2e ' + globalVariables.e2eId + '-' + currentTime;

          let dashboardName = 'AT Dashboard Name' + currentTime;
          let dashboardDescription = 'AT Dashboard description ' + currentTime;

          let dashboardFunctions = new DashboardFunctions();
          let analysis = dashboardFunctions.addAnalysisByApi(host, token, name, description, type, subType);
          expect(analysis).toBeTruthy();
          assert.isNotNull(analysis, 'analysis cannot be null');
          analysesDetails.push(analysis);

          new LoginPage().loginAs(data.user);

          
          dashboardFunctions.goToObserve();
          dashboardId = dashboardFunctions.addNewDashBoardFromExistingAnalysis(dashboardName, dashboardDescription, analysisCategoryName, analysisSubCategoryName, subCategoryName, analysesDetails);
        } catch(e) {
          logger.error(e);
        }    

      }).result.testInfo = {
        testId: id,
        data: data,
        feature: 'CREATEDELETEDASHBOARDSWITHCHARTS',
        dataProvider: 'dashboards'
      }
  });
});
