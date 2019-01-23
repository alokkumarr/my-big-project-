'use strict';

const logger = require('../../conf/logger')(__filename);
const commonFunctions = require('../utils/commonFunctions')
const SideNav = require('./SideNav');

class Header extends SideNav {

  constructor() {
    super();
    this._accountSettingIcon = element(by.css(`[e2e='account-settings-menu-btn']`));
    this._accountLogoutLink = element(by.css(`[e2e='account-settings-selector-logout']`));
    this._companyLogo = element(by.css('.company-logo'));
    this._categoryMenuIcon = element(by.css(`[e2e="main-menu-expand-btn"]`));
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
    expect(this._companyLogo.isPresent()).toBeTruthy();
  }
  openCategoryMenu() {
    commonFunctions.waitFor.elementToBeClickable(this._categoryMenuIcon);
    this._categoryMenuIcon.click();
  }
}
module.exports = Header;
