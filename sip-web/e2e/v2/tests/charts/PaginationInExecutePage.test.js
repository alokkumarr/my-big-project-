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
const Constants = require('../../helpers/Constants');

describe('Executing create and delete chart tests from charts/PaginationInExecutePage.test.js', () => {
  let analysisId;
  let host;
  let token;
  const metricName = dataSets.pivotChart;
  const metrics = 'Integer';
  const dimension = 'Date';
  const yAxisName2 = 'Long';
  const groupName = 'String';
  const sizeByName = 'Float';

  beforeAll(() => {
    logger.info('Starting charts/PaginationInExecutePage.test.js.....');
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
          analysisId,
          Constants.CHART
        );
      }
      // Logout by clearing the storage
      commonFunctions.clearLocalStorage();
      done();
    }, protractorConf.timeouts.pageResolveTimeout);
  });

  using(
    testDataReader.testData['PAGINATION']['charts_execute']
      ? testDataReader.testData['PAGINATION']['charts_execute']
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

        if (data.chartType === 'chart:pie') {
          chartDesignerPage.clickOnAttribute(dimension, 'Color By');
          chartDesignerPage.clickOnAttribute(metrics, 'Angle');
        } else {
          chartDesignerPage.clickOnAttribute(dimension, 'Dimension');
          chartDesignerPage.clickOnAttribute(metrics, 'Metrics');
        }

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
        // Pagination section
        executePage.clickOnGridViewIcon();
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
        feature: 'PAGINATION',
        dataProvider: 'charts_execute'
      };
    }
  );
});
