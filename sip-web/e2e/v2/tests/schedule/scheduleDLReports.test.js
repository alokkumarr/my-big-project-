const testDataReader = require('../../testdata/testDataReader.js');
const using = require('jasmine-data-provider');
const protractorConf = require('../../conf/protractor.conf');
const logger = require('../../conf/logger')(__filename);
const LoginPage = require('../../pages/LoginPage');
const commonFunctions = require('../../pages/utils/commonFunctions');
const AnalysisHelper = require('../../helpers/api/AnalysisHelper');
let APICommonHelpers = require('../../helpers/api/APICommonHelpers');
let AnalyzePage = require('../../pages/AnalyzePage');
const ReportDesignerPage = require('../../pages/ReportDesignerPage');
const ExecutePage = require('../../pages/ExecutePage');
const SchedulePage = require('../../pages/SchedulePage');
const dataSets = require('../../helpers/data-generation/datasets');

const executePage = new ExecutePage();
const analyzePage = new AnalyzePage();
const schedulePage = new SchedulePage();
const reportDesignerPage = new ReportDesignerPage();
describe('Executing Schedule tests from scheduleDLReports.test.js', () => {
  let analysisId;
  let host;
  let token;
  beforeAll(() => {
    logger.info('Starting Schedule tests...');
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
    logger.info('DeleteReport tests...');
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
    testDataReader.testData['SCHEDULE-REPORT']['dlreport']['positiveTest']
      ? testDataReader.testData['SCHEDULE-REPORT']['dlreport']['positiveTest']
      : {},
    (data, id) => {
      it(`${id}:${data.description}`, () => {
        try {
          const analysisType = 'table:report';
          const tables = data.tables;
          const loginPage = new LoginPage();
          loginPage.loginAs(data.user, /analyze/);
          const ReportName = `Schedule ${data.scheduleName}`;
          const ReportDescription = `Schedule DL Report ${new Date().toString()}`;
          analyzePage.goToView('card');

          //create report
          analyzePage.clickOnAddAnalysisButton();
          analyzePage.clickOnAnalysisType(analysisType);
          analyzePage.clickOnNextButton();
          analyzePage.clickOnDataPods(dataSets.report);
          analyzePage.clickOnCreateButton();
          reportDesignerPage.clickOnReportFields(tables);
          reportDesignerPage.verifyDisplayedColumns(tables);
          reportDesignerPage.clickOnSave();
          reportDesignerPage.enterAnalysisName(ReportName);
          reportDesignerPage.enterAnalysisDescription(ReportDescription);
          reportDesignerPage.clickOnSaveAndCloseDialogButton(/analyze/);
          schedulePage.handleToastMessage();

          //Verify Analysis Details
          analyzePage.clickOnAnalysisLink(ReportName);
          executePage.verifyTitle(ReportName);
          analysisId = executePage.getAnalysisId();
          executePage.clickActionLink();
          executePage.clickDetails();
          executePage.verifyDescription(ReportDescription);
          executePage.closeActionMenu();
          executePage.closeDetails();
          schedulePage.handleToastMessage();

          //Select page need to schedule report
          if(data.scheduleFrom === 'details') {
            analyzePage.clickOnAnalysisLink(ReportName);
            executePage.clickActionLink();
            executePage.clickSchedule();
          }else if (data.scheduleFrom === 'list'){
            analyzePage.goToView('list');
            executePage.clickActionLink();
            executePage.clickSchedule();
          }else if (data.scheduleFrom === 'card'){
            executePage.clickActionLink();
            executePage.clickSchedule();
          }

          const ScheduleType = data.scheduleType;
          switch (ScheduleType) {
            case 'Immediate':
              schedulePage.ScheduleImmediately();
              break;
            case 'Hourly':
              schedulePage.selectHourlyTab();
              schedulePage.clickEveryHour();
              schedulePage.selectHours(data.scheduleHours);
              schedulePage.clickMinutes();
              schedulePage.selectMinutes(data.scheduleMinutes);
              break;
            default:
              console.log("There is no valid schedule Type Mentioned ");
          }

          schedulePage.setEmail(data.userEmail);
          schedulePage.scheduleReport();

          if(data.scheduleFrom === 'card') {
            analyzePage.verifyScheduledTimingsInCardView(data.scheduleTimings);
          } else if (data.scheduleFrom === 'list') {
            analyzePage.goToView('list');
            analyzePage.verifyScheduledTimingsInListView(data.scheduleTimings);
          } else {
            analyzePage.clickOnAnalysisLink(ReportName);
            schedulePage.handleToastMessage();
            executePage.clickActionLink();
            executePage.clickDetails();
            executePage.clickPreviousVersions();
            executePage.verifyScheduleDetails();
            executePage.closeActionMenu();
          }

          //Delete the Report
          executePage.clickActionLink();
          executePage.clickOnDelete();
          executePage.confirmDelete();
          analyzePage.verifyToastMessage("Analysis deleted.");
          analyzePage.verifyAnalysisDeleted();
        } catch (e) {
          console.log(e);
        }
      }).result.testInfo = {
        testId: id,
        data: data,
        feature: 'SCHEDULE-REPORT',
        dataProvider: 'dlreport'
      };
    });
});
