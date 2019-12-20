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

describe('Executing pagination in execute page for reports from reports/DLReportPaginationInExecutePage.test.js', () => {
  let analysisId;
  let host;
  let token;
  beforeAll(() => {
    logger.info('Starting reports/DLReportPaginationInExecutePage tests...');
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
    testDataReader.testData['PAGINATION_DL']['execute_page']
      ? testDataReader.testData['PAGINATION_DL']['execute_page']
      : {},
    (data, id) => {
      it(`${id}:${data.description}`, () => {
        logger.info(`Executing test case with id: ${id}`);
        const now = new Date().getTime();
        const reportName = `e2e ${now}`;
        const reportDescription = `e2e dl report description ${now}`;
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

        const reportDesignerPage = new ReportDesignerPage();
        reportDesignerPage.clickOnReportFields(tables);
        reportDesignerPage.verifyDisplayedColumns(tables);
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
        executePage.verifyTitle(reportName);
        executePage.getAnalysisId().then(id => {
          analysisId = id;
        });
        // Pagination section
        executePage.verifyPagination();
        executePage.verifyItemPerPage();
        // Delete the report
        executePage.clickOnActionLink();
        executePage.clickOnDelete();
        executePage.confirmDelete();
        analyzePage.verifyToastMessagePresent('Analysis deleted.');
        analyzePage.verifyAnalysisDeleted();
      }).result.testInfo = {
        testId: id,
        data: data,
        feature: 'PAGINATION_DL',
        dataProvider: 'execute_page'
      };
    }
  );
});
