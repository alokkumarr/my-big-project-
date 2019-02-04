'use-strict'
const logger = require('../conf/logger')(__filename);
const commonFunctions = require('./utils/commonFunctions');
const users = require('../helpers/data-generation/users');
let Header = require('./components/Header');

class ChangePassword {

  constructor() {
    // Initialize all elements
    this._oldPassword = element(by.css(`[placeholder='Old Password']`));
    this._newPassword = element(by.css(`[placeholder='New Password']`));
    this._confirmPassword = element(by.css(`[placeholder='Confirm Password']`));
    //this._confirmPassword = element(by.css(`[e2e='confirm-password']`));
    this._errorMessage = element(by.css(`[class='error-msg']`));
    this._changeButton = element(by.buttonText('Change'));
    this._cancelButton = element(by.buttonText('Cancel'));
  }

  fillOldPassword(password) {
    logger.debug('Filling OldPwd with :' + password);
    commonFunctions.waitFor.elementToBeVisible(this._oldPassword);
    this._oldPassword.clear().sendKeys(password);
  }

  fillNewPassword(password) {
    logger.debug('Filling new password with :' + password);
    commonFunctions.waitFor.elementToBeVisible(this._newPassword);
    this._newPassword.clear().sendKeys(password);
  }

  fillConfirmPassword(password) {
    logger.debug('Filling confirm password with :' + password);
    logger.debug('checklog ' + this._confirmPassword);
    commonFunctions.waitFor.elementToBeVisible(this._ConfirmPassword);
    
    this._ConfirmPassword.clear().sendKeys(password);
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

  verifyError(expectedMessage) {
    commonFunctions.waitFor.elementToBeVisible(this._errorMessage);
    expect(this._errorMessage.getText()).toEqual(expectedMessage);
  }
}
module.exports = ChangePassword;

