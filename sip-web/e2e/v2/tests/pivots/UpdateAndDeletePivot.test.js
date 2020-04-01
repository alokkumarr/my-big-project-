const using = require('jasmine-data-provider');
const testDataReader = require('../../testdata/testDataReader.js');
const protractorConf = require('../../conf/protractor.conf');
const logger = require('../../conf/logger')(__filename);
const commonFunctions = require('../../pages/utils/commonFunctions');
const chai = require('chai');
const assert = chai.assert;
let AnalysisHelper = require('../../helpers/api/AnalysisHelper');
let APICommonHelpers = require('../../helpers/api/APICommonHelpers');

const LoginPage = require('../../pages/LoginPage');
const AnalyzePage = require('../../pages/AnalyzePage');
const ChartDesignerPage = require('../../pages/ChartDesignerPage');
const ExecutePage = require('../../pages/ExecutePage');
const Constants = require('../../helpers/Constants');
const categories = require('../../helpers/data-generation/categories');
const subCategories = require('../../helpers/data-generation/subCategories');
const Header = require('../../pages/components/Header');
const users = require('../../helpers/data-generation/users');

describe('Executing update and delete tests for pivots from pivots/UpdateAndDeletePivot.test.js', () => {
  let analysesDetails = [];
  let host;
  let token;
  const categoryName = categories.analyses.name;
  const subCategoryName = subCategories.createSubCategories.createAnalysis.name;
  const dateFieldName = 'Date';
  const numberFieldName = 'Integer';
  beforeAll(() => {
    logger.info('Starting pivots/UpdateAndDeletePivot.test.js.....');
    host = APICommonHelpers.getApiUrl(browser.baseUrl);
    //token = APICommonHelpers.generateToken(host);
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
    testDataReader.testData['UPDATE_PIVOT']['updatePivot']
      ? testDataReader.testData['UPDATE_PIVOT']['updatePivot']
      : {},
    (data, id) => {
      it(`${id}:${data.description}`, () => {
        logger.info(`Executing test case with id: ${id}`);
        const now = new Date().getTime();
        const pivotName = `e2e ${now}`;
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

        const loginPage = new LoginPage();
        loginPage.loginAs(data.user, /analyze/);
        const header = new Header();
        header.openCategoryMenu();
        header.selectCategory(categoryName);
        header.selectSubCategory(subCategoryName);

        const analyzePage = new AnalyzePage();
        analyzePage.goToView('card');
        analyzePage.clickOnAnalysisLink(pivotName);

        const executePage = new ExecutePage();
        executePage.clickOnEditLink();

        const chartDesignerPage = new ChartDesignerPage();
        chartDesignerPage.searchInputPresent();
        chartDesignerPage.clearAttributeSelection();
        chartDesignerPage.clickOnAttribute(dateFieldName, 'Row');
        chartDesignerPage.clickOnAttribute(numberFieldName, 'Data');
        // Save the analysis
        const updatedName = pivotName + ' updated';
        const updatedDescription = pivotDescription + 'updated';
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
        executePage.getAnalysisId().then(id => {
          analysesDetails.push({ analysisId: id });
        });
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
        feature: 'UPDATE_PIVOT',
        dataProvider: 'updatePivot'
      };
    }
  );
});
