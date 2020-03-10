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
            Constants.PIVOT
          );
        }
      });
      commonFunctions.clearLocalStorage();
      done();
    }, protractorConf.timeouts.pageResolveTimeout);
  });

  using(
    testDataReader.testData['PUBLISH-REPORT']['pivot']
      ? testDataReader.testData['PUBLISH-REPORT']['pivot']
      : {},
    (data, id) => {
      it(`${id}:${data.description}`, async () => {
        try {
          logger.info(`Executing test case with id: ${id}`);
          const now = new Date().getTime();
          const pivotName = `e2e pvt${now}`;
          const pivotDescription = `e2e pivot description ${now}`;
          let analysis = new AnalysisHelper().createNewAnalysis(
            host,
            token,
            pivotName,
            pivotDescription,
            Constants.PIVOT,
            null, // No subtype of Pivot.
            null
          );
          expect(analysis).toBeTruthy();
          assert.isNotNull(analysis, 'analysis cannot be null');
          analysesDetails.push(analysis);

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
          analyzePage.clickOnActionLinkByAnalysisName(pivotName);
          executePage.publishAnalysis(editSubCategoryName);
          analyzePage.verifyToastMessagePresent(data.editMessage);
          header.doLogout();

          //Login as different User
          loginPage.loginAs(data.user, /analyze/);
          header.openCategoryMenu();
          header.selectCategory(editCategoryName);
          header.selectSubCategory(editSubCategoryName);
          analyzePage.clickOnAnalysisLink(pivotName);
          executePage.clickOnEditLink();
          chartDesignerPage.searchInputPresent();
          chartDesignerPage.clearAttributeSelection();
          chartDesignerPage.clickOnAttribute(data.fieldName1, data.fieldValue1);
          chartDesignerPage.clickOnAttribute(data.fieldName2, data.fieldValue2);

           // Save pivot
          const updatedName = pivotName + ' uptd';
          const updatedDescription = pivotDescription + 'updated';
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
          chartDesignerPage.verifyPivotFields(data.fieldValue1,data.fieldName1);
          chartDesignerPage.verifyPivotFields(data.fieldValue2,data.fieldName2);
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
        dataProvider: 'pivot'
      };
    }
  );
});

