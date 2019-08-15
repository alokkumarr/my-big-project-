const using = require('jasmine-data-provider');
const testDataReader = require('../../testdata/testDataReader.js');
const protractorConf = require('../../conf/protractor.conf');
const logger = require('../../conf/logger')(__filename);
const commonFunctions = require('../../pages/utils/commonFunctions');
const dataSets = require('../../helpers/data-generation/datasets');

let AnalysisHelper = require('../../helpers/api/AnalysisHelper');
let APICommonHelpers = require('../../helpers/api/APICommonHelpers');

const LoginPage = require('../../pages/LoginPage');
const AnalyzePage = require('../../pages/AnalyzePage');
const ChartDesignerPage = require('../../pages/ChartDesignerPage');
const ExecutePage = require('../../pages/ExecutePage');
const moment = require('moment');

describe('Executing Aggregate for pivots tests from pivots/PivotsWithAggregate.test.js', () => {
  let analysisId;
  let host;
  let token;
  const metricName = dataSets.pivotChart;
  const analysisType = 'table:pivot';
  const dateFieldName = 'Date';
  const numberFieldName = 'Integer';
  const stringFieldName = 'String';
  const selectedAggregate = 'sum';
  beforeAll(() => {
    logger.info('Starting pivots/PivotsWithAggregate.test.js.....');
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
    testDataReader.testData['AGGREGATE_PIVOTS']['aggr_pivots']
      ? testDataReader.testData['AGGREGATE_PIVOTS']['aggr_pivots']
      : {},
    (data, id) => {
      it(`${id}:${data.description}`, () => {
        logger.info(`Executing test case with id: ${id}`);
        const now = moment().format();
        const pivotName = `e2e pivot ${now}`;
        const pivotDescription = `e2e pivot description ${now}`;
        const loginPage = new LoginPage();
        loginPage.loginAs(data.user, /analyze/);

        const analyzePage = new AnalyzePage();
        analyzePage.clickOnAddAnalysisButton();
        analyzePage.clickOnAnalysisType(analysisType);
        analyzePage.clickOnNextButton();
        analyzePage.clickOnDataPods(metricName);
        analyzePage.clickOnCreateButton();

        const chartDesignerPage = new ChartDesignerPage();
        chartDesignerPage.searchInputPresent();
        chartDesignerPage.clickOnAttribute(dateFieldName, 'Row');
        chartDesignerPage.clickOnAttribute(numberFieldName, 'Data');
        chartDesignerPage.clickOnDataOptionsTab();
        chartDesignerPage.clickOnDataOptions(numberFieldName);
        chartDesignerPage.clickOnSelectAndChooseAggregate(selectedAggregate); // Open the aggregate-chooser component.
        chartDesignerPage.clickOnSelectAndChooseAggregate(data.aggregate.value); // Select the aggregate form the list.

        // Save the analysis
        chartDesignerPage.clickOnSave();
        chartDesignerPage.enterAnalysisName(pivotName);
        chartDesignerPage.enterAnalysisDescription(pivotDescription);
        chartDesignerPage.clickOnSaveAndCloseDialogButton(/analyze/);

        // Verify analysis displayed in list and card view
        analyzePage.goToView('list');
        analyzePage.verifyElementPresent(
          analyzePage._analysisTitleLink(pivotName),
          true,
          'analysis should be present in list/card view'
        );
        analyzePage.goToView('card');
        // Go to detail page and very details
        analyzePage.clickOnAnalysisLink(pivotName);

        const executePage = new ExecutePage();
        executePage.verifyTitle(pivotName);
        analysisId = executePage.getAnalysisId();
        executePage.clickOnEditLink();

        // Verify the selected aggregate.
        chartDesignerPage.validateSelectedAggregate(
          numberFieldName,
          data.aggregate.designerLabel,
          data.aggregate.value
        );
        chartDesignerPage.clickOnSave();
        chartDesignerPage.clickOnSaveAndCloseDialogButton(/analyze/);
        // Delete the analysis
        executePage.clickOnActionLink();
        executePage.clickOnDelete();
        executePage.confirmDelete();
        analyzePage.verifyToastMessagePresent('Analysis deleted.');
        analyzePage.verifyAnalysisDeleted();
      }).result.testInfo = {
        testId: id,
        data: data,
        feature: 'AGGREGATE_PIVOTS',
        dataProvider: 'aggr_pivots'
      };
    }
  );
});
