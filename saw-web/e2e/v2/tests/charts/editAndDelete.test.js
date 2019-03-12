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

describe('Executing create and delete chart tests from charts/createAndDelete.test.js', () => {
  const categoryName = categories.analyses.name;
  const subCategoryName = subCategories.createSubCategories.createAnalysis.name;

  //updated fields
  const metrics = 'Integer';
  const dimension = 'String';
  const yAxisName2 = 'Long';
  const groupName = 'Date';
  const sizeByName = 'Float';
  let analysisId;
  let editedAnalysisId;
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
      if (editedAnalysisId) {
        new AnalysisHelper().deleteAnalysis(
          host,
          token,
          protractorConf.config.customerCode,
          editedAnalysisId
        );
      }
      commonFunctions.clearLocalStorage();
      done();
    }, protractorConf.timeouts.pageResolveTimeout);
  });

  using(
    testDataReader.testData['EDITELETECHART']['positiveTests']
      ? testDataReader.testData['EDITELETECHART']['positiveTests']
      : {},
    (data, id) => {
      it(`${id}:${data.description}`, () => {
        logger.info(`Executing test case with id: ${id}`);
        const chartName = `e2e chart ${new Date().toString()}`;
        const chartDescription = `e2e chart description ${new Date().toString()}`;
        let type = data.chartType.split(':')[1];
        if (!token) {
          logger.error('token cannot be null');
          expect(token).toBeTruthy();
          assert.isNotNull(token, 'token cannot be null');
        }

        //Create new analysis.
        let analysis = new AnalysisHelper().createNewAnalysis(
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
        executePage.clickOnEditLink();

        const chartDesignerPage = new ChartDesignerPage();
        chartDesignerPage.clearAttributeSelection();

        // Dimension section.
        chartDesignerPage.clickOnAttribute(dimension);
        // Group by section. i.e. Color by
        chartDesignerPage.clickOnAttribute(groupName);
        // Metric section.
        chartDesignerPage.clickOnAttribute(metrics);
        // Size section.
        if (data.chartType === 'chart:bubble') {
          chartDesignerPage.clickOnAttribute(sizeByName);
        }
        //If Combo then add one more field
        if (data.chartType === 'chart:combo') {
          chartDesignerPage.clickOnAttribute(yAxisName2);
        }
        let updatedName = chartName + ' updated';
        let updatedDescription = chartDescription + 'updated';
        //Save
        chartDesignerPage.clickOnSave();
        chartDesignerPage.enterAnalysisName(updatedName);
        chartDesignerPage.enterAnalysisDescription(updatedDescription);
        chartDesignerPage.clickOnSaveAndCloseDialogButton(/analyze/);

        // Verify analysis displayed in list and card view
        analyzePage.goToView('list');
        analyzePage.verifyElementPresent(
          analyzePage._analysisTitleLink(updatedName),
          true,
          'report should be present in list/card view'
        );
        analyzePage.goToView('card');
        // Go to detail page and very details
        analyzePage.clickOnAnalysisLink(updatedName);

        executePage.verifyTitle(updatedName);
        editedAnalysisId = executePage.getAnalysisId();

        executePage.clickOnActionLink();
        executePage.clickOnDetails();
        executePage.verifyDescription(updatedDescription);
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
        feature: 'EDITELETECHART',
        dataProvider: 'positiveTests'
      };
    }
  );
});
