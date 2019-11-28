'use strict';
const commonFunctions = require('./utils/commonFunctions');
const ConfirmationModel = require('./components/ConfirmationModel');
const dateFormat = require('dateformat');

class SchedulePage extends ConfirmationModel {
  constructor() {
    super();
    this._analysisTitle = element(by.css(`[e2e="schedule-analysis-header"]`));
    this._scheduleImmediatelyCheckBox = element(by.css(`[e2e="immediate-schedule"]`));
    this._scheduleButton = element(by.css(`[e2e='schedule-analysis-publish']`));
    this._emailInputBox = element(by.css(`[e2e='email-list-input']`));
    this.xlsxRadioButton = element(by.xpath(`//*[@value='xlsx']//div[@class='mat-radio-outer-circle']`));
    this.zipCompressTheFile = element(by.xpath('//*[@value=\'zip\']//div[@class=\'mat-checkbox-inner-container\']'));
    this._toastMessage = element(by.xpath(`//div[@class='toast-message']`));
    this._deleteToastMessage = message => element(by.xpath(`//div[contains(text(),"${message}")]`));
    this._ftpLocationDropdwonButton = element(by.css(`[e2e="ftp-bucket-list"]`));
    this._ftpCheckBox = element(by.xpath("//*[contains(@class,'mat-pseudo-checkbox')]"));

    /*Elements in Hourly Tab*/
    this.hourlyTab = element(by.xpath(`//div[text()='Hourly']`));
    this.hourlyTab_everyHourDropdown = element(by.css('[e2e="hourly-schedule-hourly-hours"]'));
    this.hourlyTab_minutesDropdown = element(by.css('[e2e="hourly-schedule-hourly-minutes"]'));


    /*Elements in Daily Tab*/
    this.dailyTab = this.yearlyTab = element(by.xpath(`//div[text()='Daily']`));
    this.dailyTab_everDayCheckbox = element(by.css(`[class='mat-radio-container']`));
    this.dailyTab_everyDay_daysDropdown = element(by.css(`[e2e="daily-schedule-daily-days"]`));
    this.dailyTab_everyDay_oneDay = element(by.css(`[class='mat-option ng-star-inserted mat-active']`));
    this.dailyTab_everyDay_hourDropdown = element(by.css(`[e2e='cron-schedule-hours']`));
    this.dailyTab_everyDay_selectHours_xpath = value => element(by.xpath(`//span[text()=' ${value} ']`));
    this.dailyTab_everyDay_minutesDropdown = element(by.css(`[e2e='cron-schedule-minutes']`));
    this.dailyTab_everyDay_selectMinutes_xpath = value => element(by.xpath(`//span[text()=' ${value} ']`));
    this.dailyTab_everyDay_timeZoneDropdown = element(by.css(`[e2e='cron-schedule-hour-type']`));
    this.dailyTab_everyDay_timeZone = value => element(by.xpath(`//span[contains(text(),'${value}')]`));

    this.dailyTab_everyWeekDay_hourDropdown = element(by.xpath("(//*[@e2e='cron-schedule-hours'])[2]"));
    this.dailyTab_everyWeekDay_minutesDropdown = element(by.xpath("(//*[@e2e='cron-schedule-minutes'])[2]"));
    this.dailyTab_everyWeekDay_timeZoneDropdown = element(by.xpath("(//*[@e2e='cron-schedule-hour-type'])[2]"));


    /*Elements in Weekly Tab*/
    this.weeklyTab = this.yearlyTab = element(by.xpath(`//div[text()='Weekly']`));
    this._overlayPane = element(by.css(`[class='cdk-overlay-pane']`));
    this.weeklyTab_hourDropdown = element(by.css(`[e2e='cron-schedule-hours']`));
    this.weeklyTab_selectHours_xpath = value => element(by.xpath(`//span[text()=' ${value} ']`));
    this.weeklyTab_minutesDropdown = element(by.css(`[e2e='cron-schedule-minutes']`));
    this.weeklyTab_selectMinutes_xpath = value => element(by.xpath(`//span[text()=' ${value} ']`));
    this.weeklyTab_timeZoneDropdown = element(by.css(`[e2e='cron-schedule-hour-type']`));
    this.weeklyTab_selectTimezone = value => element(by.xpath(`//span[contains(text(),'${value}')]`));


    /*Elements in Monthly Tab*/
    this.navigationIcon = element(by.xpath("//div[contains(@class,'pagination-after')]"));
    this.monthlyTab = element(by.xpath(`//div[text()='Monthly']`));
    this.monthlyTab_fir_radioButton = element(by.css(`[class='mat-radio-container']`));
    this.monthlyTab_fir_daysDropdown = element(by.css(`[e2e="monthly-schedule-monthly-day"]`));
    this.monthlyTab_fir_selectDay = value => element(by.cssContainingText('span',`${value}`));
    this.monthlyTab_fir_monthDropdown = element(by.css(`[e2e="monthly-schedule-monthly-month"]`));
    this.monthlyTab_fir_selectMonth = value => element(by.xpath(`//span[text()=' ${value} ']`));
    this.monthlyTab_fir_hourDropdown = element(by.css(`[e2e='cron-schedule-hours']`));
    this.monthlyTab_fir_selectHours = value => element(by.xpath(`//span[text()=' ${value} ']`));
    this.monthlyTab_fir_minutesDropdown = element(by.css(`[e2e='cron-schedule-minutes']`));
    this.monthlyTab_fir_selectMinutes = value => element(by.xpath(`//span[text()=' ${value} ']`));
    this.monthlyTab_fir_timeZoneDropdown = element(by.css(`[e2e='cron-schedule-hour-type']`));
    this.monthlyTab_fir_selectTimezone = value => element(by.xpath(`//span[contains(text(),'${value}')]`));

    this.monthlyTab_sec_weekDropDown = element(by.css(`[e2e="monthly-schedule-monthly-week-day-month"]`));
    this.monthlyTab_sec_selectWeek = value => element(by.cssContainingText('span',`${value}`));
    this.monthlyTab_sec_daysDropdown = element(by.css(`[e2e="monthly-schedule-monthly-week-day-day"]`));
    this.monthlyTab_sec_selectDay = value => element(by.cssContainingText('span',`${value}`));
    this.monthlyTab_sec_MonthDropdwon = element(by.css(`[e2e="monthly-schedule-monthly-week-day-month-week"]`));
    this.monthlyTab_sec_selectMonth = value => element(by.xpath(`//span[text()=' ${value} ']`));
    this.monthlyTab_sec_hourDropdown = element(by.xpath("(//*[@e2e='cron-schedule-hours'])[2]"));
    this.monthlyTab_sec_selectHours = value => element(by.xpath(`//span[text()=' ${value} ']`));
    this.monthlyTab_sec_minutesDropdown = element(by.xpath("(//*[@e2e='cron-schedule-minutes'])[2]"));
    this.monthlyTab_sec_selectMinutes = value => element(by.xpath(`//span[text()=' ${value} ']`));
    this.monthlyTab_sec_timeZoneDropdown = element(by.xpath("(//*[@e2e='cron-schedule-hour-type'])[2]"));
    this.monthlyTab_sec_selectTimezone = value => element(by.xpath(`//span[contains(text(),'${value}')]`));

    /*Elements in Yearly Tab*/
    this.yearlyTab = element(by.xpath(`//div[text()='Yearly']`));
    this.yearlyTab_fir_radioButton = element(by.css(`[class='mat-radio-container']`));
    this.yearlyTab_fir_monthDropdown = element(by.css(`[e2e="yearly-schedule-yearly-month-day-month"]`));
    this.yearlyTab_fir_selectMonth_css = value => element(by.cssContainingText(`span`,`${value}`));
    this.yearlyTab_fir_daysDropdown = element(by.css(`[e2e="yearly-schedule-yearly-month-day-day"]`));
    this.yearlyTab_fir_selectDay_css = value => element(by.cssContainingText(`span`,`${value}`));
    this.yearlyTab_fir_hourDropdown = element(by.css(`[e2e='cron-schedule-hours']`));
    this.yearlyTab_fir_selectHours_xpath = value => element(by.xpath(`//span[@class='mat-option-text'][text()=' ${value} ']`));
    this.yearlyTab_fir_minutesDropdown = element(by.css(`[e2e='cron-schedule-minutes']`));
    this.yearlyTab_fir_selectMinutes_xpath = value => element(by.xpath(`//span[@class='mat-option-text'][text()=' ${value} ']`));
    this.yearlyTab_fir_timeZoneDropdown = element(by.css(`[e2e='cron-schedule-hour-type']`));
    this.yearlyTab_fir_selectTimezone = value => element(by.xpath(`//span[contains(text(),'${value}')]`));

    this.yearlyTab_sec_weekDropDown = element(by.css(`[e2e="yearly-schedule-yearly-month-week-month-week"]`));
    this.yearlyTab_sec_selectWeek_css = value => element(by.cssContainingText(`span`,`${value}`));
    this.yearlyTab_sec_daysDropdown = element(by.css(`[e2e="yearly-schedule-yearly-month-week-day"]`));
    this.yearlyTab_sec_selectDay_css = value => element(by.cssContainingText(`span`,`${value}`));
    this.yearlyTab_sec_MonthDropdwon = element(by.css(`[e2e='yearly-schedule-yearly-month-week-month']`));
    this.yearlyTab_sec_selectMonth_css = value => element(by.cssContainingText(`span`,`${value}`));
    this.yearlyTab_sec_hourDropdown = element(by.xpath("(//*[@e2e='cron-schedule-hours'])[2]"));
    this.yearlyTab_sec_selectHours_xpath = value => element(by.xpath(`//span[@class='mat-option-text'][text()=' ${value} ']`));
    this.yearlyTab_sec_minutesDropdown = element(by.xpath("(//*[@e2e='cron-schedule-minutes'])[2]"));
    this.yearlyTab_sec_selectMinutes_xpath = value => element(by.xpath(`//span[@class='mat-option-text'][text()=' ${value} ']`));
    this.yearlyTab_sec_timeZoneDropdown = element(by.xpath("(//*[@e2e='cron-schedule-hour-type'])[2]"));
    this.yearlyTab_sec_selectTimezone = value => element(by.xpath(`//span[contains(text(),'${value}')]`));
  }

