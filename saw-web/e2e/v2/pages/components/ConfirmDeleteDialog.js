'use strict';

const commonFunctions = require('../utils/commonFunctions');

class ConfirmDeleteDialogModel {
  constructor() {
    this._dashboardConfirmDeleteButton = element(
      by.css('[e2e="dashboard-confirm-dialog-confirm"]')
    );
    this._dashboardCancelDeleteButton = element(
      by.css('[e2e="dashboard-confirm-dialog-cancel"]')
    );
  }

  clickOnDashboardConfirmDeleteButton() {
    commonFunctions.clickOnElement(this._dashboardConfirmDeleteButton);
  }

  clickOnDashboardCancelDeleteButton() {
    commonFunctions.clickOnElement(this._dashboardConfirmDeleteButton);
  }
}

module.exports = ConfirmDeleteDialogModel;
