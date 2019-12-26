'use strict';
const logger = require('../conf/logger')(__filename);
const commonFunctions = require('./utils/commonFunctions');
const ConfirmationModel = require('./components/ConfirmationModel');

class SchedulePage extends ConfirmationModel {
  constructor() {
    super();
    this._analysisTitle = element(by.css(`[e2e="schedule-analysis-header"]`));
    this._scheduleImmediatelyCheckBox = element(by.css(`[e2e="immediate-schedule"]`));
    this._scheduleButton = element(by.css(`[e2e='schedule-analysis-publish']`));
    this._emailInputBox = element(by.css(`[e2e='email-list-input']`));
    this._toastMessage = element(by.css(`[class='toast-message']`));
    this._removeSchedule = element(by.cssContainingText('span'," Remove Schedule "));

    //Elements in Hourly Tab
    this.hourlyTab = element(by.xpath("//div[text()='Hourly']"));
    this.hourlyTab_everyHour = element(by.css('[e2e="hourly-schedule-hourly-hours"]'));
    this.hourlyTab_selectHours = value=> element(by.xpath(`//span[text()=' ${value} ']`));
    this.hourlyTab_minutes = element(by.css('[e2e="hourly-schedule-hourly-minutes"]'));
    this.hourlyTab_selectMinutes = value=> element(by.xpath(`//span[text()=' ${value} ']`));
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
    commonFunctions.clickOnElement(this._removeSchedule);
  }

  ScheduleImmediately() {
    commonFunctions.clickOnElement(this._scheduleImmediatelyCheckBox);
  }

  scheduleReport() {
    browser.actions().mouseDown().perform();
    commonFunctions.clickOnElement(this._scheduleButton);
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

  selectHourlyTab() {
    commonFunctions.clickOnElement(this.hourlyTab);
    browser.sleep(2000); //Need to wait till hourly popup loads
  }

  clickEveryHour() {
    commonFunctions.clickOnElement(this.hourlyTab_everyHour);
  }

  selectHours(hours) {
    commonFunctions.clickOnElement(this.hourlyTab_selectHours(hours));
  }

  clickMinutes() {
    commonFunctions.clickOnElement(this.hourlyTab_minutes);
  }

  selectMinutes(minutes) {
    commonFunctions.clickOnElement(this.hourlyTab_selectMinutes(minutes));
  }
}
module.exports = SchedulePage;
