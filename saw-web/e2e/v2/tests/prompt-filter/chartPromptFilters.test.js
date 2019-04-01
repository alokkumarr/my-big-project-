const using = require('jasmine-data-provider');
const testDataReader = require('../../testdata/testDataReader.js');
const protractorConf = require('../../conf/protractor.conf');
const logger = require('../../conf/logger')(__filename);
const dataSets = require('../../helpers/data-generation/datasets');
const categories = require('../../helpers/data-generation/categories');
const subCategories = require('../../helpers/data-generation/subCategories').createSubCategories;
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
const ChartDesignerPage=require('../../pages/ChartDesignerPage');

describe('Executing chartPromptFilters tests from chartPromptFilters.test.js', () => {
  const defaultCategory = categories.privileges.name;
  const categoryName = categories.analyses.name;
  const subCategoryName = subCategories.createAnalysis.name;
  const savedCategory = 'My Analysis';
  const savedSubCategory = 'DRAFTS';

  let analysisId;
  let host;
  let token;
  beforeAll(() => {
    logger.info('Starting chartPromptFilters tests...');
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
      if (analysisId) {
        new AnalysisHelper().deleteAnalysis(
          host,
          token,
          protractorConf.config.customerCode,
          analysisId
        );
      }
      // Logout by clearing the storage
      commonFunctions.clearLocalStorage();
      done();
    }, protractorConf.timeouts.pageResolveTimeout);
  });

  using(
    testDataReader.testData['CHAT_PROMPT_FILTER']['positiveTests']
      ? testDataReader.testData['CHAT_PROMPT_FILTER']['positiveTests']
      : {},
    (data, id) => {
      it(`${id}:${data.description}`, () => {
        try {
          if (!token) {
            logger.error('token cannot be null');
            expect(token).toBeTruthy();
            assert.isNotNull(token, 'token cannot be null');
          }
          let currentTime = new Date().getTime();
          let user = data.user;
          let chartType = 'chart:column';
          let name =
            Constants.CHART + ' ' + globalVariables.e2eId + '-' + currentTime;
          let description =
            'Description:' +
            chartType +
            ' for e2e ' +
            globalVariables.e2eId +
            '-' +
            currentTime;
          let analysisType = Constants.CHART;
          let subType = chartType.split(':')[1];
          //Create new analysis.
          let analysis = new AnalysisHelper().createNewAnalysis(
            host,
            token,
            name,
            description,
            analysisType,
            subType
          );
          expect(analysis).toBeTruthy();
          const loginPage = new LoginPage();
          loginPage.loginAs(data.user, /analyze/);
            const header=new Header();
            header.openCategoryMenu();
            header.selectCategory(categoryName);
            header.selectSubCategory(subCategoryName);
          const analysisPage = new AnalyzePage();
          analysisPage.clickOnAnalysisLink(name);
          const executePage = new ExecutePage();
          executePage.clickOnEditLink();

          const chartDesignerPage = new ChartDesignerPage();
          chartDesignerPage.clickOnFilterButton();




        } catch (e) {
          logger.error(e);
        }
      }).result.testInfo = {
        testId: id,
        data: data,
        feature: 'CHAT_PROMPT_FILTER',
        dataProvider: 'positiveTests'
      };
    }
  );
});
