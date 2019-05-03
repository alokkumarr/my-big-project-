const using = require('jasmine-data-provider');
const testDataReader = require('../../testdata/testDataReader.js');
const protractorConf = require('../../conf/protractor.conf');
const logger = require('../../conf/logger')(__filename);
const dataSets = require('../../helpers/data-generation/datasets');
const categories = require('../../helpers/data-generation/categories');
const subCategories = require('../../helpers/data-generation/subCategories')
  .createSubCategories;
const Constants = require('../../helpers/Constants');
const globalVariables = require('../../helpers/data-generation/globalVariables');
const commonFunctions = require('../../pages/utils/commonFunctions');

let AnalysisHelper = require('../../helpers/api/AnalysisHelper');
let APICommonHelpers = require('../../helpers/api/APICommonHelpers');

const LoginPage = require('../../pages/LoginPage');
const AnalyzePage = require('../../pages/AnalyzePage');
const Header = require('../../pages/components/Header');
const ReportDesignerPage = require('../../pages/ReportDesignerPage');
const ExecutePage = require('../../pages/ExecutePage');
const ChartDesignerPage = require('../../pages/ChartDesignerPage');

describe('Executing pivotPromptFilters tests from pivotPromptFilters.test.js', () => {
  const categoryName = categories.analyses.name;
  const subCategoryName = subCategories.createAnalysis.name;
  const savedCategory = 'My Analysis';
  const savedSubCategory = 'DRAFTS';

  let analysisId;
  let host;
  let token;
  let editedAnalysisId;
  beforeAll(() => {
    logger.info('Starting pivotPromptFilters tests...');
    host = APICommonHelpers.getApiUrl(browser.baseUrl);
    token = APICommonHelpers.generateToken(host);
    jasmine.DEFAULT_TIMEOUT_INTERVAL = protractorConf.timeouts.timeoutInterval;
  });

  beforeEach(done => {
    setTimeout(() => {
      done();
    }, protractorConf.timeouts.pageResolveTimeout);
  });

  afterEach(done => {
    setTimeout(() => {
      const analyses = [];
      if (editedAnalysisId) {
        analyses.push(editedAnalysisId);
      }
      if (analysisId) {
        analyses.push(analysisId);
      }
      analyses.forEach(id => {
        logger.warn('delete ' + id);
        new AnalysisHelper().deleteAnalysis(
          host,
          token,
          protractorConf.config.customerCode,
          id
        );
      });

      // Logout by clearing the storage
      commonFunctions.clearLocalStorage();
      done();
    }, protractorConf.timeouts.pageResolveTimeout);
  });

  using(
    testDataReader.testData['PIVOT_PROMPT_FILTER']['positiveTests']
      ? testDataReader.testData['PIVOT_PROMPT_FILTER']['positiveTests']
      : {},
    (data, id) => {
      it(`${id}:${data.description}`, () => {
        logger.info(`Executing test case with id: ${id}`);
        try {
          if (!token) {
            logger.error('token cannot be null');
            expect(token).toBeTruthy();
            assert.isNotNull(token, 'token cannot be null');
          }
          let currentTime = new Date().getTime();
          let filters;
          if (data.fieldName !== 'Date') {
            filters = [
              {
                field: data.fieldName,
                displayedValue: data.operator + ' ' + data.value
              }
            ];
          } else {
            filters = [
              {
                field: data.fieldName,
                displayedValue: data.displayedValue
              }
            ];
          }

          let name =
            Constants.PIVOT + ' ' + globalVariables.e2eId + '-' + currentTime;
          let description =
            'Description:' +
            Constants.PIVOT +
            ' for e2e ' +
            globalVariables.e2eId +
            '-' +
            currentTime;
          let analysisType = Constants.PIVOT;
          //Create new analysis.
          let analysis = new AnalysisHelper().createNewAnalysis(
            host,
            token,
            name,
            description,
            analysisType,
            null
          );
          expect(analysis).toBeTruthy();
          analysisId = analysis.contents.analyze[0].executionId.split('::')[0];
          const loginPage = new LoginPage();
          loginPage.loginAs(data.user, /analyze/);
          const header = new Header();
          header.openCategoryMenu();
          header.selectCategory(categoryName);
          header.selectSubCategory(subCategoryName);
          const analysisPage = new AnalyzePage();
          analysisPage.clickOnAnalysisLink(name);
          const executePage = new ExecutePage();

          executePage.clickOnEditLink();
          const chartDesignerPage = new ChartDesignerPage();
          chartDesignerPage.clickOnFilterButton();
          if (analysisType === Constants.REPORT) {
            chartDesignerPage.clickOnAddFilterButtonByTableName('SALES');
          } else {
            chartDesignerPage.clickOnAddFilterButtonByTableName('sample');
          }

          chartDesignerPage.clickOnColumnInput();
          chartDesignerPage.clickOnColumnDropDown(data.fieldName);
          chartDesignerPage.clickOnPromptCheckBox();
          chartDesignerPage.clickOnApplyFilterButton();
          chartDesignerPage.validateAppliedFilters(analysisType, [
            data.fieldName
          ]);
          chartDesignerPage.clickOnSave();
          chartDesignerPage.clickOnSaveAndCloseDialogButton();

          // From analysis detail/view page
          commonFunctions.goToHome();
          header.openCategoryMenu();
          header.selectCategory(savedCategory);
          header.selectSubCategory(savedSubCategory);
          analysisPage.goToView('card');
          analysisPage.clickOnAnalysisLink(name);
          chartDesignerPage.shouldFilterDialogPresent();
          chartDesignerPage.clickOnCancelFilterModelButton();
          executePage.clickOnActionLink();
          executePage.clickOnExecuteButton();
          chartDesignerPage.shouldFilterDialogPresent();
          chartDesignerPage.verifySelectFieldValue(data.fieldName);

          chartDesignerPage.fillFilterOptions(
            data.fieldType,
            data.operator,
            data.value
          );

          chartDesignerPage.clickOnApplyFilterButton();
          executePage.verifyAppliedFilter(filters);
          //get analysis id from current url
          browser.getCurrentUrl().then(url => {
            editedAnalysisId = commonFunctions.getAnalysisIdFromUrl(url);
          });
          // VerifyPromptFromListView and by executing from action menu
          commonFunctions.goToHome();
          header.openCategoryMenu();
          header.selectCategory(savedCategory);
          header.selectSubCategory(savedSubCategory);
          analysisPage.goToView('list');
          analysisPage.clickOnActionLinkByAnalysisName(name);
          analysisPage.clickOnExecuteButtonAnalyzePage();
          chartDesignerPage.shouldFilterDialogPresent();
          chartDesignerPage.verifySelectFieldValue(data.fieldName);
          chartDesignerPage.fillFilterOptions(
            data.fieldType,
            data.operator,
            data.value
          );
          chartDesignerPage.clickOnApplyFilterButton();
          executePage.verifyAppliedFilter(filters);
          // VerifyPromptFromCardView and by executing from action menu
          commonFunctions.goToHome();
          header.openCategoryMenu();
          header.selectCategory(savedCategory);
          header.selectSubCategory(savedSubCategory);
          analysisPage.goToView('card');
          analysisPage.clickOnActionLinkByAnalysisName(name);
          analysisPage.clickOnExecuteButtonAnalyzePage();
          chartDesignerPage.shouldFilterDialogPresent();
          chartDesignerPage.verifySelectFieldValue(data.fieldName);
          chartDesignerPage.fillFilterOptions(
            data.fieldType,
            data.operator,
            data.value
          );
          chartDesignerPage.clickOnApplyFilterButton();
          //header.clickOnToastMessage();
          executePage.verifyAppliedFilter(filters);
        } catch (e) {
          console.error(e);
        }
      }).result.testInfo = {
        testId: id,
        data: data,
        feature: 'PIVOT_PROMPT_FILTER',
        dataProvider: 'positiveTests'
      };
    }
  );
});