  /*Method to verify Title in schedule Popup*/
  verifyTitle(title) {
    commonFunctions.waitFor.elementToBeVisible(this._analysisTitle);
    element(
      this._analysisTitle.getText().then(value => {
        if (value) {
          expect(value.trim()).toEqual(title.trim());
        } else {
          expect(false).toBe(
            true,
            'Analysis title cannot be , it was expected to be present but found false'
          );
        }
      })
    );
  }

  /*Method to click on schedule immediately scheckbox*/
  clickOnScheduleImmediatelyCheckBox() {
    commonFunctions.clickOnElement(this._scheduleImmediatelyCheckBox);
  }

  /*Method to click on schedule button*/
  clickOnScheduleButton() {
    commonFunctions.clickOnElement(this._scheduleButton);
  }

  /*Method to set Email address in schedule popup*/
  setEmailAs(userEmail) {
    commonFunctions.fillInput(this._emailInputBox, userEmail);
  }

  /*Method to click Publish inside FTP Dropdown */
  clickPublishToFTPDropdown() {
    commonFunctions.clickOnElement(this._ftpLocationDropdwonButton);
  }

  /*Method to select FTP test checkbox */
  selectFTPTestCheckBox() {
    commonFunctions.clickOnElement(this._ftpCheckBox);
    browser.actions().sendKeys(protractor.Key.TAB).perform().then(()=>{
      //console.log("Performed keyboard action successfully")
    },(err)=>{
      console.log(err);
    });
  }

