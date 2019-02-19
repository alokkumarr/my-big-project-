const using = require('jasmine-data-provider');
const testDataReader = require('../../testdata/testDataReader.js');
const protractorConf = require('../../conf/protractor.conf');
const logger = require('../../conf/logger')(__filename);
const dataSets = require('../../helpers/data-generation/datasets');
const commonFunctions = require('../../pages/utils/commonFunctions');

let AnalysisHelper = require('../../helpers/api/AnalysisHelper');
let APICommonHelpers = require('../../helpers/api/APICommonHelpers');

const LoginPage = require('../../pages/LoginPage');
const AnalyzePage = require('../../pages/AnalyzePage');
const DesignerPage = require('../../pages/DesignerPage');
const ExecutePage = require('../../pages/ExecutePage');

describe('Executing createAndDeleteReport tests from createAndDeleteReport.test.js', () => {
  let analysisId;
  let host;
  let token;
  beforeAll(() => {
    logger.info('Starting createAndDeleteReport tests...');
    host = APICommonHelpers.getApiUrl(browser.baseUrl);
    token = APICommonHelpers.generateToken(host);
    jasmine.DEFAULT_TIMEOUT_INTERVAL = protractorConf.timeouts.timeoutInterval;
  });

  beforeEach(done => {
    setTimeout(() => {
      done();
    }, protractorConf.timeouts.pageResolveTimeout);
  });

  afterEach(done => {
    setTimeout(() => {
      if (analysisId) {
        new AnalysisHelper().deleteAnalysis(
          host,
          token,
          protractorConf.config.customerCode,
          analysisId
        );
      }
      // Logout by clearing the storage
      commonFunctions.clearLocalStorage();
      done();
    }, protractorConf.timeouts.pageResolveTimeout);
  });

  using(
    testDataReader.testData['CREATEREPORT']['dlreport']
      ? testDataReader.testData['CREATEREPORT']['dlreport']
      : {},
    (data, id) => {
      it(`${id}:${data.description}`, () => {
        try {
          const reportName = `e2e dl report ${new Date().toString()}`;
          console.log(reportName);
          const reportDescription = `e2e dl report description ${new Date().toString()}`;
          const analysisType = 'table:report';
          const tables = data.tables;
          const loginPage = new LoginPage();
          loginPage.loginAs(data.user, /analyze/);

          const analyzePage = new AnalyzePage();
          analyzePage.goToView('card');
          analyzePage.clickOnAddAnalysisButton();
          analyzePage.clickOnAnalysisType(analysisType);
          analyzePage.clickOnNextButton();
          analyzePage.clickOnDataPods(dataSets.report);
          analyzePage.clickOnCreateButton();

          const designerPage = new DesignerPage();
          designerPage.clickOnReportFields(tables);
          designerPage.verifyDisplayedColumns(tables);
          designerPage.clickOnSave();
          designerPage.enterAnalysisName(reportName);
          designerPage.enterAnalysisDescription(reportDescription);
          designerPage.clickOnSaveAndCloseDialogButton(/analyze/);
          // Verify analysis displayed in list and card view
          analyzePage.goToView('list');
          analyzePage.verifyElementPresent(
            analyzePage._analysisTitleLink(reportName),
            true,
            'report should be present in list/card view'
          );
          analyzePage.goToView('card');
          // Go to detail page and very details
          analyzePage.clickOnAnalysisLink(reportName);

          const executePage = new ExecutePage();
          executePage.verifyTitle(reportName);
          analysisId = executePage.getAnalysisId();
          executePage.clickOnActionLink();
          executePage.clickOnDetails();
          executePage.verifyDescription(reportDescription);
          executePage.closeActionMenu();
          // Delete the report
          executePage.clickOnActionLink();
          executePage.clickOnDelete();
          executePage.confirmDelete();
          analyzePage.verifyToastMessagePresent('Analysis deleted.');
          analyzePage.verifyAnalysisDeleted();
        } catch (e) {
          console.log(e);
        }
      }).result.testInfo = {
        testId: id,
        data: data,
        feature: 'CREATEREPORT',
        dataProvider: 'dlreport'
      };
    }
  );

  using(
    testDataReader.testData['CREATEREPORT']['esreport']
      ? testDataReader.testData['CREATEREPORT']['esreport']
      : {},
    (data, id) => {
      it(`${id}:${data.description}`, () => {
        try {
          const reportName = `e2e dl report ${new Date().toString()}`;
          console.log(reportName);
          const reportDescription = `e2e dl report description ${new Date().toString()}`;
          const analysisType = 'table:report';
          const tables = data.tables;
          const loginPage = new LoginPage();
          loginPage.loginAs(data.user, /analyze/);

          const analyzePage = new AnalyzePage();
          analyzePage.goToView('card');
          analyzePage.clickOnAddAnalysisButton();
          analyzePage.clickOnAnalysisType(analysisType);
          analyzePage.clickOnNextButton();
          analyzePage.clickOnDataPods(dataSets.pivotChart);
          analyzePage.clickOnCreateButton();

          const designerPage = new DesignerPage();
          designerPage.clickOnReportFields(tables);
          // Verify that all the columns are displayed
          designerPage.verifyDisplayedColumns(tables);
          designerPage.clickOnSave();
          designerPage.enterAnalysisName(reportName);
          designerPage.enterAnalysisDescription(reportDescription);
          designerPage.clickOnSaveAndCloseDialogButton(/analyze/);
          // Verify analysis displayed in list and card view
          analyzePage.goToView('list');
          analyzePage.verifyElementPresent(
            analyzePage._analysisTitleLink(reportName),
            true,
            'report should be present in list/card view'
          );
          analyzePage.goToView('card');
          // Go to detail page and very details
          analyzePage.clickOnAnalysisLink(reportName);

          const executePage = new ExecutePage();
          executePage.verifyTitle(reportName);
          analysisId = executePage.getAnalysisId();
          executePage.clickOnActionLink();
          executePage.clickOnDetails();
          executePage.verifyDescription(reportDescription);
          executePage.closeActionMenu();
          // Delete the report
          executePage.clickOnActionLink();
          executePage.clickOnDelete();
          executePage.confirmDelete();
          analyzePage.verifyToastMessagePresent('Analysis deleted.');
          analyzePage.verifyAnalysisDeleted();
        } catch (e) {
          console.log(e);
        }
      }).result.testInfo = {
        testId: id,
        data: data,
        feature: 'CREATEREPORT',
        dataProvider: 'esreport'
      };
    }
  );
});
