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

describe('Executing pagination in preview page for reports from reports/EsReportPaginationInPreviewPage.test.js', () => {
  let analysisId;
  let host;
  let token;
  beforeAll(() => {
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
    testDataReader.testData['PAGINATION_ES']['preview_page']
      ? testDataReader.testData['PAGINATION_ES']['preview_page']
      : {},
    (data, id) => {
      it(`${id}:${data.description}`, () => {
        logger.info(`Executing test case with id: ${id}`);
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

        const reportDesignerPage = new ReportDesignerPage();
        reportDesignerPage.clickOnReportFields(tables);
        // Verify that all the columns are displayed
        reportDesignerPage.verifyDisplayedColumns(tables);
        reportDesignerPage.clickOnPreviewButton();
        const executePage = new ExecutePage();
        executePage.getAnalysisId().then(id => {
          analysisId = id;
        });
        // Pagination section
        executePage.verifyPagination();
        executePage.verifyItemPerPage();
      }).result.testInfo = {
        testId: id,
        data: data,
        feature: 'PAGINATION_ES',
        dataProvider: 'preview_page'
      };
    }
  );
});