  /*Method to handle toast message if present*/
  handleDeleteToastMessageIfPresent(message){
    this._deleteToastMessage(message).isDisplayed().then(()=>{
      commonFunctions.clickOnElement(this._deleteToastMessage(message));
    }).catch(()=>{
      //console.log('Toast Message did not display');
    });
  }

  handleToastMessageIfPresent(message){
    this._toastMessage.isDisplayed().then(()=>{
      commonFunctions.clickOnElement(this._toastMessage);
    }).catch(()=>{
      //console.log('Toast Message did not display');
    });
  }

  /*Method to verify Toast message*/
  verifyToastMessagePresent(message) {
    commonFunctions.waitFor.elementToBeVisible(this._toastMessage(message));
  }

  /*Method to click on Toast message*/
  clickOnToastMessage(message) {
    commonFunctions.clickOnElement(this._toastMessage(message));
  }

  /*Method to select XLSX format radio checkbox*/
  clickXLSXFormatRadioButton() {
    commonFunctions.clickOnElement(this.xlsxRadioButton);
  }

  /*Method to select ZIP compress file checkbox*/
  clickZipCompressFileCheckBox() {
    commonFunctions.clickOnElement(this.zipCompressTheFile);
  }

  /*Method to click on Hourly Tab*/
  clickOnHourlyTab() {
    commonFunctions.clickOnElement(this.hourlyTab);
    browser.sleep(5000);

  }

  /*Method to click on Every Hour Dropdown*/
  clickEveryHourDropdown() {
    commonFunctions.clickOnElement(this.hourlyTab_everyHourDropdown);
  }

  /*Method to select ZERO from Hours dropdown*/
  selectZeroFromEveryHourDropdown() {
    browser.actions().sendKeys(protractor.Key.ENTER).perform();
    //commonFunctions.clickOnElement(this.hourlyTab_zeroHour);
  }

  /*Method to click on Minute dropdown*/
  clickMinuteDropdown() {
    commonFunctions.clickOnElement(this.hourlyTab_minutesDropdown);
  }

  /*Method to select ONE from Minute dropdown*/
  selectOneFromMinuteDropdown() {
    /*commonFunctions.waitFor.elementToBeClickable(this.hourlyTab_oneMinute);
    commonFunctions.clickOnElement(this.hourlyTab_oneMinute);*/
    browser.actions().sendKeys(protractor.Key.ARROW_DOWN).perform();
    browser.actions().sendKeys(protractor.Key.ENTER).perform();
  }

  /*Method to click on Daily Tab*/
  clickOnDailyTab() {
    commonFunctions.clickOnElement(this.dailyTab);
    browser.sleep(5000);
  }

  /*Method to click on Everyday checkbox*/
  clickOnEveryDayCheckbox() {
    commonFunctions.clickOnElement(this.dailyTab_everDayCheckbox);
  }

  /*Method to click on Days Dropdown*/
  clickOnDaysDropdown() {
    commonFunctions.clickOnElement(this.dailyTab_everyDay_daysDropdown);
  }

  /*Method to Select ONE from days dropdown*/
  selectOneFromDaysDropdown() {
    //commonFunctions.clickOnElement(this.dailyTab_everyDay_oneDay);
    browser.actions().sendKeys(protractor.Key.ENTER).perform();
  }

  /*Method to click on Hours Dropdown*/
  clickOnHoursDropdown() {
    commonFunctions.clickOnElement(this.dailyTab_everyDay_hourDropdown);
    commonFunctions.waitFor.elementToBeVisible(this._overlayPane);
  }

  /*Method to select Minutes based on current date & Time*/
  selectHoursFromDropdown(value) {
    commonFunctions.clickOnElement(this.dailyTab_everyDay_selectHours_xpath(value));
  }

  /*Method to click on Minutes Dropdown*/
  clickOnMinutesDropdown() {
    commonFunctions.clickOnElement(this.dailyTab_everyDay_minutesDropdown);
    commonFunctions.waitFor.elementToBeVisible(this._overlayPane);
  }

  /*Method to select Minutes based on current date & Time*/
  selectMinutesFromFirstDropdown(value) {
    commonFunctions.clickOnElement(this.dailyTab_everyDay_selectMinutes_xpath(value));
  }

