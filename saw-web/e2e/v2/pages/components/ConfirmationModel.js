'use strict';

const logger = require('../../conf/logger')(__filename);
const commonFunctions = require('../utils/commonFunctions');

class ConfirmationModel {
  constructor() {
    this._deleteConfirm = element(by.css(`[e2e="confirm-dialog-ok-btn"]`));
  }

  confirmDelete() {
    commonFunctions.clickOnElement(this._deleteConfirm);
  }
}

module.exports = ConfirmationModel;
