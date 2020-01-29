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
    testDataReader.testData['SCHEDULE-REPORT']['dlreport']
      ? testDataReader.testData['SCHEDULE-REPORT']['dlreport']
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
                schedulePage.selectHourlyTab();
                schedulePage.clickEveryHour();
                schedulePage.selectHours(data.Hours);
                schedulePage.clickMinutes();
                schedulePage.selectMinutes(data.Minutes);
                break;
              case 'Daily-Everyday':
                schedulePage.selectDailyTab();
                schedulePage.selectEveryDayCheckbox();
                schedulePage.clickDays();
                schedulePage.selectDays(data.Days);
                schedulePage.clickEveryDayHours();
                schedulePage.selectHours(data.Hours);
                schedulePage.clickEveryDayMinutes();
                schedulePage.selectMinutes(data.Minutes);
                schedulePage.clickEveryDayTimeStamp();
                schedulePage.selectTimeStamp(data.timeStamp);
                break;
              case 'Daily-EveryWeekDay':
                schedulePage.selectDailyTab();
                schedulePage.selectEveryWeekDayCheckbox();
                schedulePage.clickEveryWeekDayHours();
                schedulePage.selectHours(data.Hours);
                schedulePage.clickEveryWeekDayMinutes();
                schedulePage.selectMinutes(data.Minutes);
                schedulePage.clickEveryWeekDayTimeStamp();
                schedulePage.selectTimeStamp(data.timeStamp);
                break;
              case 'Weekly':
                schedulePage.selectWeeklyTab();
                schedulePage.selectSpecificDayOfWeekCheckBox(data.dayName);
                schedulePage.clickOnWeeklyHours();
                schedulePage.selectHours(data.Hours);
                schedulePage.clickOnWeeklyMinutes();
                schedulePage.selectMinutes(data.Minutes);
                schedulePage.clickOnWeeklyTimeStamp();
                schedulePage.selectTimeStamp(data.timeStamp);
                break;
              case 'Monthly-On The Day':
                schedulePage.selectMonthlyTab();
                schedulePage.selectMonthlyFirstCheckbox();
                schedulePage.clickOnMonthlyFirstRowDays();
                schedulePage.selectMonthlyFirstRowDay(data.monthlyDay);
                schedulePage.clickOnMonthlyFirstRowMonths();
                schedulePage.selectMonthlyFirstRowMonth(data.monthlyMonth);
                schedulePage.clickOnMonthlyFirstRowHours();
                schedulePage.selectHours(data.Hours);
                schedulePage.clickOnMonthlyFirstRowMinutes();
                schedulePage.selectMinutes(data.Minutes);
                schedulePage.clickOnMonthlyFirstRowTimeStamp();
                schedulePage.selectTimeStamp(data.timeStamp);
                break;
              case 'Monthly-On The Weeks':
                schedulePage.selectMonthlyTab();
                schedulePage.selectMonthlySecondCheckbox();
                schedulePage.clickOnMonthlySecondRowWeeks();
                schedulePage.selectMonthlySecondRowWeeks(data.monthlyWeeks);
                schedulePage.clickOnMonthlySecondRowDay();
                schedulePage.selectMonthlySecondRowDay(data.monthlyDayName);
                schedulePage.clickOnMonthlySecondRowMonth();
                schedulePage.selectMonthlySecondRowMonth(data.everyMonth);
                schedulePage.clickOnMonthlySecondRowHours();
                schedulePage.selectHours(data.Hours);
                schedulePage.clickOnMonthlySecondRowMinutes();
                schedulePage.selectMinutes(data.Minutes);
                schedulePage.clickOnMonthlySecondRowTimeStamp();
                schedulePage.selectTimeStamp(data.timeStamp);
                break;
              case 'Yearly-Every-Month':
                schedulePage.selectYearlyTab();
                schedulePage.selectYearlyFirstCheckbox();
                schedulePage.clickOnYearlyFirstRowMonth();
                schedulePage.selectYearlyFirstRowMonth(data.yearlyMonth);
                schedulePage.clickOnYearlyFirstRowDays();
                schedulePage.selectYearlyFirstRowDays(data.yearlyDays);
                schedulePage.clickOnYearlyFirstRowHours();
                schedulePage.selectYearlyFirstRowHours(data.Hours);
                schedulePage.clickOnYearlyFirstRowMinutes();
                schedulePage.selectYearlyFirstRowMinutes(data.Minutes);
                schedulePage.clickOnYearlyFirstRowTimeStamp();
                schedulePage.selectYearlyFirstRowTimeStamp(data.timeStamp);
                break;
              case 'Yearly-On-Week':
                schedulePage.selectYearlyTab();
                schedulePage.selectYearlySecondCheckbox();
                schedulePage.clickOnYearlySecondRowWeeks();
                schedulePage.selectYearlySecondRowWeeks(data.yearlyWeeks);
                schedulePage.clickOnYearlySecondRowDay();
                schedulePage.selectYearlySecondRowDay(data.yearlyDayName);
                schedulePage.clickOnYearlySecondRowMonth();
                schedulePage.selectYearlySecondRowMonth(data.yearlyMonth);
                schedulePage.clickOnYearlySecondRowHours();
                schedulePage.selectYearlySecondRowHours(data.Hours);
                schedulePage.clickOnYearlySecondRowMinutes();
                schedulePage.selectYearlySecondRowMinutes(data.Minutes);
                schedulePage.clickOnYearlySecondRowTimeStamp();
                schedulePage.selectYearlySecondRowTimeStamp(data.timeStamp);
                break;
            }

          schedulePage.setEmail(data.userEmail);
          schedulePage.scheduleReport();

          if(data.scheduleFrom === 'card') {
            analyzePage.goToView('card');
            analyzePage.verifyScheduledTimingsInCardView(ReportName,data.scheduleTimings);
            analyzePage.clickOnAnalysisLink(ReportName);
          } else if (data.scheduleFrom === 'list') {
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
        dataProvider: 'dlreport'
      };
    });
});
