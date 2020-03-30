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
const moment = require('moment');

describe('Executing Schedule tests from createDeleteSchedule.test.js', () => {
  let analysisId;
  let host;
  let token;
  beforeAll(() => {
    logger.info('Starting Schedule Test Cases...');
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
          analysisId
        );
      }
      // Logout by clearing the storage
      commonFunctions.clearLocalStorage();
      done();
    }, protractorConf.timeouts.pageResolveTimeout);
  });

  using(
    testDataReader.testData['SCHEDULE-REPORT']['createDeleteSchedule']
      ? testDataReader.testData['SCHEDULE-REPORT']['createDeleteSchedule']
      : {},
    (data, id) => {
      it(`${id}:${data.description}`, () => {
        try {

          const ReportName = `schdle ${moment().format('MMM Do h mm ss a')}`;
          const analysisType = 'table:report';
          const tables = data.tables;
          const loginPage = new LoginPage();
          loginPage.loginAs(data.user, /analyze/);
          const ReportDescription = `Schedule DL/ES Report ${new Date().toString()}`;
          const analyzePage = new AnalyzePage();
          analyzePage.goToView('card');

          /*create report*/
          analyzePage.clickOnAddAnalysisButton();
          analyzePage.clickOnAnalysisType(analysisType);
          analyzePage.clickOnNextButton();
          analyzePage.clickOnDataPods(dataSets[data.reportType]);
          analyzePage.clickOnCreateButton();
          const reportDesignerPage = new ReportDesignerPage();
          reportDesignerPage.clickOnReportFields(tables);
          reportDesignerPage.verifyDisplayedColumns(tables);
          reportDesignerPage.clickOnSave();
          reportDesignerPage.enterAnalysisName(ReportName);
          reportDesignerPage.enterAnalysisDescription(ReportDescription);
          reportDesignerPage.clickOnSaveAndCloseDialogButton(/analyze/);
          const schedulePage = new SchedulePage();
          schedulePage.handleToastMessage();

          /*Verify Analysis Details*/
          schedulePage.handleToastMessage();
          analyzePage.clickOnAnalysisLink(ReportName);
          const executePage = new ExecutePage();
          executePage.verifyTitle(ReportName);
          analysisId = executePage.getAnalysisId();
          executePage.clickOnActionLink();
          executePage.clickOnDetails();
          executePage.verifyDescription(ReportDescription);
          executePage.closeActionMenu();
          executePage.closeDetails();
          schedulePage.handleToastMessage();

          //schedule From list/card/Details View
          schedulePage.scheduleFromView(data.scheduleFrom,ReportName);
          schedulePage.selectSchedule(data.scheduleType,data);
          schedulePage.setEmail(data.userEmail);
          schedulePage.scheduleReport();

          //verify Schedule Details from list/card View
          schedulePage.verifyScheduledDetails(data,ReportName);

          //remove Schedule
          schedulePage.removeScheduleTime(data,ReportName);

          //verify Removed Schedule details
          schedulePage.verifyRemovedScheduleDetails(data,ReportName);

          /*Delete the Report*/
          schedulePage.deleteReport();

        } catch (e) {
          console.log(e);
        }
      }).result.testInfo = {
        testId: id,
        data: data,
        feature: 'SCHEDULE-REPORT',
        dataProvider: 'createDeleteSchedule'
      };
    });

  using(
    testDataReader.testData['SCHEDULE-REPORT']['negativeTest']
      ? testDataReader.testData['SCHEDULE-REPORT']['negativeTest']
      : {},
    (data, id) => {
      it(`${id}:${data.description}`, () => {
        try {
          const ReportName = `schdle ${moment().format('MMM Do h mm ss a')}`;
          const analysisType = 'table:report';
          const tables = data.tables;
          const loginPage = new LoginPage();
          loginPage.loginAs(data.user, /analyze/);
          const ReportDescription = `Schedule Report ${new Date().toString()}`;
          const analyzePage = new AnalyzePage();
          analyzePage.goToView('card');

          /*create report*/
          analyzePage.clickOnAddAnalysisButton();
          analyzePage.clickOnAnalysisType(analysisType);
          analyzePage.clickOnNextButton();
          analyzePage.clickOnDataPods(dataSets.report);
          analyzePage.clickOnCreateButton();
          const reportDesignerPage = new ReportDesignerPage();
          reportDesignerPage.clickOnReportFields(tables);
          reportDesignerPage.verifyDisplayedColumns(tables);
          reportDesignerPage.clickOnSave();
          reportDesignerPage.enterAnalysisName(ReportName);
          reportDesignerPage.enterAnalysisDescription(ReportDescription);
          reportDesignerPage.clickOnSaveAndCloseDialogButton(/analyze/);
          const schedulePage = new SchedulePage();
          schedulePage.handleToastMessage();

          /*Verify Analysis Details*/
          schedulePage.handleToastMessage();
          analyzePage.clickOnAnalysisLink(ReportName);
          const executePage = new ExecutePage();
          executePage.verifyTitle(ReportName);
          analysisId = executePage.getAnalysisId();
          executePage.clickOnActionLink();
          executePage.clickOnDetails();
          executePage.verifyDescription(ReportDescription);
          executePage.closeActionMenu();
          executePage.closeDetails();
          schedulePage.handleToastMessage();
          analyzePage.clickOnAnalysisLink(ReportName);
          executePage.clickOnActionLink();
          executePage.clickSchedule();
          schedulePage.setEmail(data.userEmail);
          schedulePage.scheduleReport();
          schedulePage.verifyInvalidScheduleErrorMessage(data.invalidScheduleMessage);
          schedulePage.handleToastMessage();
          schedulePage.closeSchedule();
          executePage.clickOnActionLink();
          executePage.clickSchedule();
          schedulePage.selectHourlyTab();
          schedulePage.clickEveryHour();
          schedulePage.selectHours(data.scheduleHours);
          schedulePage.clickMinutes();
          schedulePage.selectMinutes(data.scheduleMinutes);
          schedulePage.scheduleReport();
          schedulePage.verifyInvalidOptionErrorMessage(data.invalidOptionMessage);
          schedulePage.handleToastMessage();
          schedulePage.closeSchedule();
          executePage.clickOnActionLink();
          executePage.clickSchedule();
          schedulePage.selectDailyTab();
          schedulePage.scheduleReport();
          schedulePage.verifyInvalidScheduleErrorMessage(data.invalidScheduleMessage);
          schedulePage.verifyInvalidOptionErrorMessage(data.invalidOptionMessage);
          schedulePage.handleToastMessage();
          schedulePage.closeSchedule();
          executePage.clickOnActionLink();
          executePage.clickOnDetails();
          executePage.clickPreviousVersions();
          executePage.verifyScheduleDetailsNotPresent();
          executePage.closeActionMenu();

          /*Delete the Report*/
          schedulePage.deleteReport();
        } catch (e) {
          console.log(e);
        }
      }).result.testInfo = {
        testId: id,
        data: data,
        feature: 'SCHEDULE-REPORT',
        dataProvider: 'negativeTest'
      };
    });


});
