'use strict';
const logger = require('../conf/logger')(__filename);
const commonFunctions = require('./utils/commonFunctions');

class SchedulePage {
  constructor() {
    this._analysisTitle = element(by.css(`[e2e="schedule-analysis-header"]`));
    this._scheduleImmediatelyCheckBox = element(by.css(`[e2e="immediate-schedule"]`));
    this._scheduleButton = element(by.css(`[e2e='schedule-analysis-publish']`));
    this._emailInputBox = element(by.css(`[e2e='email-list-input']`));
    this._toastMessage = element(by.css(`[class='toast-message']`));
    this._removeSchedule = element(by.css(`[e2e="remove-schedule-analysis"]`));
    this._invalidScheduleErrorMessage = element(by.css(`[e2e="invalid-schedule-msg"]`));
    this._invalidOptionErrorMessage = element(by.css(`[e2e="invalid-option-msg"]`));
    this._closeOption = element(by.css(`[fonticon="icon-close"]`));

    //Elements in Hourly Tab
    this._hourlyTab = element(by.xpath("//div[text()='Hourly']"));
    this._hourly_everyHour = element(by.css('[e2e="hourly-schedule-hourly-hours"]'));
    this._selectHrsMinTs = value=> element(by.xpath(`//span[text()=' ${value} ']`));
    this._hourly_minutes = element(by.css('[e2e="hourly-schedule-hourly-minutes"]'));
    this._hourly_timeZone = element(by.css(`[e2e='cron-schedule-hour-type']`));

    /*Elements in Daily Tab*/
    this._dailyTab = element(by.xpath(`//div[text()='Daily']`));
    this._everDayRow = element(by.css(`[e2e='daily-schedule-everyday']`));
    this._everDayCheckbox = this._everDayRow.element(by.css(`[class='mat-radio-label']`));
    this._everyDay_days = element(by.css(`[e2e="daily-schedule-daily-days"]`));
    this._everyDay_selectDays = value=> element(by.xpath(`//span[text()=' ${value} ']`));
    this._dailyFirstRow = element(by.css(`[e2e="daily-cron-date-picker-everyday"]`));
    this._everyDay_hours = this._dailyFirstRow.element(by.css(`[e2e='cron-schedule-hours']`));
    this._everyDay_minutes = this._dailyFirstRow.element(by.css(`[e2e='cron-schedule-minutes']`));
    this._everyDay_timeStamp = this._dailyFirstRow.element(by.css(`[e2e='cron-schedule-hour-type']`));

    this._everyWeekDayRow = element(by.css(`[e2e='daily-schedule-everyweekday']`));
    this._everWeekDayCheckbox = this._everyWeekDayRow.element(by.css(`[class='mat-radio-label']`));
    this._dailySecondRow = element(by.css(`[e2e="daily-cron-date-picker-everyweekday"]`));
    this._everyWeekDay_hours = this._dailySecondRow.element(by.css(`[e2e='cron-schedule-hours']`));
    this._everyWeekDay_minutes = this._dailySecondRow.element(by.css(`[e2e='cron-schedule-minutes']`));
    this._everyWeekDay_timeStamp = this._dailySecondRow.element(by.css(`[e2e='cron-schedule-hour-type']`));


    /*Elements in Weekly Tab*/
    this._weeklyTab = element(by.xpath(`//div[text()='Weekly']`));
    this._WeekField = (dayName) => element(by.css(`[e2e="weeklybasis-schedule-${dayName}"]`));
    this._specificDayOfWeekCheckBox = (dayName) => this._WeekField(dayName).element(by.css(`[class='mat-checkbox-inner-container']`));
    this._weeklyRow = element(by.css(`[e2e="weeklybasis-cron-date-picker-week-basis-date"]`));
    this._weekly_hours = this._weeklyRow.element(by.css(`[e2e='cron-schedule-hours']`));
    this._weekly_minutes = this._weeklyRow.element(by.css(`[e2e='cron-schedule-minutes']`));
    this._weekly_timeStamp = this._weeklyRow.element(by.css(`[e2e='cron-schedule-hour-type']`));

    /*Elements in Monthly Tab*/
    this._navigationIcon = element(by.xpath("//div[contains(@class,'pagination-after')]"));
    this._monthlyTab = element(by.xpath(`//div[text()='Monthly']`));
    this._monthly_FirstRadiobutton = element(by.css(`[class="everyDay mat-radio-button mat-accent"]`));
    this._monthly_FirstCheckbox = this._monthly_FirstRadiobutton.element(by.css(`[class="mat-radio-container"]`));
    this._monthly_FirstRowDays = element(by.css(`[e2e="monthly-schedule-monthly-day"]`));
    this._monthly_FirstRowMonths = element(by.css(`[e2e="monthly-schedule-monthly-month"]`));
    this._monthlyFirstRow = element(by.css(`[e2e="monthly-cron-date-picker-day-month"]`));
    this._monthlyFirst_hours = this._monthlyFirstRow.element(by.css(`[e2e='cron-schedule-hours']`));
    this._monthlyFirst_minutes = this._monthlyFirstRow.element(by.css(`[e2e='cron-schedule-minutes']`));
    this._monthlyFirst_timeStamp = this._monthlyFirstRow.element(by.css(`[e2e='cron-schedule-hour-type']`));


    this._monthly_SecondRadiobutton = element(by.css(`[e2e="monthly-schedule-everyweek"]`));
    this._monthlySecondCheckbox = this._monthly_SecondRadiobutton.element(by.css(`[class="mat-radio-container"]`));
    this._monthly_Week = element(by.css(`[e2e="monthly-schedule-monthly-week-day-month"]`));
    this._monthly_SecondRowDays = element(by.css(`[e2e="monthly-schedule-monthly-week-day-day"]`));
    this._monthly_SecondRowMonths = element(by.css(`[e2e="monthly-schedule-monthly-week-day-month-week"]`));
    this._monthlySecondRow = element(by.css(`[e2e="monthly-cron-date-picker-week-day-month"]`));
    this._monthlySecond_hours = this._monthlySecondRow.element(by.css(`[e2e='cron-schedule-hours']`));
    this._monthlySecond_minutes = this._monthlySecondRow.element(by.css(`[e2e='cron-schedule-minutes']`));
    this._monthlySecond_timeStamp = this._monthlySecondRow.element(by.css(`[e2e='cron-schedule-hour-type']`));

    /*Elements in Yearly Tab*/
    this._yearlyTab = element(by.xpath(`//div[text()='Yearly']`));
    this._yearly_FirstRadioButton = element(by.css(`[e2e="yearly-cron-schedule-everyday"]`));
    this._yearly_FirstCheckbox = this._yearly_FirstRadioButton.element(by.css(`[class="mat-radio-container"]`));
    this._yearly_FirstRowMonth = element(by.css(`[e2e="yearly-schedule-yearly-month-day-month"]`));
    this._yearly_FirstRowDays = element(by.css(`[e2e="yearly-schedule-yearly-month-day-day"]`));
    this._yearlyFirstRow = element(by.css(`[e2e="yearly-cron-date-picker-month-day-year"]`));
    this._yearlyFirst_hours = this._yearlyFirstRow.element(by.css(`[e2e='cron-schedule-hours']`));
    this._yearlyFirst_minutes = this._yearlyFirstRow.element(by.css(`[e2e='cron-schedule-minutes']`));
    this._yearlyFirst_timeStamp = this._yearlyFirstRow.element(by.css(`[e2e='cron-schedule-hour-type']`));

    this._yearly_SecondRadioButton = element(by.css(`[e2e="yearly-schedule-everyweek"]`));
    this._yearly_SecondCheckbox = this._yearly_SecondRadioButton.element(by.css(`[class="mat-radio-container"]`));
    this._yearly_SecondRowWeeks = element(by.css(`[e2e="yearly-schedule-yearly-month-week-month-week"]`));
    this._yearly_SecondRowDays = element(by.css(`[e2e="yearly-schedule-yearly-month-week-day"]`));
    this._yearly_SecondRowMonth = element(by.css(`[e2e='yearly-schedule-yearly-month-week-month']`));
    this._yearlySecondRow = element(by.css(`[e2e="yearly-cron-date-picker-month-week-year"]`));
    this._yearlySecond_hours = this._yearlySecondRow.element(by.css(`[e2e='cron-schedule-hours']`));
    this._yearlySecond_minutes = this._yearlySecondRow.element(by.css(`[e2e='cron-schedule-minutes']`));
    this._yearlySecond_timeStamp = this._yearlySecondRow.element(by.css(`[e2e='cron-schedule-hour-type']`));

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

  removeSchedule(){
    browser.actions().mouseDown().perform();
    commonFunctions.clickOnElement(this._removeSchedule);
  }

  ScheduleImmediately() {
    commonFunctions.clickOnElement(this._scheduleImmediatelyCheckBox);
  }

  scheduleReport() {
    browser.actions().mouseDown().perform();
    commonFunctions.clickOnElement(this._scheduleButton);
    browser.sleep(2000);
  }

  setEmail(userEmail) {
    commonFunctions.fillInput(this._emailInputBox, userEmail);
  }

  handleToastMessage(){
    this._toastMessage.isDisplayed().then(()=>{
      commonFunctions.clickOnElement(this._toastMessage);
    }).catch(()=>{
      logger.debug('Toast Message did not display');
    });
  }

  /*----------------------------------HOURLY TAB OBJECTS----------------------------------------------------------*/

  selectHourlyTab() {
    commonFunctions.clickOnElement(this._hourlyTab);
    browser.sleep(2000); //Need to wait till hourly popup loads
  }

  clickEveryHour() {
    commonFunctions.clickOnElement(this._hourly_everyHour);
  }

  selectHours(hours) {
    commonFunctions.clickOnElement(this._selectHrsMinTs(hours));
  }

  clickMinutes() {
    commonFunctions.clickOnElement(this._hourly_minutes);
  }

  selectMinutes(minutes) {
    commonFunctions.clickOnElement(this._selectHrsMinTs(minutes));
  }

  clickTimeStamp() {
    commonFunctions.clickOnElement(this._hourly_timeZone);
  }

  /*Method to select Timezone based on current date & Time*/
  selectTimeStamp(timeZone) {
    commonFunctions.clickOnElement(this._selectHrsMinTs(timeZone));
  }
  /*----------------------------------DAILY TAB OBJECTS----------------------------------------------------------*/

  selectDailyTab() {
    commonFunctions.clickOnElement(this._dailyTab);
    commonFunctions.waitFor.elementToBeNotVisible(this._scheduleImmediatelyCheckBox);
  }

  selectEveryDayCheckbox() {
    commonFunctions.clickOnElement(this._everDayCheckbox);
  }

  clickDays() {
    commonFunctions.clickOnElement(this._everyDay_days);
  }

  selectDays(day) {
    commonFunctions.clickOnElement(this._everyDay_selectDays(day));
  }

  clickEveryDayHours() {
    commonFunctions.clickOnElement(this._everyDay_hours);
  }

  clickEveryDayMinutes() {
    commonFunctions.clickOnElement(this._everyDay_minutes);
  }

  clickEveryDayTimeStamp() {
    commonFunctions.clickOnElement(this._everyDay_timeStamp);
  }

  selectEveryWeekDayCheckbox() {
    commonFunctions.clickOnElement(this._everWeekDayCheckbox);
  }

  clickEveryWeekDayHours() {
    commonFunctions.clickOnElement(this._everyWeekDay_hours);
  }

  clickEveryWeekDayMinutes() {
    commonFunctions.clickOnElement(this._everyWeekDay_minutes);
  }

  clickEveryWeekDayTimeStamp() {
    commonFunctions.clickOnElement(this._everyWeekDay_timeStamp);
  }

  /*----------------------------------WEEKLY TAB OBJECTS----------------------------------------------------------*/
  selectWeeklyTab() {
    commonFunctions.clickOnElement(this._weeklyTab);
    commonFunctions.waitFor.elementToBeNotVisible(this._scheduleImmediatelyCheckBox);
  }

  selectSpecificDayOfWeekCheckBox(value) {
    commonFunctions.clickOnElement(this._specificDayOfWeekCheckBox(value));
  }

  clickOnWeeklyHours() {
    commonFunctions.clickOnElement(this._weekly_hours);
  }

  clickOnWeeklyMinutes() {
    commonFunctions.clickOnElement(this._weekly_minutes);
  }

  clickOnWeeklyTimeStamp() {
    commonFunctions.clickOnElement(this._weekly_timeStamp);
  }

  /*----------------------------------MONTHLY TAB OBJECTS----------------------------------------------------------*/

  selectMonthlyTab() {
    commonFunctions.clickOnElement(this._navigationIcon);
    commonFunctions.clickOnElement(this._monthlyTab);
    commonFunctions.waitFor.elementToBeNotVisible(this._scheduleImmediatelyCheckBox);
  }

  selectMonthlyFirstCheckbox() {
    commonFunctions.clickOnElement(this._monthly_FirstCheckbox);
  }

  clickOnMonthlyFirstRowDays() {
    commonFunctions.clickOnElement(this._monthly_FirstRowDays);
  }

  selectMonthlyFirstRowDay(day) {
    commonFunctions.clickOnElement(this._selectHrsMinTs(day));
  }

  clickOnMonthlyFirstRowMonths() {
    commonFunctions.clickOnElement(this._monthly_FirstRowMonths);
  }

  selectMonthlyFirstRowMonth(month) {
    commonFunctions.clickOnElement(this._selectHrsMinTs(month));
  }

  clickOnMonthlyFirstRowHours() {
    commonFunctions.clickOnElement(this._monthlyFirst_hours);
  }

  clickOnMonthlyFirstRowMinutes() {
    commonFunctions.clickOnElement(this._monthlyFirst_minutes);
  }

  clickOnMonthlyFirstRowTimeStamp() {
    commonFunctions.clickOnElement(this._monthlyFirst_timeStamp);
  }

  selectMonthlySecondCheckbox() {
    commonFunctions.clickOnElement(this._monthlySecondCheckbox);
  }

  clickOnMonthlySecondRowWeeks() {
    commonFunctions.clickOnElement(this._monthly_Week);
  }

  selectMonthlySecondRowWeeks(week) {
    commonFunctions.clickOnElement(this._selectHrsMinTs(week));
  }

  clickOnMonthlySecondRowDay() {
    commonFunctions.clickOnElement(this._monthly_SecondRowDays);
  }

  selectMonthlySecondRowDay(day) {
    commonFunctions.clickOnElement(this._selectHrsMinTs(day));
  }

  clickOnMonthlySecondRowMonth() {
    commonFunctions.clickOnElement(this._monthly_SecondRowMonths);
  }

  selectMonthlySecondRowMonth(month) {
    commonFunctions.clickOnElement(this._selectHrsMinTs(month));
  }

  clickOnMonthlySecondRowHours() {
    commonFunctions.clickOnElement(this._monthlySecond_hours);
  }

  clickOnMonthlySecondRowMinutes() {
    commonFunctions.clickOnElement(this._monthlySecond_minutes);
  }

  clickOnMonthlySecondRowTimeStamp() {
    commonFunctions.clickOnElement(this._monthlySecond_timeStamp);
  }

  /*----------------------------------YEARLY TAB OBJECTS----------------------------------------------------------*/

  selectYearlyTab() {
    commonFunctions.clickOnElement(this._navigationIcon);
    commonFunctions.clickOnElement(this._navigationIcon);
    commonFunctions.clickOnElement(this._yearlyTab);
    commonFunctions.waitFor.elementToBeNotVisible(this._scheduleImmediatelyCheckBox);
  }

  selectYearlyFirstCheckbox() {
    commonFunctions.clickOnElement(this._yearly_FirstCheckbox);
  }

  clickOnYearlyFirstRowMonth() {
    commonFunctions.clickOnElement(this._yearly_FirstRowMonth);
  }

  selectYearlyFirstRowMonth(month) {
    commonFunctions.clickOnElement(this._selectHrsMinTs(month));
  }

  clickOnYearlyFirstRowDays() {
    commonFunctions.clickOnElement(this._yearly_FirstRowDays);
  }

  selectYearlyFirstRowDays(days) {
    commonFunctions.clickOnElement(this._selectHrsMinTs(days));
  }

  clickOnYearlyFirstRowHours() {
    commonFunctions.clickOnElement(this._yearlyFirst_hours);
  }

  selectYearlyFirstRowHours(hrs) {
    commonFunctions.clickOnElement(this._selectHrsMinTs(hrs));
  }

  clickOnYearlyFirstRowMinutes() {
    commonFunctions.clickOnElement(this._yearlyFirst_minutes);
  }

  selectYearlyFirstRowMinutes(minutes) {
    commonFunctions.clickOnElement(this._selectHrsMinTs(minutes));
  }

  clickOnYearlyFirstRowTimeStamp() {
    commonFunctions.clickOnElement(this._yearlyFirst_timeStamp);
  }

  selectYearlyFirstRowTimeStamp(timeStamp) {
    commonFunctions.clickOnElement(this._selectHrsMinTs(timeStamp));
  }

  selectYearlySecondCheckbox() {
    commonFunctions.clickOnElement(this._yearly_SecondCheckbox);
  }

  clickOnYearlySecondRowWeeks() {
    commonFunctions.clickOnElement(this._yearly_SecondRowWeeks);
  }

  selectYearlySecondRowWeeks(week) {
    commonFunctions.clickOnElement(this._selectHrsMinTs(week));
  }

  clickOnYearlySecondRowDay() {
    commonFunctions.clickOnElement(this._yearly_SecondRowDays);
  }

  selectYearlySecondRowDay(day) {
    commonFunctions.clickOnElement(this._selectHrsMinTs(day));
  }

  clickOnYearlySecondRowMonth() {
    commonFunctions.clickOnElement(this._yearly_SecondRowMonth);
  }

  selectYearlySecondRowMonth(month) {
    commonFunctions.clickOnElement(this._selectHrsMinTs(month));
  }

  clickOnYearlySecondRowHours() {
    commonFunctions.clickOnElement(this._yearlySecond_hours);
  }

  selectYearlySecondRowHours(hours) {
    commonFunctions.clickOnElement(this._selectHrsMinTs(hours));
  }

  clickOnYearlySecondRowMinutes() {
    commonFunctions.clickOnElement(this._yearlySecond_minutes);
  }

  selectYearlySecondRowMinutes(Minutes) {
    commonFunctions.clickOnElement(this._selectHrsMinTs(Minutes));
  }

  clickOnYearlySecondRowTimeStamp() {
    commonFunctions.clickOnElement(this._yearlySecond_timeStamp);
  }

  selectYearlySecondRowTimeStamp(TimeStamp) {
    commonFunctions.clickOnElement(this._selectHrsMinTs(TimeStamp));
  }

  verifyInvalidScheduleErrorMessage(errorMessage) {
    commonFunctions.waitFor.elementToBeVisible(this._invalidScheduleErrorMessage);
    element(
      this._invalidScheduleErrorMessage.getText().then(value => {
        if (value) {
          expect(value.trim()).toEqual(errorMessage.trim());
        } else {
          expect(false).toBe(
            true,
            'Error Message validation in not successful'
          );
        }
      })
    );
  }

  verifyInvalidOptionErrorMessage(errorMessage) {
    commonFunctions.waitFor.elementToBeVisible(this._invalidOptionErrorMessage);
    element(
      this._invalidOptionErrorMessage.getText().then(value => {
        if (value) {
          expect(value.trim()).toEqual(errorMessage.trim());
        } else {
          expect(false).toBe(
            true,
            'Error Message validation in not successful'
          );
        }
      })
    );
  }

  closeSchedule() {
    commonFunctions.clickOnElement(this._closeOption);
  }

  hourlySchedule(hours,minutes) {
    this.selectHourlyTab();
    this.clickEveryHour();
    this.selectHours(hours);
    this.clickMinutes();
    this.selectMinutes(minutes);
  }

  dailyEverydaySchedule(days,hours,minutes,timeStamp){
    this.selectDailyTab();
    this.selectEveryDayCheckbox();
    this.clickDays();
    this.selectDays(days);
    this.clickEveryDayHours();
    this.selectHours(hours);
    this.clickEveryDayMinutes();
    this.selectMinutes(minutes);
    this.clickEveryDayTimeStamp();
    this.selectTimeStamp(timeStamp);
  }

  dailyEveryWeekDaySchedule(hours,minutes,timeStamp) {
    this.selectDailyTab();
    this.selectEveryWeekDayCheckbox();
    this.clickEveryWeekDayHours();
    this.selectHours(hours);
    this.clickEveryWeekDayMinutes();
    this.selectMinutes(minutes);
    this.clickEveryWeekDayTimeStamp();
    this.selectTimeStamp(timeStamp);
  }

  weeklySchedule(dayname,hours,minutes,timeStamp) {
    this.selectWeeklyTab();
    this.selectSpecificDayOfWeekCheckBox(dayname);
    this.clickOnWeeklyHours();
    this.selectHours(hours);
    this.clickOnWeeklyMinutes();
    this.selectMinutes(minutes);
    this.clickOnWeeklyTimeStamp();
    this.selectTimeStamp(timeStamp);
  }

  monthlyOnTheDaySchedule(day,month,hours,minutes,timeStamp) {
    this.selectMonthlyTab();
    this.selectMonthlyFirstCheckbox();
    this.clickOnMonthlyFirstRowDays();
    this.selectMonthlyFirstRowDay(day);
    this.clickOnMonthlyFirstRowMonths();
    this.selectMonthlyFirstRowMonth(month);
    this.clickOnMonthlyFirstRowHours();
    this.selectHours(hours);
    this.clickOnMonthlyFirstRowMinutes();
    this.selectMinutes(minutes);
    this.clickOnMonthlyFirstRowTimeStamp();
    this.selectTimeStamp(timeStamp);
  }

  monthlyOnTheWeeksSchedule(week,day,month,hours,minutes,timeStamp){
    this.selectMonthlyTab();
    this.selectMonthlySecondCheckbox();
    this.clickOnMonthlySecondRowWeeks();
    this.selectMonthlySecondRowWeeks(week);
    this.clickOnMonthlySecondRowDay();
    this.selectMonthlySecondRowDay(day);
    this.clickOnMonthlySecondRowMonth();
    this.selectMonthlySecondRowMonth(month);
    this.clickOnMonthlySecondRowHours();
    this.selectHours(hours);
    this.clickOnMonthlySecondRowMinutes();
    this.selectMinutes(minutes);
    this.clickOnMonthlySecondRowTimeStamp();
    this.selectTimeStamp(timeStamp);
  }

  yearlyEveryMonthSchedule(month,days,hours,minutes,timeStamp) {
    this.selectYearlyTab();
    this.selectYearlyFirstCheckbox();
    this.clickOnYearlyFirstRowMonth();
    this.selectYearlyFirstRowMonth(month);
    this.clickOnYearlyFirstRowDays();
    this.selectYearlyFirstRowDays(days);
    this.clickOnYearlyFirstRowHours();
    this.selectYearlyFirstRowHours(hours);
    this.clickOnYearlyFirstRowMinutes();
    this.selectYearlyFirstRowMinutes(minutes);
    this.clickOnYearlyFirstRowTimeStamp();
    this.selectYearlyFirstRowTimeStamp(timeStamp);
  }

  yearlyOnWeekSchedule(weeks,day,month,hours,minutes,timeStamp) {
    this.selectYearlyTab();
    this.selectYearlySecondCheckbox();
    this.clickOnYearlySecondRowWeeks();
    this.selectYearlySecondRowWeeks(weeks);
    this.clickOnYearlySecondRowDay();
    this.selectYearlySecondRowDay(day);
    this.clickOnYearlySecondRowMonth();
    this.selectYearlySecondRowMonth(month);
    this.clickOnYearlySecondRowHours();
    this.selectYearlySecondRowHours(hours);
    this.clickOnYearlySecondRowMinutes();
    this.selectYearlySecondRowMinutes(minutes);
    this.clickOnYearlySecondRowTimeStamp();
    this.selectYearlySecondRowTimeStamp(timeStamp);
  }
}
module.exports = SchedulePage;
