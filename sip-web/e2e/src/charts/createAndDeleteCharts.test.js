const testDataReader = require('../testdata/testDataReader.js');
const using = require('jasmine-data-provider');
const commonFunctions = require('../../v2/pages/utils/commonFunctions');
const protractorConf = require('../../protractor.conf');
const dataSets = require('../javascript/data/datasets');
const ChartDesignerPage = require('../../v2/pages/ChartDesignerPage');
const ExecutePage = require('../../v2/pages/ExecutePage');
const LoginPage = require('../../v2/pages/LoginPage');
const AnalyzePage = require('../../v2/pages/AnalyzePage');
const AnalysisHelper = require('../../v2/helpers/api/AnalysisHelper');
const APICommonHelpers = require('../../v2/helpers/api/APICommonHelpers');
const logger = require('../../v2/conf/logger')(__filename);

describe('Create and delete charts: createAndDeleteCharts.test.js', () => {
  let analysisId;
  let host;
  let token;
  const yAxisName = 'Double';
  const xAxisName = 'Date';
  const groupName = 'String';
  const metricName = dataSets.pivotChart;
  const sizeByName = 'Float';
  const yAxisName2 = 'Long';

  beforeAll(() => {
    logger.info('Starting charts/createAndDelete.test.js.....');
    host = APICommonHelpers.getApiUrl(browser.baseUrl);
    token = APICommonHelpers.generateToken(host);
    jasmine.DEFAULT_TIMEOUT_INTERVAL =
      protractorConf.timeouts.extendedDefaultTimeoutInterval;
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
          analysisId
        );
      }
      // Logout by clearing the storage
      commonFunctions.clearLocalStorage();
      done();
    }, protractorConf.timeouts.pageResolveTimeout);
  });
  using(
    testDataReader.testData['CREATEDELETECHART'][
      'createDeleteChartDataProvider'
    ],
    (data, description) => {
      it(
        'should create and delete ' +
          description +
          ' testDataMetaInfo: ' +
          JSON.stringify({
            test: description,
            feature: 'CREATEDELETECHART',
            dp: 'createDeleteChartDataProvider'
          }),
        () => {
          try {
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
            // Save
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
          } catch (e) {
            console.log(e);
          }
        }
      );
    }
  );
});
