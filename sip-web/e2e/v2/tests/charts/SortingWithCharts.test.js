const using = require('jasmine-data-provider');
const testDataReader = require('../../testdata/testDataReader.js');
const protractorConf = require('../../conf/protractor.conf');
const logger = require('../../conf/logger')(__filename);
const categories = require('../../helpers/data-generation/categories');
const subCategories = require('../../helpers/data-generation/subCategories');
const commonFunctions = require('../../pages/utils/commonFunctions');
const Constants = require('../../helpers/Constants');
const assert = require('chai').assert;
const dataSets = require('../../helpers/data-generation/datasets');

let AnalysisHelper = require('../../helpers/api/AnalysisHelper');
let APICommonHelpers = require('../../helpers/api/APICommonHelpers');

const LoginPage = require('../../pages/LoginPage');
const AnalyzePage = require('../../pages/AnalyzePage');
const ChartDesignerPage = require('../../pages/ChartDesignerPage');
const ExecutePage = require('../../pages/ExecutePage');
const Header = require('../../pages/components/Header');
const users = require('../../helpers/data-generation/users');
/**
 * LIMIT does't work with group by and more than one metric field
 */
describe('Executing Sorting for charts tests from charts/SortingWithCharts.test.js', () => {
  let analysisId;
  let host;
  let token;
  const metricName = dataSets.pivotChart;
  const metrics = 'Integer';
  const dimension = 'Date';
  const groupName = 'String';
  const sizeByName = 'Float';
  beforeAll(() => {
    logger.info('Starting charts/SortingWithCharts.test.js.....');
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
    testDataReader.testData['SORTING_CHARTS']['sort_asc']
      ? testDataReader.testData['SORTING_CHARTS']['sort_asc']
      : {},
    (data, id) => {
      it(`${id}:${data.description}`, () => {
        logger.info(`Executing test case with id: ${id}`);
        const now = new Date().getTime();
        const chartName = `e2e ${now}`;
        const chartDescription = `e2e chart description ${now}`;

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

        // Apply sorting starts
        chartDesignerPage.clickOnSortButton();
        chartDesignerPage.clickOnAscSortButtonByField(dimension);
        chartDesignerPage.clickOnApplySortButton();
        // Apply sorting ends
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
          'analysis should be present in list/card view'
        );
        analyzePage.goToView('card');
        // Go to detail page and very details
        analyzePage.clickOnAnalysisLink(chartName);

        const executePage = new ExecutePage();
        executePage.verifyTitle(chartName);
        analysisId = executePage.getAnalysisId();
        executePage.clickOnEditLink();
        chartDesignerPage.clickOnSortButton();
        // Verify sort options are applied
        chartDesignerPage.verifySortOptionsApplied([dimension], 'asc');
        chartDesignerPage.clickOnApplySortButton();
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
        feature: 'SORTING_CHARTS',
        dataProvider: 'sort_asc'
      };
    }
  );
});
