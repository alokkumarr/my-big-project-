'use-strict';
const logger = require('../conf/logger')(__filename);
const commonFunctions = require('./utils/commonFunctions');
const users = require('../helpers/data-generation/users');
let Header = require('./components/Header');

class LoginPage {
  constructor() {
    // Initialize all elements
    this._userName = element(by.css(`[e2e='username-input']`));
    this._password = element(by.css(`[e2e='password-input']`));
    this._loginButton = element(by.css(`[e2e='login-btn']`));
    this._errorMessage = element(by.css(`[e2e='error-msg-section']`));
  }

  fillUserNameField(userName) {
    logger.debug('Filling user name with :' + userName);
    commonFunctions.waitFor.elementToBeVisible(this._userName);
    this._userName.clear().sendKeys(userName);
  }

  fillPasswordField(password) {
    logger.debug('Filling password with :' + password);
    commonFunctions.waitFor.elementToBeVisible(this._password);
    this._password.clear().sendKeys(password);
  }
ßßß
  clickOnLoginButton() {
    logger.debug('Click on login button');
    commonFunctions.waitFor.elementToBeVisible(this._loginButton);
    this._loginButton.click();
  }

  doLogin(userName, password) {
    logger.debug('Doing login..');
    this.fillUserNameField(userName);
    this.fillPasswordField(password);
    this.clickOnLoginButton();
  }

  logOutLogin(userName, password, redirectedPage = null) {
    browser.ignoreSynchronization = false;
    commonFunctions.goToHome();
    browser.ignoreSynchronization = true;
    if (this.isUserLoggedIn()) {
      new Header().doLogout();
    }
    this.doLogin(userName, password);
    if (redirectedPage) {
      commonFunctions.waitFor.pageToBeReady(redirectedPage);
    } else {
      commonFunctions.waitFor.pageToBeReady(/analyze/);
    }
  }
  isUserLoggedIn() {
    element(
      this._userName.isPresent().then(function(isPresent) {
        if (isPresent) {
          logger.debug(
            'User is on login page, hence do the login, no need to logout'
          );
          return false;
        }
        logger.debug('User is already loggedIn');
        return true;
      })
    );
  }

  loginAs(userName, redirectedPage = null) {
    logger.silly('loginAs--->' + userName);
    switch (userName) {
      case 'admin':
        this.logOutLogin(
          users.admin.loginId,
          users.anyUser.password,
          redirectedPage
        );
        break;
      case 'userOne':
        this.logOutLogin(
          users.userOne.loginId,
          users.anyUser.password,
          redirectedPage
        );
        break;
      case 'user':
        this.logOutLogin(
          'reportuser@synchronoss.com',
          'Sawsyncnewuser1!',
          redirectedPage
        );
        break;
      case 'analyst':
        this.logOutLogin(
          'analyst@synchronoss.com',
          'Sawsyncnewuser1!',
          redirectedPage
        );
        break;
      case 'reviewer':
        this.logOutLogin(
          'reviewer@synchronoss.com',
          'Sawsyncnewuser1!',
          redirectedPage
        );
        break;
      default:
    }
  }

  verifyError(expectedMessage) {
    commonFunctions.waitFor.elementToBeVisible(this._errorMessage);
    expect(this._errorMessage.getText()).toEqual(expectedMessage);
  }
}
module.exports = LoginPage;
