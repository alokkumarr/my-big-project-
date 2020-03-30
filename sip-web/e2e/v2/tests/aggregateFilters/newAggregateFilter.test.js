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
const users = require('../../helpers/data-generation/users');

describe('Executing Aggregate for es report tests from reports/AggregateWithESReport.test.js', () => {
  let analysisId;
  let host;
  let token;
  beforeAll(() => {
    logger.info('Starting reports/AggregateWithESReport.test.js.....');
    host = APICommonHelpers.getApiUrl(browser.baseUrl);
    token = APICommonHelpers.generateToken(
      host,
      users.admin.loginId,
      users.anyUser.password
    );
    jasmine.DEFAULT_TIMEOUT_INTERVAL = protractorConf.timeouts.timeoutInterval;
  });

  beforeEach(done => {
    setTimeout(() => {
      done();
    }, protractorConf.timeouts.pageResolveTimeout);
  });

  afterEach(function(done) {
    setTimeout(function() {
      if (analysisId) {
        new AnalysisHelper().deleteAnalysis(
          host,
          token,
          protractorConf.config.customerCode,
          analysisId
        );
      }
      commonFunctions.clearLocalStorage();
      done();
    }, protractorConf.timeouts.pageResolveTimeout);
  });

  using(
    testDataReader.testData['NEWAGGREGATE_FILTER']['esReport']
      ? testDataReader.testData['NEWAGGREGATE_FILTER']['esReport']
      : {},
    (data, id) => {
      it(`${id}:${data.description}`, () => {
        logger.info(`Executing test case with id: ${id}`);
        const now = new Date().getTime();
        const reportName = `e2e ${now}`;
        const reportDescription = `e2e es report description ${now}`;
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
        // Apply aggregation
        reportDesignerPage.applyAggregate(data.aggregate);
        reportDesignerPage.applyNewAggregateFilter(data.aggregatePopup);
        reportDesignerPage.verifyAppliedAggregateFilter(data.expectedAggregateValue);
        if(data.removeAggregate) {
          reportDesignerPage.removeAggregateAndVerify(data.expectedAggregateValue);
        }
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
        // Verify the aggregations displayed in execute page
        executePage.aggregationVerification(data.aggregate.value);
        // Delete the report
        executePage.clickOnActionLink();
        executePage.clickOnDelete();
        executePage.confirmDelete();
        analyzePage.verifyToastMessagePresent('Analysis deleted.');
        analyzePage.verifyAnalysisDeleted();
      }).result.testInfo = {
        testId: id,
        data: data,
        feature: 'NEWAGGREGATE_FILTER',
        dataProvider: 'esReport'
      };
    }
  );
});
