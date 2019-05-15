const testDataReader = require('../testdata/testDataReader.js');
const using = require('jasmine-data-provider');
const commonFunctions = require('../../v2/pages/utils/commonFunctions');
const protractorConf = require('../../protractor.conf');
const dataSets = require('../javascript/data/datasets');
const ChartDesignerPage = require('../../v2/pages/ChartDesignerPage');
const LoginPage = require('../../v2/pages/LoginPage');
const AnalyzePage = require('../../v2/pages/AnalyzePage');
const PreviewPage = require('../../v2/pages/PreviewPage');
const logger = require('../../v2/conf/logger')(__filename);

describe('Verify preview for charts: previewForCharts.test.js', () => {
  const yAxisName = 'Double';
  const xAxisName = 'Date';
  const groupName = 'String';
  const metricName = dataSets.pivotChart;
  const sizeByName = 'Float';
  const yAxisName2 = 'Long';
  beforeAll(() => {
    logger.info('Starting charts/previewForCharts.test.js.....');

    protractorConf.timeouts.extendedDefaultTimeoutInterval;
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
    testDataReader.testData['PREVIEWCHARTS']['previewChartsDataProvider'],
    function(data, description) {
      it(
        'should verify preview for ' +
          description +
          ' testDataMetaInfo: ' +
          JSON.stringify({
            test: description,
            feature: 'PREVIEWCHARTS',
            dp: 'previewChartsDataProvider'
          }),
        () => {
          try {
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
            chartDesignerPage.clickOnPreviewButton();

            const previewPage = new PreviewPage();
            previewPage.verifyAxisTitle(chartType, yAxisName, 'yaxis');
            previewPage.verifyAxisTitle(chartType, xAxisName, 'xaxis');
          } catch (e) {
            console.log(e);
          }
        }
      );
    }
  );
});
