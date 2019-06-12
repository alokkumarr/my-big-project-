'use strict';

const commonFunctions = require('../utils/commonFunctions');
const AddAlerts = require('../alerts/addAlerts');

class AlertDashboard extends AddAlerts {
  constructor() {
    super();
    this._addAlertButton = element(by.css('button[e2e="open-add-alert"]'));
    this._deleteAlertIcon = element(by.css('button[e2e="delete-alert"]'));
    this._editAlertButton = element(by.css('button[e2e="edit-alert"]'));
    this._tableDataText = text =>
      element(by.xpath(`//td[contains(text(),"${text}")]`));
  }

  clickOnAddAlertButton() {
    commonFunctions.clickOnElement(this._addAlertButton);
  }

  clickOnDeleteAlertIcon() {
    commonFunctions.clickOnElement(this._deleteAlertIcon);
  }

  clickOnEditAlertButton() {
    commonFunctions.clickOnElement(this._editAlertButton);
  }

  validateAddedAlerts(text) {
    commonFunctions.waitFor.elementToBeVisible(this._tableDataText(text));
    expect(this._tableDataText(text)).toBeTruthy();
  }

  verifyUpdatedAlert() {}
}

module.exports = AlertDashboard;
