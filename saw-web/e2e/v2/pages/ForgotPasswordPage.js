'use-strict'
const logger = require('../conf/logger')(__filename);
const commonFunctions = require('./utils/commonFunctions');
const users = require('../helpers/data-generation/users');


class ForgotPasswordPage {

  constructor() {
    // this._userName = element(by.css(`[e2e='username-input']`));
    // this._password = element(by.css(`[e2e='password-input']`));
    // this._loginButton = element(by.css(`[e2e='login-btn']`));
    // this._errorMessage = element(by.css(`[e2e='error-msg-section']`));
    this._ForgotPassword = element(by.css(`[e2e="forgot-password"]`));
  }

  clickOnForgotPasswordButton() {
    logger.debug('Click on forgotPassword? button button');
    commonFunctions.waitFor.elementToBeVisible(this._ForgotPassword);
    this._ForgotPassword.click();
  }

  doClickOnForgotPassword() {
    this.clickOnForgotPasswordButton();
    commonFunctions.waitFor.pageToBeReady(/preResetPwd/);
  }

}
module.exports = ForgotPasswordPage;

