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
const ReportDesignerPage = require('../../pages/ReportDesignerPage');
const ExecutePage = require('../../pages/ExecutePage');

describe('Executing DLReportQuery tests from DLReportQuery.test.js', () => {
  let analysisId;
  let host;
  let token;
  beforeAll(() => {
    logger.info('Starting DLReportQuery.test.jseport tests...');
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
      console.log({ analysisId });
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
    testDataReader.testData['CREATEREPORT']['dlreportquery']
      ? testDataReader.testData['CREATEREPORT']['dlreportquery']
      : {},
    (data, id) => {
      it(`${id}:${data.description}`, () => {
        try {
          const now = new Date().getTime();
          const reportName = `e2e ${now}`;
          const reportDescription = `e2e dl report description ${now}`;
          const analysisType = 'table:report';
          const tables = data.tables;
          const loginPage = new LoginPage();
          loginPage.loginAs(data.user, /analyze/);

          const analyzePage = new AnalyzePage();
          analyzePage.goToDesignerPage('card', analysisType, dataSets.report);

          const reportDesignerPage = new ReportDesignerPage();
          reportDesignerPage.clickOnQueryTab();
          reportDesignerPage.fillQuery('select * from SALES limit 5');
          reportDesignerPage.clickOnQuerySubmitButton();
          reportDesignerPage.clickOnConfirmButton();
          reportDesignerPage.verifyRowsDisplayed(5);
          reportDesignerPage.clickOnSave();
          reportDesignerPage.enterAnalysisName(reportName);
          reportDesignerPage.enterAnalysisDescription(reportDescription);
          reportDesignerPage.clickOnSaveAndCloseDialogButton(/analyze/);

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

          executePage.getAnalysisId().then(id => {
            analysisId = id;
          });

          executePage.verifyAnalysisDetailsAndDelete(
            reportName,
            reportDescription
          );
          analyzePage.verifyToastMessagePresent('Analysis deleted.');
          analyzePage.verifyAnalysisDeleted();
        } catch (e) {
          console.log(e);
        }
      }).result.testInfo = {
        testId: id,
        data: data,
        feature: 'CREATEREPORT',
        dataProvider: 'dlreportquery'
      };
    }
  );
});
