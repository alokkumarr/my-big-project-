'use strict';

const logger = require('../../../conf/logger')(__filename);
const commonFunctions = require('../../utils/commonFunctions');

class DeleteModel {
  constructor() {
    this._yesButton = element(by.css(`[e2e="delete-yes"]`));
  }

  clickOnConfirmYesButton() {
    commonFunctions.clickOnElement(this._yesButton);
  }
}

module.exports = DeleteModel;
