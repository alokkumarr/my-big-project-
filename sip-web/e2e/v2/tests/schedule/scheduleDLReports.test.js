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
    testDataReader.testData['SCHEDULE-REPORT']['positiveTest']
      ? testDataReader.testData['SCHEDULE-REPORT']['positiveTest']
      : {},
    (data, id) => {
      it(`${id}:${data.description}`, () => {
        try {
          const ReportName = `schdle ${moment().format('MMM Do h mm ss a')}`;
          const analysisType = 'table:report';
          const tables = data.tables;
          const loginPage = new LoginPage();
          loginPage.loginAs(data.user, /analyze/);
          const ReportDescription = `Schedule DL Report ${new Date().toString()}`;
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

          /*Select page need to schedule report*/
          if(data.scheduleFrom === 'details') {
            analyzePage.clickOnAnalysisLink(ReportName);
            executePage.clickOnActionLink();
            executePage.clickSchedule();
          }else if (data.scheduleFrom === 'list'){
            analyzePage.goToView('list');
            executePage.clickReportActionLink(ReportName);
            executePage.clickSchedule();
          }else if (data.scheduleFrom === 'card'){
            executePage.clickReportActionLink(ReportName);
            executePage.clickSchedule();
          }

          const ScheduleType = data.scheduleType;
            switch (ScheduleType) {
              case 'Immediate':
                schedulePage.ScheduleImmediately();
                break;
              case 'Hourly':
                schedulePage.hourlySchedule(data.Hours,data.Minutes);
                break;
              case 'Daily-Everyday':
                schedulePage.dailyEverydaySchedule(data.Days,data.Hours,data.Minutes,data.timeStamp);
                break;
              case 'Daily-EveryWeekDay':
                schedulePage.dailyEveryWeekDaySchedule(data.Hours,data.Minutes,data.timeStamp);
                break;
              case 'Weekly':
                schedulePage.weeklySchedule(data.dayName,data.Hours,data.Minutes,data.timeStamp);
                break;
              case 'Monthly-On The Day':
                schedulePage.monthlyOnTheDaySchedule(data.monthlyDay,data.monthlyMonth,data.Hours,data.Minutes,data.timeStamp);
                break;
              case 'Monthly-On The Weeks':
                schedulePage.monthlyOnTheWeeksSchedule(data.monthlyWeeks,data.monthlyDayName,data.everyMonth,data.Hours,data.Minutes,data.timeStamp);
                break;
              case 'Yearly-Every-Month':
                schedulePage.yearlyEveryMonthSchedule(data.yearlyMonth,data.yearlyDays,data.Hours,data.Minutes,data.timeStamp);
                break;
              case 'Yearly-On-Week':
                schedulePage.yearlyOnWeekSchedule(data.yearlyWeeks,data.yearlyDayName,data.yearlyMonth,data.Hours,data.Minutes,data.timeStamp);
                break;
              case 'removeSchedule':
                schedulePage.hourlySchedule(data.Hours,data.Minutes);
                break;
            }

          schedulePage.setEmail(data.userEmail);
          schedulePage.scheduleReport();

          if(data.scheduleFrom === 'card') {
            schedulePage.handleToastMessage();
            analyzePage.goToView('card');
            analyzePage.verifyScheduledTimingsInCardView(ReportName,data.scheduleTimings);
            analyzePage.clickOnAnalysisLink(ReportName);
          } else if (data.scheduleFrom === 'list') {
            schedulePage.handleToastMessage();
            analyzePage.goToView('list');
            analyzePage.verifyScheduledTimingsInListView(ReportName,data.scheduleTimings);
            analyzePage.clickOnAnalysisLink(ReportName);
          } else {
            analyzePage.clickOnAnalysisLink(ReportName);
            schedulePage.handleToastMessage();
            executePage.clickOnActionLink();
            executePage.clickOnDetails();
            executePage.clickPreviousVersions();
            executePage.verifyScheduleDetails();
            executePage.closeActionMenu();
          }

          if(ScheduleType==="removeSchedule") {
            executePage.clickOnActionLink();
            executePage.clickSchedule();
            schedulePage.removeSchedule();
            /*Verify Removed schedule*/
            if(data.scheduleFrom === 'card') {
              analyzePage.verifyScheduledTimingsInCardView(ReportName,data.noSchedule);
              analyzePage.clickOnAnalysisLink(ReportName);
            } else {
              analyzePage.goToView('list');
              analyzePage.verifyScheduledTimingsInListView(ReportName,data.blankSchedule);
              analyzePage.clickOnAnalysisLink(ReportName);
            }
          }
          /*Delete the Report*/
          schedulePage.handleToastMessage();
          executePage.clickOnActionLink();
          executePage.clickOnDelete();
          executePage.confirmDelete();
          analyzePage.verifyToastMessagePresent("Analysis deleted.");
          analyzePage.verifyAnalysisDeleted();
        } catch (e) {
          console.log(e);
        }
      }).result.testInfo = {
        testId: id,
        data: data,
        feature: 'SCHEDULE-REPORT',
        dataProvider: 'positiveTest'
      };
    });
});
