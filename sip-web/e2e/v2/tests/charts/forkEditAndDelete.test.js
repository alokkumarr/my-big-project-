const using = require('jasmine-data-provider');
const testDataReader = require('../../testdata/testDataReader.js');
const protractorConf = require('../../conf/protractor.conf');
const logger = require('../../conf/logger')(__filename);
const categories = require('../../helpers/data-generation/categories');
const subCategories = require('../../helpers/data-generation/subCategories');
const commonFunctions = require('../../pages/utils/commonFunctions');
const Constants = require('../../helpers/Constants');
const assert = require('chai').assert;

let AnalysisHelper = require('../../helpers/api/AnalysisHelper');
let APICommonHelpers = require('../../helpers/api/APICommonHelpers');

const LoginPage = require('../../pages/LoginPage');
const AnalyzePage = require('../../pages/AnalyzePage');
const ChartDesignerPage = require('../../pages/ChartDesignerPage');
const ExecutePage = require('../../pages/ExecutePage');
const Header = require('../../pages/components/Header');

describe('Executing fork and edit and delete chart tests from charts/forkEditAndDelete.test.js', () => {
  const categoryName = categories.analyses.name;
  const subCategoryName = subCategories.createSubCategories.createAnalysis.name;

  //updated fields
  const metrics = 'Integer';
  const dimension = 'String';
  const yAxisName2 = 'Long';
  const groupName = 'Date';
  const sizeByName = 'Float';
  let analysisId;
  let forkedAnalysisId;
  let host;
  let token;
  beforeAll(() => {
    logger.info('Starting charts/editAndDelete.test.js.....');
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
      if (forkedAnalysisId) {
        new AnalysisHelper().deleteAnalysis(
          host,
          token,
          protractorConf.config.customerCode,
          forkedAnalysisId
        );
      }
      commonFunctions.clearLocalStorage();
      done();
    }, protractorConf.timeouts.pageResolveTimeout);
  });

  using(
    testDataReader.testData['FORKDELETECHARTS']['positiveTests']
      ? testDataReader.testData['FORKDELETECHARTS']['positiveTests']
      : {},
    (data, id) => {
      it(`${id}:${data.description}`, () => {
        logger.info(`Executing test case with id: ${id}`);
        const chartName = `e2e chart ${new Date().toString()}`;
        const chartDescription = `e2e chart description ${new Date().toString()}`;
        const type = data.chartType.split(':')[1];
        if (!token) {
          logger.error('token cannot be null');
          expect(token).toBeTruthy();
          assert.isNotNull(token, 'token cannot be null');
        }

        //Create new analysis.
        const analysis = new AnalysisHelper().createNewAnalysis(
          host,
          token,
          chartName,
          chartDescription,
          Constants.CHART,
          type
        );
        expect(analysis).toBeTruthy();
        assert.isNotNull(analysis, 'analysis should not be null');

        const loginPage = new LoginPage();
        loginPage.loginAs(data.user, /analyze/);

        const header = new Header();
        header.openCategoryMenu();
        header.selectCategory(categoryName);
        header.selectSubCategory(subCategoryName);

        const analyzePage = new AnalyzePage();
        analyzePage.goToView('card');
        analyzePage.clickOnAnalysisLink(chartName);

        const executePage = new ExecutePage();
        executePage.clickOnForkAndEditLink();

        const chartDesignerPage = new ChartDesignerPage();
        chartDesignerPage.searchInputPresent();
        chartDesignerPage.clearAttributeSelection();

        chartDesignerPage.clickOnAttribute(dimension, 'Dimension');
        chartDesignerPage.clickOnAttribute(metrics, 'Metrics');

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
        const forkedName = chartName + ' forked';
        const forkedDescription = chartDescription + 'forked';
        //Save
        chartDesignerPage.clickOnSave();
        chartDesignerPage.enterAnalysisName(forkedName);
        chartDesignerPage.enterAnalysisDescription(forkedDescription);
        chartDesignerPage.clickOnSaveAndCloseDialogButton(/analyze/);

        // Verify analysis displayed in list and card view
        analyzePage.goToView('list');
        analyzePage.verifyElementPresent(
          analyzePage._analysisTitleLink(forkedName),
          true,
          'report should be present in list/card view'
        );
        analyzePage.goToView('card');
        // Go to detail page and very details
        analyzePage.clickOnAnalysisLink(forkedName);

        executePage.verifyTitle(forkedName);
        forkedAnalysisId = executePage.getAnalysisId();

        executePage.clickOnActionLink();
        executePage.clickOnDetails();
        executePage.verifyDescription(forkedDescription);
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
        feature: 'FORKDELETECHARTS',
        dataProvider: 'positiveTests'
      };
    }
  );
});