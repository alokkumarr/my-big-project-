'use strict';

const commonFunctions = require('../utils/commonFunctions');

class ConfirmDeleteAlertDialog {
  constructor() {
    this._affirmDeleteAlert = element(by.css('button[e2e="alert-delete-yes"]'));

    this._cancelDeleteAlert = element(by.css('button[e2e="alert-delete-no"]'));

    this._alertTitle = element(by.css('button[e2e="delete-title"]'));
  }

  clickOnConfirmDeleteAlert() {
    commonFunctions.clickOnElement(this._affirmDeleteAlert);
  }

  clickOnCancelDeleteAlert() {
    commonFunctions.clickOnElement(this._cancelDeleteAlert);
  }
}

module.exports = ConfirmDeleteAlertDialog;
