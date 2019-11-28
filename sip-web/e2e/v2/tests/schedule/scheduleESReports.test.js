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
const dateFormat = require('dateformat');


const executePage = new ExecutePage();
const analyzePage = new AnalyzePage();
const schedulePage = new SchedulePage();
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

  function CreateReport(analysisType, dataReport, tables, reportName, reportDescription){
    const executePage = new ExecutePage();
    const schedulePage = new SchedulePage();
    const analyzePage = new AnalyzePage();
    const reportDesignerPage = new ReportDesignerPage();
    analyzePage.goToView('card');
    analyzePage.clickOnAddAnalysisButton();
    analyzePage.clickOnAnalysisType(analysisType);
    analyzePage.clickOnNextButton();
    analyzePage.clickOnDataPods(dataSets.pivotChart);
    analyzePage.clickOnCreateButton();
    reportDesignerPage.clickOnReportFields(tables);
    reportDesignerPage.verifyDisplayedColumns(tables);
    reportDesignerPage.clickOnSave();
    reportDesignerPage.enterAnalysisName(reportName);
    reportDesignerPage.enterAnalysisDescription(reportDescription);
    reportDesignerPage.clickOnSaveAndCloseDialogButton(/analyze/);
    // Verify analysis displayed in list and card view
    analyzePage.goToView('list');
    analyzePage.verifyElementPresent(
      analyzePage._analysisTitleLink(reportName),
      true,
      'report should be present in list/card view'
    );
    analyzePage.goToView('card');
    // Go to detail page and very details
    analyzePage.clickOnAnalysisLink(reportName);
    executePage.verifyTitle(reportName);
    analysisId = executePage.getAnalysisId();
    executePage.clickOnActionLink();
    executePage.clickOnDetails();
    executePage.verifyDescription(reportDescription);
    executePage.closeActionMenu();
  }

  function selectEmailContents(mailUser) {
    schedulePage.setEmailAs(mailUser);
    schedulePage.clickXLSXFormatRadioButton();
    schedulePage.clickZipCompressFileCheckBox();
    schedulePage.clickPublishToFTPDropdown();
    schedulePage.selectFTPTestCheckBox();
  }

  function verifyReportScheduled(reportname) {
    analyzePage.clickOnAnalysisLink(reportname);
    schedulePage.handleToastMessageIfPresent();
    schedulePage.handleToastMessageIfPresent();
    executePage.clickOnActionLink();
    executePage.clickOnDetails();
    executePage.clickOnPreviousVersionTab();
    executePage.verifyScheduledInPreviousVersion();
    executePage.closeActionMenu();
  }

  function deleteReport(){
    const executePage = new ExecutePage();
    const analyzePage = new AnalyzePage();
    schedulePage.handleToastMessageIfPresent();
    schedulePage.handleToastMessageIfPresent();
    executePage.clickOnActionLink();
    executePage.clickOnDelete();
    executePage.confirmDelete();
    analyzePage.verifyToastMessagePresent("Analysis deleted.");
    analyzePage.verifyAnalysisDeleted();
  }

  function weekAndDay(date) {
    let days = ['Sunday','Monday','Tuesday','Wednesday',
        'Thursday','Friday','Saturday'],
      prefixes = ['first', 'second', 'third', 'fourth', 'fifth'];
    return prefixes[Math.floor(date.getDate() / 7)]; //Need to get the current week
  }

  using(
    testDataReader.testData['SCHEDULE-REPORT']['esreport']['positiveTest']
      ? testDataReader.testData['SCHEDULE-REPORT']['esreport']['positiveTest']
      : {},
    (data, id) => {
      it(`${id}:${data.description}`, () => {
        try {
          const analysisType = 'table:report';
          const scheduleAnalysisTitle  = 'Schedule Analysis';
          const tables = data.tables;
          const dataReport = dataSets.report;
          const loginPage = new LoginPage();
          loginPage.loginAs(data.user, /analyze/);
          //Immediate Report
          const immediateReportName = `Immediate ESSchedule Report`;
          const immediateReportDescription = `Immediate ESSchedule Report ${new Date().toString()}`;
          CreateReport(analysisType,dataReport,tables,immediateReportName, immediateReportDescription);
          //Schedule the Immediate Report
          executePage.clickOnActionLink();
          executePage.clickOnSchedule();
          schedulePage.verifyTitle(scheduleAnalysisTitle);
          schedulePage.clickOnScheduleImmediatelyCheckBox();
          selectEmailContents(data.userEmail);
          schedulePage.clickOnScheduleButton();
          browser.sleep(30000); //Need to add this, so that browser wait till report schedules
          //Verify Immediate scheduled report
          verifyReportScheduled(immediateReportName);
          deleteReport();
        } catch (e) {
          console.log(e);
        }
      }).result.testInfo = {
        testId: id,
        data: data,
        feature: 'SCHEDULE-REPORT',
        dataProvider: 'esreport'
      };
    });


  using(
    testDataReader.testData['SCHEDULE-REPORT']['esreport']['positiveTest']
      ? testDataReader.testData['SCHEDULE-REPORT']['esreport']['positiveTest']
      : {},
    (data, id) => {
      it(`${id}:${data.description}`, () => {
        try {
          const analysisType = 'table:report';
          const scheduleAnalysisTitle  = 'Schedule Analysis';
          const tables = data.tables;
          const dataReport = dataSets.report;
          const loginPage = new LoginPage();
          loginPage.loginAs(data.user, /analyze/);
          //Hourly Report
          const hourlyReportName = `Hourly ES ScheduleReport`;
          const hourlyReportDescription = `Hourly ES ScheduleReport  ${new Date().toString()}`;
          CreateReport(analysisType,dataReport,tables,hourlyReportName, hourlyReportDescription);
          // Schedule the Hourly Report
          executePage.clickOnActionLink();
          executePage.clickOnSchedule();
          schedulePage.verifyTitle(scheduleAnalysisTitle);
          schedulePage.clickOnHourlyTab();
          schedulePage.clickEveryHourDropdown();
          schedulePage.selectZeroFromEveryHourDropdown();
          schedulePage.clickMinuteDropdown();
          schedulePage.selectOneFromMinuteDropdown();
          selectEmailContents(data.userEmail);
          schedulePage.clickOnScheduleButton();
          browser.sleep(80000); //Need to add this, so that browser wait till report schedules
          //Verify scheduled Hourly report
          verifyReportScheduled(hourlyReportName);
          // Delete the Hourly report
          deleteReport();
        } catch (e) {
          console.log(e);
        }
      }).result.testInfo = {
        testId: id,
        data: data,
        feature: 'SCHEDULE-REPORT',
        dataProvider: 'esreport'
      };
    });

  using(
    testDataReader.testData['SCHEDULE-REPORT']['esreport']['positiveTest']
      ? testDataReader.testData['SCHEDULE-REPORT']['esreport']['positiveTest']
      : {},
    (data, id) => {
      it(`${id}:${data.description}`, () => {
        try {
          let minutes = parseInt(dateFormat(new Date(),"M"))+2;
          const analysisType = 'table:report';
          const scheduleAnalysisTitle  = 'Schedule Analysis';
          const tables = data.tables;
          const dataReport = dataSets.report;
          const loginPage = new LoginPage();
          loginPage.loginAs(data.user, /analyze/);

          //Weekly Report
          const weeklyReportName = `Weekly ES ScheduleReport`;
          const weeklyReportDescription = `Weekly ES ScheduleReport ${new Date().toString()}`;
          CreateReport(analysisType,dataReport,tables,weeklyReportName, weeklyReportDescription);
          // Schedule the Report - WEEKLY Tab
          executePage.clickOnActionLink();
          executePage.clickOnSchedule();
          schedulePage.verifyTitle(scheduleAnalysisTitle);
          schedulePage.clickOnWeeklyTab();
          schedulePage.WeeklyTab_selectMinutesHoursFromDropdown(minutes+1);
          schedulePage.WeeklyTab_clickOnTimezoneDropdown();
          schedulePage.WeeklyTab_selectTimezoneFromDropdown(dateFormat(new Date() , "TT"));
          selectEmailContents(data.userEmail);
          schedulePage.clickOnScheduleButton();

          browser.sleep(240000); //Need to add this, so that browser wait till report schedules
          //Verify EveryWeekDay scheduled report
          verifyReportScheduled(weeklyReportName);
          deleteReport();
        } catch (e) {
          console.log(e);
        }
      }).result.testInfo = {
        testId: id,
        data: data,
        feature: 'SCHEDULE-REPORT',
        dataProvider: 'esreport'
      };
    });


  using(
    testDataReader.testData['SCHEDULE-REPORT']['esreport']['positiveTest']
      ? testDataReader.testData['SCHEDULE-REPORT']['esreport']['positiveTest']
      : {},
    (data, id) => {
      it(`${id}:${data.description}`, () => {
        try {
          let minutes = parseInt(dateFormat(new Date(),"M"))+2;
          const analysisType = 'table:report';
          const scheduleAnalysisTitle  = 'Schedule Analysis';
          const tables = data.tables;
          const dataReport = dataSets.report;
          const loginPage = new LoginPage();
          loginPage.loginAs(data.user, /analyze/);

          //EveryDay Report
          const everyDayReportName = `EveryDay ES ScheduleReport`;
          const everyDayReportDescription = `EveryDay ES ScheduleReport ${new Date().toString()}`;
          CreateReport(analysisType,dataReport,tables,everyDayReportName, everyDayReportDescription);

          // Schedule the Report - Daily EveryDay Tab
          executePage.clickOnActionLink();
          executePage.clickOnSchedule();
          schedulePage.verifyTitle(scheduleAnalysisTitle);
          schedulePage.clickOnDailyTab();
          schedulePage.clickOnEveryDayCheckbox();
          schedulePage.clickOnDaysDropdown();
          schedulePage.selectOneFromDaysDropdown();
          schedulePage.DailyTab_selectHoursMinutesFromFirstRow(minutes+1);
          schedulePage.clickTimezoneDropdown();
          schedulePage.selectTimezoneFromDropdown(dateFormat(new Date() , "TT"));
          selectEmailContents(data.userEmail);
          schedulePage.clickOnScheduleButton();
          browser.sleep(240000);
          //Verify EveryDay scheduled report
          verifyReportScheduled(everyDayReportName);
          // Delete the EveryDay report
          deleteReport();
        } catch (e) {
          console.log(e);
        }
      }).result.testInfo = {
        testId: id,
        data: data,
        feature: 'SCHEDULE-REPORT',
        dataProvider: 'esreport'
      };
    });


  using(
    testDataReader.testData['SCHEDULE-REPORT']['esreport']['positiveTest']
      ? testDataReader.testData['SCHEDULE-REPORT']['esreport']['positiveTest']
      : {},
    (data, id) => {
      it(`${id}:${data.description}`, () => {
        try {
          let minutes = parseInt(dateFormat(new Date(),"M"))+2;
          const analysisType = 'table:report';
          const scheduleAnalysisTitle  = 'Schedule Analysis';
          const tables = data.tables;
          const dataReport = dataSets.report;
          const loginPage = new LoginPage();
          loginPage.loginAs(data.user, /analyze/);

          //EveryWeekDay Report
          const everyWeekDayReportName = `Every ES WeekdayReport`;
          const everyWeekDayReportDescription = `Every ES WeekdayReport ${new Date().toString()}`;
          CreateReport(analysisType,dataReport,tables,everyWeekDayReportName, everyWeekDayReportDescription);
          // Schedule the Report - Daily EveryWeekDay Tab
          executePage.clickOnActionLink();
          executePage.clickOnSchedule();
          schedulePage.verifyTitle(scheduleAnalysisTitle);
          schedulePage.clickOnDailyTab();
          schedulePage.clickOnEveryWeekDayCheckbox();
          schedulePage.DailyTab_selectHoursMinutesFromSecondRow(minutes+1);
          schedulePage.clickOnEveryWeekDayTimeZoneDropdown();
          schedulePage.selectTimezoneFromDropdown(dateFormat(new Date() , "TT"));
          selectEmailContents(data.userEmail);
          schedulePage.clickOnScheduleButton();
          browser.sleep(240000);
          //Verify EveryWeekDay scheduled report
          verifyReportScheduled(everyWeekDayReportName);
          // Delete the EveryWeekDay report
          deleteReport();
        } catch (e) {
          console.log(e);
        }
      }).result.testInfo = {
        testId: id,
        data: data,
        feature: 'SCHEDULE-REPORT',
        dataProvider: 'esreport'
      };
    });

  using(
    testDataReader.testData['SCHEDULE-REPORT']['esreport']['positiveTest']
      ? testDataReader.testData['SCHEDULE-REPORT']['esreport']['positiveTest']
      : {},
    (data, id) => {
      it(`${id}:${data.description}`, () => {
        try {
          let minutes = parseInt(dateFormat(new Date(),"M"))+2;
          const analysisType = 'table:report';
          const scheduleAnalysisTitle  = 'Schedule Analysis';
          const tables = data.tables;
          const dataReport = dataSets.report;
          const loginPage = new LoginPage();
          loginPage.loginAs(data.user, /analyze/);
          //Yearly MonthWise Report
          const yearlyMonthWiseReportName = `Yearly ES MonthWiseReport`;
          const yearlyMonthWiseReportDescription = `Yearly ES MonthWise ScheduleReport ${new Date().toString()}`;
          CreateReport(analysisType,dataReport,tables,yearlyMonthWiseReportName, yearlyMonthWiseReportDescription);
          // Schedule the Report - Yearly Tab
          executePage.clickOnActionLink();
          executePage.clickOnSchedule();
          schedulePage.verifyTitle(scheduleAnalysisTitle);
          schedulePage.clickOnYearlyTab();
          schedulePage.YearlyTab_clickOnFirstRadioButton();
          schedulePage.YearlyTab_clickOnFirstMonthsDropdown();
          schedulePage.YearlyTab_selectMonthFromFirstMonthDropdown(dateFormat(new Date(), "mmmm"));
          schedulePage.YearlyTab_clickOnFirstDaysDropdown();
          schedulePage.YearlyTab_selectDayFromFirstDaysDropdown(dateFormat(new Date(), "dd"));
          schedulePage.YearlyTab_selectHoursMinutesFromFirstRow(minutes+1);
          schedulePage.YearlyTab_clickOnFirstTimezoneDropdown();
          schedulePage.YearlyTab_selectTimeZoneFromFirstTimezoneDropdown(dateFormat(new Date() , "TT"));
          selectEmailContents(data.userEmail);
          schedulePage.clickOnScheduleButton();
          browser.sleep(240000);
          //Verify MonthWise scheduled report
          verifyReportScheduled(yearlyMonthWiseReportName);
          // Delete the MonthWise report
          deleteReport();
        } catch (e) {
          console.log(e);
        }
      }).result.testInfo = {
        testId: id,
        data: data,
        feature: 'SCHEDULE-REPORT',
        dataProvider: 'esreport'
      };
    });

  using(
    testDataReader.testData['SCHEDULE-REPORT']['esreport']['positiveTest']
      ? testDataReader.testData['SCHEDULE-REPORT']['esreport']['positiveTest']
      : {},
    (data, id) => {
      it(`${id}:${data.description}`, () => {
        try {
          let minutes = parseInt(dateFormat(new Date(),"M"))+2;
          const analysisType = 'table:report';
          const scheduleAnalysisTitle  = 'Schedule Analysis';
          const tables = data.tables;
          const dataReport = dataSets.report;
          const loginPage = new LoginPage();
          loginPage.loginAs(data.user, /analyze/);
          //Schedule Yearly WeekWise Report
          const yearlyWeekWiseReportName = `Yearly ES WeekWiseReport`;
          const yearlyWeekWiseReportDescription = `Yearly ES WeekWiseSchedule Report ${new Date().toString()}`;
          CreateReport(analysisType,dataReport,tables,yearlyWeekWiseReportName, yearlyWeekWiseReportDescription);
          // Schedule the Report - Yearly Tab
          executePage.clickOnActionLink();
          executePage.clickOnSchedule();
          schedulePage.verifyTitle(scheduleAnalysisTitle);
          schedulePage.clickOnYearlyTab();
          schedulePage.YearlyTab_clickOnSecondRadioButton();
          schedulePage.YearlyTab_clickOnWeeksDropdown();
          schedulePage.YearlyTab_selectWeekFromWeeksDropdown(weekAndDay(new Date()));
          schedulePage.YearlyTab_clickOnSecondDaysDropdown();
          schedulePage.YearlyTab_selectDaysFromSecondDaysDropdown(dateFormat(new Date(), "ddd").toUpperCase());
          schedulePage.YearlyTab_clickOnSecondMonthsDropdown();
          schedulePage.YearlyTab_selectMonthFromSecondMonthDropdown(dateFormat(new Date(), "mmmm"));
          schedulePage.YearlyTab_selectHoursMinutesFromSecondRow(minutes+2);
          schedulePage.YearlyTab_clickOnSecondTimezoneDropdown();
          schedulePage.YearlyTab_selectTimeZoneFromSecondTimezoneDropdown(dateFormat(new Date() , "TT"));
          selectEmailContents(data.userEmail);
          schedulePage.clickOnScheduleButton();

          browser.sleep(240000);
          //Verify WeekWise scheduled report
          verifyReportScheduled(yearlyWeekWiseReportName,);
          // Delete the report
          deleteReport();

        } catch (e) {
          console.log(e);
        }
      }).result.testInfo = {
        testId: id,
        data: data,
        feature: 'SCHEDULE-REPORT',
        dataProvider: 'esreport'
      };
    });
  using(
    testDataReader.testData['SCHEDULE-REPORT']['esreport']['positiveTest']
      ? testDataReader.testData['SCHEDULE-REPORT']['esreport']['positiveTest']
      : {},
    (data, id) => {
      it(`${id}:${data.description}`, () => {
        try {
          let minutes = parseInt(dateFormat(new Date(),"M"))+2;
          const analysisType = 'table:report';
          const scheduleAnalysisTitle  = 'Schedule Analysis';
          const tables = data.tables;
          const dataReport = dataSets.report;
          const loginPage = new LoginPage();
          loginPage.loginAs(data.user, /analyze/);
          //Yearly MonthWise Report
          const monthlyDayWiseReportName = `Monthly ES DayWiseReport`;
          const monthlyDayWiseReportDescription = `Monthly ES DayWiseSchedule Report ${new Date().toString()}`;
          CreateReport(analysisType,dataReport,tables,monthlyDayWiseReportName, monthlyDayWiseReportDescription);
          // Schedule the Report - Yearly Tab
          executePage.clickOnActionLink();
          executePage.clickOnSchedule();
          schedulePage.verifyTitle(scheduleAnalysisTitle);
          schedulePage.clickOnMonthlyTab();
          schedulePage.MonthlyTab_clickOnFirstRadioButton();
          schedulePage.MonthlyTab_clickOnFirstDaysDropdown();
          schedulePage.MonthlyTab_selectDayFromFirstDaysDropdown(dateFormat(new Date() , "dS"));
          schedulePage.MonthlyTab_clickOnFirstMonthsDropdown();
          schedulePage.MonthlyTab_selectMonthFromFirstMonthDropdown('1');
          schedulePage.MonthlyTab_selectHoursMinutesFromFirstRow(minutes+1);
          schedulePage.MonthlyTab_clickOnFirstTimezoneDropdown();
          schedulePage.MonthlyTab_selectTimeZoneFromFirstTimezoneDropdown(dateFormat(new Date() , "TT"));
          selectEmailContents(data.userEmail);
          schedulePage.clickOnScheduleButton();
          browser.sleep(240000);
          //Verify MonthWise scheduled report
          verifyReportScheduled(monthlyDayWiseReportName);
          // Delete the MonthWise report
          deleteReport();
        } catch (e) {
          console.log(e);
        }
      }).result.testInfo = {
        testId: id,
        data: data,
        feature: 'SCHEDULE-REPORT',
        dataProvider: 'esreport'
      };
    });

  using(
    testDataReader.testData['SCHEDULE-REPORT']['esreport']['positiveTest']
      ? testDataReader.testData['SCHEDULE-REPORT']['esreport']['positiveTest']
      : {},
    (data, id) => {
      it(`${id}:${data.description}`, () => {
        try {
          let minutes = parseInt(dateFormat(new Date(),"M"))+2;
          const analysisType = 'table:report';
          const scheduleAnalysisTitle  = 'Schedule Analysis';
          const tables = data.tables;
          const dataReport = dataSets.report;
          const loginPage = new LoginPage();
          loginPage.loginAs(data.user, /analyze/);
          //Yearly MonthWise Report
          const monthlyWeekWiseReportName = `Monthly ES DayWiseReport`;
          const monthlyWeekWiseReportDescription = `Monthly ES DayWise Schedule Report ${new Date().toString()}`;
          CreateReport(analysisType,dataReport,tables,monthlyWeekWiseReportName, monthlyWeekWiseReportDescription);
          // Schedule the Report - Yearly Tab
          executePage.clickOnActionLink();
          executePage.clickOnSchedule();
          schedulePage.verifyTitle(scheduleAnalysisTitle);
          schedulePage.clickOnMonthlyTab();
          schedulePage.MonthlyTab_clickOnSecondRadioButton();
          schedulePage.MonthlyTab_clickOnWeeksDropdown();
          schedulePage.MonthlyTab_selectWeekFromWeeksDropdown(weekAndDay(new Date()));
          schedulePage.MonthlyTab_clickOnSecondDaysDropdown();
          schedulePage.MonthlyTab_selectDaysFromSecondDaysDropdown(dateFormat(new Date() , "ddd").toUpperCase());
          schedulePage.MonthlyTab_clickOnSecondMonthsDropdown();
          schedulePage.MonthlyTab_selectMonthFromSecondMonthDropdown('1');
          schedulePage.MonthlyTab_selectHoursMinutesFromSecondRow(minutes+1);
          schedulePage.MonthlyTab_clickOnSecondTimezoneDropdown();
          schedulePage.MonthlyTab_selectTimeZoneFromSecondTimezoneDropdown(dateFormat(new Date() , "TT"));
          selectEmailContents(data.userEmail);
          schedulePage.clickOnScheduleButton();
          browser.sleep(240000);
          //Verify MonthWise scheduled report
          verifyReportScheduled(monthlyWeekWiseReportName);
          // Delete the MonthWise report
          deleteReport();
        } catch (e) {
          console.log(e);
        }
      }).result.testInfo = {
        testId: id,
        data: data,
        feature: 'SCHEDULE-REPORT',
        dataProvider: 'esreport'
      };
    });

});
