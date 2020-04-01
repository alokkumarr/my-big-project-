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
const ReportDesignerPage = require('../../pages/ReportDesignerPage');
const Header = require('../../pages/components/Header');
const LoginPage = require('../../pages/LoginPage');
const AnalyzePage = require('../../pages/AnalyzePage');
const ExecutePage = require('../../pages/ExecutePage');
const users = require('../../helpers/data-generation/users');

describe('Executing Publish Functionality for Reports from list/Card/Details View for DL/ES Reports', () => {
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
            currentAnalysis.analysisId
          );
        }
      });
      commonFunctions.clearLocalStorage();
      done();
    }, protractorConf.timeouts.pageResolveTimeout);
  });

  using(
    testDataReader.testData['PUBLISH-REPORT']['report']
      ? testDataReader.testData['PUBLISH-REPORT']['report']
      : {},
    (data, id) => {
      it(`${id}:${data.description}`, async () => {
        logger.info(`Executing test case with id: ${id}`);
        const reportType = data.analysisType;
        const now = new Date().getTime();
        const analysisName = `e2e ${now}`;
        const analysisType = data.analysisType;
        const analysisDescription = `e2e DL/ES Report description ${new Date().toString()}`;
        let analysis = await new AnalysisHelper().createNewAnalysis(
          host,
          token,
          analysisName,
          analysisDescription,
          analysisType,
          null, // No subtype of Pivot.
          null
        );
        expect(analysis).toBeTruthy();
        assert.isNotNull(analysis, 'analysis cannot be null');
        analysesDetails.push(analysis);

        const header = new Header();
        const loginPage = new LoginPage();
        const analyzePage = new AnalyzePage();
        const executePage = new ExecutePage();
        const reportDesignerPage = new ReportDesignerPage();
        loginPage.loginAs(data.loginUser, /analyze/);
        header.goToSubCategory(categoryName,subCategoryName);

        //Publish Analysis from List/Card/Details View
        analyzePage.goToViewAndSelectAnalysis(data.publishFrom,analysisName);
        if(data.publishFrom ==="details") {
          analyzePage.clickOnActionMenu();
        }
        executePage.publishAnalysis(editSubCategoryName);
        analyzePage.verifyToastMessagePresent(data.loadMessage);
        analyzePage.verifyToastMessagePresent(data.editMessage);
        header.doLogout();

        //Login as different User
        loginPage.loginAs(data.modifyUser, /analyze/);
        header.goToSubCategory(editCategoryName,editSubCategoryName);
        analyzePage.goToViewAndSelectAnalysis(data.publishFrom,analysisName);
        executePage.clickOnEditLink();
        const updatedName = analysisName + 'uptd';
        const updatedDescription = analysisDescription + 'updated';
        reportDesignerPage.clickOnReportFields(data.tables);
        reportDesignerPage.clickOnSave();
        reportDesignerPage.enterAnalysisName(updatedName);
        reportDesignerPage.enterAnalysisDescription(updatedDescription);
        reportDesignerPage.clickOnSaveAndCloseDialogButton(/analyze/);
        analyzePage.clickOnActionLinkByAnalysisName(updatedName);
        executePage.publishAnalysis(subCategoryName);
        analyzePage.verifyToastMessagePresent(data.loadMessage);
        analyzePage.verifyToastMessagePresent(data.editMessage);
        header.doLogout();

        //login as original user
        loginPage.loginAs(data.loginUser, /analyze/);
        header.goToSubCategory(categoryName,subCategoryName);
        analyzePage.clickOnAnalysisLink(updatedName);
        reportDesignerPage.verifySelectedFieldsCount(data.totalColumns);
        executePage.getAnalysisId().then(id => {
          analysesDetails.push({ analysisId: id });
        });

        // Delete the report
        executePage.deleteAnalysis();
        analyzePage.verifyToastMessagePresent('Analysis deleted.');
        analyzePage .verifyAnalysisDeleted();
      }).result.testInfo = {
        testId: id,
        data: data,
        feature: 'PUBLISH-REPORT',
        dataProvider: 'report'
      };
    }
  );
});


