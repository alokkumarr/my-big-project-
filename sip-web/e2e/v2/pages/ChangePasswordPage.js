'use-strict';
const logger = require('../conf/logger')(__filename);
const commonFunctions = require('./utils/commonFunctions');
const users = require('../helpers/data-generation/users');

class ChangePasswordPage {
  constructor() {
    // Initialize all elements
    this._oldPassword = element(by.css(`[placeholder='Old Password']`));
    this._newPassword = element(by.css(`[placeholder='New Password']`));
    this._confirmPassword = element(by.css(`[placeholder='Confirm Password']`));
    this._errorMessage = element(by.xpath(`//*[contains(@id,'mat-error')]/pre`));
    this._changeButton = element(by.buttonText('Change'));
    this._cancelButton = element(by.buttonText('Cancel'));
  }

  fillOldPassword(oldpassword) {
    commonFunctions.fillInput(this._oldPassword, oldpassword);
  }

  fillNewPassword(newpassword) {
    commonFunctions.fillInput(this._newPassword, newpassword);
  }

  fillConfirmPassword(confirmpassword) {
    commonFunctions.fillInput(this._confirmPassword, confirmpassword);
  }

  clickOnChangeButton() {
    commonFunctions.clickOnElement(this._changeButton);
  }

  clickOnCancelButton() {
    commonFunctions.clickOnElement(this._cancelButton);
  }

  doChangePwd(oldpwd, newpwd, confpwd) {
    this.fillOldPassword(oldpwd);
    this.fillNewPassword(newpwd);
    this.fillConfirmPassword(confpwd);
  }

  verifyError(expectedMessage) {
    commonFunctions.waitFor.elementToBeVisible(this._errorMessage);
    this._errorMessage.getText().then(text => {
      console.log(text);
      text = text.replace(/\r?\n/g, '').trim();
      expect(text).toEqual(expectedMessage);
    });
  }
}
module.exports = ChangePasswordPage;
