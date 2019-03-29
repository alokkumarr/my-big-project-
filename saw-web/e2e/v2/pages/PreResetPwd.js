'use-strict';
const logger = require('../conf/logger')(__filename);
const commonFunctions = require('./utils/commonFunctions');
const users = require('../helpers/data-generation/users');
let PreResetHeader = require('./components/PreResetHeader');

class PreResetPwd {
  constructor() {
    // Initialize all elements
    this._userName = element(by.css(`[e2e="Username"]`));
    this._resetButton = element(by.css(`[e2e="Reset"]`));
    this._loginButton = element(by.css(`[e2e="Login"]`));
    this._errorMessage = element(by.css(`[e2e="error-msg"]`));
    this._pageName = element(by.css(`[e2e="Reset-Password"]`));
  }

  fillUserNameField(userName) {
    commonFunctions.fillInput(this._userName, userName);
  }

  clickOnResetButton() {
    commonFunctions.clickOnElement(this._resetButton);
  }

  doReset(userName, message) {
    logger.debug('Doing Reset..');
    //message = message + ' ' + userName;
    this.fillUserNameField(userName);
    this.clickOnResetButton();
    commonFunctions.waitFor.elementToBeVisible(this._errorMessage);
    logger.info(this._errorMessage.getText());
    logger.info(message);
    expect(this._errorMessage.getText()).toEqual(message);
  }

  resetAs(userName, message) {
    logger.silly('reset with--->' + userName);
    switch (userName) {
      case 'admin':
        this.doReset(users.admin.loginId, message);
        break;
      case 'userOne':
        this.doReset(users.userOne.loginId, message);
        break;
      case 'user':
        this.doReset('reportuser@synchronoss.com', message);
        break;
      case 'analyst':
        this.doReset('analyst@synchronoss.com', message);
        break;
      case 'reviewer':
        this.doReset('reviewer@synchronoss.com', message);
        break;
      default:
    }
  }

  verifyError(expectedMessage) {
    commonFunctions.waitFor.elementToBeVisible(this._errorMessage);
    expect(this._errorMessage.getText()).toEqual(expectedMessage);
  }
}

module.exports = PreResetPwd;
