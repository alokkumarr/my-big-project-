const using = require('jasmine-data-provider');
const testDataReader = require('../../testdata/testDataReader.js');
const protractorConf = require('../../conf/protractor.conf');
const logger = require('../../conf/logger')(__filename);
const commonFunctions = require('../../pages/utils/commonFunctions');
const categories = require('../../helpers/data-generation/categories');
const subCategories = require('../../helpers/data-generation/subCategories');
const Constants = require('../../helpers/Constants');
const path = require('path');

const chai = require('chai');
const assert = chai.assert;
let AnalysisHelper = require('../../helpers/api/AnalysisHelper');
let APICommonHelpers = require('../../helpers/api/APICommonHelpers');
const Header = require('../../pages/components/Header');
const LoginPage = require('../../pages/LoginPage');
const AnalyzePage = require('../../pages/AnalyzePage');
const ExecutePage = require('../../pages/ExecutePage');
const users = require('../../helpers/data-generation/users');

describe('Executing Segregate Analysis by Type Tests', () => {
  const categoryName = categories.analyses.name;
  const subCategoryName = subCategories.createSubCategories.createAnalysis.name;
  let host;
  let token;
  let analyses = [];
  const DownloadDirectory = path.join(__dirname,'../../')+'Downloads';
  beforeAll(() => {
    logger.info('Starting Segregation of Charts, Reports and Pivots .....');
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
      analyses.forEach(id => {
        logger.warn('deleting analysis with id: ' + id);
        new AnalysisHelper().deleteAnalysis(
          host,
          token,
          protractorConf.config.customerCode,
          id,
        );
      });
      executePage.deleteDirectory(DownloadDirectory);
      commonFunctions.clearLocalStorage();
      done();
    }, protractorConf.timeouts.pageResolveTimeout);
  });

  using(
    testDataReader.testData['EXPORT_ANALYSIS']['download']
      ? testDataReader.testData['EXPORT_ANALYSIS']['download']
      : {},
    (data, id) => {
      it(`${id}:${data.description}`, async () => {
        logger.info(`Executing test case with id: ${id}`);
        const now = new Date().getTime();

        //Create Analysis from BAckEnd
        const reportName = `e2e ${now}`;
        const reportDescription = `e2e chart description ${now}`;
        if (!token) {
          logger.error('token cannot be null');
          expect(token).toBeTruthy();
          assert.isNotNull(token, 'token cannot be null');
        }
        if(data.generateAnalysis === "chart")
        {
          const type = data.chartType.split(':')[1];
          const chartName = new AnalysisHelper().createNewAnalysis(
            host,
            token,
            reportName,
            reportDescription,
            Constants.CHART,
            type
          );
          expect(chartName).toBeTruthy();
          assert.isNotNull(chartName, 'analysis should not be null');
          analyses.push(chartName.analysisId);
        }else {
          const analysisName = new AnalysisHelper().createNewAnalysis(
            host,
            token,
            reportName,
            reportDescription,
            data.generateAnalysis,
          );
          expect(analysisName).toBeTruthy();
          assert.isNotNull(analysisName, 'analysis should not be null');
          analyses.push(analysisName.analysisId);
        }

        const header = new Header();
        const loginPage = new LoginPage();
        const analyzePage = new AnalyzePage();
        const executePage = new ExecutePage();
        loginPage.loginAs(data.user, /analyze/);
        header.goToSubCategory(categoryName,subCategoryName);

        //select Analysis for export
        analyzePage.clickOnAnalysisLink(reportName);
        executePage.verifyTitle(reportName);
        executePage.clickOnActionLink();
        executePage.exportAnalysis();

        //Delete Analysis
        executePage.verifyAnalysisDetailsAndDelete(reportName,reportDescription);
        executePage.validateDownloadedFile(DownloadDirectory,reportName);
      }).result.testInfo = {
        testId: id,
        data: data,
        feature: 'EXPORT_ANALYSIS',
        dataProvider: 'download'
      };
    }
  );
});

