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
const assert = chai.assert;
const categories = require('../../helpers/data-generation/categories');
const subCategories = require('../../helpers/data-generation/subCategories');
const LoginPage = require('../../pages/LoginPage');
const ObservePage = require('../../pages/ObservePage');
const HeaderPage = require('../../pages/components/Header');
const CreateNewDashboard = require('../../pages/CreateNewDashboard');
const SaveDashboardDialog = require('../../pages/components/SaveDashboardDialog');
const ConfirmDeleteDialogModel = require('../../pages/components/ConfirmDeleteDialogModel');

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
      commonFunctions.clearLocalStorage();
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
          const currentTime = new Date().getTime();
          const subType = data.chartType.split(':')[1];
          const name = 'AT ' + data.chartType + ' ' + globalVariables.e2eId + '-' + currentTime;
          const description = 'AT Description:' + data.chartType + ' for e2e ' + globalVariables.e2eId + '-' + currentTime;
          const dashboardName = 'AT Dashboard Name' + currentTime;
          const dashboardDescription = 'AT Dashboard description ' + currentTime;

          const observePage = new ObservePage();
          let analysis = observePage.addAnalysisByApi(host, token, name, description, Constants.CHART, subType);         
          expect(analysis).toBeTruthy();
          assert.isNotNull(analysis, 'analysis cannot be null');
          analysesDetails.push(analysis);

          new LoginPage().loginAs(data.user);

          const headerPage = new HeaderPage();
          headerPage.clickOnModuleLauncher();
          headerPage.clickOnObserveLink();
          
          observePage.clickOnAddDashboardButton();

          const createNewDashboard = new CreateNewDashboard();
          createNewDashboard.clickOnAddWidgetButton();
          createNewDashboard.clickOnExistingAnalysisLink();
          createNewDashboard.clickOnCategory(analysisCategoryName);
          createNewDashboard.clickOnSubCategory(analysisSubCategoryName);
          createNewDashboard.addRemoveAnalysisById(analysesDetails);
          createNewDashboard.clickonSaveButton();

          const saveDashboardDialog= new SaveDashboardDialog();
          saveDashboardDialog.setDashboardName(dashboardName);
          saveDashboardDialog.setDashboardDescription(dashboardDescription)
          saveDashboardDialog.clickOnCategorySelect();
          saveDashboardDialog.clickOnSubCategorySelect(subCategoryName);
          saveDashboardDialog.clickOnSaveDialogButton();

          createNewDashboard.verifySaveButton();

          observePage.verifyDashboardTitle(name);
          //get dashboard id from current url
          dashboardId = createNewDashboard.getDashboardId();
          observePage.verifyDashboardTitle(dashboardName);
          // Verify added analysis
          expect(observePage.getAddedAnalysisName(name).isDisplayed).toBeTruthy();

          observePage.displayDashboardAction('Refresh');
          observePage.displayDashboardAction('Delete');
          observePage.displayDashboardAction('Edit');
          observePage.displayDashboardAction('Filter');

          browser.sleep(4000); // Below condition was failing if browser was not put to sleep. 
          observePage.verifyBrowserURLContainsText('?dashboard')
          observePage.clickOnDeleteDashboardButton();

          new ConfirmDeleteDialogModel().clickOnDashboardConfirmDeleteButton();

          observePage.verifyDashboardTitleIsDeleted(dashboardName);       
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
