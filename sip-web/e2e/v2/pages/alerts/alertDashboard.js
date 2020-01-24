'use strict';

const commonFunctions = require('../utils/commonFunctions');
const AddAlerts = require('../alerts/addAlerts');

class AlertDashboard extends AddAlerts {
  constructor() {
    super();
    this._addAlertButton = element(by.css('button[e2e="open-add-alert"]'));
    this._deleteAlertIcon = value =>
      element(by.css(`[e2e='delete-alert-${value}']`));
    this._editAlertButton = value =>
      element(by.css(`[e2e='edit-alert-${value}']`));
    this._tableDataText = text =>
      element(by.xpath(`//td[contains(text(),"${text}")]`));
  }

  clickOnAddAlertButton() {
    commonFunctions.clickOnElement(this._addAlertButton);
    commonFunctions.waitForProgressBarToComplete();
  }

  clickOnDeleteAlertIcon(value) {
    commonFunctions.clickOnElement(this._deleteAlertIcon(value));
  }

  clickOnEditAlertButton(value) {
    commonFunctions.clickOnElement(this._editAlertButton(value));
    commonFunctions.waitForProgressBarToComplete();
  }

  validateAddedAlerts(text) {
    commonFunctions.waitFor.elementToBeVisible(this._tableDataText(text));
    expect(this._tableDataText(text)).toBeTruthy();
  }

  verifyUpdatedAlert() {}
}

module.exports = AlertDashboard;
