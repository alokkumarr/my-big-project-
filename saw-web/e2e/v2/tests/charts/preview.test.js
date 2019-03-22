const using = require('jasmine-data-provider');
const testDataReader = require('../../testdata/testDataReader.js');
const protractorConf = require('../../conf/protractor.conf');
const logger = require('../../conf/logger')(__filename);
const dataSets = require('../../helpers/data-generation/datasets');
const commonFunctions = require('../../pages/utils/commonFunctions');

const LoginPage = require('../../pages/LoginPage');
const AnalyzePage = require('../../pages/AnalyzePage');
const ChartDesignerPage = require('../../pages/ChartDesignerPage');
const PreviewPage = require('../../pages/PreviewPage');

describe('Executing preview tests cases from charts/preview.test.js', () => {
  const yAxisName = 'Double';
  const xAxisName = 'Date';
  const groupName = 'String';
  const metricName = dataSets.pivotChart;
  const sizeByName = 'Float';
  beforeAll(() => {
    logger.info('Starting charts/preview.test.js.....');
    jasmine.DEFAULT_TIMEOUT_INTERVAL = protractorConf.timeouts.timeoutInterval;
  });

  beforeEach(done => {
    setTimeout(() => {
      done();
    }, protractorConf.timeouts.pageResolveTimeout);
  });

  afterEach(done => {
    setTimeout(() => {
      // Logout by clearing the storage
      commonFunctions.clearLocalStorage();
      done();
    }, protractorConf.timeouts.pageResolveTimeout);
  });

  using(
    testDataReader.testData['PREVIEW']['charts']
      ? testDataReader.testData['PREVIEW']['charts']
      : {},
    (data, id) => {
      it(`${id}:${data.description}`, () => {
        logger.info(`Executing test case with id: ${id}`);
        const chartType = data.chartType.split(':')[1];
        const loginPage = new LoginPage();
        loginPage.loginAs(data.user, /analyze/);

        const analyzePage = new AnalyzePage();
        analyzePage.clickOnAddAnalysisButton();
        analyzePage.clickOnChartType(data.chartType);
        analyzePage.clickOnNextButton();
        analyzePage.clickOnDataPods(metricName);
        analyzePage.clickOnCreateButton();

        const chartDesignerPage = new ChartDesignerPage();
        // Dimension section.
        chartDesignerPage.clickOnAttribute(xAxisName);
        // Group by section. i.e. Color by
        chartDesignerPage.clickOnAttribute(groupName);
        // Metric section.
        chartDesignerPage.clickOnAttribute(yAxisName);
        // Size section.
        if (data.chartType === 'chart:bubble') {
          chartDesignerPage.clickOnAttribute(sizeByName);
        }
        // Click on Preview
        chartDesignerPage.clickOnPreviewButton();
        const previewPage = new PreviewPage();
        previewPage.verifyAxisTitle(chartType, yAxisName, 'yaxis');
        previewPage.verifyAxisTitle(chartType, xAxisName, 'xaxis');

      }).result.testInfo = {
        testId: id,
        data: data,
        feature: 'PREVIEW',
        dataProvider: 'charts'
      };
    }
  );
});