  /*Method to select Minutes based on current date & Time*/
  DailyTab_selectHoursMinutesFromFirstRow(minutes) {
    console.log(minutes);
    if (minutes >= 58) {
      const hrs = parseInt(dateFormat(new Date(), "hh"));
      if (hrs === 12) {
        this.clickOnHoursDropdown();
        this.selectHoursFromDropdown('1');
        this.clickOnMinutesDropdown();
        this.selectMinutesFromFirstDropdown(2);
      } else {
        this.clickOnHoursDropdown();
        this.selectHoursFromDropdown(parseInt(dateFormat(new Date(), "hh")) + 1);
        this.clickOnMinutesDropdown();
        this.selectMinutesFromFirstDropdown(2);
      }
    } else if (minutes === 0) {
      this.clickOnHoursDropdown();
      this.selectHoursFromDropdown(parseInt(dateFormat(new Date(), "hh")));
      this.clickOnMinutesDropdown();
      this.selectMinutesFromFirstDropdown(3);
    } else {
      this.clickOnHoursDropdown();
      this.selectHoursFromDropdown(parseInt(dateFormat(new Date(), "hh")));
      this.clickOnMinutesDropdown();
      this.selectMinutesFromFirstDropdown(minutes);
    }
  }

  /*Method to click on Timezone Dropdown*/
  clickTimezoneDropdown() {
    commonFunctions.clickOnElement(this.dailyTab_everyDay_timeZoneDropdown);
    commonFunctions.waitFor.elementToBeVisible(this._overlayPane);
  }

  /*Method to select Timezone based on current date & Time*/
  selectTimezoneFromDropdown(value) {
    commonFunctions.clickOnElement(this.dailyTab_everyDay_timeZone(value));
  }

  /*Method to click on every Weekday checkbox*/
  clickOnEveryWeekDayCheckbox() {
    commonFunctions.clickOnElement(this.dailyTab_everDayCheckbox);
    browser.sleep(2000);
    browser.actions().sendKeys(protractor.Key.ARROW_DOWN).perform();
  }

  /*Method to click on every week day hours dropdown*/
  clickOnEveryWeekDayHoursDropdown() {
    commonFunctions.clickOnElement(this.dailyTab_everyWeekDay_hourDropdown);
    commonFunctions.waitFor.elementToBeVisible(this._overlayPane);
  }

  /*Method to click on every week day minutes dropdown*/
  clickOnEveryWeekDayMinutesDropdown() {
    commonFunctions.clickOnElement(this.dailyTab_everyWeekDay_minutesDropdown);
    commonFunctions.waitFor.elementToBeVisible(this._overlayPane);
  }

  /*Method to click on every week day timezone dropdown*/
  clickOnEveryWeekDayTimeZoneDropdown() {
    commonFunctions.clickOnElement(this.dailyTab_everyWeekDay_timeZoneDropdown);
    commonFunctions.waitFor.elementToBeVisible(this._overlayPane);
  }

  /*Method to select Minutes based on current date & Time*/
  DailyTab_selectHoursMinutesFromSecondRow(minutes) {
    console.log(minutes);
    if (minutes >= 58) {
      const hrs = parseInt(dateFormat(new Date(), "hh"));
      if (hrs === 12) {
        this.clickOnEveryWeekDayHoursDropdown();
        this.selectHoursFromDropdown('1');
        this.clickOnEveryWeekDayMinutesDropdown();
        this.selectMinutesFromFirstDropdown(2);
      } else {
        this.clickOnEveryWeekDayHoursDropdown();
        this.selectHoursFromDropdown(parseInt(dateFormat(new Date(), "hh")) + 1);
        this.clickOnEveryWeekDayMinutesDropdown();
        this.selectMinutesFromFirstDropdown(2);
      }
    } else if (minutes === 0) {
      this.clickOnEveryWeekDayHoursDropdown();
      this.selectHoursFromDropdown(parseInt(dateFormat(new Date(), "hh")));
      this.clickOnEveryWeekDayMinutesDropdown();
      this.selectMinutesFromFirstDropdown(3);
    } else {
      this.clickOnEveryWeekDayHoursDropdown();
      this.selectHoursFromDropdown(parseInt(dateFormat(new Date(), "hh")));
      this.clickOnEveryWeekDayMinutesDropdown();
      this.selectMinutesFromFirstDropdown(minutes);
    }
  }
  /*==============================================MONTHLY TAB OBJECTS=======================================*/

  /*Method to click on weekly Tab*/
  clickOnWeeklyTab() {
    commonFunctions.clickOnElement(this.weeklyTab);
    browser.sleep(5000);
  }

  /*Method to click on specific day checkbox in weekly tab*/
  WeeklyTab_selectSpecificDayCheckboxInWeeklyTab(value) {
    commonFunctions.clickOnElement(this.weeklyTab_selectDayCheckbox(value));
  }

  /*Method to click on weekly tab hours dropdown*/
  WeeklyTab_clickOnHoursDropdown() {
    commonFunctions.clickOnElement(this.weeklyTab_hourDropdown);
    commonFunctions.waitFor.elementToBeVisible(this._overlayPane);
  }

  /*Method to select Hours based on current date & Time*/
  WeeklyTab_selectHoursFromDropDown(value) {
    commonFunctions.clickOnElement(this.weeklyTab_selectHours_xpath(value));
  }

  /*Method to click on weekly tab minutes dropdown*/
  WeeklyTab_clickOnMinutesDropdown() {
    commonFunctions.clickOnElement(this.weeklyTab_minutesDropdown);
    commonFunctions.waitFor.elementToBeVisible(this._overlayPane);
  }

