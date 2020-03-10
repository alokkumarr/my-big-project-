const using = require('jasmine-data-provider');
const testDataReader = require('../../testdata/testDataReader.js');
const protractorConf = require('../../conf/protractor.conf');
const logger = require('../../conf/logger')(__filename);
const commonFunctions = require('../../pages/utils/commonFunctions');
const categories = require('../../helpers/data-generation/categories');
const subCategories = require('../../helpers/data-generation/subCategories');
const Constants = require('../../helpers/Constants');

const chai = require('chai');
const assert = chai.assert;
let AnalysisHelper = require('../../helpers/api/AnalysisHelper');
let APICommonHelpers = require('../../helpers/api/APICommonHelpers');
const ChartDesignerPage = require('../../pages/ChartDesignerPage');
const Header = require('../../pages/components/Header');
const LoginPage = require('../../pages/LoginPage');
const AnalyzePage = require('../../pages/AnalyzePage');
const ExecutePage = require('../../pages/ExecutePage');
const users = require('../../helpers/data-generation/users');

describe('Executing Publish Funtionality from list View', () => {

  //updated fields
  const metrics = 'Integer';
  const dimension = 'Date';
  const yAxisName2 = 'Long';
  const groupName = 'String';
  const sizeByName = 'Float';

  let analysesDetails = [];
  let host;
  let token;
  const categoryName = categories.analyses.name;
  const subCategoryName = subCategories.createSubCategories.createAnalysis.name;
  const editCategoryName = categories.privileges.name;
  const editSubCategoryName = subCategories.subCategories.all.name;

  beforeAll(() => {
    logger.info('Starting publish reports.test.js.....');
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
      //Delete analysis
      analysesDetails.forEach(currentAnalysis => {
        if (currentAnalysis.analysisId) {
          new AnalysisHelper().deleteAnalysis(
            host,
            token,
            protractorConf.config.customerCode,
            currentAnalysis.analysisId,
            Constants.CHART
          );
        }
      });
      commonFunctions.clearLocalStorage();
      done();
    }, protractorConf.timeouts.pageResolveTimeout);
  });

  using(
    testDataReader.testData['PUBLISH-REPORT']['chart']
      ? testDataReader.testData['PUBLISH-REPORT']['chart']
      : {},
    (data, id) => {
      it(`${id}:${data.description}`, async () => {
        try {
          logger.info(`Executing test case with id: ${id}`);
          const now = new Date().getTime();
          const chartName = `e2e cht${now}`;
          const chartDescription = `e2e chart description ${now}`;
          const type = data.chartType.split(':')[1];
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
          analysesDetails.push(analysis.analysisId);

          const header = new Header();
          const loginPage = new LoginPage();
          loginPage.loginAs(data.user, /analyze/);
          const analyzePage = new AnalyzePage();
          header.openCategoryMenu();
          header.selectCategory(categoryName);
          header.selectSubCategory(subCategoryName);
          const executePage = new ExecutePage();
          const chartDesignerPage = new ChartDesignerPage();

          //Publish Analysis
          analyzePage.clickOnActionLinkByAnalysisName(chartName);
          executePage.publishAnalysis(editSubCategoryName);
          analyzePage.verifyToastMessagePresent(data.editMessage);
          header.doLogout();

          //Login as different User
          loginPage.loginAs(data.user, /analyze/);
          header.openCategoryMenu();
          header.selectCategory(editCategoryName);
          header.selectSubCategory(editSubCategoryName);
          analyzePage.clickOnAnalysisLink(chartName);
          executePage.clickOnEditLink();
          chartDesignerPage.searchInputPresent();
          chartDesignerPage.clearAttributeSelection();

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
          // Save the analysis
          const updatedName = chartName + 'uptd';
          const updatedDescription = chartDescription + 'updated';
          chartDesignerPage.clickOnSave();
          chartDesignerPage.enterAnalysisName(updatedName);
          chartDesignerPage.enterAnalysisDescription(updatedDescription);
          chartDesignerPage.clickOnSaveAndCloseDialogButton(/analyze/);
          analyzePage.clickOnActionLinkByAnalysisName(updatedName);
          executePage.publishAnalysis(subCategoryName);
          analyzePage.verifyToastMessagePresent(data.editMessage);
          header.doLogout();

          //login as original user
          loginPage.loginAs(data.user, /analyze/);
          header.openCategoryMenu();
          header.selectCategory(categoryName);
          header.selectSubCategory(subCategoryName);
          analyzePage.clickOnAnalysisLink(updatedName);
          chartDesignerPage.verifyFields(metrics);
          chartDesignerPage.verifyFields(dimension);
          executePage.verifyTitle(updatedName);
          executePage.getAnalysisId().then(id => {
            analysesDetails.push({ analysisId: id });
          });
          executePage.clickOnActionLink();
          executePage.clickOnDetails();
          executePage.verifyDescription(updatedDescription);
          executePage.closeActionMenu();

          // Delete the report
          executePage.deleteAnalysis();
          analyzePage.verifyToastMessagePresent('Analysis deleted.');
          analyzePage .verifyAnalysisDeleted();
        } catch (e) {
          console.log(e);
        }
      }).result.testInfo = {
        testId: id,
        data: data,
        feature: 'PUBLISH-REPORT',
        dataProvider: 'chart'
      };
    }
  );
});

