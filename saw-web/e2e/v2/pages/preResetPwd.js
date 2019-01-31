'use-strict'
const logger = require('../conf/logger')(__filename);
const commonFunctions = require('./utils/commonFunctions');
const users = require('../helpers/data-generation/users');
let PreResetHeader = require('./components/PreResetHeader');

class preResetPwd {

  constructor() {
    // Initialize all elements
    this._userName = element(by.css(`[e2e="Username"]`));
    this._resetButton = element(by.css(`[e2e="Reset"]`));
    this._loginButton = element(by.css(`[e2e="Login"]`));
    this._errorMessage = element(by.css(`[[e2e="error-msg"]`));
    this._pageName = element(by.css(`[[e2e="Reset-Password"]`));

  }

  fillUserNameField(userName) {
    logger.debug('Filling user name with :' + userName);
    commonFunctions.waitFor.elementToBeVisible(this._userName);
    this._userName.clear().sendKeys(userName);
  }

  clickOnResetButton() {
    logger.debug('Click on reset button');
    commonFunctions.waitFor.elementToBeVisible(this._resetButton);
    this._resetButton.click();
  }

  doReset(userName) {
    logger.debug('Doing Reset..');
    this.fillUserNameField(userName);
    this.clickOnResetButton()
  }

  resetAs(userName, redirectedPage = null) {
    logger.silly('Reset with--->' + userName);
        this.logOutReset(users.admin.loginId,redirectedPage);

    }
  }
  logOutReset(userName, redirectedPage = null)
{
    browser.ignoreSynchronization = false;
    commonFunctions.goToHome();
    browser.ignoreSynchronization = true;
    if (this.isResetComplete()) {
   new PreResetHeader().clickLogin()
    }
    this.doReset(userName);
    /*if (redirectedPage) {
      commonFunctions.waitFor.pageToBeReady(redirectedPage);
    } else {
      commonFunctions.waitFor.pageToBeReady(/reset/);
    }*/
  }
  isResetComplete()
{
    element(this._userName.isPresent().then(function (isPresent) {
      if (isPresent) {
        logger.debug('Not yet sent the mail for reset,hence wait');
        return false;
      }
      logger.debug('Mail is already sent');
      return true;
    }));
  }

  resetAs(userName, redirectedPage = null)
{
    logger.silly('reset with--->' + userName);
    switch (userName) {
      case 'admin':
        this.logOutReset(users.admin.loginId,  redirectedPage);
        break;
      case 'userOne':
        this.logOutReset(users.userOne.loginId, redirectedPage);
        break;
      case 'user':
        this.logOutReset('reportuser@synchronoss.com', redirectedPage);
        break;
      case 'analyst':
        this.logOutReset('analyst@synchronoss.com', redirectedPage);
        break;
      case 'reviewer':
        this.logOutReset('reviewer@synchronoss.com', redirectedPage);
        break;
      default:
    }
  }

  verifyError(expectedMessage)
{
    commonFunctions.waitFor.elementToBeVisible(this._errorMessage);
    expect(this._errorMessage.getText()).toEqual(expectedMessage);
  }
}
module.exports = preResetPwd;