  /*Method to Select weekly tab minutes dropdown*/
  WeeklyTab_selectMinutesDropdown(minutes) {
    commonFunctions.clickOnElement(this.weeklyTab_selectMinutes_xpath(minutes));
  }


  /*Method to select Minutes based on current date & Time*/
  WeeklyTab_selectMinutesHoursFromDropdown(minutes) {
    console.log(minutes);
    if (minutes >= 58) {
      const hrs = parseInt(dateFormat(new Date(), "hh"));
      if (hrs === 12) {
        this.WeeklyTab_clickOnHoursDropdown();
        this.WeeklyTab_selectHoursFromDropDown('1');
        this.WeeklyTab_clickOnMinutesDropdown();
        this.WeeklyTab_selectMinutesDropdown(2);
      } else {
        this.WeeklyTab_clickOnHoursDropdown();
        this.WeeklyTab_selectHoursFromDropDown(parseInt(dateFormat(new Date(), "hh")) + 1);
        this.WeeklyTab_clickOnMinutesDropdown();
        this.WeeklyTab_selectMinutesDropdown(2);
      }
    } else if (minutes === 0) {
      this.WeeklyTab_clickOnHoursDropdown();
      this.WeeklyTab_selectHoursFromDropDown(parseInt(dateFormat(new Date(), "hh")));
      this.WeeklyTab_clickOnMinutesDropdown();
      this.WeeklyTab_selectMinutesDropdown(3);
    } else {
      this.WeeklyTab_clickOnHoursDropdown();
      this.WeeklyTab_selectHoursFromDropDown(parseInt(dateFormat(new Date(), "hh")));
      this.WeeklyTab_clickOnMinutesDropdown();
      this.WeeklyTab_selectMinutesDropdown(minutes);
    }
  }


  /*Method to click on weekly tab timezone dropdown*/
  WeeklyTab_clickOnTimezoneDropdown() {
    commonFunctions.clickOnElement(this.weeklyTab_timeZoneDropdown);
  }

  /*Method to select Timezone based on current date & Time*/
  WeeklyTab_selectTimezoneFromDropdown(value) {
    commonFunctions.clickOnElement(this.weeklyTab_selectTimezone(value));
  }

  /*==============================================MONTHLY TAB OBJECTS=======================================*/
  /*Method to click on Monthly Tab*/
  clickOnMonthlyTab() {
    commonFunctions.clickOnElement(this.navigationIcon);
    commonFunctions.clickOnElement(this.monthlyTab);
    commonFunctions.waitFor.elementToBeNotVisible(this._scheduleImmediatelyCheckBox);
    browser.sleep(5000); // job will check whether analysis has previously scheduled
  }

  /*Method to click on First radio button in Monthly Tab*/
  MonthlyTab_clickOnFirstRadioButton() {
    commonFunctions.clickOnElement(this.monthlyTab_fir_radioButton);
  }

  /*Method to click on First days dropdown*/
  MonthlyTab_clickOnFirstDaysDropdown() {
    commonFunctions.clickOnElement(this.monthlyTab_fir_daysDropdown);
    commonFunctions.waitFor.elementToBeVisible(this._overlayPane);
  }

  /*Method to select Day based on current date & Time*/
  MonthlyTab_selectDayFromFirstDaysDropdown(value) {
    //console.log(value);
    commonFunctions.clickOnElement(this.monthlyTab_fir_selectDay(value));
  }

  /*Method to click on First Month dropdown*/
  MonthlyTab_clickOnFirstMonthsDropdown() {
    commonFunctions.clickOnElement(this.monthlyTab_fir_monthDropdown);
    commonFunctions.waitFor.elementToBeVisible(this._overlayPane);
  }

  /*Method to select month based on current date & Time*/
  MonthlyTab_selectMonthFromFirstMonthDropdown(value) {
    commonFunctions.clickOnElement(this.monthlyTab_fir_selectMonth(value));
  }

  /*Method to click on fir Hours dropdown*/
  MonthlyTab_clickOnFirstHoursDropdown() {
    commonFunctions.clickOnElement(this.monthlyTab_fir_hourDropdown);
    commonFunctions.waitFor.elementToBeVisible(this._overlayPane);
  }

  /*Method to select Hours based on current date & Time*/
  MonthlyTab_selectHoursFromFirstHoursDropdown(value) {
    commonFunctions.clickOnElement(this.monthlyTab_fir_selectHours(value));
  }

  /*Method to click on fir minutes dropdown*/
  MonthlyTab_clickOnFirstMinutesDropdown() {
    commonFunctions.clickOnElement(this.monthlyTab_fir_minutesDropdown);
    commonFunctions.waitFor.elementToBeVisible(this._overlayPane);
  }

  /*Method to select Hours based on current date & Time from Second row*/
  MonthlyTab_selectMinutesFromFirstMinutesDropdown(value) {
    commonFunctions.clickOnElement(this.monthlyTab_fir_selectMinutes(value));
  }


