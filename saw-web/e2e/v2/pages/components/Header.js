'use strict';

const logger = require('../../conf/logger')(__filename);
const commonFunctions = require('../utils/commonFunctions')

class Header {

  constructor() {
    this._accountSettingIcon = element(by.css(`[e2e='account-settings-menu-btn']`));
    this._accountChangePasswordLink = element(by.css(`[e2e='account-settings-selector-change-password']`));
    this._accountLogoutLink = element(by.css(`[e2e='account-settings-selector-logout']`));
    this._companyLogo = element(by.css('.company-logo'));
  }


  doChangePassword() {
    logger.silly('Clicking on ChangePassword..');
    commonFunctions.waitFor.elementToBeVisible(this._accountSettingIcon);
    if (!this._accountSettingIcon.isPresent()) {
      commonFunctions.goToHome();
    }
    this._accountSettingIcon.click();
    commonFunctions.waitFor.elementToBeVisible(this._accountChangePasswordLink);
    this._accountChangePasswordLink.click();
    commonFunctions.waitFor.pageToBeReady(/changePwd/);
  }

  doLogout() {
    logger.silly('doing logout..');
    if (!this._accountSettingIcon.isPresent()) {
      commonFunctions.goToHome();
    }
    this._accountSettingIcon.click();
    commonFunctions.waitFor.elementToBeVisible(this._accountLogoutLink);
    this._accountLogoutLink.click();
    commonFunctions.waitFor.pageToBeReady(/login/);
    commonFunctions.clearLocalStorage();
  }

  verifyLogo() {
    commonFunctions.waitFor.elementToBeVisible(this._companyLogo);
    expect(this._companyLogo.isPresent()).toBeTruthy();
  }
}
module.exports = Header;
