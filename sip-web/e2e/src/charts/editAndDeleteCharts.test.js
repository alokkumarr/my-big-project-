const testDataReader = require('../testdata/testDataReader.js');
const using = require('jasmine-data-provider');
const commonFunctions = require('../../v2/pages/utils/commonFunctions');
const protractorConf = require('../../protractor.conf');
const categories = require('../../v2/helpers/data-generation/categories');
const subCategories = require('../../v2/helpers/data-generation/subCategories');

const ChartDesignerPage = require('../../v2/pages/ChartDesignerPage');
const ExecutePage = require('../../v2/pages/ExecutePage');
const LoginPage = require('../../v2/pages/LoginPage');
const AnalyzePage = require('../../v2/pages/AnalyzePage');
const Header = require('../../v2/pages/components/Header');

const AnalysisHelper = require('../../v2/helpers/api/AnalysisHelper');
const APICommonHelpers = require('../../v2/helpers/api/APICommonHelpers');
const logger = require('../../v2/conf/logger')(__filename);

const Constants = require('../../v2/helpers/Constants');
const assert = require('chai').assert;

describe('Edit and delete charts: editAndDeleteCharts.test.js', () => {
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
    jasmine.DEFAULT_TIMEOUT_INTERVAL =
      protractorConf.timeouts.extendedDefaultTimeoutInterval;
  });

  beforeEach(done => {
    setTimeout(() => {
      done();
    }, protractorConf.timeouts.pageResolveTimeout);
  });

  afterEach(done => {
    setTimeout(function() {
      if (analysisId) {
        new AnalysisHelper().deleteAnalysis(
          host,
          token,
          analysisId
        );
      }
      if (editedAnalysisId) {
        new AnalysisHelper().deleteAnalysis(
          host,
          token,
          editedAnalysisId
        );
      }
      commonFunctions.clearLocalStorage();
      done();
    }, protractorConf.timeouts.pageResolveTimeout);
  });
  using(
    testDataReader.testData['EDITDELETECHARTS']['editDeleteChartsDataProvider'],
    (data, description) => {
      it(
        'should edit and delete ' +
          description +
          ' testDataMetaInfo: ' +
          JSON.stringify({
            test: description,
            feature: 'EDITDELETECHARTS',
            dp: 'editDeleteChartsDataProvider'
          }),
        () => {
          try {
            const chartName = `e2e chart ${new Date().toString()}`;
            const chartDescription = `e2e chart description ${new Date().toString()}`;

            const type = data.chartType.split(':')[1];
            if (!token) {
              logger.error('token cannot be null');
              expect(token).toBeTruthy();
              assert.isNotNull(token, 'token cannot be null');
            }

            // Create new analysis.
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
            executePage.clickOnEditLink();

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
            const updatedName = chartName + ' updated';
            const updatedDescription = chartDescription + 'updated';
            // Save
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
          } catch (e) {
            logger.error(e);
          }
        }
      );
    }
  );
});
