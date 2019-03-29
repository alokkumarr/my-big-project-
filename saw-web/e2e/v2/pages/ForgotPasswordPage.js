'use-strict';
const logger = require('../conf/logger')(__filename);
const commonFunctions = require('./utils/commonFunctions');
const users = require('../helpers/data-generation/users');

class ForgotPasswordPage {
  constructor() {
    this._ForgotPassword = element(by.css(`[e2e="forgot-password"]`));
  }

  doClickOnForgotPassword() {
    commonFunctions.clickOnElement(this._ForgotPassword);
    commonFunctions.waitFor.pageToBeReady(/preResetPwd/);
  }
}
module.exports = ForgotPasswordPage;
