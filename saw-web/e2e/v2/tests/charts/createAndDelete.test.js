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
const ChartDesignerPage = require('../../pages/ChartDesignerPage');
const ExecutePage = require('../../pages/ExecutePage');

describe('Executing create and delete chart tests from charts/createAndDelete.test.js', () => {
  let analysisId;
  let host;
  let token;
  const yAxisName = 'Double';
  const xAxisName = 'Date';
  const groupName = 'String';
  const metricName = dataSets.pivotChart;
  const sizeByName = 'Float';
  beforeAll(() => {
    logger.info('Starting charts/createAndDelete.test.js.....');
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
    testDataReader.testData['CREATEDELETECHART']['charts']
      ? testDataReader.testData['CREATEDELETECHART']['charts']
      : {},
    (data, id) => {
      it(`${id}:${data.description}`, () => {
        logger.info(`Executing test case with id: ${id}`);
        const chartName = `e2e chart ${new Date().toString()}`;
        const chartDescription = `e2e chart description ${new Date().toString()}`;

        const loginPage = new LoginPage();
        loginPage.loginAs(data.user, /analyze/);

        const analyzePage = new AnalyzePage();
        analyzePage.clickOnAddAnalysisButton();
        analyzePage.clickOnChartType(data.chartType);
        analyzePage.clickOnNextButton();
        analyzePage.clickOnDataPods(metricName);
        analyzePage.clickOnCreateButton();

        const chartDesignerPage = new ChartDesignerPage();
        chartDesignerPage.searchInputPresent();
        chartDesignerPage.clickOnAttribute(xAxisName, 'Dimension');
        chartDesignerPage.clickOnAttribute(yAxisName, 'Metrics');

        if (data.chartType === 'chart:bubble') {
          chartDesignerPage.clickOnAttribute(sizeByName, 'Size');
          chartDesignerPage.clickOnAttribute(groupName, 'Color By');
        }
        // If Combo then add one more metric field
        if (data.chartType === 'chart:combo') {
          chartDesignerPage.clickOnAttribute(yAxisName2, 'Metrics');
        } else if (data.chartType !== 'chart:bubble') {
          chartDesignerPage.clickOnAttribute(groupName, 'Group By');
        }
        //Save
        chartDesignerPage.clickOnSave();
        chartDesignerPage.enterAnalysisName(chartName);
        chartDesignerPage.enterAnalysisDescription(chartDescription);
        chartDesignerPage.clickOnSaveAndCloseDialogButton(/analyze/);

        // Verify analysis displayed in list and card view
        analyzePage.goToView('list');
        analyzePage.verifyElementPresent(
          analyzePage._analysisTitleLink(chartName),
          true,
          'report should be present in list/card view'
        );
        analyzePage.goToView('card');
        // Go to detail page and very details
        analyzePage.clickOnAnalysisLink(chartName);

        const executePage = new ExecutePage();
        executePage.verifyTitle(chartName);
        analysisId = executePage.getAnalysisId();

        executePage.clickOnActionLink();
        executePage.clickOnDetails();
        executePage.verifyDescription(chartDescription);
        executePage.closeActionMenu();
        // Delete the report
        executePage.clickOnActionLink();
        executePage.clickOnDelete();
        executePage.confirmDelete();
        analyzePage.verifyToastMessagePresent('Analysis deleted.');
        analyzePage.verifyAnalysisDeleted();
      }).result.testInfo = {
        testId: id,
        data: data,
        feature: 'CREATEDELETECHART',
        dataProvider: 'charts'
      };
    }
  );
});
