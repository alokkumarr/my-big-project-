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
const Header = require('../../pages/components/Header');
const LoginPage = require('../../pages/LoginPage');
const AnalyzePage = require('../../pages/AnalyzePage');
const ExecutePage = require('../../pages/ExecutePage');
const SchedulePage = require('../../pages/SchedulePage');
const users = require('../../helpers/data-generation/users');

describe('Executing Segregate Analysis by Type Tests', () => {
  const categoryName = categories.analyses.name;
  const subCategoryName = subCategories.createSubCategories.createAnalysis.name;
  let host;
  let token;
  let analyses = [];
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
          id
        );
      });
      commonFunctions.clearLocalStorage();
      done();
    }, protractorConf.timeouts.pageResolveTimeout);
  });

  using(
    testDataReader.testData['SEGREGATE_ANALYSIS']['analysis']
      ? testDataReader.testData['SEGREGATE_ANALYSIS']['analysis']
      : {},
    (data, id) => {
      it(`${id}:${data.description}`, async () => {
        logger.info(`Executing test case with id: ${id}`);
        const now = new Date().getTime();
         //Create REPORT from Backend
        const reportName = `e2e rpt${now}`;
        const reportDescription = `e2e chart description ${now}`;
        const report = new AnalysisHelper().createNewAnalysis(
          host,
          token,
          reportName,
          reportDescription,
          Constants.REPORT,
        );
        expect(report).toBeTruthy();
        assert.isNotNull(report, 'analysis should not be null');
        analyses.push(report.analysisId);

        //Create PIVOT from Backend
        const pivotName = `e2e pvt${now}`;
        const pivotDescription = `e2e chart description ${now}`;
        const pivot = new AnalysisHelper().createNewAnalysis(
          host,
          token,
          pivotName,
          pivotDescription,
          Constants.PIVOT,
        );
        expect(pivot).toBeTruthy();
        assert.isNotNull(pivot, 'analysis should not be null');
        analyses.push(pivot.analysisId);

        //Create CHART from Backend
        const chartName = `e2e chrt${now}`;
        const chartDescription = `e2e chart description ${now}`;
        const type = data.chartType.split(':')[1];
        if (!token) {
          logger.error('token cannot be null');
          expect(token).toBeTruthy();
          assert.isNotNull(token, 'token cannot be null');
        }
        const chart = new AnalysisHelper().createNewAnalysis(
          host,
          token,
          chartName,
          chartDescription,
          Constants.CHART,
          type
        );
        expect(chart).toBeTruthy();
        assert.isNotNull(chart, 'analysis should not be null');
        analyses.push(chart.analysisId);

        const header = new Header();
        const loginPage = new LoginPage();
        const analyzePage = new AnalyzePage();
        const executePage = new ExecutePage();
        loginPage.loginAs(data.user, /analyze/);
        header.goToSubCategory(categoryName,subCategoryName);

        //Choose AnalysisType
        const array = data.analysisTypeArray;
        array.forEach(function (item) {
          analyzePage.chooseAnalysisTypeAndVerify(item,chartName,pivotName,reportName);
        });

        //Schedule Analysis
        if(data.scheduleRequired) {
          analyzePage.clickOnAnalysisTypeDropDown();
          analyzePage.selectAnalysisType(data.analysisType);
          switch (data.analysisType) {
            case "Report" : analyzePage.clickOnAnalysisLink(reportName);
            break;
            case "Chart" : analyzePage.clickOnAnalysisLink(chartName);
              break;
            case "Pivot" : analyzePage.clickOnAnalysisLink(pivotName);
              break;
          }
          executePage.clickOnActionLink();
          executePage.clickSchedule();
          const schedulePage = new SchedulePage();
          schedulePage.selectSchedule(data.scheduleType,data);
          schedulePage.setEmail(data.userEmail);
          schedulePage.scheduleReport();
          analyzePage.clickOnAnalysisTypeDropDown();
          analyzePage.selectAnalysisType("Scheduled");
          switch (data.analysisType) {
            case "Report" : analyzePage.verifyScheduledTimingsInListView(reportName,data.scheduleTimings);
              break;
            case "Chart" : analyzePage.verifyScheduledTimingsInListView(chartName,data.scheduleTimings);
              break;
            case "Pivot" : analyzePage.verifyScheduledTimingsInListView(pivotName,data.scheduleTimings);
              break;
          }
        }

        //Go to All Analysis Types
        analyzePage.clickOnAnalysisTypeDropDown();
        analyzePage.selectAnalysisType("All");
        // Delete the reports
        analyzePage.clickOnAnalysisLink(reportName);
        executePage.verifyAnalysisDetailsAndDelete(reportName,reportDescription);
        analyzePage.clickOnAnalysisLink(chartName);
        executePage.verifyAnalysisDetailsAndDelete(chartName,chartDescription);
        analyzePage.clickOnAnalysisLink(pivotName);
        executePage.verifyAnalysisDetailsAndDelete(pivotName,pivotDescription);
      }).result.testInfo = {
        testId: id,
        data: data,
        feature: 'SEGREGATE_ANALYSIS',
        dataProvider: 'analysis'
      };
    }
  );
});