  /*Method to select Minutes based on current date & Time*/
  MonthlyTab_selectHoursMinutesFromFirstRow(minutes) {
    if (minutes >= 58) {
      const hrs = parseInt(dateFormat(new Date(), "hh"));
      if (hrs === 12) {
        this.MonthlyTab_clickOnFirstHoursDropdown();
        this.MonthlyTab_selectHoursFromFirstHoursDropdown('1');
        this.MonthlyTab_clickOnFirstMinutesDropdown();
        this.MonthlyTab_selectMinutesFromFirstMinutesDropdown(2);
      } else {
        this.MonthlyTab_clickOnFirstHoursDropdown();
        this.MonthlyTab_selectHoursFromFirstHoursDropdown(parseInt(dateFormat(new Date(), "hh")) + 1);
        this.MonthlyTab_clickOnFirstMinutesDropdown();
        this.MonthlyTab_selectMinutesFromFirstMinutesDropdown(2);
      }
    } else if (minutes === 0) {
      this.MonthlyTab_clickOnFirstHoursDropdown();
      this.MonthlyTab_selectHoursFromFirstHoursDropdown(parseInt(dateFormat(new Date(), "hh")));
      this.MonthlyTab_clickOnFirstMinutesDropdown();
      this.MonthlyTab_selectMinutesFromFirstMinutesDropdown(3);
    } else {
      this.MonthlyTab_clickOnFirstHoursDropdown();
      this.MonthlyTab_selectHoursFromFirstHoursDropdown(parseInt(dateFormat(new Date(), "hh")));
      this.MonthlyTab_clickOnFirstMinutesDropdown();
      this.MonthlyTab_selectMinutesFromFirstMinutesDropdown(minutes);
    }
  }

  /*Method to click on fir timezone dropdown*/
  MonthlyTab_clickOnFirstTimezoneDropdown() {
    commonFunctions.clickOnElement(this.monthlyTab_fir_timeZoneDropdown);
    commonFunctions.waitFor.elementToBeVisible(this._overlayPane);
  }

  /*Method to select timezone based on current date & Time*/
  MonthlyTab_selectTimeZoneFromFirstTimezoneDropdown(value) {
    commonFunctions.clickOnElement(this.monthlyTab_fir_selectTimezone(value));
  }

  /*Method to click on sec radio button in Monthly Tab*/
  MonthlyTab_clickOnSecondRadioButton() {
    commonFunctions.clickOnElement(this.monthlyTab_fir_radioButton);
    browser.sleep(2000);
    browser.actions().sendKeys(protractor.Key.ARROW_DOWN).perform();
  }

  /*Method to click on Weeks dropdown*/
  MonthlyTab_clickOnWeeksDropdown() {
    commonFunctions.clickOnElement(this.monthlyTab_sec_weekDropDown);
    commonFunctions.waitFor.elementToBeVisible(this._overlayPane);
  }

  /*Method to select Week based on current date & Time*/
  MonthlyTab_selectWeekFromWeeksDropdown(value) {
    commonFunctions.clickOnElement(this.monthlyTab_sec_selectWeek(value));
  }

  /*Method to click on Second row Days Dropdown*/
  MonthlyTab_clickOnSecondDaysDropdown() {
    commonFunctions.clickOnElement(this.monthlyTab_sec_daysDropdown);
    commonFunctions.waitFor.elementToBeVisible(this._overlayPane);
  }

  /*Method to select Days based on current date & Time from Second row*/
  MonthlyTab_selectDaysFromSecondDaysDropdown(value) {
    commonFunctions.clickOnElement(this.monthlyTab_sec_selectDay(value));
  }

  /*Method to click on Second row Months Dropdown*/
  MonthlyTab_clickOnSecondMonthsDropdown() {
    commonFunctions.clickOnElement(this.monthlyTab_sec_MonthDropdwon);
    commonFunctions.waitFor.elementToBeVisible(this._overlayPane);
  }

  /*Method to select Month based on current date & Time from Second row*/
  MonthlyTab_selectMonthFromSecondMonthDropdown(value) {
    commonFunctions.clickOnElement(this.monthlyTab_sec_selectMonth(value));
  }

  /*Method to click on Second row hours Dropdown*/
  MonthlyTab_clickOnSecondHoursDropdown() {
    commonFunctions.clickOnElement(this.monthlyTab_sec_hourDropdown);
    commonFunctions.waitFor.elementToBeVisible(this._overlayPane);
  }

  /*Method to select Hours based on current date & Time from Second row*/
  MonthlyTab_selectHoursFromSecondHoursDropdown(value) {
    commonFunctions.clickOnElement(this.monthlyTab_sec_selectHours(value));
  }

  /*Method to click on Second row Minutes Dropdown*/
  MonthlyTab_clickOnSecondMinutesDropdown() {
    commonFunctions.clickOnElement(this.monthlyTab_sec_minutesDropdown);
    commonFunctions.waitFor.elementToBeVisible(this._overlayPane);
  }

  /*Method to select Hours based on current date & Time from Second row*/
  MonthlyTab_selectMinutesFromSecondMinutesDropdown(value) {
    commonFunctions.clickOnElement(this.monthlyTab_sec_selectMinutes(value));
  }

