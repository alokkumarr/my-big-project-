'use-strict'
const logger = require('../conf/logger')(__filename);
const commonFunctions = require('./utils/commonFunctions');
const users = require('../helpers/data-generation/users');

class ChangePasswordPage {

  constructor() {
    // Initialize all elements
    this._oldPassword = element(by.css(`[placeholder='Old Password']`));
    this._newPassword = element(by.css(`[placeholder='New Password']`));
    this._confirmPassword = element(by.css(`[placeholder='Confirm Password']`));
    this._errorMessage = element(by.css(`[class='error-msg']`));
    this._changeButton = element(by.buttonText('Change'));
    this._cancelButton = element(by.buttonText('Cancel'));
    this._userName = element(by.css(`[e2e='username-input']`));
    this._password = element(by.css(`[e2e='password-input']`));
    this._loginButton = element(by.css(`[e2e='login-btn']`));
    
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
  
    clickOnLoginButton() {
      logger.debug('Click on login button');
      commonFunctions.waitFor.elementToBeVisible(this._loginButton);
      this._loginButton.click();
    }

  fillOldPassword(oldpassword) {
    logger.debug('Filling OldPwd with :' + oldpassword);
    commonFunctions.waitFor.elementToBeVisible(this._oldPassword);
    this._oldPassword.clear().sendKeys(oldpassword);
  }

  fillNewPassword(newpassword) {
    logger.debug('Filling new password with :' + newpassword);
    commonFunctions.waitFor.elementToBeVisible(this._newPassword);
    this._newPassword.clear().sendKeys(newpassword);
  }

  fillConfirmPassword(confirmpassword) {
    logger.debug('Filling confirm password with :' + confirmpassword);
    commonFunctions.waitFor.elementToBeVisible(this._confirmPassword);
    this._confirmPassword.clear().sendKeys(confirmpassword);
  }

  clickOnChangeButton() {
    logger.debug('Click on Change button');
    commonFunctions.waitFor.elementToBeVisible(this._changeButton);
    this._changeButton.click();
  }

  clickOnCancelButton() {
    logger.debug('Click on Cancel button');
    commonFunctions.waitFor.elementToBeVisible(this._cancelButton);
    this._cancelButton.click();
  }

  doLogin(userName, password) {
    logger.debug('Doing login..');
    switch (userName) {
      case 'admin':
      this.fillUserNameField(users.admin.loginId);
      break;
      case 'userOne':
      this.fillUserNameField(users.userOne.loginId);
      break;
      default:
    }
    this.fillPasswordField(password);
    this.clickOnLoginButton();
  }

  doChangePwd(oldpwd,newpwd,confpwd)
  {
    this.fillOldPassword(oldpwd);
    this.fillNewPassword(newpwd);
    this.fillConfirmPassword(confpwd);
  }

  

  verifyError(expectedMessage) {
    commonFunctions.waitFor.elementToBeVisible(this._errorMessage);
    expect(this._errorMessage.getText()).toEqual(expectedMessage);
  }
}
module.exports = ChangePasswordPage;