  /*Method to select Minutes based on current date & Time*/
  MonthlyTab_selectHoursMinutesFromSecondRow(minutes) {
    console.log(minutes);
    if(minutes >=58) {
      const hrs = parseInt(dateFormat(new Date() , "hh"));
      if(hrs === 12)
      {
        this.MonthlyTab_clickOnSecondHoursDropdown();
        this.MonthlyTab_selectHoursFromSecondHoursDropdown('1');
        this.MonthlyTab_clickOnSecondMinutesDropdown();
        this.MonthlyTab_selectMinutesFromSecondMinutesDropdown(2);
      } else {
        this.MonthlyTab_clickOnSecondHoursDropdown();
        this.MonthlyTab_selectHoursFromSecondHoursDropdown(parseInt(dateFormat(new Date() , "hh"))+1);
        this.MonthlyTab_clickOnSecondMinutesDropdown();
        this.MonthlyTab_selectMinutesFromSecondMinutesDropdown(2);
      }
    }else if (minutes === 0) {
      this.MonthlyTab_clickOnSecondHoursDropdown();
      this.MonthlyTab_selectHoursFromSecondHoursDropdown(parseInt(dateFormat(new Date() , "hh")));
      this.MonthlyTab_clickOnSecondMinutesDropdown();
      this.MonthlyTab_selectMinutesFromSecondMinutesDropdown(3);
    } else {
      this.MonthlyTab_clickOnSecondHoursDropdown();
      this.MonthlyTab_selectHoursFromSecondHoursDropdown(parseInt(dateFormat(new Date() , "hh")));
      this.MonthlyTab_clickOnSecondMinutesDropdown();
      this.MonthlyTab_selectMinutesFromSecondMinutesDropdown(minutes);
    }
  }

  /*Method to click on Second row Timezone Dropdown*/
  MonthlyTab_clickOnSecondTimezoneDropdown() {
    commonFunctions.clickOnElement(this.monthlyTab_sec_timeZoneDropdown);
  }

  /*Method to select Timezone based on current date & Time from Second row*/
  MonthlyTab_selectTimeZoneFromSecondTimezoneDropdown(value) {
    commonFunctions.clickOnElement(this.monthlyTab_sec_selectTimezone(value));
  }

  /*==============================================YEARLY TAB OBJECTS=======================================*/
  /*Method to click on Yearly Tab*/
  clickOnYearlyTab() {
    commonFunctions.clickOnElement(this.navigationIcon);
    commonFunctions.clickOnElement(this.navigationIcon);
    commonFunctions.clickOnElement(this.yearlyTab);
    browser.sleep(5000);
  }

  /*Method to click on First row Radio Button*/
  YearlyTab_clickOnFirstRadioButton() {
    commonFunctions.clickOnElement(this.yearlyTab_fir_radioButton);
  }

  /*Method to click on First row Months Dropdown*/
  YearlyTab_clickOnFirstMonthsDropdown() {
    commonFunctions.clickOnElement(this.yearlyTab_fir_monthDropdown);
  }

  /*Method to select Months based on current date & Time from First row*/
  YearlyTab_selectMonthFromFirstMonthDropdown(value) {
    commonFunctions.clickOnElement(this.yearlyTab_fir_selectMonth_css(value));
  }

  /*Method to click on First row Days Dropdown*/
  YearlyTab_clickOnFirstDaysDropdown() {
    commonFunctions.clickOnElement(this.yearlyTab_fir_daysDropdown);
  }

  /*Method to select Day based on current date & Time from First row*/
  YearlyTab_selectDayFromFirstDaysDropdown(value) {
    commonFunctions.clickOnElement(this.yearlyTab_fir_selectDay_css(value));
  }

  /*Method to click on First row Hours Dropdown*/
  YearlyTab_clickOnFirstHoursDropdown() {
    commonFunctions.clickOnElement(this.yearlyTab_fir_hourDropdown);
  }

  /*Method to select Hours based on current date & Time from First row*/
  YearlyTab_selectHoursFromFirstHoursDropdown(value) {
    commonFunctions.clickOnElement(this.yearlyTab_fir_selectHours_xpath(value));
  }

  /*Method to click on First row Minutes Dropdown*/
  YearlyTab_clickOnFirstMinutesDropdown() {
    commonFunctions.clickOnElement(this.yearlyTab_fir_minutesDropdown);
  }

  /*Method to select Hours based on current date & Time from First row*/
  YearlyTab_selectMinutesFromFirstHoursDropdown(value) {
    commonFunctions.clickOnElement(this.yearlyTab_fir_selectMinutes_xpath(value));
  }

  /*Method to select Minutes based on current date & Time from First row*/
  YearlyTab_selectHoursMinutesFromFirstRow(minutes) {
    console.log(minutes);
    if(minutes >=58) {
      const hrs = parseInt(dateFormat(new Date() , "hh"));
      if(hrs === 12)
      {
        this.YearlyTab_clickOnFirstHoursDropdown();
        this.YearlyTab_selectHoursFromFirstHoursDropdown('1');
        this.YearlyTab_clickOnFirstMinutesDropdown();
        this.YearlyTab_selectMinutesFromFirstHoursDropdown(2);
      } else {
        this.YearlyTab_clickOnFirstHoursDropdown();
        this.YearlyTab_selectHoursFromFirstHoursDropdown(parseInt(dateFormat(new Date() , "hh"))+1);
        this.YearlyTab_clickOnFirstMinutesDropdown();
        this.YearlyTab_selectMinutesFromFirstHoursDropdown(2);
      }
    }else if (minutes === 0) {
      this.YearlyTab_clickOnFirstHoursDropdown();
      this.YearlyTab_selectHoursFromFirstHoursDropdown(parseInt(dateFormat(new Date() , "hh")));
      this.YearlyTab_clickOnFirstMinutesDropdown();
      this.YearlyTab_selectMinutesFromFirstHoursDropdown(3);
    } else {
      this.YearlyTab_clickOnFirstHoursDropdown();
      const regularHrs = parseInt(dateFormat(new Date() , "hh"));
      this.YearlyTab_selectHoursFromFirstHoursDropdown(regularHrs);
      this.YearlyTab_clickOnFirstMinutesDropdown();
      this.YearlyTab_selectMinutesFromFirstHoursDropdown(minutes);
    }
  }

  /*Method to click on First row Timezone Dropdown*/
  YearlyTab_clickOnFirstTimezoneDropdown() {
    commonFunctions.clickOnElement(this.yearlyTab_fir_timeZoneDropdown);
  }

  /*Method to select Timezone based on current date & Time from First row*/
  YearlyTab_selectTimeZoneFromFirstTimezoneDropdown(value) {
    commonFunctions.clickOnElement(this.yearlyTab_fir_selectTimezone(value));
  }

  /*Method to click on Second row Radio Button*/
  YearlyTab_clickOnSecondRadioButton() {
    commonFunctions.clickOnElement(this.yearlyTab_fir_radioButton);
    browser.sleep(3000);
    browser.actions().sendKeys(protractor.Key.ARROW_DOWN).perform();
  }

  /*Method to click on Second row Weeks Dropdown*/
  YearlyTab_clickOnWeeksDropdown() {
    commonFunctions.clickOnElement(this.yearlyTab_sec_weekDropDown);
  }

  /*Method to select Weeks based on current date & Time from Second row*/
  YearlyTab_selectWeekFromWeeksDropdown(value) {
    commonFunctions.clickOnElement(this.yearlyTab_sec_selectWeek_css(value));
  }

  /*Method to click on Second row Days Dropdown*/
  YearlyTab_clickOnSecondDaysDropdown() {
    commonFunctions.clickOnElement(this.yearlyTab_sec_daysDropdown);
  }

  /*Method to select Days based on current date & Time from Second row*/
  YearlyTab_selectDaysFromSecondDaysDropdown(value) {
    commonFunctions.clickOnElement(this.yearlyTab_sec_selectDay_css(value));
  }

  /*Method to click on Second row Months Dropdown*/
  YearlyTab_clickOnSecondMonthsDropdown() {
    commonFunctions.clickOnElement(this.yearlyTab_sec_MonthDropdwon);
  }

  /*Method to select Month based on current date & Time from Second row*/
  YearlyTab_selectMonthFromSecondMonthDropdown(value) {
    commonFunctions.clickOnElement(this.yearlyTab_sec_selectMonth_css(value));
  }

  /*Method to click on Second row Hours Dropdown*/
  YearlyTab_clickOnSecondHoursDropdown() {
    commonFunctions.clickOnElement(this.yearlyTab_sec_hourDropdown);
  }

  /*Method to select Hours based on current date & Time from Second row*/
  YearlyTab_selectHoursFromSecondHoursDropdown(value) {
    commonFunctions.clickOnElement(this.yearlyTab_sec_selectHours_xpath(value));
  }

  /*Method to click on Second row Minutes Dropdown*/
  YearlyTab_clickOnSecondMinutesDropdown() {
    commonFunctions.clickOnElement(this.yearlyTab_sec_minutesDropdown);
  }

  /*Method to select Hours based on current date & Time from Second row*/
  YearlyTab_selectMinutesFromSecondHoursDropdown(value) {
    commonFunctions.clickOnElement(this.yearlyTab_sec_selectMinutes_xpath(value));
  }

  /*Method to select Minutes based on current date & Time from Second row*/
  YearlyTab_selectHoursMinutesFromSecondRow(minutes) {
    console.log(minutes);
    if(minutes >=58) {
      const hrs = parseInt(dateFormat(new Date() , "hh"));
      if(hrs === 12)
      {
        this.YearlyTab_clickOnSecondHoursDropdown();
        this.YearlyTab_selectHoursFromSecondHoursDropdown('1');
        this.YearlyTab_clickOnSecondMinutesDropdown();
        this.YearlyTab_selectMinutesFromSecondHoursDropdown(2);
      } else {
        this.YearlyTab_clickOnSecondHoursDropdown();
        this.YearlyTab_selectHoursFromSecondHoursDropdown(parseInt(dateFormat(new Date() , "hh"))+1);
        this.YearlyTab_clickOnSecondMinutesDropdown();
        this.YearlyTab_selectMinutesFromSecondHoursDropdown(2);
      }
    }else if (minutes === 0) {
      this.YearlyTab_clickOnSecondHoursDropdown();
      this.YearlyTab_selectHoursFromSecondHoursDropdown(parseInt(dateFormat(new Date() , "hh")));
      this.YearlyTab_clickOnSecondMinutesDropdown();
      this.YearlyTab_selectMinutesFromSecondHoursDropdown(3);
    } else {
      this.YearlyTab_clickOnSecondHoursDropdown();
      this.YearlyTab_selectHoursFromSecondHoursDropdown(parseInt(dateFormat(new Date() , "hh")));
      this.YearlyTab_clickOnSecondMinutesDropdown();
      this.YearlyTab_selectMinutesFromSecondHoursDropdown(minutes);
    }
  }

  /*Method to click on Second row Timezone Dropdown*/
  YearlyTab_clickOnSecondTimezoneDropdown() {
    commonFunctions.clickOnElement(this.yearlyTab_sec_timeZoneDropdown);
  }

  /*Method to select Timezone based on current date & Time from Second row*/
  YearlyTab_selectTimeZoneFromSecondTimezoneDropdown(value) {
    commonFunctions.clickOnElement(this.yearlyTab_sec_selectTimezone(value));
  }

}
module.exports = SchedulePage;
